package com.tangula.android.mvp.activity.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Movie
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.tangula.android.utils.UiThreadUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Gif数据工厂.
 */
class GifDataFactory {
    companion object {
        @JvmStatic
        fun loadGif(input: InputStream): ByteArray {
            val buf = ByteArrayOutputStream()
            input.copyTo(buf, 10240)
            return buf.toByteArray()
        }

        @JvmStatic
        fun loadGif(context: Context, resId: Int): ByteArray {
            return context.resources.openRawResource(resId).use { loadGif(it) }
        }
    }

}

class GifImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {
    /**
     *  gif flag.
     */
    var isGif = false
    var gifWidth=0
    var gifHeight=0
    var gifScaleX = 1.0f
    var gifScaleY = 1.0f

    /**
     * gif data object.
     */
    var gifData: ByteArray? = null
        set(value) {
            field = value
            if(value!=null){
                val pic = BitmapFactory.decodeByteArray(value, 0, value.size)
                gifWidth=pic.width
                gifHeight=pic.height
            }
            isGif = value != null
        }

    /**
     * the movie object which contains gif pictures.
     */
    private var movie: Movie? = null

    /**
     * current start time.
     */
    var startTm = -1L

    var gifDrawingThread:Thread? = null

    override fun onDraw(canvas: Canvas?) {
        // draw normal

        //when is gif
        //draw gif's current frame override origin image
        when (isGif) {
            true -> {
                if (movie == null) {
                    movie = Movie.decodeStream(ByteArrayInputStream(gifData))

                    gifScaleX = width.toFloat() / gifWidth.toFloat()
                    gifScaleY = height.toFloat() / gifHeight.toFloat()

                    gifDrawingThread=Thread{
                        while(true) {

                            val movie_duration = movie?.duration() ?: 1

                            if (movie_duration < 34) {
                                movie?.setTime(0)
                            } else {
                                val cur_tm = System.currentTimeMillis()
                                if (startTm < 0) {
                                    startTm = cur_tm
                                }
                                val duration = (cur_tm - startTm) % movie_duration //当前时间
                                movie?.setTime(duration.toInt())
                            }

                            postInvalidate()
                            try {
                                Thread.sleep(33)
                            }catch(e:Throwable){
                                //skip exception
                            }
                        }

                    }
                    gifDrawingThread?.start()

                }

                canvas?.save(Canvas.ALL_SAVE_FLAG) //保存变换矩阵
                canvas?.scale(gifScaleX, gifScaleY)
                movie?.draw(canvas, 0f, 0f)
                canvas?.restore() //恢复变换矩阵


            }
            else -> {
                val thread = gifDrawingThread
                gifDrawingThread= null
                try {
                    thread?.interrupt()
                }catch (e:Throwable){
                    Log.e("gif", e.localizedMessage, e)
                }
                super.onDraw(canvas)

                if (movie != null || startTm >= 0) {
                    movie = null
                    startTm = -1L
                }
            }
        }
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        /*
         * view不显示时，停掉后台刷新的线程
         */
        super.onVisibilityChanged(changedView, visibility)
        if((visibility and (View.GONE or View.INVISIBLE))>0){
            if(isGif){
                movie = null
                gifDrawingThread?.interrupt()
            }
        }
    }

    override fun setImageResource(@DrawableRes @RawRes resId: Int) {
        super.setImageResource(resId)
        context.resources.openRawResource(resId).use {
            val head = ByteArray(3).apply { //读取前3Byte,判断资源是否是GIF
                it.read(this)
            }.toString(Charset.defaultCharset())
            when(head){
                "GIF"->{
                    gifData=GifDataFactory.loadGif(context, resId)
                }
                else->{
                    isGif=false
                }
            }
        }

    }
}