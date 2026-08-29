package com.chittagong.localnews.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UserProfileTest {

    private fun profile(name: String, score: Int = 100) = UserProfile(
        uid = "uid-1",
        displayName = name,
        email = "neighbour@example.com",
        homeArea = "Agrabad",
        trustScore = score,
    )

    @Test
    fun `initials take the first two words`() {
        assertEquals("SC", profile("Saikat Chowdhury").initials)
        assertEquals("SC", profile("saikat  chowdhury rahman").initials)
        assertEquals("A", profile("Ayesha").initials)
    }

    @Test
    fun `initials fall back to the email when the name is blank`() {
        assertEquals("N", profile("   ").initials)
    }

    @Test
    fun `trust tiers follow the score bands`() {
        assertEquals(TrustTier.Restricted, profile("A B", score = 20).trustTier)
        assertEquals(TrustTier.Resident, profile("A B", score = 100).trustTier)
        assertEquals(TrustTier.ActiveNeighbour, profile("A B", score = 180).trustTier)
        assertEquals(TrustTier.TrustedReporter, profile("A B", score = 300).trustTier)
    }
}
