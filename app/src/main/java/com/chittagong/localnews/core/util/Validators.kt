package com.chittagong.localnews.core.util

/**
 * Result of a single field validation. [errorMessage] is null when valid, which
 * maps directly onto Material 3's `isError` / `supportingText` contract.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
) {
    companion object {
        val Valid = ValidationResult(isValid = true)
        fun invalid(message: String) = ValidationResult(isValid = false, errorMessage = message)
    }
}

/**
 * Client-side input validation (proposal §9, "Input Validation & Sanitization").
 * Pure functions — no Android context needed beyond [Patterns], so they stay
 * trivially unit-testable.
 */
object Validators {

    /**
     * Deliberately a plain regex rather than `android.util.Patterns.EMAIL_ADDRESS`:
     * it keeps this object free of framework types, so the whole file is
     * unit-testable on the JVM without Robolectric.
     */
    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9_%+-]+(?:\\.[A-Za-z0-9_%+-]+)*" +
            "@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z]{2,}$",
    )

    private const val MIN_PASSWORD_LENGTH = 6
    private const val MIN_NAME_LENGTH = 2
    private const val MAX_NAME_LENGTH = 40

    fun validateName(raw: String): ValidationResult {
        val name = raw.trim()
        return when {
            name.isEmpty() -> ValidationResult.invalid("Name can't be empty")
            name.length < MIN_NAME_LENGTH -> ValidationResult.invalid("Name is too short")
            name.length > MAX_NAME_LENGTH -> ValidationResult.invalid("Keep it under $MAX_NAME_LENGTH characters")
            !name.any { it.isLetter() } -> ValidationResult.invalid("Name must contain letters")
            else -> ValidationResult.Valid
        }
    }

    fun validateEmail(raw: String): ValidationResult {
        val email = raw.trim()
        return when {
            email.isEmpty() -> ValidationResult.invalid("Email can't be empty")
            !EMAIL_REGEX.matches(email) ->
                ValidationResult.invalid("Enter a valid email address")

            else -> ValidationResult.Valid
        }
    }

    fun validatePassword(password: String): ValidationResult = when {
        password.isEmpty() -> ValidationResult.invalid("Password can't be empty")
        password.length < MIN_PASSWORD_LENGTH ->
            ValidationResult.invalid("Use at least $MIN_PASSWORD_LENGTH characters")

        password.none { it.isDigit() } -> ValidationResult.invalid("Add at least one number")
        password.none { it.isLetter() } -> ValidationResult.invalid("Add at least one letter")
        else -> ValidationResult.Valid
    }

    /** Login only needs a non-empty password; strength rules belong to sign-up. */
    fun validateLoginPassword(password: String): ValidationResult =
        if (password.isEmpty()) ValidationResult.invalid("Password can't be empty") else ValidationResult.Valid

    fun validateConfirmPassword(password: String, confirmation: String): ValidationResult = when {
        confirmation.isEmpty() -> ValidationResult.invalid("Please confirm your password")
        confirmation != password -> ValidationResult.invalid("Passwords don't match")
        else -> ValidationResult.Valid
    }

    fun validateArea(area: String): ValidationResult =
        if (area.isBlank()) ValidationResult.invalid("Pick your home area") else ValidationResult.Valid

    /** 0f..1f strength meter backing the sign-up password indicator. */
    fun passwordStrength(password: String): Float {
        if (password.isEmpty()) return 0f
        var score = 0
        if (password.length >= MIN_PASSWORD_LENGTH) score++
        if (password.length >= 10) score++
        if (password.any { it.isDigit() } && password.any { it.isLetter() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
        return (score / 5f).coerceIn(0f, 1f)
    }
}
