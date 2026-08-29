package com.chittagong.localnews.domain.model

/** The minimal identity surface we need from Firebase Auth. */
data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String?,
    val isEmailVerified: Boolean,
)
