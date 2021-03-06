@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)

package com.example.arch_practices.views.pages

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arch_practices.R
import com.example.arch_practices.model.Coin
import com.example.arch_practices.model.*
import com.example.arch_practices.viewmodels.CoinsViewModel
import com.example.arch_practices.navigations.BottomNavigation
import com.example.arch_practices.navigations.BottomNavigationGraph
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch

sealed class Pages(val screenRoute: String){
    object Main: Pages("main")
    object Analytic: Pages("analytic")
}

@Composable
fun MainScreen(mainNav: NavController, coinsViewModel: CoinsViewModel){
    val bottomNavController = rememberAnimatedNavController()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val currentCoin = remember { mutableStateOf<Coin?>(null)}

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            bottomSheetState.currentValue.let {
                if(currentCoin.value == null) Row(modifier = Modifier.height(1.dp)){}
                else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                    coinsViewModel.addCoinToFav(currentCoin.value ?: return@launch)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.bottom_sheet_add_coin, currentCoin.value?.name ?: ""),
                                style = TextStyle.Default,
                                color = Color.Black
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .alpha(0.2f)
                                .background(Color.Gray)
                        )
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                    mainNav.currentBackStackEntry?.savedStateHandle?.set("coin", currentCoin.value)
                                    mainNav.navigate(Pages.Analytic.screenRoute)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.bottom_sheet_analytic),
                                style = TextStyle.Default,
                                color = Color.Black
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .alpha(0.2f)
                                .background(Color.Gray)
                        )
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = "????????????",
                                style = TextStyle.Default,
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }) {
        Scaffold(
            bottomBar = {
                BottomNavigation(navController = bottomNavController)
            }
        ) { innerPadding ->
            BottomNavigationGraph(bottomNavController, innerPadding, coinsViewModel, callBottomSheet = { coin ->
                currentCoin.value = coin
                coroutineScope.launch {
                    bottomSheetState.show()
                } },
            onCardCoin = { coin ->
                coroutineScope.launch {
                    bottomSheetState.hide()
                    mainNav.currentBackStackEntry?.savedStateHandle?.set("coin", coin)
                    mainNav.navigate(Pages.Analytic.screenRoute)
                }
            })
        }
    }
}
