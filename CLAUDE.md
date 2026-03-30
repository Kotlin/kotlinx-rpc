# CLAUDE.md

kotlinx-rpc is a JetBrains Kotlin Multiplatform library for async RPC services. Experimental.
Version catalog: `versions-root/libs.versions.toml`

## Build & Test

```bash
./gradlew <module>:jvmTest --tests "TestClass.testMethod"  # Single test
```

### Validation

```bash
./gradlew checkLegacyAbi          # Binary compatibility (formerly BCV apiCheck)
./gradlew detekt                  # Static analysis (does NOT fail build, check console)
./gradlew :jpms-check:compileJava # Java module system check
./validatePublishedArtifacts.sh -s # Artifact validation (-v verbose, --dump update)
```

### Specialized commands

```bash
./publishLocal.sh                  # Publish to build/repo/ for local testing
./gradlew kotlinUpgradeYarnLock    # Fix JS dependency locks
./gradlew kotlinWasmUpgradeYarnLock # Fix WASM dependency locks
./gradlew htmlDependencyReport     # Debug dependency issues
```

## Module Map

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
- Internal shared APIs: annotate with `@InternalRpcApi`
- Dependencies go in `versions-root/libs.versions.toml`, never inline
- New modules: `includePublic()` in `settings.gradle.kts` for published, `include()` for internal
- Published modules auto-prefixed with `kotlinx-rpc-`
- Module families: common prefix + `core` for shared (e.g., `krpc-core`, `krpc-client`, `krpc-server`)
- Filesystem paths: OS-agnostic only -- `Path.of("a", "b")` not `Path.of("a/b")`
- Disable KMP targets per module: `kotlinx.rpc.exclude.<target>=true` in module's `gradle.properties`

## Testing

- Framework: JUnit 5 + kotlin-test + coroutines-test
- KMP source sets: `commonTest`, `jvmTest`, `jsTest`, `nativeTest`, etc.

## Documentation

Uses Writerside, lives in `docs/pages/`.

## Troubleshooting

- **Gradle**: `./gradlew clean` -> `./gradlew --stop` -> `./gradlew <task> --rerun-tasks --no-configuration-cache --no-build-cache`
- **JS/WASM**: delete `package-lock.json` + `build/{js,wasm}`, then run `kotlinUpgradeYarnLock`/`kotlinWasmUpgradeYarnLock`
- Full reference: `docs/environment.md`

## References

- Full dev environment guide: `docs/environment.md`
- Documentation site: https://kotlin.github.io/kotlinx-rpc/
