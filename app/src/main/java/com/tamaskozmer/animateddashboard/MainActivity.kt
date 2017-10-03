package com.tamaskozmer.animateddashboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pieChartView.addDataEntry("Nougat", 15.0)
        pieChartView.addDataEntry("Marshmallow", 33.0)
        pieChartView.addDataEntry("Lollipop", 29.0)
        pieChartView.addDataEntry("Kitkat", 15.0)
        pieChartView.addDataEntry("Other", 8.0)
    }

    override fun onBackPressed() {
        if (pieChartView.isSliceSelected()) {
            pieChartView.deselectSlice()
        } else {
            super.onBackPressed()
        }
    }
}
