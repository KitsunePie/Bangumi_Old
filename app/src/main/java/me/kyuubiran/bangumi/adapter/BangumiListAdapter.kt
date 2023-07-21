package me.kyuubiran.bangumi.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
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

    var bangumiList: MutableList<Bangumi> = mutableListOf()
        set(value) {
            field = value
            filter?.let { bangumiShowList = value.filter(it).toMutableList() } ?: run { bangumiShowList = value }
            bangumiShowList.sort()

            coLaunchMain { notifyDataSetChanged() }
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

        bangumiList.add(b)
        bangumiShowList.add(b)

        return b
    }

    suspend fun removeItem(bangumi: Bangumi) {
        val pos = getItemPos(bangumi)

        if (pos < 0) {
            Log.w("BangumiListAdapter", "removeItem: Item not found")
            return
        }

        val pos2 = bangumiList.indexOfFirst { it.id == bangumi.id }
        if (pos2 >= 0)
            bangumiList.removeAt(pos2)

        coWithMain {
            Log.d("BangumiListAdapter", "removeItem: notify removed at $pos")
            notifyItemRemoved(pos)
//            TODO: Fix this
//            I don't know why it cause stupid crash, it seems works fine but may cause mem leak because the element still in the list
//            java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionBangumiViewHolder{dcbd207 position=0 id=-1, oldPos=1, pLpos:1 scrap [attachedScrap] tmpDetached no parent} androidx.recyclerview.widget.RecyclerView{d340f37 VFED..... ......I. 0,0-1080,786 #7f08010f app:id/main_bgm_layout}, adapter:me.kyuubiran.bangumi.adapter.BangumiListAdapter@3f1f7be, layout:androidx.recyclerview.widget.LinearLayoutManager@276901f, context:me.kyuubiran.bangumi.MainActivity@6f8bfad
//            Fuck stupid android fuck stupid google

//            bangumiShowList.removeAt(pos)
        }

        coWithIO {
            val db = AppDatabase.db.bangumiDao()
            db.delete(bangumi)

            Log.i("BangumiListAdapter", "Deleted: ${bangumi.id}")
        }
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
            it.bangumiListAdapter = this
        }
        holder.bind(bangumi)
    }

    fun getItemPos(bangumi: Bangumi): Int {
        return bangumiShowList.indexOfFirst { it.id == bangumi.id }
    }

    suspend fun applyFilter(filter: (Bangumi.() -> Boolean)?) {
        filter?.let { bangumiShowList = bangumiList.filter(it).toMutableList() } ?: run { bangumiShowList = bangumiList }
        coWithMain { notifyDataSetChanged() }
    }
}