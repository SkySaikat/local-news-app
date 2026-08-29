package com.chittagong.localnews.domain.usecase

import com.chittagong.localnews.core.util.Validators
import com.chittagong.localnews.domain.repository.AuthRepository
import javax.inject.Inject

class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val check = Validators.validateEmail(email)
        if (!check.isValid) {
            return Result.failure(IllegalArgumentException(check.errorMessage))
        }
        return authRepository.sendPasswordResetEmail(email.trim())
    }
}
