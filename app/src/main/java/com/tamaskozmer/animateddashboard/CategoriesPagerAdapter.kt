package com.tamaskozmer.animateddashboard

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.tobishiba.circularviewpager.library.BaseCircularViewPagerAdapter

class CategoriesPagerAdapter(fm: FragmentManager, categories: List<Category>) : BaseCircularViewPagerAdapter<Category>(fm, categories) {

    override fun getFragmentForItem(item: Category): Fragment {
        return ListFragment.newInstance(item)
    }
}