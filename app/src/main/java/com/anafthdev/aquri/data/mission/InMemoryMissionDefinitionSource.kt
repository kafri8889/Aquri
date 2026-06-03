package com.anafthdev.aquri.data.mission

import com.anafthdev.aquri.core.mission.MissionDefinitionSource
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary in-memory mission definition source.
 *
 * This class exists to validate the mission engine contract before a Room or
 * backend-backed implementation is introduced.
 */
@Singleton
class InMemoryMissionDefinitionSource @Inject constructor(
    defaultMissionCatalog: DefaultMissionCatalog
) : MissionDefinitionSource {

    private val definitions = MutableStateFlow(defaultMissionCatalog.missions())

    override fun observeDefinitions(): Flow<List<MissionDefinition>> = definitions

    /**
     * Stub refresh entry point matching the production contract.
     */
    override suspend fun refresh() {
        delay(150)
    }
}
