package com.example.arch_practices.model

import com.example.arch_practices.R

sealed class BottomNavPage(val titleId: Int, val iconId: Int, val screenRoute: String){
    object Feed: BottomNavPage(R.string.feed_page_title,  R.drawable.ic_baseline_dns_24, "feed")
    object Portfolio: BottomNavPage(R.string.portfolio_page_title, R.drawable.ic_baseline_work_24, "user_portfolio")
}
