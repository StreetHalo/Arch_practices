package com.example.arch_practices.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arch_practices.R
import com.example.arch_practices.utils.Api
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class IntervalType{
    h1, h2, h6, h12, d1, m1, m5, m15, m30
}

sealed class DataState(open val intervalType: IntervalType){
    class Finished(val data: LineDataSet, override val intervalType: IntervalType): DataState(intervalType)
    class Loading(override val intervalType: IntervalType): DataState(intervalType)
    class Error(override val intervalType: IntervalType): DataState(intervalType)
}

class AnalyticViewModel() : ViewModel() {

    private var coin: Coin? = null
    private val dataState = MutableLiveData<DataState>()
    private var intervalType = IntervalType.h1

    fun setIntervalType(intervalType: IntervalType){
        this.intervalType = intervalType
        fetchHistories()
    }

    fun getDataState() = dataState

    fun setCoin(coin: Coin) {
        if(coin == this.coin) return
        this.coin = coin
        fetchHistories()
    }

    private fun fetchHistories() {
        dataState.value = DataState.Loading(intervalType)
        viewModelScope.launch(Dispatchers.IO) {
            val list = Api.getInstance().getHistoryById(coin?.id ?: return@launch, intervalType).data
            launch(Dispatchers.Main) {
                val data = list.map {
                   Entry(
                       it.time.toFloat(),
                       it.priceUsd.toFloat()
                   )
                }
                val dataSet = LineDataSet(data, "label").apply {
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    color = getColor(R.color.teal_200)
                    highLightColor = getColor(R.color.teal_200)
                   // fillDrawable = getBackground(context)
                    lineWidth = 2f
                    setDrawFilled(true)
                    setDrawCircles(false)

                }
                dataState.value = DataState.Finished(dataSet, intervalType)
            }
        }
    }
}