package com.anafthdev.aquri.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anafthdev.aquri.data.database.converter.DataConverter
import com.anafthdev.aquri.data.database.dao.BadgeDao
import com.anafthdev.aquri.data.database.dao.HydrationDao
import com.anafthdev.aquri.data.database.dao.MissionDao
import com.anafthdev.aquri.data.database.dao.UserDao
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.ChallengeEntity
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogEntity
import com.anafthdev.aquri.data.model.entity.LevelEntity
import com.anafthdev.aquri.data.model.entity.NotificationEntity
import com.anafthdev.aquri.data.model.entity.QuestEntity
import com.anafthdev.aquri.data.model.entity.ReminderSettingsEntity
import com.anafthdev.aquri.data.model.entity.UserBadgeEntity
import com.anafthdev.aquri.data.model.entity.UserChallengeEntity
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import com.anafthdev.aquri.data.model.entity.UserQuestEntity

@Database(
    entities = [
        UserEntity::class,
        UserGamificationEntity::class,
        LevelEntity::class,
        ReminderSettingsEntity::class,
        NotificationEntity::class,
        HydrationLogEntity::class,
        DailySummaryEntity::class,
        QuestEntity::class,
        UserQuestEntity::class,
        ChallengeEntity::class,
        UserChallengeEntity::class,
        BadgeEntity::class,
        UserBadgeEntity::class,
        BottleEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class AquriDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun hydrationDao(): HydrationDao
    abstract fun missionDao(): MissionDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        const val DATABASE_NAME = "aquri_db"
    }
}
