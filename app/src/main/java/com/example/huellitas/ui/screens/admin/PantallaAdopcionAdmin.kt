package com.example.huellitas.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.viewmodel.AdopcionAdminViewModel
import com.example.huellitas.viewmodel.EstadoAccionAdmin
import com.example.huellitas.viewmodel.EstadoAdopcionAdmin

// Colores internos de esta pantalla
private val ColorAprobar   = Color(0xFF00897B)  // Teal: "Aprobar para adopción"
private val ColorAdoptado  = Color(0xFF1565C0)  // Azul: "Marcar como Adoptado"
private val ColorRehab     = Color(0xFFFF8C00)  // Naranja: sección Rehabilitados
private val ColorParaAdopt = Color(0xFF00C853)  // Verde: sección Para Adoptar
private val ColorAdoptame  = Color(0xFFE91E63)  // Rosa: badge "Adoptame"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdopcionAdmin(
    alVolver: () -> Unit,
    viewModel: AdopcionAdminViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val estadoAccion by viewModel.estadoAccion.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val actuando = estadoAccion is EstadoAccionAdmin.Cargando

    // Mostrar resultado de cada acción del admin
    LaunchedEffect(estadoAccion) {
        when (val s = estadoAccion) {
            is EstadoAccionAdmin.Exito -> {
                snackbar.showSnackbar("✅ Estado actualizado correctamente.")
                viewModel.resetearAccion()
            }
            is EstadoAccionAdmin.Error -> {
                snackbar.showSnackbar(s.mensaje)
                viewModel.resetearAccion()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Gestión de Adopciones",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Panel de administración",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GradientStart)
            )
        }
    ) { padding ->
        when (val s = estado) {
            is EstadoAdopcionAdmin.Cargando -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GradientStart)
                }
            }

            is EstadoAdopcionAdmin.Error -> {
                Box(
                    Modifier.fillMaxSize().padding(padding).padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(s.mensaje, textAlign = TextAlign.Center, color = Color(0xFF7A757F))
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.cargar() },
                            colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
                        ) { Text("Reintentar") }
                    }
                }
            }

            is EstadoAdopcionAdmin.Exito -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F0FA))
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // ── Sección 1: Rehabilitados (pendientes de aprobación) ──
                    item {
                        EncabezadoSeccionAdmin(
                            emoji = "🤒",
                            titulo = "Rehabilitados — Pendientes de aprobación",
                            color = ColorRehab,
                            cantidad = s.rehabilitados.size
                        )
                    }

                    if (s.rehabilitados.isEmpty()) {
                        item {
                            TextoVacio("No hay animales rehabilitados pendientes.")
                        }
                    } else {
                        items(s.rehabilitados, key = { "rehab_${it.id}" }) { animal ->
                            TarjetaAdopcionAdmin(
                                animal = animal,
                                actuando = actuando,
                                botonPrimarioTexto = "✅ Aprobar para adopción",
                                botonPrimarioColor = ColorAprobar,
                                onBotonPrimario = { viewModel.aprobarParaAdopcion(animal.id) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(8.dp)) }

                    // ── Sección 2: Para adoptar (aprobados por admin, en feed público) ──
                    item {
                        EncabezadoSeccionAdmin(
                            emoji = "🏠",
                            titulo = "Para adoptar — En el feed público",
                            color = ColorParaAdopt,
                            cantidad = s.paraAdoptar.size
                        )
                    }

                    if (s.paraAdoptar.isEmpty()) {
                        item {
                            TextoVacio("No hay animales publicados para adopción.")
                        }
                    } else {
                        items(s.paraAdoptar, key = { "adopt_${it.id}" }) { animal ->
                            TarjetaAdopcionAdmin(
                                animal = animal,
                                actuando = actuando,
                                mostrarBadgeAdoptame = true,
                                botonPrimarioTexto = "🏡 Marcar como Adoptado",
                                botonPrimarioColor = ColorAdoptado,
                                onBotonPrimario = { viewModel.marcarAdoptado(animal.id) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Encabezado de sección
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EncabezadoSeccionAdmin(
    emoji: String,
    titulo: String,
    color: Color,
    cantidad: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            titulo,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = color
        ) {
            Text(
                "$cantidad",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tarjeta de animal en el panel de adopciones del admin
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaAdopcionAdmin(
    animal: AnimalDto,
    actuando: Boolean,
    mostrarBadgeAdoptame: Boolean = false,
    botonPrimarioTexto: String,
    botonPrimarioColor: Color,
    onBotonPrimario: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Foto del animal
                Box {
                    if (!animal.imagenUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = animal.imagenUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF0E6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🐶", fontSize = 26.sp)
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Nombre + badge "Adoptame" en la misma fila
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = animal.nombre?.takeIf { it.isNotBlank() } ?: "Sin nombre",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1D1A20),
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        // Badge "Perro" siempre visible
                        BadgeAnimal(texto = "Perro", color = Color(0xFF5B9BD5))
                        if (mostrarBadgeAdoptame) {
                            Spacer(Modifier.width(4.dp))
                            // Badge "Adoptame" solo en la sección de aprobados
                            BadgeAnimal(texto = "Adoptame", color = ColorAdoptame)
                        }
                    }

                    animal.raza?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = GradientStart
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFF7A757F)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = animal.ubicacion,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF7A757F),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            animal.descripcion?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF49454F),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(10.dp))

            // Botón de acción principal
            Button(
                onClick = onBotonPrimario,
                enabled = !actuando,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = botonPrimarioColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (actuando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        botonPrimarioTexto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Badge pequeño de etiqueta
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BadgeAnimal(texto: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.9f)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Texto cuando la lista está vacía
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TextoVacio(mensaje: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            mensaje,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF7A757F),
            textAlign = TextAlign.Center
        )
    }
}
