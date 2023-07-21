package me.kyuubiran.bangumi.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.databinding.ClickableItemBinding
import me.kyuubiran.bangumi.utils.toDpInt

class ClickableItem @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attributeSet, defStyleAttr) {
    private val binding by lazy { ClickableItemBinding.inflate(LayoutInflater.from(context), this, true) }

    var titleText: CharSequence?
        get() = binding.clickableItemTitle.text
        set(value) {
            binding.clickableItemTitle.text = value
        }

    var subtitleText: CharSequence?
        get() = binding.clickableItemSubtitle.text
        set(value) {
            binding.clickableItemSubtitle.text = value
            binding.clickableItemSubtitle.visibility = if (value.isNullOrBlank()) GONE else VISIBLE
        }

    var minHeight: Int
        get() = binding.clickableItemLayout.minHeight
        set(value) {
            binding.clickableItemLayout.minHeight = value
        }

    var minWidth: Int
        get() = binding.clickableItemLayout.minWidth
        set(value) {
            binding.clickableItemLayout.minWidth = value
        }

    val rightLayout: LinearLayout
        get() = binding.clickableItemRightViewLayout

    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ClickableItem)

        val titleText = typedArray.getString(R.styleable.ClickableItem_title)
        val subtitleText = typedArray.getString(R.styleable.ClickableItem_subtitle)
        val showRightArray = typedArray.getBoolean(R.styleable.ClickableItem_showRightArray, true)

        minWidth = typedArray.getDimensionPixelSize(R.styleable.ClickableItem_minWidth, 0)
        minHeight = typedArray.getDimensionPixelSize(R.styleable.ClickableItem_minHeight, 48.toDpInt(context))

        binding.clickableItemTitle.text = titleText
        binding.clickableItemSubtitle.text = subtitleText

        if (!showRightArray) {
            binding.clickableItemRightViewLayout.removeAllViews()
        }

        binding.clickableItemSubtitle.visibility = if (subtitleText.isNullOrBlank()) GONE else VISIBLE

        typedArray.recycle()
    }

}