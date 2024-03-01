package com.michaelrayven.lunarcalendar.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> ContextDropdownMenu(
    items: List<T>,
    onItemClick: (index: Int, item: T) -> Unit,
    drawItem: @Composable (item: T, onClick: () -> Unit) -> Unit = { item, onClick ->
        DropdownMenuItem(
            text = {
                Text(
                    text = item.toString(),
                    style = MaterialTheme.typography.titleSmall
                )
            },
            onClick = onClick
        )
    },
    actionIcon: @Composable () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit
) {
    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        actionIcon()

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            items.mapIndexed { index, item ->
                drawItem(item) {
                    onItemClick(index, item)
                }

                if (index < items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}