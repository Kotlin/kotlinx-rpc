# protoc-gen Codegen Reference

What each protoc-gen plugin generates from `.proto` files.

Two plugins translate `.proto` definitions into idiomatic Kotlin multiplatform code. 
Both run as standard protoc plugins. The generated code is then further processed by the [compiler plugin](../compiler-plugin/) 
which adds FIR declarations (Builder, Companion) and IR annotations (`@WithProtoDescriptor`, `@WithGrpcMarshaller`).

For what the compiler plugin does on top of this output, see:
- [protobuf/codegen.md](../protobuf/codegen.md) -- FIR/IR processing of generated messages
- [grpc/codegen.md](../grpc/codegen.md) -- FIR/IR processing of generated services

## Plugins

| Plugin | Module | Input | Output |
|---|---|------------------------------------------------------|--------------------------------------------------------------------|
| **Protobuf** | `protoc-gen/protobuf` | `message`, `enum`, `oneof`, declarations, extensions | Message interfaces + Kotlin extenstions + internal implementations |
| **gRPC** | `protoc-gen/grpc` | `service` declarations | `@Grpc` service interfaces |

Both plugins share a common framework in `protoc-gen/common` that handles the protoc plugin protocol, model parsing, and a DSL-based Kotlin code generator.

## File Layout

For each `.proto` file, the **protobuf plugin** produces three Kotlin files:

| File | Package | Contains |
|---|---|---|
| `<ProtoFile>.kt` | `<proto_package>` | Public message/enum interfaces |
| `<ProtoFile>.ext.kt` | `<proto_package>` | Extension functions (constructors, `copy`, `presence`) |
| `_rpc_internal/<ProtoFile>.kt` | `<proto_package>` | Internal implementation classes, marshallers, descriptors, encode/decode |

The **gRPC plugin** produces one file per `.proto` file:

| File | Package | Contains |
|---|---|---|
| `<ProtoFile>.kt` | `<proto_package>` | `@Grpc` service interfaces |

## Protobuf Plugin -- Messages

### Example Input

```protobuf
syntax = "proto3";

message HelloRequest {
  string name = 1;
  optional uint32 timeout = 2;
}

message HelloReply {
  string message = 1;
}
```

### Public File (`HelloworldGrpc.kt`)

Each message becomes an interface annotated with `@GeneratedProtoMessage`. Proto comments are preserved as KDoc. Proto3 `optional` fields map to nullable types.

```kotlin
@GeneratedProtoMessage
interface HelloRequest {
    val name: String
    val timeout: UInt?
}

@GeneratedProtoMessage
interface HelloReply {
    val message: String
}
```

### Extension File (`HelloworldGrpc.ext.kt`)

Builder-style constructors via `Companion.invoke`, `copy` functions, and presence accessors for optional fields:

```kotlin
operator fun HelloRequest.Companion.invoke(body: HelloRequest.Builder.() -> Unit): HelloRequest { ... }

fun HelloRequest.copy(body: HelloRequest.Builder.() -> Unit = {}): HelloRequest { ... }

val HelloRequest.presence: HelloRequestPresence get() = this.asInternal()._presence

interface HelloRequestPresence {
    val hasTimeout: Boolean
}
```

Usage:
```kotlin
val request = HelloRequest {
    name = "world"
    timeout = 5000u
}
val copy = request.copy { timeout = 3000u }
val isSet = request.presence.hasTimeout  // true
```

### Proto Type Mapping

| Proto Type          | Kotlin Type                         |
|---------------------|-------------------------------------|
| `int32`, `sint32`   | `Int`                               |
| `int64`, `sint64`   | `Long`                              |
| `uint32`, `fixed32` | `UInt`                              |
| `uint64`, `fixed64` | `ULong`                             |
| `float`             | `Float`                             |
| `double`            | `Double`                            |
| `bool`              | `Boolean`                           |
| `string`            | `String`                            |
| `bytes`             | `ByteString`                        |
| `sfixed32`          | `Int`                               |
| `sfixed64`          | `Long`                              |
| `optional T`        | `T?` (nullable + presence tracking) |
| `repeated T`        | `List<T>`                           |
| `map<K, V>`         | `Map<K, V>`                         |
| `enum`              | Generated Kotlin sealed class       |
| `message`           | Generated interface                 |

### Enums

Proto enums become Kotlin sealed types. Unknown values received over the wire are represented as `UNRECOGNIZED`:

```protobuf
enum Priority {
  PRIORITY_UNSPECIFIED = 0;
  HIGH = 1;
  LOW = 2;
}

message Task {
  required Priority priority = 1;
}
```

```kotlin
val task = Task {
    priority = Priority.HIGH
}
```

### Repeated Fields

Repeated fields map to Kotlin `List`:

```protobuf
message Numbers {
  repeated int32 values = 1;
  repeated string labels = 2;
}
```

```kotlin
val numbers = Numbers {
    values = listOf(1, 2, 3)
    labels = listOf("a", "b", "c")
}

val extended = numbers.copy {
    values = values + 4
}
```

### Map Fields

Map fields map to Kotlin `Map`:

```protobuf
message Config {
  map<string, int64> settings = 1;
}
```

```kotlin
val config = Config {
    settings = mapOf("timeout" to 30L, "retries" to 3L)
}

val updated = config.copy {
    settings = settings + ("retries" to 5L)
}
```

### OneOf

Proto `oneof` fields generate a sealed interface. Each case is a data class wrapping the value:

```protobuf
message Event {
  oneof payload {
    string text = 1;
    int32 code = 2;
  }
}
```

```kotlin
val event = Event {
    payload = Event.Payload.Text("hello")
}

when (event.payload) {
    is Event.Payload.Text -> println(event.payload.value)
    is Event.Payload.Code -> println(event.payload.value)
    null -> {}
}
```

### Nested Messages

Nested message types are accessed through the parent type. 

```protobuf
message Outer {
  message Inner {
    required int32 value = 1;
  }

  required Inner inner = 1;
}
```

```kotlin
val outer = Outer {
    inner = Outer.Inner {
        value = 42
    }
}
```

## gRPC Plugin -- Services

### Example Input

```protobuf
syntax = "proto3";

service GreeterService {
  rpc SayHello(HelloRequest) returns (HelloReply);
}
```

### Kotlin Output

Each service becomes a `@Grpc` interface.

```kotlin
@Grpc
interface GreeterService {
    suspend fun SayHello(message: HelloRequest): HelloReply
}
```

The compiler plugin then generates the stub class, `RpcServiceDescriptor`, `GrpcServiceDelegate`, and `RpcCallable` entries from this interface (see [grpc/codegen.md](../grpc/codegen.md)).

### Streaming

Streaming type is determined by the `stream` keyword in the `.proto` file:

```protobuf
service StreamingTestService {
  rpc Server(References) returns (stream References);
  rpc Client(stream References) returns (References);
  rpc Bidi(stream References) returns (stream References);
}
```

```kotlin
@Grpc
interface StreamingTestService {
    fun Server(message: References): Flow<References>
    suspend fun Client(message: Flow<References>): References
    fun Bidi(message: Flow<References>): Flow<References>
}
```

Note: server-streaming and bidi methods are **not** `suspend` since they return `Flow` directly.

### Idempotency

Reads `MethodOptions.IdempotencyLevel` from the `.proto` method options and maps to `@GrpcMethod` annotation parameters:

| Proto `idempotency_level` | Generated annotation |
|---|---|
| (default) | _(none)_ |
| `IDEMPOTENT` | `@GrpcMethod(idempotent = true)` |
| `NO_SIDE_EFFECTS` | `@GrpcMethod(idempotent = true, safe = true)` |

## Pipeline Overview

```
.proto files
    │
    ▼
protoc (or buf)
    │
    ├── protoc-gen-kotlinx-rpc-protobuf ──▶ message interfaces + internals
    │                                           │
    └── protoc-gen-kotlinx-rpc-grpc ──────▶ @Grpc service interfaces
                                                │
                                                ▼
                                        Kotlin compiler
                                     (kotlinx-rpc plugin)
                                                │
                                   ┌────────────┴────────────┐
                                   ▼                         ▼
                            FIR phase                   IR phase
                        Builder interface           @WithProtoDescriptor
                        Companion object            @WithGrpcMarshaller
                        Diagnostics                 GrpcServiceDelegate
                                                    RpcCallable metadata
```

## Plugin Options

Both plugins accept the same options (passed via `--<plugin>_opt=key=value`):

| Option                      | Default | Description                                                                                     |
|-----------------------------|---------|-------------------------------------------------------------------------------------------------|
| `explicitApiModeEnabled`    | `false` | Add `public`/`internal` visibility modifiers                                                    |
| `generateComments`          | `true`  | Preserve proto comments as KDoc                                                                 |
| `generateFileLevelComments` | `true`  | Include file-level proto comments                                                               |
| `indentSize`                | `4`     | Indentation width in spaces                                                                     |
| `platform`                  | —       | Target platform (`COMMON`, `JVM`, `JS`, `NATIVE`, `WASM_JS`, `WASM_WASI`)                       |
| `debugOutput`               | `false` | Write debug output into `protoBuild/sourceSets/<sourceSet>/protoc-gen-<protoc-plugin-name>.log` |
