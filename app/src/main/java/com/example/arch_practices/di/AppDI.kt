package com.example.arch_practices.di

import androidx.room.Room
import com.example.arch_practices.App
import com.example.arch_practices.model.AnalyticViewModel
import com.example.arch_practices.utils.Api
import com.example.arch_practices.utils.AppDatabase
import com.example.arch_practices.viewmodels.CoinsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<Api> {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        Retrofit.Builder()
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
    single {
        val db = Room.databaseBuilder(get(), AppDatabase::class.java, "database").build()
        db.coinDao()
    }

    viewModel {
        AnalyticViewModel(get(), get())
    }

    viewModel {
        CoinsViewModel(get(), get())
    }
}