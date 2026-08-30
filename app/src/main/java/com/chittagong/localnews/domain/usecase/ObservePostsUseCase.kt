package com.chittagong.localnews.domain.usecase

import com.chittagong.localnews.domain.model.Post
import com.chittagong.localnews.domain.repository.PostRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservePostsUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {
    operator fun invoke(): Flow<Result<List<Post>>> = postRepository.observePosts()
}
