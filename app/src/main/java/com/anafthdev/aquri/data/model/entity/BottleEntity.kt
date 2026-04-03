package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity representing a drink container (bottle, glass, etc.) used by the user.
 *
 * @property id Unique identifier for the bottle.
 * @property name Name of the bottle (e.g., "Sports Bottle").
 * @property volumeMl Volume capacity of the bottle in milliliters.
 * @property icon Asset name or path for the bottle's visual representation.
 * @property isCustom True if the bottle was created by the user, false if predefined.
 * @property createdAt Timestamp when the bottle was created.
 */
@Entity(tableName = "bottles")
data class BottleEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    @ColumnInfo(name = "volume_ml")
    val volumeMl: Float,
    val icon: String = "ic_bottle_default",
    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {

    companion object {
        val predefinedBottles = listOf(
            BottleEntity(
                id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
                name = "Small Glass",
                volumeMl = 200f,
                icon = "ic_glass_small"
            ),
            BottleEntity(
                id = UUID.fromString("00000000-0000-0000-0000-000000000002"),
                name = "Medium Glass",
                volumeMl = 330f,
                icon = "ic_glass_medium"
            ),
            BottleEntity(
                id = UUID.fromString("00000000-0000-0000-0000-000000000003"),
                name = "Sports Bottle",
                volumeMl = 500f,
                icon = "ic_bottle_sports"
            ),
            BottleEntity(
                id = UUID.fromString("00000000-0000-0000-0000-000000000004"),
                name = "Large Bottle",
                volumeMl = 750f,
                icon = "ic_bottle_large"
            )
        )
    }
}
