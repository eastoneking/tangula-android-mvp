package com.tangula.android.mvp.presenter.view.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * RecyclerView中每个元素的ViewHolder抽象类.
 *
 * 开发建议:不建议继承这个接口。在应用开发中，建议使用闭包、lambad、方法接口等特性处理业务相关的
 * 关联。
 * @see SimpleRecyclerDataLoader
 */
abstract class AbstractRecyclerViewItemHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var context: Context? = null

    /**
     * 绑定数据的接口.
     */
    abstract fun bindData(item: T)

}
