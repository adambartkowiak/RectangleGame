package pl.adambartkowiak.support.opengl.fps

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.util.Log
import pl.adambartkowiak.support.device.Utils
import pl.adambartkowiak.support.opengl.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class FpsRenderer {

    private var program: Int
    private val screenSize = Utils.screenSize()
    private val aspectX = 2.0f / screenSize.x
    private val aspectY = 2.0f / screenSize.y

    private val startX = 50.0f
    private val startY = screenSize.y - 300.0f
    private val endY = screenSize.y - 50.0f
    private val endX = 300.0f

    private val glStartX = startX * aspectX - 1.0f
    private val glStartY = startY * aspectY - 1.0f
    private val glEndY = endY * aspectY - 1.0f
    private val glEndX = endX * aspectX - 1.0f

    private var facesVerticesXYZ = floatArrayOf(
        glStartX, glEndY, 0.0f,
        glEndX, glEndY, 0.0f,
        glStartX, glStartY, 0.0f,
        glEndX, glEndY, 0.0f,
        glStartX, glStartY, 0.0f,
        glEndX, glStartY, 0.0f
    )

    var indices = (0..facesVerticesXYZ.size / 3).toList().toIntArray()

    init {
        val vertexShader = ShaderHelper.loadShader(GL_VERTEX_SHADER, FpsVertextShader.code)
        val fragmentShader = ShaderHelper.loadShader(GL_FRAGMENT_SHADER, FpsFragmentShader.code)

        program = glCreateProgram().also {
            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)
            glLinkProgram(it)
        }

        ShaderHelper.validateShader(program, javaClass.name)

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }


    private var positionHandle = 0
    private var fpsArrayHandle = 0
    private var posAndSizeHandle = 0
    private val vertexStride = CORDS_PER_VERTEX * FLOAT_SIZE_IN_BYTES

    fun draw(data: FloatArray) {
        //draw guid front of camera
        glUseProgram(program)

        val vertexBuffer = getVertexBuffer()
        positionHandle = glGetAttribLocation(program, "a_Position").also {

            glEnableVertexAttribArray(it)
            glVertexAttribPointer(
                it,
                CORDS_PER_VERTEX,
                GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
        }


        val floatBuffer = getVertexBuffer(data)
        fpsArrayHandle = glGetUniformLocation(program, "u_Array")
        glUniform1fv(fpsArrayHandle, 300, floatBuffer)

        val posAndSizeBuffer = getVertexBuffer(floatArrayOf(startX, startY, endX, endY))
        posAndSizeHandle = glGetUniformLocation(program, "u_postAndSize")
        glUniform1fv(posAndSizeHandle, 4, posAndSizeBuffer)


        // Draw the triangle
        val indicesBuffer = getIndicesBuffer()
        glDrawElements(
            GL_TRIANGLES,
            indices.size,
            GL_UNSIGNED_INT,
            indicesBuffer
        )

        glDisableVertexAttribArray(positionHandle)
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

    private fun getVertexBuffer(floatArray: FloatArray): FloatBuffer {
        return ByteBuffer.allocateDirect((floatArray.size) * FLOAT_SIZE_IN_BYTES).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(floatArray)
                position(0)
            }
        }
    }
}