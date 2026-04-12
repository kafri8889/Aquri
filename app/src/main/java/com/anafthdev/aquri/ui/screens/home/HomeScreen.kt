package com.anafthdev.aquri.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.data.Constant
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import com.anafthdev.aquri.data.model.enum.DrinkBottleIcon
import com.anafthdev.aquri.ui.components.AquriDropdownIcon
import com.anafthdev.aquri.ui.components.AquriDropdownMenu
import com.anafthdev.aquri.ui.components.AquriDropdownMenuItem
import com.anafthdev.aquri.ui.screens.home.components.LogDrinkBottomSheetContent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onManageBottle: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {

    val user by viewModel.user.collectAsState()
    val dailySummary by viewModel.dailySummary.collectAsState()
    val gamification by viewModel.gamification.collectAsState()
    val histories by viewModel.recentLogs.collectAsStateWithLifecycle()
    val nextReminderTime by viewModel.nextReminderTime.collectAsStateWithLifecycle()
    val slotBottles by viewModel.slotBottles.collectAsStateWithLifecycle()
    val bottles by viewModel.bottles.collectAsStateWithLifecycle()
    val drinkTypes by viewModel.drinkTypes.collectAsStateWithLifecycle()

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedLog by remember { mutableStateOf<HydrationLogWithBottle?>(null) }
    var selectedBottle by remember { mutableStateOf<BottleEntity?>(null) }
    var deletingLog by remember { mutableStateOf<HydrationLogWithBottle?>(null) }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                HeroSection(
                    user = user,
                    dailySummary = dailySummary,
                    gamification = gamification
                )
            }

            item {
                QuickRefillSection(
                    bottles = slotBottles,
                    onCustomQuickRefillClicked = onManageBottle,
                    onDrinkClick = { bottle ->
                        selectedBottle = bottle
                        selectedLog = null
                        showBottomSheet = true
                    }
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Button(
                        onClick = {
                            selectedBottle = null
                            selectedLog = null
                            showBottomSheet = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Choose Bottle", fontWeight = FontWeight.Bold)
                    }
                }
            }

            drinkHistorySection(
                nextReminderTime = nextReminderTime,
                histories = histories,
                onEdit = { log ->
                    selectedLog = log
                    selectedBottle = null
                    showBottomSheet = true
                },
                onDelete = { log ->
                    deletingLog = log
                }
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                showBottomSheet = false
                selectedLog = null
                selectedBottle = null
            },
            sheetState = sheetState
        ) {
            LogDrinkBottomSheetContent(
                bottles = bottles,
                drinkTypes = drinkTypes,
                existingLog = selectedLog,
                initialSelectedBottle = selectedBottle,
                onSave = { bottle, type, time, customAmount ->
                    if (selectedLog != null) {
                        viewModel.updateLog(selectedLog!!.log, bottle, type, time, customAmount)
                    } else {
                        viewModel.logDrink(bottle, type, time, customAmount)
                    }
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                            selectedLog = null
                            selectedBottle = null
                        }
                    }
                }
            )
        }
    }

    if (deletingLog != null) {
        AlertDialog(
            onDismissRequest = { deletingLog = null },
            title = { Text("Delete Log") },
            text = { Text("Are you sure you want to delete this drink entry? The daily total will be updated.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLog(deletingLog!!.log)
                        deletingLog = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingLog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HeroSection(
    user: UserEntity?,
    dailySummary: DailySummaryEntity?,
    gamification: UserGamificationEntity?,
    modifier: Modifier = Modifier
) {
    val totalMl = dailySummary?.totalMl ?: 0f
    val goalMl = dailySummary?.goalMl?.takeIf { it > 0 } ?: user?.dailyGoalMl ?: Constant.MIN_DAILY_GOAL
    val progress = if (goalMl > 0) totalMl / goalMl else 0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Box(
            modifier = Modifier.size(280.dp)
        ) {
            // Gauge Background & Ticks
            val gaugeColor = MaterialTheme.colorScheme.surfaceVariant
            val primaryColor = MaterialTheme.colorScheme.primary
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 24.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)
                
                // Draw background track
                drawArc(
                    color = gaugeColor,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Draw progress track
                drawArc(
                    color = primaryColor,
                    startAngle = 135f,
                    sweepAngle = 270f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Draw ticks
                val tickCount = 24
                val tickLength = 8.dp.toPx()
                val tickStartRadius = radius - strokeWidth / 2 - 12.dp.toPx()

                for (i in 0..tickCount) {
                    val angleInDegrees = 135f + (270f * i / tickCount)
                    val angleInRadians = angleInDegrees * PI / 180f

                    val start = Offset(
                        center.x + tickStartRadius * cos(angleInRadians).toFloat(),
                        center.y + tickStartRadius * sin(angleInRadians).toFloat()
                    )
                    val end = Offset(
                        center.x + (tickStartRadius - tickLength) * cos(angleInRadians).toFloat(),
                        center.y + (tickStartRadius - tickLength) * sin(angleInRadians).toFloat()
                    )

                    drawLine(
                        color = if (angleInDegrees <= 135f + 270f * progress) primaryColor else gaugeColor.copy(alpha = 0.5f),
                        start = start,
                        end = end,
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // Water Drop (Centered)
            Box(
                modifier = Modifier
                    .size(100.dp, 120.dp)
                    .align(Alignment.Center)
                    .clip(WaterDropShape())
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f))
            ) {
                WaveAnimation(
                    modifier = Modifier.fillMaxSize(),
                    progress = progress,
                    color = primaryColor,
                    amplitude = 15f,
                    frequency = 0.02f
                )
            }

            // Intake Text (Below Water Drop)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = totalMl.toInt().toString(),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "/ ${goalMl.toInt()} ml",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

class WaterDropShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val path = Path().apply {
            val cx = size.width / 2f

            // bikin lebih besar & proporsional
            val radius = size.width * 0.32f
            val cy = size.height * 0.65f

            val leftX = cx - radius
            val rightX = cx + radius
            val topY = cy - radius * 1.6f

            // mulai dari kiri bawah
            moveTo(leftX, cy)

            // sisi kiri → atas (smooth)
            cubicTo(
                leftX, cy - radius * 0.5f,
                cx - radius * 0.3f, topY,
                cx, topY
            )

            // sisi kanan → bawah (smooth)
            cubicTo(
                cx + radius * 0.3f, topY,
                rightX, cy - radius * 0.5f,
                rightX, cy
            )

            // setengah lingkaran bawah
            arcTo(
                rect = Rect(
                    cx - radius,
                    cy - radius,
                    cx + radius,
                    cy + radius
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )

            close()
        }

        return Outline.Generic(path)
    }
}

@Composable
private fun QuickRefillSection(
    bottles: List<BottleEntity?>,
    onCustomQuickRefillClicked: () -> Unit,
    onDrinkClick: (BottleEntity) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 16.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quick Refill",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(
                onClick = onCustomQuickRefillClicked,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            bottles.forEach { bottle ->
                QuickRefillButton(
                    bottle = bottle,
                    onClick = {
                        if (bottle != null) onDrinkClick(bottle)
                        else onCustomQuickRefillClicked()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickRefillButton(
    bottle: BottleEntity?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val containerColor = if (bottle != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.24f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.24f)
    
    val borderColor = if (bottle != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)

    Card(
        shape = RoundedCornerShape(28.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .height(120.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (bottle != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = DrinkBottleIcon.fromString(bottle.icon).resId),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = bottle.volumeMl.toInt().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "ml",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .drawBehindDashedBorder(MaterialTheme.colorScheme.outlineVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Empty",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun LazyListScope.drinkHistorySection(
    nextReminderTime: Long?,
    histories: List<HydrationLogWithBottle>,
    onEdit: (HydrationLogWithBottle) -> Unit,
    onDelete: (HydrationLogWithBottle) -> Unit
) {
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 16.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Drink History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }

    if (nextReminderTime != null) {
        item {
            TimelineItem(
                isFirst = true,
                isLast = histories.isEmpty(),
                isActive = true
            ) {
                NextReminderCard(
                    time = nextReminderTime,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (histories.isEmpty() && nextReminderTime == null) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No history available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        items(
            items = histories,
            key = { it.log.id }
        ) { history ->
            val index = histories.indexOf(history)
            val isFirst = nextReminderTime == null && index == 0
            val isLast = index == histories.size - 1

            TimelineItem(
                isFirst = isFirst,
                isLast = isLast,
                isActive = false
            ) {
                DrinkHistoryItem(
                    history = history,
                    onEdit = { onEdit(history) },
                    onDelete = { onDelete(history) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TimelineItem(
    isFirst: Boolean,
    isLast: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .height(IntrinsicSize.Min)
    ) {
        TimelineNode(
            isFirst = isFirst,
            isLast = isLast,
            isActive = isActive,
            modifier = Modifier.fillMaxHeight()
        )
        
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(1f)
        ) {
            content()
        }
    }
}

@Composable
private fun TimelineNode(
    isFirst: Boolean,
    isLast: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(32.dp),
        contentAlignment = Alignment.Center
    ) {
        val lineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        val activeColor = MaterialTheme.colorScheme.primary
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            if (!isFirst) {
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, centerY - (if (isActive) 12.dp.toPx() else 8.dp.toPx())),
                    strokeWidth = 2.dp.toPx()
                )
            }
            
            if (!isLast) {
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, centerY + (if (isActive) 12.dp.toPx() else 8.dp.toPx())),
                    end = Offset(centerX, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        
        Box(
            modifier = Modifier
                .size(if (isActive) 16.dp else 10.dp)
                .border(
                    width = 2.dp,
                    color = if (isActive) activeColor else lineColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(activeColor, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun NextReminderCard(
    time: Long,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeStr = timeFormatter.format(Date(time))
    
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.height(112.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Keep it up! Your next hydration is coming up.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DrinkHistoryItem(
    history: HydrationLogWithBottle,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val timeFormatter = remember { SimpleDateFormat(if (android.text.format.DateFormat.is24HourFormat(context)) "HH:mm" else "hh:mm a", Locale.getDefault()) }
    val time = timeFormatter.format(Date(history.log.loggedAt))
    
    val name = history.log.bottleName ?: history.bottle?.name ?: "Unknown"
    val color = MaterialTheme.colorScheme.primary
    val iconBg = color.copy(alpha = 0.1f)
    
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.height(74.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = history.bottle?.let { DrinkBottleIcon.fromString(it.icon).resId } 
                             ?: DrinkBottleIcon.WaterBottle.resId
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = color
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    history.drinkType?.let { type ->
                        Text(
                            text = " \u2022 ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = type.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(type.hexColor.toColorInt()),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+${history.log.amountMl.toInt()}ml",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
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
        }
    }
}

private fun Modifier.drawBehindDashedBorder(color: Color) = this.drawBehind {
    val stroke = Stroke(
        width = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(24.dp.toPx())
    )
}
