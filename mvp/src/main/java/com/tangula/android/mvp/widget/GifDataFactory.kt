package com.tangula.android.mvp.widget

import android.content.Context
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