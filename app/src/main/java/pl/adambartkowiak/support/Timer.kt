package pl.adambartkowiak.support

class Timer(start: Boolean = false) {

    init {
        if (start) {
            update()
        }
    }

    var time = 0L
    var lastTime = 0L
    var delta = 0L
    var multiplier = 1.0f

    fun update() {
        time = System.nanoTime()

        if (lastTime != 0L) {
            delta = time - lastTime
            delta = (delta.toDouble() * multiplier).toLong()
        }
        lastTime = time
    }

    fun getDeltaInMs() = delta / 1000000f
}