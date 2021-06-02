package com.v.exo.lib

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.BatteryManager
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.v.exo.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author:v
 * Time:2021/4/2
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
    lateinit var playerView: PlayerView
    private var playbackStateListener: PlaybackStateListener? = null
    private var simpleVideoListener: OnSimpleVideoListener? = null
    private var gestureController: GestureController? = null

    private var speedDialog: AlertDialog? = null
    private var btnFullScreen: ImageView? = null
    private var tvSpeed: TextView? = null
    private var batteryView: BatteryView? = null
    private var tvSystemTime: TextView? = null

    private var endLayout: FrameLayout? = null
    private var tvNext: TextView? = null
    private var tvPre: TextView? = null


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
        btnFullScreen = playerView.findViewById(R.id.exo_fullscreen)
        batteryView = playerView.findViewById(R.id.exo_battery_view)
        tvSystemTime = playerView.findViewById(R.id.exo_tv_system_time)

        endLayout = playerView.findViewById(R.id.exo_center_layout)
        tvNext = endLayout!!.findViewById(R.id.exo_tv_next)
        tvPre = endLayout!!.findViewById(R.id.exo_tv_pre)
        val tvReplay = endLayout!!.findViewById<View>(R.id.exo_tv_replay)


        tvSpeed!!.setOnClickListener(this)
        tvNext!!.setOnClickListener(this)
        tvPre!!.setOnClickListener(this)
        tvReplay!!.setOnClickListener(this)
        btnFullScreen!!.setOnClickListener(this)
        playerView.findViewById<View>(R.id.exo_iv_back).setOnClickListener(this)
//        playerView.findViewById<View>(R.id.exo_player_back).setOnClickListener(this)
        playerView.setControllerVisibilityListener {
            if (it == VISIBLE) {
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

    private fun initDefaultPlayer(url: String, currentWindow: Int, playbackPosition: Long) {
        if (player != null) return

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                50_000,
                50_000,
                1500,
                2500
            )
            .build()
        player = SimpleExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build()
        playerView.player = player
        playbackStateListener = PlaybackStateListener()


        val mediaItem = MediaItem.fromUri(url)
        player!!.let {
            Log.d(TAG, "initPlayer...")
            it.setMediaItem(mediaItem, playbackPosition)
            it.playWhenReady = true
            it.addListener(playbackStateListener!!)
            it.prepare()
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


    fun onStart(url: String, currentWindow: Int, playbackPosition: Long) {
        if (Build.VERSION.SDK_INT >= 24) {
            initDefaultPlayer(url, currentWindow, playbackPosition)
        }
    }

    fun onResume(url: String, currentWindow: Int, playbackPosition: Long) {
        hideSystemUi()
        when {
            Build.VERSION.SDK_INT < 24 -> {
                initDefaultPlayer(url, currentWindow, playbackPosition)
            }
            player == null -> {
                initDefaultPlayer(url, currentWindow, playbackPosition)
            }
            else -> {
                resumePlayer(url, currentWindow, playbackPosition)
            }
        }
        if (context is Activity) {
            (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun resumePlayer(url: String, currentWindow: Int, playbackPosition: Long) {
        if (player != null) {
            player!!.play()
        } else {
            initDefaultPlayer(url, currentWindow, playbackPosition)
        }
    }

    @Suppress("DEPRECATION")
    private fun hideSystemUi() {
        playerView.systemUiVisibility = SYSTEM_UI_FLAG_LOW_PROFILE or
                SYSTEM_UI_FLAG_FULLSCREEN or
                SYSTEM_UI_FLAG_LAYOUT_STABLE or
                SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    fun onPause() {
        pausePlayer()
        if (context is Activity) {
            (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }


    fun onStop() {
        releasePlayer()
    }

    fun onDestroy() {
        playbackStateListener = null
        gestureController = null
        simpleVideoListener = null
    }


    private fun pausePlayer() {
        player?.pause()
    }

    private fun releasePlayer() {
        player?.let {
            playbackStateListener?.run {
                it.removeListener(this)
            }
            it.stop()
            it.release()
            player = null
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.exo_speed -> {
                simpleVideoListener?.speedClick()
            }
            R.id.exo_fullscreen -> {
                check2ChangeScreenState()
            }
//            R.id.exo_player_back,
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


    fun setVideoTitle(title: String?) {
        if (!TextUtils.isEmpty(title)) {
            findViewById<TextView>(R.id.exo_tv_title).apply {
                text = title
                visibility = VISIBLE
            }
        }
    }


    fun changeSpeed(s: Float) {
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

        screenState = SCREEN_SMALL_PORTRAIT
//        btnFullScreen?.setImageResource(R.drawable.exo_ic_enlarge)
        simpleVideoListener?.screenChange(screenState)
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

        screenState = SCREEN_FULL_LAND
//        btnFullScreen?.setImageResource(R.drawable.exo_ic_shrink)
        simpleVideoListener?.screenChange(screenState)
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
        tvNext?.let {
            if (hasNext) {
                it.visibility = VISIBLE
            } else {
                it.visibility = INVISIBLE
            }
        }

        tvPre?.let {
            if (hasPre) {
                it.visibility = VISIBLE
            } else {
                it.visibility = INVISIBLE
            }
        }

        endLayout?.visibility = VISIBLE
    }

    private fun hideEndUi() {
        endLayout?.let {
            if (it.visibility == GONE) return
            it.visibility = GONE
        }
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
                    simpleVideoListener?.onVideoEnd()
                    showEndUi()
                    stateString = "ExoPlayer.STATE_ENDED     -"
                }
                else ->
                    stateString = "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "change state to $stateString")
        }


        override fun onPlayerError(error: ExoPlaybackException) {
            Log.e(TAG, "onPlayerError:error= ${error.message}")
            Toast.makeText(context, "播放视频出错，请退出页面后重试！", Toast.LENGTH_LONG).show()
            error.printStackTrace()
        }

    }

    interface OnSimpleVideoListener {
        fun onBack()
        fun onPre()
        fun onNext()
        fun onVideoEnd()
        fun speedClick()
        fun screenChange(screenState: Int)
    }

    fun setOnSimpleVideoListener(listener: OnSimpleVideoListener) {
        this.simpleVideoListener = listener
    }

}