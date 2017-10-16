package com.tamaskozmer.animateddashboard

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.expense_item.view.*

/**
 * Created by Tamas_Kozmer on 10/13/2017.
 */
class ExpensesAdapter(private val expenses: List<ExpenseItem>) : RecyclerView.Adapter<ExpensesAdapter.ExpensesViewHolder>() {

    override fun getItemCount() = expenses.size

    override fun onBindViewHolder(holder: ExpensesViewHolder, position: Int) = holder.bind(expenses[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExpensesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false))

    class ExpensesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(expenseItem: ExpenseItem) = with(itemView) {
            title.text = expenseItem.title
            price.text = expenseItem.price.toString()
        }
    }

}