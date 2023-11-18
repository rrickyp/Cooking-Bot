package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

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

    }



}