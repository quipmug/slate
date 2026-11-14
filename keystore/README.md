# signing key

`slate-debug.jks` is a public debug keystore committed on purpose. it signs both
debug and release builds so every apk built from this repository can update the
previous one. it is not a secret and it is not a play store key.

- keystore: `keystore/slate-debug.jks`
- store password: `android`
- key alias: `androiddebugkey`
- key password: `android`
- certificate SHA-256:
  `5a698a3ec45b557865ae175806d377aff30627952b1318bdf3ec59acc7e5d474`

the github workflow checks the built apk against that fingerprint. keep the
keystore if you fork the project, or replace it everywhere at once and expect
one reinstall when the key changes.
