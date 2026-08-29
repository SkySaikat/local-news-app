package com.chittagong.localnews.ui.addspot

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chittagong.localnews.ui.components.ComingSoonScreen

/**
 * Placeholder for the unified 'Add Spot' form (proposal §5.1).
 */
@Composable
fun AddSpotScreen(modifier: Modifier = Modifier) {
    ComingSoonScreen(
        icon = Icons.Outlined.AddAPhoto,
        title = "Report a spot",
        description = "Drop a pin on what you're seeing right now — waterlogging, " +
            "a traffic jam, a pop-up sale — and everyone nearby gets it.",
        version = "v2.0",
        upcoming = listOf(
            "Camera and gallery capture with preview",
            "Automatic GPS pinning and geohash indexing",
            "Category tagging and event scheduling",
            "Firebase Storage image upload",
        ),
        modifier = modifier,
    )
}
