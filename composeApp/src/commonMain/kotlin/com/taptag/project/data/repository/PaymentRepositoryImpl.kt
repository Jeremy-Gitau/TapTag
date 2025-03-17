package com.taptag.project.data.repository

import com.taptag.project.domain.repository.PaymentsRepository
import com.taptag.project.sources.remote.client.NfcServerClient
import com.taptag.project.sources.remote.dtos.PaymentsRequestData

class PaymentRepositoryImpl(
    private val client: NfcServerClient
) : PaymentsRepository {

    override suspend fun initiatePayments(data: PaymentsRequestData) {
        when (val result = client.initiatePayments(data = data)) {

//            is NetworkResult.Error -> DataResult.Error(result.message)
//            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }
}