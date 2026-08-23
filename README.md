# Atheer — Arabic video platform (Android)

A YouTube-style Arabic video app built on a modern, scalable Android foundation.

## Tech stack

| Layer      | Technology                                                        |
|------------|-------------------------------------------------------------------|
| UI         | Jetpack Compose + Material 3 (BOM `2026.06.00`), RTL-first        |
| Navigation | Navigation Compose 2.9 **type-safe routes** (kotlinx.serialization) |
| DI         | Hilt 2.60 (+ hilt-navigation-compose)                             |
| Data       | Room 2.8 (KSP, exported schemas), Kotlin Coroutines/Flow          |
| Video      | Media3 / ExoPlayer 1.11 (lifecycle-aware playback)                |
| Images     | Coil 3                                                            |
| Testing    | JUnit4, Robolectric, Turbine                                      |
| Build      | AGP 9.1, Kotlin 2.2, KSP, convention plugins in `build-logic`     |

## Architecture

Multi-module, unidirectional data flow:

```
:app                    Hilt app, type-safe NavHost, bottom navigation
├── :feature:home       Feed + category filters
├── :feature:shorts     VerticalPager + per-page ExoPlayer
├── :feature:create     Upload portal
├── :feature:subscriptions
├── :feature:search     Debounced in-memory search
├── :feature:library    Downloads + history
├── :feature:videodetail Player screen + comments + related videos
└── :core
    ├── model           Pure-Kotlin domain models
    ├── common          Coroutine qualifiers (@IoDispatcher, @ApplicationScope)
    ├── database        Room entities/DAOs/mappers (FK + indices)
    ├── data            Repository interface + OfflineFirst impl + seeding
    ├── designsystem    Theme, colors, typography
    └── ui              Shared composables (VideoCard, badges, formatters)
```

Key principles:
- **Single source of truth**: Room drives all state; the UI never applies optimistic deltas.
- **Type-safe navigation**: routes are `@Serializable` objects; system back (incl. predictive back) works natively.
- **Convention plugins** (`build-logic`) keep module build files minimal.

## Run

```bash
./gradlew :app:installDebug       # install on device/emulator
./gradlew testDebugUnitTest       # unit tests
./gradlew assembleRelease         # requires signing env vars (KEYSTORE_PATH, STORE_PASSWORD, KEY_PASSWORD)
```

Requires JDK 17+ and an Android SDK with platform android-36.1.
