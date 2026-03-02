package com.example.huellitas.navigation

/**
 * Single source of truth for all navigation routes in the app.
 * Organized by flow for clarity and maintainability.
 */
object Routes {

    // ── Onboarding Flow (shown only on first launch) ──
    const val WELCOME = "onboarding/welcome"
    const val INTRODUCTION = "onboarding/introduction"
    const val REGISTRATION = "onboarding/registration"

    // ── Main App ──
    const val HOME = "home"
    const val ADD_ANIMAL = "add_animal"
}
