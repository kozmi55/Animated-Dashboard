package com.tamaskozmer.animateddashboard

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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

    private val animationDuration = 300L

    private val data = mutableMapOf<String, Double>()
    private val pieSlices = mutableMapOf<String, PieSlice>()
    private val animationHandler = Handler()
    private val animationRunnable = object : Runnable {
        override fun run() {
            invalidate();
            animationHandler.postDelayed(this, 16)
        }

    }

    private var sum = 0.0;
    private var chartRotation = 0F;

    private lateinit var rect: RectF;
    private lateinit var piePaint: Paint

    private var animStartTime = 0L
    private var animatedRotation = 0F
    private var animationIncrement = 0F

    private var animationStartRotation = 0F

    private var selectedSlice: PieSlice? = null

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
        sum += value

        pieSlices[key] = PieSlice(randomColor())
        calculateAngles()
    }

    private fun calculateAngles() {
        var startAngle = 0F

        for ((key, value) in data) {
            Log.d("asd", "$key: ${pieSlices[key]?.color}")
            val sweepAngle = calculateSweepAngle(value)

            pieSlices[key]?.startAngle = startAngle
            pieSlices[key]?.sweepAngle = sweepAngle

            startAngle += sweepAngle
        }
    }

    fun removeDataEntry(key: String) {
        val value = data.remove(key)
        value?.let {
            sum -= value
        }

        pieSlices.remove(key)
        calculateAngles()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect = RectF(0F, 0F, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRGB(255, 255, 255)

        var delta = SystemClock.uptimeMillis() - animStartTime
        if (delta >= animationDuration) {
           delta = 300;
        }

        animatedRotation = animationStartRotation + delta * animationIncrement
        if (animatedRotation == chartRotation) {
            stopAnimation()
        }

        Log.d("asd", "onDraw - delta: $delta, animated rotation: $animatedRotation, rotation: $chartRotation")

        for ((_, value) in pieSlices) {
            var alpha = 127
            if (selectedSlice == null || value == selectedSlice) {
                alpha = 255
            }
            val color = value.color
            piePaint.color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
            val startAngle = (value.startAngle + animatedRotation) % 360
            canvas.drawArc(rect, startAngle, value.sweepAngle, true, piePaint)
        }

        piePaint.color = Color.rgb(255, 255, 255)
        canvas.drawCircle(rect.width() / 2, rect.height() / 2, 50F, piePaint)
    }

    private fun calculateSweepAngle(value: Double): Float {
        return (value / sum * 360).toFloat()
    }

    private fun randomColor(): Int {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)
        return Color.rgb(r, g, b)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("asd", "x: ${event.x}, y: ${event.y}")

        // TODO Detect clicked slice properly instead of this hack
        isDrawingCacheEnabled = true
        buildDrawingCache()
        val pixel = drawingCache.getPixel(event.x.toInt(), event.y.toInt())
        isDrawingCacheEnabled = false

        // TODO Replace color hack
        for ((key, slice) in pieSlices) {
            if (pixel == slice.color) {
                pieSliceClicked(key, slice)
                break
            }
        }

        return super.onTouchEvent(event)
    }

    private fun pieSliceClicked(name: String, slice: PieSlice) {
        Log.d("asd", "slice: $name")

        selectedSlice = slice

        val rotatingSliceOffset = 90 - slice.startAngle

        val newStartAngle = slice.startAngle + rotatingSliceOffset - slice.sweepAngle / 2
        animationStartRotation = chartRotation;
        chartRotation = newStartAngle - slice.startAngle

        animationIncrement = (chartRotation - animatedRotation) / animationDuration
//        invalidate()
        startAnimation()
    }

    fun deselectSlice() {
        selectedSlice = null

        animationStartRotation = chartRotation;
        chartRotation = 0F
        animationIncrement = (chartRotation - animatedRotation) / animationDuration

//        invalidate()
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
        Log.d("asd", "Stop animation")
        animationHandler.removeCallbacks(animationRunnable)
        animatedRotation = chartRotation
        invalidate()
    }
}
