# TeamCity Build Configuration IDs

Full catalog of build configuration IDs for the kotlinx-rpc project.

## Composite Builds

| Composite Build                   | What it runs                                  |
|-----------------------------------|-----------------------------------------------|
| `Build_kRPC_All`                  | Everything below                              |
| `Build_Code_All`                  | All platform code builds                      |
| `Build_Checks_All`                | ABI check, Detekt, JPMS, Artifacts, Changelog |
| `Build_kRPC_All_Compiler_Plugins` | Compiler plugin for all Kotlin versions       |
| `Build_kRPC_All_Gradle_Plugin`    | Gradle plugin build + tests                   |

## Platform-Specific Code Builds

| Build ID | Platforms | Agent |
|---|---|---|
| `Build_kRPC_Apple` | macOS, iOS, tvOS, watchOS | macOS |
| `Build_kRPC_Windows` | mingwX64 | Windows |
| `Build_kRPC_Java_JS_WASM_and_Linux` | JVM, JS, WASM, linuxX64/Arm64 | Linux |
| `Build_kRPC_StressTests` | JVM stress tests | Linux |

## Quality Checks

| Build ID | What |
|---|---|
| `Build_kRPC_CheckApi` | Binary compatibility (ABI) |
| `Build_kRPC_DetektVerify` | Static analysis |
| `Build_kRPC_JPMS` | Java module system |
| `Build_kRPC_ArtifactsValidation` | Published artifact list |
| `Build_kRPC_Changelog` | Changelog validation |
| `Build_kRPC_Samples` | Sample projects |
| `Build_kRPC_Protoc_Plugin_Test` | Protoc plugin tests |
| `Build_kRPC_Gradle_Plugin_Test` | Gradle plugin tests |
| `Build_kRPC_Gradle_Plugin_CheckApi` | Gradle plugin ABI |
| `Build_kRPC_Gradle_Plugin_DetektVerify` | Gradle plugin Detekt |

## Compiler Plugin Builds

Build IDs follow the pattern `Build_kRPC_Plugins_<version>_KotlinVersion_` where
`<version>` is the Kotlin version with dots replaced by underscores.

Example: Kotlin 2.3.20 -> `Build_kRPC_Plugins2_3_20_KotlinVersion_`

Check TeamCity for currently supported Kotlin versions.

### Kotlin Master

There is also a special version -- **Kotlin Master**: `Build_kRPC_Plugins_Kotlin_Master`
downloads the latest Kotlin master artifacts and builds only the compiler plugin against
them. Use this to verify forward compatibility with unreleased Kotlin versions.

Differences between this and `Build_Compiler_Plugins_Latest_Kotlin_Master`:
- `Build_kRPC_Plugins_Kotlin_Master` is not scheduled, `Build_Compiler_Plugins_Latest_Kotlin_Master` is scheduled.
- `Build_kRPC_Plugins_Kotlin_Master` builds only the compiler plugin, `Build_Compiler_Plugins_Latest_Kotlin_Master` builds the full project.

## Special / Scheduled Builds

| Build ID                                        | Purpose                                        | Trigger            |
|-------------------------------------------------|------------------------------------------------|--------------------|
| `Build_Compiler_Plugins_Latest_Public_Unstable` | Full project against latest Kotlin unstable     | Nightly            |
| `Build_Compiler_Plugins_Latest_Kotlin_Master`   | Full project against Kotlin master              | Daily 10:00 Berlin |
| `Build_kRPC_Plugins_Kotlin_Master`              | Compiler plugin only against Kotlin master      | On demand          |
| `Build_kRPC_Plugins_For_IDE`                    | IDE plugin artifacts                            | On demand          |
| `Build_kRPC_New_Ide_Versions_Checker`           | Detect new IntelliJ/Android Studio              | Every 20 mins      |
