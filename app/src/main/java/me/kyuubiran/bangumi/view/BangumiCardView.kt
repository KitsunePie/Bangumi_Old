package me.kyuubiran.bangumi.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.updateMargins
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.kyuubiran.bangumi.adapter.BangumiListAdapter
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.databinding.BangumiCardViewBinding
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.coWithMain
import me.kyuubiran.bangumi.utils.runSuspend
import me.kyuubiran.bangumi.utils.toDpFloat
import me.kyuubiran.bangumi.utils.toDpInt

class BangumiCardView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : CardView(context, attributeSet) {

    val binding by lazy { BangumiCardViewBinding.inflate(LayoutInflater.from(context), this, true) }

    var bangumi: Bangumi? = null
    var adapter: BangumiListAdapter? = null

    init {

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setOnLongClickListener {
            // TODO: show edit dialog
            true
        }

        binding.bgmcardButtonWatched.setOnClickListener {
            coLaunchIO {
                watchedButtonClicked()
            }
        }

        cardElevation = 4.toDpFloat(this)
        radius = 8.toDpFloat(this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        (layoutParams as MarginLayoutParams).updateMargins(8.toDpInt(this), 4.toDpInt(this), 8.toDpInt(this), 4.toDpInt(this))
    }

    private suspend fun watchedButtonClicked() {
        val b = bangumi ?: run { Log.w("BangumiCardView", "Bangumi was null!"); return }
        val dao = AppDatabase.db.bangumiDao()

//        val has = runSuspend {
//            dao.hasBangumi(b.id)
//        }
//
//        if (!has) {
//            Log.w("BangumiCardView", "No such bangumi found!")
//            return
//        }

        if (b.finished)
            return

        if (++b.episodeCurrent == b.episodeMax) {
            Log.i("BangumiCardView", "Finished watch ${b.title}")
        }

//        runSuspend {
//            dao.update(b)
//        }

        coWithMain {
            adapter?.let {
                it.notifyItemChanged(it.getItemPos(b))
            }
        }
    }
}