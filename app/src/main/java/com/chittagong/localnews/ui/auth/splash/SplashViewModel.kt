package com.chittagong.localnews.ui.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chittagong.localnews.domain.usecase.GetAuthSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Where the splash screen hands off to once the session check completes. */
sealed interface SplashDestination {
    data object Checking : SplashDestination
    data object Authenticated : SplashDestination
    data object Unauthenticated : SplashDestination
}

/**
 * Reads the persisted Firebase session and routes accordingly.
 *
 * Firebase restores the session from disk synchronously, so the only reason for
 * the short delay is to let the brand animation land rather than flash.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getAuthSession: GetAuthSessionUseCase,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Checking)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(BRAND_MOMENT_MS)
            _destination.value = if (getAuthSession() != null) {
                SplashDestination.Authenticated
            } else {
                SplashDestination.Unauthenticated
            }
        }
    }

    private companion object {
        const val BRAND_MOMENT_MS = 900L
    }
}
