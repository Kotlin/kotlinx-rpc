# Overview

Protoc plugins for kotlinx-rpc. Two code generators that translate `.proto` files into idiomatic Kotlin multiplatform code, 
invoked by Google's `protoc` compiler (or Buf's `protoc` compiler) via the standard plugin protocol (binary `CodeGeneratorRequest` on stdin, `CodeGeneratorResponse` on stdout).

This is a **separate included Gradle build** (JVM-only). Published artifacts are fat JARs consumed by the kotlinx-rpc Gradle plugin during user builds. The generated code depends on the `protobuf/` runtime modules.

See [codegen.md](codegen.md) for what each plugin generates, with full examples of `.proto` → Kotlin output.

## Downstream generated code — Do not edit manually

Changes to `protoc-gen/` affect checked-in generated code in other modules. 
**Never edit these files by hand** — regenerate them instead:

| Generated files | Regeneration task |
|---|---|
| `protobuf/protobuf-wkt/src/commonMain/generated-code/` | `:protobuf:protobuf-wkt:bufGenerateCommonMain` |
| `tests/protobuf-conformance/src/main/generated-code/` | `:tests:protobuf-conformance:bufGenerateMain` |
