package com.chittagong.localnews.data.remote.dto

import com.chittagong.localnews.domain.model.Post
import com.chittagong.localnews.domain.model.PostCategory
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toPostOrNull(): Post? {
    if (!exists()) return null
    return Post(
        id = id,
        authorUid = getString("authorUid").orEmpty(),
        authorName = getString("authorName").orEmpty(),
        title = getString("title").orEmpty(),
        description = getString("description").orEmpty(),
        category = PostCategory.fromString(getString("category").orEmpty()),
        area = getString("area").orEmpty(),
        latitude = getDouble("latitude"),
        longitude = getDouble("longitude"),
        imageUrl = getString("imageUrl"),
        timestamp = (get("timestamp") as? Number)?.toLong() ?: 0L,
        upvotes = (get("upvotes") as? Number)?.toInt() ?: 0,
    )
}
