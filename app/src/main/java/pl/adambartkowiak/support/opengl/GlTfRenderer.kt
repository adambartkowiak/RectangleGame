package pl.adambartkowiak.support.opengl

import android.opengl.GLES20
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import pl.adambartkowiak.support.AdvanceMath
import pl.adambartkowiak.support.BitmapProvider
import pl.adambartkowiak.support.opengl.GlTFCreator.Companion.VERTEX_PER_FACE
import pl.adambartkowiak.support.opengl.model.Model
import pl.adambartkowiak.support.opengl.model.Vec2
import pl.adambartkowiak.support.opengl.model.Vec3
import pl.adambartkowiak.support.opengl.model.Vec4
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.cos
import kotlin.math.sin

const val CORDS_PER_VERTEX = 3

const val TEXTURE_CORDS_PER_VERTEX = 2
const val SKIN_JOINT_PER_VERTEX = 4
const val SKIN_WEIGHT_PER_VERTEX = 4
const val TANGENTS_PER_VERTEX = 3

const val MATRIX_ELEMENT_COUNT = 16

const val TEX_COMPONENTS_PER_VERTEX = 2
const val FLOAT_SIZE_IN_BYTES = 4
const val INT_SIZE_IN_BYTES = 4

var verticesXYZ = mutableListOf<FloatArray>()
var normalsXYZ = mutableListOf<FloatArray>()
var texturesUvXY = mutableListOf<FloatArray>()
var jointsXYZW = mutableListOf<IntArray>()
var weightsXYZW = mutableListOf<FloatArray>()

var facesVerticesXYZ = mutableListOf<FloatArray>()
var facesNormalsXYZ = mutableListOf<FloatArray?>()
var facesTangentsXYZ = mutableListOf<FloatArray?>()
var facesTexturesUvXY = mutableListOf<FloatArray?>()
var facesSkinJointsXYZW = mutableListOf<IntArray?>()
var facesSkinWeightsXYZW = mutableListOf<FloatArray?>()

var indices = mutableListOf<IntArray>()

var fileNameTextureIDMap = mutableMapOf<String, Int>()


class GlTFRenderer(
    private val models: List<Model>?,
    private val bitmapProvider: BitmapProvider
) {

    private var program: Int
    private var modelMatrixHandle: Int = 0
    private var viewMatrixHandle: Int = 0
    private var projectionMatrixHandle: Int = 0
    private var cameraPosHandle: Int = 0
    private var baseColorFactorHandle: Int = 0

    private val readModelsMax = 20

    init {
        val vertexShader = ShaderHelper.loadShader(GLES20.GL_VERTEX_SHADER, GLTFVertextShader.code)
        val fragmentShader =
            ShaderHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, GLTFFragmentShader.code)

        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        ShaderHelper.validateShader(program, javaClass.name)

        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)

        val glTFLoader = GlTFLoader()
        val glTFCreator = GlTFCreator()

        models?.forEachIndexed lit@{ index, model ->

            if (index > readModelsMax) return@lit

            //load Mesh Data
            verticesXYZ.add(glTFLoader.loadVerticesXYZ(model))
            normalsXYZ.add(glTFLoader.loadNormalsXYZ(model))
            texturesUvXY.add(glTFLoader.loadTexturesXY(model))
//            tangentsXYZW.add(glTFLoader.loadTangentsXYZW(model))
            jointsXYZW.add(glTFLoader.loadSkinJointXYZW(model))
            weightsXYZW.add(glTFLoader.loadSkinWeightXYZW(model))


            //Create Faces
            val verticesXYZ = glTFCreator.createFacesVertices(verticesXYZ, index, model)
            val normalsXYZ = glTFCreator.createFacesNormals(normalsXYZ, index, model)
            val texturesUV = glTFCreator.createFacesTextureUv(texturesUvXY, index, model)
            val faceIndices = glTFCreator.createFacesIndices(model)

            pl.adambartkowiak.support.opengl.indices.add(faceIndices)

            //Create Faces
            facesVerticesXYZ.add(verticesXYZ)
            facesNormalsXYZ.add(normalsXYZ)
            facesTexturesUvXY.add(texturesUV)
//            facesTangentsXYZW.add(glTFCreator.createFacesTangents(tangentsXYZW, index, model))
            facesSkinJointsXYZW.add(glTFCreator.createFacesSkinJoints(jointsXYZW, index, model))
            facesSkinWeightsXYZW.add(glTFCreator.createFacesSkinWeights(weightsXYZW, index, model))

            //Calculate
//            createTangents(faceIndices, verticesXYZ, texturesUV)
        }


        //Load textures from disc and add to openGL
        val textureSet = mutableSetOf<String>()
        models?.forEach { model ->
            model.textureUri?.let { textureUri ->
                textureSet.add(textureUri)
            }
            model.normalTextureUri?.let { textureUri ->
                textureSet.add(textureUri)
            }
            model.roughnessTextureUri?.let { textureUri ->
                textureSet.add(textureUri)
            }
        }

        val texturesHandle = IntArray(textureSet.size)
        GLES20.glGenTextures(textureSet.size, texturesHandle, 0)

        texturesHandle.forEachIndexed { index, textureId ->
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesHandle[index])
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST
            )

            val textureFileName = textureSet.elementAt(index)
            GLUtils.texImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                bitmapProvider.getBitmapFromAsset("gltf/$textureFileName"),
                0
            )

            fileNameTextureIDMap[textureFileName] = textureId
        }
    }

    private fun createTangents(
        faceIndices: IntArray,
        verticesXYZ: FloatArray,
        texturesUV: FloatArray
    ) {
        val tangtents = mutableListOf<Float>()
        val facesCount = faceIndices.size / VERTEX_PER_FACE
        val cordsPerFace = VERTEX_PER_FACE * CORDS_PER_VERTEX
        val uvPerFace = VERTEX_PER_FACE * TEXTURE_CORDS_PER_VERTEX

        //init empty array
        for (faceIndex in 0 until facesCount) {
            (0 until VERTEX_PER_FACE).forEach { _ ->
                tangtents.add(0.0f)
                tangtents.add(0.0f)
                tangtents.add(0.0f)
            }
        }

        //calcs
        for (faceIndex in 0 until facesCount) {
            //vertex 0
            val posA = createPosVec(verticesXYZ, faceIndex, 0)
            val uvA = createUvVec(texturesUV, faceIndex, 0)

            //vertex 1
            val posB = createPosVec(verticesXYZ, faceIndex, 1)
            val uvB = createUvVec(texturesUV, faceIndex, 1)

            //vertex 2
            val posC = createPosVec(verticesXYZ, faceIndex, 2)
            val uvC = createUvVec(texturesUV, faceIndex, 2)


            val edge1 = Vec3(posB.x - posA.x, posB.y - posA.y, posB.z - posA.z)
            val edge2 = Vec3(posC.x - posA.x, posC.y - posA.y, posC.z - posA.z)
            val deltaUV1 = Vec2(uvB.x - uvA.x, uvB.y - uvA.y)
            val deltaUV2 = Vec2(uvC.x - uvA.x, uvC.y - uvA.y)

            val f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y)
            val tangentX = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x)
            val tangentY = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y)
            val tangentZ = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z)


            //find all vertA face index and add tangent
            //find all vertB face index and add tangent
            //find all vertC face index and add tangent

            //vertA, vertB, vertC
            for (index in 0 until verticesXYZ.size - 2 step 3) {

                //vertA
                if (verticesXYZ[index + 0] == posA.x && verticesXYZ[index + 1] == posA.y && verticesXYZ[index + 2] == posA.z) {
                    tangtents[index + 0] += tangentX
                    tangtents[index + 1] += tangentY
                    tangtents[index + 2] += tangentZ
                }

                if (verticesXYZ[index + 0] == posB.x && verticesXYZ[index + 1] == posB.y && verticesXYZ[index + 2] == posB.z) {
                    tangtents[index + 0] += tangentX
                    tangtents[index + 1] += tangentY
                    tangtents[index + 2] += tangentZ
                }

                if (verticesXYZ[index + 0] == posC.x && verticesXYZ[index + 1] == posC.y && verticesXYZ[index + 2] == posC.z) {
                    tangtents[index + 0] += tangentX
                    tangtents[index + 1] += tangentY
                    tangtents[index + 2] += tangentZ
                }

            }
        }

        facesTangentsXYZ.add(tangtents.toFloatArray())
    }

    private fun createUvVec(
        texturesUV: FloatArray,
        faceIndex: Int,
        vertexInFaceIndex: Int
    ): Vec2 {
        val uvPerFace = VERTEX_PER_FACE * TEXTURE_CORDS_PER_VERTEX
        val moveIndex = vertexInFaceIndex * TEXTURE_CORDS_PER_VERTEX

        val uvBx = texturesUV[faceIndex * uvPerFace + moveIndex]
        val uvBy = texturesUV[faceIndex * uvPerFace + moveIndex + 1]
        return Vec2(uvBx, uvBy)
    }

    private fun createPosVec(
        verticesXYZ: FloatArray,
        faceIndex: Int,
        vertexInFaceIndex: Int
    ): Vec3<Float> {
        val cordsPerFace = VERTEX_PER_FACE * CORDS_PER_VERTEX
        val moveIndex = vertexInFaceIndex * CORDS_PER_VERTEX

        val vertX = verticesXYZ[faceIndex * cordsPerFace + moveIndex]
        val vertY = verticesXYZ[faceIndex * cordsPerFace + moveIndex + 1]
        val vertZ = verticesXYZ[faceIndex * cordsPerFace + moveIndex + 2]
        return Vec3(vertX, vertY, vertZ)
    }


    private var positionHandle = 0
    private var normalHandle = 0
    private var textureMappingHandle = 0
    private var skinJointHandle = 0
    private var skinWeightHandle = 0
    private var tangentsHandle = 0

    private val vertexStride = CORDS_PER_VERTEX * FLOAT_SIZE_IN_BYTES
    private val texStride = TEX_COMPONENTS_PER_VERTEX * FLOAT_SIZE_IN_BYTES
    private val jointStride = SKIN_JOINT_PER_VERTEX * INT_SIZE_IN_BYTES
    private val weightStride = SKIN_WEIGHT_PER_VERTEX * FLOAT_SIZE_IN_BYTES
    private val tangentsStride = TANGENTS_PER_VERTEX * FLOAT_SIZE_IN_BYTES

    fun draw(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        invertedViewMatrix: FloatArray
    ) {

        GLES20.glUseProgram(program)

        val theta = System.currentTimeMillis() / 1000.0
        val r = 50
        val x = r * cos(theta).toFloat()
        val z = r * sin(theta).toFloat()

        val lightLocationXHandle = glGetUniformLocation(program, "u_lightLocationX")
        val lightLocationZHandle = glGetUniformLocation(program, "u_lightLocationZ")
        val jointMatHandle = glGetUniformLocation(program, "u_jointMat")

        GLES20.glUniform1f(lightLocationXHandle, x)
        GLES20.glUniform1f(lightLocationZHandle, z)

        //JOINTS ROTATION!!!!!
        val firstModel = models?.first()
        val inverseBindMatrices = firstModel?.inverseBindMatrices!!


        val jointsCount = inverseBindMatrices.size / MATRIX_ELEMENT_COUNT
        val jointsOrder = models?.first()?.jointsOrder

        val jointMatFloatList = mutableListOf<Float>()
        val transformedMatrixMap = mutableMapOf<Int, FloatArray>()

        if (jointsOrder != null) {

            (0 until jointsCount).forEach { index ->
                val node = firstModel.nodes[jointsOrder[index]]

                val joint = inverseBindMatrices.toFloatArray().copyOfRange(
                    index * MATRIX_ELEMENT_COUNT,
                    (index + 1) * MATRIX_ELEMENT_COUNT
                )

                val transformMatrix = FloatArray(MATRIX_ELEMENT_COUNT)
                var transformedMatrix = FloatArray(MATRIX_ELEMENT_COUNT)
                val resultMatrix = FloatArray(MATRIX_ELEMENT_COUNT)

                val tx = (node.translation?.get(0) ?: 0.0).toFloat()
                val ty = (node.translation?.get(1) ?: 0.0).toFloat()
                val tz = (node.translation?.get(2) ?: 0.0).toFloat()

                //ROTATION
                val rx = (node.rotation?.get(0) ?: 0.0).toFloat()
                val ry = (node.rotation?.get(1) ?: 0.0).toFloat()
                val rz = (node.rotation?.get(2) ?: 0.0).toFloat()
                val rw = (node.rotation?.get(3) ?: 0.0).toFloat()

                val sx = node.scale?.get(0) ?: 1.0
                val sy = node.scale?.get(1) ?: 1.0
                val sz = node.scale?.get(2) ?: 1.0

                //create translate matrix
                val translateMatrix = advanceMath.identityMatrix()
                Matrix.translateM(translateMatrix, 0, tx, ty, tz)

                //create rotation matrix
//                val rotationMatrix = advanceMath.identityMatrix()
//                Matrix.setRotateM(rotationMatrix, 0, 20.0f, 0.0f, 1.0f, 0.0f)
                val rotationMatrix = advanceMath.quaterionToRotationMatrix(Vec4(rx, ry, rz, -rw))

                //create scale matrix
                val scaleMatrix = advanceMath.identityMatrix()
                Matrix.scaleM(scaleMatrix, 0, sx.toFloat(), sy.toFloat(), sz.toFloat())

                //calculations
                Matrix.multiplyMM(transformMatrix, 0, translateMatrix, 0, rotationMatrix, 0)
                Matrix.multiplyMM(transformedMatrix, 0, translateMatrix, 0, scaleMatrix, 0)

                //--------------------------------------------------------
//                find parent index or return -1
                var parentIndex = -1
                val currentNodeIndex = jointsOrder[index]
                firstModel.nodes.forEachIndexed { nodeIndex, node ->
                    val children = node.children
                    if (children?.find { it == currentNodeIndex } != null) {
                        parentIndex = nodeIndex
                    }
                }

                val parentTransformMatrix = transformedMatrixMap[parentIndex]
                if (parentTransformMatrix != null) {
                    Matrix.multiplyMM(
                        transformedMatrix,
                        0,
                        parentTransformMatrix,
                        0,
                        transformMatrix,
                        0
                    )
                } else {

                    transformedMatrix = transformMatrix.clone()

                }
                //--------------------------------------------------------

                transformedMatrixMap[jointsOrder[index]] = transformedMatrix.clone()


                Matrix.multiplyMM(resultMatrix, 0, transformedMatrix, 0, joint, 0)


                resultMatrix.forEach {
                    jointMatFloatList.add(it)
                }
            }


            val jointMat =
                ByteBuffer.allocateDirect((jointMatFloatList.size) * FLOAT_SIZE_IN_BYTES)
                    .run {
                        order(ByteOrder.nativeOrder())
                        asFloatBuffer().apply {
                            put(jointMatFloatList.toFloatArray())
                            position(0)
                        }
                    }

            GLES20.glUniformMatrix4fv(
                jointMatHandle,
                jointMatFloatList.size / MATRIX_ELEMENT_COUNT,
                false,
                jointMat
            )

        }





        models?.forEachIndexed lit2@{ index, model ->

            if (index > readModelsMax) return@lit2

            Log.v("glGetAttribLocation", "index: $index")

            positionHandle = GLES20.glGetAttribLocation(program, "a_Position").also {
                Log.v(
                    "glGetAttribLocation",
                    "a_Position: $it, ${facesVerticesXYZ[index]?.size ?: 0}"
                )

                GLES20.glEnableVertexAttribArray(it)
                GLES20.glVertexAttribPointer(
                    it,
                    CORDS_PER_VERTEX,
                    GLES20.GL_FLOAT,
                    false,
                    vertexStride,
                    getVertexCordBuffer(index)
                )
            }

            if (facesNormalsXYZ.getOrNull(index)?.size ?: 0 > 0) {
                normalHandle = GLES20.glGetAttribLocation(program, "a_Normal").also {
                    Log.v(
                        "glGetAttribLocation",
                        "a_Normal: $it, ${facesNormalsXYZ[index]?.size ?: 0}"
                    )
                    GLES20.glEnableVertexAttribArray(it)
                    GLES20.glVertexAttribPointer(
                        it,
                        CORDS_PER_VERTEX,
                        GLES20.GL_FLOAT,
                        false,
                        vertexStride,
                        getVertexNormalBuffer(index)
                    )
                }
            }


//            if (facesTangentsXYZ.size > index && facesTangentsXYZ[index]?.size ?: 0 > 0) {
//                tangentsHandle = GLES20.glGetAttribLocation(program, "a_Tangent").also {
//                    GLES20.glEnableVertexAttribArray(it)
//                    GLES20.glVertexAttribPointer(
//                        it,
//                        TANGENTS_PER_VERTEX,
//                        GLES20.GL_FLOAT,
//                        false,
//                        tangentsStride,
//                        getTangentsBuffer(index)
//                    )
//                }
//            }

            if (facesTexturesUvXY.getOrNull(index)?.size ?: 0 > 0) {
                textureMappingHandle =
                    GLES20.glGetAttribLocation(program, "a_TextureMapping").also {
                        Log.v(
                            "glGetAttribLocation",
                            "a_TextureMapping: $it, ${facesTexturesUvXY[index]?.size ?: 0}"
                        )
                        GLES20.glEnableVertexAttribArray(it)
                        GLES20.glVertexAttribPointer(
                            it,
                            TEXTURE_CORDS_PER_VERTEX,
                            GLES20.GL_FLOAT,
                            false,
                            texStride,
                            getTextureMappingBuffer(index)
                        )
                    }
            }

            if (facesSkinJointsXYZW[index]?.size ?: 0 > 0) {
                skinJointHandle = GLES20.glGetAttribLocation(program, "a_SkinJoint").also {
                    Log.v(
                        "glGetAttribLocation",
                        "a_SkinJoint: $it, ${facesSkinJointsXYZW[index]?.size ?: 0}"
                    )
                    GLES20.glEnableVertexAttribArray(it)
                    GLES20.glVertexAttribPointer(
                        it,
                        SKIN_JOINT_PER_VERTEX,
                        GLES20.GL_INT,
                        false,
                        jointStride,
                        getSkinJointBuffer(index)
                    )
                }
            }


            if (facesSkinWeightsXYZW[index]?.size ?: 0 > 0) {
                skinWeightHandle = GLES20.glGetAttribLocation(program, "a_SkinWeight").also {
                    Log.v(
                        "glGetAttribLocation",
                        "a_SkinWeight: $it, ${facesSkinWeightsXYZW[index]?.size ?: 0}"
                    )
                    GLES20.glEnableVertexAttribArray(it)
                    GLES20.glVertexAttribPointer(
                        it,
                        SKIN_WEIGHT_PER_VERTEX,
                        GLES20.GL_FLOAT,
                        false,
                        weightStride,
                        getSkinWeightBuffer(index)
                    )
                }
            }


            //bind texture
            val textureUniformHandle = glGetUniformLocation(program, "u_Texture")
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fileNameTextureIDMap[model.textureUri] ?: 0)
            GLES20.glUniform1i(textureUniformHandle, 0)

            //bind normal texture
            val normalTextureUniformHandle = glGetUniformLocation(program, "u_normalTexture")
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
            GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D,
                fileNameTextureIDMap[model.normalTextureUri] ?: 0
            )
            GLES20.glUniform1i(normalTextureUniformHandle, 1)

            //bind roughness texture
            val roughnessTextureUniformHandle = glGetUniformLocation(program, "u_roughnessTexture")
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
            GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D,
                fileNameTextureIDMap[model.roughnessTextureUri] ?: 0
            )
            GLES20.glUniform1i(roughnessTextureUniformHandle, 2)


            // get handle to shape's transformation matrix
            modelMatrixHandle = glGetUniformLocation(program, "u_modelMatrix")
            viewMatrixHandle = glGetUniformLocation(program, "u_viewMatrix")
            projectionMatrixHandle = glGetUniformLocation(program, "u_projectionMatrix")
            cameraPosHandle = glGetUniformLocation(program, "u_cameraPosVec3")
            baseColorFactorHandle = glGetUniformLocation(program, "u_baseColorFactorVec4")
            val cameraPos =
                Vec3(invertedViewMatrix[12], invertedViewMatrix[13], invertedViewMatrix[14])

            val baseColorFactor = Vec4(
                model.baseColorFactor?.getOrNull(0) ?: -1.0f,
                model.baseColorFactor?.getOrNull(1) ?: -1.0f,
                model.baseColorFactor?.getOrNull(2) ?: -1.0f,
                model.baseColorFactor?.getOrNull(3) ?: -1.0f
            )

            // Pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, modelMatrix, 0)
            GLES20.glUniformMatrix4fv(viewMatrixHandle, 1, false, viewMatrix, 0)
            GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0)
            GLES20.glUniform3fv(cameraPosHandle, 1, cameraPos.toArray().toFloatArray(), 0)
            GLES20.glUniform4fv(
                baseColorFactorHandle,
                1,
                baseColorFactor.toArray().toFloatArray(),
                0
            )

            // Draw the triangle
            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                indices[index].size,
                GLES20.GL_UNSIGNED_INT,
                getIndicesBuffer(index)
            )

            // Disable vertex array

            GLES20.glDisableVertexAttribArray(positionHandle)
            GLES20.glDisableVertexAttribArray(normalHandle)
            GLES20.glDisableVertexAttribArray(textureMappingHandle)
            GLES20.glDisableVertexAttribArray(skinJointHandle)
            GLES20.glDisableVertexAttribArray(skinWeightHandle)
            GLES20.glDisableVertexAttribArray(tangentsHandle)
        }
    }


    private fun getVertexCordBuffer(index: Int): FloatBuffer {
        return ByteBuffer.allocateDirect((facesVerticesXYZ[index].size) * FLOAT_SIZE_IN_BYTES).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(facesVerticesXYZ[index])
                position(0)
            }
        }
    }

    private fun getVertexNormalBuffer(index: Int): FloatBuffer {
        return ByteBuffer.allocateDirect((facesNormalsXYZ[index]?.size ?: 0) * FLOAT_SIZE_IN_BYTES)
            .run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(facesNormalsXYZ[index])
                    position(0)
                }
            }
    }

    private fun getTangentsBuffer(index: Int): FloatBuffer {
        return ByteBuffer.allocateDirect(
            (facesTangentsXYZ[index]?.size ?: 0) * FLOAT_SIZE_IN_BYTES
        )
            .run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(facesTangentsXYZ[index])
                    position(0)
                }
            }
    }

    private fun getTextureMappingBuffer(index: Int): FloatBuffer {
        return ByteBuffer.allocateDirect(
            (facesTexturesUvXY[index]?.size ?: 0) * FLOAT_SIZE_IN_BYTES
        )
            .run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(facesTexturesUvXY[index])
                    position(0)
                }
            }
    }

    private fun getIndicesBuffer(index: Int): IntBuffer {
        return ByteBuffer.allocateDirect(indices[index].size * INT_SIZE_IN_BYTES).run {
            order(ByteOrder.nativeOrder())

            asIntBuffer().apply {
                put(indices[index])
                position(0)
            }
        }
    }

    private fun getSkinJointBuffer(index: Int): IntBuffer {
        return ByteBuffer.allocateDirect(
            (facesSkinJointsXYZW[index]?.size ?: 0) * INT_SIZE_IN_BYTES
        )
            .run {
                order(ByteOrder.nativeOrder())

                asIntBuffer().apply {
                    put(facesSkinJointsXYZW[index])
                    position(0)
                }
            }
    }

    private fun getSkinWeightBuffer(index: Int): FloatBuffer {
        return ByteBuffer.allocateDirect(
            (facesSkinWeightsXYZW[index]?.size ?: 0) * FLOAT_SIZE_IN_BYTES
        )
            .run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(facesSkinWeightsXYZW[index])
                    position(0)
                }
            }
    }

    companion object {
        val advanceMath = AdvanceMath()
    }
}