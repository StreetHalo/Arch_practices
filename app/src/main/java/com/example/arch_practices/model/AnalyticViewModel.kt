package com.example.arch_practices.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arch_practices.utils.Api
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class IntervalType{
    h1, h2, h6, h12, d1, m1, m5, m15, m30
}

class AnalyticViewModel() : ViewModel() {

    private val histories = MutableLiveData<List<History>>()
    private val intervalType = MutableLiveData<IntervalType>()
    private val dataSet = MutableLiveData<LineDataSet>()
    private var coin: Coin? = null

    init {
        intervalType.value = IntervalType.h1
    }

    fun updateIntervalType(intervalType: IntervalType){
        this.intervalType.value = intervalType
        fetchHistories(coin = coin ?: return)
    }

    fun getHistories() = histories

    fun getDataSet() = dataSet

    fun fetchHistories(coin: Coin) {
        this.coin = coin
        viewModelScope.launch(Dispatchers.IO) {
            val intervalType = intervalType.value ?: return@launch
            val list = Api.getInstance().getHistoryById(coin.id, intervalType).data
            launch(Dispatchers.Main) {
                histories.value = list
                dataSet.value?.clear()
                val data = list.map {
                   Entry(
                       it.time.toFloat(),
                       it.priceUsd.toFloat()
                   )
                }
                dataSet.value = LineDataSet(data, "label")
            }
        }
    }
}