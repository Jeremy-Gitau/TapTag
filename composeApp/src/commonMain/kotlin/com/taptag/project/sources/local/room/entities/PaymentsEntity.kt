package com.taptag.project.sources.local.room.entities

import kotlinx.serialization.Serializable

@Serializable
data class PaymentsRequestEntity(
    val amount: Int,
    val paymentMethod: PaymentMethod
)

enum class PaymentMethod{
    mpesa
}