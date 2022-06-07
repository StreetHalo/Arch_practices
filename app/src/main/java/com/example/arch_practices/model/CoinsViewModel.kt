package com.example.arch_practices.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.arch_practices.utils.Api
import com.example.arch_practices.utils.PagingCoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CoinsViewModel: ViewModel() {
    private val coins: Flow<PagingData<Coin>> = Pager(PagingConfig(pageSize = 100)){
        PagingCoin()
    }.flow.cachedIn(viewModelScope)

    fun getCoins() = coins

}