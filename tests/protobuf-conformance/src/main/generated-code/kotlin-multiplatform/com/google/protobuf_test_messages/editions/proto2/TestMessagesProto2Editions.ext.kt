@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.editions.proto2

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = TestAllTypesProto2 {
*    optionalInt32 = ...
* }
* ```
*/
operator fun TestAllTypesProto2.Companion.invoke(body: TestAllTypesProto2Internal.() -> Unit): TestAllTypesProto2 {
    val msg = TestAllTypesProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    optionalInt32 = ...
* }
* ```
*/
fun TestAllTypesProto2.copy(body: TestAllTypesProto2Internal.() -> Unit = {}): TestAllTypesProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2] instance.
*/
val TestAllTypesProto2.presence: TestAllTypesProto2Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ForeignMessageProto2 {
*    c = ...
* }
* ```
*/
operator fun ForeignMessageProto2.Companion.invoke(body: ForeignMessageProto2Internal.() -> Unit): ForeignMessageProto2 {
    val msg = ForeignMessageProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    c = ...
* }
* ```
*/
fun ForeignMessageProto2.copy(body: ForeignMessageProto2Internal.() -> Unit = {}): ForeignMessageProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2] instance.
*/
val ForeignMessageProto2.presence: ForeignMessageProto2Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = GroupField {
*    groupInt32 = ...
* }
* ```
*/
operator fun GroupField.Companion.invoke(body: GroupFieldInternal.() -> Unit): GroupField {
    val msg = GroupFieldInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    groupInt32 = ...
* }
* ```
*/
fun GroupField.copy(body: GroupFieldInternal.() -> Unit = {}): GroupField {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.GroupField] instance.
*/
val GroupField.presence: GroupFieldPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = UnknownToTestAllTypes {
*    optionalInt32 = ...
* }
* ```
*/
operator fun UnknownToTestAllTypes.Companion.invoke(body: UnknownToTestAllTypesInternal.() -> Unit): UnknownToTestAllTypes {
    val msg = UnknownToTestAllTypesInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    optionalInt32 = ...
* }
* ```
*/
fun UnknownToTestAllTypes.copy(body: UnknownToTestAllTypesInternal.() -> Unit = {}): UnknownToTestAllTypes {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes] instance.
*/
val UnknownToTestAllTypes.presence: UnknownToTestAllTypesPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = NullHypothesisProto2 { }
* ```
*/
operator fun NullHypothesisProto2.Companion.invoke(body: NullHypothesisProto2Internal.() -> Unit): NullHypothesisProto2 {
    val msg = NullHypothesisProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun NullHypothesisProto2.copy(body: NullHypothesisProto2Internal.() -> Unit = {}): NullHypothesisProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = EnumOnlyProto2 { }
* ```
*/
operator fun EnumOnlyProto2.Companion.invoke(body: EnumOnlyProto2Internal.() -> Unit): EnumOnlyProto2 {
    val msg = EnumOnlyProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun EnumOnlyProto2.copy(body: EnumOnlyProto2Internal.() -> Unit = {}): EnumOnlyProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = OneStringProto2 {
*    data = ...
* }
* ```
*/
operator fun OneStringProto2.Companion.invoke(body: OneStringProto2Internal.() -> Unit): OneStringProto2 {
    val msg = OneStringProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    data = ...
* }
* ```
*/
fun OneStringProto2.copy(body: OneStringProto2Internal.() -> Unit = {}): OneStringProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.OneStringProto2] instance.
*/
val OneStringProto2.presence: OneStringProto2Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ProtoWithKeywords {
*    inline = ...
* }
* ```
*/
operator fun ProtoWithKeywords.Companion.invoke(body: ProtoWithKeywordsInternal.() -> Unit): ProtoWithKeywords {
    val msg = ProtoWithKeywordsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    inline = ...
* }
* ```
*/
fun ProtoWithKeywords.copy(body: ProtoWithKeywordsInternal.() -> Unit = {}): ProtoWithKeywords {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords] instance.
*/
val ProtoWithKeywords.presence: ProtoWithKeywordsPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = TestAllRequiredTypesProto2 {
*    requiredInt32 = ...
* }
* ```
*/
operator fun TestAllRequiredTypesProto2.Companion.invoke(body: TestAllRequiredTypesProto2Internal.() -> Unit): TestAllRequiredTypesProto2 {
    val msg = TestAllRequiredTypesProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    requiredInt32 = ...
* }
* ```
*/
fun TestAllRequiredTypesProto2.copy(body: TestAllRequiredTypesProto2Internal.() -> Unit = {}): TestAllRequiredTypesProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2] instance.
*/
val TestAllRequiredTypesProto2.presence: TestAllRequiredTypesProto2Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = TestLargeOneof {
*    largeOneof = ...
* }
* ```
*/
operator fun TestLargeOneof.Companion.invoke(body: TestLargeOneofInternal.() -> Unit): TestLargeOneof {
    val msg = TestLargeOneofInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    largeOneof = ...
* }
* ```
*/
fun TestLargeOneof.copy(body: TestLargeOneofInternal.() -> Unit = {}): TestLargeOneof {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = NestedMessage {
*    a = ...
* }
* ```
*/
operator fun TestAllTypesProto2.NestedMessage.Companion.invoke(body: TestAllTypesProto2Internal.NestedMessageInternal.() -> Unit): TestAllTypesProto2.NestedMessage {
    val msg = TestAllTypesProto2Internal.NestedMessageInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    a = ...
* }
* ```
*/
fun TestAllTypesProto2.NestedMessage.copy(body: TestAllTypesProto2Internal.NestedMessageInternal.() -> Unit = {}): TestAllTypesProto2.NestedMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage] instance.
*/
val TestAllTypesProto2.NestedMessage.presence: TestAllTypesProto2Presence.NestedMessage get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = Data {
*    groupInt32 = ...
* }
* ```
*/
operator fun TestAllTypesProto2.Data.Companion.invoke(body: TestAllTypesProto2Internal.DataInternal.() -> Unit): TestAllTypesProto2.Data {
    val msg = TestAllTypesProto2Internal.DataInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    groupInt32 = ...
* }
* ```
*/
fun TestAllTypesProto2.Data.copy(body: TestAllTypesProto2Internal.DataInternal.() -> Unit = {}): TestAllTypesProto2.Data {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data] instance.
*/
val TestAllTypesProto2.Data.presence: TestAllTypesProto2Presence.Data get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MultiWordGroupField {
*    groupInt32 = ...
* }
* ```
*/
operator fun TestAllTypesProto2.MultiWordGroupField.Companion.invoke(body: TestAllTypesProto2Internal.MultiWordGroupFieldInternal.() -> Unit): TestAllTypesProto2.MultiWordGroupField {
    val msg = TestAllTypesProto2Internal.MultiWordGroupFieldInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    groupInt32 = ...
* }
* ```
*/
fun TestAllTypesProto2.MultiWordGroupField.copy(body: TestAllTypesProto2Internal.MultiWordGroupFieldInternal.() -> Unit = {}): TestAllTypesProto2.MultiWordGroupField {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField] instance.
*/
val TestAllTypesProto2.MultiWordGroupField.presence: TestAllTypesProto2Presence.MultiWordGroupField get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrect { }
* ```
*/
operator fun TestAllTypesProto2.MessageSetCorrect.Companion.invoke(body: TestAllTypesProto2Internal.MessageSetCorrectInternal.() -> Unit): TestAllTypesProto2.MessageSetCorrect {
    val msg = TestAllTypesProto2Internal.MessageSetCorrectInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun TestAllTypesProto2.MessageSetCorrect.copy(body: TestAllTypesProto2Internal.MessageSetCorrectInternal.() -> Unit = {}): TestAllTypesProto2.MessageSetCorrect {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrectExtension1 {
*    str = ...
* }
* ```
*/
operator fun TestAllTypesProto2.MessageSetCorrectExtension1.Companion.invoke(body: TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit): TestAllTypesProto2.MessageSetCorrectExtension1 {
    val msg = TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    str = ...
* }
* ```
*/
fun TestAllTypesProto2.MessageSetCorrectExtension1.copy(body: TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit = {}): TestAllTypesProto2.MessageSetCorrectExtension1 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1] instance.
*/
val TestAllTypesProto2.MessageSetCorrectExtension1.presence: TestAllTypesProto2Presence.MessageSetCorrectExtension1 get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrectExtension2 {
*    i = ...
* }
* ```
*/
operator fun TestAllTypesProto2.MessageSetCorrectExtension2.Companion.invoke(body: TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit): TestAllTypesProto2.MessageSetCorrectExtension2 {
    val msg = TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    i = ...
* }
* ```
*/
fun TestAllTypesProto2.MessageSetCorrectExtension2.copy(body: TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit = {}): TestAllTypesProto2.MessageSetCorrectExtension2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2] instance.
*/
val TestAllTypesProto2.MessageSetCorrectExtension2.presence: TestAllTypesProto2Presence.MessageSetCorrectExtension2 get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ExtensionWithOneof {
*    oneofField = ...
* }
* ```
*/
operator fun TestAllTypesProto2.ExtensionWithOneof.Companion.invoke(body: TestAllTypesProto2Internal.ExtensionWithOneofInternal.() -> Unit): TestAllTypesProto2.ExtensionWithOneof {
    val msg = TestAllTypesProto2Internal.ExtensionWithOneofInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    oneofField = ...
* }
* ```
*/
fun TestAllTypesProto2.ExtensionWithOneof.copy(body: TestAllTypesProto2Internal.ExtensionWithOneofInternal.() -> Unit = {}): TestAllTypesProto2.ExtensionWithOneof {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = OptionalGroup {
*    a = ...
* }
* ```
*/
operator fun UnknownToTestAllTypes.OptionalGroup.Companion.invoke(body: UnknownToTestAllTypesInternal.OptionalGroupInternal.() -> Unit): UnknownToTestAllTypes.OptionalGroup {
    val msg = UnknownToTestAllTypesInternal.OptionalGroupInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    a = ...
* }
* ```
*/
fun UnknownToTestAllTypes.OptionalGroup.copy(body: UnknownToTestAllTypesInternal.OptionalGroupInternal.() -> Unit = {}): UnknownToTestAllTypes.OptionalGroup {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup] instance.
*/
val UnknownToTestAllTypes.OptionalGroup.presence: UnknownToTestAllTypesPresence.OptionalGroup get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = NestedMessage {
*    a = ...
* }
* ```
*/
operator fun TestAllRequiredTypesProto2.NestedMessage.Companion.invoke(body: TestAllRequiredTypesProto2Internal.NestedMessageInternal.() -> Unit): TestAllRequiredTypesProto2.NestedMessage {
    val msg = TestAllRequiredTypesProto2Internal.NestedMessageInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    a = ...
* }
* ```
*/
fun TestAllRequiredTypesProto2.NestedMessage.copy(body: TestAllRequiredTypesProto2Internal.NestedMessageInternal.() -> Unit = {}): TestAllRequiredTypesProto2.NestedMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage] instance.
*/
val TestAllRequiredTypesProto2.NestedMessage.presence: TestAllRequiredTypesProto2Presence.NestedMessage get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = Data {
*    groupInt32 = ...
* }
* ```
*/
operator fun TestAllRequiredTypesProto2.Data.Companion.invoke(body: TestAllRequiredTypesProto2Internal.DataInternal.() -> Unit): TestAllRequiredTypesProto2.Data {
    val msg = TestAllRequiredTypesProto2Internal.DataInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    groupInt32 = ...
* }
* ```
*/
fun TestAllRequiredTypesProto2.Data.copy(body: TestAllRequiredTypesProto2Internal.DataInternal.() -> Unit = {}): TestAllRequiredTypesProto2.Data {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data] instance.
*/
val TestAllRequiredTypesProto2.Data.presence: TestAllRequiredTypesProto2Presence.Data get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrect { }
* ```
*/
operator fun TestAllRequiredTypesProto2.MessageSetCorrect.Companion.invoke(body: TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.() -> Unit): TestAllRequiredTypesProto2.MessageSetCorrect {
    val msg = TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun TestAllRequiredTypesProto2.MessageSetCorrect.copy(body: TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.() -> Unit = {}): TestAllRequiredTypesProto2.MessageSetCorrect {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrectExtension1 {
*    str = ...
* }
* ```
*/
operator fun TestAllRequiredTypesProto2.MessageSetCorrectExtension1.Companion.invoke(body: TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit): TestAllRequiredTypesProto2.MessageSetCorrectExtension1 {
    val msg = TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    str = ...
* }
* ```
*/
fun TestAllRequiredTypesProto2.MessageSetCorrectExtension1.copy(body: TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit = {}): TestAllRequiredTypesProto2.MessageSetCorrectExtension1 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1] instance.
*/
val TestAllRequiredTypesProto2.MessageSetCorrectExtension1.presence: TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension1 get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrectExtension2 {
*    i = ...
* }
* ```
*/
operator fun TestAllRequiredTypesProto2.MessageSetCorrectExtension2.Companion.invoke(body: TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit): TestAllRequiredTypesProto2.MessageSetCorrectExtension2 {
    val msg = TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    i = ...
* }
* ```
*/
fun TestAllRequiredTypesProto2.MessageSetCorrectExtension2.copy(body: TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit = {}): TestAllRequiredTypesProto2.MessageSetCorrectExtension2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2] instance.
*/
val TestAllRequiredTypesProto2.MessageSetCorrectExtension2.presence: TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension2 get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = A1 { }
* ```
*/
operator fun TestLargeOneof.A1.Companion.invoke(body: TestLargeOneofInternal.A1Internal.() -> Unit): TestLargeOneof.A1 {
    val msg = TestLargeOneofInternal.A1Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun TestLargeOneof.A1.copy(body: TestLargeOneofInternal.A1Internal.() -> Unit = {}): TestLargeOneof.A1 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A2 { }
* ```
*/
operator fun TestLargeOneof.A2.Companion.invoke(body: TestLargeOneofInternal.A2Internal.() -> Unit): TestLargeOneof.A2 {
    val msg = TestLargeOneofInternal.A2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun TestLargeOneof.A2.copy(body: TestLargeOneofInternal.A2Internal.() -> Unit = {}): TestLargeOneof.A2 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A3 { }
* ```
*/
operator fun TestLargeOneof.A3.Companion.invoke(body: TestLargeOneofInternal.A3Internal.() -> Unit): TestLargeOneof.A3 {
    val msg = TestLargeOneofInternal.A3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun TestLargeOneof.A3.copy(body: TestLargeOneofInternal.A3Internal.() -> Unit = {}): TestLargeOneof.A3 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A4 { }
* ```
*/
operator fun TestLargeOneof.A4.Companion.invoke(body: TestLargeOneofInternal.A4Internal.() -> Unit): TestLargeOneof.A4 {
    val msg = TestLargeOneofInternal.A4Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun TestLargeOneof.A4.copy(body: TestLargeOneofInternal.A4Internal.() -> Unit = {}): TestLargeOneof.A4 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A5 { }
* ```
*/
operator fun TestLargeOneof.A5.Companion.invoke(body: TestLargeOneofInternal.A5Internal.() -> Unit): TestLargeOneof.A5 {
    val msg = TestLargeOneofInternal.A5Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun TestLargeOneof.A5.copy(body: TestLargeOneofInternal.A5Internal.() -> Unit = {}): TestLargeOneof.A5 {
    return this.asInternal().copyInternal(body)
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.presence] extension property.
*/
interface TestAllTypesProto2Presence {
    val hasOptionalInt32: Boolean

    val hasOptionalInt64: Boolean

    val hasOptionalUint32: Boolean

    val hasOptionalUint64: Boolean

    val hasOptionalSint32: Boolean

    val hasOptionalSint64: Boolean

    val hasOptionalFixed32: Boolean

    val hasOptionalFixed64: Boolean

    val hasOptionalSfixed32: Boolean

    val hasOptionalSfixed64: Boolean

    val hasOptionalFloat: Boolean

    val hasOptionalDouble: Boolean

    val hasOptionalBool: Boolean

    val hasOptionalString: Boolean

    val hasOptionalBytes: Boolean

    val hasOptionalNestedMessage: Boolean

    val hasOptionalForeignMessage: Boolean

    val hasOptionalNestedEnum: Boolean

    val hasOptionalForeignEnum: Boolean

    val hasOptionalStringPiece: Boolean

    val hasOptionalCord: Boolean

    val hasRecursiveMessage: Boolean

    val hasData: Boolean

    val hasMultiwordgroupfield: Boolean

    val hasDefaultInt32: Boolean

    val hasDefaultInt64: Boolean

    val hasDefaultUint32: Boolean

    val hasDefaultUint64: Boolean

    val hasDefaultSint32: Boolean

    val hasDefaultSint64: Boolean

    val hasDefaultFixed32: Boolean

    val hasDefaultFixed64: Boolean

    val hasDefaultSfixed32: Boolean

    val hasDefaultSfixed64: Boolean

    val hasDefaultFloat: Boolean

    val hasDefaultDouble: Boolean

    val hasDefaultBool: Boolean

    val hasDefaultString: Boolean

    val hasDefaultBytes: Boolean

    val hasFieldname1: Boolean

    val hasFieldName2: Boolean

    val hasFieldName3: Boolean

    val hasField_Name4_: Boolean

    val hasField0name5: Boolean

    val hasField_0Name6: Boolean

    val hasFieldName7: Boolean

    val hasFieldName8: Boolean

    val hasField_Name9: Boolean

    val hasField_Name10: Boolean

    val hasFIELD_NAME11: Boolean

    val hasFIELDName12: Boolean

    val has_FieldName13: Boolean

    val has__FieldName14: Boolean

    val hasField_Name15: Boolean

    val hasField__Name16: Boolean

    val hasFieldName17__: Boolean

    val hasFieldName18__: Boolean

    val hasMessageSetCorrect: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage.presence] extension property.
    */
    interface NestedMessage {
        val hasA: Boolean

        val hasCorecursive: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data.presence] extension property.
    */
    interface Data {
        val hasGroupInt32: Boolean

        val hasGroupUint32: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField.presence] extension property.
    */
    interface MultiWordGroupField {
        val hasGroupInt32: Boolean

        val hasGroupUint32: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1.presence] extension property.
    */
    interface MessageSetCorrectExtension1 {
        val hasStr: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2.presence] extension property.
    */
    interface MessageSetCorrectExtension2 {
        val hasI: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2.presence] extension property.
*/
interface ForeignMessageProto2Presence {
    val hasC: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.GroupField] messages.
* Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.GroupField.presence] extension property.
*/
interface GroupFieldPresence {
    val hasGroupInt32: Boolean

    val hasGroupUint32: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes] messages.
* Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.presence] extension property.
*/
interface UnknownToTestAllTypesPresence {
    val hasOptionalInt32: Boolean

    val hasOptionalString: Boolean

    val hasNestedMessage: Boolean

    val hasOptionalgroup: Boolean

    val hasOptionalBool: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup.presence] extension property.
    */
    interface OptionalGroup {
        val hasA: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.OneStringProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.OneStringProto2.presence] extension property.
*/
interface OneStringProto2Presence {
    val hasData: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords] messages.
* Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords.presence] extension property.
*/
interface ProtoWithKeywordsPresence {
    val hasInline: Boolean

    val hasConcept: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.presence] extension property.
*/
interface TestAllRequiredTypesProto2Presence {
    val hasRequiredInt32: Boolean

    val hasRequiredInt64: Boolean

    val hasRequiredUint32: Boolean

    val hasRequiredUint64: Boolean

    val hasRequiredSint32: Boolean

    val hasRequiredSint64: Boolean

    val hasRequiredFixed32: Boolean

    val hasRequiredFixed64: Boolean

    val hasRequiredSfixed32: Boolean

    val hasRequiredSfixed64: Boolean

    val hasRequiredFloat: Boolean

    val hasRequiredDouble: Boolean

    val hasRequiredBool: Boolean

    val hasRequiredString: Boolean

    val hasRequiredBytes: Boolean

    val hasRequiredNestedMessage: Boolean

    val hasRequiredForeignMessage: Boolean

    val hasRequiredNestedEnum: Boolean

    val hasRequiredForeignEnum: Boolean

    val hasRequiredStringPiece: Boolean

    val hasRequiredCord: Boolean

    val hasRecursiveMessage: Boolean

    val hasOptionalRecursiveMessage: Boolean

    val hasData: Boolean

    val hasDefaultInt32: Boolean

    val hasDefaultInt64: Boolean

    val hasDefaultUint32: Boolean

    val hasDefaultUint64: Boolean

    val hasDefaultSint32: Boolean

    val hasDefaultSint64: Boolean

    val hasDefaultFixed32: Boolean

    val hasDefaultFixed64: Boolean

    val hasDefaultSfixed32: Boolean

    val hasDefaultSfixed64: Boolean

    val hasDefaultFloat: Boolean

    val hasDefaultDouble: Boolean

    val hasDefaultBool: Boolean

    val hasDefaultString: Boolean

    val hasDefaultBytes: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage.presence] extension property.
    */
    interface NestedMessage {
        val hasA: Boolean

        val hasCorecursive: Boolean

        val hasOptionalCorecursive: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data.presence] extension property.
    */
    interface Data {
        val hasGroupInt32: Boolean

        val hasGroupUint32: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.presence] extension property.
    */
    interface MessageSetCorrectExtension1 {
        val hasStr: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.presence] extension property.
    */
    interface MessageSetCorrectExtension2 {
        val hasI: Boolean
    }
}
