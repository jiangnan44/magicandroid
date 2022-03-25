package com.v.magicandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.v.magicandroid.invocation.Hello
import com.v.magicandroid.invocation.HelloInvocationHandler
import com.v.magicandroid.invocation.IHello

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?) {
        val iHello = HelloInvocationHandler.newInstance(Hello()) as IHello
        iHello.hello("magic")
    }


}