package com.chittagong.localnews.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chittagong.localnews.core.common.SnackbarKind
import com.chittagong.localnews.core.common.UiEvent
import com.chittagong.localnews.core.common.UiState
import com.chittagong.localnews.domain.model.UserProfile
import com.chittagong.localnews.domain.usecase.ObserveCurrentUserProfileUseCase
import com.chittagong.localnews.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeCurrentUserProfile: ObserveCurrentUserProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    /**
     * A live Firestore snapshot stream, mapped into [UiState] once. `WhileSubscribed`
     * with a 5s grace period keeps the listener alive across rotation but
     * detaches it when the user leaves the tab.
     */
    val profileState: StateFlow<UiState<UserProfile>> = observeCurrentUserProfile()
        .map { result ->
            result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Couldn't load your profile.") },
            )
        }
        .catch { emit(UiState.Error(it.message ?: "Couldn't load your profile.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = UiState.Loading,
        )

    private val _isSigningOut = MutableStateFlow(false)
    val isSigningOut: StateFlow<Boolean> = _isSigningOut.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()

    fun onSignOut() {
        if (_isSigningOut.value) return
        _isSigningOut.value = true

        viewModelScope.launch {
            signOutUseCase()
            _events.send(UiEvent.ShowSnackbar("Signed out", SnackbarKind.Info))
            _events.send(UiEvent.NavigateToLogin)
            _isSigningOut.value = false
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
