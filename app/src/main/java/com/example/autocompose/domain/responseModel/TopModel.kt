package com.example.autocompose.domain.responseModel

import com.google.gson.annotations.SerializedName

data class TopModel(
    @SerializedName("count")
    val count: Int,
    @SerializedName("model")
    val model: String
)