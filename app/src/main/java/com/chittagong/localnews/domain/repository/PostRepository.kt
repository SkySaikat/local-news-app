package com.chittagong.localnews.domain.repository

import com.chittagong.localnews.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    /** Observe all posts, sorted by newest first. */
    fun observePosts(): Flow<Result<List<Post>>>

    /** Fetch posts for a specific area. */
    fun observePostsByArea(area: String): Flow<Result<List<Post>>>

    suspend fun createPost(
        title: String,
        description: String,
        category: String,
        area: String,
        latitude: Double?,
        longitude: Double?,
        imageUrl: String?
    ): Result<Unit>
}
