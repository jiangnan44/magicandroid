package com.v.exo

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.os.EnvironmentCompat
import androidx.core.view.isVisible
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File

class CustomUiActivity : AppCompatActivity() {

    private val tag = "exoPlayer"
    private val testUrl = "Download/53926.mp4"

    private lateinit var playerView: StyledPlayerView
    private lateinit var playerControlView: StyledPlayerControlView
    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var playbackPosition = 0L
    private var currentWindow = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_ui)

        playerView = findViewById(R.id.player_view)
        playerControlView = findViewById(R.id.player_control_view)
    }


    private fun initPlayer() {
        if (player != null) return

        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player
        playerControlView.player = player

        findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_fullscreen).visibility =
            View.VISIBLE

        val filePath = Environment.getExternalStorageDirectory()
            .absolutePath
            .plus(File.separator)
            .plus(testUrl)
        Log.d(tag, "full filePath=$filePath")
        val mediaItem = MediaItem.fromUri(filePath)
        player!!.let {
            Log.d(tag, "initPlayer...")
            it.setMediaItem(mediaItem)
            it.playWhenReady = playWhenReady
            it.seekTo(currentWindow, playbackPosition)
            it.prepare()
        }

    }


    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Build.VERSION.SDK_INT < 24 || player == null) {
            initPlayer()
        }
    }

    @Suppress("DEPRECATION")
    private fun hideSystemUi() {
        playerView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < 24) {
            releasePlayer()
        }
    }


    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.let {
            playWhenReady = it.playWhenReady
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.release()
            player = null
        }
    }
}