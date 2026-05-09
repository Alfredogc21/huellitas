package com.example.huellitas.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.huellitas.ui.screens.admin.PantallaAdminPanel
import com.example.huellitas.ui.screens.admin.PantallaAgregarVeterinario
import com.example.huellitas.ui.screens.admin.PantallaLoginAdmin
import com.example.huellitas.ui.screens.admin.PantallaVeterinariosAdmin
import com.example.huellitas.ui.screens.admin.PantallaRegistroUsuario
import com.example.huellitas.ui.screens.admin.PantallaTutorialAdmin
import com.example.huellitas.ui.screens.adopcion.PantallaAdopcion
import com.example.huellitas.ui.screens.home.PantallaListaAnimales
import com.example.huellitas.ui.screens.onboarding.PantallaBienvenida
import com.example.huellitas.ui.screens.onboarding.PantallaIntroduccion
import com.example.huellitas.ui.screens.onboarding.PantallaTutorial
import com.example.huellitas.ui.screens.registration.PantallaRegistroAnimal
import com.example.huellitas.ui.screens.splash.PantallaCarga
import com.example.huellitas.ui.screens.veterinario.PantallaRegistrarPacienteVet
import com.example.huellitas.ui.screens.veterinario.PantallaVeterinario
import com.example.huellitas.viewmodel.AnimalListViewModel
import com.example.huellitas.viewmodel.AuthViewModel
import com.example.huellitas.viewmodel.VeterinarioViewModel

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

    // ViewModel compartido para autenticación (login y registro)
    val authViewModel: AuthViewModel = viewModel()

    // ViewModel compartido de veterinarios (lista + agregar comparten misma instancia)
    val vetListViewModel: VeterinarioViewModel = viewModel()

    // Estado para controlar si ya se vio el tutorial de admin
    val tutorialAdminVisto = rememberSaveable { mutableStateOf(false) }

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
                alSiguiente = {
                    alCompletarBienvenida()
                    controladorNav.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.CARGA) { inclusive = true }
                    }
                }
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
                alNavegarATutorial = { controladorNav.navigate(Rutas.TUTORIAL) },
                alNavegarAAdmin = { controladorNav.navigate(Rutas.ADMIN_LOGIN) },
                alNavegarAAdopcion = { controladorNav.navigate(Rutas.ADOPCION) },
                viewModel = listViewModel
            )
        }

        composable(Rutas.TUTORIAL) {
            PantallaTutorial(
                alFinalizar = {
                    controladorNav.popBackStack()
                    controladorNav.navigate(Rutas.REGISTRAR_ANIMAL)
                },
                alRegresar = { controladorNav.popBackStack() }
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

        // ── Flujo de administración ─────────────────────────────────

        composable(Rutas.ADMIN_LOGIN) {
            PantallaLoginAdmin(
                alIniciarSesion = { rolId, userId ->
                    // rolId 1 = Admin, 2 = Veterinario
                    if (rolId == 2) {
                        // Veterinario: ir directo al panel vet con su ID
                        controladorNav.navigate(Rutas.vetPanel(userId)) {
                            popUpTo(Rutas.ADMIN_LOGIN) { inclusive = true }
                        }
                    } else {
                        // Admin: mostrar tutorial si es la primera vez
                        if (!tutorialAdminVisto.value) {
                            controladorNav.navigate(Rutas.ADMIN_TUTORIAL) {
                                popUpTo(Rutas.ADMIN_LOGIN) { inclusive = true }
                            }
                        } else {
                            controladorNav.navigate(Rutas.ADMIN_PANEL) {
                                popUpTo(Rutas.ADMIN_LOGIN) { inclusive = true }
                            }
                        }
                    }
                },
                alVolver = { controladorNav.popBackStack() },
                alIrARegistro = {
                    controladorNav.navigate(Rutas.ADMIN_REGISTRO)
                },
                authViewModel = authViewModel
            )
        }

        composable(Rutas.ADMIN_REGISTRO) {
            PantallaRegistroUsuario(
                alRegistroExitoso = {
                    if (!tutorialAdminVisto.value) {
                        controladorNav.navigate(Rutas.ADMIN_TUTORIAL) {
                            popUpTo(Rutas.ADMIN_LOGIN) { inclusive = true }
                        }
                    } else {
                        controladorNav.navigate(Rutas.ADMIN_PANEL) {
                            popUpTo(Rutas.ADMIN_LOGIN) { inclusive = true }
                        }
                    }
                },
                alIrALogin = {
                    controladorNav.popBackStack()
                },
                authViewModel = authViewModel
            )
        }

        composable(Rutas.ADMIN_TUTORIAL) {
            PantallaTutorialAdmin(
                alFinalizar = {
                    tutorialAdminVisto.value = true
                    controladorNav.navigate(Rutas.ADMIN_PANEL) {
                        popUpTo(Rutas.ADMIN_TUTORIAL) { inclusive = true }
                    }
                },
                alRegresar = {
                    controladorNav.popBackStack()
                    controladorNav.navigate(Rutas.ADMIN_LOGIN)
                }
            )
        }

        composable(Rutas.ADMIN_PANEL) {
            PantallaAdminPanel(
                alCerrarSesion = {
                    controladorNav.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.ADMIN_PANEL) { inclusive = true }
                    }
                },
                alNavegarAVeterinarios = { controladorNav.navigate(Rutas.ADMIN_VETERINARIOS) },
                alNavegarAAdopciones = { controladorNav.navigate(Rutas.ADOPCION) }
            )
        }

        composable(Rutas.ADMIN_VETERINARIOS) {
            PantallaVeterinariosAdmin(
                alVolver = { controladorNav.popBackStack() },
                alAgregarVeterinario = { controladorNav.navigate(Rutas.ADMIN_AGREGAR_VET) },
                viewModel = vetListViewModel
            )
        }

        composable(Rutas.ADMIN_AGREGAR_VET) {
            PantallaAgregarVeterinario(
                alVolver = { controladorNav.popBackStack() },
                viewModel = vetListViewModel
            )
        }

        composable(
            route = Rutas.VET_PANEL,
            arguments = listOf(navArgument("vetId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vetId = backStackEntry.arguments?.getInt("vetId") ?: 0
            PantallaVeterinario(
                vetId = vetId,
                alCerrarSesion = {
                    controladorNav.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.VET_PANEL) { inclusive = true }
                    }
                },
                alRegistrarAnimal = { id ->
                    controladorNav.navigate(Rutas.vetRegistrarPaciente(id))
                }
            )
        }

        composable(
            route = Rutas.VET_REGISTRAR_PACIENTE,
            arguments = listOf(navArgument("vetId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vetId = backStackEntry.arguments?.getInt("vetId") ?: 0
            PantallaRegistrarPacienteVet(
                vetId = vetId,
                alVolver = { controladorNav.popBackStack() },
                alRegistrado = { controladorNav.popBackStack() }
            )
        }

        composable(Rutas.ADOPCION) {
            PantallaAdopcion(
                alVolver = { controladorNav.popBackStack() }
            )
        }
    }
}
