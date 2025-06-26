package com.example.autocompose.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocompose.data.api.PaymentApiInstance
import com.example.autocompose.domain.paymentResponseModels.PayPalCaptureResponse
import com.example.autocompose.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

data class PaymentState(
    val isLoading: Boolean = false,
    val orderId: String? = null,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val accessToken: String? = null,
    val requestId: String? = null,
)

class PaymentViewModel : ViewModel() {

    // Use the existing constant from Constants object
    val clientId = Constants.PUBLISHABLE_KEY
    private val secretKey = Constants.SECRET_KEY
    private val returnUrl = "com.example.autocompose://paypalpay"
    private val TAG = "PaymentViewModel"

    private val _paymentState = MutableStateFlow(PaymentState())
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    // Fetch access token first
    fun fetchAccessToken() {
        Log.d(TAG, "Fetching PayPal access token...")
        _paymentState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Create auth header for OAuth
                val authHeader = "Basic " + android.util.Base64.encodeToString(
                    "$clientId:$secretKey".toByteArray(),
                    android.util.Base64.NO_WRAP
                )
                Log.d(TAG, "Making token request to PayPal API")

                val response = PaymentApiInstance.api.getAccessToken(authHeader)

                if (response.isSuccessful) {
                    val tokenResponse = response.body()
                    if (tokenResponse != null) {
                        Log.d(
                            TAG,
                            "Token fetched successfully: ${tokenResponse.access_token.take(5)}..."
                        )
                        _paymentState.update { 
                            it.copy(
                                isLoading = false, 
                                accessToken = tokenResponse.access_token
                            )
                        }
                    } else {
                        Log.e(TAG, "Token response body is null")
                        _paymentState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to get access token: Response body is null"
                            )
                        }
                    }
                } else {
                    val errorResponse = response.errorBody()?.string() ?: "No error body"
                    Log.e(
                        TAG,
                        "Failed to get access token: ${response.code()} ${response.message()} - $errorResponse"
                    )
                    _paymentState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to get access token: ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching access token", e)
                _paymentState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    // Create a PayPal order with improved payload
    fun createOrder(amount: String, onOrderCreated: (String) -> Unit = {}) {
        Log.d(TAG, "Creating PayPal order for amount: $amount USD")
        // Check if we have an access token first
        if (_paymentState.value.accessToken == null) {
            Log.e(TAG, "Cannot create order: No access token available")
            _paymentState.update {
                it.copy(error = "No access token available. Please try again.")
            }
            return
        }

        _paymentState.update { it.copy(isLoading = true, error = null) }
        val uniqueRequestId = UUID.randomUUID().toString()
        Log.d(TAG, "Generated unique request ID: $uniqueRequestId")

        viewModelScope.launch {
            try {
                // Create JSON payload for order creation with improved parameters
                val jsonObject = JSONObject().apply {
                    put("intent", "CAPTURE")
                    put("purchase_units", JSONArray().apply {
                        put(JSONObject().apply {
                            put("reference_id", uniqueRequestId)
                            put("amount", JSONObject().apply {
                                put("currency_code", "USD")
                                put("value", amount)
                            })
                        })
                    })
                    put("payment_source", JSONObject().apply {
                        put("paypal", JSONObject().apply {
                            put("experience_context", JSONObject().apply {
                                put("payment_method_preference", "IMMEDIATE_PAYMENT_REQUIRED")
                                put("brand_name", "AutoCompose")
                                put("locale", "en-US")
                                put("landing_page", "LOGIN")
                                put("shipping_preference", "NO_SHIPPING")
                                put("user_action", "PAY_NOW")
                                put("return_url", returnUrl)
                                put("cancel_url", "$returnUrl?opType=cancel")
                            })
                        })
                    })
                }

                Log.d(TAG, "Order payload prepared: ${jsonObject.toString().take(100)}...")

                // Bearer token format
                val authHeader = "Bearer ${_paymentState.value.accessToken}"

                // Use PaymentApiInstance to make the API call
                Log.d(TAG, "Sending create order request to PayPal API")
                val response = PaymentApiInstance.api.createOrder(
                    jsonObject.toString().toRequestBody("application/json".toMediaType()),
                    authHeader,
                    uniqueRequestId
                )

                if (response.isSuccessful) {
                    val orderResponse = response.body()
                    if (orderResponse != null) {
                        val orderId = orderResponse.id
                        Log.d(
                            TAG,
                            "Order created successfully: $orderId with status: ${orderResponse.status}"
                        )

                        // Log links received from PayPal
                        orderResponse.links.forEach { link ->
                            Log.d(TAG, "Link: ${link.rel} - ${link.method} - ${link.href}")
                        }

                        _paymentState.update { 
                            it.copy(
                                isLoading = false, 
                                orderId = orderId,
                                requestId = uniqueRequestId
                            ) 
                        }
                        
                        onOrderCreated(orderId)
                    } else {
                        Log.e(TAG, "Response body is null")
                        _paymentState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to create order: Response body is null"
                            )
                        }
                    }
                } else {
                    val errorResponse = response.errorBody()?.string() ?: "No error body"
                    Log.e(
                        TAG,
                        "Failed to create order: ${response.code()} ${response.message()} - $errorResponse"
                    )
                    _paymentState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to create order: ${response.message()}"
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error creating order", e)
                _paymentState.update {
                    it.copy(
                        isLoading = false,
                        error = "Network error: ${e.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing order", e)
                _paymentState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    // Capture the payment after approval
    fun captureOrder(orderId: String) {
        Log.d(TAG, "Attempting to capture payment for order: $orderId")
        // Check if we have an access token
        if (_paymentState.value.accessToken == null) {
            Log.e(TAG, "Cannot capture payment: No access token available")
            _paymentState.update {
                it.copy(error = "No access token available. Please try again.")
            }
            return
        }

        _paymentState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Bearer token format
                val authHeader = "Bearer ${_paymentState.value.accessToken}"

                // Use PaymentApiInstance to make the API call
                Log.d(TAG, "Sending capture request to PayPal API")
                val response = PaymentApiInstance.api.captureOrder(
                    orderId,
                    "".toRequestBody("application/json".toMediaType()),
                    authHeader
                )

                if (response.isSuccessful) {
                    val captureResponse = response.body()
                    if (captureResponse != null) {
                        Log.d(
                            TAG,
                            "Payment captured successfully with status: ${captureResponse.status}"
                        )
                        Log.d(
                            TAG,
                            "Payer info: ${captureResponse.payer.name.given_name} ${captureResponse.payer.name.surname}, Email: ${captureResponse.payer.email_address}"
                        )

                        captureResponse.purchase_units.forEach { unit ->
                            unit.payments.captures.forEach { capture ->
                                Log.d(
                                    TAG,
                                    "Capture ID: ${capture.id}, Status: ${capture.status}, Amount: ${capture.amount.value} ${capture.amount.currency_code}"
                                )
                            }
                        }

                        logTransactionDetails(captureResponse)

                        _paymentState.update { it.copy(isLoading = false, isSuccess = true) }
                    } else {
                        Log.e(TAG, "Capture response body is null")
                        _paymentState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to capture payment: Response body is null"
                            )
                        }
                    }
                } else {
                    val errorResponse = response.errorBody()?.string() ?: "No error body"
                    Log.e(
                        TAG,
                        "Failed to capture payment: ${response.code()} ${response.message()} - $errorResponse"
                    )
                    _paymentState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to capture payment: ${response.message()}"
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error capturing payment", e)
                _paymentState.update {
                    it.copy(
                        isLoading = false,
                        error = "Network error: ${e.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing capture", e)
                _paymentState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    // Handle payment failure
    fun handlePaymentFailure(errorMessage: String) {
        Log.e(TAG, "Payment failure: $errorMessage")
        _paymentState.update {
            it.copy(
                isLoading = false,
                error = errorMessage
            )
        }
    }

    // Handle payment cancellation
    fun handlePaymentCancellation() {
        Log.d(TAG, "Payment cancelled by user")
        _paymentState.update {
            it.copy(
                isLoading = false,
                orderId = null
            )
        }
    }

    // Handle intent from PayPal
    fun handlePayPalResult(opType: String?, uri: Uri? = null) {
        Log.d(TAG, "Received PayPal callback with opType: $opType")

        // Handle explicit operation types first
        when (opType) {
            "payment" -> {
                _paymentState.value.orderId?.let { orderId ->
                    Log.d(TAG, "Processing payment confirmation for order: $orderId")
                    captureOrder(orderId)
                } ?: Log.e(TAG, "Cannot capture payment: No order ID available")
                return
            }
            "cancel" -> {
                Log.d(TAG, "Payment cancelled via PayPal callback")
                handlePaymentCancellation()
                return
            }
        }

        // If no explicit opType, check for PayPal standard redirect parameters
        if (uri != null) {
            val token = uri.getQueryParameter("token")
            val payerId = uri.getQueryParameter("PayerID")

            if (token != null && payerId != null) {
                // This is a successful payment approval
                Log.d(
                    TAG,
                    "Detected successful PayPal payment approval with token: $token and PayerID: $payerId"
                )
                captureOrder(token)
                return
            }
        }

        Log.w(TAG, "Unknown PayPal operation type or insufficient parameters")
    }

    fun resetState() {
        Log.d(TAG, "Resetting payment state")
        _paymentState.value = PaymentState()
    }

    private fun logTransactionDetails(response: PayPalCaptureResponse) {
        val sb = StringBuilder()
        sb.appendLine("==================== TRANSACTION COMPLETE ====================")
        sb.appendLine("Transaction ID: ${response.id}")
        sb.appendLine("Status: ${response.status}")
        sb.appendLine("---")
        sb.appendLine("CUSTOMER DETAILS:")
        sb.appendLine("Name: ${response.payer.name.given_name} ${response.payer.name.surname}")
        sb.appendLine("Email: ${response.payer.email_address}")
        sb.appendLine("PayPal ID: ${response.payer.payer_id}")
        sb.appendLine("---")

        response.purchase_units.forEach { unit ->
            sb.appendLine("PURCHASE DETAILS:")
            sb.appendLine("Reference ID: ${unit.reference_id}")

            unit.payments.captures.forEach { capture ->
                sb.appendLine("Capture ID: ${capture.id}")
                sb.appendLine("Capture Status: ${capture.status}")
                sb.appendLine("Amount: ${capture.amount.value} ${capture.amount.currency_code}")
            }
        }
        sb.appendLine("=============================================================")

        Log.i(TAG, sb.toString())
    }
}