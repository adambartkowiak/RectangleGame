package pl.adambartkowiak.support.opengl

import android.opengl.GLES20
import android.util.Log
import java.nio.IntBuffer

object ShaderHelper {

    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun validateShader(program: Int, programName: String? = "") {
        val validationResult = IntBuffer.allocate(1)
        GLES20.glValidateProgram(program)
        GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, validationResult)

        if (validationResult[0] == GLES20.GL_FALSE) {
            Log.e("shader", "Failed to validate shader program: $programName")
        } else {
            Log.e("shader", "Validation shader program successful: $programName")
        }
    }
}