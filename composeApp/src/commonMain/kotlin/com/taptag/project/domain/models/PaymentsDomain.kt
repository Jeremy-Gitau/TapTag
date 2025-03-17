package com.taptag.project.domain.models


data class PaymentsRequestDomain(
    val amount: Int,
    val paymentMethod: PaymentMethod
)

enum class PaymentMethod {
    mpesa
}