package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_badges")
data class UserBadgeEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "user_id")
    val userId: UUID,
    @ColumnInfo(name = "badge_id")
    val badgeId: UUID,
    @ColumnInfo(name = "earned_at")
    val earnedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean = false
)
