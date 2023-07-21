package me.kyuubiran.bangumi.utils

import androidx.room.TypeConverter
import androidx.room.util.splitToIntList

class TagListConverter {
    @TypeConverter
    fun fromTagList(tagList: List<Long>): String {
        return tagList.toString()
    }

    @TypeConverter
    fun toTagList(str: String): List<Long> {
        val s = str.substring(1, str.length - 1).replace(" ", "")
        if (s.isBlank()) return emptyList()

        return s.split(",").map { it.toLongOrNull() ?: -1L }
    }
}