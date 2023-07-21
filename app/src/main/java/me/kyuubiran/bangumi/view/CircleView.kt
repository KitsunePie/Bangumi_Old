package me.kyuubiran.bangumi.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint()
    private var circleColor: Int = Color.BLACK

    init {
        paint.isAntiAlias = true
        paint.color = circleColor
    }

    fun setCircleColor(color: Int) {
        circleColor = color
        paint.color = circleColor
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val radius = width / 2f
        canvas.drawCircle(radius, radius, radius, paint)
    }
}