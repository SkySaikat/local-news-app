package com.chittagong.localnews.core.common

/** Firestore collection and field names, kept in one place to avoid typo bugs. */
object FirestoreCollections {
    const val USERS = "users"

    // Reserved for v2.0 — declared here so the schema stays documented in code.
    const val POSTS = "posts"
    const val COMMENTS = "comments"
}

object UserFields {
    const val UID = "uid"
    const val DISPLAY_NAME = "displayName"
    const val EMAIL = "email"
    const val PROFILE_IMAGE_URL = "profileImageUrl"
    const val HOME_AREA = "homeArea"
    const val TRUST_SCORE = "trustScore"
    const val JOINED_TIMESTAMP = "joinedTimestamp"
    const val CREATED_AT = "createdAt"
}

/** Every new member starts at a neutral reputation of 100 (see proposal §7.1). */
const val DEFAULT_TRUST_SCORE: Int = 100
