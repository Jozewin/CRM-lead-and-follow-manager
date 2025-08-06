package com.techpuram.leadandfollowmanagement.presentation.navgraph

import android.os.Build
import androidx.annotation.RequiresApi
import  androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.techpuram.leadandfollowmanagement.presentation.navigator.AppNavigator
import androidx.navigation.NavHostController
import androidx.compose.runtime.LaunchedEffect

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun NavGraph(
    startDestination: String,
    followUpId: Int? = null
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        navigation(
            route = Route.AppStartNavigation.route,
            startDestination = Route.OnBoardingScreen.route
        ){
            composable(
                route = Route.OnBoardingScreen.route
            ){
                Text("Hello")
            }
        }

        navigation(
            route = Route.AppNavigation.route,
            startDestination = Route.AppNavigatorScreen.route
        ){
            composable(route = Route.AppNavigatorScreen.route){
                AppNavigator(followUpId = followUpId)
            }
        }
    }
}