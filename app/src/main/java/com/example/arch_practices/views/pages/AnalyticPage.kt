package com.example.arch_practices.views.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.arch_practices.R
import com.example.arch_practices.extensions.formatNumbersAfterDot
import com.example.arch_practices.views.TimeRangePicker
import com.example.arch_practices.extensions.setLineDataSet
import com.example.arch_practices.model.*
import com.example.arch_practices.views.AvatarCoin
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun AnalyticScreen(
    analyticViewModel: AnalyticViewModel,
    onBackPressed: () -> Unit
) {
    val dataState by analyticViewModel.getDataState().observeAsState(DataState.Loading(PeriodType.h1))
    val currentCoin by analyticViewModel.getCurrentCoin().observeAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.analytic_page))
                },
                backgroundColor = Color.White,
                contentColor = Color.Black,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPressed()
                        }) {
                        Icon(painter = painterResource(R.drawable.ic_baseline_arrow_back_24), contentDescription = "")
                    }
                }
            )
        }
    ) {
        AnalyticPage(
            coin = currentCoin ?: return@Scaffold,
            dataState = dataState, onSelectTimeRange = {
            analyticViewModel.setIntervalType(it)
        }
        )
    }
}

@Composable
fun AnalyticPage(
    coin: Coin,
    dataState: DataState,
    onSelectTimeRange: (PeriodType) -> Unit
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        item { Header(coin = coin, dataState = dataState) }
        item { TimeRangePicker(
            selectedTimeRange = dataState.periodType,
            onTimeRangeSelected = {
                    periodType ->
                onSelectTimeRange(periodType)
            }
        ) }
        item {
            when(dataState){
                is DataState.Loading ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                is DataState.Error -> {}
                is DataState.Finished -> {
                     Chart(lineDataSet = dataState.data)
                }
            }
        }
    }
}

@Composable
fun Header(coin: Coin, dataState: DataState){
    val data = if(dataState is DataState.Finished) dataState.data.values else listOf()
    val isGrow = getDifferentPrice(data) > 0f
    val isVisible = data.isNotEmpty()

    Row(
        modifier = Modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarCoin(coin = coin)
        Column(
            modifier = Modifier
                .weight(1f)
                .width(IntrinsicSize.Min)
                .padding(start = 8.dp)
        ) {
            Text(
                text = "$ ${data.lastOrNull()?.y?.toDouble().formatNumbersAfterDot()}",
                fontSize = 16.sp,
                modifier = Modifier.alpha(if(isVisible) 1f else 0f)
            )
            Text(
                text = coin.name,
                fontSize = 12.sp,
                modifier = Modifier.alpha(0.7f)
            )
        }

        Row(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .weight(1f)
                .padding(
                    start = 8.dp,
                    end = 8.dp
                ),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column{
                Text(
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End).alpha(if(isVisible) 1f else 0f)
                    ,
                    text = "$ " + if(isGrow) "+" else {""} + getDifferentPrice(data).formatNumbersAfterDot()
                )
                Text(
                    modifier = Modifier.align(Alignment.End).alpha(if(isVisible) 1f else 0f),
                    text = getDifferentPercent(data).formatNumbersAfterDot() + "%"
                )
            }
            Image(painter = painterResource(
                if(isGrow) R.drawable.ic_baseline_arrow_drop_up_24
                else R.drawable.ic_baseline_arrow_drop_down_24
            ), contentDescription = null, modifier = Modifier.alpha(if(isVisible) 1f else 0f)
            )
        }
    }
}

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

@Composable
fun Chart(
    modifier: Modifier = Modifier.padding(
        top = 4.dp,
    ),
    lineDataSet: LineDataSet? = null,
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                isDragEnabled = false
                xAxis.isEnabled = false
                axisLeft.setDrawAxisLine(false)
                axisRight.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                setScaleEnabled(true)
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

