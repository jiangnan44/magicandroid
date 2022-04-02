package com.v.pluginapp

import android.content.Intent
import android.os.Bundle
import com.v.pluginframework.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.btn_jump).setOnClickListener {
            startActivity(Intent(main, MainActivity2::class.java))
        }
    }
}