package com.example.autocompose.domain.paymentResponseModels

import com.google.gson.annotations.SerializedName

data class PayPalCaptureResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("purchase_units")
    val purchase_units: List<PurchaseUnit>,
    @SerializedName("payer")
    val payer: Payer,
    @SerializedName("links")
    val links: List<LinkDescription>,
)

data class PurchaseUnit(
    @SerializedName("reference_id")
    val reference_id: String,
    @SerializedName("amount")
    val amount: Amount,
    @SerializedName("payments")
    val payments: Payments,
)

data class Amount(
    @SerializedName("currency_code")
    val currency_code: String,
    @SerializedName("value")
    val value: String,
)

data class Payments(
    val captures: List<Capture>,
)

data class Capture(
    val id: String,
    val status: String,
    val amount: Amount,
)

data class Payer(
    val name: PayerName,
    val email_address: String,
    val payer_id: String,
)

data class PayerName(
    val given_name: String,
    val surname: String,
)