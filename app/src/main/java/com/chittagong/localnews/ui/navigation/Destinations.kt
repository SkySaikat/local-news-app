package com.chittagong.localnews.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddLocationAlt
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes.
 *
 * Every destination is a `@Serializable` type rather than a string, so
 * arguments are checked by the compiler and a typo can't produce a runtime
 * "destination not found" crash.
 */

/** Pre-authentication graph: splash → login ⇄ sign-up. */
@Serializable
data object AuthGraph

@Serializable
data object SplashRoute

@Serializable
data object LoginRoute

/** [prefilledEmail] carries the address over when a user taps "Create account". */
@Serializable
data class SignUpRoute(val prefilledEmail: String = "")

/** Post-authentication shell hosting the bottom navigation bar. */
@Serializable
data object MainShellRoute

/** The tab graph nested inside [MainShellRoute]. */
@Serializable
data object MainGraph

@Serializable
data object FeedRoute

@Serializable
data object AddSpotRoute

@Serializable
data object ProfileRoute

/**
 * The three bottom-bar entries. Held as an enum so the bar, the inner NavHost
 * and the selected-state logic can never drift apart.
 */
enum class TopLevelDestination(
    val route: Any,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val contentDescription: String,
) {
    Feed(
        route = FeedRoute,
        label = "Feed",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        contentDescription = "Nearby feed and map",
    ),
    AddSpot(
        route = AddSpotRoute,
        label = "Add Spot",
        selectedIcon = Icons.Filled.AddLocationAlt,
        unselectedIcon = Icons.Outlined.AddLocationAlt,
        contentDescription = "Report a new spot",
    ),
    Profile(
        route = ProfileRoute,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        contentDescription = "Your profile",
    ),
}
