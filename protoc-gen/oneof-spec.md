# Oneof Redesign Specification

Status: proposal. Scope: `protoc-gen/protobuf` code generation for `oneof` declarations and the runtime
support it needs in `protobuf/protobuf-lite`. The Kotlin compiler plugin is not changed.

This is a breaking change to the generated API. The current sealed-interface-with-wrapper design is removed.

## 1. Goals

- Type safe: no `!!`, no casts, no `ClassCastException` reachable from the generated API.
- Memory efficient: zero allocations when setting, decoding or reading a oneof value; no boxing of numeric
  payloads; no per-oneof storage beyond the value itself.
- Concise: a oneof member is used exactly like any other message field. The oneof-specific surface is one
  case type, one case property, one match function and one clear function.

## 2. Public API

### 2.1 Running example

```protobuf
message Event {
  oneof payload {
    string text = 1;
    int32 code = 2;
    double ratio = 3;
    Inner inner = 4;
    Level level = 5;   // enum
  }
}
```

### 2.2 Message interface (`<File>.kt`)

Every oneof member becomes a flat, non-nullable member property, exactly like a singular field of the
same type. When the member is not the active case, the property returns the proto default for its type
(`""`, `0`, `0.0`, the default message instance, the enum with number 0).

```kotlin
@GeneratedProtoMessage
interface Event {
    val text: String
    val code: Int
    val ratio: Double
    val inner: Inner
    val level: Level
}
```

That is the whole public message source. No nested sealed `Payload` interface and no per-member wrapper
classes are generated anymore, and nothing oneof-specific is nested in the message interface.

### 2.3 Case type (`<File>.ext.kt`)

The oneof contributes one case type. It is a top-level `enum class` in the ext file, placed the same way
the `<Message>Presence` interface is, so the public message file stays free of it. Entries are
singletons, so they never allocate, and `when` over them is exhaustive.

```kotlin
/**
 * Cases of the `payload` oneof of [Event]. Retrieve the active case via the [Event.payload] extension property.
 */
enum class EventPayloadCase {
    TEXT,
    CODE,
    RATIO,
    INNER,
    LEVEL,
    NOT_SET,
}
```

Entries carry no payload and no field number: nothing in the generated code or the runtime consumes a
number from the case, and a case never goes on the wire. `entries`, `name` and `ordinal` come for free
from `enum class`. Entry order is declaration order of the members, `NOT_SET` last.

For a nested message the name is flattened: `Outer.Inner` with oneof `kind` gets `OuterInnerKindCase`.
This differs from presence interfaces, which nest (`OuterPresence.Inner`); an enum per oneof has
no natural container to nest into, and a flat name keeps `when` branches short.

### 2.4 Extensions (`<File>.ext.kt`)

```kotlin
/** The active case of the `payload` oneof, or [EventPayloadCase.NOT_SET]. */
val Event.payload: EventPayloadCase

/** Returns `text` if it is the active case, otherwise null. */
val Event.textOrNull: String?          // one per member, see 2.6 for gating
val Event.codeOrNull: Int?
// ...

/** Clears the `payload` oneof regardless of its active case. */
fun Event.Builder.clearPayload()

/**
 * Exhaustive, typed dispatch on the active case. Inlined: no lambda objects, no boxing.
 */
inline fun <R> Event.whenPayload(
    text: (String) -> R,
    code: (Int) -> R,
    ratio: (Double) -> R,
    inner: (Inner) -> R,
    level: (Level) -> R,
    notSet: () -> R,
): R
```

`payload` is an extension property on purpose: the compiler plugin mirrors member properties of the
message interface into the `Builder`, and the case must not be assignable.

### 2.5 Presence interface

Each oneof member gets a presence getter, generated into the existing `<Message>Presence` interface:

```kotlin
interface EventPresence {
    val hasText: Boolean
    val hasCode: Boolean
    val hasRatio: Boolean
    val hasInner: Boolean
    val hasLevel: Boolean
}
```

The presence interface is now generated for every message that has a oneof, in addition to the
existing presence conditions. At most one `has<Member>` of a oneof is `true`. `event.payload == NOT_SET`
if and only if all are `false`.

### 2.6 Builder

The Builder is produced by the compiler plugin from the message interface, unchanged:

- every member property is mirrored as an abstract `var`,
- for every `has<Member>` getter in the presence interface a `clear<Member>()` function is generated.

Semantics, implemented by the internal class:

| Operation | Effect |
|---|---|
| `text = v` | `payload` becomes `TEXT`, other members return defaults. Assigning the default value (`""`, `0`) still selects the case. |
| `clearText()` | If `TEXT` is active, the oneof becomes `NOT_SET`. Otherwise no-op. |
| `clearPayload()` | The oneof becomes `NOT_SET`. |
| `copy { code = 1 }` | Switches the case in the copy. |

### 2.7 Usage

```kotlin
val e = Event { text = "hi"; code = 5 }        // case: Code

e.code                                          // 5
e.text                                          // ""
e.presence.hasCode                              // true
e.codeOrNull ?: -1                              // 5

when (e.payload) {                              // exhaustive, not type-linked
    EventPayloadCase.TEXT -> e.text.length
    EventPayloadCase.CODE -> e.code
    EventPayloadCase.RATIO -> e.ratio.toInt()
    EventPayloadCase.INNER -> e.inner.a
    EventPayloadCase.LEVEL -> e.level.number
    EventPayloadCase.NOT_SET -> 0
}

e.whenPayload(                                 // exhaustive and type-linked
    text = { it.length },
    code = { it },
    ratio = { it.toInt() },
    inner = { it.a },
    level = { it.number },
    notSet = { 0 },
)
```

`whenPayload` is the documented type-safe idiom. `when` over the case is kept for users coming from
protobuf-java or protobuf-go and for cases where only one or two branches are interesting.

### 2.8 Generator options

| Option | Default | Effect |
|---|---|---|
| `generateOneOfWhenFunctions` | `true` | Emit `when<Oneof>` functions. |
| `generateOptionalFieldOrNullGetters` | `false` | Existing option. Now also emits `<member>OrNull` for oneof members. The exclusion of oneof members in `FieldDeclaration.hasOrNullGetter` is removed. |

## 3. Naming

| Generated symbol | Name | Example |
|---|---|---|
| case type | top-level `enum class <Message><OneofName>Case`, UpperCamel, nested message names flattened | `EventPayloadCase`, `OuterInnerKindCase` |
| case entry | proto member name upper-cased, snake case kept | `TEXT`, `LOCAL_TS` |
| not-set case entry | `NOT_SET` | |
| case property | oneof name, lowerCamel | `payload` |
| member property | member name, lowerCamel | `text` |
| presence getter | `has<Member>` | `hasText` |
| clear member | `clear<Member>` | `clearText` |
| clear oneof | `clear<OneofName>` | `clearPayload` |
| or-null getter | `<member>OrNull` | `textOrNull` |
| when function | `when<OneofName>` | `whenPayload` |

`camelCaseNames = false` keeps proto names for members and the oneof, as for all other symbols.

Conflict handling uses the existing escaping rules (keyword backticks, suffixing on collision). New
collision cases to cover:

- a member literally named `not_set` collides with the `NOT_SET` entry,
- a top-level message or enum in the same package named `<Message><OneofName>Case` collides with the
  case type (the same class of collision `<Message>Presence` already handles),
- a member whose type is a class named like the case type or the containing message (existing
  shadowing rules apply; fully qualify).

protoc guarantees that a oneof name is never equal to a field name in the same message, so the `payload`
extension property cannot collide with a member.

## 4. Internal representation

### 4.1 Case storage: presence bits

The active case is not stored in a dedicated field. Every oneof member gets a presence index, allocated
consecutively for the members of one oneof, in declaration order. The active case is the member whose
presence bit is set; all members clear means `NOT_SET`.

- `has<Member>` reads one bit.
- Setting a member clears the sibling bits and sets its own. Because sibling indices are consecutive
  this is a single mask operation on one word. `BitSet` gets a `setExclusive(index, rangeStart, rangeEnd)`
  (or equivalent) helper so generated code does not loop.
- `<oneof>` case property reads the sibling range and maps the set bit to the enum entry through a
  generated `when`.

### 4.2 Value storage: typed slots

Per oneof the internal class declares up to two slots, chosen from the member types:

| Slot | Declared when | Holds |
|---|---|---|
| `_<oneof>Ref: Any?` | at least one member is `string`, `bytes`, a message or a group | the reference |
| `_<oneof>Num: Int` | at least one member is numeric, bool or enum, and all such members fit in 32 bits | `int32`, `sint32`, `uint32`, `fixed32`, `sfixed32`, `bool` (0/1), `float` (raw bits), enum (number) |
| `_<oneof>Num: Long` | as above, but at least one member needs 64 bits | everything above plus `int64`, `sint64`, `uint64`, `fixed64`, `sfixed64`, `double` (raw bits) |

Rules:

- Enums are stored by number in the numeric slot, never as a reference. Reads go through
  `<Enum>.fromNumber(n)`, which returns the shared data object for known values and allocates only for
  `UNRECOGNIZED`.
- Float and double are stored as raw bits (`toRawBits` / `fromBits`), so NaN payloads round-trip and
  compare equal, and `0.0` and `-0.0` are distinct. This matches protobuf-java.
- Setting a member writes its slot and resets the other slot to `null` / `0` so stale references are not
  retained.
- A oneof with no reference members has no reference slot; a oneof with no numeric members has no
  numeric slot. A oneof therefore costs 4 bytes (32-bit numeric only, or reference only), 8 bytes (64-bit
  numeric only) or 12 bytes (mixed) of inline storage and nothing else.

Measured on JDK 21 with compressed oops, standalone objects, bytes per message including the header:

| Layout | unset | string set | int32 set (outside box cache) | double set |
|---|---|---|---|---|
| current wrapper | 16 | 32 | 32 | 40 |
| this spec, mixed oneof (`Any?` + `Long`) | 24 | 24 | 24 | 24 |
| this spec, numeric-only 32-bit (`Int`) | 16 | - | 16 | - |

Allocations per set or decode: current 1, this spec 0.

Platform note: on Kotlin/JS a `Long` field is an object, so 64-bit numeric members allocate on JS. This
is not a regression versus the current wrapper and does not affect JVM, Native or Wasm.

### 4.3 Member accessors

```kotlin
override var text: String
    get() = if (presenceMask[IDX_TEXT]) _payloadRef as String else ""
    set(v) { presenceMask.setExclusive(IDX_TEXT, IDX_TEXT, IDX_LEVEL); _payloadRef = v; _payloadNum = 0L }

override var code: Int
    get() = if (presenceMask[IDX_CODE]) _payloadNum.toInt() else 0
    set(v) { presenceMask.setExclusive(IDX_CODE, IDX_TEXT, IDX_LEVEL); _payloadNum = v.toLong(); _payloadRef = null }

override var inner: Inner
    get() = if (presenceMask[IDX_INNER]) _payloadRef as Inner else InnerInternal.DEFAULT
    set(v) { ...; _payloadRef = v; _payloadNum = 0L }

override fun clearText() { if (presenceMask[IDX_TEXT]) clearPayloadInternal() }
fun clearPayloadInternal() { presenceMask.clearRange(IDX_TEXT, IDX_LEVEL); _payloadRef = null; _payloadNum = 0L }
```

### 4.4 Encoding, size, decoding

- `encodeWith` and `computeSize`: `when` over the active case, writing the member with its field number
  and wire type, reading directly from the slot.
- `decodeWith`: each member's field number is a `when` branch, as today. Scalar and enum branches assign
  through the member setter. Message branches merge: if that member is already the active case, decode
  into the existing instance; otherwise create a new internal instance, assign it, then decode into it.
  Last field wins across cases, matching proto semantics and the existing `testOneOfLastWins` test.
- Unknown enum numbers are stored as-is in the numeric slot; reading yields `UNRECOGNIZED(number)` and
  re-encoding preserves the number.

### 4.5 equals, hashCode, toString, copy

- `equals`: presence masks are already compared for the whole message, which covers the case. Then the
  active member is compared: reference slot by `==`, numeric slot by raw value (which gives raw-bits
  semantics for float and double).
- `hashCode`: `31 * result + (fieldNumber * 31 + valueHash)`, where `fieldNumber` is the active member's
  proto field number as a literal in the generated `when`, and `valueHash` is the slot hash. `NOT_SET`
  contributes `0`.
- `toString` / `asString`: only the active member is printed, as `text=hi`. `NOT_SET` prints nothing for
  the oneof. This replaces the current `field=Text(value=hi)` output.
- `copyInternal`: copies the presence bits and the slots; a message reference is deep-copied as today.
- `checkRequiredFields`: descends into the active member if it is a message.

The dedicated `oneOfHashCode`, `oneOfEquals` and `oneOfCopy` helpers are removed.

### 4.6 `whenPayload` implementation

```kotlin
inline fun <R> Event.whenPayload(text: (String) -> R, ..., notSet: () -> R): R =
    when (payload) {
        EventPayloadCase.TEXT -> text(this.text)
        EventPayloadCase.CODE -> code(this.code)
        ...
        EventPayloadCase.NOT_SET -> notSet()
    }
```

Verified with Kotlin 2.4.10: the call site inlines fully, no `Function` objects are created and
primitive lambda parameters are not boxed.

## 5. Compiler plugin

No changes. The design relies on three existing behaviors of `FirProtobufMessageGenerator`:

1. member properties of a `@GeneratedProtoMessage` interface are mirrored into `Builder` as `var`,
2. `clear<X>()` is generated on `Builder` for every `has<X>` in the presence interface,
3. extension properties and top-level classes are not mirrored.

`FirProtoMessageAnnotationChecker` validation is unaffected: it only checks the internal class, Builder,
`DESCRIPTOR` and `MARSHALLER`.

## 6. Companion change: inline presence mask

Not a prerequisite. The oneof design works unchanged on the current `BitSet`. It is listed here because
the two interact:

- `InternalMessage.presenceMask` is a `BitSet` object wrapping a `LongArray`, allocated for every
  message, including messages with no presence fields. That is two objects and about 40 bytes per
  message, more than the entire oneof storage this spec saves. If that stays, the oneof change is
  memory-neutral in practice rather than a win.
- Every oneof read and write now goes through presence bits: `has<Member>` and the member getters test a
  bit, setters clear the sibling range. With `BitSet` that is two dereferences plus a bounds check per
  access; with a `Long` field on the internal class it is a load and a mask. The current wrapper design
  reads a oneof with a single field load, so without the inline mask the new design is slightly slower
  to read than the old one, not faster.
- Sibling-range operations (`setExclusive`, `clearRange`) are one expression on a `Long` and need
  helper methods on `BitSet`.

Proposed change, tracked separately: messages with at most 64 presence bits store the mask as a single
`Long` field on the internal class; larger messages keep the array form. Ordering is a recommendation:
landing it first, or in the same release, keeps the oneof change from regressing read latency and makes
its memory numbers real.

## 7. Out of scope and unchanged

- Proto3 `optional` fields are synthetic oneofs and continue to map to `T?` with presence; they are not
  affected.
- Oneof members cannot be extensions; unchanged.
- Groups inside a oneof are message members and use the reference slot.
- `whenPayload` and `<oneof>` are not generated for synthetic oneofs.

## 8. Migration

Breaking for every user of `Message.Oneof.Variant(value)`:

| Before | After |
|---|---|
| `payload = Event.Payload.Text("hi")` | `text = "hi"` |
| `(e.payload as Event.Payload.Text).value` | `e.text` or `e.textOrNull` |
| `when (val p = e.payload) { is Event.Payload.Text -> p.value }` | `e.whenPayload(text = { it }, ...)` or `when (e.payload) { EventPayloadCase.TEXT -> e.text }` |
| `e.payload == null` | `e.payload == EventPayloadCase.NOT_SET` |

Regenerate: `protobuf/protobuf-wkt` (`Value.kind` and others), `tests/protobuf-conformance`, test protos.
Update: `protoc-gen/codegen.md`, `docs/.../grpc-generated-code.topic` (also fix the smart-cast example,
which does not compile today), `docs/.../grpc-limitations.topic` (remove the "being redesigned" entry),
`CHANGELOG.md`.

## 9. Tests

- `protobuf-lite` `oneof.proto` tests: set, clear, switch case, default-value set selects case, decode
  last-wins, message merge across two encodes, NaN and `-0.0` equality, unknown enum round-trip,
  `copy` switches case, `toString` prints only the active member.
- `name_conflicts.proto` and `name_shadowing.proto` oneof messages: keyword members, `not_set` member,
  shadowed types in members.
- Generator fixture tests in `protoc-gen/protobuf/src/test`: public, ext and internal output for a mixed
  oneof, a numeric-only 32-bit oneof, a reference-only oneof, and with `generateOneOfWhenFunctions=false`.
- Conformance suite must stay green.
- Allocation test (JVM): decoding a message with a numeric oneof allocates only the message itself.
