package me.kyuubiran.bangumi.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kyuubiran.bangumi.data.BangumiTag
import me.kyuubiran.bangumi.view.BangumiCardTagView

class CardTagListAdapter : RecyclerView.Adapter<CardTagListAdapter.TagViewHolder>() {
    private var tagList: MutableList<BangumiTag> = mutableListOf()

    fun setTags(list: List<Long>) {
        tagList.clear()
        for (i in list) {
            if (BangumiTag.allTagMap.containsKey(i)) tagList.add(BangumiTag.allTagMap[i]!!)
        }
        tagList.sort()
        notifyDataSetChanged()
    }

    inner class TagViewHolder(private val tagView: BangumiCardTagView) : RecyclerView.ViewHolder(tagView) {
        fun bind(bangumiTag: BangumiTag) {
            tagView.setBangumiTag(bangumiTag)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val v = BangumiCardTagView(parent.context)
        return TagViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagList[position]
        holder.bind(tag)
    }

    fun isEmpty() = tagList.isEmpty()
}