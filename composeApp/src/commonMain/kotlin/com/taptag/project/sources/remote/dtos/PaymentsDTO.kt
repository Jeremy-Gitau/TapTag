package com.taptag.project.sources.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PaymentsRequestData(
    val amount: Int,
    val paymentMethod: PaymentMethod
)

enum class PaymentMethod{
    mpesa
}