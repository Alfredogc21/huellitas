package com.example.huellitas.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.huellitas.R
import com.example.huellitas.ui.theme.GradientDarkEnd
import com.example.huellitas.ui.theme.GradientDarkStart
import com.example.huellitas.ui.theme.GradientEnd
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.ui.theme.HuellitasTheme
import kotlinx.coroutines.delay

/**
 * Pantalla de carga con animación Lottie.
 *
 * Se muestra brevemente al iniciar la app por primera vez.
 * La animación Lottie de huellita se reproduce con un fondo degradado
 * y transiciona automáticamente a la pantalla de bienvenida.
 *
 * @param alTerminarCarga Callback que se ejecuta al completar la carga
 */
@Composable
fun PantallaCarga(alTerminarCarga: () -> Unit) {

    val esModoOscuro = isSystemInDarkTheme()
    val coloresGradiente = if (esModoOscuro) {
        listOf(GradientDarkStart, GradientDarkEnd)
    } else {
        listOf(GradientStart, GradientEnd)
    }

    // ── Animación Lottie ──
    val composicion by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.animacion_huellitas)
    )
    val progreso by animateLottieCompositionAsState(
        composition = composicion,
        iterations = LottieConstants.IterateForever
    )

    // ── Animación de texto (fade in) ──
    var textoVisible by remember { mutableStateOf(false) }
    val alfaTexto by animateFloatAsState(
        targetValue = if (textoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "alfa_texto"
    )

    // Controla el tiempo de la pantalla de carga
    LaunchedEffect(Unit) {
        textoVisible = true
        delay(3000L) // 3 segundos de animación
        alTerminarCarga()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(coloresGradiente))
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // ── Animación Lottie de huella ──
            LottieAnimation(
                composition = composicion,
                progress = { progreso },
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Nombre de la app ──
            Text(
                text = "Huellitas",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.alpha(alfaTexto)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cada huella cuenta",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                modifier = Modifier.alpha(alfaTexto)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaCargaPreview() {
    HuellitasTheme {
        PantallaCarga(alTerminarCarga = {})
    }
}
