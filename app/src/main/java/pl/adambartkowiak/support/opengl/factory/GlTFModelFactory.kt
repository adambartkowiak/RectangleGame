package pl.adambartkowiak.support.opengl.factory

import android.opengl.Matrix
import android.util.Base64
import com.google.gson.Gson
import pl.adambartkowiak.support.AdvanceMath
import pl.adambartkowiak.support.opengl.CORDS_PER_VERTEX
import pl.adambartkowiak.support.opengl.FLOAT_SIZE_IN_BYTES
import pl.adambartkowiak.support.opengl.GlTFRenderer
import pl.adambartkowiak.support.opengl.MATRIX_ELEMENT_COUNT
import pl.adambartkowiak.support.opengl.gltf.GlTF
import pl.adambartkowiak.support.opengl.gltf.Mesh
import pl.adambartkowiak.support.opengl.gltf.Node
import pl.adambartkowiak.support.opengl.gltf.Skin
import pl.adambartkowiak.support.opengl.model.*
import pl.adambartkowiak.support.opengl.repository.AssetsRepository
import java.io.InputStream
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GlTFModelFactory(
    private val assetsRepository: AssetsRepository,
    private val filePath: String
) {
    fun create(inputStream: InputStream): List<Model> {

        val models = mutableListOf<Model>()

        val reader = inputStream.reader()
        val gltf = Gson().fromJson(reader, GlTF::class.java)

        val bufferUri = gltf.buffers.first().uri
        val data = if (bufferUri.startsWith(BASE64_PREFIX)) {
            val base64Data = bufferUri.removePrefix(BASE64_PREFIX)
            Base64.decode(base64Data, Base64.DEFAULT)
        } else {
            assetsRepository.open("$filePath$bufferUri").readBytes()
        }

        gltf.meshes.forEachIndexed { index, mesh ->

            val model = Model()
            val node = gltf.nodes.find { node -> node.mesh == index }

            val verticesFloatBuffer = createFloatArray(gltf, positionIndex(mesh), data)
            val verticesFloatBufferTransformed = transform(verticesFloatBuffer, node)
            addVertices(verticesFloatBufferTransformed, model.vertices)

            val normalsFloatBuffer = createFloatArray(gltf, normalIndex(mesh), data)
            addNormals(normalsFloatBuffer, model.normals)

            val tangentsFloatBuffer = createFloatArray(gltf, tangentIndex(mesh), data)
            addTangents(tangentsFloatBuffer, model.tangents)

            val indicesIntBuffer =
                createUShortArrayAndCastToIntArray(gltf, indicatesIndex(mesh), data)
            addFaces(indicesIntBuffer, model.faces)

            val texCoordsFloatBuffer = createFloatArray(gltf, textcoord0Index(mesh), data)
            addTexCoords(texCoordsFloatBuffer, model.textureCoords)

            model.textureUri = gltf.images?.getOrNull(textureSourceIndex(gltf, mesh))?.uri
            model.normalTextureUri =
                gltf.images?.getOrNull(normalTextureSourceIndex(gltf, mesh))?.uri
            model.roughnessTextureUri =
                gltf.images?.getOrNull(roughnessTextureSourceIndex(gltf, mesh))?.uri
            model.baseColorFactor = getBaseColorFactor(gltf, mesh)


            val jointsFloatBuffer =
                createUShortArrayAndCastToIntArray(gltf, jointsIndex(mesh), data)
            addJoints(jointsFloatBuffer, model.joints)

            val weightsFloatBuffer = createFloatArray(gltf, weightsIndex(mesh), data)
            addWeights(weightsFloatBuffer, model.weights)

            val inverseBindMatrices =
                createFloatArray(gltf, inverseBindMatricesIndex(gltf.skins, node), data)
            addInverseBindMatrices(inverseBindMatrices, model.inverseBindMatrices)

            model.jointsOrder = jointsOrder(gltf)

            //for search parent and children for bones
            //todo: can be optimized
            gltf.nodes.forEach {
                model.nodes.add(it)
            }

            models.add(model)
        }

        reader.close()
        return models
    }

    private fun jointsOrder(gltf: GlTF) =
        gltf.skins?.first()?.joints

    private fun addInverseBindMatrices(
        buffer: FloatArray?,
        inverseBindMatrices: MutableList<Float>
    ) {

        buffer?.forEach {
            inverseBindMatrices.add(it)
        }
    }

    private fun inverseBindMatricesIndex(skins: List<Skin>?, node: Node?) =
        skins?.get(node?.skin ?: 0)?.inverseBindMatrices ?: -1

    private fun textureSourceIndex(
        gltf: GlTF,
        mesh: Mesh
    ) = gltf.textures?.getOrNull(textureIndex(gltf, materialIndex(mesh)))?.source ?: -1

    private fun normalTextureSourceIndex(
        gltf: GlTF,
        mesh: Mesh
    ) = gltf.textures?.getOrNull(normalTextureIndex(gltf, materialIndex(mesh)))?.source ?: -1

    private fun roughnessTextureSourceIndex(
        gltf: GlTF,
        mesh: Mesh
    ) = gltf.textures?.getOrNull(roughnessTextureIndex(gltf, materialIndex(mesh)))?.source ?: -1

    private fun textureIndex(
        gltf: GlTF,
        materialIndex: Int
    ) = gltf.materials?.getOrNull(materialIndex)?.pbrMetallicRoughness?.baseColorTexture?.index
        ?: -1

    private fun getBaseColorFactor(
        gltf: GlTF,
        mesh: Mesh
    ) = gltf.materials?.getOrNull(materialIndex(mesh))?.pbrMetallicRoughness?.baseColorFactor

    private fun roughnessTextureIndex(
        gltf: GlTF,
        materialIndex: Int
    ) =
        gltf.materials?.getOrNull(materialIndex)?.pbrMetallicRoughness?.metallicRoughnessTexture?.index
            ?: -1

    private fun normalTextureIndex(
        gltf: GlTF,
        materialIndex: Int
    ) = gltf.materials?.getOrNull(materialIndex)?.normalTexture?.index
        ?: -1

    private fun materialIndex(mesh: Mesh) =
        mesh.primitives?.get(0)?.material ?: -1

    private fun indicatesIndex(mesh: Mesh) =
        mesh.primitives?.get(0)?.indices ?: -1

    private fun weightsIndex(mesh: Mesh) =
        mesh.primitives?.get(0)?.attributes?.WEIGHTS_0 ?: -1

    private fun jointsIndex(mesh: Mesh) =
        mesh.primitives?.get(0)?.attributes?.JOINTS_0 ?: -1

    private fun textcoord0Index(mesh: Mesh) =
        mesh.primitives?.get(0)?.attributes?.TEXCOORD_0 ?: -1

    private fun normalIndex(mesh: Mesh) =
        mesh.primitives?.get(0)?.attributes?.NORMAL ?: -1

    private fun positionIndex(mesh: Mesh) =
        mesh.primitives?.get(0)?.attributes?.POSITION ?: -1

    private fun tangentIndex(mesh: Mesh) =
        mesh.primitives?.get(0)?.attributes?.TANGENT ?: -1

    private fun createUShortArrayAndCastToIntArray(
        gltf: GlTF,
        indicesIndex: Int,
        data: ByteArray
    ): IntArray? {
        return if (indicesIndex != -1) {
            val indicesOffset = gltf.bufferViews[gltf.accessors[indicesIndex].bufferView].byteOffset
            val indicesLength = gltf.bufferViews[gltf.accessors[indicesIndex].bufferView].byteLength
            val indicesByteArray = data?.inputStream(indicesOffset, indicesLength)?.readBytes()
            toIntArrayFromUShortArray(indicesByteArray)
        } else {
            val indiceCount = data.size / FLOAT_SIZE_IN_BYTES / CORDS_PER_VERTEX
            (0 until indiceCount).toList().toIntArray()
        }
    }

    private fun createFloatArray(gltf: GlTF, accesorIndex: Int, data: ByteArray): FloatArray? {
        return if (accesorIndex != -1) {
            val offset = gltf.bufferViews[gltf.accessors[accesorIndex].bufferView].byteOffset
            val length = gltf.bufferViews[gltf.accessors[accesorIndex].bufferView].byteLength
            val byteArray = data.inputStream(offset, length).readBytes()
            return toFloatArray(byteArray)
        } else null
    }

    private fun addWeights(buffer: FloatArray?, weights: MutableList<Vec4<Float>>) {
        try {
            buffer?.let {
                for (value in buffer.indices step 4) {
                    val x = buffer[value]
                    val y = buffer[value + 1]
                    val z = buffer[value + 2]
                    val w = buffer[value + 3]
                    weights.add(Vec4(x, y, z, w))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addJoints(buffer: IntArray?, joints: MutableList<Vec4i>) {
        try {
            buffer?.let {
                for (value in buffer.indices step 4) {
                    val x = buffer[value]
                    val y = buffer[value + 1]
                    val z = buffer[value + 2]
                    val w = buffer[value + 3]
                    joints.add(Vec4i(x, y, z, w))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun transform(verticesFloatBuffer: FloatArray?, node: Node?): FloatArray? {

        val centerPoint = calcMeshCenterPoint(verticesFloatBuffer)

        return verticesFloatBuffer?.mapIndexed { index, fl ->
            (rotate(node, index, centerPoint, fl) * scale(node, index) +
                    translation(node, index)).toFloat()
        }?.toFloatArray()
    }

    private fun calcMeshCenterPoint(verticesFloatBuffer: FloatArray?): Vec3<Double> {
        val centerPoint = Vec3(0.0, 0.0, 0.0)
        val vertexCount = (verticesFloatBuffer?.size ?: 0) / THREE_DIMENSIONS
        verticesFloatBuffer?.forEachIndexed { index, fl ->
            when (index % 3) {
                0 -> centerPoint.x += fl
                1 -> centerPoint.y += fl
                2 -> centerPoint.z += fl
            }
        }

        centerPoint.x /= vertexCount
        centerPoint.y /= vertexCount
        centerPoint.z /= vertexCount
        return centerPoint
    }

    private fun rotate(
        node: Node?,
        index: Int,
        centerPoint: Vec3<Double>,
        pointOneDimenValue: Float
    ): Float {
        val transformMatrix = FloatArray(MATRIX_ELEMENT_COUNT)

        val rx = node?.rotation?.getOrNull(0)?.toFloat()
        val ry = node?.rotation?.getOrNull(1)?.toFloat()
        val rz = node?.rotation?.getOrNull(2)?.toFloat()
        val rw = node?.rotation?.getOrNull(3)?.toFloat()

        if (rx != null && ry != null && rz != null && rw != null) {
            val rotationMatrix = advanceMath.quaterionToRotationMatrix(Vec4(rx, ry, rz, -rw))
            val translateMatrix = advanceMath.identityMatrix()

            when (index % THREE_DIMENSIONS) {
                0 -> Matrix.translateM(translateMatrix, 0, pointOneDimenValue, 0.0f, 0.0f)
                1 -> Matrix.translateM(translateMatrix, 0, 0.0f, pointOneDimenValue, 0.0f)
                2 -> Matrix.translateM(translateMatrix, 0, 0.0f, 0.0f, pointOneDimenValue)
            }

            Matrix.multiplyMM(transformMatrix, 0, translateMatrix, 0, rotationMatrix, 0)

        }

        return pointOneDimenValue
    }

    private fun translation(node: Node?, index: Int): Double {
        return node?.translation?.get(index % 3) ?: 0.0
    }

    private fun scale(node: Node?, index: Int): Double {
        return node?.scale?.get(index % 3) ?: 1.0
    }

    private fun addFaces(buffer: IntArray?, indexes: MutableList<Face>) {
        try {
            buffer?.let {
                for (value in buffer.indices step 3) {
                    val a = buffer[value]
                    val b = buffer[value + 1]
                    val c = buffer[value + 2]
                    val face = Face(Vertex(a, a, a), Vertex(b, b, b), Vertex(c, c, c))
                    indexes.add(face)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addVertices(buffer: FloatArray?, vertices: MutableList<Vec3<Float>>) {
        try {
            buffer?.let {
                for (value in buffer.indices step 3) {
                    val x = buffer[value]
                    val y = buffer[value + 1]
                    val z = buffer[value + 2]
                    //glTF uses a right-handed coordinate system
                    vertices.add(Vec3(x, y, z))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addTexCoords(buffer: FloatArray?, texCoords: MutableList<Vec2>) {
        try {
            buffer?.let {
                for (value in buffer.indices step 2) {
                    val x = buffer[value]
                    val y = buffer[value + 1]
                    texCoords.add(Vec2(x, y))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addNormals(buffer: FloatArray?, normals: MutableList<Vec3<Float>>) {
        try {
            buffer?.let {
                for (value in buffer.indices step 3) {
                    val x = buffer[value]
                    val y = buffer[value + 1]
                    val z = buffer[value + 2]
                    normals.add(Vec3(x, y, z))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addTangents(buffer: FloatArray?, tangents: MutableList<Vec4<Float>>) {
        try {
            buffer?.let {
                for (value in buffer.indices step 4) {
                    val x = buffer[value]
                    val y = buffer[value + 1]
                    val z = buffer[value + 2]
                    val w = buffer[value + 3]
                    tangents.add(Vec4(x, y, z, w))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toFloatArray(bytes: ByteArray?): FloatArray? {
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val floatBuffer = buffer.asFloatBuffer()
        val floatArray = FloatArray(floatBuffer.limit())
        floatBuffer.get(floatArray)
        return floatArray
    }

    private fun toIntArrayFromUShortArray(bytes: ByteArray?): IntArray? {
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val shortBuffer = buffer.asShortBuffer()
        val shortArray = ShortArray(shortBuffer.limit())
        shortBuffer.get(shortArray)

        val uShortList = shortArray.map { it.toUShort() }
        val intList = uShortList.map { it.toInt() }

        return intList.toIntArray()
    }

    companion object {
        const val BASE64_PREFIX = "data:application/octet-stream;base64,"
        const val THREE_DIMENSIONS = 3
        val advanceMath = AdvanceMath()
    }
}