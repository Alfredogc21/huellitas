package com.example.huellitas.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.huellitas.model.OpcionFiltro
import com.example.huellitas.ui.theme.HuellitasTheme
import java.util.Date

/**
 * Fila horizontal con chips de filtro para ordenar la lista de animales.
 * Cada chip representa una estrategia de ordenamiento diferente.
 *
 * Cuando "Por fecha" está seleccionado y tiene una fecha asociada,
 * muestra la fecha seleccionada en la etiqueta del chip.
 *
 * @param filtroActual Opción de filtro seleccionada actualmente
 * @param fechaSeleccionada Fecha elegida en el calendario (solo para POR_FECHA)
 * @param alSeleccionarFiltro Callback al seleccionar un filtro
 * @param alSolicitarCalendario Callback cuando se solicita abrir el calendario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilaChipsFiltro(
    filtroActual: OpcionFiltro,
    fechaSeleccionada: Long?,
    alSeleccionarFiltro: (OpcionFiltro) -> Unit,
    alSolicitarCalendario: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OpcionFiltro.entries.forEach { opcion ->
            val estaSeleccionado = opcion == filtroActual

            val icono = when (opcion) {
                OpcionFiltro.RECIENTES -> Icons.Outlined.TrendingUp
                OpcionFiltro.POR_FECHA -> Icons.Outlined.CalendarMonth
                OpcionFiltro.ANTIGUOS -> Icons.Outlined.TrendingDown
            }

            // Etiqueta especial para "Por fecha" cuando hay fecha seleccionada
            val etiqueta = if (opcion == OpcionFiltro.POR_FECHA && estaSeleccionado && fechaSeleccionada != null) {
                DateFormat.format("dd MMM yyyy", Date(fechaSeleccionada)).toString()
            } else {
                opcion.etiqueta
            }

            FilterChip(
                selected = estaSeleccionado,
                onClick = {
                    if (opcion == OpcionFiltro.POR_FECHA) {
                        alSolicitarCalendario()
                    } else {
                        alSeleccionarFiltro(opcion)
                    }
                },
                label = { Text(etiqueta) },
                leadingIcon = {
                    Icon(
                        imageVector = icono,
                        contentDescription = opcion.etiqueta,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilaChipsFiltroPreview() {
    HuellitasTheme {
        FilaChipsFiltro(
            filtroActual = OpcionFiltro.RECIENTES,
            fechaSeleccionada = null,
            alSeleccionarFiltro = {},
            alSolicitarCalendario = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FilaChipsConFechaPreview() {
    HuellitasTheme {
        FilaChipsFiltro(
            filtroActual = OpcionFiltro.POR_FECHA,
            fechaSeleccionada = System.currentTimeMillis(),
            alSeleccionarFiltro = {},
            alSolicitarCalendario = {}
        )
    }
}
