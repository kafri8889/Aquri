package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anafthdev.aquri.data.model.enum.QuestType
import java.util.UUID

/**
 * Entity defining a hydration mission or task available to users.
 *
 * @property title Name of the quest.
 * @property description Detailed instruction for the quest.
 * @property questType Category of the quest ([QuestType]).
 * @property targetValue Numeric goal to achieve (e.g., milliliters or consecutive days).
 * @property xpReward Experience points awarded upon completion.
 */
@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    @ColumnInfo(name = "quest_type")
    val questType: QuestType,
    @ColumnInfo(name = "target_value")
    val targetValue: Float,
    @ColumnInfo(name = "xp_reward")
    val xpReward: Int,
    @ColumnInfo(name = "is_pro_only")
    val isProOnly: Boolean = false,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
