package com.chittagong.localnews.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chittagong.localnews.core.common.SnackbarKind
import com.chittagong.localnews.core.common.UiEvent
import com.chittagong.localnews.core.util.Validators
import com.chittagong.localnews.domain.usecase.SendPasswordResetUseCase
import com.chittagong.localnews.domain.usecase.SignInUseCase
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

/**
 * Everything the login screen renders. Field errors are nullable strings so
 * they map straight onto Material 3's supporting-text contract.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
    val showForgotPasswordDialog: Boolean = false,
    val forgotPasswordEmail: String = "",
    val forgotPasswordError: String? = null,
    val isSendingReset: Boolean = false,
) {
    /** Enables the CTA only once both fields have content and no live error. */
    val isSubmitEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
            emailError == null && passwordError == null && !isSubmitting
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signIn: SignInUseCase,
    private val sendPasswordReset: SendPasswordResetUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()

    /**
     * Validation is "lazy on type, eager on blur": while typing we only clear a
     * standing error, so the user isn't scolded mid-word.
     */
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onEmailFocusLost() {
        val state = _uiState.value
        if (state.email.isBlank()) return
        val result = Validators.validateEmail(state.email)
        _uiState.update { it.copy(emailError = result.errorMessage) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isSubmitting) return

        val emailResult = Validators.validateEmail(state.email)
        val passwordResult = Validators.validateLoginPassword(state.password)

        if (!emailResult.isValid || !passwordResult.isValid) {
            _uiState.update {
                it.copy(
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                )
            }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, emailError = null, passwordError = null) }

        viewModelScope.launch {
            signIn(state.email, state.password)
                .onSuccess {
                    _uiState.update { current -> current.copy(isSubmitting = false) }
                    _events.send(
                        UiEvent.ShowSnackbar("Welcome back!", SnackbarKind.Success),
                    )
                    _events.send(UiEvent.NavigateToHome)
                }
                .onFailure { throwable ->
                    _uiState.update { current -> current.copy(isSubmitting = false) }
                    _events.send(
                        UiEvent.ShowSnackbar(
                            throwable.message ?: "Couldn't sign you in.",
                            SnackbarKind.Error,
                        ),
                    )
                }
        }
    }

    // ---- Forgot password ----

    fun onForgotPasswordClick() {
        _uiState.update {
            it.copy(
                showForgotPasswordDialog = true,
                // Pre-fill with whatever they already typed — one less step.
                forgotPasswordEmail = it.email,
                forgotPasswordError = null,
            )
        }
    }

    fun onForgotPasswordDismiss() {
        if (_uiState.value.isSendingReset) return
        _uiState.update { it.copy(showForgotPasswordDialog = false, forgotPasswordError = null) }
    }

    fun onForgotPasswordEmailChange(value: String) {
        _uiState.update { it.copy(forgotPasswordEmail = value, forgotPasswordError = null) }
    }

    fun onSendResetLink() {
        val state = _uiState.value
        if (state.isSendingReset) return

        val result = Validators.validateEmail(state.forgotPasswordEmail)
        if (!result.isValid) {
            _uiState.update { it.copy(forgotPasswordError = result.errorMessage) }
            return
        }

        _uiState.update { it.copy(isSendingReset = true) }

        viewModelScope.launch {
            sendPasswordReset(state.forgotPasswordEmail)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSendingReset = false, showForgotPasswordDialog = false)
                    }
                    _events.send(
                        UiEvent.ShowSnackbar(
                            "Reset link sent to ${state.forgotPasswordEmail.trim()}",
                            SnackbarKind.Success,
                        ),
                    )
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isSendingReset = false,
                            forgotPasswordError = throwable.message
                                ?: "Couldn't send the reset link.",
                        )
                    }
                }
        }
    }
}
