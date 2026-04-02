package com.anafthdev.aquri.ui.screens.onboarding

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anafthdev.aquri.R
import com.anafthdev.aquri.ui.navigation.Destinations
import com.anafthdev.aquri.ui.screens.onboarding.components.OnboardingBottomButton
import com.anafthdev.aquri.ui.screens.onboarding.components.OnboardingCard
import com.anafthdev.aquri.ui.screens.onboarding.components.OnboardingHeader
import com.anafthdev.aquri.ui.screens.onboarding.components.OnboardingProgressIndicator

@Composable
fun OnboardingScreen2(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            OnboardingProgressIndicator(step = 1, totalSteps = 3)
            OnboardingHeader(
                titleRes = R.string.activity_title,
                subtitleRes = R.string.activity_subtitle
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActivityLevelCard(
                    level = ActivityLevel.Sedentary,
                    isSelected = uiState.activityLevel == ActivityLevel.Sedentary,
                    onClick = { viewModel.onActivityLevelSelected(ActivityLevel.Sedentary) }
                )
                ActivityLevelCard(
                    level = ActivityLevel.Moderate,
                    isSelected = uiState.activityLevel == ActivityLevel.Moderate,
                    onClick = { viewModel.onActivityLevelSelected(ActivityLevel.Moderate) }
                )
                ActivityLevelCard(
                    level = ActivityLevel.Active,
                    isSelected = uiState.activityLevel == ActivityLevel.Active,
                    onClick = { viewModel.onActivityLevelSelected(ActivityLevel.Active) }
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            OnboardingHeader(
                titleRes = R.string.climate_title,
                subtitleRes = R.string.climate_subtitle
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ClimateCard(
                    climate = Climate.Cold,
                    isSelected = uiState.climate == Climate.Cold,
                    onClick = { viewModel.onClimateSelected(Climate.Cold) },
                    modifier = Modifier.weight(1f)
                )
                ClimateCard(
                    climate = Climate.Mild,
                    isSelected = uiState.climate == Climate.Mild,
                    onClick = { viewModel.onClimateSelected(Climate.Mild) },
                    modifier = Modifier.weight(1f)
                )
                ClimateCard(
                    climate = Climate.Hot,
                    isSelected = uiState.climate == Climate.Hot,
                    onClick = { viewModel.onClimateSelected(Climate.Hot) },
                    modifier = Modifier.weight(1f)
                )
            }

            OnboardingBottomButton(
                textRes = R.string.calculate_goal,
                onClick = {
                    viewModel.saveGoal()
                    navController.navigate(Destinations.Onboarding3)
                }
            )
            
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun ActivityLevelCard(
    level: ActivityLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, borderColor, RoundedCornerShape(32.dp))
            .clickable(onClick = onClick)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (level) {
                    ActivityLevel.Sedentary -> Icons.Default.Chair
                    ActivityLevel.Moderate -> Icons.Default.DirectionsWalk
                    ActivityLevel.Active -> Icons.Default.DirectionsRun
                },
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when (level) {
                    ActivityLevel.Sedentary -> stringResource(R.string.sedentary)
                    ActivityLevel.Moderate -> stringResource(R.string.moderate)
                    ActivityLevel.Active -> stringResource(R.string.active)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when (level) {
                    ActivityLevel.Sedentary -> stringResource(R.string.sedentary_desc)
                    ActivityLevel.Moderate -> stringResource(R.string.moderate_desc)
                    ActivityLevel.Active -> stringResource(R.string.active_desc)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun ClimateCard(
    climate: Climate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingCard(
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (climate) {
                        Climate.Cold -> Icons.Default.AcUnit
                        Climate.Mild -> Icons.Default.Thermostat
                        Climate.Hot -> Icons.Default.WbSunny
                    },
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = when (climate) {
                    Climate.Cold -> stringResource(R.string.cold)
                    Climate.Mild -> stringResource(R.string.mild)
                    Climate.Hot -> stringResource(R.string.hot)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
