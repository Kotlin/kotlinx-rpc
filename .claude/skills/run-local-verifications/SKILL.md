---
name: run-local-verifications
description: Selects and runs the local checks for a kotlinx-rpc change, such as ABI, detekt, JPMS, artifact list, generated files, and compatibility tests. Use after a code change, before a commit or a PR. Not for TeamCity or GitHub Actions.
---

# Run local verifications

1. Get the changed files from `git status` and `git diff`.
2. Run each check of each matching row. Use `running_gradle_builds` for tasks and `running_gradle_tests` for tests.
3. Run the tests of the changed modules. For a `commonMain` change, `<module>:jvmTest` is sufficient.
   Run `jsTest`, `wasmJsTest`, or `nativeTest` only for a change in target-specific code.

A row with "commit the changes" has a generator, not a test: run it, check `git status`, and commit the files that changed.

| Change | Checks |
|---|---|
| Public API of a published module (`core/`, `krpc/`, `grpc/`, `protobuf/`, `utils/`) | `checkLegacyAbi` |
| Kotlin or Java source | `detekt` |
| Source or dependencies of a published JVM or KMP module, `commonMain` too | `:jpms-check:compileJava` |
| New or removed module, changed KMP targets, changed publication setup | `./scripts/validate_published_artifacts.sh -s`. `dumpPlatformTable --no-configuration-cache`, commit the changes. For a new module also `updateProperties`, `checkLegacyAbi`, `:jpms-check:compileJava`. |
| `gradle.properties` of the root or a module | `updateProperties`, commit the changes |
| `compiler-plugin/`, sources or build scripts | The `verify-compiler-plugin-compatibility` skill, and `:gradle-plugin:allVersionedFunctionalTests` |
| Compiler plugin setup in `gradle-plugin/` | `:gradle-plugin:allVersionedFunctionalTests` |
| `protoc-gen/` | All of: `:tests:protobuf-conformance:bufGenerateCommonMain`, `:protobuf:protobuf-wkt:bufGenerateCommonMain`, `./scripts/update_implicit_types.sh` (commit the changes of each), `:tests:protobuf-unittest:test`, `:tests:protobuf-conformance:jvmTest` |
| `protobuf/protobuf-wkt/` | `:protobuf:protobuf-wkt:bufGenerateCommonMain`, commit the changes |
| `krpc/` | `:tests:krpc-compatibility-tests:jvmTest`, `:tests:krpc-protocol-compatibility-tests:jvmTest` |
| `known_failures.txt` or `native_known_failures.txt` in `tests/protobuf-conformance/` | `:tests:protobuf-conformance:jvmTest` |
| npm dependencies | `kotlinUpgradeYarnLock`, `kotlinWasmUpgradeYarnLock` |
| Kotlin version | The `verify-compiler-plugin-compatibility` skill, both yarn lock tasks, `checkLegacyAbi`, `:jpms-check:compileJava`, all tests |
| `CHANGELOG.md` | `updateDocsChangelog`, commit the changes |

## Results

| Check | Facts |
|---|---|
| `checkLegacyAbi` | Compares the public API with the dumps, klib dumps too. `@InternalRpcApi` and `*.internal.**` are excluded. If the API change is intended, run `updateLegacyAbi` and commit the dumps. If not, revert the change. |
| `detekt` | Doesn't fail the build. Read the console output or `detekt/reports/`. Fix each new violation. Don't add to `detekt/baseline.xml`. |
| `:jpms-check:compileJava` | Compiles `jpms-check/src/main/java/module-info.java`, which requires all published modules. On a failure, check the `Automatic-Module-Name` of the module in the error. |
| `validate_published_artifacts.sh` | Compares the artifacts with the lists in `gradle/artifacts/`. If the change is intended, run it with `--dump -s`, read the diff, and commit. |
| `:gradle-plugin:allVersionedFunctionalTests` | Runs generated code with the `core` library on each Kotlin version. Run it from the root build. |
| Compiler plugin tests | `:compiler-plugin:allTests` runs all Kotlin versions, `:compiler-plugin:k2420Test` one version. To update the `.fir.txt` and `.ir.txt` files, read `references/golden-test-data.md` of the `verify-compiler-plugin-compatibility` skill. |
| `:tests:krpc-protocol-compatibility-tests:jvmTest` | Checks the wire format against the released versions 0.8, 0.9, and 0.10. A failure is a break of wire compatibility: stop and ask the user. |
| `:tests:krpc-compatibility-tests:jvmTest` | Checks the old API against the new implementation, with the `oldApi` and `newApi` source sets. |
| `:tests:protobuf-conformance:jvmTest` | Also builds and runs the native binary, so no native test task is necessary. Each removed known failure must pass, and each added one must fail. If a removed entry fails, the fix isn't complete or the wrong entry is removed. |
| Yarn lock tasks | If a build fails after them, delete `build/js/`, `build/wasm/`, and each `package-lock.json`, then run them again. |

The CI builds the Writerside docs. `update-doc` has the local build.
