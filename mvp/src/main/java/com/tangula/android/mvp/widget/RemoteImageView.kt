package com.tangula.android.mvp.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION
import android.util.AttributeSet
import android.widget.ImageView
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tangula.android.mvp.R
import com.tangula.android.mvp.module.Module
import okhttp3.OkHttpClient


/**
 * 远程Image的模型.
 */
data class RemoteImage(var url:String, var placeHolder:Any?, var errorHolder:Any?) : Module

/**
 * 用于显示远程图片的ImageView.
 * <p>加载过程会显示加载中的GIF动图；加载不到图片的时候显示默认占位图片</p>
 */
class RemoteImageView(context: Context, attrs: AttributeSet) : GifImageView(context, attrs) {

    companion object {
        /**
         * 加载中的动图.
         */
        var loadingGifData:ByteArray?=null

        /**
         * 默认占位图片.
         */
        var errorPlaceHolderDrawable:Drawable?=null

        /**
         * 显示图片.
         */
        @JvmStatic
        private fun loadImage(context: Context?, view: ImageView, url: String, placeHolder: Any?, errorHolder: Any?, onBeforeRequest:Runnable?, onSuccess: Runnable?, onFail: Runnable?) {
            val client = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        onBeforeRequest?.run()
                        chain.proceed(chain.request())
                    }
                    .build()

            val picasso = Picasso.Builder(context ?: view.context)
                    .downloader(OkHttp3Downloader(client))
                    .build()

            val req = picasso.load(url)
            when (placeHolder) {
                is Int -> {
                    req.placeholder(placeHolder)
                }
                is Drawable ->{
                    req.placeholder(placeHolder)
                }
                else ->{
                    req.noPlaceholder()
                }
            }
            when (errorHolder) {
                is Int -> {
                    req.error(errorHolder)
                }
                is Drawable -> {
                    req.error(errorHolder)
                }
            }

            view.post{
                req.into(view, object : Callback {
                    override fun onSuccess() {
                        onSuccess?.run()
                    }
                    override fun onError() {
                        onFail?.run()
                    }
                })
            }
        }

    }

    /**
     * 获取加载中GIF图片.
     */
    fun fetchLoadingGifData():ByteArray{
        if(loadingGifData ==null){
            loadingGifData = GifDataFactory.loadGif(this.context, R.drawable.place_holder_loading)
        }
        return loadingGifData!!
    }

    @SuppressLint("ResourceType")
            /**
     * 获取默认占位图片.
     */
    fun fetchErrorPlaceHolder():Drawable{
        if(errorPlaceHolderDrawable ==null){
            if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                errorPlaceHolderDrawable = this.context.getDrawable(R.drawable.place_holder_no_pic)
            }else {
                errorPlaceHolderDrawable = Drawable.createFromStream(this.context.resources.openRawResource(R.drawable.place_holder_no_pic), "no_pic")
            }
        }
        return errorPlaceHolderDrawable!!
    }

    /**
     * 显示远程图片.
     */
    fun showImage(url:String){

        setBackgroundColor(Color.TRANSPARENT) //设置为透明底色
        gifData = fetchLoadingGifData()

        loadImage(this.context,this,url,null,fetchErrorPlaceHolder() as Drawable?,
                Runnable{
                }, Runnable{
            isGif=false
        }, Runnable {
            isGif=false
        })
    }

    /**
     * 显示远程图片，自定义加载图片和错误图片.
     */
    fun showImage(image: RemoteImage){
        setBackgroundColor(Color.TRANSPARENT) //设置为透明底色
        var ph: Any? = null
        when(image.placeHolder){
            is ByteArray->{
                gifData = image.placeHolder as ByteArray
            }
            else ->{
                ph = image.placeHolder
            }
        }

        loadImage(this.context,this,image.url,ph,image.errorHolder,
                Runnable{
                }, Runnable{
            isGif=false
        }, Runnable {
            isGif=false
        })
    }

}
