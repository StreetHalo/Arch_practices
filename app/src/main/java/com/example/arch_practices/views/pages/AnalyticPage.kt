package com.example.arch_practices.views.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.arch_practices.R
import com.example.arch_practices.views.TimeRangePicker
import com.example.arch_practices.extensions.setLineDataSet
import com.example.arch_practices.model.AnalyticViewModel
import com.example.arch_practices.model.Coin
import com.example.arch_practices.model.DataState
import com.example.arch_practices.model.IntervalType
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun AnalyticScreen(analyticViewModel: AnalyticViewModel) {
    val dataState by analyticViewModel.getDataState().observeAsState(DataState.Loading(IntervalType.h1))
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                backgroundColor = Color.Blue,
                contentColor = Color.White,
                elevation = 12.dp,
                navigationIcon = {
                    IconButton(
                        onClick = {

                        }) {
                        Icon(painter = painterResource(com.google.android.material.R.drawable.material_ic_keyboard_arrow_left_black_24dp), contentDescription = "")
                    }
                }
            )
        }
    ) {
        AnalyticPage(dataState = dataState, onSelectTimeRange = {
            analyticViewModel.setIntervalType(it)
        })
    }
}

@Composable
fun AnalyticPage(
    dataState: DataState,
    onSelectTimeRange: (IntervalType) -> Unit
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        item { TimeRangePicker(
            selectedTimeRange = dataState.intervalType,
            onTimeRangeSelected = {
                    intervalType ->
                onSelectTimeRange(intervalType)
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

