package com.tangula.android.mvp.activity.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.tangula.android.http.ImageHttpUtils
import com.tangula.android.mvp.R
import com.tangula.android.utils.ApplicationUtils

class RemoteImageView(context: Context, attrs: AttributeSet) : GifImageView(context, attrs) {

    companion object {

        var loadingGifData:ByteArray?=null

        var errorPlaceHolderDrawable:Drawable?=null

        fun fetchLoadingGifData():ByteArray{
            if(loadingGifData==null){
                loadingGifData = GifDataFactory.loadGif(ApplicationUtils.APP, R.drawable.place_holder_loading)
            }
            return loadingGifData!!
        }

        fun fetchErrorPlaceHolder():Drawable{
            if(errorPlaceHolderDrawable==null){
                errorPlaceHolderDrawable = ApplicationUtils.APP.getDrawable(R.drawable.place_holder_no_pic)
            }
            return errorPlaceHolderDrawable!!
        }
    }

    fun showImage(url:String){
        setBackgroundColor(Color.TRANSPARENT)
        gifData = fetchLoadingGifData()
        ImageHttpUtils.loadImage(this,url,null,fetchErrorPlaceHolder(), Runnable{
            isGif=false
        }, Runnable {
            isGif=false
        })
    }

}