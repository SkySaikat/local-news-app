package com.chittagong.localnews.domain.usecase

import com.chittagong.localnews.domain.model.UserProfile
import com.chittagong.localnews.domain.repository.AuthRepository
import com.chittagong.localnews.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Joins the auth session to the Firestore profile: whenever the signed-in user
 * changes, the emitted profile stream is switched over to the new uid.
 */
class ObserveCurrentUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Result<UserProfile>> =
        authRepository.authState.flatMapLatest { authUser ->
            if (authUser == null) {
                flowOf(Result.failure(IllegalStateException("You're signed out.")))
            } else {
                userRepository.observeUserProfile(authUser.uid)
            }
        }
}
