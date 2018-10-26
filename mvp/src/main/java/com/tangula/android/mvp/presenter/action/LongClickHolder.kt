package com.tangula.android.mvp.presenter.action

import android.view.View
import com.tangula.android.mvp.presenter.Presenter

class LongClickHolder<T: View>(var view:T) : Presenter.AbstractViewHolder<T>() {

    override fun recognizeView(view: T) {
        this.view = view
    }

}