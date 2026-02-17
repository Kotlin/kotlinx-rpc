@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.edition2023

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = ComplexMessage {
*    d = ...
* }
* ```
*/
operator fun ComplexMessage.Companion.invoke(body: ComplexMessageInternal.() -> Unit): ComplexMessage {
    val msg = ComplexMessageInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    d = ...
* }
* ```
*/
fun ComplexMessage.copy(body: ComplexMessageInternal.() -> Unit = {}): ComplexMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.ComplexMessage] instance.
*/
val ComplexMessage.presence: ComplexMessagePresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = TestAllTypesEdition2023 {
*    optionalInt32 = ...
* }
* ```
*/
operator fun TestAllTypesEdition2023.Companion.invoke(body: TestAllTypesEdition2023Internal.() -> Unit): TestAllTypesEdition2023 {
    val msg = TestAllTypesEdition2023Internal().apply(body)
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
fun TestAllTypesEdition2023.copy(body: TestAllTypesEdition2023Internal.() -> Unit = {}): TestAllTypesEdition2023 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023] instance.
*/
val TestAllTypesEdition2023.presence: TestAllTypesEdition2023Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ForeignMessageEdition2023 {
*    c = ...
* }
* ```
*/
operator fun ForeignMessageEdition2023.Companion.invoke(body: ForeignMessageEdition2023Internal.() -> Unit): ForeignMessageEdition2023 {
    val msg = ForeignMessageEdition2023Internal().apply(body)
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
fun ForeignMessageEdition2023.copy(body: ForeignMessageEdition2023Internal.() -> Unit = {}): ForeignMessageEdition2023 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023] instance.
*/
val ForeignMessageEdition2023.presence: ForeignMessageEdition2023Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = GroupLikeType {
*    c = ...
* }
* ```
*/
operator fun GroupLikeType.Companion.invoke(body: GroupLikeTypeInternal.() -> Unit): GroupLikeType {
    val msg = GroupLikeTypeInternal().apply(body)
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
fun GroupLikeType.copy(body: GroupLikeTypeInternal.() -> Unit = {}): GroupLikeType {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.GroupLikeType] instance.
*/
val GroupLikeType.presence: GroupLikeTypePresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = NestedMessage {
*    a = ...
* }
* ```
*/
operator fun TestAllTypesEdition2023.NestedMessage.Companion.invoke(body: TestAllTypesEdition2023Internal.NestedMessageInternal.() -> Unit): TestAllTypesEdition2023.NestedMessage {
    val msg = TestAllTypesEdition2023Internal.NestedMessageInternal().apply(body)
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
fun TestAllTypesEdition2023.NestedMessage.copy(body: TestAllTypesEdition2023Internal.NestedMessageInternal.() -> Unit = {}): TestAllTypesEdition2023.NestedMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage] instance.
*/
val TestAllTypesEdition2023.NestedMessage.presence: TestAllTypesEdition2023Presence.NestedMessage get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = GroupLikeType {
*    groupInt32 = ...
* }
* ```
*/
operator fun TestAllTypesEdition2023.GroupLikeType.Companion.invoke(body: TestAllTypesEdition2023Internal.GroupLikeTypeInternal.() -> Unit): TestAllTypesEdition2023.GroupLikeType {
    val msg = TestAllTypesEdition2023Internal.GroupLikeTypeInternal().apply(body)
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
fun TestAllTypesEdition2023.GroupLikeType.copy(body: TestAllTypesEdition2023Internal.GroupLikeTypeInternal.() -> Unit = {}): TestAllTypesEdition2023.GroupLikeType {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType] instance.
*/
val TestAllTypesEdition2023.GroupLikeType.presence: TestAllTypesEdition2023Presence.GroupLikeType get() = this.asInternal()._presence

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.ComplexMessage] messages.
* Retrieve it via the [com.google.protobuf_test_messages.edition2023.ComplexMessage.presence] extension property.
*/
interface ComplexMessagePresence {
    val hasD: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023] messages.
* Retrieve it via the [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.presence] extension property.
*/
interface TestAllTypesEdition2023Presence {
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

    val hasGroupliketype: Boolean

    val hasDelimitedField: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage.presence] extension property.
    */
    interface NestedMessage {
        val hasA: Boolean

        val hasCorecursive: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType.presence] extension property.
    */
    interface GroupLikeType {
        val hasGroupInt32: Boolean

        val hasGroupUint32: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023] messages.
* Retrieve it via the [com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023.presence] extension property.
*/
interface ForeignMessageEdition2023Presence {
    val hasC: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.GroupLikeType] messages.
* Retrieve it via the [com.google.protobuf_test_messages.edition2023.GroupLikeType.presence] extension property.
*/
interface GroupLikeTypePresence {
    val hasC: Boolean
}
