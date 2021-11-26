package pl.adambartkowiak.support.opengl

import pl.adambartkowiak.support.opengl.model.Model

class GlTFLoader {

    fun loadVerticesXYZ(objectToRender: Model): FloatArray {
        objectToRender.let {
            val floatList = mutableListOf<Float>()
            it.vertices.forEach { vec3 ->
                floatList.add(vec3.x)
                floatList.add(vec3.y)
                floatList.add(vec3.z)
            }

            return floatList.toFloatArray()
        }
    }

    fun loadNormalsXYZ(objectToRender: Model): FloatArray {
        objectToRender.let {
            val floatList = mutableListOf<Float>()
            it.normals.forEach { vec3 ->
                floatList.add(vec3.x)
                floatList.add(vec3.y)
                floatList.add(vec3.z)
            }

            return floatList.toFloatArray()
        }
    }

    fun loadTangentsXYZW(objectToRender: Model): FloatArray {
        objectToRender.let {
            val floatList = mutableListOf<Float>()
            it.tangents.forEach { vec4 ->
                floatList.add(vec4.x)
                floatList.add(vec4.y)
                floatList.add(vec4.z)
                floatList.add(vec4.w)
            }

            return floatList.toFloatArray()
        }
    }

    fun loadSkinJointXYZW(objectToRender: Model): IntArray {
        objectToRender.let {
            val intList = mutableListOf<Int>()
            it.joints.forEach { vec4 ->
                intList.add(vec4.x)
                intList.add(vec4.y)
                intList.add(vec4.z)
                intList.add(vec4.w)
            }

            return intList.toIntArray()
        }
    }

    fun loadSkinWeightXYZW(objectToRender: Model): FloatArray {
        objectToRender.let {
            val floatList = mutableListOf<Float>()
            it.weights.forEach { vec4 ->
                floatList.add(vec4.x)
                floatList.add(vec4.y)
                floatList.add(vec4.z)
                floatList.add(vec4.w)
            }

            return floatList.toFloatArray()
        }
    }

    fun loadTexturesXY(objectToRender: Model): FloatArray {
        objectToRender.let {
            val floatList = mutableListOf<Float>()
            it.textureCoords.forEach { vec2 ->
                floatList.add(vec2.x)
                floatList.add(vec2.y)
            }

            return floatList.toFloatArray()
        }
    }
}