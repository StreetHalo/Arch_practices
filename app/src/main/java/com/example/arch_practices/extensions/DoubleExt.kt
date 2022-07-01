package com.example.arch_practices.extensions

fun Double?.formatNumbersAfterDot(number: Int = 2): String {
    return String.format("%.${number}f", this).replace(",", ".")
}
