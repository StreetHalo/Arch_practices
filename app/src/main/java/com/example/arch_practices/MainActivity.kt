package com.example.arch_practices

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.dynamicanimation.animation.DynamicAnimation
import com.example.arch_practices.model.BottomNavPage
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberAnimatedNavController()
            Scaffold(
                topBar = {},
                bottomBar = {
                    BottomNavigation(navController = navController)
                }
            ) {
                NavigationGraph(navController = navController)
            }

        }
    }


    @Preview
    @Composable
    fun FeedScreen(){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.purple_700))
                .wrapContentSize(Alignment.Center)
        ) {
            Row {
                Text(
                    text = "Feed",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                Icon(painter = painterResource(id = R.drawable.ic_baseline_work_24), contentDescription = "")
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
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }

    @Composable
    fun NavigationGraph(navController: NavHostController){
        AnimatedNavHost(
            navController = navController,
            startDestination = BottomNavPage.Feed.screenRoute,
            ){
            composable(
                BottomNavPage.Feed.screenRoute,
                enterTransition = { EnterTransition.None },
                popEnterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popExitTransition = { ExitTransition.None }
            ){
                FeedScreen()
            }
            composable(
                BottomNavPage.Portfolio.screenRoute,
                enterTransition = { EnterTransition.None },
                popEnterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popExitTransition = { ExitTransition.None }
            ){
                PortfolioScreen()
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
            contentColor = Color.Black
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            pages.forEach{
                BottomNavigationItem(
                    icon = {Icon(painter = painterResource(id = it.iconId), contentDescription = stringResource(id = it.titleId))},
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
}