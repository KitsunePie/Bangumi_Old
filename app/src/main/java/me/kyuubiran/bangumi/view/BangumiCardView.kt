package me.kyuubiran.bangumi.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.adapter.BangumiListAdapter
import me.kyuubiran.bangumi.adapter.CardTagListAdapter
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.databinding.BangumiCardViewBinding
import me.kyuubiran.bangumi.fragment.BangumiModifyFragment
import me.kyuubiran.bangumi.utils.DialogUtils
import me.kyuubiran.bangumi.utils.Utils
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.coWithMain
import me.kyuubiran.bangumi.utils.runSuspend
import me.kyuubiran.bangumi.utils.toDpFloat
import me.kyuubiran.bangumi.utils.toDpInt

class BangumiCardView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : CardView(context, attributeSet) {

    val binding by lazy { BangumiCardViewBinding.inflate(LayoutInflater.from(context), this, true) }

    private val tagListAdapter: CardTagListAdapter = CardTagListAdapter()
    private lateinit var _bangumi: Bangumi

    private val nav by lazy { findNavController() }

    var bangumi: Bangumi
        set(value) {
            _bangumi = value
            tagListAdapter.setTags(value.tags)
            val cover = Utils.getCoverImage(context, value)
            if (cover != null) {
                binding.bgmcardCoverImage.setImageBitmap(cover)
            }
            requestLayout()
        }
        get() = _bangumi

    lateinit var bangumiListAdapter: BangumiListAdapter

    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setOnLongClickListener {
            showEditDialog()
            true
        }

        binding.bgmcardTagRecyclerView.adapter = tagListAdapter
        binding.bgmcardTagRecyclerView.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }

        binding.bgmcardButtonWatched.setOnClickListener {
            coLaunchIO {
                watchedButtonClicked()
            }
        }

        setCardBackgroundColor(resources.getColor(R.color.forground, context.theme))
        cardElevation = 0f
        radius = 10.toDpFloat(this)
    }

    private fun showEditDialog() {
        val edit = ImageTextButton(context).apply {
            text = context.getString(R.string.edit)
            drawable = ResourcesCompat.getDrawable(resources, R.drawable.baseline_edit_24, context.theme)
        }

        val delete = ImageTextButton(context).apply {
            text = context.getString(R.string.delete)
            drawable = ResourcesCompat.getDrawable(resources, R.drawable.baseline_delete_forever_24, context.theme)
        }

        val dialog = DialogUtils.showAlertDialog(context) {
            setTitle(context.getString(R.string.edit_with, bangumi.title))

            val ll = LinearLayout(context).apply {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                gravity = LinearLayout.HORIZONTAL
                orientation = LinearLayout.VERTICAL

                updatePadding(16.toDpInt(context), 8.toDpInt(context), 16.toDpInt(context), 8.toDpInt(context))

                addView(edit)
                addView(delete)
            }

            setView(ll)
        }

        edit.setOnClickListener {
            BangumiModifyFragment.bangumiInEdit = bangumi
            nav.navigate(R.id.action_BangumiListFragment_to_bangumiModifyFragment)
            dialog.dismiss()
        }

        delete.setOnClickListener {
            DialogUtils.showConfirmDialog(
                context,
                context.getString(R.string.delete_with, bangumi.title),
                context.getString(R.string.delete_with_message, bangumi.title)
            ) {
                BangumiModifyFragment.bangumiInEdit = null
                coLaunchIO { bangumiListAdapter.removeItem(bangumi) }
                dialog.dismiss()
            }
        }
    }

    private val titleLayoutConstraintSet by lazy {
        val titleLayout = binding.bgmcardTitleLayout

        val first = ConstraintSet().apply {
            clone(titleLayout)
            connect(R.id.bgmcard_title_layout, ConstraintSet.START, R.id.bgmcard_cover_image, ConstraintSet.END)
        }
        val second = ConstraintSet().apply {
            clone(titleLayout)
            connect(R.id.bgmcard_title_layout, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        }

        first to second
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        (layoutParams as MarginLayoutParams).updateMargins(8.toDpInt(this), 4.toDpInt(this), 8.toDpInt(this), 4.toDpInt(this))

        val titleLayout = binding.bgmcardTitleLayout
        val imageCover = binding.bgmcardCoverImage

        imageCover.visibility = if (imageCover.drawable == null) View.GONE else View.VISIBLE

        if (imageCover.visibility == View.VISIBLE) {
            titleLayoutConstraintSet.first.applyTo(titleLayout)
        } else {
            titleLayoutConstraintSet.second.applyTo(titleLayout)
        }
    }

    private suspend fun watchedButtonClicked() {
        val dao = AppDatabase.db.bangumiDao()

        val has = runSuspend {
            dao.hasBangumi(bangumi.id)
        }

        if (!has) {
            Log.w("BangumiCardView", "No such bangumi found!")
            return
        }

        if (bangumi.finished)
            return

        if (++bangumi.currentEpisode == bangumi.totalEpisode) {
            Log.i("BangumiCardView", "Finished watch ${bangumi.title}")
        }

        runSuspend {
            dao.update(bangumi)
        }

        coWithMain {
            bangumiListAdapter.let {
                it.notifyItemChanged(it.getItemPos(bangumi))
            }
        }
    }
}