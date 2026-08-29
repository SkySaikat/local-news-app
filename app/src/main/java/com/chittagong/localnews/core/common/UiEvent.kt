package com.chittagong.localnews.core.common

/**
 * One-shot UI effects (snackbars, navigation) that must not be replayed on
 * configuration change. ViewModels emit these through a [kotlinx.coroutines.channels.Channel]
 * so each event is consumed exactly once.
 */
sealed interface UiEvent {

    data class ShowSnackbar(
        val message: String,
        val kind: SnackbarKind = SnackbarKind.Info,
    ) : UiEvent

    data object NavigateToHome : UiEvent

    data object NavigateToLogin : UiEvent
}

enum class SnackbarKind { Info, Success, Error }
