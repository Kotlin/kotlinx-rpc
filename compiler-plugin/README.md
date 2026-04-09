# Overview

This is the Kotlin compiler plugin for kotlinx-rpc. 
It runs in two phases: FIR (frontend diagnostics + declaration generation, uses K2) and IR (backend codegen).

## Build & Test

Use `assemble` task to do the basic check of the plugin.
Use `verify-compiler-plugin-compatibility` skill to check compilation on different versions of Kotlin.

Use `running_gradle_tests` skill for tests:
- All compiler plugin tests: `:tests:compiler-plugin-tests:test`
- Single box test (codegen): `:tests:compiler-plugin-tests:test --tests "*BoxTestGenerated.testSimple"`
- Single diagnostic test: `:tests:compiler-plugin-tests:test --tests "*DiagnosticTestGenerated.testRpcService"`
- Update expected `.fir.txt` / `.fir.ir.txt` outputs after changing codegen: `:tests:compiler-plugin-tests:test -Dkotlin.test.update.test.data=true`

Test data lives in `tests/compiler-plugin-tests/src/testData/`:
- `box/*.kt` -- Black-box codegen tests. Must define `fun box(): String` returning `"OK"`. Paired with `.fir.txt` and `.fir.ir.txt` expected outputs.
- `diagnostics/*.kt` -- FIR diagnostic tests. Use `<!DIAGNOSTIC_NAME!>code<!>` markers for expected errors. Paired with `.fir.txt` expected output.

Directives: `// RUN_PIPELINE_TILL: BACKEND` (full pipeline) or `// RUN_PIPELINE_TILL: FRONTEND` (FIR only).

## Generation References (detailed)

Protocol-specific codegen and diagnostics details live alongside each protocol module:
- [kRPC codegen](../krpc/codegen.md) -- stub class, companion descriptor, invokators
- [gRPC codegen](../grpc/codegen.md) -- gRPC-specific additions: `delegate()`, `MethodDescriptor`, streaming types
- [Protobuf codegen](../protobuf/codegen.md) -- `Builder` interface, `@WithProtoDescriptor`/`@WithGrpcMarshaller` annotations

## Generated test runners — Do not edit manually

`tests/compiler-plugin-tests/src/test-gen/` contains auto-generated Java test classes (`BoxTestGenerated.java`, `DiagnosticTestGenerated.java`). 
**Do not edit them manually.** Regenerate after adding, removing, or renaming test data files:
```
:tests:compiler-plugin-tests:generateTests
```

## Version Compatibility

See `verify-compiler-plugin-compatibility` skill.
