# gRPC codegen

What the compiler plugin adds for an `@Grpc` interface to the [kRPC codegen](../krpc/codegen.md).
The `protoc-gen` plugins generate the `@Grpc` interfaces and the message types: [protoc-gen codegen](../protoc-gen/codegen.md).

## Companion

```kotlin
companion object : RpcServiceDescriptor<MyService>, GrpcServiceDescriptor<MyService>
```

The generated `delegate(resolver, marshallerConfig)` function returns a `GrpcServiceDelegate`.
The delegate has the gRPC `ServiceDescriptor` and one `MethodDescriptor` for each function.

| Value | Source |
|---|---|
| Service package | `@Grpc(protoPackage)` when the annotation sets it, else the Kotlin package |
| Service name | `@Grpc(protoServiceName)` when it isn't blank, else the Kotlin class name |
| Full service name | `<package>.<name>`, or `<name>` when the package is empty |
| Method name | `@Grpc.Method(name)` when it isn't blank, else the Kotlin function name |
| `idempotent`, `safe`, `sampledToLocalTracing` | `@Grpc.Method`. The defaults are `false`, `false`, `true`. |
| Request and response types | The parameter type and the return type. `Flow<T>` becomes `T`. |

| Parameter is a `Flow` | Return type is a `Flow` | `MethodType` |
|---|---|---|
| No | No | `UNARY` |
| No | Yes | `SERVER_STREAMING` |
| Yes | No | `CLIENT_STREAMING` |
| Yes | Yes | `BIDI_STREAMING` |

## Marshallers

The generated code gets the marshaller of a request or response type from the first source that has one:

1. The `MARSHALLER` object of the internal class, for a type with `@GeneratedProtoMessage`.
2. The object that `@WithGrpcMarshaller` on the type names.
3. `resolver.resolveOrNull(typeOf<T>())`. If the result is `null`, the code throws `IllegalArgumentException`.

When `marshallerConfig` isn't `null`, the code wraps the marshaller in `ConfiguredGrpcMarshallerDelegate(marshallerConfig, marshaller)`.

## Diagnostics

`FirGrpcServiceDeclarationChecker` checks each `@Grpc` interface, in addition to the `@Rpc` diagnostics:

| Diagnostic | Condition |
|---|---|
| `MULTIPLE_PARAMETERS_IN_GRPC_SERVICE` | A function has more than one parameter |
| `NULLABLE_PARAMETER_IN_GRPC_SERVICE` | The parameter type is nullable |
| `NULLABLE_RETURN_TYPE_IN_GRPC_SERVICE` | The return type is nullable |
| `NON_TOP_LEVEL_CLIENT_STREAMING_IN_RPC_SERVICE` | A `Flow` is inside the parameter type, not the parameter type itself |
| `WRONG_PROTO_PACKAGE_VALUE` | `protoPackage` doesn't match `[a-zA-Z_][a-zA-Z0-9_]*(\.[a-zA-Z_][a-zA-Z0-9_]*)*` |
| `WRONG_PROTO_METHOD_NAME_VALUE` | `@Grpc.Method(name)` doesn't match `[a-zA-Z_][a-zA-Z0-9_]*` |
| `WRONG_SAFE_IDEMPOTENT_COMBINATION` | `safe = true` with `idempotent = false` |

`FirWithGrpcMarshallerDeclarationChecker` checks each class with `@WithGrpcMarshaller`:

| Diagnostic | Condition |
|---|---|
| `NOT_AN_OBJECT_REFERENCE_IN_WITH_MARSHALLER_ANNOTATION` | `marshaller` names a class or an interface, not an object |
| `MARSHALLER_TYPE_MISMATCH` | The type argument of the marshaller isn't the annotated type |
