package me.kyuubiran.bangumi.utils

import androidx.room.TypeConverter
import androidx.room.util.splitToIntList

class TagListConverter {
    @TypeConverter
    fun fromTagList(tagList: List<Int>): String {
        return tagList.toString()
    }

    @TypeConverter
    fun toTagList(str: String): List<Int> {
        val s = str.substring(1, str.length - 1).replace(" ", "")
        if (s.isBlank()) return emptyList()

        return splitToIntList(s) ?: emptyList()
    }
}