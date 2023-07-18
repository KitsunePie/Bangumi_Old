package me.kyuubiran.bangumi.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.ParcelField
import kotlinx.serialization.Serializable
import me.kyuubiran.bangumi.utils.Colors

@Entity
@Serializable
data class Tag(
    var name: String,
    var priority: Int = 0,
    var color: Int = Colors.ORANGE
) : Comparable<Tag> {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    ) {
        id = parcel.readInt()
    }

    override fun compareTo(other: Tag): Int {
        return priority.compareTo(other.priority)
    }

    companion object CREATOR : Parcelable.Creator<Tag> {
        override fun createFromParcel(parcel: Parcel): Tag {
            return Tag(parcel)
        }

        override fun newArray(size: Int): Array<Tag?> {
            return arrayOfNulls(size)
        }
    }
}