package com.tamaskozmer.animateddashboard

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

private const val INITIAL_ANIMATION_DURATION = 750L
private const val SELECT_ANIMATION_DURATION = 300L

private const val BORDER_SIZE = 20F
private const val CENTER_RADIUS = 150F

private const val TAG = "PieChartView"

private const val MAX_ANGLE = 360

class PieChartView : View {

    private val data = mutableMapOf<String, Double>()
    private val pieSlices = mutableMapOf<String, PieSlice>()

    private val backgroundColor = ContextCompat.getColor(context, R.color.outerBackground)
    private val initialAnimationHelper: AnimationHelper = AnimationHelper(this, INITIAL_ANIMATION_DURATION, 0F, 360F)

    private var dataValueSum = 0.0

    private var chartRotation = 0F
    private lateinit var rect: RectF
    private lateinit var slicesRect: RectF

    private lateinit var center: Point
    private lateinit var piePaint: Paint
    private lateinit var backgroundPaint: Paint

    private lateinit var separatorPaint: Paint

    private var rotationAnimationHelper: AnimationHelper? = null

    private var selectedSlice: PieSlice? = null

    var sliceClickedListener: ((String) -> Unit)? = null
    var sliceDeselectedListener: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        piePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        piePaint.style = Paint.Style.FILL

        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = ContextCompat.getColor(context, R.color.innerBackground)

        separatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        separatorPaint.style = Paint.Style.STROKE
        separatorPaint.color = ContextCompat.getColor(context, R.color.innerBackground)
        separatorPaint.strokeWidth = BORDER_SIZE
    }

    fun addDataEntry(key: String, value: Double, color: Int) {
        data[key] = value
        dataValueSum += value

        pieSlices[key] = PieSlice(color)
        calculateAngles()
    }

    fun removeDataEntry(key: String) {
        val value = data.remove(key)
        value?.let {
            dataValueSum -= value
        }

        pieSlices.remove(key)
        calculateAngles()
    }

    private fun calculateAngles() {
        var startAngle = 0F

        for ((key, value) in data) {
            val sweepAngle = (value / dataValueSum * MAX_ANGLE).toFloat()

            pieSlices[key]?.startAngle = startAngle
            pieSlices[key]?.sweepAngle = sweepAngle

            startAngle += sweepAngle
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        rect = RectF(0F, 0F, width.toFloat(), height.toFloat())
        slicesRect = RectF(BORDER_SIZE, BORDER_SIZE, width - BORDER_SIZE, height - BORDER_SIZE)
        center = Point(width / 2, height / 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (initialAnimationHelper.state == AnimationHelper.State.NOT_STARTED) {
            initialAnimationHelper.startAnimation()
        }

        canvas.drawColor(backgroundColor)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), backgroundPaint)

        rotationAnimationHelper?.handleAnimation()
        initialAnimationHelper.handleAnimation()

        drawSlices(canvas)
        drawCenterSpace(canvas)
        drawSeparators(canvas)
    }

    private fun drawSlices(canvas: Canvas) {
        for ((_, value) in pieSlices) {
            var alpha = 100
            if (selectedSlice == null || value == selectedSlice) {
                alpha = 255
            }

            val color = value.color
            piePaint.color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))

            val animatedRotation = rotationAnimationHelper?.animatedValue ?: 0F

            val startAngle = (value.startAngle + animatedRotation) % MAX_ANGLE
            var sweepAngle = value.sweepAngle;

            if (initialAnimationHelper.state != AnimationHelper.State.FINISHED) {
                sweepAngle = if (initialAnimationHelper.animatedValue < startAngle) {
                    0F
                } else {
                    minOf(initialAnimationHelper.animatedValue - startAngle, value.sweepAngle)
                }
            }

            canvas.drawArc(slicesRect, startAngle, sweepAngle, true, piePaint)
        }
    }

    private fun drawCenterSpace(canvas: Canvas) {
        canvas.drawCircle(rect.width() / 2, rect.height() / 2, CENTER_RADIUS, backgroundPaint)
    }

    private fun drawSeparators(canvas: Canvas) {
        val animatedRotation = rotationAnimationHelper?.animatedValue ?: 0F

        for ((_, value) in pieSlices) {
            val startAngle = (value.startAngle + animatedRotation) % MAX_ANGLE
            canvas.drawArc(slicesRect, startAngle, value.sweepAngle, true, separatorPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "x: ${event.x}, y: ${event.y}")

        if (initialAnimationHelper.state == AnimationHelper.State.FINISHED) {
            for ((key, slice) in pieSlices) {
                if (sliceContainsPoint(slice, event.x, event.y)) {
                    pieSliceClicked(key, slice)
                    break
                }
            }
        }

        return super.onTouchEvent(event)
    }

    private fun sliceContainsPoint(slice: PieSlice, x: Float, y: Float): Boolean {
        val realX = x - center.x
        val realY = y - center.y

        val radius = Math.sqrt((realX * realX + realY * realY).toDouble())

        if (radius < CENTER_RADIUS || radius > rect.width() / 2) {
            return false
        }

        val angle = Math.toDegrees(Math.atan2(realY.toDouble(), realX.toDouble()))

        val realAngle = if (angle >= 0) angle else MAX_ANGLE + angle

        val startAngle = slice.startAngle + chartRotation
        val endAngle = startAngle + slice.sweepAngle

        if (endAngle > MAX_ANGLE) {
            if (realAngle > startAngle || realAngle < endAngle - MAX_ANGLE) {
                return true
            }
        } else {
            if (realAngle > startAngle && realAngle < endAngle) {
                return true
            }
        }

        return false
    }

    private fun pieSliceClicked(name: String, slice: PieSlice) {
        Log.d(TAG, "slice: $name")

        if (selectedSlice != slice) {
            selectedSlice = slice

            sliceClickedListener?.invoke(name)

            calculateChartRotation(slice)
        }
    }

    private fun calculateChartRotation(selectedSlice: PieSlice) {
        val rotatingSliceOffset = 90 - selectedSlice.startAngle

        val newStartAngle = selectedSlice.startAngle + rotatingSliceOffset - selectedSlice.sweepAngle / 2
        val startRotation = chartRotation;
        chartRotation = newStartAngle - selectedSlice.startAngle

        if (chartRotation < 0) {
            chartRotation += MAX_ANGLE
        }

        rotationAnimationHelper = AnimationHelper(this, SELECT_ANIMATION_DURATION, startRotation, chartRotation, shouldAnimateBackwards(startRotation, chartRotation))
        rotationAnimationHelper?.startAnimation()
    }

    fun selectSlice(key: String) {
        val slice = pieSlices[key]
        if (slice != null && slice != selectedSlice) {
            selectedSlice = slice
            calculateChartRotation(slice)
        }
    }

    fun deselectSlice() {
        if (selectedSlice == null) {
            return
        }

        selectedSlice = null
        sliceDeselectedListener?.invoke()

        val startRotation = chartRotation;
        chartRotation = 0F

        rotationAnimationHelper = AnimationHelper(this, SELECT_ANIMATION_DURATION, startRotation, chartRotation, shouldAnimateBackwards(startRotation, chartRotation))
        rotationAnimationHelper?.startAnimation()
    }

    private fun shouldAnimateBackwards(startRotation: Float, endRotation: Float): Boolean {
        val forwardDistance = if (endRotation - startRotation < 0) {
            endRotation - startRotation + MAX_ANGLE
        } else {
            endRotation - startRotation
        }

        val backwardsDistance = if (startRotation - chartRotation < 0) {
            startRotation - endRotation + MAX_ANGLE
        } else {
            startRotation - endRotation
        }

        return backwardsDistance < forwardDistance
    }

    fun isSliceSelected(): Boolean {
        return chartRotation != 0F
    }
}
