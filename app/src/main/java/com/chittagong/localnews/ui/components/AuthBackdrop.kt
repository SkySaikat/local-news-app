package com.chittagong.localnews.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shared backdrop for the auth screens: a soft vertical wash plus two colour
 * orbs.
 *
 * The orbs are radial gradients fading to transparent rather than blurred
 * circles — `Modifier.blur` is a no-op below API 31, and this renders
 * identically on every supported device.
 */
@Composable
fun AuthBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val orbPrimary = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
    val orbTertiary = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f)

    Box(
        modifier = modifier
            .fillMaxSize()
            // Base surface first, then a translucent wash on top: the tint is
            // derived from the active scheme, so it tracks Material You dynamic
            // color instead of fighting it with a hard-coded brand teal.
            .background(MaterialTheme.colorScheme.surface)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        Color.Transparent,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-110).dp, y = (-130).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(orbPrimary, Color.Transparent),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 110.dp, y = 60.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(orbTertiary, Color.Transparent),
                    ),
                ),
        )
        content()
    }
}
