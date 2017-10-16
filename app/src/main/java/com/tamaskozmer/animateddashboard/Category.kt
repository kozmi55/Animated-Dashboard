package com.tamaskozmer.animateddashboard

import java.io.Serializable

/**
 * Created by Tamas_Kozmer on 10/13/2017.
 */
data class Category(val name: String, val items: List<ExpenseItem>, val color: Int) : Serializable