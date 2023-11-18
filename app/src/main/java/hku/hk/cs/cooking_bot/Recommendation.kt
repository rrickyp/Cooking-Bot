package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class Recommendation : AppCompatActivity() {
    private var cook_now_button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
        cook_now_button = findViewById(R.id.button2)

        cook_now_button!!.setOnClickListener {
            // setContentView(R.layout.recommended_recipe);
            val goToRecipe = Intent(this, RecommendedRecipeActivity::class.java)
            startActivity(goToRecipe)
        }

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
        } else {
            textView.text = "Test Food"
        }

    }



}