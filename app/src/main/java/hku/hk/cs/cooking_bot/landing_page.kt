package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class landing_page : AppCompatActivity() {
    private var loginButton: TextView? = null
    private var registerButton: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_landing_page)
        // Assuming your login button has the id "loginButton"
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        loginButton!!.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }
        registerButton!!.setOnClickListener {
            val intent = Intent(this, register::class.java)
            startActivity(intent)
            finish()
        }
    }
}