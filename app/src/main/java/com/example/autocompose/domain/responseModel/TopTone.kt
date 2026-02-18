package com.example.autocompose.domain.responseModel

import com.google.gson.annotations.SerializedName

data class TopTone(
    @SerializedName("count")
    val count: Int,
    @SerializedName("tone")
    val tone: String
)