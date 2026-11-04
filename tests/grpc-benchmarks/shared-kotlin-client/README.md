# Shared Kotlin benchmark client

This directory contains the source-only benchmark harness used by both Kotlin
gRPC benchmark clients. It is intentionally not a Gradle project: each client
compiles these sources in its own build against either the current repository
modules or the published legacy artifacts.

The client-specific adapters are:

- `../kotlinx-rpc-client/src/commonMain` for the current generated service API;
- `../legacy-ios-client/src/commonMain` for the legacy generated service API.

Keep common sources compatible with the Kotlin version pinned by the legacy
build. Shared tests are executed by the current client's JVM test target, while
both iOS release link tasks verify source compatibility with their respective
client and compiler-plugin versions.
