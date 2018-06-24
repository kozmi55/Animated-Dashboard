package com.tamaskozmer.animateddashboard

import android.os.Handler
import android.os.SystemClock
import android.view.View

/**
 * Created by Tamas_Kozmer on 10/24/2017.
 */
class AnimationHelper(
        private val view: View,
        private val duration: Long,
        private val startValue: Float,
        private val endValue: Float) {

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
    private val increment = (endValue - startValue) / duration

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