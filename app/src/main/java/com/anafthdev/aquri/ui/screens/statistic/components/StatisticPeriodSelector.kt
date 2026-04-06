package com.anafthdev.aquri.ui.screens.statistic.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anafthdev.aquri.ui.screens.statistic.StatisticFilter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun StatisticPeriodSelector(
    selectedFilter: StatisticFilter,
    selectedDate: Long,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val periodText = remember(selectedFilter, selectedDate) {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        when (selectedFilter) {
            StatisticFilter.Daily -> {
                val today = Calendar.getInstance()
                if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                ) {
                    "Today"
                } else {
                    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(selectedDate))
                }
            }
            StatisticFilter.Weekly -> {
                // Set to first day of week
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val start = calendar.timeInMillis
                val weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
                
                // Set to last day of week
                calendar.add(Calendar.DAY_OF_WEEK, 6)
                val end = calendar.timeInMillis
                
                val format = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                "${format.format(Date(start))} - ${format.format(Date(end))} (W-$weekOfMonth)"
            }
            StatisticFilter.Monthly -> {
                SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(selectedDate))
            }
            StatisticFilter.Yearly -> {
                SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(selectedDate))
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Previous",
                modifier = Modifier.size(16.dp)
            )
        }

        // Set text style based on filter type to accommodate longer strings without resizing flickering
        val textStyle = when (selectedFilter) {
            StatisticFilter.Weekly -> MaterialTheme.typography.titleSmall.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            else -> MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = periodText,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onTextClick() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )

        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Next",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
