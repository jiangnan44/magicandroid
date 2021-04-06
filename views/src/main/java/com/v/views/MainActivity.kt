package com.v.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.v.views.magic.SimpleRatingBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {

        val ratingBar: SimpleRatingBar = findViewById(R.id.srb)
        ratingBar.setOnRatingChangeListener { _, rating ->
            Log.d("SimpleRating","on rating change...$rating")
            Toast.makeText(this, "Rating now $rating", Toast.LENGTH_SHORT).show()
        }
    }
}