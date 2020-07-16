package r2.adam.bashfullib

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import r2.adam.bashful.BashfulSelectListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bashful.setBashfulSelectListener(object : BashfulSelectListener {
            override fun click1() {
                Toast.makeText(this@MainActivity, "Click 1", Toast.LENGTH_SHORT).show()

            }

            override fun click2() {
                Toast.makeText(this@MainActivity, "Click 2", Toast.LENGTH_SHORT).show()
            }

            override fun click3() {
                Toast.makeText(this@MainActivity, "Click 3", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
