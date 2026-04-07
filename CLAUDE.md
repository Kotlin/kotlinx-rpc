# CLAUDE.md

kotlinx-rpc is a Kotlin Multiplatform library for building RPC services.
Version catalog: `versions-root/libs.versions.toml`

## Effort Level
Always operate at maximum effort level. Do not ask about effort preferences.

## Protocols

EXTREMELY IMPORTANT.
This library is a **protocol agnostic** library. It can be called a Kotlin RPC toolkit.
It hosts different protocols:
- kRPC (custom protocol)
- gRPC (HTTP/2 + Protocol Buffers)

They are not TRANSPORTS. Each protocol has its own client and server implementations, as well as code generation patterns.

## Build & Test

**IMPORTANT: Always use Gradle skills** (`running_gradle_builds`, `running_gradle_tests`, `managing_gradle_dependencies`, `introspecting_gradle_projects`, `gradle_expert`, etc.) instead of running `./gradlew` directly. 
Gradle skills provide structured feedback, failure diagnostics, and background orchestration that raw shell commands lack.

### Testing
Use the `running_gradle_tests` skill. Example tasks:
- `<module>:jvmTest --tests "TestClass.testMethod"` -- Single test
- `<module>:jvmTest` -- All JVM tests for a module

### Building
Use the `running_gradle_builds` skill. Example tasks:
- `<module>:build` -- Build a module
- `assemble` -- Assemble all modules

### Validation
Use the `running_gradle_builds` skill for these tasks:
- `checkLegacyAbi` -- Binary compatibility (formerly BCV apiCheck)
- `detekt` -- Static analysis (does NOT fail build, check console)
- `:jpms-check:compileJava` -- Java module system check
- `kotlinUpgradeYarnLock` -- Fix JS dependency locks
- `kotlinWasmUpgradeYarnLock` -- Fix WASM dependency locks

Non-Gradle validation:
```bash
./validatePublishedArtifacts.sh -s # Artifact validation (-v verbose, --dump update)
```

### Other specialized commands

```bash
./publishLocal.sh                  # Publish to build/repo/ for local testing
```

## Module Map

The most important modules have brief READMEs in their directories, read when you need to work with the module.

### Core (multiplatform)
- `:core` -- RPC abstractions (`@Rpc`, `RpcClient`, `RpcServer`, `RpcCall`, descriptors)
- `:utils` -- Shared utilities

### kRPC transport (multiplatform)
- `:krpc:krpc-core`, `:krpc:krpc-client`, `:krpc:krpc-server` -- Protocol, client, server
- `:krpc:krpc-logging`, `:krpc:krpc-test` -- Logging, test utilities
- `:krpc:krpc-serialization:krpc-serialization-{core,json,cbor,protobuf}` -- Serialization formats
- `:krpc:krpc-ktor:krpc-ktor-{core,server,client}` -- Ktor integration

### gRPC transport (experimental KMP)
- `:grpc:grpc-{core,client,server,ktor-server,marshaller,marshaller-kotlinx-serialization}`

### Protobuf (mainly used with gRPC)
- `:protobuf:protobuf-{api,wkt,core}`

### Included builds (separate Gradle projects)
- `compiler-plugin/` -- Kotlin K2 compiler plugin (FIR diagnostics + IR codegen)
- `gradle-plugin/` -- Gradle plugin (`org.jetbrains.kotlinx.rpc.plugin`)
- `protoc-gen/` -- Protobuf code generator
- `dokka-plugin/` -- Documentation plugin

### Tests (not published)
- `:tests:compiler-plugin-tests` -- Compiler plugin box/diagnostic tests (skipped for Kotlin master builds)
- `:tests:krpc-compatibility-tests`, `:tests:krpc-protocol-compatibility-tests` -- Protocol compat
- `:tests:protobuf-conformance` -- Protobuf spec conformance (not on Windows)
- `:tests:protobuf-unittest`, `:tests:grpc-test-server`, `:tests:test-protos`

## Architecture

### RPC Call Flow
1. User defines `@Rpc` interface with `suspend` methods and/or `Flow` return types
2. **Compiler plugin** generates stub class: each method creates `RpcCall(descriptor, methodName, args, serviceId)` and delegates to `RpcClient.call()` (unary) or `RpcClient.callServerStreaming()` (streaming)
3. **Client** serializes the call and sends over wire
4. **Server** deserializes, looks up `RpcServiceDescriptor`, invokes `RpcInvokator.call(serviceImpl, args)`
5. Response flows back through the same layers
6. Client and Server are bound to RPC protocols (kRPC or gRPC) 

### Key abstractions (`core/src/commonMain/kotlin/kotlinx/rpc/`)
- `@Rpc` annotation on interface -- triggers compiler plugin
- `RpcServiceDescriptor<T>` -- generated per-service metadata + stub factory
- `RpcCallable` -- per-method metadata + `RpcInvokator` (either `UnaryResponse` or `FlowResponse`)
- `RpcCall` -- single invocation payload: descriptor + method name + args + service ID
- `RpcClient` / `RpcServer` -- protocol-agnostic client and server interfaces

### kRPC protocol
Custom protocol.

### gRPC protocol
HTTP/2 + Protocol Buffers. Code generated from `.proto` files via `protoc-gen/`. Uses `@Grpc` annotation.

## Compiler Plugin

### Module structure
- `compiler-plugin-common` -- Shared between backend and k2
- `compiler-plugin-k2` -- FIR frontend: diagnostics, declaration generation
- `compiler-plugin-backend` -- IR backend: codegen (stub classes, descriptors)
- `compiler-plugin-cli` -- Entrypoint, combines backend and k2

## Conventions

- All published modules require **explicit return types and visibility**
- All public types require **KDoc**
- Internal APIs shared across modules: annotate with `@InternalRpcApi`
- Dependencies go in `versions-root/libs.versions.toml`, never inline
- New modules: `includePublic()` in `settings.gradle.kts` for published, `include()` for internal
- Published modules auto-prefixed with `kotlinx-rpc-`
- Module families: common prefix + `core` for shared (e.g., `krpc-core`, `krpc-client`, `krpc-server`)
- Filesystem paths: OS-agnostic only -- `Path.of("a", "b")` not `Path.of("a/b")`
- Disable KMP targets per module: `kotlinx.rpc.exclude.<target>=true` in module's `gradle.properties`
- Use [gradle-conventions](gradle-conventions) and [gradle-conventions-settings](gradle-conventions-settings) for all Gradle project changes when the change is either large or is intended for more than one subproject. Keep actual build files clean. 

## Testing

- Framework: JUnit 5 + kotlin-test + coroutines-test
- KMP source sets: `commonTest`, `jvmTest`, `jsTest`, `nativeTest`, etc.

## Documentation

Uses Writerside, lives in `docs/pages/`.

## Troubleshooting

- **Gradle**: Use the `running_gradle_builds` skill with `clean`, then `--stop` via Bash, then re-run the task with `--rerun-tasks --no-configuration-cache --no-build-cache` flags via the skill
- **JS/WASM**: delete `package-lock.json` + `build/{js,wasm}`, then use the `running_gradle_builds` skill to run `kotlinUpgradeYarnLock`/`kotlinWasmUpgradeYarnLock`

## References

- Documentation site: https://kotlin.github.io/kotlinx-rpc/
