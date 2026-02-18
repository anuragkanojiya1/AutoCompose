package com.example.autocompose.domain.responseModel

import com.google.gson.annotations.SerializedName

data class UpdateSubscriptionResponse(
    @SerializedName("status")
    val status: String
)
