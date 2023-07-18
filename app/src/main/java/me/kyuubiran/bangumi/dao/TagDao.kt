package me.kyuubiran.bangumi.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.kyuubiran.bangumi.data.Tag

@Dao
interface TagDao {
    @Insert
    fun insert(tag: Tag): Long

    @Update
    fun update(tag: Tag)

    @Delete
    fun delete(tag: Tag)

    @Query("SELECT * FROM tag")
    fun getAllTags(): List<Tag>

    @Query("SELECT EXISTS(SELECT 1 FROM tag WHERE id = :id LIMIT 1)")
    fun hasTag(id: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM tag WHERE name = :tagName LIMIT 1)")
    fun hasTag(tagName: String): Boolean
}