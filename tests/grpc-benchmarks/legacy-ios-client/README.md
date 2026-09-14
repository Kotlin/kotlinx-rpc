# Legacy iOS benchmark client

This is a standalone Gradle build for benchmarking the published legacy
kotlinx-rpc gRPC client on iOS. It intentionally remains outside the repository
root `settings.gradle.kts` so that the legacy and current implementations cannot
be substituted for one another or linked into the same native executable.

The build uses:

- Kotlin `2.3.0`;
- `org.jetbrains.kotlinx.rpc.plugin:0.11.0-grpc-189`;
- `org.jetbrains.kotlinx:kotlinx-rpc-grpc-client:0.11.0-grpc-189`;
- the canonical protos from `../protos/src/commonMain/proto`.

Compile the Apple Silicon simulator executable with:

```shell
./gradlew linkReleaseExecutableIosSimulatorArm64
```
