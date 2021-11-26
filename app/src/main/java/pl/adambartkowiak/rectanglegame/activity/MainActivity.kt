package pl.adambartkowiak.rectanglegame.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import pl.adambartkowiak.rectanglegame.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        startGameButton.setOnClickListener {
            startActivity(
//                GameActivity.getStartIntent(
//                    this,
//                    "imports/normal_rog_test/",
//                    "normal_rog_test.gltf"
//                )
                GameActivity.getStartIntent(
                    this,
                    "imports/fireman1/",
                    "map1.gltf"
                )
//                GameActivity.getStartIntent(
//                    this,
//                    "imports/shapes_001a/",
//                    "shapes_001a.gltf"
//                )
            )
        }

        selectTestButton.setOnClickListener {
            startActivity(SelectTestActivity.getStartIntent(this))
        }

        objButton.setOnClickListener {
            startActivity(ObjActivity.getStartIntent(this))
        }

        exitButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
