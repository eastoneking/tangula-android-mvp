package com.tangula.android.mvp.activity.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import io.reactivex.Observable
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

        /**
         * 根据资源id加载Gif图片数据.
         * @param context 读取资源的上下文对象.
         * @param resId 资源Id.
         * @return Gif图片文件内容.
         */
        @JvmStatic
        fun loadGif(context: Context, resId: Int): ByteArray {
            return context.resources.openRawResource(resId).use { loadGif(it) }
        }
    }

}

/**
 * Gif图片组件.
 */
open class GifImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {
    /**
     *  gif flag.
     */
    var isGif = false
    /**
     * gif图片的原始宽度.
     */
    var gifWidth = 0
    /**
     * gif图片的原始高度.
     */
    var gifHeight = 0
    /**
     * 显示时的x轴的缩放比例.
     */
    var gifScaleX = 1.0f
    /**
     * 显示时y轴的缩放比例.
     */
    var gifScaleY = 1.0f

    /**
     * gif data object.
     */
    var gifData: ByteArray? = null
        set(value) {
            field = value
            if (value != null) {
                val pic = BitmapFactory.decodeByteArray(value, 0, value.size)
                gifWidth = pic.width
                gifHeight = pic.height
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
    private var startTm = -1L

    /**
     * 刷新GIF当前帧的线程.
     */
    private var gifDrawingThread: Thread? = null


    override fun onDraw(canvas: Canvas?) {
        when (isGif) {
            true -> {
                if (movie == null) {

                    //原有刷新进度的Thread存在的时候，先中断原有线程
                    //避免没有关闭原来的线程，就新启动一个线程
                    //导致原有线程没有被结束，一直占用资源
                    interruptGifMovieRefreshThreadIfExists()

                    //在后台线程中刷新GIF当前帧
                    //否则会报 I/Choreographer: Skipped 78 frames!  The application may be doing too much work on its main thread. -- warn-1
                    startNewGifMovieRefreshThread()

                }
                drawMovie(canvas)
            }
            else -> {
                //按照默认方式显示图片
                super.onDraw(canvas)
                clearGifResources()
            }
        }
    }

    fun clearGifResources() {
        val thread = gifDrawingThread
        gifDrawingThread = null
        //清理上一次播放GIF图片的状态
        if (thread != null) {
            try {
                thread.interrupt()
            } catch (e: Throwable) {
                Log.e("gif", e.localizedMessage, e)
            }
        }
        if (movie != null || startTm >= 0) {
            movie = null
            startTm = -1L
        }
        //清理结束
    }

    private fun drawMovie(canvas: Canvas?) {
        if(movie!=null) {
            canvas?.save(Canvas.ALL_SAVE_FLAG) //保存变换矩阵
            canvas?.scale(gifScaleX, gifScaleY)
            movie?.draw(canvas, 0f, 0f)
            canvas?.restore() //恢复变换矩阵
        }else{
            super.draw(canvas)
        }
    }

    /**
     * 初始化Movie.
     * <p>View被设置为isGif之后,第一次渲染(draw)的时候,还没有将GIF解析为Movie对象.这个方法用于在这个时候提供初始化功能.</p>
     */
    private fun initMovie() {
        calcMovieScale()
        movie = Movie.decodeStream(ByteArrayInputStream(gifData))
    }

    /**
     * 计算渲染Movie时的缩放比例.
     */
    private fun calcMovieScale() {
        gifScaleX = width.toFloat() / gifWidth.toFloat()
        gifScaleY = height.toFloat() / gifHeight.toFloat()
    }

    private fun startNewGifMovieRefreshThread() {
        if (gifDrawingThread == null) {
            synchronized(this) {
                if (gifDrawingThread == null) {
                    gifDrawingThread = Thread {
                        while (true) {
                            if (movie == null) {
                                //把GIF读成Movie
                                initMovie()
                            }
                            updateMovieTime()

                            //在非UI线程中通知下一次的UI运行期间刷新界面.
                            //上面注释中"warn-1"处可能是由于在UI线程中调用了invalidate()方法
                            //invalidate()方法会立即在UI线程中重新渲染界面，所以相当于递归调用
                            //onDraw()方法，这会导致在一个UI期间内不断刷新界面，浪费计算资源
                            //所以这里用postInvalidate()方法，顺便把切帧放在非UI线程中.
                            postInvalidate()
                            try {
                                Thread.sleep(33)
                            } catch (e: Throwable) {
                                //skip exception
                            }
                        }

                    }.also {
                        it.isDaemon=true
                        it.start()
                    }
                }

            }
        }

    }

    private fun interruptGifMovieRefreshThreadIfExists() {
        if (gifDrawingThread != null) {
            try {
                gifDrawingThread?.interrupt()
            } catch (e: Throwable) {
                //skip write log
            }
        }
    }

    /**
     * 更新Movie的播放时长.
     */
    private fun updateMovieTime() {
        val movie_duration = movie?.duration() ?: 1

        if (movie_duration < 34) {
            //按照一秒30帧计算，一帧最少33.3...毫秒，如果低于这个间隔，人眼无法识别
            //所以这个时候就不耗费时间算第几帧了，直接显示第一帧的图片
            movie?.setTime(0)
        } else {
            val cur_tm = System.currentTimeMillis()
            if (startTm < 0) {
                startTm = cur_tm
            }
            //当前播放时间。经过时长与movie时长取模，保证播放时间不超过movie时长
            val duration = (cur_tm - startTm) % movie_duration
            movie?.setTime(duration.toInt())
        }
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        /*
         * view不显示时，停掉后台刷新的线程
         */
        super.onVisibilityChanged(changedView, visibility)
        if ((visibility and (View.GONE or View.INVISIBLE)) > 0) {
            if (isGif) {
                //清理当前的movie和线程
                //下次重新显示，或者Activity被resume的时候，这些临时变量会被重新设置
                movie = null
                startTm = -1
                gifDrawingThread?.interrupt()
            }
        }
    }

    override fun setImageResource(@DrawableRes @RawRes resId: Int) {
        super.setImageResource(resId)

        //支持ImageView显示本地Drawable资源的时候，根据本地资源判断是否是GIF图片，并显示动画
        context.resources.openRawResource(resId).use {
            //读取前3 Byte,判断资源是否是GIF
            val head = ByteArray(3).apply {
                it.read(this)
            }.toString(Charset.defaultCharset())
            when (head) {
                "GIF" -> {
                    gifData = GifDataFactory.loadGif(context, resId)
                }
                else -> {
                    isGif = false
                }
            }
        }

    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        isGif=false
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        isGif=false
    }

    override fun setImageIcon(icon: Icon?) {
        super.setImageIcon(icon)
        isGif=false
    }

    override fun setImageMatrix(matrix: Matrix?) {
        super.setImageMatrix(matrix)
        isGif=false
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        isGif=false
    }

}