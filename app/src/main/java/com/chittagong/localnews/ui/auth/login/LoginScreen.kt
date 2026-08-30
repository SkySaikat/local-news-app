package com.chittagong.localnews.ui.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittagong.localnews.BuildConfig
import com.chittagong.localnews.core.common.UiEvent
import com.chittagong.localnews.ui.components.AppSnackbarHost
import com.chittagong.localnews.ui.components.AppTextField
import com.chittagong.localnews.ui.components.AuthBackdrop
import com.chittagong.localnews.ui.components.BrandMark
import com.chittagong.localnews.ui.components.PasswordTextField
import com.chittagong.localnews.ui.components.PrimaryButton
import com.chittagong.localnews.ui.components.SecondaryButton
import com.chittagong.localnews.ui.components.rememberAppSnackbarController
import com.chittagong.localnews.ui.theme.spacing
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: (prefilledEmail: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = rememberAppSnackbarController()
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarController.show(event.message, event.kind)
                UiEvent.NavigateToHome -> onNavigateToHome()
                UiEvent.NavigateToLogin -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        snackbarHost = { AppSnackbarHost(snackbarController) },
    ) { innerPadding ->
        AuthBackdrop {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(innerPadding)
                    .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
            ) {
                Spacer(Modifier.height(MaterialTheme.spacing.xxl))

                BrandMark(size = 64.dp)

                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.lg),
                )
                Text(
                    text = "Sign in to see what's happening around you in Chittagong.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
                )

                Spacer(Modifier.height(MaterialTheme.spacing.xl))

                AppTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Email",
                    leadingIcon = Icons.Outlined.MailOutline,
                    placeholder = "you@example.com",
                    errorMessage = uiState.emailError,
                    enabled = !uiState.isSubmitting,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.onFocusChanged { state: FocusState ->
                        if (!state.isFocused) viewModel.onEmailFocusLost()
                    },
                )

                Spacer(Modifier.height(MaterialTheme.spacing.md))

                PasswordTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Password",
                    errorMessage = uiState.passwordError,
                    enabled = !uiState.isSubmitting,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboard?.hide()
                            viewModel.onSubmit()
                        },
                    ),
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(
                        onClick = viewModel::onForgotPasswordClick,
                        enabled = !uiState.isSubmitting,
                    ) {
                        Text("Forgot password?", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(Modifier.height(MaterialTheme.spacing.md))

                PrimaryButton(
                    text = "Sign in",
                    onClick = {
                        keyboard?.hide()
                        viewModel.onSubmit()
                    },
                    enabled = uiState.isSubmitEnabled,
                    loading = uiState.isSubmitting,
                    icon = Icons.AutoMirrored.Filled.Login,
                )

                Spacer(Modifier.height(MaterialTheme.spacing.md))

                SecondaryButton(
                    text = "Continue with Google",
                    onClick = {
                        keyboard?.hide()
                        scope.launch {
                            try {
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = credentialManager.getCredential(context, request)
                                val credential = result.credential

                                if (credential is GoogleIdTokenCredential) {
                                    viewModel.onGoogleSignInSuccess(credential.idToken)
                                } else {
                                    viewModel.onGoogleSignInError("Unexpected credential type.")
                                }
                            } catch (e: GetCredentialException) {
                                viewModel.onGoogleSignInError(e.message ?: "Google sign-in failed.")
                            }
                        }
                    },
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(MaterialTheme.spacing.lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "New to the neighbourhood?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(
                        onClick = { onNavigateToSignUp(uiState.email) },
                        enabled = !uiState.isSubmitting,
                    ) {
                        Text("Create account", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(Modifier.height(MaterialTheme.spacing.xl))
            }
        }
    }

    if (uiState.showForgotPasswordDialog) {
        ForgotPasswordDialog(
            email = uiState.forgotPasswordEmail,
            onEmailChange = viewModel::onForgotPasswordEmailChange,
            errorMessage = uiState.forgotPasswordError,
            isSending = uiState.isSendingReset,
            onDismiss = viewModel::onForgotPasswordDismiss,
            onSend = viewModel::onSendResetLink,
        )
    }
}
