package com.chittagong.localnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.chittagong.localnews.core.util.Validators
import com.chittagong.localnews.ui.theme.brandColors
import com.chittagong.localnews.ui.theme.spacing

/**
 * Password input with a reveal toggle and an optional strength meter.
 *
 * The meter is only shown on sign-up ([showStrengthMeter]) — on login it would
 * be noise, and grading an existing password is meaningless.
 */
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Outlined.Lock,
    errorMessage: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    showStrengthMeter: Boolean = false,
) {
    var revealed by remember { mutableStateOf(false) }
    val isError = errorMessage != null

    val strength = if (showStrengthMeter) Validators.passwordStrength(value) else 0f
    val animatedStrength by animateFloatAsState(targetValue = strength, label = "strength")
    val strengthColor by animateColorAsState(
        targetValue = when {
            strength >= 0.8f -> MaterialTheme.brandColors.success
            strength >= 0.5f -> MaterialTheme.brandColors.warning
            else -> MaterialTheme.colorScheme.error
        },
        label = "strengthColor",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            label = { Text(label) },
            leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { revealed = !revealed }) {
                    Icon(
                        imageVector = if (revealed) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        },
                        contentDescription = if (revealed) "Hide password" else "Show password",
                    )
                }
            },
            isError = isError,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            visualTransformation = if (revealed) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction,
            ),
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
        )

        AnimatedVisibility(visible = showStrengthMeter && value.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.xs,
                        end = MaterialTheme.spacing.xs,
                        top = MaterialTheme.spacing.sm,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            ) {
                LinearProgressIndicator(
                    progress = { animatedStrength },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp),
                    color = strengthColor,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )
                Text(
                    text = when {
                        strength >= 0.8f -> "Strong"
                        strength >= 0.5f -> "Fair"
                        else -> "Weak"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = strengthColor,
                )
            }
        }

        AnimatedVisibility(
            visible = isError,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Text(
                text = errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.md,
                    top = MaterialTheme.spacing.xs,
                ),
            )
        }
    }
}
