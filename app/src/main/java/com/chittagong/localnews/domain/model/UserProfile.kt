package com.chittagong.localnews.domain.model

/**
 * Domain representation of a `users` document (proposal §7.1).
 *
 * Deliberately free of Firebase types so ViewModels and Composables never
 * depend on the SDK.
 */
data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val homeArea: String,
    val trustScore: Int,
    val profileImageUrl: String? = null,
    val joinedTimestamp: Long = 0L,
) {
    /** "SC" for "Saikat Chowdhury" — drives the avatar fallback. */
    val initials: String
        get() = displayName.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
            .ifEmpty { email.firstOrNull()?.uppercaseChar()?.toString() ?: "?" }

    /** Reputation tier shown as a badge on the profile screen. */
    val trustTier: TrustTier
        get() = when {
            trustScore >= 250 -> TrustTier.TrustedReporter
            trustScore >= 150 -> TrustTier.ActiveNeighbour
            trustScore >= 50 -> TrustTier.Resident
            else -> TrustTier.Restricted
        }
}

enum class TrustTier(val label: String) {
    TrustedReporter("Trusted Reporter"),
    ActiveNeighbour("Active Neighbour"),
    Resident("Resident"),
    Restricted("Limited"),
}
