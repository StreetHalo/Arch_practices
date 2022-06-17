@file:OptIn(ExperimentalMaterialApi::class)

package com.example.arch_practices

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.arch_practices.extensions.formatNumbersAfterDot
import com.example.arch_practices.model.Coin
import kotlinx.coroutines.launch

@Composable
fun CryptoCard(
    coin: Coin,
    onMenuClick: (Coin) -> Unit,
    onCardClick: (Coin) -> Unit,
    onFavClick: (Coin) -> Unit
) {
    Card(
        elevation = 4.dp,
        onClick = {
            onCardClick(coin)
        },
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
            AvatarCoin(coin = coin)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(
                    horizontalArrangement = SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    TitleCoinText(coin = coin)
                    Row {
                        IconButton(
                            modifier = Modifier.
                            then(
                                Modifier
                                    .size(22.dp)
                                    .padding(end = 4.dp)),
                            onClick = { onFavClick(coin) }){
                            Image(
                                painterResource(
                                    id = if(coin.isSaved) R.drawable.ic_baseline_star_rate_24
                                    else R.drawable.ic_baseline_star_border_24),
                                contentDescription = null,
                            )
                        }
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
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PriceCoinText(coin = coin)
                    PercentCoinText(coin = coin)
                }
            }
        }
    }
}

@Composable
fun FavCryptoCard(coin: Coin,
                  onCardClick: (Coin) -> Unit,
                  onSwipeToRemove: (Coin) -> Unit){
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberDismissState(initialValue = DismissValue.Default)
    if (dismissState.isDismissed(DismissDirection.EndToStart)){
        onSwipeToRemove(coin)
        coroutineScope.launch {
            dismissState.snapTo(DismissValue.Default)
        }
    }

    SwipeToDismiss(
        state = dismissState,
        background = {
            val direction = dismissState.dismissDirection
            if (direction == DismissDirection.EndToStart) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    backgroundColor = Color.Red
                ) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                        ) {
                        Column(
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.heightIn(2.dp))
                            Text(
                                text = stringResource(R.string.remove),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },

        dismissContent = {
            Card(
                elevation = 4.dp,
                onClick = {
                    onCardClick(coin)
                },
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
                    AvatarCoin(coin = coin)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ){
                            TitleCoinText(coin = coin)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PriceCoinText(coin = coin)
                            PercentCoinText(coin = coin)
                        }
                    }
                }
            }
        },
        directions = setOf(DismissDirection.EndToStart),
    )
}
fun getColorByChangePercent(percent: Double) =
    when{
        percent > 0 -> R.color.positive_change_percent
        percent < 0 -> R.color.negative_change_percent
        else -> R.color.non_change_percent
    }

@Composable
fun TitleCoinText(coin: Coin){
    Text(
        text = coin.name,
        fontSize = 16.sp
    )
}

@Composable
fun PercentCoinText(coin: Coin){
    Text(
        text = "${coin.changePercent24Hr.formatNumbersAfterDot()}%",
        fontSize = 12.sp,
        color = colorResource(id = getColorByChangePercent(coin.changePercent24Hr)),
        modifier = Modifier.padding(
            start = 12.dp,
            end = 2.dp
        )
    )
}

@Composable
fun PriceCoinText(coin: Coin){
    Text(
        text = "$ ${coin.priceUsd.formatNumbersAfterDot(4)}",
        fontSize = 12.sp,
        modifier = Modifier.alpha(0.7f)
    )
}

@Composable
fun AvatarCoin(coin: Coin){
    val coinName = coin.symbol.lowercase()
    val uriPath = "android.resource://" + App.instance.packageName.toString() + "/drawable/${coinName}"
    val uri: Uri = Uri.parse(uriPath)

    Box(contentAlignment = Alignment.BottomEnd){
        Image(
            painter = rememberAsyncImagePainter(uri, placeholder = painterResource(id = R.drawable.ic_baseline_currency_bitcoin_24)),
            contentDescription = " ",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
    }
}
