package com.chittagong.localnews.domain.usecase

import com.chittagong.localnews.core.common.ChittagongAreas
import com.chittagong.localnews.core.util.Validators
import com.chittagong.localnews.domain.model.UserProfile
import com.chittagong.localnews.domain.repository.AuthRepository
import com.chittagong.localnews.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Registration is two writes that must succeed together: the Auth credential and
 * the Firestore `users` document. If the profile write fails we surface the
 * error rather than leaving the user in a half-registered state with no profile.
 */
class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        homeArea: String,
    ): Result<UserProfile> {
        Validators.validateName(name).takeIf { !it.isValid }?.let {
            return Result.failure(IllegalArgumentException(it.errorMessage))
        }
        Validators.validateEmail(email).takeIf { !it.isValid }?.let {
            return Result.failure(IllegalArgumentException(it.errorMessage))
        }
        Validators.validatePassword(password).takeIf { !it.isValid }?.let {
            return Result.failure(IllegalArgumentException(it.errorMessage))
        }
        if (!ChittagongAreas.isValid(homeArea)) {
            return Result.failure(IllegalArgumentException("Pick a valid home area"))
        }

        val cleanName = name.trim()
        val cleanEmail = email.trim()

        return authRepository.signUp(cleanEmail, password, cleanName)
            .fold(
                onSuccess = { authUser ->
                    userRepository.createUserProfile(
                        uid = authUser.uid,
                        displayName = cleanName,
                        email = cleanEmail,
                        homeArea = homeArea,
                    ).onFailure {
                        // The credential exists but the profile does not; sign the
                        // session out so the app never boots into an empty profile.
                        authRepository.signOut()
                    }
                },
                onFailure = { Result.failure(it) },
            )
    }
}
