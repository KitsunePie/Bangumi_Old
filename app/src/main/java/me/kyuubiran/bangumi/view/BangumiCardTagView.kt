package me.kyuubiran.bangumi.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.updatePadding
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.data.BangumiTag
import me.kyuubiran.bangumi.databinding.TagViewBinding
import me.kyuubiran.bangumi.utils.Colors
import me.kyuubiran.bangumi.utils.toDpInt

/**
 * For CardView
 */
class BangumiCardTagView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val binding by lazy { TagViewBinding.inflate(LayoutInflater.from(context), this, true) }

    init {
        updatePadding(4.toDpInt(context))

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BangumiCardTagView)

        val backgroundColor = typedArray.getColor(R.styleable.BangumiCardTagView_backgroundColor, Colors.ORANGE)
        binding.tagViewText.setBackgroundColor(backgroundColor)

        typedArray.recycle()
    }

    private lateinit var bangumiTag: BangumiTag

    fun setBangumiTag(tag: BangumiTag) {
        this.bangumiTag = tag
        binding.tagViewText.text = tag.name
        binding.tagViewText.setBackgroundColor(tag.color)
    }

}