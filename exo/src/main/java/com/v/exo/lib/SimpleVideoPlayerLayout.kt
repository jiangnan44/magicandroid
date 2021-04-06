package com.v.exo.lib

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.v.exo.R

/**
 * Author:v
 * Time:2021/4/2
 * Email:348075425@qq.com
 *
 * !!life is too complecated,simplify it,simplify it!!
 */
class SimpleVideoPlayerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {
    companion object {
        private const val TAG = "SVPV"

        const val SCREEN_SMALL_PORTRAIT = 0x0//the phone is on portrait
        const val SCREEN_FULL_LAND = 0x1
        const val SCREEN_FULL_PORTRAIT = 0x2

    }

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var playbackStateListener: PlaybackStateListener? = null


    private var tvSpeed: TextView? = null
    private var btnFullScreen: ImageButton? = null


    var screenState = SCREEN_SMALL_PORTRAIT
    private var playWhenReady = true
    private var playbackPosition = 0L
    private var currentWindow = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.simple_video_player_layout, this)

        initViews()
    }

    private fun initViews() {
        this.playerView = findViewById(R.id.exo_player_view)
        tvSpeed = playerView.findViewById(R.id.exo_speed)
        btnFullScreen = playerView.findViewById(R.id.exo_fullscreen)


        tvSpeed!!.setOnClickListener(this)
        btnFullScreen!!.setOnClickListener(this)
    }

    private fun initDefaultPlayer(url: String, currentWindow: Int, playbackPosition: Long) {
        if (player != null) return

        player = SimpleExoPlayer.Builder(context).build()
        playerView.player = player
        playbackStateListener = PlaybackStateListener()

        val mediaItem = MediaItem.fromUri(url)
        player!!.let {
            Log.d(TAG, "initPlayer...")
            it.setMediaItem(mediaItem)
            it.playWhenReady = playWhenReady
            it.addListener(playbackStateListener!!)
            it.seekTo(currentWindow, playbackPosition)
            it.prepare()
        }
    }


    /**
     * you have to set up the
     * @param player
     * before invoke this method
     */
    fun setPlayer(@NonNull player: ExoPlayer) {
        this.player?.let {
            if (playbackStateListener != null) {
                it.removeListener(playbackStateListener!!)
            }
        }

        initViews()
        this.player = player
        playerView.player = player

        playbackStateListener = PlaybackStateListener()
        this.player!!.addListener(playbackStateListener!!)
    }

    fun setPlayWhenReady(playWhenReady: Boolean) {//default is true
        player?.let {
            it.playWhenReady = playWhenReady
            this.playWhenReady = playWhenReady
        }
    }

    fun onStart(url: String, currentWindow: Int, playbackPosition: Long) {
        if (Build.VERSION.SDK_INT >= 24) {
            initDefaultPlayer(url, currentWindow, playbackPosition)
        }
    }

    fun onResume(url: String, currentWindow: Int, playbackPosition: Long) {
        hideSystemUi()
        if (Build.VERSION.SDK_INT < 24 || player == null) {
            initDefaultPlayer(url, currentWindow, playbackPosition)
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

    fun onPause() {
        if (Build.VERSION.SDK_INT < 24) {
            releasePlayer()
        }
    }


    fun onStop() {
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.exo_speed -> {
                showSpeedChooseDialog()
            }

            R.id.exo_fullscreen -> {
                check2ChangeScreenState()
            }
        }
    }

    private fun showSpeedChooseDialog() {
        val speeds = arrayOf("0.75", "1.0", "1.25", "1.5", "2.0")
        AlertDialog.Builder(context)
            .setTitle("Please choose speed!")
            .setItems(speeds) { _, which ->
                val s = speeds[which].toFloat()
                changeSpeed(s)
            }
    }

    private fun changeSpeed(s: Float) {
        player?.let {
            if (s == it.playbackParameters.speed) {
                return
            }
            it.setPlaybackParameters(PlaybackParameters(s))
            tvSpeed?.text = s.toString().plus("倍")
        }
    }

    private fun check2ChangeScreenState() {
        when (screenState) {
            SCREEN_SMALL_PORTRAIT -> {//change 2 full screen land
                change2FullScreenLand()
            }
            SCREEN_FULL_LAND -> {//change 2 screen small portrait
                change2ScreenSmallPortrait()
            }
            SCREEN_FULL_PORTRAIT -> {
                //do your own thing
            }
            else -> {
                //do your own thing
            }
        }
    }

    private fun change2ScreenSmallPortrait() {

    }

    private fun change2FullScreenLand() {

    }

    private fun replay() {
        player?.seekTo(currentWindow, 0L)
    }

    private fun changeUrl(@NonNull url: String, lastPosition: Long) {
        player?.let {
            it.stop()
            it.setMediaItem(MediaItem.fromUri(url))
            it.seekTo(lastPosition)
            it.playWhenReady = true
            it.prepare()
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
            Log.d(TAG, "change state to $stateString")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
            }
            Log.d(TAG, "onIsPlayingChanged:isPlaying= $isPlaying")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            Log.e(TAG, "onPlayerError:error= ${error.message}")
        }


    }

}