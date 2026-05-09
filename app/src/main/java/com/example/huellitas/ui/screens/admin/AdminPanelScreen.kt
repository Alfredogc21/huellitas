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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huellitas.R
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.network.dto.EstadisticasAdminDto
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.ui.theme.HuellitasTheme
import com.example.huellitas.ui.theme.PurpleDark
import com.example.huellitas.viewmodel.AdminPanelViewModel
import com.example.huellitas.viewmodel.EstadoEstadisticas

/**
 * Panel de administración principal.
 * Muestra estadísticas reales desde la API y navegación a módulos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdminPanel(
    alCerrarSesion: () -> Unit,
    alNavegarAVeterinarios: () -> Unit = {},
    alNavegarAAdopciones: () -> Unit = {},
    viewModel: AdminPanelViewModel = viewModel()
) {
    val estadoStats by viewModel.estadisticas.collectAsState()
    val recientes by viewModel.recientes.collectAsState()
    var filtroRecientes by remember { mutableStateOf(0) } // 0=Tratamiento, 1=Para adoptar, 2=Adoptados

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
                    IconButton(onClick = { viewModel.cargar() }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Actualizar estadísticas",
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
            // ── Estadísticas desde la API ──
            when (val s = estadoStats) {
                is EstadoEstadisticas.Cargando -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GradientStart, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                    }
                }
                is EstadoEstadisticas.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No se pudieron cargar las estadísticas", color = Color(0xFF7A757F), style = MaterialTheme.typography.bodySmall)
                    }
                }
                is EstadoEstadisticas.Exito -> {
                    val d = s.datos
                    // Fila 1: Total · Activos · Adoptados
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TarjetaEstadistica(valor = "${d.total}",        etiqueta = "Total 📋",        colorFondo = PurpleDark)
                        TarjetaEstadistica(valor = "${d.activos}",      etiqueta = "Activos 🐾",      colorFondo = Color(0xFF5B9BD5))
                        TarjetaEstadistica(valor = "${d.adoptados}",    etiqueta = "Adoptados 🏡",    colorFondo = Color(0xFF00897B))
                    }
                    // Fila 2: En progreso · Rehabilitados · Para adoptar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TarjetaEstadistica(valor = "${d.enProgreso}",   etiqueta = "En tratamiento ⏳", colorFondo = Color(0xFFFF9800))
                        TarjetaEstadistica(valor = "${d.rehabilitados}",etiqueta = "Rehabilitados 🩺", colorFondo = Color(0xFF8E24AA))
                        TarjetaEstadistica(valor = "${d.paraAdoptar}",  etiqueta = "Para adoptar 💚",  colorFondo = Color(0xFF00C853))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFCAC4D0))
            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(32.dp))

            // ── Sección Recientes con filtros ──
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFCAC4D0))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Registros recientes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PurpleDark,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Chips de filtro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chips = listOf(
                    "⏳ En tratamiento",
                    "💚 Para adoptar",
                    "🏡 Adoptados"
                )
                chips.forEachIndexed { idx, etiqueta ->
                    FilterChip(
                        selected = filtroRecientes == idx,
                        onClick = { filtroRecientes = idx },
                        label = { Text(etiqueta, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GradientStart,
                            selectedLabelColor = Color.White,
                            labelColor = Color(0xFF1D1A20)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val listaActual = when (filtroRecientes) {
                0 -> recientes.enTratamiento
                1 -> recientes.paraAdoptar
                else -> recientes.adoptados
            }

            if (listaActual.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay registros en esta categoría",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7A757F)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listaActual.forEach { animal ->
                        TarjetaAnimalReciente(animal = animal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Tarjeta compacta para la lista de animales recientes en el panel admin.
 */
@Composable
private fun TarjetaAnimalReciente(animal: AnimalDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto
            if (!animal.imagenUrl.isNullOrBlank()) {
                AsyncImage(
                    model = animal.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0E6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Pets, contentDescription = null, tint = GradientStart, modifier = Modifier.size(26.dp))
                }
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.nombre?.takeIf { it.isNotBlank() } ?: "Sin nombre",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF1D1A20),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                animal.raza?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = GradientStart)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(11.dp), tint = Color(0xFF7A757F))
                    Spacer(Modifier.width(2.dp))
                    Text(
                        animal.ubicacion,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7A757F),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Badge de estado
            val (estadoTexto, estadoColor) = when (animal.idEstado) {
                4 -> "⏳ Trat." to Color(0xFFFF9800)
                6 -> "💚 Adoptar" to Color(0xFF00C853)
                2 -> "🏡 Adoptado" to Color(0xFF00897B)
                else -> animal.estado to GradientStart
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = estadoColor.copy(alpha = 0.15f)
            ) {
                Text(
                    estadoTexto,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = estadoColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
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
