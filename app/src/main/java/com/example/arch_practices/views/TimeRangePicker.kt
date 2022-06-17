package com.example.arch_practices.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.arch_practices.model.IntervalType


@Composable
fun TimeRangePicker(
    modifier: Modifier = Modifier,
    selectedTimeRange: IntervalType = IntervalType.d1,
    onTimeRangeSelected: (IntervalType) -> Unit = {}
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(IntervalType.values()){intervalType ->
            TimeRangeChip(
                time = intervalType,
                isSelected = selectedTimeRange == intervalType
            ) {
                onTimeRangeSelected(intervalType)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TimeRangeChip(
    time: IntervalType,
    isSelected: Boolean,
    onTimeRangeSelected: (IntervalType) -> Unit
){
    Chip(
        modifier = Modifier.padding(
            start = 2.dp,
            end = 2.dp,
            top = 4.dp,
            bottom = 4.dp),
        onClick = {
        onTimeRangeSelected(time)
    },
    ){
        Text(
            text = time.toString(),
            color = if (isSelected) MaterialTheme.colors.background else MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(8.dp)
        )
    }
}