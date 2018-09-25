package com.tangula.android.mvp.presenter.view.recyclerview

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewParent

abstract class AbstractRecyclerViewItemHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var context: Context? = null

    abstract fun bindData(item: T)

}
