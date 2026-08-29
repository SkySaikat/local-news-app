package com.chittagong.localnews.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The validators back every inline error on the auth forms, so they're the one
 * piece of v1.0 logic worth locking down with tests.
 */
class ValidatorsTest {

    @Test
    fun `accepts a well formed email`() {
        assertTrue(Validators.validateEmail("saikat.ctg@example.com").isValid)
        assertTrue(Validators.validateEmail("  spaced@example.co.uk  ").isValid)
    }

    @Test
    fun `rejects malformed emails`() {
        assertFalse(Validators.validateEmail("").isValid)
        assertFalse(Validators.validateEmail("no-at-sign.com").isValid)
        assertFalse(Validators.validateEmail("missing@tld").isValid)
        assertFalse(Validators.validateEmail("@example.com").isValid)
    }

    @Test
    fun `password must be long enough and mixed`() {
        assertFalse(Validators.validatePassword("abc").isValid)
        assertFalse(Validators.validatePassword("abcdefgh").isValid)
        assertFalse(Validators.validatePassword("12345678").isValid)
        assertTrue(Validators.validatePassword("chittagong1").isValid)
    }

    @Test
    fun `login only requires a non-empty password`() {
        assertFalse(Validators.validateLoginPassword("").isValid)
        assertTrue(Validators.validateLoginPassword("x").isValid)
    }

    @Test
    fun `confirmation must match the password`() {
        assertFalse(Validators.validateConfirmPassword("secret1", "secret2").isValid)
        assertTrue(Validators.validateConfirmPassword("secret1", "secret1").isValid)
    }

    @Test
    fun `name rejects blanks and digit-only input`() {
        assertFalse(Validators.validateName("   ").isValid)
        assertFalse(Validators.validateName("1").isValid)
        assertFalse(Validators.validateName("12345").isValid)
        assertTrue(Validators.validateName("Saikat Chowdhury").isValid)
    }

    @Test
    fun `strength meter climbs with complexity`() {
        assertEquals(0f, Validators.passwordStrength(""), 0.001f)
        assertTrue(
            Validators.passwordStrength("Chittagong@2026") >
                Validators.passwordStrength("abc123"),
        )
    }
}
