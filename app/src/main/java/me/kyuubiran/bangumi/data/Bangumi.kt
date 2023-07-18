package me.kyuubiran.bangumi.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Bangumi(
    var title: String,
    var desc: String = "",
    var currentEpisode: Int = 1,
    var totalEpisode: Int = -1,
    var offset: Int = 0,
    var tags: MutableList<Tag> = mutableListOf(),
    /** marks finish watched */
) : Comparable<Bangumi> {
    val finished
        get() = currentEpisode == totalEpisode && totalEpisode > 0

    override fun compareTo(other: Bangumi): Int {
        if (finished != other.finished) {
            return if (finished) 1 else -1
        }

        return id.compareTo(other.id)
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}