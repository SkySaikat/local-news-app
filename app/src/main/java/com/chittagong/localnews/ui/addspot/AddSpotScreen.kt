package com.chittagong.localnews.ui.addspot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittagong.localnews.core.common.UiEvent
import com.chittagong.localnews.domain.model.PostCategory
import com.chittagong.localnews.ui.components.AppSnackbarHost
import com.chittagong.localnews.ui.components.AppTextField
import com.chittagong.localnews.ui.components.AreaDropdown
import com.chittagong.localnews.ui.components.PrimaryButton
import com.chittagong.localnews.ui.components.rememberAppSnackbarController
import com.chittagong.localnews.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddSpotScreen(
    modifier: Modifier = Modifier,
    viewModel: AddSpotViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = rememberAppSnackbarController()
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarController.show(event.message, event.kind)
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report a spot") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarController) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.md)
        ) {
            AppTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = "Title",
                placeholder = "E.g., Waterlogging on Agrabad Road",
                enabled = !uiState.isSubmitting
            )

            Spacer(Modifier.height(MaterialTheme.spacing.md))

            AppTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = "Description",
                placeholder = "Add more details about what's happening...",
                singleLine = false,
                modifier = Modifier.height(120.dp),
                enabled = !uiState.isSubmitting
            )

            Spacer(Modifier.height(MaterialTheme.spacing.md))

            AreaDropdown(
                selectedArea = uiState.area,
                onAreaSelected = viewModel::onAreaChange,
                enabled = !uiState.isSubmitting
            )

            Spacer(Modifier.height(MaterialTheme.spacing.lg))

            Text(
                text = "Category",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.xs)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                PostCategory.entries.filter { it != PostCategory.OTHER }.forEach { category ->
                    FilterChip(
                        selected = uiState.category == category,
                        onClick = { viewModel.onCategoryChange(category) },
                        label = { Text(category.name) },
                        enabled = !uiState.isSubmitting
                    )
                }
            }

            Spacer(Modifier.height(MaterialTheme.spacing.xl))

            PrimaryButton(
                text = "Submit Spot",
                onClick = {
                    keyboard?.hide()
                    viewModel.onSubmit()
                },
                enabled = uiState.isSubmitEnabled,
                loading = uiState.isSubmitting,
                icon = Icons.Default.Send
            )

            Spacer(Modifier.height(MaterialTheme.spacing.xl))
        }
    }
}
