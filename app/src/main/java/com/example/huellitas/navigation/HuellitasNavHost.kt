package com.example.huellitas.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.huellitas.ui.screens.home.PantallaListaAnimales
import com.example.huellitas.ui.screens.onboarding.PantallaBienvenida
import com.example.huellitas.ui.screens.onboarding.PantallaIntroduccion
import com.example.huellitas.ui.screens.registration.PantallaRegistroAnimal
import com.example.huellitas.ui.screens.splash.PantallaCarga
import com.example.huellitas.viewmodel.AnimalListViewModel

private const val DURACION_ANIMACION = 400

/**
 * Host de navegación principal de Huellitas.
 *
 * Gestiona los flujos:
 * 1. **Carga**: Animación Lottie de precarga
 * 2. **Bienvenida**: Pantalla de bienvenida → Introducción → [Ver animales | Registrar]
 * 3. **App principal**: Lista de animales con opción de agregar nuevos
 *
 * @param controladorNav Controlador de navegación que gestiona el back stack
 * @param bienvenidaCompletada Si el usuario ya completó la bienvenida
 * @param alCompletarBienvenida Callback para marcar la bienvenida como completada
 */
@Composable
fun NavHostHuellitas(
    controladorNav: NavHostController,
    bienvenidaCompletada: Boolean,
    alCompletarBienvenida: () -> Unit
) {
    val destinoInicial = if (bienvenidaCompletada) Rutas.INICIO else Rutas.CARGA

    // ViewModel compartido: la pantalla de lista y el callback de registro
    // comparten la misma instancia para que al registrar se recargue la lista.
    val listViewModel: AnimalListViewModel = viewModel()

    NavHost(
        navController = controladorNav,
        startDestination = destinoInicial,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { ancho -> ancho },
                animationSpec = tween(DURACION_ANIMACION)
            ) + fadeIn(animationSpec = tween(DURACION_ANIMACION))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { ancho -> -ancho },
                animationSpec = tween(DURACION_ANIMACION)
            ) + fadeOut(animationSpec = tween(DURACION_ANIMACION))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { ancho -> -ancho },
                animationSpec = tween(DURACION_ANIMACION)
            ) + fadeIn(animationSpec = tween(DURACION_ANIMACION))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { ancho -> ancho },
                animationSpec = tween(DURACION_ANIMACION)
            ) + fadeOut(animationSpec = tween(DURACION_ANIMACION))
        }
    ) {
        // ── Pantalla de carga (Lottie) ──────────────────────────────

        composable(Rutas.CARGA) {
            PantallaCarga(
                alTerminarCarga = {
                    controladorNav.navigate(Rutas.BIENVENIDA) {
                        popUpTo(Rutas.CARGA) { inclusive = true }
                    }
                }
            )
        }

        // ── Bienvenida ──────────────────────────────────────────────

        composable(Rutas.BIENVENIDA) {
            PantallaBienvenida(
                alSiguiente = { controladorNav.navigate(Rutas.INTRODUCCION) }
            )
        }

        composable(Rutas.INTRODUCCION) {
            PantallaIntroduccion(
                alVerAnimales = {
                    alCompletarBienvenida()
                    controladorNav.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.CARGA) { inclusive = true }
                    }
                },
                alRegistrarAnimal = {
                    alCompletarBienvenida()
                    // Navegamos a INICIO primero para tener back stack correcto
                    controladorNav.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.CARGA) { inclusive = true }
                    }
                    controladorNav.navigate(Rutas.REGISTRAR_ANIMAL)
                }
            )
        }

        // ── Aplicación principal ────────────────────────────────────

        composable(Rutas.INICIO) {
            PantallaListaAnimales(
                alNavegarARegistro = { controladorNav.navigate(Rutas.REGISTRAR_ANIMAL) },
                viewModel = listViewModel
            )
        }

        composable(Rutas.REGISTRAR_ANIMAL) {
            PantallaRegistroAnimal(
                alCompletarRegistro = {
                    controladorNav.popBackStack()
                    // Recargar la lista en segundo plano al volver del registro
                    listViewModel.refrescar()
                }
            )
        }
    }
}
