package hku.hk.cs.cooking_bot

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ScanPage : AppCompatActivity() {
    private var btn_camera: Button? = null
    private var btn_gallery: Button? = null
    private var btn_scan: Button? = null
    private var generate_recipe: LinearLayout? = null
    private var scanSuccessful: Boolean? = false
    private var bitmap: Bitmap? = null
    private var url:String? = "http://10.68.60.178:8080/recognize"
    private var ingredients = arrayListOf<String>()
    private var imageView: ImageView? = null
    private var matchingFoods = ArrayList<String>()
    private lateinit var recyclerViewScanResults: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_page)

        val intent = getIntent()
        val data = intent.getStringArrayListExtra("user_data")

        btn_camera = findViewById(R.id.btn_camera)
        btn_gallery = findViewById(R.id.btn_gallery)
        btn_scan = findViewById(R.id.scanButton)
        imageView = findViewById(R.id.imageView)
        btn_camera!!.setOnClickListener {
            getCamera()
        }
        btn_gallery!!.setOnClickListener {
            getGallery()
        }
        btn_scan!!.setOnClickListener {
            uploadImage()
        }

        generate_recipe = findViewById(R.id.generateButton) as LinearLayout
        updateGenerateButtonState()

        generate_recipe!!.setOnClickListener {
            if (scanSuccessful == true) {
                val goToRecommendation = Intent(this, Recommendation::class.java)
                goToRecommendation.putStringArrayListExtra("matchingFoods", matchingFoods)
                goToRecommendation.putStringArrayListExtra("user_data", data)
                startActivity(goToRecommendation)
            }
        }

        val click_cancel_button = findViewById(R.id.cancel_button) as TextView
        click_cancel_button.setOnClickListener {

            finish()

        }

    }

    private fun getCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 8)
    }

    private fun getGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 7)
    }

    private fun updateGenerateButtonState() {
        if (scanSuccessful == true) {
            generate_recipe!!.isClickable = true
            generate_recipe!!.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF9A3D"))
        } else {
            generate_recipe!!.isClickable = false
            generate_recipe!!.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ECECEC"))
        }
    }

    private fun uploadImage() {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val ingredientsArray = jsonObject.getJSONArray("ingredients")
                    val matchingFoodsArray = jsonObject.getJSONArray("matching_foods")

                    ingredients.clear()
                    matchingFoods.clear()

                    for (i in 0 until ingredientsArray.length()) {
                        ingredients.add(ingredientsArray.getString(i))
                    }

                    for (i in 0 until matchingFoodsArray.length()) {
                        matchingFoods.add(matchingFoodsArray.getString(i))
                    }

                    println("Ingredients: $ingredients")
                    println("Matching Foods: $matchingFoods")

                    scanSuccessful = true
                    updateIngredientsTextView(ingredients) // Update the UI with the retrieved ingredients
                    updateGenerateButtonState()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
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

    private fun updateIngredientsTextView(ingredients: List<String>) {
        recyclerViewScanResults = findViewById(R.id.recyclerViewScanResults)
        // val ingredientsText = ingredients.joinToString("\n")
        // tvIngredients.text = ingredientsText
        val layoutManager = GridLayoutManager(this, 3) // Set the desired number of columns
        recyclerViewScanResults.layoutManager = layoutManager

        val adapter = ScanResultAdapter(this, ingredients)
        recyclerViewScanResults.adapter = adapter

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
        // Set the bitmap to the ImageView
        imageView?.setImageBitmap(bitmap)
    }

    private fun imageToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()
        val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return encoded
    }
}