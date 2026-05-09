package com.example.huellitas.ui.screens.adopcion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huellitas.model.Animal
import com.example.huellitas.ui.theme.GradientEnd
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.ui.theme.HuellitasTheme
import com.example.huellitas.viewmodel.AdopcionViewModel
import com.example.huellitas.viewmodel.EstadoAdopcion

private val PurpleDark = Color(0xFF6B34A8)
private val PurpleLight = Color(0xFFEDE7F6)
private val GreenAdopcion = Color(0xFF2E7D32)

/**
 * Pantalla de perritos en adopción.
 * Muestra los perros con estado "Rehabilitado" disponibles para adoptar.
 *
 * @param alVolver Callback para regresar al feed principal
 * @param viewModel ViewModel de adopción
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdopcion(
    alVolver: () -> Unit,
    viewModel: AdopcionViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Perritos en Adopción",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Encuentra a tu nuevo mejor amigo",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        onClick = alVolver,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F0FA))
                .padding(paddingInterno)
        ) {
            // ── Banner informativo ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "\uD83C\uDFE0 ¡Dale un hogar a un perrito rescatado!", fontWeight = FontWeight.Bold, color = PurpleDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Todos estos perritos han sido rescatados y están listos para recibir amor y cuidados. Al adoptar, les das una segunda oportunidad de vida.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PurpleDark.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Barra de búsqueda ──
            OutlinedTextField(
                value = busqueda,
                onValueChange = { viewModel.buscar(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("Buscar por nombre, raza o descripción...", color = PurpleDark.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp)) {
                        Icon(Icons.Outlined.Search, contentDescription = null, tint = GradientStart, modifier = Modifier.size(20.dp))
                        Box(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .size(8.dp)
                                .background(GreenAdopcion, CircleShape)
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GradientStart,
                    unfocusedBorderColor = Color(0xFFCAC4D0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = PurpleDark,
                    unfocusedTextColor = PurpleDark
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Contenido ──
            when (estado) {
                is EstadoAdopcion.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GradientStart)
                    }
                }

                is EstadoAdopcion.Error -> {
                    EstadoErrorAdopcion(alReintentar = { viewModel.cargar() })
                }

                is EstadoAdopcion.Exito -> {
                    val animales = (estado as EstadoAdopcion.Exito).animales

                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refrescar() },
                        modifier = Modifier.weight(1f)
                    ) {
                        if (animales.isEmpty()) {
                            EstadoVacioAdopcion()
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(items = animales, key = { it.id }) { animal ->
                                    TarjetaAdopcion(animal = animal)
                                }
                            }
                        }
                    }
                }
            }

            // ── Sección ¿Cómo adoptar? ──
            SeccionComoAdoptar()
        }
    }
}

@Composable
private fun TarjetaAdopcion(animal: Animal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Imagen
            if (animal.imagenUrl != null) {
                AsyncImage(
                    model = animal.imagenUrl,
                    contentDescription = animal.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        .background(PurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Pets, contentDescription = null, tint = GradientStart, modifier = Modifier.size(40.dp))
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (animal.nombre.isBlank()) "Sin nombre" else animal.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PurpleDark,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Outlined.Favorite, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(16.dp))
                }

                if (animal.raza.isNotBlank()) {
                    Text(
                        text = animal.raza,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                }

                if (animal.descripcion.isNotBlank()) {
                    Text(
                        text = animal.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = GradientStart, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = animal.ubicacion,
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleDark.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoVacioAdopcion() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "\uD83D\uDC36", style = MaterialTheme.typography.displaySmall)
                Text(
                    text = "No hay perritos disponibles en este momento",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Vuelve pronto para conocer a nuevos amigos",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBDBDBD),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EstadoErrorAdopcion(alReintentar: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No se pudo cargar la lista",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
private fun SeccionComoAdoptar() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\uD83D\uDCCB ¿Cómo Adoptar?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PurpleDark
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PasoAdopcion(numero = "1\uFE0F⃣", titulo = "Elige tu perrito", descripcion = "Explora nuestra galería y elige el perrito que más te guste")
                PasoAdopcion(numero = "2\uFE0F⃣", titulo = "Llena el formulario", descripcion = "Completa el formulario de adopción con tus datos")
                PasoAdopcion(numero = "3\uFE0F⃣", titulo = "¡Adopta!", descripcion = "Nos contactaremos contigo para coordinar la adopción")
            }
        }
    }
}

@Composable
private fun PasoAdopcion(numero: String, titulo: String, descripcion: String) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = numero, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = PurpleDark,
            textAlign = TextAlign.Center
        )
        Text(
            text = descripcion,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF43A047),
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.labelSmall.fontSize * 1.3f
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaAdopcionPreview() {
    HuellitasTheme {
        PantallaAdopcion(alVolver = {})
    }
}
