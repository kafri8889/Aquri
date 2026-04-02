package com.anafthdev.aquri.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.data.repository.PreferenceRepository
import com.anafthdev.aquri.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Destinations?>(null)
    val startDestination: StateFlow<Destinations?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val isCompleted = preferenceRepository.isOnboardingCompleted.first()
            _startDestination.update { if (isCompleted) Destinations.Home else Destinations.Onboarding1 }
        }
    }
}
