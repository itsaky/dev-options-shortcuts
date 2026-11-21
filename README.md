# DevToggles

Home screen widgets and Quick Settings tiles for Android developer options.

Saves you the trip to Settings > Developer Options for the toggles you use
most. This is a personal-use project, but you're welcome to build it yourself
and use it.

## Features

- Home screen widgets (Jetpack Glance) for each toggle and an all-in-one list
- Quick Settings tiles for one-tap toggling from the notification shade
- Copies `adb connect <address>` to the clipboard when enabling wireless
  debugging -- pair with KDE Connect or similar to sync the clipboard to your
  workstation and just paste + enter to connect
- Discovers the WADB address via mDNS (NSD) so the displayed IP:port is always
  current

## Building from source

Install JDK 21 and the Android SDK (build-tools 37.0.0, platform android-37),
set `ANDROID_HOME`, and run:

```
./gradlew assembleRelease
```

The signed APK is at `app/build/outputs/apk/release/app-release.apk`.

A Nix flake is included that pins the SDK, JDK, and all host tooling. If you
have Nix with flakes enabled:

```
nix develop --command ./gradlew assembleRelease
```

## Reproducible builds

Release builds are reproducible. CI builds the APK twice on independent runners
and verifies identical SHA-256 checksums. A pinned debug signing key and the Nix
flake lock ensure the toolchain and signing are deterministic across builds.

## Setup

The app needs `WRITE_SECURE_SETTINGS` to toggle developer options. Grant it
via ADB:

```
adb shell pm grant com.itsaky.devtoggles android.permission.WRITE_SECURE_SETTINGS
```

Or grant it from the app itself via Shizuku or root.

## License

```
Copyright (C) 2026 Akash Yadav

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3 as
published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
