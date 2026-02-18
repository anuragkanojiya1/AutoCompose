package com.example.autocompose.domain.paymentResponseModels

import com.google.gson.annotations.SerializedName

data class PaymentIntentModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("clientSecret")
    val clientSecret: String
)