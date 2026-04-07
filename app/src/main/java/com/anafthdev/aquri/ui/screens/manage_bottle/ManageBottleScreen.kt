package com.anafthdev.aquri.ui.screens.manage_bottle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.model.enum.DrinkBottleIcon
import com.anafthdev.aquri.ui.components.AquriDropdownIcon
import com.anafthdev.aquri.ui.components.AquriDropdownMenu
import com.anafthdev.aquri.ui.components.AquriDropdownMenuItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBottleScreen(
    onNavigateBack: () -> Unit,
    viewModel: ManageBottleViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val bottles by viewModel.bottles.collectAsState()
    val defaultBottles by viewModel.defaultBottles.collectAsState()
    val customBottles by viewModel.customBottles.collectAsState()
    val defaultDrinkTypes by viewModel.defaultDrinkTypes.collectAsState()
    val customDrinkTypes by viewModel.customDrinkTypes.collectAsState()
    
    var selectedSlot by remember { mutableIntStateOf(1) }
    var showBottleBottomSheet by remember { mutableStateOf(false) }
    var showDrinkTypeBottomSheet by remember { mutableStateOf(false) }
    var editingBottle by remember { mutableStateOf<BottleEntity?>(null) }
    var editingDrinkType by remember { mutableStateOf<DrinkTypeEntity?>(null) }
    var deletingBottle by remember { mutableStateOf<BottleEntity?>(null) }
    var deletingDrinkType by remember { mutableStateOf<DrinkTypeEntity?>(null) }
    
    val bottleSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val drinkTypeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Manage Vessels",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Customize your dashboard slots and beverage types.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = "Dashboard Slots",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DashboardSlot(
                            index = 1,
                            isSelected = selectedSlot == 1,
                            bottle = bottles.find { it.id == user?.bottleSlot1 },
                            onClick = { selectedSlot = 1 },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        DashboardSlot(
                            index = 2,
                            isSelected = selectedSlot == 2,
                            bottle = bottles.find { it.id == user?.bottleSlot2 },
                            onClick = { selectedSlot = 2 },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        DashboardSlot(
                            index = 3,
                            isSelected = selectedSlot == 3,
                            bottle = bottles.find { it.id == user?.bottleSlot3 },
                            onClick = { selectedSlot = 3 },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Text(
                        text = "DEFAULT BOTTLES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            items(
                count = defaultBottles.size,
                span = { GridItemSpan(maxLineSpan) }
            ) { index ->
                val bottle = defaultBottles[index]
                val isAssigned = listOf(user?.bottleSlot1, user?.bottleSlot2, user?.bottleSlot3).contains(bottle.id)
                BottleListItem(
                    bottle = bottle,
                    isAssignedToCurrentSlot = when(selectedSlot) {
                        1 -> user?.bottleSlot1 == bottle.id
                        2 -> user?.bottleSlot2 == bottle.id
                        3 -> user?.bottleSlot3 == bottle.id
                        else -> false
                    },
                    onClick = {
                        viewModel.assignBottleToSlot(selectedSlot, bottle.id)
                    }
                )
            }
            
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "MY CUSTOM BOTTLES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            items(count = customBottles.size) { index ->
                val bottle = customBottles[index]
                val isAssignedToCurrentSlot = when(selectedSlot) {
                    1 -> user?.bottleSlot1 == bottle.id
                    2 -> user?.bottleSlot2 == bottle.id
                    3 -> user?.bottleSlot3 == bottle.id
                    else -> false
                }
                CustomBottleCard(
                    bottle = bottle,
                    isAssigned = isAssignedToCurrentSlot,
                    onEdit = { 
                        editingBottle = bottle
                        showBottleBottomSheet = true
                    },
                    onDelete = { deletingBottle = bottle },
                    onClick = { viewModel.assignBottleToSlot(selectedSlot, bottle.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                CreateNewCard(
                    text = "New Bottle",
                    onClick = { 
                        editingBottle = null
                        showBottleBottomSheet = true 
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(
                        text = "DRINK TYPES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(
                count = defaultDrinkTypes.size,
                span = { GridItemSpan(maxLineSpan) }
            ) { index ->
                val drinkType = defaultDrinkTypes[index]
                DrinkTypeListItem(
                    drinkType = drinkType,
                    onClick = { /* Predefined types are view-only here or can be selectable if needed */ }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "MY CUSTOM DRINK TYPES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(count = customDrinkTypes.size) { index ->
                val drinkType = customDrinkTypes[index]
                CustomDrinkTypeCard(
                    drinkType = drinkType,
                    onEdit = { 
                        editingDrinkType = drinkType
                        showDrinkTypeBottomSheet = true
                    },
                    onDelete = { deletingDrinkType = drinkType },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                CreateNewCard(
                    text = "New Drink Type",
                    onClick = { 
                        editingDrinkType = null
                        showDrinkTypeBottomSheet = true 
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // --- Dialogs & Bottom Sheets ---

    if (deletingBottle != null) {
        AlertDialog(
            onDismissRequest = { deletingBottle = null },
            title = { Text("Delete Bottle") },
            text = { Text("Are you sure you want to delete '${deletingBottle?.name}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCustomBottle(deletingBottle!!)
                        deletingBottle = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingBottle = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (deletingDrinkType != null) {
        AlertDialog(
            onDismissRequest = { deletingDrinkType = null },
            title = { Text("Delete Drink Type") },
            text = { Text("Are you sure you want to delete '${deletingDrinkType?.name}'? History logs using this type might be affected.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCustomDrinkType(deletingDrinkType!!)
                        deletingDrinkType = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingDrinkType = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showBottleBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                showBottleBottomSheet = false
                editingBottle = null
            },
            sheetState = bottleSheetState
        ) {
            CreateBottleBottomSheetContent(
                initialBottle = editingBottle,
                onSave = { name, volume, icon ->
                    if (editingBottle != null) {
                        viewModel.updateCustomBottle(
                            editingBottle!!.copy(
                                name = name,
                                volumeMl = volume,
                                icon = icon
                            )
                        )
                    } else {
                        viewModel.createCustomBottle(name, volume, icon)
                    }
                    scope.launch { bottleSheetState.hide() }.invokeOnCompletion {
                        if (!bottleSheetState.isVisible) {
                            showBottleBottomSheet = false
                            editingBottle = null
                        }
                    }
                }
            )
        }
    }

    if (showDrinkTypeBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                showDrinkTypeBottomSheet = false
                editingDrinkType = null
            },
            sheetState = drinkTypeSheetState
        ) {
            CreateDrinkTypeBottomSheetContent(
                initialDrinkType = editingDrinkType,
                onSave = { name, color ->
                    if (editingDrinkType != null) {
                        viewModel.updateCustomDrinkType(
                            editingDrinkType!!.copy(
                                name = name,
                                hexColor = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                            )
                        )
                    } else {
                        viewModel.createCustomDrinkType(
                            name, 
                            String.format("#%06X", (0xFFFFFF and color.toArgb()))
                        )
                    }
                    scope.launch { drinkTypeSheetState.hide() }.invokeOnCompletion {
                        if (!drinkTypeSheetState.isVisible) {
                            showDrinkTypeBottomSheet = false
                            editingDrinkType = null
                        }
                    }
                }
            )
        }
    }
}

// --- Components ---

@Composable
fun DashboardSlot(
    index: Int,
    isSelected: Boolean,
    bottle: BottleEntity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable { onClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (bottle != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = bottle.icon.let { DrinkBottleIcon.fromString(it).resId }
                        ),
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${bottle.volumeMl.toInt()}ml",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = bottle.name,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "SLOT $index",
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun BottleListItem(
    bottle: BottleEntity,
    isAssignedToCurrentSlot: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = DrinkBottleIcon.fromString(bottle.icon).resId),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = bottle.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${bottle.volumeMl.toInt()}ml \u2022 Predefined",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(
                        width = 2.dp,
                        color = if (isAssignedToCurrentSlot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
                    .background(
                        if (isAssignedToCurrentSlot) MaterialTheme.colorScheme.primary else Color.Transparent,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isAssignedToCurrentSlot) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DrinkTypeListItem(
    drinkType: DrinkTypeEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(android.graphics.Color.parseColor(drinkType.hexColor)), CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = drinkType.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Predefined Type",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CustomBottleCard(
    bottle: BottleEntity,
    isAssigned: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = DrinkBottleIcon.fromString(bottle.icon).resId),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AquriDropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        items = listOf(
                            AquriDropdownMenuItem(
                                text = "Edit",
                                icon = AquriDropdownIcon.Vector(Icons.Default.Edit),
                                onClick = onEdit
                            ),
                            AquriDropdownMenuItem(
                                text = "Delete",
                                icon = AquriDropdownIcon.Vector(Icons.Default.Delete),
                                isDestructive = true,
                                onClick = onDelete
                            )
                        )
                    )
                }
            }

            Column {
                Text(
                    text = bottle.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${bottle.volumeMl.toInt()}ml",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isAssigned) {
                Button(
                    onClick = { /* Already assigned */ },
                    shape = RoundedCornerShape(24.dp),
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Assigned", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Assign", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CustomDrinkTypeCard(
    drinkType: DrinkTypeEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(android.graphics.Color.parseColor(drinkType.hexColor)), CircleShape)
                )

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AquriDropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        items = listOf(
                            AquriDropdownMenuItem(
                                text = "Edit",
                                icon = AquriDropdownIcon.Vector(Icons.Default.Edit),
                                onClick = onEdit
                            ),
                            AquriDropdownMenuItem(
                                text = "Delete",
                                icon = AquriDropdownIcon.Vector(Icons.Default.Delete),
                                isDestructive = true,
                                onClick = onDelete
                            )
                        )
                    )
                }
            }

            Text(
                text = drinkType.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CreateNewCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(32.dp))
            .clickable { onClick() }
            .drawBehindDashedBorder(MaterialTheme.colorScheme.outlineVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateBottleBottomSheetContent(
    initialBottle: BottleEntity? = null,
    onSave: (String, Float, String) -> Unit
) {
    var name by remember { mutableStateOf(initialBottle?.name ?: "") }
    var volume by remember { 
        mutableStateOf(
            initialBottle?.volumeMl?.let { 
                if (it % 1f == 0f) it.toInt().toString() else it.toString() 
            } ?: ""
        ) 
    }
    var selectedIcon by remember { 
        mutableStateOf(
            initialBottle?.icon?.let { DrinkBottleIcon.fromString(it) } ?: DrinkBottleIcon.Bottle1
        ) 
    }

    var nameError by remember { mutableStateOf<String?>(null) }
    var volumeError by remember { mutableStateOf<String?>(null) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = if (initialBottle != null) "Edit Custom Bottle" else "New Custom Bottle",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (initialBottle != null) "Update your custom vessel details." else "Define a unique vessel for your daily goal.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("BOTTLE NAME", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = name,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = {
                name = it.take(20) // Max 20 char
                if (nameError != null && it.isNotBlank()) nameError = null
            },
            isError = nameError != null,
            supportingText = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (nameError != null) {
                        Text(nameError!!, modifier = Modifier.weight(1f))
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text("${name.length}/20")
                }
            },
            placeholder = { Text("e.g., Office Carafe") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Text("VOLUME", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = volume,
            shape = RoundedCornerShape(16.dp),
            isError = volumeError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() || it == '.' }) {
                    volume = input.take(4) // Max 9999ml
                    volumeError = null
                }
            },
            suffix = {
                Text("ml")
            },
            supportingText = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (volumeError != null) {
                        Text(volumeError!!, modifier = Modifier.weight(1f))
                    } else {
                        Text("Max volume 9999ml", modifier = Modifier.weight(1f))
                    }
                    Text("${volume.length}/4")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        val icons = remember { DrinkBottleIcon.entries }
        Text("SELECT ICON", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            icons.forEach { icon ->
                val isSelected = selectedIcon == icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .border(
                            width = 2.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedIcon = icon },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon.resId),
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                val vol = volume.toFloatOrNull() ?: 0f
                if (name.isBlank()) nameError = "Name cannot be empty"
                if (vol <= 0) volumeError = "Invalid volume"
                
                if (name.isNotBlank() && vol > 0) {
                    onSave(name, vol, selectedIcon.name)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (initialBottle != null) "Update Bottle" else "Save Bottle")
        }
    }
}

@Composable
fun CreateDrinkTypeBottomSheetContent(
    initialDrinkType: DrinkTypeEntity? = null,
    onSave: (String, Color) -> Unit
) {
    var name by remember { mutableStateOf(initialDrinkType?.name ?: "") }
    var selectedColor by remember { 
        mutableStateOf(
            initialDrinkType?.hexColor?.let { Color(it.toColorInt()) } ?: Color(0xFF00ACC1)
        ) 
    }

    val colors = listOf(
        Color(0xFF00ACC1), Color(0xFF26A69A), Color(0xFFEF6C00), 
        Color(0xFFFDD835), Color(0xFFE53935), Color(0xFF1E88E5),
        Color(0xFFBDBDBD), Color(0xFF7E57C2), Color(0xFFD81B60),
        Color(0xFF43A047), Color(0xFF8E24AA), Color(0xFF3949AB)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = if (initialDrinkType != null) "Edit Drink Type" else "New Drink Type",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("NAME", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = name,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            onValueChange = { name = it.take(16) },
            placeholder = { Text("e.g., Green Tea") },
            supportingText = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Text("${name.length}/16")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Text("SELECT COLOR", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.take(6).forEach { color ->
                ColorOption(
                    color = color,
                    isSelected = selectedColor == color,
                    onClick = { selectedColor = color }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.drop(6).forEach { color ->
                ColorOption(
                    color = color,
                    isSelected = selectedColor == color,
                    onClick = { selectedColor = color }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { if (name.isNotBlank()) onSave(name, selectedColor) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (initialDrinkType != null) "Update Type" else "Save Type")
        }
    }
}

@Composable
fun ColorOption(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() }
    )
}

fun Modifier.drawBehindDashedBorder(color: Color) = this.drawBehind {
    val stroke = Stroke(
        width = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(32.dp.toPx())
    )
}
