package com.example.arch_practices.utils

import com.example.arch_practices.model.Coin
import com.example.arch_practices.model.History
import com.example.arch_practices.model.IntervalType
import com.example.arch_practices.model.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("assets")
    suspend fun getCoins(@Query("offset") offset: Int): Response<Coin>

    @GET("assets/{id}/history")
    suspend fun getHistoryById(@Path("id") coinId: String, @Query("interval") interval: IntervalType): Response<History>

    companion object {
        private var api: Api? = null

        fun getInstance(): Api{
            if(api == null){
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                api = Retrofit.Builder()
                    .baseUrl("https://api.coincap.io/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(
                        OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                            .build()
                    )
                    .build()
                    .create(Api::class.java)
            }
            return api!!
        }
    }
}