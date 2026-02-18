package com.example.autocompose.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocompose.data.database.AppDatabase
import com.example.autocompose.data.database.Entity
import com.example.autocompose.data.repository.EmailRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FrequentEmailViewModel @Inject constructor(
    private val repository: EmailRepository
) : ViewModel() {

    private val TAG = "FrequentEmailViewModel"

    private val _frequentEmails = MutableStateFlow<List<Entity>>(emptyList())
    val frequentEmails = _frequentEmails.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Entity>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()

    init {
        loadFrequentEmails()
    }

    fun loadFrequentEmails(limit: Int = 10) {
        viewModelScope.launch {
            try {
                _frequentEmails.value = repository.getMostFrequentEmails(limit)
            } catch (e: Exception) {
                Log.e(TAG, "Load failed", e)
                _frequentEmails.value = emptyList()
            }
        }
    }

    fun syncEmails() {
        viewModelScope.launch {
            repository.syncFromFirestoreToRoom()
            loadFrequentEmails()
        }
    }

    fun saveOrUpdateEmail(subject: String, emailBody: String) {
        viewModelScope.launch {
            repository.saveOrUpdateEmail(subject, emailBody)
            loadFrequentEmails()
        }
    }

    fun deleteEmail(id: Int) {
        viewModelScope.launch {
            val entity = _frequentEmails.value.firstOrNull { it.id == id } ?: return@launch
            repository.deleteById(entity)
            loadFrequentEmails()
        }
    }

    fun deleteEmailByContent(subject: String, body: String) {
        viewModelScope.launch {
            repository.deleteByContent(subject, body)
            loadFrequentEmails()
        }
    }

    fun clearAllFrequentEmails() {
        viewModelScope.launch {
            repository.clearAll()
            _frequentEmails.value = emptyList()
        }
    }

    fun searchEmails(query: String) {
        if (query.isBlank()) {
            clearSearch()
            return
        }

        _isSearchActive.value = true
        _searchResults.value = _frequentEmails.value.filter {
            it.subject.contains(query, true) ||
                    it.emailBody.contains(query, true)
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
        _isSearchActive.value = false
    }
}
