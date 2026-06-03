package com.anafthdev.aquri.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.repository.AuthRepository
import com.anafthdev.aquri.data.repository.PreferenceRepository
import com.anafthdev.aquri.data.repository.UserRepository
import com.anafthdev.aquri.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferenceRepository: PreferenceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onLoginEmailChanged(value: String) {
        _uiState.update { it.copy(loginEmail = value, message = null) }
    }

    fun onLoginPasswordChanged(value: String) {
        _uiState.update { it.copy(loginPassword = value, message = null) }
    }

    fun onRegisterNameChanged(value: String) {
        _uiState.update { it.copy(registerName = value, message = null) }
    }

    fun onRegisterEmailChanged(value: String) {
        _uiState.update { it.copy(registerEmail = value, message = null) }
    }

    fun onRegisterPasswordChanged(value: String) {
        _uiState.update { it.copy(registerPassword = value, message = null) }
    }

    fun onRepeatPasswordChanged(value: String) {
        _uiState.update { it.copy(repeatPassword = value, message = null) }
    }

    fun login() {
        viewModelScope.launch {
            val state = _uiState.value
            val email = state.loginEmail.trim()
            val password = state.loginPassword

            if (email.isBlank() || password.isBlank()) {
                showMessage("Email and password are required.")
                return@launch
            }

            val user = userRepository.getUser().first()
            val passwordHash = password.sha256()

            if (user?.email.equals(email, ignoreCase = true) && user?.passwordHash == passwordHash) {
                authRepository.setAuthenticated()
                navigateToNextDestination()
            } else {
                showMessage("No matching local account found.")
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            val state = _uiState.value
            val name = state.registerName.trim()
            val email = state.registerEmail.trim()
            val password = state.registerPassword
            val repeatPassword = state.repeatPassword

            when {
                name.isBlank() -> showMessage("Name is required.")
                email.isBlank() -> showMessage("Email is required.")
                password.length < 6 -> showMessage("Password must be at least 6 characters.")
                password != repeatPassword -> showMessage("Repeat password does not match.")
                else -> {
                    val existingUser = userRepository.getUser().first()
                    val now = System.currentTimeMillis()
                    val user = (existingUser ?: UserEntity(createdAt = now)).copy(
                        name = name,
                        email = email,
                        passwordHash = password.sha256(),
                        updatedAt = now
                    )

                    userRepository.insertUser(user)
                    authRepository.setAuthenticated()
                    navigateToNextDestination()
                }
            }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            authRepository.setGuestMode()
            navigateToNextDestination()
        }
    }

    fun continueWithGoogle() {
        showMessage("Google sign-in is not connected yet.")
    }

    fun consumeNavigation() {
        _uiState.update { it.copy(navigationTarget = null) }
    }

    private suspend fun navigateToNextDestination() {
        val isOnboardingCompleted = preferenceRepository.isOnboardingCompleted.first()
        _uiState.update {
            it.copy(
                message = null,
                navigationTarget = if (isOnboardingCompleted) Destinations.Home else Destinations.Onboarding1
            )
        }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }
}

data class AuthUiState(
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val repeatPassword: String = "",
    val message: String? = null,
    val navigationTarget: Destinations? = null
)

private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray())
    return bytes.joinToString(separator = "") { byte -> "%02x".format(byte) }
}
