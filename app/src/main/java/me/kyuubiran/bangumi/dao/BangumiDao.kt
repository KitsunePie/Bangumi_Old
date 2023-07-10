package me.kyuubiran.bangumi.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.kyuubiran.bangumi.data.Bangumi

@Dao
interface BangumiDao {
    @Insert
    fun insert(bangumi: Bangumi): Long

    @Update
    fun update(bangumi: Bangumi)

    @Delete
    fun delete(bangumi: Bangumi)

    @Query("SELECT * FROM bangumi")
    fun getAllBangumis(): List<Bangumi>

    @Query("SELECT EXISTS(SELECT 1 FROM bangumi WHERE id = :id LIMIT 1)")
    fun hasBangumi(id: Long): Boolean
}