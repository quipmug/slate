# changelog

all notable changes to slate are recorded here. the format follows
[keep a changelog](https://keepachangelog.com/en/1.1.0/), versions follow
[semantic versioning](https://semver.org/spec/v2.0.0.html).

## [0.6.1] — 2026-11-15

### fixed

- release apks are now signed with the committed `keystore/slate-debug.jks`, so
  every future release can install over the previous one. `0.6.0` and older used
  a throwaway ci debug key, so this release is a one-time reinstall.
- the floating dock is hidden on habit detail and the blocked-app picker again,
  so it no longer covers the bottom of those screens.
- bottom-tab taps pop to the start destination and restore state, so switching
  tabs no longer stacks duplicate screens or breaks the back button.
- the ui rolls over at local midnight while the app stays open, instead of
  waiting for the next resume to update “today”.
- focus settings refresh while a timer is active, so the screen leaves the
  running state when the timer expires.
- opening the app no longer replaces a reminder that is already waiting to fire.

### changed

- build plugins now come from the version catalog, including hilt `2.52` and ksp
  `2.0.21-1.0.28`, so the plugin and compiler versions cannot drift apart again.
- ci checks the apk signer fingerprint after every build.
- version `0.6.1`, version code `9`.

## [0.6.0] — 2026-11-14

### changed

- architecture refresh: hilt dependency injection, navigation compose and room
  for habits. the app behaves the same, but state and navigation have one owner
  again.
- habits are migrated once from the old datastore json into room, so an update
  keeps existing history.
- the focus accessibility service now collects settings live, so starting a timer
  or editing blocked apps takes effect without restarting the service.
- reminder work is created through hilt's worker factory instead of relying on
  the default workmanager initializer.
- removed the settings polling loop and the view model's midnight loop; today is
  refreshed on resume and the ui observes state directly.
- unit tests moved to junit 5 and the build aligns all hilt, room and lifecycle
  versions with the version catalog. ci now gets past aar metadata again.
- release builds enable r8 minification and keep hilt, room, navigation, compose
  and workmanager entry points.

### fixed

- aligned `androidx.hilt:hilt-navigation-compose` and `hilt-work` with agp 8.7.3
  and compile sdk 35. the previous `1.4.0` artifacts required agp 9.1 and api 37,
  which failed every build.
- restored the missing `first`, `collect` and `@Inject` imports, the correct
  `NavController` package and the activity view-model delegate.
- removed the broken hilt test dependency and the unused test annotation
  processor.
- `allowBackup=false` and the backup rules no longer contradict each other.

### release

- version `0.6.0`, version code `8`.

## [0.5.1] — 2026-11-13

### fixed

- the first-run `begin` button now dismisses the welcome screen. an ancestor
  pointer interceptor used to cancel the tap gesture, so the ripple appeared
  but nothing happened.
- `begin` is enabled immediately instead of waiting for the welcome animation.
- the focus blocker re-checks the live protected-package list immediately before
  sending an app home, so a stale or edited blocked list can never trap the
  phone.
- expanded the protected list with settings, dialer, launcher, system ui,
  installer and permission-controller packages from common android vendors.
- added package-visibility queries for home, settings and dialer intents so the
  protected list resolves correctly on more devices.
- removed the doubled “until” in the focus confirmation copy.

### changed

- version `0.5.1`, version code `7`.

## [0.5.0] — 2026-11-12

### added

- strict focus: choose apps, pick 1h, 4h, 8h or 24h, and the selected apps are
  closed whenever they open until the timer expires on its own.
- an accessibility-service blocker that only reads the foreground package name.
- focus confirmation, a running timer, and a protected-package safety list.

### changed

- starting focus or editing blocked apps is refused while a timer is active.
- version `0.5.0`, version code `6`.

## [0.4.0] — 2026-11-12

### added

- a first-run welcome that fills a small trail grid, then stays out of the way.
- safer date handling around week boundaries and midnight.

### changed

- quieter motion across week changes, day fills and screen transitions.
- version `0.4.0`, version code `5`.

## [0.3.0] — 2026-11-11

### added

- the rounded-square trail grid and the floating bottom dock.
- habit strips, hairline cards and a one-canvas year heatmap.

### changed

- version `0.3.0`, version code `4`.

## [0.2.1] — 2026-11-11

### changed

- cleaner module boundaries and a more restrained visual pass.
- version `0.2.1`, version code `3`.

## [0.2.0] — 2026-11-11

### added

- week navigation, editable past weeks, weekly targets, streaks and the year view.
- light, dark and auto themes; local datastore persistence.

### changed

- version `0.2.0`, version code `2`.

## [0.1.0] — 2026-11-11

### added

- the first minimal weekly habit tracker: one row per habit, one square per day.
- version `0.1.0`, version code `1`.

[0.6.1]: https://github.com/quipmug/slate/compare/v0.6.0...v0.6.1
[0.6.0]: https://github.com/quipmug/slate/compare/v0.5.1...v0.6.0
[0.5.1]: https://github.com/quipmug/slate/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/quipmug/slate/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/quipmug/slate/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/quipmug/slate/compare/v0.2.1...v0.3.0
[0.2.1]: https://github.com/quipmug/slate/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/quipmug/slate/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/quipmug/slate/releases/tag/v0.1.0
