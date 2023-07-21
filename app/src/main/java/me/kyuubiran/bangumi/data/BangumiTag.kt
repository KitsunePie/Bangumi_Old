package me.kyuubiran.bangumi.data

import android.os.Parcel
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
    var id: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    ) {
        id = parcel.readInt()
    }

    override fun compareTo(other: BangumiTag): Int {
        return other.priority.compareTo(priority)
    }

    companion object {
        @JvmStatic
        @Ignore
        var allTagMap: MutableMap<Int, BangumiTag> = mutableMapOf()

        @get:Ignore
        val allTagList: List<BangumiTag>
            get() = allTagMap.values.toMutableList().apply { sort() }
    }
}