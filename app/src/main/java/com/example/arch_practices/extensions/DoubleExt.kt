package com.example.arch_practices.extensions

import android.util.Log

fun Double?.formatNumbersAfterDot(number: Int = 2): String {
    val num = String.format("%.${number}f", this).replace(",", ".")
    Log.d("ddd","num $num")

    return num
}
