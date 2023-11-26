package hku.hk.cs.cooking_bot

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import hku.hk.cs.cooking_bot.data.Constants
import org.json.JSONArray

class Recommendation : AppCompatActivity() {

    data class FoodData(
        val food_name: String,
        val ingredients: List<String>,
        val total_ingredients: Int,
        val serving_size: Int,
        val preparation_time: Int,
        val cooking_time: Int,
        val how_to_cook: String,
        val image_path: String,
        val video_path: String,
        val id: String
    )

    private var cook_now_button: Button? = null
    private var selected_food_data: FoodData? = null
    private var selected_food: FoodData? = null

    private var otherRecommends:FlexboxLayout? = null
    private var currentActivity: Activity? = null
    private var username:String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
        currentActivity = this
        cook_now_button = findViewById(R.id.button2)
        otherRecommends = findViewById(R.id.flexboxRecommendations)

        val intent = getIntent()
        val data = intent.getStringArrayListExtra("user_data")
        username = data!![0]

        val home_view = findViewById(R.id.home_icon) as ImageView

        home_view.setOnClickListener {
            val goBackToHome = Intent(this, Home::class.java)
            goBackToHome.putStringArrayListExtra("user_data", data)
            // Now you can use the data
            // For example, you can print it to the log
            startActivity(goBackToHome)
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

        val textView = findViewById<TextView>(R.id.textView3)

        val matchingFoods = intent.getStringArrayListExtra("matchingFoods")
        if (matchingFoods != null && matchingFoods.isNotEmpty()) {
            val foodName = matchingFoods[0]
            val foodNameFormat = foodName.substringAfter("\"").substringBefore("\"")
            textView.text = foodNameFormat
            getDataFromAPI(foodNameFormat)
            val foodNames = mutableListOf<String>()
            for (i in 1 until matchingFoods.size) {
                val nextFoodName = matchingFoods[i]
                val nextFoodNameFormat = nextFoodName.substringAfter("\"").substringBefore("\"")
                foodNames.add(nextFoodNameFormat)
            }
            getOtherRecommendationFromAPI(foodNames.toTypedArray())
        } else {
            textView.text = "TestFood"
        }

        cook_now_button?.setOnClickListener {
            if (selected_food != null) {
                val jsonSelectedFoodData = Gson().toJson(selected_food)
                val goToRecipe = Intent(this, RecommendedRecipeActivity::class.java)
                goToRecipe.putExtra("selected_food_data", jsonSelectedFoodData)
                val temp = arrayListOf<String>(username)
                goToRecipe.putExtra("user_data", temp)
                startActivity(goToRecipe)
            }
        }

    }


    private fun getDataFromAPI(foodName: String) {
        val url = "${Constants.BASE_URL}/recognize/$foodName"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                // Handle the successful response here
                val foodDataJson = response
                val foodData = Gson().fromJson(foodDataJson, FoodData::class.java)
                selected_food = foodData

                val imageView = findViewById<ImageView>(R.id.imageView)
                val imagePath = selected_food?.image_path
                if (imagePath != null && imagePath.isNotEmpty()) {
                    val drawableId = resources.getIdentifier(imagePath, "drawable", packageName)
                    if (drawableId != 0) {
                        imageView.setImageResource(drawableId)
                    }
                }

                val timeAndIngredientsTextView = findViewById<TextView>(R.id.textView4)
                val cookingTime = selected_food?.cooking_time ?: 0
                val preparationTime = selected_food?.preparation_time ?: 0
                val totalTime = cookingTime + preparationTime

                val timeAndIngredientsText = "$totalTime mins | ${selected_food?.total_ingredients} ingredients"
                timeAndIngredientsTextView.text = timeAndIngredientsText

            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        ) {}
        requestQueue.add(stringRequest)
    }
private fun getOtherRecommendationFromAPI(foodNames: Array<String>) {
    val url = "${Constants.BASE_URL}/recognizemany"
    val requestQueue = Volley.newRequestQueue(this)
    val jsonArrayRequest = object : JsonArrayRequest(
        Method.POST, url, JSONArray(foodNames),
        Response.Listener<JSONArray> { response ->
            // Handle the successful response here
            response?.let {
                val allIng = it
                for (i in 0 until it.length()) {
                    val foodDataJson = it.getJSONObject(i).toString()
                    val foodData = Gson().fromJson(foodDataJson, FoodData::class.java)
                    selected_food_data = foodData
                    val imageName = selected_food_data?.image_path // "pizza"
                    val resId = resources.getIdentifier(imageName, "drawable", packageName)

                    val density = resources.displayMetrics.density
                    val sizeInDp = 150
                    val marginInDp = 10
                    val sizeInPx = (sizeInDp * density).toInt()
                    val marginInPx = (marginInDp * density).toInt()

                    val params = LinearLayout.LayoutParams(sizeInPx, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
                    }

                    val heightInDp = 120
                    val heightInPx = (heightInDp * density).toInt()

                    val imageView = ImageView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            heightInPx
                        )
                        scaleType = ImageView.ScaleType.CENTER_CROP

                        if (resId != 0) {
                            setImageResource(resId)
                        }
                        else {
                            val notfound = resources.getIdentifier("food_hub", "drawable", packageName)

                            setImageResource(notfound)
                        }
                    }

                    val cardView = CardView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            val marginInDp = 8
                            val marginInPx = (marginInDp * density).toInt()
                            setMargins(0, marginInPx, 0, marginInPx)
                        }
                        radius = 12 * density
                        addView(imageView)
                    }

                    val linearLayout = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams = params
                        addView(cardView)
                    }

                    // Create TextViews and add them to the LinearLayout
                    val textView13 = TextView(this).apply {
                        id = View.generateViewId()
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            val marginInDp = 5
                            val marginInPx = (marginInDp * density).toInt()
                            setMargins(0, marginInPx, 0, marginInPx)
                        }
                        text = selected_food_data?.food_name
                        textSize = 18f
                        setTypeface(null, Typeface.BOLD)
                    }
                    linearLayout.addView(textView13)
                    val textView14 = TextView(this).apply {
                        id = View.generateViewId()
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        text = "Prep Time: ${selected_food_data?.preparation_time} mins"
                        textSize = 15f
                    }
                    linearLayout.addView(textView14)
                    val textView15 = TextView(this).apply {
                        id = View.generateViewId()
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        text = "Cook Time: ${selected_food_data?.cooking_time} mins"
                        textSize = 15f
                    }
                    linearLayout.addView(textView15)
                    val textView16 = TextView(this).apply {
                        id = View.generateViewId()
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        text = "Total Ingredients: ${selected_food_data?.total_ingredients}"
                        textSize = 15f
                    }
                    linearLayout.addView(textView16)
                    val cookNowButton = Button(this).apply {
                        text = "Cook Now"
                        setBackgroundResource(R.drawable.button_corner)
                        setTextColor(Color.parseColor("#FFFFFF"))
                        setPadding(0, 0, 0, 0)
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            val marginInDp = 12
                            val marginInPx = (marginInDp * density).toInt()
                            setMargins(0, marginInPx, 0, marginInPx)
                        }
                        setOnClickListener {
                            val foodDataJson = allIng.getJSONObject(i).toString()
                            val foodData = Gson().fromJson(foodDataJson, FoodData::class.java)
                            selected_food_data = foodData
                            val jsonSelectedFoodData = Gson().toJson(selected_food_data)
                            val goToRecipe = Intent(currentActivity, RecommendedRecipeActivity::class.java)
                            goToRecipe.putExtra("selected_food_data", jsonSelectedFoodData)
                            val temp = arrayListOf<String>(username)
                            goToRecipe.putExtra("user_data", temp)
                            startActivity(goToRecipe)
                        }
                    }
                    linearLayout.addView(cookNowButton)
                    otherRecommends?.addView(linearLayout)
                }
            }
        },
        Response.ErrorListener { error ->
            error.printStackTrace()
        }
    ) {}
    requestQueue.add(jsonArrayRequest)
}
}