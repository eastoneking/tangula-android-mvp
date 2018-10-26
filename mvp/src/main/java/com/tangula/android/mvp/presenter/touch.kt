package com.tangula.android.mvp.presenter

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.tangula.android.mvp.R

class TouchModule:SimpleBindableModule(){
    var event: MotionEvent? by fetchDefaultDelegate(this, null as MotionEvent?)
    val touchListens: MutableList<View.OnTouchListener> = mutableListOf()
    var returnValue=false
}

var View.tglTouchModule: TouchModule?
    get() = getTag(R.string.tag_tgl_touch_module) as TouchModule?
    set(value){
        setTag(R.string.tag_tgl_touch_module, value)
    }

@Suppress("unused")
fun <V : View> bindTouch(view: V, onTouch: View.OnTouchListener) {
    if(view.tglTouchModule==null) {
        bindViewAndModule(view, TouchModule(), { vh ->
            vh.fireWhenNewIsNull = false
            vh.fireWhenOldIsNull = false
        }) { cm ->
            cm?.regPropertyListener("event") { self, _, _, nl ->
                synchronized(self) {
                    var result = false
                    for (cur in cm.touchListens) {
                        try {
                            result = result || cur.onTouch(view, nl as MotionEvent)
                            if (result) {
                                break
                            }
                        } catch (ex: Throwable) {
                            Log.e("mvp", ex.localizedMessage, ex)
                        }
                    }
                    cm.returnValue = result
                }
            }
            view.tglTouchModule = cm
            view.setOnTouchListener{_,ev->
                cm?.event = ev
                cm?.returnValue?:false
            }
        }
    }
    view.tglTouchModule!!.touchListens.add(onTouch)
}










