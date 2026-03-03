package com.example.huellitas.ui.screens.home

import android.text.format.DateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huellitas.model.Animal
import com.example.huellitas.model.OpcionFiltro
import com.example.huellitas.ui.components.FilaChipsFiltro
import com.example.huellitas.ui.components.TarjetaAnimal
import com.example.huellitas.ui.theme.GradientEnd
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.ui.theme.HuellitasTheme
import com.example.huellitas.ui.theme.PurpleDark
import com.example.huellitas.ui.theme.PurpleText
import com.example.huellitas.viewmodel.AnimalListViewModel
import com.example.huellitas.viewmodel.EstadoListaAnimales
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * Pantalla principal que muestra todos los animales registrados
 * con opciones de filtrado y ordenamiento.
 *
 * @param alNavegarARegistro Callback para navegar al formulario de registro
 * @param viewModel ViewModel que provee los datos desde la API
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaAnimales(
    alNavegarARegistro: () -> Unit,
    viewModel: AnimalListViewModel = viewModel()
) {
    val estadoActual by viewModel.estado.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isCargandoMas by viewModel.isCargandoMas.collectAsState()
    val tipoSeleccionado by viewModel.tipoSeleccionado.collectAsState()

    var filtroActual by rememberSaveable { mutableStateOf(OpcionFiltro.RECIENTES) }
    var fechaSeleccionada by rememberSaveable { mutableStateOf<Long?>(null) }
    var mostrarCalendario by rememberSaveable { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Aplicar filtro/ordenamiento client-side sobre la lista descargada
    val animalesFiltrados = remember(estadoActual, filtroActual, fechaSeleccionada) {
        val lista = (estadoActual as? EstadoListaAnimales.Exito)?.animales ?: emptyList()
        cuando(filtroActual, fechaSeleccionada, lista)
    }

    // Detectar cuando se llega al final de la lista para cargar más
    val debeSolicitarMas by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val ultimoVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && ultimoVisible >= totalItems - 3
        }
    }

    LaunchedEffect(debeSolicitarMas) {
        if (debeSolicitarMas) {
            viewModel.cargarMas()
        }
    }

    if (mostrarCalendario) {
        DialogoCalendario(
            alSeleccionarFecha = { milisegundos ->
                fechaSeleccionada = milisegundos
                filtroActual = OpcionFiltro.POR_FECHA
                mostrarCalendario = false
                scope.launch { listState.scrollToItem(0) }
            },
            alCancelar = { mostrarCalendario = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.huellitas.R.drawable.logo_huellitas),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Huellitas a Salvo",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = alNavegarARegistro,
                icon = { Icon(Icons.Outlined.Add, contentDescription = "Registrar animal") },
                text = { Text("Registrar") },
                containerColor = GradientStart,
                contentColor = Color.White
            )
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F0FA))
                .padding(paddingInterno)
        ) {
            // ── Chips de tipo de animal (siempre fijos arriba) ──
            FilaTipoAnimal(
                tipoSeleccionado = tipoSeleccionado,
                alSeleccionarTipo = { idTipo ->
                    viewModel.seleccionarTipo(idTipo)
                    scope.launch { listState.scrollToItem(0) }
                }
            )

            // ── Chips de ordenamiento/fecha (siempre fijos arriba) ──
            FilaChipsFiltro(
                filtroActual = filtroActual,
                fechaSeleccionada = fechaSeleccionada,
                alSeleccionarFiltro = { opcion ->
                    filtroActual = opcion
                    if (opcion != OpcionFiltro.POR_FECHA) fechaSeleccionada = null
                    scope.launch { listState.scrollToItem(0) }
                },
                alSolicitarCalendario = { mostrarCalendario = true }
            )

            // ── Contenido principal con pull-to-refresh ──
            when (estadoActual) {
                is EstadoListaAnimales.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is EstadoListaAnimales.Error -> {
                    EstadoError(
                        mensaje = (estadoActual as EstadoListaAnimales.Error).mensaje,
                        alReintentar = { viewModel.cargarAnimales() }
                    )
                }

                is EstadoListaAnimales.Exito -> {
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
                        fontWeight = FontWeight.Medium,
                        color = PurpleDark,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    // ── Lista con pull-to-refresh ──
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refrescar() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (animalesFiltrados.isEmpty()) {
                            EstadoVacio()
                        } else {
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 88.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(items = animalesFiltrados, key = { it.id }) { animal ->
                                    TarjetaAnimal(animal = animal)
                                }

                                // Indicador de carga al final de la lista
                                if (isCargandoMas) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fila de chips para filtrar por tipo de animal.
 */
@Composable
private fun FilaTipoAnimal(
    tipoSeleccionado: Int?,
    alSeleccionarTipo: (Int?) -> Unit
) {
    val tipos = listOf(
        null to "Todos",
        1 to "\uD83D\uDC36 Perro"
        // 2 to "\uD83D\uDC31 Gato",   // Oculto temporalmente
        // 3 to "Otro"                   // Oculto temporalmente
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tipos.forEach { (id, label) ->
            FilterChip(
                selected = tipoSeleccionado == id,
                onClick = { alSeleccionarTipo(id) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GradientStart.copy(alpha = 0.15f),
                    selectedLabelColor = PurpleDark,
                    labelColor = Color(0xFF1D1A20)
                )
            )
        }
    }
}

/**
 * Aplica el filtro/ordenamiento a la lista de animales.
 */
private fun cuando(
    filtro: OpcionFiltro,
    fechaMs: Long?,
    lista: List<Animal>
): List<Animal> = when (filtro) {
    OpcionFiltro.RECIENTES -> lista.sortedByDescending { it.fechaRegistro }
    OpcionFiltro.ANTIGUOS -> lista.sortedBy { it.fechaRegistro }
    OpcionFiltro.POR_FECHA -> {
        if (fechaMs != null) {
            val calendarioSeleccionado = Calendar.getInstance().apply { timeInMillis = fechaMs }
            lista.filter { animal ->
                val c = Calendar.getInstance().apply { time = animal.fechaRegistro }
                c.get(Calendar.YEAR) == calendarioSeleccionado.get(Calendar.YEAR) &&
                        c.get(Calendar.DAY_OF_YEAR) == calendarioSeleccionado.get(Calendar.DAY_OF_YEAR)
            }
        } else {
            lista.sortedByDescending { it.fechaRegistro }
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
 * Estado de error con botón de reintentar.
 */
@Composable
private fun EstadoError(mensaje: String, alReintentar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Refresh,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No se pudieron cargar los animales",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = alReintentar,
            colors = ButtonDefaults.buttonColors(
                containerColor = GradientStart,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Reintentar")
        }
    }
}


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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaListaAnimalesPreview() {
    HuellitasTheme {
        PantallaListaAnimales(alNavegarARegistro = {})
    }
}

