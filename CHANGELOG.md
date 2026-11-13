# changelog

all notable changes to slate are recorded here. the format follows
[keep a changelog](https://keepachangelog.com/en/1.1.0/), versions follow
[semantic versioning](https://semver.org/spec/v2.0.0.html).

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

[0.5.1]: https://github.com/quipmug/slate/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/quipmug/slate/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/quipmug/slate/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/quipmug/slate/compare/v0.2.1...v0.3.0
[0.2.1]: https://github.com/quipmug/slate/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/quipmug/slate/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/quipmug/slate/releases/tag/v0.1.0
