package com.example.autocompose.domain.model

import com.google.gson.annotations.SerializedName

data class Model(
    @SerializedName("tone")
    val tone: String,
    @SerializedName("ai_model")
    val ai_model: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("context")
    val context: String
)