package com.tamaskozmer.animateddashboard

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tamaskozmer.animateddashboard.data.Category
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    companion object {
        fun newInstance(category: Category): ListFragment {
            val fragment = ListFragment()
            val bundle = Bundle()
            bundle.putSerializable("category", category)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val category = arguments?.get("category") as Category?

        category?.let {
            recyclerView.setBackgroundColor(category.color)
            recyclerView.adapter = ExpensesAdapter(category)
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }
    }

    fun scrollToTop() {
        recyclerView.smoothScrollToPosition(0)
    }
}