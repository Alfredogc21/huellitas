package com.example.huellitas.ui.screens.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.foundation.Canvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huellitas.R
import com.example.huellitas.ui.theme.GradientDarkEnd
import com.example.huellitas.ui.theme.GradientDarkStart
import com.example.huellitas.ui.theme.GradientEnd
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.ui.theme.HuellitasTheme
import com.example.huellitas.ui.theme.PurpleDark
import com.example.huellitas.ui.theme.PurpleText
import kotlinx.coroutines.delay

/**
 * Pantalla de bienvenida que replica el diseño del cliente.
 *
 * Muestra un fondo degradado púrpura con una forma de chevron blanco
 * en la parte superior que contiene el logo y nombre "Huellitas a Salvo".
 * Debajo muestra 3 imágenes de mascotas en un collage con esquinas
 * redondeadas, el texto "peluditos que necesitan de tu ayuda" y
 * un botón blanco "iniciar".
 *
 * @param alSiguiente Callback para navegar a la siguiente pantalla
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
    var imagenesVisibles by remember { mutableStateOf(false) }
    var textoVisible by remember { mutableStateOf(false) }
    var botonVisible by remember { mutableStateOf(false) }

    val alfaLogo by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alfa_logo"
    )
    val alfaImagenes by animateFloatAsState(
        targetValue = if (imagenesVisibles) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "alfa_imagenes"
    )
    val alfaTexto by animateFloatAsState(
        targetValue = if (textoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alfa_texto"
    )
    val alfaBoton by animateFloatAsState(
        targetValue = if (botonVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alfa_boton"
    )

    LaunchedEffect(Unit) {
        logoVisible = true
        delay(300)
        imagenesVisibles = true
        delay(300)
        textoVisible = true
        delay(200)
        botonVisible = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(coloresGradiente))
    ) {
        val anchoTotal = constraints.maxWidth.toFloat()
        val altoTotal = constraints.maxHeight.toFloat()

        // ── Forma de chevron blanco en la parte superior ──
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val chevronAlto = altoTotal * 0.35f
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(anchoTotal, 0f)
                lineTo(anchoTotal, chevronAlto * 0.65f)
                lineTo(anchoTotal / 2f, chevronAlto)
                lineTo(0f, chevronAlto * 0.65f)
                close()
            }
            drawPath(path, Color.White, style = Fill)
        }

        // ── Contenido principal ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Zona del logo (dentro del chevron blanco) ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.30f)
                    .alpha(alfaLogo),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ── Logo oficial ──
                Image(
                    painter = painterResource(id = R.drawable.logo_huellitas),
                    contentDescription = "Logo Huellitas a Salvo",
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Collage de 3 imágenes de mascotas ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 24.dp)
                    .alpha(alfaImagenes),
                contentAlignment = Alignment.Center
            ) {
                // Fila superior: 2 imágenes lado a lado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    // Imagen 1: Perrito (izquierda)
                    Image(
                        painter = painterResource(id = R.drawable.pet_dog_1),
                        contentDescription = "Perrito callejero",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Imagen 2: Perrito (derecha)
                    Image(
                        painter = painterResource(id = R.drawable.pet_dog_1),
                        contentDescription = "Perrito callejero",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    // Gatito oculto temporalmente:
                    // Image(
                    //     painter = painterResource(id = R.drawable.pet_cat_1),
                    //     contentDescription = "Gatito callejero",
                    //     modifier = Modifier
                    //         .size(150.dp)
                    //         .clip(RoundedCornerShape(20.dp)),
                    //     contentScale = ContentScale.Crop
                    // )
                }

                // Imagen 3: Cachorro (centrado abajo, superpuesto)
                Image(
                    painter = painterResource(id = R.drawable.pet_puppy_1),
                    contentDescription = "Cachorro necesitado",
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 20.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Texto descriptivo ──
            Text(
                text = "peluditos que\nnecesitan de tu\nayuda",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center,
                lineHeight = 38.sp,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .alpha(alfaTexto)
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Botón "iniciar" ──
            Button(
                onClick = alSiguiente,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
                    .height(56.dp)
                    .alpha(alfaBoton),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = PurpleText
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = "iniciar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
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
