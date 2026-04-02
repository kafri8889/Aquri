package com.anafthdev.aquri.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val DAILY_GOAL = floatPreferencesKey("daily_goal")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    val dailyGoal: Flow<Float> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_GOAL] ?: 0f
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setDailyGoal(goal: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_GOAL] = goal
        }
    }
}
