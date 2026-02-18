package com.example.autocompose.data.api

import android.util.Log
import com.example.autocompose.di.PayPalOkHttp
import com.example.autocompose.di.PayPalRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentNetworkModule {

    private val TAG = "PaymentAPI"

    @Provides
    @Singleton
    @PayPalOkHttp
    fun providePayPalOkHttpClient(): OkHttpClient {
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
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(customInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }
    @Provides
    @Singleton
    @PayPalRetrofit
    fun providePayPalRetrofit(
       @PayPalOkHttp client: OkHttpClient
    ): Retrofit {

        val baseUrl =
//            if (BuildConfig.DEBUG) {
//            "https://api-m.sandbox.paypal.com/"
//        } else {
            "https://api-m.paypal.com/"
//        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePaymentApi(
       @PayPalRetrofit retrofit: Retrofit
    ): PaymentAPI {
        return retrofit.create(PaymentAPI::class.java)
    }
}