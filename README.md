# slate

a quiet, monochrome habit tracker for android. weekly view, day by day check-ins,
nothing else.

## what it does

- weekly grid: one row per habit, one softened square per day, monday to sunday
- a quiet trail of the last 53 weeks on every habit card
- a first-run welcome that fills a small grid, then stays out of the way
- per-day check-ins against a weekly target (1–7 times a week)
- step through past weeks and mark missed days
- habit detail: streak, target, past weeks, rename, delete
- year view: a compact heatmap of every day you kept
- light, dark and auto themes
- one optional daily reminder, scheduled at a time you choose, with a test action
- reset all habits without losing appearance or reminder settings
- everything is stored locally with datastore. no account, no cloud, no tracking.

## design

- palette: paper `#f3f3f0`, ink `#0b0b0c`, night `#09090a`,
  dark surfaces `#17171a` with `#2b2b30` hairlines, secondary text `#a6a6aa`
- font: inter, bundled in the app
- no uppercase labels, no gradients, no shadows
- lowercase copy in english only
- rounded-square days, one-canvas heatmaps, hairline cards
- a floating bottom dock with habits, year and settings
- quiet motion only: the first-run grid fill, week changes, day fills,
  the back-to-week reveal, dock pills and screen fades
- tabular numerals, 48dp touch targets, english dates on every phone

## architecture

- `data`: the habit model, pure `HabitRules`, and separate habit and settings
  flows over datastore; json decoding runs off the main thread
- `domain`: pure week progress, streak, year count and heatmap grid functions
- `reminder`: one-time work that reschedules itself after every run; the
  notification builder is shared with the settings test action
- `ui`: one `SlateViewModel` owns state and actions, screens stay stateless,
  navigation, the selected tab and the selected week survive rotation; the
  53-week trail is cached as a bitmap so scrolling stays cheap
- backend behavior is covered by plain unit tests: dates, habit rules, progress
  and reminder timing

## build

requirements: jdk 17, android sdk with platform 35 and build-tools 35.0.0.

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

the apk lands in `app/build/outputs/apk/debug/app-debug.apk`.

to build a release apk, configure your own signing key in `app/build.gradle.kts`
or use android studio's generate signed bundle / apk flow.

## install

1. copy the apk to your phone and open it, or run `adb install -r app-debug.apk`.
2. allow installs from the app you used to transfer the file if asked.
3. no internet permission is requested at all.

## github actions

every push to `main` builds a debug apk and uploads it as a workflow artifact.
open the latest run under actions, download `slate-debug`, unzip, install.

## stack

kotlin, jetpack compose, material 3, datastore, kotlinx.serialization, workmanager.
no room, no hilt, no navigation library.

## license

code is mit. the bundled inter font is licensed under the sil open font license,
see `licenses/Inter-OFL.txt`.
