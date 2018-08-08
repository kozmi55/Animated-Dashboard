package com.tamaskozmer.animateddashboard

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tamaskozmer.animateddashboard.data.Category
import com.tamaskozmer.animateddashboard.data.ExpenseItem
import kotlinx.android.synthetic.main.expense_item.view.*
import kotlinx.android.synthetic.main.header_item.view.*

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_LIST_ITEM = 1

class ExpensesAdapter(private val category: Category) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount() = category.items.size + 1 // Add 1 for the header

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ExpensesViewHolder) {
            holder.bind(category.items[position - 1])
        } else if (holder is HeaderViewHolder){
            holder.bind(category)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == VIEW_TYPE_LIST_ITEM) {
                ExpensesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false))
            } else {
                HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false))
            }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_LIST_ITEM
    }

    class ExpensesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(expenseItem: ExpenseItem) {
            with(itemView) {
                title.text = expenseItem.title
                price.text = "${expenseItem.price} $"
            }
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(category: Category) {
            itemView.category_title.text = category.name
            itemView.category_title.setTextColor(category.color)
        }
    }
}