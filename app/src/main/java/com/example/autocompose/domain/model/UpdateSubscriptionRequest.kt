package com.example.autocompose.domain.model

import com.google.gson.annotations.SerializedName

data class UpdateSubscriptionRequest(
    @SerializedName("subscription")
    val subscription: String
)