package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "created_by")
    val createdBy: UUID?, // Null for system challenges
    val title: String,
    @ColumnInfo(name = "target_volume_ml")
    val targetVolumeMl: Float,
    @ColumnInfo(name = "duration_days")
    val durationDays: Int,
    @ColumnInfo(name = "start_date")
    val startDate: Long,
    @ColumnInfo(name = "end_date")
    val endDate: Long,
    @ColumnInfo(name = "is_personal")
    val isPersonal: Boolean = false,
    @ColumnInfo(name = "is_pro_only")
    val isProOnly: Boolean = false,
    @ColumnInfo(name = "reward_xp")
    val rewardXp: Int,
    @ColumnInfo(name = "reward_badge_id")
    val rewardBadgeId: UUID? = null
)
