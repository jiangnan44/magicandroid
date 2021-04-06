package com.v.exo

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView

class PlayerActivity : AppCompatActivity(), View.OnClickListener {
    private val tag = "exoPlayer"
    private val testUrl =
        "http://opfront-1300174126.cos.ap-beijing.myqcloud.com/fvideo1616999434687_26954_0.mp4"

    private val testUrl0 =
        "http://opfront-1300174126.cos.ap-beijing.myqcloud.com/f1616988184912_26959_0.mp4"

    private lateinit var playerView: PlayerView
    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var playbackPosition = 0L
    private var currentWindow = 0


    private var playbackStateListener: PlaybackStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.exo_player)
    }

    private fun initPlayer() {
        if (player != null) return

        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player
        playbackStateListener = PlaybackStateListener()

        val mediaItem = MediaItem.fromUri(testUrl)
        player!!.let {
            Log.d(tag, "initPlayer...")
            it.setMediaItem(mediaItem)
            it.playWhenReady = playWhenReady
            it.seekTo(currentWindow, playbackPosition)
            it.addListener(playbackStateListener!!)
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
            playbackStateListener?.run {
                it.removeListener(this)
            }
            it.release()
            player = null
        }
    }

    private inner class PlaybackStateListener : Player.EventListener {

        override fun onPlaybackStateChanged(state: Int) {
            val stateString: String
            when (state) {
                ExoPlayer.STATE_IDLE -> {
                    stateString = "ExoPlayer.STATE_IDLE      -";
                }
                ExoPlayer.STATE_BUFFERING -> {
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                }
                ExoPlayer.STATE_READY -> {
                    stateString = "ExoPlayer.STATE_READY     -";
                }
                ExoPlayer.STATE_ENDED -> {
                    stateString = "ExoPlayer.STATE_ENDED     -";
                }
                else ->
                    stateString = "UNKNOWN_STATE             -";
            }
            Log.d(tag, "change state to $stateString")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.d(tag, "onIsPlayingChanged:isPlaying= $isPlaying")
            Log.d(tag, "onIsPlayingChanged:player.isPlaying= ${player?.isPlaying}")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            Log.e(tag, "onPlayerError:error= ${error.message}")
        }


    }

    var flag = true

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ap_btn_replay -> {
                player?.seekTo(currentWindow, 0L)
            }

            R.id.ap_btn_change -> {
                player?.let {
                    it.stop()
//                    it.release() //can't release
                    val url = if (flag) {
                        testUrl0
                    } else {
                        testUrl
                    }
                    flag = !flag
                    it.setMediaItem(MediaItem.fromUri(url))
                    it.seekTo(2000L)
                    it.playWhenReady = true
                    it.prepare()
                }
            }
        }
    }
}