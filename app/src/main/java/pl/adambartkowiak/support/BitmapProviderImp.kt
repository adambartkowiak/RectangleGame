package pl.adambartkowiak.support

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream

class BitmapProviderImp(val context: Context) : BitmapProvider {

    val buffer = mutableMapOf<String, Bitmap>()

    override fun getBitmapFromAsset(
        filePath: String
    ): Bitmap? {
        val assetManager = context.assets
        val istr: InputStream
        var bitmap: Bitmap? = buffer.get(filePath)

        if (bitmap == null) {
            try {
                istr = assetManager.open(filePath!!)

                val options = BitmapFactory.Options()
                options.inScaled = false // No pre-scaling

                bitmap = BitmapFactory.decodeStream(istr, null, options)

                buffer[filePath] = bitmap!!
            } catch (e: IOException) { // handle exception
            }
        }

        return bitmap
    }

    override fun isCached(filePath: String) = buffer[filePath] != null
}