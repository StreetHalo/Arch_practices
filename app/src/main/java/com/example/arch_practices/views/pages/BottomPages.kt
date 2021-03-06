package com.example.arch_practices.views.pages

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.arch_practices.App
import com.example.arch_practices.views.CryptoCard
import com.example.arch_practices.views.FavCryptoCard
import com.example.arch_practices.R
import com.example.arch_practices.model.Coin
import com.example.arch_practices.viewmodels.CoinsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject

sealed class BottomNavPage(val titleId: Int, val iconId: Int, val screenRoute: String) {
    object Feed : BottomNavPage(R.string.feed_page_title, R.drawable.ic_baseline_dns_24, "feed")
    object Portfolio : BottomNavPage(
        R.string.portfolio_page_title,
        R.drawable.ic_baseline_work_24,
        "user_portfolio"
    )
}

@Composable
fun FeedScreen(
    callBottomSheet: (Coin) -> Unit,
    onCardCoin: (Coin) -> Unit,
    viewModel: CoinsViewModel
) {
    val coins: LazyPagingItems<Coin> = viewModel.getCoins().collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context: Context by inject()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        LazyColumn {
            items(items = coins) { coin: Coin? ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 2.dp,
                            bottom = 2.dp
                        )
                ) {
                    CryptoCard(Coin(
                        id = coin?.id ?: "",
                        name = coin?.name ?: "",
                        priceUsd = coin?.priceUsd ?: 0.0,
                        changePercent24Hr = coin?.changePercent24Hr ?: 0.0,
                        symbol = coin?.symbol ?: "",
                        isSaved = viewModel.isAddedToFav(coin ?: return@Row)
                    ),
                        onCardClick = { coin ->
                            onCardCoin(coin)
                        },
                        onMenuClick = { coin ->
                            callBottomSheet(coin)
                        },
                        onFavClick = { coin ->
                            if (coin.isSaved) viewModel.removeCoinFromFav(coin)
                            else viewModel.addCoinToFav(coin)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = if(coin.isSaved) context.getString(R.string.coin_removed, coin.name)
                                    else context.getString(R.string.coin_added, coin.name)
                                )
                            }
                            coins.refresh()
                        }
                    )
                }
            }

            coins.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    loadState.refresh is LoadState.Error -> {
                        item { ErrorPage() }
                    }

                    loadState.append is LoadState.Loading -> {
                        item { CircularProgressIndicator() }

                    }

                    loadState.append is LoadState.Error -> {
                        item { ErrorPage() }
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun PortfolioScreen(
    viewModel: CoinsViewModel,
    onCardCoin: (Coin) -> Unit,
    ){
    val coins by viewModel.getFavCoins().observeAsState()
    val isShowDialog = remember { mutableStateOf(false)  }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val selectedCoin: MutableState<Coin?> = remember{ mutableStateOf(null)}
    val context: Context by inject()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        RemoveCoinDialog(
            isOpenDialog = isShowDialog,
            coin = selectedCoin.value,
            onRemoveCoin = {coin ->
                viewModel.removeCoinFromFav(coin)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.coin_removed, coin.name)
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopCenter)
        ) {
            if(coins.isNullOrEmpty()) EmptyPortfolio()
            else LazyColumn {
                items(items = coins ?: listOf()){ coin: Coin? ->
                    Row(modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 2.dp,
                            bottom = 2.dp
                        )
                    ) {
                        FavCryptoCard(Coin(
                            id = coin?.id ?: "",
                            name = coin?.name ?: "",
                            priceUsd = coin?.priceUsd ?: 0.0,
                            changePercent24Hr = coin?.changePercent24Hr ?: 0.0,
                            symbol = coin?.symbol ?: ""
                        ),
                            onCardClick =  { coin ->
                                onCardCoin(coin)
                            },
                            onSwipeToRemove = { coin ->
                                selectedCoin.value = coin
                                isShowDialog.value = true
                            })
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun EmptyPortfolio(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_work_24),
                modifier = Modifier
                    .size(52.dp)
                    .alpha(0.7f),
                contentDescription = null)
            Spacer(modifier = Modifier.height(height = 8.dp))
            Text(text = stringResource(id = R.string.empty_portfolio))
        }

    }
}

@Composable
fun RemoveCoinDialog(
    coin: Coin?,
    isOpenDialog: MutableState<Boolean>,
    onRemoveCoin: (Coin) -> Unit
) {
    if (!isOpenDialog.value) return
    AlertDialog(
        onDismissRequest = {
            isOpenDialog.value = false
        },
        title = {
            Text(text = stringResource(id = R.string.attention))
        },
        text = {
            Text(stringResource(id = R.string.is_remove_coin, coin?.name ?: ""))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    isOpenDialog.value = false
                    onRemoveCoin(coin ?: return@TextButton)
                }) {
                Text(
                    stringResource(id = R.string.????).uppercase()
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    isOpenDialog.value = false
                }) {
                Text(
                    stringResource(id = R.string.cancel).uppercase(),
                )
            }
        })
}