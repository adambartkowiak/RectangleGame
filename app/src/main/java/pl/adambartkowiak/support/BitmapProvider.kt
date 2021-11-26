package pl.adambartkowiak.support

import android.graphics.Bitmap

interface BitmapProvider {
    fun getBitmapFromAsset(filePath: String): Bitmap?
    fun isCached(filePath: String): Boolean
}