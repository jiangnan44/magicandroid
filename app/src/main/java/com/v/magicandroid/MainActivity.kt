package com.v.magicandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.v.magicandroid.invocation.Hello
import com.v.magicandroid.invocation.HelloInvocationHandler
import com.v.magicandroid.invocation.IHello
import com.v.pluginframework.HookManager
import com.v.pluginframework.ProxyActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_load_plugin -> {
                loadPlugin()
            }
            R.id.btn_to_plugin -> {
                toPluginPage()
            }
            else -> {
                val iHello = HelloInvocationHandler.newInstance(Hello()) as IHello
                iHello.hello("magic")
            }
        }
    }

    private fun toPluginPage() {
        startActivity(Intent(this, ProxyActivity::class.java).apply {
            putExtra("ClassName", HookManager.instance.pkgInfo.activities[0].name)
        })
    }

    private fun loadPlugin() {
        HookManager.instance.loadPlugin(this)
        Toast.makeText(this, "Loaded!!", Toast.LENGTH_SHORT).show()
    }


}