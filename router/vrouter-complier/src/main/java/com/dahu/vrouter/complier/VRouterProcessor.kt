package com.dahu.vrouter.complier

import com.dahu.vrouter.annotion.IRouter
import com.dahu.vrouter.annotion.VRouter
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * Author:v
 * Time:2021/9/16
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class VRouterProcessor : AbstractProcessor() {
    private lateinit var messager: Messager
    private lateinit var typeUtils: Types
    private lateinit var elementUtils: Elements
    private lateinit var filer: Filer

    private val injectMaps = hashMapOf<String, String>()


    override fun init(env: ProcessingEnvironment?) {
        super.init(env)
        env?.let {
            messager = env.messager
            typeUtils = env.typeUtils
            elementUtils = env.elementUtils
            filer = env.filer
        }

    }

    @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
    override fun process(set: MutableSet<out TypeElement>?, env: RoundEnvironment?): Boolean {
        if (set == null || env == null) return false
        log("process in")
        val elementList = env.getElementsAnnotatedWith(VRouter::class.java)

        if (elementList.isNullOrEmpty()) return false
        val activityType = elementUtils.getTypeElement(VRouterConst.TYPE_ACTIVITY).asType()


        val sb = StringBuilder("{")

        for (element in elementList) {
            log(element.toString())
            val type = element.asType()

            if (typeUtils.isSubtype(type, activityType)) {
                val typeElement = element as TypeElement

                val path = typeElement.getAnnotation(VRouter::class.java).path
                if (injectMaps[path] == null) {
                    val className = typeElement.qualifiedName.toString()
                    injectMaps[path] = className
                    log("path:$path====== className:$className")
                    sb.append("\"$path\":")
                        .append("\"${className}\",")
                }
            }
        }

        sb.deleteAt(sb.lastIndexOf(","))
        sb.append("}")

        log("json:::${sb.toString()}")
        val funSpec = FunSpec.builder(VRouterConst.FUN_GET_CLASSES)
            .returns(String::class)
            .addStatement("return %S", sb.toString())
            .addModifiers(KModifier.OVERRIDE)
            .build()
        val pkg = VRouter::class.java.`package`.name
        log(pkg)
        val className = VRouterConst.FILE_NAME.plus(processingEnv.hashCode())
        val typeSpec = TypeSpec.classBuilder(className)
            .addFunction(funSpec)
            .addSuperinterface(IRouter::class)
            .build()
        val file = FileSpec.builder(pkg, className)
            .addType(typeSpec)
            .build()


        val generatedDir = processingEnv.options["kapt.kotlin.generated"]
        val outputFile = File(generatedDir).apply { mkdirs() }
        log(outputFile.absolutePath)
        file.writeTo(outputFile)
        return true
    }


    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return setOf(VRouter::class.java.canonicalName).toMutableSet()
    }


    private fun log(msg: String?) {
        messager.printMessage(Diagnostic.Kind.WARNING, "VRouterProcessor:::$msg\n")
    }

}