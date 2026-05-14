package com.anafthdev.aquri.core.mission.engine

import com.anafthdev.aquri.core.mission.evaluator.MissionEvaluator
import com.anafthdev.aquri.core.mission.model.MissionCardModel
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core orchestrator that delegates mission evaluation to the first evaluator
 * capable of handling a mission definition's trigger type.
 */
@Singleton
class MissionEngine @Inject constructor(
    private val evaluators: Set<@JvmSuppressWildcards MissionEvaluator>
) {

    /**
     * Evaluates a batch of active mission definitions against the current user state.
     *
     * Missions without a registered evaluator are skipped intentionally so the
     * system can tolerate partially rolled-out trigger types.
     */
    fun evaluate(
        definitions: List<MissionDefinition>,
        context: MissionEvaluationContext,
        claimedMissionState: Map<String, Long?>
    ): List<MissionCardModel> {
        return definitions
            .filter { it.isActive }
            .mapNotNull { definition ->
                val evaluator = evaluators.firstOrNull { it.supports(definition) } ?: return@mapNotNull null
                evaluator.evaluate(
                    definition = definition,
                    context = context,
                    claimedAt = claimedMissionState[definition.id]
                )
            }
    }
}
