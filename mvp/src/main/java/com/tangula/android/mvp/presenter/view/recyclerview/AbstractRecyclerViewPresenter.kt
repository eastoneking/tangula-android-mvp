package com.tangula.android.mvp.presenter.view.recyclerview

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tangula.android.mvp.module.DefaultPaginationModule
import com.tangula.android.mvp.presenter.GeneralPresenter
import com.tangula.utils.function.*
import com.tangula.utils.function.Function
import org.jetbrains.annotations.NotNull

/**
 * 抽象的RecyclerViewPresenter.
 *
 * 开发建议：最好不要继承这个抽象类，并直接使用它的子类。这个类之所以是抽象的，并不是为了给开发人员
 * 自定义Presenter的时候继承的，而是防止类被不正确的初始化的。因为RecyclerView中封装的内容会很复杂，
 * 通过继承的方式在子类中扩展方法受限于单继承会导致庞大的子类型，切不能互相通用。这里更倾向于通过
 * 组合模式、工厂模式、策略模式配合方法类型和回调函数的方式在需要实例的时候组装一个所需的对象。
 *
 * @param[T]   列表对象类型.
 * @param[VH] view holder, Presenter的ViewHolder类型.
 * @param[IVH]  item view holder.
 */
abstract class AbstractRecyclerViewPresenter<T, VH : RecyclerViewHolder, IVH : RecyclerView.ViewHolder>
/**
 * 构造函数.
 *
 * @param[orientation]  方向.
 * [LinearLayoutManager.VERTICAL] or [LinearLayoutManager.HORIZONTAL]
 * see LinearLayoutManager.VERTICAL
 * @param[vhFac]               Present的ViewHodler的工厂方法.
 * @param[itemViewFac] 列表中每条记录对应的视图的工厂方法.
 * 一般从layout中加载一个布局.
 * @param[recyclerVhFac]       RecyclerView的ViewHolder工厂方法.
 * @param[itemBindFunc]        每条记录的绑定处理函数.
 */
protected constructor(private val orientation: Int,
                      private val vhFac: Supplier<VH>,
                      private val itemViewFac: BiFunction<View, Int, View>,
                      private val recyclerVhFac: Function<View, IVH>,
                      private val itemBindFunc: BiConsumer<IVH, T>) : GeneralPresenter<VH, DefaultPaginationModule<T>>(vhFac) {

    /**
     * RecyclerView的Adapter.
     * <p>通过这个adapter属性，封装的对象可以直接操作RecyclerView的数据.例如在回到函数中</p>
     */
    var adapter: RecyclerView.Adapter<IVH> = object : RecyclerView.Adapter<IVH>() {

        /**
         * 调用构造函数中定义的itemViewFac工厂方法创建.
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IVH {
            return recyclerVhFac.invoke(itemViewFac.invoke(parent, viewType))
        }

        /**
         * 绑定ViewHolder中的记录属性和视图对象.
         *
         * 这个方法会调用构造函数中传递的[itemBindFunc]函数完成这些功能，并不做其他的事情.
         *
         * @param[holder] ViewHolder对象.
         * @param[position] 当前记录在记录集中的位置.
         */
        override fun onBindViewHolder(holder: IVH, position: Int) {
            itemBindFunc.accept(holder, module.items[position])
        }

        /**
         * 获得记录数量.
         */
        override fun getItemCount(): Int {
            val m = module ?: return 0
            val items = m.items
            return items?.size ?: 0
        }
    }

    init {
        //设置布局方向.
        updateRecyclerViewLayout()
        //关联adapter
        this.viewHolder.view.adapter = this.adapter
    }

    fun updateModule(module:DefaultPaginationModule<T>){
        super.setModule(module)
    }


    /**
     * 设置RecyclerView的布局.
     */
    @Suppress( "MemberVisibilityCanBePrivate")
    protected fun updateRecyclerViewLayout() {
        val vh = this.viewHolder
        val rv = vh.view
        rv.layoutManager = LinearLayoutManager(rv.context, this.orientation, false)
    }

    /**
     * 刷新记录.
     *
     * 调用了[refreshPagination]，在刷新页面的同时刷新页码.
     */
    override fun onRefresh() {
        //this.adapter.notifyDataSetChanged()
        refreshPagination()
    }

    /**
     * 更新页码.
     */
    protected abstract fun refreshPagination()

    companion object {

        /**
         * The call back factory which create the data item's view holder.
         *
         * The view holder must defined a constructor with only one parameter which's type is [View].
         */
        val selfRecyclerVhFactory =  { view:View, clazz:Class<Any> ->
            var res: AbstractRecyclerViewItemHolder<Any>? = null
            for (cs in clazz.constructors) {
                val types = cs.parameterTypes
                if (types.isNotEmpty() && types.size == 1) {
                    val type = types[0]
                    if (type.isInstance(view)) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            res = cs.newInstance(view) as AbstractRecyclerViewItemHolder<Any>
                        } catch (e: Exception) {
                            Log.e("console", "EX_ARVP00001:"+e.localizedMessage, e)
                        }
                        break
                    }
                }
            }
            res
        }

        /**
         * 水平布局的RecyclerView.
         * @param[T] 记录类型.
         * @param[context] 用于读取资源的上下文.
         * @param[rvw] 用于显示的RecyclerView对象.
         * @param[resId] 用于显示单条记录的布局资源标识.
         * @param[clazz] 记录类型.
         * @param funcLoadData 加载记录的方法.
         * <p>这个方法在每次[refresh]的时候回调.</p>
         * @param funcRefreshPage 刷新页码信息的方法.
         * <p>这个方法在每次[refresh]的时候回调.</p>
         * @return 返回值是个[AbstractRecyclerViewItemHolder]类的实例.
         */
        @JvmStatic
        @Suppress("unchecked_cast")
        fun <T> displaySimpleHorizontalRecyclerView(
                context: Context,
                @NotNull rvw: RecyclerView,
                resId: Int,
                clazz: Class<out AbstractRecyclerViewItemHolder<T>>,
                funcLoadData: BiConsumer<DefaultPaginationModule<T>, Consumer<DefaultPaginationModule<T>>>,
                funcRefreshPage: Consumer<DefaultPaginationModule<T>>
        ): AbstractRecyclerViewPresenter<T, RecyclerViewHolder, AbstractRecyclerViewItemHolder<T>> {
            return displaySimpleRecyclerView(LinearLayoutManager.HORIZONTAL, context, rvw, resId,Function{vw:View->selfRecyclerVhFactory(vw , clazz as Class<Any>) as AbstractRecyclerViewItemHolder<T>} , funcLoadData, funcRefreshPage)
        }

        /**
         * 水平布局的RecyclerView.
         * @param[T] 记录类型.
         * @param[context] 用于读取资源的上下文.
         * @param[rvw] 用于显示的RecyclerView对象.
         * @param[resId] 用于显示单条记录的布局资源标识.
         * @param[clazz] 记录类型.
         * @param funcLoadData 加载记录的方法.
         * <p>这个方法在每次[refresh]的时候回调.</p>
         * @param funcRefreshPage 刷新页码信息的方法.
         * <p>这个方法在每次[refresh]的时候回调.</p>
         * @return 返回值是个[AbstractRecyclerViewItemHolder]类的实例.
        </T> */
        @JvmStatic
        @Suppress("unchecked_cast")
        fun <T> displaySimpleRecyclerView(
                context: Context,
                rvw: RecyclerView,
                resId: Int,
                clazz: Class<out AbstractRecyclerViewItemHolder<T>>,
                funcLoadData: BiConsumer<DefaultPaginationModule<T>, Consumer<DefaultPaginationModule<T>>>,
                funcRefreshPage: Consumer<DefaultPaginationModule<T>>
        ): AbstractRecyclerViewPresenter<T, RecyclerViewHolder, AbstractRecyclerViewItemHolder<T>> {
            return displaySimpleRecyclerView(LinearLayoutManager.VERTICAL, context, rvw, resId, Function{vw:View->selfRecyclerVhFactory(vw , clazz as Class<Any>) as AbstractRecyclerViewItemHolder<T>}, funcLoadData, funcRefreshPage)
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

            val vhf = Supplier<RecyclerViewHolder> {object : RecyclerViewHolder(rvw) {}}

            val itemVwFac = BiFunction<View, Int, View> { parent, _ ->
                    LayoutInflater.from(context).inflate(resId, parent as ViewGroup, false)
            }

            return object : AbstractRecyclerViewPresenter<T, RecyclerViewHolder, AbstractRecyclerViewItemHolder<T>>(orientation, vhf, itemVwFac, facItemHolder, BiConsumer { ivh, t -> ivh.bindData(t) }) {
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

        }
    }
}
