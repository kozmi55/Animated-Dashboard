package com.tamaskozmer.animateddashboard

import android.os.Handler
import android.os.SystemClock
import android.view.View

private const val MAX_VALUE = 360F

class AnimationHelper(
        private val view: View,
        private val duration: Long,
        private val startValue: Float,
        private val endValue: Float,
        reversed: Boolean = false) {

    var animatedValue = 0F
        private set
    var state: State = State.NOT_STARTED
        private set

    private val animationHandler = Handler()
    private val animationRunnable = object : Runnable {
        override fun run() {
            view.invalidate();
            animationHandler.postDelayed(this, 16)
        }
    }

    private val increment = if (reversed) {
        val diff = if (startValue - endValue > 0) {
            startValue - endValue
        } else {
            startValue - endValue + MAX_VALUE
        }
        -diff / duration
    } else {
        val diff = if (endValue - startValue > 0) {
            endValue - startValue
        } else {
            endValue - startValue + MAX_VALUE
        }
        diff / duration
    }

    private var animationStartTime = 0L

    fun startAnimation() {
        state = State.RUNNING
        animationStartTime = SystemClock.uptimeMillis()
        animationHandler.removeCallbacks(animationRunnable)
        animationHandler.post(animationRunnable)
    }

    fun handleAnimation() {
        var delta = SystemClock.uptimeMillis() - animationStartTime
        if (delta >= duration) {
            delta = duration;
        }

        animatedValue = startValue + delta * increment
        if (animatedValue < 0) {
            animatedValue += MAX_VALUE
        }
        if (delta >= duration) {
            stopAnimation()
        }
    }

    private fun stopAnimation() {
        state = State.FINISHED
        animatedValue = endValue
        animationHandler.removeCallbacks(animationRunnable)
        view.invalidate()
    }

    enum class State {
        NOT_STARTED, RUNNING, FINISHED
    }
}