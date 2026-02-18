package com.example.autocompose.domain.paymentResponseModels

data class PayPalCaptureResponse(
    val id: String,
    val status: String,
    val purchase_units: List<PurchaseUnit>,
    val payer: Payer,
    val links: List<LinkDescription>,
)

data class PurchaseUnit(
    val reference_id: String,
    val amount: Amount,
    val payments: Payments,
)

data class Amount(
    val currency_code: String,
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