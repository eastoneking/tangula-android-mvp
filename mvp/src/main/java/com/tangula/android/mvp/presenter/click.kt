package com.tangula.android.mvp.presenter

import android.util.Log
import android.view.View

@Suppress("unused")
class ClickModule<V: View>:SimpleBindableModule(){
    var targetView:V? by fetchDefaultDelegate(this, null as V?)
}

@Suppress("unused")
fun <V: View> bindClick(view:V, onClick:(V)->Unit){
    bindViewAndModule(view, ClickModule<V>(), {vh->
        vh.fireWhenNewIsNull=false
        vh.fireWhenOldIsNull=false
    }) {cm->
        cm?.regPropertyListener("targetView") {_,_,_,_->
            onClick(view)
        }
        view.setOnClickListener{
            cm?.targetView = view
        }
    }
}


@Suppress("unused")
fun <V: View> bindLongClick(view:V, onClick:(V)->Unit){
    bindViewAndModule(view, ClickModule<V>(),{vh->
        vh.fireWhenNewIsNull=false
        vh.fireWhenOldIsNull=false
    }) {cm->
        cm?.regPropertyListener("targetView") {_,_,_,_->
            onClick(view)
        }
        view.setOnLongClickListener{
            cm?.targetView = view
            true
        }
    }
}
