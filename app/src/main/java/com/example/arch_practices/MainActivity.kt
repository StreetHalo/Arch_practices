@file:OptIn(ExperimentalAnimationApi::class)

package com.example.arch_practices

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.arch_practices.model.AnalyticScreen
import com.example.arch_practices.model.Coin
import com.example.arch_practices.model.CoinsViewModel
import com.example.arch_practices.model.pages.MainScreen
import com.example.arch_practices.model.pages.Pages
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model: CoinsViewModel by viewModels()
        setContent {
            App(model)
        }
    }
}

@Composable
fun App(coinsViewModel: CoinsViewModel) {
    val navController = rememberAnimatedNavController()
    Scaffold(
    ) { innerPadding ->
        NavigationGraph(navController, innerPadding, coinsViewModel)
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, innerPadding: PaddingValues, coinsViewModel: CoinsViewModel){
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
            MainScreen(navController, coinsViewModel = coinsViewModel)
        }
        //            arguments = listOf(navArgument("coin") { type = NavType.StringType },
        composable(
            Pages.Analytic.screenRoute,
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popExitTransition = { ExitTransition.None }
        ){
            val coin = navController.previousBackStackEntry?.savedStateHandle?.get<Coin>("coin")
            coin?.let { coin ->
                AnalyticScreen(coin)
            }
        }
    }
}