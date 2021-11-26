package pl.adambartkowiak.rectanglegame.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_select_test.*
import pl.adambartkowiak.rectanglegame.ModelPathName
import pl.adambartkowiak.rectanglegame.R
import java.util.ArrayList

class ModelListActivity : AppCompatActivity() {

    private val modelList by lazy { intent.getParcelableArrayListExtra<ModelPathName>(EXTRA_MODEL_LIST) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_test)

        modelList?.forEach { test ->
            val button = Button(this)
            button.text = test.fileName
            button.setOnClickListener {
                startActivity(GameActivity.getStartIntent(this, test.path, test.fileName))
            }

            contentContainer.addView(button)
        }
    }

    companion object {
        private const val EXTRA_MODEL_LIST = "EXTRA_MODEL_LIST"

        fun getStartIntent(context: Context, modelList: ArrayList<ModelPathName>): Intent {
            val intent = Intent(context, ModelListActivity::class.java)
            intent.putParcelableArrayListExtra(EXTRA_MODEL_LIST, modelList)
            return intent
        }
    }
}
