package hku.hk.cs.cooking_bot

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

class RecommendedRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recommended_recipe)

        val back_text_view = findViewById(R.id.back) as TextView
        val save_view = findViewById(R.id.saveView) as LinearLayout
        val bookmark = findViewById(R.id.bookmarkIcon) as ImageView
        val save_text = findViewById(R.id.saveText) as TextView
        var saveButton_status = false


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

}