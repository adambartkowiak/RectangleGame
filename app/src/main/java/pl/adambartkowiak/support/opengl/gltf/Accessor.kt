package pl.adambartkowiak.support.opengl.gltf

data class Accessor(
    val bufferView: Int,
    val byteOffset: Int?,
    val componentType: Int,
    val count: Int,
    val max: List<Float>?,
    val min: List<Float>?,
    val type: String
    //Add Sparse
)