package com.example.arch_practices.model

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.room.Room
import com.example.arch_practices.App
import com.example.arch_practices.utils.AppDatabase
import com.example.arch_practices.utils.CoinDao
import com.example.arch_practices.utils.PagingCoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoinsViewModel() : ViewModel() {

    var db: AppDatabase = Room.databaseBuilder(App.instance, AppDatabase::class.java, "database").build()
    var userDao: CoinDao = db.coinDao()

    private val coins: Flow<PagingData<Coin>> = Pager(PagingConfig(pageSize = 100)) {
        PagingCoin()
    }.flow.cachedIn(viewModelScope)

    init {
        updateFavList()
    }

    private val favCoins = MutableLiveData<List<Coin>>()

    fun getCoins() = coins

    fun getFavCoins() = favCoins

    fun isAddedToFav(coin: Coin) = favCoins.value?.find { it.name == coin.name } != null

    fun addCoinToFav(coin: Coin) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.addToFav(coin)
            updateFavList()
        }
    }

    fun removeCoinFromFav(coin: Coin){
        viewModelScope.launch(Dispatchers.IO) {
            userDao.remove(coin)
            updateFavList()
        }
    }

    private fun updateFavList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = userDao.getAllFavCoins()
            withContext(Dispatchers.Main) {
                favCoins.value = list
            }
        }
    }
}