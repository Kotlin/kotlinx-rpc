# Builds

## Builds for a change

Get the changed paths (`git diff --name-only main...HEAD`), and start each build of each matching row:
`teamcity run start <build-id> --branch <branch>`.

| Change | Build ID | Runs |
|---|---|---|
| `core/`, `utils/`, `krpc/`, `protobuf/`, `bom/`, `grpc/` | `Build_kRPC_Java_JS_WASM_and_Linux` | JVM, JS, WASM, and Linux targets. Sufficient for a `commonMain` change outside `grpc/`. |
| `grpc/`, or native code or `expect` declarations in the other paths | `Build_kRPC_Apple` | macOS, iOS, tvOS, watchOS |
| Native code or `expect` declarations in the paths without `grpc/` | `Build_kRPC_Windows` | mingwX64 |
| kRPC wire format, serialization, or concurrency | `Build_kRPC_StressTests` | JVM stress tests |
| Public API of a published module | `Build_kRPC_CheckApi` | ABI dumps |
| Kotlin or Java source | `Build_kRPC_DetektVerify` | Detekt. Doesn't fail, so read the output. |
| Packages of a published JVM or KMP module | `Build_kRPC_JPMS` | Java module system |
| New or removed module, changed KMP targets | `Build_kRPC_ArtifactsValidation`, `Build_kRPC_CheckApi`, `Build_kRPC_JPMS`, and the three platform builds | |
| Changelog | `Build_kRPC_Changelog` | Changelog check. GitHub Actions check all other docs. |
| `samples/` | `Build_kRPC_Samples` | Sample projects |
| `compiler-plugin/` | `Build_kRPC_Compiler_Plugin` | Compiles and tests the plugin on all supported Kotlin and IDE versions (`:compiler-plugin:allTests`) |
| `compiler-plugin/` | `Build_kRPC_Gradle_Plugin_Test` | Functional tests on each Kotlin version |
| `compiler-plugin/` | `Build_kRPC_Plugins_Kotlin_Master` | Builds only the compiler plugin against the latest Kotlin master artifacts |
| `gradle-plugin/` | `Build_kRPC_Gradle_Plugin_Test` | Gradle plugin tests |
| Public API of `gradle-plugin/` | `Build_kRPC_Gradle_Plugin_CheckApi` | ABI of the Gradle plugin |
| Kotlin source of `gradle-plugin/` | `Build_kRPC_Gradle_Plugin_DetektVerify` | Detekt for the Gradle plugin |
| `protoc-gen/` | `Build_kRPC_Protoc_Plugin_Test`, `Build_kRPC_Java_JS_WASM_and_Linux` | Generator tests, and the protobuf runtime with the generated code |
| Kotlin version | `Build_kRPC_Compiler_Plugin`, the three platform builds, `Build_kRPC_CheckApi` | |

## Composite builds

Prefer the builds above. They give a result faster.

| Build ID | Runs |
|---|---|
| `Build_kRPC_All` | All builds below |
| `Build_Code_All` | The platform builds and the stress tests |
| `Build_Checks_All` | ABI, Detekt, JPMS, artifacts, changelog |
| `Build_kRPC_All_Compiler_Plugins` | `Build_kRPC_Compiler_Plugin` |
| `Build_kRPC_All_Gradle_Plugin` | Gradle plugin tests, ABI, and Detekt |

## Other builds

| Build ID | Runs | Trigger |
|---|---|---|
| `Build_Compiler_Plugins_Latest_Public_Unstable` | The whole project against the latest unstable Kotlin | Each night |
| `Build_Compiler_Plugins_Latest_Kotlin_Master` | The whole project against Kotlin master | Each day at 10:00 Berlin time |
| `Build_kRPC_Plugins_Kotlin_Master` | Only the compiler plugin against Kotlin master | On demand |
| `Build_kRPC_Plugins_For_IDE` | The IDE plugin artifacts | On demand |
| `Build_kRPC_New_Ide_Versions_Checker` | Finds new IntelliJ IDEA and Android Studio versions | Each 20 minutes |
