package me.kyuubiran.bangumi.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.databinding.ImageTextButtonBinding

class ImageTextButton @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attributeSet, defStyleAttr) {
    private val binding by lazy {
        ImageTextButtonBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var drawable: Drawable? = null
        set(value) = binding.image.setImageDrawable(value).also { field = value }
        get() = binding.image.drawable

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ImageTextButton)

        val text = typedArray.getString(R.styleable.ImageTextButton_text)
        binding.text.text = text
        val img = typedArray.getDrawable(R.styleable.ImageTextButton_image)
        binding.image.setImageDrawable(img)

        typedArray.recycle()
    }

    var text: CharSequence?
        get() = binding.text.text
        set(value) {
            binding.text.text = value
        }

    var textSize: Float
        get() = binding.text.textSize
        set(value) {
            binding.text.textSize = value
        }

    val textColors: ColorStateList?
        get() = binding.text.textColors

    fun setTextColor(color: Int) {
        binding.text.setTextColor(color)
    }
}