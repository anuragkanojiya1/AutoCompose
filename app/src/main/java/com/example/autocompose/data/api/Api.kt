package com.example.autocompose.data.api

import com.example.autocompose.domain.model.AnalyticsResponse
import com.example.autocompose.domain.model.BackendResponse
import com.example.autocompose.domain.model.Model
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {

    @POST("/generate_email")
    @Headers("Content-Type: application/json")
    fun apiCall(
        @Body request: Model
    ) : Call<BackendResponse>

    @GET("/trending")
    @Headers("Content-Type: application/json")
    fun getTrending() : Call<AnalyticsResponse>
}