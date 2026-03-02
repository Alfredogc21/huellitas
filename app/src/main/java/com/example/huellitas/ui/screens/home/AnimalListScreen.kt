package com.example.huellitas.ui.screens.home

import android.text.format.DateFormat
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import com.example.huellitas.model.OpcionFiltro
import com.example.huellitas.model.TipoAnimal
import com.example.huellitas.ui.components.FilaChipsFiltro
import com.example.huellitas.ui.components.TarjetaAnimal
import com.example.huellitas.ui.theme.HuellitasTheme
import java.util.Calendar
import java.util.Date

/**
 * Pantalla principal que muestra todos los animales registrados
 * con opciones de filtrado y ordenamiento.
 *
 * Funcionalidades:
 * - Barra superior colapsable con marca de la app
 * - Chips de filtro: Recientes, Por fecha (calendario), Más antiguos
 * - Lista desplazable de [TarjetaAnimal] con imágenes
 * - FAB para registrar un nuevo animal
 * - Estado vacío cuando no hay resultados
 *
 * @param alNavegarARegistro Callback para navegar al formulario de registro
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaAnimales(alNavegarARegistro: () -> Unit) {

    val animalesEjemplo = remember { crearAnimalesEjemplo() }
    var filtroActual by rememberSaveable { mutableStateOf(OpcionFiltro.RECIENTES) }
    var fechaSeleccionada by rememberSaveable { mutableStateOf<Long?>(null) }
    var mostrarCalendario by rememberSaveable { mutableStateOf(false) }

    // Aplicar filtro/ordenamiento según la opción seleccionada
    val animalesFiltrados = remember(filtroActual, fechaSeleccionada, animalesEjemplo) {
        when (filtroActual) {
            OpcionFiltro.RECIENTES -> {
                animalesEjemplo.sortedByDescending { it.fechaRegistro }
            }
            OpcionFiltro.POR_FECHA -> {
                if (fechaSeleccionada != null) {
                    val calendarioSeleccionado = Calendar.getInstance().apply {
                        timeInMillis = fechaSeleccionada!!
                    }
                    animalesEjemplo.filter { animal ->
                        val calendarioAnimal = Calendar.getInstance().apply {
                            time = animal.fechaRegistro
                        }
                        calendarioAnimal.get(Calendar.YEAR) == calendarioSeleccionado.get(Calendar.YEAR) &&
                                calendarioAnimal.get(Calendar.DAY_OF_YEAR) == calendarioSeleccionado.get(Calendar.DAY_OF_YEAR)
                    }
                } else {
                    animalesEjemplo.sortedByDescending { it.fechaRegistro }
                }
            }
            OpcionFiltro.ANTIGUOS -> {
                animalesEjemplo.sortedBy { it.fechaRegistro }
            }
        }
    }

    // ── Diálogo de calendario ──
    if (mostrarCalendario) {
        DialogoCalendario(
            alSeleccionarFecha = { milisegundos ->
                fechaSeleccionada = milisegundos
                filtroActual = OpcionFiltro.POR_FECHA
                mostrarCalendario = false
            },
            alCancelar = { mostrarCalendario = false }
        )
    }

    val comportamientoScroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(comportamientoScroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Huellitas",
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = comportamientoScroll,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = alNavegarARegistro,
                icon = { Icon(Icons.Outlined.Add, contentDescription = "Registrar animal") },
                text = { Text("Registrar") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
        ) {
            // ── Chips de filtro ──
            FilaChipsFiltro(
                filtroActual = filtroActual,
                fechaSeleccionada = fechaSeleccionada,
                alSeleccionarFiltro = { opcion ->
                    filtroActual = opcion
                    if (opcion != OpcionFiltro.POR_FECHA) {
                        fechaSeleccionada = null
                    }
                },
                alSolicitarCalendario = { mostrarCalendario = true }
            )

            // ── Contador de resultados ──
            val textoContador = if (filtroActual == OpcionFiltro.POR_FECHA && fechaSeleccionada != null) {
                val fechaTexto = DateFormat.format("dd MMM yyyy", Date(fechaSeleccionada!!)).toString()
                "${animalesFiltrados.size} animales el $fechaTexto"
            } else {
                "${animalesFiltrados.size} animales encontrados"
            }

            Text(
                text = textoContador,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // ── Lista de animales ──
            AnimatedVisibility(
                visible = animalesFiltrados.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 88.dp // Espacio para el FAB
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = animalesFiltrados,
                        key = { it.id }
                    ) { animal ->
                        TarjetaAnimal(animal = animal)
                    }
                }
            }

            // ── Estado vacío ──
            if (animalesFiltrados.isEmpty()) {
                EstadoVacio()
            }
        }
    }
}

/**
 * Diálogo de selector de fecha usando Material 3 DatePicker.
 * Permite al usuario elegir una fecha para filtrar los resultados.
 *
 * @param alSeleccionarFecha Callback con los milisegundos de la fecha seleccionada
 * @param alCancelar Callback al cancelar la selección
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoCalendario(
    alSeleccionarFecha: (Long) -> Unit,
    alCancelar: () -> Unit
) {
    val estadoFecha = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = alCancelar,
        confirmButton = {
            TextButton(
                onClick = {
                    estadoFecha.selectedDateMillis?.let { alSeleccionarFecha(it) }
                },
                enabled = estadoFecha.selectedDateMillis != null
            ) {
                Text("Seleccionar")
            }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(
            state = estadoFecha,
            title = {
                Text(
                    text = "Filtrar por fecha",
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                )
            }
        )
    }
}

/**
 * Estado vacío amigable mostrado cuando no hay animales
 * que coincidan con el filtro actual.
 */
@Composable
private fun EstadoVacio() {
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

// ═══════════════════════════════════════════════════════════════
// Datos de ejemplo con imágenes de Unsplash
// En producción vendrían de un repositorio/base de datos
// ═══════════════════════════════════════════════════════════════

private fun crearAnimalesEjemplo(): List<Animal> = listOf(
    Animal(
        id = "1",
        nombre = "Max",
        tipo = TipoAnimal.PERRO,
        raza = "Golden Retriever",
        descripcion = "Muy juguetón y amigable. Le encanta correr y jugar con niños. Está en buen estado de salud.",
        ubicacion = "Parque Central, Zona 1",
        contacto = "max.rescate@email.com",
        imagenUrl = "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=400&h=300&fit=crop",
        fechaRegistro = Date(System.currentTimeMillis() - 86_400_000L * 1)
    ),
    Animal(
        id = "2",
        nombre = "Luna",
        tipo = TipoAnimal.GATO,
        raza = "Siamés",
        descripcion = "Tímida pero muy cariñosa cuando toma confianza. Le gustan los lugares tranquilos y cálidos.",
        ubicacion = "Calle 10 #20, Colonia Centro",
        contacto = "+502 5555-1234",
        imagenUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400&h=300&fit=crop",
        fechaRegistro = Date(System.currentTimeMillis() - 86_400_000L * 3)
    ),
    Animal(
        id = "3",
        nombre = "Buddy",
        tipo = TipoAnimal.PERRO,
        raza = "Mestizo",
        descripcion = "Muy sociable con otros perros. Necesita un hogar con patio para correr y espacio abierto.",
        ubicacion = "Refugio Animal Municipal",
        contacto = "buddy.ayuda@email.com",
        imagenUrl = "https://images.unsplash.com/photo-1561037404-61cd46aa615b?w=400&h=300&fit=crop",
        fechaRegistro = Date(System.currentTimeMillis() - 86_400_000L * 7)
    ),
    Animal(
        id = "4",
        nombre = "Misi",
        tipo = TipoAnimal.GATO,
        raza = "Persa",
        descripcion = "Encontrada en una caja cerca del mercado. Necesita atención veterinaria y mucho cariño.",
        ubicacion = "Mercado La Terminal",
        contacto = "+502 4444-5678",
        imagenUrl = "https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=400&h=300&fit=crop",
        fechaRegistro = Date(System.currentTimeMillis() - 86_400_000L * 14)
    ),
    Animal(
        id = "5",
        nombre = "Rocky",
        tipo = TipoAnimal.PERRO,
        raza = "Pastor Alemán",
        descripcion = "Adulto, bien portado. Fue abandonado por su familia. Busca un nuevo hogar amoroso y estable.",
        ubicacion = "Avenida Reforma, Zona 9",
        contacto = "rocky.adopta@email.com",
        imagenUrl = "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400&h=300&fit=crop",
        fechaRegistro = Date()
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaListaAnimalesPreview() {
    HuellitasTheme {
        PantallaListaAnimales(alNavegarARegistro = {})
    }
}
