package pl.adambartkowiak.support.opengl.gltf

data class Primitive(
    val attributes: Attributes?,
    val indices: Int?,
    val material: Int?,
    val mode: Int?,
    val targets: List<Attributes>?
)
