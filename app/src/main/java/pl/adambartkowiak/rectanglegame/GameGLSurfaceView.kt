package pl.adambartkowiak.rectanglegame

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import pl.adambartkowiak.support.opengl.model.Model


class GameGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    GLSurfaceView(context, attrs) {
    private var objectsToRender: List<Model>? = null
    private var worldModel = WorldModel()

    //    private var gameLogic = GameLogic()
    private var deltaAccumulator = 0f


    private var moveInXAxis = 0.0f
    private var lastX: Float? = null
    private var renderer: GameRenderer? = null


    fun update(delta: Float) {
        deltaAccumulator += delta

        //update physic
        while (deltaAccumulator >= GameLogic.INTERVAL) {
            deltaAccumulator -= GameLogic.INTERVAL
//            gameLogic.update(worldModel)
        }
    }

    fun setData(models: List<Model>?) {
        objectsToRender = models
    }

    fun initialize() {
        setEGLContextClientVersion(2)

        renderer = GameRenderer(this.context)
        renderer?.models = objectsToRender
        setRenderer(renderer)
    }

    //    override fun surfaceDestroyed(holder: SurfaceHolder?) {
//        var retry = true
//        while (retry) {
//            try {
//                thread.setRunning(false)
//                thread.join()
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//            retry = false
//        }
//    }
//
//    override fun surfaceCreated(holder: SurfaceHolder) {
//        this.surfaceHolder = holder
//        thread = MainGameThread(holder, this)
//        thread.setRunning(true)
//        thread.start()
//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val result = super.onTouchEvent(event)


        if (event?.actionMasked == MotionEvent.ACTION_DOWN) {
            lastX = event.rawX
            return true
        }

        if (event?.actionMasked == MotionEvent.ACTION_MOVE) {
            lastX?.let {
                moveInXAxis += (event.rawX - it) / 5.0f

                renderer?.setRotateX(moveInXAxis)
            }
            lastX = event.rawX
            return true
        }

        return result
    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//
//        synchronized(pointers) {
//
//            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
//                val pointerId = event.getPointerId(event.actionIndex)
//                val touchPointer = pointers.find { it.id == -1 }
//                Log.v("Touch", "pointerId $pointerId touchPointer ${touchPointer?.id}")
//
//                touchPointer?.apply {
//                    id = pointerId
//                    x = event.getX(event.actionIndex)
//                    y = event.getY(event.actionIndex)
//                    state = TouchPointer.actionToString(event.actionMasked)
//                }
//            }
//
//            if (event.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
//                val pointerId = event.getPointerId(event.actionIndex)
//                val touchPointer = pointers.find { it.id == -1 }
//                touchPointer?.apply {
//                    id = pointerId
//                    x = event.getX(event.actionIndex)
//                    y = event.getY(event.actionIndex)
//                    state = TouchPointer.actionToString(event.actionMasked)
//                }
//            }
//
//            if (event.actionMasked == MotionEvent.ACTION_UP) {
//                val pointerId = event.getPointerId(event.actionIndex)
//                val touchPointer = pointers.find { it.id == pointerId }
//
//                Log.v("Touch", "pointerId $pointerId touchPointer ${touchPointer?.id}")
//                touchPointer?.apply {
//                    id = -1
//                    x = event.getX(event.actionIndex)
//                    y = event.getY(event.actionIndex)
//                    state = TouchPointer.actionToString(event.actionMasked)
//                }
//
//            }
//
//            if (event.actionMasked == MotionEvent.ACTION_POINTER_UP) {
//                val pointerId = event.getPointerId(event.actionIndex)
//                val touchPointer = pointers.find { it.id == pointerId }
//                touchPointer?.apply {
//                    id = -1
//                    x = event.getX(event.actionIndex)
//                    y = event.getY(event.actionIndex)
//                    state = TouchPointer.actionToString(event.actionMasked)
//                }
//
//            }
//
//            if (event.actionMasked == MotionEvent.ACTION_CANCEL) {
//                pointers.forEach {
//                    it.id = -1
//                    it.state = "Cancel"
//                }
//            }
//
//            if (event.actionMasked == MotionEvent.ACTION_MOVE) {
//                //check pointer
//                for (pointerIndex in 0 until event.pointerCount) {
//                    val pointerId = event.getPointerId(pointerIndex)
//                    val touchPointer = pointers.find { it.id == pointerId }
//                    Log.v("Touch", "pointerId $pointerId touchPointer ${touchPointer?.id}")
//                    touchPointer?.apply {
//                        id = pointerId
//                        x = event.getX(pointerIndex)
//                        y = event.getY(pointerIndex)
//                        state = TouchPointer.actionToString(event.actionMasked)
//                    }
//                }
//            }
//
//
//
//            controls.forEach()
//            { button ->
//                pointers.forEach { pointer ->
//                    if (insideX(pointer, button) && insideY(pointer, button)) {
//                        button.onTouch(pointer)
//                    }
//                }
//            }
//        }
//
//        return true
//    }

//    private fun insideX(pointer: TouchPointer, it: Button): Boolean {
//        return pointer.x > it.position.x && pointer.x < (it.position.x + (it.bitmap?.width
//            ?: 0))
//    }
//
//    private fun insideY(pointer: TouchPointer, it: Button): Boolean {
//        return pointer.y > it.position.y && pointer.y < (it.position.y + (it.bitmap?.height
//            ?: 0))
//    }

}