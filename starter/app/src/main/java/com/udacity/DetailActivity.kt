package com.udacity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        if (intent.hasExtra("NotificationSuccess")) {
            Log.i("DetailActiivty", "NotificationSuccess:" +
                    "${intent.getBooleanExtra("NotificationSuccess", false)}")
            if (intent.getBooleanExtra("NotificationSuccess", false)) {
                status.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                status.text = getString(R.string.success)
            } else {
                status.setTextColor(ContextCompat.getColor(this, R.color.color_red))
                status.text = getString(R.string.fail)
            }
        }

        if (intent.hasExtra("NotificationTitle")) {
            file_name.text = intent.getStringExtra("NotificationTitle")
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}