package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SavedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved)

        val intent = getIntent()
        val data = intent.getStringArrayListExtra("user_data")

        val home_view = findViewById(R.id.home_icon) as ImageView
        home_view.setOnClickListener {

            val goBackToHome = Intent(this, Home::class.java)
            goBackToHome.putStringArrayListExtra("user_data", data)
            startActivity(goBackToHome)
            finish()

        }

        val profile_button = findViewById(R.id.setting_icon) as ImageView
        profile_button.setOnClickListener {

            val goToProfileActivity = Intent(this, ProfileActivity::class.java)
            goToProfileActivity.putStringArrayListExtra("user_data", data)
            startActivity(goToProfileActivity)
            finish()

        }
    }
}