package com.chittagong.localnews.core.common

/**
 * Single source of truth for the state of any screen-level data load.
 *
 * Screens render exactly one of these four branches, which keeps the UI
 * exhaustive at compile time — no "loading flag + nullable data + error string"
 * triples that can drift out of sync.
 */
sealed interface UiState<out T> {

    /** Nothing requested yet. Useful for lazily-triggered loads. */
    data object Idle : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data class Error(val message: String) : UiState<Nothing>
}

val UiState<*>.isLoading: Boolean get() = this is UiState.Loading

fun <T> UiState<T>.dataOrNull(): T? = (this as? UiState.Success)?.data

inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Error -> this
    UiState.Loading -> UiState.Loading
    UiState.Idle -> UiState.Idle
}
