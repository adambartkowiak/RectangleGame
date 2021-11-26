package pl.adambartkowiak.support.opengl.gltf

data class Material(
    val doubleSided: Boolean?,
    val emissiveFactor: List<Float>?,
    val name: String?,
    val pbrMetallicRoughness: PbrMetallicRoughness?,
    val normalTexture: Texture?
)
