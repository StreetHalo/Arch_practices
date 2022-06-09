package com.example.arch_practices.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.Room
import com.example.arch_practices.App
import com.example.arch_practices.utils.Api
import com.example.arch_practices.utils.AppDatabase
import com.example.arch_practices.utils.CoinDao
import com.example.arch_practices.utils.PagingCoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoinsViewModel(): ViewModel() {
    lateinit var db: AppDatabase
    lateinit var userDao: CoinDao
    private val coins: Flow<PagingData<Coin>> = Pager(PagingConfig(pageSize = 100)){
        PagingCoin()
    }.flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                db = Room.databaseBuilder(
                    App.instance,
                    AppDatabase::class.java, "database"
                ).build()
                userDao = db.coinDao()
            }
            updateFavList()
        }
    }
    private val favCoins = MutableLiveData<List<Coin>>()

    fun getCoins() = coins

    fun getFavCoins() = favCoins

    fun addCoinToFav(coin: Coin) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.addToFav(coin)

            withContext(Dispatchers.Main){
                updateFavList()
            }
        }
    }

    private fun updateFavList(){
        viewModelScope.launch(Dispatchers.IO) {
            val list = userDao.getAllFavCoins()
            withContext(Dispatchers.Main){
                favCoins.value = list
            }
        }
    }
}