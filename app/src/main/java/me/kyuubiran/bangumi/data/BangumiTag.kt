package me.kyuubiran.bangumi.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import me.kyuubiran.bangumi.utils.Colors

@Entity
@Serializable
data class BangumiTag(
    var name: String,
    var priority: Int = 0,
    var color: Int = Colors.ORANGE
) : Comparable<BangumiTag> {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun compareTo(other: BangumiTag): Int {
        return other.priority.compareTo(priority)
    }

    companion object {
        @JvmStatic
        @Ignore
        var allTagMap: MutableMap<Long, BangumiTag> = mutableMapOf()

        @get:Ignore
        val allTagList: List<BangumiTag>
            get() = allTagMap.values.toMutableList().apply { sort() }
    }
}