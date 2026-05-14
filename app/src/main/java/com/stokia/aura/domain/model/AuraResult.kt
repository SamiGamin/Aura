package com.stokia.aura.domain.model

/**
 * Sealed wrapper for operation results across the app.
 */
sealed class AuraResult<out T> {
    data class Success<T>(val data: T) : AuraResult<T>()
    data class Failure(val message: String, val exception: Throwable? = null) : AuraResult<Nothing>()
}
