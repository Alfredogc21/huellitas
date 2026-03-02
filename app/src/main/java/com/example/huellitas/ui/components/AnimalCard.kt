package com.example.huellitas.ui.components

import android.text.format.DateFormat
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.huellitas.model.Animal
import com.example.huellitas.model.AnimalType
import com.example.huellitas.ui.theme.CatChip
import com.example.huellitas.ui.theme.DogChip
import com.example.huellitas.ui.theme.HuellitasTheme
import java.util.Date

/**
 * Beautiful card component for displaying animal information.
 * Features a placeholder image area, type badge, and organized info layout.
 *
 * @param animal The animal data to display
 * @param modifier External layout modifier
 */
@Composable
fun AnimalCard(
    animal: Animal,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // ── Image Placeholder with Type Badge ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Pets,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )

                // Type badge (top-end corner)
                val chipColor = when (animal.type) {
                    AnimalType.DOG -> DogChip
                    AnimalType.CAT -> CatChip
                    AnimalType.OTHER -> MaterialTheme.colorScheme.tertiary
                }

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(20.dp),
                    color = chipColor.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = animal.type.label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.surface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ── Content Section ──
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = animal.name.ifEmpty { "Animal sin nombre" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (animal.breed.isNotEmpty()) {
                    Text(
                        text = animal.breed,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Text(
                    text = animal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                InfoRow(
                    icon = Icons.Outlined.LocationOn,
                    text = animal.location,
                    contentDescription = "Ubicación"
                )

                InfoRow(
                    icon = Icons.Outlined.Phone,
                    text = animal.contact,
                    contentDescription = "Contacto"
                )

                InfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    text = DateFormat.format("dd MMM yyyy", animal.registrationDate).toString(),
                    contentDescription = "Fecha de registro"
                )
            }
        }
    }
}

/**
 * A single info row with an icon and text label.
 */
@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
    contentDescription: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimalCardPreview() {
    HuellitasTheme {
        AnimalCard(
            animal = Animal(
                id = "1",
                name = "Max",
                type = AnimalType.DOG,
                breed = "Labrador",
                description = "Muy juguetón, le encanta correr y jugar con niños.",
                location = "Parque central",
                contact = "max@example.com",
                registrationDate = Date()
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
