package com.tamaskozmer.animateddashboard

import android.content.Context
import android.support.v4.content.ContextCompat

/**
 * Created by Tamas_Kozmer on 10/13/2017.
 */
class DataProvider(val context: Context) {

    fun getData(): List<Category> {
        val housing = Category("Housing", listOf(
                ExpenseItem("Rent", 40000),
                ExpenseItem("Utilities", 10000)), ContextCompat.getColor(context, R.color.housing))

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
                ExpenseItem("Groceries", 3000)), ContextCompat.getColor(context, R.color.food))

        val entertainment = Category("Entertainment", listOf(
                ExpenseItem("Cinema", 3000),
                ExpenseItem("Drinking", 10000),
                ExpenseItem("Gokart", 8000),
                ExpenseItem("Concert", 5000)), ContextCompat.getColor(context, R.color.entertainment))

        val sport = Category("Sport", listOf(
                ExpenseItem("Gym subscription", 10000),
                ExpenseItem("Supplements", 15000)), ContextCompat.getColor(context, R.color.sport))

        return listOf(housing, food, entertainment, sport)
    }
}