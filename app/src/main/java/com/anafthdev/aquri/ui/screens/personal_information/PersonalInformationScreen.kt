package com.anafthdev.aquri.ui.screens.personal_information

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anafthdev.aquri.data.model.enum.ActivityLevel
import com.anafthdev.aquri.data.model.enum.Climate
import com.anafthdev.aquri.data.model.enum.Gender
import com.anafthdev.aquri.ui.components.ClayCard
import com.anafthdev.aquri.ui.components.ClayIconButton
import com.anafthdev.aquri.ui.components.ClayPrimaryButton
import com.anafthdev.aquri.ui.components.ClaySelectableCard
import com.anafthdev.aquri.ui.components.ClaySurface
import com.anafthdev.aquri.ui.components.clayTint
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun PersonalInformationScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PersonalInformationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 44.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClayIconButton(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onNavigateBack,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Only data Aquri needs for hydration.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ProfileSummaryCard(state)

        SectionTitle("ACCOUNT")
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = AquriTheme.clay.surfaceStrong,
            shape = RoundedCornerShape(AquriTheme.clay.radiusLarge)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PersonalTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChanged,
                    label = "Name",
                    placeholder = state.displayName,
                    icon = Icons.Default.Person
                )
                PersonalTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = "Email",
                    placeholder = state.displayEmail,
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
            }
        }

        SectionTitle("HYDRATION PROFILE")
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = AquriTheme.clay.surfaceStrong,
            shape = RoundedCornerShape(AquriTheme.clay.radiusLarge)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OptionGroupTitle("Gender")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GenderOption(
                        label = "Male",
                        icon = Icons.Default.Male,
                        selected = state.gender == Gender.Male,
                        onClick = { viewModel.onGenderSelected(Gender.Male) },
                        modifier = Modifier.weight(1f)
                    )
                    GenderOption(
                        label = "Female",
                        icon = Icons.Default.Female,
                        selected = state.gender == Gender.Female,
                        onClick = { viewModel.onGenderSelected(Gender.Female) },
                        modifier = Modifier.weight(1f)
                    )
                    GenderOption(
                        label = "Other",
                        icon = Icons.Default.Transgender,
                        selected = state.gender == Gender.Other,
                        onClick = { viewModel.onGenderSelected(Gender.Other) },
                        modifier = Modifier.weight(1f)
                    )
                }

                PersonalTextField(
                    value = state.weight,
                    onValueChange = viewModel::onWeightChanged,
                    label = "Weight",
                    placeholder = "-",
                    icon = Icons.Default.Scale,
                    keyboardType = KeyboardType.Decimal,
                    trailingText = "kg"
                )

                OptionGroupTitle("Activity")
                SegmentRow {
                    ActivityLevel.entries.forEach { level ->
                        SegmentChip(
                            text = level.displayName(),
                            selected = state.activityLevel == level,
                            onClick = { viewModel.onActivityLevelSelected(level) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OptionGroupTitle("Climate")
                SegmentRow {
                    Climate.entries.forEach { climate ->
                        SegmentChip(
                            text = climate.displayName(),
                            selected = state.climate == climate,
                            onClick = { viewModel.onClimateSelected(climate) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        SectionTitle("CALCULATED GOAL")
        CalculatedGoalCard(state)

        state.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AquriTheme.clay.radiusSmall))
                    .background(clayTint(MaterialTheme.colorScheme.primaryContainer, 0.72f))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }

        ClayPrimaryButton(
            onClick = viewModel::saveChanges,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save Changes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun ProfileSummaryCard(state: PersonalInformationUiState) {
    ClaySurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AquriTheme.clay.radiusLarge),
        containerColor = MaterialTheme.colorScheme.primary,
        borderColor = Color.White.copy(alpha = 0.7f),
        elevation = AquriTheme.clay.floatingElevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClaySurface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                containerColor = Color.White.copy(alpha = 0.22f),
                borderColor = Color.White.copy(alpha = 0.8f),
                elevation = AquriTheme.clay.pressedElevation
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = state.displayEmail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PersonalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        trailingIcon = trailingText?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AquriTheme.clay.surface,
            unfocusedContainerColor = AquriTheme.clay.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.72f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f),
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun GenderOption(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ClaySelectableCard(
        selected = selected,
        onClick = onClick,
        modifier = modifier.height(92.dp),
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        contentPadding = PaddingValues(10.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun SegmentRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun SegmentChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(AquriTheme.clay.radiusSmall))
            .background(if (selected) clayTint(MaterialTheme.colorScheme.primaryContainer, 0.84f) else AquriTheme.clay.surface)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f),
                shape = RoundedCornerShape(AquriTheme.clay.radiusSmall)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CalculatedGoalCard(state: PersonalInformationUiState) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = clayTint(MaterialTheme.colorScheme.primaryContainer, 0.72f),
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
        shape = RoundedCornerShape(AquriTheme.clay.radiusLarge)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClaySurface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                containerColor = AquriTheme.clay.surfaceStrong,
                elevation = AquriTheme.clay.pressedElevation
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily hydration goal",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Updated from weight, activity, climate, and gender.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = state.dailyGoalText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun OptionGroupTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Black
    )
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

private fun ActivityLevel.displayName(): String {
    return when (this) {
        ActivityLevel.Sedentary -> "Low"
        ActivityLevel.Moderate -> "Medium"
        ActivityLevel.Active -> "High"
    }
}

private fun Climate.displayName(): String {
    return when (this) {
        Climate.Cold -> "Cold"
        Climate.Mild -> "Mild"
        Climate.Hot -> "Hot"
    }
}
