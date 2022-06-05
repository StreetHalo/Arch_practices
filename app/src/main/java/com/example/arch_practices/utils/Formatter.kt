package com.example.arch_practices.utils

import com.example.arch_practices.R

object Formatter {
    fun getColorByChangePercent(percent: Long) =
        when{
            percent > 0 -> R.color.positive_change_percent
            percent < 0 -> R.color.negative_change_percent
            else -> R.color.non_change_percent
        }

}