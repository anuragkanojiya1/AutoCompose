package com.example.autocompose.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object PaymentApiInstance {

    private val TAG = "PaymentAPI"

    private fun apiInstance(isSandbox: Boolean = true): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val customInterceptor = Interceptor { chain ->
            val request = chain.request()
            val requestId = request.header("PayPal-Request-Id")

            Log.d(TAG, "→ ${request.method} ${request.url} ${requestId?.let { "ID: $it" } ?: ""}")

            try {
                val response = chain.proceed(request)
                Log.d(TAG, "← ${response.code} ${response.message} for ${request.url}")
                response
            } catch (e: Exception) {
                Log.e(TAG, "! Network error for ${request.url}: ${e.message}")
                throw e
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(customInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val baseUrl = if (isSandbox) {
            "https://api-m.sandbox.paypal.com/"
        } else {
            "https://api-m.paypal.com/"
        }

        Log.d(TAG, "Initializing PayPal API client with baseUrl: $baseUrl")

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: PaymentAPI = apiInstance().create(PaymentAPI::class.java)
}