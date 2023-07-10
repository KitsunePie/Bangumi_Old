package me.kyuubiran.bangumi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bangumi(
    var title: String,
    var desc: String = "",
    var episodeCurrent: Int = 1,
    var episodeMax: Int = -1,
    var tags: MutableList<String> = arrayListOf(),
    /** marks finish watched */
    var finished: Boolean = false,
    ) : Comparable<Bangumi> {
    override fun compareTo(other: Bangumi): Int {
        if (finished != other.finished) {
            return if (finished) 1 else -1
        }

        return id.compareTo(other.id)
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}