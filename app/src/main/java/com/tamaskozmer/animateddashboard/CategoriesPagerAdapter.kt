package com.tamaskozmer.animateddashboard

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import com.tamaskozmer.animateddashboard.data.Category
import com.tobishiba.circularviewpager.library.BaseCircularViewPagerAdapter

class CategoriesPagerAdapter(fm: FragmentManager, private val categories: List<Category>) : BaseCircularViewPagerAdapter<Category>(fm, categories) {

    private val pageReferenceMap = mutableMapOf<Int, ListFragment>()

    override fun getFragmentForItem(item: Category): Fragment {
        val fragment = ListFragment.newInstance(item)
        pageReferenceMap[categories.indexOf(item) + 1] = fragment
        return fragment
    }

    override fun destroyItem(container: ViewGroup?, position: Int, fragment: Any?) {
        super.destroyItem(container, position, fragment)
        pageReferenceMap.remove(position)
    }

    fun getFragmentAtPosition(position: Int): ListFragment? {
        return pageReferenceMap[position]
    }
}