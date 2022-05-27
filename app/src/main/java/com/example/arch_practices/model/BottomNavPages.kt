package com.example.arch_practices.model

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.arch_practices.CryptoCard
import com.example.arch_practices.R
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

sealed class BottomNavPage(val titleId: Int, val iconId: Int, val screenRoute: String){
    object Feed: BottomNavPage(R.string.feed_page_title,  R.drawable.ic_baseline_dns_24, "feed")
    object Portfolio: BottomNavPage(R.string.portfolio_page_title, R.drawable.ic_baseline_work_24, "user_portfolio")
}
var onClick: ((Int) -> Unit)? = null

@Preview
@Composable
fun FeedScreen(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        LazyColumn(){
            items(32){
                CryptoCard(it, onClick)
            }
        }
    }
}

@Preview
@Composable
fun PortfolioScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.purple_700))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Portfolio",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavigationGraph(navController: NavHostController, innerPadding: PaddingValues) {
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
            FeedScreen()
        }
        composable(
            BottomNavPage.Portfolio.screenRoute,
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            PortfolioScreen()
        }
    }
}


@Composable
fun BottomNavigation(navController: NavController, onClickListener: (Int) -> Unit){
    onClick = onClickListener
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
