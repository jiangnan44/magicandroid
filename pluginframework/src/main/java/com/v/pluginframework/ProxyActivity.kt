package com.v.pluginframework

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Author:v
 * Time:2022/3/30
 */
class ProxyActivity : Activity() {
    private lateinit var pluginObj: ProxyActivityInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val className = intent.getStringExtra("ClassName")
        Log.w("vvv", "className=$className")
        try {
            val pluginClass = classLoader.loadClass(className)
            val pluginConstructor = pluginClass.getConstructor()
            pluginObj =
                pluginConstructor.newInstance() as ProxyActivityInterface

            pluginObj.attach(this)
            pluginObj.onCreate(savedInstanceState)
        } catch (e: Exception) {
            if (e is ClassCastException) {
                finish()
                Toast.makeText(this, "Not Support!!", Toast.LENGTH_SHORT).show()
                return
            }
            e.printStackTrace()
        }
    }

    override fun startActivity(oldIntent: Intent?) {
        val clsName = oldIntent?.component?.className

        super.startActivity(Intent(this, ProxyActivity::class.java).apply {
            putExtra("ClassName", clsName)
        })
    }

    override fun getClassLoader(): ClassLoader {
        return HookManager.instance.loader
    }

    override fun getResources(): Resources {
        return HookManager.instance.res
    }

    override fun onResume() {
        super.onResume()
        pluginObj.onResume()
    }

    override fun onStart() {
        super.onStart()
        pluginObj.onStart()
    }

//    .... other method
}