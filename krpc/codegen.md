# kRPC codegen

What the compiler plugin generates for an `@Rpc` interface.
An `@Grpc` interface gets the same code and the additions in [gRPC codegen](../grpc/codegen.md).

## Declarations (FIR)

`FirRpcServiceGenerator` declares two empty classes in each interface that `FirRpcPredicates.rpc` matches:

```
@Rpc interface MyService
  └─ class $rpcServiceStub     key RpcGeneratedStubKey. public, final, @Deprecated(HIDDEN)
       └─ companion object     key FirRpcServiceStubCompanionObject
```

## Bodies (IR)

`RpcStubGenerator` fills the two classes:

| Declaration | Generated code |
|---|---|
| `$rpcServiceStub` | Implements the service. Each function passes an `RpcCall` to the `RpcClient`. |
| The companion | Implements `RpcServiceDescriptor<MyService>`, with one `RpcCallable` for each function |
| The service interface | Gets the `@WithServiceDescriptor($rpcServiceStub.Companion::class)` annotation |

```kotlin
final override suspend fun myMethod(arg1: Type1, arg2: Type2): ReturnType {
    return __rpc_client.call(RpcCall(
        descriptor = Companion,
        callableName = "myMethod",
        arguments = arrayOf(arg1, arg2),
        serviceId = __rpc_stub_id,
    ))
}
```

A function that isn't `suspend` and returns a `Flow` calls `__rpc_client.callServerStreaming` (`isNonSuspendingWithFlowReturn()`).

At runtime, `serviceDescriptorOf<MyService>()` returns the descriptor, and `client.withService<MyService>()` creates the stub.

## Diagnostics

`FirRpcServiceDeclarationChecker` checks each `@Rpc` interface:

| Diagnostic | Condition |
|---|---|
| `TYPE_PARAMETERS_IN_RPC_INTERFACE` | The interface has type parameters |
| `TYPE_PARAMETERS_IN_RPC_FUNCTION` | A function has type parameters |
| `NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE` | A function isn't `suspend` and doesn't return a `Flow` |
| `SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE` | A `suspend` function returns a `Flow` |
| `AD_HOC_POLYMORPHISM_IN_RPC_SERVICE` | Two functions have the same name |
| `FIELD_IN_RPC_SERVICE` | The interface has a property |
| `STATE_FLOW_IN_RPC_SERVICE`, `SHARED_FLOW_IN_RPC_SERVICE` | A function signature has a `StateFlow` or a `SharedFlow`, at any depth |
| `NESTED_STREAMING_IN_RPC_SERVICE` | A `Flow` is inside another `Flow` |
| `NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE` | A `Flow` is inside a parameter type or a return type, not the return type itself |

Other checkers:

| Checker | Diagnostic | Condition |
|---|---|---|
| `FirRpcAnnotationChecker` | `WRONG_RPC_ANNOTATION_TARGET` | `@Rpc` is on a class that isn't an interface |
| `FirCheckedAnnotationCheckers.kt` | `CHECKED_ANNOTATION_VIOLATION` | A type argument doesn't have the annotation that [`@CheckedTypeAnnotation`](../core/README.md) requires |
