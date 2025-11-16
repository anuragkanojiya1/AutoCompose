package com.example.autocompose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autocompose.ui.navigation.Screen

@Composable
fun BottomBar(navController: NavController){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigate(Screen.Home.route) },
                modifier = Modifier
                    .weight(0.5f)
                    .size(64.dp),
            ) {
                Icon(Icons.Default.Home, contentDescription = "Home",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            IconButton(onClick = { navController.navigate(Screen.Analytics.route) },
                modifier = Modifier
                    .weight(0.5f)
                    .size(64.dp)
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = "Trends",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}