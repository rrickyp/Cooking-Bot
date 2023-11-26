package hku.hk.cs.cooking_bot

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {
    private var username:String = ""
    private var user_name: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        val intent = getIntent()
        user_name = findViewById(R.id.user_name)

        // Get the data from the intent
        val data = intent.getStringArrayListExtra("data")
        // Now you can use the data
        // For example, you can print it to the log
        username = data!![0]
        user_name!!.setText(username)
        val see_all_button = findViewById(R.id.textView6) as TextView
        see_all_button.setOnClickListener {

            val goToSavedActivity = Intent(this, SavedActivity::class.java)
            startActivity(goToSavedActivity)

        }

        val saved_button = findViewById(R.id.saved_icon) as ImageView
            saved_button.setOnClickListener {

                val goToSavedActivity = Intent(this, SavedActivity::class.java)
            startActivity(goToSavedActivity)

        }

        val scan_frame = findViewById(R.id.scanLinearLayout) as LinearLayout
        scan_frame.setOnClickListener {

            val goToScanPage = Intent(this, ScanPage::class.java)
            startActivity(goToScanPage)

        }
    }

}