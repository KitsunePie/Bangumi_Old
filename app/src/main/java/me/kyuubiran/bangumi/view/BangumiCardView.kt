package me.kyuubiran.bangumi.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.databinding.BangumiCardViewBinding

class BangumiCardView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) :
    CardView(context, attributeSet, defStyleAttr) {

    private val binding by lazy { BangumiCardViewBinding.inflate(LayoutInflater.from(context), this, true) }

    var bangumi: Bangumi? = null

    init {

        setOnLongClickListener {
            // TODO: show edit dialog
            true
        }

        binding.bgmcardButtonWatched.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                watchedButtonClicked()
            }
        }
    }

    private suspend fun watchedButtonClicked() {
        val b = bangumi ?: run { Log.w("BangumiCardView", "Bangumi was null!"); return }
        val dao = AppDatabase.db.bangumiDao()

        var has = false

        suspend {
            has = dao.hasBangumi(b.id)
        }()

        if (!has) {
            Log.w("BangumiCardView", "No such bangumi found!")
            return
        }

        ++b.episodeCurrent
        if (b.episodeMax > 0 && b.episodeMax < b.episodeCurrent)
            b.episodeMax = b.episodeCurrent

        suspend {
            AppDatabase.db.bangumiDao().update(b)
        }()
    }

}
