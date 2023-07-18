package me.kyuubiran.bangumi.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.kyuubiran.bangumi.dao.BangumiDao
import me.kyuubiran.bangumi.dao.TagDao
import me.kyuubiran.bangumi.utils.TagListConverter

@Database(entities = [Bangumi::class, Tag::class], exportSchema = true, version = 1)
@TypeConverters(TagListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        lateinit var db: AppDatabase
    }

    abstract fun bangumiDao(): BangumiDao
    abstract fun tagDao(): TagDao
}