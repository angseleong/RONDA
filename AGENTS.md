# RONDA Project - Core System Rules

**You are working on RONDA (Real-time On-Device Detection Agent), an Android app to prevent social engineering scams.**
**You MUST adhere to the following rules unconditionally throughout this project.**

## Context Documents
Before making architectural decisions or starting a task, ALWAYS refer to the following documents located in the `docs/` folder:
- **`docs/PRD.md`**: The single source of truth for product requirements and scope.
- **`docs/ARCHITECTURE.md`**: Details the Firebase schema, component communication, and UI flows.
- **`docs/TODO.md`**: The active task list. Never work on anything outside the currently active task here.

## 1. Tech Stack & Environment
- **Platform**: Android native (Kotlin).
- **UI Framework**: Jetpack Compose (Material 3).
- **Async**: Kotlin Coroutines and Flow. Do NOT use RxJava.
- **Minimum SDK**: `minSdk 30`. Always ensure code is compatible with API 30+.
- **Dependencies**: Do not add external third-party libraries unnecessarily. Stick to standard AndroidX and Firebase.

## 2. STRICT Security Constraints (VIOLATION = DISQUALIFICATION)
- **NO SMS PERMISSIONS**: You MUST NEVER request `READ_SMS`, `RECEIVE_SMS`, or any other SMS-related permissions in the AndroidManifest. RONDA detects malware that uses these permissions, it does not use them itself.
- **NO ACCESSIBILITY SERVICE**: You MUST NEVER use or implement an `AccessibilityService`. Detection must be done purely via `PackageManager`.
- **Install Source Verification**: Use `PackageManager.getInstallSourceInfo()` (API 30+) to check where an app was installed from. Do NOT use the deprecated `getInstallerPackageName()`.
- **Permissions for Blocking**: The overlay feature relies solely on `SYSTEM_ALERT_WINDOW` and `PACKAGE_USAGE_STATS`.

## 3. Workflow & Scope
- **Follow TODO.md**: ONLY execute tasks that are currently active in `TODO.md`. Do NOT implement features that are not part of the active step.
- **Simplicity over Abstraction**: Choose the simplest implementation that fully meets the current requirements. Avoid speculative abstractions, configuration, and indirection. Do not preserve backward compatibility; remove obsolete paths instead.
- **Iterative Growth**: Grow the system in layers. Start from the smallest version that works end to end, and add each new capability on top of a product that already works.
- **Use Proven Patterns**: Adopt established Android conventions and standard APIs (e.g., standard `WorkManager`, `BroadcastReceiver`) rather than inventing custom approaches from scratch.
- **Dependencies**: Lean on the dependencies already in the project before writing your own implementation. Prefer established libraries.
- **No Scope Creep**: Do not create files, folders, UI screens, or logic outside of the current active task. This is a Proof of Concept (POC) with a tight deadline.
- **No Premature Refactoring**: Do not refactor code that is already working and meets the POC requirements. Speed is more important than perfect abstraction right now.

<!-- 
POST-POC ROADMAP RULES (To be uncommented ONLY AFTER the hackathon POC phase is over):
- Make architectural decisions for the long term. Do not accept a stopgap that only works for now and is meant to be replaced later.
- Keep components strictly modular and concerns clearly separated (e.g. Clean Architecture).
-->
