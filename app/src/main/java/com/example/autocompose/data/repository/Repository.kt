package com.example.autocompose.data.repository

import com.example.autocompose.data.api.ApiInstance
import com.example.autocompose.data.api.PaymentApiInstance
import com.example.autocompose.domain.responseModel.AnalyticsResponse
import com.example.autocompose.domain.responseModel.BackendResponse
import com.example.autocompose.domain.model.Model
import com.example.autocompose.domain.model.UpdateSubscriptionRequest
import com.example.autocompose.domain.responseModel.SubscriptionResponse
import com.example.autocompose.domain.responseModel.UpdateSubscriptionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {
    
    private val api = ApiInstance.api
    private val paymentApi = PaymentApiInstance.api
    
    suspend fun generateEmail(
        tone: String,
        aiModel: String,
        language: String,
        context: String,
        token: String
    ): Result<BackendResponse> {
        return try {
            val request = Model(
                tone = tone,
                ai_model = aiModel,
                language = language,
                context = context
            )
            
            val response = withContext(Dispatchers.IO) {
                api.apiCall(request, "Bearer ${token}").execute()
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

    suspend fun getTrends() : Result<AnalyticsResponse> {
        return try {
            val response = withContext(Dispatchers.IO) {
                api.getTrending().execute()
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

    suspend fun checkSubscription(token: String): Result<SubscriptionResponse>{

        return try {
            val response = withContext(Dispatchers.IO) {
                api.checkSubscription(token).execute()
            }

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Check Subscription Error: ${response.errorBody()?.string() ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSubscription(subscription: UpdateSubscriptionRequest, token: String): Result<UpdateSubscriptionResponse> {

        return try {
            val response = withContext(Dispatchers.IO) {
                api.updateSubscription(subscription, token).execute()
            }

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(" Update Subscription Error: ${response.errorBody()?.string() ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
