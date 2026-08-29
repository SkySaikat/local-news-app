package com.chittagong.localnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.chittagong.localnews.core.common.AreaGroup
import com.chittagong.localnews.core.common.ChittagongAreas
import com.chittagong.localnews.ui.theme.spacing

/**
 * Home-area picker backed by [ChittagongAreas], rendered as scrollable sections
 * ("Chittagong city" then "Greater Chittagong").
 *
 * Read-only by design: a free-text area would fragment the data set the v3.0
 * proximity feed groups on.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaDropdown(
    selectedArea: String,
    onAreaSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Home area",
    enabled: Boolean = true,
    errorMessage: String? = null,
    groups: List<AreaGroup> = ChittagongAreas.GROUPED,
) {
    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = it },
        ) {
            OutlinedTextField(
                value = selectedArea,
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                label = { Text(label) },
                placeholder = { Text("Where in Chittagong?") },
                leadingIcon = {
                    Icon(Icons.Outlined.LocationCity, contentDescription = null)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(chevronRotation),
                    )
                },
                isError = errorMessage != null,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 420.dp),
            ) {
                groups.forEach { group ->
                    Text(
                        text = group.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(
                            start = MaterialTheme.spacing.md,
                            end = MaterialTheme.spacing.md,
                            top = MaterialTheme.spacing.md,
                            bottom = MaterialTheme.spacing.xs,
                        ),
                    )

                    group.areas.forEach { area ->
                        DropdownMenuItem(
                            text = { Text(area) },
                            onClick = {
                                onAreaSelected(area)
                                expanded = false
                            },
                            trailingIcon = {
                                if (area == selectedArea) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = errorMessage != null) {
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
