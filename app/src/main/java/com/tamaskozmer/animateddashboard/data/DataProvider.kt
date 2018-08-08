package com.tamaskozmer.animateddashboard.data

import android.content.Context
import android.support.v4.content.ContextCompat
import com.tamaskozmer.animateddashboard.R

class DataProvider(private val context: Context) {

    fun getData(): List<Category> {
        val housing = Category("Housing", listOf(
                ExpenseItem("Rent", 400),
                ExpenseItem("Utilities", 100)), ContextCompat.getColor(context, R.color.housing))

        val food = Category("Food", listOf(
                ExpenseItem("Groceries", 150),
                ExpenseItem("Lunch", 15),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Dinner", 10),
                ExpenseItem("Groceries", 30)), ContextCompat.getColor(context, R.color.food))

        val entertainment = Category("Entertainment", listOf(
                ExpenseItem("Cinema", 30),
                ExpenseItem("Night out", 100),
                ExpenseItem("Gokart", 80),
                ExpenseItem("Concert", 50)), ContextCompat.getColor(context, R.color.entertainment))

        val sport = Category("Sport", listOf(
                ExpenseItem("Gym subscription", 100),
                ExpenseItem("Supplements", 150)), ContextCompat.getColor(context, R.color.sport))

        return listOf(housing, food, entertainment, sport)
    }
}