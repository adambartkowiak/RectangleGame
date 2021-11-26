package pl.adambartkowiak.support.opengl.model

import pl.adambartkowiak.support.opengl.gltf.Node

class Model(
    var vertices: MutableList<Vec3<Float>> = mutableListOf(),
    var faces: MutableList<Face> = mutableListOf(),
    var normals: MutableList<Vec3<Float>> = mutableListOf(),
    var tangents: MutableList<Vec4<Float>> = mutableListOf(),
    var textureCoords: MutableList<Vec2> = mutableListOf(),
    var textureUri: String? = null,
    var normalTextureUri: String? = null,
    var roughnessTextureUri: String? = null,
    var joints: MutableList<Vec4i> = mutableListOf(),
    var jointsOrder: List<Int>? = mutableListOf(),
    var weights: MutableList<Vec4<Float>> = mutableListOf(),
    var inverseBindMatrices: MutableList<Float> = mutableListOf(),
    var nodes: MutableList<Node> = mutableListOf(),
    var baseColorFactor: List<Float>? = listOf()
)


