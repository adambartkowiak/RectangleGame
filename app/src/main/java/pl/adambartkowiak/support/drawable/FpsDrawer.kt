package pl.adambartkowiak.support.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import pl.adambartkowiak.support.Timer

class FpsDrawer : Drawable() {

    private val timer = Timer()
    private val buffer = arrayListOf<Int>()
    private val greenPaint = Paint().apply {
        color = Color.rgb(0, 200, 0)
        strokeWidth = 2f
    }
    private val blackSemiTransparentPaint = Paint().apply {
        color = Color.argb(150, 50, 50, 50)
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val blackSemiTransparentLightPaint = Paint().apply {
        color = Color.argb(150, 50, 50, 50)
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        timer.update()
        addDeltaToBuffer()

        buffer.forEachIndexed { index, l ->
            val collPos = index.toFloat() * VIEW_SCALE
            val collHeight = l * VIEW_SCALE
            canvas.drawLine(collPos, 200f, collPos, 200f - collHeight, greenPaint)
        }

        canvas.drawLine(
            1f,
            200f - 16f * VIEW_SCALE,
            FPS_BUFFER_SIZE * VIEW_SCALE,
            200f - 16f * VIEW_SCALE,
            blackSemiTransparentLightPaint
        )
        canvas.drawRect(1f, 1f, FPS_BUFFER_SIZE * VIEW_SCALE, 200f, blackSemiTransparentPaint)
    }

    private fun addDeltaToBuffer() {
        buffer.add(0, timer.getDeltaInMs().toInt())
        while (buffer.size > FPS_BUFFER_SIZE) {
            buffer.removeAt(FPS_BUFFER_SIZE)
        }
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    companion object {
        private const val FPS_BUFFER_SIZE = 200
        private const val VIEW_SCALE = 2f
    }

}