package com.tangula.android.mvp.presenter.view.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.tangula.android.mvp.module.DefaultPaginationModule
import com.tangula.android.utils.UiThreadUtils
import com.tangula.utils.function.BiConsumer
import com.tangula.utils.function.Consumer
import java.lang.reflect.ParameterizedType

/**
 * 简单的RecyclerView数据加载器.
 * @param[T] 要显示的列表数据的数据类型.
 * @param[VH] 单条记录的ViewHolder.
 */
abstract class SimpleRecyclerDataLoader<T, VH : AbstractRecyclerViewItemHolder<T>>
/**
 * 构造函数.
 * @param[content] 读取资源的上下文.
 * @param[recyclerView] 用于显示数据的RecyclerView对象.
 * @param[resId] 单条数据项的布局资源ID.
 * @param[orientation] 布局方向(或方式).
 */
protected constructor(content: Context, recyclerView: RecyclerView, resId: Int, orientation: OrientationEnum) {

    /**
     * 对RecyclerView对象操作的Presenter实例.
     *
     * 通过这个对象可以设置和获得模型，RecyclerView的Adapter。
     */
    protected lateinit var presenter: AbstractRecyclerViewPresenter<T, RecyclerViewHolder, VH>

    /**
     * RecyclerView中显示数据的方向(或方式).
     */
    enum class OrientationEnum {
        /**
         * 垂直方向.
         */
        VERTICAL,
        /**
         * 水平方向.
         */
        HORIZONTAL
    }

    /**
     * 加载新数据填充到当前内容的填充方式.
     *
     */
    enum class ResFillTypeEnum {
        /**
         * 附加在当前结果之后.
         */
        APPENDING,
        /**
         * 丢弃当前结果，并把新获得的结果当做模型的数据.
         */
        REFRESH,
        /**
         * 新数据在最前的模式.
         */
        INSERT_BEFORE
    }

    private var loading = false

    /**
     * The module of data filling type.
     * 模型数据的填充方式.
     */
    private var moduleDataFillType: ResFillTypeEnum = ResFillTypeEnum.REFRESH

    init {
        /*
         * This is constructor!
         */


        /*
         * SimpleRecyclerDataLoader is an abstract class, so any one who want use it must extends
         * from it. When they extends it, would specify a record item's view holder's type as the
         * second generic parameter. We fetch the type with following code, and save it to a local
         * variable which named "vhclzz".
         */
        @Suppress("UNCHECKED_CAST")
        val vhclzz = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VH>



        //set the scroll listener, which handle the data loading processor when user scroll the view.
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

                super.onScrollStateChanged(recyclerView, newState)

                val offset = recyclerView!!.computeVerticalScrollOffset()
                val max = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent()

                var module = presenter.module

                if (module == null) {
                    module = DefaultPaginationModule()
                    module.pageIndex = 1
                    module.pageSize = 10
                    module.total = 0
                    module.items = mutableListOf()
                }

                /*
                var loadingLimit: Int = max

                when{
                    max<300-> loadingLimit = 0
                    max<50000-> loadingLimit = Math.ceil(max*0.8).toInt()
                    module.items.size>3 -> loadingLimit = max/module.items.size*(module.items.size-2)
                }
                */

                //calculate the max value of the data.
                //when total is 0 -> max page number is 1
                //else max page number is total/pageSize, and plus 1 when the total mod pageSize is not zero.
                val maxPageNumber = module.total / module.pageSize + (if (module.total % module.pageSize > 0) 1 else 0) + (if (module.total == 0) 1 else 0)


                if (newState == 0) {
                    /*
                     * the newState is zero means scrolling end.
                     */

                    //offset is the position of scrolling.
                    if (offset == 0) {
                        //scrolling to the end.
                        val pageIndex = module.pageIndex + 1
                        if (pageIndex <= maxPageNumber) {
                            module.pageIndex = pageIndex
                            moduleDataFillType = ResFillTypeEnum.INSERT_BEFORE
                            presenter.refresh()
                        }


                    } else if (offset >= max && !loading) {

                        //drop from the begining.
                        //module.pageIndex = 1
                        //moduleDataFillType = ResFillTypeEnum.REFRESH
                        //presenter.refresh()

                    }
                }

            }

        })

        // The constructor is not end.

        @Suppress("UNCHECKED_CAST")
        when (orientation) {
            SimpleRecyclerDataLoader.OrientationEnum.VERTICAL -> presenter = AbstractRecyclerViewPresenter.displaySimpleRecyclerView(content, recyclerView, resId, vhclzz,
                    BiConsumer { module, resNotify ->
                        //loadData(module, resNotify)

                        if (loadDataAndFillRes != null) {
                            loadDataAndFillRes!!(module, resNotify)
                        } else {
                            loadData(module, resNotify)
                        }
                    },
                    Consumer { module -> refreshPagination(module) }) as AbstractRecyclerViewPresenter<T, RecyclerViewHolder, VH>
            SimpleRecyclerDataLoader.OrientationEnum.HORIZONTAL -> presenter = AbstractRecyclerViewPresenter.displaySimpleHorizontalRecyclerView(content, recyclerView, resId, vhclzz,
                    BiConsumer { module, resNotify ->
                        //loadData(module, resNotify)

                        if (loadDataAndFillRes != null) {
                            loadDataAndFillRes!!(module, resNotify)
                        } else {
                            loadData(module, resNotify)
                        }
                    },
                    Consumer { module -> refreshPagination(module) }) as AbstractRecyclerViewPresenter<T, RecyclerViewHolder, VH>
        }

        presenter.refresh() //load the data immediate when the data loader be created.

        /*
         * The end of constructor.
         */
    }


    /**
     * This property is a function which would execute when loading data.
     *
     * There has two mode for loading, [ResFillTypeEnum.REFRESH] and [ResFillTypeEnum.APPENDING].
     * When the loader's [moduleDataFillType] is [ResFillTypeEnum.REFRESH], the data module's item would be
     * the original result. This mode's processor is like a web application. If the user turn pages,
     * the current page display latest result.
     * When the loader's [moduleDataFillType] is [ResFillTypeEnum.APPENDING], the module's item
     * would be appended by the latest result. It like the flow-module.
     *
     */
    private var loadDataAndFillRes: ((DefaultPaginationModule<T>?, Consumer<DefaultPaginationModule<T>>) -> Unit)? = {
        cm: DefaultPaginationModule<T>?,
        cb: Consumer<DefaultPaginationModule<T>> ->
        // The argument cb is defined in GeneralPresetner::refresh by default.
        // And it function is invoke function [refreshPagination] in this type.

        updateLoadingStatus(true)  //When loading begin, set the loading status to disable others

        var module = this.presenter.module

        if (module == null) {
            //set the default value when first loading.
            module = DefaultPaginationModule()
            module.items = mutableListOf()
            presenter.updateModule(module)
        }


        when (this.moduleDataFillType) {
            ResFillTypeEnum.REFRESH -> {

                loadData(cm, Consumer { newModule ->
                    // refersh data
                    module.items.clear() //clear current data.

                    newModule.items.reverse()
                    module.items.addAll(newModule.items) //update data as latest result.

                    module.pageIndex = newModule.pageIndex
                    module.pageSize = newModule.pageSize
                    module.total = newModule.total

                    UiThreadUtils.runInUiThread { //updating views must run in the UI thread.
                        this.presenter.adapter.notifyDataSetChanged()
                        cb.accept(module)
                        presenter.viewHolder.view.scrollToPosition(newModule.items.size-1)
                        updateLoadingStatus(false)
                    }

                })
            }
            ResFillTypeEnum.APPENDING -> {

                loadData(module, Consumer { latestResult ->
                    val oldMaxIndex = module.items.size // keep current max index

                    latestResult.items.forEach { cur ->
                        module.items.add(cur)
                    }

                    module.pageIndex = latestResult.pageIndex
                    module.pageSize = latestResult.pageSize
                    module.total = latestResult.total

                    UiThreadUtils.runInUiThread {
                        // notify the adapter which be inserted,
                        // from keeped index and the range was latest result's size.
                        this.presenter.adapter.notifyItemRangeInserted(oldMaxIndex, latestResult.items.size)
                        cb.accept(module)
                        updateLoadingStatus(false)
                    }

                })

            }
            ResFillTypeEnum.INSERT_BEFORE -> {

                loadData(module, Consumer { latestResult ->

                    latestResult.items.forEach { cur ->
                        module.items.add(0, cur)
                    }

                    module.pageIndex = latestResult.pageIndex
                    module.pageSize = latestResult.pageSize
                    module.total = latestResult.total

                    UiThreadUtils.runInUiThread {
                        // notify the adapter which be inserted,
                        // from keeped index and the range was latest result's size.
                        this.presenter.adapter.notifyItemRangeInserted(0, latestResult.items.size)
                        cb.accept(module)
                        presenter.viewHolder.view.scrollToPosition(latestResult.items.size-1)
                        updateLoadingStatus(false)
                    }

                })
            }
        }

    }

    /**
     * The lock object for property [loading], please use function [updateLoadingStatus].
     */
    private val loadingStatusUpdateLock = Object()

    /**
     * To update loading status.
     *
     * This function would lock [loadingStatusUpdateLock].
     */
    private fun updateLoadingStatus(value:Boolean){
        synchronized(loadingStatusUpdateLock){
            loading = value
        }
    }


    /**
     * How to load data.
     */
    protected abstract fun loadData(currentModule: DefaultPaginationModule<T>?, callback: Consumer<DefaultPaginationModule<T>>)

    /**
     * How to update the pagination infomation.
     */
    protected open fun refreshPagination(module: DefaultPaginationModule<T>?){

    }

}
