package me.kyuubiran.bangumi.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import me.kyuubiran.bangumi.data.Bangumi
import java.io.File
import java.io.InputStream

object Utils {
    fun getCoverImage(ctx: Context, bgm: Bangumi): Bitmap? {
        val dataPath = ctx.filesDir.absolutePath + "/bangumi/cover"
        val files = File(dataPath)
        if (!files.exists()) {
            files.mkdirs()
            return null
        }

        val file = files.listFiles()?.firstOrNull { it.isFile && it.nameWithoutExtension == bgm.id.toString() } ?: return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    fun deleteCoverImage(ctx: Context, bgm: Bangumi) {
        val dataPath = ctx.filesDir.absolutePath + "/bangumi/cover"
        val files = File(dataPath)
        if (!files.exists()) {
            files.mkdirs()
            return
        }

        val file = files.listFiles()?.firstOrNull { it.isFile && it.nameWithoutExtension == bgm.id.toString() } ?: return
        file.delete()
    }

    fun getCoverImagePath(ctx: Context, bgm: Bangumi): String {
        val dataPath = ctx.filesDir.absolutePath + "/bangumi/cover"
        val files = File(dataPath)
        if (!files.exists()) {
            files.mkdirs()
            return ""
        }

        val file = files.listFiles()?.firstOrNull { it.isFile && it.nameWithoutExtension == bgm.id.toString() } ?: return ""
        return file.absolutePath
    }

    fun hasCoverImage(ctx: Context, bgm: Bangumi): Boolean {
        val dataPath = ctx.filesDir.absolutePath + "/bangumi/cover"
        val files = File(dataPath)
        if (!files.exists()) {
            files.mkdirs()
            return false
        }

        return files.listFiles()?.any { it.isFile && it.nameWithoutExtension == bgm.id.toString() } ?: return false
    }

    fun saveCoverImage(ctx: Context, bgm: Bangumi, ifs: InputStream) {
        val dataPath = ctx.filesDir.absolutePath + "/bangumi/cover"
        val files = File(dataPath)
        if (!files.exists()) {
            files.mkdirs()
        }

        val exists = hasCoverImage(ctx, bgm)

        if (exists) {
            val file = files.listFiles()?.firstOrNull { it.isFile && it.nameWithoutExtension == bgm.id.toString() } ?: return
            file.delete()
        }

        File(dataPath, "${bgm.id}.jpg").outputStream().use {
            ifs.copyTo(it)
        }
    }

    fun Int.colorStr() = String.format("#%06X", 0xFFFFFF and this)
}