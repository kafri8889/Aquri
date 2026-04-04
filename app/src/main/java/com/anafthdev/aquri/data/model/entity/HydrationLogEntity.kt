package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anafthdev.aquri.data.model.enum.DrinkType
import java.util.UUID

/**
 * Entity representing a single drink entry logged by the user.
 *
 * @property id Unique identifier for this log entry.
 * @property userId Reference to the [UserEntity] who created this log.
 * @property bottleId Reference to the [BottleEntity] used, if any. Null for manual entries.
 * @property amountMl Amount of water/liquid consumed in milliliters.
 * @property drinkType The category of drink consumed ([DrinkType]).
 * @property loggedAt Exact timestamp when the entry was created.
 * @property logDate Simplified date timestamp (midnight) for easier grouping/queries by day.
 */
@Entity(tableName = "hydration_logs")
data class HydrationLogEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "user_id")
    val userId: UUID,
    @ColumnInfo(name = "bottle_id")
    val bottleId: UUID? = null,
    @ColumnInfo(name = "amount_ml")
    val amountMl: Float,
    @ColumnInfo(name = "bottle_name")
    val bottleName: String? = null,
    @ColumnInfo(name = "drink_type")
    val drinkType: DrinkType = DrinkType.Water,
    @ColumnInfo(name = "logged_at")
    val loggedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "log_date")
    val logDate: Long // Should represent only the date (e.g., midnight timestamp)
)
