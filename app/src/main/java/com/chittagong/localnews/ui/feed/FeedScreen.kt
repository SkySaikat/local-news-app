package com.chittagong.localnews.ui.feed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chittagong.localnews.ui.components.ComingSoonScreen

/**
 * Placeholder for the map-centric home screen (proposal §5.1).
 *
 * v2.0 swaps the body for a Google Map with post markers plus a bottom-sheet
 * feed; the destination, tab and its state hoisting already exist.
 */
@Composable
fun FeedScreen(modifier: Modifier = Modifier) {
    ComingSoonScreen(
        icon = Icons.Outlined.Map,
        title = "Your neighbourhood feed",
        description = "An interactive map of alerts, deals and events happening " +
            "within walking distance of you.",
        version = "v2.0",
        upcoming = listOf(
            "Google Maps with custom alert markers",
            "Live GPS positioning via FusedLocationProvider",
            "500m / 2km / 10km proximity filters",
            "Category tabs: Alerts, Deals, Fun, Neighbour Chat",
        ),
        modifier = modifier,
    )
}
