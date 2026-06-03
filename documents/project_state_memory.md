# Aquri Project State Memory

Last read: 2026-06-02
Workspace: `D:\DOCUMENTS_V3\Android Projects\Aquri`

## Current Repository State

- Single-module Android app named `Aquri`.
- Main app module: `:app`.
- Main package: `com.anafthdev.aquri`.
- Git branch: `master`, tracking `origin/master`.
- Working tree was clean before this memory file was added.

## Stack

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX Navigation Compose with typed `@Serializable` destinations
- Hilt dependency injection
- Room database
- DataStore Preferences
- WorkManager dependencies are present
- Firebase Analytics and Crashlytics dependencies are present
- Vico charts for statistics
- Timber logging in debug builds

Important versions from `gradle/libs.versions.toml`:

- Android Gradle Plugin: `9.2.1`
- Kotlin: `2.3.21`
- Compose BOM: `2026.05.00`
- compileSdk: Android 36 extension/minor API 1
- minSdk: 28
- targetSdk: 36

## Runtime Entry Points

- `AquriApplication` is annotated with `@HiltAndroidApp`.
- `AquriApplication.onCreate()` plants `Timber.DebugTree()` in debug builds and launches `DatabaseInitializer.initialize()`.
- `MainActivity` is annotated with `@AndroidEntryPoint`.
- `MainActivity` enables edge-to-edge UI and renders `MainScreen()` inside `AquriTheme` and `ProvideVicoTheme`.
- The debug application id is `com.anafthdev.aquri.debug`.

## Architecture Shape

Current top-level package folders:

- `core`
- `data`
- `di`
- `ui`
- `utils`

Current screen folders:

- `home`
- `main`
- `manage_bottle`
- `mission`
- `onboarding`
- `profile`
- `statistic`

The project follows repository and ViewModel boundaries. UI code should depend on ViewModels and repositories/services, not DAOs directly.

## Navigation

- Navigation definitions live in `ui/navigation`.
- `Destinations` is a sealed `@Serializable` class.
- Main bottom navigation items are `Home`, `Statistic`, `Mission`, and `Profile`.
- Onboarding routes are `Onboarding1`, `Onboarding2`, and `Onboarding3`.
- `ManageBottle` is reachable from `Home`.
- `MainViewModel` chooses the start destination from `PreferenceRepository.isOnboardingCompleted`.

## Database And Data Layer

- Room database class: `data/database/AquriDatabase.kt`.
- Database name: `aquri_db`.
- Current database version: `3`.
- `exportSchema = false`.
- DAO interfaces:
  - `UserDao`
  - `HydrationDao`
  - `MissionDao`
  - `BadgeDao`
- DI module `DatabaseModule` creates the database with `Room.databaseBuilder(...).build()`.
- There are no migrations configured in the current database builder.
- `DatabaseInitializer` seeds predefined bottles and drink types through `HydrationRepository`.

Current repository classes:

- `HydrationRepository`
- `UserRepository`
- `MissionRepository`
- `BadgeRepository`
- `PreferenceRepository`

Hydration flow:

- `HomeViewModel` observes the current user, bottles, drink types, today summary, slot bottles, recent logs, gamification, reminder settings, and next reminder time.
- `HomeViewModel.logDrink()` inserts a `HydrationLogEntity`.
- After insert/update/delete, `HomeViewModel` recalculates the affected `DailySummaryEntity`.
- Logs are queried by date using midnight timestamps from `DateTimeUtils`.

Known implementation detail:

- `HomeViewModel.nextReminderTime` is currently mocked as one hour from now. Real reminder scheduling logic is not implemented there yet.

## Mission System

The mission system already has a foundation under `core/mission` and `data/mission`.

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
- `ui/screens/mission/MissionViewModel.kt`
- `ui/screens/mission/MissionScreen.kt`

Current design:

- Mission definitions are separated from user progress.
- Evaluators own rule logic.
- `MissionEngine` evaluates definitions into dashboard-ready card models.
- `MissionService` is the app-facing facade and combines mission definitions, claimed state, hydration logs, daily summaries, and gamification.
- Daily, weekly, and one-time missions are the main current product target.
- Monthly and event recurrence exist for future expansion.
- Claim state is currently backed by the in-memory progress store.
- `MissionViewModel` still injects placeholder highlighted badges, level rewards, and challenge preview content.

## Testing Workflow Preference

For real device or emulator testing, prefer Android CLI first:

```powershell
android help
android run --apks=app/build/outputs/apk/debug/app-debug.apk
android layout --pretty --device=<serial>
```

Prior known environment detail from memory:

- `android` may not be on PATH in the default PowerShell shell.
- A local `android.exe` was previously usable when present.
- `ANDROID_USER_HOME` may need to point to a writable workspace folder to avoid analytics spool permission errors.

Gradle commands from the project instructions:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat lintDebug
```

## Latest Verification

Command run on 2026-06-02:

```powershell
.\gradlew.bat testDebugUnitTest
```

Result:

- Build failed because 1 of 4 unit tests failed.
- Failing class: `com.anafthdev.aquri.utils.DateTimeUtilsTest`.
- Failing test: `getWeekRange_isCorrect`.
- Failure: expected day of month `6`, but actual was `5`.
- Report path: `app/build/reports/tests/testDebugUnitTest/index.html`.

This likely depends on system timezone or the expected timestamp/week boundary assumption. Do not treat unit tests as passing until this is fixed or revalidated.

## Development Guidance For Future Work

- Inspect local patterns before changing architecture.
- Keep changes focused and do not refactor unrelated code.
- Use existing `di/` modules for dependency provisioning.
- Place UI screens in `ui/screens`.
- Place reusable composables in `ui/components` or screen-local `components`.
- Keep business logic out of composables.
- ViewModels should depend on repositories or app-facing services.
- UI should not access DAOs directly.
- For mission work, extend definitions/evaluators/service first, then update ViewModel/UI.
- For hydration work, update `HydrationRepository`, `HomeViewModel`, and summary recalculation carefully.
- For database schema changes, remember there are currently no Room migrations configured.
