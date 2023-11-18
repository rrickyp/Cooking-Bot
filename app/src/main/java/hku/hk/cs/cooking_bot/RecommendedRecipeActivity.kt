package hku.hk.cs.cooking_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class RecommendedRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recommended_recipe)

        val back_text_view = findViewById(R.id.back) as TextView

        back_text_view!!.setOnClickListener {

            // setContentView(R.layout.activity_recommendation);
            val goBackToReccomendation = Intent(this, Recommendation::class.java)
            startActivity(goBackToReccomendation)

        }

    }

}