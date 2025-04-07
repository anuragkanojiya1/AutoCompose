package com.example.autocompose.data.repository

import com.example.autocompose.data.api.ApiInstance
import com.example.autocompose.data.database.AppDatabase
import com.example.autocompose.domain.model.BackendResponse
import com.example.autocompose.domain.model.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {
    
    private val api = ApiInstance.api
    
    suspend fun generateEmail(
        tone: String,
        aiModel: String,
        language: String,
        context: String
    ): Result<BackendResponse> {
        return try {
            val request = Model(
                tone = tone,
                ai_model = aiModel,
                language = language,
                context = context
            )
            
            val response = withContext(Dispatchers.IO) {
                api.apiCall(request).execute()
            }
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.errorBody()?.string() ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}