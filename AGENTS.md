# AGENTS.md

## Project Context

This repository is a single-module Android application named **Aquri**.

### Technology Stack

* Kotlin
* Jetpack Compose
* Gradle Kotlin DSL
* AndroidX
* Material 3
* Repository pattern
* Dependency Injection (`di/`)

### Main Package

`app/src/main/java/com/anafthdev/aquri`

---

## Project Structure

```text
app/src/main/java/com/anafthdev/aquri/
├── data/
│   ├── database/
│   ├── dao/
│   ├── model/
│   └── repository/
├── di/
├── ui/
│   ├── screens/
│   ├── components/
│   ├── theme/
│   └── navigation/
```

Other important directories:

```text
app/src/main/res/
app/src/test/java/
app/src/androidTest/java/
documents/
```

---

## Development Principles

Before implementing any feature:

1. Inspect the existing architecture and coding patterns.
2. Reuse existing abstractions whenever possible.
3. Prefer minimal and focused changes.
4. Do not refactor unrelated code.
5. Do not introduce a new architecture unless clearly necessary.
6. If the request is ambiguous, create an implementation plan and ask for approval before coding.

---

## Android Device and Emulator Testing

When testing on a real Android device or emulator, use the terminal tool **`android-cli`**.

The available commands can be inspected with:

```bash
android help
```

Use `android-cli` whenever device or emulator interaction is required, including:

* Checking connected devices
* Installing APKs
* Launching applications
* Reading logcat
* Running instrumentation tests
* Capturing screenshots
* Measuring performance
* Inspecting application state

Do not rely solely on Gradle commands when the task explicitly requires testing on an actual device or emulator.

---

## Build Commands

Run all commands from the repository root.

### Build Debug APK

```bash
./gradlew assembleDebug
```

### Install Debug APK

```bash
./gradlew installDebug
```

### Run Unit Tests

```bash
./gradlew testDebugUnitTest
```

### Run Instrumentation Tests

```bash
./gradlew connectedDebugAndroidTest
```

### Run Android Lint

```bash
./gradlew lintDebug
```

### Clean Build Outputs

```bash
./gradlew clean
```

---

## Validation Expectations

Before completing a task, run the most relevant verification steps.

### Logic, Repository, and ViewModel Changes

```bash
./gradlew testDebugUnitTest
```

### UI or Runtime Changes Requiring Device/Emulator

```bash
./gradlew connectedDebugAndroidTest
```

Use `android-cli` for additional real-device validation when necessary.

### General Code Quality

```bash
./gradlew lintDebug
```

If tests cannot be executed, clearly explain why and specify what should be run manually.

---

## Kotlin Coding Style

### General Rules

* Use 4-space indentation.
* Write idiomatic Kotlin.
* Prefer clear and descriptive names.
* Keep functions and classes focused.
* Use trailing commas where appropriate.

### Naming Conventions

| Element                  | Convention       | Example                 |
| ------------------------ | ---------------- | ----------------------- |
| Classes and Types        | PascalCase       | `MissionRepository`     |
| Functions and Properties | camelCase        | `loadMissionProgress()` |
| Resources                | snake_case       | `ic_bottle1_24dp.xml`   |
| Screen Files             | `*Screen.kt`     | `MissionScreen.kt`      |
| ViewModels               | `*ViewModel.kt`  | `MissionViewModel.kt`   |
| Reusable Components      | `*Components.kt` | `MissionComponents.kt`  |

---

## Jetpack Compose Guidelines

* Place screens in `ui/screens/`.
* Place reusable composables in `ui/components/`.
* Place navigation code in `ui/navigation/`.
* Place theming code in `ui/theme/`.
* Avoid business logic inside composables.
* Use state hoisting where appropriate.
* Keep composables small and readable.

---

## Data Layer Guidelines

* Models belong in `data/model/`.
* DAO interfaces belong in `data/dao/`.
* Database configuration belongs in `data/database/`.
* Repositories belong in `data/repository/`.
* UI must not access DAOs directly.
* ViewModels should depend on repositories rather than low-level data sources.

---

## Dependency Injection

Use the existing dependency injection structure under `di/`.

Do not create ad-hoc singletons unless the project already follows that pattern.

---

## Feature Implementation Workflow

When implementing a new feature:

1. Identify related existing files and architecture patterns.
2. Create or update data models if necessary.
3. Implement repository changes.
4. Add ViewModel state and business logic.
5. Build the Compose UI.
6. Integrate navigation if required.
7. Run appropriate tests and validation commands.
8. Summarize changes and results.

---

## Mission System Guidance

When implementing the mission system, focus first on a scalable foundation.

Recommended architecture:

* Mission models
* Mission progress/state
* Mission repository
* ViewModel integration
* UI foundation
* Extensible support for future mission types

Avoid hardcoding mission logic directly inside UI components.

---

## Commit Guidelines

Use concise imperative commit messages.

Examples:

* `Implement mission foundation`
* `Add bottle progress repository`
* `Fix navigation state handling`
* `Update Material 3 screen layout`

Each commit should address a single feature or fix.

---

## Pull Request Guidelines

Each pull request should include:

* Summary of behavioral changes
* Linked issue or task (if available)
* Test and validation evidence
* Screenshots or recordings for UI changes

---

## Security and Configuration

Do not commit:

* API keys
* Tokens
* Private keys
* Local machine-specific settings

Special care should be taken with:

* `app/google-services.json`
* `local.properties`

`local.properties` must remain local-only.

---

## Final Response Requirements

When completing a task, provide:

1. Summary of what changed
2. List of modified files
3. Commands and tests executed
4. Known limitations or assumptions
5. Suggested next steps when relevant

Be explicit and honest if something could not be tested.
