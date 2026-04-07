package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity representing a type of beverage (e.g., Water, Coffee, Tea).
 * Users can create custom drink types with specific colors.
 *
 * @property id Unique identifier for the drink type.
 * @property name Display name of the beverage.
 * @property hexColor Hexadecimal color string for UI representation (e.g., "#006064").
 * @property isCustom True if created by the user, false if predefined.
 */
@Entity(tableName = "drink_types")
data class DrinkTypeEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    @ColumnInfo(name = "hex_color")
    val hexColor: String,
    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = false
) {
    companion object {
        val predefinedDrinkTypes = listOf(
            DrinkTypeEntity(
                id = UUID.fromString("00000000-0000-0000-0001-000000000001"),
                name = "Water",
                hexColor = "#006064"
            ),
            DrinkTypeEntity(
                id = UUID.fromString("00000000-0000-0000-0001-000000000002"),
                name = "Tea",
                hexColor = "#26A69A"
            ),
            DrinkTypeEntity(
                id = UUID.fromString("00000000-0000-0000-0001-000000000003"),
                name = "Coffee",
                hexColor = "#EF6C00"
            ),
            DrinkTypeEntity(
                id = UUID.fromString("00000000-0000-0000-0001-000000000004"),
                name = "Juice",
                hexColor = "#FDD835"
            ),
            DrinkTypeEntity(
                id = UUID.fromString("00000000-0000-0000-0001-000000000005"),
                name = "Soda",
                hexColor = "#E53935"
            ),
            DrinkTypeEntity(
                id = UUID.fromString("00000000-0000-0000-0001-000000000006"),
                name = "Sports Drink",
                hexColor = "#1E88E5"
            ),
            DrinkTypeEntity(
                id = UUID.fromString("00000000-0000-0000-0001-000000000007"),
                name = "Milk",
                hexColor = "#BDBDBD"
            ),
            DrinkTypeEntity(
                id = UUID.fromString("00000000-0000-0000-0001-000000000008"),
                name = "Other",
                hexColor = "#7E57C2"
            )
        )
    }
}
