package com.stokia.aura.domain.model

/**
 * Core domain model representing a user in Aura.
 * Pure Kotlin — no Android/Firebase dependencies.
 */
data class AuraUser(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val coverUrl: String = "",
    val publicKey: String = "",
    val fcmToken: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
