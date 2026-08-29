package com.chittagong.localnews.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chittagong.localnews.core.common.SnackbarKind
import com.chittagong.localnews.ui.theme.brandColors
import com.chittagong.localnews.ui.theme.spacing

/**
 * Pairs a [SnackbarHostState] with the severity of the message currently being
 * shown, so the host can colour itself without smuggling metadata through the
 * message string.
 */
@Stable
class AppSnackbarController(val hostState: SnackbarHostState) {

    var currentKind: SnackbarKind by mutableStateOf(SnackbarKind.Info)
        private set

    suspend fun show(
        message: String,
        kind: SnackbarKind = SnackbarKind.Info,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        currentKind = kind
        hostState.showSnackbar(message = message, duration = duration)
    }
}

@Composable
fun rememberAppSnackbarController(): AppSnackbarController {
    val hostState = remember { SnackbarHostState() }
    return remember(hostState) { AppSnackbarController(hostState) }
}

/** Snackbar host that colours and icons itself by the controller's current kind. */
@Composable
fun AppSnackbarHost(
    controller: AppSnackbarController,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = controller.hostState, modifier = modifier) { data ->
        val kind = controller.currentKind

        val containerColor = when (kind) {
            SnackbarKind.Success -> MaterialTheme.brandColors.successContainer
            SnackbarKind.Error -> MaterialTheme.colorScheme.errorContainer
            SnackbarKind.Info -> MaterialTheme.colorScheme.inverseSurface
        }
        val contentColor = when (kind) {
            SnackbarKind.Success -> MaterialTheme.colorScheme.onSurface
            SnackbarKind.Error -> MaterialTheme.colorScheme.onErrorContainer
            SnackbarKind.Info -> MaterialTheme.colorScheme.inverseOnSurface
        }
        val icon = when (kind) {
            SnackbarKind.Success -> Icons.Outlined.CheckCircle
            SnackbarKind.Error -> Icons.Outlined.ErrorOutline
            SnackbarKind.Info -> Icons.Outlined.Info
        }

        Snackbar(
            modifier = Modifier.padding(MaterialTheme.spacing.md),
            shape = MaterialTheme.shapes.medium,
            containerColor = containerColor,
            contentColor = contentColor,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor,
                )
                Text(text = data.visuals.message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
