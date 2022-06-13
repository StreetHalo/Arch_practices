package com.example.arch_practices.model

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.arch_practices.CryptoCard
import com.example.arch_practices.FavCryptoCard
import com.example.arch_practices.R
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

sealed class BottomNavPage(val titleId: Int, val iconId: Int, val screenRoute: String) {
    object Feed : BottomNavPage(R.string.feed_page_title, R.drawable.ic_baseline_dns_24, "feed")
    object Portfolio : BottomNavPage(
        R.string.portfolio_page_title,
        R.drawable.ic_baseline_work_24,
        "user_portfolio"
    )
}

@Composable
fun FeedScreen(callBottomSheet: (Coin) -> Unit, viewModel: CoinsViewModel) {
    val coins: LazyPagingItems<Coin> = viewModel.getCoins().collectAsLazyPagingItems()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        coins.apply {
            when {
                loadState.refresh is LoadState.Loading -> {

                    Log.d("ddd", "loadState.refresh is LoadState.Loading")

                    CircularProgressIndicator()
                    //You can add modifier to manage load state when first time response page is loading
                }
                loadState.append is LoadState.Loading -> {
                    if (coins.itemCount == 0) return
                    FeedList(
                        viewModel = viewModel,
                        coins = coins,
                        callBottomSheet = callBottomSheet
                    )
                    Log.d("ddd", "loadState.append is LoadState.Loading")
                }

                loadState.append is LoadState.Error -> {
                    //You can use modifier to show error message
                }
            }
        }
    }
}

@Composable
fun FeedList(viewModel: CoinsViewModel,
             coins: LazyPagingItems<Coin>,
             callBottomSheet: (Coin) -> Unit){
    Log.d("ddd","size ${coins.itemCount}")
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
                        name = coin?.name ?: "",
                        priceUsd = coin?.priceUsd ?: 0.0,
                        changePercent24Hr = coin?.changePercent24Hr ?: 0.0,
                        symbol = coin?.symbol ?: "",
                        isSaved = viewModel.isAddedToFav(coin ?: return@Row)
                    ),
                        onCardClick = { coin ->

                        },
                        onMenuClick = { coin ->
                            callBottomSheet(coin)
                        },
                        onFavClick = { coin ->
                            if (coin.isSaved) viewModel.removeCoinFromFav(coin)
                            else viewModel.addCoinToFav(coin)
                            coins.refresh()
                            //coins.peek(0)
                            //coins.retry()
                        })
                }
            }
        }
}

@Composable
fun PortfolioScreen(viewModel: CoinsViewModel){
    val coins by viewModel.getFavCoins().observeAsState(initial = emptyList())
    val isShowDialog = remember { mutableStateOf(false)  }
    val selectedCoin: MutableState<Coin?> = remember{ mutableStateOf(null)}
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        RemoveCoinDialog(
            isOpenDialog = isShowDialog,
            coin = selectedCoin.value,
            onRemoveCoin = {coin ->
                viewModel.removeCoinFromFav(coin)
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopCenter)
        ) {
            LazyColumn {
                items(items = coins){ coin: Coin? ->
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
                            name = coin?.name ?: "",
                            priceUsd = coin?.priceUsd ?: 0.0,
                            changePercent24Hr = coin?.changePercent24Hr ?: 0.0,
                            symbol = coin?.symbol ?: ""
                        ),
                            onCardClick =  { coin ->

                            },
                            onSwipeToRemove = { coin ->
                                selectedCoin.value = coin
                                isShowDialog.value = true
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun RemoveCoinDialog(
    coin: Coin?,
    isOpenDialog: MutableState<Boolean>,
    onRemoveCoin: (Coin) -> Unit
) {
    if(!isOpenDialog.value) return
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
                    stringResource(id = R.string.ОК).uppercase()
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavigationGraph(navController: NavHostController,
                          innerPadding: PaddingValues,
                          coinsViewModel: CoinsViewModel,
                          callBottomSheet: (Coin) -> Unit) {
    AnimatedNavHost(
        navController,
        modifier = Modifier.padding(innerPadding),
        startDestination = BottomNavPage.Feed.screenRoute) {
        composable(
            BottomNavPage.Feed.screenRoute,
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            FeedScreen(callBottomSheet, coinsViewModel)
        }
        composable(
            BottomNavPage.Portfolio.screenRoute,
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            PortfolioScreen(coinsViewModel)
        }
    }
}


@Composable
fun BottomNavigation(navController: NavController){
    val pages = listOf(
        BottomNavPage.Feed,
        BottomNavPage.Portfolio
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        pages.forEach{
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = it.iconId), contentDescription = stringResource(id = it.titleId)) },
                label = { Text(text = stringResource(id = it.titleId))},
                selectedContentColor = colorResource(id = R.color.purple_500),
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == it.screenRoute,
                onClick = {
                    navController.navigate(it.screenRoute){
                        navController.graph.startDestinationRoute.let { screenRoute ->
                            popUpTo(screenRoute ?: return@let){
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
