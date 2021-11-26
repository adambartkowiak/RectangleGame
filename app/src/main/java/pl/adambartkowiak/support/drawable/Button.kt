package pl.adambartkowiak.support.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import pl.adambartkowiak.support.OnTouchListener
import pl.adambartkowiak.support.TouchPointer

class Button(var bitmap: Bitmap? = null, var touchListener: OnTouchListener? = null) :
    Drawable(), OnTouchListener {

    var position = PointF(0f, 0f)

    override fun draw(canvas: Canvas) {
        canvas.apply {
            bitmap?.let {
                drawBitmap(
                    it,
                    position.x,
                    position.y,
                    null
                )
            }
        }
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun onTouch(touchPointer: TouchPointer) {
        touchListener?.onTouch(touchPointer)
    }
}