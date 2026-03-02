package com.example.huellitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.huellitas.navigation.HuellitasNavHost
import com.example.huellitas.ui.theme.HuellitasTheme

/**
 * Single Activity entry point for Huellitas.
 *
 * Uses edge-to-edge display and delegates all UI
 * to Jetpack Compose via [HuellitasNavHost].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuellitasTheme {
                HuellitasMainContent()
            }
        }
    }
}

/**
 * Root composable that wires up navigation.
 *
 * Manages onboarding completion state to determine the start
 * destination. On first launch the user sees the onboarding flow;
 * on subsequent launches they go straight to the animal list.
 *
 * Note: In production, [isOnboardingCompleted] should be persisted
 * using Jetpack DataStore or SharedPreferences so it survives
 * process death and app reinstalls.
 */
@Composable
private fun HuellitasMainContent() {
    val navController = rememberNavController()

    // TODO: Replace with DataStore for real persistence across sessions
    var isOnboardingCompleted by rememberSaveable { mutableStateOf(false) }

    HuellitasNavHost(
        navController = navController,
        isOnboardingCompleted = isOnboardingCompleted,
        onOnboardingComplete = { isOnboardingCompleted = true }
    )
}
