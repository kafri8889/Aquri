package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_quests")
data class UserQuestEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "user_id")
    val userId: UUID,
    @ColumnInfo(name = "quest_id")
    val questId: UUID,
    @ColumnInfo(name = "assigned_date")
    val assignedDate: Long,
    @ColumnInfo(name = "progress_value")
    val progressValue: Float = 0f,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null
)
