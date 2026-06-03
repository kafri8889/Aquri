package com.anafthdev.aquri.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anafthdev.aquri.data.database.converter.DataConverter
import com.anafthdev.aquri.data.database.dao.BadgeDao
import com.anafthdev.aquri.data.database.dao.HydrationDao
import com.anafthdev.aquri.data.database.dao.MissionDao
import com.anafthdev.aquri.data.database.dao.UserDao
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.ChallengeEntity
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogEntity
import com.anafthdev.aquri.data.model.entity.LevelEntity
import com.anafthdev.aquri.data.model.entity.NotificationEntity
import com.anafthdev.aquri.data.model.entity.QuestEntity
import com.anafthdev.aquri.data.model.entity.ReminderSettingsEntity
import com.anafthdev.aquri.data.model.entity.UserBadgeEntity
import com.anafthdev.aquri.data.model.entity.UserChallengeEntity
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import com.anafthdev.aquri.data.model.entity.UserMissionClaimEntity
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
        UserMissionClaimEntity::class,
        BottleEntity::class,
        DrinkTypeEntity::class
    ],
    version = 4,
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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_gamification ADD COLUMN coin_balance INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_mission_claims (
                        id TEXT NOT NULL PRIMARY KEY,
                        user_id TEXT NOT NULL,
                        mission_id TEXT NOT NULL,
                        claimed_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_user_mission_claims_user_id_mission_id ON user_mission_claims(user_id, mission_id)"
                )
            }
        }
    }
}
