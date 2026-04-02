package com.anafthdev.aquri.ui.screens.onboarding

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.anafthdev.aquri.R
import com.anafthdev.aquri.ui.navigation.Destinations
import com.anafthdev.aquri.ui.screens.onboarding.components.OnboardingBottomButton
import com.anafthdev.aquri.ui.screens.onboarding.components.OnboardingCard
import java.text.DecimalFormat

@Composable
fun OnboardingScreen3(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val goalFormatter = DecimalFormat("#,###")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.goal_title).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.4.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = goalFormatter.format(uiState.dailyGoal),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-3.2).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.ml),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ResultSummaryCard(
                    icon = Icons.Default.MonitorWeight,
                    label = "BODY WEIGHT",
                    value = "${uiState.weight} ${if (uiState.weightUnit == WeightUnit.KG) "kg" else "lbs"}"
                )
                ResultSummaryCard(
                    icon = Icons.Default.DirectionsRun,
                    label = "ACTIVITY LEVEL",
                    value = uiState.activityLevel.name
                )
                ResultSummaryCard(
                    icon = Icons.Default.Thermostat,
                    label = "CLIMATE FACTOR",
                    value = "Adjusted for local weather"
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = stringResource(R.string.goal_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 35.dp),
                lineHeight = 26.sp
            )
            
            Spacer(modifier = Modifier.weight(1f))

            OnboardingBottomButton(
                textRes = R.string.lets_go,
                onClick = {
                    viewModel.completeOnboarding()
                    navController.navigate(Destinations.Home) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ResultSummaryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    OnboardingCard(
        isSelected = false,
        onClick = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
