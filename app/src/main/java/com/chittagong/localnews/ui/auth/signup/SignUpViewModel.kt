package com.chittagong.localnews.ui.auth.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.chittagong.localnews.core.common.SnackbarKind
import com.chittagong.localnews.core.common.UiEvent
import com.chittagong.localnews.core.util.Validators
import com.chittagong.localnews.domain.usecase.SignUpUseCase
import com.chittagong.localnews.ui.navigation.SignUpRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val homeArea: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val homeAreaError: String? = null,
    val isSubmitting: Boolean = false,
) {
    val isSubmitEnabled: Boolean
        get() = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
            confirmPassword.isNotBlank() && homeArea.isNotBlank() && !isSubmitting
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUp: SignUpUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        // Carry over the email the user already typed on the login screen.
        SignUpUiState(email = savedStateHandle.toRoute<SignUpRoute>().prefilledEmail),
    )
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, nameError = null) }

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, emailError = null) }

    fun onPasswordChange(value: String) = _uiState.update {
        it.copy(
            password = value,
            passwordError = null,
            // Re-check the confirmation against the new password immediately.
            confirmPasswordError = if (it.confirmPassword.isNotEmpty() && it.confirmPassword != value) {
                "Passwords don't match"
            } else {
                null
            },
        )
    }

    fun onConfirmPasswordChange(value: String) = _uiState.update {
        it.copy(confirmPassword = value, confirmPasswordError = null)
    }

    fun onHomeAreaChange(value: String) = _uiState.update {
        it.copy(homeArea = value, homeAreaError = null)
    }

    fun onNameFocusLost() = validateField { state ->
        state.copy(nameError = Validators.validateName(state.name).errorMessage)
    }

    fun onEmailFocusLost() = validateField { state ->
        state.copy(emailError = Validators.validateEmail(state.email).errorMessage)
    }

    fun onPasswordFocusLost() = validateField { state ->
        state.copy(passwordError = Validators.validatePassword(state.password).errorMessage)
    }

    fun onConfirmPasswordFocusLost() = validateField { state ->
        state.copy(
            confirmPasswordError = Validators
                .validateConfirmPassword(state.password, state.confirmPassword)
                .errorMessage,
        )
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isSubmitting) return

        val nameResult = Validators.validateName(state.name)
        val emailResult = Validators.validateEmail(state.email)
        val passwordResult = Validators.validatePassword(state.password)
        val confirmResult = Validators.validateConfirmPassword(state.password, state.confirmPassword)
        val areaResult = Validators.validateArea(state.homeArea)

        val allValid = nameResult.isValid && emailResult.isValid && passwordResult.isValid &&
            confirmResult.isValid && areaResult.isValid

        if (!allValid) {
            _uiState.update {
                it.copy(
                    nameError = nameResult.errorMessage,
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                    confirmPasswordError = confirmResult.errorMessage,
                    homeAreaError = areaResult.errorMessage,
                )
            }
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            signUp(
                name = state.name,
                email = state.email,
                password = state.password,
                homeArea = state.homeArea,
            ).onSuccess { profile ->
                _uiState.update { it.copy(isSubmitting = false) }
                _events.send(
                    UiEvent.ShowSnackbar(
                        "Welcome to ${profile.homeArea}, ${profile.displayName}!",
                        SnackbarKind.Success,
                    ),
                )
                _events.send(UiEvent.NavigateToHome)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isSubmitting = false) }
                _events.send(
                    UiEvent.ShowSnackbar(
                        throwable.message ?: "Couldn't create your account.",
                        SnackbarKind.Error,
                    ),
                )
            }
        }
    }

    /** Runs a per-field check on blur, but never on an untouched empty field. */
    private inline fun validateField(transform: (SignUpUiState) -> SignUpUiState) {
        _uiState.update { state -> transform(state) }
    }
}
