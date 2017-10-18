package com.tamaskozmer.animateddashboard

import android.graphics.Color

/**
 * Created by Tamas_Kozmer on 10/13/2017.
 */
class DataProvider {

    fun getData(): List<Category> {
        val housing = Category("Housing", listOf(
                ExpenseItem("Rent", 60000),
                ExpenseItem("Utilities", 10000)), Color.RED)

        val food = Category("Food", listOf(
                ExpenseItem("Groceries", 15000),
                ExpenseItem("Lunch", 1500),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Dinner", 1000),
                ExpenseItem("Groceries", 3000)), Color.BLUE)

        val entertainment = Category("Entertainment", listOf(
                ExpenseItem("Cinema", 3000),
                ExpenseItem("Drinking", 10000),
                ExpenseItem("Gokart", 8000),
                ExpenseItem("Concert", 5000)), Color.parseColor("#FFAB07B4"))

        val sport = Category("Sport", listOf(
                ExpenseItem("Gym subscription", 10000),
                ExpenseItem("Supplements", 15000)), Color.parseColor("#FF0BB45D"))

        return listOf(housing, food, entertainment, sport)
    }
}