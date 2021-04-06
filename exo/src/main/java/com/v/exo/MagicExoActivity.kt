package com.v.exo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.v.exo.lib.SimpleVideoPlayerLayout

class MagicExoActivity : AppCompatActivity() {
    private val tag = "MagicExoActivity"
    private val testUrl =
        "http://opfront-1300174126.cos.ap-beijing.myqcloud.com/fvideo1616999434687_26954_0.mp4"

    private val testUrl0 =
        "http://opfront-1300174126.cos.ap-beijing.myqcloud.com/f1616988184912_26959_0.mp4"

    private lateinit var playerLayout: SimpleVideoPlayerLayout

    private var playWhenReady = true
    private var playbackPosition = 0L
    private var currentWindow = 0
    private var currentUrl = testUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magic_exo)

        playerLayout = findViewById(R.id.exo_player_layout)
        currentUrl = testUrl
    }


    override fun onStart() {
        super.onStart()
        playerLayout.onStart(currentUrl, currentWindow, playbackPosition)
    }

    override fun onResume() {
        super.onResume()
        playerLayout.onResume(currentUrl, currentWindow, playbackPosition)
    }


    override fun onPause() {
        super.onPause()
        playerLayout.onPause()
    }


    override fun onStop() {
        super.onStop()
        playerLayout.onStop()
    }


}