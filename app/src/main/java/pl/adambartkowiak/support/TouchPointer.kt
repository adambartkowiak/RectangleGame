package pl.adambartkowiak.support

import android.view.MotionEvent

class TouchPointer(var id: Int, var x: Float, var y: Float, var state: String) {

    companion object {
        fun actionToString(action: Int): String {
            return when (action) {
                MotionEvent.ACTION_DOWN -> "Down"
                MotionEvent.ACTION_MOVE -> "Move"
                MotionEvent.ACTION_POINTER_DOWN -> "Pointer Down"
                MotionEvent.ACTION_UP -> "Up"
                MotionEvent.ACTION_POINTER_UP -> "Pointer Up"
                MotionEvent.ACTION_OUTSIDE -> "Outside"
                MotionEvent.ACTION_CANCEL -> "Cancel"
                else -> "Unknown"
            }
        }
    }
}