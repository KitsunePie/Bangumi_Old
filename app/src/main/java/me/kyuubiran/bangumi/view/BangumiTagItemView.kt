package me.kyuubiran.bangumi.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.adapter.FragmentTagListAdapter
import me.kyuubiran.bangumi.data.BangumiTag
import me.kyuubiran.bangumi.databinding.TagItemViewBinding
import me.kyuubiran.bangumi.fragment.BangumiModifyFragment
import me.kyuubiran.bangumi.fragment.BangumiTagModifyFragment
import me.kyuubiran.bangumi.utils.DialogUtils
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.toDpInt

/**
 * For Tag management fragment
 */
class BangumiTagItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    lateinit var adapter: FragmentTagListAdapter

    private val binding: TagItemViewBinding by lazy { TagItemViewBinding.inflate(LayoutInflater.from(context), this, true) }

    private lateinit var bangumiTag: BangumiTag

    private val nav by lazy { findNavController() }

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
            setTitle(context.getString(R.string.edit_with, bangumiTag.name))

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
            BangumiTagModifyFragment.tagInEdit = bangumiTag
            nav.navigate(R.id.action_tagListFragment_to_tagModifyFragment)
            dialog.dismiss()
        }

        delete.setOnClickListener {
            DialogUtils.showConfirmDialog(
                context,
                context.getString(R.string.delete_with, bangumiTag.name),
                context.getString(R.string.delete_with_message, bangumiTag.name)
            ) {
                BangumiTagModifyFragment.tagInEdit = null
                coLaunchIO { adapter.removeItem(bangumiTag) }
                dialog.dismiss()
            }
        }
    }

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        setOnLongClickListener {
            showEditDialog()
            true
        }
    }

    fun setBangumiTag(tag: BangumiTag) {
        this.bangumiTag = tag
        binding.tagItemViewTagName.text = tag.name
        binding.tagItemViewColor.setCircleColor(tag.color)
        binding.tagItemViewPriorityText.text = tag.priority.toString()
    }

}