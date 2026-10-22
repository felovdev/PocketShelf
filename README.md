# PocketShelf 📚

**Your stuff, always handy.** A lightweight, offline-first Android shelf for links, snippets, addresses, commands, and notes.

[![Build APK](https://github.com/felovdev/app/actions/workflows/android.yml/badge.svg)](https://github.com/felovdev/app/actions/workflows/android.yml)

## Features
- Save useful text and links with a title and tags.
- Search saved items instantly.
- Share text into PocketShelf from other Android apps.
- Data stays on-device in a local SQLite database; no account, ads, analytics, or network permission.
- Long-press an item to delete it.

## Get the APK
Open **Actions → Android APK → latest successful run → Artifacts → PocketShelf-debug-apk**. The workflow builds a debug APK from source on GitHub Actions.

> This is an early MVP. Backups, import/export, and encrypted storage are planned, not yet implemented.

## Build locally
Requirements: JDK 17, Android SDK platform 35, Build Tools 35.0.0, Gradle 8.9.

```bash
gradle assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Privacy
PocketShelf stores content in app-private local storage. It does not request internet access. Uninstalling the app removes its local database, so export/backup should be added before storing anything irreplaceable.

## License
MIT — see [LICENSE](LICENSE).
