package com.chittagong.localnews.data.repository

import com.chittagong.localnews.core.util.AuthErrorMapper
import com.chittagong.localnews.di.IoDispatcher
import com.chittagong.localnews.domain.model.AuthUser
import com.chittagong.localnews.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * The only class in the app allowed to touch [FirebaseAuth].
 *
 * Every suspend function returns a [Result] with an already user-readable
 * message, so callers never need to know a Firebase exception type exists.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override val authState: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toAuthUser())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override fun currentUser(): AuthUser? = firebaseAuth.currentUser?.toAuthUser()

    override suspend fun signIn(email: String, password: String): Result<AuthUser> =
        withContext(ioDispatcher) {
            runCatching {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                requireNotNull(result.user?.toAuthUser()) { "Sign-in returned no user." }
            }.mapError()
        }

    override suspend fun signUp(
        email: String,
        password: String,
        displayName: String,
    ): Result<AuthUser> = withContext(ioDispatcher) {
        runCatching {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = requireNotNull(result.user) { "Sign-up returned no user." }

            // Mirror the name onto the Auth record too, so FirebaseUser.displayName
            // is populated even before the Firestore document is read.
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profileUpdate).await()
            user.toAuthUser().copy(displayName = displayName)
        }.mapError()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                firebaseAuth.sendPasswordResetEmail(email).await()
                Unit
            }.mapError()
        }

    override fun signOut() = firebaseAuth.signOut()

    private fun FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName,
        isEmailVerified = isEmailVerified,
    )

    /** Replaces a raw SDK throwable with one carrying presentable copy. */
    private fun <T> Result<T>.mapError(): Result<T> = recoverCatching { throwable ->
        throw AuthException(AuthErrorMapper.toMessage(throwable), throwable)
    }
}

/** Carries a message that is already safe to render in a snackbar. */
class AuthException(
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
