package com.example.arch_practices.utils

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.arch_practices.model.Coin
@Dao
interface CoinDao {
    @Query("SELECT * FROM coin")
    fun getAllFavCoins(): List<Coin>

    @Query("SELECT * FROM coin WHERE name = :name")
    fun getCoinByName(name: String): Coin

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToFav(coin: Coin)

    @Update
    fun update(coin: Coin)

    @Delete
    fun remove(coin: Coin)
}