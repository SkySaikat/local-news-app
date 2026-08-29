package com.chittagong.localnews.domain.usecase

import com.chittagong.localnews.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke() = authRepository.signOut()
}
