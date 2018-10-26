package com.tangula.android.mvp.presenter

import android.view.View

@Suppress("unused")
class ClickModule<V: View>:SimpleBindableModule(){
    var targetView:V? by fetchDefaultDelegate(this, null as V?)
    var result:Boolean=false
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
fun <V: View> bindLongClick(view:V, onClick:(V)->Boolean){
    bindViewAndModule(view, ClickModule<V>(),{vh->
        vh.fireWhenNewIsNull=false
        vh.fireWhenOldIsNull=false
    }) {cm->
        cm?.regPropertyListener("targetView") {_,_,_,_->
            cm.result=onClick(view)
        }
        view.setOnLongClickListener{
            cm?.targetView = view
            cm?.result?:false
        }
    }
}


fun View.tglBindClick(onClick:(View)->Unit){
    bindClick(this, onClick)
}

fun View.tglBindLongClick(onClick:(View)->Boolean){
    bindLongClick(this, onClick)
}


