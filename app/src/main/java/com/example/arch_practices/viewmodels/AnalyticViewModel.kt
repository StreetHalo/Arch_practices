package com.example.arch_practices.model

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arch_practices.App
import com.example.arch_practices.R
import com.example.arch_practices.extensions.formatNumbersAfterDot
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
    private var dataSet = LineDataSet(listOf(), "label")
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

    fun getCurrentCoin() = coin.value

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
                dataSet = LineDataSet(data, "label").apply {
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    fillDrawable = getGraphBackground(data)
                    highLightColor = getGraphColor(data)
                    color = getGraphColor(data)
                    lineWidth = 1f
                    setDrawFilled(true)
                    setDrawCircles(false)
                }
                dataState.value = DataState.Finished(dataSet, periodType)
            }
        }
    }

    fun getFormattedDifferentPrice(): String{
       return when(dataState.value){
            is DataState.Error -> ""
            else -> "$ " + if(isPriceGrow(dataSet.values)) "+" else {""} + getDifferentPrice(dataSet.values).formatNumbersAfterDot()
        }
    }

    fun getFormattedDifferentPercent(): String{
        return when(dataState.value){
            is DataState.Error -> ""
            else -> getDifferentPercent(dataSet.values).formatNumbersAfterDot() + "%"
        }
    }

    fun getCurrentCoinPrice() = "$ ${dataSet.values.lastOrNull()?.y?.toDouble().formatNumbersAfterDot()}"


    private fun getDifferentPrice(data: List<Entry>): Double {
        if(data.isEmpty()) return 0.0
        val firstData = data.first().y.toDouble()
        val lastData = data.last().y.toDouble()

        return lastData - firstData
    }

    private fun getDifferentPercent(data: List<Entry>): Double {
        if(data.isEmpty()) return 0.0
        val firstData = data.first().y.toDouble()
        val lastData = data.last().y.toDouble()

        return (lastData - firstData) / lastData * 100
    }

    private fun getGraphColor(data: List<Entry>) = App.instance.getColor(
        if(isPriceGrow(data)) R.color.positive_change_percent
        else R.color.negative_change_percent
        )

    private fun getGraphBackground(data: List<Entry>) = App.instance.getDrawable(
        if(isPriceGrow(data)) R.drawable.graph_background_positive
        else R.drawable.graph_background_negative
        )

    private fun isPriceGrow(data: List<Entry>) = getDifferentPrice(data) > 0

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

    fun getGrowIconId() = when(dataState.value){
        is DataState.Error -> R.drawable.ic_baseline_remove_24
        else -> {
            if(isPriceGrow(dataSet.values)) R.drawable.ic_baseline_arrow_drop_up_24
            else R.drawable.ic_baseline_arrow_drop_down_24
        }
    }
}