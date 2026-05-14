# Mission System Guide

## Overview

The mission system is a reusable subsystem for Aquri that supports:

- server-driven mission definitions
- automatic progress detection from app data
- mission grouping by recurrence
- reusable evaluator-based rule logic
- a thin feature layer that only renders dashboard state

Current mission recurrence support in the engine:

- `Daily`
- `Weekly`
- `OneTime`

The enum still contains `Monthly` and `Event` for future expansion, but the current product target is the 3 types above.

## Goals

This system is designed so that:

1. The backend can add, edit, disable, or remove missions without changing UI logic.
2. The app can detect mission progress automatically from user behavior.
3. Mission rules stay isolated in dedicated evaluators.
4. The Mission screen only consumes precomputed dashboard data.
5. Future non-hydration missions can plug into the same engine.

## Folder Structure

Core engine:

- `app/src/main/java/com/anafthdev/aquri/core/mission`

Current temporary adapters:

- `app/src/main/java/com/anafthdev/aquri/data/mission`

Feature consumer:

- `app/src/main/java/com/anafthdev/aquri/ui/screens/mission`

Important files:

- `core/mission/model/MissionDefinition.kt`
- `core/mission/model/MissionModels.kt`
- `core/mission/model/MissionEvent.kt`
- `core/mission/engine/MissionEvaluationContext.kt`
- `core/mission/engine/MissionEngine.kt`
- `core/mission/evaluator/MissionEvaluator.kt`
- `core/mission/evaluator/HydrationMissionEvaluators.kt`
- `core/mission/MissionContracts.kt`
- `core/mission/MissionService.kt`
- `data/mission/InMemoryMissionDefinitionSource.kt`
- `data/mission/InMemoryMissionProgressStore.kt`

## High-Level Architecture

The system is split into 5 layers.

### 1. Mission Definition Layer

This layer describes what a mission is.

Main model:

```kotlin
data class MissionDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: MissionCategory,
    val recurrence: MissionRecurrence,
    val reward: MissionReward,
    val trigger: MissionTrigger,
    val isActive: Boolean = true
)
```

This model should map cleanly from backend payloads.

Definition data is immutable from the app's point of view.

### Reward Structure

Mission rewards are defined by `MissionReward`.

```kotlin
data class MissionReward(
    val xp: Int = 0,
    val coins: Int = 0
)
```

Supported combinations:

- XP only
- coins only
- XP and coins together

### 2. Mission Progress Layer

This layer stores user-specific mission state.

Main model:

```kotlin
data class MissionProgress(
    val missionId: String,
    val progress: Float,
    val status: MissionStatus,
    val claimedAt: Long? = null
)
```

Notes:

- `progress` is normalized to `0f..1f`
- `status` is derived as `Active`, `Completed`, or `Claimed`
- current implementation only persists claim timestamps in memory

### 3. Evaluator Layer

Each mission rule family has its own evaluator.

Contract:

```kotlin
interface MissionEvaluator {
    fun supports(definition: MissionDefinition): Boolean

    fun evaluate(
        definition: MissionDefinition,
        context: MissionEvaluationContext,
        claimedAt: Long?
    ): MissionCardModel
}
```

This keeps rule logic isolated and scalable.

### 4. Engine Layer

`MissionEngine` receives:

- active mission definitions
- evaluation context
- claimed mission state

Then it:

1. finds a matching evaluator for each mission
2. calculates progress
3. derives status
4. returns `MissionCardModel`

### 5. Service Layer

`MissionService` is the app-facing facade.

It:

- observes repositories
- creates evaluation context
- filters claim state by recurrence period
- calls `MissionEngine`
- groups output into `MissionDashboard`

The UI should depend on this service, not on evaluators or repository internals.

## Supported Mission Types

## Daily Missions

Daily missions reset logically every day because:

- progress is derived from today's data only
- claim state is only considered active if it was claimed on the same day

Examples:

- drink between `18:00-21:00`
- log `6` hydration entries
- reach `80%` of daily goal

## Weekly Missions

Weekly missions reset logically every week because:

- progress is derived from current-week summaries only
- claim state is only considered active for the current week

Example:

- hit daily goal `5` days this week

## One-Time Missions

One-time missions never reset automatically.

Behavior:

- progress can become `Completed`
- once claimed, status remains `Claimed`
- claim state is treated as permanently active

Example:

- first evening hydration

## Current Trigger Types

Triggers define the mission rule payload.

### `HydrationTimeWindow`

```kotlin
MissionTrigger.HydrationTimeWindow(
    startHourInclusive = 18,
    endHourExclusive = 21,
    requiredLogs = 1
)
```

Meaning:

- count today's hydration logs whose local time is inside the given window
- progress = `matchingLogs / requiredLogs`

Used for:

- daily evening missions
- one-time first evening hydration

### `HydrationLogCount`

```kotlin
MissionTrigger.HydrationLogCount(requiredLogs = 6)
```

Meaning:

- count all hydration logs for the current day
- progress = `todayLogs.size / requiredLogs`

### `DailyGoalPercentage`

```kotlin
MissionTrigger.DailyGoalPercentage(targetPercentage = 0.8f)
```

Meaning:

- compare `todaySummary.totalMl` against `user.dailyGoalMl * targetPercentage`

### `WeeklyGoalDays`

```kotlin
MissionTrigger.WeeklyGoalDays(requiredDays = 5)
```

Meaning:

- count how many current-week summaries have `goalReached == true`

### `GenericCounter`

Reserved for future non-hydration event streams.

Current status:

- trigger exists
- evaluator exists
- actual event aggregation is not implemented yet

## Automatic Progress Detection Flow

This is the current automatic mission flow.

1. User logs hydration from `HomeViewModel`.
2. `HydrationRepository` inserts `HydrationLogEntity`.
3. `HydrationRepository` or the calling flow updates `DailySummaryEntity`.
4. `MissionService.observeDashboard()` receives new repository emissions.
5. `MissionService` builds a fresh `MissionEvaluationContext`.
6. `MissionEngine` re-evaluates all active mission definitions.
7. `MissionViewModel` receives the new dashboard.
8. `MissionScreen` re-renders automatically.

This is why a mission like `Drink between 18:00-21:00` can progress without any mission button interaction.

## Detailed Data Processing Flow

This section explains how a single mission moves through the system with concrete example data.

We use this sample mission:

```kotlin
MissionDefinition(
    id = "daily_evening_window",
    title = "Drink between 18:00-21:00",
    description = "Any hydration log inside the evening window counts.",
    category = MissionCategory.Hydration,
    recurrence = MissionRecurrence.Daily,
    reward = MissionReward(xp = 20, coins = 5),
    trigger = MissionTrigger.HydrationTimeWindow(
        startHourInclusive = 18,
        endHourExclusive = 21,
        requiredLogs = 1
    )
)
```

Now assume the user logs one drink at `19:12`.

### Step 1. MissionDefinitionSource emits mission rules

At this stage, the system only knows mission configuration. There is no user progress yet.

Example output from `MissionDefinitionSource.observeDefinitions()`:

```kotlin
listOf(
    MissionDefinition(
        id = "daily_evening_window",
        title = "Drink between 18:00-21:00",
        description = "Any hydration log inside the evening window counts.",
        category = MissionCategory.Hydration,
        recurrence = MissionRecurrence.Daily,
        reward = MissionReward(xp = 20, coins = 5),
        trigger = MissionTrigger.HydrationTimeWindow(
            startHourInclusive = 18,
            endHourExclusive = 21,
            requiredLogs = 1
        )
    )
)
```

What this module contributes:

- static mission metadata
- reward payload
- recurrence type
- trigger rule definition

### Step 2. Repository layer emits user and activity data

The mission system also consumes user state and activity state from repositories.

Example `UserRepository.getUser()` output:

```kotlin
UserEntity(
    id = user-123,
    dailyGoalMl = 2500f,
    ...
)
```

Example `HydrationRepository.getLogsWithBottle(user.id)` output:

```kotlin
listOf(
    HydrationLogWithBottle(
        log = HydrationLogEntity(
            userId = user-123,
            amountMl = 350f,
            loggedAt = 1747224720000, // 19:12
            logDate = 1747184400000   // same day midnight
        ),
        bottle = ...
    )
)
```

Example `HydrationRepository.getDailySummaries(user.id)` output:

```kotlin
listOf(
    DailySummaryEntity(
        userId = user-123,
        summaryDate = 1747184400000,
        totalMl = 350f,
        goalMl = 2500f,
        completionPct = 0.14f,
        goalReached = false
    )
)
```

Example `MissionProgressStore.observeClaimedMissionState()` output before claim:

```kotlin
emptyMap<String, Long?>()
```

What these modules contribute:

- user profile and daily goal
- today hydration logs
- today and weekly summary state
- persisted claim state

### Step 3. MissionService combines all streams

`MissionService.observeDashboard()` is the central integration point.

It combines:

- mission definitions
- claimed mission state
- hydration logs
- daily summaries
- user gamification

At this stage, `MissionService` also applies recurrence-aware claim filtering.

For example:

- a `Daily` claim only remains active for the same day
- a `Weekly` claim only remains active for the same week
- a `OneTime` claim remains active permanently

The result is an `activeClaimState` map passed into evaluation.

### Step 4. MissionService builds MissionEvaluationContext

The raw repository data is converted into a normalized snapshot for evaluators.

Example context:

```kotlin
MissionEvaluationContext(
    user = UserEntity(
        id = user-123,
        dailyGoalMl = 2500f,
        ...
    ),
    now = 1747224720000,
    locale = Locale("id", "ID"),
    todayLogs = listOf(
        HydrationLogWithBottle(
            log = HydrationLogEntity(
                amountMl = 350f,
                loggedAt = 1747224720000
            ),
            bottle = ...
        )
    ),
    todaySummary = DailySummaryEntity(
        totalMl = 350f,
        goalMl = 2500f,
        goalReached = false
    ),
    weekSummaries = listOf(
        DailySummaryEntity(
            totalMl = 350f,
            goalMl = 2500f,
            goalReached = false
        )
    ),
    lastEvent = null
)
```

Why this exists:

- evaluators do not need repository access
- all evaluators receive a uniform input shape
- unit tests can construct this object directly

### Step 5. MissionEngine routes each mission to a matching evaluator

`MissionEngine.evaluate(...)` receives:

```kotlin
definitions = listOf(dailyEveningWindowMission)
context = MissionEvaluationContext(...)
claimedMissionState = emptyMap()
```

Then for each mission:

1. skip inactive missions
2. find the first evaluator whose `supports(definition)` returns `true`
3. call `evaluate(definition, context, claimedAt)`

For the example mission:

- trigger type is `MissionTrigger.HydrationTimeWindow`
- selected evaluator is `HydrationTimeWindowMissionEvaluator`

### Step 6. Evaluator calculates raw progress

The selected evaluator extracts the trigger payload:

```kotlin
MissionTrigger.HydrationTimeWindow(
    startHourInclusive = 18,
    endHourExclusive = 21,
    requiredLogs = 1
)
```

It checks `context.todayLogs` and counts logs inside the local-time window.

Example calculation:

```kotlin
matchingLogs = 1
progress = matchingLogs / requiredLogs.toFloat()
progress = 1 / 1f = 1.0f
```

If there were no matching logs:

```kotlin
matchingLogs = 0
progress = 0 / 1f = 0f
```

### Step 7. BaseMissionEvaluator converts progress into mission state

The shared base evaluator normalizes progress and derives status:

- `Claimed` if `claimedAt != null`
- `Completed` if `progress >= 1f`
- `Active` otherwise

Example result before claim:

```kotlin
MissionCardModel(
    definition = MissionDefinition(
        id = "daily_evening_window",
        reward = MissionReward(xp = 20, coins = 5),
        ...
    ),
    progress = MissionProgress(
        missionId = "daily_evening_window",
        progress = 1.0f,
        status = MissionStatus.Completed,
        claimedAt = null
    )
)
```

Example result if the user has not logged in the correct time window:

```kotlin
MissionCardModel(
    definition = MissionDefinition(...),
    progress = MissionProgress(
        missionId = "daily_evening_window",
        progress = 0f,
        status = MissionStatus.Active,
        claimedAt = null
    )
)
```

### Step 8. MissionService groups evaluated results into MissionDashboard

After all missions are evaluated, `MissionService` groups them by recurrence and adds gamification header data.

Example output:

```kotlin
MissionDashboard(
    level = 7,
    levelTitle = "Water Warrior",
    totalXp = 11200,
    currentLevelXp = 10400,
    nextLevelXp = 12800,
    currentStreak = 5,
    shieldCount = 1,
    dailyMissions = listOf(
        MissionCardModel(
            definition = MissionDefinition(
                id = "daily_evening_window",
                title = "Drink between 18:00-21:00",
                reward = MissionReward(xp = 20, coins = 5),
                ...
            ),
            progress = MissionProgress(
                missionId = "daily_evening_window",
                progress = 1.0f,
                status = MissionStatus.Completed,
                claimedAt = null
            )
        )
    ),
    weeklyMissions = emptyList(),
    oneTimeMissions = emptyList(),
    challengePreview = ChallengePreview(...)
)
```

This is the first shape that is truly feature-ready.

### Step 9. MissionViewModel converts dashboard to UI state

`MissionViewModel` observes `MissionService.observeDashboard()` and copies the dashboard fields into `MissionUiState`.

Example `MissionUiState`:

```kotlin
MissionUiState(
    isLoading = false,
    isRefreshing = false,
    errorMessage = null,
    level = 7,
    levelTitle = "Water Warrior",
    totalXp = 11200,
    currentLevelXp = 10400,
    nextLevelXp = 12800,
    streakCount = 5,
    shieldCount = 1,
    dailyMissions = listOf(
        MissionCardModel(
            definition = MissionDefinition(
                id = "daily_evening_window",
                reward = MissionReward(xp = 20, coins = 5),
                ...
            ),
            progress = MissionProgress(
                missionId = "daily_evening_window",
                progress = 1.0f,
                status = MissionStatus.Completed,
                claimedAt = null
            )
        )
    ),
    weeklyMissions = emptyList(),
    oneTimeMissions = emptyList(),
    challengePreview = ChallengePreview(...)
)
```

The ViewModel does not contain mission rule logic. It only exposes screen-ready state.

### Step 10. MissionScreen renders the UI

`MissionScreen` reads `MissionUiState` and renders:

- XP card
- streak/shield card
- daily missions section
- weekly missions section
- one-time missions section

For the example mission card, the rendered data is roughly:

- title: `Drink between 18:00-21:00`
- progress bar: `1.0`
- reward label: `+20 XP • +5 Coins`
- action button: `Claim`

### Step 11. Claim flow after completion

When the user taps `Claim`:

1. `MissionScreen` calls `MissionViewModel.onMissionActionClick(mission)`
2. `MissionViewModel` calls `MissionService.claimReward(mission.definition.id)`
3. `MissionService` delegates to `MissionProgressStore.markClaimed(...)`
4. claim state flow emits again
5. `MissionService.observeDashboard()` recomputes the dashboard
6. the mission status becomes `Claimed`

Example claim state after claim:

```kotlin
mapOf(
    "daily_evening_window" to 1747225000000
)
```

Example mission after recomputation:

```kotlin
MissionProgress(
    missionId = "daily_evening_window",
    progress = 1.0f,
    status = MissionStatus.Claimed,
    claimedAt = 1747225000000
)
```

Rendered UI result:

- reward label still visible
- action button changes to `Claimed`
- button becomes disabled

### Step 12. Next day reset behavior for Daily missions

Because the mission is `Daily`, yesterday's claim does not stay active forever.

Next day:

- today logs are different
- today summary is different
- `MissionService` filters out yesterday's claim for daily recurrence
- the same mission can appear again as `Active` or `Completed` depending on today's behavior

This is how daily and weekly missions can recur without manually deleting old definitions.

## Module-by-Module Responsibility Summary

- `MissionDefinitionSource`
  Provides mission rules and reward payloads.
- `HydrationRepository` / `UserRepository`
  Provide the user activity and user profile state used for evaluation.
- `MissionProgressStore`
  Stores mutable user-side mission state, currently claim timestamps.
- `MissionService`
  Combines all streams, builds context, filters recurrence-aware claim state, and produces the dashboard.
- `MissionEngine`
  Routes each mission definition to the correct evaluator.
- `MissionEvaluator`
  Calculates mission progress for a specific trigger family.
- `MissionViewModel`
  Exposes dashboard data as screen state.
- `MissionScreen`
  Renders the state and forwards user actions such as claim.

## Evaluation Context

Evaluators do not call repositories directly. They receive a snapshot.

```kotlin
data class MissionEvaluationContext(
    val user: UserEntity,
    val now: Long,
    val locale: Locale,
    val todayLogs: List<HydrationLogWithBottle>,
    val todaySummary: DailySummaryEntity?,
    val weekSummaries: List<DailySummaryEntity>,
    val lastEvent: MissionEvent? = null
)
```

Why this matters:

- evaluators stay deterministic
- unit testing gets easier
- business rules stay independent from storage

## Current Dashboard Model

The feature consumes:

```kotlin
data class MissionDashboard(
    val level: Int,
    val levelTitle: String,
    val totalXp: Int,
    val currentLevelXp: Int,
    val nextLevelXp: Int,
    val currentStreak: Int,
    val shieldCount: Int,
    val dailyMissions: List<MissionCardModel>,
    val weeklyMissions: List<MissionCardModel>,
    val oneTimeMissions: List<MissionCardModel>,
    val challengePreview: ChallengePreview?
)
```

This is already grouped by recurrence so the screen does not need filtering logic.

## Claim State and Reset Logic

This is important.

Claim state is stored separately from definitions. The service decides whether a stored claim is still active for the current period.

Current behavior:

- `Daily`: claim only applies on the same day
- `Weekly`: claim only applies in the same week
- `OneTime`: claim always applies permanently
- `Monthly`: claim applies in the same month
- `Event`: currently treated as always active

This means a daily mission that was claimed yesterday will not stay claimed today.

## Current Temporary Implementations

### `InMemoryMissionDefinitionSource`

Purpose:

- validates the architecture before backend integration
- provides sample missions for local development

Current sample missions:

- daily evening window
- daily log count
- daily goal 80%
- weekly goal days
- one-time first evening hydration

Those sample missions now include XP and coin rewards.

### `InMemoryMissionProgressStore`

Purpose:

- stores claim timestamps behind the `MissionProgressStore` contract

Limitations:

- in-memory only
- app restart resets state
- not synced to backend

## Contracts

### `MissionDefinitionSource`

```kotlin
interface MissionDefinitionSource {
    fun observeDefinitions(): Flow<List<MissionDefinition>>
    suspend fun refresh()
}
```

Intended production implementation:

- fetch definitions from backend
- cache them locally
- emit the cached + refreshed mission list

### `MissionProgressStore`

```kotlin
interface MissionProgressStore {
    fun observeClaimedMissionState(): Flow<Map<String, Long?>>
    suspend fun markClaimed(
        missionId: String,
        claimedAt: Long = System.currentTimeMillis()
    )
}
```

Intended production implementation:

- persist claim/progress locally
- sync to backend
- support period-aware reset metadata

## Tutorial: Add a New Mission Definition

If the rule can be expressed by an existing trigger, you only need a new `MissionDefinition`.

Example daily mission:

```kotlin
MissionDefinition(
    id = "daily_morning_window",
    title = "Drink before 09:00",
    description = "Start your morning early.",
    category = MissionCategory.Hydration,
    recurrence = MissionRecurrence.Daily,
    reward = MissionReward(xp = 10, coins = 3),
    trigger = MissionTrigger.HydrationTimeWindow(
        startHourInclusive = 6,
        endHourExclusive = 9,
        requiredLogs = 1
    )
)
```

Example one-time mission:

```kotlin
MissionDefinition(
    id = "onetime_first_goal_push",
    title = "First 80% push",
    description = "Reach 80% of your goal for the first time.",
    category = MissionCategory.Hydration,
    recurrence = MissionRecurrence.OneTime,
    reward = MissionReward(xp = 50, coins = 20),
    trigger = MissionTrigger.DailyGoalPercentage(
        targetPercentage = 0.8f
    )
)
```

## Tutorial: Add a New Trigger Family

If a mission cannot be represented by existing triggers:

1. Add a new subtype to `MissionTrigger`
2. Create a new evaluator
3. Register it in `MissionModule`
4. Emit that trigger shape from backend data

Example:

```kotlin
data class StepCount(
    val requiredSteps: Int
) : MissionTrigger
```

Then implement:

```kotlin
class StepCountMissionEvaluator : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.StepCount
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        // Calculate progress from step data
        return 0f
    }
}
```

Then register it in DI:

```kotlin
@Provides
fun provideMissionEvaluators(...): Set<MissionEvaluator>
```

## Tutorial: Connect Backend Definitions Later

Recommended production path:

1. Keep `MissionDefinition` as the app-side canonical model.
2. Create a backend DTO or Firestore model.
3. Map backend payload into `MissionDefinition`.
4. Store cached definitions locally.
5. Replace `InMemoryMissionDefinitionSource` with a real source implementing `MissionDefinitionSource`.

Recommended backend fields:

- `id`
- `title`
- `description`
- `category`
- `recurrence`
- `reward`
- `trigger_type`
- `trigger_payload`
- `is_active`
- `start_at`
- `end_at`
- `priority`

Recommended `reward` payload:

```json
{
  "xp": 25,
  "coins": 8
}
```

## Tutorial: Connect Backend/User Progress Later

Recommended production path:

1. Persist claim and progress records locally.
2. Add backend sync for mission progress.
3. Store reset boundary metadata if needed.
4. Make reward granting server-authoritative.

Suggested progress schema:

- `mission_id`
- `user_id`
- `progress`
- `status`
- `claimed_at`
- `updated_at`
- `period_key`

`period_key` is especially useful for recurring missions like daily and weekly because it makes reset handling explicit.

## Recommended Rules For Contributors

- Do not put mission rule logic in `MissionScreen`
- Do not put mission rule logic in `MissionViewModel`
- Prefer adding a new evaluator over adding `when` branches in the feature layer
- Keep backend mission payload mapping outside UI code
- Keep definition data separate from mutable progress data
- Use `MissionService` as the single app-facing entry point

## Current Limitations

- local persistence is not implemented yet
- backend sync is not implemented yet
- generic non-hydration event counting is not implemented yet
- mission rewards support XP and coins, but a persistent user coin wallet is not implemented yet
- one-time missions are supported logically, but persistence is still temporary because the progress store is in-memory

## Summary

The engine now supports the 3 product-required mission types:

- `Daily`
- `Weekly`
- `OneTime`

Daily and weekly claims are period-aware, while one-time claims remain permanent. The feature layer consumes grouped dashboard data, and the engine is ready to be connected to real backend and local persistence adapters later.
