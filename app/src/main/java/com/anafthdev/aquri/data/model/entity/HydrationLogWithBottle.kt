package com.anafthdev.aquri.data.model.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class representing a hydration log entry combined with its associated bottle information.
 * Used for displaying history with bottle icons and names.
 */
data class HydrationLogWithBottle(
    @Embedded val log: HydrationLogEntity,
    @Relation(
        parentColumn = "bottle_id",
        entityColumn = "id"
    )
    val bottle: BottleEntity?
)
