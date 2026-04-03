package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity tracking the user's progression, levels, and reward system.
 *
 * @property userId Reference to the [UserEntity].
 * @property totalXp Total experience points accumulated from logs and missions.
 * @property currentLevel Current level number.
 * @property currentStreak Number of consecutive days hydration goals were met.
 * @property shieldCount Number of "Shield" items used to protect streaks during missed days.
 */
@Entity(tableName = "user_gamification")
data class UserGamificationEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "user_id")
    val userId: UUID,
    @ColumnInfo(name = "total_xp")
    val totalXp: Int = 0,
    @ColumnInfo(name = "current_level")
    val currentLevel: Int = 1,
    @ColumnInfo(name = "level_title")
    val levelTitle: String = "Hydro Initiate",
    @ColumnInfo(name = "current_streak")
    val currentStreak: Int = 0,
    @ColumnInfo(name = "highest_streak")
    val highestStreak: Int = 0,
    @ColumnInfo(name = "shield_count")
    val shieldCount: Int = 0,
    @ColumnInfo(name = "last_active_date")
    val lastActiveDate: Long = System.currentTimeMillis()
)
