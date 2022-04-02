package com.v.pluginframework

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

/**
 * Author:v
 * Time:2022/3/30
 */
interface ProxyActivityInterface {
    fun attach(proxyActivity: Activity)
    fun setContentView(@LayoutRes resId: Int)
    fun findViewById(@IdRes id: Int): View
    fun startActivity(intent: Intent)

    fun onCreate(saveInstanceState: Bundle?)
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
    fun onSaveInstanceState(outState: Bundle?)
    fun onTouchEvent(event: MotionEvent): Boolean
    fun onBackPressed()
}