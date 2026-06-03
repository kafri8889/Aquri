package com.anafthdev.aquri.data.mission

import com.anafthdev.aquri.core.mission.MissionProgressStore
import com.anafthdev.aquri.data.database.dao.MissionDao
import com.anafthdev.aquri.data.model.entity.UserMissionClaimEntity
import com.anafthdev.aquri.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class RoomMissionProgressStore @Inject constructor(
    private val missionDao: MissionDao,
    private val userRepository: UserRepository
) : MissionProgressStore {

    override fun observeClaimedMissionState(): Flow<Map<String, Long?>> {
        return userRepository.getUser()
            .flatMapLatest { user ->
                if (user == null) {
                    emptyFlow()
                } else {
                    missionDao.getUserMissionClaims(user.id)
                }
            }
            .map { claims ->
                claims.associate { it.missionId to it.claimedAt }
            }
    }

    override suspend fun markClaimed(missionId: String, claimedAt: Long): Boolean {
        val user = userRepository.getUser().filterNotNull().firstOrNull() ?: return false
        if (missionDao.getUserMissionClaim(user.id, missionId) != null) return false

        val insertedId = missionDao.insertUserMissionClaim(
            UserMissionClaimEntity(
                userId = user.id,
                missionId = missionId,
                claimedAt = claimedAt
            )
        )
        return insertedId != -1L
    }
}
