package me.kyuubiran.bangumi.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import me.kyuubiran.bangumi.databinding.ColorPickerRgbBinding

class ColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val binding: ColorPickerRgbBinding by lazy { ColorPickerRgbBinding.inflate(LayoutInflater.from(context), this, true) }

    var r: Int = 0
    var g: Int = 0
    var b: Int = 0

    fun setColor(color: Int) {
        r = color shr 16 and 0xFF
        g = color shr 8 and 0xFF
        b = color and 0xFF

        notifyColorChanged()
    }

    fun setColor(r: Int, g: Int, b: Int) {
        this.r = r
        this.g = g
        this.b = b

        notifyColorChanged()
    }

    fun getColor(): Int {
        return Color.rgb(r, g, b)
    }


    private fun notifyColorChanged() {
        r = r.coerceIn(0, 255)
        g = g.coerceIn(0, 255)
        b = b.coerceIn(0, 255)
//
//        binding.colorPickerEtR.setText(r.toString())
//        binding.colorPickerEtG.setText(g.toString())
//        binding.colorPickerEtB.setText(b.toString())

        binding.colorPickerSliderR.value = r.toFloat()
        binding.colorPickerSliderG.value = g.toFloat()
        binding.colorPickerSliderB.value = b.toFloat()

//        Log.d("ColorPickerView", "Color Changed: ${getColor().colorStr()}")

        binding.colorPickerColorPreview.setCircleColor(getColor())
    }


    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        binding.colorPickerSliderR.addOnChangeListener { _, value, _ ->
            r = value.toInt()
            notifyColorChanged()
        }
        binding.colorPickerSliderG.addOnChangeListener { _, value, _ ->
            g = value.toInt()
            notifyColorChanged()
        }
        binding.colorPickerSliderB.addOnChangeListener { _, value, _ ->
            b = value.toInt()
            notifyColorChanged()
        }
//
//        binding.colorPickerEtR.addTextChangedListener {
//            r = if (it.isNullOrBlank()) {
//                0
//            } else {
//                it.toString().toIntOrNull() ?: 0
//            }
//        }
//
//        binding.colorPickerEtG.addTextChangedListener {
//            g = if (it.isNullOrBlank()) {
//                0
//            } else {
//                it.toString().toIntOrNull() ?: 0
//            }
//        }
//
//        binding.colorPickerEtB.addTextChangedListener {
//            b = if (it.isNullOrBlank()) {
//                0
//            } else {
//                it.toString().toIntOrNull() ?: 0
//            }
//        }
    }

}