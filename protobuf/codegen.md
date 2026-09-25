# Protobuf Codegen Reference

What the compiler plugin generates for `@GeneratedProtoMessage` interfaces.

These interfaces are produced by the separate `protoc-gen` plugin from `.proto` files (see [protoc-gen/codegen.md](../protoc-gen/codegen.md) for what the protoc plugin generates). 
The compiler plugin then adds FIR declarations and IR annotations to bridge the generated internal classes with the public message API.

## FIR Phase -- Declaration Shapes

`FirProtobufMessageGenerator` triggers on interfaces matched by `FirRpcPredicates.generatedProtoMessage`:

```
@GeneratedProtoMessage interface MyMessage { val x: Int; val y: String }   // y is a member of `oneof choice`
  ├─ nested interface Builder             (key: FirGeneratedProtoMessageBuilderKey)
  │    ├─ abstract override var x: Int    (key: FirGeneratedProtoMessageBuilderPropertyKey)
  │    ├─ abstract override var y: String (key: FirGeneratedProtoMessageBuilderPropertyKey)
  │    ├─ abstract fun clearY()           (key: FirGeneratedProtoMessageBuilderFunctionKey)
  │    └─ abstract fun clearChoice()      (key: FirGeneratedProtoMessageBuilderFunctionKey)
  └─ companion object                     (key: FirGeneratedProtoMessageCompanionObject)
```

### Builder Interface

- Extends the message interface itself (`Builder : MyMessage`)
- Properties mirror all properties of the message
- Properties are `var` (not `val`), `abstract`, `override`, `public`
- A `clear<Field>()` function is declared for every field with a `has<Field>` getter
  in the generated `<Message>Presence` interface
- A `clear<OneOf>()` function is declared for every name listed in the `@GeneratedProtoOneOfs(names = [...])`
  annotation of the internal class (`MyMessageInternal`)
- The functions are `abstract`, `public`, return `Unit` and are implemented by the internal class

Only the presence interface and the annotations of the internal class are inspected. The member scope of the
internal class is never resolved here: it extends the builder, so doing so would re-enter builder generation.

### Companion Object

Generated so that extensions can be added to it:
```kotlin
operator fun MyMessage.Companion.invoke(): MyMessage = ...
```

### Internal Class Resolution

For each `@GeneratedProtoMessage` interface, the processor finds its corresponding internal implementation:

```
MyMessage → MyMessageInternal
Outer.Nested → OuterInternal.NestedInternal
```

### Generated Annotations

`ProtoDescriptorGenerator.generate()` adds two annotations to the message interface:

```kotlin
@WithProtoDescriptor(MyMessageInternal.DESCRIPTOR::class)
@WithGrpcMarshaller(MyMessageInternal.MARSHALLER::class)
interface MyMessage { ... }
```

These annotations are used at runtime by `protoDescriptorOf()` and `grpcMarshallerOf()` respectively.

## FIR Diagnostics (`FirProtoMessageAnnotationChecker`)

Runs on all `@GeneratedProtoMessage` interfaces. Reports a single diagnostic:

| Diagnostic | Condition |
|---|---|
| `PROTO_MESSAGE_IS_GENERATED_ONLY` | Manual `@GeneratedProtoMessage` usage -- validation fails |

Validation checks (all must pass or `PROTO_MESSAGE_IS_GENERATED_ONLY` is reported):
1. Declaration is an interface
2. Internal class exists (e.g., `MyMessageInternal`)
3. Builder interface exists as nested class
4. Builder extends the message interface
5. Internal class extends the Builder interface
6. Internal class has `DESCRIPTOR` object extending `ProtoDescriptor`
7. Internal class has `MARSHALLER` object extending `GrpcMarshaller`
