package com.example.arch_practices.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.arch_practices.model.Coin

@Database(entities = [Coin::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
}