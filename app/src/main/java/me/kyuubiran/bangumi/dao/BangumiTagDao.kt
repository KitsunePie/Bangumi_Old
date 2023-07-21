package me.kyuubiran.bangumi.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.kyuubiran.bangumi.data.BangumiTag

@Dao
interface BangumiTagDao {
    @Insert
    fun insert(tag: BangumiTag): Long

    @Update
    fun update(tag: BangumiTag)

    @Delete
    fun delete(tag: BangumiTag)

    @Query("SELECT * FROM bangumitag")
    fun getAllTags(): List<BangumiTag>

    @Query("SELECT EXISTS(SELECT 1 FROM bangumitag WHERE id = :id LIMIT 1)")
    fun hasTag(id: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM bangumitag WHERE name = :tagName LIMIT 1)")
    fun hasTag(tagName: String): Boolean
}