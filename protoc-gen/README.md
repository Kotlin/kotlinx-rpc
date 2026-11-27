# Overview

Two protoc plugins that generate Kotlin Multiplatform code from `.proto` files.
`protoc` or Buf runs them through the standard plugin protocol: a binary `CodeGeneratorRequest` on stdin, a `CodeGeneratorResponse` on stdout.

This is an included Gradle build for the JVM only. It publishes fat JARs, which the kotlinx-rpc Gradle plugin runs in user builds.
The generated code depends on the `protobuf/` runtime modules.

[codegen.md](codegen.md) shows what each plugin generates.

## Generated code in other modules

A change in `protoc-gen/` changes generated code that is checked in. Never edit that code by hand. Regenerate it:

| Generated files | Task |
|---|---|
| `protobuf/protobuf-wkt/src/commonMain/generated-code/` | `:protobuf:protobuf-wkt:protobufGenerateCommonMain` |
| `tests/protobuf-conformance/src/commonMain/generated-code/` | `:tests:protobuf-conformance:protobufGenerateCommonMain` |
