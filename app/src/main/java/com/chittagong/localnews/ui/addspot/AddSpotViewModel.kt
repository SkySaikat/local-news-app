package com.chittagong.localnews.ui.addspot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chittagong.localnews.core.common.SnackbarKind
import com.chittagong.localnews.core.common.UiEvent
import com.chittagong.localnews.domain.model.PostCategory
import com.chittagong.localnews.domain.usecase.CreatePostUseCase
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

data class AddSpotUiState(
    val title: String = "",
    val description: String = "",
    val category: PostCategory = PostCategory.ALERT,
    val area: String = "",
    val isSubmitting: Boolean = false,
) {
    val isSubmitEnabled: Boolean
        get() = title.isNotBlank() && description.isNotBlank() && area.isNotBlank() && !isSubmitting
}

@HiltViewModel
class AddSpotViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSpotUiState())
    val uiState: StateFlow<AddSpotUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onCategoryChange(value: PostCategory) {
        _uiState.update { it.copy(category = value) }
    }

    fun onAreaChange(value: String) {
        _uiState.update { it.copy(area = value) }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isSubmitting) return

        if (!state.isSubmitEnabled) {
             viewModelScope.launch {
                 _events.send(UiEvent.ShowSnackbar("Please fill out all required fields.", SnackbarKind.Error))
             }
             return
        }

        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            createPostUseCase(
                title = state.title.trim(),
                description = state.description.trim(),
                category = state.category.name,
                area = state.area,
            ).onSuccess {
                _uiState.update { AddSpotUiState() } // Reset form on success
                _events.send(UiEvent.ShowSnackbar("Spot created successfully!", SnackbarKind.Success))
            }.onFailure { throwable ->
                _uiState.update { it.copy(isSubmitting = false) }
                _events.send(UiEvent.ShowSnackbar(throwable.message ?: "Failed to create spot.", SnackbarKind.Error))
            }
        }
    }
}
