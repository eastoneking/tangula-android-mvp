package com.tangula.android.mvp.presenter.view.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log

import com.tangula.android.mvp.module.DefaultPaginationModule
import com.tangula.utils.function.BiConsumer
import com.tangula.utils.function.Consumer

import java.lang.reflect.ParameterizedType
import java.util.ArrayList

import android.support.v7.widget.helper.ItemTouchHelper.*

abstract class SimpleRecyclerDataLoader<T, VH : AbstractRecyclerViewItemHolder<T>> protected constructor(content: Context, recycleView: RecyclerView, resId: Int, orientation: OrientationEnum) {

    protected lateinit var presenter: AbstractRecyclerViewPresenter<T, RecyclerViewHolder, VH>

    enum class OrientationEnum {
        VERTICAL, HORIZONTAL
    }

    init {
        @Suppress("UNCHECKED_CAST")
        val vhclzz = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VH>

        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val offset = recyclerView!!.computeVerticalScrollOffset()
                val max = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent()
                if (newState == 0) {
                    if (offset == 0) {
                        val module = presenter.module
                        module.pageIndex = 1
                        presenter.refresh()
                    } else if (offset == max) {
                        val module = presenter.module
                        module.pageIndex = module.pageIndex + 1
                        presenter.refresh()
                    }
                }

            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        @Suppress("UNCHECKED_CAST")
        when (orientation) {
            SimpleRecyclerDataLoader.OrientationEnum.VERTICAL -> presenter = AbstractRecyclerViewPresenter.displaySimpleRecyclerView(content, recycleView, resId, vhclzz, BiConsumer { module, resNotify -> loadData(module, resNotify) },
                    Consumer { module -> refreshPagination(module) }) as  AbstractRecyclerViewPresenter<T, RecyclerViewHolder, VH>
            SimpleRecyclerDataLoader.OrientationEnum.HORIZONTAL -> presenter = AbstractRecyclerViewPresenter.displaySimpleHorizontalRecyclerView(content, recycleView, resId, vhclzz, BiConsumer { module, resNotify -> loadData(module, resNotify) },
                    Consumer { module -> refreshPagination(module) }) as  AbstractRecyclerViewPresenter<T, RecyclerViewHolder, VH>
        }
    }

    protected abstract fun loadData(currentModule: DefaultPaginationModule<T>?, callback: Consumer<DefaultPaginationModule<T>>)

    protected abstract fun refreshPagination(module: DefaultPaginationModule<T>?)

}
