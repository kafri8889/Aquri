package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anafthdev.aquri.data.model.enum.ActivityLevel
import com.anafthdev.aquri.data.model.enum.Climate
import com.anafthdev.aquri.data.model.enum.Gender
import java.util.UUID

/**
 * Entity representing the user's profile and biological data.
 * This information is used to calculate personalized hydration goals.
 *
 * @property id Unique identifier for the user (UUID).
 * @property name User's display name.
 * @property email User's registered email address.
 * @property passwordHash Hashed password for security.
 * @property gender User's biological gender ([Gender]).
 * @property weightKg User's weight in Kilograms.
 * @property activityLevel User's daily movement intensity ([ActivityLevel]).
 * @property climate Local weather conditions where the user resides ([Climate]).
 * @property dailyGoalMl Calculated ideal hydration target in milliliters.
 * @property isPro Indicates if the user has premium features unlocked.
 * @property createdAt Timestamp when the profile was created.
 * @property updatedAt Timestamp of the last profile update.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val email: String = "",
    @ColumnInfo(name = "password_hash")
    val passwordHash: String = "",
    val gender: Gender = Gender.Male,
    @ColumnInfo(name = "weight_kg")
    val weightKg: Float = 0f,
    @ColumnInfo(name = "activity_level")
    val activityLevel: ActivityLevel = ActivityLevel.Moderate,
    val climate: Climate = Climate.Mild,
    @ColumnInfo(name = "daily_goal_ml")
    val dailyGoalMl: Float = 0f,
    @ColumnInfo(name = "is_pro")
    val isPro: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
