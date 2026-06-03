package com.anafthdev.aquri.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anafthdev.aquri.R
import com.anafthdev.aquri.ui.components.ClayCard
import com.anafthdev.aquri.ui.components.ClayPrimaryButton
import com.anafthdev.aquri.ui.components.clayTint
import com.anafthdev.aquri.ui.navigation.Destinations
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onAuthComplete: (Destinations) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.navigationTarget) {
        state.navigationTarget?.let { destination ->
            onAuthComplete(destination)
            viewModel.consumeNavigation()
        }
    }

    AuthPage(
        title = stringResource(R.string.login_title),
        subtitle = "Sign in for Aquri Pro, backup, and restore. Guest mode stays free.",
        modifier = modifier
    ) {
        GoogleAuthButton(onClick = viewModel::continueWithGoogle)
        AuthDivider()

        AuthTextField(
            value = state.loginEmail,
            onValueChange = viewModel::onLoginEmailChanged,
            label = stringResource(R.string.login_email),
            icon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        AuthTextField(
            value = state.loginPassword,
            onValueChange = viewModel::onLoginPasswordChanged,
            label = stringResource(R.string.login_password),
            icon = Icons.Default.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.login() })
        )

        TextButton(
            onClick = { },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = stringResource(R.string.login_forgot_password),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        AuthMessage(message = state.message)

        ClayPrimaryButton(
            onClick = viewModel::login,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.login_email_button),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black
            )
        }

        AuthInlineLink(
            text = "No account yet?",
            actionText = "Create account",
            actionIcon = Icons.Default.PersonAdd,
            onClick = onNavigateToRegister
        )

        GuestButton(onClick = viewModel::continueAsGuest)
    }
}

@Composable
internal fun AuthPage(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 38.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        AuthHero(
            title = title,
            subtitle = subtitle
        )

        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AquriTheme.clay.radiusExtraLarge),
            containerColor = AquriTheme.clay.surfaceStrong,
            elevation = AquriTheme.clay.floatingElevation
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
                content = content
            )
        }
    }
}

@Composable
private fun AuthHero(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .shadow(
                    elevation = AquriTheme.clay.floatingElevation,
                    shape = RoundedCornerShape(30.dp),
                    ambientColor = AquriTheme.clay.shadow,
                    spotColor = AquriTheme.clay.shadow
                )
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.68f),
                    shape = RoundedCornerShape(30.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AuthBenefitChip(
                text = "Backup",
                modifier = Modifier.weight(1f)
            )
            AuthBenefitChip(
                text = "Restore",
                modifier = Modifier.weight(1f)
            )
            AuthBenefitChip(
                text = "Guest",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AuthBenefitChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(clayTint(MaterialTheme.colorScheme.primaryContainer, 0.62f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.76f),
                shape = CircleShape
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
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
internal fun GoogleAuthButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = AquriTheme.clay.cardElevation,
                shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
                ambientColor = AquriTheme.clay.shadow,
                spotColor = AquriTheme.clay.shadow
            ),
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = AquriTheme.clay.surfaceMuted,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_logo_2025),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.login_google_button),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun AuthDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        )
        Text(
            text = "OR",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
internal fun AuthMessage(message: String?) {
    if (message == null) return

    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AquriTheme.clay.radiusSmall))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.52f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    )
}

@Composable
internal fun AuthInlineLink(
    text: String,
    actionText: String,
    actionIcon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = onClick) {
            Icon(
                imageVector = actionIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = actionText,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
internal fun GuestButton(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(AquriTheme.clay.radiusMedium))
            .background(AquriTheme.clay.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(AquriTheme.clay.radiusMedium)
            )
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.login_guest_button),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Black
        )
    }
}
