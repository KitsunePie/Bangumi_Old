package me.kyuubiran.bangumi.utils

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromString(value: String?): MutableList<String> {
        return value?.split(",")?.toMutableList() ?: mutableListOf()
    }

    @TypeConverter
    fun toString(value: MutableList<String>?): String {
        return value?.joinToString(",") ?: ""
    }
}