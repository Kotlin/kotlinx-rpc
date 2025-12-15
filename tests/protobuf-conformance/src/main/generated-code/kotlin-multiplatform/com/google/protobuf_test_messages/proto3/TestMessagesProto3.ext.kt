@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.proto3

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = TestAllTypesProto3 {
*    optionalInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.Companion.invoke(body: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.() -> Unit): com.google.protobuf_test_messages.proto3.TestAllTypesProto3 { 
    val msg = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal().apply(body)
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
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.copy(body: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto3.TestAllTypesProto3 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto3.TestAllTypesProto3] instance.
*/
val com.google.protobuf_test_messages.proto3.TestAllTypesProto3.presence: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ForeignMessage {
*    c = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.proto3.ForeignMessage.Companion.invoke(body: com.google.protobuf_test_messages.proto3.ForeignMessageInternal.() -> Unit): com.google.protobuf_test_messages.proto3.ForeignMessage { 
    val msg = com.google.protobuf_test_messages.proto3.ForeignMessageInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto3.ForeignMessage.copy(body: com.google.protobuf_test_messages.proto3.ForeignMessageInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto3.ForeignMessage { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = NullHypothesisProto3 { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto3.NullHypothesisProto3.Companion.invoke(body: com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal.() -> Unit): com.google.protobuf_test_messages.proto3.NullHypothesisProto3 { 
    val msg = com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto3.NullHypothesisProto3.copy(body: com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto3.NullHypothesisProto3 { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = EnumOnlyProto3 { }
* ```
*/
operator fun com.google.protobuf_test_messages.proto3.EnumOnlyProto3.Companion.invoke(body: com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal.() -> Unit): com.google.protobuf_test_messages.proto3.EnumOnlyProto3 { 
    val msg = com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
fun com.google.protobuf_test_messages.proto3.EnumOnlyProto3.copy(body: com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal.() -> Unit = {}): com.google.protobuf_test_messages.proto3.EnumOnlyProto3 { 
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
operator fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage.Companion.invoke(body: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage { 
    val msg = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal().apply(body)
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
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage.copy(body: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal.() -> Unit = {}): com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage] instance.
*/
val com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage.presence: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Presence.NestedMessage get() = this.asInternal()._presence

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto3.TestAllTypesProto3] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto3.TestAllTypesProto3.presence] extension property.
*/
interface TestAllTypesProto3Presence { 
    val hasOptionalNestedMessage: kotlin.Boolean

    val hasOptionalForeignMessage: kotlin.Boolean

    val hasRecursiveMessage: kotlin.Boolean

    val hasOptionalBoolWrapper: kotlin.Boolean

    val hasOptionalInt32Wrapper: kotlin.Boolean

    val hasOptionalInt64Wrapper: kotlin.Boolean

    val hasOptionalUint32Wrapper: kotlin.Boolean

    val hasOptionalUint64Wrapper: kotlin.Boolean

    val hasOptionalFloatWrapper: kotlin.Boolean

    val hasOptionalDoubleWrapper: kotlin.Boolean

    val hasOptionalStringWrapper: kotlin.Boolean

    val hasOptionalBytesWrapper: kotlin.Boolean

    val hasOptionalDuration: kotlin.Boolean

    val hasOptionalTimestamp: kotlin.Boolean

    val hasOptionalFieldMask: kotlin.Boolean

    val hasOptionalStruct: kotlin.Boolean

    val hasOptionalAny: kotlin.Boolean

    val hasOptionalValue: kotlin.Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage.presence] extension property.
    */
    interface NestedMessage { 
        val hasCorecursive: kotlin.Boolean
    }
}
