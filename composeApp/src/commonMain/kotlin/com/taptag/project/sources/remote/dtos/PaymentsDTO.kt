package com.taptag.project.sources.remote.dtos

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "payment")
data class PaymentsRequestData(
    val amount: Int,
    val paymentMethod: PaymentMethodEntity
)

enum class PaymentMethodEntity{
    mpesa
}