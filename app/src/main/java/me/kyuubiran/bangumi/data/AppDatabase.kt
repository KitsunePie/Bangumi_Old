package me.kyuubiran.bangumi.data

import androidx.room.Database
import androidx.room.RoomDatabase
import me.kyuubiran.bangumi.dao.BangumiDao
import me.kyuubiran.bangumi.dao.BangumiTagDao

@Database(entities = [Bangumi::class, BangumiTag::class], exportSchema = true, version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        lateinit var db: AppDatabase
    }

    abstract fun bangumiDao(): BangumiDao
    abstract fun tagDao(): BangumiTagDao
}