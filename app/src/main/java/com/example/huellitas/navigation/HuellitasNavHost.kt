package com.example.huellitas.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.huellitas.ui.screens.home.AnimalListScreen
import com.example.huellitas.ui.screens.onboarding.IntroductionScreen
import com.example.huellitas.ui.screens.onboarding.WelcomeScreen
import com.example.huellitas.ui.screens.registration.AnimalRegistrationScreen

private const val ANIMATION_DURATION = 400

/**
 * Main navigation host for Huellitas.
 *
 * Manages two flows:
 * 1. **Onboarding**: Welcome → Introduction → Registration (first launch only)
 * 2. **Main App**: Animal list with option to add new animals
 *
 * @param navController The navigation controller managing back stack
 * @param isOnboardingCompleted Whether the user has completed onboarding
 * @param onOnboardingComplete Callback to mark onboarding as done
 */
@Composable
fun HuellitasNavHost(
    navController: NavHostController,
    isOnboardingCompleted: Boolean,
    onOnboardingComplete: () -> Unit
) {
    val startDestination = if (isOnboardingCompleted) Routes.HOME else Routes.WELCOME

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
        }
    ) {
        // ── Onboarding ──────────────────────────────────────────────

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onNext = { navController.navigate(Routes.INTRODUCTION) }
            )
        }

        composable(Routes.INTRODUCTION) {
            IntroductionScreen(
                onNext = { navController.navigate(Routes.REGISTRATION) }
            )
        }

        composable(Routes.REGISTRATION) {
            AnimalRegistrationScreen(
                onRegistrationComplete = {
                    onOnboardingComplete()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                isOnboarding = true
            )
        }

        // ── Main App ────────────────────────────────────────────────

        composable(Routes.HOME) {
            AnimalListScreen(
                onNavigateToRegister = { navController.navigate(Routes.ADD_ANIMAL) }
            )
        }

        composable(Routes.ADD_ANIMAL) {
            AnimalRegistrationScreen(
                onRegistrationComplete = { navController.popBackStack() },
                isOnboarding = false
            )
        }
    }
}
