@file:OptIn(ExperimentalMaterialApi::class)

package com.example.arch_practices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arch_practices.model.Coin
import kotlinx.coroutines.launch

@Composable
@Preview
fun CryptoCard(coin: Coin,
               onMenuClick: (Coin) -> Unit,
               onCardClick: (Coin) -> Unit) {
    Card(
        elevation = 4.dp,
        onClick = {
            onCardClick(coin)
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 2.dp,
                bottom = 2.dp
            ),
        border = BorderStroke(1.dp, colorResource(id = R.color.card_border))

    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.BottomEnd){
                Image(
                    painter = painterResource(R.drawable.coin),
                    contentDescription = "Contact profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.gold))
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_check_circle_24),
                    contentDescription = "",
                    tint = colorResource(id = R.color.gold),
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.white))
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(
                    horizontalArrangement = SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = coin.name,
                        fontSize = 16.sp
                    )
                    IconButton(
                        modifier = Modifier.
                        then(Modifier.size(20.dp)),
                        onClick = { onMenuClick(coin) }){
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_more_vert_24),
                            contentDescription = null,
                        )
                    }
                }


                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$ ${coin.priceUsd}",
                        fontSize = 12.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Text(
                        text = "${coin.changePercent24Hr}%",
                        fontSize = 12.sp,
                        color = colorResource(id = getColorByChangePercent(coin.changePercent24Hr)),
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 2.dp
                        )
                    )
                }

                // Add a vertical space between the author and message texts
            }
        }
    }
}

fun getColorByChangePercent(percent: Double) =
    when{
        percent > 0 -> R.color.positive_change_percent
        percent < 0 -> R.color.negative_change_percent
        else -> R.color.non_change_percent
    }
