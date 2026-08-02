# LeaderBoard 🏆

A multi-module Android application built with **Jetpack Compose**, **Kotlin Coroutines & Flow**, and **Hilt Dependency Injection**. The app displays a dynamic Top 20 player leaderboard with real-time score updates, rank recalculations, and visual change highlighting.

---

## 🌟 Features

- **Live Score Generator**: Simulates background player score updates at random intervals (0.5s – 3.0s).
- **Top 20 Leaderboard**: Dynamically maintains and displays the top 20 players based on total scores.
- **Scalable $O(\log N)$ Architecture**: Designed to seamlessly scale to 100,000+ players using efficient bucketed data structures.
- **Real-Time Visual Highlighting**: Automatically highlights player items for 1.5 seconds when their score or rank changes.
- **Background Lifecycle Management**: When the app goes to the background (`ON_STOP`), top 20 calculation pauses while score generation continues in memory. Upon resuming (`ON_START`), latest scores sync and top 20 recalculates immediately.
- **Dense Ranking with Tie-Breaking**: Players with equal scores share the same rank, and subsequent ranks skip accordingly (e.g., ranks: 1, 1, 3).
- **Decoupled Architecture**: Clean separation into independent, reusable modules (`gameengine`, `leaderboardengine`, and `app`).

---

## ⚡ Scalability & Performance Analysis ($O(\log N)$ Design)

The engine was architected with high-scale datasets (100,000+ players) in mind. Rather than storing players in a flat list and performing expensive $O(N \log N)$ sorting on every score update, it uses a **bucketed dual-data-structure approach**:

### Data Structures Used
1. **`TreeMap<Int, MutableSet<Player>>` (Score Buckets)**: Maps score values to sets of players having that exact score, automatically ordered by score in **descending order**.
2. **`HashMap<Int, Int>` (`playerScores`)**: Fast lookup table mapping `playerId -> currentScore`.

### Time Complexity Breakdown
| Operation | Time Complexity | Explanation |
| :--- | :--- | :--- |
| **Score Update** | **$O(\log K)$** | $O(1)$ lookup for old score, $O(1)$ set removal, $O(\log K)$ insertion into new `TreeMap` score bucket ($K$ = number of unique scores, $K \le N$). |
| **Top 20 Retrieval** | **$O(M)$** | Traversing only the top score buckets until $M = 20$ players are collected. **Does NOT sort all $N$ players** ($O(N \log N)$ avoided!). |
| **Player Score Lookup** | **$O(1)$** | Direct index lookup via `playerScores` HashMap. |

---

## 🚀 Further Scalability Suggestions (For 1M+ Players & Backend Production)

To scale this system even further for enterprise production with **millions of concurrent users** and **thousands of updates/sec**:

1**Flow Throttling / Rate-Limiting on Client UI**:
   - At extreme update rates (e.g. 5,000 score updates/sec), updating Compose UI for every single score change can cause UI jank.
   - Use `sample(200.ms)` or `debounce(100.ms)` on `_topPlayerList` in Kotlin Flow to cap UI renders at 5–10 FPS while keeping backend data accurate.

---

## 🏗️ Architecture & Module Breakdown

The project follows a clean, modular design to ensure code reuse, maintainability, and testability:

```
LeaderBoard/
├── app/                  # Android UI Layer (Jetpack Compose, Hilt DI, ViewModel)
├── leaderboardengine/    # Leaderboard Core Business Logic (Ranking, Data structures)
└── gameengine/           # Data & Live Score Generator (Player mock data, Score updates)
```

### 1. `:gameengine`
- **`Player`**: Primary player data model (`id`, `name`, `points`).
- **`ScoreGenerator`**: Reads initial player data from `players.json` via **Moshi** and continuously updates player scores using Kotlin `StateFlow`.
- **`MoshiModule`**: Hilt module providing Moshi JSON serialization instances.

### 2. `:leaderboardengine`
- **`LeaderBoard`**: Core engine maintaining `TreeMap<Int, MutableSet<Player>>` (descending points) and `playerScores: MutableMap<Int, Int>` lookup map.
- **`TopPlayerUIModel`**: Immutable UI representation of ranked players (`id`, `name`, `score`, `rank`).
- **`TopPlayerMapper`**: Maps raw engine data to UI-friendly models.

### 3. `:app`
- **`MainActivity`**: Single-activity entry point configuring edge-to-edge layout and hosting Compose UI.
- **`LeaderBoardViewModel`**: Connects `LeaderBoard` engine to Compose, managing highlight state snapshots (`previousPlayers`, `highlightedIds`).
- **`LeaderBoardScreen` & `PlayerItem`**: Compose UI displaying the smooth, scrollable list with fade-in/fade-out highlight animations.

---

## 🛠️ Tech Stack & Dependencies

- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose (Material 3)
- **Dependency Injection**: Hilt (Dagger)
- **Async & Reactive**: Kotlin Coroutines & `StateFlow`
- **JSON Parsing**: Moshi
- **Testing**: JUnit 4, MockK, Coroutines Test (`runTest`)

---

## 🚀 How to Run the Project

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer recommended
- **JDK**: Version 17 or higher
- **Android SDK**: API level 24 (Android 7.0) minimum, compiled against API 35

### Running via Android Studio
1. Open Android Studio and select **Open** -> Navigate to the project root directory (`LeaderBoard`).
2. Allow Gradle to sync dependencies.
3. Select an Emulator or connected Physical Device (API level 24+).
4. Run the `:app` configuration by clicking **Run** (`Control + R` or `Shift + F10`).

---

## 🧪 Running Unit Tests

Unit tests are included in the `:leaderboardengine` module covering leaderboard sorting, tie handling, top 20 result capping, duplicate prevention, background lifecycle pause/resume, and dynamic rank recalculations.

### Run All Unit Tests
```bash
./gradlew test
```

---

## 📋 Unit Test Coverage Overview

- **`getTop20 with no players emits empty list`**: Verifies initial state.
- **`getTop20 sorts registered players by points descending`**: Validates sorting logic.
- **`getTop20 assigns same rank to tied scores and skips next rank`**: Validates dense ranking tie logic.
- **`getTop20 caps result at 20 players`**: Ensures max size limit.
- **`updateScore moves player into new score bucket reflected in getTop20`**: Validates score updates.
- **`updateScore does not leave stale duplicate entries across buckets`**: Ensures clean data cleanup across score buckets.
- **`updateScore for unregistered player still adds them`**: Validates dynamic player registration.
- **`updateScore recalculates ranks correctly after tie is broken`**: Verifies rank recalculations after score changes.
- **`resumeLeaderBoard syncs fresh scores from scoreGenerator and updates top20`**: Validates background score accumulation and foreground recalculations.
