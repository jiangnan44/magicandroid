package com.v.exo

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class PlayListActivity : AppCompatActivity() {
    private val tag = "exoPlayerList"
    private val testUrl =
        "http://opfront-1300174126.cos.ap-beijing.myqcloud.com/fvideo1616999434687_26954_0.mp4"
    private val testUrl0 =
        "http://opfront-1300174126.cos.ap-beijing.myqcloud.com/f1616988184912_26959_0.mp4"
    private val testUrl1 =
        "http://opfront-1300174126.cos.ap-beijing.myqcloud.com/f1616988239117_27038_0.mp4"
    private val testUrl2 =
        "http://opfront-1300174126.cos.ap-beijing.myqcloud.com/f1616988252177_26963_0.mp4"

    private lateinit var playerView: PlayerView
    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var playbackPosition = 0L
    private var currentWindow = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)

        playerView = findViewById(R.id.exo_player)
    }

    private fun initPlayer() {
        val mediaList = getMediaItems()
        player = SimpleExoPlayer.Builder(this).build().apply {
            Log.d(tag, "initPlayer...")
            setMediaItems(mediaList)
            playWhenReady = playWhenReady
            seekTo(currentWindow, playbackPosition)
            prepare()
        }
        playerView.player = player
    }

    private fun getMediaItems(): ArrayList<MediaItem> {
        val ret = ArrayList<MediaItem>(4)
        ret.add(MediaItem.fromUri(testUrl))
        ret.add(MediaItem.fromUri(testUrl0))
        ret.add(MediaItem.Builder()
            .setUri(testUrl1)
            .setClipStartPositionMs(10_000L)
            .setClipEndPositionMs(20_000L)
            .build())

        ret.add(MediaItem.Builder()
            .setUri(testUrl2)
            .setMediaId("id")
            .setTag("tag").build())

        return ret
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