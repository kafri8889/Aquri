package com.anafthdev.aquri.data.database

import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.repository.HydrationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val hydrationRepository: HydrationRepository
) {
    suspend fun initialize() {
        val bottles = hydrationRepository.getAllBottles().first()
        if (bottles.isEmpty()) {
            BottleEntity.predefinedBottles.forEach { hydrationRepository.insertBottle(it) }
        }
    }
}
