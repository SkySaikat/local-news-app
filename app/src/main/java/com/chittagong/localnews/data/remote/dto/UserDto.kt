package com.chittagong.localnews.data.remote.dto

import com.chittagong.localnews.core.common.DEFAULT_TRUST_SCORE
import com.chittagong.localnews.domain.model.UserProfile
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Wire shape of a `users` document. Firestore instantiates this reflectively,
 * hence the no-arg defaults and the empty constructor requirement.
 */
@IgnoreExtraProperties
data class UserDto(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val homeArea: String = "",
    val trustScore: Long = DEFAULT_TRUST_SCORE.toLong(),
    val joinedTimestamp: Long = 0L,
)

fun UserDto.toDomain(): UserProfile = UserProfile(
    uid = uid,
    displayName = displayName,
    email = email,
    homeArea = homeArea,
    trustScore = trustScore.toInt(),
    profileImageUrl = profileImageUrl?.takeIf { it.isNotBlank() },
    joinedTimestamp = joinedTimestamp,
)

/**
 * Hand-rolled mapping rather than [DocumentSnapshot.toObject] so a single bad
 * field (e.g. a legacy `trustScore` stored as a string) degrades gracefully
 * instead of throwing and blanking the whole profile.
 */
fun DocumentSnapshot.toUserProfileOrNull(): UserProfile? {
    if (!exists()) return null
    return UserProfile(
        uid = getString("uid") ?: id,
        displayName = getString("displayName").orEmpty(),
        email = getString("email").orEmpty(),
        homeArea = getString("homeArea").orEmpty(),
        trustScore = (get("trustScore") as? Number)?.toInt() ?: DEFAULT_TRUST_SCORE,
        profileImageUrl = getString("profileImageUrl")?.takeIf { it.isNotBlank() },
        joinedTimestamp = (get("joinedTimestamp") as? Number)?.toLong()
            ?: (get("createdAt") as? Number)?.toLong()
            ?: 0L,
    )
}
