package pl.adambartkowiak.rectanglegame.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_obj.*
import pl.adambartkowiak.rectanglegame.R
import pl.adambartkowiak.support.opengl.model.Model
import pl.adambartkowiak.support.opengl.factory.ModelFactory
import pl.adambartkowiak.support.opengl.repository.AssetsRepository

class DebugModelActivity : AppCompatActivity() {

    private var obj: Model? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obj)

        filarButton.setOnClickListener {

            val stream = assets.open("test/TriangleWithoutIndices/glTF/TriangleWithoutIndices.gltf")
            val assetsRepository = AssetsRepository(assets)
            obj =
                ModelFactory(assetsRepository, "test/TriangleWithoutIndices/glTF/").create(stream, ModelFactory.ModelFormat.FORMAT_GLTF)
                    .first()

            val stringBuffer = StringBuffer()
            stringBuffer.append("Vertices\n")
            obj?.vertices?.forEachIndexed { index, vertex ->
                stringBuffer.append("$index:  ${vertex.x}  ${vertex.y}  ${vertex.z}\n")
            }
            stringBuffer.append("\nFaces\n")
            obj?.faces?.forEachIndexed { index, face ->
                stringBuffer.append("$index:  ${face.p1}  ${face.p2}  ${face.p3}\n")
            }
            stringBuffer.append("\nNormals\n")
            obj?.normals?.forEachIndexed { index, face ->
                stringBuffer.append("$index:  ${face.x}  ${face.y}  ${face.z}\n")
            }

            stringBuffer.append("\nJoints\n")
            obj?.joints?.forEachIndexed { index, joint ->
                stringBuffer.append("$index:  ${joint.x}  ${joint.y}  ${joint.z} ${joint.w}\n")
            }

            stringBuffer.append("\nWeights\n")
            obj?.weights?.forEachIndexed { index, weight ->
                stringBuffer.append("$index:  ${weight.x}  ${weight.y}  ${weight.z} ${weight.w}\n")
            }


            stringBuffer.append("\nJoint Matrice\n")
            obj?.inverseBindMatrices?.forEachIndexed { index, matriceVal ->
                stringBuffer.append("$index:  ${matriceVal}\n")
            }


            text.text = stringBuffer.toString()
        }
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, DebugModelActivity::class.java)
        }
    }
}
