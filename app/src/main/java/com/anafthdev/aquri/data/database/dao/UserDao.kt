package com.anafthdev.aquri.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anafthdev.aquri.data.model.entity.NotificationEntity
import com.anafthdev.aquri.data.model.entity.ReminderSettingsEntity
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface UserDao {

    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM user_gamification WHERE user_id = :userId")
    fun getGamification(userId: UUID): Flow<UserGamificationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGamification(gamification: UserGamificationEntity)

    @Query("SELECT * FROM reminder_settings WHERE user_id = :userId")
    fun getReminderSettings(userId: UUID): Flow<ReminderSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderSettings(settings: ReminderSettingsEntity)

    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY sent_at DESC")
    fun getNotifications(userId: UUID): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: UUID)
}
