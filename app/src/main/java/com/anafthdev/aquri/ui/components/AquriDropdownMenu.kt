package com.anafthdev.aquri.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

sealed class AquriDropdownIcon {
    data class Vector(val imageVector: ImageVector) : AquriDropdownIcon()
    data class Resource(val resId: Int) : AquriDropdownIcon()
}

data class AquriDropdownMenuItem(
    val text: String,
    val icon: AquriDropdownIcon,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false
)

@Composable
fun AquriDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<AquriDropdownMenuItem>,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp)
) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(24.dp))
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            offset = offset,
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (item.isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        item.onClick()
                        onDismissRequest()
                    },
                    leadingIcon = {
                        when (val icon = item.icon) {
                            is AquriDropdownIcon.Vector -> {
                                Icon(
                                    imageVector = icon.imageVector,
                                    contentDescription = null,
                                    tint = if (item.isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            is AquriDropdownIcon.Resource -> {
                                Icon(
                                    painter = painterResource(id = icon.resId),
                                    contentDescription = null,
                                    tint = if (item.isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}
