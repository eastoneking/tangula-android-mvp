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


        vw_act_main_gif.tglBindClick {
            startActivity(Intent(this, GifTestActivity::class.java))
        }

        vw_act_main_recyclerview.tglBindClick {
            startActivity(Intent(this, RecyclerViewTestActivity::class.java))
        }

        vw_act_main_click.tglBindClick {
            Log.v("console", "click")
        }


        vw_act_main_click.tglBindLongClick {
            Log.v("console", "long click")
            false
        }

        /*
        bindTouch(vw_act_main_text, View.OnTouchListener { _, ev ->
            Log.v("console", "touch ${ev.action} ${System.currentTimeMillis()}")
            false
        })
        */

        vw_act_main_text.tglBindFlingLeft  { _,_,_,_ ->
            Log.v("console", "fling left")
            false
        }

        vw_act_main_text.tglBindFlingRight  { _,_,_,_ ->
            Log.v("console", "fling right")
            false
        }

        vw_act_main_text.tglBindFlingUp  { _,_,_,_ ->
            Log.v("console", "fling up")
            false
        }

        vw_act_main_text.tglBindFlingDown  { _,_,_,_ ->
            Log.v("console", "fling down")
            false
        }
    }

}
