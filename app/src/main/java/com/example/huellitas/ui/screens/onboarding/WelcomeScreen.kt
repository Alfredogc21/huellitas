package com.example.huellitas.ui.screens.onboarding

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.huellitas.ui.components.IndicadorPagina
import com.example.huellitas.ui.theme.GradientDarkEnd
import com.example.huellitas.ui.theme.GradientDarkStart
import com.example.huellitas.ui.theme.GradientEnd
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.ui.theme.HuellitasTheme
import com.example.huellitas.ui.theme.PrimaryLight
import kotlinx.coroutines.delay

/**
 * Primera pantalla de bienvenida con gradiente y animaciones.
 *
 * Muestra el logo, nombre y eslogan de la app con elementos
 * decorativos flotantes y animaciones de entrada escalonadas.
 * Solo se muestra en el primer inicio de la aplicación.
 *
 * @param alSiguiente Callback para navegar a la pantalla de introducción
 */
@Composable
fun PantallaBienvenida(alSiguiente: () -> Unit) {
    val esModoOscuro = isSystemInDarkTheme()
    val coloresGradiente = if (esModoOscuro) {
        listOf(GradientDarkStart, GradientDarkEnd)
    } else {
        listOf(GradientStart, GradientEnd)
    }

    // ── Animaciones de entrada escalonada ──
    var logoVisible by remember { mutableStateOf(false) }
    var tituloVisible by remember { mutableStateOf(false) }
    var subtituloVisible by remember { mutableStateOf(false) }
    var botonVisible by remember { mutableStateOf(false) }

    val alfaLogo by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alfa_logo"
    )
    val alfaTitulo by animateFloatAsState(
        targetValue = if (tituloVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alfa_titulo"
    )
    val alfaSubtitulo by animateFloatAsState(
        targetValue = if (subtituloVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alfa_subtitulo"
    )
    val alfaBoton by animateFloatAsState(
        targetValue = if (botonVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alfa_boton"
    )

    // Entrada escalonada de elementos
    LaunchedEffect(Unit) {
        logoVisible = true
        delay(300)
        tituloVisible = true
        delay(300)
        subtituloVisible = true
        delay(300)
        botonVisible = true
    }

    // ── Animaciones flotantes de fondo ──
    val transicionInfinita = rememberInfiniteTransition(label = "flotante")
    val desplazamiento1 by transicionInfinita.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "desplazamiento_1"
    )
    val desplazamiento2 by transicionInfinita.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "desplazamiento_2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(coloresGradiente))
            .systemBarsPadding()
    ) {
        // ── Elementos decorativos flotantes ──
        CirculoDecorativo(
            tamano = 120,
            alfa = 0.08f,
            offsetX = -30,
            offsetY = 80 + desplazamiento1.toInt()
        )
        CirculoDecorativo(
            tamano = 80,
            alfa = 0.06f,
            offsetX = 280,
            offsetY = 150 + desplazamiento2.toInt()
        )
        CirculoDecorativo(
            tamano = 60,
            alfa = 0.1f,
            offsetX = 50,
            offsetY = 600 + desplazamiento1.toInt()
        )
        CirculoDecorativo(
            tamano = 100,
            alfa = 0.07f,
            offsetX = 260,
            offsetY = 500 + desplazamiento2.toInt()
        )

        // ── Contenido principal ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // ── Logo con animación ──
            Surface(
                modifier = Modifier
                    .size(140.dp)
                    .alpha(alfaLogo),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Pets,
                    contentDescription = "Logo Huellitas",
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.surface
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Título ──
            Text(
                text = "Huellitas",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alfaTitulo)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Eslogan ──
            Text(
                text = "Cada huella cuenta.\nAyuda a encontrar hogar a quienes\nmás lo necesitan.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alfaSubtitulo)
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Indicador de página ──
            IndicadorPagina(
                totalPaginas = 2,
                paginaActual = 0,
                colorActivo = MaterialTheme.colorScheme.surface,
                colorInactivo = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Botón de acción ──
            Button(
                onClick = alSiguiente,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(alfaBoton),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryLight
                )
            ) {
                Text(
                    text = "Comenzar",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Círculo decorativo translúcido flotante para el fondo.
 * Aporta profundidad y dinamismo visual a la pantalla.
 */
@Composable
private fun CirculoDecorativo(
    tamano: Int,
    alfa: Float,
    offsetX: Int,
    offsetY: Int
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX, offsetY) }
            .size(tamano.dp)
            .alpha(alfa)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaBienvenidaPreview() {
    HuellitasTheme {
        PantallaBienvenida(alSiguiente = {})
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PantallaBienvenidaOscuraPreview() {
    HuellitasTheme(darkTheme = true) {
        PantallaBienvenida(alSiguiente = {})
    }
}
