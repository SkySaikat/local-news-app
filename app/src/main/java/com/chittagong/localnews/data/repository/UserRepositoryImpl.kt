package com.chittagong.localnews.data.repository

import com.chittagong.localnews.core.common.DEFAULT_TRUST_SCORE
import com.chittagong.localnews.core.common.FirestoreCollections
import com.chittagong.localnews.core.common.UserFields
import com.chittagong.localnews.core.util.AuthErrorMapper
import com.chittagong.localnews.data.remote.dto.toUserProfileOrNull
import com.chittagong.localnews.di.IoDispatcher
import com.chittagong.localnews.domain.model.UserProfile
import com.chittagong.localnews.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * The only class in the app allowed to touch the Firestore `users` collection.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {

    private fun userDoc(uid: String) =
        firestore.collection(FirestoreCollections.USERS).document(uid)

    override fun observeUserProfile(uid: String): Flow<Result<UserProfile>> = callbackFlow {
        val registration = userDoc(uid).addSnapshotListener { snapshot, error ->
            when {
                error != null ->
                    trySend(Result.failure(ProfileException(AuthErrorMapper.toMessage(error), error)))

                snapshot == null || !snapshot.exists() ->
                    trySend(Result.failure(ProfileException("We couldn't find your profile.")))

                else -> {
                    val profile = snapshot.toUserProfileOrNull()
                    trySend(
                        if (profile != null) {
                            Result.success(profile)
                        } else {
                            Result.failure(ProfileException("Your profile data looks corrupted."))
                        },
                    )
                }
            }
        }
        awaitClose { registration.remove() }
    }.flowOn(ioDispatcher)

    override suspend fun getUserProfile(uid: String): Result<UserProfile> =
        withContext(ioDispatcher) {
            runCatching {
                val snapshot = userDoc(uid).get().await()
                snapshot.toUserProfileOrNull()
                    ?: throw ProfileException("We couldn't find your profile.")
            }.mapError()
        }

    override suspend fun createUserProfile(
        uid: String,
        displayName: String,
        email: String,
        homeArea: String,
    ): Result<UserProfile> = withContext(ioDispatcher) {
        runCatching {
            val now = System.currentTimeMillis()
            val profile = mapOf(
                UserFields.UID to uid,
                UserFields.DISPLAY_NAME to displayName,
                UserFields.EMAIL to email,
                UserFields.HOME_AREA to homeArea,
                UserFields.TRUST_SCORE to DEFAULT_TRUST_SCORE,
                UserFields.PROFILE_IMAGE_URL to "",
                UserFields.JOINED_TIMESTAMP to now,
                // `createdAt` mirrors joinedTimestamp; both are documented in the
                // proposal's schema and cheap to keep in sync.
                UserFields.CREATED_AT to now,
            )
            userDoc(uid).set(profile).await()

            UserProfile(
                uid = uid,
                displayName = displayName,
                email = email,
                homeArea = homeArea,
                trustScore = DEFAULT_TRUST_SCORE,
                profileImageUrl = null,
                joinedTimestamp = now,
            )
        }.mapError()
    }

    override suspend fun updateHomeArea(uid: String, homeArea: String): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                userDoc(uid).update(UserFields.HOME_AREA, homeArea).await()
                Unit
            }.mapError()
        }

    private fun <T> Result<T>.mapError(): Result<T> = recoverCatching { throwable ->
        if (throwable is ProfileException) throw throwable
        throw ProfileException(AuthErrorMapper.toMessage(throwable), throwable)
    }
}

/** Firestore failure carrying user-presentable copy. */
class ProfileException(
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
