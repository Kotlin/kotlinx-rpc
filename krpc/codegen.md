# kRPC Codegen Reference

What the compiler plugin generates for `@Rpc` service interfaces.

The compiler plugin works in two phases. 
First, the FIR phase declares empty "shell" classes (a stub class and its companion) inside the service interface. 
Then, the IR phase fills these shells with actual method implementations: the stub class gets override methods that delegate to `RpcClient`, and the companion gets the `RpcServiceDescriptor` implementation. 

At runtime, the generated descriptor is accessed via `serviceDescriptorOf<MyService>()`, 
and `client.withService<MyService>()` creates the instance of the generated stub implementation.

## FIR Phase -- Declaration Shapes

`FirRpcServiceGenerator` triggers on interfaces matched by `FirRpcPredicates.rpc`:

```
@Rpc interface MyService { ... }
  └─ nested class $rpcServiceStub        (key: RpcGeneratedStubKey)
       └─ companion object               (key: FirRpcServiceStubCompanionObject)
```

The stub class is marked `FINAL`, `PUBLIC`, and `@Deprecated(hidden)` (hidden from user code).

## IR Phase -- Stub Class

`RpcStubGenerator.generateStubClass()` fills in the FIR stub.

### Method Generation

For each method in the service interface, one of two call patterns is generated:

**Suspend method (unary RPC):**
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

**Non-suspend method returning Flow (server streaming):**
```kotlin
final override fun streamMethod(arg: Type1): Flow<ReturnType> {
    return __rpc_client.callServerStreaming(RpcCall(
        descriptor = Companion,
        callableName = "streamMethod",
        arguments = arrayOf(arg),
        serviceId = __rpc_stub_id,
    ))
}
```

The branching logic is in `isNonSuspendingWithFlowReturn()` -- checks `returnType == Flow<> && !isSuspend`.

## IR Phase

Makes the companion extend `RpcServiceDescriptor<MyService>` and generates the implementation.

After stub generation, `@WithServiceDescriptor($rpcServiceStub.Companion::class)` is added to the original service interface.

## FIR Diagnostics

### Core RPC (`FirRpcServiceDeclarationChecker`)

Runs on all `@Rpc` interfaces:

| Diagnostic | Condition |
|---|---|
| `TYPE_PARAMETERS_IN_RPC_INTERFACE` | Interface has type parameters |
| `TYPE_PARAMETERS_IN_RPC_FUNCTION` | Method has type parameters |
| `NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE` | Non-suspend method returns non-Flow type |
| `AD_HOC_POLYMORPHISM_IN_RPC_SERVICE` | Multiple methods with same name (overloading) |
| `FIELD_IN_RPC_SERVICE` | Property declared in interface |
| `SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE` | `suspend fun` returning `Flow` |
| `STATE_FLOW_IN_RPC_SERVICE` | `StateFlow` anywhere in method signatures (recursive) |
| `SHARED_FLOW_IN_RPC_SERVICE` | `SharedFlow` anywhere in method signatures (recursive) |
| `NESTED_STREAMING_IN_RPC_SERVICE` | `Flow<Flow<T>>` -- parent chain contains another Flow |
| `NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE` | `Flow` nested inside parameter/return types (not direct return) |

### Checked Type Annotation (`FirCheckedAnnotationCheckers`)

Cross-cutting, applies to type arguments marked with `@CheckedTypeAnnotation`:

| Diagnostic | Condition |
|---|---|
| `CHECKED_ANNOTATION_VIOLATION` | Type argument doesn't carry the required annotation |

Check [README.md](../core/README.md) to learn more about `@CheckedTypeAnnotation`.
