package com.anafthdev.aquri.data.repository

import com.anafthdev.aquri.data.database.dao.UserDao
import com.anafthdev.aquri.data.model.entity.NotificationEntity
import com.anafthdev.aquri.data.model.entity.ReminderSettingsEntity
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for managing user-related data, including profiles,
 * gamification stats, and communication (notifications).
 *
 * This acts as a clean abstraction over [UserDao].
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    fun getUser(): Flow<UserEntity?> = userDao.getUser()

    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    fun getGamification(userId: UUID): Flow<UserGamificationEntity?> = userDao.getGamification(userId)

    suspend fun insertGamification(gamification: UserGamificationEntity) = userDao.insertGamification(gamification)

    fun getReminderSettings(userId: UUID): Flow<ReminderSettingsEntity?> = userDao.getReminderSettings(userId)

    suspend fun insertReminderSettings(settings: ReminderSettingsEntity) = userDao.insertReminderSettings(settings)

    fun getNotifications(userId: UUID): Flow<List<NotificationEntity>> = userDao.getNotifications(userId)

    suspend fun insertNotification(notification: NotificationEntity) = userDao.insertNotification(notification)

    suspend fun markNotificationAsRead(id: UUID) = userDao.markNotificationAsRead(id)
}
