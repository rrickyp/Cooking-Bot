package hku.hk.cs.cooking_bot

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.core.content.ContextCompat
import com.google.gson.Gson

class RecommendedRecipeActivity : AppCompatActivity() {

    data class FoodData(val food_name: String, val ingredients: List<String>, val total_ingredients: Int, val serving_size: Int, val preparation_time: Int, val cooking_time: Int, val how_to_cook: String, val image_path: String, val video_path: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recommended_recipe)

        val back_text_view = findViewById(R.id.back) as TextView
        val save_view = findViewById(R.id.saveView) as LinearLayout
        val bookmark = findViewById(R.id.bookmarkIcon) as ImageView
        val save_text = findViewById(R.id.saveText) as TextView

        var saveButton_status = false

        val selectedFoodDataJson = intent.getStringExtra("selected_food_data")
        val selectedFoodData = Gson().fromJson(selectedFoodDataJson, FoodData::class.java)

        val foodNameTextView = findViewById<TextView>(R.id.textView14)
        val totalIngredientsTextView = findViewById<TextView>(R.id.textView23)
        val servingSizeTextView = findViewById<TextView>(R.id.textView24)
        val prepTimeTextView = findViewById<TextView>(R.id.textView25)
        val cookingTimeTextView = findViewById<TextView>(R.id.textView26)

        foodNameTextView.text = selectedFoodData.food_name
        totalIngredientsTextView.text = selectedFoodData.total_ingredients.toString() + " ingredients"
        servingSizeTextView.text = "Serving size: " + selectedFoodData.serving_size.toString() + " portions"
        prepTimeTextView.text = "Prep time: " + selectedFoodData.preparation_time.toString() + " mins"
        cookingTimeTextView.text = "Cooking time: " + selectedFoodData.cooking_time.toString() + " mins"

        val imageView = findViewById<ImageView>(R.id.imageView2)
        val imagePath = selectedFoodData.image_path
        println("IMGPATH : $imagePath")
        if (imagePath != null && imagePath.isNotEmpty()) {
            val drawableId = resources.getIdentifier(imagePath, "drawable", packageName)
            if (drawableId != 0) {
                imageView.setImageResource(drawableId)
            }
        }


        updateIngredientsTextView(selectedFoodData)
        updateCookingInstructions(selectedFoodData)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoName = selectedFoodData?.video_path
        println("videoName: $videoName")
        val videoPath = "android.resource://" + packageName + "/raw/" + videoName
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setVideoPath(videoPath)
        videoView.setMediaController(mediaController)
        videoView.start()

        back_text_view!!.setOnClickListener {

            // setContentView(R.layout.activity_recommendation);
            val goBackToReccomendation = Intent(this, Recommendation::class.java)
            startActivity(goBackToReccomendation)

        }


        save_view!!.setOnClickListener {

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
                // GRIZ tolong bantu atur warnanya ya
                save_view.getBackground().setTint(Color.parseColor("#E5E1E1"));
                save_text.setTextColor(Color.parseColor("#000000"))
                save_text.setText("Recipe Saved")
                bookmark.setImageResource(R.drawable.savedfilled)

                saveButton_status = true
            }

        }
    }


    private fun updateIngredientsTextView(selectedFoodData: FoodData) {
        val tvIngredients = findViewById<TextView>(R.id.textView15)
        val tvRemainingIngredients = findViewById<TextView>(R.id.textView16)
        val bulletPoint = "\u2022 " // Bullet point character
        val maxVisibleIngredients = 5

        if (selectedFoodData.ingredients.size > maxVisibleIngredients) {
            val visibleIngredients = selectedFoodData.ingredients.take(maxVisibleIngredients)
            val remainingIngredients = selectedFoodData.ingredients.drop(maxVisibleIngredients)
            val visibleIngredientsText = visibleIngredients.joinToString("\n") { "$bulletPoint$it" }
            val remainingIngredientsText = remainingIngredients.joinToString("\n") { "$bulletPoint$it" }

            tvIngredients.text = visibleIngredientsText
            tvRemainingIngredients.text = remainingIngredientsText
            tvRemainingIngredients.visibility = View.VISIBLE
        } else {
            val ingredientsText = selectedFoodData.ingredients.joinToString("\n") { "$bulletPoint$it" }
            tvIngredients.text = ingredientsText
            tvRemainingIngredients.visibility = View.GONE
        }
    }

    private fun updateCookingInstructions(selectedFoodData: FoodData) {
        val instructionsTv = findViewById<TextView>(R.id.textView21)
        val instructionsText = selectedFoodData.how_to_cook.replace(", ", "\n")
        instructionsTv.text = instructionsText
    }

}