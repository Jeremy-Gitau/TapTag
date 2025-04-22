package com.taptag.project.domain.repository

import com.taptag.project.sources.remote.dtos.PaymentsRequestData


interface PaymentsRepository {

    suspend fun initiatePayments(data: PaymentsRequestData)

}