package com.chittagong.localnews.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Brand palette generated from a Karnaphuli-teal seed (#00696E) with a warm
 * amber tertiary that stands in for Chittagong's alert/vendor accents.
 *
 * These are the fallback schemes: on Android 12+ dynamic color takes over
 * unless the user disables it in Settings.
 */

// ---- Light ----
val LightPrimary = Color(0xFF00696E)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFF9CF1F6)
val LightOnPrimaryContainer = Color(0xFF002022)

val LightSecondary = Color(0xFF4A6365)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFCCE8E9)
val LightOnSecondaryContainer = Color(0xFF051F21)

val LightTertiary = Color(0xFF8B5000)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFFFDCBE)
val LightOnTertiaryContainer = Color(0xFF2C1600)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

val LightBackground = Color(0xFFF4FBFA)
val LightOnBackground = Color(0xFF161D1D)
val LightSurface = Color(0xFFF4FBFA)
val LightOnSurface = Color(0xFF161D1D)
val LightSurfaceVariant = Color(0xFFDAE4E4)
val LightOnSurfaceVariant = Color(0xFF3F4949)
val LightOutline = Color(0xFF6F7979)
val LightOutlineVariant = Color(0xFFBEC8C8)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFEFF5F5)
val LightSurfaceContainer = Color(0xFFE9EFEF)
val LightSurfaceContainerHigh = Color(0xFFE3EAE9)
val LightSurfaceContainerHighest = Color(0xFFDDE4E4)
val LightInverseSurface = Color(0xFF2B3231)
val LightInverseOnSurface = Color(0xFFECF2F1)
val LightInversePrimary = Color(0xFF80D4DA)
val LightScrim = Color(0xFF000000)

// ---- Dark ----
val DarkPrimary = Color(0xFF80D4DA)
val DarkOnPrimary = Color(0xFF00363A)
val DarkPrimaryContainer = Color(0xFF004F53)
val DarkOnPrimaryContainer = Color(0xFF9CF1F6)

val DarkSecondary = Color(0xFFB0CCCD)
val DarkOnSecondary = Color(0xFF1B3436)
val DarkSecondaryContainer = Color(0xFF324B4D)
val DarkOnSecondaryContainer = Color(0xFFCCE8E9)

val DarkTertiary = Color(0xFFFFB865)
val DarkOnTertiary = Color(0xFF4A2800)
val DarkTertiaryContainer = Color(0xFF693C00)
val DarkOnTertiaryContainer = Color(0xFFFFDCBE)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

val DarkBackground = Color(0xFF0E1515)
val DarkOnBackground = Color(0xFFDDE4E4)
val DarkSurface = Color(0xFF0E1515)
val DarkOnSurface = Color(0xFFDDE4E4)
val DarkSurfaceVariant = Color(0xFF3F4949)
val DarkOnSurfaceVariant = Color(0xFFBEC8C8)
val DarkOutline = Color(0xFF899393)
val DarkOutlineVariant = Color(0xFF3F4949)
val DarkSurfaceContainerLowest = Color(0xFF090F0F)
val DarkSurfaceContainerLow = Color(0xFF161D1D)
val DarkSurfaceContainer = Color(0xFF1A2121)
val DarkSurfaceContainerHigh = Color(0xFF252B2B)
val DarkSurfaceContainerHighest = Color(0xFF303636)
val DarkInverseSurface = Color(0xFFDDE4E4)
val DarkInverseOnSurface = Color(0xFF2B3231)
val DarkInversePrimary = Color(0xFF00696E)
val DarkScrim = Color(0xFF000000)

/**
 * Semantic colours that Material's scheme has no slot for. Exposed through
 * [LocalBrandColors] so screens read them the same way they read the scheme.
 */
data class BrandColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
)

val LightBrandColors = BrandColors(
    success = Color(0xFF2E6B4F),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFB6F2D0),
    warning = Color(0xFF8B5000),
    warningContainer = Color(0xFFFFDCBE),
)

val DarkBrandColors = BrandColors(
    success = Color(0xFF8FD6AE),
    onSuccess = Color(0xFF00391F),
    successContainer = Color(0xFF14512F),
    warning = Color(0xFFFFB865),
    warningContainer = Color(0xFF693C00),
)
