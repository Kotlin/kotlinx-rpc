@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.proto2

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = TestAllTypesProto2 {
*    optionalInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllTypesProto2 { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.copy(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllTypesProto2 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllTypesProto2.presence: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ForeignMessageProto2 {
*    c = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.ForeignMessageProto2.Companion.invoke(body: com.google.protobuf_test_messages.proto2.ForeignMessageProto2Internal.() -> Unit): com.google.protobuf_test_messages.proto2.ForeignMessageProto2 { 
    val msg = com.google.protobuf_test_messages.proto2.ForeignMessageProto2Internal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.ForeignMessageProto2.copy(body: com.google.protobuf_test_messages.proto2.ForeignMessageProto2Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.ForeignMessageProto2 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.ForeignMessageProto2] instance.
*/
val com.google.protobuf_test_messages.proto2.ForeignMessageProto2.presence: com.google.protobuf_test_messages.proto2.ForeignMessageProto2Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = GroupField {
*    groupInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.GroupField.Companion.invoke(body: com.google.protobuf_test_messages.proto2.GroupFieldInternal.() -> Unit): com.google.protobuf_test_messages.proto2.GroupField { 
    val msg = com.google.protobuf_test_messages.proto2.GroupFieldInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.GroupField.copy(body: com.google.protobuf_test_messages.proto2.GroupFieldInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.GroupField { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.GroupField] instance.
*/
val com.google.protobuf_test_messages.proto2.GroupField.presence: com.google.protobuf_test_messages.proto2.GroupFieldPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = UnknownToTestAllTypes {
*    optionalInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.Companion.invoke(body: com.google.protobuf_test_messages.proto2.UnknownToTestAllTypesInternal.() -> Unit): com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes { 
    val msg = com.google.protobuf_test_messages.proto2.UnknownToTestAllTypesInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.copy(body: com.google.protobuf_test_messages.proto2.UnknownToTestAllTypesInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes] instance.
*/
val com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.presence: com.google.protobuf_test_messages.proto2.UnknownToTestAllTypesPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = NullHypothesisProto2 { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.NullHypothesisProto2.Companion.invoke(body: com.google.protobuf_test_messages.proto2.NullHypothesisProto2Internal.() -> Unit): com.google.protobuf_test_messages.proto2.NullHypothesisProto2 { 
    val msg = com.google.protobuf_test_messages.proto2.NullHypothesisProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto2.NullHypothesisProto2.copy(body: com.google.protobuf_test_messages.proto2.NullHypothesisProto2Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.NullHypothesisProto2 { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = EnumOnlyProto2 { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.EnumOnlyProto2.Companion.invoke(body: com.google.protobuf_test_messages.proto2.EnumOnlyProto2Internal.() -> Unit): com.google.protobuf_test_messages.proto2.EnumOnlyProto2 { 
    val msg = com.google.protobuf_test_messages.proto2.EnumOnlyProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto2.EnumOnlyProto2.copy(body: com.google.protobuf_test_messages.proto2.EnumOnlyProto2Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.EnumOnlyProto2 { 
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
operator fun com.google.protobuf_test_messages.proto2.OneStringProto2.Companion.invoke(body: com.google.protobuf_test_messages.proto2.OneStringProto2Internal.() -> Unit): com.google.protobuf_test_messages.proto2.OneStringProto2 { 
    val msg = com.google.protobuf_test_messages.proto2.OneStringProto2Internal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.OneStringProto2.copy(body: com.google.protobuf_test_messages.proto2.OneStringProto2Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.OneStringProto2 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.OneStringProto2] instance.
*/
val com.google.protobuf_test_messages.proto2.OneStringProto2.presence: com.google.protobuf_test_messages.proto2.OneStringProto2Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ProtoWithKeywords {
*    inline = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.ProtoWithKeywords.Companion.invoke(body: com.google.protobuf_test_messages.proto2.ProtoWithKeywordsInternal.() -> Unit): com.google.protobuf_test_messages.proto2.ProtoWithKeywords { 
    val msg = com.google.protobuf_test_messages.proto2.ProtoWithKeywordsInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.ProtoWithKeywords.copy(body: com.google.protobuf_test_messages.proto2.ProtoWithKeywordsInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.ProtoWithKeywords { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.ProtoWithKeywords] instance.
*/
val com.google.protobuf_test_messages.proto2.ProtoWithKeywords.presence: com.google.protobuf_test_messages.proto2.ProtoWithKeywordsPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = TestAllRequiredTypesProto2 {
*    requiredInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2 { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.copy(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.presence: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = TestLargeOneof {
*    largeOneof = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestLargeOneof.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.() -> Unit): com.google.protobuf_test_messages.proto2.TestLargeOneof { 
    val msg = com.google.protobuf_test_messages.proto2.TestLargeOneofInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestLargeOneof.copy(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestLargeOneof { 
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
operator fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.NestedMessageInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage.copy(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.NestedMessageInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage.presence: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Presence.NestedMessage get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = Data {
*    groupInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.DataInternal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.DataInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data.copy(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.DataInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data.presence: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Presence.Data get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MultiWordGroupField {
*    groupInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField.copy(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField.presence: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Presence.MultiWordGroupField get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrect { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect.copy(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect { 
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
operator fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1 { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1.copy(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1.presence: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Presence.MessageSetCorrectExtension1 get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrectExtension2 {
*    i = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2 { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2.copy(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2.presence: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Presence.MessageSetCorrectExtension2 get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ExtensionWithOneof {
*    oneofField = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.ExtensionWithOneof.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.ExtensionWithOneof { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllTypesProto2.ExtensionWithOneof.copy(body: com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllTypesProto2.ExtensionWithOneof { 
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
operator fun com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup.Companion.invoke(body: com.google.protobuf_test_messages.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal.() -> Unit): com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup { 
    val msg = com.google.protobuf_test_messages.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup.copy(body: com.google.protobuf_test_messages.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup] instance.
*/
val com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup.presence: com.google.protobuf_test_messages.proto2.UnknownToTestAllTypesPresence.OptionalGroup get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = NestedMessage {
*    a = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage.copy(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage.presence: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Presence.NestedMessage get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = Data {
*    groupInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.DataInternal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.DataInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data.copy(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.DataInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data.presence: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Presence.Data get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrect { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect.copy(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect { 
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
operator fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1 { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.copy(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.presence: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension1 get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrectExtension2 {
*    i = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2 { 
    val msg = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal().apply(body)
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
fun com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.copy(body: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2] instance.
*/
val com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.presence: com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension2 get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = A1 { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A1.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A1Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestLargeOneof.A1 { 
    val msg = com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A1Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A1.copy(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A1Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestLargeOneof.A1 { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A2 { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A2.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A2Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestLargeOneof.A2 { 
    val msg = com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A2.copy(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A2Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestLargeOneof.A2 { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A3 { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A3.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A3Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestLargeOneof.A3 { 
    val msg = com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A3.copy(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A3Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestLargeOneof.A3 { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A4 { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A4.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A4Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestLargeOneof.A4 { 
    val msg = com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A4Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A4.copy(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A4Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestLargeOneof.A4 { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A5 { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A5.Companion.invoke(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A5Internal.() -> Unit): com.google.protobuf_test_messages.proto2.TestLargeOneof.A5 { 
    val msg = com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A5Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto2.TestLargeOneof.A5.copy(body: com.google.protobuf_test_messages.proto2.TestLargeOneofInternal.A5Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto2.TestLargeOneof.A5 { 
    return this.asInternal().copyInternal(body)
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.presence] extension property.
*/
interface TestAllTypesProto2Presence { 
    val hasOptionalInt32: kotlin.Boolean

    val hasOptionalInt64: kotlin.Boolean

    val hasOptionalUint32: kotlin.Boolean

    val hasOptionalUint64: kotlin.Boolean

    val hasOptionalSint32: kotlin.Boolean

    val hasOptionalSint64: kotlin.Boolean

    val hasOptionalFixed32: kotlin.Boolean

    val hasOptionalFixed64: kotlin.Boolean

    val hasOptionalSfixed32: kotlin.Boolean

    val hasOptionalSfixed64: kotlin.Boolean

    val hasOptionalFloat: kotlin.Boolean

    val hasOptionalDouble: kotlin.Boolean

    val hasOptionalBool: kotlin.Boolean

    val hasOptionalString: kotlin.Boolean

    val hasOptionalBytes: kotlin.Boolean

    val hasOptionalNestedMessage: kotlin.Boolean

    val hasOptionalForeignMessage: kotlin.Boolean

    val hasOptionalNestedEnum: kotlin.Boolean

    val hasOptionalForeignEnum: kotlin.Boolean

    val hasOptionalStringPiece: kotlin.Boolean

    val hasOptionalCord: kotlin.Boolean

    val hasRecursiveMessage: kotlin.Boolean

    val hasData: kotlin.Boolean

    val hasMultiwordgroupfield: kotlin.Boolean

    val hasDefaultInt32: kotlin.Boolean

    val hasDefaultInt64: kotlin.Boolean

    val hasDefaultUint32: kotlin.Boolean

    val hasDefaultUint64: kotlin.Boolean

    val hasDefaultSint32: kotlin.Boolean

    val hasDefaultSint64: kotlin.Boolean

    val hasDefaultFixed32: kotlin.Boolean

    val hasDefaultFixed64: kotlin.Boolean

    val hasDefaultSfixed32: kotlin.Boolean

    val hasDefaultSfixed64: kotlin.Boolean

    val hasDefaultFloat: kotlin.Boolean

    val hasDefaultDouble: kotlin.Boolean

    val hasDefaultBool: kotlin.Boolean

    val hasDefaultString: kotlin.Boolean

    val hasDefaultBytes: kotlin.Boolean

    val hasFieldname1: kotlin.Boolean

    val hasFieldName2: kotlin.Boolean

    val hasFieldName3: kotlin.Boolean

    val hasField_Name4_: kotlin.Boolean

    val hasField0name5: kotlin.Boolean

    val hasField_0Name6: kotlin.Boolean

    val hasFieldName7: kotlin.Boolean

    val hasFieldName8: kotlin.Boolean

    val hasField_Name9: kotlin.Boolean

    val hasField_Name10: kotlin.Boolean

    val hasFIELD_NAME11: kotlin.Boolean

    val hasFIELDName12: kotlin.Boolean

    val has_FieldName13: kotlin.Boolean

    val has__FieldName14: kotlin.Boolean

    val hasField_Name15: kotlin.Boolean

    val hasField__Name16: kotlin.Boolean

    val hasFieldName17__: kotlin.Boolean

    val hasFieldName18__: kotlin.Boolean

    val hasMessageSetCorrect: kotlin.Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage.presence] extension property.
    */
    interface NestedMessage { 
        val hasA: kotlin.Boolean

        val hasCorecursive: kotlin.Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data.presence] extension property.
    */
    interface Data { 
        val hasGroupInt32: kotlin.Boolean

        val hasGroupUint32: kotlin.Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField.presence] extension property.
    */
    interface MultiWordGroupField { 
        val hasGroupInt32: kotlin.Boolean

        val hasGroupUint32: kotlin.Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1.presence] extension property.
    */
    interface MessageSetCorrectExtension1 { 
        val hasStr: kotlin.Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2.presence] extension property.
    */
    interface MessageSetCorrectExtension2 { 
        val hasI: kotlin.Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.ForeignMessageProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.ForeignMessageProto2.presence] extension property.
*/
interface ForeignMessageProto2Presence { 
    val hasC: kotlin.Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.GroupField] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.GroupField.presence] extension property.
*/
interface GroupFieldPresence { 
    val hasGroupInt32: kotlin.Boolean

    val hasGroupUint32: kotlin.Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.presence] extension property.
*/
interface UnknownToTestAllTypesPresence { 
    val hasOptionalInt32: kotlin.Boolean

    val hasOptionalString: kotlin.Boolean

    val hasNestedMessage: kotlin.Boolean

    val hasOptionalgroup: kotlin.Boolean

    val hasOptionalBool: kotlin.Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup.presence] extension property.
    */
    interface OptionalGroup { 
        val hasA: kotlin.Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.OneStringProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.OneStringProto2.presence] extension property.
*/
interface OneStringProto2Presence { 
    val hasData: kotlin.Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.ProtoWithKeywords] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.ProtoWithKeywords.presence] extension property.
*/
interface ProtoWithKeywordsPresence { 
    val hasInline: kotlin.Boolean

    val hasConcept: kotlin.Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.presence] extension property.
*/
interface TestAllRequiredTypesProto2Presence { 
    val hasRequiredInt32: kotlin.Boolean

    val hasRequiredInt64: kotlin.Boolean

    val hasRequiredUint32: kotlin.Boolean

    val hasRequiredUint64: kotlin.Boolean

    val hasRequiredSint32: kotlin.Boolean

    val hasRequiredSint64: kotlin.Boolean

    val hasRequiredFixed32: kotlin.Boolean

    val hasRequiredFixed64: kotlin.Boolean

    val hasRequiredSfixed32: kotlin.Boolean

    val hasRequiredSfixed64: kotlin.Boolean

    val hasRequiredFloat: kotlin.Boolean

    val hasRequiredDouble: kotlin.Boolean

    val hasRequiredBool: kotlin.Boolean

    val hasRequiredString: kotlin.Boolean

    val hasRequiredBytes: kotlin.Boolean

    val hasRequiredNestedMessage: kotlin.Boolean

    val hasRequiredForeignMessage: kotlin.Boolean

    val hasRequiredNestedEnum: kotlin.Boolean

    val hasRequiredForeignEnum: kotlin.Boolean

    val hasRequiredStringPiece: kotlin.Boolean

    val hasRequiredCord: kotlin.Boolean

    val hasRecursiveMessage: kotlin.Boolean

    val hasOptionalRecursiveMessage: kotlin.Boolean

    val hasData: kotlin.Boolean

    val hasDefaultInt32: kotlin.Boolean

    val hasDefaultInt64: kotlin.Boolean

    val hasDefaultUint32: kotlin.Boolean

    val hasDefaultUint64: kotlin.Boolean

    val hasDefaultSint32: kotlin.Boolean

    val hasDefaultSint64: kotlin.Boolean

    val hasDefaultFixed32: kotlin.Boolean

    val hasDefaultFixed64: kotlin.Boolean

    val hasDefaultSfixed32: kotlin.Boolean

    val hasDefaultSfixed64: kotlin.Boolean

    val hasDefaultFloat: kotlin.Boolean

    val hasDefaultDouble: kotlin.Boolean

    val hasDefaultBool: kotlin.Boolean

    val hasDefaultString: kotlin.Boolean

    val hasDefaultBytes: kotlin.Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage.presence] extension property.
    */
    interface NestedMessage { 
        val hasA: kotlin.Boolean

        val hasCorecursive: kotlin.Boolean

        val hasOptionalCorecursive: kotlin.Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data.presence] extension property.
    */
    interface Data { 
        val hasGroupInt32: kotlin.Boolean

        val hasGroupUint32: kotlin.Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.presence] extension property.
    */
    interface MessageSetCorrectExtension1 { 
        val hasStr: kotlin.Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.presence] extension property.
    */
    interface MessageSetCorrectExtension2 { 
        val hasI: kotlin.Boolean
    }
}
