package me.kyuubiran.bangumi.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.view.BangumiCardView

class BangumiListAdapter : RecyclerView.Adapter<BangumiListAdapter.BangumiViewHolder>() {
    var bangumiList: MutableList<Bangumi> = mutableListOf()

    inner class BangumiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.bgmcard_text_title)
        private val descTextView: TextView = itemView.findViewById(R.id.bgmcard_text_description)
        private val epTextView: TextView = itemView.findViewById(R.id.bgmcard_ep_text)

        fun bind(bangumi: Bangumi) {
            titleTextView.text = bangumi.title
            descTextView.text = bangumi.desc

            val epText = if (bangumi.episodeMax > 0) "${bangumi.episodeCurrent}/${bangumi.episodeMax}"
            else "${bangumi.episodeCurrent}"
            epTextView.text = itemView.context.getString(R.string.ep_text, epText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangumiViewHolder {
        val view = BangumiCardView(parent.context)
        return BangumiViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bangumiList.size
    }

    override fun onBindViewHolder(holder: BangumiViewHolder, position: Int) {
        val bangumi = bangumiList[position]
        (holder.itemView as BangumiCardView).bangumi = bangumi
        holder.bind(bangumi)
    }
}