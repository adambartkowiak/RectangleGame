package pl.adambartkowiak.support.opengl.gltf

data class Mesh(
    val name: String?,
    val primitives: List<Primitive>?,
    val weights: List<Float>?
)