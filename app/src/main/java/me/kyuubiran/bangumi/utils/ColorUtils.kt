package me.kyuubiran.bangumi.utils

import android.graphics.Color

object ColorUtils {
    fun getDarkerColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.8f
        return Color.HSVToColor(hsv)
    }
}