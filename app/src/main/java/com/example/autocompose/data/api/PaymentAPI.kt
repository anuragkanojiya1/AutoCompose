package com.example.autocompose.data.api

import com.example.autocompose.domain.model.UpdateSubscriptionRequest
import com.example.autocompose.domain.paymentResponseModels.CustomerModel
import com.example.autocompose.domain.paymentResponseModels.PayPalCaptureResponse
import com.example.autocompose.domain.paymentResponseModels.PayPalOrderResponse
import com.example.autocompose.domain.paymentResponseModels.PaymentIntentModel
import com.example.autocompose.domain.paymentResponseModels.TokenResponse
import com.example.autocompose.domain.responseModel.UpdateSubscriptionResponse
import com.example.autocompose.utils.Constants.SECRET_KEY
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentAPI {

//    @Headers("Authorization : Bearer $SECRET_KEY")
//    @POST("v1/customer")
//    suspend fun getCustomers() : Response<CustomerModel>
//
//    @Headers("Authorization : Bearer $SECRET_KEY",
//        "Stripe-Version: 2025-04-30.basil")
//    @POST("v1/ephemeral_keys")
//    suspend fun getEphemeralKey(
//        @Query("customer") customer : String
//    ) : Response<CustomerModel>
//
//    @Headers("Authorization : Bearer $SECRET_KEY")
//    @POST("v1/payment_intents")
//    suspend fun getPaymentIntent(
//        @Query("customer") customer : String,
//        @Query("amount") amount : String="100",
//        @Query("currency") currency : String="inr",
//        @Query("automatic_payment_methods[enabled]") automatePay : Boolean=true
//    ) : Response<PaymentIntentModel>

    @FormUrlEncoded
    @POST("v1/oauth2/token")
    suspend fun getAccessToken(
        @retrofit2.http.Header("Authorization") authHeader: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): Response<TokenResponse>
    
    @POST("v2/checkout/orders")
    suspend fun createOrder(
        @Body requestBody: RequestBody,
        @retrofit2.http.Header("Authorization") authHeader: String,
        @retrofit2.http.Header("PayPal-Request-Id") requestId: String
    ): Response<PayPalOrderResponse>
    
    @POST("v2/checkout/orders/{orderId}/capture")
    suspend fun captureOrder(
        @Path("orderId") orderId: String,
        @Body requestBody: RequestBody,
        @retrofit2.http.Header("Authorization") authHeader: String
    ): Response<PayPalCaptureResponse>
}