package pl.adambartkowiak.support.opengl.gltf

data class Node(
    val name: String?,
    val children: List<Int>?,
    val rotation: List<Double>?,
    val scale: List<Double>?,
    val translation: List<Double>?,
    val camera: Int?,
    val matrix: List<Double>?,
    val mesh: Int?,
    val skin: Int?
)
