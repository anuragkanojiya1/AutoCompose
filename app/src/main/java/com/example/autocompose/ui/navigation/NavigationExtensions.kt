package com.example.autocompose.ui.navigation

import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Navigation extension functions for easier navigation
fun NavController.navigateToPayment(amount: String) {
    val encodedAmount = URLEncoder.encode(amount, StandardCharsets.UTF_8.toString())
    this.navigate("payment_screen/$encodedAmount")
}