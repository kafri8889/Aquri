package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "user_mission_claims",
    indices = [
        Index(
            value = ["user_id", "mission_id"],
            unique = true
        )
    ]
)
data class UserMissionClaimEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "user_id")
    val userId: UUID,
    @ColumnInfo(name = "mission_id")
    val missionId: String,
    @ColumnInfo(name = "claimed_at")
    val claimedAt: Long
)
