# protoc-gen codegen

What the two `protoc` plugins generate from `.proto` files. The compiler plugin then processes the output:
[protobuf codegen](../protobuf/codegen.md) for messages and [gRPC codegen](../grpc/codegen.md) for services.

| Plugin | Module | Input | Output files for `<File>.proto`, in the proto package |
|---|---|---|---|
| Protobuf | `protoc-gen/protobuf` | Messages, enums, oneofs, extensions | `<File>.kt`: public message interfaces and enums |
| | | | `<File>.ext.kt`: extensions (constructors, `copy`, `presence`, oneof accessors) |
| | | | `_rpc_internal/<File>.kt`: internal classes with the codec, `DESCRIPTOR`, and `MARSHALLER` |
| gRPC | `protoc-gen/grpc` | Services | `<File>.kt`: `@Grpc` interfaces |

`protoc-gen/common` has the shared code: the protoc plugin protocol, the model, and the Kotlin code generator.

## Messages

```protobuf
message HelloRequest {
  string name = 1;
  optional uint32 timeout = 2;
}
```

```kotlin
// Helloworld.kt
@GeneratedProtoMessage
interface HelloRequest {
    val name: String
    val timeout: UInt?
}

// Helloworld.ext.kt
operator fun HelloRequest.Companion.invoke(body: HelloRequest.Builder.() -> Unit): HelloRequest
fun HelloRequest.copy(body: HelloRequest.Builder.() -> Unit = {}): HelloRequest
val HelloRequest.presence: HelloRequestPresence

interface HelloRequestPresence {
    val hasTimeout: Boolean
}

// Use
val request = HelloRequest { name = "world"; timeout = 5000u }
val copy = request.copy { timeout = 3000u }
request.presence.hasTimeout  // true
```

- Proto comments become KDoc.
- The compiler plugin declares `HelloRequest.Builder` and the companion.
- A nested message is a nested interface: `Outer.Inner`.
- `MARSHALLER` extends `ProtoGrpcMarshaller`. It computes the exact encoded size before it writes, and reads
  through a bounded `AbstractGrpcMessageReader`.

| Proto type | Kotlin type |
|---|---|
| `int32`, `sint32`, `sfixed32` | `Int` |
| `int64`, `sint64`, `sfixed64` | `Long` |
| `uint32`, `fixed32` | `UInt` |
| `uint64`, `fixed64` | `ULong` |
| `float`, `double`, `bool`, `string` | `Float`, `Double`, `Boolean`, `String` |
| `bytes` | `ByteString` |
| `optional T` | `T?`, with a `has<Field>` presence getter |
| `repeated T` | `List<T>` |
| `map<K, V>` | `Map<K, V>` |
| `message` | The generated interface |
| `enum` | The generated sealed class |

An enum is a sealed class. `UNRECOGNIZED` holds a number that the `.proto` file doesn't have:

```kotlin
@GeneratedProtoEnum
sealed class Priority(open val number: Int) {
    data object PRIORITY_UNSPECIFIED : Priority(number = 0)
    data object HIGH : Priority(number = 1)
    data class UNRECOGNIZED(override val number: Int) : Priority(number)

    companion object {
        val entries: List<Priority>   // without UNRECOGNIZED
    }
}
```

## Oneofs

```protobuf
message Event {
  oneof payload {
    string text = 1;
    int32 code = 2;
  }
}
```

```kotlin
@GeneratedProtoMessage
interface Event {
    val text: String
    val code: Int
}

enum class EventPayloadCase { TEXT, CODE, NOT_SET }

val Event.payload: EventPayloadCase
inline fun <R> Event.whenPayload(text: (String) -> R, code: (Int) -> R, notSet: () -> R): R

// Use
val event = Event {
    text = "hello"
    code = 5                                   // changes the case: text is "" and hasText is false
}
event.presence.hasCode                         // true
event.whenPayload(text = { it.length }, code = { it }, notSet = { 0 })
event.copy { clearPayload() }.payload          // EventPayloadCase.NOT_SET
```

- Each member is a property of the message that isn't nullable, like a singular field of the same type.
  A member that isn't the active case returns the proto default of its type.
- Each member has a `has<Member>` presence getter and a `clear<Member>()` builder function.
- Each oneof has a `<Message><OneOf>Case` enum, a `<oneOf>` property with the active case, a `clear<OneOf>()`
  builder function, and a `when<OneOf>` function with one branch for each case.
- The compiler plugin declares the `clear` functions on `Builder`, and the internal class implements them.
  The internal class has `@GeneratedProtoOneOfs(names = [...])` to give the oneof names to the compiler plugin.
- The internal class doesn't store the active case. The members of one oneof have consecutive presence bits,
  and the active member is the one with a set bit.
- A oneof has at most two value slots: `Any?` for strings, bytes, and messages, and `Int` or `Long` for numbers,
  booleans, and enums. So a set, a decode, or a read of a member allocates no object.
  A float or a double is in the slot as raw bits, and an enum as its number.

## Services

```protobuf
service StreamingTestService {
  rpc Unary(References) returns (References);
  rpc Server(References) returns (stream References);
  rpc Client(stream References) returns (References);
  rpc Bidi(stream References) returns (stream References);
}
```

```kotlin
@Grpc
interface StreamingTestService {
    @Grpc.Method(name = "Unary")
    suspend fun unary(message: References): References

    @Grpc.Method(name = "Server")
    fun server(message: References): Flow<References>

    @Grpc.Method(name = "Client")
    suspend fun client(message: Flow<References>): References

    @Grpc.Method(name = "Bidi")
    fun bidi(message: Flow<References>): Flow<References>
}
```

A function that returns a `Flow` isn't `suspend`.

| Proto `idempotency_level` | `@Grpc.Method` arguments |
|---|---|
| Not set | None |
| `IDEMPOTENT` | `idempotent = true` |
| `NO_SIDE_EFFECTS` | `idempotent = true, safe = true` |

## Plugin options

Both plugins take the same options, as `--<plugin>_opt=<key>=<value>`:

| Option | Default | Effect |
|---|---|---|
| `explicitApiModeEnabled` | `false` | Adds the `public` and `internal` modifiers |
| `generateComments` | `true` | Makes KDoc from the proto comments |
| `generateFileLevelComments` | `true` | Adds the file-level proto comments |
| `generateOptionalFieldOrNullGetters` | `false` | Adds a `<field>OrNull` getter for each field with presence, including oneof members |
| `generateOneOfWhenFunctions` | `true` | Adds the `when<OneOf>` function for each oneof |
| `camelCaseNames` | `true` | Makes Kotlin names in camel case. `false` uses the proto names. |
| `indentSize` | `4` | Indent in spaces |
| `platform` | `jvm` | `common`, `jvm`, `android`, `js`, `native`, or `wasm` |
| `debugOutput` | None | Path of a log file. The Gradle plugin sets `protoc-gen-<plugin name>.log`, in `build/protoBuild/sourceSets/<sourceSet>/`. |
| `protoNamesOutput` | None | Path of a file that gets the names of the generated declarations |
| `ignoreFiles` | None | Proto files to skip, with `\u0000` between them. The Gradle plugin sets it to the files that an imported source set generates. |
