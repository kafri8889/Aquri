package com.anafthdev.aquri.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import com.anafthdev.aquri.data.model.enum.DrinkBottleIcon
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDrinkBottomSheetContent(
    bottles: List<BottleEntity>,
    drinkTypes: List<DrinkTypeEntity>,
    existingLog: HydrationLogWithBottle? = null,
    initialSelectedBottle: BottleEntity? = null,
    onSave: (BottleEntity, DrinkTypeEntity, Long) -> Unit
) {
    var selectedBottle by remember { 
        mutableStateOf(
            existingLog?.bottle ?: 
            initialSelectedBottle ?: 
            bottles.firstOrNull()
        ) 
    }
    
    var selectedDrinkType by remember { 
        mutableStateOf(
            drinkTypes.find { it.id == existingLog?.log?.drinkTypeId } ?: 
            drinkTypes.find { it.name == "Water" } ?: 
            drinkTypes.firstOrNull()
        ) 
    }
    
    var logTime by remember { 
        mutableStateOf(existingLog?.log?.loggedAt ?: System.currentTimeMillis()) 
    }
    
    var showTimePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val timeFormatter = remember { SimpleDateFormat(if (android.text.format.DateFormat.is24HourFormat(context)) "HH:mm" else "hh:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = if (existingLog != null) "Edit Log" else "Add Log",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- Bottle Selection ---
        Text(
            text = "SELECT BOTTLE",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(bottles) { bottle ->
                val isSelected = selectedBottle?.id == bottle.id
                Card(
                    shape = RoundedCornerShape(24.dp),
                    onClick = { selectedBottle = bottle },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.size(width = 100.dp, height = 120.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = DrinkBottleIcon.fromString(bottle.icon).resId),
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${bottle.volumeMl.toInt()}ml",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = bottle.name,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- Drink Type Selection ---
        Text(
            text = "DRINK TYPE",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(drinkTypes) { type ->
                val isSelected = selectedDrinkType?.id == type.id
                val color = Color(android.graphics.Color.parseColor(type.hexColor))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) color.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .border(
                            width = 2.dp,
                            color = if (isSelected) color else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedDrinkType = type }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = type.name,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- Time Selection ---
        Text(
            text = "TIME",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable { showTimePicker = true }
                .padding(16.dp)
                .padding(horizontal = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = timeFormatter.format(Date(logTime)),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // --- Save Button ---
        Button(
            onClick = {
                val bottle = selectedBottle ?: return@Button
                val type = selectedDrinkType ?: return@Button
                onSave(bottle, type, logTime)
            },
            shape = RoundedCornerShape(16.dp),
            enabled = selectedBottle != null && selectedDrinkType != null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (existingLog != null) "Update Log" else "Save Log",
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showTimePicker) {
        val calendar = Calendar.getInstance().apply { timeInMillis = logTime }
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = android.text.format.DateFormat.is24HourFormat(context)
        )

        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                val newCal = Calendar.getInstance().apply {
                                    timeInMillis = logTime
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    set(Calendar.MINUTE, timePickerState.minute)
                                }
                                logTime = newCal.timeInMillis
                                showTimePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
