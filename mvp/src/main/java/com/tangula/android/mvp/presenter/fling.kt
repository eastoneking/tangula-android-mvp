package com.tangula.android.mvp.presenter

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


fun  <V : View>  bindFling(view: V, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){

    val gd = GestureDetector(view.context, object: GestureDetector.SimpleOnGestureListener(){
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            return onFly(e1, e2, velocityX, velocityY)
        }
    })

    bindTouch(view, View.OnTouchListener{ _, ev->
        gd.onTouchEvent(ev)
    })
}

fun  <V : View>  bindFling(view: V, minDistanceX:Float, minDistanceY:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){

    bindFling(view) {e1,e2,dx, dy->
        var res = false
        if(Math.abs(dx)>Math.abs(minDistanceX)&&Math.abs(dy)>Math.abs(minDistanceY)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun  <V : View>  bindFlingLeft(view: V, minDistanceX:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){

    bindFling(view) {e1,e2,dx, dy->
        var res = false
        val adx = Math.abs(dx)
        val ady = Math.abs(dy)
        if(dx<0&&adx>ady&&adx>Math.abs(minDistanceX)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun  <V : View>  bindFlingLeft(view: V, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFlingLeft(view, 0f, onFly)
}


fun  <V : View>  bindFlingRight(view: V, minDistanceX:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){

    bindFling(view) {e1,e2,dx, dy->
        var res = false
        val adx = Math.abs(dx)
        val ady = Math.abs(dy)
        if(dx>0&&adx>ady&&adx>Math.abs(minDistanceX)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun  <V : View>  bindFlingRight(view: V, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFlingRight(view, 0f, onFly)
}

fun  <V : View>  bindFlingUp(view: V, minDistanceY:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFling(view) {e1,e2,dx, dy->
        var res = false
        val adx = Math.abs(dx)
        val ady = Math.abs(dy)
        if(dy<0&&adx<ady&&ady>Math.abs(minDistanceY)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun  <V : View>  bindFlingUp(view: V, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFlingUp(view, 0f, onFly)
}


fun  <V : View>  bindFlingDown(view: V, minDistanceY:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFling(view) {e1,e2,dx, dy->
        var res = false
        val adx = Math.abs(dx)
        val ady = Math.abs(dy)
        if(dy>0&&adx<ady&&ady>Math.abs(minDistanceY)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun  <V : View>  bindFlingDown(view: V, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFlingDown(view, 0f, onFly)
}

fun  View.tglBindFling(onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){

    val gd = GestureDetector(this.context, object: GestureDetector.SimpleOnGestureListener(){
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            return onFly(e1, e2, velocityX, velocityY)
        }
    })

    bindTouch(this, View.OnTouchListener{ _, ev->
        gd.onTouchEvent(ev)
    })
}

fun  View.tglBindFling(minDistanceX:Float, minDistanceY:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){

    bindFling(this) {e1,e2,dx, dy->
        var res = false
        if(Math.abs(dx)>Math.abs(minDistanceX)&&Math.abs(dy)>Math.abs(minDistanceY)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun  View.tglBindFlingLeft( minDistanceX:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){

    bindFling(this) {e1,e2,dx, dy->
        var res = false
        val adx = Math.abs(dx)
        val ady = Math.abs(dy)
        if(dx<0&&adx>ady&&adx>Math.abs(minDistanceX)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun View.tglBindFlingLeft(onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFlingLeft(this, 0f, onFly)
}


fun  View.tglBindFlingRight( minDistanceX:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){

    bindFling(this) {e1,e2,dx, dy->
        var res = false
        val adx = Math.abs(dx)
        val ady = Math.abs(dy)
        if(dx>0&&adx>ady&&adx>Math.abs(minDistanceX)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun  View.tglBindFlingRight( onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFlingRight(this, 0f, onFly)
}

fun   View.tglBindFlingUp( minDistanceY:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFling(this) {e1,e2,dx, dy->
        var res = false
        val adx = Math.abs(dx)
        val ady = Math.abs(dy)
        if(dy<0&&adx<ady&&ady>Math.abs(minDistanceY)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun  View.tglBindFlingUp(onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFlingUp(this, 0f, onFly)
}


fun  View.tglBindFlingDown(minDistanceY:Float, onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFling(this) {e1,e2,dx, dy->
        var res = false
        val adx = Math.abs(dx)
        val ady = Math.abs(dy)
        if(dy>0&&adx<ady&&ady>Math.abs(minDistanceY)){
            res = onFly(e1,e2,dx,dy)
        }
        res
    }
}

fun  View.tglBindFlingDown(onFly:(MotionEvent?, MotionEvent?, Float, Float)->Boolean){
    bindFlingDown(this, 0f, onFly)
}

