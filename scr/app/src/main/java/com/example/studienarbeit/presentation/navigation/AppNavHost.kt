package com.example.studienarbeit.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studienarbeit.presentation.screens.map.MapScreen
import com.example.studienarbeit.presentation.screens.map.MapViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigator: Navigator,
    startDestination: String = Navigator.NavTarget.MAP.label,
    innerPadding: PaddingValues
) {
    LaunchedEffect("navigation") {
        navigator.sharedFlow.onEach {
            navController.navigate(it.label)
        }.launchIn(this)
    }
    //TODO : fix this
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.padding(innerPadding),
    ) {

        composable(Navigator.NavTarget.MAP.label) { backStackEntry ->

            val viewModel = hiltViewModel<MapViewModel>()
            MapScreen(viewModel = viewModel)
        }
        composable(Navigator.NavTarget.LOGIN.label) {
           // SignInScreen()
        }

    }
}