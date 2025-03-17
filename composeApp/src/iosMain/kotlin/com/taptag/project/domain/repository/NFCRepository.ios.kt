package com.taptag.project.domain.repository

import com.taptag.project.domain.helpers.NFCResult
import com.taptag.project.domain.models.NFCPermissionStatus
import kotlinx.coroutines.flow.Flow

class NFCRepositoryIosImpl(): NFCRepository {
    override suspend fun requestPermission(): NFCPermissionStatus {
        TODO("Not yet implemented")
    }

    override suspend fun checkPermission(): NFCPermissionStatus {
        TODO("Not yet implemented")
    }

    override suspend fun isNFCAvailable(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isNFCEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun startNFCSession(): Flow<NFCResult> {
        TODO("Not yet implemented")
    }

    override fun stopNFCSession() {
        TODO("Not yet implemented")
    }
}