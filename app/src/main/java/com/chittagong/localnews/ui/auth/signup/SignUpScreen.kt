package com.chittagong.localnews.ui.auth.signup

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.rounded.PersonAddAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittagong.localnews.core.common.UiEvent
import com.chittagong.localnews.ui.components.AppSnackbarHost
import com.chittagong.localnews.ui.components.AppTextField
import com.chittagong.localnews.ui.components.AreaDropdown
import com.chittagong.localnews.ui.components.AuthBackdrop
import com.chittagong.localnews.ui.components.PasswordTextField
import com.chittagong.localnews.ui.components.PrimaryButton
import com.chittagong.localnews.ui.components.rememberAppSnackbarController
import com.chittagong.localnews.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = rememberAppSnackbarController()
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarController.show(event.message, event.kind)
                UiEvent.NavigateToHome -> onNavigateToHome()
                UiEvent.NavigateToLogin -> onNavigateBack()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        snackbarHost = { AppSnackbarHost(snackbarController) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !uiState.isSubmitting,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to sign in",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
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
                Text(
                    text = "Join your neighbourhood",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Report what you see, and get alerts from people on your street.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
                )

                Spacer(Modifier.height(MaterialTheme.spacing.xl))

                AppTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Full name",
                    leadingIcon = Icons.Outlined.PersonOutline,
                    placeholder = "e.g. Saikat Chowdhury",
                    errorMessage = uiState.nameError,
                    enabled = !uiState.isSubmitting,
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.onFocusChanged {
                        if (!it.isFocused && uiState.name.isNotEmpty()) viewModel.onNameFocusLost()
                    },
                )

                Spacer(Modifier.height(MaterialTheme.spacing.md))

                AppTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Email",
                    leadingIcon = Icons.Outlined.MailOutline,
                    placeholder = "you@example.com",
                    errorMessage = uiState.emailError,
                    enabled = !uiState.isSubmitting,
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier.onFocusChanged {
                        if (!it.isFocused && uiState.email.isNotEmpty()) viewModel.onEmailFocusLost()
                    },
                )

                Spacer(Modifier.height(MaterialTheme.spacing.md))

                AreaDropdown(
                    selectedArea = uiState.homeArea,
                    onAreaSelected = viewModel::onHomeAreaChange,
                    enabled = !uiState.isSubmitting,
                    errorMessage = uiState.homeAreaError,
                )

                Spacer(Modifier.height(MaterialTheme.spacing.md))

                PasswordTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Password",
                    errorMessage = uiState.passwordError,
                    enabled = !uiState.isSubmitting,
                    showStrengthMeter = true,
                    modifier = Modifier.onFocusChanged {
                        if (!it.isFocused && uiState.password.isNotEmpty()) {
                            viewModel.onPasswordFocusLost()
                        }
                    },
                )

                Spacer(Modifier.height(MaterialTheme.spacing.md))

                PasswordTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = "Confirm password",
                    errorMessage = uiState.confirmPasswordError,
                    enabled = !uiState.isSubmitting,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboard?.hide()
                            viewModel.onSubmit()
                        },
                    ),
                    modifier = Modifier.onFocusChanged {
                        if (!it.isFocused && uiState.confirmPassword.isNotEmpty()) {
                            viewModel.onConfirmPasswordFocusLost()
                        }
                    },
                )

                Spacer(Modifier.height(MaterialTheme.spacing.lg))

                PrimaryButton(
                    text = "Create account",
                    onClick = {
                        keyboard?.hide()
                        viewModel.onSubmit()
                    },
                    enabled = uiState.isSubmitEnabled,
                    loading = uiState.isSubmitting,
                    icon = Icons.Rounded.PersonAddAlt,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.sm),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Already have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = onNavigateBack, enabled = !uiState.isSubmitting) {
                        Text("Sign in", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(Modifier.height(MaterialTheme.spacing.xl))
            }
        }
    }
}
