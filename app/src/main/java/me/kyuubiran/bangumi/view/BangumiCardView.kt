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
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.databinding.BangumiCardViewBinding
import me.kyuubiran.bangumi.utils.toDpFloat
import me.kyuubiran.bangumi.utils.toDpInt

class BangumiCardView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : CardView(context, attributeSet) {

    private val binding by lazy { BangumiCardViewBinding.inflate(LayoutInflater.from(context), this, true) }

    var bangumi: Bangumi? = null

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
            CoroutineScope(Dispatchers.IO).launch {
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