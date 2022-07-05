package com.example.arch_practices.utils

import com.example.arch_practices.model.Coin
import com.example.arch_practices.model.History
import com.example.arch_practices.model.IntervalType
import com.example.arch_practices.model.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("assets")
    suspend fun getCoins(@Query("offset") offset: Int): Response<Coin>

    @GET("assets/{id}/history")
    suspend fun getHistoryById(
        @Path("id") coinId: String,
        @Query("interval") interval: IntervalType,
        @Query("start") start: Long,
        @Query("end") end: Long
    ): Response<History>
}