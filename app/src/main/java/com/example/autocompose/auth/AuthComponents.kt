package com.example.autocompose.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.autocompose.R
import com.example.autocompose.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun SocialLoginButton(
    onClick: () -> Unit,
    iconRes: Int,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8F7F7))
            .padding(12.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Social login",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun GoogleSignInButton(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isGoogleLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Button(
            onClick = {

                if (isGoogleLoading) return@Button

                isGoogleLoading = true

                scope.launch {

                    val result = GoogleSignInUtils.signIn(context)
                    isGoogleLoading = false

                    result.onSuccess {
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.LogIn.route) { inclusive = true }
                        }
                    }.onFailure {
                        Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isGoogleLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            if (isGoogleLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )

            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Sign in with Google",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}