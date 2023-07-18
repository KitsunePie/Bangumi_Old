package me.kyuubiran.bangumi.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kyuubiran.bangumi.MyApp
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.fragment.BangumiListFragment
import me.kyuubiran.bangumi.utils.coLaunchMain
import me.kyuubiran.bangumi.utils.coWithIO
import me.kyuubiran.bangumi.utils.coWithMain
import me.kyuubiran.bangumi.utils.runSuspend
import me.kyuubiran.bangumi.view.BangumiCardView

class BangumiListAdapter : RecyclerView.Adapter<BangumiListAdapter.BangumiViewHolder>() {
    private var bangumiShowList: MutableList<Bangumi> = mutableListOf()
    private var filter: (Bangumi.() -> Boolean)? = null
    lateinit var fragment: BangumiListFragment

    var bangumiList: MutableList<Bangumi> = mutableListOf()
        set(value) {
            field = value
            filter?.let { bangumiShowList = value.filter(it).toMutableList() } ?: run { bangumiShowList = value }

            coLaunchMain { runSuspend { notifyDataSetChanged() } }
        }

    inner class BangumiViewHolder(private val bangumiCardView: BangumiCardView) : RecyclerView.ViewHolder(bangumiCardView) {
        fun bind(bangumi: Bangumi) {
            bangumiCardView.binding.bgmcardTitleText.text = bangumi.title
            bangumiCardView.binding.bgmcardDescriptionText.text = bangumi.desc

            val epText = if (bangumi.totalEpisode > 0) "${bangumi.currentEpisode}/${bangumi.totalEpisode}"
            else "${bangumi.currentEpisode}"
            bangumiCardView.binding.bgmcardEpText.text = itemView.context.getString(R.string.ep_text, epText)
            bangumiCardView.binding.bgmcardButtonWatched.isEnabled = !bangumi.finished
        }
    }

    suspend fun newItem(): Bangumi {
        val b = Bangumi("Title")

        coWithIO {
            val db = AppDatabase.db.bangumiDao()
            db.insert(b)

            Log.i("BangumiListAdapter", "Inserted: ${b.id}")
        }

        bangumiList.add(0, b)
        bangumiShowList.add(0, b)

        coWithMain {
            notifyItemInserted(0)
        }
        return b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangumiViewHolder {
        val view = BangumiCardView(parent.context)
        return BangumiViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bangumiShowList.size
    }

    override fun onBindViewHolder(holder: BangumiViewHolder, position: Int) {
        val bangumi = bangumiShowList[position]
        (holder.itemView as BangumiCardView).let {
            it.bangumi = bangumi
            it.adapter = this
        }
        holder.bind(bangumi)
    }

    fun getItemPos(bangumi: Bangumi): Int {
        var idx = bangumiShowList.indexOf(bangumi)
        if (idx < 0)
            idx = bangumiShowList.indexOfFirst { it.id == bangumi.id }

        return idx
    }

    fun applyFilter(filter: (Bangumi.() -> Boolean)?) {
        filter?.let { bangumiShowList = bangumiList.filter(it).toMutableList() } ?: run { bangumiShowList = bangumiList }
        notifyDataSetChanged()
    }
}