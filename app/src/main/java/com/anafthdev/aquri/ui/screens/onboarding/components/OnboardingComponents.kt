package com.anafthdev.aquri.ui.screens.onboarding.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anafthdev.aquri.R

@Composable
fun OnboardingProgressIndicator(
    step: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(totalSteps) { i ->
                val progress = if (i <= step) 1f else 0f
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "STEP ${step + 1} OF $totalSteps",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = when (step) {
                    0 -> stringResource(R.string.basic_info)
                    1 -> stringResource(R.string.activity_climate)
                    else -> ""
                }.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun OnboardingHeader(
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 45.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(subtitleRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            lineHeight = 24.sp,
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }
}

@Composable
fun OnboardingBottomButton(
    @StringRes textRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(9999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(textRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun OnboardingCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, borderColor, RoundedCornerShape(32.dp))
            .clickable(onClick = onClick)
            .padding(24.dp)
    ) {
        content()
    }
}
