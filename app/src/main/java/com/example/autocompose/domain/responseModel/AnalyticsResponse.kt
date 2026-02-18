package com.example.autocompose.domain.responseModel

import com.google.gson.annotations.SerializedName

data class AnalyticsResponse(
    @SerializedName("top_languages")
    val top_languages: List<TopLanguage>,
    @SerializedName("top_models")
    val top_models: List<TopModel>,
    @SerializedName("top_tones")
    val top_tones: List<TopTone>
)
