# Overview

This is the `:core` module of kotlinx-rpc -- the protocol-agnostic runtime that defines the public API for RPC services. 
It is a Kotlin Multiplatform module (JVM, JS, Native, WasmJS, WasmWasi). 
The compiler plugin generates code against the interfaces defined here.

`@Rpc` -- annotation that triggers compiler plugin code generation

## @CheckedTypeAnnotation annotations family

`@CheckedTypeAnnotation` is a meta-annotation that enables compile-time type-safety checks on type parameters.
When an annotation (e.g. `@Rpc`) is itself annotated with `@CheckedTypeAnnotation`,
the compiler plugin verifies that any type argument passed to a type parameter marked with that annotation
actually carries the annotation on its declaration. This turns annotation misuse into a compile error
instead of a runtime failure.

**How it works:**
```kotlin
fun <@Rpc reified T : Any> RpcClient.withService(): T  // T must be an @Rpc-annotated interface
```
Calling `withService<NotAnRpcService>()` produces a compiler error because `NotAnRpcService` is not annotated with `@Rpc`.

The `checkFor` parameter handles the case where the annotation being checked requires constructor arguments
and therefore cannot be placed directly on a type parameter (e.g. `@HasWithGrpcMarshaller` checks for `@WithGrpcMarshaller`).

**Applied in the library:**
- `@Rpc` — annotated with `@CheckedTypeAnnotation`; enforced on type parameters of `withService`, `registerService`, etc.
- `@HasWithGrpcMarshaller` — annotated with `@CheckedTypeAnnotation(WithGrpcMarshaller::class)`; enforced on gRPC marshaller factory functions

## Generation References (detailed)

Protocol-specific codegen and diagnostics details live alongside each protocol module:
- [kRPC codegen](../krpc/codegen.md) -- stub class, companion descriptor, invokators
- [gRPC codegen](../grpc/codegen.md) -- gRPC-specific additions: `delegate()`, `MethodDescriptor`, streaming types
- [Protobuf codegen](../protobuf/codegen.md) -- `Builder` interface, `@WithProtoDescriptor`/`@WithGrpcMarshaller` annotations
