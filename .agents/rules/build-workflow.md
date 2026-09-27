---
trigger: always_on
description: Project-specific rule for the "build" command in the Bodhi Merit (aistudeobodi tree) workspace
---

# Project Build Rule ("build")

Whenever the user says **"build"** (or "build now") in this project (`MeritTree` / `aistudeobodi tree`):
1. Automatically check GitHub for updates by running `git pull origin main`.
2. If there are new commits (or if a build is requested), pull the latest changes into the workspace, run `.\gradlew.bat assembleDebug`, and copy `app\build\outputs\apk\debug\app-debug.apk` to `.\bodhi-merit.apk` in the workspace root folder.
3. Report the pulled commit(s) and link to the updated `bodhi-merit.apk` in the root directory.
