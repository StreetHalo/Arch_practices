package com.example.arch_practices.model.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.arch_practices.R
import com.example.arch_practices.components.TimeRangePicker
import com.example.arch_practices.extensions.setLineDataSet
import com.example.arch_practices.model.AnalyticViewModel
import com.example.arch_practices.model.Coin
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun AnalyticScreen(coin: Coin, analyticViewModel: AnalyticViewModel) {
    val lineDataSet by analyticViewModel.getDataSet().observeAsState(LineDataSet(listOf(), "bla-bla"))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        TimeRangePicker()
        Chart(lineDataSet = lineDataSet)
        Text(text = "Hi there! \n I'am ${coin.name}")
        Text(text = "${coin.changePercent24Hr}")
    }
}

@Composable
fun Chart(
    modifier: Modifier = Modifier,
    lineDataSet: LineDataSet? = null,
) {

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                isDragEnabled = false
                xAxis.isEnabled = false
                axisLeft.setDrawAxisLine(false)
                axisLeft.textColor = resources.getColor(R.color.purple_500)
                axisRight.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                setScaleEnabled(false)
                setDrawGridBackground(false)
                setDrawBorders(false)
                invalidate()

                setLineDataSet(lineDataSet = lineDataSet)
            }
        },
        update = {
            it.setLineDataSet(lineDataSet = lineDataSet)
        },
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(300.dp)
    )
}

