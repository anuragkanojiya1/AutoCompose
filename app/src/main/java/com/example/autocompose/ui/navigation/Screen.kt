package com.example.autocompose.ui.navigation

sealed class Screen(val route: String){
    object Home : Screen("home")
    object AgentScreen : Screen("agent_screen")
    object DraftAgentScreen : Screen("draft_agent_screen/{subject}/{emailBody}")
    object SettingsScreen : Screen("settings_screen")
    object Splash : Screen("splash_screen")
    object Analytics : Screen("analytics_screen")
    object LogIn : Screen("log_in_screen")
    object SignUp : Screen("sign_up_screen")
    object PaymentScreen : Screen("payment_screen/{amount}")
    object SubscriptionScreen : Screen("subscription_screen")
    object Onboarding : Screen("onboarding_screen")
}
