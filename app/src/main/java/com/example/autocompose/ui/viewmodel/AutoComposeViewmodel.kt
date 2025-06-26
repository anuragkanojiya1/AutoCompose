package com.example.autocompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocompose.data.repository.Repository
import com.example.autocompose.domain.model.UpdateSubscriptionRequest
import com.example.autocompose.domain.responseModel.TopLanguage
import com.example.autocompose.domain.responseModel.TopModel
import com.example.autocompose.domain.responseModel.TopTone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AutoComposeViewmodel : ViewModel() {

    private val repository = Repository()

    private val _subject = MutableStateFlow<String>("")
    val subject: MutableStateFlow<String> = _subject

    private val _generatedEmail = MutableStateFlow<String>("")
    val generatedEmail: MutableStateFlow<String> = _generatedEmail
    
    private val _tone = MutableStateFlow<String>("")
    val tone: MutableStateFlow<String> = _tone

    private val _ai_model = MutableStateFlow<MutableList<String>>(mutableListOf())
    val ai_model: MutableStateFlow<MutableList<String>> = _ai_model

    private val _language = MutableStateFlow<String>("")
    val language: MutableStateFlow<String> = _language

    private val _topLanguages = MutableStateFlow<List<TopLanguage>>(emptyList())
    val topLanguages: MutableStateFlow<List<TopLanguage>> = _topLanguages

    private val _topModels = MutableStateFlow<List<TopModel>>(emptyList())
    val topModels: MutableStateFlow<List<TopModel>> = _topModels

    private val _topTones = MutableStateFlow<List<TopTone>>(emptyList())
    val topTones: MutableStateFlow<List<TopTone>> = _topTones

    private val _checkSubscription = MutableStateFlow<String>("free")
    val checkSubscription: MutableStateFlow<String> = _checkSubscription

    private val _updateSubscription = MutableStateFlow<String>("")
    val updateSubscription: MutableStateFlow<String> = _updateSubscription

    fun generateEmail(tone: String, ai_model: String, language: String, context: String, token: String) {
        viewModelScope.launch {
            try {
                val result = repository.generateEmail(tone, ai_model, language, context, token)
                
                result.fold(
                    onSuccess = { response ->
                        Log.d("AutoComposeViewmodel", "Email generated successfully: ${response.email.body}")
                        Log.d("AutoComposeViewmodel", "Email subject: ${response.email.subject}")
                        _subject.value = response.email.subject
                        _generatedEmail.value = response.email.body
                    },
                    onFailure = { exception ->
                        Log.e("AutoComposeViewmodel", "Error generating email: ${exception.localizedMessage}")
                        _generatedEmail.value = "Error: ${exception.message} \n Try Again"
                    }
                )
            } catch (e: Exception) {
                Log.e("AutoComposeViewmodel", "An error occurred: ${e.localizedMessage}")
                _generatedEmail.value = "Error: ${e.message}"
            }
        }
    }

    fun getTrends(maxRetries: Int = 3) {
        viewModelScope.launch {
            var currentAttempt = 0
            var success = false

            while (currentAttempt < maxRetries && !success) {
                currentAttempt++
                try {
                    Log.d(
                        "AutoComposeViewmodel",
                        "Fetching trends, attempt $currentAttempt of $maxRetries"
                    )
                    val result = repository.getTrends()

                    result.fold(
                        onSuccess = { response ->
                            Log.d("AutoComposeViewmodel", "Trends fetched successfully")
                            Log.d(
                                "LanguageDebug",
                                "Raw languages data from API: ${response.top_languages}"
                            )
                            _topLanguages.value = response.top_languages
                            _topModels.value = response.top_models
                            _topTones.value = response.top_tones
                            success = true
                        },
                        onFailure = { exception ->
                            Log.e(
                                "AutoComposeViewmodel",
                                "Error fetching trends (attempt $currentAttempt): ${exception.localizedMessage}"
                            )
                            if (currentAttempt < maxRetries) {
                                kotlinx.coroutines.delay(1000) // Wait 1 second before retrying
                            }
                        }
                    )
                } catch (e: Exception) {
                    Log.e(
                        "AutoComposeViewmodel",
                        "Exception while fetching trends (attempt $currentAttempt): ${e.localizedMessage}"
                    )
                    if (currentAttempt < maxRetries) {
                        kotlinx.coroutines.delay(1000) // Wait 1 second before retrying
                    }
                }
            }

            if (!success) {
                Log.e("AutoComposeViewmodel", "Failed to fetch trends after $maxRetries attempts")
            }
        }
    }

    fun checkSubscription(token: String) {
        viewModelScope.launch {
            try {
                val result = repository.checkSubscription(token)

                Log.d("Token test c", token)
                result.fold(
                    onSuccess = { response ->
                        Log.d("AutoComposeViewmodel", "Subscription checked successfully ${response.subscription}")
                        _checkSubscription.value = response.subscription
                    },
                    onFailure = { exception ->
                        Log.e(
                            "AutoCompose",
                            "Error checking subscription: ${exception.localizedMessage}"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e(
                    "AutoComposeViewmodel",
                    "An error occurred while checking subscription: ${e.localizedMessage}"
                )
            }
        }
    }

    fun updateSubscription(subscription: UpdateSubscriptionRequest, token: String){
        viewModelScope.launch {
            try {
                val result = repository.updateSubscription(subscription, token)

                Log.d("Token test u", token)

                result.fold(
                    onSuccess = { response ->
                        Log.d("AutoComposeViewmodel", "Subscription checked successfully")
                        _updateSubscription.value = response.status
                    },
                    onFailure = { exception ->
                        Log.e(
                            "AutoCompose",
                            "Error checking subscription: ${exception.localizedMessage}"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e(
                    "AutoComposeViewmodel",
                    "An error occurred while checking subscription: ${e.localizedMessage}"
                )
            }
        }
    }

}
