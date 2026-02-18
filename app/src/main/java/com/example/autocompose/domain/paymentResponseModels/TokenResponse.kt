package com.example.autocompose.domain.paymentResponseModels

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("scope")
    val scope: String? = null,
    @SerializedName("access_token")
    val access_token: String,
    @SerializedName("token_type")
    val token_type: String,
    @SerializedName("app_id")
    val app_id: String? = null,
    @SerializedName("expires_in")
    val expires_in: Long,
    @SerializedName("nonce")
    val nonce: String? = null,
)