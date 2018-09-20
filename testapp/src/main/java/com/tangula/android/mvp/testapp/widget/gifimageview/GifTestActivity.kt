package com.tangula.android.mvp.testapp.widget.gifimageview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tangula.android.mvp.testapp.R
import com.tangula.android.utils.ApplicationUtils
import kotlinx.android.synthetic.main.activity_gif_test.*

class GifTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_test)
        ApplicationUtils.APP = application
        vw_gif_test_image.showImage("https://www.baidu.com/img/bd_logo1.png")
    }


}
