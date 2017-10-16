package com.tamaskozmer.animateddashboard

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by Tamas_Kozmer on 10/13/2017.
 */
class CategoriesPagerAdapter(fm: FragmentManager, private val categories: List<Category>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return ListFragment.newInstance(categories[position])
    }

    override fun getCount(): Int {
        return categories.size
    }
}