package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_challenges")
data class UserChallengeEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "user_id")
    val userId: UUID,
    @ColumnInfo(name = "challenge_id")
    val challengeId: UUID,
    @ColumnInfo(name = "joined_at")
    val joinedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "progress_ml")
    val progressMl: Float = 0f,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null
)
