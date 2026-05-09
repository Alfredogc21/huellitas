package com.example.huellitas.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huellitas.R
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.ui.theme.HuellitasTheme
import com.example.huellitas.ui.theme.PurpleDark

/**
 * Panel de administración principal.
 *
 * Muestra un dashboard con estadísticas de reportes,
 * filtros por estado, secciones de Veterinarios/Adopciones,
 * y la lista de reportes (actualmente vacía como en el diseño).
 *
 * @param alCerrarSesion Callback para cerrar sesión y volver al feed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdminPanel(
    alCerrarSesion: () -> Unit,
    alNavegarAVeterinarios: () -> Unit = {},
    alNavegarAAdopciones: () -> Unit = {}
) {
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Panel Administrativo",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Gestión de Reportes",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.logo_huellitas),
                        contentDescription = "Logo Huellitas",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Configuración futura */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Configuración",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = alCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F0FA))
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Tarjetas de estadísticas ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TarjetaEstadistica(
                    valor = "0",
                    etiqueta = "Total \uD83D\uDCCB",
                    colorFondo = PurpleDark
                )
                TarjetaEstadistica(
                    valor = "0",
                    etiqueta = "Nuevos \uD83C\uDD95",
                    colorFondo = Color(0xFF00C853)
                )
                TarjetaEstadistica(
                    valor = "0",
                    etiqueta = "En proceso \u23F3",
                    colorFondo = Color(0xFFFF9800)
                )
                TarjetaEstadistica(
                    valor = "0",
                    etiqueta = "Resueltos \uD83D\uDCE6",
                    colorFondo = Color(0xFF5B9BD5)
                )
            }

            // ── Chips de filtrado ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filtros = listOf(
                    "\uD83D\uDCC1 Todos",
                    "\uD83C\uDD95 Nuevos",
                    "\u23F3 En Progreso",
                    "\u2705 Resueltos"
                )
                filtros.forEach { filtro ->
                    val seleccionado = filtroSeleccionado == filtro.substringAfter(" ")
                    FilterChip(
                        selected = seleccionado,
                        onClick = { filtroSeleccionado = filtro.substringAfter(" ") },
                        label = { Text(filtro, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GradientStart,
                            selectedLabelColor = Color.White,
                            labelColor = Color(0xFF1D1A20)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Secciones: Veterinarios y Adopciones ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaSeccion(
                    emoji = "\uD83D\uDC3E",
                    titulo = "Veterinarios",
                    descripcion = "Gestiona profesionales médicos",
                    colorFondo = Color(0xFF00897B),
                    modifier = Modifier.weight(1f),
                    onClick = alNavegarAVeterinarios
                )
                TarjetaSeccion(
                    emoji = "\uD83D\uDC95",
                    titulo = "Adopciones",
                    descripcion = "Panel de perros en adopción",
                    colorFondo = Color(0xFFD81B60),
                    modifier = Modifier.weight(1f),
                    onClick = alNavegarAAdopciones
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Lista de reportes (vacía por ahora) ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFFCAC4D0)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No hay reportes en esta categoría",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7A757F),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Tarjeta de estadística individual con valor grande y etiqueta.
 */
@Composable
private fun TarjetaEstadistica(
    valor: String,
    etiqueta: String,
    colorFondo: Color
) {
    Surface(
        modifier = Modifier
            .width(140.dp)
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        color = colorFondo
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = valor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Tarjeta de sección (Veterinarios / Adopciones) con gradiente.
 */
@Composable
private fun TarjetaSeccion(
    emoji: String,
    titulo: String,
    descripcion: String,
    colorFondo: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = colorFondo
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder de imagen
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(emoji, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$emoji $titulo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Text(
                text = "→",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaAdminPanelPreview() {
    HuellitasTheme {
        PantallaAdminPanel(alCerrarSesion = {})
    }
}
