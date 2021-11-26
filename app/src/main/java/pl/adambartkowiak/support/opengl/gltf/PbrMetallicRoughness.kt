package pl.adambartkowiak.support.opengl.gltf

data class PbrMetallicRoughness(
    val baseColorTexture: Texture?,
    val metallicRoughnessTexture: Texture?,
    val metallicFactor: Float?,
    val roughnessFactor: Float?,
    val baseColorFactor: List<Float>?
)
