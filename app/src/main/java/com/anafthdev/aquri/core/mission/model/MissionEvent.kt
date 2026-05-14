package com.anafthdev.aquri.core.mission.model

/**
 * Canonical event model for automatic mission detection.
 *
 * The current implementation mainly derives progress from repository state,
 * but this event layer exists so the mission system can evolve into a true
 * event-driven engine for both hydration and non-hydration behaviors.
 */
sealed interface MissionEvent {
    val occurredAt: Long

    /**
     * Emitted when the user logs a drink.
     */
    data class HydrationLogged(
        override val occurredAt: Long,
        val amountMl: Float,
        val drinkTypeId: String? = null
    ) : MissionEvent

    /**
     * Emitted when the app recalculates the daily hydration summary.
     */
    data class DailySummaryUpdated(
        override val occurredAt: Long,
        val totalMl: Float,
        val goalMl: Float
    ) : MissionEvent

    /**
     * Emitted when a recurring period closes and mission rollover/reset logic
     * needs to be evaluated.
     */
    data class PeriodClosed(
        override val occurredAt: Long,
        val period: MissionRecurrence
    ) : MissionEvent

    /**
     * Generic event shape for future behaviors outside hydration.
     */
    data class Generic(
        override val occurredAt: Long,
        val name: String,
        val payload: Map<String, String> = emptyMap()
    ) : MissionEvent
}
