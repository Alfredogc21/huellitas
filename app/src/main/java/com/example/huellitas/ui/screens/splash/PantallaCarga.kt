package com.example.huellitas.ui.screens.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.huellitas.R
import com.example.huellitas.ui.theme.HuellitasTheme
import kotlinx.coroutines.delay

/**
 * Pantalla de carga con el logo oficial de Huellitas a Salvo.
 *
 * Se muestra brevemente al iniciar la app por primera vez.
 * Presenta el logo con una animación de escala suave (pulse)
 * y el nombre de la app debajo con fade-in.
 *
 * @param alTerminarCarga Callback que se ejecuta al completar la carga
 */
@Composable
fun PantallaCarga(alTerminarCarga: () -> Unit) {

    // ── Animación de logo (pulse suave) ──
    val transicionInfinita = rememberInfiniteTransition(label = "logo_pulse")
    val escalaLogo by transicionInfinita.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala_logo"
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
            .background(Color.White)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // ── Logo oficial ──
            Image(
                painter = painterResource(id = R.drawable.logo_huellitas),
                contentDescription = "Logo Huellitas a Salvo",
                modifier = Modifier
                    .size(220.dp)
                    .scale(escalaLogo)
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
