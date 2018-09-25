package com.tangula.android.mvp.presenter.view.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.tangula.android.mvp.module.DefaultPaginationModule
import com.tangula.utils.function.BiConsumer
import com.tangula.utils.function.Consumer
import java.lang.reflect.ParameterizedType

abstract class SimpleRecyclerDataLoader<T, VH : AbstractRecyclerViewItemHolder<T>> protected constructor(content: Context, recycleView: RecyclerView, resId: Int, orientation: OrientationEnum) {

    protected lateinit var presenter: AbstractRecyclerViewPresenter<T, RecyclerViewHolder, VH>

    enum class OrientationEnum {
        VERTICAL, HORIZONTAL
    }

    enum class ResFillTypeEnum{
        APPENDING, REFRESH
    }

    private var moduleDataFillType: ResFillTypeEnum = ResFillTypeEnum.REFRESH

    init {
        @Suppress("UNCHECKED_CAST")
        val vhclzz = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VH>


        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

                super.onScrollStateChanged(recyclerView, newState)

                val offset = recyclerView!!.computeVerticalScrollOffset()
                val max = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent()

                var module = presenter.module

                if(module==null){
                    module = DefaultPaginationModule()
                    module.pageIndex = 1
                    module.pageSize = 10
                    module.total = 0
                }

                //最大页码
                val max_page_number = module.total/module.pageSize+(if (module.total%module.pageSize>0) 1 else 0)+(if (module.total==0) 1 else 0)


                if (newState == 0) {
                    if (offset == 0) {
                        //下拉刷新
                        module.pageIndex = 1
                        moduleDataFillType = ResFillTypeEnum.REFRESH
                        presenter.refresh()
                    } else if (offset == max) {
                        //上滑加载
                        var page_index = module.pageIndex + 1

                        if(page_index<=max_page_number) {
                            module.pageIndex = page_index
                            moduleDataFillType = ResFillTypeEnum.APPENDING
                           presenter.refresh()
                        }
                        //TODO 更多的最后一页
                    }
                }

            }

        })



        @Suppress("UNCHECKED_CAST")
        when (orientation) {
            SimpleRecyclerDataLoader.OrientationEnum.VERTICAL -> presenter = AbstractRecyclerViewPresenter.displaySimpleRecyclerView(content, recycleView, resId, vhclzz,
                    BiConsumer { module, resNotify ->
                        //loadData(module, resNotify)

                        if(loadDataAndFillRes!=null) {
                            loadDataAndFillRes!!(module, resNotify)
                        }else{
                            loadData(module, resNotify)
                        }
                    },
                    Consumer { module -> refreshPagination(module) }) as  AbstractRecyclerViewPresenter<T, RecyclerViewHolder, VH>
            SimpleRecyclerDataLoader.OrientationEnum.HORIZONTAL -> presenter = AbstractRecyclerViewPresenter.displaySimpleHorizontalRecyclerView(content, recycleView, resId, vhclzz,
                    BiConsumer { module, resNotify ->
                        //loadData(module, resNotify)

                        if(loadDataAndFillRes!=null) {
                            loadDataAndFillRes!!(module, resNotify)
                        }else{
                            loadData(module, resNotify)
                        }
                    },
                    Consumer { module -> refreshPagination(module) }) as  AbstractRecyclerViewPresenter<T, RecyclerViewHolder, VH>
        }

        presenter.refresh()
    }


    var loadDataAndFillRes:((DefaultPaginationModule<T>?,  Consumer<DefaultPaginationModule<T>>)->Unit)?={cm: DefaultPaginationModule<T>?, cb: Consumer<DefaultPaginationModule<T>>->
        when(this.moduleDataFillType){
            ResFillTypeEnum.REFRESH -> {
                loadData(cm, cb)
            }
            ResFillTypeEnum.APPENDING->{
                val items = mutableListOf<T>().apply {
                    this.addAll(cm?.items?: listOf())
                }
                loadData(cm, Consumer{it->
                    it.items = items.apply{addAll(it.items)}
                    cb.accept(it)
                })
            }
        }

    }



    protected abstract fun loadData(currentModule: DefaultPaginationModule<T>?, callback: Consumer<DefaultPaginationModule<T>>)

    protected abstract fun refreshPagination(module: DefaultPaginationModule<T>?)

}
