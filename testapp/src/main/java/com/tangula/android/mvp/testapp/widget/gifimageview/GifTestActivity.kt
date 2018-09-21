package com.tangula.android.mvp.testapp.widget.gifimageview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tangula.android.mvp.activity.widget.GifDataFactory
import com.tangula.android.mvp.testapp.R
import com.tangula.android.utils.ApplicationUtils
import kotlinx.android.synthetic.main.activity_gif_test.*

class GifTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        ApplicationUtils.APP = application  //记录APP对象

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_test)

        vw_gif_test_image.gifData = GifDataFactory.loadGif(this, R.drawable.loading)
        vw_gif_test_image.isGif=true

        vw_gif_test_image.setImageResource(R.drawable.loading)

        vw_gif_test_image.showImage("https://www.baidu.com/img/bd_logo1.png") //显示百度搜索首页中的百度Logo图片.
    }

    override fun onPause() {
        super.onPause()
    }


}
