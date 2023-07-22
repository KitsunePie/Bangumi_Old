package me.kyuubiran.bangumi.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Bangumi(
    var title: String,
    var desc: String = "",
    var currentEpisode: Int = 1,
    var totalEpisode: Int = 0,
    // Tag ids
    @ColumnInfo(name = "tags")
    @SerialName("tags")
    var baseTags: String = "",
    /** marks finish watched */
) : Comparable<Bangumi> {

    val finished
        get() = currentEpisode == totalEpisode && totalEpisode > 0

    override fun compareTo(other: Bangumi): Int {
        if (finished != other.finished) {
            return if (finished) 1 else -1
        }

        return other.id.compareTo(id)
    }

    // Tag ids
    var tags
        get() = baseTags.takeIf { it.isNotBlank() }
            ?.split("|")
            ?.takeIf { it.isNotEmpty() }
            ?.map { it.toLong() }
            ?.toMutableList() ?: mutableListOf()
        set(value) {
            baseTags = value.joinToString("|")
        }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}