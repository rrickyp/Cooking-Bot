package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SavedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved)

        val home_view = findViewById(R.id.home_icon) as ImageView

        home_view.setOnClickListener {

            val goBackToHome = Intent(this, Home::class.java)
            startActivity(goBackToHome)

        }
    }
}