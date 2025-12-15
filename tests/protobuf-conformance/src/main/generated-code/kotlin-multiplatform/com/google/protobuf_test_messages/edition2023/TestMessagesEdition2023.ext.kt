@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.edition2023

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = ComplexMessage {
*    d = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.edition2023.ComplexMessage.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.ComplexMessageInternal.() -> Unit): com.google.protobuf_test_messages.edition2023.ComplexMessage { 
    val msg = com.google.protobuf_test_messages.edition2023.ComplexMessageInternal().apply(body)
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
fun com.google.protobuf_test_messages.edition2023.ComplexMessage.copy(body: com.google.protobuf_test_messages.edition2023.ComplexMessageInternal.() -> Unit = {}): com.google.protobuf_test_messages.edition2023.ComplexMessage { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.ComplexMessage] instance.
*/
val com.google.protobuf_test_messages.edition2023.ComplexMessage.presence: com.google.protobuf_test_messages.edition2023.ComplexMessagePresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = TestAllTypesEdition2023 {
*    optionalInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.() -> Unit): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023 { 
    val msg = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal().apply(body)
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.copy(body: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.() -> Unit = {}): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023] instance.
*/
val com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.presence: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = ForeignMessageEdition2023 {
*    c = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal.() -> Unit): com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023 { 
    val msg = com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal().apply(body)
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
fun com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023.copy(body: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal.() -> Unit = {}): com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023 { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023] instance.
*/
val com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023.presence: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Presence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = GroupLikeType {
*    c = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.edition2023.GroupLikeType.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal.() -> Unit): com.google.protobuf_test_messages.edition2023.GroupLikeType { 
    val msg = com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal().apply(body)
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
fun com.google.protobuf_test_messages.edition2023.GroupLikeType.copy(body: com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal.() -> Unit = {}): com.google.protobuf_test_messages.edition2023.GroupLikeType { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.GroupLikeType] instance.
*/
val com.google.protobuf_test_messages.edition2023.GroupLikeType.presence: com.google.protobuf_test_messages.edition2023.GroupLikeTypePresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = NestedMessage {
*    a = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage { 
    val msg = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal().apply(body)
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage.copy(body: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal.() -> Unit = {}): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage] instance.
*/
val com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage.presence: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Presence.NestedMessage get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = GroupLikeType {
*    groupInt32 = ...
* }
* ```
*/
operator fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal.() -> Unit): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType { 
    val msg = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal().apply(body)
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType.copy(body: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal.() -> Unit = {}): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType] instance.
*/
val com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType.presence: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Presence.GroupLikeType get() = this.asInternal()._presence

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.ComplexMessage] messages.
* Retrieve it via the [com.google.protobuf_test_messages.edition2023.ComplexMessage.presence] extension property.
*/
interface ComplexMessagePresence { 
    val hasD: kotlin.Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023] messages.
* Retrieve it via the [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.presence] extension property.
*/
interface TestAllTypesEdition2023Presence { 
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

    val hasGroupliketype: kotlin.Boolean

    val hasDelimitedField: kotlin.Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage.presence] extension property.
    */
    interface NestedMessage { 
        val hasA: kotlin.Boolean

        val hasCorecursive: kotlin.Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType.presence] extension property.
    */
    interface GroupLikeType { 
        val hasGroupInt32: kotlin.Boolean

        val hasGroupUint32: kotlin.Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023] messages.
* Retrieve it via the [com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023.presence] extension property.
*/
interface ForeignMessageEdition2023Presence { 
    val hasC: kotlin.Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.edition2023.GroupLikeType] messages.
* Retrieve it via the [com.google.protobuf_test_messages.edition2023.GroupLikeType.presence] extension property.
*/
interface GroupLikeTypePresence { 
    val hasC: kotlin.Boolean
}
