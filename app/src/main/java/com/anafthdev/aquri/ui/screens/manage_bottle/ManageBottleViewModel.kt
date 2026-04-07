package com.anafthdev.aquri.ui.screens.manage_bottle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.repository.HydrationRepository
import com.anafthdev.aquri.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ManageBottleViewModel @Inject constructor(
    private val hydrationRepository: HydrationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val user: StateFlow<UserEntity?> = userRepository.getUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val bottles: StateFlow<List<BottleEntity>> = hydrationRepository.getAllBottles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val defaultBottles: StateFlow<List<BottleEntity>> = bottles
        .map { list -> list.filter { !it.isCustom } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val customBottles: StateFlow<List<BottleEntity>> = bottles
        .map { list -> list.filter { it.isCustom } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val drinkTypes: StateFlow<List<DrinkTypeEntity>> = hydrationRepository.getAllDrinkTypes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val defaultDrinkTypes: StateFlow<List<DrinkTypeEntity>> = drinkTypes
        .map { list -> list.filter { !it.isCustom } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val customDrinkTypes: StateFlow<List<DrinkTypeEntity>> = drinkTypes
        .map { list -> list.filter { it.isCustom } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun assignBottleToSlot(slotIndex: Int, bottleId: UUID?) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            val updatedUser = when (slotIndex) {
                1 -> currentUser.copy(bottleSlot1 = bottleId)
                2 -> currentUser.copy(bottleSlot2 = bottleId)
                3 -> currentUser.copy(bottleSlot3 = bottleId)
                else -> currentUser
            }
            userRepository.updateUser(updatedUser)
        }
    }

    fun createCustomBottle(name: String, volumeMl: Float, icon: String) {
        viewModelScope.launch {
            hydrationRepository.insertBottle(
                BottleEntity(
                    name = name,
                    volumeMl = volumeMl,
                    icon = icon,
                    isCustom = true
                )
            )
        }
    }

    fun updateCustomBottle(bottle: BottleEntity) {
        viewModelScope.launch {
            hydrationRepository.updateBottle(bottle)
        }
    }

    fun deleteCustomBottle(bottle: BottleEntity) {
        viewModelScope.launch {
            // Check if bottle is assigned to any slot and unassign it
            val currentUser = user.value ?: return@launch
            var updatedUser = currentUser
            var needsUpdate = false

            if (currentUser.bottleSlot1 == bottle.id) {
                updatedUser = updatedUser.copy(bottleSlot1 = null)
                needsUpdate = true
            }
            if (currentUser.bottleSlot2 == bottle.id) {
                updatedUser = updatedUser.copy(bottleSlot2 = null)
                needsUpdate = true
            }
            if (currentUser.bottleSlot3 == bottle.id) {
                updatedUser = updatedUser.copy(bottleSlot3 = null)
                needsUpdate = true
            }

            if (needsUpdate) {
                userRepository.updateUser(updatedUser)
            }

            hydrationRepository.deleteBottle(bottle)
        }
    }

    fun createCustomDrinkType(name: String, hexColor: String) {
        viewModelScope.launch {
            hydrationRepository.insertDrinkType(
                DrinkTypeEntity(
                    name = name,
                    hexColor = hexColor,
                    isCustom = true
                )
            )
        }
    }

    fun updateCustomDrinkType(drinkType: DrinkTypeEntity) {
        viewModelScope.launch {
            hydrationRepository.updateDrinkType(drinkType)
        }
    }

    fun deleteCustomDrinkType(drinkType: DrinkTypeEntity) {
        viewModelScope.launch {
            hydrationRepository.deleteDrinkType(drinkType)
        }
    }
}
