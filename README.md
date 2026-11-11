# slate

a quiet, monochrome habit tracker for android. weekly view, day by day check-ins,
nothing else.

## what it does

- weekly grid: one row per habit, one circle per day, monday to sunday
- per-day check-ins against a weekly target (1–7 times a week)
- swipe through past weeks and mark missed days
- habit detail: streak, target, past weeks, rename, delete
- year view: a compact heatmap of every day you kept
- light, dark and auto themes
- one optional daily reminder, scheduled at a time you choose
- everything is stored locally with datastore. no account, no cloud, no tracking.

## design

- palette: paper `#f7f7f6`, ink `#0d0d0e`, night `#0a0a0b`
- font: inter, bundled in the app
- no uppercase labels, no gradients, no shadows
- lowercase copy in english only
- quiet motion only: week changes, day fills, theme pills and screen fades
- tabular numerals, 48dp touch targets, english dates on every phone

## build

requirements: jdk 17, android sdk with platform 35 and build-tools 35.0.0.

```bash
./gradlew assembleDebug
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
