# Pool Scorekeeper

Android scorekeeper for straight pool. Cowboy Pool is a placeholder screen.

The scoring rules live in the `:domain` Java module and are defined by its JUnit tests. The Android app (`:app`) is a Material 3 UI on top of that engine.

## Build

Requires JDK 17+ (JDK 21 works). The Android app also needs Android SDK 35. If no SDK is configured, Gradle only builds `:domain` so the rule tests still run.

```bash
./gradlew test                  # scoring rules + undo
./gradlew assembleDebug         # APK (needs Android SDK / Android Studio)
```

Open the project in Android Studio if you prefer. Studio writes `local.properties` and the `:app` module is included automatically.

## Straight pool

- Start or resume a two-player match
- Record shot made, miss, safe, foul, and new rack
- Undo reverses the last scoring action (not saved across resume)
- Win applause still plays when a player reaches the target
