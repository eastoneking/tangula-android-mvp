package com.tangula.android.mvp.activity.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Movie
import android.util.AttributeSet
import android.widget.ImageView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Gif数据工厂.
 */
class GifDataFactory {
    companion object {
        @JvmStatic
        fun loadGif(input: InputStream): ByteArray {
            val buf = ByteArrayOutputStream()
            input.copyTo(buf, 10240)
            val data = buf.toByteArray()
            return data
        }
        @JvmStatic
        fun loadGif(context: Context, resId: Int): ByteArray {
            return loadGif(context.resources.openRawResource(resId))
        }
    }

}

class GifImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {
    /**
     *  gif flag.
     */
    var isGif = false

    /**
     * gif data object.
     */
    var gifData: ByteArray?
        get() = gifData
        set(value) {
            gifData = value
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

    override fun onDraw(canvas: Canvas?) {
        // draw normal
        super.onDraw(canvas)

        //when is gif
        //draw gif's current frame override origin image
        when (isGif) {
            true -> {
                if (movie == null) {
                    movie = Movie.decodeStream(ByteArrayInputStream(gifData))
                }

                val cur_tm = System.currentTimeMillis()
                if (startTm < 0) {
                    startTm = cur_tm
                }

                val duration = (cur_tm - startTm) as Int

                movie?.apply {
                    setTime(duration)
                    draw(canvas)
                }
            }
            else -> {
                if (movie != null || startTm >= 0) {
                    movie = null
                    startTm = -1L
                }
            }
        }
    }
}