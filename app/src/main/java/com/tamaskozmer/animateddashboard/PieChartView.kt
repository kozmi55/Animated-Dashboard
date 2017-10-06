package com.tamaskozmer.animateddashboard

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.*


/**
 * Created by Tamas_Kozmer on 9/29/2017.
 */
class PieChartView : View {

    private val INITIAL_ANIMATION_DURATION = 750L
    private val INIT_ANIMATION_INCREMENT = 360F / INITIAL_ANIMATION_DURATION

    private val SELECT_ANIMATION_DURATION = 300L

    private val TAG = "PieChartView"

    private val data = mutableMapOf<String, Double>()
    private val pieSlices = mutableMapOf<String, PieSlice>()
    private val animationHandler = Handler()
    private val animationRunnable = object : Runnable {
        override fun run() {
            invalidate();
            animationHandler.postDelayed(this, 16)
        }

    }

    private var dataValueSum = 0.0;
    private var chartRotation = 0F;

    private lateinit var rect: RectF;
    private lateinit var piePaint: Paint
    private lateinit var center: Point

    private var animStartTime = 0L
    private var animatedRotation = 0F
    private var animationIncrement = 0F
    private var animationStartRotation = 0F

    private var selectedSlice: PieSlice? = null

    private var initialAnimationStartTime = 0L
    private var initialAnimationAnimatedValue = 0F
    private var initialAnimationStarted = false
    private var initialAnimationFinished = false

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
        piePaint = Paint(Paint.ANTI_ALIAS_FLAG);
        piePaint.style = Paint.Style.FILL;
        isDrawingCacheEnabled = true
    }

    fun addDataEntry(key: String, value: Double) {
        data[key] = value
        dataValueSum += value

        pieSlices[key] = PieSlice(randomColor())
        calculateAngles()
    }

    private fun randomColor(): Int {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)
        return Color.rgb(r, g, b)
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
            val sweepAngle = (value / dataValueSum * 360).toFloat()

            pieSlices[key]?.startAngle = startAngle
            pieSlices[key]?.sweepAngle = sweepAngle

            startAngle += sweepAngle
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        rect = RectF(0F, 0F, width.toFloat(), height.toFloat())
        center = Point(width / 2, height / 2)
        Log.d(TAG, "centerX: ${center.x}, centerY: ${center.y}")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!initialAnimationStarted) {
            startInitialAnimation()
        }

        canvas.drawRGB(255, 255, 255)

        handleInitAnimation()
        handleRotationAnimation()

        drawSlices(canvas)
        drawCenterSpace(canvas)
    }

    private fun startInitialAnimation() {
        initialAnimationStarted = true
        initialAnimationStartTime = SystemClock.uptimeMillis()
        animationHandler.removeCallbacks(animationRunnable)
        animationHandler.post(animationRunnable)
    }

    private fun handleInitAnimation() {
        var delta = SystemClock.uptimeMillis() - initialAnimationStartTime
        if (delta >= INITIAL_ANIMATION_DURATION) {
            delta = INITIAL_ANIMATION_DURATION;
            initialAnimationAnimatedValue = 360F
            initialAnimationFinished = true
        }

        initialAnimationAnimatedValue = (delta * INIT_ANIMATION_INCREMENT)
        if (initialAnimationAnimatedValue == 360F) {
            stopAnimation()
        }
    }

    private fun handleRotationAnimation() {
        var delta = SystemClock.uptimeMillis() - animStartTime
        if (delta >= SELECT_ANIMATION_DURATION) {
            delta = SELECT_ANIMATION_DURATION;
        }

        animatedRotation = animationStartRotation + delta * animationIncrement
        if (animatedRotation == chartRotation) {
            stopAnimation()
        }
    }

    private fun drawSlices(canvas: Canvas) {
        for ((_, value) in pieSlices) {
            var alpha = 20
            if (selectedSlice == null || value == selectedSlice) {
                alpha = 255
            }

            val color = value.color
            piePaint.color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))

            val startAngle = (value.startAngle + animatedRotation) % 360
            var sweepAngle = value.sweepAngle;

            if (!initialAnimationFinished) {
                sweepAngle = if (initialAnimationAnimatedValue < startAngle) {
                    0F
                } else {
                    minOf(initialAnimationAnimatedValue - startAngle, value.sweepAngle)
                }
            }

            canvas.drawArc(rect, startAngle, sweepAngle, true, piePaint)
        }
    }

    private fun drawCenterSpace(canvas: Canvas) {
        piePaint.color = Color.rgb(255, 255, 255)
        canvas.drawCircle(rect.width() / 2, rect.height() / 2, 150F, piePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "x: ${event.x}, y: ${event.y}")

        if (initialAnimationFinished) {
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

        if (radius < 150 || radius > rect.width() / 2) {
            return false
        }

        val angle = Math.toDegrees(Math.atan2(realY.toDouble(), realX.toDouble()))

        val realAngle = if (angle >= 0) angle else 360 + angle

        val startAngle = slice.startAngle + chartRotation
        val endAngle = startAngle + slice.sweepAngle

        if (endAngle > 360) {
            if (realAngle > startAngle || realAngle < endAngle - 360) {
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

        selectedSlice = slice

        calculateChartRotation(slice)
        animationIncrement = (chartRotation - animatedRotation) / SELECT_ANIMATION_DURATION

        startAnimation()
    }

    private fun calculateChartRotation(selectedSlice: PieSlice) {
        val rotatingSliceOffset = 90 - selectedSlice.startAngle

        val newStartAngle = selectedSlice.startAngle + rotatingSliceOffset - selectedSlice.sweepAngle / 2
        animationStartRotation = chartRotation;
        chartRotation = newStartAngle - selectedSlice.startAngle

        if (chartRotation < 0) {
            chartRotation += 360
        }
    }

    fun deselectSlice() {
        selectedSlice = null

        animationStartRotation = chartRotation;
        chartRotation = 0F
        animationIncrement = (chartRotation - animatedRotation) / SELECT_ANIMATION_DURATION

        startAnimation()
    }

    fun isSliceSelected(): Boolean {
        return chartRotation != 0F
    }

    private fun startAnimation() {
        animStartTime = SystemClock.uptimeMillis()
        animationHandler.removeCallbacks(animationRunnable)
        animationHandler.post(animationRunnable)
    }

    private fun stopAnimation() {
        animationHandler.removeCallbacks(animationRunnable)
        animatedRotation = chartRotation
        invalidate()
    }
}
