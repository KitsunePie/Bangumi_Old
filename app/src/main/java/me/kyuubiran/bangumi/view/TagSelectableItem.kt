package me.kyuubiran.bangumi.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import me.kyuubiran.bangumi.data.BangumiTag
import me.kyuubiran.bangumi.databinding.TagSelectableItemBinding
import me.kyuubiran.bangumi.utils.toDpInt

class TagSelectableItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val binding by lazy { TagSelectableItemBinding.inflate(LayoutInflater.from(context), this, true) }

    private var _bangumiTag: BangumiTag? = null

    var bangumiTag: BangumiTag
        set(value) {
            _bangumiTag = value

            binding.tagSelectableItemColor.setCircleColor(value.color)
            binding.tagSelectableItemNameText.text = value.name
        }
        get() = _bangumiTag!!

    var isChecked: Boolean
        get() = binding.tagSelectableItemCheckbox.isChecked
        set(value) {
            binding.tagSelectableItemCheckbox.isChecked = value
        }


    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        minimumHeight = 48.toDpInt(context)
        setOnClickListener { isChecked = !isChecked }
    }

}