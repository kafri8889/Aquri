package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity representing an aggregated summary of hydration for a specific day.
 * Used for generating charts and tracking daily progress history.
 *
 * @property summaryDate Midnight timestamp representing the specific day.
 * @property totalMl Total water consumed on this day.
 * @property goalMl The user's goal at the time of this summary.
 * @property completionPct Percentage of goal reached (totalMl / goalMl).
 */
@Entity(tableName = "daily_summaries")
data class DailySummaryEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "user_id")
    val userId: UUID,
    @ColumnInfo(name = "summary_date")
    val summaryDate: Long, // Midnight timestamp
    @ColumnInfo(name = "total_ml")
    val totalMl: Float = 0f,
    @ColumnInfo(name = "goal_ml")
    val goalMl: Float = 0f,
    @ColumnInfo(name = "completion_pct")
    val completionPct: Float = 0f,
    @ColumnInfo(name = "xp_earned")
    val xpEarned: Int = 0,
    @ColumnInfo(name = "goal_reached")
    val goalReached: Boolean = false
)
