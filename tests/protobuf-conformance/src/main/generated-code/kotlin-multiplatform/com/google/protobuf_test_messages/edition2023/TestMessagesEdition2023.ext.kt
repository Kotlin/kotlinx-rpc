@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.edition2023

import kotlinx.io.bytestring.ByteString
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
* Returns the value of the `d` field if present, otherwise null.
*/
val ComplexMessage.dOrNull: Int? get() = if (this.presence.hasD) this.d else null

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
* Returns the value of the `optionalInt32` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalInt32OrNull: Int? get() = if (this.presence.hasOptionalInt32) this.optionalInt32 else null

/**
* Returns the value of the `optionalInt64` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalInt64OrNull: Long? get() = if (this.presence.hasOptionalInt64) this.optionalInt64 else null

/**
* Returns the value of the `optionalUint32` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalUint32OrNull: UInt? get() = if (this.presence.hasOptionalUint32) this.optionalUint32 else null

/**
* Returns the value of the `optionalUint64` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalUint64OrNull: ULong? get() = if (this.presence.hasOptionalUint64) this.optionalUint64 else null

/**
* Returns the value of the `optionalSint32` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalSint32OrNull: Int? get() = if (this.presence.hasOptionalSint32) this.optionalSint32 else null

/**
* Returns the value of the `optionalSint64` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalSint64OrNull: Long? get() = if (this.presence.hasOptionalSint64) this.optionalSint64 else null

/**
* Returns the value of the `optionalFixed32` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalFixed32OrNull: UInt? get() = if (this.presence.hasOptionalFixed32) this.optionalFixed32 else null

/**
* Returns the value of the `optionalFixed64` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalFixed64OrNull: ULong? get() = if (this.presence.hasOptionalFixed64) this.optionalFixed64 else null

/**
* Returns the value of the `optionalSfixed32` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalSfixed32OrNull: Int? get() = if (this.presence.hasOptionalSfixed32) this.optionalSfixed32 else null

/**
* Returns the value of the `optionalSfixed64` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalSfixed64OrNull: Long? get() = if (this.presence.hasOptionalSfixed64) this.optionalSfixed64 else null

/**
* Returns the value of the `optionalFloat` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalFloatOrNull: Float? get() = if (this.presence.hasOptionalFloat) this.optionalFloat else null

/**
* Returns the value of the `optionalDouble` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalDoubleOrNull: Double? get() = if (this.presence.hasOptionalDouble) this.optionalDouble else null

/**
* Returns the value of the `optionalBool` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalBoolOrNull: Boolean? get() = if (this.presence.hasOptionalBool) this.optionalBool else null

/**
* Returns the value of the `optionalString` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalStringOrNull: String? get() = if (this.presence.hasOptionalString) this.optionalString else null

/**
* Returns the value of the `optionalBytes` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalBytesOrNull: ByteString? get() = if (this.presence.hasOptionalBytes) this.optionalBytes else null

/**
* Returns the value of the `optionalNestedMessage` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalNestedMessageOrNull: TestAllTypesEdition2023.NestedMessage? get() = if (this.presence.hasOptionalNestedMessage) this.optionalNestedMessage else null

/**
* Returns the value of the `optionalForeignMessage` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalForeignMessageOrNull: ForeignMessageEdition2023? get() = if (this.presence.hasOptionalForeignMessage) this.optionalForeignMessage else null

/**
* Returns the value of the `optionalNestedEnum` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalNestedEnumOrNull: TestAllTypesEdition2023.NestedEnum? get() = if (this.presence.hasOptionalNestedEnum) this.optionalNestedEnum else null

/**
* Returns the value of the `optionalForeignEnum` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalForeignEnumOrNull: ForeignEnumEdition2023? get() = if (this.presence.hasOptionalForeignEnum) this.optionalForeignEnum else null

/**
* Returns the value of the `optionalStringPiece` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalStringPieceOrNull: String? get() = if (this.presence.hasOptionalStringPiece) this.optionalStringPiece else null

/**
* Returns the value of the `optionalCord` field if present, otherwise null.
*/
val TestAllTypesEdition2023.optionalCordOrNull: String? get() = if (this.presence.hasOptionalCord) this.optionalCord else null

/**
* Returns the value of the `recursiveMessage` field if present, otherwise null.
*/
val TestAllTypesEdition2023.recursiveMessageOrNull: TestAllTypesEdition2023? get() = if (this.presence.hasRecursiveMessage) this.recursiveMessage else null

/**
* Returns the value of the `groupliketype` field if present, otherwise null.
*/
val TestAllTypesEdition2023.groupliketypeOrNull: TestAllTypesEdition2023.GroupLikeType? get() = if (this.presence.hasGroupliketype) this.groupliketype else null

/**
* Returns the value of the `delimitedField` field if present, otherwise null.
*/
val TestAllTypesEdition2023.delimitedFieldOrNull: TestAllTypesEdition2023.GroupLikeType? get() = if (this.presence.hasDelimitedField) this.delimitedField else null

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
* Returns the value of the `c` field if present, otherwise null.
*/
val ForeignMessageEdition2023.cOrNull: Int? get() = if (this.presence.hasC) this.c else null

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
* Returns the value of the `c` field if present, otherwise null.
*/
val GroupLikeType.cOrNull: Int? get() = if (this.presence.hasC) this.c else null

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
* Returns the value of the `a` field if present, otherwise null.
*/
val TestAllTypesEdition2023.NestedMessage.aOrNull: Int? get() = if (this.presence.hasA) this.a else null

/**
* Returns the value of the `corecursive` field if present, otherwise null.
*/
val TestAllTypesEdition2023.NestedMessage.corecursiveOrNull: TestAllTypesEdition2023? get() = if (this.presence.hasCorecursive) this.corecursive else null

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
* Returns the value of the `groupInt32` field if present, otherwise null.
*/
val TestAllTypesEdition2023.GroupLikeType.groupInt32OrNull: Int? get() = if (this.presence.hasGroupInt32) this.groupInt32 else null

/**
* Returns the value of the `groupUint32` field if present, otherwise null.
*/
val TestAllTypesEdition2023.GroupLikeType.groupUint32OrNull: UInt? get() = if (this.presence.hasGroupUint32) this.groupUint32 else null

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

val TestAllTypesEdition2023.extensionInt32: Int get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionInt32) ?: TestMessagesEdition2023KtExtensions.extensionInt32.defaultValue.value

val TestAllTypesEdition2023.Companion.extensionInt32: ProtoExtensionDescriptor<TestAllTypesEdition2023, Int> get() = TestMessagesEdition2023KtExtensions.extensionInt32

val TestAllTypesEdition2023Presence.hasExtensionInt32: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.extensionInt32)

var TestAllTypesEdition2023.Builder.extensionInt32: Int
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionInt32) ?: TestMessagesEdition2023KtExtensions.extensionInt32.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.extensionInt32, value) }

fun TestAllTypesEdition2023.Builder.clearExtensionInt32() {
    asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.extensionInt32, null)
}

val TestAllTypesEdition2023.extensionString: String get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionString) ?: TestMessagesEdition2023KtExtensions.extensionString.defaultValue.value

val TestAllTypesEdition2023.Companion.extensionString: ProtoExtensionDescriptor<TestAllTypesEdition2023, String> get() = TestMessagesEdition2023KtExtensions.extensionString

val TestAllTypesEdition2023Presence.hasExtensionString: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.extensionString)

var TestAllTypesEdition2023.Builder.extensionString: String
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionString) ?: TestMessagesEdition2023KtExtensions.extensionString.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.extensionString, value) }

fun TestAllTypesEdition2023.Builder.clearExtensionString() {
    asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.extensionString, null)
}

val TestAllTypesEdition2023.extensionBytes: ByteString get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionBytes) ?: TestMessagesEdition2023KtExtensions.extensionBytes.defaultValue.value

val TestAllTypesEdition2023.Companion.extensionBytes: ProtoExtensionDescriptor<TestAllTypesEdition2023, ByteString> get() = TestMessagesEdition2023KtExtensions.extensionBytes

val TestAllTypesEdition2023Presence.hasExtensionBytes: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.extensionBytes)

var TestAllTypesEdition2023.Builder.extensionBytes: ByteString
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.extensionBytes) ?: TestMessagesEdition2023KtExtensions.extensionBytes.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.extensionBytes, value) }

fun TestAllTypesEdition2023.Builder.clearExtensionBytes() {
    asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.extensionBytes, null)
}

val TestAllTypesEdition2023.groupliketype: GroupLikeType get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.groupliketype) ?: TestMessagesEdition2023KtExtensions.groupliketype.defaultValue.value

val TestAllTypesEdition2023.Companion.groupliketype: ProtoExtensionDescriptor<TestAllTypesEdition2023, GroupLikeType> get() = TestMessagesEdition2023KtExtensions.groupliketype

val TestAllTypesEdition2023Presence.hasGroupliketype: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.groupliketype)

var TestAllTypesEdition2023.Builder.groupliketype: GroupLikeType
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.groupliketype) ?: TestMessagesEdition2023KtExtensions.groupliketype.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.groupliketype, value) }

fun TestAllTypesEdition2023.Builder.clearGroupliketype() {
    asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.groupliketype, null)
}

val TestAllTypesEdition2023.delimitedExt: GroupLikeType get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.delimitedExt) ?: TestMessagesEdition2023KtExtensions.delimitedExt.defaultValue.value

val TestAllTypesEdition2023.Companion.delimitedExt: ProtoExtensionDescriptor<TestAllTypesEdition2023, GroupLikeType> get() = TestMessagesEdition2023KtExtensions.delimitedExt

val TestAllTypesEdition2023Presence.hasDelimitedExt: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesEdition2023KtExtensions.delimitedExt)

var TestAllTypesEdition2023.Builder.delimitedExt: GroupLikeType
    get() = asInternal().getExtensionValue(TestMessagesEdition2023KtExtensions.delimitedExt) ?: TestMessagesEdition2023KtExtensions.delimitedExt.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.delimitedExt, value) }

fun TestAllTypesEdition2023.Builder.clearDelimitedExt() {
    asInternal().setExtensionValue(TestMessagesEdition2023KtExtensions.delimitedExt, null)
}
