# Remote Verification Table

Map **what changed** to the **minimal set of TeamCity builds** to trigger.
Prefer targeted builds over composite `_All` configurations.

## How to use

1. Identify changed paths (e.g., `git diff --name-only main...HEAD`)
2. Match against rows below -- multiple rows can match, union all build IDs
3. Trigger with: `teamcity run start <BUILD_ID> --branch <branch>`
4. For composite builds that aggregate others, use `--watch` or poll with `teamcity run view`

## Decision Table

### Platform code builds (run tests + compile across targets)

| What changed | Build ID | What it covers |
|---|---|---|
| `core/`, `utils/`, `krpc/`, `protobuf/`, `bom/` | `Build_kRPC_Java_JS_WASM_and_Linux` | JVM, JS, WASM, linuxX64, linuxArm64 |
| `core/`, `utils/`, `krpc/`, `protobuf/`, `bom/` | `Build_kRPC_Apple` | macOS, iOS, tvOS, watchOS |
| `core/`, `utils/`, `krpc/`, `protobuf/`, `bom/` | `Build_kRPC_Windows` | mingwX64 |
| `grpc/` | `Build_kRPC_Java_JS_WASM_and_Linux` | gRPC JVM + KMP targets |
| `grpc/` | `Build_kRPC_Apple` | gRPC native targets |
| `krpc/` wire format, serialization, concurrency | `Build_kRPC_StressTests` | JVM stress tests for kRPC |

**Guidance**: If the change is `commonMain`-only, `Build_kRPC_Java_JS_WASM_and_Linux` alone
often suffices. Add Apple/Windows only when touching native-specific code, expect declarations,
or want full coverage before merge.

### Quality checks

| What changed | Build ID | What it checks |
|---|---|---|
| Public API signatures in any published module | `Build_kRPC_CheckApi` | Binary compatibility (ABI dumps) |
| Any Kotlin/Java source | `Build_kRPC_DetektVerify` | Static analysis (won't fail build -- check output) |
| Package structure in published JVM/KMP modules | `Build_kRPC_JPMS` | Java module system exports |
| New/removed modules, KMP target changes | `Build_kRPC_ArtifactsValidation` | Published artifact list consistency |
| `docs/pages/` changelog content | `Build_kRPC_Changelog` | Changelog validation |
| Sample projects under `samples/` | `Build_kRPC_Samples` | Sample compilation/tests |

### Compiler plugin

| What changed | Build ID | What it covers |
|---|---|---|
| `compiler-plugin/` (any source or template) | `Build_kRPC_Plugins<V>_KotlinVersion_` | Single Kotlin version (fastest feedback) |
| `compiler-plugin/` (any source or template) | `Build_kRPC_All_Compiler_Plugins` | All 11 Kotlin versions (use after single-version passes) |
| `compiler-plugin/` (any source or template) | `Build_kRPC_Plugins_Kotlin_Master` | Compiler plugin against Kotlin Master |

**Version ID pattern**: dots replaced by underscores, e.g.:
- Kotlin 2.3.20 -> `Build_kRPC_Plugins2_3_20_KotlinVersion_`
- Kotlin 2.1.0  -> `Build_kRPC_Plugins2_1_0_KotlinVersion_`

**Tip**: Start with the current project Kotlin version build. If it passes and the change
doesn't touch CSM templates, skip the `_All` composite. If CSM templates changed,
`Build_kRPC_All_Compiler_Plugins` is required.

**Kotlin Master**: `Build_kRPC_Plugins_Kotlin_Master` downloads the latest Kotlin master
artifacts and builds only the compiler plugin against them. Use this to verify forward
compatibility with unreleased Kotlin versions.

### Gradle plugin

| What changed | Build ID | What it checks |
|---|---|---|
| `gradle-plugin/` sources | `Build_kRPC_Gradle_Plugin_Test` | Gradle plugin functional tests |
| `gradle-plugin/` public API | `Build_kRPC_Gradle_Plugin_CheckApi` | Gradle plugin ABI compatibility |
| `gradle-plugin/` Kotlin sources | `Build_kRPC_Gradle_Plugin_DetektVerify` | Gradle plugin static analysis |

**Shortcut**: If the change is non-trivial, `Build_kRPC_All_Gradle_Plugin` runs all three.
For a one-line fix, just `Build_kRPC_Gradle_Plugin_Test` is sufficient.

### Protoc generators

| What changed | Build ID | What it checks |
|---|---|---|
| `protoc-gen/` sources | `Build_kRPC_Protoc_Plugin_Test` | Protoc plugin codegen tests |
| `protoc-gen/` + downstream runtime | `Build_kRPC_Java_JS_WASM_and_Linux` | Integration with protobuf runtime |

## Common Scenarios

### "I fixed a bug in `krpc/krpc-client`"
```
Build_kRPC_Java_JS_WASM_and_Linux   # primary platform tests
Build_kRPC_CheckApi                  # if public API touched
Build_kRPC_StressTests               # if concurrency-related
```

### "I changed the compiler plugin IR backend"
```
Build_kRPC_Plugins2_3_20_KotlinVersion_   # current version first
# if passes and CSM templates changed:
Build_kRPC_All_Compiler_Plugins            # all versions
```

### "I added a new published module"
```
Build_kRPC_ArtifactsValidation       # artifact list
Build_kRPC_CheckApi                  # ABI dumps
Build_kRPC_JPMS                      # JPMS exports
Build_kRPC_Java_JS_WASM_and_Linux    # builds + tests
Build_kRPC_Apple                     # native targets
Build_kRPC_Windows                   # mingw target
```

### "I modified protoc-gen code generation"
```
Build_kRPC_Protoc_Plugin_Test        # protoc codegen tests
Build_kRPC_Java_JS_WASM_and_Linux    # downstream integration
Build_kRPC_CheckApi                  # if generated API changed
```

### "I changed Gradle plugin configuration"
```
Build_kRPC_Gradle_Plugin_Test        # functional tests
Build_kRPC_Gradle_Plugin_CheckApi    # if public API changed
```

### "I updated the Kotlin version"
```
Build_kRPC_All_Compiler_Plugins      # all compiler plugin versions
Build_kRPC_Java_JS_WASM_and_Linux    # runtime compilation
Build_kRPC_Apple                     # native compilation
Build_kRPC_Windows                   # mingw compilation
Build_kRPC_CheckApi                  # ABI may shift
```

### "I only changed documentation"
```
Build_kRPC_Changelog                 # if changelog touched
# No other TC builds needed -- docs are verified by GitHub Actions
```

### "I changed gRPC modules"
```
Build_kRPC_Java_JS_WASM_and_Linux    # primary gRPC targets
Build_kRPC_Apple                     # native gRPC interop
Build_kRPC_CheckApi                  # if public API touched
```

## Composite `_All` Builds Reference (deprioritized)

These run broad sweeps. **Never use**. 
Prefer targeted builds above for faster feedback.

| Build ID | What it aggregates |
|---|---|
| `Build_kRPC_All` | Everything: all code, checks, compiler plugins, gradle plugin |
| `Build_Code_All` | All platform code builds (Java/JS/WASM/Linux + Apple + Windows + StressTests) |
| `Build_Checks_All` | All quality checks (ABI + Detekt + JPMS + Artifacts + Changelog) |
| `Build_kRPC_All_Compiler_Plugins` | Compiler plugin for all 11 Kotlin versions |
| `Build_kRPC_All_Gradle_Plugin` | Gradle plugin build + test + ABI + Detekt |
