@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.editions.proto3

import com.google.protobuf.kotlin.*
import com.google.protobuf.kotlin.AnyInternal
import com.google.protobuf.kotlin.BoolValue
import com.google.protobuf.kotlin.BoolValueInternal
import com.google.protobuf.kotlin.BytesValue
import com.google.protobuf.kotlin.BytesValueInternal
import com.google.protobuf.kotlin.DoubleValue
import com.google.protobuf.kotlin.DoubleValueInternal
import com.google.protobuf.kotlin.Duration
import com.google.protobuf.kotlin.DurationInternal
import com.google.protobuf.kotlin.FieldMask
import com.google.protobuf.kotlin.FieldMaskInternal
import com.google.protobuf.kotlin.FloatValue
import com.google.protobuf.kotlin.FloatValueInternal
import com.google.protobuf.kotlin.Int32Value
import com.google.protobuf.kotlin.Int32ValueInternal
import com.google.protobuf.kotlin.Int64Value
import com.google.protobuf.kotlin.Int64ValueInternal
import com.google.protobuf.kotlin.ListValue
import com.google.protobuf.kotlin.ListValueInternal
import com.google.protobuf.kotlin.NullValue
import com.google.protobuf.kotlin.StringValue
import com.google.protobuf.kotlin.StringValueInternal
import com.google.protobuf.kotlin.Struct
import com.google.protobuf.kotlin.StructInternal
import com.google.protobuf.kotlin.Timestamp
import com.google.protobuf.kotlin.TimestampInternal
import com.google.protobuf.kotlin.UInt32Value
import com.google.protobuf.kotlin.UInt32ValueInternal
import com.google.protobuf.kotlin.UInt64Value
import com.google.protobuf.kotlin.UInt64ValueInternal
import com.google.protobuf.kotlin.Value
import com.google.protobuf.kotlin.ValueInternal
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.bool
import kotlinx.rpc.protobuf.internal.bytes
import kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException
import kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException
import kotlinx.rpc.protobuf.internal.double
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.fixed32
import kotlinx.rpc.protobuf.internal.fixed64
import kotlinx.rpc.protobuf.internal.float
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.int64
import kotlinx.rpc.protobuf.internal.packedBool
import kotlinx.rpc.protobuf.internal.packedDouble
import kotlinx.rpc.protobuf.internal.packedEnum
import kotlinx.rpc.protobuf.internal.packedFixed32
import kotlinx.rpc.protobuf.internal.packedFixed64
import kotlinx.rpc.protobuf.internal.packedFloat
import kotlinx.rpc.protobuf.internal.packedInt32
import kotlinx.rpc.protobuf.internal.packedInt64
import kotlinx.rpc.protobuf.internal.packedSFixed32
import kotlinx.rpc.protobuf.internal.packedSFixed64
import kotlinx.rpc.protobuf.internal.packedSInt32
import kotlinx.rpc.protobuf.internal.packedSInt64
import kotlinx.rpc.protobuf.internal.packedUInt32
import kotlinx.rpc.protobuf.internal.packedUInt64
import kotlinx.rpc.protobuf.internal.sFixed32
import kotlinx.rpc.protobuf.internal.sFixed64
import kotlinx.rpc.protobuf.internal.sInt32
import kotlinx.rpc.protobuf.internal.sInt64
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag
import kotlinx.rpc.protobuf.internal.uInt32
import kotlinx.rpc.protobuf.internal.uInt64

class TestAllTypesProto3Internal: TestAllTypesProto3, InternalMessage(fieldsWithPresence = 18) {
    private object PresenceIndices {
        const val optionalNestedMessage: Int = 0
        const val optionalForeignMessage: Int = 1
        const val recursiveMessage: Int = 2
        const val optionalBoolWrapper: Int = 3
        const val optionalInt32Wrapper: Int = 4
        const val optionalInt64Wrapper: Int = 5
        const val optionalUint32Wrapper: Int = 6
        const val optionalUint64Wrapper: Int = 7
        const val optionalFloatWrapper: Int = 8
        const val optionalDoubleWrapper: Int = 9
        const val optionalStringWrapper: Int = 10
        const val optionalBytesWrapper: Int = 11
        const val optionalDuration: Int = 12
        const val optionalTimestamp: Int = 13
        const val optionalFieldMask: Int = 14
        const val optionalStruct: Int = 15
        const val optionalAny: Int = 16
        const val optionalValue: Int = 17
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var optionalInt32: Int by MsgFieldDelegate { 0 }
    override var optionalInt64: Long by MsgFieldDelegate { 0L }
    override var optionalUint32: UInt by MsgFieldDelegate { 0u }
    override var optionalUint64: ULong by MsgFieldDelegate { 0uL }
    override var optionalSint32: Int by MsgFieldDelegate { 0 }
    override var optionalSint64: Long by MsgFieldDelegate { 0L }
    override var optionalFixed32: UInt by MsgFieldDelegate { 0u }
    override var optionalFixed64: ULong by MsgFieldDelegate { 0uL }
    override var optionalSfixed32: Int by MsgFieldDelegate { 0 }
    override var optionalSfixed64: Long by MsgFieldDelegate { 0L }
    override var optionalFloat: Float by MsgFieldDelegate { 0.0f }
    override var optionalDouble: Double by MsgFieldDelegate { 0.0 }
    override var optionalBool: Boolean by MsgFieldDelegate { false }
    override var optionalString: String by MsgFieldDelegate { "" }
    override var optionalBytes: ByteArray by MsgFieldDelegate { byteArrayOf() }
    override var optionalNestedMessage: TestAllTypesProto3.NestedMessage by MsgFieldDelegate(PresenceIndices.optionalNestedMessage) { NestedMessageInternal() }
    override var optionalForeignMessage: ForeignMessage by MsgFieldDelegate(PresenceIndices.optionalForeignMessage) { ForeignMessageInternal() }
    override var optionalNestedEnum: TestAllTypesProto3.NestedEnum by MsgFieldDelegate { TestAllTypesProto3.NestedEnum.FOO }
    override var optionalForeignEnum: ForeignEnum by MsgFieldDelegate { ForeignEnum.FOREIGN_FOO }
    override var optionalAliasedEnum: TestAllTypesProto3.AliasedEnum by MsgFieldDelegate { TestAllTypesProto3.AliasedEnum.ALIAS_FOO }
    override var optionalStringPiece: String by MsgFieldDelegate { "" }
    override var optionalCord: String by MsgFieldDelegate { "" }
    override var recursiveMessage: TestAllTypesProto3 by MsgFieldDelegate(PresenceIndices.recursiveMessage) { TestAllTypesProto3Internal() }
    override var repeatedInt32: List<Int> by MsgFieldDelegate { mutableListOf() }
    override var repeatedInt64: List<Long> by MsgFieldDelegate { mutableListOf() }
    override var repeatedUint32: List<UInt> by MsgFieldDelegate { mutableListOf() }
    override var repeatedUint64: List<ULong> by MsgFieldDelegate { mutableListOf() }
    override var repeatedSint32: List<Int> by MsgFieldDelegate { mutableListOf() }
    override var repeatedSint64: List<Long> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFixed32: List<UInt> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFixed64: List<ULong> by MsgFieldDelegate { mutableListOf() }
    override var repeatedSfixed32: List<Int> by MsgFieldDelegate { mutableListOf() }
    override var repeatedSfixed64: List<Long> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFloat: List<Float> by MsgFieldDelegate { mutableListOf() }
    override var repeatedDouble: List<Double> by MsgFieldDelegate { mutableListOf() }
    override var repeatedBool: List<Boolean> by MsgFieldDelegate { mutableListOf() }
    override var repeatedString: List<String> by MsgFieldDelegate { mutableListOf() }
    override var repeatedBytes: List<ByteArray> by MsgFieldDelegate { mutableListOf() }
    override var repeatedNestedMessage: List<TestAllTypesProto3.NestedMessage> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignMessage: List<ForeignMessage> by MsgFieldDelegate { mutableListOf() }
    override var repeatedNestedEnum: List<TestAllTypesProto3.NestedEnum> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignEnum: List<ForeignEnum> by MsgFieldDelegate { mutableListOf() }
    override var repeatedStringPiece: List<String> by MsgFieldDelegate { mutableListOf() }
    override var repeatedCord: List<String> by MsgFieldDelegate { mutableListOf() }
    override var packedInt32: List<Int> by MsgFieldDelegate { mutableListOf() }
    override var packedInt64: List<Long> by MsgFieldDelegate { mutableListOf() }
    override var packedUint32: List<UInt> by MsgFieldDelegate { mutableListOf() }
    override var packedUint64: List<ULong> by MsgFieldDelegate { mutableListOf() }
    override var packedSint32: List<Int> by MsgFieldDelegate { mutableListOf() }
    override var packedSint64: List<Long> by MsgFieldDelegate { mutableListOf() }
    override var packedFixed32: List<UInt> by MsgFieldDelegate { mutableListOf() }
    override var packedFixed64: List<ULong> by MsgFieldDelegate { mutableListOf() }
    override var packedSfixed32: List<Int> by MsgFieldDelegate { mutableListOf() }
    override var packedSfixed64: List<Long> by MsgFieldDelegate { mutableListOf() }
    override var packedFloat: List<Float> by MsgFieldDelegate { mutableListOf() }
    override var packedDouble: List<Double> by MsgFieldDelegate { mutableListOf() }
    override var packedBool: List<Boolean> by MsgFieldDelegate { mutableListOf() }
    override var packedNestedEnum: List<TestAllTypesProto3.NestedEnum> by MsgFieldDelegate { mutableListOf() }
    override var unpackedInt32: List<Int> by MsgFieldDelegate { mutableListOf() }
    override var unpackedInt64: List<Long> by MsgFieldDelegate { mutableListOf() }
    override var unpackedUint32: List<UInt> by MsgFieldDelegate { mutableListOf() }
    override var unpackedUint64: List<ULong> by MsgFieldDelegate { mutableListOf() }
    override var unpackedSint32: List<Int> by MsgFieldDelegate { mutableListOf() }
    override var unpackedSint64: List<Long> by MsgFieldDelegate { mutableListOf() }
    override var unpackedFixed32: List<UInt> by MsgFieldDelegate { mutableListOf() }
    override var unpackedFixed64: List<ULong> by MsgFieldDelegate { mutableListOf() }
    override var unpackedSfixed32: List<Int> by MsgFieldDelegate { mutableListOf() }
    override var unpackedSfixed64: List<Long> by MsgFieldDelegate { mutableListOf() }
    override var unpackedFloat: List<Float> by MsgFieldDelegate { mutableListOf() }
    override var unpackedDouble: List<Double> by MsgFieldDelegate { mutableListOf() }
    override var unpackedBool: List<Boolean> by MsgFieldDelegate { mutableListOf() }
    override var unpackedNestedEnum: List<TestAllTypesProto3.NestedEnum> by MsgFieldDelegate { mutableListOf() }
    override var mapInt32Int32: Map<Int, Int> by MsgFieldDelegate { mutableMapOf() }
    override var mapInt64Int64: Map<Long, Long> by MsgFieldDelegate { mutableMapOf() }
    override var mapUint32Uint32: Map<UInt, UInt> by MsgFieldDelegate { mutableMapOf() }
    override var mapUint64Uint64: Map<ULong, ULong> by MsgFieldDelegate { mutableMapOf() }
    override var mapSint32Sint32: Map<Int, Int> by MsgFieldDelegate { mutableMapOf() }
    override var mapSint64Sint64: Map<Long, Long> by MsgFieldDelegate { mutableMapOf() }
    override var mapFixed32Fixed32: Map<UInt, UInt> by MsgFieldDelegate { mutableMapOf() }
    override var mapFixed64Fixed64: Map<ULong, ULong> by MsgFieldDelegate { mutableMapOf() }
    override var mapSfixed32Sfixed32: Map<Int, Int> by MsgFieldDelegate { mutableMapOf() }
    override var mapSfixed64Sfixed64: Map<Long, Long> by MsgFieldDelegate { mutableMapOf() }
    override var mapInt32Float: Map<Int, Float> by MsgFieldDelegate { mutableMapOf() }
    override var mapInt32Double: Map<Int, Double> by MsgFieldDelegate { mutableMapOf() }
    override var mapBoolBool: Map<Boolean, Boolean> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringString: Map<String, String> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringBytes: Map<String, ByteArray> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringNestedMessage: Map<String, TestAllTypesProto3.NestedMessage> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignMessage: Map<String, ForeignMessage> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringNestedEnum: Map<String, TestAllTypesProto3.NestedEnum> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignEnum: Map<String, ForeignEnum> by MsgFieldDelegate { mutableMapOf() }
    override var optionalBoolWrapper: BoolValue by MsgFieldDelegate(PresenceIndices.optionalBoolWrapper) { BoolValueInternal() }
    override var optionalInt32Wrapper: Int32Value by MsgFieldDelegate(PresenceIndices.optionalInt32Wrapper) { Int32ValueInternal() }
    override var optionalInt64Wrapper: Int64Value by MsgFieldDelegate(PresenceIndices.optionalInt64Wrapper) { Int64ValueInternal() }
    override var optionalUint32Wrapper: UInt32Value by MsgFieldDelegate(PresenceIndices.optionalUint32Wrapper) { UInt32ValueInternal() }
    override var optionalUint64Wrapper: UInt64Value by MsgFieldDelegate(PresenceIndices.optionalUint64Wrapper) { UInt64ValueInternal() }
    override var optionalFloatWrapper: FloatValue by MsgFieldDelegate(PresenceIndices.optionalFloatWrapper) { FloatValueInternal() }
    override var optionalDoubleWrapper: DoubleValue by MsgFieldDelegate(PresenceIndices.optionalDoubleWrapper) { DoubleValueInternal() }
    override var optionalStringWrapper: StringValue by MsgFieldDelegate(PresenceIndices.optionalStringWrapper) { StringValueInternal() }
    override var optionalBytesWrapper: BytesValue by MsgFieldDelegate(PresenceIndices.optionalBytesWrapper) { BytesValueInternal() }
    override var repeatedBoolWrapper: List<BoolValue> by MsgFieldDelegate { mutableListOf() }
    override var repeatedInt32Wrapper: List<Int32Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedInt64Wrapper: List<Int64Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedUint32Wrapper: List<UInt32Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedUint64Wrapper: List<UInt64Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFloatWrapper: List<FloatValue> by MsgFieldDelegate { mutableListOf() }
    override var repeatedDoubleWrapper: List<DoubleValue> by MsgFieldDelegate { mutableListOf() }
    override var repeatedStringWrapper: List<StringValue> by MsgFieldDelegate { mutableListOf() }
    override var repeatedBytesWrapper: List<BytesValue> by MsgFieldDelegate { mutableListOf() }
    override var optionalDuration: Duration by MsgFieldDelegate(PresenceIndices.optionalDuration) { DurationInternal() }
    override var optionalTimestamp: Timestamp by MsgFieldDelegate(PresenceIndices.optionalTimestamp) { TimestampInternal() }
    override var optionalFieldMask: FieldMask by MsgFieldDelegate(PresenceIndices.optionalFieldMask) { FieldMaskInternal() }
    override var optionalStruct: Struct by MsgFieldDelegate(PresenceIndices.optionalStruct) { StructInternal() }
    override var optionalAny: com.google.protobuf.kotlin.Any by MsgFieldDelegate(PresenceIndices.optionalAny) { AnyInternal() }
    override var optionalValue: Value by MsgFieldDelegate(PresenceIndices.optionalValue) { ValueInternal() }
    override var optionalNullValue: NullValue by MsgFieldDelegate { NullValue.NULL_VALUE }
    override var repeatedDuration: List<Duration> by MsgFieldDelegate { mutableListOf() }
    override var repeatedTimestamp: List<Timestamp> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFieldmask: List<FieldMask> by MsgFieldDelegate { mutableListOf() }
    override var repeatedStruct: List<Struct> by MsgFieldDelegate { mutableListOf() }
    override var repeatedAny: List<com.google.protobuf.kotlin.Any> by MsgFieldDelegate { mutableListOf() }
    override var repeatedValue: List<Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedListValue: List<ListValue> by MsgFieldDelegate { mutableListOf() }
    override var fieldname1: Int by MsgFieldDelegate { 0 }
    override var fieldName2: Int by MsgFieldDelegate { 0 }
    override var FieldName3: Int by MsgFieldDelegate { 0 }
    override var field_Name4_: Int by MsgFieldDelegate { 0 }
    override var field0name5: Int by MsgFieldDelegate { 0 }
    override var field_0Name6: Int by MsgFieldDelegate { 0 }
    override var fieldName7: Int by MsgFieldDelegate { 0 }
    override var FieldName8: Int by MsgFieldDelegate { 0 }
    override var field_Name9: Int by MsgFieldDelegate { 0 }
    override var Field_Name10: Int by MsgFieldDelegate { 0 }
    override var FIELD_NAME11: Int by MsgFieldDelegate { 0 }
    override var FIELDName12: Int by MsgFieldDelegate { 0 }
    override var _FieldName13: Int by MsgFieldDelegate { 0 }
    override var __FieldName14: Int by MsgFieldDelegate { 0 }
    override var field_Name15: Int by MsgFieldDelegate { 0 }
    override var field__Name16: Int by MsgFieldDelegate { 0 }
    override var fieldName17__: Int by MsgFieldDelegate { 0 }
    override var FieldName18__: Int by MsgFieldDelegate { 0 }
    override var oneofField: TestAllTypesProto3.OneofField? = null

    @InternalRpcApi
    val _presence: TestAllTypesProto3Presence = object : TestAllTypesProto3Presence {
        override val hasOptionalNestedMessage: Boolean get() = presenceMask[0]

        override val hasOptionalForeignMessage: Boolean get() = presenceMask[1]

        override val hasRecursiveMessage: Boolean get() = presenceMask[2]

        override val hasOptionalBoolWrapper: Boolean get() = presenceMask[3]

        override val hasOptionalInt32Wrapper: Boolean get() = presenceMask[4]

        override val hasOptionalInt64Wrapper: Boolean get() = presenceMask[5]

        override val hasOptionalUint32Wrapper: Boolean get() = presenceMask[6]

        override val hasOptionalUint64Wrapper: Boolean get() = presenceMask[7]

        override val hasOptionalFloatWrapper: Boolean get() = presenceMask[8]

        override val hasOptionalDoubleWrapper: Boolean get() = presenceMask[9]

        override val hasOptionalStringWrapper: Boolean get() = presenceMask[10]

        override val hasOptionalBytesWrapper: Boolean get() = presenceMask[11]

        override val hasOptionalDuration: Boolean get() = presenceMask[12]

        override val hasOptionalTimestamp: Boolean get() = presenceMask[13]

        override val hasOptionalFieldMask: Boolean get() = presenceMask[14]

        override val hasOptionalStruct: Boolean get() = presenceMask[15]

        override val hasOptionalAny: Boolean get() = presenceMask[16]

        override val hasOptionalValue: Boolean get() = presenceMask[17]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = optionalInt32.hashCode()
        result = 31 * result + optionalInt64.hashCode()
        result = 31 * result + optionalUint32.hashCode()
        result = 31 * result + optionalUint64.hashCode()
        result = 31 * result + optionalSint32.hashCode()
        result = 31 * result + optionalSint64.hashCode()
        result = 31 * result + optionalFixed32.hashCode()
        result = 31 * result + optionalFixed64.hashCode()
        result = 31 * result + optionalSfixed32.hashCode()
        result = 31 * result + optionalSfixed64.hashCode()
        result = 31 * result + optionalFloat.hashCode()
        result = 31 * result + optionalDouble.hashCode()
        result = 31 * result + optionalBool.hashCode()
        result = 31 * result + optionalString.hashCode()
        result = 31 * result + optionalBytes.contentHashCode()
        result = 31 * result + if (presenceMask[0]) optionalNestedMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[1]) optionalForeignMessage.hashCode() else 0
        result = 31 * result + optionalNestedEnum.hashCode()
        result = 31 * result + optionalForeignEnum.hashCode()
        result = 31 * result + optionalAliasedEnum.hashCode()
        result = 31 * result + optionalStringPiece.hashCode()
        result = 31 * result + optionalCord.hashCode()
        result = 31 * result + if (presenceMask[2]) recursiveMessage.hashCode() else 0
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
        result = 31 * result + mapInt32Float.hashCode()
        result = 31 * result + mapInt32Double.hashCode()
        result = 31 * result + mapBoolBool.hashCode()
        result = 31 * result + mapStringString.hashCode()
        result = 31 * result + mapStringBytes.hashCode()
        result = 31 * result + mapStringNestedMessage.hashCode()
        result = 31 * result + mapStringForeignMessage.hashCode()
        result = 31 * result + mapStringNestedEnum.hashCode()
        result = 31 * result + mapStringForeignEnum.hashCode()
        result = 31 * result + if (presenceMask[3]) optionalBoolWrapper.hashCode() else 0
        result = 31 * result + if (presenceMask[4]) optionalInt32Wrapper.hashCode() else 0
        result = 31 * result + if (presenceMask[5]) optionalInt64Wrapper.hashCode() else 0
        result = 31 * result + if (presenceMask[6]) optionalUint32Wrapper.hashCode() else 0
        result = 31 * result + if (presenceMask[7]) optionalUint64Wrapper.hashCode() else 0
        result = 31 * result + if (presenceMask[8]) optionalFloatWrapper.hashCode() else 0
        result = 31 * result + if (presenceMask[9]) optionalDoubleWrapper.hashCode() else 0
        result = 31 * result + if (presenceMask[10]) optionalStringWrapper.hashCode() else 0
        result = 31 * result + if (presenceMask[11]) optionalBytesWrapper.hashCode() else 0
        result = 31 * result + repeatedBoolWrapper.hashCode()
        result = 31 * result + repeatedInt32Wrapper.hashCode()
        result = 31 * result + repeatedInt64Wrapper.hashCode()
        result = 31 * result + repeatedUint32Wrapper.hashCode()
        result = 31 * result + repeatedUint64Wrapper.hashCode()
        result = 31 * result + repeatedFloatWrapper.hashCode()
        result = 31 * result + repeatedDoubleWrapper.hashCode()
        result = 31 * result + repeatedStringWrapper.hashCode()
        result = 31 * result + repeatedBytesWrapper.hashCode()
        result = 31 * result + if (presenceMask[12]) optionalDuration.hashCode() else 0
        result = 31 * result + if (presenceMask[13]) optionalTimestamp.hashCode() else 0
        result = 31 * result + if (presenceMask[14]) optionalFieldMask.hashCode() else 0
        result = 31 * result + if (presenceMask[15]) optionalStruct.hashCode() else 0
        result = 31 * result + if (presenceMask[16]) optionalAny.hashCode() else 0
        result = 31 * result + if (presenceMask[17]) optionalValue.hashCode() else 0
        result = 31 * result + optionalNullValue.hashCode()
        result = 31 * result + repeatedDuration.hashCode()
        result = 31 * result + repeatedTimestamp.hashCode()
        result = 31 * result + repeatedFieldmask.hashCode()
        result = 31 * result + repeatedStruct.hashCode()
        result = 31 * result + repeatedAny.hashCode()
        result = 31 * result + repeatedValue.hashCode()
        result = 31 * result + repeatedListValue.hashCode()
        result = 31 * result + fieldname1.hashCode()
        result = 31 * result + fieldName2.hashCode()
        result = 31 * result + FieldName3.hashCode()
        result = 31 * result + field_Name4_.hashCode()
        result = 31 * result + field0name5.hashCode()
        result = 31 * result + field_0Name6.hashCode()
        result = 31 * result + fieldName7.hashCode()
        result = 31 * result + FieldName8.hashCode()
        result = 31 * result + field_Name9.hashCode()
        result = 31 * result + Field_Name10.hashCode()
        result = 31 * result + FIELD_NAME11.hashCode()
        result = 31 * result + FIELDName12.hashCode()
        result = 31 * result + _FieldName13.hashCode()
        result = 31 * result + __FieldName14.hashCode()
        result = 31 * result + field_Name15.hashCode()
        result = 31 * result + field__Name16.hashCode()
        result = 31 * result + fieldName17__.hashCode()
        result = 31 * result + FieldName18__.hashCode()
        result = 31 * result + (oneofField?.oneOfHashCode() ?: 0)
        return result
    }

    fun TestAllTypesProto3.OneofField.oneOfHashCode(): Int {
        val offset = when (this) {
            is TestAllTypesProto3.OneofField.OneofUint32 -> 0
            is TestAllTypesProto3.OneofField.OneofNestedMessage -> 1
            is TestAllTypesProto3.OneofField.OneofString -> 2
            is TestAllTypesProto3.OneofField.OneofBytes -> 3
            is TestAllTypesProto3.OneofField.OneofBool -> 4
            is TestAllTypesProto3.OneofField.OneofUint64 -> 5
            is TestAllTypesProto3.OneofField.OneofFloat -> 6
            is TestAllTypesProto3.OneofField.OneofDouble -> 7
            is TestAllTypesProto3.OneofField.OneofEnum -> 8
            is TestAllTypesProto3.OneofField.OneofNullValue -> 9
        }

        return hashCode() + offset
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAllTypesProto3Internal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (optionalInt32 != other.optionalInt32) return false
        if (optionalInt64 != other.optionalInt64) return false
        if (optionalUint32 != other.optionalUint32) return false
        if (optionalUint64 != other.optionalUint64) return false
        if (optionalSint32 != other.optionalSint32) return false
        if (optionalSint64 != other.optionalSint64) return false
        if (optionalFixed32 != other.optionalFixed32) return false
        if (optionalFixed64 != other.optionalFixed64) return false
        if (optionalSfixed32 != other.optionalSfixed32) return false
        if (optionalSfixed64 != other.optionalSfixed64) return false
        if (optionalFloat != other.optionalFloat) return false
        if (optionalDouble != other.optionalDouble) return false
        if (optionalBool != other.optionalBool) return false
        if (optionalString != other.optionalString) return false
        if (!optionalBytes.contentEquals(other.optionalBytes)) return false
        if (presenceMask[0] && optionalNestedMessage != other.optionalNestedMessage) return false
        if (presenceMask[1] && optionalForeignMessage != other.optionalForeignMessage) return false
        if (optionalNestedEnum != other.optionalNestedEnum) return false
        if (optionalForeignEnum != other.optionalForeignEnum) return false
        if (optionalAliasedEnum != other.optionalAliasedEnum) return false
        if (optionalStringPiece != other.optionalStringPiece) return false
        if (optionalCord != other.optionalCord) return false
        if (presenceMask[2] && recursiveMessage != other.recursiveMessage) return false
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
        if (mapInt32Float != other.mapInt32Float) return false
        if (mapInt32Double != other.mapInt32Double) return false
        if (mapBoolBool != other.mapBoolBool) return false
        if (mapStringString != other.mapStringString) return false
        if (mapStringBytes != other.mapStringBytes) return false
        if (mapStringNestedMessage != other.mapStringNestedMessage) return false
        if (mapStringForeignMessage != other.mapStringForeignMessage) return false
        if (mapStringNestedEnum != other.mapStringNestedEnum) return false
        if (mapStringForeignEnum != other.mapStringForeignEnum) return false
        if (presenceMask[3] && optionalBoolWrapper != other.optionalBoolWrapper) return false
        if (presenceMask[4] && optionalInt32Wrapper != other.optionalInt32Wrapper) return false
        if (presenceMask[5] && optionalInt64Wrapper != other.optionalInt64Wrapper) return false
        if (presenceMask[6] && optionalUint32Wrapper != other.optionalUint32Wrapper) return false
        if (presenceMask[7] && optionalUint64Wrapper != other.optionalUint64Wrapper) return false
        if (presenceMask[8] && optionalFloatWrapper != other.optionalFloatWrapper) return false
        if (presenceMask[9] && optionalDoubleWrapper != other.optionalDoubleWrapper) return false
        if (presenceMask[10] && optionalStringWrapper != other.optionalStringWrapper) return false
        if (presenceMask[11] && optionalBytesWrapper != other.optionalBytesWrapper) return false
        if (repeatedBoolWrapper != other.repeatedBoolWrapper) return false
        if (repeatedInt32Wrapper != other.repeatedInt32Wrapper) return false
        if (repeatedInt64Wrapper != other.repeatedInt64Wrapper) return false
        if (repeatedUint32Wrapper != other.repeatedUint32Wrapper) return false
        if (repeatedUint64Wrapper != other.repeatedUint64Wrapper) return false
        if (repeatedFloatWrapper != other.repeatedFloatWrapper) return false
        if (repeatedDoubleWrapper != other.repeatedDoubleWrapper) return false
        if (repeatedStringWrapper != other.repeatedStringWrapper) return false
        if (repeatedBytesWrapper != other.repeatedBytesWrapper) return false
        if (presenceMask[12] && optionalDuration != other.optionalDuration) return false
        if (presenceMask[13] && optionalTimestamp != other.optionalTimestamp) return false
        if (presenceMask[14] && optionalFieldMask != other.optionalFieldMask) return false
        if (presenceMask[15] && optionalStruct != other.optionalStruct) return false
        if (presenceMask[16] && optionalAny != other.optionalAny) return false
        if (presenceMask[17] && optionalValue != other.optionalValue) return false
        if (optionalNullValue != other.optionalNullValue) return false
        if (repeatedDuration != other.repeatedDuration) return false
        if (repeatedTimestamp != other.repeatedTimestamp) return false
        if (repeatedFieldmask != other.repeatedFieldmask) return false
        if (repeatedStruct != other.repeatedStruct) return false
        if (repeatedAny != other.repeatedAny) return false
        if (repeatedValue != other.repeatedValue) return false
        if (repeatedListValue != other.repeatedListValue) return false
        if (fieldname1 != other.fieldname1) return false
        if (fieldName2 != other.fieldName2) return false
        if (FieldName3 != other.FieldName3) return false
        if (field_Name4_ != other.field_Name4_) return false
        if (field0name5 != other.field0name5) return false
        if (field_0Name6 != other.field_0Name6) return false
        if (fieldName7 != other.fieldName7) return false
        if (FieldName8 != other.FieldName8) return false
        if (field_Name9 != other.field_Name9) return false
        if (Field_Name10 != other.Field_Name10) return false
        if (FIELD_NAME11 != other.FIELD_NAME11) return false
        if (FIELDName12 != other.FIELDName12) return false
        if (_FieldName13 != other._FieldName13) return false
        if (__FieldName14 != other.__FieldName14) return false
        if (field_Name15 != other.field_Name15) return false
        if (field__Name16 != other.field__Name16) return false
        if (fieldName17__ != other.fieldName17__) return false
        if (FieldName18__ != other.FieldName18__) return false
        if (oneofField != other.oneofField) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("TestAllTypesProto3(")
            appendLine("${nextIndentString}optionalInt32=${optionalInt32},")
            appendLine("${nextIndentString}optionalInt64=${optionalInt64},")
            appendLine("${nextIndentString}optionalUint32=${optionalUint32},")
            appendLine("${nextIndentString}optionalUint64=${optionalUint64},")
            appendLine("${nextIndentString}optionalSint32=${optionalSint32},")
            appendLine("${nextIndentString}optionalSint64=${optionalSint64},")
            appendLine("${nextIndentString}optionalFixed32=${optionalFixed32},")
            appendLine("${nextIndentString}optionalFixed64=${optionalFixed64},")
            appendLine("${nextIndentString}optionalSfixed32=${optionalSfixed32},")
            appendLine("${nextIndentString}optionalSfixed64=${optionalSfixed64},")
            appendLine("${nextIndentString}optionalFloat=${optionalFloat},")
            appendLine("${nextIndentString}optionalDouble=${optionalDouble},")
            appendLine("${nextIndentString}optionalBool=${optionalBool},")
            appendLine("${nextIndentString}optionalString=${optionalString},")
            appendLine("${nextIndentString}optionalBytes=${optionalBytes.contentToString()},")
            if (presenceMask[0]) {
                appendLine("${nextIndentString}optionalNestedMessage=${optionalNestedMessage.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalNestedMessage=<unset>,")
            }

            if (presenceMask[1]) {
                appendLine("${nextIndentString}optionalForeignMessage=${optionalForeignMessage.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalForeignMessage=<unset>,")
            }

            appendLine("${nextIndentString}optionalNestedEnum=${optionalNestedEnum},")
            appendLine("${nextIndentString}optionalForeignEnum=${optionalForeignEnum},")
            appendLine("${nextIndentString}optionalAliasedEnum=${optionalAliasedEnum},")
            appendLine("${nextIndentString}optionalStringPiece=${optionalStringPiece},")
            appendLine("${nextIndentString}optionalCord=${optionalCord},")
            if (presenceMask[2]) {
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
            appendLine("${nextIndentString}mapInt32Float=${mapInt32Float},")
            appendLine("${nextIndentString}mapInt32Double=${mapInt32Double},")
            appendLine("${nextIndentString}mapBoolBool=${mapBoolBool},")
            appendLine("${nextIndentString}mapStringString=${mapStringString},")
            appendLine("${nextIndentString}mapStringBytes=${mapStringBytes},")
            appendLine("${nextIndentString}mapStringNestedMessage=${mapStringNestedMessage},")
            appendLine("${nextIndentString}mapStringForeignMessage=${mapStringForeignMessage},")
            appendLine("${nextIndentString}mapStringNestedEnum=${mapStringNestedEnum},")
            appendLine("${nextIndentString}mapStringForeignEnum=${mapStringForeignEnum},")
            if (presenceMask[3]) {
                appendLine("${nextIndentString}optionalBoolWrapper=${optionalBoolWrapper.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalBoolWrapper=<unset>,")
            }

            if (presenceMask[4]) {
                appendLine("${nextIndentString}optionalInt32Wrapper=${optionalInt32Wrapper.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalInt32Wrapper=<unset>,")
            }

            if (presenceMask[5]) {
                appendLine("${nextIndentString}optionalInt64Wrapper=${optionalInt64Wrapper.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalInt64Wrapper=<unset>,")
            }

            if (presenceMask[6]) {
                appendLine("${nextIndentString}optionalUint32Wrapper=${optionalUint32Wrapper.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalUint32Wrapper=<unset>,")
            }

            if (presenceMask[7]) {
                appendLine("${nextIndentString}optionalUint64Wrapper=${optionalUint64Wrapper.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalUint64Wrapper=<unset>,")
            }

            if (presenceMask[8]) {
                appendLine("${nextIndentString}optionalFloatWrapper=${optionalFloatWrapper.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalFloatWrapper=<unset>,")
            }

            if (presenceMask[9]) {
                appendLine("${nextIndentString}optionalDoubleWrapper=${optionalDoubleWrapper.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalDoubleWrapper=<unset>,")
            }

            if (presenceMask[10]) {
                appendLine("${nextIndentString}optionalStringWrapper=${optionalStringWrapper.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalStringWrapper=<unset>,")
            }

            if (presenceMask[11]) {
                appendLine("${nextIndentString}optionalBytesWrapper=${optionalBytesWrapper.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalBytesWrapper=<unset>,")
            }

            appendLine("${nextIndentString}repeatedBoolWrapper=${repeatedBoolWrapper},")
            appendLine("${nextIndentString}repeatedInt32Wrapper=${repeatedInt32Wrapper},")
            appendLine("${nextIndentString}repeatedInt64Wrapper=${repeatedInt64Wrapper},")
            appendLine("${nextIndentString}repeatedUint32Wrapper=${repeatedUint32Wrapper},")
            appendLine("${nextIndentString}repeatedUint64Wrapper=${repeatedUint64Wrapper},")
            appendLine("${nextIndentString}repeatedFloatWrapper=${repeatedFloatWrapper},")
            appendLine("${nextIndentString}repeatedDoubleWrapper=${repeatedDoubleWrapper},")
            appendLine("${nextIndentString}repeatedStringWrapper=${repeatedStringWrapper},")
            appendLine("${nextIndentString}repeatedBytesWrapper=${repeatedBytesWrapper},")
            if (presenceMask[12]) {
                appendLine("${nextIndentString}optionalDuration=${optionalDuration.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalDuration=<unset>,")
            }

            if (presenceMask[13]) {
                appendLine("${nextIndentString}optionalTimestamp=${optionalTimestamp.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalTimestamp=<unset>,")
            }

            if (presenceMask[14]) {
                appendLine("${nextIndentString}optionalFieldMask=${optionalFieldMask.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalFieldMask=<unset>,")
            }

            if (presenceMask[15]) {
                appendLine("${nextIndentString}optionalStruct=${optionalStruct.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalStruct=<unset>,")
            }

            if (presenceMask[16]) {
                appendLine("${nextIndentString}optionalAny=${optionalAny.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalAny=<unset>,")
            }

            if (presenceMask[17]) {
                appendLine("${nextIndentString}optionalValue=${optionalValue.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}optionalValue=<unset>,")
            }

            appendLine("${nextIndentString}optionalNullValue=${optionalNullValue},")
            appendLine("${nextIndentString}repeatedDuration=${repeatedDuration},")
            appendLine("${nextIndentString}repeatedTimestamp=${repeatedTimestamp},")
            appendLine("${nextIndentString}repeatedFieldmask=${repeatedFieldmask},")
            appendLine("${nextIndentString}repeatedStruct=${repeatedStruct},")
            appendLine("${nextIndentString}repeatedAny=${repeatedAny},")
            appendLine("${nextIndentString}repeatedValue=${repeatedValue},")
            appendLine("${nextIndentString}repeatedListValue=${repeatedListValue},")
            appendLine("${nextIndentString}fieldname1=${fieldname1},")
            appendLine("${nextIndentString}fieldName2=${fieldName2},")
            appendLine("${nextIndentString}FieldName3=${FieldName3},")
            appendLine("${nextIndentString}field_Name4_=${field_Name4_},")
            appendLine("${nextIndentString}field0name5=${field0name5},")
            appendLine("${nextIndentString}field_0Name6=${field_0Name6},")
            appendLine("${nextIndentString}fieldName7=${fieldName7},")
            appendLine("${nextIndentString}FieldName8=${FieldName8},")
            appendLine("${nextIndentString}field_Name9=${field_Name9},")
            appendLine("${nextIndentString}Field_Name10=${Field_Name10},")
            appendLine("${nextIndentString}FIELD_NAME11=${FIELD_NAME11},")
            appendLine("${nextIndentString}FIELDName12=${FIELDName12},")
            appendLine("${nextIndentString}_FieldName13=${_FieldName13},")
            appendLine("${nextIndentString}__FieldName14=${__FieldName14},")
            appendLine("${nextIndentString}field_Name15=${field_Name15},")
            appendLine("${nextIndentString}field__Name16=${field__Name16},")
            appendLine("${nextIndentString}fieldName17__=${fieldName17__},")
            appendLine("${nextIndentString}FieldName18__=${FieldName18__},")
            appendLine("${nextIndentString}oneofField=${oneofField},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    fun copyInternal(body: TestAllTypesProto3Internal.() -> Unit): TestAllTypesProto3Internal {
        val copy = TestAllTypesProto3Internal()
        copy.optionalInt32 = this.optionalInt32
        copy.optionalInt64 = this.optionalInt64
        copy.optionalUint32 = this.optionalUint32
        copy.optionalUint64 = this.optionalUint64
        copy.optionalSint32 = this.optionalSint32
        copy.optionalSint64 = this.optionalSint64
        copy.optionalFixed32 = this.optionalFixed32
        copy.optionalFixed64 = this.optionalFixed64
        copy.optionalSfixed32 = this.optionalSfixed32
        copy.optionalSfixed64 = this.optionalSfixed64
        copy.optionalFloat = this.optionalFloat
        copy.optionalDouble = this.optionalDouble
        copy.optionalBool = this.optionalBool
        copy.optionalString = this.optionalString
        copy.optionalBytes = this.optionalBytes.copyOf()
        if (presenceMask[0]) {
            copy.optionalNestedMessage = this.optionalNestedMessage.copy()
        }

        if (presenceMask[1]) {
            copy.optionalForeignMessage = this.optionalForeignMessage.copy()
        }

        copy.optionalNestedEnum = this.optionalNestedEnum
        copy.optionalForeignEnum = this.optionalForeignEnum
        copy.optionalAliasedEnum = this.optionalAliasedEnum
        copy.optionalStringPiece = this.optionalStringPiece
        copy.optionalCord = this.optionalCord
        if (presenceMask[2]) {
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
        copy.mapInt32Float = this.mapInt32Float.mapValues { it.value }
        copy.mapInt32Double = this.mapInt32Double.mapValues { it.value }
        copy.mapBoolBool = this.mapBoolBool.mapValues { it.value }
        copy.mapStringString = this.mapStringString.mapValues { it.value }
        copy.mapStringBytes = this.mapStringBytes.mapValues { it.value.copyOf() }
        copy.mapStringNestedMessage = this.mapStringNestedMessage.mapValues { it.value.copy() }
        copy.mapStringForeignMessage = this.mapStringForeignMessage.mapValues { it.value.copy() }
        copy.mapStringNestedEnum = this.mapStringNestedEnum.mapValues { it.value }
        copy.mapStringForeignEnum = this.mapStringForeignEnum.mapValues { it.value }
        if (presenceMask[3]) {
            copy.optionalBoolWrapper = this.optionalBoolWrapper.copy()
        }

        if (presenceMask[4]) {
            copy.optionalInt32Wrapper = this.optionalInt32Wrapper.copy()
        }

        if (presenceMask[5]) {
            copy.optionalInt64Wrapper = this.optionalInt64Wrapper.copy()
        }

        if (presenceMask[6]) {
            copy.optionalUint32Wrapper = this.optionalUint32Wrapper.copy()
        }

        if (presenceMask[7]) {
            copy.optionalUint64Wrapper = this.optionalUint64Wrapper.copy()
        }

        if (presenceMask[8]) {
            copy.optionalFloatWrapper = this.optionalFloatWrapper.copy()
        }

        if (presenceMask[9]) {
            copy.optionalDoubleWrapper = this.optionalDoubleWrapper.copy()
        }

        if (presenceMask[10]) {
            copy.optionalStringWrapper = this.optionalStringWrapper.copy()
        }

        if (presenceMask[11]) {
            copy.optionalBytesWrapper = this.optionalBytesWrapper.copy()
        }

        copy.repeatedBoolWrapper = this.repeatedBoolWrapper.map { it.copy() }
        copy.repeatedInt32Wrapper = this.repeatedInt32Wrapper.map { it.copy() }
        copy.repeatedInt64Wrapper = this.repeatedInt64Wrapper.map { it.copy() }
        copy.repeatedUint32Wrapper = this.repeatedUint32Wrapper.map { it.copy() }
        copy.repeatedUint64Wrapper = this.repeatedUint64Wrapper.map { it.copy() }
        copy.repeatedFloatWrapper = this.repeatedFloatWrapper.map { it.copy() }
        copy.repeatedDoubleWrapper = this.repeatedDoubleWrapper.map { it.copy() }
        copy.repeatedStringWrapper = this.repeatedStringWrapper.map { it.copy() }
        copy.repeatedBytesWrapper = this.repeatedBytesWrapper.map { it.copy() }
        if (presenceMask[12]) {
            copy.optionalDuration = this.optionalDuration.copy()
        }

        if (presenceMask[13]) {
            copy.optionalTimestamp = this.optionalTimestamp.copy()
        }

        if (presenceMask[14]) {
            copy.optionalFieldMask = this.optionalFieldMask.copy()
        }

        if (presenceMask[15]) {
            copy.optionalStruct = this.optionalStruct.copy()
        }

        if (presenceMask[16]) {
            copy.optionalAny = this.optionalAny.copy()
        }

        if (presenceMask[17]) {
            copy.optionalValue = this.optionalValue.copy()
        }

        copy.optionalNullValue = this.optionalNullValue
        copy.repeatedDuration = this.repeatedDuration.map { it.copy() }
        copy.repeatedTimestamp = this.repeatedTimestamp.map { it.copy() }
        copy.repeatedFieldmask = this.repeatedFieldmask.map { it.copy() }
        copy.repeatedStruct = this.repeatedStruct.map { it.copy() }
        copy.repeatedAny = this.repeatedAny.map { it.copy() }
        copy.repeatedValue = this.repeatedValue.map { it.copy() }
        copy.repeatedListValue = this.repeatedListValue.map { it.copy() }
        copy.fieldname1 = this.fieldname1
        copy.fieldName2 = this.fieldName2
        copy.FieldName3 = this.FieldName3
        copy.field_Name4_ = this.field_Name4_
        copy.field0name5 = this.field0name5
        copy.field_0Name6 = this.field_0Name6
        copy.fieldName7 = this.fieldName7
        copy.FieldName8 = this.FieldName8
        copy.field_Name9 = this.field_Name9
        copy.Field_Name10 = this.Field_Name10
        copy.FIELD_NAME11 = this.FIELD_NAME11
        copy.FIELDName12 = this.FIELDName12
        copy._FieldName13 = this._FieldName13
        copy.__FieldName14 = this.__FieldName14
        copy.field_Name15 = this.field_Name15
        copy.field__Name16 = this.field__Name16
        copy.fieldName17__ = this.fieldName17__
        copy.FieldName18__ = this.FieldName18__
        copy.oneofField = this.oneofField?.oneOfCopy()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    fun TestAllTypesProto3.OneofField.oneOfCopy(): TestAllTypesProto3.OneofField {
        return when (this) {
            is TestAllTypesProto3.OneofField.OneofUint32 -> {
                this
            }
            is TestAllTypesProto3.OneofField.OneofNestedMessage -> {
                TestAllTypesProto3.OneofField.OneofNestedMessage(this.value.copy())
            }
            is TestAllTypesProto3.OneofField.OneofString -> {
                this
            }
            is TestAllTypesProto3.OneofField.OneofBytes -> {
                TestAllTypesProto3.OneofField.OneofBytes(this.value.copyOf())
            }
            is TestAllTypesProto3.OneofField.OneofBool -> {
                this
            }
            is TestAllTypesProto3.OneofField.OneofUint64 -> {
                this
            }
            is TestAllTypesProto3.OneofField.OneofFloat -> {
                this
            }
            is TestAllTypesProto3.OneofField.OneofDouble -> {
                this
            }
            is TestAllTypesProto3.OneofField.OneofEnum -> {
                this
            }
            is TestAllTypesProto3.OneofField.OneofNullValue -> {
                this
            }
        }
    }

    class NestedMessageInternal: TestAllTypesProto3.NestedMessage, InternalMessage(fieldsWithPresence = 1) {
        private object PresenceIndices {
            const val corecursive: Int = 0
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var a: Int by MsgFieldDelegate { 0 }
        override var corecursive: TestAllTypesProto3 by MsgFieldDelegate(PresenceIndices.corecursive) { TestAllTypesProto3Internal() }

        @InternalRpcApi
        val _presence: TestAllTypesProto3Presence.NestedMessage = object : TestAllTypesProto3Presence.NestedMessage {
            override val hasCorecursive: Boolean get() = presenceMask[0]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = a.hashCode()
            result = 31 * result + if (presenceMask[0]) corecursive.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as NestedMessageInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (a != other.a) return false
            if (presenceMask[0] && corecursive != other.corecursive) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.NestedMessage(")
                appendLine("${nextIndentString}a=${a},")
                if (presenceMask[0]) {
                    appendLine("${nextIndentString}corecursive=${corecursive.asInternal().asString(indent = indent + 4)},")
                } else {
                    appendLine("${nextIndentString}corecursive=<unset>,")
                }

                append("${indentString})")
            }
        }

        @InternalRpcApi
        fun copyInternal(body: NestedMessageInternal.() -> Unit): NestedMessageInternal {
            val copy = NestedMessageInternal()
            copy.a = this.a
            if (presenceMask[0]) {
                copy.corecursive = this.corecursive.copy()
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object CODEC: MessageCodec<TestAllTypesProto3.NestedMessage> {
            override fun encode(value: TestAllTypesProto3.NestedMessage, config: CodecConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtobufConfig)
                }
                encoder.flush()
                internalMsg._unknownFields.copyTo(buffer)
                return buffer
            }

            override fun decode(source: Source, config: CodecConfig?): TestAllTypesProto3.NestedMessage {
                WireDecoder(source).use {
                    val msg = NestedMessageInternal()
                    checkForPlatformDecodeException {
                        NestedMessageInternal.decodeWith(msg, it, config as? ProtobufConfig)
                    }
                    msg.checkRequiredFields()
                    msg._unknownFieldsEncoder?.flush()
                    return msg
                }
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapInt32Int32EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate { 0 }
        var value: Int by MsgFieldDelegate { 0 }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32Int32EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapInt32Int32Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapInt64Int64EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Long by MsgFieldDelegate { 0L }
        var value: Long by MsgFieldDelegate { 0L }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt64Int64EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapInt64Int64Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapUint32Uint32EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: UInt by MsgFieldDelegate { 0u }
        var value: UInt by MsgFieldDelegate { 0u }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapUint32Uint32EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapUint32Uint32Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapUint64Uint64EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: ULong by MsgFieldDelegate { 0uL }
        var value: ULong by MsgFieldDelegate { 0uL }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapUint64Uint64EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapUint64Uint64Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapSint32Sint32EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate { 0 }
        var value: Int by MsgFieldDelegate { 0 }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSint32Sint32EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapSint32Sint32Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapSint64Sint64EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Long by MsgFieldDelegate { 0L }
        var value: Long by MsgFieldDelegate { 0L }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSint64Sint64EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapSint64Sint64Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapFixed32Fixed32EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: UInt by MsgFieldDelegate { 0u }
        var value: UInt by MsgFieldDelegate { 0u }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapFixed32Fixed32EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapFixed32Fixed32Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapFixed64Fixed64EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: ULong by MsgFieldDelegate { 0uL }
        var value: ULong by MsgFieldDelegate { 0uL }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapFixed64Fixed64EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapFixed64Fixed64Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapSfixed32Sfixed32EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate { 0 }
        var value: Int by MsgFieldDelegate { 0 }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSfixed32Sfixed32EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapSfixed32Sfixed32Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapSfixed64Sfixed64EntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Long by MsgFieldDelegate { 0L }
        var value: Long by MsgFieldDelegate { 0L }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSfixed64Sfixed64EntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapSfixed64Sfixed64Entry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapInt32FloatEntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate { 0 }
        var value: Float by MsgFieldDelegate { 0.0f }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32FloatEntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapInt32FloatEntry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapInt32DoubleEntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Int by MsgFieldDelegate { 0 }
        var value: Double by MsgFieldDelegate { 0.0 }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32DoubleEntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapInt32DoubleEntry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapBoolBoolEntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: Boolean by MsgFieldDelegate { false }
        var value: Boolean by MsgFieldDelegate { false }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapBoolBoolEntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapBoolBoolEntry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapStringStringEntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate { "" }
        var value: String by MsgFieldDelegate { "" }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringStringEntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapStringStringEntry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapStringBytesEntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate { "" }
        var value: ByteArray by MsgFieldDelegate { byteArrayOf() }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.contentHashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringBytesEntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (!value.contentEquals(other.value)) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapStringBytesEntry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value.contentToString()},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapStringNestedMessageEntryInternal: InternalMessage(fieldsWithPresence = 1) {
        private object PresenceIndices {
            const val value: Int = 0
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate { "" }
        var value: TestAllTypesProto3.NestedMessage by MsgFieldDelegate(PresenceIndices.value) { NestedMessageInternal() }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + if (presenceMask[0]) value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringNestedMessageEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (key != other.key) return false
            if (presenceMask[0] && value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapStringNestedMessageEntry(")
                appendLine("${nextIndentString}key=${key},")
                if (presenceMask[0]) {
                    appendLine("${nextIndentString}value=${value.asInternal().asString(indent = indent + 4)},")
                } else {
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapStringForeignMessageEntryInternal: InternalMessage(fieldsWithPresence = 1) {
        private object PresenceIndices {
            const val value: Int = 0
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate { "" }
        var value: ForeignMessage by MsgFieldDelegate(PresenceIndices.value) { ForeignMessageInternal() }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + if (presenceMask[0]) value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringForeignMessageEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (key != other.key) return false
            if (presenceMask[0] && value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapStringForeignMessageEntry(")
                appendLine("${nextIndentString}key=${key},")
                if (presenceMask[0]) {
                    appendLine("${nextIndentString}value=${value.asInternal().asString(indent = indent + 4)},")
                } else {
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapStringNestedEnumEntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate { "" }
        var value: TestAllTypesProto3.NestedEnum by MsgFieldDelegate { TestAllTypesProto3.NestedEnum.FOO }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringNestedEnumEntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapStringNestedEnumEntry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    class MapStringForeignEnumEntryInternal: InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        var key: String by MsgFieldDelegate { "" }
        var value: ForeignEnum by MsgFieldDelegate { ForeignEnum.FOREIGN_FOO }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringForeignEnumEntryInternal
            other.checkRequiredFields()
            if (key != other.key) return false
            if (value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            return buildString {
                appendLine("TestAllTypesProto3.MapStringForeignEnumEntry(")
                appendLine("${nextIndentString}key=${key},")
                appendLine("${nextIndentString}value=${value},")
                append("${indentString})")
            }
        }

        @InternalRpcApi
        companion object
    }

    @InternalRpcApi
    object CODEC: MessageCodec<TestAllTypesProto3> {
        override fun encode(value: TestAllTypesProto3, config: CodecConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        override fun decode(source: Source, config: CodecConfig?): TestAllTypesProto3 {
            WireDecoder(source).use {
                val msg = TestAllTypesProto3Internal()
                checkForPlatformDecodeException {
                    TestAllTypesProto3Internal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    companion object
}

class ForeignMessageInternal: ForeignMessage, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var c: Int by MsgFieldDelegate { 0 }

    override fun hashCode(): Int {
        checkRequiredFields()
        return c.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ForeignMessageInternal
        other.checkRequiredFields()
        if (c != other.c) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("ForeignMessage(")
            appendLine("${nextIndentString}c=${c},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    fun copyInternal(body: ForeignMessageInternal.() -> Unit): ForeignMessageInternal {
        val copy = ForeignMessageInternal()
        copy.c = this.c
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object CODEC: MessageCodec<ForeignMessage> {
        override fun encode(value: ForeignMessage, config: CodecConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        override fun decode(source: Source, config: CodecConfig?): ForeignMessage {
            WireDecoder(source).use {
                val msg = ForeignMessageInternal()
                checkForPlatformDecodeException {
                    ForeignMessageInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    companion object
}

class NullHypothesisProto3Internal: NullHypothesisProto3, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override fun hashCode(): Int {
        checkRequiredFields()
        return this::class.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as NullHypothesisProto3Internal
        other.checkRequiredFields()
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("NullHypothesisProto3(")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    fun copyInternal(body: NullHypothesisProto3Internal.() -> Unit): NullHypothesisProto3Internal {
        val copy = NullHypothesisProto3Internal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object CODEC: MessageCodec<NullHypothesisProto3> {
        override fun encode(value: NullHypothesisProto3, config: CodecConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        override fun decode(source: Source, config: CodecConfig?): NullHypothesisProto3 {
            WireDecoder(source).use {
                val msg = NullHypothesisProto3Internal()
                checkForPlatformDecodeException {
                    NullHypothesisProto3Internal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    companion object
}

class EnumOnlyProto3Internal: EnumOnlyProto3, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override fun hashCode(): Int {
        checkRequiredFields()
        return this::class.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumOnlyProto3Internal
        other.checkRequiredFields()
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("EnumOnlyProto3(")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    fun copyInternal(body: EnumOnlyProto3Internal.() -> Unit): EnumOnlyProto3Internal {
        val copy = EnumOnlyProto3Internal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object CODEC: MessageCodec<EnumOnlyProto3> {
        override fun encode(value: EnumOnlyProto3, config: CodecConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        override fun decode(source: Source, config: CodecConfig?): EnumOnlyProto3 {
            WireDecoder(source).use {
                val msg = EnumOnlyProto3Internal()
                checkForPlatformDecodeException {
                    EnumOnlyProto3Internal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    companion object
}

@InternalRpcApi
fun TestAllTypesProto3Internal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        optionalNestedMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[1]) {
        optionalForeignMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[2]) {
        recursiveMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[3]) {
        optionalBoolWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[4]) {
        optionalInt32Wrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[5]) {
        optionalInt64Wrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[6]) {
        optionalUint32Wrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[7]) {
        optionalUint64Wrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[8]) {
        optionalFloatWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[9]) {
        optionalDoubleWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[10]) {
        optionalStringWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[11]) {
        optionalBytesWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[12]) {
        optionalDuration.asInternal().checkRequiredFields()
    }

    if (presenceMask[13]) {
        optionalTimestamp.asInternal().checkRequiredFields()
    }

    if (presenceMask[14]) {
        optionalFieldMask.asInternal().checkRequiredFields()
    }

    if (presenceMask[15]) {
        optionalStruct.asInternal().checkRequiredFields()
    }

    if (presenceMask[16]) {
        optionalAny.asInternal().checkRequiredFields()
    }

    if (presenceMask[17]) {
        optionalValue.asInternal().checkRequiredFields()
    }

    oneofField?.also {
        when {
            it is TestAllTypesProto3.OneofField.OneofNestedMessage -> {
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

    repeatedBoolWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedInt32Wrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedInt64Wrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedUint32Wrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedUint64Wrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedFloatWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedDoubleWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedStringWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedBytesWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedDuration.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedTimestamp.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedFieldmask.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedStruct.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedAny.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedValue.forEach {
        it.asInternal().checkRequiredFields()
    }

    repeatedListValue.forEach {
        it.asInternal().checkRequiredFields()
    }

    mapStringNestedMessage.values.forEach {
        it.asInternal().checkRequiredFields()
    }

    mapStringForeignMessage.values.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (optionalInt32 != 0) {
        encoder.writeInt32(fieldNr = 1, value = optionalInt32)
    }

    if (optionalInt64 != 0L) {
        encoder.writeInt64(fieldNr = 2, value = optionalInt64)
    }

    if (optionalUint32 != 0u) {
        encoder.writeUInt32(fieldNr = 3, value = optionalUint32)
    }

    if (optionalUint64 != 0uL) {
        encoder.writeUInt64(fieldNr = 4, value = optionalUint64)
    }

    if (optionalSint32 != 0) {
        encoder.writeSInt32(fieldNr = 5, value = optionalSint32)
    }

    if (optionalSint64 != 0L) {
        encoder.writeSInt64(fieldNr = 6, value = optionalSint64)
    }

    if (optionalFixed32 != 0u) {
        encoder.writeFixed32(fieldNr = 7, value = optionalFixed32)
    }

    if (optionalFixed64 != 0uL) {
        encoder.writeFixed64(fieldNr = 8, value = optionalFixed64)
    }

    if (optionalSfixed32 != 0) {
        encoder.writeSFixed32(fieldNr = 9, value = optionalSfixed32)
    }

    if (optionalSfixed64 != 0L) {
        encoder.writeSFixed64(fieldNr = 10, value = optionalSfixed64)
    }

    if (optionalFloat != 0.0f) {
        encoder.writeFloat(fieldNr = 11, value = optionalFloat)
    }

    if (optionalDouble != 0.0) {
        encoder.writeDouble(fieldNr = 12, value = optionalDouble)
    }

    if (optionalBool != false) {
        encoder.writeBool(fieldNr = 13, value = optionalBool)
    }

    if (optionalString.isNotEmpty()) {
        encoder.writeString(fieldNr = 14, value = optionalString)
    }

    if (optionalBytes.isNotEmpty()) {
        encoder.writeBytes(fieldNr = 15, value = optionalBytes)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 18, value = optionalNestedMessage.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[1]) {
        encoder.writeMessage(fieldNr = 19, value = optionalForeignMessage.asInternal()) { encodeWith(it, config) }
    }

    if (optionalNestedEnum != TestAllTypesProto3.NestedEnum.FOO) {
        encoder.writeEnum(fieldNr = 21, value = optionalNestedEnum.number)
    }

    if (optionalForeignEnum != ForeignEnum.FOREIGN_FOO) {
        encoder.writeEnum(fieldNr = 22, value = optionalForeignEnum.number)
    }

    if (optionalAliasedEnum != TestAllTypesProto3.AliasedEnum.ALIAS_FOO) {
        encoder.writeEnum(fieldNr = 23, value = optionalAliasedEnum.number)
    }

    if (optionalStringPiece.isNotEmpty()) {
        encoder.writeString(fieldNr = 24, value = optionalStringPiece)
    }

    if (optionalCord.isNotEmpty()) {
        encoder.writeString(fieldNr = 25, value = optionalCord)
    }

    if (presenceMask[2]) {
        encoder.writeMessage(fieldNr = 27, value = recursiveMessage.asInternal()) { encodeWith(it, config) }
    }

    if (repeatedInt32.isNotEmpty()) {
        encoder.writePackedInt32(fieldNr = 31, value = repeatedInt32, fieldSize = WireSize.packedInt32(repeatedInt32))
    }

    if (repeatedInt64.isNotEmpty()) {
        encoder.writePackedInt64(fieldNr = 32, value = repeatedInt64, fieldSize = WireSize.packedInt64(repeatedInt64))
    }

    if (repeatedUint32.isNotEmpty()) {
        encoder.writePackedUInt32(fieldNr = 33, value = repeatedUint32, fieldSize = WireSize.packedUInt32(repeatedUint32))
    }

    if (repeatedUint64.isNotEmpty()) {
        encoder.writePackedUInt64(fieldNr = 34, value = repeatedUint64, fieldSize = WireSize.packedUInt64(repeatedUint64))
    }

    if (repeatedSint32.isNotEmpty()) {
        encoder.writePackedSInt32(fieldNr = 35, value = repeatedSint32, fieldSize = WireSize.packedSInt32(repeatedSint32))
    }

    if (repeatedSint64.isNotEmpty()) {
        encoder.writePackedSInt64(fieldNr = 36, value = repeatedSint64, fieldSize = WireSize.packedSInt64(repeatedSint64))
    }

    if (repeatedFixed32.isNotEmpty()) {
        encoder.writePackedFixed32(fieldNr = 37, value = repeatedFixed32)
    }

    if (repeatedFixed64.isNotEmpty()) {
        encoder.writePackedFixed64(fieldNr = 38, value = repeatedFixed64)
    }

    if (repeatedSfixed32.isNotEmpty()) {
        encoder.writePackedSFixed32(fieldNr = 39, value = repeatedSfixed32)
    }

    if (repeatedSfixed64.isNotEmpty()) {
        encoder.writePackedSFixed64(fieldNr = 40, value = repeatedSfixed64)
    }

    if (repeatedFloat.isNotEmpty()) {
        encoder.writePackedFloat(fieldNr = 41, value = repeatedFloat)
    }

    if (repeatedDouble.isNotEmpty()) {
        encoder.writePackedDouble(fieldNr = 42, value = repeatedDouble)
    }

    if (repeatedBool.isNotEmpty()) {
        encoder.writePackedBool(fieldNr = 43, value = repeatedBool, fieldSize = WireSize.packedBool(repeatedBool))
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
            encoder.writeMessage(fieldNr = 48, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedForeignMessage.isNotEmpty()) {
        repeatedForeignMessage.forEach {
            encoder.writeMessage(fieldNr = 49, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedNestedEnum.isNotEmpty()) {
        encoder.writePackedEnum(fieldNr = 51, value = repeatedNestedEnum.map { it.number }, fieldSize = WireSize.packedEnum(repeatedNestedEnum.map { it.number }))
    }

    if (repeatedForeignEnum.isNotEmpty()) {
        encoder.writePackedEnum(fieldNr = 52, value = repeatedForeignEnum.map { it.number }, fieldSize = WireSize.packedEnum(repeatedForeignEnum.map { it.number }))
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
        encoder.writePackedInt32(fieldNr = 75, value = packedInt32, fieldSize = WireSize.packedInt32(packedInt32))
    }

    if (packedInt64.isNotEmpty()) {
        encoder.writePackedInt64(fieldNr = 76, value = packedInt64, fieldSize = WireSize.packedInt64(packedInt64))
    }

    if (packedUint32.isNotEmpty()) {
        encoder.writePackedUInt32(fieldNr = 77, value = packedUint32, fieldSize = WireSize.packedUInt32(packedUint32))
    }

    if (packedUint64.isNotEmpty()) {
        encoder.writePackedUInt64(fieldNr = 78, value = packedUint64, fieldSize = WireSize.packedUInt64(packedUint64))
    }

    if (packedSint32.isNotEmpty()) {
        encoder.writePackedSInt32(fieldNr = 79, value = packedSint32, fieldSize = WireSize.packedSInt32(packedSint32))
    }

    if (packedSint64.isNotEmpty()) {
        encoder.writePackedSInt64(fieldNr = 80, value = packedSint64, fieldSize = WireSize.packedSInt64(packedSint64))
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
        encoder.writePackedBool(fieldNr = 87, value = packedBool, fieldSize = WireSize.packedBool(packedBool))
    }

    if (packedNestedEnum.isNotEmpty()) {
        encoder.writePackedEnum(fieldNr = 88, value = packedNestedEnum.map { it.number }, fieldSize = WireSize.packedEnum(packedNestedEnum.map { it.number }))
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
            TestAllTypesProto3Internal.MapInt32Int32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 56, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapInt64Int64.isNotEmpty()) {
        mapInt64Int64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapInt64Int64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 57, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapUint32Uint32.isNotEmpty()) {
        mapUint32Uint32.forEach { kEntry ->
            TestAllTypesProto3Internal.MapUint32Uint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 58, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapUint64Uint64.isNotEmpty()) {
        mapUint64Uint64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapUint64Uint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 59, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapSint32Sint32.isNotEmpty()) {
        mapSint32Sint32.forEach { kEntry ->
            TestAllTypesProto3Internal.MapSint32Sint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 60, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapSint64Sint64.isNotEmpty()) {
        mapSint64Sint64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapSint64Sint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 61, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapFixed32Fixed32.isNotEmpty()) {
        mapFixed32Fixed32.forEach { kEntry ->
            TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 62, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapFixed64Fixed64.isNotEmpty()) {
        mapFixed64Fixed64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 63, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapSfixed32Sfixed32.isNotEmpty()) {
        mapSfixed32Sfixed32.forEach { kEntry ->
            TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 64, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapSfixed64Sfixed64.isNotEmpty()) {
        mapSfixed64Sfixed64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 65, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapInt32Float.isNotEmpty()) {
        mapInt32Float.forEach { kEntry ->
            TestAllTypesProto3Internal.MapInt32FloatEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 66, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapInt32Double.isNotEmpty()) {
        mapInt32Double.forEach { kEntry ->
            TestAllTypesProto3Internal.MapInt32DoubleEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 67, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapBoolBool.isNotEmpty()) {
        mapBoolBool.forEach { kEntry ->
            TestAllTypesProto3Internal.MapBoolBoolEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 68, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapStringString.isNotEmpty()) {
        mapStringString.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringStringEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 69, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapStringBytes.isNotEmpty()) {
        mapStringBytes.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringBytesEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 70, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapStringNestedMessage.isNotEmpty()) {
        mapStringNestedMessage.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 71, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapStringForeignMessage.isNotEmpty()) {
        mapStringForeignMessage.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 72, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapStringNestedEnum.isNotEmpty()) {
        mapStringNestedEnum.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 73, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (mapStringForeignEnum.isNotEmpty()) {
        mapStringForeignEnum.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 74, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (presenceMask[3]) {
        encoder.writeMessage(fieldNr = 201, value = optionalBoolWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[4]) {
        encoder.writeMessage(fieldNr = 202, value = optionalInt32Wrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[5]) {
        encoder.writeMessage(fieldNr = 203, value = optionalInt64Wrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[6]) {
        encoder.writeMessage(fieldNr = 204, value = optionalUint32Wrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[7]) {
        encoder.writeMessage(fieldNr = 205, value = optionalUint64Wrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[8]) {
        encoder.writeMessage(fieldNr = 206, value = optionalFloatWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[9]) {
        encoder.writeMessage(fieldNr = 207, value = optionalDoubleWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[10]) {
        encoder.writeMessage(fieldNr = 208, value = optionalStringWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[11]) {
        encoder.writeMessage(fieldNr = 209, value = optionalBytesWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (repeatedBoolWrapper.isNotEmpty()) {
        repeatedBoolWrapper.forEach {
            encoder.writeMessage(fieldNr = 211, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedInt32Wrapper.isNotEmpty()) {
        repeatedInt32Wrapper.forEach {
            encoder.writeMessage(fieldNr = 212, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedInt64Wrapper.isNotEmpty()) {
        repeatedInt64Wrapper.forEach {
            encoder.writeMessage(fieldNr = 213, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedUint32Wrapper.isNotEmpty()) {
        repeatedUint32Wrapper.forEach {
            encoder.writeMessage(fieldNr = 214, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedUint64Wrapper.isNotEmpty()) {
        repeatedUint64Wrapper.forEach {
            encoder.writeMessage(fieldNr = 215, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedFloatWrapper.isNotEmpty()) {
        repeatedFloatWrapper.forEach {
            encoder.writeMessage(fieldNr = 216, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedDoubleWrapper.isNotEmpty()) {
        repeatedDoubleWrapper.forEach {
            encoder.writeMessage(fieldNr = 217, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedStringWrapper.isNotEmpty()) {
        repeatedStringWrapper.forEach {
            encoder.writeMessage(fieldNr = 218, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedBytesWrapper.isNotEmpty()) {
        repeatedBytesWrapper.forEach {
            encoder.writeMessage(fieldNr = 219, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (presenceMask[12]) {
        encoder.writeMessage(fieldNr = 301, value = optionalDuration.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[13]) {
        encoder.writeMessage(fieldNr = 302, value = optionalTimestamp.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[14]) {
        encoder.writeMessage(fieldNr = 303, value = optionalFieldMask.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[15]) {
        encoder.writeMessage(fieldNr = 304, value = optionalStruct.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[16]) {
        encoder.writeMessage(fieldNr = 305, value = optionalAny.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[17]) {
        encoder.writeMessage(fieldNr = 306, value = optionalValue.asInternal()) { encodeWith(it, config) }
    }

    if (optionalNullValue != NullValue.NULL_VALUE) {
        encoder.writeEnum(fieldNr = 307, value = optionalNullValue.number)
    }

    if (repeatedDuration.isNotEmpty()) {
        repeatedDuration.forEach {
            encoder.writeMessage(fieldNr = 311, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedTimestamp.isNotEmpty()) {
        repeatedTimestamp.forEach {
            encoder.writeMessage(fieldNr = 312, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedFieldmask.isNotEmpty()) {
        repeatedFieldmask.forEach {
            encoder.writeMessage(fieldNr = 313, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedStruct.isNotEmpty()) {
        repeatedStruct.forEach {
            encoder.writeMessage(fieldNr = 324, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedAny.isNotEmpty()) {
        repeatedAny.forEach {
            encoder.writeMessage(fieldNr = 315, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedValue.isNotEmpty()) {
        repeatedValue.forEach {
            encoder.writeMessage(fieldNr = 316, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (repeatedListValue.isNotEmpty()) {
        repeatedListValue.forEach {
            encoder.writeMessage(fieldNr = 317, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (fieldname1 != 0) {
        encoder.writeInt32(fieldNr = 401, value = fieldname1)
    }

    if (fieldName2 != 0) {
        encoder.writeInt32(fieldNr = 402, value = fieldName2)
    }

    if (FieldName3 != 0) {
        encoder.writeInt32(fieldNr = 403, value = FieldName3)
    }

    if (field_Name4_ != 0) {
        encoder.writeInt32(fieldNr = 404, value = field_Name4_)
    }

    if (field0name5 != 0) {
        encoder.writeInt32(fieldNr = 405, value = field0name5)
    }

    if (field_0Name6 != 0) {
        encoder.writeInt32(fieldNr = 406, value = field_0Name6)
    }

    if (fieldName7 != 0) {
        encoder.writeInt32(fieldNr = 407, value = fieldName7)
    }

    if (FieldName8 != 0) {
        encoder.writeInt32(fieldNr = 408, value = FieldName8)
    }

    if (field_Name9 != 0) {
        encoder.writeInt32(fieldNr = 409, value = field_Name9)
    }

    if (Field_Name10 != 0) {
        encoder.writeInt32(fieldNr = 410, value = Field_Name10)
    }

    if (FIELD_NAME11 != 0) {
        encoder.writeInt32(fieldNr = 411, value = FIELD_NAME11)
    }

    if (FIELDName12 != 0) {
        encoder.writeInt32(fieldNr = 412, value = FIELDName12)
    }

    if (_FieldName13 != 0) {
        encoder.writeInt32(fieldNr = 413, value = _FieldName13)
    }

    if (__FieldName14 != 0) {
        encoder.writeInt32(fieldNr = 414, value = __FieldName14)
    }

    if (field_Name15 != 0) {
        encoder.writeInt32(fieldNr = 415, value = field_Name15)
    }

    if (field__Name16 != 0) {
        encoder.writeInt32(fieldNr = 416, value = field__Name16)
    }

    if (fieldName17__ != 0) {
        encoder.writeInt32(fieldNr = 417, value = fieldName17__)
    }

    if (FieldName18__ != 0) {
        encoder.writeInt32(fieldNr = 418, value = FieldName18__)
    }

    oneofField?.also {
        when (val value = it) {
            is TestAllTypesProto3.OneofField.OneofUint32 -> {
                encoder.writeUInt32(fieldNr = 111, value = value.value)
            }
            is TestAllTypesProto3.OneofField.OneofNestedMessage -> {
                encoder.writeMessage(fieldNr = 112, value = value.value.asInternal()) { encodeWith(it, config) }
            }
            is TestAllTypesProto3.OneofField.OneofString -> {
                encoder.writeString(fieldNr = 113, value = value.value)
            }
            is TestAllTypesProto3.OneofField.OneofBytes -> {
                encoder.writeBytes(fieldNr = 114, value = value.value)
            }
            is TestAllTypesProto3.OneofField.OneofBool -> {
                encoder.writeBool(fieldNr = 115, value = value.value)
            }
            is TestAllTypesProto3.OneofField.OneofUint64 -> {
                encoder.writeUInt64(fieldNr = 116, value = value.value)
            }
            is TestAllTypesProto3.OneofField.OneofFloat -> {
                encoder.writeFloat(fieldNr = 117, value = value.value)
            }
            is TestAllTypesProto3.OneofField.OneofDouble -> {
                encoder.writeDouble(fieldNr = 118, value = value.value)
            }
            is TestAllTypesProto3.OneofField.OneofEnum -> {
                encoder.writeEnum(fieldNr = 119, value = value.value.number)
            }
            is TestAllTypesProto3.OneofField.OneofNullValue -> {
                encoder.writeEnum(fieldNr = 120, value = value.value.number)
            }
        }
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.Companion.decodeWith(msg: TestAllTypesProto3Internal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.optionalInt32 = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.optionalInt64 = decoder.readInt64()
            }
            tag.fieldNr == 3 && tag.wireType == WireType.VARINT -> {
                msg.optionalUint32 = decoder.readUInt32()
            }
            tag.fieldNr == 4 && tag.wireType == WireType.VARINT -> {
                msg.optionalUint64 = decoder.readUInt64()
            }
            tag.fieldNr == 5 && tag.wireType == WireType.VARINT -> {
                msg.optionalSint32 = decoder.readSInt32()
            }
            tag.fieldNr == 6 && tag.wireType == WireType.VARINT -> {
                msg.optionalSint64 = decoder.readSInt64()
            }
            tag.fieldNr == 7 && tag.wireType == WireType.FIXED32 -> {
                msg.optionalFixed32 = decoder.readFixed32()
            }
            tag.fieldNr == 8 && tag.wireType == WireType.FIXED64 -> {
                msg.optionalFixed64 = decoder.readFixed64()
            }
            tag.fieldNr == 9 && tag.wireType == WireType.FIXED32 -> {
                msg.optionalSfixed32 = decoder.readSFixed32()
            }
            tag.fieldNr == 10 && tag.wireType == WireType.FIXED64 -> {
                msg.optionalSfixed64 = decoder.readSFixed64()
            }
            tag.fieldNr == 11 && tag.wireType == WireType.FIXED32 -> {
                msg.optionalFloat = decoder.readFloat()
            }
            tag.fieldNr == 12 && tag.wireType == WireType.FIXED64 -> {
                msg.optionalDouble = decoder.readDouble()
            }
            tag.fieldNr == 13 && tag.wireType == WireType.VARINT -> {
                msg.optionalBool = decoder.readBool()
            }
            tag.fieldNr == 14 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.optionalString = decoder.readString()
            }
            tag.fieldNr == 15 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.optionalBytes = decoder.readBytes()
            }
            tag.fieldNr == 18 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.optionalNestedMessage = TestAllTypesProto3Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.optionalNestedMessage.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 19 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[1]) {
                    msg.optionalForeignMessage = ForeignMessageInternal()
                }

                decoder.readMessage(msg.optionalForeignMessage.asInternal(), { msg, decoder -> ForeignMessageInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 21 && tag.wireType == WireType.VARINT -> {
                msg.optionalNestedEnum = TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 22 && tag.wireType == WireType.VARINT -> {
                msg.optionalForeignEnum = ForeignEnum.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 23 && tag.wireType == WireType.VARINT -> {
                msg.optionalAliasedEnum = TestAllTypesProto3.AliasedEnum.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 24 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.optionalStringPiece = decoder.readString()
            }
            tag.fieldNr == 25 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.optionalCord = decoder.readString()
            }
            tag.fieldNr == 27 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[2]) {
                    msg.recursiveMessage = TestAllTypesProto3Internal()
                }

                decoder.readMessage(msg.recursiveMessage.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 31 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedInt32 += decoder.readPackedInt32()
            }
            tag.fieldNr == 31 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readInt32()
                (msg.repeatedInt32 as MutableList).add(elem)
            }
            tag.fieldNr == 32 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedInt64 += decoder.readPackedInt64()
            }
            tag.fieldNr == 32 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readInt64()
                (msg.repeatedInt64 as MutableList).add(elem)
            }
            tag.fieldNr == 33 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedUint32 += decoder.readPackedUInt32()
            }
            tag.fieldNr == 33 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readUInt32()
                (msg.repeatedUint32 as MutableList).add(elem)
            }
            tag.fieldNr == 34 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedUint64 += decoder.readPackedUInt64()
            }
            tag.fieldNr == 34 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readUInt64()
                (msg.repeatedUint64 as MutableList).add(elem)
            }
            tag.fieldNr == 35 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedSint32 += decoder.readPackedSInt32()
            }
            tag.fieldNr == 35 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readSInt32()
                (msg.repeatedSint32 as MutableList).add(elem)
            }
            tag.fieldNr == 36 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedSint64 += decoder.readPackedSInt64()
            }
            tag.fieldNr == 36 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readSInt64()
                (msg.repeatedSint64 as MutableList).add(elem)
            }
            tag.fieldNr == 37 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedFixed32 += decoder.readPackedFixed32()
            }
            tag.fieldNr == 37 && tag.wireType == WireType.FIXED32 -> {
                val elem = decoder.readFixed32()
                (msg.repeatedFixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 38 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedFixed64 += decoder.readPackedFixed64()
            }
            tag.fieldNr == 38 && tag.wireType == WireType.FIXED64 -> {
                val elem = decoder.readFixed64()
                (msg.repeatedFixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 39 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedSfixed32 += decoder.readPackedSFixed32()
            }
            tag.fieldNr == 39 && tag.wireType == WireType.FIXED32 -> {
                val elem = decoder.readSFixed32()
                (msg.repeatedSfixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 40 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedSfixed64 += decoder.readPackedSFixed64()
            }
            tag.fieldNr == 40 && tag.wireType == WireType.FIXED64 -> {
                val elem = decoder.readSFixed64()
                (msg.repeatedSfixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 41 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedFloat += decoder.readPackedFloat()
            }
            tag.fieldNr == 41 && tag.wireType == WireType.FIXED32 -> {
                val elem = decoder.readFloat()
                (msg.repeatedFloat as MutableList).add(elem)
            }
            tag.fieldNr == 42 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedDouble += decoder.readPackedDouble()
            }
            tag.fieldNr == 42 && tag.wireType == WireType.FIXED64 -> {
                val elem = decoder.readDouble()
                (msg.repeatedDouble as MutableList).add(elem)
            }
            tag.fieldNr == 43 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedBool += decoder.readPackedBool()
            }
            tag.fieldNr == 43 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readBool()
                (msg.repeatedBool as MutableList).add(elem)
            }
            tag.fieldNr == 44 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = decoder.readString()
                (msg.repeatedString as MutableList).add(elem)
            }
            tag.fieldNr == 45 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = decoder.readBytes()
                (msg.repeatedBytes as MutableList).add(elem)
            }
            tag.fieldNr == 48 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = TestAllTypesProto3Internal.NestedMessageInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedNestedMessage as MutableList).add(elem)
            }
            tag.fieldNr == 49 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = ForeignMessageInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> ForeignMessageInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedForeignMessage as MutableList).add(elem)
            }
            tag.fieldNr == 51 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedNestedEnum += decoder.readPackedEnum().map { TestAllTypesProto3.NestedEnum.fromNumber(it) }
            }
            tag.fieldNr == 51 && tag.wireType == WireType.VARINT -> {
                val elem = TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
                (msg.repeatedNestedEnum as MutableList).add(elem)
            }
            tag.fieldNr == 52 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedForeignEnum += decoder.readPackedEnum().map { ForeignEnum.fromNumber(it) }
            }
            tag.fieldNr == 52 && tag.wireType == WireType.VARINT -> {
                val elem = ForeignEnum.fromNumber(decoder.readEnum())
                (msg.repeatedForeignEnum as MutableList).add(elem)
            }
            tag.fieldNr == 54 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = decoder.readString()
                (msg.repeatedStringPiece as MutableList).add(elem)
            }
            tag.fieldNr == 55 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = decoder.readString()
                (msg.repeatedCord as MutableList).add(elem)
            }
            tag.fieldNr == 75 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedInt32 += decoder.readPackedInt32()
            }
            tag.fieldNr == 75 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readInt32()
                (msg.packedInt32 as MutableList).add(elem)
            }
            tag.fieldNr == 76 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedInt64 += decoder.readPackedInt64()
            }
            tag.fieldNr == 76 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readInt64()
                (msg.packedInt64 as MutableList).add(elem)
            }
            tag.fieldNr == 77 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedUint32 += decoder.readPackedUInt32()
            }
            tag.fieldNr == 77 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readUInt32()
                (msg.packedUint32 as MutableList).add(elem)
            }
            tag.fieldNr == 78 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedUint64 += decoder.readPackedUInt64()
            }
            tag.fieldNr == 78 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readUInt64()
                (msg.packedUint64 as MutableList).add(elem)
            }
            tag.fieldNr == 79 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedSint32 += decoder.readPackedSInt32()
            }
            tag.fieldNr == 79 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readSInt32()
                (msg.packedSint32 as MutableList).add(elem)
            }
            tag.fieldNr == 80 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedSint64 += decoder.readPackedSInt64()
            }
            tag.fieldNr == 80 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readSInt64()
                (msg.packedSint64 as MutableList).add(elem)
            }
            tag.fieldNr == 81 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedFixed32 += decoder.readPackedFixed32()
            }
            tag.fieldNr == 81 && tag.wireType == WireType.FIXED32 -> {
                val elem = decoder.readFixed32()
                (msg.packedFixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 82 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedFixed64 += decoder.readPackedFixed64()
            }
            tag.fieldNr == 82 && tag.wireType == WireType.FIXED64 -> {
                val elem = decoder.readFixed64()
                (msg.packedFixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 83 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedSfixed32 += decoder.readPackedSFixed32()
            }
            tag.fieldNr == 83 && tag.wireType == WireType.FIXED32 -> {
                val elem = decoder.readSFixed32()
                (msg.packedSfixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 84 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedSfixed64 += decoder.readPackedSFixed64()
            }
            tag.fieldNr == 84 && tag.wireType == WireType.FIXED64 -> {
                val elem = decoder.readSFixed64()
                (msg.packedSfixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 85 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedFloat += decoder.readPackedFloat()
            }
            tag.fieldNr == 85 && tag.wireType == WireType.FIXED32 -> {
                val elem = decoder.readFloat()
                (msg.packedFloat as MutableList).add(elem)
            }
            tag.fieldNr == 86 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedDouble += decoder.readPackedDouble()
            }
            tag.fieldNr == 86 && tag.wireType == WireType.FIXED64 -> {
                val elem = decoder.readDouble()
                (msg.packedDouble as MutableList).add(elem)
            }
            tag.fieldNr == 87 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedBool += decoder.readPackedBool()
            }
            tag.fieldNr == 87 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readBool()
                (msg.packedBool as MutableList).add(elem)
            }
            tag.fieldNr == 88 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.packedNestedEnum += decoder.readPackedEnum().map { TestAllTypesProto3.NestedEnum.fromNumber(it) }
            }
            tag.fieldNr == 88 && tag.wireType == WireType.VARINT -> {
                val elem = TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
                (msg.packedNestedEnum as MutableList).add(elem)
            }
            tag.fieldNr == 89 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedInt32 += decoder.readPackedInt32()
            }
            tag.fieldNr == 89 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readInt32()
                (msg.unpackedInt32 as MutableList).add(elem)
            }
            tag.fieldNr == 90 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedInt64 += decoder.readPackedInt64()
            }
            tag.fieldNr == 90 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readInt64()
                (msg.unpackedInt64 as MutableList).add(elem)
            }
            tag.fieldNr == 91 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedUint32 += decoder.readPackedUInt32()
            }
            tag.fieldNr == 91 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readUInt32()
                (msg.unpackedUint32 as MutableList).add(elem)
            }
            tag.fieldNr == 92 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedUint64 += decoder.readPackedUInt64()
            }
            tag.fieldNr == 92 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readUInt64()
                (msg.unpackedUint64 as MutableList).add(elem)
            }
            tag.fieldNr == 93 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedSint32 += decoder.readPackedSInt32()
            }
            tag.fieldNr == 93 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readSInt32()
                (msg.unpackedSint32 as MutableList).add(elem)
            }
            tag.fieldNr == 94 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedSint64 += decoder.readPackedSInt64()
            }
            tag.fieldNr == 94 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readSInt64()
                (msg.unpackedSint64 as MutableList).add(elem)
            }
            tag.fieldNr == 95 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedFixed32 += decoder.readPackedFixed32()
            }
            tag.fieldNr == 95 && tag.wireType == WireType.FIXED32 -> {
                val elem = decoder.readFixed32()
                (msg.unpackedFixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 96 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedFixed64 += decoder.readPackedFixed64()
            }
            tag.fieldNr == 96 && tag.wireType == WireType.FIXED64 -> {
                val elem = decoder.readFixed64()
                (msg.unpackedFixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 97 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedSfixed32 += decoder.readPackedSFixed32()
            }
            tag.fieldNr == 97 && tag.wireType == WireType.FIXED32 -> {
                val elem = decoder.readSFixed32()
                (msg.unpackedSfixed32 as MutableList).add(elem)
            }
            tag.fieldNr == 98 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedSfixed64 += decoder.readPackedSFixed64()
            }
            tag.fieldNr == 98 && tag.wireType == WireType.FIXED64 -> {
                val elem = decoder.readSFixed64()
                (msg.unpackedSfixed64 as MutableList).add(elem)
            }
            tag.fieldNr == 99 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedFloat += decoder.readPackedFloat()
            }
            tag.fieldNr == 99 && tag.wireType == WireType.FIXED32 -> {
                val elem = decoder.readFloat()
                (msg.unpackedFloat as MutableList).add(elem)
            }
            tag.fieldNr == 100 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedDouble += decoder.readPackedDouble()
            }
            tag.fieldNr == 100 && tag.wireType == WireType.FIXED64 -> {
                val elem = decoder.readDouble()
                (msg.unpackedDouble as MutableList).add(elem)
            }
            tag.fieldNr == 101 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedBool += decoder.readPackedBool()
            }
            tag.fieldNr == 101 && tag.wireType == WireType.VARINT -> {
                val elem = decoder.readBool()
                (msg.unpackedBool as MutableList).add(elem)
            }
            tag.fieldNr == 102 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.unpackedNestedEnum += decoder.readPackedEnum().map { TestAllTypesProto3.NestedEnum.fromNumber(it) }
            }
            tag.fieldNr == 102 && tag.wireType == WireType.VARINT -> {
                val elem = TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
                (msg.unpackedNestedEnum as MutableList).add(elem)
            }
            tag.fieldNr == 56 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapInt32Int32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapInt32Int32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapInt32Int32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 57 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapInt64Int64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapInt64Int64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapInt64Int64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 58 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapUint32Uint32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapUint32Uint32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 59 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapUint64Uint64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapUint64Uint64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 60 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapSint32Sint32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapSint32Sint32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 61 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapSint64Sint64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapSint64Sint64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 62 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapFixed32Fixed32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 63 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapFixed64Fixed64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 64 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapSfixed32Sfixed32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 65 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapSfixed64Sfixed64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 66 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapInt32FloatEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapInt32FloatEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapInt32Float as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 67 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapInt32DoubleEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapInt32Double as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 68 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapBoolBoolEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapBoolBoolEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapBoolBool as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 69 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapStringStringEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapStringStringEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringString as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 70 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapStringBytesEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapStringBytesEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringBytes as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 71 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringNestedMessage as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 72 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringForeignMessage as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 73 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringNestedEnum as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 74 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringForeignEnum as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 201 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[3]) {
                    msg.optionalBoolWrapper = BoolValueInternal()
                }

                decoder.readMessage(msg.optionalBoolWrapper.asInternal(), { msg, decoder -> BoolValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 202 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[4]) {
                    msg.optionalInt32Wrapper = Int32ValueInternal()
                }

                decoder.readMessage(msg.optionalInt32Wrapper.asInternal(), { msg, decoder -> Int32ValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 203 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[5]) {
                    msg.optionalInt64Wrapper = Int64ValueInternal()
                }

                decoder.readMessage(msg.optionalInt64Wrapper.asInternal(), { msg, decoder -> Int64ValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 204 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[6]) {
                    msg.optionalUint32Wrapper = UInt32ValueInternal()
                }

                decoder.readMessage(msg.optionalUint32Wrapper.asInternal(), { msg, decoder -> UInt32ValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 205 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[7]) {
                    msg.optionalUint64Wrapper = UInt64ValueInternal()
                }

                decoder.readMessage(msg.optionalUint64Wrapper.asInternal(), { msg, decoder -> UInt64ValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 206 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[8]) {
                    msg.optionalFloatWrapper = FloatValueInternal()
                }

                decoder.readMessage(msg.optionalFloatWrapper.asInternal(), { msg, decoder -> FloatValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 207 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[9]) {
                    msg.optionalDoubleWrapper = DoubleValueInternal()
                }

                decoder.readMessage(msg.optionalDoubleWrapper.asInternal(), { msg, decoder -> DoubleValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 208 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[10]) {
                    msg.optionalStringWrapper = StringValueInternal()
                }

                decoder.readMessage(msg.optionalStringWrapper.asInternal(), { msg, decoder -> StringValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 209 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[11]) {
                    msg.optionalBytesWrapper = BytesValueInternal()
                }

                decoder.readMessage(msg.optionalBytesWrapper.asInternal(), { msg, decoder -> BytesValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 211 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = BoolValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> BoolValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedBoolWrapper as MutableList).add(elem)
            }
            tag.fieldNr == 212 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = Int32ValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> Int32ValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedInt32Wrapper as MutableList).add(elem)
            }
            tag.fieldNr == 213 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = Int64ValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> Int64ValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedInt64Wrapper as MutableList).add(elem)
            }
            tag.fieldNr == 214 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = UInt32ValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> UInt32ValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedUint32Wrapper as MutableList).add(elem)
            }
            tag.fieldNr == 215 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = UInt64ValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> UInt64ValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedUint64Wrapper as MutableList).add(elem)
            }
            tag.fieldNr == 216 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = FloatValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> FloatValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedFloatWrapper as MutableList).add(elem)
            }
            tag.fieldNr == 217 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = DoubleValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> DoubleValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedDoubleWrapper as MutableList).add(elem)
            }
            tag.fieldNr == 218 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = StringValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> StringValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedStringWrapper as MutableList).add(elem)
            }
            tag.fieldNr == 219 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = BytesValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> BytesValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedBytesWrapper as MutableList).add(elem)
            }
            tag.fieldNr == 301 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[12]) {
                    msg.optionalDuration = DurationInternal()
                }

                decoder.readMessage(msg.optionalDuration.asInternal(), { msg, decoder -> DurationInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 302 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[13]) {
                    msg.optionalTimestamp = TimestampInternal()
                }

                decoder.readMessage(msg.optionalTimestamp.asInternal(), { msg, decoder -> TimestampInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 303 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[14]) {
                    msg.optionalFieldMask = FieldMaskInternal()
                }

                decoder.readMessage(msg.optionalFieldMask.asInternal(), { msg, decoder -> FieldMaskInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 304 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[15]) {
                    msg.optionalStruct = StructInternal()
                }

                decoder.readMessage(msg.optionalStruct.asInternal(), { msg, decoder -> StructInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 305 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[16]) {
                    msg.optionalAny = AnyInternal()
                }

                decoder.readMessage(msg.optionalAny.asInternal(), { msg, decoder -> AnyInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 306 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[17]) {
                    msg.optionalValue = ValueInternal()
                }

                decoder.readMessage(msg.optionalValue.asInternal(), { msg, decoder -> ValueInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 307 && tag.wireType == WireType.VARINT -> {
                msg.optionalNullValue = NullValue.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 311 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = DurationInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> DurationInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedDuration as MutableList).add(elem)
            }
            tag.fieldNr == 312 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = TimestampInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> TimestampInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedTimestamp as MutableList).add(elem)
            }
            tag.fieldNr == 313 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = FieldMaskInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> FieldMaskInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedFieldmask as MutableList).add(elem)
            }
            tag.fieldNr == 324 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = StructInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> StructInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedStruct as MutableList).add(elem)
            }
            tag.fieldNr == 315 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = AnyInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> AnyInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedAny as MutableList).add(elem)
            }
            tag.fieldNr == 316 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = ValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> ValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedValue as MutableList).add(elem)
            }
            tag.fieldNr == 317 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = ListValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> ListValueInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedListValue as MutableList).add(elem)
            }
            tag.fieldNr == 401 && tag.wireType == WireType.VARINT -> {
                msg.fieldname1 = decoder.readInt32()
            }
            tag.fieldNr == 402 && tag.wireType == WireType.VARINT -> {
                msg.fieldName2 = decoder.readInt32()
            }
            tag.fieldNr == 403 && tag.wireType == WireType.VARINT -> {
                msg.FieldName3 = decoder.readInt32()
            }
            tag.fieldNr == 404 && tag.wireType == WireType.VARINT -> {
                msg.field_Name4_ = decoder.readInt32()
            }
            tag.fieldNr == 405 && tag.wireType == WireType.VARINT -> {
                msg.field0name5 = decoder.readInt32()
            }
            tag.fieldNr == 406 && tag.wireType == WireType.VARINT -> {
                msg.field_0Name6 = decoder.readInt32()
            }
            tag.fieldNr == 407 && tag.wireType == WireType.VARINT -> {
                msg.fieldName7 = decoder.readInt32()
            }
            tag.fieldNr == 408 && tag.wireType == WireType.VARINT -> {
                msg.FieldName8 = decoder.readInt32()
            }
            tag.fieldNr == 409 && tag.wireType == WireType.VARINT -> {
                msg.field_Name9 = decoder.readInt32()
            }
            tag.fieldNr == 410 && tag.wireType == WireType.VARINT -> {
                msg.Field_Name10 = decoder.readInt32()
            }
            tag.fieldNr == 411 && tag.wireType == WireType.VARINT -> {
                msg.FIELD_NAME11 = decoder.readInt32()
            }
            tag.fieldNr == 412 && tag.wireType == WireType.VARINT -> {
                msg.FIELDName12 = decoder.readInt32()
            }
            tag.fieldNr == 413 && tag.wireType == WireType.VARINT -> {
                msg._FieldName13 = decoder.readInt32()
            }
            tag.fieldNr == 414 && tag.wireType == WireType.VARINT -> {
                msg.__FieldName14 = decoder.readInt32()
            }
            tag.fieldNr == 415 && tag.wireType == WireType.VARINT -> {
                msg.field_Name15 = decoder.readInt32()
            }
            tag.fieldNr == 416 && tag.wireType == WireType.VARINT -> {
                msg.field__Name16 = decoder.readInt32()
            }
            tag.fieldNr == 417 && tag.wireType == WireType.VARINT -> {
                msg.fieldName17__ = decoder.readInt32()
            }
            tag.fieldNr == 418 && tag.wireType == WireType.VARINT -> {
                msg.FieldName18__ = decoder.readInt32()
            }
            tag.fieldNr == 111 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto3.OneofField.OneofUint32(decoder.readUInt32())
            }
            tag.fieldNr == 112 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.oneofField as? TestAllTypesProto3.OneofField.OneofNestedMessage) ?: TestAllTypesProto3.OneofField.OneofNestedMessage(TestAllTypesProto3Internal.NestedMessageInternal()).also {
                    msg.oneofField = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 113 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.oneofField = TestAllTypesProto3.OneofField.OneofString(decoder.readString())
            }
            tag.fieldNr == 114 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.oneofField = TestAllTypesProto3.OneofField.OneofBytes(decoder.readBytes())
            }
            tag.fieldNr == 115 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto3.OneofField.OneofBool(decoder.readBool())
            }
            tag.fieldNr == 116 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto3.OneofField.OneofUint64(decoder.readUInt64())
            }
            tag.fieldNr == 117 && tag.wireType == WireType.FIXED32 -> {
                msg.oneofField = TestAllTypesProto3.OneofField.OneofFloat(decoder.readFloat())
            }
            tag.fieldNr == 118 && tag.wireType == WireType.FIXED64 -> {
                msg.oneofField = TestAllTypesProto3.OneofField.OneofDouble(decoder.readDouble())
            }
            tag.fieldNr == 119 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto3.OneofField.OneofEnum(TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum()))
            }
            tag.fieldNr == 120 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto3.OneofField.OneofNullValue(NullValue.fromNumber(decoder.readEnum()))
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.computeSize(): Int {
    var __result = 0
    if (optionalInt32 != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(optionalInt32))
    }

    if (optionalInt64 != 0L) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int64(optionalInt64))
    }

    if (optionalUint32 != 0u) {
        __result += (WireSize.tag(3, WireType.VARINT) + WireSize.uInt32(optionalUint32))
    }

    if (optionalUint64 != 0uL) {
        __result += (WireSize.tag(4, WireType.VARINT) + WireSize.uInt64(optionalUint64))
    }

    if (optionalSint32 != 0) {
        __result += (WireSize.tag(5, WireType.VARINT) + WireSize.sInt32(optionalSint32))
    }

    if (optionalSint64 != 0L) {
        __result += (WireSize.tag(6, WireType.VARINT) + WireSize.sInt64(optionalSint64))
    }

    if (optionalFixed32 != 0u) {
        __result += (WireSize.tag(7, WireType.FIXED32) + WireSize.fixed32(optionalFixed32))
    }

    if (optionalFixed64 != 0uL) {
        __result += (WireSize.tag(8, WireType.FIXED64) + WireSize.fixed64(optionalFixed64))
    }

    if (optionalSfixed32 != 0) {
        __result += (WireSize.tag(9, WireType.FIXED32) + WireSize.sFixed32(optionalSfixed32))
    }

    if (optionalSfixed64 != 0L) {
        __result += (WireSize.tag(10, WireType.FIXED64) + WireSize.sFixed64(optionalSfixed64))
    }

    if (optionalFloat != 0.0f) {
        __result += (WireSize.tag(11, WireType.FIXED32) + WireSize.float(optionalFloat))
    }

    if (optionalDouble != 0.0) {
        __result += (WireSize.tag(12, WireType.FIXED64) + WireSize.double(optionalDouble))
    }

    if (optionalBool != false) {
        __result += (WireSize.tag(13, WireType.VARINT) + WireSize.bool(optionalBool))
    }

    if (optionalString.isNotEmpty()) {
        __result += WireSize.string(optionalString).let { WireSize.tag(14, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (optionalBytes.isNotEmpty()) {
        __result += WireSize.bytes(optionalBytes).let { WireSize.tag(15, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += optionalNestedMessage.asInternal()._size.let { WireSize.tag(18, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[1]) {
        __result += optionalForeignMessage.asInternal()._size.let { WireSize.tag(19, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (optionalNestedEnum != TestAllTypesProto3.NestedEnum.FOO) {
        __result += (WireSize.tag(21, WireType.VARINT) + WireSize.enum(optionalNestedEnum.number))
    }

    if (optionalForeignEnum != ForeignEnum.FOREIGN_FOO) {
        __result += (WireSize.tag(22, WireType.VARINT) + WireSize.enum(optionalForeignEnum.number))
    }

    if (optionalAliasedEnum != TestAllTypesProto3.AliasedEnum.ALIAS_FOO) {
        __result += (WireSize.tag(23, WireType.VARINT) + WireSize.enum(optionalAliasedEnum.number))
    }

    if (optionalStringPiece.isNotEmpty()) {
        __result += WireSize.string(optionalStringPiece).let { WireSize.tag(24, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (optionalCord.isNotEmpty()) {
        __result += WireSize.string(optionalCord).let { WireSize.tag(25, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[2]) {
        __result += recursiveMessage.asInternal()._size.let { WireSize.tag(27, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (repeatedInt32.isNotEmpty()) {
        __result += WireSize.packedInt32(repeatedInt32).let { WireSize.tag(31, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (repeatedInt64.isNotEmpty()) {
        __result += WireSize.packedInt64(repeatedInt64).let { WireSize.tag(32, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (repeatedUint32.isNotEmpty()) {
        __result += WireSize.packedUInt32(repeatedUint32).let { WireSize.tag(33, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (repeatedUint64.isNotEmpty()) {
        __result += WireSize.packedUInt64(repeatedUint64).let { WireSize.tag(34, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (repeatedSint32.isNotEmpty()) {
        __result += WireSize.packedSInt32(repeatedSint32).let { WireSize.tag(35, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (repeatedSint64.isNotEmpty()) {
        __result += WireSize.packedSInt64(repeatedSint64).let { WireSize.tag(36, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (repeatedFixed32.isNotEmpty()) {
        __result += WireSize.packedFixed32(repeatedFixed32).let { WireSize.tag(37, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (repeatedFixed64.isNotEmpty()) {
        __result += WireSize.packedFixed64(repeatedFixed64).let { WireSize.tag(38, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (repeatedSfixed32.isNotEmpty()) {
        __result += WireSize.packedSFixed32(repeatedSfixed32).let { WireSize.tag(39, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (repeatedSfixed64.isNotEmpty()) {
        __result += WireSize.packedSFixed64(repeatedSfixed64).let { WireSize.tag(40, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (repeatedFloat.isNotEmpty()) {
        __result += WireSize.packedFloat(repeatedFloat).let { WireSize.tag(41, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (repeatedDouble.isNotEmpty()) {
        __result += WireSize.packedDouble(repeatedDouble).let { WireSize.tag(42, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (repeatedBool.isNotEmpty()) {
        __result += WireSize.packedBool(repeatedBool).let { WireSize.tag(43, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (repeatedString.isNotEmpty()) {
        __result += repeatedString.sumOf { WireSize.string(it).let { WireSize.tag(44, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedBytes.isNotEmpty()) {
        __result += repeatedBytes.sumOf { WireSize.bytes(it).let { WireSize.tag(45, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedNestedMessage.isNotEmpty()) {
        __result += repeatedNestedMessage.sumOf { it.asInternal()._size.let { WireSize.tag(48, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedForeignMessage.isNotEmpty()) {
        __result += repeatedForeignMessage.sumOf { it.asInternal()._size.let { WireSize.tag(49, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedNestedEnum.isNotEmpty()) {
        __result += WireSize.packedEnum(repeatedNestedEnum.map { it.number }).let { WireSize.tag(51, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (repeatedForeignEnum.isNotEmpty()) {
        __result += WireSize.packedEnum(repeatedForeignEnum.map { it.number }).let { WireSize.tag(52, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (repeatedStringPiece.isNotEmpty()) {
        __result += repeatedStringPiece.sumOf { WireSize.string(it).let { WireSize.tag(54, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedCord.isNotEmpty()) {
        __result += repeatedCord.sumOf { WireSize.string(it).let { WireSize.tag(55, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (packedInt32.isNotEmpty()) {
        __result += WireSize.packedInt32(packedInt32).let { WireSize.tag(75, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (packedInt64.isNotEmpty()) {
        __result += WireSize.packedInt64(packedInt64).let { WireSize.tag(76, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (packedUint32.isNotEmpty()) {
        __result += WireSize.packedUInt32(packedUint32).let { WireSize.tag(77, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (packedUint64.isNotEmpty()) {
        __result += WireSize.packedUInt64(packedUint64).let { WireSize.tag(78, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (packedSint32.isNotEmpty()) {
        __result += WireSize.packedSInt32(packedSint32).let { WireSize.tag(79, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (packedSint64.isNotEmpty()) {
        __result += WireSize.packedSInt64(packedSint64).let { WireSize.tag(80, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (packedFixed32.isNotEmpty()) {
        __result += WireSize.packedFixed32(packedFixed32).let { WireSize.tag(81, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (packedFixed64.isNotEmpty()) {
        __result += WireSize.packedFixed64(packedFixed64).let { WireSize.tag(82, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (packedSfixed32.isNotEmpty()) {
        __result += WireSize.packedSFixed32(packedSfixed32).let { WireSize.tag(83, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (packedSfixed64.isNotEmpty()) {
        __result += WireSize.packedSFixed64(packedSfixed64).let { WireSize.tag(84, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (packedFloat.isNotEmpty()) {
        __result += WireSize.packedFloat(packedFloat).let { WireSize.tag(85, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (packedDouble.isNotEmpty()) {
        __result += WireSize.packedDouble(packedDouble).let { WireSize.tag(86, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (packedBool.isNotEmpty()) {
        __result += WireSize.packedBool(packedBool).let { WireSize.tag(87, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (packedNestedEnum.isNotEmpty()) {
        __result += WireSize.packedEnum(packedNestedEnum.map { it.number }).let { WireSize.tag(88, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (unpackedInt32.isNotEmpty()) {
        __result += unpackedInt32.sumOf { WireSize.tag(89, WireType.VARINT) + WireSize.int32(it) }
    }

    if (unpackedInt64.isNotEmpty()) {
        __result += unpackedInt64.sumOf { WireSize.tag(90, WireType.VARINT) + WireSize.int64(it) }
    }

    if (unpackedUint32.isNotEmpty()) {
        __result += unpackedUint32.sumOf { WireSize.tag(91, WireType.VARINT) + WireSize.uInt32(it) }
    }

    if (unpackedUint64.isNotEmpty()) {
        __result += unpackedUint64.sumOf { WireSize.tag(92, WireType.VARINT) + WireSize.uInt64(it) }
    }

    if (unpackedSint32.isNotEmpty()) {
        __result += unpackedSint32.sumOf { WireSize.tag(93, WireType.VARINT) + WireSize.sInt32(it) }
    }

    if (unpackedSint64.isNotEmpty()) {
        __result += unpackedSint64.sumOf { WireSize.tag(94, WireType.VARINT) + WireSize.sInt64(it) }
    }

    if (unpackedFixed32.isNotEmpty()) {
        __result += unpackedFixed32.sumOf { WireSize.tag(95, WireType.FIXED32) + WireSize.fixed32(it) }
    }

    if (unpackedFixed64.isNotEmpty()) {
        __result += unpackedFixed64.sumOf { WireSize.tag(96, WireType.FIXED64) + WireSize.fixed64(it) }
    }

    if (unpackedSfixed32.isNotEmpty()) {
        __result += unpackedSfixed32.sumOf { WireSize.tag(97, WireType.FIXED32) + WireSize.sFixed32(it) }
    }

    if (unpackedSfixed64.isNotEmpty()) {
        __result += unpackedSfixed64.sumOf { WireSize.tag(98, WireType.FIXED64) + WireSize.sFixed64(it) }
    }

    if (unpackedFloat.isNotEmpty()) {
        __result += unpackedFloat.sumOf { WireSize.tag(99, WireType.FIXED32) + WireSize.float(it) }
    }

    if (unpackedDouble.isNotEmpty()) {
        __result += unpackedDouble.sumOf { WireSize.tag(100, WireType.FIXED64) + WireSize.double(it) }
    }

    if (unpackedBool.isNotEmpty()) {
        __result += unpackedBool.sumOf { WireSize.tag(101, WireType.VARINT) + WireSize.bool(it) }
    }

    if (unpackedNestedEnum.isNotEmpty()) {
        __result += unpackedNestedEnum.sumOf { WireSize.tag(102, WireType.VARINT) + WireSize.enum(it.number) }
    }

    if (mapInt32Int32.isNotEmpty()) {
        __result += mapInt32Int32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapInt32Int32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(56, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapInt64Int64.isNotEmpty()) {
        __result += mapInt64Int64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapInt64Int64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(57, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapUint32Uint32.isNotEmpty()) {
        __result += mapUint32Uint32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapUint32Uint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(58, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapUint64Uint64.isNotEmpty()) {
        __result += mapUint64Uint64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapUint64Uint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(59, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapSint32Sint32.isNotEmpty()) {
        __result += mapSint32Sint32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapSint32Sint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(60, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapSint64Sint64.isNotEmpty()) {
        __result += mapSint64Sint64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapSint64Sint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(61, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapFixed32Fixed32.isNotEmpty()) {
        __result += mapFixed32Fixed32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(62, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapFixed64Fixed64.isNotEmpty()) {
        __result += mapFixed64Fixed64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(63, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapSfixed32Sfixed32.isNotEmpty()) {
        __result += mapSfixed32Sfixed32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(64, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapSfixed64Sfixed64.isNotEmpty()) {
        __result += mapSfixed64Sfixed64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(65, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapInt32Float.isNotEmpty()) {
        __result += mapInt32Float.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapInt32FloatEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(66, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapInt32Double.isNotEmpty()) {
        __result += mapInt32Double.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapInt32DoubleEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(67, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapBoolBool.isNotEmpty()) {
        __result += mapBoolBool.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapBoolBoolEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(68, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapStringString.isNotEmpty()) {
        __result += mapStringString.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringStringEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(69, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapStringBytes.isNotEmpty()) {
        __result += mapStringBytes.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringBytesEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(70, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapStringNestedMessage.isNotEmpty()) {
        __result += mapStringNestedMessage.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(71, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapStringForeignMessage.isNotEmpty()) {
        __result += mapStringForeignMessage.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(72, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapStringNestedEnum.isNotEmpty()) {
        __result += mapStringNestedEnum.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(73, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (mapStringForeignEnum.isNotEmpty()) {
        __result += mapStringForeignEnum.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(74, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (presenceMask[3]) {
        __result += optionalBoolWrapper.asInternal()._size.let { WireSize.tag(201, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[4]) {
        __result += optionalInt32Wrapper.asInternal()._size.let { WireSize.tag(202, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[5]) {
        __result += optionalInt64Wrapper.asInternal()._size.let { WireSize.tag(203, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[6]) {
        __result += optionalUint32Wrapper.asInternal()._size.let { WireSize.tag(204, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[7]) {
        __result += optionalUint64Wrapper.asInternal()._size.let { WireSize.tag(205, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[8]) {
        __result += optionalFloatWrapper.asInternal()._size.let { WireSize.tag(206, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[9]) {
        __result += optionalDoubleWrapper.asInternal()._size.let { WireSize.tag(207, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[10]) {
        __result += optionalStringWrapper.asInternal()._size.let { WireSize.tag(208, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[11]) {
        __result += optionalBytesWrapper.asInternal()._size.let { WireSize.tag(209, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (repeatedBoolWrapper.isNotEmpty()) {
        __result += repeatedBoolWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(211, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedInt32Wrapper.isNotEmpty()) {
        __result += repeatedInt32Wrapper.sumOf { it.asInternal()._size.let { WireSize.tag(212, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedInt64Wrapper.isNotEmpty()) {
        __result += repeatedInt64Wrapper.sumOf { it.asInternal()._size.let { WireSize.tag(213, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedUint32Wrapper.isNotEmpty()) {
        __result += repeatedUint32Wrapper.sumOf { it.asInternal()._size.let { WireSize.tag(214, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedUint64Wrapper.isNotEmpty()) {
        __result += repeatedUint64Wrapper.sumOf { it.asInternal()._size.let { WireSize.tag(215, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedFloatWrapper.isNotEmpty()) {
        __result += repeatedFloatWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(216, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedDoubleWrapper.isNotEmpty()) {
        __result += repeatedDoubleWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(217, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedStringWrapper.isNotEmpty()) {
        __result += repeatedStringWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(218, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedBytesWrapper.isNotEmpty()) {
        __result += repeatedBytesWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(219, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[12]) {
        __result += optionalDuration.asInternal()._size.let { WireSize.tag(301, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[13]) {
        __result += optionalTimestamp.asInternal()._size.let { WireSize.tag(302, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[14]) {
        __result += optionalFieldMask.asInternal()._size.let { WireSize.tag(303, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[15]) {
        __result += optionalStruct.asInternal()._size.let { WireSize.tag(304, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[16]) {
        __result += optionalAny.asInternal()._size.let { WireSize.tag(305, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[17]) {
        __result += optionalValue.asInternal()._size.let { WireSize.tag(306, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (optionalNullValue != NullValue.NULL_VALUE) {
        __result += (WireSize.tag(307, WireType.VARINT) + WireSize.enum(optionalNullValue.number))
    }

    if (repeatedDuration.isNotEmpty()) {
        __result += repeatedDuration.sumOf { it.asInternal()._size.let { WireSize.tag(311, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedTimestamp.isNotEmpty()) {
        __result += repeatedTimestamp.sumOf { it.asInternal()._size.let { WireSize.tag(312, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedFieldmask.isNotEmpty()) {
        __result += repeatedFieldmask.sumOf { it.asInternal()._size.let { WireSize.tag(313, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedStruct.isNotEmpty()) {
        __result += repeatedStruct.sumOf { it.asInternal()._size.let { WireSize.tag(324, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedAny.isNotEmpty()) {
        __result += repeatedAny.sumOf { it.asInternal()._size.let { WireSize.tag(315, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedValue.isNotEmpty()) {
        __result += repeatedValue.sumOf { it.asInternal()._size.let { WireSize.tag(316, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (repeatedListValue.isNotEmpty()) {
        __result += repeatedListValue.sumOf { it.asInternal()._size.let { WireSize.tag(317, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (fieldname1 != 0) {
        __result += (WireSize.tag(401, WireType.VARINT) + WireSize.int32(fieldname1))
    }

    if (fieldName2 != 0) {
        __result += (WireSize.tag(402, WireType.VARINT) + WireSize.int32(fieldName2))
    }

    if (FieldName3 != 0) {
        __result += (WireSize.tag(403, WireType.VARINT) + WireSize.int32(FieldName3))
    }

    if (field_Name4_ != 0) {
        __result += (WireSize.tag(404, WireType.VARINT) + WireSize.int32(field_Name4_))
    }

    if (field0name5 != 0) {
        __result += (WireSize.tag(405, WireType.VARINT) + WireSize.int32(field0name5))
    }

    if (field_0Name6 != 0) {
        __result += (WireSize.tag(406, WireType.VARINT) + WireSize.int32(field_0Name6))
    }

    if (fieldName7 != 0) {
        __result += (WireSize.tag(407, WireType.VARINT) + WireSize.int32(fieldName7))
    }

    if (FieldName8 != 0) {
        __result += (WireSize.tag(408, WireType.VARINT) + WireSize.int32(FieldName8))
    }

    if (field_Name9 != 0) {
        __result += (WireSize.tag(409, WireType.VARINT) + WireSize.int32(field_Name9))
    }

    if (Field_Name10 != 0) {
        __result += (WireSize.tag(410, WireType.VARINT) + WireSize.int32(Field_Name10))
    }

    if (FIELD_NAME11 != 0) {
        __result += (WireSize.tag(411, WireType.VARINT) + WireSize.int32(FIELD_NAME11))
    }

    if (FIELDName12 != 0) {
        __result += (WireSize.tag(412, WireType.VARINT) + WireSize.int32(FIELDName12))
    }

    if (_FieldName13 != 0) {
        __result += (WireSize.tag(413, WireType.VARINT) + WireSize.int32(_FieldName13))
    }

    if (__FieldName14 != 0) {
        __result += (WireSize.tag(414, WireType.VARINT) + WireSize.int32(__FieldName14))
    }

    if (field_Name15 != 0) {
        __result += (WireSize.tag(415, WireType.VARINT) + WireSize.int32(field_Name15))
    }

    if (field__Name16 != 0) {
        __result += (WireSize.tag(416, WireType.VARINT) + WireSize.int32(field__Name16))
    }

    if (fieldName17__ != 0) {
        __result += (WireSize.tag(417, WireType.VARINT) + WireSize.int32(fieldName17__))
    }

    if (FieldName18__ != 0) {
        __result += (WireSize.tag(418, WireType.VARINT) + WireSize.int32(FieldName18__))
    }

    oneofField?.also {
        when (val value = it) {
            is TestAllTypesProto3.OneofField.OneofUint32 -> {
                __result += (WireSize.tag(111, WireType.VARINT) + WireSize.uInt32(value.value))
            }
            is TestAllTypesProto3.OneofField.OneofNestedMessage -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(112, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestAllTypesProto3.OneofField.OneofString -> {
                __result += WireSize.string(value.value).let { WireSize.tag(113, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestAllTypesProto3.OneofField.OneofBytes -> {
                __result += WireSize.bytes(value.value).let { WireSize.tag(114, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestAllTypesProto3.OneofField.OneofBool -> {
                __result += (WireSize.tag(115, WireType.VARINT) + WireSize.bool(value.value))
            }
            is TestAllTypesProto3.OneofField.OneofUint64 -> {
                __result += (WireSize.tag(116, WireType.VARINT) + WireSize.uInt64(value.value))
            }
            is TestAllTypesProto3.OneofField.OneofFloat -> {
                __result += (WireSize.tag(117, WireType.FIXED32) + WireSize.float(value.value))
            }
            is TestAllTypesProto3.OneofField.OneofDouble -> {
                __result += (WireSize.tag(118, WireType.FIXED64) + WireSize.double(value.value))
            }
            is TestAllTypesProto3.OneofField.OneofEnum -> {
                __result += (WireSize.tag(119, WireType.VARINT) + WireSize.enum(value.value.number))
            }
            is TestAllTypesProto3.OneofField.OneofNullValue -> {
                __result += (WireSize.tag(120, WireType.VARINT) + WireSize.enum(value.value.number))
            }
        }
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3.asInternal(): TestAllTypesProto3Internal {
    return this as? TestAllTypesProto3Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ForeignMessageInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun ForeignMessageInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (c != 0) {
        encoder.writeInt32(fieldNr = 1, value = c)
    }
}

@InternalRpcApi
fun ForeignMessageInternal.Companion.decodeWith(msg: ForeignMessageInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.c = decoder.readInt32()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun ForeignMessageInternal.computeSize(): Int {
    var __result = 0
    if (c != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(c))
    }

    return __result
}

@InternalRpcApi
fun ForeignMessage.asInternal(): ForeignMessageInternal {
    return this as? ForeignMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun NullHypothesisProto3Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun NullHypothesisProto3Internal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    // no fields to encode
}

@InternalRpcApi
fun NullHypothesisProto3Internal.Companion.decodeWith(msg: NullHypothesisProto3Internal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun NullHypothesisProto3Internal.computeSize(): Int {
    var __result = 0
    return __result
}

@InternalRpcApi
fun NullHypothesisProto3.asInternal(): NullHypothesisProto3Internal {
    return this as? NullHypothesisProto3Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun EnumOnlyProto3Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun EnumOnlyProto3Internal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    // no fields to encode
}

@InternalRpcApi
fun EnumOnlyProto3Internal.Companion.decodeWith(msg: EnumOnlyProto3Internal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun EnumOnlyProto3Internal.computeSize(): Int {
    var __result = 0
    return __result
}

@InternalRpcApi
fun EnumOnlyProto3.asInternal(): EnumOnlyProto3Internal {
    return this as? EnumOnlyProto3Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesProto3Internal.NestedMessageInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        corecursive.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.NestedMessageInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (a != 0) {
        encoder.writeInt32(fieldNr = 1, value = a)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 2, value = corecursive.asInternal()) { encodeWith(it, config) }
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.NestedMessageInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.NestedMessageInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.a = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.corecursive = TestAllTypesProto3Internal()
                }

                decoder.readMessage(msg.corecursive.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.decodeWith(msg, decoder, config) })
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.NestedMessageInternal.computeSize(): Int {
    var __result = 0
    if (a != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(a))
    }

    if (presenceMask[0]) {
        __result += corecursive.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3.NestedMessage.asInternal(): TestAllTypesProto3Internal.NestedMessageInternal {
    return this as? TestAllTypesProto3Internal.NestedMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32Int32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32Int32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0) {
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (value != 0) {
        encoder.writeInt32(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32Int32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapInt32Int32EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readInt32()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapInt32Int32EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(key))
    }

    if (value != 0) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int32(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32Int32EntryInternal.asInternal(): TestAllTypesProto3Internal.MapInt32Int32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt64Int64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt64Int64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0L) {
        encoder.writeInt64(fieldNr = 1, value = key)
    }

    if (value != 0L) {
        encoder.writeInt64(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt64Int64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapInt64Int64EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readInt64()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readInt64()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapInt64Int64EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0L) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int64(key))
    }

    if (value != 0L) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int64(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt64Int64EntryInternal.asInternal(): TestAllTypesProto3Internal.MapInt64Int64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0u) {
        encoder.writeUInt32(fieldNr = 1, value = key)
    }

    if (value != 0u) {
        encoder.writeUInt32(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapUint32Uint32EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readUInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readUInt32()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0u) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt32(key))
    }

    if (value != 0u) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.uInt32(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.asInternal(): TestAllTypesProto3Internal.MapUint32Uint32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0uL) {
        encoder.writeUInt64(fieldNr = 1, value = key)
    }

    if (value != 0uL) {
        encoder.writeUInt64(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapUint64Uint64EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readUInt64()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readUInt64()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0uL) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt64(key))
    }

    if (value != 0uL) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.uInt64(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.asInternal(): TestAllTypesProto3Internal.MapUint64Uint64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0) {
        encoder.writeSInt32(fieldNr = 1, value = key)
    }

    if (value != 0) {
        encoder.writeSInt32(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapSint32Sint32EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readSInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readSInt32()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.sInt32(key))
    }

    if (value != 0) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.sInt32(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.asInternal(): TestAllTypesProto3Internal.MapSint32Sint32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0L) {
        encoder.writeSInt64(fieldNr = 1, value = key)
    }

    if (value != 0L) {
        encoder.writeSInt64(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapSint64Sint64EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readSInt64()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readSInt64()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0L) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.sInt64(key))
    }

    if (value != 0L) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.sInt64(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.asInternal(): TestAllTypesProto3Internal.MapSint64Sint64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0u) {
        encoder.writeFixed32(fieldNr = 1, value = key)
    }

    if (value != 0u) {
        encoder.writeFixed32(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.FIXED32 -> {
                msg.key = decoder.readFixed32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.FIXED32 -> {
                msg.value = decoder.readFixed32()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0u) {
        __result += (WireSize.tag(1, WireType.FIXED32) + WireSize.fixed32(key))
    }

    if (value != 0u) {
        __result += (WireSize.tag(2, WireType.FIXED32) + WireSize.fixed32(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.asInternal(): TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0uL) {
        encoder.writeFixed64(fieldNr = 1, value = key)
    }

    if (value != 0uL) {
        encoder.writeFixed64(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.FIXED64 -> {
                msg.key = decoder.readFixed64()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.FIXED64 -> {
                msg.value = decoder.readFixed64()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0uL) {
        __result += (WireSize.tag(1, WireType.FIXED64) + WireSize.fixed64(key))
    }

    if (value != 0uL) {
        __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.fixed64(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.asInternal(): TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0) {
        encoder.writeSFixed32(fieldNr = 1, value = key)
    }

    if (value != 0) {
        encoder.writeSFixed32(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.FIXED32 -> {
                msg.key = decoder.readSFixed32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.FIXED32 -> {
                msg.value = decoder.readSFixed32()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0) {
        __result += (WireSize.tag(1, WireType.FIXED32) + WireSize.sFixed32(key))
    }

    if (value != 0) {
        __result += (WireSize.tag(2, WireType.FIXED32) + WireSize.sFixed32(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.asInternal(): TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0L) {
        encoder.writeSFixed64(fieldNr = 1, value = key)
    }

    if (value != 0L) {
        encoder.writeSFixed64(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.FIXED64 -> {
                msg.key = decoder.readSFixed64()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.FIXED64 -> {
                msg.value = decoder.readSFixed64()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0L) {
        __result += (WireSize.tag(1, WireType.FIXED64) + WireSize.sFixed64(key))
    }

    if (value != 0L) {
        __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.sFixed64(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.asInternal(): TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32FloatEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32FloatEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0) {
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (value != 0.0f) {
        encoder.writeFloat(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32FloatEntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapInt32FloatEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.FIXED32 -> {
                msg.value = decoder.readFloat()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapInt32FloatEntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(key))
    }

    if (value != 0.0f) {
        __result += (WireSize.tag(2, WireType.FIXED32) + WireSize.float(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32FloatEntryInternal.asInternal(): TestAllTypesProto3Internal.MapInt32FloatEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != 0) {
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (value != 0.0) {
        encoder.writeDouble(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapInt32DoubleEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.FIXED64 -> {
                msg.value = decoder.readDouble()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.computeSize(): Int {
    var __result = 0
    if (key != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(key))
    }

    if (value != 0.0) {
        __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.double(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.asInternal(): TestAllTypesProto3Internal.MapInt32DoubleEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapBoolBoolEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapBoolBoolEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key != false) {
        encoder.writeBool(fieldNr = 1, value = key)
    }

    if (value != false) {
        encoder.writeBool(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapBoolBoolEntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapBoolBoolEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readBool()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readBool()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapBoolBoolEntryInternal.computeSize(): Int {
    var __result = 0
    if (key != false) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.bool(key))
    }

    if (value != false) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.bool(value))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapBoolBoolEntryInternal.asInternal(): TestAllTypesProto3Internal.MapBoolBoolEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringStringEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringStringEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (value.isNotEmpty()) {
        encoder.writeString(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringStringEntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapStringStringEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.value = decoder.readString()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapStringStringEntryInternal.computeSize(): Int {
    var __result = 0
    if (key.isNotEmpty()) {
        __result += WireSize.string(key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (value.isNotEmpty()) {
        __result += WireSize.string(value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringStringEntryInternal.asInternal(): TestAllTypesProto3Internal.MapStringStringEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringBytesEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringBytesEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (value.isNotEmpty()) {
        encoder.writeBytes(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringBytesEntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapStringBytesEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.value = decoder.readBytes()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapStringBytesEntryInternal.computeSize(): Int {
    var __result = 0
    if (key.isNotEmpty()) {
        __result += WireSize.string(key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (value.isNotEmpty()) {
        __result += WireSize.bytes(value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringBytesEntryInternal.asInternal(): TestAllTypesProto3Internal.MapStringBytesEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it, config) }
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.value = TestAllTypesProto3Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.value.asInternal(), { msg, decoder -> TestAllTypesProto3Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.computeSize(): Int {
    var __result = 0
    if (key.isNotEmpty()) {
        __result += WireSize.string(key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.asInternal(): TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it, config) }
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.value = ForeignMessageInternal()
                }

                decoder.readMessage(msg.value.asInternal(), { msg, decoder -> ForeignMessageInternal.decodeWith(msg, decoder, config) })
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.computeSize(): Int {
    var __result = 0
    if (key.isNotEmpty()) {
        __result += WireSize.string(key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.asInternal(): TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (value != TestAllTypesProto3.NestedEnum.FOO) {
        encoder.writeEnum(fieldNr = 2, value = value.number)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.computeSize(): Int {
    var __result = 0
    if (key.isNotEmpty()) {
        __result += WireSize.string(key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (value != TestAllTypesProto3.NestedEnum.FOO) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.enum(value.number))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.asInternal(): TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (value != ForeignEnum.FOREIGN_FOO) {
        encoder.writeEnum(fieldNr = 2, value = value.number)
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.Companion.decodeWith(msg: TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = ForeignEnum.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.computeSize(): Int {
    var __result = 0
    if (key.isNotEmpty()) {
        __result += WireSize.string(key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (value != ForeignEnum.FOREIGN_FOO) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.enum(value.number))
    }

    return __result
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.asInternal(): TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal {
    return this
}

@InternalRpcApi
fun ForeignEnum.Companion.fromNumber(number: Int): ForeignEnum {
    return when (number) {
        0 -> {
            ForeignEnum.FOREIGN_FOO
        }
        1 -> {
            ForeignEnum.FOREIGN_BAR
        }
        2 -> {
            ForeignEnum.FOREIGN_BAZ
        }
        else -> {
            ForeignEnum.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
fun TestAllTypesProto3.NestedEnum.Companion.fromNumber(number: Int): TestAllTypesProto3.NestedEnum {
    return when (number) {
        0 -> {
            TestAllTypesProto3.NestedEnum.FOO
        }
        1 -> {
            TestAllTypesProto3.NestedEnum.BAR
        }
        2 -> {
            TestAllTypesProto3.NestedEnum.BAZ
        }
        -1 -> {
            TestAllTypesProto3.NestedEnum.NEG
        }
        else -> {
            TestAllTypesProto3.NestedEnum.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
fun TestAllTypesProto3.AliasedEnum.Companion.fromNumber(number: Int): TestAllTypesProto3.AliasedEnum {
    return when (number) {
        0 -> {
            TestAllTypesProto3.AliasedEnum.ALIAS_FOO
        }
        1 -> {
            TestAllTypesProto3.AliasedEnum.ALIAS_BAR
        }
        2 -> {
            TestAllTypesProto3.AliasedEnum.ALIAS_BAZ
        }
        else -> {
            TestAllTypesProto3.AliasedEnum.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
fun EnumOnlyProto3.Bool.Companion.fromNumber(number: Int): EnumOnlyProto3.Bool {
    return when (number) {
        0 -> {
            EnumOnlyProto3.Bool.kFalse
        }
        1 -> {
            EnumOnlyProto3.Bool.kTrue
        }
        else -> {
            EnumOnlyProto3.Bool.UNRECOGNIZED(number)
        }
    }
}
