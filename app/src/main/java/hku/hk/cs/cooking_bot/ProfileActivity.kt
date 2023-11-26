package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        val username = findViewById<TextView>(R.id.textView18) as TextView
        val intent = getIntent()
        val data = intent.getStringArrayListExtra("user_data")

        username.text = "@" + data!![0]

        val saved_button = findViewById(R.id.saved_icon) as ImageView
        saved_button.setOnClickListener {

            val goToSavedActivity = Intent(this, SavedActivity::class.java)
            goToSavedActivity.putStringArrayListExtra("user_data", data)
            startActivity(goToSavedActivity)
            finish()

        }

        val home_button = findViewById(R.id.home_icon) as ImageView
        home_button.setOnClickListener {

            val goToHomeActivity = Intent(this, Home::class.java)
            goToHomeActivity.putStringArrayListExtra("user_data", data)
            startActivity(goToHomeActivity)
            finish()

        }

        val logout_button = findViewById<TextView>(R.id.textView29) as TextView
        logout_button.setOnClickListener {

            val goToLandingPageActivity = Intent(this, landing_page::class.java)
            startActivity(goToLandingPageActivity)
            finish()
        }
    }
}