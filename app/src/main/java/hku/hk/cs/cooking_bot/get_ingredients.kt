package hku.hk.cs.cooking_bot

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import org.json.JSONArray
import org.json.JSONObject

class get_ingredients : AppCompatActivity() {
    private var btn_camera: Button? = null
    private var btn_gallery: Button? = null
    private var btn_upload: Button? = null
    private var bitmap:Bitmap? = null
    private var url:String? = "http://10.70.56.177:5000/recognize"
    private var ingredients = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_ingredients)
        btn_camera = findViewById(R.id.btn_camera)
        btn_gallery = findViewById(R.id.btn_gallery)
        btn_upload = findViewById(R.id.upload)
        btn_camera!!.setOnClickListener {
            getCamera()
        }
        btn_gallery!!.setOnClickListener {
            getGallery()
        }
        btn_upload!!.setOnClickListener {
            uploadImage()
        }
    }
    fun getGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 7)


    }
    fun getCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 8)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 7 && resultCode == RESULT_OK && data != null) {
            val path: Uri? = data.data
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, path)
        }
        if (requestCode == 8 && resultCode == RESULT_OK && data != null) {
            bitmap = data.extras!!.get("data") as Bitmap
        }
    }
    fun uploadImage() {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                    val jsonArray = JSONArray(response)
                    // insert the name into the array
                    println(jsonArray.length())
                    for (i in 0 until jsonArray.length()){
                        val jsonObj = jsonArray.getJSONObject(i)
                        ingredients.add(jsonObj.get("name") as String)
                    }
                println(ingredients)
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["image"] = imageToString(bitmap!!)
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
    private fun imageToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()
        val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return encoded
    }
}