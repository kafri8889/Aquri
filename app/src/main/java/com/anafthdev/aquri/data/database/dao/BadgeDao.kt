package com.anafthdev.aquri.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.model.entity.UserBadgeEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface BadgeDao {

    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeEntity)

    @Query("SELECT * FROM user_badges WHERE user_id = :userId")
    fun getUserBadges(userId: UUID): Flow<List<UserBadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserBadge(userBadge: UserBadgeEntity)
}
