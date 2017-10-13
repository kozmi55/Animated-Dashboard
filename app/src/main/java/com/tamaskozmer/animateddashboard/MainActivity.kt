package com.tamaskozmer.animateddashboard

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var sliceSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        pieChartView.addDataEntry("Nougat", 15.0)
        pieChartView.addDataEntry("Marshmallow", 33.0)
        pieChartView.addDataEntry("Lollipop", 29.0)
        pieChartView.addDataEntry("Kitkat", 15.0)
        pieChartView.addDataEntry("Other", 8.0)

        pieChartView.sliceSelectedListener = { key ->
            Toast.makeText(this, key, Toast.LENGTH_SHORT).show()
            if (!sliceSelected) {
                animateSelect()
            }
            sliceSelected = true
        }

        pieChartView.sliceDeselectedListener = {
            sliceSelected = false
            hideBottomSheet()
            val scaleAnimation = createScaleAnimation(0.5f, 1.0f)
            scaleAnimation.playTogether(createTranslateAnimation(pieChartView.translationY, pieChartView.translationY + 500))
            scaleAnimation.start()
        }
    }

    private fun animateSelect() {
        val scaleAnimation = createScaleAnimation(1.0f, 0.5f)
        scaleAnimation.playTogether(createTranslateAnimation(pieChartView.translationY, pieChartView.translationY - 500))
        scaleAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                showBottomSheet()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
        scaleAnimation.start()
    }

    private fun showBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun createScaleAnimation(start: Float, end: Float): AnimatorSet {
        val scaleX = ObjectAnimator.ofFloat(pieChartView, "scaleX", start, end)
                .setDuration(300)

        val scaleY = ObjectAnimator.ofFloat(pieChartView, "scaleY", start, end)
                .setDuration(300)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        return animatorSet
    }

    private fun createTranslateAnimation(start: Float, end: Float): Animator {
        return ObjectAnimator.ofFloat(pieChartView, "translationY", start, end)
                .setDuration(300)
    }

    override fun onBackPressed() {
        if (pieChartView.isSliceSelected()) {
            pieChartView.deselectSlice()
        } else {
            super.onBackPressed()
        }
    }
}
