package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response
import com.google.gson.Gson

class Recommendation : AppCompatActivity() {

    data class FoodData(val food_name: String, val ingredients: List<String>, val total_ingredients: Int, val serving_size: Int, val preparation_time: Int, val cooking_time: Int, val how_to_cook: String)
    private var cook_now_button: Button? = null
    private var selected_food_data: FoodData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
        cook_now_button = findViewById(R.id.button2)

        val home_view = findViewById(R.id.home_icon) as ImageView

        home_view.setOnClickListener {
            val goBackToHome = Intent(this, Home::class.java)
            startActivity(goBackToHome)
        }

        val saved_button = findViewById(R.id.saved_icon) as ImageView
            saved_button.setOnClickListener {

            val goToSavedActivity = Intent(this, SavedActivity::class.java)
            startActivity(goToSavedActivity)
        }

        val textView = findViewById<TextView>(R.id.textView3)

        val matchingFoods = intent.getStringArrayListExtra("matchingFoods")
        println(matchingFoods)
        if (matchingFoods != null && matchingFoods.isNotEmpty()) {
            val foodName = matchingFoods[0]
            val foodNameFormat = foodName.substringAfter("\"").substringBefore("\"")
            textView.text = foodNameFormat
            getDataFromAPI(foodNameFormat)
        } else {
            textView.text = "Test Food"
        }

        cook_now_button!!.setOnClickListener {
            if (selected_food_data != null) {
                val jsonSelectedFoodData = Gson().toJson(selected_food_data)
                val goToRecipe = Intent(this, RecommendedRecipeActivity::class.java)
                goToRecipe.putExtra("selected_food_data", jsonSelectedFoodData)
                println(jsonSelectedFoodData)
                startActivity(goToRecipe)
            }
        }



    }

    private fun getDataFromAPI(foodName: String) {
        val url = "http://10.68.12.61:8080/recognize/$foodName"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                // Handle the successful response here
                println("Ini hasil narik data: $response")
                val foodDataJson = response
                val foodData = Gson().fromJson(foodDataJson, FoodData::class.java)
                selected_food_data = foodData
                println(selected_food_data)
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        ) {}
        requestQueue.add(stringRequest)
    }


}