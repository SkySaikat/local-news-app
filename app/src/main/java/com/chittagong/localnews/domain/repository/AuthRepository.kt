package com.chittagong.localnews.domain.repository

import com.chittagong.localnews.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * All Firebase Authentication access goes through this interface, so swapping
 * the backend (or faking it in tests) never touches the UI layer.
 */
interface AuthRepository {

    /** Emits the signed-in user, or null once they sign out. Backed by an auth state listener. */
    val authState: Flow<AuthUser?>

    /** Synchronous session check used by the splash screen for instant routing. */
    fun currentUser(): AuthUser?

    suspend fun signIn(email: String, password: String): Result<AuthUser>

    /** Creates the credential only. Profile document creation is [UserRepository]'s job. */
    suspend fun signUp(email: String, password: String, displayName: String): Result<AuthUser>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    fun signOut()
}
