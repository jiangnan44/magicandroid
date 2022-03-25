package com.v.magicandroid.invocation

import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Author:v
 * Time:2022/2/24
 */

interface IHello {
    fun hello(arg: String)
}

class Hello : IHello {
    override fun hello(arg: String) {
        Log.i("vvv", "hello $arg")
    }
}


class HelloInvocationHandler : InvocationHandler {

    companion object {
        fun newInstance(obj: Any): Any {
            return Proxy.newProxyInstance(
                obj.javaClass.classLoader,
                obj.javaClass.interfaces,
                HelloInvocationHandler(obj)
            )
        }
    }

    private val obj: Any


    private constructor(obj: Any) {
        this.obj = obj
    }


    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {

        Log.i("vvv", "do thing before $method $args")
        val result = method?.invoke(obj, args?.get(0))

        Log.i("vvv", "do thing after ")
        return result
    }
}