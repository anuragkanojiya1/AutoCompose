package com.example.autocompose.ui.navigation

import android.app.Application
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.autocompose.ui.composables.AgentScreen
import com.example.autocompose.ui.composables.DraftAgentScreen
import com.example.autocompose.ui.composables.HomeScreen
import com.example.autocompose.ui.viewmodel.AutoComposeViewmodel
import com.example.autocompose.ui.viewmodel.FrequentEmailViewModel

@Composable
fun NavGraph(navController: NavController,
             frequentEmailViewModel: FrequentEmailViewModel,
             autoComposeViewmodel: AutoComposeViewmodel,
             application: Application
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }) {
            HomeScreen(
                frequentEmailViewModel = frequentEmailViewModel,
                application = application,
                navController = navController
            )
        }
        composable(Screen.AgentScreen.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }) {
            AgentScreen(autoComposeViewmodel, frequentEmailViewModel)
        }
        composable(
            route = Screen.DraftAgentScreen.route,
            arguments = listOf(
                navArgument("subject") { type = NavType.StringType },
                navArgument("emailBody") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subject = backStackEntry.arguments?.getString("subject") ?: ""
            val emailBody = backStackEntry.arguments?.getString("emailBody") ?: ""
            DraftAgentScreen(autoComposeViewmodel, frequentEmailViewModel, subject, emailBody)
        }
        composable(
            route = Screen.SettingsScreen.route
        ){
        }

    }
}