package com.tangula.android.mvp.presenter.view.recyclerview

import android.support.v7.widget.RecyclerView

import com.tangula.android.mvp.presenter.Presenter

/**
 * 抽象RecyclerViewHolder.
 *
 * 开发建议:不建议继承这个接口。这个接口是为了内部封装Android的ViewHolder而提供的。在应用开发中，
 * 建议使用闭包、lambad、方法接口等特性处理业务相关的关联。
 * @see SimpleRecyclerDataLoader
 */
abstract class RecyclerViewHolder(
        /**
         * 视图对象.
         */
        val view: RecyclerView) : Presenter.ViewHolder
