package hku.hk.cs.cooking_bot

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hku.hk.cs.cooking_bot.data.Constants
import org.json.JSONArray
import org.json.JSONObject

class RecommendedRecipeActivity : AppCompatActivity() {

    data class FoodData(val food_name: String, val ingredients: List<String>, val total_ingredients: Int, val serving_size: Int, val preparation_time: Int, val cooking_time: Int, val how_to_cook: String, val image_path: String, val video_path: String, val id: String)
    private var id:String= ""
    private var username:String=""
    private var saveButton_status:Boolean =  false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recommended_recipe)
        val data = intent.getStringArrayListExtra("user_data")
        // Now you can use the data
        // For example, you can print it to the log
        username = data!![0]
        val back_text_view = findViewById(R.id.back) as TextView
        val save_view = findViewById(R.id.saveView) as LinearLayout
        val bookmark = findViewById(R.id.bookmarkIcon) as ImageView
        val save_text = findViewById(R.id.saveText) as TextView
        saveButton_status = false

        val selectedFoodDataJson = intent.getStringExtra("selected_food_data")
        val selectedFoodData = Gson().fromJson(selectedFoodDataJson, FoodData::class.java)
        id = selectedFoodData?.id!!
        getSavedRecipes(username, id)

        val foodNameTextView = findViewById<TextView>(R.id.textView14)
        val totalIngredientsTextView = findViewById<TextView>(R.id.textView23)
        val servingSizeTextView = findViewById<TextView>(R.id.textView24)
        val prepTimeTextView = findViewById<TextView>(R.id.textView25)
        val cookingTimeTextView = findViewById<TextView>(R.id.textView26)

        foodNameTextView.text = selectedFoodData?.food_name ?: "Cheeseburger"
        totalIngredientsTextView.text = (selectedFoodData?.total_ingredients?.toString() ?: "8") + " ingredients"
        servingSizeTextView.text = "Serving size: " + (selectedFoodData?.serving_size?.toString() ?: "2") + " portions"
        prepTimeTextView.text = "Prep time: " + (selectedFoodData?.preparation_time?.toString() ?: "5") + " mins"
        cookingTimeTextView.text = "Cooking time: " + (selectedFoodData?.cooking_time?.toString() ?: "10") + " mins"

        val imageView = findViewById<ImageView>(R.id.imageView2)
        val imagePath = selectedFoodData?.image_path ?: "burger"
        if (imagePath != null && imagePath.isNotEmpty()) {
            val drawableId = resources.getIdentifier(imagePath, "drawable", packageName)
            if (drawableId != 0) {
                imageView.setImageResource(drawableId)
            }
            else {
                val notfound = resources.getIdentifier("logo", "drawable", packageName)
                imageView.setImageResource(notfound)
            }
        }

        val how_to_cook_data = selectedFoodData?.how_to_cook ?: "1. Cook the patty, 2. Assemble the patty with the rest of the ingredients."
        val ingredientsData= selectedFoodData?.ingredients ?: listOf("2 Buns", "100g Patty", "2 Slices of Cheese", "1 Leaf of Lettuce", "2 Slices of Tomato", "Ketchup", "Mustard", "100g Onion")
        updateIngredientsTextView(ingredientsData)
        updateCookingInstructions(how_to_cook_data)

        //for cheeseburger only
        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoName = selectedFoodData?.video_path ?: "burger_vid_tutorial"
        val videoId = resources.getIdentifier(videoName, "raw", packageName)

        if (videoId != 0) {
            val videoPath = "android.resource://$packageName/raw/$videoName"
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setVideoPath(videoPath)
            videoView.setMediaController(mediaController)
            videoView.start()
        } else {
            Toast.makeText(this, "Warning: Video not found", Toast.LENGTH_SHORT).show()
        }


        back_text_view!!.setOnClickListener {
                finish()
        }


        save_view!!.setOnClickListener {
            val url = "${Constants.BASE_URL}/save_recipe?username=${username}&recipe_id=${id}"
            val requestQueue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(
                Method.GET, url,
                Response.Listener<String> { response ->
                    // Parse the JSON response
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getBoolean("status")

                    // If the status is true, set the save_text to "Unsave recipe" and change the background color of save_view
                    if (status) {
//                        Toast.makeText(this, "Saved list updated!",Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Failed to update!",Toast.LENGTH_SHORT).show()

                    }

                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                }
            ) {}
            requestQueue.add(stringRequest)
            if (saveButton_status == true) { // unsaved recipe

                // create pop up notification
                Toast.makeText(this, "Recipe Unsaved!",Toast.LENGTH_SHORT).show()

                // change the bookmarkIcon imageView and the color of the saveView
                save_view.getBackground().setTint(Color.parseColor("#FF9A3D"));
                save_text.setTextColor(Color.parseColor("#FFFFFF"))
                save_text.setText("Save Recipe")
                bookmark.setImageResource(R.drawable.bookmark)

                saveButton_status = false

            } else { // save recipe

                // create pop up notification
                Toast.makeText(this, "Recipe Saved!",Toast.LENGTH_SHORT).show()

                // change the bookmarkIcon imageView and the color of the saveView
                save_view.getBackground().setTint(Color.parseColor("#E5E1E1"));
                save_text.setTextColor(Color.parseColor("#000000"))
                save_text.setText("Recipe Saved")
                bookmark.setImageResource(R.drawable.savedfilled)

                saveButton_status = true
            }


        }
    }


    private fun updateIngredientsTextView(ingredientsData: List<String>) {
        val tvIngredients = findViewById<TextView>(R.id.textView15)
        val tvRemainingIngredients = findViewById<TextView>(R.id.textView16)
        val bulletPoint = "\u2022 " // Bullet point character
        val maxVisibleIngredients = 5

        if (ingredientsData.size > maxVisibleIngredients) {
            val visibleIngredients = ingredientsData.take(maxVisibleIngredients)
            val remainingIngredients = ingredientsData.drop(maxVisibleIngredients)
            val visibleIngredientsText = visibleIngredients.joinToString("\n") { "$bulletPoint$it" }
            val remainingIngredientsText = remainingIngredients.joinToString("\n") { "$bulletPoint$it" }

            tvIngredients.text = visibleIngredientsText
            tvRemainingIngredients.text = remainingIngredientsText
            tvRemainingIngredients.visibility = View.VISIBLE
        } else {

            val ingredientsText = ingredientsData.joinToString("\n") { "$bulletPoint$it" }
            tvIngredients.text = ingredientsText
            tvRemainingIngredients.visibility = View.GONE
        }
    }

    private fun updateCookingInstructions(how_to_cook_data: String) {
        val instructionsTv = findViewById<TextView>(R.id.textView21)
        val instructionsText = how_to_cook_data.replace(", ", "\n")
        instructionsTv.text = instructionsText
    }
    private fun getSavedRecipes(username: String, id:String) {
        val url = "${Constants.BASE_URL}/check_save?username=${username}&recipe_id=${id}"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                // Parse the JSON response
                val jsonResponse = JSONObject(response)
                val status = jsonResponse.getBoolean("status")

                // If the status is true, set the save_text to "Unsave recipe" and change the background color of save_view
                if (status) {
                    saveButton_status = true
                    val save_text = findViewById<TextView>(R.id.saveText)
                    val save_view = findViewById<LinearLayout>(R.id.saveView)
//                    save_view.setBackgroundColor(Color.parseColor("#BDBDBD")) // Set to grey color
                    save_view.getBackground().setTint(Color.parseColor("#E5E1E1"));
                    save_text.setTextColor(Color.parseColor("#000000"))
                    save_text.setText("Recipe Saved")
                    val bookmark = findViewById(R.id.bookmarkIcon) as ImageView
                    bookmark.setImageResource(R.drawable.savedfilled)
                }
                else {
                    saveButton_status = false
                    val save_text = findViewById<TextView>(R.id.saveText)
                    val save_view = findViewById<LinearLayout>(R.id.saveView)
//                    save_view.setBackgroundColor(Color.parseColor("#BDBDBD")) // Set to grey color
                    save_view.getBackground().setTint(Color.parseColor("#FF9A3D"));
                    save_text.setTextColor(Color.parseColor("#FFFFFF"))
                    save_text.setText("Save Recipe")
                    val bookmark = findViewById(R.id.bookmarkIcon) as ImageView
                    bookmark.setImageResource(R.drawable.bookmark)
                }

            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        ) {}
        requestQueue.add(stringRequest)
    }

}