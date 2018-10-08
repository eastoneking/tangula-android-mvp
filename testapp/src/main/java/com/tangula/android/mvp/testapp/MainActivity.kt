package com.tangula.android.mvp.testapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tangula.android.mvp.testapp.presenter.view.recyclerview.RecyclerViewTestActivity
import com.tangula.android.mvp.testapp.widget.gifimageview.GifTestActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vw_act_main_gif.setOnClickListener{
            startActivity(Intent(this, GifTestActivity::class.java))
        }

        vw_act_main_recyclerview.setOnClickListener {
            startActivity(Intent(this, RecyclerViewTestActivity::class.java))
        }

    }

}
