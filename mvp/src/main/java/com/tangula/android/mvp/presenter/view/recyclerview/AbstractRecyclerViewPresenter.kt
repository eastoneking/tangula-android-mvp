package com.tangula.android.mvp.presenter.view.recyclerview

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tangula.android.mvp.module.DefaultPaginationModule
import com.tangula.android.mvp.module.PaginationModule
import com.tangula.android.mvp.presenter.GeneralPresenter
import com.tangula.utils.function.BiConsumer
import com.tangula.utils.function.BiFunction
import com.tangula.utils.function.Consumer
import com.tangula.utils.function.Function
import com.tangula.utils.function.Supplier

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * 抽象的RecyclerViewPresente
 *
 * @param <T>   列表对象类型.
 * @param <VH> view holder, Presenter的ViewHolder类型.
 * @param <IVH>  item view holder.
</IVH></VH></T> */
abstract class AbstractRecyclerViewPresenter<T, VH : RecyclerViewHolder, IVH : RecyclerView.ViewHolder>
/**
 * 构造函数.
 *
 * @param  orientation  方向.
 * see LinearLayoutManager.VERTICAL
 * @param vhFac               Present的ViewHodler的工厂方法.
 * @param recyclerItemViewFac 列表中每条记录对应的视图的工厂方法.
 * 一般从layout中加载一个布局.
 * @param recyclerVhFac       RecyclerView的ViewHolder工厂方法.
 * @param itemBindFunc        每条记录的绑定处理函数.
 */
protected constructor(
        /**
         * 方向.
         *
         * LinearLayoutManager.VERTICAL or LinearLayoutManager.HORIZONTAL
         */
        private val orientation: Int, vhFac: Supplier<VH>, private val itemViewFac: BiFunction<View, Int, View>, private val recyclerVhFac: Function<View, IVH>, private val itemBindFunc: BiConsumer<IVH, T>) : GeneralPresenter<VH, DefaultPaginationModule<T>>(vhFac) {

    internal var adapter: RecyclerView.Adapter<IVH> = object : RecyclerView.Adapter<IVH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IVH {
            return recyclerVhFac.invoke(itemViewFac.invoke(parent, viewType))
        }

        override fun onBindViewHolder(holder: IVH, position: Int) {
            itemBindFunc.accept(holder, module.items[position])
        }

        override fun getItemCount(): Int {
            val m = module ?: return 0
            val items = m.items
            return items?.size ?: 0
        }
    }

    init {
        updateRecyclerViewLayout()

        val vh = this.viewHolder
        val rv = vh.view
        rv.adapter = this.adapter
    }


    /**
     * 设置RecyclerView的布局.
     */
    protected fun updateRecyclerViewLayout() {
        val vh = this.viewHolder
        val rv = vh.view
        rv.layoutManager = LinearLayoutManager(rv.context, this.orientation, false)
    }

    override fun onRefresh() {
        this.adapter.notifyDataSetChanged()
        refreshPagination()
    }

    /**
     * 更新页码.
     */
    protected abstract fun refreshPagination()

    companion object {

        /**
         * 水平.
         * @param context .
         * @param rvw .
         * @param resId .
         * @param clazz .
         * @param funcLoadData .
         * @param funcRefreshPage .
         * @param <T> .
         * @return .
        </T> */
        @JvmStatic
        fun <T> displaySimpleHorizontalRecyclerView(
                context: Context,
                rvw: RecyclerView,
                resId: Int,
                clazz: Class<out AbstractRecyclerViewItemHolder<T>>,
                funcLoadData: BiConsumer<DefaultPaginationModule<T>, Consumer<DefaultPaginationModule<T>>>,
                funcRefreshPage: Consumer<DefaultPaginationModule<T>>
        ): AbstractRecyclerViewPresenter<T, RecyclerViewHolder, AbstractRecyclerViewItemHolder<T>> {
            return displaySimpleRecyclerView(LinearLayoutManager.HORIZONTAL, context, rvw, resId, Function { view ->
                var res: AbstractRecyclerViewItemHolder<T>? = null
                for (cs in clazz.constructors) {
                    val types = cs.parameterTypes
                    if (types != null && types.size == 1) {
                        val type = types[0]
                        if (type.isInstance(view)) {
                            try {
                                @Suppress("UNCHECKED_CAST")
                                res = cs.newInstance(view) as AbstractRecyclerViewItemHolder<T>
                            } catch (e: InstantiationException) {
                                Log.e("console", e.localizedMessage, e)
                            } catch (e: IllegalAccessException) {
                                Log.e("console", e.localizedMessage, e)
                            } catch (e: InvocationTargetException) {
                                Log.e("console", e.localizedMessage, e)
                            }

                            break
                        }
                    }
                }
                res
            }, funcLoadData, funcRefreshPage)
        }

        /**
         * 垂直.
         * @param context .
         * @param rvw .
         * @param resId .
         * @param clazz .
         * @param funcLoadData .
         * @param funcRefreshPage .
         * @param <T> .
         * @return .
        </T> */
        @JvmStatic
        fun <T> displaySimpleRecyclerView(
                context: Context,
                rvw: RecyclerView,
                resId: Int,
                clazz: Class<out AbstractRecyclerViewItemHolder<T>>,
                funcLoadData: BiConsumer<DefaultPaginationModule<T>, Consumer<DefaultPaginationModule<T>>>,
                funcRefreshPage: Consumer<DefaultPaginationModule<T>>
        ): AbstractRecyclerViewPresenter<T, RecyclerViewHolder, AbstractRecyclerViewItemHolder<T>> {
            return displaySimpleRecyclerView(LinearLayoutManager.VERTICAL, context, rvw, resId, Function { view ->
                var res: AbstractRecyclerViewItemHolder<T>? = null
                for (cs in clazz.constructors) {
                    val types = cs.parameterTypes
                    if (types != null && types.size == 1) {
                        val type = types[0]
                        if (type.isInstance(view)) {
                            try {
                                @Suppress("UNCHECKED_CAST")
                                res = cs.newInstance(view) as AbstractRecyclerViewItemHolder<T> //根据泛型参数实际类型的构造函数创建对象
                                res.context = context
                            } catch (e: InstantiationException) {
                                Log.e("console", e.localizedMessage, e)
                            } catch (e: IllegalAccessException) {
                                Log.e("console", e.localizedMessage, e)
                            } catch (e: InvocationTargetException) {
                                Log.e("console", e.localizedMessage, e)
                            }

                            break
                        }
                    }
                }
                res
            }, funcLoadData, funcRefreshPage)
        }

        @JvmStatic
        fun <T> displaySimpleRecyclerView(
                orientation: Int,
                context: Context,
                rvw: RecyclerView,
                resId: Int,
                facItemHolder: Function<View, AbstractRecyclerViewItemHolder<T>>,
                funcLoadData: BiConsumer<DefaultPaginationModule<T>, Consumer<DefaultPaginationModule<T>>>?,
                funcRefreshPage: Consumer<DefaultPaginationModule<T>>?
        ): AbstractRecyclerViewPresenter<T, RecyclerViewHolder, AbstractRecyclerViewItemHolder<T>> {

            val vhf = Supplier<RecyclerViewHolder> {
                object : RecyclerViewHolder(rvw) {

                }
            }


            val itemVwFac = BiFunction<View, Int, View> { parent, _ -> LayoutInflater.from(context).inflate(resId, parent as ViewGroup, false) }

            val res = object : AbstractRecyclerViewPresenter<T, RecyclerViewHolder, AbstractRecyclerViewItemHolder<T>>(orientation, vhf, itemVwFac, facItemHolder, BiConsumer { ivh, t -> ivh.bindData(t) }) {
                override fun loadModule(callbackLoadResultHandler: Consumer<DefaultPaginationModule<T>>) {
                    funcLoadData?.accept(module, callbackLoadResultHandler)
                }

                override fun refreshPagination() {
                    if (funcRefreshPage != null) {
                        val m = module
                        if (m != null) {
                            funcRefreshPage.accept(m)
                        }
                    }
                }
            }

            return res

        }
    }
}
