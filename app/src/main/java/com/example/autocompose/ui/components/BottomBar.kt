package com.example.autocompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.autocompose.ui.navigation.Screen

@Composable
fun BottomBar(navController: NavController){

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val homeRoute = Screen.Home.route
    val analyticsRoute = Screen.Analytics.route

    val isSelectedH = currentRoute == homeRoute
    val isSelectedA = currentRoute == analyticsRoute

    val glowColor = if (isSelectedH || isSelectedA) MaterialTheme.colorScheme.primary else Color.Transparent

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.navigate(homeRoute) {
                        popUpTo(homeRoute) { inclusive = false }
                        launchSingleTop = true
                    }
                          },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 8.dp)
                    .then(
                        if (isSelectedH) Modifier.shadow(
                            elevation = 16.dp,
                            ambientColor = glowColor,
                            spotColor = glowColor,
                            shape = RoundedCornerShape(32.dp)
                        ) else Modifier
                    )
                    .size(64.dp),
            ) {
                Icon(Icons.Default.Home, contentDescription = "Home",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            IconButton(onClick = {
                navController.navigate(analyticsRoute) {
                    popUpTo(homeRoute) { inclusive = false }
                    launchSingleTop = true
                }
                                 },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 8.dp)
                    .then(
                        if (isSelectedA) Modifier.shadow(
                            elevation = 16.dp,
                            ambientColor = glowColor,
                            spotColor = glowColor,
                            shape = RoundedCornerShape(32.dp)
                        ) else Modifier
                    )
                    .size(64.dp)
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = "Trends",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}