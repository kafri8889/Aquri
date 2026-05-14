package com.anafthdev.aquri.core.mission

import com.anafthdev.aquri.core.mission.model.MissionDefinition
import kotlinx.coroutines.flow.Flow

/**
 * Source of mission definitions.
 *
 * The intended production implementation is a backend-backed source with local
 * caching, but the feature layer only depends on this contract.
 */
interface MissionDefinitionSource {
    fun observeDefinitions(): Flow<List<MissionDefinition>>
    suspend fun refresh()
}

/**
 * Store for user-specific mission state that is mutable independently from the
 * mission definitions, such as claimed timestamps.
 */
interface MissionProgressStore {
    fun observeClaimedMissionState(): Flow<Map<String, Long?>>
    suspend fun markClaimed(missionId: String, claimedAt: Long = System.currentTimeMillis())
}
