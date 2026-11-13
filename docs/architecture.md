# architecture

slate is one android module, one activity and one view model. the code is split
by responsibility, not by screen: pure rules live in `data` and `domain`, android
integration lives at the edges, and every composable is stateless enough to
preview.

```
app/src/main/java/com/slate/weekly
├── MainActivity.kt              edge-to-edge host, notification permission
├── SlateApp.kt                  application object, owns the store
├── data/                        persistence and rules around it
├── domain/                      pure functions, no android imports
├── focus/                       accessibility service and its status helper
├── reminder/                    workmanager reminder and notifications
├── ui/                          screens, view model, navigation, theme
└── util/                        date helpers used by the grid
```

## data

| file | responsibility |
|:--|:--|
| `Habit.kt` | the serializable habit record: id, name, weekly target, creation day, checked days. |
| `HabitRules.kt` | pure validation and editing rules: clean names and targets, toggle semantics, normalisation and de-duplication. |
| `Settings.kt` | `SlateSettings`, `SlateData`, `ThemeMode` and the persisted preference keys’ shape. |
| `SlateStore.kt` | the only writer. wraps a preferences datastore, decodes json off the main thread, exposes `data` and `settings` flows, and refuses unsafe focus changes. |
| `InstalledApps.kt` | loads launchable apps, hides protected packages, and resolves the live protected set (launcher, settings, dialer, system ui, installer, permission controller, slate). |

`SlateStore` enforces three invariants: a habit name is never blank, focus cannot
start without at least one safe blocked package, and blocked packages cannot be
edited while a timer is active. the accessibility service repeats the protected
check at runtime so the persisted list is never the last line of defence.

## domain

| file | responsibility |
|:--|:--|
| `HabitProgress.kt` | week progress, completion, streaks, year counts and the heatmap grid. pure functions over epoch days. |
| `Focus.kt` | clamps a focus duration and answers whether a timer is still active. |

everything here is deterministic and covered by plain jvm unit tests.

## focus

| file | responsibility |
|:--|:--|
| `FocusAccess.kt` | reads the enabled accessibility services from secure settings and reports whether slate’s service is on. |
| `FocusAccessibilityService.kt` | collects the settings flow, keeps the current timer and blocked set in memory, and sends a blocked app home when it comes to the foreground. it re-checks protected packages before acting. |

the service never reads screen content. it observes the foreground package only,
which is the minimum needed to close a blocked app.

## reminder

| file | responsibility |
|:--|:--|
| `Reminders.kt` | schedules one unique one-time work request at the next chosen local time, then appends the following day after each run. |
| `ReminderWorker.kt` | shows the notification, then schedules the next occurrence. |
| `ReminderNotifier.kt` | creates the channel and builds the shared notification used by both the worker and the settings test action. |

## ui

| file | responsibility |
|:--|:--|
| `App.kt` | root composition: theme, lifecycle refresh, screen transitions, welcome overlay, back handling. |
| `SlateViewModel.kt` | owns `SlateData`, derives the ui state, exposes intent-style actions, tracks today across midnight. |
| `Navigation.kt` | the tiny `Screen` sealed interface and `ScreenSaver`; no navigation library. |
| `WelcomeScreen.kt` | first-run trail animation and the `begin` action. the screen is shown once, keyed by `welcome_seen`. |
| `HomeScreen.kt` | the week: habit rows, day squares, week navigation, add button. |
| `HabitScreen.kt` | habit detail: streak, target, past weeks, rename and delete. |
| `YearScreen.kt` | year heatmap, all habits or a single habit, with a month grid. |
| `SettingsScreen.kt` | appearance, reminder, focus entry, reset, version. |
| `FocusAppsScreen.kt` | app picker and duration choices; refuses edits while focus is active. |
| `AddHabitSheet.kt` | the bottom sheet for a new habit and its weekly target. |
| `BottomBar.kt` | the floating three-item dock. |
| `NotificationSupport.kt` | notification permission and channel helpers. |
| `components/` | shared primitives: trail grid, week strip, chevron, icons, controls and buttons. |
| `theme/` | monochrome palette, inter typography and the material 3 theme. |

state flows one way: the store emits `SlateData`, the view model derives
`SlateUiState`, screens render it, and user actions call back into the view model.
only `App.kt` remembers navigation and the selected week; both survive
configuration changes through `rememberSaveable`.

## util

| file | responsibility |
|:--|:--|
| `Dates.kt` | monday-based week boundaries, week coercion, and the english date formatter used across the grid. |

## build and delivery

- `app/build.gradle.kts` pins compile/target sdk 35, min sdk 26, jdk 17 and the
  version that reaches the apk.
- `.github/workflows/android.yml` runs `assembleDebug testDebugUnitTest lintDebug`
  on every push to `main` and uploads the debug apk as a workflow artifact.
- releases carry the same apk as a release asset. build output stays out of git.
