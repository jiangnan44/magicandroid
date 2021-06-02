package com.v.commonlib.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Author:v
 * Time:2020/12/29
 */
object FileUtil {

    public const val PROVIDER_AUTHORITIES = "com.v.magicandroid.provider"
    private const val TAG = "FileUtil"
    private const val DIR_PIC = "pictures"
    private const val DIR_VIDEO_CACHE = "videocache"


    fun getAppRootDir(context: Context): File {
        var file: File? = if (hasExternalStorage()) {
            context.getExternalFilesDir("")
        } else {
            context.filesDir
        }

        if (file != null) {
            return file
        }

        file = File(Environment.getDataDirectory(), context.packageName)
        if (!file.exists()) {
            file.mkdirs()
        }

        return file
    }

    fun getAppRootDirPath(context: Context): String {
        val file = getAppRootDir(context)
        return file.absolutePath
    }

    fun getAppVideoCacheDir(context: Context): File {
        val root = getAppRootDir(context)
        val file = File(root, DIR_VIDEO_CACHE)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun getAppPictureCacheDir(context: Context): File {
        val root = getAppRootDir(context)
        val file = File(root, DIR_PIC)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun hasExternalStorage(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }


    fun getProviderFileUri(context: Context, file: File): Uri? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, PROVIDER_AUTHORITIES, file)
        } else {
            return Uri.fromFile(file)
        }
    }

    fun getFileUri(file: File): Uri {
        return Uri.fromFile(file)
    }

    fun saveBitmap2File(context: Context, avatar: ByteArray, phone: String?): Boolean {
        if (phone.isNullOrBlank()) return false
        val file = File(getAppPictureCacheDir(context), phone)
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            val fos = FileOutputStream(file)
            fos.write(avatar)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun getBitmapFromCacheFile(context: Context, phone: String?): Bitmap? {
        if (phone.isNullOrBlank()) return null
        val file = File(getAppPictureCacheDir(context), phone)
        if (!file.exists()) {
            return null
        }
        return BitmapFactory.decodeFile(file.absolutePath)
    }
}