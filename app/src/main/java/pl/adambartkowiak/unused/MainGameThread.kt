package pl.adambartkowiak.unused

import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import pl.adambartkowiak.rectanglegame.GameGLSurfaceView
import pl.adambartkowiak.support.Timer


class MainGameThread(
    private var surfaceHolder: SurfaceHolder,
    private var gameGLSurfaceView: GameGLSurfaceView
) :
    Thread(TAG) {

    private var running = false
    private var canvas: Canvas? = null

    private val mainTimer = Timer()
    private val lockCanvasTimer = Timer()
    private val unlockCanvasTimer = Timer()
    private val updateLogicAndDrawTimer = Timer()
    private val updateLogicTimer = Timer()
    private val drawTimer = Timer()

    override fun run() {
        while (running) {
            canvas = null
            try {
                lockCanvasTimer.update()
                canvas = surfaceHolder.lockCanvas()
                lockCanvasTimer.update()

                updateLogicAndDrawTimer.update()
                synchronized(surfaceHolder) {
                    updateLogicTimer.update()
                    mainTimer.update()
                    gameGLSurfaceView.update(mainTimer.getDeltaInMs())
                    updateLogicTimer.update()

                    drawTimer.update()
                    gameGLSurfaceView.draw(canvas)
                    drawTimer.update()
                }
                updateLogicAndDrawTimer.update()
            } catch (e: Exception) {
            } finally {
                if (canvas != null) {
                    try {
                        unlockCanvasTimer.update()
                        surfaceHolder.unlockCanvasAndPost(canvas)
                        unlockCanvasTimer.update()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (mainTimer.getDeltaInMs() > 30) {
                    Log.v(TAG, "-")
                    Log.v(TAG, "loop ${mainTimer.getDeltaInMs()}ms")
                    Log.v(TAG, "lock canvas ${lockCanvasTimer.getDeltaInMs()}ms")
                    Log.v(TAG, "update logic ${updateLogicTimer.getDeltaInMs()}ms")
                    Log.v(TAG, "draw canvas ${drawTimer.getDeltaInMs()}ms")
                    Log.v(TAG, "update and draw ${updateLogicAndDrawTimer.getDeltaInMs()}ms")
                    Log.v(TAG, "unlock canvas ${unlockCanvasTimer.getDeltaInMs()}ms")
                }
            }
        }
    }

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    companion object {
        private const val TAG = "MainGameThread"
    }
}