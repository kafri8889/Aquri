package com.anafthdev.aquri.data.database

import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.repository.HydrationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val hydrationRepository: HydrationRepository
) {
    suspend fun initialize() {
        val currentBottles = hydrationRepository.getAllBottles().first()
        if (currentBottles.isEmpty()) {
            BottleEntity.predefinedBottles.forEach { hydrationRepository.insertBottle(it) }
        } else {
            // Ensure Other bottle exists
            if (currentBottles.none { it.id == BottleEntity.OTHER_BOTTLE_ID }) {
                BottleEntity.predefinedBottles.find { it.id == BottleEntity.OTHER_BOTTLE_ID }?.let {
                    hydrationRepository.insertBottle(it)
                }
            }
        }

        val drinkTypes = hydrationRepository.getAllDrinkTypes().first()
        if (drinkTypes.isEmpty()) {
            DrinkTypeEntity.predefinedDrinkTypes.forEach { hydrationRepository.insertDrinkType(it) }
        }
    }
}
