package pl.adambartkowiak.rectanglegame.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import pl.adambartkowiak.rectanglegame.ModelPathName
import pl.adambartkowiak.rectanglegame.R

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        selectModelButton.setOnClickListener {
            startActivity(ModelListActivity.getStartIntent(this, gameModelList))
        }

        selectTestButton.setOnClickListener {
            startActivity(ModelListActivity.getStartIntent(this, testModelList))
        }

        objButton.setOnClickListener {
            startActivity(DebugModelActivity.getStartIntent(this))
        }

        exitButton.setOnClickListener {
            finish()
        }
    }

    private val gameModelList = ArrayList<ModelPathName>().apply {
        add(ModelPathName("imports/normal_rog_test/", "normal_rog_test.gltf"))
        add(ModelPathName("imports/fireman1/", "map1.gltf"))
        add(ModelPathName("imports/shapes_001a/", "shapes_001a.gltf"))
    }

    private val testModelList = ArrayList<ModelPathName>().apply {
        add(ModelPathName("test/TriangleWithoutIndices/glTF/", "TriangleWithoutIndices.gltf"))
        add(ModelPathName("test/Triangle/glTF/", "Triangle.gltf"))
        add(ModelPathName("test/AnimatedTriangle/glTF/", "AnimatedTriangle.gltf"))
        add(ModelPathName("test/AnimatedMorphCube/glTF/", "AnimatedMorphCube.gltf"))
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, MainMenuActivity::class.java)
        }
    }
}
