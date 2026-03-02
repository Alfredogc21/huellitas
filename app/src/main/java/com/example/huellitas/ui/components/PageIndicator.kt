package com.example.huellitas.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.huellitas.ui.theme.HuellitasTheme

/**
 * Indicador de página animado para las pantallas de bienvenida.
 * El punto activo se expande horizontalmente para dar énfasis visual.
 *
 * @param totalPaginas Número total de páginas
 * @param paginaActual Índice de la página actual (base 0)
 * @param colorActivo Color del punto activo
 * @param colorInactivo Color de los puntos inactivos
 */
@Composable
fun IndicadorPagina(
    totalPaginas: Int,
    paginaActual: Int,
    modifier: Modifier = Modifier,
    colorActivo: Color = MaterialTheme.colorScheme.primary,
    colorInactivo: Color = MaterialTheme.colorScheme.outlineVariant
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPaginas) { indice ->
            val estaSeleccionado = indice == paginaActual

            val ancho by animateDpAsState(
                targetValue = if (estaSeleccionado) 28.dp else 8.dp,
                animationSpec = tween(durationMillis = 300),
                label = "ancho_indicador"
            )

            val color by animateColorAsState(
                targetValue = if (estaSeleccionado) colorActivo else colorInactivo,
                animationSpec = tween(durationMillis = 300),
                label = "color_indicador"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(ancho)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IndicadorPaginaPreview() {
    HuellitasTheme {
        IndicadorPagina(totalPaginas = 2, paginaActual = 0)
    }
}
