package me.kyuubiran.bangumi

import android.app.Application
import androidx.room.Room
import me.kyuubiran.bangumi.data.AppDatabase

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        AppDatabase.db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bangumi"
        ).build()
    }
}