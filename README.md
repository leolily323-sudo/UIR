# UIR (Universal Identity Runtime)

UIR is a phone-first, local-first AI-agent runtime project. Development happens
in GitHub Codespaces, and APK builds happen through GitHub Actions.

The target runtime device is a Samsung Galaxy A53.

Milestone 1 focuses on local identity, memory, diagnostics, and recovery. The
current app stores identity, memories, tasks, event logs, and snapshots locally
with Room. Saved snapshots can be viewed and individually restored; restoring
replaces the current identity, memories, and tasks with the snapshot contents
while preserving the snapshot itself.

## Build

Build the debug APK with:

```shell
./gradlew assembleDebug
```

GitHub Actions runs this build on every push to `main` and uploads the APK as
the `uir-debug-apk` workflow artifact.
