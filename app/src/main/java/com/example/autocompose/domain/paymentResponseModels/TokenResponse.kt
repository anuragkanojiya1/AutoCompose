package com.example.autocompose.domain.paymentResponseModels

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val app_id: String,
    val expires_in: Int,
    val nonce: String,
)