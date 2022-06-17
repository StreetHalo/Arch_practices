@file:OptIn(ExperimentalAnimationApi::class)

package com.example.arch_practices

import android.os.Bundle
import android.util.Log
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
import com.example.arch_practices.model.AnalyticViewModel
import com.example.arch_practices.views.pages.AnalyticScreen
import com.example.arch_practices.model.Coin
import com.example.arch_practices.navigations.NavigationGraph
import com.example.arch_practices.viewmodels.CoinsViewModel
import com.example.arch_practices.views.pages.MainScreen
import com.example.arch_practices.views.pages.Pages
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val coinsViewModel: CoinsViewModel by viewModels()
        val analyticViewModel: AnalyticViewModel by viewModels()
        setContent {
            App(coinsViewModel, analyticViewModel)
        }
    }
}

@Composable
fun App(coinsViewModel: CoinsViewModel, analyticViewModel: AnalyticViewModel) {
    val navController = rememberAnimatedNavController()
    Scaffold(
    ) { innerPadding ->
        NavigationGraph(navController, innerPadding, coinsViewModel, analyticViewModel)
    }
}