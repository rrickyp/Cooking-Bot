package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class register : AppCompatActivity() {
    private var goToLogin: TextView? = null
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        goToLogin = findViewById(R.id.goToLogin)
        usernameEditText = findViewById(R.id.editTextTextUsernameR)
        passwordEditText = findViewById(R.id.editTextTextPasswordR)
        registerButton = findViewById(R.id.register)
        goToLogin!!.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Create a POST request with username and password
            val jsonObject = JSONObject()
            jsonObject.put("username", username)
            jsonObject.put("password", password)

            val queue = Volley.newRequestQueue(this)
            val url = "http://10.68.60.178:8080/register"

            val request = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    // TODO: Handle the server response
                    if (response.getBoolean("success")) {
                        // Start the Home activity if the login is successful
                        val temp = arrayListOf<String>("aaa")
                        val intent = Intent(this, login::class.java).apply {
                            putStringArrayListExtra("data", temp)
                        }
                        startActivity(intent)
                        println("haaaaaaaa")
                    } else {
                        // Show an error message if the login is unsuccessful
                        Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    // TODO: Handle the error
                    Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show()
                }
            )

            queue.add(request)
        }
    }
}