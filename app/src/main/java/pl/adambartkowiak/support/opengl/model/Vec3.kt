package pl.adambartkowiak.support.opengl.model

data class Vec3<T>(var x: T, var y: T, var z: T) {
    fun toArray() = arrayListOf(x, y, z)
}