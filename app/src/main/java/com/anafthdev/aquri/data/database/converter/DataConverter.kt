package com.anafthdev.aquri.data.database.converter

import androidx.room.TypeConverter
import com.anafthdev.aquri.data.model.enum.ActivityLevel
import com.anafthdev.aquri.data.model.enum.BadgeCategory
import com.anafthdev.aquri.data.model.enum.Climate
import com.anafthdev.aquri.data.model.enum.DrinkType
import com.anafthdev.aquri.data.model.enum.FrequencyType
import com.anafthdev.aquri.data.model.enum.Gender
import com.anafthdev.aquri.data.model.enum.QuestType
import com.anafthdev.aquri.data.model.enum.Rarity
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Room TypeConverters to handle data types that are not natively supported by SQLite.
 *
 * This includes:
 * - [UUID] conversion to/from String.
 * - [Enum] conversion using name strings.
 * - [List] of Strings conversion to/from JSON strings.
 */
class DataConverter {

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun toUUID(uuid: String?): UUID? = uuid?.let { UUID.fromString(it) }

    @TypeConverter
    fun fromGender(gender: Gender?): String? = gender?.name

    @TypeConverter
    fun toGender(gender: String?): Gender? = gender?.let { Gender.valueOf(it) }

    @TypeConverter
    fun fromActivityLevel(activityLevel: ActivityLevel?): String? = activityLevel?.name

    @TypeConverter
    fun toActivityLevel(activityLevel: String?): ActivityLevel? = activityLevel?.let { ActivityLevel.valueOf(it) }

    @TypeConverter
    fun fromClimate(climate: Climate?): String? = climate?.name

    @TypeConverter
    fun toClimate(climate: String?): Climate? = climate?.let { Climate.valueOf(it) }

    @TypeConverter
    fun fromDrinkType(drinkType: DrinkType?): String? = drinkType?.name

    @TypeConverter
    fun toDrinkType(drinkType: String?): DrinkType? = drinkType?.let { DrinkType.valueOf(it) }

    @TypeConverter
    fun fromFrequencyType(frequencyType: FrequencyType?): String? = frequencyType?.name

    @TypeConverter
    fun toFrequencyType(frequencyType: String?): FrequencyType? = frequencyType?.let { FrequencyType.valueOf(it) }

    @TypeConverter
    fun fromQuestType(questType: QuestType?): String? = questType?.name

    @TypeConverter
    fun toQuestType(questType: String?): QuestType? = questType?.let { QuestType.valueOf(it) }

    @TypeConverter
    fun fromBadgeCategory(badgeCategory: BadgeCategory?): String? = badgeCategory?.name

    @TypeConverter
    fun toBadgeCategory(badgeCategory: String?): BadgeCategory? = badgeCategory?.let { BadgeCategory.valueOf(it) }

    @TypeConverter
    fun fromRarity(rarity: Rarity?): String? = rarity?.name

    @TypeConverter
    fun toRarity(rarity: String?): Rarity? = rarity?.let { Rarity.valueOf(it) }

    @TypeConverter
    fun fromListString(list: List<String>?): String? = list?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toListString(json: String?): List<String>? = json?.let { Json.decodeFromString(it) }
}
