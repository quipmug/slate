# architecture

slate is one android module, one activity and one view model. the code is split
by responsibility, not by screen: pure rules live in `data` and `domain`, android
integration lives at the edges, and every composable is stateless enough to
preview. hilt builds the object graph, navigation compose owns the routes, room
stores habits and datastore keeps settings.

```
app/src/main/java/com/slate/weekly
├── MainActivity.kt              edge-to-edge host, @AndroidEntryPoint
├── SlateApp.kt                  @HiltAndroidApp, workmanager configuration
├── data/                        room, datastore, migration and rules around them
├── domain/                      pure functions, no android imports
├── focus/                       accessibility service and its status helper
├── reminder/                    workmanager reminder and notifications
├── ui/                          screens, view model, navigation, theme
└── util/                        date helpers used by the grid
```

## data

| file | responsibility |
|:--|:--|
| `Habit.kt` | the immutable habit record used by the ui: id, name, weekly target, creation day, checked days. |
| `HabitEntity.kt` | the room row for a habit. |
| `HabitCheck.kt` | the room row for one checked day, keyed by habit id and epoch day, with a cascade delete. |
| `HabitDao.kt` | room queries and transactions: habits with checks, upsert, toggle, rename, target, delete and reset. |
| `SlateDatabase.kt` | the room database, version 1, with exported schemas under `app/schemas`. |
| `DatabaseModule.kt` | hilt providers for the database and dao. |
| `HabitRules.kt` | pure validation and editing rules: clean names and targets, toggle semantics, normalisation and de-duplication. |
| `Settings.kt` | `SlateSettings`, `SlateData`, `ThemeMode` and the persisted preference keys’ shape. |
| `SlateStore.kt` | the only writer. migrates legacy datastore habits into room once, combines room habits with the settings flow, and refuses unsafe focus changes. |
| `InstalledApps.kt` | loads launchable apps, hides protected packages, and resolves the live protected set (launcher, settings, dialer, system ui, installer, permission controller, slate). |

`SlateStore` enforces three invariants: a habit name is never blank, focus cannot
start without at least one safe blocked package, and blocked packages cannot be
edited while a timer is active. the accessibility service repeats the protected
check at runtime and keeps collecting the settings flow, so both focus start and
edits reach the service without a restart.

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
| `App.kt` | root composition: theme, navigation host, lifecycle refresh, welcome overlay, add sheet and back handling. |
| `SlateViewModel.kt` | an `@HiltViewModel` that combines `SlateData` and today into the ui state, exposes intent-style actions and refreshes the day on resume. |
| `Navigation.kt` | navigation compose routes (`home`, `detail/{habitId}`, `year`, `settings`, `focus_apps`) and the `LocalDate` saver. |
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
navigation is a `NavHost`; only `App.kt` remembers the selected week and the add
sheet state, and both survive configuration changes through `rememberSaveable`.

## util

| file | responsibility |
|:--|:--|
| `Dates.kt` | monday-based week boundaries, week coercion, and the english date formatter used across the grid. |

## build and delivery

- `gradle/libs.versions.toml` is the single source for versions. the pinned
  androidx stack stays on agp 8.7.3 / compile sdk 35, so hilt navigation stays on
  `1.2.0` and room on `2.7.x` until the toolchain moves.
- `app/build.gradle.kts` pins compile/target sdk 35, min sdk 26, jdk 17 and the
  version that reaches the apk. ksp generates hilt and room code, and room
  schemas are exported to `app/schemas`.
- `.github/workflows/android.yml` runs `assembleDebug testDebugUnitTest lintDebug`
  on every push to `main` and uploads the debug apk as a workflow artifact.
- pushing a `v*` tag runs the same checks and publishes a github release with
  `slate-debug.apk`, its sha256 file and `docs/releases/<tag>.md` as the body.
  build output stays out of git.
