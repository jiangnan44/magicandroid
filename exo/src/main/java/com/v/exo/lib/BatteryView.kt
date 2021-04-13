package com.v.exo.lib

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * Author:v
 * Time:2021/4/13
 * all the code are just demo,manager your own ui
 */
class BatteryView : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        paintFrame = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeWidth = dp2px(1f)
        }
        paintInner = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }

    private lateinit var paintFrame: Paint
    private lateinit var paintInner: Paint

    private val batteryHeadWidth = dp2px(2f)
    private val batteryHeadHeight = dp2px(5f)
    private val batteryInsideMargin = dp2px(1f)
    private val batteryWidth = dp2px(20f)
    private val batteryHeight = dp2px(10f)

    private var batteryLeft = 0f
    private var batteryTop = 0f


    private var mPower = 100

    fun updatePower(power: Int) {
        mPower = when {
            power < 0 -> 0
            power > 100 -> 100
            else -> power
        }
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return


        canvas.drawRect(batteryLeft, batteryTop, batteryWidth, batteryHeight, paintFrame)


        val percent = mPower / 100f
        if (percent > 0) {
            val pLeft = batteryLeft + batteryInsideMargin
            val pTop = batteryTop + batteryInsideMargin
            val pRight = pLeft + (batteryWidth - batteryInsideMargin * 2) * percent
            val pBottom = pTop + batteryHeight - batteryInsideMargin * 2
            canvas.drawRect(pLeft, pTop, pRight, pBottom, paintInner)
        }

        val hLeft = batteryLeft + batteryWidth
        val hTop = batteryTop + (batteryHeight - batteryHeadHeight) / 2f
        val hRight = hLeft + batteryHeadWidth
        val hBottom = hTop + batteryHeadHeight

        canvas.drawRect(hLeft, hTop, hRight, hBottom, paintInner)
    }


    private fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

}