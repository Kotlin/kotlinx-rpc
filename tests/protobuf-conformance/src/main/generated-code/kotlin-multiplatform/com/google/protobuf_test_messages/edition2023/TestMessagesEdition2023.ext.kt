@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.edition2023

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoExtensionDescriptor
import kotlinx.rpc.protobuf.internal.InternalPresenceObject

/**
* Constructs a new message.
* ```
* val message = ComplexMessage {
*    d = ...
* }
* ```
*/
operator fun ComplexMessage.Companion.invoke(body: ComplexMessage.Builder.() -> Unit): ComplexMessage {
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
fun ComplexMessage.copy(body: ComplexMessage.Builder.() -> Unit = {}): ComplexMessage {
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
operator fun TestAllTypesEdition2023.Companion.invoke(body: TestAllTypesEdition2023.Builder.() -> Unit): TestAllTypesEdition2023 {
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
fun TestAllTypesEdition2023.copy(body: TestAllTypesEdition2023.Builder.() -> Unit = {}): TestAllTypesEdition2023 {
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
operator fun ForeignMessageEdition2023.Companion.invoke(body: ForeignMessageEdition2023.Builder.() -> Unit): ForeignMessageEdition2023 {
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
fun ForeignMessageEdition2023.copy(body: ForeignMessageEdition2023.Builder.() -> Unit = {}): ForeignMessageEdition2023 {
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
operator fun GroupLikeType.Companion.invoke(body: GroupLikeType.Builder.() -> Unit): GroupLikeType {
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
fun GroupLikeType.copy(body: GroupLikeType.Builder.() -> Unit = {}): GroupLikeType {
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
operator fun TestAllTypesEdition2023.NestedMessage.Companion.invoke(body: TestAllTypesEdition2023.NestedMessage.Builder.() -> Unit): TestAllTypesEdition2023.NestedMessage {
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
fun TestAllTypesEdition2023.NestedMessage.copy(body: TestAllTypesEdition2023.NestedMessage.Builder.() -> Unit = {}): TestAllTypesEdition2023.NestedMessage {
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
operator fun TestAllTypesEdition2023.GroupLikeType.Companion.invoke(body: TestAllTypesEdition2023.GroupLikeType.Builder.() -> Unit): TestAllTypesEdition2023.GroupLikeType {
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
fun TestAllTypesEdition2023.GroupLikeType.copy(body: TestAllTypesEdition2023.GroupLikeType.Builder.() -> Unit = {}): TestAllTypesEdition2023.GroupLikeType {
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

val TestAllTypesEdition2023.extensionInt32: Int? get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionInt32)

val TestAllTypesEdition2023.Companion.extensionInt32: ProtoExtensionDescriptor<TestAllTypesEdition2023, Int> get() = TestMessagesEdition2023KtExtensions.extensionInt32

val TestAllTypesEdition2023Presence.hasExtensionInt32: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.extensionInt32)

var TestAllTypesEdition2023.Builder.extensionInt32: Int?
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionInt32)
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.extensionInt32, value) }

val TestAllTypesEdition2023.extensionString: String? get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionString)

val TestAllTypesEdition2023.Companion.extensionString: ProtoExtensionDescriptor<TestAllTypesEdition2023, String> get() = TestMessagesEdition2023KtExtensions.extensionString

val TestAllTypesEdition2023Presence.hasExtensionString: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.extensionString)

var TestAllTypesEdition2023.Builder.extensionString: String?
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionString)
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.extensionString, value) }

val TestAllTypesEdition2023.extensionBytes: ByteArray? get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionBytes)

val TestAllTypesEdition2023.Companion.extensionBytes: ProtoExtensionDescriptor<TestAllTypesEdition2023, ByteArray> get() = TestMessagesEdition2023KtExtensions.extensionBytes

val TestAllTypesEdition2023Presence.hasExtensionBytes: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.extensionBytes)

var TestAllTypesEdition2023.Builder.extensionBytes: ByteArray?
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionBytes)
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.extensionBytes, value) }

val TestAllTypesEdition2023.groupliketype: GroupLikeType get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.groupliketype) ?: TestMessagesEdition2023KtExtensions.groupliketype.defaultValue.value

val TestAllTypesEdition2023.Companion.groupliketype: ProtoExtensionDescriptor<TestAllTypesEdition2023, GroupLikeType> get() = TestMessagesEdition2023KtExtensions.groupliketype

val TestAllTypesEdition2023Presence.hasGroupliketype: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.groupliketype)

var TestAllTypesEdition2023.Builder.groupliketype: GroupLikeType
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.groupliketype) ?: TestMessagesEdition2023KtExtensions.groupliketype.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.groupliketype, value) }

val TestAllTypesEdition2023.delimitedExt: GroupLikeType get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.delimitedExt) ?: TestMessagesEdition2023KtExtensions.delimitedExt.defaultValue.value

val TestAllTypesEdition2023.Companion.delimitedExt: ProtoExtensionDescriptor<TestAllTypesEdition2023, GroupLikeType> get() = TestMessagesEdition2023KtExtensions.delimitedExt

val TestAllTypesEdition2023Presence.hasDelimitedExt: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.delimitedExt)

var TestAllTypesEdition2023.Builder.delimitedExt: GroupLikeType
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.delimitedExt) ?: TestMessagesEdition2023KtExtensions.delimitedExt.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.delimitedExt, value) }

