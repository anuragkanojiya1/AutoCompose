package com.example.autocompose.domain.paymentResponseModels

data class TokenResponse(
    val scope: String? = null,
    val access_token: String,
    val token_type: String,
    val app_id: String? = null,
    val expires_in: Long,
    val nonce: String? = null,
)