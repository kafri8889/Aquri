package com.anafthdev.aquri.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun clayTint(color: Color, alpha: Float): Color {
    return color.copy(alpha = alpha).compositeOver(MaterialTheme.colorScheme.background)
}

@Composable
fun ClaySurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(AquriTheme.clay.radiusLarge),
    containerColor: Color = AquriTheme.clay.surface,
    borderColor: Color = AquriTheme.clay.highlight,
    elevation: Dp = AquriTheme.clay.cardElevation,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = AquriTheme.clay.shadow,
                spotColor = AquriTheme.clay.shadow
            )
            .clip(shape)
            .background(containerColor.compositeOver(MaterialTheme.colorScheme.background))
            .border(
                width = AquriTheme.clay.borderWidth,
                color = borderColor.copy(alpha = AquriTheme.clay.borderAlpha),
                shape = shape
            )
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(AquriTheme.clay.radiusLarge),
    containerColor: Color = AquriTheme.clay.surface,
    borderColor: Color = AquriTheme.clay.highlight,
    elevation: Dp = AquriTheme.clay.cardElevation,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier.shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = AquriTheme.clay.shadow,
        spotColor = AquriTheme.clay.shadow
    )

    if (onClick == null) {
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor.compositeOver(MaterialTheme.colorScheme.background)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = cardModifier.border(
                width = AquriTheme.clay.borderWidth,
                color = borderColor.copy(alpha = AquriTheme.clay.borderAlpha),
                shape = shape
            ),
            content = content
        )
    } else {
        Card(
            onClick = onClick,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor.compositeOver(MaterialTheme.colorScheme.background)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = cardModifier.border(
                width = AquriTheme.clay.borderWidth,
                color = borderColor.copy(alpha = AquriTheme.clay.borderAlpha),
                shape = shape
            ),
            content = content
        )
    }
}

@Composable
fun ClaySelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(AquriTheme.clay.radiusLarge),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val clay = AquriTheme.clay
    val source = remember { MutableInteractionSource() }

    ClaySurface(
        modifier = modifier.clickable(
            enabled = enabled,
            role = Role.Button,
            interactionSource = source,
            indication = null,
            onClick = onClick
        ),
        shape = shape,
        containerColor = if (selected) clayTint(MaterialTheme.colorScheme.primaryContainer, 0.72f) else clay.surface,
        borderColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else clay.highlight,
        elevation = if (selected) clay.floatingElevation else clay.cardElevation,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun ClayPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = contentPadding,
        modifier = modifier.shadow(
            elevation = if (enabled) AquriTheme.clay.cardElevation else 0.dp,
            shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
            ambientColor = AquriTheme.clay.shadow,
            spotColor = AquriTheme.clay.shadow
        ),
        content = content
    )
}

@Composable
fun ClayIconButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = AquriTheme.clay.surfaceStrong
) {
    ClaySurface(
        modifier = modifier
            .defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        shape = CircleShape,
        containerColor = if (enabled) containerColor else MaterialTheme.colorScheme.surfaceVariant,
        elevation = if (enabled) AquriTheme.clay.cardElevation else 0.dp,
        contentPadding = PaddingValues(10.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = if (enabled) tint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ClayChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = if (selected) AquriTheme.clay.cardElevation else AquriTheme.clay.pressedElevation,
                shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
                ambientColor = AquriTheme.clay.shadow,
                spotColor = AquriTheme.clay.shadow
            )
            .clip(RoundedCornerShape(AquriTheme.clay.radiusMedium))
            .background(if (selected) clayTint(MaterialTheme.colorScheme.primaryContainer, 0.82f) else AquriTheme.clay.surface)
            .border(
                BorderStroke(
                    AquriTheme.clay.borderWidth,
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else AquriTheme.clay.highlight
                ),
                RoundedCornerShape(AquriTheme.clay.radiusMedium)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AssetImage(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember(assetPath) {
        try {
            context.assets.open(assetPath).use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}
