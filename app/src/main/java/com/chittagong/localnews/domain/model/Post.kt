package com.chittagong.localnews.domain.model

data class Post(
    val id: String,
    val authorUid: String,
    val authorName: String,
    val title: String,
    val description: String,
    val category: PostCategory,
    val area: String,
    val latitude: Double?,
    val longitude: Double?,
    val imageUrl: String?,
    val timestamp: Long,
    val upvotes: Int = 0,
)

enum class PostCategory {
    ALERT,
    DEAL,
    FUN,
    CHAT,
    OTHER;

    companion object {
        fun fromString(value: String): PostCategory = entries.find { it.name.equals(value, ignoreCase = true) } ?: OTHER
    }
}
