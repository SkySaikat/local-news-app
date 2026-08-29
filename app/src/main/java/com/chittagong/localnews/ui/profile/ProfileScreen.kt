package com.chittagong.localnews.ui.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittagong.localnews.core.common.UiEvent
import com.chittagong.localnews.core.common.UiState
import com.chittagong.localnews.domain.model.TrustTier
import com.chittagong.localnews.domain.model.UserProfile
import com.chittagong.localnews.ui.components.AppSnackbarController
import com.chittagong.localnews.ui.components.SecondaryButton
import com.chittagong.localnews.ui.theme.brandColors
import com.chittagong.localnews.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The one fully-live tab in v1.0: reads the signed-in user's Firestore document
 * and offers the logout action.
 */
@Composable
fun ProfileScreen(
    onSignedOut: () -> Unit,
    snackbarController: AppSnackbarController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val isSigningOut by viewModel.isSigningOut.collectAsStateWithLifecycle()
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarController.show(event.message, event.kind)
                UiEvent.NavigateToLogin -> onSignedOut()
                UiEvent.NavigateToHome -> Unit
            }
        }
    }

    // Signing out drops the auth session, which makes the profile stream emit a
    // failure a beat before navigation happens. Hold the spinner instead of
    // flashing "we couldn't load your profile" on the way out.
    val displayState = if (isSigningOut) UiState.Loading else profileState

    AnimatedContent(
        targetState = displayState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "profileState",
        modifier = modifier.fillMaxSize(),
    ) { state ->
        when (state) {
            UiState.Loading, UiState.Idle -> ProfileLoading()

            is UiState.Error -> ProfileError(message = state.message)

            is UiState.Success -> ProfileContent(
                profile = state.data,
                isSigningOut = isSigningOut,
                onLogoutClick = { showLogoutConfirmation = true },
            )
        }
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            shape = MaterialTheme.shapes.large,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            title = { Text("Log out?") },
            text = { Text("You'll need to sign in again to post or see your neighbourhood feed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmation = false
                        viewModel.onSignOut()
                    },
                ) {
                    Text("Log out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) { Text("Stay") }
            },
        )
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    isSigningOut: Boolean,
    onLogoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(MaterialTheme.spacing.lg))

        InitialsAvatar(initials = profile.initials)

        Text(
            text = profile.displayName.ifBlank { "Neighbour" },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = MaterialTheme.spacing.md),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = profile.homeArea.ifBlank { "Chittagong" },
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.lg))

        TrustScoreCard(profile = profile)

        Spacer(Modifier.height(MaterialTheme.spacing.md))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Column(modifier = Modifier.padding(vertical = MaterialTheme.spacing.sm)) {
                ProfileRow(
                    icon = Icons.Outlined.MailOutline,
                    label = "Email",
                    value = profile.email.ifBlank { "Not provided" },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                ProfileRow(
                    icon = Icons.Outlined.Place,
                    label = "Home area",
                    value = profile.homeArea.ifBlank { "Not set" },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                ProfileRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Member since",
                    value = profile.joinedTimestamp.toJoinedDateLabel(),
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.xl))

        SecondaryButton(
            text = "Log out",
            onClick = onLogoutClick,
            loading = isSigningOut,
            icon = Icons.AutoMirrored.Outlined.Logout,
            contentColor = MaterialTheme.colorScheme.error,
        )

        Text(
            text = "Local News Chittagong · v1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.lg),
        )
    }
}

@Composable
private fun InitialsAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(104.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary,
                    ),
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun TrustScoreCard(profile: UserProfile) {
    val tier = profile.trustTier
    val tierColor = when (tier) {
        TrustTier.TrustedReporter -> MaterialTheme.brandColors.success
        TrustTier.ActiveNeighbour -> MaterialTheme.colorScheme.primary
        TrustTier.Resident -> MaterialTheme.brandColors.warning
        TrustTier.Restricted -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Column {
                        Text(
                            text = "Trust score",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = tier.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = tierColor,
                        )
                    }
                }
                Text(
                    text = profile.trustScore.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            // 300 is the ceiling the reputation system tops out at in v3.0.
            LinearProgressIndicator(
                progress = { (profile.trustScore / 300f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.md)
                    .height(8.dp),
                color = tierColor,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                gapSize = 0.dp,
                drawStopIndicator = {},
            )

            Text(
                text = "Report accurately and upvote good posts to climb the tiers.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
            )
        }
    }
}

@Composable
private fun ProfileRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.md,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ProfileLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ProfileError(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = "We couldn't load your profile",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = MaterialTheme.spacing.md),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
        )
    }
}

private fun Long.toJoinedDateLabel(): String {
    if (this <= 0L) return "Recently"
    return SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(this))
}
