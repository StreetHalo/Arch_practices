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
import java.time.Instant

enum class IntervalType(val timestamp: Long){
    h1(3600000),
    h2(h1.timestamp * 2),
    h6(h1.timestamp * 6),
    h12(h1.timestamp * 12),
    d1(h1.timestamp * 24),
    m1(d1.timestamp * 30),
    m5(m1.timestamp * 5),
    m15(m1.timestamp * 15),
    m30(m1.timestamp * 30)
}

enum class PeriodType(val timestamp: Long){
    h1(3600000),
    h2(h1.timestamp * 2),
    h6(h1.timestamp * 6),
    h12(h1.timestamp * 12),
    d1(h1.timestamp * 24),
    M1(d1.timestamp * 30),
    M6(M1.timestamp * 5),
    M12(M1.timestamp * 15)
}

sealed class DataState(open val periodType: PeriodType){
    class Finished(val data: LineDataSet, override val periodType: PeriodType): DataState(periodType)
    class Loading(override val periodType: PeriodType): DataState(periodType)
    class Error(override val periodType: PeriodType): DataState(periodType)
}

class AnalyticViewModel() : ViewModel() {

    private var coin = MutableLiveData<Coin?>()
    private val dataState = MutableLiveData<DataState>()
    private var periodType = PeriodType.h1

    fun setIntervalType(periodType: PeriodType){
        this.periodType = periodType
        fetchHistories()
    }

    fun getDataState() = dataState

    fun setCoin(coin: Coin) {
        if(coin == this.coin.value) return
        this.coin.value = coin
        fetchHistories()
    }

    fun getCurrentCoin() = coin

    private fun fetchHistories() {
        val currentTime = Instant.now().toEpochMilli()
        dataState.value = DataState.Loading(periodType)
        viewModelScope.launch(Dispatchers.IO) {
            val list = Api.getInstance().getHistoryById(
                coinId = coin.value?.id ?: return@launch,
                interval = getIntervalType(periodType),
                start = currentTime - periodType.timestamp,
                end = currentTime
            ).data

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
                dataState.value = DataState.Finished(dataSet, periodType)
            }
        }
    }

    private fun getIntervalType(periodType: PeriodType) = when(periodType){
        PeriodType.h1 -> IntervalType.m1
        PeriodType.h2 -> IntervalType.m1
        PeriodType.h6 -> IntervalType.m15
        PeriodType.h12 -> IntervalType.m15
        PeriodType.d1 -> IntervalType.m15
        PeriodType.M1 -> IntervalType.h12
        PeriodType.M6 -> IntervalType.d1
        PeriodType.M12 -> IntervalType.d1
    }
}