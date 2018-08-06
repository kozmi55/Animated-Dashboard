package com.tamaskozmer.animateddashboard

import java.io.Serializable

data class Category(val name: String, val items: List<ExpenseItem>, val color: Int) : Serializable