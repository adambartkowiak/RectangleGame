package pl.adambartkowiak.support.opengl.hud

import android.opengl.GLES20
import android.opengl.GLES20.*
import pl.adambartkowiak.support.device.Utils
import pl.adambartkowiak.support.opengl.*
import pl.adambartkowiak.support.opengl.shader.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class HudRenderer {

    private var program: Int
    private val screenSize = Utils.screenSize()
    private val aspectX = 2.0f / screenSize.x
    private val aspectY = 2.0f / screenSize.y

    private val startXa = 50.0f * aspectX - 1.0f
    private val startYa = 50.0f * aspectY - 1.0f
    private val endXa = 300.0f * aspectX - 1.0f
    private val endYa = 300.0f * aspectY - 1.0f

    private val endXb = (screenSize.x - 50.0f) * aspectX - 1.0f
    private val startYb = (50.0f) * aspectY - 1.0f
    private val startXb = (screenSize.x - 300.0f) * aspectX - 1.0f
    private val endYb = (300.0f) * aspectY - 1.0f

    private var facesVerticesXYZ = floatArrayOf(
        startXa, startYa, 0.0f,
        endXa, startYa, 0.0f,
        startXa, endYa, 0.0f,
        endXa, startYa, 0.0f,
        startXa, endYa, 0.0f,
        endXa, endYa, 0.0f,

        startXb, startYb, 0.0f,
        endXb, startYb, 0.0f,
        startXb, endYb, 0.0f,
        endXb, startYb, 0.0f,
        startXb, endYb, 0.0f,
        endXb, endYb, 0.0f
    )

    var indices = (0..facesVerticesXYZ.size / 3).toList().toIntArray()

    init {
        val vertexShader = ShaderHelper.loadShader(GLES20.GL_VERTEX_SHADER, HudVertextShader.code)
        val fragmentShader =
            ShaderHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, HudFragmentShader.code)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        ShaderHelper.validateShader(program, javaClass.name)

        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)
    }


    private var positionHandle = 0
    private val vertexStride = CORDS_PER_VERTEX * FLOAT_SIZE_IN_BYTES

    fun draw() {
        //draw guid front of camera
        glUseProgram(program)

        val vertexBuffer = getVertexBuffer()
        positionHandle = GLES20.glGetAttribLocation(program, "a_Position").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                CORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
        }

        // Draw the triangle
        val indicesBuffer = getIndicesBuffer()
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_INT,
            indicesBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun getVertexBuffer(): FloatBuffer {
        return ByteBuffer.allocateDirect(facesVerticesXYZ.size * FLOAT_SIZE_IN_BYTES)
            .run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(facesVerticesXYZ)
                    position(0)
                }
            }
    }

    private fun getIndicesBuffer(): IntBuffer {
        return ByteBuffer.allocateDirect(indices.size * INT_SIZE_IN_BYTES).run {
            order(ByteOrder.nativeOrder())

            asIntBuffer().apply {
                put(indices)
                position(0)
            }
        }
    }
}