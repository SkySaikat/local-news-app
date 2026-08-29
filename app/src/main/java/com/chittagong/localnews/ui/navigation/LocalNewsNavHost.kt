package com.chittagong.localnews.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.chittagong.localnews.ui.auth.login.LoginScreen
import com.chittagong.localnews.ui.auth.signup.SignUpScreen
import com.chittagong.localnews.ui.auth.splash.SplashScreen
import com.chittagong.localnews.ui.main.MainShellScreen

private const val TRANSITION_MS = 320

/**
 * Root navigation graph.
 *
 * Two branches only: [AuthGraph] (splash, login, sign-up) and [MainShellRoute]
 * (the bottom-bar shell, which owns its own nested [MainGraph]). Crossing
 * between them always clears the other side's back stack, so pressing back
 * after login can never land on the login screen again.
 */
@Composable
fun LocalNewsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = AuthGraph,
        modifier = modifier,
        enterTransition = { slideInHorizontally(tween(TRANSITION_MS)) { it / 6 } + fadeIn(tween(TRANSITION_MS)) },
        exitTransition = { fadeOut(tween(TRANSITION_MS / 2)) },
        popEnterTransition = { fadeIn(tween(TRANSITION_MS)) },
        popExitTransition = {
            slideOutHorizontally(tween(TRANSITION_MS)) { it / 6 } + fadeOut(tween(TRANSITION_MS))
        },
    ) {
        navigation<AuthGraph>(startDestination = SplashRoute) {

            composable<SplashRoute>(
                // The splash is a brand moment, not a page — it dissolves rather
                // than sliding, and it is never returned to.
                exitTransition = { fadeOut(tween(TRANSITION_MS)) },
            ) {
                SplashScreen(
                    onNavigateToHome = { navController.navigateToMainShell() },
                    onNavigateToLogin = {
                        navController.navigate(LoginRoute) {
                            popUpTo(SplashRoute) { inclusive = true }
                        }
                    },
                )
            }

            composable<LoginRoute>(
                enterTransition = { fadeIn(tween(TRANSITION_MS)) + scaleIn(initialScale = 0.96f) },
            ) {
                LoginScreen(
                    onNavigateToHome = { navController.navigateToMainShell() },
                    onNavigateToSignUp = { prefilledEmail ->
                        navController.navigate(SignUpRoute(prefilledEmail = prefilledEmail))
                    },
                )
            }

            composable<SignUpRoute>(
                enterTransition = {
                    slideInHorizontally(tween(TRANSITION_MS)) { fullWidth -> fullWidth / 3 } +
                        fadeIn(tween(TRANSITION_MS))
                },
            ) { backStackEntry ->
                // Read here purely to keep the route's argument contract explicit;
                // the ViewModel resolves it again from its SavedStateHandle.
                backStackEntry.toRoute<SignUpRoute>()

                SignUpScreen(
                    onNavigateToHome = { navController.navigateToMainShell() },
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }

        composable<MainShellRoute> {
            MainShellScreen(
                onSignedOut = {
                    // Straight to login rather than back through the splash —
                    // we already know the session is gone.
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}

/** Enters the app shell and drops the entire auth back stack behind it. */
private fun NavHostController.navigateToMainShell() {
    navigate(MainShellRoute) {
        popUpTo(AuthGraph) { inclusive = true }
        launchSingleTop = true
    }
}