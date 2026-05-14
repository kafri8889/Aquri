# AQURI — Development Document v1.1.0

> **Platform:** Android Native (Kotlin + Jetpack Compose)  
> **Backend:** Firebase (Firestore + Auth + Cloud Functions + FCM)  
> **Date:** 2 April 2026  
> **Status:** Pre-Development  
> **Confidentiality:** Internal Use Only — © 2025 Aquri / Dalsicore

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [System Architecture](#3-system-architecture)
4. [Firestore Data Model](#4-firestore-data-model)
5. [Gamification System](#5-gamification-system)
6. [Cloud Functions](#6-cloud-functions)
7. [Screen Inventory](#7-screen-inventory)
8. [Design System](#8-design-system)
9. [Development Phases](#9-development-phases)
10. [Security & Privacy](#10-security--privacy)
11. [Testing Strategy](#11-testing-strategy)
12. [Appendix](#appendix)

---

## 1. Project Overview

### 1.1 Product Summary

Aquri is an Android-native hydration reminder application combining daily water intake tracking with a gamification system. Users log water consumption, earn XP, level up, maintain streaks, complete quests, and collect badges — forming a habit-building loop that motivates consistent hydration.

- **Language:** Kotlin (100% — no Java)
- **UI:** Jetpack Compose
- **Min SDK:** API 26 (Android 8.0 Oreo and above)

### 1.2 Goals & Success Metrics

| Metric | Target |
|---|---|
| DAU/MAU Ratio | ≥ 40% — users open the app daily |
| Day-30 Retention | ≥ 35% of new installs still active |
| Goal Completion Rate | ≥ 60% of users hit daily goal ≥ 3×/week |
| Streak ≥ 7 Days | ≥ 25% of active users hold a 7-day streak |
| Pro Conversion | ≥ 5% of active free users upgrade to Pro (Google Play Billing) |
| Play Store Rating | ≥ 4.5 stars |

### 1.3 Target Users

- Health-conscious Android users aged 18–40 building better hydration habits
- Fitness enthusiasts tracking overall wellness alongside workouts
- Office workers with sedentary lifestyles who forget to drink water
- Users motivated by gamification and streak-based habit trackers

---

## 2. Technology Stack

### 2.1 Android Frontend

| Technology | Choice |
|---|---|
| Language | Kotlin 2.x (100% Kotlin — no Java) |
| UI Framework | Jetpack Compose (Material Design 3) |
| Min / Target SDK | minSdk 26 (Android 8.0) / targetSdk 35 (Android 15) |
| Architecture | MVVM + Clean Architecture (UI → ViewModel → UseCase → Repository) |
| Navigation | Compose Navigation 2.x (type-safe routes via Kotlin Serialization) |
| DI Framework | Hilt (Dagger Hilt) — constructor injection throughout |
| Async | Kotlin Coroutines + Flow |
| Local Database | Room 2.x (SQLite wrapper) — offline-first hydration log store |
| Preferences | DataStore (Proto) — replaces SharedPreferences |
| HTTP Client | Ktor Client (for REST calls, e.g. Expo Push fallback) |
| Image Loading | Coil 3.x (Compose-native) |
| Animations | Jetpack Compose Animation + Lottie Compose (badge unlock) |
| Charts | Vico (Compose-native chart library) |
| Billing (Pro) | Google Play Billing Library 7.x |
| Crash Reporting | Firebase Crashlytics |
| Analytics | Firebase Analytics |

### 2.2 Backend — Firebase

| Service | Role |
|---|---|
| Authentication | Firebase Auth (Email/Password + Google Sign-In) |
| Primary Database | Cloud Firestore (NoSQL document store) |
| Serverless Functions | Cloud Functions for Firebase (Node.js 20 / TypeScript) |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| Remote Config | Firebase Remote Config — feature flags, XP tuning without release |
| File Storage | Firebase Storage — badge icons, user avatars |
| Hosting (future) | Firebase Hosting — optional web dashboard for stats |
| Scheduled Jobs | Cloud Functions + Cloud Scheduler (pub/sub) — daily summary, quest assignment, streak evaluation |

### 2.3 DevOps & Tooling

| Tool | Choice |
|---|---|
| Build System | Gradle (Kotlin DSL) + Android Gradle Plugin 8.x |
| Version Control | Git + GitHub (trunk-based, feature branches) |
| CI/CD | GitHub Actions — build, lint, test, deploy to Firebase App Distribution |
| Beta Distribution | Firebase App Distribution |
| Production Release | Google Play Internal Track → Closed Testing → Production |
| Code Quality | Detekt + ktlint + Android Lint |
| Testing | JUnit 5 + MockK + Turbine + Compose UI Test + Espresso (E2E) |
| Crash Monitoring | Firebase Crashlytics |
| Performance | Firebase Performance Monitoring |
| Secrets Management | google-services.json (gitignored) + GitHub Actions Secrets |

---

## 3. System Architecture

### 3.1 High-Level Overview

Aquri follows an **offline-first** architecture. All hydration logs are written to the local Room database first, then synced to Firestore in the background via a WorkManager sync worker. This ensures full functionality without internet — critical for a daily habit app.

```
┌──────────────────────────────────────────────────────────────────────┐
│                     Android App (Kotlin + Compose)                   │
│  ┌────────────────┐  ┌──────────────────┐  ┌─────────────────────┐  │
│  │   Compose UI   │  │   ViewModels     │  │     UseCases        │  │
│  │   (Screens)    │◄─┤  (StateFlow)     │◄─┤  (Business Logic)   │  │
│  └────────────────┘  └──────────────────┘  └────────┬────────────┘  │
│                                                      │               │
│  ┌───────────────────────────────────────────────────▼───────────┐  │
│  │                       Repository Layer                        │  │
│  │  ┌─────────────────────────┐  ┌──────────────────────────┐   │  │
│  │  │  Room DB (Local Source) │  │  Firebase SDK (Remote)   │   │  │
│  │  │  - HydrationLogDao      │  │  - Firestore             │   │  │
│  │  │  - DailySummaryDao      │  │  - Firebase Auth         │   │  │
│  │  │  - UserPrefsDataStore   │  │  - Firebase Storage      │   │  │
│  │  └─────────────────────────┘  └──────────────────────────┘   │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌─────────────────────────────┐  ┌─────────────────────────────┐  │
│  │  WorkManager (Sync Worker)  │  │   AlarmManager / FCM        │  │
│  │  - Offline → Firestore sync │  │   (Hydration Reminders)     │  │
│  └─────────────────────────────┘  └─────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
                          │ Firebase SDK (HTTPS)
┌──────────────────────────────────────────────────────────────────────┐
│                          Firebase Backend                            │
│  ┌──────────────┐  ┌──────────────────┐  ┌─────────────────────┐   │
│  │  Firebase    │  │  Cloud Firestore  │  │  Cloud Functions    │   │
│  │  Auth        │  │  (Users, Logs,    │  │  (Gamification      │   │
│  │  (JWT / UID) │  │  Gamification,    │  │  Engine, Quests,    │   │
│  │              │  │  Quests, Badges)  │  │  Streak Eval, FCM)  │   │
│  └──────────────┘  └──────────────────┘  └─────────────────────┘   │
│  ┌──────────────────┐  ┌───────────────────┐  ┌─────────────────┐  │
│  │  Firebase Storage│  │  Remote Config    │  │  FCM            │  │
│  │  (Badge icons,   │  │  (Feature flags,  │  │  (Push notif.)  │  │
│  │   Avatars)       │  │   XP multipliers) │  │                 │  │
│  └──────────────────┘  └───────────────────┘  └─────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.2 MVVM + Clean Architecture Layers

| Layer | Responsibility |
|---|---|
| **UI Layer** | Jetpack Compose screens + Composables. Observes `StateFlow`/`UiState` from ViewModel. Zero business logic. |
| **ViewModel Layer** | One ViewModel per screen. Holds `UiState` as `StateFlow`. Calls UseCases via Kotlin Coroutines. Survives config changes. |
| **UseCase Layer** | Single-responsibility Kotlin classes (e.g., `LogHydrationUseCase`, `GrantXpUseCase`). Orchestrates Repositories. Fully unit-testable. |
| **Repository Layer** | Interface + implementation. Decides whether to serve from Room cache or fetch from Firestore. Implements offline-first merge logic. |
| **Data Sources** | Room DAOs (local) and Firebase SDK wrappers (remote). Neither layer knows about the other — the Repository bridges them. |

### 3.3 Module / Directory Structure

```
aquri/
├── app/                        # App module — DI graph root, MainActivity
├── core/
│   ├── database/               # Room schema, DAOs, migrations
│   ├── datastore/              # Proto DataStore schemas
│   ├── firebase/               # Firebase SDK wrappers (Auth, Firestore, FCM)
│   ├── sync/                   # WorkManager sync workers
│   ├── notifications/          # Notification channels, scheduling
│   └── ui/                     # Shared Compose components, theme, tokens
├── feature/
│   ├── onboarding/             # 3-step onboarding flow
│   ├── home/                   # Home screen (progress + quick-add)
│   ├── stats/                  # Statistics & history
│   ├── missions/               # Quests, XP, levels, badges
│   ├── profile/                # Profile, settings, pro upgrade
│   ├── badges/                 # Badge gallery screen
│   ├── challenges/             # Epic & personal challenges
│   └── auth/                   # Login / Register screens
└── functions/                  # Cloud Functions (TypeScript, separate project)
    ├── src/
    │   ├── gamification/       # XP grant, level-up, streak eval
    │   ├── quests/             # Daily quest assignment
    │   ├── notifications/      # FCM reminder sender
    │   └── triggers/           # Firestore onCreate/onUpdate triggers
    └── package.json
```

---

## 4. Firestore Data Model

### 4.1 Collection Overview

Security Rules enforce that users can only read/write their own documents.

| Collection / Document Path | Key Fields |
|---|---|
| `users/{uid}` | `name`, `email`, `gender`, `weight_kg`, `activity_level`, `climate`, `daily_goal_ml`, `is_pro`, `created_at` |
| `users/{uid}/hydration_logs/{logId}` | `amount_ml`, `drink_type`, `logged_at`, `log_date`, `synced` |
| `users/{uid}/daily_summaries/{date}` | `total_ml`, `goal_ml`, `completion_pct`, `xp_earned`, `goal_reached` |
| `users/{uid}/gamification` | `total_xp`, `current_level`, `level_title`, `current_streak`, `longest_streak`, `shield_count`, `last_active_date` |
| `users/{uid}/user_quests/{questId}` | `quest_ref`, `assigned_date`, `progress_value`, `is_completed`, `completed_at` |
| `users/{uid}/user_badges/{badgeId}` | `badge_ref`, `earned_at`, `is_featured` |
| `users/{uid}/user_challenges/{chalId}` | `challenge_ref`, `joined_at`, `progress_ml`, `is_completed`, `completed_at` |
| `users/{uid}/reminder_settings` | `is_enabled`, `schedule_times` (array), `frequency_type`, `interval_minutes`, `smart_reminders` |
| `quests/{questId}` | `title`, `description`, `type`, `target_value`, `xp_reward`, `is_pro_only`, `is_active` |
| `badges/{badgeId}` | `name`, `description`, `icon_url`, `category`, `rarity`, `is_pro_only`, `unlock_condition` |
| `challenges/{chalId}` | `name`, `target_volume_ml`, `duration_days`, `start_date`, `end_date`, `reward_xp`, `reward_badge_ref` |
| `levels/{level}` | `level_number`, `title`, `xp_required`, `badge_icon` |

### 4.2 Offline-First with Room Cache

All writes go to Room first. A WorkManager periodic sync task (every 15 minutes, or immediately on reconnect) pushes pending local writes to Firestore using batch writes with idempotent document IDs.

| Data | Caching Strategy |
|---|---|
| `hydration_logs` | Room table mirrors Firestore. Includes `synced: Boolean`; WorkManager syncs rows where `synced = false`. |
| `daily_summaries` | Room table mirrors Firestore. Updated optimistically on each log; finalized by Cloud Function at midnight. |
| `gamification` | Cached in DataStore Proto (not Room). Refreshed from Firestore on app open and after any XP-granting action. |
| `quests` | Global quest templates cached in Room for offline display. Refreshed daily. |

### 4.3 Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own subtree
    match /users/{uid}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }
    // Global collections are read-only for authenticated users
    match /quests/{id}     { allow read: if request.auth != null; allow write: if false; }
    match /badges/{id}     { allow read: if request.auth != null; allow write: if false; }
    match /challenges/{id} { allow read: if request.auth != null; allow write: if false; }
    match /levels/{id}     { allow read: if request.auth != null; allow write: if false; }
  }
}
```

### 4.4 Hydration Goal Formula

Stored in `users/{uid}.daily_goal_ml`. Recalculated when weight, activity, or climate changes.

```kotlin
// HydrationGoalCalculator.kt
fun calculate(weightKg: Float, activity: ActivityLevel, climate: Climate): Int {
    var goal = weightKg * 35f  // 35ml per kg baseline

    goal *= when (activity) {
        ActivityLevel.SEDENTARY -> 1.0f
        ActivityLevel.MODERATE  -> 1.2f
        ActivityLevel.ACTIVE    -> 1.4f
    }

    goal *= when (climate) {
        Climate.COLD      -> 0.9f
        Climate.TEMPERATE -> 1.0f
        Climate.HOT       -> 1.15f
    }

    // Round to nearest 50ml, clamp to [1500, 4500]
    return (Math.round(goal / 50f) * 50).coerceIn(1500, 4500)
}
```

---

## 5. Gamification System

### 5.1 XP & Leveling

XP is awarded **once per qualifying event per day** — not per individual log entry. The Cloud Function `onDailySummaryFinalized` is the authoritative XP granter after midnight.

| Action | XP Reward | Frequency |
|---|---|---|
| Daily goal reached (≥ 100%) | Base 50 XP | Once per day |
| Goal ≥ 80% (close call) | Base 20 XP | Once per day |
| First log of the day | +5 XP | Once per day |
| Daily quest completed | +15–30 XP | Per quest (max 3/day) |
| Weekly quest completed | +100 XP | Per quest |
| Epic challenge finished | +250–500 XP | One-time |
| Streak milestone (7, 14, 30…) | +50–200 XP bonus | One-time per milestone |

#### Level Thresholds

Formula: **XP required to reach level N = N² × 80** (cumulative)

| Level & Title | Total XP Required |
|---|---|
| 1 — Hydration Newbie | 0 XP |
| 5 — Water Warrior | 8,000 XP |
| 10 — Hydro Apprentice | 32,000 XP |
| 14 — Hydro Knight | 62,720 XP |
| 20 — Ocean Commander | 128,000 XP |
| 30 — Aqua Master | 288,000 XP |
| 50 — Poseidon's Chosen | 800,000 XP |

### 5.2 Streak & Shield System

| Rule | Condition |
|---|---|
| Streak increment | Daily goal met before 23:59 local time |
| Streak preserved | Goal missed but ≥ 1 shield available — shield consumed automatically |
| Streak reset | Goal missed AND no shields available |
| Shield earned | Every 7-day streak milestone + select quest rewards |
| Max shields held | 3 (Free) / 5 (Pro) |

### 5.3 Quest System

#### Quest Types

| Type | Description |
|---|---|
| **Daily Quests** | 3 auto-assigned at 00:05 WIB by Cloud Scheduler. Expire at midnight. Examples: "Drink 500ml before 10am", "Log 6 entries today". |
| **Weekly Quests** | 1 assigned per Monday at 00:05 WIB. Higher XP, longer duration. |
| **Epic Challenges** | Global time-limited challenges (e.g. Ocean Marathon: 2L/day for 7 days). Completion grants exclusive badges and large XP. |
| **Personal Challenges (Pro)** | User-designed challenges with custom name, target volume, duration, and optional reminder. |

### 5.4 Badge Categories

| Category | Description | Rarity Range |
|---|---|---|
| Milestones | Volume-based (e.g., 1,000L lifetime) | Common → Legendary |
| Habits | Streak & consistency-based | Common → Epic |
| Special Events | Seasonal / time-limited | Rare → Legendary |
| Challenges | Earned by completing Epic Challenges | Epic → Legendary |

---

## 6. Cloud Functions

### 6.1 Function Inventory

| Function | Trigger & Purpose |
|---|---|
| `onHydrationLogCreated` | Firestore `onCreate` on `users/{uid}/hydration_logs/{id}`. Updates today's `daily_summary` optimistically. |
| `onDailySummaryFinalized` | Cloud Scheduler 00:05 WIB. Finalises previous day's summary, calls `evaluateStreak` and `grantXp` for all active users. |
| `evaluateStreak` | Internal helper. Checks `goal_reached` on `daily_summary`. Increments streak, applies shield if needed, or resets streak. |
| `grantXp` | Internal helper. Awards XP for a given action enum. Checks for level-up; if leveled up, updates `level_title` and triggers badge check. |
| `assignDailyQuests` | Cloud Scheduler 00:05 WIB. Assigns 3 fresh daily quests per active user based on level and quest history. |
| `onQuestCompleted` | Firestore `onUpdate` on `user_quests/{id}` when `is_completed` flips to `true`. Calls `grantXp` and `checkBadgeEligibility`. |
| `checkBadgeEligibility` | Internal helper. Runs all badge unlock conditions for a user. Writes new earned badges to `user_badges`. |
| `sendHydrationReminder` | Cloud Scheduler every 30 min. Reads `reminder_settings` for users due a reminder; sends FCM data message via Admin SDK. |
| `onUserCreated` | Firebase Auth `onCreate`. Creates `users/{uid}` document with defaults; sends welcome FCM notification. |

### 6.2 FCM Push Notification Strategy

Reminders are sent as **FCM data messages** (not notification messages) — the Android app handles display using a custom `NotificationCompat` builder.

| Aspect | Implementation |
|---|---|
| Reminder type | FCM data message → handled by `AquriFirebaseService` (extends `FirebaseMessagingService`) |
| Scheduling | Cloud Scheduler checks users with a due reminder window every 30 min. Respects DND hours (22:00–07:00) and Smart Reminders (Pro). |
| Local fallback | `AlarmManager` exact alarms as local fallback if FCM is unreliable (device offline). `AlarmManager` fires WorkManager task. |
| Token refresh | FCM token stored in `users/{uid}.fcm_token`. Refreshed via `onNewToken` callback. |
| Notification channel | `CHANNEL_REMINDERS` (importance HIGH) created at app startup. Required on Android 8+. |

### 6.3 WorkManager Sync

```kotlin
// SyncHydrationLogsWorker.kt
class SyncHydrationLogsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val hydrationRepository: HydrationRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            hydrationRepository.syncPendingLogs() // pushes Room rows where synced=false
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}

// Enqueue periodic + immediate-on-network-available
WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "sync_hydration_logs",
    ExistingPeriodicWorkPolicy.KEEP,
    PeriodicWorkRequestBuilder<SyncHydrationLogsWorker>(15, TimeUnit.MINUTES)
        .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
        .build()
)
```

---

## 7. Screen Inventory

### 7.1 Onboarding Flow

| Screen | Content |
|---|---|
| Onboarding: Personal Info | Gender selection (Male / Female / Other), body weight input with KG/LBS unit toggle |
| Onboarding: Activity & Climate | Activity level (Sedentary / Moderate / Active), local climate (Cold / Temperate / Hot) |
| Onboarding: Goal Result | Personalised daily goal with bento-card breakdown (weight, activity, climate contributions). "Get Started" button. |

### 7.2 Main App — Bottom Navigation (4 Tabs)

| Tab | Content |
|---|---|
| **Home** | Wave/fill progress bar (today's intake vs goal), quick-add section (preset: 150ml, 250ml, 350ml, 500ml, custom), recent drink history, hydration tip card |
| **Stats** | Time range selector (7D / 30D / 90D), hero stat cards (average daily, best day, efficiency %), Vico bar chart, volume comparison vs previous period, weekly insights |
| **Missions** | XP + level card with animated progress bar, streak count + shield indicator, daily quest grid, epic challenge card, badge progression preview |
| **Profile** | User hero card (avatar, streak, missions won, total volume), achievement bento grid, Go Pro CTA, hydration settings, account & privacy settings, logout |

### 7.3 Secondary Screens

| Screen | Content |
|---|---|
| Badges Gallery | Full badge collection with locked/unlocked states, rarity indicators, category filter chips, "Next Achievement" highlight card |
| Epic Challenges | Featured challenge (Ocean Marathon hero card), secondary challenges bento grid, motivation banner with legendary badge tease |
| Daily Quests Detail | Hero progress card, full quest grid (all states), bonus quest section, weekly challenge card |
| Personal Challenge (Pro) | Challenge builder: name input, target volume slider, duration picker, reminder toggle, reward preview card, share CTA |
| Statistics — History Detail | Detailed log history per day, per drink type breakdown, exportable summary (Pro) |

---

## 8. Design System

### 8.1 Jetpack Compose Theme Setup

Custom `MaterialTheme` wrapper (`AquriTheme`) using Material Design 3 tokens. All Composables receive design tokens through `CompositionLocal` — never hardcoded values.

```kotlin
// AquriTheme.kt
private val LightColorScheme = lightColorScheme(
    primary          = Color(0xFF00B4D8), // Brand Cyan
    onPrimary        = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF00677D), // Brand Dark Cyan
    background       = Color(0xFFF7F9FB),
    surface          = Color(0xFFFFFFFF),
    onBackground     = Color(0xFF191C1E), // Text Primary
    onSurface        = Color(0xFF3D494D), // Text Secondary
    outline          = Color(0xFFE0E3E5),
)

@Composable
fun AquriTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = AquriTypography,
        shapes      = AquriShapes,
        content     = content
    )
}
```

### 8.2 Typography

**Primary typeface:** Plus Jakarta Sans (downloadable via Google Fonts API or bundled in `res/font/`). Follows Material Design 3 type scale.

| Text Style | Spec & Usage |
|---|---|
| `displayLarge` (ExtraBold 800) | 36sp — Onboarding headings, goal number display |
| `headlineLarge` (Bold 700) | 30sp — Screen titles |
| `headlineMedium` (Bold 700) | 24sp — Card headings |
| `titleMedium` (Bold 700) | 16sp uppercase — Section labels |
| `bodyLarge` (Medium 500) | 16sp — Descriptive text, subtitles |
| `labelSmall` (Regular 400) | 10–12sp uppercase — Captions, meta text |
| Button label (ExtraBold 800) | 18sp — Primary CTA text |

### 8.3 Colour Tokens

| Token / Hex | Usage |
|---|---|
| `primary` / `#00B4D8` | Primary brand Cyan — CTAs, active states, progress fill |
| `primaryContainer` / `#00677D` | Dark Cyan — gradient start, selected indicator text |
| `background` / `#F7F9FB` | Main app background |
| `surface` / `#FFFFFF` | Cards, inputs, bottom sheets |
| `surfaceVariant` / `#F2F4F6` | Input field background |
| `onBackground` / `#191C1E` | Headings, primary text |
| `onSurface` / `#3D494D` | Body text, descriptions |
| `onSurfaceVariant` / `#6D797E` | Labels, placeholders, captions |
| `outline` / `#E0E3E5` | Inactive dividers, unselected progress segments |
| Glow overlay | `rgba(76, 214, 251, 0.1)` — decorative blur backgrounds (custom) |

### 8.4 Key Composable Patterns

#### Primary Button
Pill shape (`CircleShape`), height 64dp, gradient brush `#00677D → #00B4D8` (135°), elevation shadow via modifier. Font: Plus Jakarta Sans ExtraBold 18sp white.

#### Card
`RoundedCornerShape(32.dp)`, white surface, subtle shadow via `CardDefaults.cardElevation(defaultElevation = 2.dp)`. Bento-grid cards use asymmetric widths via `weight()` modifiers.

#### Input Field
Custom Composable (not `OutlinedTextField`). Background `surfaceVariant`, shape `RoundedCornerShape(32.dp)`, height 96dp for numeric display inputs showing large ExtraBold font (36sp).

#### Selection Button
- **Selected:** `BorderStroke(2.dp, primary)` + outer glow via `Modifier.shadow` with `blurRadius`
- **Unselected:** border transparent, white background

---

## 9. Development Phases

### Phase 1 — Foundation (Weeks 1–3)

**Goal:** Runnable app skeleton with auth, onboarding, and basic hydration logging syncing to Firestore.

1. Android project setup: Kotlin + Compose + Hilt + Room + DataStore + ktlint + Detekt
2. Firebase project init: Auth, Firestore, Storage, FCM, Crashlytics enabled
3. Room schema: `HydrationLog` + `DailySummary` tables + migrations
4. WorkManager sync worker: offline Room → Firestore push
5. Firebase Auth screens: login (email + Google), register, forgot password
6. Onboarding 3-step flow: gender, weight (KG/LBS toggle), activity, climate → goal calc
7. Home screen: wave progress bar, quick-add buttons, recent history
8. `onHydrationLogCreated` Cloud Function: optimistic `daily_summary` update

### Phase 2 — Core Features (Weeks 4–6)

**Goal:** All four main tabs functional with live data from Firestore.

9. Stats screen: Vico bar chart, efficiency score, period comparison
10. `assignDailyQuests` Cloud Scheduler + Missions tab quest grid UI
11. `grantXp` + `evaluateStreak` Cloud Functions
12. Streak card + shield indicator UI on Missions tab
13. Profile screen: hero section, hydration settings, account settings
14. FCM reminder system: Cloud Scheduler + `AquriFirebaseService` + `AlarmManager` fallback
15. Notification channel setup, `reminder_settings` UI in Profile

### Phase 3 — Gamification (Weeks 7–9)

**Goal:** Full gamification loop — badges, challenges, epic quests, animated level-ups.

16. Badge system: seed badge/quest/level Firestore collections, `checkBadgeEligibility` function
17. Badges Gallery screen: unlocked/locked states, rarity chips, category filters
18. Epic Challenges screen + `user_challenges` participation + Cloud Function progress tracking
19. Daily Quests Detail screen (hero card, full quest grid, bonus quest)
20. Animated XP bar + Lottie level-up celebration overlay
21. Weekly quest system

### Phase 4 — Pro & Polish (Weeks 10–12)

**Goal:** Google Play Billing, Pro features, performance polish, Play Store submission.

22. Google Play Billing Library: subscription setup, purchase flow, entitlement check via Firebase Remote Config / Firestore
23. Personal Challenge builder (Pro): Compose form + Cloud Function backend
24. Smart Reminders (Pro): Firebase Remote Config flag, context-aware FCM scheduling
25. Advanced Stats export (Pro): CSV generation + Android share sheet
26. Onboarding improvements: illustrations, empty states, error handling with Snackbars
27. Performance audit: Compose recomposition trace, Firestore query indexes, Room query plans
28. Baseline Profiles generation for startup performance
29. Google Play Store listing assets + internal testing track → production rollout

---

## 10. Security & Privacy

### 10.1 Firebase Security

- `google-services.json` is **gitignored**. Injected during CI builds via GitHub Actions secrets (base64-encoded env var).
- Firebase API keys in `google-services.json` are scoped by Android app package name + SHA-1 certificate fingerprint — cannot be used from other apps.
- Firebase Admin SDK private key (service account JSON) is **never** embedded in the APK. Lives only in Cloud Functions environment config.
- All Firestore access governed by Security Rules (Section 4.3). No open read/write.
- Firebase App Check (Play Integrity API) is enabled to block non-genuine Android clients.

### 10.2 Local Data Security

- Firebase Auth tokens stored by the Firebase Android SDK in app's private encrypted `SharedPreferences` — not accessible by other apps.
- Room database stored in app's private data directory. On Android 7+, encrypted by OS sandbox. For Pro users, consider enabling SQLCipher for additional encryption.
- No PII (name, email, weight) logged to Crashlytics or Analytics. User ID (UID) is pseudonymous.

### 10.3 Privacy

- Hydration and body weight data is personal health data. Privacy policy must clearly state it is not sold or shared with third parties.
- Firebase Analytics events use anonymised identifiers. Custom events (e.g., `hydration_log_added`) contain no PII.
- Users can export data and delete account (GDPR / UU PDP compliance) from Profile settings. Account deletion triggers a Cloud Function that deletes all user sub-collections and the Firebase Auth account.

---

## 11. Testing Strategy

| Layer | Scope |
|---|---|
| **Unit Tests** (JUnit 5 + MockK + Turbine) | Pure Kotlin logic: goal calculator, XP formula, streak rules, date helpers, ViewModel state transitions. Target: 100% coverage on `domain/` and `utils/`. Turbine tests `StateFlow` emissions from ViewModels. |
| **Repository Tests** (JUnit 5 + Robolectric) | Repository implementations tested with in-memory Room database and fake Firestore (Firebase Emulator Suite). |
| **Compose UI Tests** (Compose UI Test + Hilt Testing) | Key screens: onboarding flow, home quick-add interaction, quest completion animation. Uses `createAndroidComposeRule` with test Hilt modules providing fake repositories. |
| **Notification Tests** (Robolectric + ShadowNotificationManager) | Verify correct notification channel, content, and FCM payload parsing. |
| **E2E Tests** (Espresso + Firebase Emulator Suite) | Critical journeys against local emulators: full onboarding, first hydration log, daily quest completion, badge earn, Pro upgrade stub. Run in GitHub Actions on every PR. |
| **Manual QA** | Design review on physical devices: Pixel 6 (1080p), Samsung Galaxy A series (800p, One UI). Test on minSdk 26 device/emulator. Verify font scaling and large-text accessibility. |

---

## Appendix

### A. Gradle Dependencies (Key Libraries)

```kotlin
// build.gradle.kts (app module)

// Jetpack Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.06.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.8.0")

// Hilt
implementation("com.google.dagger:hilt-android:2.51")
ksp("com.google.dagger:hilt-compiler:2.51")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// DataStore
implementation("androidx.datastore:datastore:1.1.1")

// Firebase BOM
implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-messaging-ktx")
implementation("com.google.firebase:firebase-crashlytics-ktx")
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-config-ktx")

// WorkManager
implementation("androidx.work:work-runtime-ktx:2.9.0")
implementation("androidx.hilt:hilt-work:1.2.0")

// Charts
implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.18")

// Coil
implementation("io.coil-kt.coil3:coil-compose:3.0.4")

// Lottie
implementation("com.airbnb.android:lottie-compose:6.4.1")

// Google Play Billing
implementation("com.android.billingclient:billing-ktx:7.0.0")
```

### B. Naming Conventions

| Context | Convention |
|---|---|
| Files (Kotlin) | `PascalCase` matching class name (e.g., `HydrationLogRepository.kt`) |
| Composable functions | `PascalCase` (e.g., `WaveProgressBar`, `QuickAddSection`) |
| ViewModel | `[Screen]ViewModel` (e.g., `HomeViewModel`, `MissionsViewModel`) |
| UseCase | `[Verb][Noun]UseCase` (e.g., `LogHydrationUseCase`, `GrantXpUseCase`) |
| Repository interface | `[Domain]Repository` (e.g., `HydrationRepository`) |
| Room DAO | `[Entity]Dao` (e.g., `HydrationLogDao`) |
| Variables / functions | `camelCase` (e.g., `dailyGoalMl`, `calculateStreak()`) |
| Constants / enums | `SCREAMING_SNAKE_CASE` (e.g., `ACTIVITY_LEVEL_ACTIVE`) |
| Firestore collections | `snake_case` plural (e.g., `hydration_logs`, `user_badges`) |
| Firestore doc fields | `snake_case` (e.g., `logged_at`, `is_completed`) |
| Compose preview fns | `Preview` suffix (e.g., `HomeScreenPreview`) |

### C. Environment & Secrets

```bash
# GitHub Actions secrets (injected at CI build time)
GOOGLE_SERVICES_JSON_BASE64   # base64-encoded google-services.json
KEYSTORE_FILE_BASE64          # base64-encoded release keystore
KEYSTORE_PASSWORD
KEY_ALIAS
KEY_PASSWORD

# Cloud Functions environment config
firebase functions:config:set aquri.admin_key="..."

# Local development
# Place google-services.json in app/ (gitignored)
# Use Firebase Emulator Suite for local Firestore + Auth + Functions:
firebase emulators:start
```

### D. Figma Design Reference

The Figma design file (Main) is the source of truth for all UI implementation. All spacing, colours, and typography are derived from the Figma design tokens as specified in Section 8.

**Figma File:** https://www.figma.com/design/Y4k5P22lC8JuqN6ijVx1qb/Main
