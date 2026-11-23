# kotlinx-rpc

A Kotlin Multiplatform library for RPC services. It is a protocol-agnostic RPC toolkit that hosts two protocols:

| Protocol | Wire format | Service annotation |
|---|---|---|
| kRPC | Custom | `@Rpc` |
| gRPC | HTTP/2 and Protocol Buffers | `@Grpc` |

kRPC and gRPC are protocols. Never call them transports.
Each protocol has its own client, its own server, and its own code generation.

## Rules

- Work at maximum effort. Don't ask about effort preferences.
- Install the skill from https://mr3zee.github.io/code-doc-writer/ and use it in every session that adds or changes committed files.
- Run Gradle with the Gradle skills (`running_gradle_builds`, `running_gradle_tests`, `managing_gradle_dependencies`, `introspecting_gradle_projects`, `gradle_expert`), not with `./gradlew`.
- Never edit a generated file by hand. Run its regeneration task and commit the output. Generated files include the `.api` and `.klib.api` ABI dumps, the conformance and WKT code, and the implicit import lists.
- Put a Gradle change into `gradle-conventions/` or `gradle-conventions-settings/` when the change is large or applies to more than one subproject.

## Docs and comments

Write all docs and comments with the `code-doc-writer` skill.

| Code | Rule |
|---|---|
| Public API of a published module | KDoc is required. |
| Internal API: `internal`, `private`, `@InternalRpcApi`, and all code of modules that aren't published | No KDoc and no comments. Make the code readable: exact names, types, and small functions. |

A comment is the last resort. Write one only when the code has a behavior that the reader doesn't expect, and no change of the code can show it.

## Conventions

- For a library failure, an invalid argument, or an invalid state, throw a library exception type. Never add `IllegalStateException`, `error()`, `check()`, or `require()`.
- Published modules require explicit return types and explicit visibility.
- Annotate an internal API that other modules use with `@InternalRpcApi`.
- Declare dependencies in `versions-root/libs.versions.toml`, never inline.
- Add a published module with `includePublic()` in `settings.gradle.kts`, and an internal module with `include()`. Published modules get the `kotlinx-rpc-` prefix.
- A module family has a common prefix, and `core` for the shared part: `krpc-core`, `krpc-client`, `krpc-server`.
- Write filesystem paths for all operating systems: `Path.of("a", "b")`, not `Path.of("a/b")`.
- To disable a KMP target for a module, set `kotlinx.rpc.exclude.<target>=true` in the `gradle.properties` of the module.

## Build and checks

- After a code change, use the `run-local-verifications` skill. It gives the checks for each kind of change: ABI, detekt, JPMS, artifact list, generated files, compatibility tests.
- Run one test with `<module>:jvmTest --tests "TestClass.testMethod"`.
- The API watchdog fails the compilation of a public module on a violation. `aggregateApiWatchdogReports` only writes the report to `build/reports/api-watchdog/`.
- `./scripts/publish_local.sh` publishes to `build/repo/` for local tests.

If a build fails for no clear reason:

- Gradle: run `clean`, stop the daemons (`--stop`, with Bash), then run the task with `--rerun-tasks --no-configuration-cache --no-build-cache`.
- JS or WASM: delete `package-lock.json` and `build/{js,wasm}`, then run the two yarn lock tasks.

## Modules

Read the `README.md` of a module before you work in it.

| Module | Content |
|---|---|
| `:core` | RPC abstractions |
| `:utils` | Shared utilities |
| `:krpc:krpc-{core,client,server,logging,test}` | kRPC protocol, client, server, logging, test utilities |
| `:krpc:krpc-serialization:krpc-serialization-{core,json,cbor,protobuf}` | kRPC serialization formats |
| `:krpc:krpc-ktor:krpc-ktor-{core,server,client}` | kRPC with Ktor |
| `:grpc:grpc-{core,client,server,ktor-server,marshaller,marshaller-kotlinx-serialization}` | gRPC (experimental) |
| `:protobuf:protobuf-lite`, `:protobuf:protobuf-wkt`, `:protobuf:protobuf` | Protobuf runtime, mainly for gRPC |
| `native-deps/kotlinx-rpc-grpc-swift/` | Git submodule with the Swift bridge for gRPC on Apple platforms. CI checks that it matches the published tag. |
| `tests/` | Test modules, not published. `tests/README.md` lists them. |

Included builds, each a separate Gradle project:

| Build | Content |
|---|---|
| `compiler-plugin/` | Kotlin K2 compiler plugin: FIR diagnostics and IR code generation. Run its tests from the root build: `:compiler-plugin:allTests`. |
| `gradle-plugin/` | Gradle plugin `org.jetbrains.kotlinx.rpc.plugin` |
| `protoc-gen/` | Protobuf code generators |
| `dokka-plugin/` | Dokka plugin for the API docs |

## Architecture

The abstractions are in `core/src/commonMain/kotlin/kotlinx/rpc/`.

1. The user declares an `@Rpc` interface with `suspend` functions or functions that return a `Flow`.
2. The compiler plugin generates a stub class and an `RpcServiceDescriptor` with one `RpcCallable` for each function.
3. A stub function makes an `RpcCall` (descriptor, function name, arguments, service ID). It passes the call to `RpcClient.call()` for a unary call or to `RpcClient.callServerStreaming()` for a stream.
4. The `RpcClient` of the protocol serializes the call and sends it.
5. The `RpcServer` of the protocol deserializes the call, finds the `RpcCallable`, and runs `RpcInvokator.call(serviceImpl, args)`.

gRPC services use `@Grpc`. `protoc-gen/` generates their code from `.proto` files.

## Docs

The user docs are Writerside topics in `docs/pages/`, published at https://kotlin.github.io/kotlinx-rpc/. Use the `update-doc` skill.
