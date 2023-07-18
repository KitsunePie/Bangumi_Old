package me.kyuubiran.bangumi.utils

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.kyuubiran.bangumi.data.Tag

class TagListConverter {

    @TypeConverter
    fun fromTagList(tagList: List<Tag>): String {
        return Json.encodeToString(tagList)
    }

    @TypeConverter
    fun toTagList(json: String): List<Tag> {
        return Json.decodeFromString(json)
    }
}