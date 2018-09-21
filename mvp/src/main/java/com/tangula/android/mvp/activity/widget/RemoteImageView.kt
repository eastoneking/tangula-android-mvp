package com.tangula.android.mvp.activity.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.tangula.android.http.ImageHttpUtils
import com.tangula.android.mvp.R

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

    }

    /**
     * 获取加载中GIF图片.
     */
    fun fetchLoadingGifData():ByteArray{
        if(loadingGifData==null){
            loadingGifData = GifDataFactory.loadGif(this.context, R.drawable.place_holder_loading)
        }
        return loadingGifData!!
    }

    /**
     * 获取默认占位图片.
     */
    fun fetchErrorPlaceHolder():Drawable{
        if(errorPlaceHolderDrawable==null){
            errorPlaceHolderDrawable = this.context.getDrawable(R.drawable.place_holder_no_pic)
        }
        return errorPlaceHolderDrawable!!
    }

    /**
     * 显示远程图片.
     */
    fun showImage(url:String){
        setBackgroundColor(Color.TRANSPARENT) //设置为透明底色
        gifData = fetchLoadingGifData()
        ImageHttpUtils.loadImage(this,url,null,fetchErrorPlaceHolder(), Runnable{
            isGif=false
        }, Runnable {
            isGif=false
        })
    }

}