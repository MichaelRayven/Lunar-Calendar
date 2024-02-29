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
    drawItem: @Composable (index: Int,  item: T, onClick: () -> Unit) -> Unit = { index, item, onClick ->
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
    actionIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More"
        )
    }
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            actionIcon()
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.mapIndexed { index, item ->
                drawItem(index, item) {
                    onItemClick(index, item)
                    expanded = false
                }

                if (index < items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}