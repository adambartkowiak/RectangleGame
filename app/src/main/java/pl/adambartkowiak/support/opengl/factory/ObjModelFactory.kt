package pl.adambartkowiak.support.opengl.factory

import pl.adambartkowiak.support.opengl.model.*
import java.io.InputStream
import java.lang.Exception

class ObjModelFactory() {
    fun create(inputStream: InputStream): List<Model> {
        val vertices = arrayListOf<Vec3<Float>>()
        val faces = arrayListOf<Face>()
        val normals = arrayListOf<Vec3<Float>>()
        val textureCoords = arrayListOf<Vec2>()

        inputStream.bufferedReader().use { reader ->
            reader.forEachLine {
                when {
                    isVertex(it) -> addVertices(it, vertices)
                    isFace(it) -> addFaces(it, faces)
                    isNormal(it) -> addNormals(it, normals)
                    isTextureCoord(it) -> addTextureCoords(it, textureCoords)
                }
            }
            reader.close()
        }

        val model = Model(
            vertices,
            faces,
            normals,
            textureCoords = textureCoords,
            textureUri = null
        )

        return listOf(model)
    }

    private fun isVertex(it: String) = it.startsWith("v ")

    private fun isTextureCoord(it: String) = it.startsWith("vt ")

    private fun isNormal(it: String) = it.startsWith("vn ")

    private fun isFace(it: String) = it.startsWith("f ")

    private fun addFaces(it: String, indexes: ArrayList<Face>) {
        try {
            val (_, xIndexes, yIndexes, zIndexes) = it.split(" ")
            val (v1, vt1, vn1) = xIndexes.split("/")
            val (v2, vt2, vn2) = yIndexes.split("/")
            val (v3, vt3, vn3) = zIndexes.split("/")

            indexes.add(
                Face(
                    Vertex(
                        v1.toInt() - 1,
                        vt1.toInt() - 1,
                        vn1.toInt() - 1
                    ),
                    Vertex(
                        v2.toInt() - 1,
                        vt2.toInt() - 1,
                        vn2.toInt() - 1
                    ),
                    Vertex(
                        v3.toInt() - 1,
                        vt3.toInt() - 1,
                        vn3.toInt() - 1
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addVertices(it: String, vertices: ArrayList<Vec3<Float>>) {
        try {
            val (_, x, y, z) = it.split(" ")
            vertices.add(
                Vec3(
                    x.toFloat(),
                    y.toFloat(),
                    z.toFloat()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addNormals(it: String, normals: ArrayList<Vec3<Float>>) {
        try {
            val (_, x, y, z) = it.split(" ")
            normals.add(
                Vec3(
                    x.toFloat(),
                    y.toFloat(),
                    z.toFloat()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addTextureCoords(it: String, textureCoords: java.util.ArrayList<Vec2>) {
        try {
            val (_, x, y) = it.split(" ")
            textureCoords.add(
                Vec2(
                    x.toFloat(),
                    y.toFloat()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}