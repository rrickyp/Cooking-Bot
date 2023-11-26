package hku.hk.cs.cooking_bot

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Home : AppCompatActivity() {
    private var username:String = ""
    private var user_name: TextView? = null
    private var currActivity:Activity = this
    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
    data class Recipe(
        val cooking_time: Int,
        val food_name: String,
        val how_to_cook: String,
        val id: Int,
        val image_path: String,
        val ingredients: List<String>,
        val preparation_time: Int,
        val serving_size: Int,
        val total_ingredients: Int,
        val video_path: String
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        val intent = getIntent()
        val sdf = SimpleDateFormat("hh:mm a")
        val currentTime= sdf.format(Date())
        user_name = findViewById(R.id.user_name)
        val time_text = findViewById<TextView>(R.id.textView2)
        val greeting_text = findViewById<TextView>(R.id.textView1)

        if (currentTime.toString().takeLast(2) == "PM"){
            greeting_text.text = "Good Evening,"
        }

        time_text.text = currentTime
        // Get the data from the intent
        val data = intent.getStringArrayListExtra("user_data")
        // Now you can use the data
        // For example, you can print it to the log
        username = data!![0]
        user_name!!.setText(username)
        getSavedRecipes(username)
        val see_all_button = findViewById(R.id.textView6) as TextView
        see_all_button.setOnClickListener {

            val goToSavedActivity = Intent(this, SavedActivity::class.java)
            goToSavedActivity.putStringArrayListExtra("user_data", data)
            startActivity(goToSavedActivity)
            finish()
        }

        val saved_button = findViewById(R.id.saved_icon) as ImageView
        saved_button.setOnClickListener {

            val goToSavedActivity = Intent(this, SavedActivity::class.java)
            goToSavedActivity.putStringArrayListExtra("user_data", data)
            startActivity(goToSavedActivity)
            finish()

        }

        val profile_button = findViewById(R.id.setting_icon) as ImageView
        profile_button.setOnClickListener {

            val goToProfileActivity = Intent(this, ProfileActivity::class.java)
            goToProfileActivity.putStringArrayListExtra("user_data", data)
            startActivity(goToProfileActivity)
            finish()

        }

        val scan_frame = findViewById(R.id.scanLinearLayout) as LinearLayout
        scan_frame.setOnClickListener {

            val goToScanPage = Intent(this, ScanPage::class.java)
            goToScanPage.putStringArrayListExtra("user_data", data)
            startActivity(goToScanPage)

        }
    }

    private fun getSavedRecipes(username: String) {
        val url = "http://10.68.60.178:8080/user_recipes?username=$username"
        val requestQueue = Volley.newRequestQueue(this)
        val saved_recipe_home: androidx.gridlayout.widget.GridLayout = findViewById(R.id.saved_recipe)
        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                // Parse the JSON response
                val recipes: List<Recipe> = Gson().fromJson(response, object : TypeToken<List<Recipe>>() {}.type)

                // Update the UI on the main thread
                if (recipes.isEmpty()) {
                    Toast.makeText(this, "No saved recipes", Toast.LENGTH_SHORT).show()
                    return@Listener
                }
                var count = 0
                for (recipe in recipes) {
                    count += 1
                    if (count > 3) {
                        break
                    }
                    // Create a new LinearLayout for the recipe
                    val recipeLayout = LinearLayout(currActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            val marginInDp = 12
                            val marginInPx = (marginInDp * resources.displayMetrics.density).toInt()
                            bottomMargin = marginInPx
                        }
                        orientation = LinearLayout.HORIZONTAL

                        val recipeImageView = ShapeableImageView(currActivity).apply {
                            id = View.generateViewId()
                            layoutParams = LinearLayout.LayoutParams(68.dp, 68.dp).apply {
                                val marginInDp = 20
                                val marginInPx = (marginInDp * resources.displayMetrics.density).toInt()
                                marginEnd = marginInPx
                            }
                            adjustViewBounds = true
                            scaleType = ImageView.ScaleType.CENTER_CROP

                            val resId = resources.getIdentifier(recipe.image_path, "drawable", packageName)
                            if (resId != 0) {
                                setImageResource(resId)
                            } else {
                                val notfound = resources.getIdentifier("food_hub", "drawable", packageName)
                                setImageResource(notfound)
                            }

                            shapeAppearanceModel = shapeAppearanceModel.withCornerSize(50f)
                        }

                        // Create a new LinearLayout for the recipe details
                        val recipeDetailsLayout = LinearLayout(currActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                            orientation = LinearLayout.VERTICAL

                            // Create a new TextView for the recipe details
                            val recipeDetailsTextView = TextView(currActivity).apply {
                                id = View.generateViewId()
                                text = "${recipe.cooking_time}mins ∙ ${recipe.total_ingredients} ingredients" // Replace with actual recipe details
                                textSize = 10f
                                gravity = Gravity.LEFT
                                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                                setTextColor(Color.parseColor("#ADADAD"))
                            }

                            // Create a new TextView for the recipe name
                            val recipeNameTextView = TextView(currActivity).apply {
                                id = View.generateViewId()
                                text = recipe.food_name
                                textSize = 14f
                                gravity = Gravity.LEFT
                                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                            }

                            // Create a new TextView for the recipe ingredients
                            val recipeIngredientsTextView = TextView(currActivity).apply {
                                id = View.generateViewId()
                                text = recipe.ingredients.joinToString(" ∙ ")
                                textSize = 10f
                                gravity = Gravity.LEFT
                                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                                setTextColor(Color.parseColor("#ADADAD"))
                            }

                            // Add the TextViews to the LinearLayout
                            addView(recipeDetailsTextView)
                            addView(recipeNameTextView)
                            addView(recipeIngredientsTextView)
                        }

                        // Add the ImageView and LinearLayout to the recipe layout
                        addView(recipeImageView)
                        addView(recipeDetailsLayout)
                        setOnClickListener {
                            val modifiedRecipe = recipe.copy(ingredients = recipe.ingredients)

                            val jsonSelectedFoodData = Gson().toJson(modifiedRecipe)
                            val goToRecipe = Intent(currActivity, RecommendedRecipeActivity::class.java)
                            goToRecipe.putExtra("selected_food_data", jsonSelectedFoodData)
                            val temp = arrayListOf<String>(username)
                            goToRecipe.putExtra("user_data", temp)
                            println("jsonselecteddata: $jsonSelectedFoodData")
                            currActivity.startActivity(goToRecipe)
                        }
                    }

                    // Add the LinearLayout to the grid layout
                    saved_recipe_home.addView(recipeLayout)
                }

            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        ) {}
        requestQueue.add(stringRequest)
    }
}