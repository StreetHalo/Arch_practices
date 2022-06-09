package com.example.arch_practices.model

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

sealed class BottomNavPage(val titleId: Int, val iconId: Int, val screenRoute: String){
    object Feed: BottomNavPage(R.string.feed_page_title,  R.drawable.ic_baseline_dns_24, "feed")
    object Portfolio: BottomNavPage(R.string.portfolio_page_title, R.drawable.ic_baseline_work_24, "user_portfolio")
}

@Composable
fun FeedScreen(callBottomSheet: (Coin) -> Unit, viewModel: CoinsViewModel) {
    val coins: LazyPagingItems<Coin> = viewModel.getCoins().collectAsLazyPagingItems()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
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
                    CryptoCard(Coin(
                        name = coin?.name ?: "",
                        priceUsd = coin?.priceUsd ?: 0.0,
                        changePercent24Hr = coin?.changePercent24Hr ?: 0.0,
                        symbol = coin?.symbol ?: ""
                    ),
                        onCardClick =  { coin ->

                        },
                        onMenuClick = { coin ->
                            callBottomSheet(coin)
                        })
                }
            }

            coins.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        //You can add modifier to manage load state when first time response page is loading
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                    }
                    loadState.append is LoadState.Error -> {
                        //You can use modifier to show error message
                    }
                }
        }
    }
}
}

@Composable
fun PortfolioScreen(viewModel: CoinsViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.purple_700))
            .wrapContentSize(Alignment.Center)
    ) {
        val coins = viewModel.getFavCoins().value ?: listOf()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            LazyColumn {
                items(coins){ coin: Coin? ->
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
                            onMenuClick = { coin ->
                               // callBottomSheet(coin)
                            },
                            onSwipeToRemove = {

                            })
                    }
                }
            }
        }
    }
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
