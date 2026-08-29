package com.chittagong.localnews.domain.usecase

import com.chittagong.localnews.core.util.Validators
import com.chittagong.localnews.domain.model.AuthUser
import com.chittagong.localnews.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Signs a returning user in. Re-validates on the way through so an invalid
 * payload never costs a network round trip.
 */
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthUser> {
        val emailCheck = Validators.validateEmail(email)
        if (!emailCheck.isValid) {
            return Result.failure(IllegalArgumentException(emailCheck.errorMessage))
        }
        val passwordCheck = Validators.validateLoginPassword(password)
        if (!passwordCheck.isValid) {
            return Result.failure(IllegalArgumentException(passwordCheck.errorMessage))
        }
        return authRepository.signIn(email.trim(), password)
    }
}
