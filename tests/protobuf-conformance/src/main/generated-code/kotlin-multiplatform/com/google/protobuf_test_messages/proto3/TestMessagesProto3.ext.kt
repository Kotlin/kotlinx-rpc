@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.proto3

import com.google.protobuf.kotlin.BoolValue
import com.google.protobuf.kotlin.BytesValue
import com.google.protobuf.kotlin.DoubleValue
import com.google.protobuf.kotlin.Duration
import com.google.protobuf.kotlin.Empty
import com.google.protobuf.kotlin.FieldMask
import com.google.protobuf.kotlin.FloatValue
import com.google.protobuf.kotlin.Int32Value
import com.google.protobuf.kotlin.Int64Value
import com.google.protobuf.kotlin.StringValue
import com.google.protobuf.kotlin.Struct
import com.google.protobuf.kotlin.Timestamp
import com.google.protobuf.kotlin.UInt32Value
import com.google.protobuf.kotlin.UInt64Value
import com.google.protobuf.kotlin.Value
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
operator fun TestAllTypesProto3.Companion.invoke(body: TestAllTypesProto3.Builder.() -> Unit): TestAllTypesProto3 {
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
fun TestAllTypesProto3.copy(body: TestAllTypesProto3.Builder.() -> Unit = {}): TestAllTypesProto3 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto3.TestAllTypesProto3] instance.
*/
val TestAllTypesProto3.presence: TestAllTypesProto3Presence get() = this.asInternal()._presence

/**
* Returns the value of the `optionalNestedMessage` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalNestedMessageOrNull: TestAllTypesProto3.NestedMessage? get() = if (this.presence.hasOptionalNestedMessage) this.optionalNestedMessage else null

/**
* Returns the value of the `optionalForeignMessage` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalForeignMessageOrNull: ForeignMessage? get() = if (this.presence.hasOptionalForeignMessage) this.optionalForeignMessage else null

/**
* Returns the value of the `recursiveMessage` field if present, otherwise null.
*/
val TestAllTypesProto3.recursiveMessageOrNull: TestAllTypesProto3? get() = if (this.presence.hasRecursiveMessage) this.recursiveMessage else null

/**
* Returns the value of the `optionalBoolWrapper` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalBoolWrapperOrNull: BoolValue? get() = if (this.presence.hasOptionalBoolWrapper) this.optionalBoolWrapper else null

/**
* Returns the value of the `optionalInt32Wrapper` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalInt32WrapperOrNull: Int32Value? get() = if (this.presence.hasOptionalInt32Wrapper) this.optionalInt32Wrapper else null

/**
* Returns the value of the `optionalInt64Wrapper` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalInt64WrapperOrNull: Int64Value? get() = if (this.presence.hasOptionalInt64Wrapper) this.optionalInt64Wrapper else null

/**
* Returns the value of the `optionalUint32Wrapper` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalUint32WrapperOrNull: UInt32Value? get() = if (this.presence.hasOptionalUint32Wrapper) this.optionalUint32Wrapper else null

/**
* Returns the value of the `optionalUint64Wrapper` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalUint64WrapperOrNull: UInt64Value? get() = if (this.presence.hasOptionalUint64Wrapper) this.optionalUint64Wrapper else null

/**
* Returns the value of the `optionalFloatWrapper` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalFloatWrapperOrNull: FloatValue? get() = if (this.presence.hasOptionalFloatWrapper) this.optionalFloatWrapper else null

/**
* Returns the value of the `optionalDoubleWrapper` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalDoubleWrapperOrNull: DoubleValue? get() = if (this.presence.hasOptionalDoubleWrapper) this.optionalDoubleWrapper else null

/**
* Returns the value of the `optionalStringWrapper` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalStringWrapperOrNull: StringValue? get() = if (this.presence.hasOptionalStringWrapper) this.optionalStringWrapper else null

/**
* Returns the value of the `optionalBytesWrapper` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalBytesWrapperOrNull: BytesValue? get() = if (this.presence.hasOptionalBytesWrapper) this.optionalBytesWrapper else null

/**
* Returns the value of the `optionalDuration` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalDurationOrNull: Duration? get() = if (this.presence.hasOptionalDuration) this.optionalDuration else null

/**
* Returns the value of the `optionalTimestamp` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalTimestampOrNull: Timestamp? get() = if (this.presence.hasOptionalTimestamp) this.optionalTimestamp else null

/**
* Returns the value of the `optionalFieldMask` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalFieldMaskOrNull: FieldMask? get() = if (this.presence.hasOptionalFieldMask) this.optionalFieldMask else null

/**
* Returns the value of the `optionalStruct` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalStructOrNull: Struct? get() = if (this.presence.hasOptionalStruct) this.optionalStruct else null

/**
* Returns the value of the `optionalAny` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalAnyOrNull: com.google.protobuf.kotlin.Any? get() = if (this.presence.hasOptionalAny) this.optionalAny else null

/**
* Returns the value of the `optionalValue` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalValueOrNull: Value? get() = if (this.presence.hasOptionalValue) this.optionalValue else null

/**
* Returns the value of the `optionalEmpty` field if present, otherwise null.
*/
val TestAllTypesProto3.optionalEmptyOrNull: Empty? get() = if (this.presence.hasOptionalEmpty) this.optionalEmpty else null

/**
* Constructs a new message.
* ```
* val message = ForeignMessage {
*    c = ...
* }
* ```
*/
operator fun ForeignMessage.Companion.invoke(body: ForeignMessage.Builder.() -> Unit): ForeignMessage {
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
fun ForeignMessage.copy(body: ForeignMessage.Builder.() -> Unit = {}): ForeignMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = NullHypothesisProto3 { }
* ```
*/
operator fun NullHypothesisProto3.Companion.invoke(body: NullHypothesisProto3.Builder.() -> Unit): NullHypothesisProto3 {
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
fun NullHypothesisProto3.copy(body: NullHypothesisProto3.Builder.() -> Unit = {}): NullHypothesisProto3 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = EnumOnlyProto3 { }
* ```
*/
operator fun EnumOnlyProto3.Companion.invoke(body: EnumOnlyProto3.Builder.() -> Unit): EnumOnlyProto3 {
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
fun EnumOnlyProto3.copy(body: EnumOnlyProto3.Builder.() -> Unit = {}): EnumOnlyProto3 {
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
operator fun TestAllTypesProto3.NestedMessage.Companion.invoke(body: TestAllTypesProto3.NestedMessage.Builder.() -> Unit): TestAllTypesProto3.NestedMessage {
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
fun TestAllTypesProto3.NestedMessage.copy(body: TestAllTypesProto3.NestedMessage.Builder.() -> Unit = {}): TestAllTypesProto3.NestedMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage] instance.
*/
val TestAllTypesProto3.NestedMessage.presence: TestAllTypesProto3Presence.NestedMessage get() = this.asInternal()._presence

/**
* Returns the value of the `corecursive` field if present, otherwise null.
*/
val TestAllTypesProto3.NestedMessage.corecursiveOrNull: TestAllTypesProto3? get() = if (this.presence.hasCorecursive) this.corecursive else null

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto3.TestAllTypesProto3] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto3.TestAllTypesProto3.presence] extension property.
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

    val hasOptionalEmpty: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage.presence] extension property.
    */
    interface NestedMessage {
        val hasCorecursive: Boolean
    }
}
