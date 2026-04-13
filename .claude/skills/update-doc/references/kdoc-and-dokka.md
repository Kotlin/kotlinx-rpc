# KDoc and Dokka

## KDoc Conventions

All public types in published modules require KDoc (per CLAUDE.md conventions). The
project follows standard Kotlin KDoc patterns:

- First line: concise summary of what the declaration does
- `@param` for each parameter (including type parameters like `@param T`)
- `@return` for non-Unit return types
- `@see` for cross-references to related APIs
- Code examples in triple-backtick fenced blocks with `kotlin` language tag
- `@property` tags for data class properties when documented at class level

**Example pattern** (from `core/src/commonMain/kotlin/kotlinx/rpc/RpcClient.kt`):
```kotlin
/**
 * Performs a unary RPC call.
 *
 * @param T The expected return type of the call.
 * @param call The [RpcCall] describing the method to invoke.
 * @return The result of the RPC invocation.
 */
```

## Visibility Annotations

- `@InternalRpcApi` — marks internal APIs. The custom Dokka plugin
  (`dokka-plugin/src/main/kotlin/kotlinx/rpc/dokka/HideInternalRpcApiTransformer.kt`)
  strips these from generated API docs. No KDoc required.
- `@ExperimentalRpcApi` — marks experimental APIs. These still appear in docs and
  need KDoc.

## Dokka Build

Dokka is configured via convention plugins in `gradle-conventions/`:
- `conventions-dokka-spec.gradle.kts` — core Dokka config for public modules
- `conventions-dokka-public.gradle.kts` — applied to modules using `includePublic()`

Key settings:
- `failOnWarning = true` — KDoc warnings fail the build (this is the enforcement)
- `documentedVisibilities = {Public, Protected}`
- `suppressObviousFunctions = true`
- Source links point to GitHub at the tagged version

Generate API docs by running the `dokkaGenerate` Gradle task (use the
`running_gradle_builds` skill). Output goes to `docs/pages/api/` — don't edit by hand.

## Generated documentation files — Do not edit manually

Several documentation files are machine-generated. **Never edit them by hand** — run the
regeneration task and commit the output.

The custom Dokka plugin at `dokka-plugin/` also adds version badges to gRPC/protobuf
module pages, configured via `rpc-dokka-config.properties` (auto-generated at build time
with `coreVersion` and `grpcDevVersion`).
