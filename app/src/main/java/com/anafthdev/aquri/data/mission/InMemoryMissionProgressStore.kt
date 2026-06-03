package com.anafthdev.aquri.data.mission

import com.anafthdev.aquri.core.mission.MissionProgressStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary in-memory user mission state store.
 *
 * Only claim state is stored here for now. A real implementation should persist
 * this data locally and synchronize it with the backend.
 */
@Singleton
class InMemoryMissionProgressStore @Inject constructor() : MissionProgressStore {

    private val claimedState = MutableStateFlow<Map<String, Long?>>(emptyMap())

    override fun observeClaimedMissionState(): Flow<Map<String, Long?>> = claimedState

    override suspend fun markClaimed(missionId: String, claimedAt: Long): Boolean {
        if (claimedState.value.containsKey(missionId)) return false
        claimedState.update { current ->
            current + (missionId to claimedAt)
        }
        return true
    }
}
