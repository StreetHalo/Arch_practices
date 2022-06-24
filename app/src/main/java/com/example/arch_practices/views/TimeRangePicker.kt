package com.example.arch_practices.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.arch_practices.model.IntervalType
import com.example.arch_practices.model.PeriodType


@Composable
fun TimeRangePicker(
    modifier: Modifier = Modifier,
    selectedTimeRange: PeriodType = PeriodType.d1,
    onTimeRangeSelected: (PeriodType) -> Unit = {}
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(PeriodType.values()){periodType ->
            TimeRangeChip(
                time = periodType,
                isSelected = selectedTimeRange == periodType
            ) {
                onTimeRangeSelected(periodType)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TimeRangeChip(
    time: PeriodType,
    isSelected: Boolean,
    onTimeRangeSelected: (PeriodType) -> Unit
){
    Chip(
        colors = ChipDefaults.chipColors(
            backgroundColor = if (isSelected) Color.Black else Color.White
        ),
        modifier = Modifier.padding(
            start = 2.dp,
            end = 2.dp,
            top = 4.dp,
            bottom = 4.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.Black
        ),
        onClick = {
        onTimeRangeSelected(time)
    },
    ){
        Text(
            text = time.toString(),
            color = if (isSelected) Color.White else Color.Black,
            modifier = Modifier.padding(8.dp),
        )
    }
}