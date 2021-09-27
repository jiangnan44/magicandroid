package com.dahu.vrouter

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.ArrayMap
import android.util.Log
import androidx.annotation.RequiresApi
import com.dahu.vrouter.annotion.VRouter

/**
 * Author:v
 * Time:2021/9/16
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object Router {
    private val routerMap = ArrayMap<String, String>()

    fun initRouter(application: Application) {
        initMap(application)
    }

    private fun initMap(application: Application) {

        if (routerMap.isNotEmpty()) return

        val info = application.packageManager.getPackageInfo(
            application.packageName,
            PackageManager.GET_ACTIVITIES
        )

        for (activity in info.activities) {
            val an = activity.name
            val clazz = Class.forName(an)
            val annotation = clazz.getAnnotation(VRouter::class.java)
            if (annotation != null) {
                routerMap[annotation.path] = an
                Log.w("router", "${activity.name} is Anotationed")
            }
        }
    }
}