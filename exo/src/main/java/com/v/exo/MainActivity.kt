package com.v.exo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestReadStoragePermission()
    }

    private fun requestReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_video_player -> {
                go2Activity(PlayerActivity::class.java)
            }
            R.id.btn_video_player_list -> {
                go2Activity(PlayListActivity::class.java)
            }
            R.id.btn_video_player_custom -> {
                go2Activity(CustomUiActivity::class.java)
            }
            R.id.btn_video_player_magic -> {
                go2Activity(MagicExoActivity::class.java)
            }
        }
    }


    private fun go2Activity(clz: Class<*>) {
        startActivity(Intent(this, clz))
    }
}