package com.anafthdev.aquri.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class AuthMode {
    Unknown,
    Guest,
    Authenticated
}

data class AuthSession(
    val mode: AuthMode = AuthMode.Unknown
) {
    val isGuest: Boolean = mode == AuthMode.Guest
    val isAuthenticated: Boolean = mode == AuthMode.Authenticated
}

@Singleton
class AuthRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object PreferenceKeys {
        val AUTH_MODE = stringPreferencesKey("auth_mode")
    }

    val session: Flow<AuthSession> = dataStore.data
        .map { preferences ->
            val mode = when (preferences[PreferenceKeys.AUTH_MODE]) {
                AuthMode.Guest.name -> AuthMode.Guest
                AuthMode.Authenticated.name -> AuthMode.Authenticated
                else -> AuthMode.Unknown
            }

            AuthSession(mode)
        }

    suspend fun setGuestMode() {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.AUTH_MODE] = AuthMode.Guest.name
        }
    }

    suspend fun setAuthenticated() {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.AUTH_MODE] = AuthMode.Authenticated.name
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.AUTH_MODE] = AuthMode.Unknown.name
        }
    }
}
