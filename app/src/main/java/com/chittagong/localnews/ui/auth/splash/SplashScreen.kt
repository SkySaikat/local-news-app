package com.chittagong.localnews.ui.auth.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittagong.localnews.ui.components.AuthBackdrop
import com.chittagong.localnews.ui.components.BrandMark
import com.chittagong.localnews.ui.theme.spacing

/**
 * First frame after the system splash. Holds for a beat while the Firebase
 * session is checked, then routes to the main shell or the login screen.
 */
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Authenticated -> onNavigateToHome()
            SplashDestination.Unauthenticated -> onNavigateToLogin()
            SplashDestination.Checking -> Unit
        }
    }

    // Mark springs up and settles; the wordmark fades in just behind it.
    val markScale = remember { Animatable(0.7f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        markScale.animateTo(1f, tween(durationMillis = 600, easing = EaseOutBack))
    }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, tween(durationMillis = 500, delayMillis = 180))
    }

    val pulse = rememberInfiniteTransition(label = "pulse")
    val haloScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "halo",
    )

    AuthBackdrop(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(haloScale)
                        .alpha(0.16f)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        ),
                )
                BrandMark(modifier = Modifier.scale(markScale.value), size = 88.dp)
            }

            Text(
                text = "Local News",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.lg)
                    .alpha(contentAlpha.value),
            )
            Text(
                text = "Chittagong",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(contentAlpha.value),
            )
            Text(
                text = "Real-time alerts from your street, not an algorithm.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.md)
                    .alpha(contentAlpha.value * 0.9f),
            )
        }
    }
}
