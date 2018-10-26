package com.tangula.android.mvp.testapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.tangula.android.mvp.presenter.*
import com.tangula.android.mvp.testapp.presenter.view.recyclerview.RecyclerViewTestActivity
import com.tangula.android.mvp.testapp.widget.gifimageview.GifTestActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bindClick(vw_act_main_gif) {
            startActivity(Intent(this, GifTestActivity::class.java))
        }

        bindClick(vw_act_main_recyclerview) {
            startActivity(Intent(this, RecyclerViewTestActivity::class.java))
        }

        bindClick(vw_act_main_click) {
            Log.v("console", "click")
        }


        bindLongClick(vw_act_main_click) {
            Log.v("console", "long click")
        }

        /*
        bindTouch(vw_act_main_text, View.OnTouchListener { _, ev ->
            Log.v("console", "touch ${ev.action} ${System.currentTimeMillis()}")
            false
        })
        */

        bindFlingLeft(vw_act_main_text)  { _,_,dx,dy ->
            Log.v("console", "fling left")
            false
        }

        bindFlingRight(vw_act_main_text)  { _,_,dx,dy ->
            Log.v("console", "fling right")
            false
        }

        bindFlingUp(vw_act_main_text)  { _,_,dx,dy ->
            Log.v("console", "fling up")
            false
        }

        bindFlingDown(vw_act_main_text)  { _,_,dx,dy ->
            Log.v("console", "fling down")
            false
        }
    }

}
