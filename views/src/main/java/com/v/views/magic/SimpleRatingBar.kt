package com.v.views.magic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.v.views.R
import kotlin.math.abs

/**
 * Author:v
 * Time:2021/3/16
 */
class SimpleRatingBar : View {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context!!, attrs)
    }

    private var bgStarBitmap: Bitmap? = null
    private var halfStarBitmap: Bitmap? = null
    private var selectedStarBitmap: Bitmap? = null

    private var totalStarCount = 5//default star size is 5
    private var selectedStarCount = 0f
    var isStepHalf = false//whether step half
    var isSelectEnable = false

    private var starSize = 0
    private var starPadding = 0

    private fun initView(context: Context, attrs: AttributeSet?) {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.SimpleRatingBar)
        totalStarCount = ta.getInt(R.styleable.SimpleRatingBar_totalStarCount, 5)
        selectedStarCount = ta.getFloat(R.styleable.SimpleRatingBar_selectedStarCount, 0f)
        isStepHalf = ta.getBoolean(R.styleable.SimpleRatingBar_isStepHalf, isStepHalf)
        isSelectEnable = ta.getBoolean(R.styleable.SimpleRatingBar_isSelectable, true)
        starSize = ta.getDimension(R.styleable.SimpleRatingBar_starSize, dp2px(30f)).toInt()
        starPadding = ta.getDimension(R.styleable.SimpleRatingBar_starPadding, dp2px(10f)).toInt()

        val bgStarResId = ta.getResourceId(R.styleable.SimpleRatingBar_backgroundStar, 0)
        setBackgroundStarDrawable(bgStarResId)
        val selectedStarResId = ta.getResourceId(R.styleable.SimpleRatingBar_selectedStar, 0)
        setSelectedStarDrawable(selectedStarResId)
        val halfStarResId = ta.getResourceId(R.styleable.SimpleRatingBar_halfStar, 0)
        setHalfStarDrawable(halfStarResId)
        ta.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val height = if (MeasureSpec.EXACTLY == heightMode) {
            heightSize
        } else {
            val minHeight = paddingTop + paddingBottom + starSize
            heightSize.coerceAtMost(minHeight)
        }


        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val width = if (MeasureSpec.EXACTLY == widthMode) {
            widthSize//match_parent or accurate dp
        } else {//warp_count
            val minWidth = (paddingLeft + paddingRight +
                    (starSize + starPadding) * totalStarCount - starPadding).toInt()
            minWidth.coerceAtMost(widthSize)
        }
        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas) {
        val paddingL = paddingLeft
        val top = paddingTop.toFloat()
        var left = 0f


        val selectCount = selectedStarCount.toInt()
        val hasHalf = selectedStarCount - selectCount > 0//has half



        for (i in 1..selectCount) {
            left = paddingL + (i - 1) * (starPadding + starSize).toFloat()
            canvas.drawBitmap(selectedStarBitmap!!, left, top, null)
        }

        if (selectCount >= totalStarCount) return
        left = paddingL + (selectCount) * (starPadding + starSize).toFloat()
        if (hasHalf) {
            canvas.drawBitmap(halfStarBitmap!!, left, top, null)
        } else {
            canvas.drawBitmap(bgStarBitmap!!, left, top, null)
        }

        for (i in (selectCount + 2)..totalStarCount) {
            left = paddingL + (i - 1) * (starPadding + starSize).toFloat()
            canvas.drawBitmap(bgStarBitmap!!, left, top, null)
        }

    }


    private var startX = -1f
    private var startY = -1f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isSelectEnable || event == null) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (startX == -1f) {
                    startX = event.x
                }
                if (startY == -1f) {
                    startY = event.y
                }
            }
            MotionEvent.ACTION_UP -> {
                val x = event.x
                val y = event.y
                val area = starSize / 2
                //if your star is a little bit small,you may change the area
                if ((abs(startX - x) < area) && (abs(startY - y) < area)) {
                    selectStar(x)
                }

                startX = -1f
                startY = -1f
            }
        }


        return true
    }

    private fun selectStar(x: Float) {
        var position = x / (starSize + starPadding)
        if (position < 0) position = 0f
        if (position > totalStarCount) position = totalStarCount.toFloat()

        val count = position.toInt()
        val f = position % 1 * (starSize + starPadding)
        if (f != 0f) {
            position = if (isStepHalf) {
                val halfSize = starSize / 2
                if (f <= halfSize) {
                    count + 0.5f
                } else {
                    count + 1f
                }
            } else {
                count + 1f
            }
        }

        if (position != selectedStarCount) {
            selectedStarCount = position
            invalidate()
            ratingChangeListener?.onRatingChanged(this, selectedStarCount)
        }

    }


    fun setBackgroundStarDrawable(@DrawableRes resId: Int) {
//        if (resId==currentResId) no need
        bgStarBitmap = getBitmap(resId)
    }

    fun setHalfStarDrawable(@DrawableRes resId: Int) {
        if (resId == 0) {
            return
        }
//        if (resId==currentResId) no need
        halfStarBitmap = getBitmap(resId)
    }

    fun setSelectedStarDrawable(@DrawableRes resId: Int) {
//        if (resId==currentResId) no need
        selectedStarBitmap = getBitmap(resId)
    }

    /**
     *NOTE! this won't invalidate ui
     */
    fun setTotalStars(count: Int) {
        totalStarCount = count
    }


    fun setSelectStars(count: Float) {
        selectedStarCount = count
        invalidate()
    }

    fun getSelectStars(): Float {
        return selectedStarCount
    }

    private fun getBitmap(resId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, resId)
            ?: throw IllegalArgumentException("Wrong Drawable resource Id:$resId")

        val bm = Bitmap.createBitmap(
            starSize,
            starSize,
            Bitmap.Config.ARGB_8888
        )
        Canvas(bm).apply {
            drawable.setBounds(0, 0, starSize, starSize)
            drawable.draw(this)
        }


        return bm
    }

    private var ratingChangeListener: OnRatingChangeListener? = null
    fun setOnRatingChangeListener(action: (SimpleRatingBar, Float) -> Unit) {
        this.ratingChangeListener = object : OnRatingChangeListener {
            override fun onRatingChanged(ratingBar: SimpleRatingBar, rating: Float) {
                action(ratingBar, rating)
            }
        }
    }

    fun setOnRatingChangeListener(listener: OnRatingChangeListener) {
        this.ratingChangeListener = listener
    }

    interface OnRatingChangeListener {
        fun onRatingChanged(ratingBar: SimpleRatingBar, rating: Float)
    }


    private fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bgStarBitmap?.recycle()
        selectedStarBitmap?.recycle()
        halfStarBitmap?.recycle()
    }

}