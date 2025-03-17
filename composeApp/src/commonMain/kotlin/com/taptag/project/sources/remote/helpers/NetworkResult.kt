package com.taptag.project.sources.remote.helpers

sealed interface NetworkResult<out T> {
    data class Success<out T>(
        val data: T
    ) : NetworkResult<T>

    data class Error(
        val message: String
    ) : NetworkResult<Nothing>
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        val result = apiCall()
        NetworkResult.Success(result)
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "An Error Occurred")
    }
}