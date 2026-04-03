package com.anafthdev.aquri.data.repository

import com.anafthdev.aquri.data.database.dao.BadgeDao
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.model.entity.UserBadgeEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeRepository @Inject constructor(
    private val badgeDao: BadgeDao
) {

    fun getAllBadges(): Flow<List<BadgeEntity>> = badgeDao.getAllBadges()

    suspend fun insertBadge(badge: BadgeEntity) = badgeDao.insertBadge(badge)

    fun getUserBadges(userId: UUID): Flow<List<UserBadgeEntity>> = badgeDao.getUserBadges(userId)

    suspend fun insertUserBadge(userBadge: UserBadgeEntity) = badgeDao.insertUserBadge(userBadge)
}
