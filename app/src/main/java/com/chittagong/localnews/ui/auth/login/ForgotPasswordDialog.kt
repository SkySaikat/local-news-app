package com.chittagong.localnews.ui.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.chittagong.localnews.ui.components.AppTextField
import com.chittagong.localnews.ui.theme.spacing

/**
 * Password reset flow. Kept as a dialog rather than a screen so the user never
 * loses the email they already typed on the form behind it.
 */
@Composable
fun ForgotPasswordDialog(
    email: String,
    onEmailChange: (String) -> Unit,
    errorMessage: String?,
    isSending: Boolean,
    onDismiss: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        icon = {
            Icon(
                imageVector = Icons.Outlined.LockReset,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = { Text(text = "Reset your password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                Text(
                    text = "We'll email you a secure link to set a new password.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AppTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    leadingIcon = Icons.Outlined.MailOutline,
                    errorMessage = errorMessage,
                    enabled = !isSending,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = { onSend() }),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSend, enabled = !isSending) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Send link")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSending) {
                Text("Cancel")
            }
        },
    )
}
