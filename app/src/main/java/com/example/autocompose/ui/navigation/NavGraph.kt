package com.example.autocompose.ui.navigation

import OnboardingScreen
import android.app.Application
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.autocompose.SplashScreen
import com.example.autocompose.auth.LogInScreen
import com.example.autocompose.auth.SignUpScreen
import com.example.autocompose.ui.composables.AgentScreen
import com.example.autocompose.ui.composables.AnalyticsScreen
import com.example.autocompose.ui.composables.DraftAgentScreen
import com.example.autocompose.ui.composables.HomeScreen
import com.example.autocompose.ui.composables.PaymentScreen
import com.example.autocompose.ui.composables.SettingsScreen
import com.example.autocompose.ui.composables.SubscriptionScreen
import com.example.autocompose.ui.viewmodel.AutoComposeViewmodel
import com.example.autocompose.ui.viewmodel.FrequentEmailViewModel
import com.example.autocompose.ui.viewmodel.PaymentViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    navController: NavController,
//    frequentEmailViewModel: FrequentEmailViewModel,
//    autoComposeViewmodel: AutoComposeViewmodel,
//    paymentViewModel: PaymentViewModel,
//    application: Application,
    firebaseAuth: FirebaseAuth
) {

    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Splash.route) {
        composable(Screen.Home.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(600)) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(600)) + fadeOut()
            }
        ) {
            HomeScreen(
//                frequentEmailViewModel = frequentEmailViewModel,
//                application = application,
                navController = navController
            )
        }
        composable(Screen.AgentScreen.route,
            enterTransition = {
                slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)) + fadeIn()
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500)) + fadeOut()
            }
        ) {
            AgentScreen(
//                autoComposeViewmodel, frequentEmailViewModel,
                navController = navController
            )
        }
        composable(Screen.Analytics.route,
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) }
        ) {
            AnalyticsScreen(
//                autoComposeViewmodel,
                navController = navController
            )
        }
        composable(
            route = Screen.DraftAgentScreen.route,
            arguments = listOf(
                navArgument("subject") { type = NavType.StringType },
                navArgument("emailBody") { type = NavType.StringType }
            ),
            enterTransition = {
                scaleIn(initialScale = 0.8f, animationSpec = tween(500)) + fadeIn()
            },
            exitTransition = {
                scaleOut(targetScale = 1.2f, animationSpec = tween(500)) + fadeOut()
            }
        ) { backStackEntry ->
            val subject = backStackEntry.arguments?.getString("subject") ?: ""
            val emailBody = backStackEntry.arguments?.getString("emailBody") ?: ""
            DraftAgentScreen(
//                autoComposeViewmodel, frequentEmailViewModel,
                navController = navController, passSubject = subject, passEmailContent = emailBody)
        }

        // Payment Screen
        composable(
            route = Screen.PaymentScreen.route,
            arguments = listOf(
                navArgument("amount") { type = NavType.StringType }
            ),
            enterTransition = {
                scaleIn(initialScale = 0.8f, animationSpec = tween(500)) + fadeIn()
            },
            exitTransition = {
                scaleOut(targetScale = 1.2f, animationSpec = tween(500)) + fadeOut()
            }
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount") ?: "5.00"
            PaymentScreen(navController,
//                autoComposeViewmodel, paymentViewModel,
                amount = amount)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController, firebaseAuth)
        }

        composable(Screen.LogIn.route) {
            LogInScreen(navController, firebaseAuth)
        }

        composable(Screen.Splash.route){
            SplashScreen(navController)
        }
        composable(
            route = Screen.SettingsScreen.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(600)) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(600)) + fadeOut()
            }
        ){
            SettingsScreen(navController)
        }

        composable(
            route = Screen.SubscriptionScreen.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(600)) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(600)) + fadeOut()
            }
        ) {
            SubscriptionScreen(navController = navController,
//                autoComposeViewmodel
            )
        }

        composable(
            route = Screen.Onboarding.route,
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) }
        ) {
            OnboardingScreen(navController = navController)
        }
    }
}
