package me.kyuubiran.bangumi.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.view.BangumiCardView

class BangumiListAdapter : RecyclerView.Adapter<BangumiListAdapter.BangumiViewHolder>() {
    var bangumiList: MutableList<Bangumi> = mutableListOf()

    inner class BangumiViewHolder(val bangumiCardView: BangumiCardView) : RecyclerView.ViewHolder(bangumiCardView) {

        fun bind(bangumi: Bangumi) {
            bangumiCardView.binding.bgmcardTitleText.text = bangumi.title
            bangumiCardView.binding.bgmcardDescriptionText.text = bangumi.desc

            val epText = if (bangumi.episodeMax > 0) "${bangumi.episodeCurrent}/${bangumi.episodeMax}"
            else "${bangumi.episodeCurrent}"
            bangumiCardView.binding.bgmcardEpText.text = itemView.context.getString(R.string.ep_text, epText)
            bangumiCardView.binding.bgmcardButtonWatched.isEnabled = !bangumi.finished
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
        (holder.itemView as BangumiCardView).let {
            it.bangumi = bangumi
            it.adapter = this
        }
        holder.bind(bangumi)
    }

    fun getItemPos(bangumi: Bangumi): Int {
        return bangumiList.indexOf(bangumi)
    }
}