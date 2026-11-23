# Protobuf codegen

What the compiler plugin generates for a `@GeneratedProtoMessage` interface.
The protobuf `protoc-gen` plugin generates these interfaces and their internal classes: [protoc-gen codegen](../protoc-gen/codegen.md).

## Declarations (FIR)

`FirProtobufMessageGenerator` declares a builder and a companion in each interface that
`FirRpcPredicates.generatedProtoMessage` matches:

```
@GeneratedProtoMessage interface MyMessage { val x: Int; val y: String }   // y is a member of `oneof choice`
  ├─ interface Builder : MyMessage        key FirGeneratedProtoMessageBuilderKey
  │    ├─ abstract override var x: Int    key FirGeneratedProtoMessageBuilderPropertyKey
  │    ├─ abstract override var y: String
  │    ├─ abstract fun clearY()           key FirGeneratedProtoMessageBuilderFunctionKey
  │    └─ abstract fun clearChoice()
  └─ companion object                     key FirGeneratedProtoMessageCompanionObject
```

| Builder member | Declared for |
|---|---|
| `var <field>` | Each property of the message |
| `clear<Field>()` | Each field with a `has<Field>` getter in the generated `<Message>Presence` interface |
| `clear<OneOf>()` | Each name in `@GeneratedProtoOneOfs(names = [...])` on the internal class |

- The internal class implements the builder. Its name has the `Internal` suffix at each level:
  `MyMessage` has `MyMessageInternal`, and `Outer.Nested` has `OuterInternal.NestedInternal`.
- The generator reads only the presence interface and the annotations of the internal class.
  It never resolves the member scope of the internal class: that class extends the builder, so the resolution
  starts the builder generation again.
- The companion exists for the generated extensions, for example `operator fun MyMessage.Companion.invoke(...)`.

## Annotations (IR)

`ProtoDescriptorGenerator` adds two annotations to the message interface.
`protoDescriptorOf()` and `grpcMarshallerOf()` read them at runtime.

```kotlin
@WithProtoDescriptor(MyMessageInternal.DESCRIPTOR::class)
@WithGrpcMarshaller(MyMessageInternal.MARSHALLER::class)
interface MyMessage
```

## Diagnostics

`FirProtoMessageAnnotationChecker` reports `PROTO_MESSAGE_IS_GENERATED_ONLY` on a `@GeneratedProtoMessage`
declaration that the protobuf plugin didn't generate. A generated declaration passes all these checks:

1. The declaration is an interface.
2. The internal class and the nested `Builder` interface exist.
3. `Builder` extends the message interface.
4. The internal class extends `Builder`.
5. The internal class has a `DESCRIPTOR` object that extends `ProtoDescriptor`.
6. The internal class has a `MARSHALLER` object that implements `GrpcMarshaller`.
