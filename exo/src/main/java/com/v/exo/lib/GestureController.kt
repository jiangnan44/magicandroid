package com.v.exo.lib

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/**
 * Author:v
 * Time:2021/4/14
 */
class GestureController(private val context: Context) : View.OnTouchListener {
    private val TAG = "GestureController"

    private val heightThreshold =
        context.resources.displayMetrics.heightPixels * 0.33f // one third of screen height
    private val widthThreshold =
        context.resources.displayMetrics.widthPixels * 0.5f//half of screen width
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val maxVolume: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)



    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, PlayerGestureListener())
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return false
    }



    private inner class PlayerGestureListener : GestureDetector.SimpleOnGestureListener() {
        private var isFirstTouch = false
        private var changeVolume = false
        private var changeBrightness = false

        //change progress of the player,this function need player,finish the function yourself
        private var changeProgress = false


        override fun onDown(e: MotionEvent?): Boolean {
            isFirstTouch = true
            return super.onDown(e)
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false

            val oldX = e1.x
            val oldY = e1.y
            val deltaX = e2.x - oldX
            val deltaY = oldY - e2.y
            if (isFirstTouch) {
                changeProgress = abs(distanceX) >= abs(distanceY)
                if (!changeProgress) {
                    changeVolume = oldX > widthThreshold
                    changeBrightness = !changeVolume
                }
            }

            if (!changeProgress) {
                val percent = deltaY / heightThreshold
                if (changeVolume) {
                    updateVolume(percent)
                } else {
                    updateBrightness(percent)
                }
            } else {
//                need player state, e.g. player is onError, you can't seekTo
//                currentVideoPosition = player.currentPosition
//                val seekToPosition = currentVideoPosition + deltaX / screenWidth * player.duration
            }

            return super.onScroll(e1, e2, distanceX, distanceY)
        }

    }

    private fun updateVolume(percent: Float) {
        var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (currentVolume < 0) currentVolume = 0
        var targetVolume = (percent * maxVolume + currentVolume).toInt()
        if (targetVolume > maxVolume) targetVolume = maxVolume
        if (targetVolume < 0) targetVolume = 0
        Log.d(TAG, "updateVolume percent=$percent targetVolume=$targetVolume")
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)

        //maybe show dialog
    }

    private fun updateBrightness(percent: Float) {
        if (context !is Activity) {
            Log.e(TAG, " Context is NOT a valid activity!")
            return
        }
        val window = (context as Activity).window
        var currentBrightness = window.attributes.screenBrightness
        if (currentBrightness < 0.0f) currentBrightness = 0.5f
        window.attributes = window.attributes.apply {
            screenBrightness = currentBrightness + percent
            if (screenBrightness > 1.0f) screenBrightness = 1.0f
            else if (screenBrightness < 0.01f) screenBrightness = 0.01f
            Log.d(TAG, "updateBrightness percent=$percent screenBrightness=$screenBrightness")
        }

        //maybe show dialog
    }

}