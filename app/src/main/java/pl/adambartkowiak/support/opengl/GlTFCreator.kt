package pl.adambartkowiak.support.opengl

import pl.adambartkowiak.support.opengl.model.Model

class GlTFCreator {

    fun createFacesVertices(
        verticesXYZ: List<FloatArray>,
        index: Int,
        model: Model
    ): FloatArray {
        model.let { obj ->
            val floatList = mutableListOf<Float>()
            obj.faces.forEach { face ->
                verticesXYZ[index].let {
                    val p1Index = face.p1.pos * CORDS_PER_VERTEX
                    val p2Index = face.p2.pos * CORDS_PER_VERTEX
                    val p3Index = face.p3.pos * CORDS_PER_VERTEX

                    floatList.add(it[p1Index])
                    floatList.add(it[p1Index + 1])
                    floatList.add(it[p1Index + 2])

                    floatList.add(it[p2Index])
                    floatList.add(it[p2Index + 1])
                    floatList.add(it[p2Index + 2])

                    floatList.add(it[p3Index])
                    floatList.add(it[p3Index + 1])
                    floatList.add(it[p3Index + 2])
                }
            }

            return floatList.toFloatArray()
        }
    }

    fun createFacesNormals(
        normalsXYZ: List<FloatArray>,
        index: Int,
        model: Model
    ): FloatArray {
        model.let { obj ->
            val floatList = mutableListOf<Float>()
            obj.faces.forEach { face ->
                normalsXYZ[index].takeIf { it.isNotEmpty() }?.let {
                    val p1Index = face.p1.normal * NORMAL_VECTORS_PER_VERTEX
                    val p2Index = face.p2.normal * NORMAL_VECTORS_PER_VERTEX
                    val p3Index = face.p3.normal * NORMAL_VECTORS_PER_VERTEX

                    floatList.add(it[p1Index])
                    floatList.add(it[p1Index + 1])
                    floatList.add(it[p1Index + 2])

                    floatList.add(it[p2Index])
                    floatList.add(it[p2Index + 1])
                    floatList.add(it[p2Index + 2])

                    floatList.add(it[p3Index])
                    floatList.add(it[p3Index + 1])
                    floatList.add(it[p3Index + 2])
                }
            }

            return floatList.toFloatArray()
        }
    }

    fun createFacesTextureUv(
        texturesUvXY: List<FloatArray>,
        index: Int,
        model: Model
    ): FloatArray {

        model.let { obj ->
            val floatList = mutableListOf<Float>()
            obj.faces.forEach { face ->
                texturesUvXY[index].takeIf { it.isNotEmpty() }?.let {
                    val p1Index = face.p1.uv * TEXTURE_CORDS_PER_VERTEX
                    val p2Index = face.p2.uv * TEXTURE_CORDS_PER_VERTEX
                    val p3Index = face.p3.uv * TEXTURE_CORDS_PER_VERTEX

                    floatList.add(it[p1Index])
                    floatList.add(it[p1Index + 1])

                    floatList.add(it[p2Index])
                    floatList.add(it[p2Index + 1])

                    floatList.add(it[p3Index])
                    floatList.add(it[p3Index + 1])
                }
            }

            return floatList.toFloatArray()
        }
    }

    fun createFacesSkinJoints(
        jointsXYZW: List<IntArray>,
        index: Int,
        model: Model
    ): IntArray? {

        if (jointsXYZW.first().isEmpty()) {
            return null
        }

        model.let { obj ->
            val intList = mutableListOf<Int>()
            obj.faces.forEach { face ->
                jointsXYZW.getOrNull(index)?.takeIf { it.isNotEmpty() }?.let {
                    val p1Index = face.p1.pos * SKIN_JOINT_PER_VERTEX
                    val p2Index = face.p2.pos * SKIN_JOINT_PER_VERTEX
                    val p3Index = face.p3.pos * SKIN_JOINT_PER_VERTEX

                    intList.add(it[p1Index])
                    intList.add(it[p1Index + 1])
                    intList.add(it[p1Index + 2])
                    intList.add(it[p1Index + 3])

                    intList.add(it[p2Index])
                    intList.add(it[p2Index + 1])
                    intList.add(it[p2Index + 2])
                    intList.add(it[p2Index + 3])

                    intList.add(it[p3Index])
                    intList.add(it[p3Index + 1])
                    intList.add(it[p3Index + 2])
                    intList.add(it[p3Index + 3])
                }
            }

            return intList.toIntArray()
        }
    }

    fun createFacesSkinWeights(
        weightsXYZW: List<FloatArray>,
        index: Int,
        model: Model
    ): FloatArray? {
        model.let { obj ->
            val floatList = mutableListOf<Float>()
            obj.faces.forEach { face ->
                weightsXYZW.getOrNull(index)?.takeIf { it.isNotEmpty() }?.let {
                    val p1Index = face.p1.pos * SKIN_WEIGHT_PER_VERTEX
                    val p2Index = face.p2.pos * SKIN_WEIGHT_PER_VERTEX
                    val p3Index = face.p3.pos * SKIN_WEIGHT_PER_VERTEX

                    floatList.add(it[p1Index])
                    floatList.add(it[p1Index + 1])
                    floatList.add(it[p1Index + 2])
                    floatList.add(it[p1Index + 3])

                    floatList.add(it[p2Index])
                    floatList.add(it[p2Index + 1])
                    floatList.add(it[p2Index + 2])
                    floatList.add(it[p2Index + 3])

                    floatList.add(it[p3Index])
                    floatList.add(it[p3Index + 1])
                    floatList.add(it[p3Index + 2])
                    floatList.add(it[p3Index + 3])
                }
            }

            return floatList.toFloatArray()
        }
    }

    fun createFacesTangents(
        tangentsXYZW: List<FloatArray>,
        index: Int,
        model: Model
    ): FloatArray? {
        model.let { obj ->
            val floatList = mutableListOf<Float>()
            obj.faces.forEach { face ->
                tangentsXYZW.getOrNull(index)?.takeIf { it.isNotEmpty() }?.let {
                    val p1Index = face.p1.pos * TANGENTS_PER_VERTEX
                    val p2Index = face.p2.pos * TANGENTS_PER_VERTEX
                    val p3Index = face.p3.pos * TANGENTS_PER_VERTEX

                    floatList.add(it[p1Index])
                    floatList.add(it[p1Index + 1])
                    floatList.add(it[p1Index + 2])
                    floatList.add(it[p1Index + 3])

                    floatList.add(it[p2Index])
                    floatList.add(it[p2Index + 1])
                    floatList.add(it[p2Index + 2])
                    floatList.add(it[p2Index + 3])

                    floatList.add(it[p3Index])
                    floatList.add(it[p3Index + 1])
                    floatList.add(it[p3Index + 2])
                    floatList.add(it[p3Index + 3])
                }
            }

            return floatList.toFloatArray()
        }
    }

    fun createFacesIndices(model: Model): IntArray {
        val max = model.faces.size * VERTEX_PER_FACE
        return (0 until max).toList().toIntArray()
    }

    companion object {
        const val VERTEX_PER_FACE = 3
        const val CORDS_PER_VERTEX = 3
        const val NORMAL_VECTORS_PER_VERTEX = 3
        const val TEXTURE_CORDS_PER_VERTEX = 2
        const val SKIN_JOINT_PER_VERTEX = 4
        const val SKIN_WEIGHT_PER_VERTEX = 4
        const val TANGENTS_PER_VERTEX = 4
    }
}