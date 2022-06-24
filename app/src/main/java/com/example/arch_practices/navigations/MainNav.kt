package com.example.arch_practices.navigations

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.arch_practices.model.AnalyticViewModel
import com.example.arch_practices.model.Coin
import com.example.arch_practices.viewmodels.CoinsViewModel
import com.example.arch_practices.views.pages.AnalyticScreen
import com.example.arch_practices.views.pages.MainScreen
import com.example.arch_practices.views.pages.Pages
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    coinsViewModel: CoinsViewModel,
    analyticViewModel: AnalyticViewModel
){
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
        composable(
            Pages.Analytic.screenRoute,
             enterTransition = { EnterTransition.None },
             popEnterTransition = { EnterTransition.None },
             exitTransition = { ExitTransition.None },
             popExitTransition = { ExitTransition.None }
        ){
            val coin = navController.previousBackStackEntry?.savedStateHandle?.get<Coin>("coin") ?: return@composable
            analyticViewModel.setCoin(coin)
            AnalyticScreen(analyticViewModel, onBackPressed = {
                navController.popBackStack()
            })
        }
    }
}