# LeaderBoard 🏆

A multi-module Android application built with **Jetpack Compose**, **Kotlin Coroutines & Flow**, and **Hilt Dependency Injection**. The app displays a dynamic Top 20 player leaderboard with real-time score updates, rank recalculations, and visual change highlighting.

---

## 🌟 Features

- **Live Score Generator**: Simulates background player score updates at random intervals (0.5s – 3.0s).
- **Top 20 Leaderboard**: Dynamically maintains and displays the top 20 players based on total scores.
- **Efficient Data Structures**: Uses `TreeMap` and `HashMap` in the engine to update player scores in $O(\log K)$ time and maintain dense rank ordering.
- **Real-Time Visual Highlighting**: Automatically highlights player items for 1.5 seconds when their score or rank changes.
- **Dense Ranking with Tie-Breaking**: Players with equal scores share the same rank, and subsequent ranks skip accordingly (e.g., ranks: 1, 1, 3).
- **Decoupled Architecture**: Clean separation into independent, reusable modules (`gameengine`, `leaderboardengine`, and `app`).

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

## 🧪 Running Unit Tests

Unit tests are included in the `:leaderboardengine` module covering leaderboard sorting, tie handling, top 20 result capping, duplicate prevention, and dynamic rank recalculations.
1. Open unit test class
2. Click on run icon on top of the class or on individual methods

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
