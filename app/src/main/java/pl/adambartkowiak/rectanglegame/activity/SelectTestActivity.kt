package pl.adambartkowiak.rectanglegame.activity

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_select_test.*
import pl.adambartkowiak.rectanglegame.R

class SelectTestActivity : AppCompatActivity() {

    val tests = mutableListOf<Pair<String, String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_test)

        tests.add(Pair("test/TriangleWithoutIndices/glTF/", "TriangleWithoutIndices.gltf"))
        tests.add(Pair("test/Triangle/glTF/", "Triangle.gltf"))
        tests.add(Pair("test/AnimatedTriangle/glTF/", "AnimatedTriangle.gltf"))
        tests.add(Pair("test/AnimatedMorphCube/glTF/", "AnimatedMorphCube.gltf"))



        tests.forEach { test ->
            val button = Button(this)
            button.text = test.second
            button.setOnClickListener {
                startActivity(GameActivity.getStartIntent(this, test.first, test.second))
            }

            contentContainer.addView(button)
        }
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, SelectTestActivity::class.java)
        }
    }
}
