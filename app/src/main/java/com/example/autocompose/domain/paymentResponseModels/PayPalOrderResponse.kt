package com.example.autocompose.domain.paymentResponseModels

data class PayPalOrderResponse(
    val id: String,
    val status: String,
    val links: List<LinkDescription>,
)

data class LinkDescription(
    val href: String,
    val rel: String,
    val method: String,
)