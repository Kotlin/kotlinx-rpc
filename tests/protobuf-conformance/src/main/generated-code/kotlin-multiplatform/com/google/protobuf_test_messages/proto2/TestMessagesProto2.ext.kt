@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.proto2

import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoExtensionDescriptor
import kotlinx.rpc.protobuf.internal.InternalPresenceObject

/**
* Constructs a new message.
* ```
* val message = TestAllTypesProto2 {
*    optionalInt32 = ...
* }
* ```
*/
operator fun TestAllTypesProto2.Companion.invoke(body: TestAllTypesProto2.Builder.() -> Unit): TestAllTypesProto2 {
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
fun TestAllTypesProto2.copy(body: TestAllTypesProto2.Builder.() -> Unit = {}): TestAllTypesProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2] instance.
*/
val TestAllTypesProto2.presence: TestAllTypesProto2Presence get() = this.asInternal()._presence

/**
* Returns the value of the `optionalInt32` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalInt32OrNull: Int? get() = if (this.presence.hasOptionalInt32) this.optionalInt32 else null

/**
* Returns the value of the `optionalInt64` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalInt64OrNull: Long? get() = if (this.presence.hasOptionalInt64) this.optionalInt64 else null

/**
* Returns the value of the `optionalUint32` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalUint32OrNull: UInt? get() = if (this.presence.hasOptionalUint32) this.optionalUint32 else null

/**
* Returns the value of the `optionalUint64` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalUint64OrNull: ULong? get() = if (this.presence.hasOptionalUint64) this.optionalUint64 else null

/**
* Returns the value of the `optionalSint32` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalSint32OrNull: Int? get() = if (this.presence.hasOptionalSint32) this.optionalSint32 else null

/**
* Returns the value of the `optionalSint64` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalSint64OrNull: Long? get() = if (this.presence.hasOptionalSint64) this.optionalSint64 else null

/**
* Returns the value of the `optionalFixed32` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalFixed32OrNull: UInt? get() = if (this.presence.hasOptionalFixed32) this.optionalFixed32 else null

/**
* Returns the value of the `optionalFixed64` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalFixed64OrNull: ULong? get() = if (this.presence.hasOptionalFixed64) this.optionalFixed64 else null

/**
* Returns the value of the `optionalSfixed32` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalSfixed32OrNull: Int? get() = if (this.presence.hasOptionalSfixed32) this.optionalSfixed32 else null

/**
* Returns the value of the `optionalSfixed64` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalSfixed64OrNull: Long? get() = if (this.presence.hasOptionalSfixed64) this.optionalSfixed64 else null

/**
* Returns the value of the `optionalFloat` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalFloatOrNull: Float? get() = if (this.presence.hasOptionalFloat) this.optionalFloat else null

/**
* Returns the value of the `optionalDouble` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalDoubleOrNull: Double? get() = if (this.presence.hasOptionalDouble) this.optionalDouble else null

/**
* Returns the value of the `optionalBool` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalBoolOrNull: Boolean? get() = if (this.presence.hasOptionalBool) this.optionalBool else null

/**
* Returns the value of the `optionalString` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalStringOrNull: String? get() = if (this.presence.hasOptionalString) this.optionalString else null

/**
* Returns the value of the `optionalBytes` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalBytesOrNull: ByteString? get() = if (this.presence.hasOptionalBytes) this.optionalBytes else null

/**
* Returns the value of the `optionalNestedMessage` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalNestedMessageOrNull: TestAllTypesProto2.NestedMessage? get() = if (this.presence.hasOptionalNestedMessage) this.optionalNestedMessage else null

/**
* Returns the value of the `optionalForeignMessage` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalForeignMessageOrNull: ForeignMessageProto2? get() = if (this.presence.hasOptionalForeignMessage) this.optionalForeignMessage else null

/**
* Returns the value of the `optionalNestedEnum` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalNestedEnumOrNull: TestAllTypesProto2.NestedEnum? get() = if (this.presence.hasOptionalNestedEnum) this.optionalNestedEnum else null

/**
* Returns the value of the `optionalForeignEnum` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalForeignEnumOrNull: ForeignEnumProto2? get() = if (this.presence.hasOptionalForeignEnum) this.optionalForeignEnum else null

/**
* Returns the value of the `optionalStringPiece` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalStringPieceOrNull: String? get() = if (this.presence.hasOptionalStringPiece) this.optionalStringPiece else null

/**
* Returns the value of the `optionalCord` field if present, otherwise null.
*/
val TestAllTypesProto2.optionalCordOrNull: String? get() = if (this.presence.hasOptionalCord) this.optionalCord else null

/**
* Returns the value of the `recursiveMessage` field if present, otherwise null.
*/
val TestAllTypesProto2.recursiveMessageOrNull: TestAllTypesProto2? get() = if (this.presence.hasRecursiveMessage) this.recursiveMessage else null

/**
* Returns the value of the `data` field if present, otherwise null.
*/
val TestAllTypesProto2.dataOrNull: TestAllTypesProto2.Data? get() = if (this.presence.hasData) this.data else null

/**
* Returns the value of the `multiwordgroupfield` field if present, otherwise null.
*/
val TestAllTypesProto2.multiwordgroupfieldOrNull: TestAllTypesProto2.MultiWordGroupField? get() = if (this.presence.hasMultiwordgroupfield) this.multiwordgroupfield else null

/**
* Returns the value of the `defaultInt32` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultInt32OrNull: Int? get() = if (this.presence.hasDefaultInt32) this.defaultInt32 else null

/**
* Returns the value of the `defaultInt64` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultInt64OrNull: Long? get() = if (this.presence.hasDefaultInt64) this.defaultInt64 else null

/**
* Returns the value of the `defaultUint32` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultUint32OrNull: UInt? get() = if (this.presence.hasDefaultUint32) this.defaultUint32 else null

/**
* Returns the value of the `defaultUint64` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultUint64OrNull: ULong? get() = if (this.presence.hasDefaultUint64) this.defaultUint64 else null

/**
* Returns the value of the `defaultSint32` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultSint32OrNull: Int? get() = if (this.presence.hasDefaultSint32) this.defaultSint32 else null

/**
* Returns the value of the `defaultSint64` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultSint64OrNull: Long? get() = if (this.presence.hasDefaultSint64) this.defaultSint64 else null

/**
* Returns the value of the `defaultFixed32` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultFixed32OrNull: UInt? get() = if (this.presence.hasDefaultFixed32) this.defaultFixed32 else null

/**
* Returns the value of the `defaultFixed64` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultFixed64OrNull: ULong? get() = if (this.presence.hasDefaultFixed64) this.defaultFixed64 else null

/**
* Returns the value of the `defaultSfixed32` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultSfixed32OrNull: Int? get() = if (this.presence.hasDefaultSfixed32) this.defaultSfixed32 else null

/**
* Returns the value of the `defaultSfixed64` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultSfixed64OrNull: Long? get() = if (this.presence.hasDefaultSfixed64) this.defaultSfixed64 else null

/**
* Returns the value of the `defaultFloat` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultFloatOrNull: Float? get() = if (this.presence.hasDefaultFloat) this.defaultFloat else null

/**
* Returns the value of the `defaultDouble` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultDoubleOrNull: Double? get() = if (this.presence.hasDefaultDouble) this.defaultDouble else null

/**
* Returns the value of the `defaultBool` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultBoolOrNull: Boolean? get() = if (this.presence.hasDefaultBool) this.defaultBool else null

/**
* Returns the value of the `defaultString` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultStringOrNull: String? get() = if (this.presence.hasDefaultString) this.defaultString else null

/**
* Returns the value of the `defaultBytes` field if present, otherwise null.
*/
val TestAllTypesProto2.defaultBytesOrNull: ByteString? get() = if (this.presence.hasDefaultBytes) this.defaultBytes else null

/**
* Returns the value of the `fieldname1` field if present, otherwise null.
*/
val TestAllTypesProto2.fieldname1OrNull: Int? get() = if (this.presence.hasFieldname1) this.fieldname1 else null

/**
* Returns the value of the `fieldName2` field if present, otherwise null.
*/
val TestAllTypesProto2.fieldName2OrNull: Int? get() = if (this.presence.hasFieldName2) this.fieldName2 else null

/**
* Returns the value of the `FieldName3` field if present, otherwise null.
*/
val TestAllTypesProto2.FieldName3OrNull: Int? get() = if (this.presence.hasFieldName3) this.FieldName3 else null

/**
* Returns the value of the `field_Name4_` field if present, otherwise null.
*/
val TestAllTypesProto2.field_Name4_OrNull: Int? get() = if (this.presence.hasField_Name4_) this.field_Name4_ else null

/**
* Returns the value of the `field0name5` field if present, otherwise null.
*/
val TestAllTypesProto2.field0name5OrNull: Int? get() = if (this.presence.hasField0name5) this.field0name5 else null

/**
* Returns the value of the `field_0Name6` field if present, otherwise null.
*/
val TestAllTypesProto2.field_0Name6OrNull: Int? get() = if (this.presence.hasField_0Name6) this.field_0Name6 else null

/**
* Returns the value of the `fieldName7` field if present, otherwise null.
*/
val TestAllTypesProto2.fieldName7OrNull: Int? get() = if (this.presence.hasFieldName7) this.fieldName7 else null

/**
* Returns the value of the `FieldName8` field if present, otherwise null.
*/
val TestAllTypesProto2.FieldName8OrNull: Int? get() = if (this.presence.hasFieldName8) this.FieldName8 else null

/**
* Returns the value of the `field_Name9` field if present, otherwise null.
*/
val TestAllTypesProto2.field_Name9OrNull: Int? get() = if (this.presence.hasField_Name9) this.field_Name9 else null

/**
* Returns the value of the `Field_Name10` field if present, otherwise null.
*/
val TestAllTypesProto2.Field_Name10OrNull: Int? get() = if (this.presence.hasField_Name10) this.Field_Name10 else null

/**
* Returns the value of the `FIELD_NAME11` field if present, otherwise null.
*/
val TestAllTypesProto2.FIELD_NAME11OrNull: Int? get() = if (this.presence.hasFIELD_NAME11) this.FIELD_NAME11 else null

/**
* Returns the value of the `FIELDName12` field if present, otherwise null.
*/
val TestAllTypesProto2.FIELDName12OrNull: Int? get() = if (this.presence.hasFIELDName12) this.FIELDName12 else null

/**
* Returns the value of the `_FieldName13` field if present, otherwise null.
*/
val TestAllTypesProto2._FieldName13OrNull: Int? get() = if (this.presence.has_FieldName13) this._FieldName13 else null

/**
* Returns the value of the `__FieldName14` field if present, otherwise null.
*/
val TestAllTypesProto2.__FieldName14OrNull: Int? get() = if (this.presence.has__FieldName14) this.__FieldName14 else null

/**
* Returns the value of the `field_Name15` field if present, otherwise null.
*/
val TestAllTypesProto2.field_Name15OrNull: Int? get() = if (this.presence.hasField_Name15) this.field_Name15 else null

/**
* Returns the value of the `field__Name16` field if present, otherwise null.
*/
val TestAllTypesProto2.field__Name16OrNull: Int? get() = if (this.presence.hasField__Name16) this.field__Name16 else null

/**
* Returns the value of the `fieldName17__` field if present, otherwise null.
*/
val TestAllTypesProto2.fieldName17__OrNull: Int? get() = if (this.presence.hasFieldName17__) this.fieldName17__ else null

/**
* Returns the value of the `FieldName18__` field if present, otherwise null.
*/
val TestAllTypesProto2.FieldName18__OrNull: Int? get() = if (this.presence.hasFieldName18__) this.FieldName18__ else null

/**
* Returns the value of the `messageSetCorrect` field if present, otherwise null.
*/
val TestAllTypesProto2.messageSetCorrectOrNull: TestAllTypesProto2.MessageSetCorrect? get() = if (this.presence.hasMessageSetCorrect) this.messageSetCorrect else null

/**
* Constructs a new message.
* ```
* val message = ForeignMessageProto2 {
*    c = ...
* }
* ```
*/
operator fun ForeignMessageProto2.Companion.invoke(body: ForeignMessageProto2.Builder.() -> Unit): ForeignMessageProto2 {
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
fun ForeignMessageProto2.copy(body: ForeignMessageProto2.Builder.() -> Unit = {}): ForeignMessageProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.ForeignMessageProto2] instance.
*/
val ForeignMessageProto2.presence: ForeignMessageProto2Presence get() = this.asInternal()._presence

/**
* Returns the value of the `c` field if present, otherwise null.
*/
val ForeignMessageProto2.cOrNull: Int? get() = if (this.presence.hasC) this.c else null

/**
* Constructs a new message.
* ```
* val message = GroupField {
*    groupInt32 = ...
* }
* ```
*/
operator fun GroupField.Companion.invoke(body: GroupField.Builder.() -> Unit): GroupField {
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
fun GroupField.copy(body: GroupField.Builder.() -> Unit = {}): GroupField {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.GroupField] instance.
*/
val GroupField.presence: GroupFieldPresence get() = this.asInternal()._presence

/**
* Returns the value of the `groupInt32` field if present, otherwise null.
*/
val GroupField.groupInt32OrNull: Int? get() = if (this.presence.hasGroupInt32) this.groupInt32 else null

/**
* Returns the value of the `groupUint32` field if present, otherwise null.
*/
val GroupField.groupUint32OrNull: UInt? get() = if (this.presence.hasGroupUint32) this.groupUint32 else null

/**
* Constructs a new message.
* ```
* val message = UnknownToTestAllTypes {
*    optionalInt32 = ...
* }
* ```
*/
operator fun UnknownToTestAllTypes.Companion.invoke(body: UnknownToTestAllTypes.Builder.() -> Unit): UnknownToTestAllTypes {
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
fun UnknownToTestAllTypes.copy(body: UnknownToTestAllTypes.Builder.() -> Unit = {}): UnknownToTestAllTypes {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes] instance.
*/
val UnknownToTestAllTypes.presence: UnknownToTestAllTypesPresence get() = this.asInternal()._presence

/**
* Returns the value of the `optionalInt32` field if present, otherwise null.
*/
val UnknownToTestAllTypes.optionalInt32OrNull: Int? get() = if (this.presence.hasOptionalInt32) this.optionalInt32 else null

/**
* Returns the value of the `optionalString` field if present, otherwise null.
*/
val UnknownToTestAllTypes.optionalStringOrNull: String? get() = if (this.presence.hasOptionalString) this.optionalString else null

/**
* Returns the value of the `nestedMessage` field if present, otherwise null.
*/
val UnknownToTestAllTypes.nestedMessageOrNull: ForeignMessageProto2? get() = if (this.presence.hasNestedMessage) this.nestedMessage else null

/**
* Returns the value of the `optionalgroup` field if present, otherwise null.
*/
val UnknownToTestAllTypes.optionalgroupOrNull: UnknownToTestAllTypes.OptionalGroup? get() = if (this.presence.hasOptionalgroup) this.optionalgroup else null

/**
* Returns the value of the `optionalBool` field if present, otherwise null.
*/
val UnknownToTestAllTypes.optionalBoolOrNull: Boolean? get() = if (this.presence.hasOptionalBool) this.optionalBool else null

/**
* Constructs a new message.
* ```
* val message = NullHypothesisProto2 { }
* ```
*/
operator fun NullHypothesisProto2.Companion.invoke(body: NullHypothesisProto2.Builder.() -> Unit): NullHypothesisProto2 {
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
fun NullHypothesisProto2.copy(body: NullHypothesisProto2.Builder.() -> Unit = {}): NullHypothesisProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = EnumOnlyProto2 { }
* ```
*/
operator fun EnumOnlyProto2.Companion.invoke(body: EnumOnlyProto2.Builder.() -> Unit): EnumOnlyProto2 {
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
fun EnumOnlyProto2.copy(body: EnumOnlyProto2.Builder.() -> Unit = {}): EnumOnlyProto2 {
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
operator fun OneStringProto2.Companion.invoke(body: OneStringProto2.Builder.() -> Unit): OneStringProto2 {
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
fun OneStringProto2.copy(body: OneStringProto2.Builder.() -> Unit = {}): OneStringProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.OneStringProto2] instance.
*/
val OneStringProto2.presence: OneStringProto2Presence get() = this.asInternal()._presence

/**
* Returns the value of the `data` field if present, otherwise null.
*/
val OneStringProto2.dataOrNull: String? get() = if (this.presence.hasData) this.data else null

/**
* Constructs a new message.
* ```
* val message = ProtoWithKeywords {
*    inline = ...
* }
* ```
*/
operator fun ProtoWithKeywords.Companion.invoke(body: ProtoWithKeywords.Builder.() -> Unit): ProtoWithKeywords {
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
fun ProtoWithKeywords.copy(body: ProtoWithKeywords.Builder.() -> Unit = {}): ProtoWithKeywords {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.ProtoWithKeywords] instance.
*/
val ProtoWithKeywords.presence: ProtoWithKeywordsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `inline` field if present, otherwise null.
*/
val ProtoWithKeywords.inlineOrNull: Int? get() = if (this.presence.hasInline) this.inline else null

/**
* Returns the value of the `concept` field if present, otherwise null.
*/
val ProtoWithKeywords.conceptOrNull: String? get() = if (this.presence.hasConcept) this.concept else null

/**
* Constructs a new message.
* ```
* val message = TestAllRequiredTypesProto2 {
*    requiredInt32 = ...
* }
* ```
*/
operator fun TestAllRequiredTypesProto2.Companion.invoke(body: TestAllRequiredTypesProto2.Builder.() -> Unit): TestAllRequiredTypesProto2 {
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
fun TestAllRequiredTypesProto2.copy(body: TestAllRequiredTypesProto2.Builder.() -> Unit = {}): TestAllRequiredTypesProto2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2] instance.
*/
val TestAllRequiredTypesProto2.presence: TestAllRequiredTypesProto2Presence get() = this.asInternal()._presence

/**
* Returns the value of the `optionalRecursiveMessage` field if present, otherwise null.
*/
val TestAllRequiredTypesProto2.optionalRecursiveMessageOrNull: TestAllRequiredTypesProto2? get() = if (this.presence.hasOptionalRecursiveMessage) this.optionalRecursiveMessage else null

/**
* Constructs a new message.
* ```
* val message = TestLargeOneof {
*    largeOneof = ...
* }
* ```
*/
operator fun TestLargeOneof.Companion.invoke(body: TestLargeOneof.Builder.() -> Unit): TestLargeOneof {
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
fun TestLargeOneof.copy(body: TestLargeOneof.Builder.() -> Unit = {}): TestLargeOneof {
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
operator fun TestAllTypesProto2.NestedMessage.Companion.invoke(body: TestAllTypesProto2.NestedMessage.Builder.() -> Unit): TestAllTypesProto2.NestedMessage {
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
fun TestAllTypesProto2.NestedMessage.copy(body: TestAllTypesProto2.NestedMessage.Builder.() -> Unit = {}): TestAllTypesProto2.NestedMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage] instance.
*/
val TestAllTypesProto2.NestedMessage.presence: TestAllTypesProto2Presence.NestedMessage get() = this.asInternal()._presence

/**
* Returns the value of the `a` field if present, otherwise null.
*/
val TestAllTypesProto2.NestedMessage.aOrNull: Int? get() = if (this.presence.hasA) this.a else null

/**
* Returns the value of the `corecursive` field if present, otherwise null.
*/
val TestAllTypesProto2.NestedMessage.corecursiveOrNull: TestAllTypesProto2? get() = if (this.presence.hasCorecursive) this.corecursive else null

/**
* Constructs a new message.
* ```
* val message = Data {
*    groupInt32 = ...
* }
* ```
*/
operator fun TestAllTypesProto2.Data.Companion.invoke(body: TestAllTypesProto2.Data.Builder.() -> Unit): TestAllTypesProto2.Data {
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
fun TestAllTypesProto2.Data.copy(body: TestAllTypesProto2.Data.Builder.() -> Unit = {}): TestAllTypesProto2.Data {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data] instance.
*/
val TestAllTypesProto2.Data.presence: TestAllTypesProto2Presence.Data get() = this.asInternal()._presence

/**
* Returns the value of the `groupInt32` field if present, otherwise null.
*/
val TestAllTypesProto2.Data.groupInt32OrNull: Int? get() = if (this.presence.hasGroupInt32) this.groupInt32 else null

/**
* Returns the value of the `groupUint32` field if present, otherwise null.
*/
val TestAllTypesProto2.Data.groupUint32OrNull: UInt? get() = if (this.presence.hasGroupUint32) this.groupUint32 else null

/**
* Constructs a new message.
* ```
* val message = MultiWordGroupField {
*    groupInt32 = ...
* }
* ```
*/
operator fun TestAllTypesProto2.MultiWordGroupField.Companion.invoke(body: TestAllTypesProto2.MultiWordGroupField.Builder.() -> Unit): TestAllTypesProto2.MultiWordGroupField {
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
fun TestAllTypesProto2.MultiWordGroupField.copy(body: TestAllTypesProto2.MultiWordGroupField.Builder.() -> Unit = {}): TestAllTypesProto2.MultiWordGroupField {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField] instance.
*/
val TestAllTypesProto2.MultiWordGroupField.presence: TestAllTypesProto2Presence.MultiWordGroupField get() = this.asInternal()._presence

/**
* Returns the value of the `groupInt32` field if present, otherwise null.
*/
val TestAllTypesProto2.MultiWordGroupField.groupInt32OrNull: Int? get() = if (this.presence.hasGroupInt32) this.groupInt32 else null

/**
* Returns the value of the `groupUint32` field if present, otherwise null.
*/
val TestAllTypesProto2.MultiWordGroupField.groupUint32OrNull: UInt? get() = if (this.presence.hasGroupUint32) this.groupUint32 else null

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrect { }
* ```
*/
operator fun TestAllTypesProto2.MessageSetCorrect.Companion.invoke(body: TestAllTypesProto2.MessageSetCorrect.Builder.() -> Unit): TestAllTypesProto2.MessageSetCorrect {
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
fun TestAllTypesProto2.MessageSetCorrect.copy(body: TestAllTypesProto2.MessageSetCorrect.Builder.() -> Unit = {}): TestAllTypesProto2.MessageSetCorrect {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect] instance.
*/
val TestAllTypesProto2.MessageSetCorrect.presence: TestAllTypesProto2Presence.MessageSetCorrect get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrectExtension1 {
*    str = ...
* }
* ```
*/
operator fun TestAllTypesProto2.MessageSetCorrectExtension1.Companion.invoke(body: TestAllTypesProto2.MessageSetCorrectExtension1.Builder.() -> Unit): TestAllTypesProto2.MessageSetCorrectExtension1 {
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
fun TestAllTypesProto2.MessageSetCorrectExtension1.copy(body: TestAllTypesProto2.MessageSetCorrectExtension1.Builder.() -> Unit = {}): TestAllTypesProto2.MessageSetCorrectExtension1 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1] instance.
*/
val TestAllTypesProto2.MessageSetCorrectExtension1.presence: TestAllTypesProto2Presence.MessageSetCorrectExtension1 get() = this.asInternal()._presence

/**
* Returns the value of the `str` field if present, otherwise null.
*/
val TestAllTypesProto2.MessageSetCorrectExtension1.strOrNull: String? get() = if (this.presence.hasStr) this.str else null

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrectExtension2 {
*    i = ...
* }
* ```
*/
operator fun TestAllTypesProto2.MessageSetCorrectExtension2.Companion.invoke(body: TestAllTypesProto2.MessageSetCorrectExtension2.Builder.() -> Unit): TestAllTypesProto2.MessageSetCorrectExtension2 {
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
fun TestAllTypesProto2.MessageSetCorrectExtension2.copy(body: TestAllTypesProto2.MessageSetCorrectExtension2.Builder.() -> Unit = {}): TestAllTypesProto2.MessageSetCorrectExtension2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2] instance.
*/
val TestAllTypesProto2.MessageSetCorrectExtension2.presence: TestAllTypesProto2Presence.MessageSetCorrectExtension2 get() = this.asInternal()._presence

/**
* Returns the value of the `i` field if present, otherwise null.
*/
val TestAllTypesProto2.MessageSetCorrectExtension2.iOrNull: Int? get() = if (this.presence.hasI) this.i else null

/**
* Constructs a new message.
* ```
* val message = ExtensionWithOneof {
*    oneofField = ...
* }
* ```
*/
operator fun TestAllTypesProto2.ExtensionWithOneof.Companion.invoke(body: TestAllTypesProto2.ExtensionWithOneof.Builder.() -> Unit): TestAllTypesProto2.ExtensionWithOneof {
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
fun TestAllTypesProto2.ExtensionWithOneof.copy(body: TestAllTypesProto2.ExtensionWithOneof.Builder.() -> Unit = {}): TestAllTypesProto2.ExtensionWithOneof {
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
operator fun UnknownToTestAllTypes.OptionalGroup.Companion.invoke(body: UnknownToTestAllTypes.OptionalGroup.Builder.() -> Unit): UnknownToTestAllTypes.OptionalGroup {
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
fun UnknownToTestAllTypes.OptionalGroup.copy(body: UnknownToTestAllTypes.OptionalGroup.Builder.() -> Unit = {}): UnknownToTestAllTypes.OptionalGroup {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup] instance.
*/
val UnknownToTestAllTypes.OptionalGroup.presence: UnknownToTestAllTypesPresence.OptionalGroup get() = this.asInternal()._presence

/**
* Returns the value of the `a` field if present, otherwise null.
*/
val UnknownToTestAllTypes.OptionalGroup.aOrNull: Int? get() = if (this.presence.hasA) this.a else null

/**
* Constructs a new message.
* ```
* val message = NestedMessage {
*    a = ...
* }
* ```
*/
operator fun TestAllRequiredTypesProto2.NestedMessage.Companion.invoke(body: TestAllRequiredTypesProto2.NestedMessage.Builder.() -> Unit): TestAllRequiredTypesProto2.NestedMessage {
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
fun TestAllRequiredTypesProto2.NestedMessage.copy(body: TestAllRequiredTypesProto2.NestedMessage.Builder.() -> Unit = {}): TestAllRequiredTypesProto2.NestedMessage {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage] instance.
*/
val TestAllRequiredTypesProto2.NestedMessage.presence: TestAllRequiredTypesProto2Presence.NestedMessage get() = this.asInternal()._presence

/**
* Returns the value of the `optionalCorecursive` field if present, otherwise null.
*/
val TestAllRequiredTypesProto2.NestedMessage.optionalCorecursiveOrNull: TestAllRequiredTypesProto2? get() = if (this.presence.hasOptionalCorecursive) this.optionalCorecursive else null

/**
* Constructs a new message.
* ```
* val message = Data {
*    groupInt32 = ...
* }
* ```
*/
operator fun TestAllRequiredTypesProto2.Data.Companion.invoke(body: TestAllRequiredTypesProto2.Data.Builder.() -> Unit): TestAllRequiredTypesProto2.Data {
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
fun TestAllRequiredTypesProto2.Data.copy(body: TestAllRequiredTypesProto2.Data.Builder.() -> Unit = {}): TestAllRequiredTypesProto2.Data {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data] instance.
*/
val TestAllRequiredTypesProto2.Data.presence: TestAllRequiredTypesProto2Presence.Data get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrect { }
* ```
*/
operator fun TestAllRequiredTypesProto2.MessageSetCorrect.Companion.invoke(body: TestAllRequiredTypesProto2.MessageSetCorrect.Builder.() -> Unit): TestAllRequiredTypesProto2.MessageSetCorrect {
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
fun TestAllRequiredTypesProto2.MessageSetCorrect.copy(body: TestAllRequiredTypesProto2.MessageSetCorrect.Builder.() -> Unit = {}): TestAllRequiredTypesProto2.MessageSetCorrect {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect] instance.
*/
val TestAllRequiredTypesProto2.MessageSetCorrect.presence: TestAllRequiredTypesProto2Presence.MessageSetCorrect get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = MessageSetCorrectExtension1 {
*    str = ...
* }
* ```
*/
operator fun TestAllRequiredTypesProto2.MessageSetCorrectExtension1.Companion.invoke(body: TestAllRequiredTypesProto2.MessageSetCorrectExtension1.Builder.() -> Unit): TestAllRequiredTypesProto2.MessageSetCorrectExtension1 {
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
fun TestAllRequiredTypesProto2.MessageSetCorrectExtension1.copy(body: TestAllRequiredTypesProto2.MessageSetCorrectExtension1.Builder.() -> Unit = {}): TestAllRequiredTypesProto2.MessageSetCorrectExtension1 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1] instance.
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
operator fun TestAllRequiredTypesProto2.MessageSetCorrectExtension2.Companion.invoke(body: TestAllRequiredTypesProto2.MessageSetCorrectExtension2.Builder.() -> Unit): TestAllRequiredTypesProto2.MessageSetCorrectExtension2 {
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
fun TestAllRequiredTypesProto2.MessageSetCorrectExtension2.copy(body: TestAllRequiredTypesProto2.MessageSetCorrectExtension2.Builder.() -> Unit = {}): TestAllRequiredTypesProto2.MessageSetCorrectExtension2 {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2] instance.
*/
val TestAllRequiredTypesProto2.MessageSetCorrectExtension2.presence: TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension2 get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = A1 { }
* ```
*/
operator fun TestLargeOneof.A1.Companion.invoke(body: TestLargeOneof.A1.Builder.() -> Unit): TestLargeOneof.A1 {
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
fun TestLargeOneof.A1.copy(body: TestLargeOneof.A1.Builder.() -> Unit = {}): TestLargeOneof.A1 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A2 { }
* ```
*/
operator fun TestLargeOneof.A2.Companion.invoke(body: TestLargeOneof.A2.Builder.() -> Unit): TestLargeOneof.A2 {
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
fun TestLargeOneof.A2.copy(body: TestLargeOneof.A2.Builder.() -> Unit = {}): TestLargeOneof.A2 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A3 { }
* ```
*/
operator fun TestLargeOneof.A3.Companion.invoke(body: TestLargeOneof.A3.Builder.() -> Unit): TestLargeOneof.A3 {
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
fun TestLargeOneof.A3.copy(body: TestLargeOneof.A3.Builder.() -> Unit = {}): TestLargeOneof.A3 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A4 { }
* ```
*/
operator fun TestLargeOneof.A4.Companion.invoke(body: TestLargeOneof.A4.Builder.() -> Unit): TestLargeOneof.A4 {
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
fun TestLargeOneof.A4.copy(body: TestLargeOneof.A4.Builder.() -> Unit = {}): TestLargeOneof.A4 {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = A5 { }
* ```
*/
operator fun TestLargeOneof.A5.Companion.invoke(body: TestLargeOneof.A5.Builder.() -> Unit): TestLargeOneof.A5 {
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
fun TestLargeOneof.A5.copy(body: TestLargeOneof.A5.Builder.() -> Unit = {}): TestLargeOneof.A5 {
    return this.asInternal().copyInternal(body)
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.presence] extension property.
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
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage.presence] extension property.
    */
    interface NestedMessage {
        val hasA: Boolean

        val hasCorecursive: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.Data.presence] extension property.
    */
    interface Data {
        val hasGroupInt32: Boolean

        val hasGroupUint32: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField.presence] extension property.
    */
    interface MultiWordGroupField {
        val hasGroupInt32: Boolean

        val hasGroupUint32: Boolean
    }

    interface MessageSetCorrect

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1.presence] extension property.
    */
    interface MessageSetCorrectExtension1 {
        val hasStr: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2.presence] extension property.
    */
    interface MessageSetCorrectExtension2 {
        val hasI: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.ForeignMessageProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.ForeignMessageProto2.presence] extension property.
*/
interface ForeignMessageProto2Presence {
    val hasC: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.GroupField] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.GroupField.presence] extension property.
*/
interface GroupFieldPresence {
    val hasGroupInt32: Boolean

    val hasGroupUint32: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.presence] extension property.
*/
interface UnknownToTestAllTypesPresence {
    val hasOptionalInt32: Boolean

    val hasOptionalString: Boolean

    val hasNestedMessage: Boolean

    val hasOptionalgroup: Boolean

    val hasOptionalBool: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup.presence] extension property.
    */
    interface OptionalGroup {
        val hasA: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.OneStringProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.OneStringProto2.presence] extension property.
*/
interface OneStringProto2Presence {
    val hasData: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.ProtoWithKeywords] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.ProtoWithKeywords.presence] extension property.
*/
interface ProtoWithKeywordsPresence {
    val hasInline: Boolean

    val hasConcept: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2] messages.
* Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.presence] extension property.
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
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage.presence] extension property.
    */
    interface NestedMessage {
        val hasA: Boolean

        val hasCorecursive: Boolean

        val hasOptionalCorecursive: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data.presence] extension property.
    */
    interface Data {
        val hasGroupInt32: Boolean

        val hasGroupUint32: Boolean
    }

    interface MessageSetCorrect

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.presence] extension property.
    */
    interface MessageSetCorrectExtension1 {
        val hasStr: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2] messages.
    * Retrieve it via the [com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.presence] extension property.
    */
    interface MessageSetCorrectExtension2 {
        val hasI: Boolean
    }
}

val TestAllTypesProto2.extensionInt32: Int get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.extensionInt32) ?: TestMessagesProto2KtExtensions.extensionInt32.defaultValue.value

val TestAllTypesProto2.Companion.extensionInt32: ProtoExtensionDescriptor<TestAllTypesProto2, Int> get() = TestMessagesProto2KtExtensions.extensionInt32

val TestAllTypesProto2Presence.hasExtensionInt32: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesProto2KtExtensions.extensionInt32)

var TestAllTypesProto2.Builder.extensionInt32: Int
    get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.extensionInt32) ?: TestMessagesProto2KtExtensions.extensionInt32.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesProto2KtExtensions.extensionInt32, value) }

val TestAllTypesProto2.extensionString: String get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.extensionString) ?: TestMessagesProto2KtExtensions.extensionString.defaultValue.value

val TestAllTypesProto2.Companion.extensionString: ProtoExtensionDescriptor<TestAllTypesProto2, String> get() = TestMessagesProto2KtExtensions.extensionString

val TestAllTypesProto2Presence.hasExtensionString: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesProto2KtExtensions.extensionString)

var TestAllTypesProto2.Builder.extensionString: String
    get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.extensionString) ?: TestMessagesProto2KtExtensions.extensionString.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesProto2KtExtensions.extensionString, value) }

val TestAllTypesProto2.extensionBytes: ByteString get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.extensionBytes) ?: TestMessagesProto2KtExtensions.extensionBytes.defaultValue.value

val TestAllTypesProto2.Companion.extensionBytes: ProtoExtensionDescriptor<TestAllTypesProto2, ByteString> get() = TestMessagesProto2KtExtensions.extensionBytes

val TestAllTypesProto2Presence.hasExtensionBytes: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesProto2KtExtensions.extensionBytes)

var TestAllTypesProto2.Builder.extensionBytes: ByteString
    get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.extensionBytes) ?: TestMessagesProto2KtExtensions.extensionBytes.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesProto2KtExtensions.extensionBytes, value) }

val TestAllTypesProto2.groupfield: GroupField get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.groupfield) ?: TestMessagesProto2KtExtensions.groupfield.defaultValue.value

val TestAllTypesProto2.Companion.groupfield: ProtoExtensionDescriptor<TestAllTypesProto2, GroupField> get() = TestMessagesProto2KtExtensions.groupfield

val TestAllTypesProto2Presence.hasGroupfield: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesProto2KtExtensions.groupfield)

var TestAllTypesProto2.Builder.groupfield: GroupField
    get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.groupfield) ?: TestMessagesProto2KtExtensions.groupfield.defaultValue.value
    set(value) { asInternal().setExtensionValue(TestMessagesProto2KtExtensions.groupfield, value) }

object TestAllTypesProto2Extensions {
    object MessageSetCorrectExtension1Extensions {
        val TestAllTypesProto2.MessageSetCorrect.messageSetExtension: TestAllTypesProto2.MessageSetCorrectExtension1 get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension1.messageSetExtension) ?: TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension1.messageSetExtension.defaultValue.value

        val TestAllTypesProto2.MessageSetCorrect.Companion.messageSetExtension: ProtoExtensionDescriptor<TestAllTypesProto2.MessageSetCorrect, TestAllTypesProto2.MessageSetCorrectExtension1> get() = TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension1.messageSetExtension

        val TestAllTypesProto2Presence.MessageSetCorrect.hasMessageSetExtension: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension1.messageSetExtension)

        var TestAllTypesProto2.MessageSetCorrect.Builder.messageSetExtension: TestAllTypesProto2.MessageSetCorrectExtension1
            get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension1.messageSetExtension) ?: TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension1.messageSetExtension.defaultValue.value
            set(value) { asInternal().setExtensionValue(TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension1.messageSetExtension, value) }
    }

    object MessageSetCorrectExtension2Extensions {
        val TestAllTypesProto2.MessageSetCorrect.messageSetExtension: TestAllTypesProto2.MessageSetCorrectExtension2 get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension2.messageSetExtension) ?: TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension2.messageSetExtension.defaultValue.value

        val TestAllTypesProto2.MessageSetCorrect.Companion.messageSetExtension: ProtoExtensionDescriptor<TestAllTypesProto2.MessageSetCorrect, TestAllTypesProto2.MessageSetCorrectExtension2> get() = TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension2.messageSetExtension

        val TestAllTypesProto2Presence.MessageSetCorrect.hasMessageSetExtension: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension2.messageSetExtension)

        var TestAllTypesProto2.MessageSetCorrect.Builder.messageSetExtension: TestAllTypesProto2.MessageSetCorrectExtension2
            get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension2.messageSetExtension) ?: TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension2.messageSetExtension.defaultValue.value
            set(value) { asInternal().setExtensionValue(TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension2.messageSetExtension, value) }
    }

    object ExtensionWithOneofExtensions {
        val TestAllTypesProto2.MessageSetCorrect.extensionWithOneof: TestAllTypesProto2.ExtensionWithOneof get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllTypesProto2.ExtensionWithOneof.extensionWithOneof) ?: TestMessagesProto2KtExtensions.TestAllTypesProto2.ExtensionWithOneof.extensionWithOneof.defaultValue.value

        val TestAllTypesProto2.MessageSetCorrect.Companion.extensionWithOneof: ProtoExtensionDescriptor<TestAllTypesProto2.MessageSetCorrect, TestAllTypesProto2.ExtensionWithOneof> get() = TestMessagesProto2KtExtensions.TestAllTypesProto2.ExtensionWithOneof.extensionWithOneof

        val TestAllTypesProto2Presence.MessageSetCorrect.hasExtensionWithOneof: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesProto2KtExtensions.TestAllTypesProto2.ExtensionWithOneof.extensionWithOneof)

        var TestAllTypesProto2.MessageSetCorrect.Builder.extensionWithOneof: TestAllTypesProto2.ExtensionWithOneof
            get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllTypesProto2.ExtensionWithOneof.extensionWithOneof) ?: TestMessagesProto2KtExtensions.TestAllTypesProto2.ExtensionWithOneof.extensionWithOneof.defaultValue.value
            set(value) { asInternal().setExtensionValue(TestMessagesProto2KtExtensions.TestAllTypesProto2.ExtensionWithOneof.extensionWithOneof, value) }
    }
}

object TestAllRequiredTypesProto2Extensions {
    object MessageSetCorrectExtension1Extensions {
        val TestAllRequiredTypesProto2.MessageSetCorrect.messageSetExtension: TestAllRequiredTypesProto2.MessageSetCorrectExtension1 get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.messageSetExtension) ?: TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.messageSetExtension.defaultValue.value

        val TestAllRequiredTypesProto2.MessageSetCorrect.Companion.messageSetExtension: ProtoExtensionDescriptor<TestAllRequiredTypesProto2.MessageSetCorrect, TestAllRequiredTypesProto2.MessageSetCorrectExtension1> get() = TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.messageSetExtension

        val TestAllRequiredTypesProto2Presence.MessageSetCorrect.hasMessageSetExtension: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.messageSetExtension)

        var TestAllRequiredTypesProto2.MessageSetCorrect.Builder.messageSetExtension: TestAllRequiredTypesProto2.MessageSetCorrectExtension1
            get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.messageSetExtension) ?: TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.messageSetExtension.defaultValue.value
            set(value) { asInternal().setExtensionValue(TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.messageSetExtension, value) }
    }

    object MessageSetCorrectExtension2Extensions {
        val TestAllRequiredTypesProto2.MessageSetCorrect.messageSetExtension: TestAllRequiredTypesProto2.MessageSetCorrectExtension2 get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.messageSetExtension) ?: TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.messageSetExtension.defaultValue.value

        val TestAllRequiredTypesProto2.MessageSetCorrect.Companion.messageSetExtension: ProtoExtensionDescriptor<TestAllRequiredTypesProto2.MessageSetCorrect, TestAllRequiredTypesProto2.MessageSetCorrectExtension2> get() = TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.messageSetExtension

        val TestAllRequiredTypesProto2Presence.MessageSetCorrect.hasMessageSetExtension: Boolean get() = (this as InternalPresenceObject).hasExtension(TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.messageSetExtension)

        var TestAllRequiredTypesProto2.MessageSetCorrect.Builder.messageSetExtension: TestAllRequiredTypesProto2.MessageSetCorrectExtension2
            get() = asInternal().getExtensionValue(TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.messageSetExtension) ?: TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.messageSetExtension.defaultValue.value
            set(value) { asInternal().setExtensionValue(TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.messageSetExtension, value) }
    }
}
