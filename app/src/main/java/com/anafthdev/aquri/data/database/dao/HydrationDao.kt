package com.anafthdev.aquri.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface HydrationDao {

    // --- Logs ---

    @Transaction
    @Query("SELECT * FROM hydration_logs WHERE user_id = :userId ORDER BY logged_at DESC")
    fun getLogsWithBottle(userId: UUID): Flow<List<HydrationLogWithBottle>>

    @Transaction
    @Query("SELECT * FROM hydration_logs WHERE log_date = :date")
    fun getLogsWithBottleByDate(date: Long): Flow<List<HydrationLogWithBottle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HydrationLogEntity)

    @Update
    suspend fun updateLog(log: HydrationLogEntity)

    @Delete
    suspend fun deleteLog(log: HydrationLogEntity)

    // --- Bottles ---

    @Query("SELECT * FROM bottles ORDER BY volume_ml ASC")
    fun getAllBottles(): Flow<List<BottleEntity>>

    @Query("SELECT * FROM bottles WHERE id = :id")
    suspend fun getBottleById(id: UUID): BottleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBottle(bottle: BottleEntity)

    @Update
    suspend fun updateBottle(bottle: BottleEntity)

    @Delete
    suspend fun deleteBottle(bottle: BottleEntity)

    // --- Drink Types ---

    @Query("SELECT * FROM drink_types ORDER BY name ASC")
    fun getAllDrinkTypes(): Flow<List<DrinkTypeEntity>>

    @Query("SELECT * FROM drink_types WHERE id = :id")
    suspend fun getDrinkTypeById(id: UUID): DrinkTypeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrinkType(drinkType: DrinkTypeEntity)

    @Update
    suspend fun updateDrinkType(drinkType: DrinkTypeEntity)

    @Delete
    suspend fun deleteDrinkType(drinkType: DrinkTypeEntity)

    // --- Summaries ---

    @Query("SELECT * FROM daily_summaries WHERE user_id = :userId ORDER BY summary_date DESC")
    fun getDailySummaries(userId: UUID): Flow<List<DailySummaryEntity>>

    @Query("SELECT * FROM daily_summaries WHERE summary_date = :date")
    fun getDailySummary(date: Long): Flow<DailySummaryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySummary(summary: DailySummaryEntity)
}
