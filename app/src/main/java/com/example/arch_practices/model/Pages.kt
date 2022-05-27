@file:OptIn(ExperimentalAnimationApi::class)

package com.example.arch_practices.model

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.arch_practices.R
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

sealed class Pages(val screenRoute: String){
    object Main: Pages("main")
    object Crypto: Pages("crypto")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(mainNav: NavController){
    val bottomNavController = rememberAnimatedNavController()
    var onClickListener: (Int) -> Unit = { id -> mainNav.navigate(Pages.Crypto.screenRoute)}

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                backgroundColor = Color.Blue,
                contentColor = Color.White,
                elevation = 12.dp
            )
        },
        bottomBar = {
            BottomNavigation(navController = bottomNavController, onClickListener)
        }
    ) { innerPadding ->
        BottomNavigationGraph(bottomNavController, innerPadding)
    }
}

@Preview
@Composable
fun CryptoScreen(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(text = "BLA_BLA!")
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, innerPadding: PaddingValues){
    AnimatedNavHost(
        navController = navController,
        startDestination = Pages.Main.screenRoute,
        modifier = Modifier.padding(innerPadding)
    ){
        composable(
            Pages.Main.screenRoute,
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popExitTransition = { ExitTransition.None }
        ){
            MainScreen(navController)
        }
        composable(
            Pages.Crypto.screenRoute,
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popExitTransition = { ExitTransition.None }
        ){
            CryptoScreen()
        }
    }
}
