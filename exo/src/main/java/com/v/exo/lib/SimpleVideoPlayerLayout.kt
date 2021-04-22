package com.v.exo.lib

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.BatteryManager
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.v.exo.R
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

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

    var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var playbackStateListener: PlaybackStateListener? = null
    private var simpleVideoListener: OnSimpleVideoListener? = null
    private var gestureController: GestureController? = null

    private var speedDialog: AlertDialog? = null
    private var btnFullScreen: ImageButton? = null
    private var tvSpeed: TextView? = null
    private var batteryView: BatteryView? = null
    private var tvSystemTime: TextView? = null

    /**
     * because I have a large list of video info entity,
     * so I control next and pre myself instead of a list of MediaItem
     */
    private var tvNext: TextView? = null
    private var tvPre: TextView? = null
    private var tvReplay: TextView? = null


    private var mmParent: ViewGroup? = null
    private var mmWidth = 0
    private var mmHeight = 0
    private var mmIndex = -1


    var screenState = SCREEN_SMALL_PORTRAIT
    var hasNext = false
    var hasPre = false

    init {
        LayoutInflater.from(context).inflate(R.layout.simple_video_player_layout, this)

        initViews()
    }

    private fun initViews() {
        this.playerView = findViewById(R.id.exo_player_view)
        tvSpeed = playerView.findViewById(R.id.exo_speed)
        tvNext = playerView.findViewById(R.id.exo_tv_next)
        tvPre = playerView.findViewById(R.id.exo_tv_pre)
        tvReplay = playerView.findViewById(R.id.exo_tv_replay)
        btnFullScreen = playerView.findViewById(R.id.exo_fullscreen)
        batteryView = playerView.findViewById(R.id.exo_battery_view)
        tvSystemTime = playerView.findViewById(R.id.exo_tv_system_time)


        tvSpeed!!.setOnClickListener(this)
        tvNext!!.setOnClickListener(this)
        tvPre!!.setOnClickListener(this)
        tvReplay!!.setOnClickListener(this)
        btnFullScreen!!.setOnClickListener(this)
        findViewById<View>(R.id.exo_iv_back).setOnClickListener(this)
        playerView.setControllerVisibilityListener {
            if (it == View.VISIBLE) {
                updateBatteryAndSystemTime()
            }
        }
        updateBatteryAndSystemTime()
    }


    private fun updateBatteryAndSystemTime() {
        batteryView?.let {
            val manager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val level = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            it.updatePower(level)
            Log.d(TAG, "updateBatterLevel level=$level")
        }
        tvSystemTime?.let {
            it.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }
    }

    private fun initDefaultPlayer(
        url: String,
        currentWindow: Int,
        playbackPosition: Long,
        playWhenReady: Boolean
    ) {
        if (player != null) return

        player = SimpleExoPlayer.Builder(context).build()
        playerView.player = player
        playbackStateListener = PlaybackStateListener()


        //use cache will cause extra time loading/buffering,yet next time open will like a lighting
        val mediaSource = ExoSimpleCache.createMediaSource(url, context)
        val mediaItem = MediaItem.fromUri(url)
        player!!.let {
            Log.d(TAG, "initPlayer...")
//            it.setMediaSource(mediaSource, playbackPosition)
            it.setMediaItem(mediaItem, playbackPosition)
            it.playWhenReady = playWhenReady
            it.addListener(playbackStateListener!!)
//            it.seekToDefaultPosition(currentWindow) //will cause bad buffering
            it.prepare()
        }
    }


    /**
     * you have to set up the
     * @param player
     * before invoke this method
     */
    fun replacePlayer(@NonNull player: ExoPlayer) {
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
        }
    }

    /**
     * whether use gesture to control volume and brightness
     */
    fun useGestureController() {
        if (gestureController == null) {
            gestureController = GestureController(context)
            playerView.setOnTouchListener(gestureController)
        }
    }


    fun onBack(): Boolean {
        speedDialog?.let {
            if (it.isShowing) {
                it.dismiss()
                return true
            }
        }

        if (screenState == SCREEN_FULL_LAND) {
            change2ScreenSmallPortrait()
            return true
        }
        return false
    }


    fun onStart(url: String, currentWindow: Int, playbackPosition: Long, playWhenReady: Boolean) {
        if (Build.VERSION.SDK_INT >= 24) {
            initDefaultPlayer(url, currentWindow, playbackPosition, playWhenReady)
        }
    }

    fun onResume(url: String, currentWindow: Int, playbackPosition: Long, playWhenReady: Boolean) {
        hideSystemUi()
        if (Build.VERSION.SDK_INT < 24 || player == null) {
            initDefaultPlayer(url, currentWindow, playbackPosition, playWhenReady)
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

    fun onDestroy() {
        playbackStateListener = null
        gestureController = null
        simpleVideoListener = null
    }


    private fun releasePlayer() {
        player?.let {
            playbackStateListener?.run {
                it.removeListener(this)
            }
            it.release()
            player = null
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.exo_speed -> {
                showSpeedChooseDialog()
            }
            R.id.exo_fullscreen -> {
                check2ChangeScreenState()
            }
            R.id.exo_iv_back -> {
                simpleVideoListener?.onBack()
            }
            R.id.exo_tv_next -> {
                simpleVideoListener?.onNext()
            }
            R.id.exo_tv_pre -> {
                simpleVideoListener?.onPre()
            }
            R.id.exo_tv_replay -> {
                replay()
            }
        }
    }

    //just a demo, handle your own dialog and ui for real project
    private fun showSpeedChooseDialog() {
        val speeds = arrayOf("0.75", "1.0", "1.25", "1.5", "2.0")
        AlertDialog.Builder(context)
            .setTitle("Please choose speed!")
            .setItems(speeds) { _, which ->
                val s = speeds[which].toFloat()
                changeSpeed(s)
            }
            .show()
    }

    private fun changeSpeed(s: Float) {
        if (player == null || s == player!!.playbackParameters.speed) {
            return
        }
        player!!.setPlaybackParameters(PlaybackParameters(s))
        tvSpeed?.text = s.toString().plus("倍")
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
        if (context !is Activity) {
            throw IllegalArgumentException("The Context of $TAG is NOT a valid activity!")
        }

        val decorView = (context as Activity).window.decorView as ViewGroup
        decorView.removeView(this)

        val lp = LayoutParams(
            mmWidth,
            mmHeight
        )

        mmParent?.addView(this, mmIndex, lp)
        (context as Activity).requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onScreenSmallPortrait()
    }

    private fun change2FullScreenLand() {
        val parent = parent as ViewGroup
        if (screenState == SCREEN_SMALL_PORTRAIT) {
            if (mmIndex == -1) {
                mmParent = parent
                mmHeight = layoutParams.height
                mmWidth = layoutParams.width
                mmIndex = parent.indexOfChild(this)
            }
        }

        if (context !is Activity) {
            throw IllegalArgumentException("The Context of $TAG is NOT a valid activity!")
        }

        parent.removeViewAt(mmIndex)
        val decorView = (context as Activity).window.decorView as ViewGroup
        decorView.addView(
            this,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )
        (context as Activity).requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onFullScreenLand()
    }

    open fun onScreenSmallPortrait() {
        screenState = SCREEN_SMALL_PORTRAIT
    }

    open fun onFullScreenLand() {
        screenState = SCREEN_FULL_LAND
    }

    fun replay() {
        player?.seekTo(0L)
        hideEndUi()
    }

    fun changeUrl(@NonNull url: String, lastPosition: Long) {
        player?.let {
            it.stop()
            it.setMediaItem(MediaItem.fromUri(url), lastPosition)
            it.playWhenReady = true
            it.prepare()
        }
        hideEndUi()
    }


    private fun showEndUi() {
        playerView.overlayFrameLayout?.setBackgroundColor(Color.parseColor("#777777"))
        if (hasNext) {
            tvNext?.visibility = VISIBLE
        }
        if (hasPre) {
            tvPre?.visibility = VISIBLE
        }
        tvReplay?.visibility = VISIBLE
    }

    private fun hideEndUi() {
        if (tvReplay?.visibility == INVISIBLE) return
        playerView.overlayFrameLayout?.background = null
        tvNext?.visibility = INVISIBLE
        tvPre?.visibility = INVISIBLE
        tvReplay?.visibility = INVISIBLE
    }


    private inner class PlaybackStateListener : Player.EventListener {

        override fun onPlaybackStateChanged(state: Int) {
            val stateString: String
            when (state) {
                ExoPlayer.STATE_IDLE -> {
                    stateString = "ExoPlayer.STATE_IDLE      -"
                }
                ExoPlayer.STATE_BUFFERING -> {
                    stateString = "ExoPlayer.STATE_BUFFERING -"
                    hideEndUi()
                }
                ExoPlayer.STATE_READY -> {
                    stateString = "ExoPlayer.STATE_READY     -"
                }
                ExoPlayer.STATE_ENDED -> {
                    showEndUi()
                    stateString = "ExoPlayer.STATE_ENDED     -"
                }
                else ->
                    stateString = "UNKNOWN_STATE             -"
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

    interface OnSimpleVideoListener {
        fun onBack()
        fun onPre()
        fun onNext()
    }

    fun setOnSimpleVideoListener(listener: OnSimpleVideoListener) {
        this.simpleVideoListener = listener
    }

}