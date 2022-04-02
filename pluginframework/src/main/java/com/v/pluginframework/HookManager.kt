package com.v.pluginframework

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import dalvik.system.DexClassLoader
import java.io.File

/**
 * Author:v
 * Time:2022/3/30
 */
class HookManager private constructor() {

    companion object {
        val instance by lazy {
            HookManager()
        }
    }


    lateinit var res: Resources
    lateinit var loader: ClassLoader
    lateinit var pkgInfo: PackageInfo


    fun loadPlugin(activity: Activity) {
        //maybe download plugin apk from net
        val dir = activity.getDir("plugin", Context.MODE_PRIVATE)
        val name = "plugin.apk"
        val path = File(dir, name).absolutePath
        Log.w("vvv","path=$path")


        val dexOutDir = activity.getDir("dex", Context.MODE_PRIVATE)
        loader = DexClassLoader(path, dexOutDir.absolutePath, null, activity.classLoader)

        val pm = activity.packageManager
        pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)!!

        val assetManagerCls = AssetManager::class.java
        try {
            val assetManagerObj = assetManagerCls.newInstance()
            val addAssetPathMethod = assetManagerCls.getMethod("addAssetPath", String::class.java)
            addAssetPathMethod.isAccessible = true
            addAssetPathMethod.invoke(assetManagerObj, path)

            res = Resources(
                assetManagerObj,
                activity.resources.displayMetrics,
                activity.resources.configuration
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}