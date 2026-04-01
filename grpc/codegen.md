# gRPC Codegen Reference

What the compiler plugin generates **additionally** for `@Grpc` service interfaces, beyond the shared kRPC codegen.

All `@Grpc` services get the same stub class, constructor, methods, and companion base as `@Rpc` services (see [kRPC codegen](../krpc/codegen.md)). 
This document covers only the gRPC-specific additions.

For what the `protoc-gen` plugins generate *before* the compiler plugin runs (the `@Grpc` interfaces and message types that this codegen operates on), 
see [protoc-gen/codegen.md](../protoc-gen/codegen.md).

## Companion Object -- Additional Supertype

The companion has two supertypes now:
```kotlin
companion object : RpcServiceDescriptor<MyService>, GrpcServiceDescriptor<MyService>
```

It adds a `delegate()` method that returns a `GrpcServiceDelegate` instance. 
The latter contains the `MethodDescriptor`s for all methods in the service and a dedicated gRPC style `ServiceDescriptor` for the service.

### Service Name Resolution

`ServiceDeclaration.serviceFqName` is computed as:
- If `@Grpc(protoPackage = "my.proto.package", protoServiceName = "some)` is non-empty: `"$protoPackage.$protoServiceName"`
- If any of `protoPackage` or `protoServiceName` is empty the value default's to the Kotlin class name and package.

### Method Name Resolution

`ServiceDeclaration.Method.grpcName` reads `@Grpc.Method(name = "...")`:
- If the annotation's `name` is non-blank: uses that value
- Otherwise: falls back to the Kotlin method name

### Streaming Type Detection

The `MethodType` enum value is determined by checking `Flow` on parameter and return types:

| Request (param) | Response (return) | MethodType |
|---|---|---|
| Not Flow | Not Flow | `UNARY` |
| Not Flow | Flow | `SERVER_STREAMING` |
| Flow | Not Flow | `CLIENT_STREAMING` |
| Flow | Flow | `BIDI_STREAMING` |

For `MethodDescriptor` type arguments, Flow types are unwrapped: `Flow<T>` becomes `T`.

### Marshaller Resolution

For each request/response type, marshaller is resolved in order:
1. **Proto declaration** -- if the type has `@GeneratedProtoMessage`, use its internal `MARSHALLER` object directly
2. **`@WithGrpcMarshaller` annotation** -- use the referenced marshaller object
3. **Resolver fallback** -- call `resolver.resolveOrNull(typeOf<T>())`, throw `IllegalArgumentException` if null

When `marshallerConfig` is non-null, the marshaller is wrapped in `ConfiguredGrpcMarshallerDelegate(config, marshaller)`.

### `@Grpc.Method` Annotation Parameters

Read at IR time from method annotations:
- `idempotent: Boolean` (default `false`)
- `safe: Boolean` (default `false`)
- `sampledToLocalTracing: Boolean` (default `true`)

### RpcCallable Extra Argument

For gRPC services, `RpcCallable` constructor is called with 5 arguments (vs 4 for kRPC). The 5th parameter carries gRPC-specific method metadata.

## FIR Diagnostics (`FirGrpcServiceDeclarationChecker`)

Additional to the `@Rpc` diagnostics.

Runs on all `@Grpc` interfaces:

| Diagnostic | Condition |
|---|---|
| `MULTIPLE_PARAMETERS_IN_GRPC_SERVICE` | Method has >1 parameter |
| `NULLABLE_PARAMETER_IN_GRPC_SERVICE` | Parameter type is nullable |
| `NULLABLE_RETURN_TYPE_IN_GRPC_SERVICE` | Return type is nullable |
| `NON_TOP_LEVEL_CLIENT_STREAMING_IN_RPC_SERVICE` | `Flow` nested in parameter type (not direct) |
| `WRONG_PROTO_PACKAGE_VALUE` | `@Grpc(protoPackage=...)` doesn't match `[a-zA-Z_][a-zA-Z0-9_]*(\.[a-zA-Z_][a-zA-Z0-9_]*)*` |
| `WRONG_PROTO_METHOD_NAME_VALUE` | `@Grpc.Method(name=...)` doesn't match `[a-zA-Z_][a-zA-Z0-9_]*` |
| `WRONG_SAFE_IDEMPOTENT_COMBINATION` | `safe=true` with `idempotent=false` (must explicitly set `idempotent=true`) |

## FIR Diagnostics (`FirWithGrpcMarshallerDeclarationChecker`)

Runs on classes annotated with `@WithGrpcMarshaller`:

| Diagnostic | Condition |
|---|---|
| `NOT_AN_OBJECT_REFERENCE_IN_WITH_MARSHALLER_ANNOTATION` | `marshaller` references a class/interface, not an object |
| `MARSHALLER_TYPE_MISMATCH` | Marshaller's type argument doesn't match the annotated message type |
