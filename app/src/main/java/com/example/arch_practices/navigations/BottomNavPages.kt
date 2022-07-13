package com.example.arch_practices.navigations

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.arch_practices.R
import com.example.arch_practices.model.Coin
import com.example.arch_practices.viewmodels.CoinsViewModel
import com.example.arch_practices.views.pages.BottomNavPage
import com.example.arch_practices.views.pages.FeedScreen
import com.example.arch_practices.views.pages.PortfolioScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavigationGraph(navController: NavHostController,
                          innerPadding: PaddingValues,
                          coinsViewModel: CoinsViewModel,
                          callBottomSheet: (Coin) -> Unit,
                          onCardCoin: (Coin) -> Unit) {
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
            FeedScreen(callBottomSheet, onCardCoin, coinsViewModel)
        }
        composable(
            BottomNavPage.Portfolio.screenRoute,
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            PortfolioScreen(coinsViewModel, onCardCoin)
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
