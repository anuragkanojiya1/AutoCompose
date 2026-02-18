package com.example.autocompose.domain.paymentResponseModels

import com.google.gson.annotations.SerializedName

data class PayPalOrderResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("links")
    val links: List<LinkDescription>,
)

data class LinkDescription(
    @SerializedName("href")
    val href: String,
    @SerializedName("rel")
    val rel: String,
    @SerializedName("method")
    val method: String,
)