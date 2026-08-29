package com.chittagong.localnews.domain.repository

import com.chittagong.localnews.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/** Owns the Firestore `users` collection. */
interface UserRepository {

    /** Live profile stream — the profile screen updates without a manual refresh. */
    fun observeUserProfile(uid: String): Flow<Result<UserProfile>>

    suspend fun getUserProfile(uid: String): Result<UserProfile>

    /**
     * Writes the initial profile document created at sign-up:
     * uid, displayName, homeArea, trustScore = 100, createdAt.
     */
    suspend fun createUserProfile(
        uid: String,
        displayName: String,
        email: String,
        homeArea: String,
    ): Result<UserProfile>

    suspend fun updateHomeArea(uid: String, homeArea: String): Result<Unit>
}
