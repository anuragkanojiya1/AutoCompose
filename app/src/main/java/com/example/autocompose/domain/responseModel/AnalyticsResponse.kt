package com.example.autocompose.domain.responseModel

data class AnalyticsResponse(
    val top_languages: List<TopLanguage>,
    val top_models: List<TopModel>,
    val top_tones: List<TopTone>
)