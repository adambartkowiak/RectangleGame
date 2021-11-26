package pl.adambartkowiak.rectanglegame.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import pl.adambartkowiak.rectanglegame.GameGLSurfaceView
import pl.adambartkowiak.support.device.Utils
import pl.adambartkowiak.support.opengl.factory.ModelFactory
import pl.adambartkowiak.support.opengl.repository.AssetsRepository

class GameActivity : AppCompatActivity() {

    private val path by lazy { intent.getStringExtra(EXTRA_PATH) }
    private val fileName by lazy { intent.getStringExtra(EXTRA_FILE_NAME) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        Utils.init(this)

        //GLTF
        val assetsRepository = AssetsRepository(assets)
        val stream = assets.open("$path$fileName")
        val model =
            ModelFactory(assetsRepository, path!!).create(
                stream,
                ModelFactory.ModelFormat.FORMAT_GLTF
            )
        val gameView = GameGLSurfaceView(this)

        gameView.setData(model)
        gameView.initialize()
        setContentView(gameView)
    }

    companion object {
        private const val EXTRA_PATH = "EXTRA_PATH"
        private const val EXTRA_FILE_NAME = "EXTRA_FILE_NAME"

        fun getStartIntent(context: Context, path: String, fileName: String): Intent {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(EXTRA_PATH, path)
            intent.putExtra(EXTRA_FILE_NAME, fileName)
            return intent
        }
    }
}