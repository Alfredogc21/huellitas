package com.example.huellitas.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.huellitas.model.Animal
import com.example.huellitas.model.TipoAnimal
import com.example.huellitas.ui.theme.CatChip
import com.example.huellitas.ui.theme.DogChip
import com.example.huellitas.ui.theme.HuellitasTheme
import java.util.Date

/**
 * Tarjeta de animal con imagen cargada mediante Coil,
 * badge de tipo, y disposición organizada de la información.
 *
 * Características:
 * - Carga asíncrona de imagen con placeholder
 * - Badge de tipo (Perro/Gato) con color distintivo
 * - Información organizada con íconos
 *
 * @param animal Datos del animal a mostrar
 * @param modifier Modificador externo de layout
 */
@Composable
fun TarjetaAnimal(
    animal: Animal,
    modifier: Modifier = Modifier
) {
    var mostrarZoom by remember { mutableStateOf(false) }

    // Diálogo de zoom a pantalla completa
    if (mostrarZoom && animal.imagenUrl != null) {
        DialogoZoomImagen(
            imagenUrl = animal.imagenUrl,
            descripcion = "Foto de ${animal.nombre}",
            alCerrar = { mostrarZoom = false }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // ── Imagen del animal con badge de tipo ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                if (animal.imagenUrl != null) {
                    // Carga de imagen desde URL con Coil (cacheada en disco y memoria)
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(animal.imagenUrl)
                            .crossfade(true)
                            .size(600, 400)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "Foto de ${animal.nombre}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { mostrarZoom = true }
                    )
                } else {
                    // Placeholder cuando no hay imagen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Pets,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }

                // Badge de tipo (esquina superior derecha)
                val colorBadge = when (animal.tipo) {
                    TipoAnimal.PERRO -> DogChip
                    TipoAnimal.GATO -> CatChip
                    TipoAnimal.OTRO -> MaterialTheme.colorScheme.tertiary
                }

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(20.dp),
                    color = colorBadge.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = animal.tipo.etiqueta,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.surface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ── Sección de contenido ──
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = animal.nombre.ifEmpty { "Animal sin nombre" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (animal.raza.isNotEmpty()) {
                    Text(
                        text = animal.raza,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Text(
                    text = animal.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                FilaInfo(
                    icono = Icons.Outlined.LocationOn,
                    texto = animal.ubicacion,
                    descripcionContenido = "Ubicación"
                )

                FilaInfo(
                    icono = Icons.Outlined.Phone,
                    texto = animal.contacto,
                    descripcionContenido = "Contacto"
                )

                FilaInfo(
                    icono = Icons.Outlined.CalendarToday,
                    texto = DateFormat.format("dd MMM yyyy", animal.fechaRegistro).toString(),
                    descripcionContenido = "Fecha de registro"
                )
            }
        }
    }
}

/**
 * Fila de información con ícono y texto.
 * Util para mostrar ubicación, contacto, fecha, etc.
 */
@Composable
private fun FilaInfo(
    icono: ImageVector,
    texto: String,
    descripcionContenido: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = descripcionContenido,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaAnimalConImagenPreview() {
    HuellitasTheme {
        TarjetaAnimal(
            animal = Animal(
                id = "1",
                nombre = "Max",
                tipo = TipoAnimal.PERRO,
                raza = "Labrador",
                descripcion = "Muy juguetón, le encanta correr y jugar con niños.",
                ubicacion = "Parque central",
                contacto = "max@ejemplo.com",
                imagenUrl = "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=400&h=300&fit=crop",
                fechaRegistro = Date()
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaAnimalSinImagenPreview() {
    HuellitasTheme {
        TarjetaAnimal(
            animal = Animal(
                id = "2",
                nombre = "Luna",
                tipo = TipoAnimal.GATO,
                raza = "Siamés",
                descripcion = "Tímida pero cariñosa.",
                ubicacion = "Calle 10",
                contacto = "luna@ejemplo.com",
                fechaRegistro = Date()
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
