package com.chittagong.localnews.domain.usecase

import com.chittagong.localnews.domain.model.AuthUser
import com.chittagong.localnews.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Signs a user in using Google credentials obtained via Credential Manager.
 */
class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): Result<AuthUser> {
        if (idToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Google ID token is missing."))
        }
        return authRepository.signInWithGoogle(idToken)
    }
}
