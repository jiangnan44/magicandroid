package com.dahu.vrouter.annotion

/**
 * Author:v
 * Time:2021/9/16
 */

@Target(AnnotationTarget.CLASS)
annotation class VRouter(
    val path: String
)

interface IRouter {
    fun getPaths(): String
}