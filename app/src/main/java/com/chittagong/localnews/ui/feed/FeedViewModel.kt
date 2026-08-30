package com.chittagong.localnews.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chittagong.localnews.domain.model.Post
import com.chittagong.localnews.domain.usecase.ObservePostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val observePosts: ObservePostsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState(isLoading = true))
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            observePosts().collect { result ->
                result.onSuccess { posts ->
                    _uiState.update { it.copy(posts = posts, isLoading = false, errorMessage = null) }
                }.onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message ?: "Failed to load posts.") }
                }
            }
        }
    }

    fun onRefresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadPosts()
    }
}
