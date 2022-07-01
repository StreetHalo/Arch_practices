package com.example.arch_practices.views.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.arch_practices.R

@Preview
@Composable
fun ErrorPage(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_sentiment_very_dissatisfied_24),
                modifier = Modifier
                    .size(52.dp)
                    .alpha(0.7f),
                contentDescription = null)
            Spacer(modifier = Modifier.height(height = 8.dp))
            Text(text = stringResource(id = R.string.error_connection))
        }

    }
}