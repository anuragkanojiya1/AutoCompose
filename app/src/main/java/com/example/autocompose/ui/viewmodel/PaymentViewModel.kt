package com.example.autocompose.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocompose.data.api.PaymentAPI
import com.example.autocompose.domain.paymentResponseModels.PayPalCaptureResponse
import com.example.autocompose.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

data class PaymentState(
    val isLoading: Boolean = false,
    val orderId: String? = null,
    val approvalUrl: String? = null,       // approval link to open in browser
    val isSuccess: Boolean = false,
    val error: String? = null,
    val accessToken: String? = null,
    val requestId: String? = null,
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentApi: PaymentAPI
) : ViewModel() {

    private val clientId = Constants.PUBLISHABLE_KEY
    private val secretKey = Constants.SECRET_KEY
    private val returnUrl = "com.example.autocompose://paypalpay"
    private val TAG = "PaymentViewModel"

    private val _paymentState = MutableStateFlow(PaymentState())
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    // Public wrapper to fetch token (keeps old call sites working)
    fun fetchAccessToken() {
        viewModelScope.launch { ensureAccessToken() }
    }

    // Ensures we have a token. Returns true if token is present/obtained.
    private suspend fun ensureAccessToken(): Boolean = withContext(Dispatchers.IO) {
        if (_paymentState.value.accessToken != null) {
            Log.d(TAG, "Access token already present")
            return@withContext true
        }

        try {
            _paymentState.update { it.copy(isLoading = true, error = null) }

            val authHeader = "Basic " + android.util.Base64.encodeToString(
                "$clientId:$secretKey".toByteArray(),
                android.util.Base64.NO_WRAP
            )

            Log.d(TAG, "Making token request to PayPal API")
            val response = paymentApi.getAccessToken(authHeader)

            Log.d(TAG, "Raw token response = ${response.raw()}")

            return@withContext if (response.isSuccessful) {
                val tokenResponse = try {
                    response.body()
                } catch (e: ClassCastException) {
                    // Defensive fallback: parse JSON string if converter fails
                    Log.w(TAG, "ClassCastException while parsing token body, trying manual parse", e)
                    val raw = response.errorBody()?.string() ?: response.raw().toString()
                    try {
                        val jo = JSONObject(raw)
                        val token = jo.optString("access_token", null)
                        token?.let { object {
                            val access_token = it
                        } } // not used directly, we'll set manually below
                    } catch (pe: Exception) {
                        null
                    }
                }

                // If body is parsed by converter:
                val accessToken = tokenResponse?.let {
                    // try reflection-like safe extraction (common field name)
                    val field = try { tokenResponse::class.java.getDeclaredField("access_token") } catch (_: Exception) { null }
                    field?.let {
                        it.isAccessible = true
                        it.get(tokenResponse) as? String
                    } ?: run {
                        // fallback: try property named accessToken
                        try {
                            val f = tokenResponse::class.java.getDeclaredField("accessToken")
                            f.isAccessible = true
                            f.get(tokenResponse) as? String
                        } catch (_: Exception) {
                            null
                        }
                    }
                }

                // final fallback: try reading JSON body direct (if converter failed)
                val finalToken = accessToken ?: run {
                    try {
                        val rawBody = response.body()?.toString()
                        // can't rely on body().toString(); try errorBody
                        val err = response.errorBody()?.string()
                        null
                    } catch (_: Exception) { null }
                }

                // Prefer token from body when available; else try reading known response structure
                val tokenToUse = accessToken ?: tokenResponse?.let {
                    // If we failed to extract programmatically, try json parse of response.body() string
                    null
                }

                // If still null, try a second attempt to read the response again as JSON (best-effort)
                var finalTokenValue: String? = null
                if (tokenToUse != null) finalTokenValue = tokenToUse
                else {
                    try {
                        val rawText = response.body()?.let { it.toString() } ?: response.errorBody()?.string()
                        if (!rawText.isNullOrBlank()) {
                            val jo = JSONObject(rawText)
                            finalTokenValue = jo.optString("access_token", null)
                        }
                    } catch (_: Exception) {
                        finalTokenValue = null
                    }
                }

                if (!finalTokenValue.isNullOrBlank()) {
                    Log.d(TAG, "Token fetched successfully")
                    _paymentState.update { it.copy(isLoading = false, accessToken = finalTokenValue) }
                    true
                } else {
                    Log.e(TAG, "Token response body is null or malformed")
                    _paymentState.update {
                        it.copy(isLoading = false, error = "Failed to get access token: Response body is null or malformed")
                    }
                    false
                }
            } else {
                val err = response.errorBody()?.string() ?: "No error body"
                Log.e(TAG, "Failed to get access token: ${response.code()} ${response.message()} - $err")
                _paymentState.update {
                    it.copy(isLoading = false, error = "Failed to get access token: ${response.message()}")
                }
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching access token", e)
            _paymentState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            false
        }
    }

    // Create a PayPal order. This will ensure a token exists first, then create order and extract approval link.
    fun createOrder(amount: String, onApprovalUrlReady: (String) -> Unit = {}) {
        viewModelScope.launch {
            Log.d(TAG, "Creating PayPal order for amount: $amount USD")

            val tokenOk = ensureAccessToken()
            if (!tokenOk) {
                Log.e(TAG, "Cannot create order: Failed to obtain access token")
                _paymentState.update { it.copy(error = "Failed to obtain access token") }
                return@launch
            }

            _paymentState.update { it.copy(isLoading = true, error = null) }
            val uniqueRequestId = UUID.randomUUID().toString()
            Log.d(TAG, "Generated unique request ID: $uniqueRequestId")

            try {
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

                Log.d(TAG, "Order payload prepared: ${jsonObject.toString().take(120)}...")

                val authHeader = "Bearer ${_paymentState.value.accessToken}"
                val response = paymentApi.createOrder(
                    jsonObject.toString().toRequestBody("application/json".toMediaType()),
                    authHeader,
                    uniqueRequestId
                )

                if (response.isSuccessful) {
                    val orderResponse = response.body()
                    // Defensive: try to extract order id and approval link
                    var orderId: String? = null
                    var approvalUrl: String? = null

                    try {
                        if (orderResponse != null) {
                            orderId = orderResponse.id
                            orderResponse.links.forEach { link ->
                                if (link.rel.equals("approve", true) || link.rel.equals("payer-action", true)) {
                                    approvalUrl = link.href
                                }
                            }
                        } else {
                            // If converter returned null (rare), parse raw response
                            val raw = response.errorBody()?.string() ?: response.raw().toString()
                            val jo = JSONObject(raw)
                            orderId = jo.optString("id", null)
                            val links = jo.optJSONArray("links")
                            if (links != null) {
                                for (i in 0 until links.length()) {
                                    val l = links.getJSONObject(i)
                                    val rel = l.optString("rel")
                                    if (rel.equals("approve", true) || rel.equals("payer-action", true)) {
                                        approvalUrl = l.optString("href", null)
                                        break
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to extract order details via converter; trying manual parse", e)
                        try {
                            val raw = response.errorBody()?.string()
                            if (!raw.isNullOrBlank()) {
                                val jo = JSONObject(raw)
                                orderId = jo.optString("id", null)
                                val links = jo.optJSONArray("links")
                                if (links != null) {
                                    for (i in 0 until links.length()) {
                                        val l = links.getJSONObject(i)
                                        val rel = l.optString("rel")
                                        if (rel.equals("approve", true) || rel.equals("payer-action", true)) {
                                            approvalUrl = l.optString("href", null)
                                            break
                                        }
                                    }
                                }
                            }
                        } catch (_: Exception) { /* ignore */ }
                    }

                    if (orderId != null) {
                        Log.d(TAG, "Order created: $orderId (approvalUrl=${approvalUrl?.take(60)})")
                        _paymentState.update {
                            it.copy(
                                isLoading = false,
                                orderId = orderId,
                                approvalUrl = approvalUrl,
                                requestId = uniqueRequestId
                            )
                        }
                        approvalUrl?.let { onApprovalUrlReady(it) } ?: onApprovalUrlReady("https://www.paypal.com/checkoutnow?token=$orderId")
                    } else {
                        Log.e(TAG, "Order created but failed to parse order id")
                        val err = response.errorBody()?.string() ?: "No error body"
                        _paymentState.update { it.copy(isLoading = false, error = "Failed to create order: parse error") }
                    }
                } else {
                    val err = response.errorBody()?.string() ?: "No error body"
                    Log.e(TAG, "Failed to create order: ${response.code()} ${response.message()} - $err")
                    _paymentState.update { it.copy(isLoading = false, error = "Failed to create order: ${response.message()}") }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error creating order", e)
                _paymentState.update { it.copy(isLoading = false, error = "Network error: ${e.message}") }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing order", e)
                _paymentState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    // Capture the payment after approval
    fun captureOrder(orderId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Attempting to capture payment for order: $orderId")

            val tokenOk = ensureAccessToken()
            if (!tokenOk) {
                Log.e(TAG, "Cannot capture payment: no access token")
                _paymentState.update { it.copy(error = "No access token available. Please try again.") }
                return@launch
            }

            _paymentState.update { it.copy(isLoading = true, error = null) }

            try {
                val authHeader = "Bearer ${_paymentState.value.accessToken}"
                Log.d(TAG, "Sending capture request to PayPal API")
                val response = paymentApi.captureOrder(
                    orderId,
                    "".toRequestBody("application/json".toMediaType()),
                    authHeader
                )

                if (response.isSuccessful) {
                    val captureResponse = response.body()
                    if (captureResponse != null) {
                        Log.d(TAG, "Payment captured successfully with status: ${captureResponse.status}")
                        logTransactionDetails(captureResponse)
                        _paymentState.update { it.copy(isLoading = false, isSuccess = true) }
                    } else {
                        Log.e(TAG, "Capture response body is null")
                        _paymentState.update { it.copy(isLoading = false, error = "Failed to capture payment: Response body is null") }
                    }
                } else {
                    val errorResponse = response.errorBody()?.string() ?: "No error body"
                    Log.e(TAG, "Failed to capture payment: ${response.code()} ${response.message()} - $errorResponse")
                    _paymentState.update { it.copy(isLoading = false, error = "Failed to capture payment: ${response.message()}") }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error capturing payment", e)
                _paymentState.update { it.copy(isLoading = false, error = "Network error: ${e.message}") }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing capture", e)
                _paymentState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    // Handle payment failure
    fun handlePaymentFailure(errorMessage: String) {
        Log.e(TAG, "Payment failure: $errorMessage")
        _paymentState.update { it.copy(isLoading = false, error = errorMessage) }
    }

    // Handle payment cancellation
    fun handlePaymentCancellation() {
        Log.d(TAG, "Payment cancelled by user")
        _paymentState.update { it.copy(isLoading = false, orderId = null, approvalUrl = null) }
    }

    // Handle intent from PayPal - pass the whole URI; viewModel handles token/PayerID or opType
    fun handlePayPalResult(opType: String?, uri: Uri? = null) {
        Log.d(TAG, "Received PayPal callback with opType: $opType, uri=$uri")

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

        if (uri != null) {
            val token = uri.getQueryParameter("token")
            val payerId = uri.getQueryParameter("PayerID") ?: uri.getQueryParameter("PayerId") // some variants
            if (!token.isNullOrBlank()) {
                Log.d(TAG, "Detected PayPal approval with token=$token, payerId=$payerId")
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
