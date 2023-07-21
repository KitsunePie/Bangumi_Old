package me.kyuubiran.bangumi.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.BangumiTag
import me.kyuubiran.bangumi.utils.coLaunchMain
import me.kyuubiran.bangumi.utils.coWithIO
import me.kyuubiran.bangumi.utils.coWithMain
import me.kyuubiran.bangumi.view.BangumiTagItemView

class FragmentTagListAdapter : RecyclerView.Adapter<FragmentTagListAdapter.TagViewHolder>() {

    var tagList: MutableList<BangumiTag> = mutableListOf()
        set(value) {
            field = value
            field.sort()
            coLaunchMain { notifyDataSetChanged() }
        }

    inner class TagViewHolder(private val tagView: BangumiTagItemView) : RecyclerView.ViewHolder(tagView) {
        fun bind(tag: BangumiTag) {
            tagView.setBangumiTag(tag)
            tagView.adapter = this@FragmentTagListAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val v = BangumiTagItemView(parent.context)
        return TagViewHolder(v)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagList[position]
        holder.bind(tag)
    }

    suspend fun newItem(): BangumiTag {
        val tag = BangumiTag("Tag")
        coWithIO { AppDatabase.db.tagDao().insert(tag) }
        BangumiTag.allTagMap[tag.id] = tag
        tagList.add(tag)

//        coWithMain {
//            notifyItemInserted(0)
//        }
        return tag
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    fun getItemPos(bangumiTag: BangumiTag): Int {
        return tagList.indexOfFirst { it.id == bangumiTag.id }
    }

    fun isEmpty() = tagList.isEmpty()

    suspend fun removeItem(bangumiTag: BangumiTag) {
        val pos = getItemPos(bangumiTag)

        if (pos < 0) {
            Log.w("FragmentTagListAdapter", "removeItem: Item not found")
            return
        }

        tagList.removeAt(pos)
        tagList.sort()

        coWithIO {
            AppDatabase.db.tagDao().delete(bangumiTag)
            BangumiTag.allTagMap.remove(bangumiTag.id)

            coWithMain { notifyDataSetChanged() }
        }
    }
}