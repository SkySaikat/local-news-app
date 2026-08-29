package com.chittagong.localnews.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chittagong.localnews.ui.addspot.AddSpotScreen
import com.chittagong.localnews.ui.components.AppSnackbarHost
import com.chittagong.localnews.ui.components.rememberAppSnackbarController
import com.chittagong.localnews.ui.feed.FeedScreen
import com.chittagong.localnews.ui.navigation.AddSpotRoute
import com.chittagong.localnews.ui.navigation.FeedRoute
import com.chittagong.localnews.ui.navigation.MainGraph
import com.chittagong.localnews.ui.navigation.ProfileRoute
import com.chittagong.localnews.ui.navigation.TopLevelDestination
import com.chittagong.localnews.ui.profile.ProfileScreen

/**
 * The authenticated application shell.
 *
 * Owns its own [NavHostController] so tab switching never touches the root
 * graph — the root only knows "auth" versus "signed in".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainShellScreen(
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentTab = currentDestination.toTopLevelDestination()
    val snackbarController = rememberAppSnackbarController()

    Scaffold(
        modifier = modifier,
        snackbarHost = { AppSnackbarHost(snackbarController) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = currentTab.label,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 0.dp,
            ) {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = currentDestination.isSelected(destination)
                    val iconScale by animateFloatAsState(
                        targetValue = if (selected) 1.1f else 1f,
                        animationSpec = spring(dampingRatio = 0.55f),
                        label = "tabIcon",
                    )

                    NavigationBarItem(
                        selected = selected,
                        onClick = { navController.navigateToTab(destination) },
                        icon = {
                            Icon(
                                imageVector = if (selected) {
                                    destination.selectedIcon
                                } else {
                                    destination.unselectedIcon
                                },
                                contentDescription = destination.contentDescription,
                                modifier = Modifier.scale(iconScale),
                            )
                        },
                        label = { Text(destination.label) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            route = MainGraph::class,
            startDestination = FeedRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(180)) + scaleIn(initialScale = 0.98f) },
            exitTransition = { fadeOut(animationSpec = tween(120)) },
            popEnterTransition = { fadeIn(animationSpec = tween(180)) + scaleIn(initialScale = 0.98f) },
            popExitTransition = { fadeOut(animationSpec = tween(120)) + scaleOut(targetScale = 0.98f) },
        ) {
            composable<FeedRoute> { FeedScreen() }
            composable<AddSpotRoute> { AddSpotScreen() }
            composable<ProfileRoute> {
                ProfileScreen(
                    onSignedOut = onSignedOut,
                    snackbarController = snackbarController,
                )
            }
        }
    }
}

/**
 * Standard bottom-bar navigation: single-top, state-saving, and popping back to
 * the graph's start so the back stack can't grow one entry per tab tap.
 */
private fun NavHostController.navigateToTab(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavDestination?.isSelected(destination: TopLevelDestination): Boolean =
    this?.hierarchy?.any { it.hasRoute(destination.route::class) } == true

private fun NavDestination?.toTopLevelDestination(): TopLevelDestination =
    TopLevelDestination.entries.firstOrNull { isSelected(it) } ?: TopLevelDestination.Feed
