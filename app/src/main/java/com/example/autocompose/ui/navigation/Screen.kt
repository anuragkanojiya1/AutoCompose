package com.example.autocompose.ui.navigation

sealed class Screen(val route: String){
    object Home : Screen("home")
    object AgentScreen : Screen("agent_screen")
    object DraftAgentScreen : Screen("draft_agent_screen/{subject}/{emailBody}")
    object SettingsScreen : Screen("settings_screen")
}