package pl.adambartkowiak.support.opengl.gltf

data class BufferView(
    val buffer: Int,
    val byteLength: Int,
    val byteOffset: Int,
    val byteStride: Int?,
    val target: Int?
)
