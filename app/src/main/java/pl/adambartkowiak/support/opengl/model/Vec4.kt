package pl.adambartkowiak.support.opengl.model

data class Vec4<T>(val x: T, val y: T, val z: T, val w: T) {
    fun toArray() = arrayListOf(x, y, z, w)
}