package com.tamaskozmer.animateddashboard

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import com.tamaskozmer.animateddashboard.bottomsheet.BottomSheetUtils
import com.tamaskozmer.animateddashboard.bottomsheet.ViewPagerBottomSheetBehavior
import com.tamaskozmer.animateddashboard.data.Category
import com.tamaskozmer.animateddashboard.data.DataProvider
import com.tobishiba.circularviewpager.library.CircularViewPagerHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*

private const val ANIMATION_DURATION = 300L

class MainActivity : AppCompatActivity() {

    private val dataProvider = DataProvider(this)

    private lateinit var bottomSheetBehavior: ViewPagerBottomSheetBehavior<LinearLayout>
    private lateinit var data: List<Category>
    private lateinit var adapter: CategoriesPagerAdapter

    private var sliceSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
        initViewPager()
        initBottomSheet()
        initPieChartListeners()
    }

    private fun initData() {
        data = dataProvider.getData()

        for ((name, items, color) in data.reversed()) {
            pieChartView.addDataEntry(name, items.sumBy { it.price }.toDouble(), color)
        }
    }

    private fun initViewPager() {
        adapter = CategoriesPagerAdapter(supportFragmentManager, data)
        viewPager.adapter = adapter

        val circularViewPagerHandler = CircularViewPagerHandler(viewPager)
        viewPager.addOnPageChangeListener(circularViewPagerHandler)

        circularViewPagerHandler.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // TODO Smooth animation when scrolling
            }

            override fun onPageSelected(position: Int) {
                when {
                    position >= 0 -> sliceSelected(position % data.size)
                    position == -1 -> sliceSelected(data.size - 1)
                }
            }
        })
    }

    private fun sliceSelected(index: Int) {
        val name = data[index].name
        pieChartView.selectSlice(name)

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            adapter.getFragmentAtPosition(viewPager.currentItem)?.scrollToTop()
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = ViewPagerBottomSheetBehavior.from(bottom_sheet)
        BottomSheetUtils.setupViewPager(viewPager)

        bottomSheetBehavior.state = ViewPagerBottomSheetBehavior.STATE_HIDDEN


        bottomSheetBehavior.setBottomSheetCallback(object : ViewPagerBottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                handleSlideOffset(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    pieChartView.deselectSlice()
                }
            }
        })
    }

    private fun handleSlideOffset(slideOffset: Float) {
        if (slideOffset > 0) {
            pieChartView.alpha = 1 - slideOffset * 2
            val scale = (0.5 - slideOffset).toFloat()
            pieChartView.scaleX = scale
            pieChartView.scaleY = scale
        }
    }


    private fun initPieChartListeners() {
        pieChartView.sliceClickedListener = { key ->
            selectViewPagerPage(key)

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

    private fun selectViewPagerPage(key: String) {
        val position = data.indexOfFirst { it.name == key }
        viewPager.setCurrentItem(position + 1, true)
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
        bottomSheetBehavior.state = ViewPagerBottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = ViewPagerBottomSheetBehavior.STATE_HIDDEN
    }

    private fun createScaleAnimation(start: Float, end: Float): AnimatorSet {
        val scaleX = ObjectAnimator.ofFloat(pieChartView, "scaleX", start, end)
                .setDuration(ANIMATION_DURATION)

        val scaleY = ObjectAnimator.ofFloat(pieChartView, "scaleY", start, end)
                .setDuration(ANIMATION_DURATION)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        return animatorSet
    }

    private fun createTranslateAnimation(start: Float, end: Float): Animator {
        return ObjectAnimator.ofFloat(pieChartView, "translationY", start, end)
                .setDuration(ANIMATION_DURATION)
    }

    override fun onBackPressed() {
        when {
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> {
                adapter.getFragmentAtPosition(viewPager.currentItem)?.scrollToTop()
                bottomSheetBehavior.state = ViewPagerBottomSheetBehavior.STATE_COLLAPSED
            }
            pieChartView.isSliceSelected() -> pieChartView.deselectSlice()
            else -> super.onBackPressed()
        }
    }
}
