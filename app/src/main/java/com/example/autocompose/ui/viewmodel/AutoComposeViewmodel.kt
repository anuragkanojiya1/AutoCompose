package com.example.autocompose.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.autocompose.data.database.AppDatabase
import com.example.autocompose.data.repository.Repository
import com.example.autocompose.domain.model.BackendResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AutoComposeViewmodel: ViewModel() {

    private val repository = Repository()

    private val _subject = MutableStateFlow<String>("")
    val subject: MutableStateFlow<String> = _subject

    private val _generatedEmail = MutableStateFlow<String>("")
    val generatedEmail: MutableStateFlow<String> = _generatedEmail

    fun generateEmail(tone: String, ai_model: String, language: String, context: String) {
        viewModelScope.launch{
            try {
                val result = repository.generateEmail(tone, ai_model, language, context)
                
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

}