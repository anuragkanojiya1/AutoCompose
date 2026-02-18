package com.example.autocompose.domain.responseModel

import com.google.gson.annotations.SerializedName

data class TopLanguage(
    @SerializedName("count")
    val count: Int,
    @SerializedName("language")
    val language: String
)