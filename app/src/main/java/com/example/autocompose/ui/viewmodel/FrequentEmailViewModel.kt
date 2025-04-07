package com.example.autocompose.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocompose.data.database.AppDatabase
import com.example.autocompose.data.database.Entity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FrequentEmailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val TAG = "FrequentEmailViewModel"
    private val database = AppDatabase.getDataBase(application)
    private val emailDao = database.entityDao()
    
    private val _frequentEmails = MutableStateFlow<List<Entity>>(emptyList())
    val frequentEmails: StateFlow<List<Entity>> = _frequentEmails.asStateFlow()
    
    init {
        Log.d(TAG, "Initializing FrequentEmailViewModel")
        loadFrequentEmails()
    }
    
    fun loadFrequentEmails(limit: Int = 10) {
        Log.d(TAG, "Loading frequent emails with limit: $limit")
        viewModelScope.launch {
            try {
                val emails = emailDao.getMostFrequentEmails(limit)
                Log.d(TAG, "Loaded ${emails.size} frequent emails")
                if (emails.isNotEmpty()) {
                    Log.d(TAG, "First email: ${emails[0].subject}, frequency: ${emails[0].frequency}")
                }
                _frequentEmails.value = emails
            } catch (e: Exception) {
                Log.e(TAG, "Error loading frequent emails", e)
                _frequentEmails.value = emptyList()
            }
        }
    }
    
    fun saveOrUpdateEmail(subject: String, emailBody: String) {
        Log.d(TAG, "Saving or updating email with subject: $subject")
        viewModelScope.launch {
            try {
                val existingEmail = emailDao.findEmail(subject, emailBody)
                
                if (existingEmail != null) {
                    // If email exists, increment its frequency
                    Log.d(TAG, "Email exists with ID: ${existingEmail.id}, current frequency: ${existingEmail.frequency}")
                    emailDao.incrementFrequency(existingEmail.id)
                    Log.d(TAG, "Incremented frequency for email ID: ${existingEmail.id}")
                } else {
                    // If email doesn't exist, insert it with frequency 1
                    Log.d(TAG, "Email does not exist, creating new entry with frequency 1")
                    val newEmail = Entity(
                        subject = subject,
                        emailBody = emailBody,
                        frequency = 1
                    )
                    emailDao.insertEmails(newEmail)
                    Log.d(TAG, "Inserted new email")
                }
                
                // Refresh the frequent emails list
                loadFrequentEmails()
            } catch (e: Exception) {
                Log.e(TAG, "Error saving or updating email", e)
            }
        }
    }
    
    fun clearAllFrequentEmails() {
        Log.d(TAG, "Clearing all frequent emails")
        viewModelScope.launch {
            try {
                emailDao.deleteAllEmails()
                _frequentEmails.value = emptyList()
                Log.d(TAG, "All emails deleted successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing emails", e)
            }
        }
    }
}