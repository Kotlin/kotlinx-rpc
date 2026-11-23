# Overview

The `:core` module is the protocol-agnostic runtime with the public API for RPC services.
It is a Kotlin Multiplatform module (JVM, JS, Native, WasmJS, WasmWasi).
The compiler plugin generates code against the interfaces of this module.

## `@CheckedTypeAnnotation`

`@CheckedTypeAnnotation` is a meta-annotation for annotations on type parameters, for example `@Rpc`.
The compiler plugin reports an error when a type argument doesn't have the annotation on its declaration:

```kotlin
fun <@Rpc reified T : Any> RpcClient.withService(): T

withService<NotAnRpcService>() // compiler error: NotAnRpcService has no @Rpc
```

An annotation with constructor arguments can't be on a type parameter. For such an annotation, a second annotation
names it in `checkFor`: `@HasWithGrpcMarshaller` is `@CheckedTypeAnnotation(WithGrpcMarshaller::class)`, and the
plugin checks the type argument for `@WithGrpcMarshaller`.

| Annotation | Checked on |
|---|---|
| `@Rpc` | Type parameters of `withService`, `registerService`, and similar functions |
| `@HasWithGrpcMarshaller` | Type parameters of the gRPC marshaller factory functions |

## Generated code

| Protocol | Reference |
|---|---|
| kRPC | [krpc/codegen.md](../krpc/codegen.md): stub class, companion descriptor, invokators |
| gRPC | [grpc/codegen.md](../grpc/codegen.md): `delegate()`, `MethodDescriptor`, streaming types |
| Protobuf | [protobuf/codegen.md](../protobuf/codegen.md): `Builder` interface, `@WithProtoDescriptor` and `@WithGrpcMarshaller` |
