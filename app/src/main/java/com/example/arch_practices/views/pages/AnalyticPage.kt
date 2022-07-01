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
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.*

@Composable
fun AnalyticScreen(
    analyticViewModel: AnalyticViewModel,
    onBackPressed: () -> Unit
) {
    val dataState by analyticViewModel.getDataState().observeAsState(DataState.Loading(PeriodType.h1))

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
            dataState,
            analyticViewModel, onSelectTimeRange = {
            analyticViewModel.setIntervalType(it)
        }
        )
    }
}

@Composable
fun AnalyticPage(
    dataState: DataState,
    analyticViewModel: AnalyticViewModel,
    onSelectTimeRange: (PeriodType) -> Unit
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        item { Header(analyticViewModel) }
        item { TimeRangePicker(
            selectedTimeRange = dataState.periodType,
            onTimeRangeSelected = {
                    periodType ->
                onSelectTimeRange(periodType)
            }
        ) }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(300.dp),
                contentAlignment = Alignment.Center
            ) {
                when(dataState){
                    is DataState.Loading -> CircularProgressIndicator()
                    is DataState.Error -> ErrorPage()
                    is DataState.Finished -> Chart(lineDataSet = dataState.data)
                }
            }
        }
    }
}

@Composable
fun Header(analyticViewModel: AnalyticViewModel){
    Row(
        modifier = Modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarCoin(coin = analyticViewModel.getCurrentCoin() ?: return)
        Column(
            modifier = Modifier
                .weight(1f)
                .width(IntrinsicSize.Min)
                .padding(start = 8.dp)
        ) {
            Text(
                text = analyticViewModel.getCurrentCoinPrice(),
                fontSize = 16.sp,
            )
            Text(
                text = analyticViewModel.getCurrentCoin()?.name ?: "",
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
                    text = analyticViewModel.getFormattedDifferentPrice()
                )
                Text(
                    text = analyticViewModel.getFormattedDifferentPercent()
                )
            }
            Image(painter = painterResource(
                analyticViewModel.getGrowIconId()
            ), contentDescription = null)
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
                axisLeft.valueFormatter = AmountFormatter()
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

class AmountFormatter: ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return "$ ${value.toDouble().formatNumbersAfterDot()}"
    }
}

