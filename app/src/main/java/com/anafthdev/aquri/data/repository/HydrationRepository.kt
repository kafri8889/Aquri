package com.anafthdev.aquri.data.repository

import com.anafthdev.aquri.data.database.dao.HydrationDao
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling all hydration-related data operations.
 *
 * Manages [HydrationLogEntity] records, [BottleEntity] definitions, 
 * and [DailySummaryEntity] for statistics.
 */
@Singleton
class HydrationRepository @Inject constructor(
    private val hydrationDao: HydrationDao
) {

    // --- Logs ---

    fun getLogsWithBottle(userId: UUID): Flow<List<HydrationLogWithBottle>> = hydrationDao.getLogsWithBottle(userId)

    fun getLogsWithBottleByDate(date: Long): Flow<List<HydrationLogWithBottle>> = hydrationDao.getLogsWithBottleByDate(date)

    suspend fun insertLog(log: HydrationLogEntity) = hydrationDao.insertLog(log)

    suspend fun updateLog(log: HydrationLogEntity) = hydrationDao.updateLog(log)

    suspend fun deleteLog(log: HydrationLogEntity) = hydrationDao.deleteLog(log)

    // --- Bottles ---

    fun getAllBottles(): Flow<List<BottleEntity>> = hydrationDao.getAllBottles()

    suspend fun getBottleById(id: UUID): BottleEntity? = hydrationDao.getBottleById(id)

    suspend fun insertBottle(bottle: BottleEntity) = hydrationDao.insertBottle(bottle)

    suspend fun updateBottle(bottle: BottleEntity) = hydrationDao.updateBottle(bottle)

    suspend fun deleteBottle(bottle: BottleEntity) = hydrationDao.deleteBottle(bottle)

    // --- Drink Types ---

    fun getAllDrinkTypes(): Flow<List<DrinkTypeEntity>> = hydrationDao.getAllDrinkTypes()

    suspend fun getDrinkTypeById(id: UUID): DrinkTypeEntity? = hydrationDao.getDrinkTypeById(id)

    suspend fun insertDrinkType(drinkType: DrinkTypeEntity) = hydrationDao.insertDrinkType(drinkType)

    suspend fun updateDrinkType(drinkType: DrinkTypeEntity) = hydrationDao.updateDrinkType(drinkType)

    suspend fun deleteDrinkType(drinkType: DrinkTypeEntity) = hydrationDao.deleteDrinkType(drinkType)

    // --- Summaries ---

    fun getDailySummaries(userId: UUID): Flow<List<DailySummaryEntity>> = hydrationDao.getDailySummaries(userId)

    fun getDailySummary(date: Long): Flow<DailySummaryEntity?> = hydrationDao.getDailySummary(date)

    suspend fun insertDailySummary(summary: DailySummaryEntity) = hydrationDao.insertDailySummary(summary)
}
