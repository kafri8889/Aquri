package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anafthdev.aquri.data.model.enum.BadgeCategory
import com.anafthdev.aquri.data.model.enum.Rarity
import java.util.UUID

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    @ColumnInfo(name = "icon_url")
    val iconUrl: String,
    val category: BadgeCategory,
    val rarity: Rarity,
    @ColumnInfo(name = "is_pro_only")
    val isProOnly: Boolean = false
)
