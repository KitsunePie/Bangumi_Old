package me.kyuubiran.bangumi.utils

import android.content.Context
import android.graphics.drawable.Drawable
import me.kyuubiran.bangumi.data.Bangumi
import java.io.File

object Utils {
    fun getCoverImage(ctx: Context, bgm: Bangumi): Drawable? {
        val dataPath = ctx.filesDir.absolutePath + "/bangumi/cover"
        val files = File(dataPath)
        if (!files.exists()) {
            files.mkdirs()
            return null
        }

        val file = files.listFiles()?.firstOrNull { it.isFile && it.nameWithoutExtension == bgm.id.toString() } ?: return null
        return Drawable.createFromPath(file.absolutePath)
    }

    fun Int.colorStr() = String.format("#%06X", 0xFFFFFF and this)
}