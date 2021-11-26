package pl.adambartkowiak.support.device

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import pl.adambartkowiak.support.opengl.model.Vec2


object Utils {
    private var sizeCached = Vec2()

    fun init(context: Context) {
        sizeCached = screenSize(context)
    }

    fun screenSize(context: Context? = null): Vec2 {
        return context?.let { it ->
            val wm = it.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            val vec2 = Vec2(size.x.toFloat(), size.y.toFloat())
            sizeCached = vec2
            return vec2
        } ?: sizeCached
    }

    fun openGLAspect(context: Context? = null): Vec2 {
        return Utils.screenSize()
    }
}