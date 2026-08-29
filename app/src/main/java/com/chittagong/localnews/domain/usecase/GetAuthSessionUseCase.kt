package com.chittagong.localnews.domain.usecase

import com.chittagong.localnews.domain.model.AuthUser
import com.chittagong.localnews.domain.repository.AuthRepository
import javax.inject.Inject

/** Used by the splash screen to decide between the auth graph and the main graph. */
class GetAuthSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): AuthUser? = authRepository.currentUser()
}
