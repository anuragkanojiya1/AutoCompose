package com.example.autocompose.domain.paymentResponseModels

import com.google.gson.annotations.SerializedName

data class CustomerModel(
    @SerializedName("id")
    val id: String,
)