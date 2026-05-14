package com.anafthdev.aquri.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anafthdev.aquri.ui.theme.AquriTheme

data class AquriSelectorOption(
    val label: String,
    val icon: ImageVector
)

/**
 * A custom selector component with two custom options.
 */
@Composable
fun AquriSelector(
    option1: AquriSelectorOption,
    option2: AquriSelectorOption,
    selectedOption: Int, // 1 for option1, 2 for option2
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        // Option 1 label
        Text(
            text = option1.label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (selectedOption == 1) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedOption == 1) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            ),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onOptionSelected(1)
            }
        )

        // The Switch/Selector component
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onOptionSelected(if (selectedOption == 1) 2 else 1)
                }
        ) {
            val indicatorOffset by animateDpAsState(
                targetValue = if (selectedOption == 1) 6.dp else 54.dp,
                label = "indicatorOffset"
            )

            // White indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .align(Alignment.CenterStart)
                    .size(40.dp, 32.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = if (selectedOption == 1) option1.icon else option2.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
        }

        // Option 2 label
        Text(
            text = option2.label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (selectedOption == 2) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedOption == 2) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            ),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onOptionSelected(2)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AquriSelectorLightPreview() {
    AquriTheme {
        Surface {
            Box(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                AquriSelector(
                    option1 = AquriSelectorOption("Developer", Icons.Default.Code),
                    option2 = AquriSelectorOption("Designer", Icons.Default.Create),
                    selectedOption = 1,
                    onOptionSelected = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AquriSelectorDarkPreview() {
    AquriTheme(darkTheme = true) {
        Surface {
            Box(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                AquriSelector(
                    option1 = AquriSelectorOption("Developer", Icons.Default.Code),
                    option2 = AquriSelectorOption("Designer", Icons.Default.Create),
                    selectedOption = 2,
                    onOptionSelected = {}
                )
            }
        }
    }
}
