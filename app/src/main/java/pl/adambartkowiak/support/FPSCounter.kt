package pl.adambartkowiak.support

import android.util.Log

class FPSCounter {
    var startTime = System.nanoTime()
    var frames = 0

    fun logFrame() {
        frames++
        if (System.nanoTime() - startTime >= 1000000000) {
            Log.d("FPSCounter", "fps: $frames")
            frames = 0
            startTime = System.nanoTime()
        }
    }
}