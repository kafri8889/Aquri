package com.anafthdev.aquri.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anafthdev.aquri.data.model.entity.ChallengeEntity
import com.anafthdev.aquri.data.model.entity.QuestEntity
import com.anafthdev.aquri.data.model.entity.UserChallengeEntity
import com.anafthdev.aquri.data.model.entity.UserQuestEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MissionDao {

    @Query("SELECT * FROM quests WHERE is_active = 1")
    fun getActiveQuests(): Flow<List<QuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity)

    @Query("SELECT * FROM user_quests WHERE user_id = :userId")
    fun getUserQuests(userId: UUID): Flow<List<UserQuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserQuest(userQuest: UserQuestEntity)

    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Query("SELECT * FROM user_challenges WHERE user_id = :userId")
    fun getUserChallenges(userId: UUID): Flow<List<UserChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserChallenge(userChallenge: UserChallengeEntity)
}
