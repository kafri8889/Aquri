package com.anafthdev.aquri.ui.screens.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anafthdev.aquri.R
import com.anafthdev.aquri.ui.components.ClayPrimaryButton
import com.anafthdev.aquri.ui.navigation.Destinations

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onAuthComplete: (Destinations) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.navigationTarget) {
        state.navigationTarget?.let { destination ->
            onAuthComplete(destination)
            viewModel.consumeNavigation()
        }
    }

    AuthPage(
        title = stringResource(R.string.register_title),
        subtitle = "Create an Aquri account for backup, restore, and future Pro access.",
        modifier = modifier
    ) {
        GoogleAuthButton(onClick = viewModel::continueWithGoogle)
        AuthDivider()

        AuthTextField(
            value = state.registerName,
            onValueChange = viewModel::onRegisterNameChanged,
            label = stringResource(R.string.register_name),
            icon = Icons.Default.Person,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        AuthTextField(
            value = state.registerEmail,
            onValueChange = viewModel::onRegisterEmailChanged,
            label = stringResource(R.string.register_email),
            icon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        AuthTextField(
            value = state.registerPassword,
            onValueChange = viewModel::onRegisterPasswordChanged,
            label = stringResource(R.string.register_password),
            icon = Icons.Default.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )
        AuthTextField(
            value = state.repeatPassword,
            onValueChange = viewModel::onRepeatPasswordChanged,
            label = stringResource(R.string.register_repeat_password),
            icon = Icons.Default.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.register() })
        )

        AuthMessage(message = state.message)

        ClayPrimaryButton(
            onClick = viewModel::register,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.register_button),
                fontWeight = FontWeight.Black
            )
        }

        AuthInlineLink(
            text = "Already registered?",
            actionText = "Back to login",
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onClick = onNavigateBack
        )

        GuestButton(onClick = viewModel::continueAsGuest)
    }
}
