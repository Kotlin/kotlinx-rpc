@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.editions.proto3

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = TestAllTypesProto3 {
*    optionalInt32 = ...
* }
* ```
*/
operator fun TestAllTypesProto3.Companion.invoke(body: TestAllTypesProto3Internal.() -> Unit): TestAllTypesProto3 {
    val msg = TestAllTypesProto3Internal().apply(body)
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
fun TestAllTypesProto3.copy(body: TestAllTypesProto3Internal.() -> Unit = {}): TestAllTypesProto3 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3] instance.
*/
val TestAllTypesProto3.presence: TestAllTypesProto3Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ForeignMessage {
*    c = ...
* }
* ```
*/
operator fun ForeignMessage.Companion.invoke(body: ForeignMessageInternal.() -> Unit): ForeignMessage {
    val msg = ForeignMessageInternal().apply(body)
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
fun ForeignMessage.copy(body: ForeignMessageInternal.() -> Unit = {}): ForeignMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = NullHypothesisProto3 { }
* ```
*/
operator fun NullHypothesisProto3.Companion.invoke(body: NullHypothesisProto3Internal.() -> Unit): NullHypothesisProto3 {
    val msg = NullHypothesisProto3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun NullHypothesisProto3.copy(body: NullHypothesisProto3Internal.() -> Unit = {}): NullHypothesisProto3 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = EnumOnlyProto3 { }
* ```
*/
operator fun EnumOnlyProto3.Companion.invoke(body: EnumOnlyProto3Internal.() -> Unit): EnumOnlyProto3 {
    val msg = EnumOnlyProto3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun EnumOnlyProto3.copy(body: EnumOnlyProto3Internal.() -> Unit = {}): EnumOnlyProto3 {
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
operator fun TestAllTypesProto3.NestedMessage.Companion.invoke(body: TestAllTypesProto3Internal.NestedMessageInternal.() -> Unit): TestAllTypesProto3.NestedMessage {
    val msg = TestAllTypesProto3Internal.NestedMessageInternal().apply(body)
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
fun TestAllTypesProto3.NestedMessage.copy(body: TestAllTypesProto3Internal.NestedMessageInternal.() -> Unit = {}): TestAllTypesProto3.NestedMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedMessage] instance.
*/
val TestAllTypesProto3.NestedMessage.presence: TestAllTypesProto3Presence.NestedMessage get() = this.asInternal()._presence

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3] messages.
* Retrieve it via the [com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.presence] extension property.
*/
interface TestAllTypesProto3Presence {
    val hasOptionalNestedMessage: Boolean

    val hasOptionalForeignMessage: Boolean

    val hasRecursiveMessage: Boolean

    val hasOptionalBoolWrapper: Boolean

    val hasOptionalInt32Wrapper: Boolean

    val hasOptionalInt64Wrapper: Boolean

    val hasOptionalUint32Wrapper: Boolean

    val hasOptionalUint64Wrapper: Boolean

    val hasOptionalFloatWrapper: Boolean

    val hasOptionalDoubleWrapper: Boolean

    val hasOptionalStringWrapper: Boolean

    val hasOptionalBytesWrapper: Boolean

    val hasOptionalDuration: Boolean

    val hasOptionalTimestamp: Boolean

    val hasOptionalFieldMask: Boolean

    val hasOptionalStruct: Boolean

    val hasOptionalAny: Boolean

    val hasOptionalValue: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedMessage.presence] extension property.
    */
    interface NestedMessage {
        val hasCorecursive: Boolean
    }
}
