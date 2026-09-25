package com.chittagong.localnews.domain.usecase

import com.chittagong.localnews.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        category: String,
        area: String,
        latitude: Double? = null,
        longitude: Double? = null,
        imageUrl: String? = null
    ): Result<Unit> {
        if (title.isBlank() || description.isBlank() || category.isBlank() || area.isBlank()) {
            return Result.failure(IllegalArgumentException("Please fill out all required fields."))
        }
        return postRepository.createPost(
            title = title,
            description = description,
            category = category,
            area = area,
            latitude = latitude,
            longitude = longitude,
            imageUrl = imageUrl
        )
    }
}
