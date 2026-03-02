package com.example.huellitas.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.huellitas.model.Animal
import com.example.huellitas.model.AnimalType
import com.example.huellitas.model.SortOption
import com.example.huellitas.ui.components.AnimalCard
import com.example.huellitas.ui.components.FilterChipRow
import com.example.huellitas.ui.theme.HuellitasTheme
import java.util.Date

private const val SEVEN_DAYS_MS = 7 * 86_400_000L

/**
 * Main screen showing all registered animals with sorting options.
 *
 * Features:
 * - Collapsible large top bar for branding
 * - Filter chips: Recientes (7 days), Por fecha, Más antiguos
 * - Scrollable list of [AnimalCard] items
 * - FAB to register a new animal
 * - Empty state when no animals match
 *
 * @param onNavigateToRegister Callback to navigate to the registration form
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(onNavigateToRegister: () -> Unit) {
    val sampleAnimals = remember { createSampleAnimals() }
    var sortOption by rememberSaveable { mutableStateOf(SortOption.RECENT) }

    val sortedAnimals = remember(sortOption, sampleAnimals) {
        when (sortOption) {
            SortOption.RECENT -> {
                val cutoff = System.currentTimeMillis() - SEVEN_DAYS_MS
                sampleAnimals
                    .filter { it.registrationDate.time >= cutoff }
                    .sortedByDescending { it.registrationDate }
            }
            SortOption.BY_DATE -> sampleAnimals.sortedByDescending { it.registrationDate }
            SortOption.OLDEST -> sampleAnimals.sortedBy { it.registrationDate }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Huellitas",
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToRegister,
                icon = { Icon(Icons.Outlined.Add, contentDescription = "Registrar animal") },
                text = { Text("Registrar") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Filter Chips ──
            FilterChipRow(
                currentSort = sortOption,
                onSortSelected = { sortOption = it }
            )

            // ── Results Count ──
            Text(
                text = "${sortedAnimals.size} animales encontrados",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // ── Animal List ──
            AnimatedVisibility(
                visible = sortedAnimals.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 88.dp // Space for FAB
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = sortedAnimals,
                        key = { it.id }
                    ) { animal ->
                        AnimalCard(animal = animal)
                    }
                }
            }

            // ── Empty State ──
            if (sortedAnimals.isEmpty()) {
                EmptyState()
            }
        }
    }
}

/**
 * Friendly empty state shown when no animals match the current filter.
 */
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Pets,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay animales registrados",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sé el primero en registrar un animal que necesite ayuda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Creates sample animal data for UI design preview.
 * In production, this would come from a repository/database.
 */
private fun createSampleAnimals(): List<Animal> = listOf(
    Animal(
        id = "1",
        name = "Max",
        type = AnimalType.DOG,
        breed = "Labrador",
        description = "Muy juguetón y amigable. Le encanta correr y jugar con niños. Está en buen estado de salud.",
        location = "Parque Central, Zona 1",
        contact = "max.rescate@email.com",
        registrationDate = Date(System.currentTimeMillis() - 86_400_000L * 1)
    ),
    Animal(
        id = "2",
        name = "Luna",
        type = AnimalType.CAT,
        breed = "Siamés",
        description = "Tímida pero muy cariñosa cuando toma confianza. Le gustan los lugares tranquilos.",
        location = "Calle 10 #20, Colonia Centro",
        contact = "+502 5555-1234",
        registrationDate = Date(System.currentTimeMillis() - 86_400_000L * 3)
    ),
    Animal(
        id = "3",
        name = "Buddy",
        type = AnimalType.DOG,
        breed = "Mestizo",
        description = "Muy sociable con otros perros. Necesita un hogar con patio para correr.",
        location = "Refugio Animal Municipal",
        contact = "buddy.help@email.com",
        registrationDate = Date(System.currentTimeMillis() - 86_400_000L * 7)
    ),
    Animal(
        id = "4",
        name = "Misi",
        type = AnimalType.CAT,
        breed = "Persa",
        description = "Encontrada en una caja cerca del mercado. Necesita atención veterinaria urgente.",
        location = "Mercado La Terminal",
        contact = "+502 4444-5678",
        registrationDate = Date(System.currentTimeMillis() - 86_400_000L * 14)
    ),
    Animal(
        id = "5",
        name = "Rocky",
        type = AnimalType.DOG,
        breed = "Pastor Alemán",
        description = "Adulto, bien portado. Fue abandonado por su familia. Busca un nuevo hogar amoroso.",
        location = "Avenida Reforma, Zona 9",
        contact = "rocky.adopta@email.com",
        registrationDate = Date()
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AnimalListScreenPreview() {
    HuellitasTheme {
        AnimalListScreen(onNavigateToRegister = {})
    }
}
