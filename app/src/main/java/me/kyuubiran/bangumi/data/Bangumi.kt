package me.kyuubiran.bangumi.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Bangumi(
    var title: String,
    var desc: String = "",
    var episodeCurrent: Int = 1,
    var episodeMax: Int = -1,
    var tags: MutableList<String> = arrayListOf(),
    /** marks finish watched */
) : Comparable<Bangumi> {
    val finished
        get() = episodeCurrent == episodeMax && episodeMax > 0

    override fun compareTo(other: Bangumi): Int {
        if (finished != other.finished) {
            return if (finished) 1 else -1
        }

        return id.compareTo(other.id)
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}