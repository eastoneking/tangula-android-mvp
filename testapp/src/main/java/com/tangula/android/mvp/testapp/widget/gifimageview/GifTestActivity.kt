package com.tangula.android.mvp.testapp.widget.gifimageview

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tangula.android.mvp.testapp.R
import kotlinx.android.synthetic.main.activity_gif_test.*

class GifTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_test)

        vw_gif_test_image.setBackgroundColor(Color.TRANSPARENT)
        vw_gif_test_image.setImageResource(R.drawable.loading3)
    }


}
