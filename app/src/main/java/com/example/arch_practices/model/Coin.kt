package com.example.arch_practices.model

data class Coin(
    val changePercent24Hr: Double,
    val name: String,
    val priceUsd: Double,
    val symbol: String,
)