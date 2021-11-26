package pl.adambartkowiak.support.opengl.repository

import android.content.res.AssetManager

class AssetsRepository(private val assetsManager: AssetManager) {
    fun open(fileName: String) = assetsManager.open(fileName)
}