@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf_test_messages.editions.proto2

import kotlinx.io.Buffer
import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

class TestAllTypesProto2Internal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 58) { 
    private object PresenceIndices { 
        const val optionalInt32: Int = 0
        const val optionalInt64: Int = 1
        const val optionalUint32: Int = 2
        const val optionalUint64: Int = 3
        const val optionalSint32: Int = 4
        const val optionalSint64: Int = 5
        const val optionalFixed32: Int = 6
        const val optionalFixed64: Int = 7
        const val optionalSfixed32: Int = 8
        const val optionalSfixed64: Int = 9
        const val optionalFloat: Int = 10
        const val optionalDouble: Int = 11
        const val optionalBool: Int = 12
        const val optionalString: Int = 13
        const val optionalBytes: Int = 14
        const val optionalNestedMessage: Int = 15
        const val optionalForeignMessage: Int = 16
        const val optionalNestedEnum: Int = 17
        const val optionalForeignEnum: Int = 18
        const val optionalStringPiece: Int = 19
        const val optionalCord: Int = 20
        const val recursiveMessage: Int = 21
        const val data: Int = 22
        const val multiwordgroupfield: Int = 23
        const val defaultInt32: Int = 24
        const val defaultInt64: Int = 25
        const val defaultUint32: Int = 26
        const val defaultUint64: Int = 27
        const val defaultSint32: Int = 28
        const val defaultSint64: Int = 29
        const val defaultFixed32: Int = 30
        const val defaultFixed64: Int = 31
        const val defaultSfixed32: Int = 32
        const val defaultSfixed64: Int = 33
        const val defaultFloat: Int = 34
        const val defaultDouble: Int = 35
        const val defaultBool: Int = 36
        const val defaultString: Int = 37
        const val defaultBytes: Int = 38
        const val fieldname1: Int = 39
        const val fieldName2: Int = 40
        const val FieldName3: Int = 41
        const val field_Name4_: Int = 42
        const val field0name5: Int = 43
        const val field_0Name6: Int = 44
        const val fieldName7: Int = 45
        const val FieldName8: Int = 46
        const val field_Name9: Int = 47
        const val Field_Name10: Int = 48
        const val FIELD_NAME11: Int = 49
        const val FIELDName12: Int = 50
        const val _FieldName13: Int = 51
        const val __FieldName14: Int = 52
        const val field_Name15: Int = 53
        const val field__Name16: Int = 54
        const val fieldName17__: Int = 55
        const val FieldName18__: Int = 56
        const val messageSetCorrect: Int = 57
    }

    private object BytesDefaults { 
        val defaultBytes: ByteArray = "joshua".encodeToByteArray()
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var optionalInt32: Int? by MsgFieldDelegate(PresenceIndices.optionalInt32) { null }
    override var optionalInt64: Long? by MsgFieldDelegate(PresenceIndices.optionalInt64) { null }
    override var optionalUint32: UInt? by MsgFieldDelegate(PresenceIndices.optionalUint32) { null }
    override var optionalUint64: ULong? by MsgFieldDelegate(PresenceIndices.optionalUint64) { null }
    override var optionalSint32: Int? by MsgFieldDelegate(PresenceIndices.optionalSint32) { null }
    override var optionalSint64: Long? by MsgFieldDelegate(PresenceIndices.optionalSint64) { null }
    override var optionalFixed32: UInt? by MsgFieldDelegate(PresenceIndices.optionalFixed32) { null }
    override var optionalFixed64: ULong? by MsgFieldDelegate(PresenceIndices.optionalFixed64) { null }
    override var optionalSfixed32: Int? by MsgFieldDelegate(PresenceIndices.optionalSfixed32) { null }
    override var optionalSfixed64: Long? by MsgFieldDelegate(PresenceIndices.optionalSfixed64) { null }
    override var optionalFloat: Float? by MsgFieldDelegate(PresenceIndices.optionalFloat) { null }
    override var optionalDouble: Double? by MsgFieldDelegate(PresenceIndices.optionalDouble) { null }
    override var optionalBool: Boolean? by MsgFieldDelegate(PresenceIndices.optionalBool) { null }
    override var optionalString: String? by MsgFieldDelegate(PresenceIndices.optionalString) { null }
    override var optionalBytes: ByteArray? by MsgFieldDelegate(PresenceIndices.optionalBytes) { null }
    override var optionalNestedMessage: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage by MsgFieldDelegate(PresenceIndices.optionalNestedMessage) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal() }
    override var optionalForeignMessage: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 by MsgFieldDelegate(PresenceIndices.optionalForeignMessage) { com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal() }
    override var optionalNestedEnum: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum? by MsgFieldDelegate(PresenceIndices.optionalNestedEnum) { null }
    override var optionalForeignEnum: com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2? by MsgFieldDelegate(PresenceIndices.optionalForeignEnum) { null }
    override var optionalStringPiece: String? by MsgFieldDelegate(PresenceIndices.optionalStringPiece) { null }
    override var optionalCord: String? by MsgFieldDelegate(PresenceIndices.optionalCord) { null }
    override var recursiveMessage: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2 by MsgFieldDelegate(PresenceIndices.recursiveMessage) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal() }
    override var repeatedInt32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }
    override var repeatedInt64: List<kotlin.Long> by MsgFieldDelegate { mutableListOf() }
    override var repeatedUint32: List<kotlin.UInt> by MsgFieldDelegate { mutableListOf() }
    override var repeatedUint64: List<kotlin.ULong> by MsgFieldDelegate { mutableListOf() }
    override var repeatedSint32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }
    override var repeatedSint64: List<kotlin.Long> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFixed32: List<kotlin.UInt> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFixed64: List<kotlin.ULong> by MsgFieldDelegate { mutableListOf() }
    override var repeatedSfixed32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }
    override var repeatedSfixed64: List<kotlin.Long> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFloat: List<kotlin.Float> by MsgFieldDelegate { mutableListOf() }
    override var repeatedDouble: List<kotlin.Double> by MsgFieldDelegate { mutableListOf() }
    override var repeatedBool: List<kotlin.Boolean> by MsgFieldDelegate { mutableListOf() }
    override var repeatedString: List<kotlin.String> by MsgFieldDelegate { mutableListOf() }
    override var repeatedBytes: List<kotlin.ByteArray> by MsgFieldDelegate { mutableListOf() }
    override var repeatedNestedMessage: List<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignMessage: List<com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2> by MsgFieldDelegate { mutableListOf() }
    override var repeatedNestedEnum: List<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignEnum: List<com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2> by MsgFieldDelegate { mutableListOf() }
    override var repeatedStringPiece: List<kotlin.String> by MsgFieldDelegate { mutableListOf() }
    override var repeatedCord: List<kotlin.String> by MsgFieldDelegate { mutableListOf() }
    override var packedInt32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }
    override var packedInt64: List<kotlin.Long> by MsgFieldDelegate { mutableListOf() }
    override var packedUint32: List<kotlin.UInt> by MsgFieldDelegate { mutableListOf() }
    override var packedUint64: List<kotlin.ULong> by MsgFieldDelegate { mutableListOf() }
    override var packedSint32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }
    override var packedSint64: List<kotlin.Long> by MsgFieldDelegate { mutableListOf() }
    override var packedFixed32: List<kotlin.UInt> by MsgFieldDelegate { mutableListOf() }
    override var packedFixed64: List<kotlin.ULong> by MsgFieldDelegate { mutableListOf() }
    override var packedSfixed32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }
    override var packedSfixed64: List<kotlin.Long> by MsgFieldDelegate { mutableListOf() }
    override var packedFloat: List<kotlin.Float> by MsgFieldDelegate { mutableListOf() }
    override var packedDouble: List<kotlin.Double> by MsgFieldDelegate { mutableListOf() }
    override var packedBool: List<kotlin.Boolean> by MsgFieldDelegate { mutableListOf() }
    override var packedNestedEnum: List<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum> by MsgFieldDelegate { mutableListOf() }
    override var unpackedInt32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }
    override var unpackedInt64: List<kotlin.Long> by MsgFieldDelegate { mutableListOf() }
    override var unpackedUint32: List<kotlin.UInt> by MsgFieldDelegate { mutableListOf() }
    override var unpackedUint64: List<kotlin.ULong> by MsgFieldDelegate { mutableListOf() }
    override var unpackedSint32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }
    override var unpackedSint64: List<kotlin.Long> by MsgFieldDelegate { mutableListOf() }
    override var unpackedFixed32: List<kotlin.UInt> by MsgFieldDelegate { mutableListOf() }
    override var unpackedFixed64: List<kotlin.ULong> by MsgFieldDelegate { mutableListOf() }
    override var unpackedSfixed32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }
    override var unpackedSfixed64: List<kotlin.Long> by MsgFieldDelegate { mutableListOf() }
    override var unpackedFloat: List<kotlin.Float> by MsgFieldDelegate { mutableListOf() }
    override var unpackedDouble: List<kotlin.Double> by MsgFieldDelegate { mutableListOf() }
    override var unpackedBool: List<kotlin.Boolean> by MsgFieldDelegate { mutableListOf() }
    override var unpackedNestedEnum: List<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum> by MsgFieldDelegate { mutableListOf() }
    override var mapInt32Int32: Map<kotlin.Int, kotlin.Int> by MsgFieldDelegate { mutableMapOf() }
    override var mapInt64Int64: Map<kotlin.Long, kotlin.Long> by MsgFieldDelegate { mutableMapOf() }
    override var mapUint32Uint32: Map<kotlin.UInt, kotlin.UInt> by MsgFieldDelegate { mutableMapOf() }
    override var mapUint64Uint64: Map<kotlin.ULong, kotlin.ULong> by MsgFieldDelegate { mutableMapOf() }
    override var mapSint32Sint32: Map<kotlin.Int, kotlin.Int> by MsgFieldDelegate { mutableMapOf() }
    override var mapSint64Sint64: Map<kotlin.Long, kotlin.Long> by MsgFieldDelegate { mutableMapOf() }
    override var mapFixed32Fixed32: Map<kotlin.UInt, kotlin.UInt> by MsgFieldDelegate { mutableMapOf() }
    override var mapFixed64Fixed64: Map<kotlin.ULong, kotlin.ULong> by MsgFieldDelegate { mutableMapOf() }
    override var mapSfixed32Sfixed32: Map<kotlin.Int, kotlin.Int> by MsgFieldDelegate { mutableMapOf() }
    override var mapSfixed64Sfixed64: Map<kotlin.Long, kotlin.Long> by MsgFieldDelegate { mutableMapOf() }
    override var mapInt32Bool: Map<kotlin.Int, kotlin.Boolean> by MsgFieldDelegate { mutableMapOf() }
    override var mapInt32Float: Map<kotlin.Int, kotlin.Float> by MsgFieldDelegate { mutableMapOf() }
    override var mapInt32Double: Map<kotlin.Int, kotlin.Double> by MsgFieldDelegate { mutableMapOf() }
    override var mapInt32NestedMessage: Map<kotlin.Int, com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage> by MsgFieldDelegate { mutableMapOf() }
    override var mapBoolBool: Map<kotlin.Boolean, kotlin.Boolean> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringString: Map<kotlin.String, kotlin.String> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringBytes: Map<kotlin.String, kotlin.ByteArray> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringNestedMessage: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignMessage: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringNestedEnum: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignEnum: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2> by MsgFieldDelegate { mutableMapOf() }
    override var data: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data by MsgFieldDelegate(PresenceIndices.data) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal() }
    override var multiwordgroupfield: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField by MsgFieldDelegate(PresenceIndices.multiwordgroupfield) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal() }
    override var defaultInt32: Int by MsgFieldDelegate(PresenceIndices.defaultInt32) { -123456789 }
    override var defaultInt64: Long by MsgFieldDelegate(PresenceIndices.defaultInt64) { -9123456789123456789L }
    override var defaultUint32: UInt by MsgFieldDelegate(PresenceIndices.defaultUint32) { 2123456789u }
    override var defaultUint64: ULong by MsgFieldDelegate(PresenceIndices.defaultUint64) { 10123456789123456789uL }
    override var defaultSint32: Int by MsgFieldDelegate(PresenceIndices.defaultSint32) { -123456789 }
    override var defaultSint64: Long by MsgFieldDelegate(PresenceIndices.defaultSint64) { -9123456789123456789L }
    override var defaultFixed32: UInt by MsgFieldDelegate(PresenceIndices.defaultFixed32) { 2123456789u }
    override var defaultFixed64: ULong by MsgFieldDelegate(PresenceIndices.defaultFixed64) { 10123456789123456789uL }
    override var defaultSfixed32: Int by MsgFieldDelegate(PresenceIndices.defaultSfixed32) { -123456789 }
    override var defaultSfixed64: Long by MsgFieldDelegate(PresenceIndices.defaultSfixed64) { -9123456789123456789L }
    override var defaultFloat: Float by MsgFieldDelegate(PresenceIndices.defaultFloat) { Float.fromBits(0x50061C46) }
    override var defaultDouble: Double by MsgFieldDelegate(PresenceIndices.defaultDouble) { Double.fromBits(0x44ADA56A4B0835C0L) }
    override var defaultBool: Boolean by MsgFieldDelegate(PresenceIndices.defaultBool) { true }
    override var defaultString: String by MsgFieldDelegate(PresenceIndices.defaultString) { "Rosebud" }
    override var defaultBytes: ByteArray by MsgFieldDelegate(PresenceIndices.defaultBytes) { BytesDefaults.defaultBytes }
    override var fieldname1: Int? by MsgFieldDelegate(PresenceIndices.fieldname1) { null }
    override var fieldName2: Int? by MsgFieldDelegate(PresenceIndices.fieldName2) { null }
    override var FieldName3: Int? by MsgFieldDelegate(PresenceIndices.FieldName3) { null }
    override var field_Name4_: Int? by MsgFieldDelegate(PresenceIndices.field_Name4_) { null }
    override var field0name5: Int? by MsgFieldDelegate(PresenceIndices.field0name5) { null }
    override var field_0Name6: Int? by MsgFieldDelegate(PresenceIndices.field_0Name6) { null }
    override var fieldName7: Int? by MsgFieldDelegate(PresenceIndices.fieldName7) { null }
    override var FieldName8: Int? by MsgFieldDelegate(PresenceIndices.FieldName8) { null }
    override var field_Name9: Int? by MsgFieldDelegate(PresenceIndices.field_Name9) { null }
    override var Field_Name10: Int? by MsgFieldDelegate(PresenceIndices.Field_Name10) { null }
    override var FIELD_NAME11: Int? by MsgFieldDelegate(PresenceIndices.FIELD_NAME11) { null }
    override var FIELDName12: Int? by MsgFieldDelegate(PresenceIndices.FIELDName12) { null }
    override var _FieldName13: Int? by MsgFieldDelegate(PresenceIndices._FieldName13) { null }
    override var __FieldName14: Int? by MsgFieldDelegate(PresenceIndices.__FieldName14) { null }
    override var field_Name15: Int? by MsgFieldDelegate(PresenceIndices.field_Name15) { null }
    override var field__Name16: Int? by MsgFieldDelegate(PresenceIndices.field__Name16) { null }
    override var fieldName17__: Int? by MsgFieldDelegate(PresenceIndices.fieldName17__) { null }
    override var FieldName18__: Int? by MsgFieldDelegate(PresenceIndices.FieldName18__) { null }
    override var messageSetCorrect: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect by MsgFieldDelegate(PresenceIndices.messageSetCorrect) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal() }
    override var oneofField: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField? = null

    @kotlinx.rpc.internal.utils.InternalRpcApi
    val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence = object : com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence { 
        override val hasOptionalInt32: kotlin.Boolean get() = presenceMask[0]

        override val hasOptionalInt64: kotlin.Boolean get() = presenceMask[1]

        override val hasOptionalUint32: kotlin.Boolean get() = presenceMask[2]

        override val hasOptionalUint64: kotlin.Boolean get() = presenceMask[3]

        override val hasOptionalSint32: kotlin.Boolean get() = presenceMask[4]

        override val hasOptionalSint64: kotlin.Boolean get() = presenceMask[5]

        override val hasOptionalFixed32: kotlin.Boolean get() = presenceMask[6]

        override val hasOptionalFixed64: kotlin.Boolean get() = presenceMask[7]

        override val hasOptionalSfixed32: kotlin.Boolean get() = presenceMask[8]

        override val hasOptionalSfixed64: kotlin.Boolean get() = presenceMask[9]

        override val hasOptionalFloat: kotlin.Boolean get() = presenceMask[10]

        override val hasOptionalDouble: kotlin.Boolean get() = presenceMask[11]

        override val hasOptionalBool: kotlin.Boolean get() = presenceMask[12]

        override val hasOptionalString: kotlin.Boolean get() = presenceMask[13]

        override val hasOptionalBytes: kotlin.Boolean get() = presenceMask[14]

        override val hasOptionalNestedMessage: kotlin.Boolean get() = presenceMask[15]

        override val hasOptionalForeignMessage: kotlin.Boolean get() = presenceMask[16]

        override val hasOptionalNestedEnum: kotlin.Boolean get() = presenceMask[17]

        override val hasOptionalForeignEnum: kotlin.Boolean get() = presenceMask[18]

        override val hasOptionalStringPiece: kotlin.Boolean get() = presenceMask[19]

        override val hasOptionalCord: kotlin.Boolean get() = presenceMask[20]

        override val hasRecursiveMessage: kotlin.Boolean get() = presenceMask[21]

        override val hasData: kotlin.Boolean get() = presenceMask[22]

        override val hasMultiwordgroupfield: kotlin.Boolean get() = presenceMask[23]

        override val hasDefaultInt32: kotlin.Boolean get() = presenceMask[24]

        override val hasDefaultInt64: kotlin.Boolean get() = presenceMask[25]

        override val hasDefaultUint32: kotlin.Boolean get() = presenceMask[26]

        override val hasDefaultUint64: kotlin.Boolean get() = presenceMask[27]

        override val hasDefaultSint32: kotlin.Boolean get() = presenceMask[28]

        override val hasDefaultSint64: kotlin.Boolean get() = presenceMask[29]

        override val hasDefaultFixed32: kotlin.Boolean get() = presenceMask[30]

        override val hasDefaultFixed64: kotlin.Boolean get() = presenceMask[31]

        override val hasDefaultSfixed32: kotlin.Boolean get() = presenceMask[32]

        override val hasDefaultSfixed64: kotlin.Boolean get() = presenceMask[33]

        override val hasDefaultFloat: kotlin.Boolean get() = presenceMask[34]

        override val hasDefaultDouble: kotlin.Boolean get() = presenceMask[35]

        override val hasDefaultBool: kotlin.Boolean get() = presenceMask[36]

        override val hasDefaultString: kotlin.Boolean get() = presenceMask[37]

        override val hasDefaultBytes: kotlin.Boolean get() = presenceMask[38]

        override val hasFieldname1: kotlin.Boolean get() = presenceMask[39]

        override val hasFieldName2: kotlin.Boolean get() = presenceMask[40]

        override val hasFieldName3: kotlin.Boolean get() = presenceMask[41]

        override val hasField_Name4_: kotlin.Boolean get() = presenceMask[42]

        override val hasField0name5: kotlin.Boolean get() = presenceMask[43]

        override val hasField_0Name6: kotlin.Boolean get() = presenceMask[44]

        override val hasFieldName7: kotlin.Boolean get() = presenceMask[45]

        override val hasFieldName8: kotlin.Boolean get() = presenceMask[46]

        override val hasField_Name9: kotlin.Boolean get() = presenceMask[47]

        override val hasField_Name10: kotlin.Boolean get() = presenceMask[48]

        override val hasFIELD_NAME11: kotlin.Boolean get() = presenceMask[49]

        override val hasFIELDName12: kotlin.Boolean get() = presenceMask[50]

        override val has_FieldName13: kotlin.Boolean get() = presenceMask[51]

        override val has__FieldName14: kotlin.Boolean get() = presenceMask[52]

        override val hasField_Name15: kotlin.Boolean get() = presenceMask[53]

        override val hasField__Name16: kotlin.Boolean get() = presenceMask[54]

        override val hasFieldName17__: kotlin.Boolean get() = presenceMask[55]

        override val hasFieldName18__: kotlin.Boolean get() = presenceMask[56]

        override val hasMessageSetCorrect: kotlin.Boolean get() = presenceMask[57]
    }

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        var result = if (presenceMask[0]) (optionalInt32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[1]) (optionalInt64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[2]) (optionalUint32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[3]) (optionalUint64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[4]) (optionalSint32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[5]) (optionalSint64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[6]) (optionalFixed32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[7]) (optionalFixed64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[8]) (optionalSfixed32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[9]) (optionalSfixed64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[10]) (optionalFloat?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[11]) (optionalDouble?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[12]) (optionalBool?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[13]) (optionalString?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[14]) (optionalBytes?.contentHashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[15]) optionalNestedMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[16]) optionalForeignMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[17]) (optionalNestedEnum?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[18]) (optionalForeignEnum?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[19]) (optionalStringPiece?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[20]) (optionalCord?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[21]) recursiveMessage.hashCode() else 0
        result = 31 * result + repeatedInt32.hashCode()
        result = 31 * result + repeatedInt64.hashCode()
        result = 31 * result + repeatedUint32.hashCode()
        result = 31 * result + repeatedUint64.hashCode()
        result = 31 * result + repeatedSint32.hashCode()
        result = 31 * result + repeatedSint64.hashCode()
        result = 31 * result + repeatedFixed32.hashCode()
        result = 31 * result + repeatedFixed64.hashCode()
        result = 31 * result + repeatedSfixed32.hashCode()
        result = 31 * result + repeatedSfixed64.hashCode()
        result = 31 * result + repeatedFloat.hashCode()
        result = 31 * result + repeatedDouble.hashCode()
        result = 31 * result + repeatedBool.hashCode()
        result = 31 * result + repeatedString.hashCode()
        result = 31 * result + repeatedBytes.hashCode()
        result = 31 * result + repeatedNestedMessage.hashCode()
        result = 31 * result + repeatedForeignMessage.hashCode()
        result = 31 * result + repeatedNestedEnum.hashCode()
        result = 31 * result + repeatedForeignEnum.hashCode()
        result = 31 * result + repeatedStringPiece.hashCode()
        result = 31 * result + repeatedCord.hashCode()
        result = 31 * result + packedInt32.hashCode()
        result = 31 * result + packedInt64.hashCode()
        result = 31 * result + packedUint32.hashCode()
        result = 31 * result + packedUint64.hashCode()
        result = 31 * result + packedSint32.hashCode()
        result = 31 * result + packedSint64.hashCode()
        result = 31 * result + packedFixed32.hashCode()
        result = 31 * result + packedFixed64.hashCode()
        result = 31 * result + packedSfixed32.hashCode()
        result = 31 * result + packedSfixed64.hashCode()
        result = 31 * result + packedFloat.hashCode()
        result = 31 * result + packedDouble.hashCode()
        result = 31 * result + packedBool.hashCode()
        result = 31 * result + packedNestedEnum.hashCode()
        result = 31 * result + unpackedInt32.hashCode()
        result = 31 * result + unpackedInt64.hashCode()
        result = 31 * result + unpackedUint32.hashCode()
        result = 31 * result + unpackedUint64.hashCode()
        result = 31 * result + unpackedSint32.hashCode()
        result = 31 * result + unpackedSint64.hashCode()
        result = 31 * result + unpackedFixed32.hashCode()
        result = 31 * result + unpackedFixed64.hashCode()
        result = 31 * result + unpackedSfixed32.hashCode()
        result = 31 * result + unpackedSfixed64.hashCode()
        result = 31 * result + unpackedFloat.hashCode()
        result = 31 * result + unpackedDouble.hashCode()
        result = 31 * result + unpackedBool.hashCode()
        result = 31 * result + unpackedNestedEnum.hashCode()
        result = 31 * result + mapInt32Int32.hashCode()
        result = 31 * result + mapInt64Int64.hashCode()
        result = 31 * result + mapUint32Uint32.hashCode()
        result = 31 * result + mapUint64Uint64.hashCode()
        result = 31 * result + mapSint32Sint32.hashCode()
        result = 31 * result + mapSint64Sint64.hashCode()
        result = 31 * result + mapFixed32Fixed32.hashCode()
        result = 31 * result + mapFixed64Fixed64.hashCode()
        result = 31 * result + mapSfixed32Sfixed32.hashCode()
        result = 31 * result + mapSfixed64Sfixed64.hashCode()
        result = 31 * result + mapInt32Bool.hashCode()
        result = 31 * result + mapInt32Float.hashCode()
        result = 31 * result + mapInt32Double.hashCode()
        result = 31 * result + mapInt32NestedMessage.hashCode()
        result = 31 * result + mapBoolBool.hashCode()
        result = 31 * result + mapStringString.hashCode()
        result = 31 * result + mapStringBytes.hashCode()
        result = 31 * result + mapStringNestedMessage.hashCode()
        result = 31 * result + mapStringForeignMessage.hashCode()
        result = 31 * result + mapStringNestedEnum.hashCode()
        result = 31 * result + mapStringForeignEnum.hashCode()
        result = 31 * result + if (presenceMask[22]) data.hashCode() else 0
        result = 31 * result + if (presenceMask[23]) multiwordgroupfield.hashCode() else 0
        result = 31 * result + if (presenceMask[24]) defaultInt32.hashCode() else 0
        result = 31 * result + if (presenceMask[25]) defaultInt64.hashCode() else 0
        result = 31 * result + if (presenceMask[26]) defaultUint32.hashCode() else 0
        result = 31 * result + if (presenceMask[27]) defaultUint64.hashCode() else 0
        result = 31 * result + if (presenceMask[28]) defaultSint32.hashCode() else 0
        result = 31 * result + if (presenceMask[29]) defaultSint64.hashCode() else 0
        result = 31 * result + if (presenceMask[30]) defaultFixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[31]) defaultFixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[32]) defaultSfixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[33]) defaultSfixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[34]) defaultFloat.hashCode() else 0
        result = 31 * result + if (presenceMask[35]) defaultDouble.hashCode() else 0
        result = 31 * result + if (presenceMask[36]) defaultBool.hashCode() else 0
        result = 31 * result + if (presenceMask[37]) defaultString.hashCode() else 0
        result = 31 * result + if (presenceMask[38]) defaultBytes.contentHashCode() else 0
        result = 31 * result + if (presenceMask[39]) (fieldname1?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[40]) (fieldName2?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[41]) (FieldName3?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[42]) (field_Name4_?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[43]) (field0name5?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[44]) (field_0Name6?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[45]) (fieldName7?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[46]) (FieldName8?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[47]) (field_Name9?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[48]) (Field_Name10?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[49]) (FIELD_NAME11?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[50]) (FIELDName12?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[51]) (_FieldName13?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[52]) (__FieldName14?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[53]) (field_Name15?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[54]) (field__Name16?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[55]) (fieldName17__?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[56]) (FieldName18__?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[57]) messageSetCorrect.hashCode() else 0
        result = 31 * result + (oneofField?.oneOfHashCode() ?: 0)
        return result
    }

    fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.oneOfHashCode(): kotlin.Int { 
        val offset = when (this) { 
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint32 -> 0
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofNestedMessage -> 1
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofString -> 2
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBytes -> 3
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBool -> 4
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint64 -> 5
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofFloat -> 6
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofDouble -> 7
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofEnum -> 8
        }

        return hashCode() + offset
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAllTypesProto2Internal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && optionalInt32 != other.optionalInt32) return false
        if (presenceMask[1] && optionalInt64 != other.optionalInt64) return false
        if (presenceMask[2] && optionalUint32 != other.optionalUint32) return false
        if (presenceMask[3] && optionalUint64 != other.optionalUint64) return false
        if (presenceMask[4] && optionalSint32 != other.optionalSint32) return false
        if (presenceMask[5] && optionalSint64 != other.optionalSint64) return false
        if (presenceMask[6] && optionalFixed32 != other.optionalFixed32) return false
        if (presenceMask[7] && optionalFixed64 != other.optionalFixed64) return false
        if (presenceMask[8] && optionalSfixed32 != other.optionalSfixed32) return false
        if (presenceMask[9] && optionalSfixed64 != other.optionalSfixed64) return false
        if (presenceMask[10] && optionalFloat != other.optionalFloat) return false
        if (presenceMask[11] && optionalDouble != other.optionalDouble) return false
        if (presenceMask[12] && optionalBool != other.optionalBool) return false
        if (presenceMask[13] && optionalString != other.optionalString) return false
        if (presenceMask[14] && ((optionalBytes != null && (other.optionalBytes == null || !optionalBytes!!.contentEquals(other.optionalBytes!!))) || optionalBytes == null)) return false
        if (presenceMask[15] && optionalNestedMessage != other.optionalNestedMessage) return false
        if (presenceMask[16] && optionalForeignMessage != other.optionalForeignMessage) return false
        if (presenceMask[17] && optionalNestedEnum != other.optionalNestedEnum) return false
        if (presenceMask[18] && optionalForeignEnum != other.optionalForeignEnum) return false
        if (presenceMask[19] && optionalStringPiece != other.optionalStringPiece) return false
        if (presenceMask[20] && optionalCord != other.optionalCord) return false
        if (presenceMask[21] && recursiveMessage != other.recursiveMessage) return false
        if (repeatedInt32 != other.repeatedInt32) return false
        if (repeatedInt64 != other.repeatedInt64) return false
        if (repeatedUint32 != other.repeatedUint32) return false
        if (repeatedUint64 != other.repeatedUint64) return false
        if (repeatedSint32 != other.repeatedSint32) return false
        if (repeatedSint64 != other.repeatedSint64) return false
        if (repeatedFixed32 != other.repeatedFixed32) return false
        if (repeatedFixed64 != other.repeatedFixed64) return false
        if (repeatedSfixed32 != other.repeatedSfixed32) return false
        if (repeatedSfixed64 != other.repeatedSfixed64) return false
        if (repeatedFloat != other.repeatedFloat) return false
        if (repeatedDouble != other.repeatedDouble) return false
        if (repeatedBool != other.repeatedBool) return false
        if (repeatedString != other.repeatedString) return false
        if (repeatedBytes != other.repeatedBytes) return false
        if (repeatedNestedMessage != other.repeatedNestedMessage) return false
        if (repeatedForeignMessage != other.repeatedForeignMessage) return false
        if (repeatedNestedEnum != other.repeatedNestedEnum) return false
        if (repeatedForeignEnum != other.repeatedForeignEnum) return false
        if (repeatedStringPiece != other.repeatedStringPiece) return false
        if (repeatedCord != other.repeatedCord) return false
        if (packedInt32 != other.packedInt32) return false
        if (packedInt64 != other.packedInt64) return false
        if (packedUint32 != other.packedUint32) return false
        if (packedUint64 != other.packedUint64) return false
        if (packedSint32 != other.packedSint32) return false
        if (packedSint64 != other.packedSint64) return false
        if (packedFixed32 != other.packedFixed32) return false
        if (packedFixed64 != other.packedFixed64) return false
        if (packedSfixed32 != other.packedSfixed32) return false
        if (packedSfixed64 != other.packedSfixed64) return false
        if (packedFloat != other.packedFloat) return false
        if (packedDouble != other.packedDouble) return false
        if (packedBool != other.packedBool) return false
        if (packedNestedEnum != other.packedNestedEnum) return false
        if (unpackedInt32 != other.unpackedInt32) return false
        if (unpackedInt64 != other.unpackedInt64) return false
        if (unpackedUint32 != other.unpackedUint32) return false
        if (unpackedUint64 != other.unpackedUint64) return false
        if (unpackedSint32 != other.unpackedSint32) return false
        if (unpackedSint64 != other.unpackedSint64) return false
        if (unpackedFixed32 != other.unpackedFixed32) return false
        if (unpackedFixed64 != other.unpackedFixed64) return false
        if (unpackedSfixed32 != other.unpackedSfixed32) return false
        if (unpackedSfixed64 != other.unpackedSfixed64) return false
        if (unpackedFloat != other.unpackedFloat) return false
        if (unpackedDouble != other.unpackedDouble) return false
        if (unpackedBool != other.unpackedBool) return false
        if (unpackedNestedEnum != other.unpackedNestedEnum) return false
        if (mapInt32Int32 != other.mapInt32Int32) return false
        if (mapInt64Int64 != other.mapInt64Int64) return false
        if (mapUint32Uint32 != other.mapUint32Uint32) return false
        if (mapUint64Uint64 != other.mapUint64Uint64) return false
        if (mapSint32Sint32 != other.mapSint32Sint32) return false
        if (mapSint64Sint64 != other.mapSint64Sint64) return false
        if (mapFixed32Fixed32 != other.mapFixed32Fixed32) return false
        if (mapFixed64Fixed64 != other.mapFixed64Fixed64) return false
        if (mapSfixed32Sfixed32 != other.mapSfixed32Sfixed32) return false
        if (mapSfixed64Sfixed64 != other.mapSfixed64Sfixed64) return false
        if (mapInt32Bool != other.mapInt32Bool) return false
        if (mapInt32Float != other.mapInt32Float) return false
        if (mapInt32Double != other.mapInt32Double) return false
        if (mapInt32NestedMessage != other.mapInt32NestedMessage) return false
        if (mapBoolBool != other.mapBoolBool) return false
        if (mapStringString != other.mapStringString) return false
        if (mapStringBytes != other.mapStringBytes) return false
        if (mapStringNestedMessage != other.mapStringNestedMessage) return false
        if (mapStringForeignMessage != other.mapStringForeignMessage) return false
        if (mapStringNestedEnum != other.mapStringNestedEnum) return false
        if (mapStringForeignEnum != other.mapStringForeignEnum) return false
        if (presenceMask[22] && data != other.data) return false
        if (presenceMask[23] && multiwordgroupfield != other.multiwordgroupfield) return false
        if (presenceMask[24] && defaultInt32 != other.defaultInt32) return false
        if (presenceMask[25] && defaultInt64 != other.defaultInt64) return false
        if (presenceMask[26] && defaultUint32 != other.defaultUint32) return false
        if (presenceMask[27] && defaultUint64 != other.defaultUint64) return false
        if (presenceMask[28] && defaultSint32 != other.defaultSint32) return false
        if (presenceMask[29] && defaultSint64 != other.defaultSint64) return false
        if (presenceMask[30] && defaultFixed32 != other.defaultFixed32) return false
        if (presenceMask[31] && defaultFixed64 != other.defaultFixed64) return false
        if (presenceMask[32] && defaultSfixed32 != other.defaultSfixed32) return false
        if (presenceMask[33] && defaultSfixed64 != other.defaultSfixed64) return false
        if (presenceMask[34] && defaultFloat != other.defaultFloat) return false
        if (presenceMask[35] && defaultDouble != other.defaultDouble) return false
        if (presenceMask[36] && defaultBool != other.defaultBool) return false
        if (presenceMask[37] && defaultString != other.defaultString) return false
        if (presenceMask[38] && !defaultBytes.contentEquals(other.defaultBytes)) return false
        if (presenceMask[39] && fieldname1 != other.fieldname1) return false
        if (presenceMask[40] && fieldName2 != other.fieldName2) return false
        if (presenceMask[41] && FieldName3 != other.FieldName3) return false
        if (presenceMask[42] && field_Name4_ != other.field_Name4_) return false
        if (presenceMask[43] && field0name5 != other.field0name5) return false
        if (presenceMask[44] && field_0Name6 != other.field_0Name6) return false
        if (presenceMask[45] && fieldName7 != other.fieldName7) return false
        if (presenceMask[46] && FieldName8 != other.FieldName8) return false
        if (presenceMask[47] && field_Name9 != other.field_Name9) return false
        if (presenceMask[48] && Field_Name10 != other.Field_Name10) return false
        if (presenceMask[49] && FIELD_NAME11 != other.FIELD_NAME11) return false
        if (presenceMask[50] && FIELDName12 != other.FIELDName12) return false
        if (presenceMask[51] && _FieldName13 != other._FieldName13) return false
        if (presenceMask[52] && __FieldName14 != other.__FieldName14) return false
        if (presenceMask[53] && field_Name15 != other.field_Name15) return false
        if (presenceMask[54] && field__Name16 != other.field__Name16) return false
        if (presenceMask[55] && fieldName17__ != other.fieldName17__) return false
        if (presenceMask[56] && FieldName18__ != other.FieldName18__) return false
        if (presenceMask[57] && messageSetCorrect != other.messageSetCorrect) return false
        if (oneofField != other.oneofField) return false
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2(")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}optionalInt32=${optionalInt32},")
            } else { 
                appendLine("${nextIndentString}optionalInt32=<unset>,")
            }

            if (presenceMask[1]) { 
                appendLine("${nextIndentString}optionalInt64=${optionalInt64},")
            } else { 
                appendLine("${nextIndentString}optionalInt64=<unset>,")
            }

            if (presenceMask[2]) { 
                appendLine("${nextIndentString}optionalUint32=${optionalUint32},")
            } else { 
                appendLine("${nextIndentString}optionalUint32=<unset>,")
            }

            if (presenceMask[3]) { 
                appendLine("${nextIndentString}optionalUint64=${optionalUint64},")
            } else { 
                appendLine("${nextIndentString}optionalUint64=<unset>,")
            }

            if (presenceMask[4]) { 
                appendLine("${nextIndentString}optionalSint32=${optionalSint32},")
            } else { 
                appendLine("${nextIndentString}optionalSint32=<unset>,")
            }

            if (presenceMask[5]) { 
                appendLine("${nextIndentString}optionalSint64=${optionalSint64},")
            } else { 
                appendLine("${nextIndentString}optionalSint64=<unset>,")
            }

            if (presenceMask[6]) { 
                appendLine("${nextIndentString}optionalFixed32=${optionalFixed32},")
            } else { 
                appendLine("${nextIndentString}optionalFixed32=<unset>,")
            }

            if (presenceMask[7]) { 
                appendLine("${nextIndentString}optionalFixed64=${optionalFixed64},")
            } else { 
                appendLine("${nextIndentString}optionalFixed64=<unset>,")
            }

            if (presenceMask[8]) { 
                appendLine("${nextIndentString}optionalSfixed32=${optionalSfixed32},")
            } else { 
                appendLine("${nextIndentString}optionalSfixed32=<unset>,")
            }

            if (presenceMask[9]) { 
                appendLine("${nextIndentString}optionalSfixed64=${optionalSfixed64},")
            } else { 
                appendLine("${nextIndentString}optionalSfixed64=<unset>,")
            }

            if (presenceMask[10]) { 
                appendLine("${nextIndentString}optionalFloat=${optionalFloat},")
            } else { 
                appendLine("${nextIndentString}optionalFloat=<unset>,")
            }

            if (presenceMask[11]) { 
                appendLine("${nextIndentString}optionalDouble=${optionalDouble},")
            } else { 
                appendLine("${nextIndentString}optionalDouble=<unset>,")
            }

            if (presenceMask[12]) { 
                appendLine("${nextIndentString}optionalBool=${optionalBool},")
            } else { 
                appendLine("${nextIndentString}optionalBool=<unset>,")
            }

            if (presenceMask[13]) { 
                appendLine("${nextIndentString}optionalString=${optionalString},")
            } else { 
                appendLine("${nextIndentString}optionalString=<unset>,")
            }

            if (presenceMask[14]) { 
                appendLine("${nextIndentString}optionalBytes=${optionalBytes.contentToString()},")
            } else { 
                appendLine("${nextIndentString}optionalBytes=<unset>,")
            }

            if (presenceMask[15]) { 
                appendLine("${nextIndentString}optionalNestedMessage=${optionalNestedMessage.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}optionalNestedMessage=<unset>,")
            }

            if (presenceMask[16]) { 
                appendLine("${nextIndentString}optionalForeignMessage=${optionalForeignMessage.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}optionalForeignMessage=<unset>,")
            }

            if (presenceMask[17]) { 
                appendLine("${nextIndentString}optionalNestedEnum=${optionalNestedEnum},")
            } else { 
                appendLine("${nextIndentString}optionalNestedEnum=<unset>,")
            }

            if (presenceMask[18]) { 
                appendLine("${nextIndentString}optionalForeignEnum=${optionalForeignEnum},")
            } else { 
                appendLine("${nextIndentString}optionalForeignEnum=<unset>,")
            }

            if (presenceMask[19]) { 
                appendLine("${nextIndentString}optionalStringPiece=${optionalStringPiece},")
            } else { 
                appendLine("${nextIndentString}optionalStringPiece=<unset>,")
            }

            if (presenceMask[20]) { 
                appendLine("${nextIndentString}optionalCord=${optionalCord},")
            } else { 
                appendLine("${nextIndentString}optionalCord=<unset>,")
            }

            if (presenceMask[21]) { 
                appendLine("${nextIndentString}recursiveMessage=${recursiveMessage.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}recursiveMessage=<unset>,")
            }

            appendLine("${nextIndentString}repeatedInt32=${repeatedInt32},")
            appendLine("${nextIndentString}repeatedInt64=${repeatedInt64},")
            appendLine("${nextIndentString}repeatedUint32=${repeatedUint32},")
            appendLine("${nextIndentString}repeatedUint64=${repeatedUint64},")
            appendLine("${nextIndentString}repeatedSint32=${repeatedSint32},")
            appendLine("${nextIndentString}repeatedSint64=${repeatedSint64},")
            appendLine("${nextIndentString}repeatedFixed32=${repeatedFixed32},")
            appendLine("${nextIndentString}repeatedFixed64=${repeatedFixed64},")
            appendLine("${nextIndentString}repeatedSfixed32=${repeatedSfixed32},")
            appendLine("${nextIndentString}repeatedSfixed64=${repeatedSfixed64},")
            appendLine("${nextIndentString}repeatedFloat=${repeatedFloat},")
            appendLine("${nextIndentString}repeatedDouble=${repeatedDouble},")
            appendLine("${nextIndentString}repeatedBool=${repeatedBool},")
            appendLine("${nextIndentString}repeatedString=${repeatedString},")
            appendLine("${nextIndentString}repeatedBytes=${repeatedBytes},")
            appendLine("${nextIndentString}repeatedNestedMessage=${repeatedNestedMessage},")
            appendLine("${nextIndentString}repeatedForeignMessage=${repeatedForeignMessage},")
            appendLine("${nextIndentString}repeatedNestedEnum=${repeatedNestedEnum},")
            appendLine("${nextIndentString}repeatedForeignEnum=${repeatedForeignEnum},")
            appendLine("${nextIndentString}repeatedStringPiece=${repeatedStringPiece},")
            appendLine("${nextIndentString}repeatedCord=${repeatedCord},")
            appendLine("${nextIndentString}packedInt32=${packedInt32},")
            appendLine("${nextIndentString}packedInt64=${packedInt64},")
            appendLine("${nextIndentString}packedUint32=${packedUint32},")
            appendLine("${nextIndentString}packedUint64=${packedUint64},")
            appendLine("${nextIndentString}packedSint32=${packedSint32},")
            appendLine("${nextIndentString}packedSint64=${packedSint64},")
            appendLine("${nextIndentString}packedFixed32=${packedFixed32},")
            appendLine("${nextIndentString}packedFixed64=${packedFixed64},")
            appendLine("${nextIndentString}packedSfixed32=${packedSfixed32},")
            appendLine("${nextIndentString}packedSfixed64=${packedSfixed64},")
            appendLine("${nextIndentString}packedFloat=${packedFloat},")
            appendLine("${nextIndentString}packedDouble=${packedDouble},")
            appendLine("${nextIndentString}packedBool=${packedBool},")
            appendLine("${nextIndentString}packedNestedEnum=${packedNestedEnum},")
            appendLine("${nextIndentString}unpackedInt32=${unpackedInt32},")
            appendLine("${nextIndentString}unpackedInt64=${unpackedInt64},")
            appendLine("${nextIndentString}unpackedUint32=${unpackedUint32},")
            appendLine("${nextIndentString}unpackedUint64=${unpackedUint64},")
            appendLine("${nextIndentString}unpackedSint32=${unpackedSint32},")
            appendLine("${nextIndentString}unpackedSint64=${unpackedSint64},")
            appendLine("${nextIndentString}unpackedFixed32=${unpackedFixed32},")
            appendLine("${nextIndentString}unpackedFixed64=${unpackedFixed64},")
            appendLine("${nextIndentString}unpackedSfixed32=${unpackedSfixed32},")
            appendLine("${nextIndentString}unpackedSfixed64=${unpackedSfixed64},")
            appendLine("${nextIndentString}unpackedFloat=${unpackedFloat},")
            appendLine("${nextIndentString}unpackedDouble=${unpackedDouble},")
            appendLine("${nextIndentString}unpackedBool=${unpackedBool},")
            appendLine("${nextIndentString}unpackedNestedEnum=${unpackedNestedEnum},")
            appendLine("${nextIndentString}mapInt32Int32=${mapInt32Int32},")
            appendLine("${nextIndentString}mapInt64Int64=${mapInt64Int64},")
            appendLine("${nextIndentString}mapUint32Uint32=${mapUint32Uint32},")
            appendLine("${nextIndentString}mapUint64Uint64=${mapUint64Uint64},")
            appendLine("${nextIndentString}mapSint32Sint32=${mapSint32Sint32},")
            appendLine("${nextIndentString}mapSint64Sint64=${mapSint64Sint64},")
            appendLine("${nextIndentString}mapFixed32Fixed32=${mapFixed32Fixed32},")
            appendLine("${nextIndentString}mapFixed64Fixed64=${mapFixed64Fixed64},")
            appendLine("${nextIndentString}mapSfixed32Sfixed32=${mapSfixed32Sfixed32},")
            appendLine("${nextIndentString}mapSfixed64Sfixed64=${mapSfixed64Sfixed64},")
            appendLine("${nextIndentString}mapInt32Bool=${mapInt32Bool},")
            appendLine("${nextIndentString}mapInt32Float=${mapInt32Float},")
            appendLine("${nextIndentString}mapInt32Double=${mapInt32Double},")
            appendLine("${nextIndentString}mapInt32NestedMessage=${mapInt32NestedMessage},")
            appendLine("${nextIndentString}mapBoolBool=${mapBoolBool},")
            appendLine("${nextIndentString}mapStringString=${mapStringString},")
            appendLine("${nextIndentString}mapStringBytes=${mapStringBytes},")
            appendLine("${nextIndentString}mapStringNestedMessage=${mapStringNestedMessage},")
            appendLine("${nextIndentString}mapStringForeignMessage=${mapStringForeignMessage},")
            appendLine("${nextIndentString}mapStringNestedEnum=${mapStringNestedEnum},")
            appendLine("${nextIndentString}mapStringForeignEnum=${mapStringForeignEnum},")
            if (presenceMask[22]) { 
                appendLine("${nextIndentString}data=${data.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}data=<unset>,")
            }

            if (presenceMask[23]) { 
                appendLine("${nextIndentString}multiwordgroupfield=${multiwordgroupfield.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}multiwordgroupfield=<unset>,")
            }

            if (presenceMask[24]) { 
                appendLine("${nextIndentString}defaultInt32=${defaultInt32},")
            } else { 
                appendLine("${nextIndentString}defaultInt32=<unset>,")
            }

            if (presenceMask[25]) { 
                appendLine("${nextIndentString}defaultInt64=${defaultInt64},")
            } else { 
                appendLine("${nextIndentString}defaultInt64=<unset>,")
            }

            if (presenceMask[26]) { 
                appendLine("${nextIndentString}defaultUint32=${defaultUint32},")
            } else { 
                appendLine("${nextIndentString}defaultUint32=<unset>,")
            }

            if (presenceMask[27]) { 
                appendLine("${nextIndentString}defaultUint64=${defaultUint64},")
            } else { 
                appendLine("${nextIndentString}defaultUint64=<unset>,")
            }

            if (presenceMask[28]) { 
                appendLine("${nextIndentString}defaultSint32=${defaultSint32},")
            } else { 
                appendLine("${nextIndentString}defaultSint32=<unset>,")
            }

            if (presenceMask[29]) { 
                appendLine("${nextIndentString}defaultSint64=${defaultSint64},")
            } else { 
                appendLine("${nextIndentString}defaultSint64=<unset>,")
            }

            if (presenceMask[30]) { 
                appendLine("${nextIndentString}defaultFixed32=${defaultFixed32},")
            } else { 
                appendLine("${nextIndentString}defaultFixed32=<unset>,")
            }

            if (presenceMask[31]) { 
                appendLine("${nextIndentString}defaultFixed64=${defaultFixed64},")
            } else { 
                appendLine("${nextIndentString}defaultFixed64=<unset>,")
            }

            if (presenceMask[32]) { 
                appendLine("${nextIndentString}defaultSfixed32=${defaultSfixed32},")
            } else { 
                appendLine("${nextIndentString}defaultSfixed32=<unset>,")
            }

            if (presenceMask[33]) { 
                appendLine("${nextIndentString}defaultSfixed64=${defaultSfixed64},")
            } else { 
                appendLine("${nextIndentString}defaultSfixed64=<unset>,")
            }

            if (presenceMask[34]) { 
                appendLine("${nextIndentString}defaultFloat=${defaultFloat},")
            } else { 
                appendLine("${nextIndentString}defaultFloat=<unset>,")
            }

            if (presenceMask[35]) { 
                appendLine("${nextIndentString}defaultDouble=${defaultDouble},")
            } else { 
                appendLine("${nextIndentString}defaultDouble=<unset>,")
            }

            if (presenceMask[36]) { 
                appendLine("${nextIndentString}defaultBool=${defaultBool},")
            } else { 
                appendLine("${nextIndentString}defaultBool=<unset>,")
            }

            if (presenceMask[37]) { 
                appendLine("${nextIndentString}defaultString=${defaultString},")
            } else { 
                appendLine("${nextIndentString}defaultString=<unset>,")
            }

            if (presenceMask[38]) { 
                appendLine("${nextIndentString}defaultBytes=${defaultBytes.contentToString()},")
            } else { 
                appendLine("${nextIndentString}defaultBytes=<unset>,")
            }

            if (presenceMask[39]) { 
                appendLine("${nextIndentString}fieldname1=${fieldname1},")
            } else { 
                appendLine("${nextIndentString}fieldname1=<unset>,")
            }

            if (presenceMask[40]) { 
                appendLine("${nextIndentString}fieldName2=${fieldName2},")
            } else { 
                appendLine("${nextIndentString}fieldName2=<unset>,")
            }

            if (presenceMask[41]) { 
                appendLine("${nextIndentString}FieldName3=${FieldName3},")
            } else { 
                appendLine("${nextIndentString}FieldName3=<unset>,")
            }

            if (presenceMask[42]) { 
                appendLine("${nextIndentString}field_Name4_=${field_Name4_},")
            } else { 
                appendLine("${nextIndentString}field_Name4_=<unset>,")
            }

            if (presenceMask[43]) { 
                appendLine("${nextIndentString}field0name5=${field0name5},")
            } else { 
                appendLine("${nextIndentString}field0name5=<unset>,")
            }

            if (presenceMask[44]) { 
                appendLine("${nextIndentString}field_0Name6=${field_0Name6},")
            } else { 
                appendLine("${nextIndentString}field_0Name6=<unset>,")
            }

            if (presenceMask[45]) { 
                appendLine("${nextIndentString}fieldName7=${fieldName7},")
            } else { 
                appendLine("${nextIndentString}fieldName7=<unset>,")
            }

            if (presenceMask[46]) { 
                appendLine("${nextIndentString}FieldName8=${FieldName8},")
            } else { 
                appendLine("${nextIndentString}FieldName8=<unset>,")
            }

            if (presenceMask[47]) { 
                appendLine("${nextIndentString}field_Name9=${field_Name9},")
            } else { 
                appendLine("${nextIndentString}field_Name9=<unset>,")
            }

            if (presenceMask[48]) { 
                appendLine("${nextIndentString}Field_Name10=${Field_Name10},")
            } else { 
                appendLine("${nextIndentString}Field_Name10=<unset>,")
            }

            if (presenceMask[49]) { 
                appendLine("${nextIndentString}FIELD_NAME11=${FIELD_NAME11},")
            } else { 
                appendLine("${nextIndentString}FIELD_NAME11=<unset>,")
            }

            if (presenceMask[50]) { 
                appendLine("${nextIndentString}FIELDName12=${FIELDName12},")
            } else { 
                appendLine("${nextIndentString}FIELDName12=<unset>,")
            }

            if (presenceMask[51]) { 
                appendLine("${nextIndentString}_FieldName13=${_FieldName13},")
            } else { 
                appendLine("${nextIndentString}_FieldName13=<unset>,")
            }

            if (presenceMask[52]) { 
                appendLine("${nextIndentString}__FieldName14=${__FieldName14},")
            } else { 
                appendLine("${nextIndentString}__FieldName14=<unset>,")
            }

            if (presenceMask[53]) { 
                appendLine("${nextIndentString}field_Name15=${field_Name15},")
            } else { 
                appendLine("${nextIndentString}field_Name15=<unset>,")
            }

            if (presenceMask[54]) { 
                appendLine("${nextIndentString}field__Name16=${field__Name16},")
            } else { 
                appendLine("${nextIndentString}field__Name16=<unset>,")
            }

            if (presenceMask[55]) { 
                appendLine("${nextIndentString}fieldName17__=${fieldName17__},")
            } else { 
                appendLine("${nextIndentString}fieldName17__=<unset>,")
            }

            if (presenceMask[56]) { 
                appendLine("${nextIndentString}FieldName18__=${FieldName18__},")
            } else { 
                appendLine("${nextIndentString}FieldName18__=<unset>,")
            }

            if (presenceMask[57]) { 
                appendLine("${nextIndentString}messageSetCorrect=${messageSetCorrect.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}messageSetCorrect=<unset>,")
            }

            appendLine("${nextIndentString}oneofField=${oneofField},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal()
        if (presenceMask[0]) { 
            copy.optionalInt32 = this.optionalInt32
        }

        if (presenceMask[1]) { 
            copy.optionalInt64 = this.optionalInt64
        }

        if (presenceMask[2]) { 
            copy.optionalUint32 = this.optionalUint32
        }

        if (presenceMask[3]) { 
            copy.optionalUint64 = this.optionalUint64
        }

        if (presenceMask[4]) { 
            copy.optionalSint32 = this.optionalSint32
        }

        if (presenceMask[5]) { 
            copy.optionalSint64 = this.optionalSint64
        }

        if (presenceMask[6]) { 
            copy.optionalFixed32 = this.optionalFixed32
        }

        if (presenceMask[7]) { 
            copy.optionalFixed64 = this.optionalFixed64
        }

        if (presenceMask[8]) { 
            copy.optionalSfixed32 = this.optionalSfixed32
        }

        if (presenceMask[9]) { 
            copy.optionalSfixed64 = this.optionalSfixed64
        }

        if (presenceMask[10]) { 
            copy.optionalFloat = this.optionalFloat
        }

        if (presenceMask[11]) { 
            copy.optionalDouble = this.optionalDouble
        }

        if (presenceMask[12]) { 
            copy.optionalBool = this.optionalBool
        }

        if (presenceMask[13]) { 
            copy.optionalString = this.optionalString
        }

        if (presenceMask[14]) { 
            copy.optionalBytes = this.optionalBytes?.copyOf()
        }

        if (presenceMask[15]) { 
            copy.optionalNestedMessage = this.optionalNestedMessage.copy()
        }

        if (presenceMask[16]) { 
            copy.optionalForeignMessage = this.optionalForeignMessage.copy()
        }

        if (presenceMask[17]) { 
            copy.optionalNestedEnum = this.optionalNestedEnum
        }

        if (presenceMask[18]) { 
            copy.optionalForeignEnum = this.optionalForeignEnum
        }

        if (presenceMask[19]) { 
            copy.optionalStringPiece = this.optionalStringPiece
        }

        if (presenceMask[20]) { 
            copy.optionalCord = this.optionalCord
        }

        if (presenceMask[21]) { 
            copy.recursiveMessage = this.recursiveMessage.copy()
        }

        copy.repeatedInt32 = this.repeatedInt32.map { it }
        copy.repeatedInt64 = this.repeatedInt64.map { it }
        copy.repeatedUint32 = this.repeatedUint32.map { it }
        copy.repeatedUint64 = this.repeatedUint64.map { it }
        copy.repeatedSint32 = this.repeatedSint32.map { it }
        copy.repeatedSint64 = this.repeatedSint64.map { it }
        copy.repeatedFixed32 = this.repeatedFixed32.map { it }
        copy.repeatedFixed64 = this.repeatedFixed64.map { it }
        copy.repeatedSfixed32 = this.repeatedSfixed32.map { it }
        copy.repeatedSfixed64 = this.repeatedSfixed64.map { it }
        copy.repeatedFloat = this.repeatedFloat.map { it }
        copy.repeatedDouble = this.repeatedDouble.map { it }
        copy.repeatedBool = this.repeatedBool.map { it }
        copy.repeatedString = this.repeatedString.map { it }
        copy.repeatedBytes = this.repeatedBytes.map { it.copyOf() }
        copy.repeatedNestedMessage = this.repeatedNestedMessage.map { it.copy() }
        copy.repeatedForeignMessage = this.repeatedForeignMessage.map { it.copy() }
        copy.repeatedNestedEnum = this.repeatedNestedEnum.map { it }
        copy.repeatedForeignEnum = this.repeatedForeignEnum.map { it }
        copy.repeatedStringPiece = this.repeatedStringPiece.map { it }
        copy.repeatedCord = this.repeatedCord.map { it }
        copy.packedInt32 = this.packedInt32.map { it }
        copy.packedInt64 = this.packedInt64.map { it }
        copy.packedUint32 = this.packedUint32.map { it }
        copy.packedUint64 = this.packedUint64.map { it }
        copy.packedSint32 = this.packedSint32.map { it }
        copy.packedSint64 = this.packedSint64.map { it }
        copy.packedFixed32 = this.packedFixed32.map { it }
        copy.packedFixed64 = this.packedFixed64.map { it }
        copy.packedSfixed32 = this.packedSfixed32.map { it }
        copy.packedSfixed64 = this.packedSfixed64.map { it }
        copy.packedFloat = this.packedFloat.map { it }
        copy.packedDouble = this.packedDouble.map { it }
        copy.packedBool = this.packedBool.map { it }
        copy.packedNestedEnum = this.packedNestedEnum.map { it }
        copy.unpackedInt32 = this.unpackedInt32.map { it }
        copy.unpackedInt64 = this.unpackedInt64.map { it }
        copy.unpackedUint32 = this.unpackedUint32.map { it }
        copy.unpackedUint64 = this.unpackedUint64.map { it }
        copy.unpackedSint32 = this.unpackedSint32.map { it }
        copy.unpackedSint64 = this.unpackedSint64.map { it }
        copy.unpackedFixed32 = this.unpackedFixed32.map { it }
        copy.unpackedFixed64 = this.unpackedFixed64.map { it }
        copy.unpackedSfixed32 = this.unpackedSfixed32.map { it }
        copy.unpackedSfixed64 = this.unpackedSfixed64.map { it }
        copy.unpackedFloat = this.unpackedFloat.map { it }
        copy.unpackedDouble = this.unpackedDouble.map { it }
        copy.unpackedBool = this.unpackedBool.map { it }
        copy.unpackedNestedEnum = this.unpackedNestedEnum.map { it }
        copy.mapInt32Int32 = this.mapInt32Int32.mapValues { it.value }
        copy.mapInt64Int64 = this.mapInt64Int64.mapValues { it.value }
        copy.mapUint32Uint32 = this.mapUint32Uint32.mapValues { it.value }
        copy.mapUint64Uint64 = this.mapUint64Uint64.mapValues { it.value }
        copy.mapSint32Sint32 = this.mapSint32Sint32.mapValues { it.value }
        copy.mapSint64Sint64 = this.mapSint64Sint64.mapValues { it.value }
        copy.mapFixed32Fixed32 = this.mapFixed32Fixed32.mapValues { it.value }
        copy.mapFixed64Fixed64 = this.mapFixed64Fixed64.mapValues { it.value }
        copy.mapSfixed32Sfixed32 = this.mapSfixed32Sfixed32.mapValues { it.value }
        copy.mapSfixed64Sfixed64 = this.mapSfixed64Sfixed64.mapValues { it.value }
        copy.mapInt32Bool = this.mapInt32Bool.mapValues { it.value }
        copy.mapInt32Float = this.mapInt32Float.mapValues { it.value }
        copy.mapInt32Double = this.mapInt32Double.mapValues { it.value }
        copy.mapInt32NestedMessage = this.mapInt32NestedMessage.mapValues { it.value.copy() }
        copy.mapBoolBool = this.mapBoolBool.mapValues { it.value }
        copy.mapStringString = this.mapStringString.mapValues { it.value }
        copy.mapStringBytes = this.mapStringBytes.mapValues { it.value.copyOf() }
        copy.mapStringNestedMessage = this.mapStringNestedMessage.mapValues { it.value.copy() }
        copy.mapStringForeignMessage = this.mapStringForeignMessage.mapValues { it.value.copy() }
        copy.mapStringNestedEnum = this.mapStringNestedEnum.mapValues { it.value }
        copy.mapStringForeignEnum = this.mapStringForeignEnum.mapValues { it.value }
        if (presenceMask[22]) { 
            copy.data = this.data.copy()
        }

        if (presenceMask[23]) { 
            copy.multiwordgroupfield = this.multiwordgroupfield.copy()
        }

        if (presenceMask[24]) { 
            copy.defaultInt32 = this.defaultInt32
        }

        if (presenceMask[25]) { 
            copy.defaultInt64 = this.defaultInt64
        }

        if (presenceMask[26]) { 
            copy.defaultUint32 = this.defaultUint32
        }

        if (presenceMask[27]) { 
            copy.defaultUint64 = this.defaultUint64
        }

        if (presenceMask[28]) { 
            copy.defaultSint32 = this.defaultSint32
        }

        if (presenceMask[29]) { 
            copy.defaultSint64 = this.defaultSint64
        }

        if (presenceMask[30]) { 
            copy.defaultFixed32 = this.defaultFixed32
        }

        if (presenceMask[31]) { 
            copy.defaultFixed64 = this.defaultFixed64
        }

        if (presenceMask[32]) { 
            copy.defaultSfixed32 = this.defaultSfixed32
        }

        if (presenceMask[33]) { 
            copy.defaultSfixed64 = this.defaultSfixed64
        }

        if (presenceMask[34]) { 
            copy.defaultFloat = this.defaultFloat
        }

        if (presenceMask[35]) { 
            copy.defaultDouble = this.defaultDouble
        }

        if (presenceMask[36]) { 
            copy.defaultBool = this.defaultBool
        }

        if (presenceMask[37]) { 
            copy.defaultString = this.defaultString
        }

        if (presenceMask[38]) { 
            copy.defaultBytes = this.defaultBytes.copyOf()
        }

        if (presenceMask[39]) { 
            copy.fieldname1 = this.fieldname1
        }

        if (presenceMask[40]) { 
            copy.fieldName2 = this.fieldName2
        }

        if (presenceMask[41]) { 
            copy.FieldName3 = this.FieldName3
        }

        if (presenceMask[42]) { 
            copy.field_Name4_ = this.field_Name4_
        }

        if (presenceMask[43]) { 
            copy.field0name5 = this.field0name5
        }

        if (presenceMask[44]) { 
            copy.field_0Name6 = this.field_0Name6
        }

        if (presenceMask[45]) { 
            copy.fieldName7 = this.fieldName7
        }

        if (presenceMask[46]) { 
            copy.FieldName8 = this.FieldName8
        }

        if (presenceMask[47]) { 
            copy.field_Name9 = this.field_Name9
        }

        if (presenceMask[48]) { 
            copy.Field_Name10 = this.Field_Name10
        }

        if (presenceMask[49]) { 
            copy.FIELD_NAME11 = this.FIELD_NAME11
        }

        if (presenceMask[50]) { 
            copy.FIELDName12 = this.FIELDName12
        }

        if (presenceMask[51]) { 
            copy._FieldName13 = this._FieldName13
        }

        if (presenceMask[52]) { 
            copy.__FieldName14 = this.__FieldName14
        }

        if (presenceMask[53]) { 
            copy.field_Name15 = this.field_Name15
        }

        if (presenceMask[54]) { 
            copy.field__Name16 = this.field__Name16
        }

        if (presenceMask[55]) { 
            copy.fieldName17__ = this.fieldName17__
        }

        if (presenceMask[56]) { 
            copy.FieldName18__ = this.FieldName18__
        }

        if (presenceMask[57]) { 
            copy.messageSetCorrect = this.messageSetCorrect.copy()
        }

        copy.oneofField = this.oneofField?.oneOfCopy()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.oneOfCopy(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField { 
        return when (this) { 
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint32 -> { 
                this
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofNestedMessage -> { 
                com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofNestedMessage(this.value.copy())
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofString -> { 
                this
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBytes -> { 
                com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBytes(this.value.copyOf())
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBool -> { 
                this
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint64 -> { 
                this
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofFloat -> { 
                this
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofDouble -> { 
                this
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofEnum -> { 
                this
            }
        }
    }

    class NestedMessageInternal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val a: Int = 0
            const val corecursive: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var a: Int? by MsgFieldDelegate(PresenceIndices.a) { null }
        override var corecursive: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2 by MsgFieldDelegate(PresenceIndices.corecursive) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.NestedMessage = object : com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.NestedMessage { 
            override val hasA: kotlin.Boolean get() = presenceMask[0]

            override val hasCorecursive: kotlin.Boolean get() = presenceMask[1]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) (a?.hashCode() ?: 0) else 0
            result = 31 * result + if (presenceMask[1]) corecursive.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as NestedMessageInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && a != other.a) return false
            if (presenceMask[1] && corecursive != other.corecursive) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}a=${a},")
                } else { 
                    appendLine("${nextIndentString}a=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}corecursive=${corecursive.asInternal().asString(indent = indent + 4)},")
                } else { 
                    appendLine("${nextIndentString}corecursive=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal()
            if (presenceMask[0]) { 
                copy.a = this.a
            }

            if (presenceMask[1]) { 
                copy.corecursive = this.corecursive.copy()
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt32Int32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Int by MsgFieldDelegate(PresenceIndices.value) { 0 }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32Int32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapInt32Int32Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt64Int64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Long by MsgFieldDelegate(PresenceIndices.key) { 0L }
        var value: Long by MsgFieldDelegate(PresenceIndices.value) { 0L }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt64Int64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapInt64Int64Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapUint32Uint32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: UInt by MsgFieldDelegate(PresenceIndices.key) { 0u }
        var value: UInt by MsgFieldDelegate(PresenceIndices.value) { 0u }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapUint32Uint32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapUint32Uint32Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapUint64Uint64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: ULong by MsgFieldDelegate(PresenceIndices.key) { 0uL }
        var value: ULong by MsgFieldDelegate(PresenceIndices.value) { 0uL }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapUint64Uint64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapUint64Uint64Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapSint32Sint32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Int by MsgFieldDelegate(PresenceIndices.value) { 0 }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSint32Sint32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapSint32Sint32Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapSint64Sint64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Long by MsgFieldDelegate(PresenceIndices.key) { 0L }
        var value: Long by MsgFieldDelegate(PresenceIndices.value) { 0L }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSint64Sint64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapSint64Sint64Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapFixed32Fixed32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: UInt by MsgFieldDelegate(PresenceIndices.key) { 0u }
        var value: UInt by MsgFieldDelegate(PresenceIndices.value) { 0u }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapFixed32Fixed32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapFixed32Fixed32Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapFixed64Fixed64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: ULong by MsgFieldDelegate(PresenceIndices.key) { 0uL }
        var value: ULong by MsgFieldDelegate(PresenceIndices.value) { 0uL }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapFixed64Fixed64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapFixed64Fixed64Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapSfixed32Sfixed32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Int by MsgFieldDelegate(PresenceIndices.value) { 0 }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSfixed32Sfixed32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapSfixed32Sfixed32Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapSfixed64Sfixed64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Long by MsgFieldDelegate(PresenceIndices.key) { 0L }
        var value: Long by MsgFieldDelegate(PresenceIndices.value) { 0L }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSfixed64Sfixed64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapSfixed64Sfixed64Entry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt32BoolEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Boolean by MsgFieldDelegate(PresenceIndices.value) { false }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32BoolEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapInt32BoolEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt32FloatEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Float by MsgFieldDelegate(PresenceIndices.value) { 0.0f }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32FloatEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapInt32FloatEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt32DoubleEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Double by MsgFieldDelegate(PresenceIndices.value) { 0.0 }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32DoubleEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapInt32DoubleEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt32NestedMessageEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal() }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32NestedMessageEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapInt32NestedMessageEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value.asInternal().asString(indent = indent + 4)},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapBoolBoolEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Boolean by MsgFieldDelegate(PresenceIndices.key) { false }
        var value: Boolean by MsgFieldDelegate(PresenceIndices.value) { false }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapBoolBoolEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapBoolBoolEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringStringEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: String by MsgFieldDelegate(PresenceIndices.value) { "" }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringStringEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapStringStringEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringBytesEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: ByteArray by MsgFieldDelegate(PresenceIndices.value) { byteArrayOf() }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.contentHashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringBytesEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && !value.contentEquals(other.value)) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapStringBytesEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value.contentToString()},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringNestedMessageEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal() }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringNestedMessageEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapStringNestedMessageEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value.asInternal().asString(indent = indent + 4)},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringForeignMessageEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal() }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringForeignMessageEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapStringForeignMessageEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value.asInternal().asString(indent = indent + 4)},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringNestedEnumEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.FOO }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringNestedEnumEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapStringNestedEnumEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringForeignEnumEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val key: Int = 0
            const val value: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2 by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.FOREIGN_FOO }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringForeignEnumEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && key != other.key) return false
            if (presenceMask[1] && value != other.value) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MapStringForeignEnumEntry(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}key=${key},")
                } else { 
                    appendLine("${nextIndentString}key=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}value=${value},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class DataInternal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val groupInt32: Int = 0
            const val groupUint32: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var groupInt32: Int? by MsgFieldDelegate(PresenceIndices.groupInt32) { null }
        override var groupUint32: UInt? by MsgFieldDelegate(PresenceIndices.groupUint32) { null }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.Data = object : com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.Data { 
            override val hasGroupInt32: kotlin.Boolean get() = presenceMask[0]

            override val hasGroupUint32: kotlin.Boolean get() = presenceMask[1]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) (groupInt32?.hashCode() ?: 0) else 0
            result = 31 * result + if (presenceMask[1]) (groupUint32?.hashCode() ?: 0) else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as DataInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && groupInt32 != other.groupInt32) return false
            if (presenceMask[1] && groupUint32 != other.groupUint32) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}groupInt32=${groupInt32},")
                } else { 
                    appendLine("${nextIndentString}groupInt32=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}groupUint32=${groupUint32},")
                } else { 
                    appendLine("${nextIndentString}groupUint32=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal()
            if (presenceMask[0]) { 
                copy.groupInt32 = this.groupInt32
            }

            if (presenceMask[1]) { 
                copy.groupUint32 = this.groupUint32
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MultiWordGroupFieldInternal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val groupInt32: Int = 0
            const val groupUint32: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var groupInt32: Int? by MsgFieldDelegate(PresenceIndices.groupInt32) { null }
        override var groupUint32: UInt? by MsgFieldDelegate(PresenceIndices.groupUint32) { null }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.MultiWordGroupField = object : com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.MultiWordGroupField { 
            override val hasGroupInt32: kotlin.Boolean get() = presenceMask[0]

            override val hasGroupUint32: kotlin.Boolean get() = presenceMask[1]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) (groupInt32?.hashCode() ?: 0) else 0
            result = 31 * result + if (presenceMask[1]) (groupUint32?.hashCode() ?: 0) else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MultiWordGroupFieldInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && groupInt32 != other.groupInt32) return false
            if (presenceMask[1] && groupUint32 != other.groupUint32) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}groupInt32=${groupInt32},")
                } else { 
                    appendLine("${nextIndentString}groupInt32=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}groupUint32=${groupUint32},")
                } else { 
                    appendLine("${nextIndentString}groupUint32=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal()
            if (presenceMask[0]) { 
                copy.groupInt32 = this.groupInt32
            }

            if (presenceMask[1]) { 
                copy.groupUint32 = this.groupUint32
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MessageSetCorrectInternal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return this::class.hashCode()
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectInternal
            other.checkRequiredFields()
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect(")
                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MessageSetCorrectExtension1Internal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            const val str: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var str: String? by MsgFieldDelegate(PresenceIndices.str) { null }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.MessageSetCorrectExtension1 = object : com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.MessageSetCorrectExtension1 { 
            override val hasStr: kotlin.Boolean get() = presenceMask[0]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return if (presenceMask[0]) (str?.hashCode() ?: 0) else 0
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectExtension1Internal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && str != other.str) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}str=${str},")
                } else { 
                    appendLine("${nextIndentString}str=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal()
            if (presenceMask[0]) { 
                copy.str = this.str
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MessageSetCorrectExtension2Internal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            const val i: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var i: Int? by MsgFieldDelegate(PresenceIndices.i) { null }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.MessageSetCorrectExtension2 = object : com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Presence.MessageSetCorrectExtension2 { 
            override val hasI: kotlin.Boolean get() = presenceMask[0]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return if (presenceMask[0]) (i?.hashCode() ?: 0) else 0
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectExtension2Internal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && i != other.i) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}i=${i},")
                } else { 
                    appendLine("${nextIndentString}i=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal()
            if (presenceMask[0]) { 
                copy.i = this.i
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class ExtensionWithOneofInternal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var oneofField: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField? = null

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return (oneofField?.oneOfHashCode() ?: 0)
        }

        fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.oneOfHashCode(): kotlin.Int { 
            val offset = when (this) { 
                is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.A -> 0
                is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.B -> 1
            }

            return hashCode() + offset
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as ExtensionWithOneofInternal
            other.checkRequiredFields()
            if (oneofField != other.oneofField) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof(")
                appendLine("${nextIndentString}oneofField=${oneofField},")
                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal()
            copy.oneofField = this.oneofField?.oneOfCopy()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.oneOfCopy(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField { 
            return this
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class ForeignMessageProto2Internal: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        const val c: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var c: Int? by MsgFieldDelegate(PresenceIndices.c) { null }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    val _presence: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Presence = object : com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Presence { 
        override val hasC: kotlin.Boolean get() = presenceMask[0]
    }

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return if (presenceMask[0]) (c?.hashCode() ?: 0) else 0
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ForeignMessageProto2Internal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && c != other.c) return false
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2(")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}c=${c},")
            } else { 
                appendLine("${nextIndentString}c=<unset>,")
            }

            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal()
        if (presenceMask[0]) { 
            copy.c = this.c
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class GroupFieldInternal: com.google.protobuf_test_messages.editions.proto2.GroupField, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
    private object PresenceIndices { 
        const val groupInt32: Int = 0
        const val groupUint32: Int = 1
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var groupInt32: Int? by MsgFieldDelegate(PresenceIndices.groupInt32) { null }
    override var groupUint32: UInt? by MsgFieldDelegate(PresenceIndices.groupUint32) { null }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    val _presence: com.google.protobuf_test_messages.editions.proto2.GroupFieldPresence = object : com.google.protobuf_test_messages.editions.proto2.GroupFieldPresence { 
        override val hasGroupInt32: kotlin.Boolean get() = presenceMask[0]

        override val hasGroupUint32: kotlin.Boolean get() = presenceMask[1]
    }

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        var result = if (presenceMask[0]) (groupInt32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[1]) (groupUint32?.hashCode() ?: 0) else 0
        return result
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as GroupFieldInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && groupInt32 != other.groupInt32) return false
        if (presenceMask[1] && groupUint32 != other.groupUint32) return false
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.GroupField(")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}groupInt32=${groupInt32},")
            } else { 
                appendLine("${nextIndentString}groupInt32=<unset>,")
            }

            if (presenceMask[1]) { 
                appendLine("${nextIndentString}groupUint32=${groupUint32},")
            } else { 
                appendLine("${nextIndentString}groupUint32=<unset>,")
            }

            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal()
        if (presenceMask[0]) { 
            copy.groupInt32 = this.groupInt32
        }

        if (presenceMask[1]) { 
            copy.groupUint32 = this.groupUint32
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.GroupField> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.GroupField, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.GroupField { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class UnknownToTestAllTypesInternal: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 5) { 
    private object PresenceIndices { 
        const val optionalInt32: Int = 0
        const val optionalString: Int = 1
        const val nestedMessage: Int = 2
        const val optionalgroup: Int = 3
        const val optionalBool: Int = 4
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var optionalInt32: Int? by MsgFieldDelegate(PresenceIndices.optionalInt32) { null }
    override var optionalString: String? by MsgFieldDelegate(PresenceIndices.optionalString) { null }
    override var nestedMessage: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 by MsgFieldDelegate(PresenceIndices.nestedMessage) { com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal() }
    override var optionalgroup: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup by MsgFieldDelegate(PresenceIndices.optionalgroup) { com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal() }
    override var optionalBool: Boolean? by MsgFieldDelegate(PresenceIndices.optionalBool) { null }
    override var repeatedInt32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    val _presence: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesPresence = object : com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesPresence { 
        override val hasOptionalInt32: kotlin.Boolean get() = presenceMask[0]

        override val hasOptionalString: kotlin.Boolean get() = presenceMask[1]

        override val hasNestedMessage: kotlin.Boolean get() = presenceMask[2]

        override val hasOptionalgroup: kotlin.Boolean get() = presenceMask[3]

        override val hasOptionalBool: kotlin.Boolean get() = presenceMask[4]
    }

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        var result = if (presenceMask[0]) (optionalInt32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[1]) (optionalString?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[2]) nestedMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[3]) optionalgroup.hashCode() else 0
        result = 31 * result + if (presenceMask[4]) (optionalBool?.hashCode() ?: 0) else 0
        result = 31 * result + repeatedInt32.hashCode()
        return result
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UnknownToTestAllTypesInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && optionalInt32 != other.optionalInt32) return false
        if (presenceMask[1] && optionalString != other.optionalString) return false
        if (presenceMask[2] && nestedMessage != other.nestedMessage) return false
        if (presenceMask[3] && optionalgroup != other.optionalgroup) return false
        if (presenceMask[4] && optionalBool != other.optionalBool) return false
        if (repeatedInt32 != other.repeatedInt32) return false
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes(")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}optionalInt32=${optionalInt32},")
            } else { 
                appendLine("${nextIndentString}optionalInt32=<unset>,")
            }

            if (presenceMask[1]) { 
                appendLine("${nextIndentString}optionalString=${optionalString},")
            } else { 
                appendLine("${nextIndentString}optionalString=<unset>,")
            }

            if (presenceMask[2]) { 
                appendLine("${nextIndentString}nestedMessage=${nestedMessage.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}nestedMessage=<unset>,")
            }

            if (presenceMask[3]) { 
                appendLine("${nextIndentString}optionalgroup=${optionalgroup.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}optionalgroup=<unset>,")
            }

            if (presenceMask[4]) { 
                appendLine("${nextIndentString}optionalBool=${optionalBool},")
            } else { 
                appendLine("${nextIndentString}optionalBool=<unset>,")
            }

            appendLine("${nextIndentString}repeatedInt32=${repeatedInt32},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal()
        if (presenceMask[0]) { 
            copy.optionalInt32 = this.optionalInt32
        }

        if (presenceMask[1]) { 
            copy.optionalString = this.optionalString
        }

        if (presenceMask[2]) { 
            copy.nestedMessage = this.nestedMessage.copy()
        }

        if (presenceMask[3]) { 
            copy.optionalgroup = this.optionalgroup.copy()
        }

        if (presenceMask[4]) { 
            copy.optionalBool = this.optionalBool
        }

        copy.repeatedInt32 = this.repeatedInt32.map { it }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    class OptionalGroupInternal: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            const val a: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var a: Int? by MsgFieldDelegate(PresenceIndices.a) { null }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesPresence.OptionalGroup = object : com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesPresence.OptionalGroup { 
            override val hasA: kotlin.Boolean get() = presenceMask[0]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return if (presenceMask[0]) (a?.hashCode() ?: 0) else 0
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as OptionalGroupInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && a != other.a) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}a=${a},")
                } else { 
                    appendLine("${nextIndentString}a=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal()
            if (presenceMask[0]) { 
                copy.a = this.a
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class NullHypothesisProto2Internal: com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return this::class.hashCode()
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as NullHypothesisProto2Internal
        other.checkRequiredFields()
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2(")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class EnumOnlyProto2Internal: com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return this::class.hashCode()
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumOnlyProto2Internal
        other.checkRequiredFields()
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2(")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class OneStringProto2Internal: com.google.protobuf_test_messages.editions.proto2.OneStringProto2, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        const val data: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var data: String? by MsgFieldDelegate(PresenceIndices.data) { null }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    val _presence: com.google.protobuf_test_messages.editions.proto2.OneStringProto2Presence = object : com.google.protobuf_test_messages.editions.proto2.OneStringProto2Presence { 
        override val hasData: kotlin.Boolean get() = presenceMask[0]
    }

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return if (presenceMask[0]) (data?.hashCode() ?: 0) else 0
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as OneStringProto2Internal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && data != other.data) return false
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.OneStringProto2(")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}data=${data},")
            } else { 
                appendLine("${nextIndentString}data=<unset>,")
            }

            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal()
        if (presenceMask[0]) { 
            copy.data = this.data
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.OneStringProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.OneStringProto2, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.OneStringProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class ProtoWithKeywordsInternal: com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
    private object PresenceIndices { 
        const val inline: Int = 0
        const val concept: Int = 1
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var inline: Int? by MsgFieldDelegate(PresenceIndices.inline) { null }
    override var concept: String? by MsgFieldDelegate(PresenceIndices.concept) { null }
    override var requires: List<kotlin.String> by MsgFieldDelegate { mutableListOf() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    val _presence: com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsPresence = object : com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsPresence { 
        override val hasInline: kotlin.Boolean get() = presenceMask[0]

        override val hasConcept: kotlin.Boolean get() = presenceMask[1]
    }

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        var result = if (presenceMask[0]) (inline?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[1]) (concept?.hashCode() ?: 0) else 0
        result = 31 * result + requires.hashCode()
        return result
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ProtoWithKeywordsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && inline != other.inline) return false
        if (presenceMask[1] && concept != other.concept) return false
        if (requires != other.requires) return false
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords(")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}inline=${inline},")
            } else { 
                appendLine("${nextIndentString}inline=<unset>,")
            }

            if (presenceMask[1]) { 
                appendLine("${nextIndentString}concept=${concept},")
            } else { 
                appendLine("${nextIndentString}concept=<unset>,")
            }

            appendLine("${nextIndentString}requires=${requires},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal()
        if (presenceMask[0]) { 
            copy.inline = this.inline
        }

        if (presenceMask[1]) { 
            copy.concept = this.concept
        }

        copy.requires = this.requires.map { it }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class TestAllRequiredTypesProto2Internal: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 39) { 
    private object PresenceIndices { 
        const val requiredInt32: Int = 0
        const val requiredInt64: Int = 1
        const val requiredUint32: Int = 2
        const val requiredUint64: Int = 3
        const val requiredSint32: Int = 4
        const val requiredSint64: Int = 5
        const val requiredFixed32: Int = 6
        const val requiredFixed64: Int = 7
        const val requiredSfixed32: Int = 8
        const val requiredSfixed64: Int = 9
        const val requiredFloat: Int = 10
        const val requiredDouble: Int = 11
        const val requiredBool: Int = 12
        const val requiredString: Int = 13
        const val requiredBytes: Int = 14
        const val requiredNestedMessage: Int = 15
        const val requiredForeignMessage: Int = 16
        const val requiredNestedEnum: Int = 17
        const val requiredForeignEnum: Int = 18
        const val requiredStringPiece: Int = 19
        const val requiredCord: Int = 20
        const val recursiveMessage: Int = 21
        const val optionalRecursiveMessage: Int = 22
        const val data: Int = 23
        const val defaultInt32: Int = 24
        const val defaultInt64: Int = 25
        const val defaultUint32: Int = 26
        const val defaultUint64: Int = 27
        const val defaultSint32: Int = 28
        const val defaultSint64: Int = 29
        const val defaultFixed32: Int = 30
        const val defaultFixed64: Int = 31
        const val defaultSfixed32: Int = 32
        const val defaultSfixed64: Int = 33
        const val defaultFloat: Int = 34
        const val defaultDouble: Int = 35
        const val defaultBool: Int = 36
        const val defaultString: Int = 37
        const val defaultBytes: Int = 38
    }

    private object BytesDefaults { 
        val defaultBytes: ByteArray = "joshua".encodeToByteArray()
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var requiredInt32: Int by MsgFieldDelegate(PresenceIndices.requiredInt32) { 0 }
    override var requiredInt64: Long by MsgFieldDelegate(PresenceIndices.requiredInt64) { 0L }
    override var requiredUint32: UInt by MsgFieldDelegate(PresenceIndices.requiredUint32) { 0u }
    override var requiredUint64: ULong by MsgFieldDelegate(PresenceIndices.requiredUint64) { 0uL }
    override var requiredSint32: Int by MsgFieldDelegate(PresenceIndices.requiredSint32) { 0 }
    override var requiredSint64: Long by MsgFieldDelegate(PresenceIndices.requiredSint64) { 0L }
    override var requiredFixed32: UInt by MsgFieldDelegate(PresenceIndices.requiredFixed32) { 0u }
    override var requiredFixed64: ULong by MsgFieldDelegate(PresenceIndices.requiredFixed64) { 0uL }
    override var requiredSfixed32: Int by MsgFieldDelegate(PresenceIndices.requiredSfixed32) { 0 }
    override var requiredSfixed64: Long by MsgFieldDelegate(PresenceIndices.requiredSfixed64) { 0L }
    override var requiredFloat: Float by MsgFieldDelegate(PresenceIndices.requiredFloat) { 0.0f }
    override var requiredDouble: Double by MsgFieldDelegate(PresenceIndices.requiredDouble) { 0.0 }
    override var requiredBool: Boolean by MsgFieldDelegate(PresenceIndices.requiredBool) { false }
    override var requiredString: String by MsgFieldDelegate(PresenceIndices.requiredString) { "" }
    override var requiredBytes: ByteArray by MsgFieldDelegate(PresenceIndices.requiredBytes) { byteArrayOf() }
    override var requiredNestedMessage: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage by MsgFieldDelegate(PresenceIndices.requiredNestedMessage) { com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal() }
    override var requiredForeignMessage: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 by MsgFieldDelegate(PresenceIndices.requiredForeignMessage) { com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal() }
    override var requiredNestedEnum: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum by MsgFieldDelegate(PresenceIndices.requiredNestedEnum) { com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum.FOO }
    override var requiredForeignEnum: com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2 by MsgFieldDelegate(PresenceIndices.requiredForeignEnum) { com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.FOREIGN_FOO }
    override var requiredStringPiece: String by MsgFieldDelegate(PresenceIndices.requiredStringPiece) { "" }
    override var requiredCord: String by MsgFieldDelegate(PresenceIndices.requiredCord) { "" }
    override var recursiveMessage: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2 by MsgFieldDelegate(PresenceIndices.recursiveMessage) { com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal() }
    override var optionalRecursiveMessage: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2 by MsgFieldDelegate(PresenceIndices.optionalRecursiveMessage) { com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal() }
    override var data: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data by MsgFieldDelegate(PresenceIndices.data) { com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal() }
    override var defaultInt32: Int by MsgFieldDelegate(PresenceIndices.defaultInt32) { -123456789 }
    override var defaultInt64: Long by MsgFieldDelegate(PresenceIndices.defaultInt64) { -9123456789123456789L }
    override var defaultUint32: UInt by MsgFieldDelegate(PresenceIndices.defaultUint32) { 2123456789u }
    override var defaultUint64: ULong by MsgFieldDelegate(PresenceIndices.defaultUint64) { 10123456789123456789uL }
    override var defaultSint32: Int by MsgFieldDelegate(PresenceIndices.defaultSint32) { -123456789 }
    override var defaultSint64: Long by MsgFieldDelegate(PresenceIndices.defaultSint64) { -9123456789123456789L }
    override var defaultFixed32: UInt by MsgFieldDelegate(PresenceIndices.defaultFixed32) { 2123456789u }
    override var defaultFixed64: ULong by MsgFieldDelegate(PresenceIndices.defaultFixed64) { 10123456789123456789uL }
    override var defaultSfixed32: Int by MsgFieldDelegate(PresenceIndices.defaultSfixed32) { -123456789 }
    override var defaultSfixed64: Long by MsgFieldDelegate(PresenceIndices.defaultSfixed64) { -9123456789123456789L }
    override var defaultFloat: Float by MsgFieldDelegate(PresenceIndices.defaultFloat) { Float.fromBits(0x50061C46) }
    override var defaultDouble: Double by MsgFieldDelegate(PresenceIndices.defaultDouble) { Double.fromBits(0x44ADA56A4B0835C0L) }
    override var defaultBool: Boolean by MsgFieldDelegate(PresenceIndices.defaultBool) { true }
    override var defaultString: String by MsgFieldDelegate(PresenceIndices.defaultString) { "Rosebud" }
    override var defaultBytes: ByteArray by MsgFieldDelegate(PresenceIndices.defaultBytes) { BytesDefaults.defaultBytes }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence = object : com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence { 
        override val hasRequiredInt32: kotlin.Boolean get() = presenceMask[0]

        override val hasRequiredInt64: kotlin.Boolean get() = presenceMask[1]

        override val hasRequiredUint32: kotlin.Boolean get() = presenceMask[2]

        override val hasRequiredUint64: kotlin.Boolean get() = presenceMask[3]

        override val hasRequiredSint32: kotlin.Boolean get() = presenceMask[4]

        override val hasRequiredSint64: kotlin.Boolean get() = presenceMask[5]

        override val hasRequiredFixed32: kotlin.Boolean get() = presenceMask[6]

        override val hasRequiredFixed64: kotlin.Boolean get() = presenceMask[7]

        override val hasRequiredSfixed32: kotlin.Boolean get() = presenceMask[8]

        override val hasRequiredSfixed64: kotlin.Boolean get() = presenceMask[9]

        override val hasRequiredFloat: kotlin.Boolean get() = presenceMask[10]

        override val hasRequiredDouble: kotlin.Boolean get() = presenceMask[11]

        override val hasRequiredBool: kotlin.Boolean get() = presenceMask[12]

        override val hasRequiredString: kotlin.Boolean get() = presenceMask[13]

        override val hasRequiredBytes: kotlin.Boolean get() = presenceMask[14]

        override val hasRequiredNestedMessage: kotlin.Boolean get() = presenceMask[15]

        override val hasRequiredForeignMessage: kotlin.Boolean get() = presenceMask[16]

        override val hasRequiredNestedEnum: kotlin.Boolean get() = presenceMask[17]

        override val hasRequiredForeignEnum: kotlin.Boolean get() = presenceMask[18]

        override val hasRequiredStringPiece: kotlin.Boolean get() = presenceMask[19]

        override val hasRequiredCord: kotlin.Boolean get() = presenceMask[20]

        override val hasRecursiveMessage: kotlin.Boolean get() = presenceMask[21]

        override val hasOptionalRecursiveMessage: kotlin.Boolean get() = presenceMask[22]

        override val hasData: kotlin.Boolean get() = presenceMask[23]

        override val hasDefaultInt32: kotlin.Boolean get() = presenceMask[24]

        override val hasDefaultInt64: kotlin.Boolean get() = presenceMask[25]

        override val hasDefaultUint32: kotlin.Boolean get() = presenceMask[26]

        override val hasDefaultUint64: kotlin.Boolean get() = presenceMask[27]

        override val hasDefaultSint32: kotlin.Boolean get() = presenceMask[28]

        override val hasDefaultSint64: kotlin.Boolean get() = presenceMask[29]

        override val hasDefaultFixed32: kotlin.Boolean get() = presenceMask[30]

        override val hasDefaultFixed64: kotlin.Boolean get() = presenceMask[31]

        override val hasDefaultSfixed32: kotlin.Boolean get() = presenceMask[32]

        override val hasDefaultSfixed64: kotlin.Boolean get() = presenceMask[33]

        override val hasDefaultFloat: kotlin.Boolean get() = presenceMask[34]

        override val hasDefaultDouble: kotlin.Boolean get() = presenceMask[35]

        override val hasDefaultBool: kotlin.Boolean get() = presenceMask[36]

        override val hasDefaultString: kotlin.Boolean get() = presenceMask[37]

        override val hasDefaultBytes: kotlin.Boolean get() = presenceMask[38]
    }

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        var result = if (presenceMask[0]) requiredInt32.hashCode() else 0
        result = 31 * result + if (presenceMask[1]) requiredInt64.hashCode() else 0
        result = 31 * result + if (presenceMask[2]) requiredUint32.hashCode() else 0
        result = 31 * result + if (presenceMask[3]) requiredUint64.hashCode() else 0
        result = 31 * result + if (presenceMask[4]) requiredSint32.hashCode() else 0
        result = 31 * result + if (presenceMask[5]) requiredSint64.hashCode() else 0
        result = 31 * result + if (presenceMask[6]) requiredFixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[7]) requiredFixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[8]) requiredSfixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[9]) requiredSfixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[10]) requiredFloat.hashCode() else 0
        result = 31 * result + if (presenceMask[11]) requiredDouble.hashCode() else 0
        result = 31 * result + if (presenceMask[12]) requiredBool.hashCode() else 0
        result = 31 * result + if (presenceMask[13]) requiredString.hashCode() else 0
        result = 31 * result + if (presenceMask[14]) requiredBytes.contentHashCode() else 0
        result = 31 * result + if (presenceMask[15]) requiredNestedMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[16]) requiredForeignMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[17]) requiredNestedEnum.hashCode() else 0
        result = 31 * result + if (presenceMask[18]) requiredForeignEnum.hashCode() else 0
        result = 31 * result + if (presenceMask[19]) requiredStringPiece.hashCode() else 0
        result = 31 * result + if (presenceMask[20]) requiredCord.hashCode() else 0
        result = 31 * result + if (presenceMask[21]) recursiveMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[22]) optionalRecursiveMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[23]) data.hashCode() else 0
        result = 31 * result + if (presenceMask[24]) defaultInt32.hashCode() else 0
        result = 31 * result + if (presenceMask[25]) defaultInt64.hashCode() else 0
        result = 31 * result + if (presenceMask[26]) defaultUint32.hashCode() else 0
        result = 31 * result + if (presenceMask[27]) defaultUint64.hashCode() else 0
        result = 31 * result + if (presenceMask[28]) defaultSint32.hashCode() else 0
        result = 31 * result + if (presenceMask[29]) defaultSint64.hashCode() else 0
        result = 31 * result + if (presenceMask[30]) defaultFixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[31]) defaultFixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[32]) defaultSfixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[33]) defaultSfixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[34]) defaultFloat.hashCode() else 0
        result = 31 * result + if (presenceMask[35]) defaultDouble.hashCode() else 0
        result = 31 * result + if (presenceMask[36]) defaultBool.hashCode() else 0
        result = 31 * result + if (presenceMask[37]) defaultString.hashCode() else 0
        result = 31 * result + if (presenceMask[38]) defaultBytes.contentHashCode() else 0
        return result
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAllRequiredTypesProto2Internal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && requiredInt32 != other.requiredInt32) return false
        if (presenceMask[1] && requiredInt64 != other.requiredInt64) return false
        if (presenceMask[2] && requiredUint32 != other.requiredUint32) return false
        if (presenceMask[3] && requiredUint64 != other.requiredUint64) return false
        if (presenceMask[4] && requiredSint32 != other.requiredSint32) return false
        if (presenceMask[5] && requiredSint64 != other.requiredSint64) return false
        if (presenceMask[6] && requiredFixed32 != other.requiredFixed32) return false
        if (presenceMask[7] && requiredFixed64 != other.requiredFixed64) return false
        if (presenceMask[8] && requiredSfixed32 != other.requiredSfixed32) return false
        if (presenceMask[9] && requiredSfixed64 != other.requiredSfixed64) return false
        if (presenceMask[10] && requiredFloat != other.requiredFloat) return false
        if (presenceMask[11] && requiredDouble != other.requiredDouble) return false
        if (presenceMask[12] && requiredBool != other.requiredBool) return false
        if (presenceMask[13] && requiredString != other.requiredString) return false
        if (presenceMask[14] && !requiredBytes.contentEquals(other.requiredBytes)) return false
        if (presenceMask[15] && requiredNestedMessage != other.requiredNestedMessage) return false
        if (presenceMask[16] && requiredForeignMessage != other.requiredForeignMessage) return false
        if (presenceMask[17] && requiredNestedEnum != other.requiredNestedEnum) return false
        if (presenceMask[18] && requiredForeignEnum != other.requiredForeignEnum) return false
        if (presenceMask[19] && requiredStringPiece != other.requiredStringPiece) return false
        if (presenceMask[20] && requiredCord != other.requiredCord) return false
        if (presenceMask[21] && recursiveMessage != other.recursiveMessage) return false
        if (presenceMask[22] && optionalRecursiveMessage != other.optionalRecursiveMessage) return false
        if (presenceMask[23] && data != other.data) return false
        if (presenceMask[24] && defaultInt32 != other.defaultInt32) return false
        if (presenceMask[25] && defaultInt64 != other.defaultInt64) return false
        if (presenceMask[26] && defaultUint32 != other.defaultUint32) return false
        if (presenceMask[27] && defaultUint64 != other.defaultUint64) return false
        if (presenceMask[28] && defaultSint32 != other.defaultSint32) return false
        if (presenceMask[29] && defaultSint64 != other.defaultSint64) return false
        if (presenceMask[30] && defaultFixed32 != other.defaultFixed32) return false
        if (presenceMask[31] && defaultFixed64 != other.defaultFixed64) return false
        if (presenceMask[32] && defaultSfixed32 != other.defaultSfixed32) return false
        if (presenceMask[33] && defaultSfixed64 != other.defaultSfixed64) return false
        if (presenceMask[34] && defaultFloat != other.defaultFloat) return false
        if (presenceMask[35] && defaultDouble != other.defaultDouble) return false
        if (presenceMask[36] && defaultBool != other.defaultBool) return false
        if (presenceMask[37] && defaultString != other.defaultString) return false
        if (presenceMask[38] && !defaultBytes.contentEquals(other.defaultBytes)) return false
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2(")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}requiredInt32=${requiredInt32},")
            } else { 
                appendLine("${nextIndentString}requiredInt32=<unset>,")
            }

            if (presenceMask[1]) { 
                appendLine("${nextIndentString}requiredInt64=${requiredInt64},")
            } else { 
                appendLine("${nextIndentString}requiredInt64=<unset>,")
            }

            if (presenceMask[2]) { 
                appendLine("${nextIndentString}requiredUint32=${requiredUint32},")
            } else { 
                appendLine("${nextIndentString}requiredUint32=<unset>,")
            }

            if (presenceMask[3]) { 
                appendLine("${nextIndentString}requiredUint64=${requiredUint64},")
            } else { 
                appendLine("${nextIndentString}requiredUint64=<unset>,")
            }

            if (presenceMask[4]) { 
                appendLine("${nextIndentString}requiredSint32=${requiredSint32},")
            } else { 
                appendLine("${nextIndentString}requiredSint32=<unset>,")
            }

            if (presenceMask[5]) { 
                appendLine("${nextIndentString}requiredSint64=${requiredSint64},")
            } else { 
                appendLine("${nextIndentString}requiredSint64=<unset>,")
            }

            if (presenceMask[6]) { 
                appendLine("${nextIndentString}requiredFixed32=${requiredFixed32},")
            } else { 
                appendLine("${nextIndentString}requiredFixed32=<unset>,")
            }

            if (presenceMask[7]) { 
                appendLine("${nextIndentString}requiredFixed64=${requiredFixed64},")
            } else { 
                appendLine("${nextIndentString}requiredFixed64=<unset>,")
            }

            if (presenceMask[8]) { 
                appendLine("${nextIndentString}requiredSfixed32=${requiredSfixed32},")
            } else { 
                appendLine("${nextIndentString}requiredSfixed32=<unset>,")
            }

            if (presenceMask[9]) { 
                appendLine("${nextIndentString}requiredSfixed64=${requiredSfixed64},")
            } else { 
                appendLine("${nextIndentString}requiredSfixed64=<unset>,")
            }

            if (presenceMask[10]) { 
                appendLine("${nextIndentString}requiredFloat=${requiredFloat},")
            } else { 
                appendLine("${nextIndentString}requiredFloat=<unset>,")
            }

            if (presenceMask[11]) { 
                appendLine("${nextIndentString}requiredDouble=${requiredDouble},")
            } else { 
                appendLine("${nextIndentString}requiredDouble=<unset>,")
            }

            if (presenceMask[12]) { 
                appendLine("${nextIndentString}requiredBool=${requiredBool},")
            } else { 
                appendLine("${nextIndentString}requiredBool=<unset>,")
            }

            if (presenceMask[13]) { 
                appendLine("${nextIndentString}requiredString=${requiredString},")
            } else { 
                appendLine("${nextIndentString}requiredString=<unset>,")
            }

            if (presenceMask[14]) { 
                appendLine("${nextIndentString}requiredBytes=${requiredBytes.contentToString()},")
            } else { 
                appendLine("${nextIndentString}requiredBytes=<unset>,")
            }

            if (presenceMask[15]) { 
                appendLine("${nextIndentString}requiredNestedMessage=${requiredNestedMessage.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}requiredNestedMessage=<unset>,")
            }

            if (presenceMask[16]) { 
                appendLine("${nextIndentString}requiredForeignMessage=${requiredForeignMessage.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}requiredForeignMessage=<unset>,")
            }

            if (presenceMask[17]) { 
                appendLine("${nextIndentString}requiredNestedEnum=${requiredNestedEnum},")
            } else { 
                appendLine("${nextIndentString}requiredNestedEnum=<unset>,")
            }

            if (presenceMask[18]) { 
                appendLine("${nextIndentString}requiredForeignEnum=${requiredForeignEnum},")
            } else { 
                appendLine("${nextIndentString}requiredForeignEnum=<unset>,")
            }

            if (presenceMask[19]) { 
                appendLine("${nextIndentString}requiredStringPiece=${requiredStringPiece},")
            } else { 
                appendLine("${nextIndentString}requiredStringPiece=<unset>,")
            }

            if (presenceMask[20]) { 
                appendLine("${nextIndentString}requiredCord=${requiredCord},")
            } else { 
                appendLine("${nextIndentString}requiredCord=<unset>,")
            }

            if (presenceMask[21]) { 
                appendLine("${nextIndentString}recursiveMessage=${recursiveMessage.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}recursiveMessage=<unset>,")
            }

            if (presenceMask[22]) { 
                appendLine("${nextIndentString}optionalRecursiveMessage=${optionalRecursiveMessage.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}optionalRecursiveMessage=<unset>,")
            }

            if (presenceMask[23]) { 
                appendLine("${nextIndentString}data=${data.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}data=<unset>,")
            }

            if (presenceMask[24]) { 
                appendLine("${nextIndentString}defaultInt32=${defaultInt32},")
            } else { 
                appendLine("${nextIndentString}defaultInt32=<unset>,")
            }

            if (presenceMask[25]) { 
                appendLine("${nextIndentString}defaultInt64=${defaultInt64},")
            } else { 
                appendLine("${nextIndentString}defaultInt64=<unset>,")
            }

            if (presenceMask[26]) { 
                appendLine("${nextIndentString}defaultUint32=${defaultUint32},")
            } else { 
                appendLine("${nextIndentString}defaultUint32=<unset>,")
            }

            if (presenceMask[27]) { 
                appendLine("${nextIndentString}defaultUint64=${defaultUint64},")
            } else { 
                appendLine("${nextIndentString}defaultUint64=<unset>,")
            }

            if (presenceMask[28]) { 
                appendLine("${nextIndentString}defaultSint32=${defaultSint32},")
            } else { 
                appendLine("${nextIndentString}defaultSint32=<unset>,")
            }

            if (presenceMask[29]) { 
                appendLine("${nextIndentString}defaultSint64=${defaultSint64},")
            } else { 
                appendLine("${nextIndentString}defaultSint64=<unset>,")
            }

            if (presenceMask[30]) { 
                appendLine("${nextIndentString}defaultFixed32=${defaultFixed32},")
            } else { 
                appendLine("${nextIndentString}defaultFixed32=<unset>,")
            }

            if (presenceMask[31]) { 
                appendLine("${nextIndentString}defaultFixed64=${defaultFixed64},")
            } else { 
                appendLine("${nextIndentString}defaultFixed64=<unset>,")
            }

            if (presenceMask[32]) { 
                appendLine("${nextIndentString}defaultSfixed32=${defaultSfixed32},")
            } else { 
                appendLine("${nextIndentString}defaultSfixed32=<unset>,")
            }

            if (presenceMask[33]) { 
                appendLine("${nextIndentString}defaultSfixed64=${defaultSfixed64},")
            } else { 
                appendLine("${nextIndentString}defaultSfixed64=<unset>,")
            }

            if (presenceMask[34]) { 
                appendLine("${nextIndentString}defaultFloat=${defaultFloat},")
            } else { 
                appendLine("${nextIndentString}defaultFloat=<unset>,")
            }

            if (presenceMask[35]) { 
                appendLine("${nextIndentString}defaultDouble=${defaultDouble},")
            } else { 
                appendLine("${nextIndentString}defaultDouble=<unset>,")
            }

            if (presenceMask[36]) { 
                appendLine("${nextIndentString}defaultBool=${defaultBool},")
            } else { 
                appendLine("${nextIndentString}defaultBool=<unset>,")
            }

            if (presenceMask[37]) { 
                appendLine("${nextIndentString}defaultString=${defaultString},")
            } else { 
                appendLine("${nextIndentString}defaultString=<unset>,")
            }

            if (presenceMask[38]) { 
                appendLine("${nextIndentString}defaultBytes=${defaultBytes.contentToString()},")
            } else { 
                appendLine("${nextIndentString}defaultBytes=<unset>,")
            }

            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal()
        if (presenceMask[0]) { 
            copy.requiredInt32 = this.requiredInt32
        }

        if (presenceMask[1]) { 
            copy.requiredInt64 = this.requiredInt64
        }

        if (presenceMask[2]) { 
            copy.requiredUint32 = this.requiredUint32
        }

        if (presenceMask[3]) { 
            copy.requiredUint64 = this.requiredUint64
        }

        if (presenceMask[4]) { 
            copy.requiredSint32 = this.requiredSint32
        }

        if (presenceMask[5]) { 
            copy.requiredSint64 = this.requiredSint64
        }

        if (presenceMask[6]) { 
            copy.requiredFixed32 = this.requiredFixed32
        }

        if (presenceMask[7]) { 
            copy.requiredFixed64 = this.requiredFixed64
        }

        if (presenceMask[8]) { 
            copy.requiredSfixed32 = this.requiredSfixed32
        }

        if (presenceMask[9]) { 
            copy.requiredSfixed64 = this.requiredSfixed64
        }

        if (presenceMask[10]) { 
            copy.requiredFloat = this.requiredFloat
        }

        if (presenceMask[11]) { 
            copy.requiredDouble = this.requiredDouble
        }

        if (presenceMask[12]) { 
            copy.requiredBool = this.requiredBool
        }

        if (presenceMask[13]) { 
            copy.requiredString = this.requiredString
        }

        if (presenceMask[14]) { 
            copy.requiredBytes = this.requiredBytes.copyOf()
        }

        if (presenceMask[15]) { 
            copy.requiredNestedMessage = this.requiredNestedMessage.copy()
        }

        if (presenceMask[16]) { 
            copy.requiredForeignMessage = this.requiredForeignMessage.copy()
        }

        if (presenceMask[17]) { 
            copy.requiredNestedEnum = this.requiredNestedEnum
        }

        if (presenceMask[18]) { 
            copy.requiredForeignEnum = this.requiredForeignEnum
        }

        if (presenceMask[19]) { 
            copy.requiredStringPiece = this.requiredStringPiece
        }

        if (presenceMask[20]) { 
            copy.requiredCord = this.requiredCord
        }

        if (presenceMask[21]) { 
            copy.recursiveMessage = this.recursiveMessage.copy()
        }

        if (presenceMask[22]) { 
            copy.optionalRecursiveMessage = this.optionalRecursiveMessage.copy()
        }

        if (presenceMask[23]) { 
            copy.data = this.data.copy()
        }

        if (presenceMask[24]) { 
            copy.defaultInt32 = this.defaultInt32
        }

        if (presenceMask[25]) { 
            copy.defaultInt64 = this.defaultInt64
        }

        if (presenceMask[26]) { 
            copy.defaultUint32 = this.defaultUint32
        }

        if (presenceMask[27]) { 
            copy.defaultUint64 = this.defaultUint64
        }

        if (presenceMask[28]) { 
            copy.defaultSint32 = this.defaultSint32
        }

        if (presenceMask[29]) { 
            copy.defaultSint64 = this.defaultSint64
        }

        if (presenceMask[30]) { 
            copy.defaultFixed32 = this.defaultFixed32
        }

        if (presenceMask[31]) { 
            copy.defaultFixed64 = this.defaultFixed64
        }

        if (presenceMask[32]) { 
            copy.defaultSfixed32 = this.defaultSfixed32
        }

        if (presenceMask[33]) { 
            copy.defaultSfixed64 = this.defaultSfixed64
        }

        if (presenceMask[34]) { 
            copy.defaultFloat = this.defaultFloat
        }

        if (presenceMask[35]) { 
            copy.defaultDouble = this.defaultDouble
        }

        if (presenceMask[36]) { 
            copy.defaultBool = this.defaultBool
        }

        if (presenceMask[37]) { 
            copy.defaultString = this.defaultString
        }

        if (presenceMask[38]) { 
            copy.defaultBytes = this.defaultBytes.copyOf()
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    class NestedMessageInternal: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 3) { 
        private object PresenceIndices { 
            const val a: Int = 0
            const val corecursive: Int = 1
            const val optionalCorecursive: Int = 2
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var a: Int by MsgFieldDelegate(PresenceIndices.a) { 0 }
        override var corecursive: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2 by MsgFieldDelegate(PresenceIndices.corecursive) { com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal() }
        override var optionalCorecursive: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2 by MsgFieldDelegate(PresenceIndices.optionalCorecursive) { com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence.NestedMessage = object : com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence.NestedMessage { 
            override val hasA: kotlin.Boolean get() = presenceMask[0]

            override val hasCorecursive: kotlin.Boolean get() = presenceMask[1]

            override val hasOptionalCorecursive: kotlin.Boolean get() = presenceMask[2]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) a.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) corecursive.hashCode() else 0
            result = 31 * result + if (presenceMask[2]) optionalCorecursive.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as NestedMessageInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && a != other.a) return false
            if (presenceMask[1] && corecursive != other.corecursive) return false
            if (presenceMask[2] && optionalCorecursive != other.optionalCorecursive) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}a=${a},")
                } else { 
                    appendLine("${nextIndentString}a=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}corecursive=${corecursive.asInternal().asString(indent = indent + 4)},")
                } else { 
                    appendLine("${nextIndentString}corecursive=<unset>,")
                }

                if (presenceMask[2]) { 
                    appendLine("${nextIndentString}optionalCorecursive=${optionalCorecursive.asInternal().asString(indent = indent + 4)},")
                } else { 
                    appendLine("${nextIndentString}optionalCorecursive=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal()
            if (presenceMask[0]) { 
                copy.a = this.a
            }

            if (presenceMask[1]) { 
                copy.corecursive = this.corecursive.copy()
            }

            if (presenceMask[2]) { 
                copy.optionalCorecursive = this.optionalCorecursive.copy()
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class DataInternal: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val groupInt32: Int = 0
            const val groupUint32: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var groupInt32: Int by MsgFieldDelegate(PresenceIndices.groupInt32) { 0 }
        override var groupUint32: UInt by MsgFieldDelegate(PresenceIndices.groupUint32) { 0u }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence.Data = object : com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence.Data { 
            override val hasGroupInt32: kotlin.Boolean get() = presenceMask[0]

            override val hasGroupUint32: kotlin.Boolean get() = presenceMask[1]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = if (presenceMask[0]) groupInt32.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) groupUint32.hashCode() else 0
            return result
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as DataInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && groupInt32 != other.groupInt32) return false
            if (presenceMask[1] && groupUint32 != other.groupUint32) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}groupInt32=${groupInt32},")
                } else { 
                    appendLine("${nextIndentString}groupInt32=<unset>,")
                }

                if (presenceMask[1]) { 
                    appendLine("${nextIndentString}groupUint32=${groupUint32},")
                } else { 
                    appendLine("${nextIndentString}groupUint32=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal()
            if (presenceMask[0]) { 
                copy.groupInt32 = this.groupInt32
            }

            if (presenceMask[1]) { 
                copy.groupUint32 = this.groupUint32
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MessageSetCorrectInternal: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return this::class.hashCode()
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectInternal
            other.checkRequiredFields()
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect(")
                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MessageSetCorrectExtension1Internal: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            const val str: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var str: String by MsgFieldDelegate(PresenceIndices.str) { "" }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension1 = object : com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension1 { 
            override val hasStr: kotlin.Boolean get() = presenceMask[0]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return if (presenceMask[0]) str.hashCode() else 0
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectExtension1Internal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && str != other.str) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}str=${str},")
                } else { 
                    appendLine("${nextIndentString}str=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal()
            if (presenceMask[0]) { 
                copy.str = this.str
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MessageSetCorrectExtension2Internal: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            const val i: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var i: Int by MsgFieldDelegate(PresenceIndices.i) { 0 }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        val _presence: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension2 = object : com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension2 { 
            override val hasI: kotlin.Boolean get() = presenceMask[0]
        }

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return if (presenceMask[0]) i.hashCode() else 0
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectExtension2Internal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && i != other.i) return false
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2(")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}i=${i},")
                } else { 
                    appendLine("${nextIndentString}i=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal()
            if (presenceMask[0]) { 
                copy.i = this.i
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class TestLargeOneofInternal: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var largeOneof: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof? = null

    override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return (largeOneof?.oneOfHashCode() ?: 0)
    }

    fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.oneOfHashCode(): kotlin.Int { 
        val offset = when (this) { 
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A1 -> 0
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A2 -> 1
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A3 -> 2
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A4 -> 3
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A5 -> 4
        }

        return hashCode() + offset
    }

    override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestLargeOneofInternal
        other.checkRequiredFields()
        if (largeOneof != other.largeOneof) return false
        return true
    }

    override fun toString(): kotlin.String { 
        return asString()
    }

    fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf_test_messages.editions.proto2.TestLargeOneof(")
            appendLine("${nextIndentString}largeOneof=${largeOneof},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal { 
        val copy = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal()
        copy.largeOneof = this.largeOneof?.oneOfCopy()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.oneOfCopy(): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof { 
        return when (this) { 
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A1 -> { 
                com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A1(this.value.copy())
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A2 -> { 
                com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A2(this.value.copy())
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A3 -> { 
                com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A3(this.value.copy())
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A4 -> { 
                com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A4(this.value.copy())
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A5 -> { 
                com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A5(this.value.copy())
            }
        }
    }

    class A1Internal: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return this::class.hashCode()
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A1Internal
            other.checkRequiredFields()
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1(")
                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class A2Internal: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return this::class.hashCode()
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A2Internal
            other.checkRequiredFields()
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2(")
                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class A3Internal: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return this::class.hashCode()
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A3Internal
            other.checkRequiredFields()
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3(")
                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class A4Internal: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return this::class.hashCode()
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A4Internal
            other.checkRequiredFields()
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4(")
                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class A5Internal: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            return this::class.hashCode()
        }

        override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A5Internal
            other.checkRequiredFields()
            return true
        }

        override fun toString(): kotlin.String { 
            return asString()
        }

        fun asString(indent: kotlin.Int = 0): kotlin.String { 
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString { 
                appendLine("com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5(")
                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        fun copyInternal(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal { 
            val copy = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                val internalMsg = value.asInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    internalMsg.encodeWith(encoder)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[15]) { 
        optionalNestedMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[16]) { 
        optionalForeignMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[21]) { 
        recursiveMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[22]) { 
        data.asInternal().checkRequiredFields()
    }

    if (presenceMask[23]) { 
        multiwordgroupfield.asInternal().checkRequiredFields()
    }

    if (presenceMask[57]) { 
        messageSetCorrect.asInternal().checkRequiredFields()
    }

    oneofField?.also { 
        when { 
            it is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofNestedMessage -> { 
                it.value.asInternal().checkRequiredFields()
            }
        }
    }

    repeatedNestedMessage.forEach { 
        it.asInternal().checkRequiredFields()
    }

    repeatedForeignMessage.forEach { 
        it.asInternal().checkRequiredFields()
    }

    mapInt32NestedMessage.values.forEach { 
        it.asInternal().checkRequiredFields()
    }

    mapStringNestedMessage.values.forEach { 
        it.asInternal().checkRequiredFields()
    }

    mapStringForeignMessage.values.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    optionalInt32?.also { 
        encoder.writeInt32(fieldNr = 1, value = it)
    }

    optionalInt64?.also { 
        encoder.writeInt64(fieldNr = 2, value = it)
    }

    optionalUint32?.also { 
        encoder.writeUInt32(fieldNr = 3, value = it)
    }

    optionalUint64?.also { 
        encoder.writeUInt64(fieldNr = 4, value = it)
    }

    optionalSint32?.also { 
        encoder.writeSInt32(fieldNr = 5, value = it)
    }

    optionalSint64?.also { 
        encoder.writeSInt64(fieldNr = 6, value = it)
    }

    optionalFixed32?.also { 
        encoder.writeFixed32(fieldNr = 7, value = it)
    }

    optionalFixed64?.also { 
        encoder.writeFixed64(fieldNr = 8, value = it)
    }

    optionalSfixed32?.also { 
        encoder.writeSFixed32(fieldNr = 9, value = it)
    }

    optionalSfixed64?.also { 
        encoder.writeSFixed64(fieldNr = 10, value = it)
    }

    optionalFloat?.also { 
        encoder.writeFloat(fieldNr = 11, value = it)
    }

    optionalDouble?.also { 
        encoder.writeDouble(fieldNr = 12, value = it)
    }

    optionalBool?.also { 
        encoder.writeBool(fieldNr = 13, value = it)
    }

    optionalString?.also { 
        encoder.writeString(fieldNr = 14, value = it)
    }

    optionalBytes?.also { 
        encoder.writeBytes(fieldNr = 15, value = it)
    }

    if (presenceMask[15]) { 
        encoder.writeMessage(fieldNr = 18, value = optionalNestedMessage.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[16]) { 
        encoder.writeMessage(fieldNr = 19, value = optionalForeignMessage.asInternal()) { encodeWith(it) }
    }

    optionalNestedEnum?.also { 
        encoder.writeEnum(fieldNr = 21, value = it.number)
    }

    optionalForeignEnum?.also { 
        encoder.writeEnum(fieldNr = 22, value = it.number)
    }

    optionalStringPiece?.also { 
        encoder.writeString(fieldNr = 24, value = it)
    }

    optionalCord?.also { 
        encoder.writeString(fieldNr = 25, value = it)
    }

    if (presenceMask[21]) { 
        encoder.writeMessage(fieldNr = 27, value = recursiveMessage.asInternal()) { encodeWith(it) }
    }

    if (repeatedInt32.isNotEmpty()) { 
        repeatedInt32.forEach { 
            encoder.writeInt32(31, it)
        }
    }

    if (repeatedInt64.isNotEmpty()) { 
        repeatedInt64.forEach { 
            encoder.writeInt64(32, it)
        }
    }

    if (repeatedUint32.isNotEmpty()) { 
        repeatedUint32.forEach { 
            encoder.writeUInt32(33, it)
        }
    }

    if (repeatedUint64.isNotEmpty()) { 
        repeatedUint64.forEach { 
            encoder.writeUInt64(34, it)
        }
    }

    if (repeatedSint32.isNotEmpty()) { 
        repeatedSint32.forEach { 
            encoder.writeSInt32(35, it)
        }
    }

    if (repeatedSint64.isNotEmpty()) { 
        repeatedSint64.forEach { 
            encoder.writeSInt64(36, it)
        }
    }

    if (repeatedFixed32.isNotEmpty()) { 
        repeatedFixed32.forEach { 
            encoder.writeFixed32(37, it)
        }
    }

    if (repeatedFixed64.isNotEmpty()) { 
        repeatedFixed64.forEach { 
            encoder.writeFixed64(38, it)
        }
    }

    if (repeatedSfixed32.isNotEmpty()) { 
        repeatedSfixed32.forEach { 
            encoder.writeSFixed32(39, it)
        }
    }

    if (repeatedSfixed64.isNotEmpty()) { 
        repeatedSfixed64.forEach { 
            encoder.writeSFixed64(40, it)
        }
    }

    if (repeatedFloat.isNotEmpty()) { 
        repeatedFloat.forEach { 
            encoder.writeFloat(41, it)
        }
    }

    if (repeatedDouble.isNotEmpty()) { 
        repeatedDouble.forEach { 
            encoder.writeDouble(42, it)
        }
    }

    if (repeatedBool.isNotEmpty()) { 
        repeatedBool.forEach { 
            encoder.writeBool(43, it)
        }
    }

    if (repeatedString.isNotEmpty()) { 
        repeatedString.forEach { 
            encoder.writeString(44, it)
        }
    }

    if (repeatedBytes.isNotEmpty()) { 
        repeatedBytes.forEach { 
            encoder.writeBytes(45, it)
        }
    }

    if (repeatedNestedMessage.isNotEmpty()) { 
        repeatedNestedMessage.forEach { 
            encoder.writeMessage(fieldNr = 48, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedForeignMessage.isNotEmpty()) { 
        repeatedForeignMessage.forEach { 
            encoder.writeMessage(fieldNr = 49, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedNestedEnum.isNotEmpty()) { 
        repeatedNestedEnum.forEach { 
            encoder.writeEnum(51, it.number)
        }
    }

    if (repeatedForeignEnum.isNotEmpty()) { 
        repeatedForeignEnum.forEach { 
            encoder.writeEnum(52, it.number)
        }
    }

    if (repeatedStringPiece.isNotEmpty()) { 
        repeatedStringPiece.forEach { 
            encoder.writeString(54, it)
        }
    }

    if (repeatedCord.isNotEmpty()) { 
        repeatedCord.forEach { 
            encoder.writeString(55, it)
        }
    }

    if (packedInt32.isNotEmpty()) { 
        encoder.writePackedInt32(fieldNr = 75, value = packedInt32, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedInt32(packedInt32))
    }

    if (packedInt64.isNotEmpty()) { 
        encoder.writePackedInt64(fieldNr = 76, value = packedInt64, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedInt64(packedInt64))
    }

    if (packedUint32.isNotEmpty()) { 
        encoder.writePackedUInt32(fieldNr = 77, value = packedUint32, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedUInt32(packedUint32))
    }

    if (packedUint64.isNotEmpty()) { 
        encoder.writePackedUInt64(fieldNr = 78, value = packedUint64, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedUInt64(packedUint64))
    }

    if (packedSint32.isNotEmpty()) { 
        encoder.writePackedSInt32(fieldNr = 79, value = packedSint32, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedSInt32(packedSint32))
    }

    if (packedSint64.isNotEmpty()) { 
        encoder.writePackedSInt64(fieldNr = 80, value = packedSint64, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedSInt64(packedSint64))
    }

    if (packedFixed32.isNotEmpty()) { 
        encoder.writePackedFixed32(fieldNr = 81, value = packedFixed32)
    }

    if (packedFixed64.isNotEmpty()) { 
        encoder.writePackedFixed64(fieldNr = 82, value = packedFixed64)
    }

    if (packedSfixed32.isNotEmpty()) { 
        encoder.writePackedSFixed32(fieldNr = 83, value = packedSfixed32)
    }

    if (packedSfixed64.isNotEmpty()) { 
        encoder.writePackedSFixed64(fieldNr = 84, value = packedSfixed64)
    }

    if (packedFloat.isNotEmpty()) { 
        encoder.writePackedFloat(fieldNr = 85, value = packedFloat)
    }

    if (packedDouble.isNotEmpty()) { 
        encoder.writePackedDouble(fieldNr = 86, value = packedDouble)
    }

    if (packedBool.isNotEmpty()) { 
        encoder.writePackedBool(fieldNr = 87, value = packedBool, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedBool(packedBool))
    }

    if (packedNestedEnum.isNotEmpty()) { 
        encoder.writePackedEnum(fieldNr = 88, value = packedNestedEnum.map { it.number }, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedEnum(packedNestedEnum.map { it.number }))
    }

    if (unpackedInt32.isNotEmpty()) { 
        unpackedInt32.forEach { 
            encoder.writeInt32(89, it)
        }
    }

    if (unpackedInt64.isNotEmpty()) { 
        unpackedInt64.forEach { 
            encoder.writeInt64(90, it)
        }
    }

    if (unpackedUint32.isNotEmpty()) { 
        unpackedUint32.forEach { 
            encoder.writeUInt32(91, it)
        }
    }

    if (unpackedUint64.isNotEmpty()) { 
        unpackedUint64.forEach { 
            encoder.writeUInt64(92, it)
        }
    }

    if (unpackedSint32.isNotEmpty()) { 
        unpackedSint32.forEach { 
            encoder.writeSInt32(93, it)
        }
    }

    if (unpackedSint64.isNotEmpty()) { 
        unpackedSint64.forEach { 
            encoder.writeSInt64(94, it)
        }
    }

    if (unpackedFixed32.isNotEmpty()) { 
        unpackedFixed32.forEach { 
            encoder.writeFixed32(95, it)
        }
    }

    if (unpackedFixed64.isNotEmpty()) { 
        unpackedFixed64.forEach { 
            encoder.writeFixed64(96, it)
        }
    }

    if (unpackedSfixed32.isNotEmpty()) { 
        unpackedSfixed32.forEach { 
            encoder.writeSFixed32(97, it)
        }
    }

    if (unpackedSfixed64.isNotEmpty()) { 
        unpackedSfixed64.forEach { 
            encoder.writeSFixed64(98, it)
        }
    }

    if (unpackedFloat.isNotEmpty()) { 
        unpackedFloat.forEach { 
            encoder.writeFloat(99, it)
        }
    }

    if (unpackedDouble.isNotEmpty()) { 
        unpackedDouble.forEach { 
            encoder.writeDouble(100, it)
        }
    }

    if (unpackedBool.isNotEmpty()) { 
        unpackedBool.forEach { 
            encoder.writeBool(101, it)
        }
    }

    if (unpackedNestedEnum.isNotEmpty()) { 
        unpackedNestedEnum.forEach { 
            encoder.writeEnum(102, it.number)
        }
    }

    if (mapInt32Int32.isNotEmpty()) { 
        mapInt32Int32.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 56, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapInt64Int64.isNotEmpty()) { 
        mapInt64Int64.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 57, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapUint32Uint32.isNotEmpty()) { 
        mapUint32Uint32.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 58, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapUint64Uint64.isNotEmpty()) { 
        mapUint64Uint64.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 59, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapSint32Sint32.isNotEmpty()) { 
        mapSint32Sint32.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 60, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapSint64Sint64.isNotEmpty()) { 
        mapSint64Sint64.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 61, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapFixed32Fixed32.isNotEmpty()) { 
        mapFixed32Fixed32.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 62, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapFixed64Fixed64.isNotEmpty()) { 
        mapFixed64Fixed64.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 63, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapSfixed32Sfixed32.isNotEmpty()) { 
        mapSfixed32Sfixed32.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 64, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapSfixed64Sfixed64.isNotEmpty()) { 
        mapSfixed64Sfixed64.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 65, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapInt32Bool.isNotEmpty()) { 
        mapInt32Bool.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 104, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapInt32Float.isNotEmpty()) { 
        mapInt32Float.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 66, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapInt32Double.isNotEmpty()) { 
        mapInt32Double.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 67, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapInt32NestedMessage.isNotEmpty()) { 
        mapInt32NestedMessage.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 103, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapBoolBool.isNotEmpty()) { 
        mapBoolBool.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 68, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapStringString.isNotEmpty()) { 
        mapStringString.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 69, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapStringBytes.isNotEmpty()) { 
        mapStringBytes.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 70, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapStringNestedMessage.isNotEmpty()) { 
        mapStringNestedMessage.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 71, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapStringForeignMessage.isNotEmpty()) { 
        mapStringForeignMessage.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 72, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapStringNestedEnum.isNotEmpty()) { 
        mapStringNestedEnum.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 73, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapStringForeignEnum.isNotEmpty()) { 
        mapStringForeignEnum.forEach { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 74, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (presenceMask[22]) { 
        encoder.writeGroupMessage(fieldNr = 201, value = data.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[23]) { 
        encoder.writeGroupMessage(fieldNr = 204, value = multiwordgroupfield.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[24]) { 
        encoder.writeInt32(fieldNr = 241, value = defaultInt32)
    }

    if (presenceMask[25]) { 
        encoder.writeInt64(fieldNr = 242, value = defaultInt64)
    }

    if (presenceMask[26]) { 
        encoder.writeUInt32(fieldNr = 243, value = defaultUint32)
    }

    if (presenceMask[27]) { 
        encoder.writeUInt64(fieldNr = 244, value = defaultUint64)
    }

    if (presenceMask[28]) { 
        encoder.writeSInt32(fieldNr = 245, value = defaultSint32)
    }

    if (presenceMask[29]) { 
        encoder.writeSInt64(fieldNr = 246, value = defaultSint64)
    }

    if (presenceMask[30]) { 
        encoder.writeFixed32(fieldNr = 247, value = defaultFixed32)
    }

    if (presenceMask[31]) { 
        encoder.writeFixed64(fieldNr = 248, value = defaultFixed64)
    }

    if (presenceMask[32]) { 
        encoder.writeSFixed32(fieldNr = 249, value = defaultSfixed32)
    }

    if (presenceMask[33]) { 
        encoder.writeSFixed64(fieldNr = 250, value = defaultSfixed64)
    }

    if (presenceMask[34]) { 
        encoder.writeFloat(fieldNr = 251, value = defaultFloat)
    }

    if (presenceMask[35]) { 
        encoder.writeDouble(fieldNr = 252, value = defaultDouble)
    }

    if (presenceMask[36]) { 
        encoder.writeBool(fieldNr = 253, value = defaultBool)
    }

    if (presenceMask[37]) { 
        encoder.writeString(fieldNr = 254, value = defaultString)
    }

    if (presenceMask[38]) { 
        encoder.writeBytes(fieldNr = 255, value = defaultBytes)
    }

    fieldname1?.also { 
        encoder.writeInt32(fieldNr = 401, value = it)
    }

    fieldName2?.also { 
        encoder.writeInt32(fieldNr = 402, value = it)
    }

    FieldName3?.also { 
        encoder.writeInt32(fieldNr = 403, value = it)
    }

    field_Name4_?.also { 
        encoder.writeInt32(fieldNr = 404, value = it)
    }

    field0name5?.also { 
        encoder.writeInt32(fieldNr = 405, value = it)
    }

    field_0Name6?.also { 
        encoder.writeInt32(fieldNr = 406, value = it)
    }

    fieldName7?.also { 
        encoder.writeInt32(fieldNr = 407, value = it)
    }

    FieldName8?.also { 
        encoder.writeInt32(fieldNr = 408, value = it)
    }

    field_Name9?.also { 
        encoder.writeInt32(fieldNr = 409, value = it)
    }

    Field_Name10?.also { 
        encoder.writeInt32(fieldNr = 410, value = it)
    }

    FIELD_NAME11?.also { 
        encoder.writeInt32(fieldNr = 411, value = it)
    }

    FIELDName12?.also { 
        encoder.writeInt32(fieldNr = 412, value = it)
    }

    _FieldName13?.also { 
        encoder.writeInt32(fieldNr = 413, value = it)
    }

    __FieldName14?.also { 
        encoder.writeInt32(fieldNr = 414, value = it)
    }

    field_Name15?.also { 
        encoder.writeInt32(fieldNr = 415, value = it)
    }

    field__Name16?.also { 
        encoder.writeInt32(fieldNr = 416, value = it)
    }

    fieldName17__?.also { 
        encoder.writeInt32(fieldNr = 417, value = it)
    }

    FieldName18__?.also { 
        encoder.writeInt32(fieldNr = 418, value = it)
    }

    if (presenceMask[57]) { 
        encoder.writeMessage(fieldNr = 500, value = messageSetCorrect.asInternal()) { encodeWith(it) }
    }

    oneofField?.also { 
        when (val value = it) { 
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint32 -> { 
                encoder.writeUInt32(fieldNr = 111, value = value.value)
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofNestedMessage -> { 
                encoder.writeMessage(fieldNr = 112, value = value.value.asInternal()) { encodeWith(it) }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofString -> { 
                encoder.writeString(fieldNr = 113, value = value.value)
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBytes -> { 
                encoder.writeBytes(fieldNr = 114, value = value.value)
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBool -> { 
                encoder.writeBool(fieldNr = 115, value = value.value)
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint64 -> { 
                encoder.writeUInt64(fieldNr = 116, value = value.value)
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofFloat -> { 
                encoder.writeFloat(fieldNr = 117, value = value.value)
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofDouble -> { 
                encoder.writeDouble(fieldNr = 118, value = value.value)
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofEnum -> { 
                encoder.writeEnum(fieldNr = 119, value = value.value.number)
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalInt32 = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalInt64 = decoder.readInt64()
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalUint32 = decoder.readUInt32()
            }
            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalUint64 = decoder.readUInt64()
            }
            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalSint32 = decoder.readSInt32()
            }
            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalSint64 = decoder.readSInt64()
            }
            tag.fieldNr == 7 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.optionalFixed32 = decoder.readFixed32()
            }
            tag.fieldNr == 8 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.optionalFixed64 = decoder.readFixed64()
            }
            tag.fieldNr == 9 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.optionalSfixed32 = decoder.readSFixed32()
            }
            tag.fieldNr == 10 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.optionalSfixed64 = decoder.readSFixed64()
            }
            tag.fieldNr == 11 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.optionalFloat = decoder.readFloat()
            }
            tag.fieldNr == 12 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.optionalDouble = decoder.readDouble()
            }
            tag.fieldNr == 13 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalBool = decoder.readBool()
            }
            tag.fieldNr == 14 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.optionalString = decoder.readString()
            }
            tag.fieldNr == 15 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.optionalBytes = decoder.readBytes()
            }
            tag.fieldNr == 18 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[15]) { 
                    msg.optionalNestedMessage = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.optionalNestedMessage.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal::decodeWith)
            }
            tag.fieldNr == 19 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[16]) { 
                    msg.optionalForeignMessage = com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal()
                }

                decoder.readMessage(msg.optionalForeignMessage.asInternal(), com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal::decodeWith)
            }
            tag.fieldNr == 21 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalNestedEnum = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 22 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalForeignEnum = com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 24 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.optionalStringPiece = decoder.readString()
            }
            tag.fieldNr == 25 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.optionalCord = decoder.readString()
            }
            tag.fieldNr == 27 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[21]) { 
                    msg.recursiveMessage = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal()
                }

                decoder.readMessage(msg.recursiveMessage.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal::decodeWith)
            }
            tag.fieldNr == 31 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedInt32 += decoder.readPackedInt32()
            }
            tag.fieldNr == 31 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readInt32()
                (msg.repeatedInt32 as MutableList).add(elem)
            }
            tag.fieldNr == 32 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedInt64 += decoder.readPackedInt64()
            }
            tag.fieldNr == 32 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readInt64()
                (msg.repeatedInt64 as MutableList).add(elem)
            }
            tag.fieldNr == 33 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedUint32 += decoder.readPackedUInt32()
            }
            tag.fieldNr == 33 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readUInt32()
                (msg.repeatedUint32 as MutableList).add(elem)
            }
            tag.fieldNr == 34 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedUint64 += decoder.readPackedUInt64()
            }
            tag.fieldNr == 34 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readUInt64()
                (msg.repeatedUint64 as MutableList).add(elem)
            }
            tag.fieldNr == 35 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedSint32 += decoder.readPackedSInt32()
            }
            tag.fieldNr == 35 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readSInt32()
                (msg.repeatedSint32 as MutableList).add(elem)
            }
            tag.fieldNr == 36 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedSint64 += decoder.readPackedSInt64()
            }
            tag.fieldNr == 36 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readSInt64()
                (msg.repeatedSint64 as MutableList).add(elem)
            }
            tag.fieldNr == 37 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedFixed32 += decoder.readPackedFixed32()
            }
            tag.fieldNr == 37 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                val elem = decoder.readFixed32()
                (msg.repeatedFixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 38 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedFixed64 += decoder.readPackedFixed64()
            }
            tag.fieldNr == 38 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                val elem = decoder.readFixed64()
                (msg.repeatedFixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 39 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedSfixed32 += decoder.readPackedSFixed32()
            }
            tag.fieldNr == 39 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                val elem = decoder.readSFixed32()
                (msg.repeatedSfixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 40 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedSfixed64 += decoder.readPackedSFixed64()
            }
            tag.fieldNr == 40 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                val elem = decoder.readSFixed64()
                (msg.repeatedSfixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 41 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedFloat += decoder.readPackedFloat()
            }
            tag.fieldNr == 41 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                val elem = decoder.readFloat()
                (msg.repeatedFloat as MutableList).add(elem)
            }
            tag.fieldNr == 42 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedDouble += decoder.readPackedDouble()
            }
            tag.fieldNr == 42 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                val elem = decoder.readDouble()
                (msg.repeatedDouble as MutableList).add(elem)
            }
            tag.fieldNr == 43 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedBool += decoder.readPackedBool()
            }
            tag.fieldNr == 43 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readBool()
                (msg.repeatedBool as MutableList).add(elem)
            }
            tag.fieldNr == 44 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = decoder.readString()
                (msg.repeatedString as MutableList).add(elem)
            }
            tag.fieldNr == 45 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = decoder.readBytes()
                (msg.repeatedBytes as MutableList).add(elem)
            }
            tag.fieldNr == 48 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal::decodeWith)
                (msg.repeatedNestedMessage as MutableList).add(elem)
            }
            tag.fieldNr == 49 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal::decodeWith)
                (msg.repeatedForeignMessage as MutableList).add(elem)
            }
            tag.fieldNr == 51 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedNestedEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.fromNumber(it) }
            }
            tag.fieldNr == 51 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
                (msg.repeatedNestedEnum as MutableList).add(elem)
            }
            tag.fieldNr == 52 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedForeignEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.fromNumber(it) }
            }
            tag.fieldNr == 52 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.fromNumber(decoder.readEnum())
                (msg.repeatedForeignEnum as MutableList).add(elem)
            }
            tag.fieldNr == 54 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = decoder.readString()
                (msg.repeatedStringPiece as MutableList).add(elem)
            }
            tag.fieldNr == 55 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = decoder.readString()
                (msg.repeatedCord as MutableList).add(elem)
            }
            tag.fieldNr == 75 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedInt32 += decoder.readPackedInt32()
            }
            tag.fieldNr == 75 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readInt32()
                (msg.packedInt32 as MutableList).add(elem)
            }
            tag.fieldNr == 76 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedInt64 += decoder.readPackedInt64()
            }
            tag.fieldNr == 76 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readInt64()
                (msg.packedInt64 as MutableList).add(elem)
            }
            tag.fieldNr == 77 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedUint32 += decoder.readPackedUInt32()
            }
            tag.fieldNr == 77 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readUInt32()
                (msg.packedUint32 as MutableList).add(elem)
            }
            tag.fieldNr == 78 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedUint64 += decoder.readPackedUInt64()
            }
            tag.fieldNr == 78 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readUInt64()
                (msg.packedUint64 as MutableList).add(elem)
            }
            tag.fieldNr == 79 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedSint32 += decoder.readPackedSInt32()
            }
            tag.fieldNr == 79 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readSInt32()
                (msg.packedSint32 as MutableList).add(elem)
            }
            tag.fieldNr == 80 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedSint64 += decoder.readPackedSInt64()
            }
            tag.fieldNr == 80 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readSInt64()
                (msg.packedSint64 as MutableList).add(elem)
            }
            tag.fieldNr == 81 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedFixed32 += decoder.readPackedFixed32()
            }
            tag.fieldNr == 81 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                val elem = decoder.readFixed32()
                (msg.packedFixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 82 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedFixed64 += decoder.readPackedFixed64()
            }
            tag.fieldNr == 82 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                val elem = decoder.readFixed64()
                (msg.packedFixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 83 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedSfixed32 += decoder.readPackedSFixed32()
            }
            tag.fieldNr == 83 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                val elem = decoder.readSFixed32()
                (msg.packedSfixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 84 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedSfixed64 += decoder.readPackedSFixed64()
            }
            tag.fieldNr == 84 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                val elem = decoder.readSFixed64()
                (msg.packedSfixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 85 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedFloat += decoder.readPackedFloat()
            }
            tag.fieldNr == 85 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                val elem = decoder.readFloat()
                (msg.packedFloat as MutableList).add(elem)
            }
            tag.fieldNr == 86 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedDouble += decoder.readPackedDouble()
            }
            tag.fieldNr == 86 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                val elem = decoder.readDouble()
                (msg.packedDouble as MutableList).add(elem)
            }
            tag.fieldNr == 87 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedBool += decoder.readPackedBool()
            }
            tag.fieldNr == 87 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readBool()
                (msg.packedBool as MutableList).add(elem)
            }
            tag.fieldNr == 88 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.packedNestedEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.fromNumber(it) }
            }
            tag.fieldNr == 88 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
                (msg.packedNestedEnum as MutableList).add(elem)
            }
            tag.fieldNr == 89 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedInt32 += decoder.readPackedInt32()
            }
            tag.fieldNr == 89 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readInt32()
                (msg.unpackedInt32 as MutableList).add(elem)
            }
            tag.fieldNr == 90 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedInt64 += decoder.readPackedInt64()
            }
            tag.fieldNr == 90 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readInt64()
                (msg.unpackedInt64 as MutableList).add(elem)
            }
            tag.fieldNr == 91 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedUint32 += decoder.readPackedUInt32()
            }
            tag.fieldNr == 91 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readUInt32()
                (msg.unpackedUint32 as MutableList).add(elem)
            }
            tag.fieldNr == 92 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedUint64 += decoder.readPackedUInt64()
            }
            tag.fieldNr == 92 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readUInt64()
                (msg.unpackedUint64 as MutableList).add(elem)
            }
            tag.fieldNr == 93 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedSint32 += decoder.readPackedSInt32()
            }
            tag.fieldNr == 93 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readSInt32()
                (msg.unpackedSint32 as MutableList).add(elem)
            }
            tag.fieldNr == 94 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedSint64 += decoder.readPackedSInt64()
            }
            tag.fieldNr == 94 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readSInt64()
                (msg.unpackedSint64 as MutableList).add(elem)
            }
            tag.fieldNr == 95 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedFixed32 += decoder.readPackedFixed32()
            }
            tag.fieldNr == 95 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                val elem = decoder.readFixed32()
                (msg.unpackedFixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 96 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedFixed64 += decoder.readPackedFixed64()
            }
            tag.fieldNr == 96 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                val elem = decoder.readFixed64()
                (msg.unpackedFixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 97 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedSfixed32 += decoder.readPackedSFixed32()
            }
            tag.fieldNr == 97 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                val elem = decoder.readSFixed32()
                (msg.unpackedSfixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 98 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedSfixed64 += decoder.readPackedSFixed64()
            }
            tag.fieldNr == 98 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                val elem = decoder.readSFixed64()
                (msg.unpackedSfixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 99 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedFloat += decoder.readPackedFloat()
            }
            tag.fieldNr == 99 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                val elem = decoder.readFloat()
                (msg.unpackedFloat as MutableList).add(elem)
            }
            tag.fieldNr == 100 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedDouble += decoder.readPackedDouble()
            }
            tag.fieldNr == 100 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                val elem = decoder.readDouble()
                (msg.unpackedDouble as MutableList).add(elem)
            }
            tag.fieldNr == 101 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedBool += decoder.readPackedBool()
            }
            tag.fieldNr == 101 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readBool()
                (msg.unpackedBool as MutableList).add(elem)
            }
            tag.fieldNr == 102 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.unpackedNestedEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.fromNumber(it) }
            }
            tag.fieldNr == 102 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
                (msg.unpackedNestedEnum as MutableList).add(elem)
            }
            tag.fieldNr == 56 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal::decodeWith)
                    (msg.mapInt32Int32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 57 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal::decodeWith)
                    (msg.mapInt64Int64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 58 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal::decodeWith)
                    (msg.mapUint32Uint32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 59 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal::decodeWith)
                    (msg.mapUint64Uint64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 60 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal::decodeWith)
                    (msg.mapSint32Sint32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 61 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal::decodeWith)
                    (msg.mapSint64Sint64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 62 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal::decodeWith)
                    (msg.mapFixed32Fixed32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 63 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal::decodeWith)
                    (msg.mapFixed64Fixed64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 64 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal::decodeWith)
                    (msg.mapSfixed32Sfixed32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 65 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal::decodeWith)
                    (msg.mapSfixed64Sfixed64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 104 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal::decodeWith)
                    (msg.mapInt32Bool as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 66 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal::decodeWith)
                    (msg.mapInt32Float as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 67 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal::decodeWith)
                    (msg.mapInt32Double as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 103 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal::decodeWith)
                    (msg.mapInt32NestedMessage as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 68 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal::decodeWith)
                    (msg.mapBoolBool as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 69 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal::decodeWith)
                    (msg.mapStringString as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 70 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal::decodeWith)
                    (msg.mapStringBytes as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 71 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal::decodeWith)
                    (msg.mapStringNestedMessage as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 72 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal::decodeWith)
                    (msg.mapStringForeignMessage as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 73 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal::decodeWith)
                    (msg.mapStringNestedEnum as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 74 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal::decodeWith)
                    (msg.mapStringForeignEnum as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 201 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.START_GROUP -> { 
                if (!msg.presenceMask[22]) { 
                    msg.data = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal()
                }

                com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal.decodeWith(msg.data.asInternal(), decoder, tag)
            }
            tag.fieldNr == 204 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.START_GROUP -> { 
                if (!msg.presenceMask[23]) { 
                    msg.multiwordgroupfield = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal()
                }

                com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal.decodeWith(msg.multiwordgroupfield.asInternal(), decoder, tag)
            }
            tag.fieldNr == 241 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultInt32 = decoder.readInt32()
            }
            tag.fieldNr == 242 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultInt64 = decoder.readInt64()
            }
            tag.fieldNr == 243 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultUint32 = decoder.readUInt32()
            }
            tag.fieldNr == 244 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultUint64 = decoder.readUInt64()
            }
            tag.fieldNr == 245 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultSint32 = decoder.readSInt32()
            }
            tag.fieldNr == 246 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultSint64 = decoder.readSInt64()
            }
            tag.fieldNr == 247 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.defaultFixed32 = decoder.readFixed32()
            }
            tag.fieldNr == 248 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.defaultFixed64 = decoder.readFixed64()
            }
            tag.fieldNr == 249 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.defaultSfixed32 = decoder.readSFixed32()
            }
            tag.fieldNr == 250 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.defaultSfixed64 = decoder.readSFixed64()
            }
            tag.fieldNr == 251 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.defaultFloat = decoder.readFloat()
            }
            tag.fieldNr == 252 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.defaultDouble = decoder.readDouble()
            }
            tag.fieldNr == 253 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultBool = decoder.readBool()
            }
            tag.fieldNr == 254 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.defaultString = decoder.readString()
            }
            tag.fieldNr == 255 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.defaultBytes = decoder.readBytes()
            }
            tag.fieldNr == 401 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.fieldname1 = decoder.readInt32()
            }
            tag.fieldNr == 402 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.fieldName2 = decoder.readInt32()
            }
            tag.fieldNr == 403 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.FieldName3 = decoder.readInt32()
            }
            tag.fieldNr == 404 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.field_Name4_ = decoder.readInt32()
            }
            tag.fieldNr == 405 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.field0name5 = decoder.readInt32()
            }
            tag.fieldNr == 406 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.field_0Name6 = decoder.readInt32()
            }
            tag.fieldNr == 407 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.fieldName7 = decoder.readInt32()
            }
            tag.fieldNr == 408 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.FieldName8 = decoder.readInt32()
            }
            tag.fieldNr == 409 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.field_Name9 = decoder.readInt32()
            }
            tag.fieldNr == 410 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.Field_Name10 = decoder.readInt32()
            }
            tag.fieldNr == 411 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.FIELD_NAME11 = decoder.readInt32()
            }
            tag.fieldNr == 412 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.FIELDName12 = decoder.readInt32()
            }
            tag.fieldNr == 413 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg._FieldName13 = decoder.readInt32()
            }
            tag.fieldNr == 414 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.__FieldName14 = decoder.readInt32()
            }
            tag.fieldNr == 415 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.field_Name15 = decoder.readInt32()
            }
            tag.fieldNr == 416 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.field__Name16 = decoder.readInt32()
            }
            tag.fieldNr == 417 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.fieldName17__ = decoder.readInt32()
            }
            tag.fieldNr == 418 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.FieldName18__ = decoder.readInt32()
            }
            tag.fieldNr == 500 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[57]) { 
                    msg.messageSetCorrect = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal()
                }

                decoder.readMessage(msg.messageSetCorrect.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal::decodeWith)
            }
            tag.fieldNr == 111 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint32(decoder.readUInt32())
            }
            tag.fieldNr == 112 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.oneofField as? com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofNestedMessage) ?: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofNestedMessage(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal()).also { 
                    msg.oneofField = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal::decodeWith)
            }
            tag.fieldNr == 113 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofString(decoder.readString())
            }
            tag.fieldNr == 114 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBytes(decoder.readBytes())
            }
            tag.fieldNr == 115 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBool(decoder.readBool())
            }
            tag.fieldNr == 116 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint64(decoder.readUInt64())
            }
            tag.fieldNr == 117 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofFloat(decoder.readFloat())
            }
            tag.fieldNr == 118 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofDouble(decoder.readDouble())
            }
            tag.fieldNr == 119 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofEnum(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum()))
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.computeSize(): Int { 
    var __result = 0
    optionalInt32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    optionalInt64?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(it))
    }

    optionalUint32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(it))
    }

    optionalUint64?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(it))
    }

    optionalSint32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt32(it))
    }

    optionalSint64?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt64(it))
    }

    optionalFixed32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(7, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.fixed32(it))
    }

    optionalFixed64?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(8, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.fixed64(it))
    }

    optionalSfixed32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(9, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.sFixed32(it))
    }

    optionalSfixed64?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(10, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.sFixed64(it))
    }

    optionalFloat?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(11, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(it))
    }

    optionalDouble?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(12, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(it))
    }

    optionalBool?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(13, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(it))
    }

    optionalString?.also { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(it).let { kotlinx.rpc.protobuf.internal.WireSize.tag(14, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    optionalBytes?.also { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(it).let { kotlinx.rpc.protobuf.internal.WireSize.tag(15, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[15]) { 
        __result += optionalNestedMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(18, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[16]) { 
        __result += optionalForeignMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(19, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    optionalNestedEnum?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(21, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(it.number))
    }

    optionalForeignEnum?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(22, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(it.number))
    }

    optionalStringPiece?.also { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(it).let { kotlinx.rpc.protobuf.internal.WireSize.tag(24, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    optionalCord?.also { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(it).let { kotlinx.rpc.protobuf.internal.WireSize.tag(25, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[21]) { 
        __result += recursiveMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(27, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedInt32.isNotEmpty()) { 
        __result += repeatedInt32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.int32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(31, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (repeatedInt64.isNotEmpty()) { 
        __result += repeatedInt64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.int64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(32, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (repeatedUint32.isNotEmpty()) { 
        __result += repeatedUint32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.uInt32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(33, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (repeatedUint64.isNotEmpty()) { 
        __result += repeatedUint64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.uInt64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(34, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (repeatedSint32.isNotEmpty()) { 
        __result += repeatedSint32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.sInt32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(35, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (repeatedSint64.isNotEmpty()) { 
        __result += repeatedSint64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.sInt64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(36, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (repeatedFixed32.isNotEmpty()) { 
        __result += repeatedFixed32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.fixed32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(37, kotlinx.rpc.protobuf.internal.WireType.FIXED32) }
    }

    if (repeatedFixed64.isNotEmpty()) { 
        __result += repeatedFixed64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.fixed64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(38, kotlinx.rpc.protobuf.internal.WireType.FIXED64) }
    }

    if (repeatedSfixed32.isNotEmpty()) { 
        __result += repeatedSfixed32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.sFixed32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(39, kotlinx.rpc.protobuf.internal.WireType.FIXED32) }
    }

    if (repeatedSfixed64.isNotEmpty()) { 
        __result += repeatedSfixed64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.sFixed64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(40, kotlinx.rpc.protobuf.internal.WireType.FIXED64) }
    }

    if (repeatedFloat.isNotEmpty()) { 
        __result += repeatedFloat.sumOf { kotlinx.rpc.protobuf.internal.WireSize.float(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(41, kotlinx.rpc.protobuf.internal.WireType.FIXED32) }
    }

    if (repeatedDouble.isNotEmpty()) { 
        __result += repeatedDouble.sumOf { kotlinx.rpc.protobuf.internal.WireSize.double(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(42, kotlinx.rpc.protobuf.internal.WireType.FIXED64) }
    }

    if (repeatedBool.isNotEmpty()) { 
        __result += repeatedBool.sumOf { kotlinx.rpc.protobuf.internal.WireSize.bool(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(43, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (repeatedString.isNotEmpty()) { 
        __result += repeatedString.sumOf { kotlinx.rpc.protobuf.internal.WireSize.string(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(44, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedBytes.isNotEmpty()) { 
        __result += repeatedBytes.sumOf { kotlinx.rpc.protobuf.internal.WireSize.bytes(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(45, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedNestedMessage.isNotEmpty()) { 
        __result += repeatedNestedMessage.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(48, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedForeignMessage.isNotEmpty()) { 
        __result += repeatedForeignMessage.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(49, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedNestedEnum.isNotEmpty()) { 
        __result += repeatedNestedEnum.sumOf { kotlinx.rpc.protobuf.internal.WireSize.enum(it.number) + kotlinx.rpc.protobuf.internal.WireSize.tag(51, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (repeatedForeignEnum.isNotEmpty()) { 
        __result += repeatedForeignEnum.sumOf { kotlinx.rpc.protobuf.internal.WireSize.enum(it.number) + kotlinx.rpc.protobuf.internal.WireSize.tag(52, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (repeatedStringPiece.isNotEmpty()) { 
        __result += repeatedStringPiece.sumOf { kotlinx.rpc.protobuf.internal.WireSize.string(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(54, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedCord.isNotEmpty()) { 
        __result += repeatedCord.sumOf { kotlinx.rpc.protobuf.internal.WireSize.string(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(55, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (packedInt32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedInt32(packedInt32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(75, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedInt64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedInt64(packedInt64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(76, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedUint32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedUInt32(packedUint32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(77, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedUint64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedUInt64(packedUint64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(78, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedSint32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedSInt32(packedSint32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(79, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedSint64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedSInt64(packedSint64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(80, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedFixed32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedFixed32(packedFixed32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(81, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedFixed64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedFixed64(packedFixed64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(82, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedSfixed32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedSFixed32(packedSfixed32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(83, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedSfixed64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedSFixed64(packedSfixed64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(84, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedFloat.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedFloat(packedFloat).let { kotlinx.rpc.protobuf.internal.WireSize.tag(85, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedDouble.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedDouble(packedDouble).let { kotlinx.rpc.protobuf.internal.WireSize.tag(86, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedBool.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedBool(packedBool).let { kotlinx.rpc.protobuf.internal.WireSize.tag(87, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (packedNestedEnum.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedEnum(packedNestedEnum.map { it.number }).let { kotlinx.rpc.protobuf.internal.WireSize.tag(88, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (unpackedInt32.isNotEmpty()) { 
        __result += unpackedInt32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.int32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(89, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (unpackedInt64.isNotEmpty()) { 
        __result += unpackedInt64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.int64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(90, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (unpackedUint32.isNotEmpty()) { 
        __result += unpackedUint32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.uInt32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(91, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (unpackedUint64.isNotEmpty()) { 
        __result += unpackedUint64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.uInt64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(92, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (unpackedSint32.isNotEmpty()) { 
        __result += unpackedSint32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.sInt32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(93, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (unpackedSint64.isNotEmpty()) { 
        __result += unpackedSint64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.sInt64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(94, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (unpackedFixed32.isNotEmpty()) { 
        __result += unpackedFixed32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.fixed32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(95, kotlinx.rpc.protobuf.internal.WireType.FIXED32) }
    }

    if (unpackedFixed64.isNotEmpty()) { 
        __result += unpackedFixed64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.fixed64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(96, kotlinx.rpc.protobuf.internal.WireType.FIXED64) }
    }

    if (unpackedSfixed32.isNotEmpty()) { 
        __result += unpackedSfixed32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.sFixed32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(97, kotlinx.rpc.protobuf.internal.WireType.FIXED32) }
    }

    if (unpackedSfixed64.isNotEmpty()) { 
        __result += unpackedSfixed64.sumOf { kotlinx.rpc.protobuf.internal.WireSize.sFixed64(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(98, kotlinx.rpc.protobuf.internal.WireType.FIXED64) }
    }

    if (unpackedFloat.isNotEmpty()) { 
        __result += unpackedFloat.sumOf { kotlinx.rpc.protobuf.internal.WireSize.float(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(99, kotlinx.rpc.protobuf.internal.WireType.FIXED32) }
    }

    if (unpackedDouble.isNotEmpty()) { 
        __result += unpackedDouble.sumOf { kotlinx.rpc.protobuf.internal.WireSize.double(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(100, kotlinx.rpc.protobuf.internal.WireType.FIXED64) }
    }

    if (unpackedBool.isNotEmpty()) { 
        __result += unpackedBool.sumOf { kotlinx.rpc.protobuf.internal.WireSize.bool(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(101, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (unpackedNestedEnum.isNotEmpty()) { 
        __result += unpackedNestedEnum.sumOf { kotlinx.rpc.protobuf.internal.WireSize.enum(it.number) + kotlinx.rpc.protobuf.internal.WireSize.tag(102, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    if (mapInt32Int32.isNotEmpty()) { 
        __result += mapInt32Int32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt64Int64.isNotEmpty()) { 
        __result += mapInt64Int64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapUint32Uint32.isNotEmpty()) { 
        __result += mapUint32Uint32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapUint64Uint64.isNotEmpty()) { 
        __result += mapUint64Uint64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSint32Sint32.isNotEmpty()) { 
        __result += mapSint32Sint32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSint64Sint64.isNotEmpty()) { 
        __result += mapSint64Sint64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapFixed32Fixed32.isNotEmpty()) { 
        __result += mapFixed32Fixed32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapFixed64Fixed64.isNotEmpty()) { 
        __result += mapFixed64Fixed64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSfixed32Sfixed32.isNotEmpty()) { 
        __result += mapSfixed32Sfixed32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSfixed64Sfixed64.isNotEmpty()) { 
        __result += mapSfixed64Sfixed64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt32Bool.isNotEmpty()) { 
        __result += mapInt32Bool.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt32Float.isNotEmpty()) { 
        __result += mapInt32Float.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt32Double.isNotEmpty()) { 
        __result += mapInt32Double.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt32NestedMessage.isNotEmpty()) { 
        __result += mapInt32NestedMessage.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapBoolBool.isNotEmpty()) { 
        __result += mapBoolBool.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringString.isNotEmpty()) { 
        __result += mapStringString.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringBytes.isNotEmpty()) { 
        __result += mapStringBytes.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringNestedMessage.isNotEmpty()) { 
        __result += mapStringNestedMessage.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringForeignMessage.isNotEmpty()) { 
        __result += mapStringForeignMessage.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringNestedEnum.isNotEmpty()) { 
        __result += mapStringNestedEnum.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringForeignEnum.isNotEmpty()) { 
        __result += mapStringForeignEnum.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (presenceMask[22]) { 
        __result += data.asInternal()._size.let { (2 * kotlinx.rpc.protobuf.internal.WireSize.tag(201, kotlinx.rpc.protobuf.internal.WireType.START_GROUP)) + it }
    }

    if (presenceMask[23]) { 
        __result += multiwordgroupfield.asInternal()._size.let { (2 * kotlinx.rpc.protobuf.internal.WireSize.tag(204, kotlinx.rpc.protobuf.internal.WireType.START_GROUP)) + it }
    }

    if (presenceMask[24]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(241, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(defaultInt32))
    }

    if (presenceMask[25]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(242, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(defaultInt64))
    }

    if (presenceMask[26]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(243, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(defaultUint32))
    }

    if (presenceMask[27]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(244, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(defaultUint64))
    }

    if (presenceMask[28]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(245, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt32(defaultSint32))
    }

    if (presenceMask[29]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(246, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt64(defaultSint64))
    }

    if (presenceMask[30]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(247, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.fixed32(defaultFixed32))
    }

    if (presenceMask[31]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(248, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.fixed64(defaultFixed64))
    }

    if (presenceMask[32]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(249, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.sFixed32(defaultSfixed32))
    }

    if (presenceMask[33]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(250, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.sFixed64(defaultSfixed64))
    }

    if (presenceMask[34]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(251, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(defaultFloat))
    }

    if (presenceMask[35]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(252, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(defaultDouble))
    }

    if (presenceMask[36]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(253, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(defaultBool))
    }

    if (presenceMask[37]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(defaultString).let { kotlinx.rpc.protobuf.internal.WireSize.tag(254, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[38]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(defaultBytes).let { kotlinx.rpc.protobuf.internal.WireSize.tag(255, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    fieldname1?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(401, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    fieldName2?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(402, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    FieldName3?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(403, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    field_Name4_?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(404, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    field0name5?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(405, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    field_0Name6?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(406, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    fieldName7?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(407, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    FieldName8?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(408, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    field_Name9?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(409, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    Field_Name10?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(410, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    FIELD_NAME11?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(411, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    FIELDName12?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(412, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    _FieldName13?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(413, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    __FieldName14?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(414, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    field_Name15?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(415, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    field__Name16?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(416, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    fieldName17__?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(417, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    FieldName18__?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(418, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    if (presenceMask[57]) { 
        __result += messageSetCorrect.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(500, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    oneofField?.also { 
        when (val value = it) { 
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint32 -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(111, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(value.value))
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofNestedMessage -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(112, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofString -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(113, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBytes -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(114, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofBool -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(115, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(value.value))
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofUint64 -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(116, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(value.value))
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofFloat -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(117, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(value.value))
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofDouble -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(118, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(value.value))
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField.OneofEnum -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(119, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(value.value.number))
            }
        }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    c?.also { 
        encoder.writeInt32(fieldNr = 1, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.c = decoder.readInt32()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal.computeSize(): Int { 
    var __result = 0
    c?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2.asInternal(): com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    groupInt32?.also { 
        encoder.writeInt32(fieldNr = 122, value = it)
    }

    groupUint32?.also { 
        encoder.writeUInt32(fieldNr = 123, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 122 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.groupInt32 = decoder.readInt32()
            }
            tag.fieldNr == 123 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.groupUint32 = decoder.readUInt32()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal.computeSize(): Int { 
    var __result = 0
    groupInt32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(122, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    groupUint32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(123, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(it))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.GroupField.asInternal(): com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[2]) { 
        nestedMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[3]) { 
        optionalgroup.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    optionalInt32?.also { 
        encoder.writeInt32(fieldNr = 1001, value = it)
    }

    optionalString?.also { 
        encoder.writeString(fieldNr = 1002, value = it)
    }

    if (presenceMask[2]) { 
        encoder.writeMessage(fieldNr = 1003, value = nestedMessage.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[3]) { 
        encoder.writeGroupMessage(fieldNr = 1004, value = optionalgroup.asInternal()) { encodeWith(it) }
    }

    optionalBool?.also { 
        encoder.writeBool(fieldNr = 1006, value = it)
    }

    if (repeatedInt32.isNotEmpty()) { 
        repeatedInt32.forEach { 
            encoder.writeInt32(1011, it)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1001 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalInt32 = decoder.readInt32()
            }
            tag.fieldNr == 1002 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.optionalString = decoder.readString()
            }
            tag.fieldNr == 1003 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[2]) { 
                    msg.nestedMessage = com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal()
                }

                decoder.readMessage(msg.nestedMessage.asInternal(), com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal::decodeWith)
            }
            tag.fieldNr == 1004 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.START_GROUP -> { 
                if (!msg.presenceMask[3]) { 
                    msg.optionalgroup = com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal()
                }

                com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal.decodeWith(msg.optionalgroup.asInternal(), decoder, tag)
            }
            tag.fieldNr == 1006 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalBool = decoder.readBool()
            }
            tag.fieldNr == 1011 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedInt32 += decoder.readPackedInt32()
            }
            tag.fieldNr == 1011 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = decoder.readInt32()
                (msg.repeatedInt32 as MutableList).add(elem)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.computeSize(): Int { 
    var __result = 0
    optionalInt32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1001, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    optionalString?.also { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(it).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1002, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[2]) { 
        __result += nestedMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(1003, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[3]) { 
        __result += optionalgroup.asInternal()._size.let { (2 * kotlinx.rpc.protobuf.internal.WireSize.tag(1004, kotlinx.rpc.protobuf.internal.WireType.START_GROUP)) + it }
    }

    optionalBool?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1006, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(it))
    }

    if (repeatedInt32.isNotEmpty()) { 
        __result += repeatedInt32.sumOf { kotlinx.rpc.protobuf.internal.WireSize.int32(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(1011, kotlinx.rpc.protobuf.internal.WireType.VARINT) }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.asInternal(): com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2.asInternal(): com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2.asInternal(): com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    data?.also { 
        encoder.writeString(fieldNr = 1, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.data = decoder.readString()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal.computeSize(): Int { 
    var __result = 0
    data?.also { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(it).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.OneStringProto2.asInternal(): com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    inline?.also { 
        encoder.writeInt32(fieldNr = 1, value = it)
    }

    concept?.also { 
        encoder.writeString(fieldNr = 2, value = it)
    }

    if (requires.isNotEmpty()) { 
        requires.forEach { 
            encoder.writeString(3, it)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.inline = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.concept = decoder.readString()
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = decoder.readString()
                (msg.requires as MutableList).add(elem)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal.computeSize(): Int { 
    var __result = 0
    inline?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    concept?.also { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(it).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (requires.isNotEmpty()) { 
        __result += requires.sumOf { kotlinx.rpc.protobuf.internal.WireSize.string(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords.asInternal(): com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.checkRequiredFields() { 
    if (!presenceMask[0]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredInt32")
    }

    if (!presenceMask[1]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredInt64")
    }

    if (!presenceMask[2]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredUint32")
    }

    if (!presenceMask[3]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredUint64")
    }

    if (!presenceMask[4]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredSint32")
    }

    if (!presenceMask[5]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredSint64")
    }

    if (!presenceMask[6]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredFixed32")
    }

    if (!presenceMask[7]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredFixed64")
    }

    if (!presenceMask[8]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredSfixed32")
    }

    if (!presenceMask[9]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredSfixed64")
    }

    if (!presenceMask[10]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredFloat")
    }

    if (!presenceMask[11]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredDouble")
    }

    if (!presenceMask[12]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredBool")
    }

    if (!presenceMask[13]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredString")
    }

    if (!presenceMask[14]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredBytes")
    }

    if (!presenceMask[15]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredNestedMessage")
    }

    if (!presenceMask[16]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredForeignMessage")
    }

    if (!presenceMask[17]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredNestedEnum")
    }

    if (!presenceMask[18]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredForeignEnum")
    }

    if (!presenceMask[19]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredStringPiece")
    }

    if (!presenceMask[20]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredCord")
    }

    if (!presenceMask[21]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "recursiveMessage")
    }

    if (!presenceMask[23]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "data")
    }

    if (!presenceMask[24]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultInt32")
    }

    if (!presenceMask[25]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultInt64")
    }

    if (!presenceMask[26]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultUint32")
    }

    if (!presenceMask[27]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultUint64")
    }

    if (!presenceMask[28]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultSint32")
    }

    if (!presenceMask[29]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultSint64")
    }

    if (!presenceMask[30]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultFixed32")
    }

    if (!presenceMask[31]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultFixed64")
    }

    if (!presenceMask[32]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultSfixed32")
    }

    if (!presenceMask[33]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultSfixed64")
    }

    if (!presenceMask[34]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultFloat")
    }

    if (!presenceMask[35]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultDouble")
    }

    if (!presenceMask[36]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultBool")
    }

    if (!presenceMask[37]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultString")
    }

    if (!presenceMask[38]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultBytes")
    }

    if (presenceMask[15]) { 
        requiredNestedMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[16]) { 
        requiredForeignMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[21]) { 
        recursiveMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[22]) { 
        optionalRecursiveMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[23]) { 
        data.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = requiredInt32)
    }

    if (presenceMask[1]) { 
        encoder.writeInt64(fieldNr = 2, value = requiredInt64)
    }

    if (presenceMask[2]) { 
        encoder.writeUInt32(fieldNr = 3, value = requiredUint32)
    }

    if (presenceMask[3]) { 
        encoder.writeUInt64(fieldNr = 4, value = requiredUint64)
    }

    if (presenceMask[4]) { 
        encoder.writeSInt32(fieldNr = 5, value = requiredSint32)
    }

    if (presenceMask[5]) { 
        encoder.writeSInt64(fieldNr = 6, value = requiredSint64)
    }

    if (presenceMask[6]) { 
        encoder.writeFixed32(fieldNr = 7, value = requiredFixed32)
    }

    if (presenceMask[7]) { 
        encoder.writeFixed64(fieldNr = 8, value = requiredFixed64)
    }

    if (presenceMask[8]) { 
        encoder.writeSFixed32(fieldNr = 9, value = requiredSfixed32)
    }

    if (presenceMask[9]) { 
        encoder.writeSFixed64(fieldNr = 10, value = requiredSfixed64)
    }

    if (presenceMask[10]) { 
        encoder.writeFloat(fieldNr = 11, value = requiredFloat)
    }

    if (presenceMask[11]) { 
        encoder.writeDouble(fieldNr = 12, value = requiredDouble)
    }

    if (presenceMask[12]) { 
        encoder.writeBool(fieldNr = 13, value = requiredBool)
    }

    if (presenceMask[13]) { 
        encoder.writeString(fieldNr = 14, value = requiredString)
    }

    if (presenceMask[14]) { 
        encoder.writeBytes(fieldNr = 15, value = requiredBytes)
    }

    if (presenceMask[15]) { 
        encoder.writeMessage(fieldNr = 18, value = requiredNestedMessage.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[16]) { 
        encoder.writeMessage(fieldNr = 19, value = requiredForeignMessage.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[17]) { 
        encoder.writeEnum(fieldNr = 21, value = requiredNestedEnum.number)
    }

    if (presenceMask[18]) { 
        encoder.writeEnum(fieldNr = 22, value = requiredForeignEnum.number)
    }

    if (presenceMask[19]) { 
        encoder.writeString(fieldNr = 24, value = requiredStringPiece)
    }

    if (presenceMask[20]) { 
        encoder.writeString(fieldNr = 25, value = requiredCord)
    }

    if (presenceMask[21]) { 
        encoder.writeMessage(fieldNr = 27, value = recursiveMessage.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[22]) { 
        encoder.writeMessage(fieldNr = 28, value = optionalRecursiveMessage.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[23]) { 
        encoder.writeGroupMessage(fieldNr = 201, value = data.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[24]) { 
        encoder.writeInt32(fieldNr = 241, value = defaultInt32)
    }

    if (presenceMask[25]) { 
        encoder.writeInt64(fieldNr = 242, value = defaultInt64)
    }

    if (presenceMask[26]) { 
        encoder.writeUInt32(fieldNr = 243, value = defaultUint32)
    }

    if (presenceMask[27]) { 
        encoder.writeUInt64(fieldNr = 244, value = defaultUint64)
    }

    if (presenceMask[28]) { 
        encoder.writeSInt32(fieldNr = 245, value = defaultSint32)
    }

    if (presenceMask[29]) { 
        encoder.writeSInt64(fieldNr = 246, value = defaultSint64)
    }

    if (presenceMask[30]) { 
        encoder.writeFixed32(fieldNr = 247, value = defaultFixed32)
    }

    if (presenceMask[31]) { 
        encoder.writeFixed64(fieldNr = 248, value = defaultFixed64)
    }

    if (presenceMask[32]) { 
        encoder.writeSFixed32(fieldNr = 249, value = defaultSfixed32)
    }

    if (presenceMask[33]) { 
        encoder.writeSFixed64(fieldNr = 250, value = defaultSfixed64)
    }

    if (presenceMask[34]) { 
        encoder.writeFloat(fieldNr = 251, value = defaultFloat)
    }

    if (presenceMask[35]) { 
        encoder.writeDouble(fieldNr = 252, value = defaultDouble)
    }

    if (presenceMask[36]) { 
        encoder.writeBool(fieldNr = 253, value = defaultBool)
    }

    if (presenceMask[37]) { 
        encoder.writeString(fieldNr = 254, value = defaultString)
    }

    if (presenceMask[38]) { 
        encoder.writeBytes(fieldNr = 255, value = defaultBytes)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requiredInt32 = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requiredInt64 = decoder.readInt64()
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requiredUint32 = decoder.readUInt32()
            }
            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requiredUint64 = decoder.readUInt64()
            }
            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requiredSint32 = decoder.readSInt32()
            }
            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requiredSint64 = decoder.readSInt64()
            }
            tag.fieldNr == 7 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.requiredFixed32 = decoder.readFixed32()
            }
            tag.fieldNr == 8 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.requiredFixed64 = decoder.readFixed64()
            }
            tag.fieldNr == 9 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.requiredSfixed32 = decoder.readSFixed32()
            }
            tag.fieldNr == 10 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.requiredSfixed64 = decoder.readSFixed64()
            }
            tag.fieldNr == 11 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.requiredFloat = decoder.readFloat()
            }
            tag.fieldNr == 12 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.requiredDouble = decoder.readDouble()
            }
            tag.fieldNr == 13 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requiredBool = decoder.readBool()
            }
            tag.fieldNr == 14 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.requiredString = decoder.readString()
            }
            tag.fieldNr == 15 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.requiredBytes = decoder.readBytes()
            }
            tag.fieldNr == 18 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[15]) { 
                    msg.requiredNestedMessage = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.requiredNestedMessage.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal::decodeWith)
            }
            tag.fieldNr == 19 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[16]) { 
                    msg.requiredForeignMessage = com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal()
                }

                decoder.readMessage(msg.requiredForeignMessage.asInternal(), com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal::decodeWith)
            }
            tag.fieldNr == 21 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requiredNestedEnum = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 22 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requiredForeignEnum = com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 24 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.requiredStringPiece = decoder.readString()
            }
            tag.fieldNr == 25 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.requiredCord = decoder.readString()
            }
            tag.fieldNr == 27 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[21]) { 
                    msg.recursiveMessage = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal()
                }

                decoder.readMessage(msg.recursiveMessage.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal::decodeWith)
            }
            tag.fieldNr == 28 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[22]) { 
                    msg.optionalRecursiveMessage = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal()
                }

                decoder.readMessage(msg.optionalRecursiveMessage.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal::decodeWith)
            }
            tag.fieldNr == 201 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.START_GROUP -> { 
                if (!msg.presenceMask[23]) { 
                    msg.data = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal()
                }

                com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal.decodeWith(msg.data.asInternal(), decoder, tag)
            }
            tag.fieldNr == 241 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultInt32 = decoder.readInt32()
            }
            tag.fieldNr == 242 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultInt64 = decoder.readInt64()
            }
            tag.fieldNr == 243 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultUint32 = decoder.readUInt32()
            }
            tag.fieldNr == 244 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultUint64 = decoder.readUInt64()
            }
            tag.fieldNr == 245 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultSint32 = decoder.readSInt32()
            }
            tag.fieldNr == 246 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultSint64 = decoder.readSInt64()
            }
            tag.fieldNr == 247 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.defaultFixed32 = decoder.readFixed32()
            }
            tag.fieldNr == 248 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.defaultFixed64 = decoder.readFixed64()
            }
            tag.fieldNr == 249 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.defaultSfixed32 = decoder.readSFixed32()
            }
            tag.fieldNr == 250 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.defaultSfixed64 = decoder.readSFixed64()
            }
            tag.fieldNr == 251 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.defaultFloat = decoder.readFloat()
            }
            tag.fieldNr == 252 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.defaultDouble = decoder.readDouble()
            }
            tag.fieldNr == 253 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.defaultBool = decoder.readBool()
            }
            tag.fieldNr == 254 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.defaultString = decoder.readString()
            }
            tag.fieldNr == 255 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.defaultBytes = decoder.readBytes()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(requiredInt32))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(requiredInt64))
    }

    if (presenceMask[2]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(requiredUint32))
    }

    if (presenceMask[3]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(requiredUint64))
    }

    if (presenceMask[4]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt32(requiredSint32))
    }

    if (presenceMask[5]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt64(requiredSint64))
    }

    if (presenceMask[6]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(7, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.fixed32(requiredFixed32))
    }

    if (presenceMask[7]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(8, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.fixed64(requiredFixed64))
    }

    if (presenceMask[8]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(9, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.sFixed32(requiredSfixed32))
    }

    if (presenceMask[9]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(10, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.sFixed64(requiredSfixed64))
    }

    if (presenceMask[10]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(11, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(requiredFloat))
    }

    if (presenceMask[11]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(12, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(requiredDouble))
    }

    if (presenceMask[12]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(13, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(requiredBool))
    }

    if (presenceMask[13]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(requiredString).let { kotlinx.rpc.protobuf.internal.WireSize.tag(14, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[14]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(requiredBytes).let { kotlinx.rpc.protobuf.internal.WireSize.tag(15, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[15]) { 
        __result += requiredNestedMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(18, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[16]) { 
        __result += requiredForeignMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(19, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[17]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(21, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(requiredNestedEnum.number))
    }

    if (presenceMask[18]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(22, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(requiredForeignEnum.number))
    }

    if (presenceMask[19]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(requiredStringPiece).let { kotlinx.rpc.protobuf.internal.WireSize.tag(24, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[20]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(requiredCord).let { kotlinx.rpc.protobuf.internal.WireSize.tag(25, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[21]) { 
        __result += recursiveMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(27, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[22]) { 
        __result += optionalRecursiveMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(28, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[23]) { 
        __result += data.asInternal()._size.let { (2 * kotlinx.rpc.protobuf.internal.WireSize.tag(201, kotlinx.rpc.protobuf.internal.WireType.START_GROUP)) + it }
    }

    if (presenceMask[24]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(241, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(defaultInt32))
    }

    if (presenceMask[25]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(242, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(defaultInt64))
    }

    if (presenceMask[26]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(243, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(defaultUint32))
    }

    if (presenceMask[27]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(244, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(defaultUint64))
    }

    if (presenceMask[28]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(245, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt32(defaultSint32))
    }

    if (presenceMask[29]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(246, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt64(defaultSint64))
    }

    if (presenceMask[30]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(247, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.fixed32(defaultFixed32))
    }

    if (presenceMask[31]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(248, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.fixed64(defaultFixed64))
    }

    if (presenceMask[32]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(249, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.sFixed32(defaultSfixed32))
    }

    if (presenceMask[33]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(250, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.sFixed64(defaultSfixed64))
    }

    if (presenceMask[34]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(251, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(defaultFloat))
    }

    if (presenceMask[35]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(252, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(defaultDouble))
    }

    if (presenceMask[36]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(253, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(defaultBool))
    }

    if (presenceMask[37]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(defaultString).let { kotlinx.rpc.protobuf.internal.WireSize.tag(254, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[38]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(defaultBytes).let { kotlinx.rpc.protobuf.internal.WireSize.tag(255, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.checkRequiredFields() { 
    // no required fields to check
    largeOneof?.also { 
        when { 
            it is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A1 -> { 
                it.value.asInternal().checkRequiredFields()
            }
            it is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A2 -> { 
                it.value.asInternal().checkRequiredFields()
            }
            it is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A3 -> { 
                it.value.asInternal().checkRequiredFields()
            }
            it is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A4 -> { 
                it.value.asInternal().checkRequiredFields()
            }
            it is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A5 -> { 
                it.value.asInternal().checkRequiredFields()
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    largeOneof?.also { 
        when (val value = it) { 
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A1 -> { 
                encoder.writeMessage(fieldNr = 1, value = value.value.asInternal()) { encodeWith(it) }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A2 -> { 
                encoder.writeMessage(fieldNr = 2, value = value.value.asInternal()) { encodeWith(it) }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A3 -> { 
                encoder.writeMessage(fieldNr = 3, value = value.value.asInternal()) { encodeWith(it) }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A4 -> { 
                encoder.writeMessage(fieldNr = 4, value = value.value.asInternal()) { encodeWith(it) }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A5 -> { 
                encoder.writeMessage(fieldNr = 5, value = value.value.asInternal()) { encodeWith(it) }
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.largeOneof as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A1) ?: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A1(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal()).also { 
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal::decodeWith)
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.largeOneof as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A2) ?: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A2(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal()).also { 
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal::decodeWith)
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.largeOneof as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A3) ?: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A3(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal()).also { 
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal::decodeWith)
            }
            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.largeOneof as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A4) ?: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A4(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal()).also { 
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal::decodeWith)
            }
            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.largeOneof as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A5) ?: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A5(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal()).also { 
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal::decodeWith)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.computeSize(): Int { 
    var __result = 0
    largeOneof?.also { 
        when (val value = it) { 
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A1 -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A2 -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A3 -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A4 -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
            is com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof.A5 -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
        }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[1]) { 
        corecursive.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    a?.also { 
        encoder.writeInt32(fieldNr = 1, value = it)
    }

    if (presenceMask[1]) { 
        encoder.writeMessage(fieldNr = 2, value = corecursive.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.a = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[1]) { 
                    msg.corecursive = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal()
                }

                decoder.readMessage(msg.corecursive.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal::decodeWith)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal.computeSize(): Int { 
    var __result = 0
    a?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    if (presenceMask[1]) { 
        __result += corecursive.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeInt32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readInt32()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32Int32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeInt64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readInt64()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readInt64()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt64Int64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeUInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeUInt32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readUInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readUInt32()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint32Uint32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeUInt64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeUInt64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readUInt64()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readUInt64()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapUint64Uint64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeSInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeSInt32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readSInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readSInt32()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt32(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint32Sint32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeSInt64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeSInt64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readSInt64()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readSInt64()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt64(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSint64Sint64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeFixed32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeFixed32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.key = decoder.readFixed32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.value = decoder.readFixed32()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.fixed32(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.fixed32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeFixed64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeFixed64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.key = decoder.readFixed64()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.value = decoder.readFixed64()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.fixed64(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.fixed64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeSFixed32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeSFixed32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.key = decoder.readSFixed32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.value = decoder.readSFixed32()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.sFixed32(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.sFixed32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeSFixed64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeSFixed64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.key = decoder.readSFixed64()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.value = decoder.readSFixed64()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.sFixed64(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.sFixed64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeBool(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readBool()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32BoolEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeFloat(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.value = decoder.readFloat()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32FloatEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeDouble(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.value = decoder.readDouble()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32DoubleEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[1]) { 
        value.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[1]) { 
                    msg.value = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.value.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal::decodeWith)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(key))
    }

    if (presenceMask[1]) { 
        __result += value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeBool(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeBool(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.key = decoder.readBool()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readBool()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(key))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapBoolBoolEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeString(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.value = decoder.readString()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[1]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringStringEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeBytes(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.value = decoder.readBytes()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[1]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringBytesEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[1]) { 
        value.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[1]) { 
                    msg.value = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.value.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal::decodeWith)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[1]) { 
        __result += value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[1]) { 
        value.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[1]) { 
                    msg.value = com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal()
                }

                decoder.readMessage(msg.value.asInternal(), com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal::decodeWith)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[1]) { 
        __result += value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeEnum(fieldNr = 2, value = value.number)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(value.number))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeEnum(fieldNr = 2, value = value.number)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.fromNumber(decoder.readEnum())
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(value.number))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    groupInt32?.also { 
        encoder.writeInt32(fieldNr = 202, value = it)
    }

    groupUint32?.also { 
        encoder.writeUInt32(fieldNr = 203, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, startGroup: kotlinx.rpc.protobuf.internal.KTag) { 
    while (true) { 
        val tag = decoder.readTag() ?: throw ProtobufDecodingException("Missing END_GROUP tag for field: ${startGroup.fieldNr}.")
        if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
            if (tag.fieldNr != startGroup.fieldNr) { 
                throw ProtobufDecodingException("Wrong END_GROUP tag. Expected ${startGroup.fieldNr}, got ${tag.fieldNr}.")
            }

            return
        }

        when { 
            tag.fieldNr == 202 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.groupInt32 = decoder.readInt32()
            }
            tag.fieldNr == 203 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.groupUint32 = decoder.readUInt32()
            }
            else -> { 
                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal.computeSize(): Int { 
    var __result = 0
    groupInt32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(202, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    groupUint32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(203, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(it))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    groupInt32?.also { 
        encoder.writeInt32(fieldNr = 205, value = it)
    }

    groupUint32?.also { 
        encoder.writeUInt32(fieldNr = 206, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, startGroup: kotlinx.rpc.protobuf.internal.KTag) { 
    while (true) { 
        val tag = decoder.readTag() ?: throw ProtobufDecodingException("Missing END_GROUP tag for field: ${startGroup.fieldNr}.")
        if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
            if (tag.fieldNr != startGroup.fieldNr) { 
                throw ProtobufDecodingException("Wrong END_GROUP tag. Expected ${startGroup.fieldNr}, got ${tag.fieldNr}.")
            }

            return
        }

        when { 
            tag.fieldNr == 205 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.groupInt32 = decoder.readInt32()
            }
            tag.fieldNr == 206 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.groupUint32 = decoder.readUInt32()
            }
            else -> { 
                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal.computeSize(): Int { 
    var __result = 0
    groupInt32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(205, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    groupUint32?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(206, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(it))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    str?.also { 
        encoder.writeString(fieldNr = 25, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 25 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.str = decoder.readString()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.computeSize(): Int { 
    var __result = 0
    str?.also { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(it).let { kotlinx.rpc.protobuf.internal.WireSize.tag(25, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    i?.also { 
        encoder.writeInt32(fieldNr = 9, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 9 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.i = decoder.readInt32()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.computeSize(): Int { 
    var __result = 0
    i?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(9, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    oneofField?.also { 
        when (val value = it) { 
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.A -> { 
                encoder.writeInt32(fieldNr = 1, value = value.value)
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.B -> { 
                encoder.writeInt32(fieldNr = 2, value = value.value)
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.A(decoder.readInt32())
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.B(decoder.readInt32())
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.computeSize(): Int { 
    var __result = 0
    oneofField?.also { 
        when (val value = it) { 
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.A -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(value.value))
            }
            is com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField.B -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(value.value))
            }
        }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    a?.also { 
        encoder.writeInt32(fieldNr = 1, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, startGroup: kotlinx.rpc.protobuf.internal.KTag) { 
    while (true) { 
        val tag = decoder.readTag() ?: throw ProtobufDecodingException("Missing END_GROUP tag for field: ${startGroup.fieldNr}.")
        if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
            if (tag.fieldNr != startGroup.fieldNr) { 
                throw ProtobufDecodingException("Wrong END_GROUP tag. Expected ${startGroup.fieldNr}, got ${tag.fieldNr}.")
            }

            return
        }

        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.a = decoder.readInt32()
            }
            else -> { 
                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal.computeSize(): Int { 
    var __result = 0
    a?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup.asInternal(): com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.checkRequiredFields() { 
    if (!presenceMask[0]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("NestedMessage", "a")
    }

    if (!presenceMask[1]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("NestedMessage", "corecursive")
    }

    if (presenceMask[1]) { 
        corecursive.asInternal().checkRequiredFields()
    }

    if (presenceMask[2]) { 
        optionalCorecursive.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = a)
    }

    if (presenceMask[1]) { 
        encoder.writeMessage(fieldNr = 2, value = corecursive.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[2]) { 
        encoder.writeMessage(fieldNr = 3, value = optionalCorecursive.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.a = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[1]) { 
                    msg.corecursive = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal()
                }

                decoder.readMessage(msg.corecursive.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal::decodeWith)
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[2]) { 
                    msg.optionalCorecursive = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal()
                }

                decoder.readMessage(msg.optionalCorecursive.asInternal(), com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal::decodeWith)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(a))
    }

    if (presenceMask[1]) { 
        __result += corecursive.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[2]) { 
        __result += optionalCorecursive.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal.checkRequiredFields() { 
    if (!presenceMask[0]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("Data", "groupInt32")
    }

    if (!presenceMask[1]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("Data", "groupUint32")
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 202, value = groupInt32)
    }

    if (presenceMask[1]) { 
        encoder.writeUInt32(fieldNr = 203, value = groupUint32)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, startGroup: kotlinx.rpc.protobuf.internal.KTag) { 
    while (true) { 
        val tag = decoder.readTag() ?: throw ProtobufDecodingException("Missing END_GROUP tag for field: ${startGroup.fieldNr}.")
        if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
            if (tag.fieldNr != startGroup.fieldNr) { 
                throw ProtobufDecodingException("Wrong END_GROUP tag. Expected ${startGroup.fieldNr}, got ${tag.fieldNr}.")
            }

            return
        }

        when { 
            tag.fieldNr == 202 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.groupInt32 = decoder.readInt32()
            }
            tag.fieldNr == 203 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.groupUint32 = decoder.readUInt32()
            }
            else -> { 
                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(202, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(groupInt32))
    }

    if (presenceMask[1]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(203, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(groupUint32))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.checkRequiredFields() { 
    if (!presenceMask[0]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("MessageSetCorrectExtension1", "str")
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 25, value = str)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 25 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.str = decoder.readString()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(str).let { kotlinx.rpc.protobuf.internal.WireSize.tag(25, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.checkRequiredFields() { 
    if (!presenceMask[0]) { 
        throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException.missingRequiredField("MessageSetCorrectExtension2", "i")
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 9, value = i)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 9 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.i = decoder.readInt32()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.computeSize(): Int { 
    var __result = 0
    if (presenceMask[0]) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(9, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(i))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5.asInternal(): com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal { 
    return this as? com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2 { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.FOREIGN_FOO
        }
        1 -> { 
            com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.FOREIGN_BAR
        }
        2 -> { 
            com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.FOREIGN_BAZ
        }
        else -> { 
            com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.FOO
        }
        1 -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.BAR
        }
        2 -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.BAZ
        }
        -1 -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.NEG
        }
        else -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2.Bool.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2.Bool { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2.Bool.kFalse
        }
        1 -> { 
            com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2.Bool.kTrue
        }
        else -> { 
            com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2.Bool.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum.FOO
        }
        1 -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum.BAR
        }
        2 -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum.BAZ
        }
        -1 -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum.NEG
        }
        else -> { 
            com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum.UNRECOGNIZED(number)
        }
    }
}
