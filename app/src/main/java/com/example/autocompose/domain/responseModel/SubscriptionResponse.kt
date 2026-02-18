package com.example.autocompose.domain.responseModel

import com.google.gson.annotations.SerializedName

data class SubscriptionResponse(
    @SerializedName("subscription")
    val subscription: String
)