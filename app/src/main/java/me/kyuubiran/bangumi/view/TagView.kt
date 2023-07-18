package me.kyuubiran.bangumi.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.databinding.TagViewBinding
import me.kyuubiran.bangumi.utils.ColorUtils
import me.kyuubiran.bangumi.utils.Colors

class TagView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val binding by lazy { TagViewBinding.inflate(LayoutInflater.from(context), this, true) }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagView)

        val backgroundColor = typedArray.getColor(R.styleable.TagView_backgroundColor, Colors.ORANGE)

        setBackgroundColor(backgroundColor)

        val darkerColor = ColorUtils.getDarkerColor(backgroundColor)
        binding.tagViewText.setTextColor(darkerColor)

        typedArray.recycle()
    }


}