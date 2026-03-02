package com.example.huellitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.huellitas.navigation.NavHostHuellitas
import com.example.huellitas.ui.theme.HuellitasTheme

/**
 * Actividad principal y único punto de entrada de Huellitas.
 *
 * Utiliza pantalla edge-to-edge y delega toda la interfaz
 * a Jetpack Compose mediante [NavHostHuellitas].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuellitasTheme {
                ContenidoPrincipal()
            }
        }
    }
}

/**
 * Composable raíz que conecta la navegación.
 *
 * Gestiona el estado de bienvenida completada para determinar
 * el destino inicial. En el primer inicio se muestra la carga
 * y bienvenida; después se va directo a la lista de animales.
 *
 * Nota: En producción, `bienvenidaCompletada` debería persistirse
 * usando Jetpack DataStore o SharedPreferences para sobrevivir
 * al cierre de la app y reinstalaciones.
 */
@Composable
private fun ContenidoPrincipal() {
    val controladorNav = rememberNavController()

    // TODO: Reemplazar con DataStore para persistencia real entre sesiones
    val estadoBienvenida = rememberSaveable { mutableStateOf(false) }

    NavHostHuellitas(
        controladorNav = controladorNav,
        bienvenidaCompletada = estadoBienvenida.value,
        alCompletarBienvenida = { estadoBienvenida.value = true }
    )
}
