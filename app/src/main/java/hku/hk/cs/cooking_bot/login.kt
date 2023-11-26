package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class login : AppCompatActivity() {
    private var goToRegister: TextView? = null
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        goToRegister = findViewById(R.id.goToRegister)
        usernameEditText = findViewById(R.id.editTextTextUsernameL)
        passwordEditText = findViewById(R.id.editTextTextPasswordL)
        loginButton = findViewById(R.id.login)
        goToRegister!!.setOnClickListener {
            val intent = Intent(this, register::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Create a POST request with username and password
            val jsonObject = JSONObject()
            jsonObject.put("username", username)
            jsonObject.put("password", password)

            val queue = Volley.newRequestQueue(this)
            val url = "http://10.68.60.178:8080/login"

            val request = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    // TODO: Handle the server response
                    if (response.getBoolean("success")) {
                        // Start the Home activity if the login is successful
                        val temp = arrayListOf<String>(username)
                        val intent = Intent(this, Home::class.java).apply {
                            putStringArrayListExtra("user_data", temp)
                        }
                        startActivity(intent)
                    } else {
                        // Show an error message if the login is unsuccessful
                        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    // TODO: Handle the error
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            )

            queue.add(request)
        }
    }
}