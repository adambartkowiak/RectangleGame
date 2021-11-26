package pl.adambartkowiak.support.opengl.factory

import pl.adambartkowiak.support.opengl.model.Model
import pl.adambartkowiak.support.opengl.repository.AssetsRepository
import java.io.InputStream

class ModelFactory(private val assetsRepository: AssetsRepository, private val filePath: String) {

    fun create(inputStream: InputStream, modelFormat: ModelFormat): List<Model> {

        if (modelFormat == ModelFormat.FORMAT_OBJ) {
            return ObjModelFactory().create(inputStream)
        } else { //if (modelFormat == ModelFormat.FORMAT_GLTF) {
            return GlTFModelFactory(assetsRepository, filePath).create(inputStream)
        }
    }

    enum class ModelFormat {
        FORMAT_OBJ,
        FORMAT_GLTF
    }
}