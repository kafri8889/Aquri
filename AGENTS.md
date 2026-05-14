# Repository Guidelines

## Project Structure & Module Organization
This repository is a single-module Android app (`:app`) named **Aquri**.
- Kotlin source: `app/src/main/java/com/anafthdev/aquri`
- UI (Jetpack Compose): `ui/` (`screens/`, `components/`, `theme/`, `navigation/`)
- Data layer: `data/` (`database/`, `dao/`, `model/`, `repository/`)
- Dependency injection: `di/`
- Unit tests: `app/src/test/java/...`
- Instrumentation/UI tests: `app/src/androidTest/java/...`
- Resources and assets: `app/src/main/res/`
- Project notes/docs: `documents/`

## Build, Test, and Development Commands
Run from repository root:
- `./gradlew assembleDebug` builds the debug APK.
- `./gradlew installDebug` installs debug build on a connected device/emulator.
- `./gradlew testDebugUnitTest` runs local JVM unit tests.
- `./gradlew connectedDebugAndroidTest` runs instrumentation tests (device/emulator required).
- `./gradlew lintDebug` runs Android lint checks.
- `./gradlew clean` removes build outputs.

## Coding Style & Naming Conventions
- Language: Kotlin (JDK 11 target), Jetpack Compose, Gradle Kotlin DSL.
- Use 4-space indentation and keep trailing commas where Kotlin formatter expects them.
- Types/classes: `PascalCase` (`HomeViewModel`, `MissionRepository`).
- Functions/properties: `camelCase`.
- Compose UI files end with `Screen.kt`, `ViewModel.kt`, or `...Components.kt` as applicable.
- Resource names use `snake_case` (for example, `ic_bottle1_24dp.xml`).

## Testing Guidelines
- Frameworks: JUnit4 (`app/src/test`) and AndroidX instrumentation (`app/src/androidTest`).
- Name tests with `*Test.kt` and keep package paths aligned with production code.
- Prefer focused tests for repository logic, utilities, and ViewModel behavior.
- Run `testDebugUnitTest` before every PR; run `connectedDebugAndroidTest` for UI/behavior changes.

## Commit & Pull Request Guidelines
Recent history favors imperative, descriptive subjects (for example, `Implement bottle management and update Material 3 design system`).
- Commit format: short imperative summary, optional body for context/tradeoffs.
- Keep commits scoped to one feature/fix.
- PRs should include:
  - Clear summary of behavior changes
  - Linked issue/task (if available)
  - Test evidence (`./gradlew testDebugUnitTest`, lint, and/or androidTest)
  - Screenshots or recordings for UI changes

## Security & Configuration Tips
- Do not commit secrets beyond required app config files.
- Validate Firebase/Google service changes carefully (`app/google-services.json`).
- Keep local machine-specific settings in local-only files (`local.properties`).
