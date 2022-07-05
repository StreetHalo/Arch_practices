@file:OptIn(ExperimentalAnimationApi::class)

package com.example.arch_practices

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import com.example.arch_practices.model.AnalyticViewModel
import com.example.arch_practices.navigations.NavigationGraph
import com.example.arch_practices.viewmodels.CoinsViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.viewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    val navController = rememberAnimatedNavController()
    Scaffold(
    ) { innerPadding ->
        NavigationGraph(navController, innerPadding, getViewModel(), getViewModel())
    }
}