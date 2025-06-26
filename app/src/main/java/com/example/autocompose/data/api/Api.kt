package com.example.autocompose.data.api

import com.example.autocompose.domain.responseModel.AnalyticsResponse
import com.example.autocompose.domain.responseModel.BackendResponse
import com.example.autocompose.domain.model.Model
import com.example.autocompose.domain.model.UpdateSubscriptionRequest
import com.example.autocompose.domain.responseModel.SubscriptionResponse
import com.example.autocompose.domain.responseModel.UpdateSubscriptionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {

    @POST("/generate_email")
    @Headers("Content-Type: application/json")
    fun apiCall(
        @Body request: Model,
        @Header("Authorization") token: String
    ) : Call<BackendResponse>

    @GET("/trending")
    @Headers("Content-Type: application/json")
    fun getTrending() : Call<AnalyticsResponse>

    @POST("/update_subscription")
    @Headers("Content-Type: application/json")
    fun updateSubscription(
        @Body request: UpdateSubscriptionRequest,
        @Header("Authorization") token: String
    ) : Call<UpdateSubscriptionResponse>

    @GET("/check_subscription")
    @Headers("Content-Type: application/json")
    fun checkSubscription(
        @Header("Authorization") token: String
    ) : Call<SubscriptionResponse>

}