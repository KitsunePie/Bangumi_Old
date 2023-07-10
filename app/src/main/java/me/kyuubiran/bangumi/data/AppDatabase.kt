package me.kyuubiran.bangumi.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.kyuubiran.bangumi.dao.BangumiDao
import me.kyuubiran.bangumi.utils.ListStringConverter

@Database(entities = [Bangumi::class], version = 1)
@TypeConverters(ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        lateinit var db: AppDatabase
    }

    abstract fun bangumiDao(): BangumiDao
}