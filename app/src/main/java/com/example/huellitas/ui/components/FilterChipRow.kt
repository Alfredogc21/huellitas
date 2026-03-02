package com.example.huellitas.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
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
import com.example.huellitas.model.SortOption
import com.example.huellitas.ui.theme.HuellitasTheme

/**
 * Horizontal scrollable row of filter chips for sorting the animal list.
 * Each chip represents a different sorting strategy.
 *
 * @param currentSort The currently selected sort option
 * @param onSortSelected Callback when user selects a new sort option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipRow(
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SortOption.entries.forEach { option ->
            val isSelected = option == currentSort

            val icon = when (option) {
                SortOption.RECENT -> Icons.Outlined.TrendingUp
                SortOption.BY_DATE -> Icons.Outlined.Schedule
                SortOption.OLDEST -> Icons.Outlined.TrendingDown
            }

            FilterChip(
                selected = isSelected,
                onClick = { onSortSelected(option) },
                label = { Text(option.label) },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = option.label,
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
private fun FilterChipRowPreview() {
    HuellitasTheme {
        FilterChipRow(
            currentSort = SortOption.RECENT,
            onSortSelected = {}
        )
    }
}
