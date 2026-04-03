package com.anafthdev.aquri.data.repository

import com.anafthdev.aquri.data.database.dao.MissionDao
import com.anafthdev.aquri.data.model.entity.ChallengeEntity
import com.anafthdev.aquri.data.model.entity.QuestEntity
import com.anafthdev.aquri.data.model.entity.UserChallengeEntity
import com.anafthdev.aquri.data.model.entity.UserQuestEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionRepository @Inject constructor(
    private val missionDao: MissionDao
) {

    fun getActiveQuests(): Flow<List<QuestEntity>> = missionDao.getActiveQuests()

    suspend fun insertQuest(quest: QuestEntity) = missionDao.insertQuest(quest)

    fun getUserQuests(userId: UUID): Flow<List<UserQuestEntity>> = missionDao.getUserQuests(userId)

    suspend fun insertUserQuest(userQuest: UserQuestEntity) = missionDao.insertUserQuest(userQuest)

    fun getAllChallenges(): Flow<List<ChallengeEntity>> = missionDao.getAllChallenges()

    suspend fun insertChallenge(challenge: ChallengeEntity) = missionDao.insertChallenge(challenge)

    fun getUserChallenges(userId: UUID): Flow<List<UserChallengeEntity>> = missionDao.getUserChallenges(userId)

    suspend fun insertUserChallenge(userChallenge: UserChallengeEntity) = missionDao.insertUserChallenge(userChallenge)
}
