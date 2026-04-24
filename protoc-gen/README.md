# Overview

Protoc plugins for kotlinx-rpc. Two code generators that translate `.proto` files into idiomatic Kotlin multiplatform code, 
invoked by Google's `protoc` compiler (or Buf's `protoc` compiler) via the standard plugin protocol (binary `CodeGeneratorRequest` on stdin, `CodeGeneratorResponse` on stdout).

This is a **separate included Gradle build** (JVM-only). Published artifacts are fat JARs consumed by the kotlinx-rpc Gradle plugin during user builds. The generated code depends on the `protobuf/` runtime modules.

See [codegen.md](codegen.md) for what each plugin generates, with full examples of `.proto` → Kotlin output.

## Downstream generated code

Changes to `protoc-gen/` affect the Kotlin code generated into downstream modules. The generated output is written into each module's `build/protoBuild/generated/<sourceSetName>/` tree and is **not** committed. Gradle regenerates it automatically when `protoc-gen/` inputs change:

| Downstream module | Regeneration task |
|---|---|
| `protobuf/protobuf-wkt` | `:protobuf:protobuf-wkt:bufGenerateCommonMain` |
| `tests/protobuf-conformance` | `:tests:protobuf-conformance:bufGenerateCommonMain` |
