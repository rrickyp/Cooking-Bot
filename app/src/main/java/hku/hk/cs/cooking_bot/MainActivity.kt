package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val temp = arrayListOf<String>("aaa")
        val intent = Intent(this, landing_page::class.java).apply {
            putStringArrayListExtra("data", temp)
        }
        startActivity(intent)
    }
}