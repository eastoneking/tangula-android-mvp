package com.tangula.android.mvp.presenter.action

import android.view.View
import com.tangula.android.mvp.module.Module
import com.tangula.android.mvp.presenter.GeneralPresenter
import com.tangula.utils.function.BiFunction
import com.tangula.utils.function.Consumer
import com.tangula.utils.function.Supplier

class LongClickPresenter<V: View, M:Module>(vhFac: Supplier<LongClickHolder<V>>?, val moduleFac:Supplier<M?>, val onClick:BiFunction<V, M, Boolean>) : GeneralPresenter<LongClickHolder<V>, M>(vhFac) {

    constructor(view:V, mdl:M?, onClick:BiFunction<V, M, Boolean>): this(Supplier { LongClickHolder<V>(view)}, Supplier{mdl}, onClick)

    constructor(view:V, onClick:BiFunction<V, M, Boolean>): this(view, null as M?, onClick)

    override fun loadModule(callbackLoadResultHandler: Consumer<M>?) {
        callbackLoadResultHandler?.accept(moduleFac.get())
    }

    override fun onRefresh() {
        this.viewHolder.view.setOnLongClickListener{_->
            onClick.invoke(viewHolder.view,module)
        }
    }



}