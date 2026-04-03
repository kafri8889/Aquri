package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey
    @ColumnInfo(name = "level_number")
    val levelNumber: Int,
    val title: String,
    @ColumnInfo(name = "xp_required")
    val xpRequired: Int,
    @ColumnInfo(name = "badge_icon")
    val badgeIcon: String
)
