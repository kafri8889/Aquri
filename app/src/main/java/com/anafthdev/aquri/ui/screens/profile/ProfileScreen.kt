package com.anafthdev.aquri.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.ui.components.ClayCard
import com.anafthdev.aquri.ui.components.ClaySurface
import com.anafthdev.aquri.ui.components.clayTint
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun ProfileScreen(
    onPersonalInformation: () -> Unit,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var dialogMessage by remember { mutableStateOf<String?>(null) }

    dialogMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { dialogMessage = null },
            confirmButton = {
                TextButton(onClick = { dialogMessage = null }) {
                    Text("OK")
                }
            },
            title = { Text("Aquri") },
            text = { Text(message) }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 64.dp, bottom = 126.dp)
    ) {
        ProfileHeader(state)
        TotalVolumeCard(state)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStatCard(
                label = "CURRENT STREAK",
                value = state.currentStreakDays.toString(),
                unit = "Days",
                icon = Icons.Default.LocalFireDepartment,
                tint = AquriTheme.clay.coral,
                modifier = Modifier.weight(1f)
            )
            ProfileStatCard(
                label = "MISSIONS",
                value = state.missionsWon.toString(),
                unit = "Won",
                icon = Icons.Default.EmojiEvents,
                tint = AquriTheme.clay.sunny,
                modifier = Modifier.weight(1f)
            )
        }

        ProUpgradeCard(
            isPro = state.isPro,
            isAuthenticated = state.isAuthenticated,
            onLoginRequired = onLoginRequired,
            onShowMessage = { dialogMessage = it }
        )

        SectionTitle("HYDRATION GOALS")
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = AquriTheme.clay.surfaceStrong,
            shape = RoundedCornerShape(AquriTheme.clay.radiusLarge)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                GoalRow(
                    icon = Icons.Default.WaterDrop,
                    title = "Daily Goal",
                    subtitle = "Based on your saved profile",
                    trailing = state.dailyGoalText,
                    iconTint = MaterialTheme.colorScheme.primary,
                    iconBackground = clayTint(MaterialTheme.colorScheme.primaryContainer, 0.72f)
                )
                GoalRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Smart Reminders",
                    subtitle = "Uses local reminder settings",
                    iconTint = Color(0xFFB76612),
                    iconBackground = AquriTheme.clay.sunny,
                    switchChecked = true
                )
            }
        }

        SectionTitle("ACCOUNT & PRIVACY")
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = AquriTheme.clay.surfaceStrong,
            shape = RoundedCornerShape(AquriTheme.clay.radiusLarge)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                AccountRow(
                    icon = Icons.Default.Person,
                    title = "Personal Information",
                    subtitle = if (state.displayName == "-") "No profile name saved" else state.displayName,
                    onClick = onPersonalInformation
                )
                AccountRow(
                    icon = Icons.Default.Security,
                    title = "Security & Login",
                    subtitle = if (state.isAuthenticated) "Logged in" else "Guest access",
                    locked = !state.isAuthenticated,
                    onClick = {
                        if (state.isAuthenticated) {
                            dialogMessage = "Security settings are not available yet."
                        } else {
                            onLoginRequired()
                        }
                    }
                )
                AccountRow(
                    icon = Icons.Default.Backup,
                    title = "Backup Data",
                    subtitle = if (state.isAuthenticated) "Back up Aquri data" else "Login required",
                    locked = !state.isAuthenticated,
                    onClick = {
                        if (state.isAuthenticated) {
                            dialogMessage = "Backup is not connected yet."
                        } else {
                            onLoginRequired()
                        }
                    }
                )
                AccountRow(
                    icon = Icons.Default.Restore,
                    title = "Restore Data",
                    subtitle = if (state.isAuthenticated) "Restore from a backup" else "Login required",
                    locked = !state.isAuthenticated,
                    onClick = {
                        if (state.isAuthenticated) {
                            dialogMessage = "Restore is not connected yet."
                        } else {
                            onLoginRequired()
                        }
                    }
                )
                AccountRow(
                    icon = Icons.Default.CloudUpload,
                    title = "Export My Data",
                    subtitle = "Local export",
                    onClick = { dialogMessage = "Export is not available yet." }
                )
            }
        }

        LogoutButton(
            isAuthenticated = state.isAuthenticated,
            onClick = {
                if (state.isAuthenticated) {
                    viewModel.logout(onComplete = onLoginRequired)
                } else {
                    onLoginRequired()
                }
            }
        )
    }
}

@Composable
private fun ProfileHeader(state: ProfileUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = state.displayName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = state.subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TotalVolumeCard(state: ProfileUiState) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = AquriTheme.clay.surfaceStrong,
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "TOTAL VOLUME",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = state.totalVolumeLiters,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Liters",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = if (state.totalVolumeLiters == "0") {
                        "No hydration volume logged yet."
                    } else {
                        "Calculated from your saved hydration history."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(44.dp)
            )
        }
    }
}

@Composable
private fun ProfileStatCard(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    ClayCard(
        modifier = modifier.height(92.dp),
        containerColor = AquriTheme.clay.surfaceStrong,
        shape = RoundedCornerShape(AquriTheme.clay.radiusSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProUpgradeCard(
    isPro: Boolean,
    isAuthenticated: Boolean,
    onLoginRequired: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    ClaySurface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isAuthenticated) {
                    onShowMessage("Aquri Pro checkout is not connected yet.")
                } else {
                    onLoginRequired()
                }
            },
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        containerColor = MaterialTheme.colorScheme.primary,
        borderColor = Color.White.copy(alpha = 0.7f),
        elevation = AquriTheme.clay.floatingElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isPro) "Aquri Pro Active" else "Unlock Aquri Pro",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Advanced metrics, backup, restore, and premium controls.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAuthenticated) "UPGRADE NOW" else "LOGIN TO UPGRADE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.sp
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.sp,
        modifier = Modifier.padding(top = 2.dp)
    )
}

@Composable
private fun GoalRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    iconBackground: Color,
    trailing: String? = null,
    switchChecked: Boolean? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClaySurface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            containerColor = iconBackground,
            elevation = AquriTheme.clay.pressedElevation
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.End,
                lineHeight = 14.sp
            )
        }
        if (switchChecked != null) {
            Switch(
                checked = switchChecked,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
private fun AccountRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    locked: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(AquriTheme.clay.radiusSmall))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = if (locked) Icons.Default.Lock else Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = if (locked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun LogoutButton(
    isAuthenticated: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(AquriTheme.clay.radiusSmall))
            .background(AquriTheme.clay.surfaceStrong)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.38f)),
                RoundedCornerShape(AquriTheme.clay.radiusSmall)
            )
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isAuthenticated) "LOG OUT" else "SIGN IN",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.sp
        )
    }
}
