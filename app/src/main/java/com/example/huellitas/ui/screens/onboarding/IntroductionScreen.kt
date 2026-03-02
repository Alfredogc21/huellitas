package com.example.huellitas.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.huellitas.ui.components.IndicadorPagina
import com.example.huellitas.ui.theme.HuellitasTheme

/**
 * Segunda pantalla de bienvenida — explica el propósito de la app
 * mediante 3 tarjetas de características ilustradas.
 *
 * Al finalizar, presenta dos opciones de navegación:
 * - Ver la lista de animales registrados
 * - Registrar un nuevo animal
 *
 * @param alVerAnimales Callback para navegar a la lista de animales
 * @param alRegistrarAnimal Callback para navegar al formulario de registro
 */
@Composable
fun PantallaIntroduccion(
    alVerAnimales: () -> Unit,
    alRegistrarAnimal: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Encabezado ──
            Text(
                text = "¿Cómo funciona?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tres simples pasos para hacer la diferencia",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Tarjetas de características ──
            TarjetaCaracteristica(
                icono = Icons.Outlined.Pets,
                titulo = "Registra",
                descripcion = "Encuentra un animal callejero y registra su información para que otros puedan ayudar."
            )

            Spacer(modifier = Modifier.height(16.dp))

            TarjetaCaracteristica(
                icono = Icons.Outlined.Share,
                titulo = "Comparte",
                descripcion = "La información queda visible para toda la comunidad que busca ayudar."
            )

            Spacer(modifier = Modifier.height(16.dp))

            TarjetaCaracteristica(
                icono = Icons.Outlined.Favorite,
                titulo = "Conecta",
                descripcion = "Personas interesadas pueden contactar y darle un hogar al animal."
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Indicador de página ──
            IndicadorPagina(totalPaginas = 2, paginaActual = 1)

            Spacer(modifier = Modifier.height(32.dp))

            // ── Dos botones de acción ──
            Text(
                text = "¿Qué te gustaría hacer?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón principal: Ver animales
            Button(
                onClick = alVerAnimales,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Icons.Outlined.List,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ver animales",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón secundario: Registrar animal
            OutlinedButton(
                onClick = alRegistrarAnimal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Icons.Outlined.PostAdd,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Registrar animal",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Tarjeta individual con ícono, título y descripción.
 * Usada para explicar las funcionalidades de la app.
 */
@Composable
private fun TarjetaCaracteristica(
    icono: ImageVector,
    titulo: String,
    descripcion: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaIntroduccionPreview() {
    HuellitasTheme {
        PantallaIntroduccion(
            alVerAnimales = {},
            alRegistrarAnimal = {}
        )
    }
}
