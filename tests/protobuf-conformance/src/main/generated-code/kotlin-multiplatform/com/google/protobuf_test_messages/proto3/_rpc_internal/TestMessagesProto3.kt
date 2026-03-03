@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.proto3

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
import kotlinx.rpc.grpc.marshaller.MarshallerConfig
import kotlinx.rpc.grpc.marshaller.MessageMarshaller
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
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

class TestAllTypesProto3Internal: TestAllTypesProto3.Builder, InternalMessage(fieldsWithPresence = 18) {
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
        if (this.optionalInt32 != other.optionalInt32) return false
        if (this.optionalInt64 != other.optionalInt64) return false
        if (this.optionalUint32 != other.optionalUint32) return false
        if (this.optionalUint64 != other.optionalUint64) return false
        if (this.optionalSint32 != other.optionalSint32) return false
        if (this.optionalSint64 != other.optionalSint64) return false
        if (this.optionalFixed32 != other.optionalFixed32) return false
        if (this.optionalFixed64 != other.optionalFixed64) return false
        if (this.optionalSfixed32 != other.optionalSfixed32) return false
        if (this.optionalSfixed64 != other.optionalSfixed64) return false
        if (this.optionalFloat != other.optionalFloat) return false
        if (this.optionalDouble != other.optionalDouble) return false
        if (this.optionalBool != other.optionalBool) return false
        if (this.optionalString != other.optionalString) return false
        if (!this.optionalBytes.contentEquals(other.optionalBytes)) return false
        if (presenceMask[0] && this.optionalNestedMessage != other.optionalNestedMessage) return false
        if (presenceMask[1] && this.optionalForeignMessage != other.optionalForeignMessage) return false
        if (this.optionalNestedEnum != other.optionalNestedEnum) return false
        if (this.optionalForeignEnum != other.optionalForeignEnum) return false
        if (this.optionalAliasedEnum != other.optionalAliasedEnum) return false
        if (this.optionalStringPiece != other.optionalStringPiece) return false
        if (this.optionalCord != other.optionalCord) return false
        if (presenceMask[2] && this.recursiveMessage != other.recursiveMessage) return false
        if (this.repeatedInt32 != other.repeatedInt32) return false
        if (this.repeatedInt64 != other.repeatedInt64) return false
        if (this.repeatedUint32 != other.repeatedUint32) return false
        if (this.repeatedUint64 != other.repeatedUint64) return false
        if (this.repeatedSint32 != other.repeatedSint32) return false
        if (this.repeatedSint64 != other.repeatedSint64) return false
        if (this.repeatedFixed32 != other.repeatedFixed32) return false
        if (this.repeatedFixed64 != other.repeatedFixed64) return false
        if (this.repeatedSfixed32 != other.repeatedSfixed32) return false
        if (this.repeatedSfixed64 != other.repeatedSfixed64) return false
        if (this.repeatedFloat != other.repeatedFloat) return false
        if (this.repeatedDouble != other.repeatedDouble) return false
        if (this.repeatedBool != other.repeatedBool) return false
        if (this.repeatedString != other.repeatedString) return false
        if (this.repeatedBytes != other.repeatedBytes) return false
        if (this.repeatedNestedMessage != other.repeatedNestedMessage) return false
        if (this.repeatedForeignMessage != other.repeatedForeignMessage) return false
        if (this.repeatedNestedEnum != other.repeatedNestedEnum) return false
        if (this.repeatedForeignEnum != other.repeatedForeignEnum) return false
        if (this.repeatedStringPiece != other.repeatedStringPiece) return false
        if (this.repeatedCord != other.repeatedCord) return false
        if (this.packedInt32 != other.packedInt32) return false
        if (this.packedInt64 != other.packedInt64) return false
        if (this.packedUint32 != other.packedUint32) return false
        if (this.packedUint64 != other.packedUint64) return false
        if (this.packedSint32 != other.packedSint32) return false
        if (this.packedSint64 != other.packedSint64) return false
        if (this.packedFixed32 != other.packedFixed32) return false
        if (this.packedFixed64 != other.packedFixed64) return false
        if (this.packedSfixed32 != other.packedSfixed32) return false
        if (this.packedSfixed64 != other.packedSfixed64) return false
        if (this.packedFloat != other.packedFloat) return false
        if (this.packedDouble != other.packedDouble) return false
        if (this.packedBool != other.packedBool) return false
        if (this.packedNestedEnum != other.packedNestedEnum) return false
        if (this.unpackedInt32 != other.unpackedInt32) return false
        if (this.unpackedInt64 != other.unpackedInt64) return false
        if (this.unpackedUint32 != other.unpackedUint32) return false
        if (this.unpackedUint64 != other.unpackedUint64) return false
        if (this.unpackedSint32 != other.unpackedSint32) return false
        if (this.unpackedSint64 != other.unpackedSint64) return false
        if (this.unpackedFixed32 != other.unpackedFixed32) return false
        if (this.unpackedFixed64 != other.unpackedFixed64) return false
        if (this.unpackedSfixed32 != other.unpackedSfixed32) return false
        if (this.unpackedSfixed64 != other.unpackedSfixed64) return false
        if (this.unpackedFloat != other.unpackedFloat) return false
        if (this.unpackedDouble != other.unpackedDouble) return false
        if (this.unpackedBool != other.unpackedBool) return false
        if (this.unpackedNestedEnum != other.unpackedNestedEnum) return false
        if (this.mapInt32Int32 != other.mapInt32Int32) return false
        if (this.mapInt64Int64 != other.mapInt64Int64) return false
        if (this.mapUint32Uint32 != other.mapUint32Uint32) return false
        if (this.mapUint64Uint64 != other.mapUint64Uint64) return false
        if (this.mapSint32Sint32 != other.mapSint32Sint32) return false
        if (this.mapSint64Sint64 != other.mapSint64Sint64) return false
        if (this.mapFixed32Fixed32 != other.mapFixed32Fixed32) return false
        if (this.mapFixed64Fixed64 != other.mapFixed64Fixed64) return false
        if (this.mapSfixed32Sfixed32 != other.mapSfixed32Sfixed32) return false
        if (this.mapSfixed64Sfixed64 != other.mapSfixed64Sfixed64) return false
        if (this.mapInt32Float != other.mapInt32Float) return false
        if (this.mapInt32Double != other.mapInt32Double) return false
        if (this.mapBoolBool != other.mapBoolBool) return false
        if (this.mapStringString != other.mapStringString) return false
        if (this.mapStringBytes != other.mapStringBytes) return false
        if (this.mapStringNestedMessage != other.mapStringNestedMessage) return false
        if (this.mapStringForeignMessage != other.mapStringForeignMessage) return false
        if (this.mapStringNestedEnum != other.mapStringNestedEnum) return false
        if (this.mapStringForeignEnum != other.mapStringForeignEnum) return false
        if (presenceMask[3] && this.optionalBoolWrapper != other.optionalBoolWrapper) return false
        if (presenceMask[4] && this.optionalInt32Wrapper != other.optionalInt32Wrapper) return false
        if (presenceMask[5] && this.optionalInt64Wrapper != other.optionalInt64Wrapper) return false
        if (presenceMask[6] && this.optionalUint32Wrapper != other.optionalUint32Wrapper) return false
        if (presenceMask[7] && this.optionalUint64Wrapper != other.optionalUint64Wrapper) return false
        if (presenceMask[8] && this.optionalFloatWrapper != other.optionalFloatWrapper) return false
        if (presenceMask[9] && this.optionalDoubleWrapper != other.optionalDoubleWrapper) return false
        if (presenceMask[10] && this.optionalStringWrapper != other.optionalStringWrapper) return false
        if (presenceMask[11] && this.optionalBytesWrapper != other.optionalBytesWrapper) return false
        if (this.repeatedBoolWrapper != other.repeatedBoolWrapper) return false
        if (this.repeatedInt32Wrapper != other.repeatedInt32Wrapper) return false
        if (this.repeatedInt64Wrapper != other.repeatedInt64Wrapper) return false
        if (this.repeatedUint32Wrapper != other.repeatedUint32Wrapper) return false
        if (this.repeatedUint64Wrapper != other.repeatedUint64Wrapper) return false
        if (this.repeatedFloatWrapper != other.repeatedFloatWrapper) return false
        if (this.repeatedDoubleWrapper != other.repeatedDoubleWrapper) return false
        if (this.repeatedStringWrapper != other.repeatedStringWrapper) return false
        if (this.repeatedBytesWrapper != other.repeatedBytesWrapper) return false
        if (presenceMask[12] && this.optionalDuration != other.optionalDuration) return false
        if (presenceMask[13] && this.optionalTimestamp != other.optionalTimestamp) return false
        if (presenceMask[14] && this.optionalFieldMask != other.optionalFieldMask) return false
        if (presenceMask[15] && this.optionalStruct != other.optionalStruct) return false
        if (presenceMask[16] && this.optionalAny != other.optionalAny) return false
        if (presenceMask[17] && this.optionalValue != other.optionalValue) return false
        if (this.optionalNullValue != other.optionalNullValue) return false
        if (this.repeatedDuration != other.repeatedDuration) return false
        if (this.repeatedTimestamp != other.repeatedTimestamp) return false
        if (this.repeatedFieldmask != other.repeatedFieldmask) return false
        if (this.repeatedStruct != other.repeatedStruct) return false
        if (this.repeatedAny != other.repeatedAny) return false
        if (this.repeatedValue != other.repeatedValue) return false
        if (this.repeatedListValue != other.repeatedListValue) return false
        if (this.fieldname1 != other.fieldname1) return false
        if (this.fieldName2 != other.fieldName2) return false
        if (this.FieldName3 != other.FieldName3) return false
        if (this.field_Name4_ != other.field_Name4_) return false
        if (this.field0name5 != other.field0name5) return false
        if (this.field_0Name6 != other.field_0Name6) return false
        if (this.fieldName7 != other.fieldName7) return false
        if (this.FieldName8 != other.FieldName8) return false
        if (this.field_Name9 != other.field_Name9) return false
        if (this.Field_Name10 != other.Field_Name10) return false
        if (this.FIELD_NAME11 != other.FIELD_NAME11) return false
        if (this.FIELDName12 != other.FIELDName12) return false
        if (this._FieldName13 != other._FieldName13) return false
        if (this.__FieldName14 != other.__FieldName14) return false
        if (this.field_Name15 != other.field_Name15) return false
        if (this.field__Name16 != other.field__Name16) return false
        if (this.fieldName17__ != other.fieldName17__) return false
        if (this.FieldName18__ != other.FieldName18__) return false
        if (this.oneofField != other.oneofField) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("TestAllTypesProto3(")
        builder.appendLine("${nextIndentString}optionalInt32=${this.optionalInt32},")
        builder.appendLine("${nextIndentString}optionalInt64=${this.optionalInt64},")
        builder.appendLine("${nextIndentString}optionalUint32=${this.optionalUint32},")
        builder.appendLine("${nextIndentString}optionalUint64=${this.optionalUint64},")
        builder.appendLine("${nextIndentString}optionalSint32=${this.optionalSint32},")
        builder.appendLine("${nextIndentString}optionalSint64=${this.optionalSint64},")
        builder.appendLine("${nextIndentString}optionalFixed32=${this.optionalFixed32},")
        builder.appendLine("${nextIndentString}optionalFixed64=${this.optionalFixed64},")
        builder.appendLine("${nextIndentString}optionalSfixed32=${this.optionalSfixed32},")
        builder.appendLine("${nextIndentString}optionalSfixed64=${this.optionalSfixed64},")
        builder.appendLine("${nextIndentString}optionalFloat=${this.optionalFloat},")
        builder.appendLine("${nextIndentString}optionalDouble=${this.optionalDouble},")
        builder.appendLine("${nextIndentString}optionalBool=${this.optionalBool},")
        builder.appendLine("${nextIndentString}optionalString=${this.optionalString},")
        builder.appendLine("${nextIndentString}optionalBytes=${this.optionalBytes.contentToString()},")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}optionalNestedMessage=${this.optionalNestedMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalNestedMessage=<unset>,")
        }

        if (presenceMask[1]) {
            builder.appendLine("${nextIndentString}optionalForeignMessage=${this.optionalForeignMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalForeignMessage=<unset>,")
        }

        builder.appendLine("${nextIndentString}optionalNestedEnum=${this.optionalNestedEnum},")
        builder.appendLine("${nextIndentString}optionalForeignEnum=${this.optionalForeignEnum},")
        builder.appendLine("${nextIndentString}optionalAliasedEnum=${this.optionalAliasedEnum},")
        builder.appendLine("${nextIndentString}optionalStringPiece=${this.optionalStringPiece},")
        builder.appendLine("${nextIndentString}optionalCord=${this.optionalCord},")
        if (presenceMask[2]) {
            builder.appendLine("${nextIndentString}recursiveMessage=${this.recursiveMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}recursiveMessage=<unset>,")
        }

        builder.appendLine("${nextIndentString}repeatedInt32=${this.repeatedInt32},")
        builder.appendLine("${nextIndentString}repeatedInt64=${this.repeatedInt64},")
        builder.appendLine("${nextIndentString}repeatedUint32=${this.repeatedUint32},")
        builder.appendLine("${nextIndentString}repeatedUint64=${this.repeatedUint64},")
        builder.appendLine("${nextIndentString}repeatedSint32=${this.repeatedSint32},")
        builder.appendLine("${nextIndentString}repeatedSint64=${this.repeatedSint64},")
        builder.appendLine("${nextIndentString}repeatedFixed32=${this.repeatedFixed32},")
        builder.appendLine("${nextIndentString}repeatedFixed64=${this.repeatedFixed64},")
        builder.appendLine("${nextIndentString}repeatedSfixed32=${this.repeatedSfixed32},")
        builder.appendLine("${nextIndentString}repeatedSfixed64=${this.repeatedSfixed64},")
        builder.appendLine("${nextIndentString}repeatedFloat=${this.repeatedFloat},")
        builder.appendLine("${nextIndentString}repeatedDouble=${this.repeatedDouble},")
        builder.appendLine("${nextIndentString}repeatedBool=${this.repeatedBool},")
        builder.appendLine("${nextIndentString}repeatedString=${this.repeatedString},")
        builder.appendLine("${nextIndentString}repeatedBytes=${this.repeatedBytes},")
        builder.appendLine("${nextIndentString}repeatedNestedMessage=${this.repeatedNestedMessage},")
        builder.appendLine("${nextIndentString}repeatedForeignMessage=${this.repeatedForeignMessage},")
        builder.appendLine("${nextIndentString}repeatedNestedEnum=${this.repeatedNestedEnum},")
        builder.appendLine("${nextIndentString}repeatedForeignEnum=${this.repeatedForeignEnum},")
        builder.appendLine("${nextIndentString}repeatedStringPiece=${this.repeatedStringPiece},")
        builder.appendLine("${nextIndentString}repeatedCord=${this.repeatedCord},")
        builder.appendLine("${nextIndentString}packedInt32=${this.packedInt32},")
        builder.appendLine("${nextIndentString}packedInt64=${this.packedInt64},")
        builder.appendLine("${nextIndentString}packedUint32=${this.packedUint32},")
        builder.appendLine("${nextIndentString}packedUint64=${this.packedUint64},")
        builder.appendLine("${nextIndentString}packedSint32=${this.packedSint32},")
        builder.appendLine("${nextIndentString}packedSint64=${this.packedSint64},")
        builder.appendLine("${nextIndentString}packedFixed32=${this.packedFixed32},")
        builder.appendLine("${nextIndentString}packedFixed64=${this.packedFixed64},")
        builder.appendLine("${nextIndentString}packedSfixed32=${this.packedSfixed32},")
        builder.appendLine("${nextIndentString}packedSfixed64=${this.packedSfixed64},")
        builder.appendLine("${nextIndentString}packedFloat=${this.packedFloat},")
        builder.appendLine("${nextIndentString}packedDouble=${this.packedDouble},")
        builder.appendLine("${nextIndentString}packedBool=${this.packedBool},")
        builder.appendLine("${nextIndentString}packedNestedEnum=${this.packedNestedEnum},")
        builder.appendLine("${nextIndentString}unpackedInt32=${this.unpackedInt32},")
        builder.appendLine("${nextIndentString}unpackedInt64=${this.unpackedInt64},")
        builder.appendLine("${nextIndentString}unpackedUint32=${this.unpackedUint32},")
        builder.appendLine("${nextIndentString}unpackedUint64=${this.unpackedUint64},")
        builder.appendLine("${nextIndentString}unpackedSint32=${this.unpackedSint32},")
        builder.appendLine("${nextIndentString}unpackedSint64=${this.unpackedSint64},")
        builder.appendLine("${nextIndentString}unpackedFixed32=${this.unpackedFixed32},")
        builder.appendLine("${nextIndentString}unpackedFixed64=${this.unpackedFixed64},")
        builder.appendLine("${nextIndentString}unpackedSfixed32=${this.unpackedSfixed32},")
        builder.appendLine("${nextIndentString}unpackedSfixed64=${this.unpackedSfixed64},")
        builder.appendLine("${nextIndentString}unpackedFloat=${this.unpackedFloat},")
        builder.appendLine("${nextIndentString}unpackedDouble=${this.unpackedDouble},")
        builder.appendLine("${nextIndentString}unpackedBool=${this.unpackedBool},")
        builder.appendLine("${nextIndentString}unpackedNestedEnum=${this.unpackedNestedEnum},")
        builder.appendLine("${nextIndentString}mapInt32Int32=${this.mapInt32Int32},")
        builder.appendLine("${nextIndentString}mapInt64Int64=${this.mapInt64Int64},")
        builder.appendLine("${nextIndentString}mapUint32Uint32=${this.mapUint32Uint32},")
        builder.appendLine("${nextIndentString}mapUint64Uint64=${this.mapUint64Uint64},")
        builder.appendLine("${nextIndentString}mapSint32Sint32=${this.mapSint32Sint32},")
        builder.appendLine("${nextIndentString}mapSint64Sint64=${this.mapSint64Sint64},")
        builder.appendLine("${nextIndentString}mapFixed32Fixed32=${this.mapFixed32Fixed32},")
        builder.appendLine("${nextIndentString}mapFixed64Fixed64=${this.mapFixed64Fixed64},")
        builder.appendLine("${nextIndentString}mapSfixed32Sfixed32=${this.mapSfixed32Sfixed32},")
        builder.appendLine("${nextIndentString}mapSfixed64Sfixed64=${this.mapSfixed64Sfixed64},")
        builder.appendLine("${nextIndentString}mapInt32Float=${this.mapInt32Float},")
        builder.appendLine("${nextIndentString}mapInt32Double=${this.mapInt32Double},")
        builder.appendLine("${nextIndentString}mapBoolBool=${this.mapBoolBool},")
        builder.appendLine("${nextIndentString}mapStringString=${this.mapStringString},")
        builder.appendLine("${nextIndentString}mapStringBytes=${this.mapStringBytes},")
        builder.appendLine("${nextIndentString}mapStringNestedMessage=${this.mapStringNestedMessage},")
        builder.appendLine("${nextIndentString}mapStringForeignMessage=${this.mapStringForeignMessage},")
        builder.appendLine("${nextIndentString}mapStringNestedEnum=${this.mapStringNestedEnum},")
        builder.appendLine("${nextIndentString}mapStringForeignEnum=${this.mapStringForeignEnum},")
        if (presenceMask[3]) {
            builder.appendLine("${nextIndentString}optionalBoolWrapper=${this.optionalBoolWrapper.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalBoolWrapper=<unset>,")
        }

        if (presenceMask[4]) {
            builder.appendLine("${nextIndentString}optionalInt32Wrapper=${this.optionalInt32Wrapper.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalInt32Wrapper=<unset>,")
        }

        if (presenceMask[5]) {
            builder.appendLine("${nextIndentString}optionalInt64Wrapper=${this.optionalInt64Wrapper.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalInt64Wrapper=<unset>,")
        }

        if (presenceMask[6]) {
            builder.appendLine("${nextIndentString}optionalUint32Wrapper=${this.optionalUint32Wrapper.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalUint32Wrapper=<unset>,")
        }

        if (presenceMask[7]) {
            builder.appendLine("${nextIndentString}optionalUint64Wrapper=${this.optionalUint64Wrapper.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalUint64Wrapper=<unset>,")
        }

        if (presenceMask[8]) {
            builder.appendLine("${nextIndentString}optionalFloatWrapper=${this.optionalFloatWrapper.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalFloatWrapper=<unset>,")
        }

        if (presenceMask[9]) {
            builder.appendLine("${nextIndentString}optionalDoubleWrapper=${this.optionalDoubleWrapper.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalDoubleWrapper=<unset>,")
        }

        if (presenceMask[10]) {
            builder.appendLine("${nextIndentString}optionalStringWrapper=${this.optionalStringWrapper.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalStringWrapper=<unset>,")
        }

        if (presenceMask[11]) {
            builder.appendLine("${nextIndentString}optionalBytesWrapper=${this.optionalBytesWrapper.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalBytesWrapper=<unset>,")
        }

        builder.appendLine("${nextIndentString}repeatedBoolWrapper=${this.repeatedBoolWrapper},")
        builder.appendLine("${nextIndentString}repeatedInt32Wrapper=${this.repeatedInt32Wrapper},")
        builder.appendLine("${nextIndentString}repeatedInt64Wrapper=${this.repeatedInt64Wrapper},")
        builder.appendLine("${nextIndentString}repeatedUint32Wrapper=${this.repeatedUint32Wrapper},")
        builder.appendLine("${nextIndentString}repeatedUint64Wrapper=${this.repeatedUint64Wrapper},")
        builder.appendLine("${nextIndentString}repeatedFloatWrapper=${this.repeatedFloatWrapper},")
        builder.appendLine("${nextIndentString}repeatedDoubleWrapper=${this.repeatedDoubleWrapper},")
        builder.appendLine("${nextIndentString}repeatedStringWrapper=${this.repeatedStringWrapper},")
        builder.appendLine("${nextIndentString}repeatedBytesWrapper=${this.repeatedBytesWrapper},")
        if (presenceMask[12]) {
            builder.appendLine("${nextIndentString}optionalDuration=${this.optionalDuration.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalDuration=<unset>,")
        }

        if (presenceMask[13]) {
            builder.appendLine("${nextIndentString}optionalTimestamp=${this.optionalTimestamp.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalTimestamp=<unset>,")
        }

        if (presenceMask[14]) {
            builder.appendLine("${nextIndentString}optionalFieldMask=${this.optionalFieldMask.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalFieldMask=<unset>,")
        }

        if (presenceMask[15]) {
            builder.appendLine("${nextIndentString}optionalStruct=${this.optionalStruct.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalStruct=<unset>,")
        }

        if (presenceMask[16]) {
            builder.appendLine("${nextIndentString}optionalAny=${this.optionalAny.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalAny=<unset>,")
        }

        if (presenceMask[17]) {
            builder.appendLine("${nextIndentString}optionalValue=${this.optionalValue.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalValue=<unset>,")
        }

        builder.appendLine("${nextIndentString}optionalNullValue=${this.optionalNullValue},")
        builder.appendLine("${nextIndentString}repeatedDuration=${this.repeatedDuration},")
        builder.appendLine("${nextIndentString}repeatedTimestamp=${this.repeatedTimestamp},")
        builder.appendLine("${nextIndentString}repeatedFieldmask=${this.repeatedFieldmask},")
        builder.appendLine("${nextIndentString}repeatedStruct=${this.repeatedStruct},")
        builder.appendLine("${nextIndentString}repeatedAny=${this.repeatedAny},")
        builder.appendLine("${nextIndentString}repeatedValue=${this.repeatedValue},")
        builder.appendLine("${nextIndentString}repeatedListValue=${this.repeatedListValue},")
        builder.appendLine("${nextIndentString}fieldname1=${this.fieldname1},")
        builder.appendLine("${nextIndentString}fieldName2=${this.fieldName2},")
        builder.appendLine("${nextIndentString}FieldName3=${this.FieldName3},")
        builder.appendLine("${nextIndentString}field_Name4_=${this.field_Name4_},")
        builder.appendLine("${nextIndentString}field0name5=${this.field0name5},")
        builder.appendLine("${nextIndentString}field_0Name6=${this.field_0Name6},")
        builder.appendLine("${nextIndentString}fieldName7=${this.fieldName7},")
        builder.appendLine("${nextIndentString}FieldName8=${this.FieldName8},")
        builder.appendLine("${nextIndentString}field_Name9=${this.field_Name9},")
        builder.appendLine("${nextIndentString}Field_Name10=${this.Field_Name10},")
        builder.appendLine("${nextIndentString}FIELD_NAME11=${this.FIELD_NAME11},")
        builder.appendLine("${nextIndentString}FIELDName12=${this.FIELDName12},")
        builder.appendLine("${nextIndentString}_FieldName13=${this._FieldName13},")
        builder.appendLine("${nextIndentString}__FieldName14=${this.__FieldName14},")
        builder.appendLine("${nextIndentString}field_Name15=${this.field_Name15},")
        builder.appendLine("${nextIndentString}field__Name16=${this.field__Name16},")
        builder.appendLine("${nextIndentString}fieldName17__=${this.fieldName17__},")
        builder.appendLine("${nextIndentString}FieldName18__=${this.FieldName18__},")
        builder.appendLine("${nextIndentString}oneofField=${this.oneofField},")
        builder.append("${indentString})")
        return builder.toString()
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

    class NestedMessageInternal: TestAllTypesProto3.NestedMessage.Builder, InternalMessage(fieldsWithPresence = 1) {
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
            if (this.a != other.a) return false
            if (presenceMask[0] && this.corecursive != other.corecursive) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.NestedMessage(")
            builder.appendLine("${nextIndentString}a=${this.a},")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}corecursive=${this.corecursive.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}corecursive=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
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
        object MARSHALLER: MessageMarshaller<TestAllTypesProto3.NestedMessage> {
            override fun encode(value: TestAllTypesProto3.NestedMessage, config: MarshallerConfig?): Source {
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

            override fun decode(source: Source, config: MarshallerConfig?): TestAllTypesProto3.NestedMessage {
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
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto3.NestedMessage> {
            override val fullName: String = "protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage"
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapInt32Int32Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapInt64Int64Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapUint32Uint32Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapUint64Uint64Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapSint32Sint32Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapSint64Sint64Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapFixed32Fixed32Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapFixed64Fixed64Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapSfixed32Sfixed32Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapSfixed64Sfixed64Entry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapInt32FloatEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapInt32DoubleEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapBoolBoolEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapStringStringEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (!this.value.contentEquals(other.value)) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapStringBytesEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value.contentToString()},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (presenceMask[0] && this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapStringNestedMessageEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}value=${this.value.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (presenceMask[0] && this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapStringForeignMessageEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}value=${this.value.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapStringNestedEnumEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
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
            if (this.key != other.key) return false
            if (this.value != other.value) return false
            return true
        }

        override fun toString(): String {
            return asString()
        }

        fun asString(indent: Int = 0): String {
            checkRequiredFields()
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("TestAllTypesProto3.MapStringForeignEnumEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            builder.appendLine("${nextIndentString}value=${this.value},")
            builder.append("${indentString})")
            return builder.toString()
        }

        @InternalRpcApi
        companion object
    }

    @InternalRpcApi
    object MARSHALLER: MessageMarshaller<TestAllTypesProto3> {
        override fun encode(value: TestAllTypesProto3, config: MarshallerConfig?): Source {
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

        override fun decode(source: Source, config: MarshallerConfig?): TestAllTypesProto3 {
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
    object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto3> {
        override val fullName: String = "protobuf_test_messages.proto3.TestAllTypesProto3"
    }

    @InternalRpcApi
    companion object
}

class ForeignMessageInternal: ForeignMessage.Builder, InternalMessage(fieldsWithPresence = 0) {
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
        if (this.c != other.c) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ForeignMessage(")
        builder.appendLine("${nextIndentString}c=${this.c},")
        builder.append("${indentString})")
        return builder.toString()
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
    object MARSHALLER: MessageMarshaller<ForeignMessage> {
        override fun encode(value: ForeignMessage, config: MarshallerConfig?): Source {
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

        override fun decode(source: Source, config: MarshallerConfig?): ForeignMessage {
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
    object DESCRIPTOR: ProtoDescriptor<ForeignMessage> {
        override val fullName: String = "protobuf_test_messages.proto3.ForeignMessage"
    }

    @InternalRpcApi
    companion object
}

class NullHypothesisProto3Internal: NullHypothesisProto3.Builder, InternalMessage(fieldsWithPresence = 0) {
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
        val builder = StringBuilder()
        builder.appendLine("NullHypothesisProto3(")
        builder.append("${indentString})")
        return builder.toString()
    }

    @InternalRpcApi
    fun copyInternal(body: NullHypothesisProto3Internal.() -> Unit): NullHypothesisProto3Internal {
        val copy = NullHypothesisProto3Internal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: MessageMarshaller<NullHypothesisProto3> {
        override fun encode(value: NullHypothesisProto3, config: MarshallerConfig?): Source {
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

        override fun decode(source: Source, config: MarshallerConfig?): NullHypothesisProto3 {
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
    object DESCRIPTOR: ProtoDescriptor<NullHypothesisProto3> {
        override val fullName: String = "protobuf_test_messages.proto3.NullHypothesisProto3"
    }

    @InternalRpcApi
    companion object
}

class EnumOnlyProto3Internal: EnumOnlyProto3.Builder, InternalMessage(fieldsWithPresence = 0) {
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
        val builder = StringBuilder()
        builder.appendLine("EnumOnlyProto3(")
        builder.append("${indentString})")
        return builder.toString()
    }

    @InternalRpcApi
    fun copyInternal(body: EnumOnlyProto3Internal.() -> Unit): EnumOnlyProto3Internal {
        val copy = EnumOnlyProto3Internal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: MessageMarshaller<EnumOnlyProto3> {
        override fun encode(value: EnumOnlyProto3, config: MarshallerConfig?): Source {
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

        override fun decode(source: Source, config: MarshallerConfig?): EnumOnlyProto3 {
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
    object DESCRIPTOR: ProtoDescriptor<EnumOnlyProto3> {
        override val fullName: String = "protobuf_test_messages.proto3.EnumOnlyProto3"
    }

    @InternalRpcApi
    companion object
}

@InternalRpcApi
fun TestAllTypesProto3Internal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        this.optionalNestedMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[1]) {
        this.optionalForeignMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[2]) {
        this.recursiveMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[3]) {
        this.optionalBoolWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[4]) {
        this.optionalInt32Wrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[5]) {
        this.optionalInt64Wrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[6]) {
        this.optionalUint32Wrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[7]) {
        this.optionalUint64Wrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[8]) {
        this.optionalFloatWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[9]) {
        this.optionalDoubleWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[10]) {
        this.optionalStringWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[11]) {
        this.optionalBytesWrapper.asInternal().checkRequiredFields()
    }

    if (presenceMask[12]) {
        this.optionalDuration.asInternal().checkRequiredFields()
    }

    if (presenceMask[13]) {
        this.optionalTimestamp.asInternal().checkRequiredFields()
    }

    if (presenceMask[14]) {
        this.optionalFieldMask.asInternal().checkRequiredFields()
    }

    if (presenceMask[15]) {
        this.optionalStruct.asInternal().checkRequiredFields()
    }

    if (presenceMask[16]) {
        this.optionalAny.asInternal().checkRequiredFields()
    }

    if (presenceMask[17]) {
        this.optionalValue.asInternal().checkRequiredFields()
    }

    this.oneofField?.also {
        when {
            it is TestAllTypesProto3.OneofField.OneofNestedMessage -> {
                it.value.asInternal().checkRequiredFields()
            }
        }
    }

    this.repeatedNestedMessage.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedForeignMessage.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedBoolWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedInt32Wrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedInt64Wrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedUint32Wrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedUint64Wrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedFloatWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedDoubleWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedStringWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedBytesWrapper.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedDuration.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedTimestamp.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedFieldmask.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedStruct.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedAny.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedValue.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.repeatedListValue.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.mapStringNestedMessage.values.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.mapStringForeignMessage.values.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.optionalInt32 != 0) {
        encoder.writeInt32(fieldNr = 1, value = this.optionalInt32)
    }

    if (this.optionalInt64 != 0L) {
        encoder.writeInt64(fieldNr = 2, value = this.optionalInt64)
    }

    if (this.optionalUint32 != 0u) {
        encoder.writeUInt32(fieldNr = 3, value = this.optionalUint32)
    }

    if (this.optionalUint64 != 0uL) {
        encoder.writeUInt64(fieldNr = 4, value = this.optionalUint64)
    }

    if (this.optionalSint32 != 0) {
        encoder.writeSInt32(fieldNr = 5, value = this.optionalSint32)
    }

    if (this.optionalSint64 != 0L) {
        encoder.writeSInt64(fieldNr = 6, value = this.optionalSint64)
    }

    if (this.optionalFixed32 != 0u) {
        encoder.writeFixed32(fieldNr = 7, value = this.optionalFixed32)
    }

    if (this.optionalFixed64 != 0uL) {
        encoder.writeFixed64(fieldNr = 8, value = this.optionalFixed64)
    }

    if (this.optionalSfixed32 != 0) {
        encoder.writeSFixed32(fieldNr = 9, value = this.optionalSfixed32)
    }

    if (this.optionalSfixed64 != 0L) {
        encoder.writeSFixed64(fieldNr = 10, value = this.optionalSfixed64)
    }

    if (this.optionalFloat != 0.0f) {
        encoder.writeFloat(fieldNr = 11, value = this.optionalFloat)
    }

    if (this.optionalDouble != 0.0) {
        encoder.writeDouble(fieldNr = 12, value = this.optionalDouble)
    }

    if (this.optionalBool != false) {
        encoder.writeBool(fieldNr = 13, value = this.optionalBool)
    }

    if (this.optionalString.isNotEmpty()) {
        encoder.writeString(fieldNr = 14, value = this.optionalString)
    }

    if (this.optionalBytes.isNotEmpty()) {
        encoder.writeBytes(fieldNr = 15, value = this.optionalBytes)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 18, value = this.optionalNestedMessage.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[1]) {
        encoder.writeMessage(fieldNr = 19, value = this.optionalForeignMessage.asInternal()) { encodeWith(it, config) }
    }

    if (this.optionalNestedEnum != TestAllTypesProto3.NestedEnum.FOO) {
        encoder.writeEnum(fieldNr = 21, value = this.optionalNestedEnum.number)
    }

    if (this.optionalForeignEnum != ForeignEnum.FOREIGN_FOO) {
        encoder.writeEnum(fieldNr = 22, value = this.optionalForeignEnum.number)
    }

    if (this.optionalAliasedEnum != TestAllTypesProto3.AliasedEnum.ALIAS_FOO) {
        encoder.writeEnum(fieldNr = 23, value = this.optionalAliasedEnum.number)
    }

    if (this.optionalStringPiece.isNotEmpty()) {
        encoder.writeString(fieldNr = 24, value = this.optionalStringPiece)
    }

    if (this.optionalCord.isNotEmpty()) {
        encoder.writeString(fieldNr = 25, value = this.optionalCord)
    }

    if (presenceMask[2]) {
        encoder.writeMessage(fieldNr = 27, value = this.recursiveMessage.asInternal()) { encodeWith(it, config) }
    }

    if (this.repeatedInt32.isNotEmpty()) {
        encoder.writePackedInt32(fieldNr = 31, value = this.repeatedInt32, fieldSize = WireSize.packedInt32(this.repeatedInt32))
    }

    if (this.repeatedInt64.isNotEmpty()) {
        encoder.writePackedInt64(fieldNr = 32, value = this.repeatedInt64, fieldSize = WireSize.packedInt64(this.repeatedInt64))
    }

    if (this.repeatedUint32.isNotEmpty()) {
        encoder.writePackedUInt32(fieldNr = 33, value = this.repeatedUint32, fieldSize = WireSize.packedUInt32(this.repeatedUint32))
    }

    if (this.repeatedUint64.isNotEmpty()) {
        encoder.writePackedUInt64(fieldNr = 34, value = this.repeatedUint64, fieldSize = WireSize.packedUInt64(this.repeatedUint64))
    }

    if (this.repeatedSint32.isNotEmpty()) {
        encoder.writePackedSInt32(fieldNr = 35, value = this.repeatedSint32, fieldSize = WireSize.packedSInt32(this.repeatedSint32))
    }

    if (this.repeatedSint64.isNotEmpty()) {
        encoder.writePackedSInt64(fieldNr = 36, value = this.repeatedSint64, fieldSize = WireSize.packedSInt64(this.repeatedSint64))
    }

    if (this.repeatedFixed32.isNotEmpty()) {
        encoder.writePackedFixed32(fieldNr = 37, value = this.repeatedFixed32)
    }

    if (this.repeatedFixed64.isNotEmpty()) {
        encoder.writePackedFixed64(fieldNr = 38, value = this.repeatedFixed64)
    }

    if (this.repeatedSfixed32.isNotEmpty()) {
        encoder.writePackedSFixed32(fieldNr = 39, value = this.repeatedSfixed32)
    }

    if (this.repeatedSfixed64.isNotEmpty()) {
        encoder.writePackedSFixed64(fieldNr = 40, value = this.repeatedSfixed64)
    }

    if (this.repeatedFloat.isNotEmpty()) {
        encoder.writePackedFloat(fieldNr = 41, value = this.repeatedFloat)
    }

    if (this.repeatedDouble.isNotEmpty()) {
        encoder.writePackedDouble(fieldNr = 42, value = this.repeatedDouble)
    }

    if (this.repeatedBool.isNotEmpty()) {
        encoder.writePackedBool(fieldNr = 43, value = this.repeatedBool, fieldSize = WireSize.packedBool(this.repeatedBool))
    }

    if (this.repeatedString.isNotEmpty()) {
        this.repeatedString.forEach {
            encoder.writeString(44, it)
        }
    }

    if (this.repeatedBytes.isNotEmpty()) {
        this.repeatedBytes.forEach {
            encoder.writeBytes(45, it)
        }
    }

    if (this.repeatedNestedMessage.isNotEmpty()) {
        this.repeatedNestedMessage.forEach {
            encoder.writeMessage(fieldNr = 48, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedForeignMessage.isNotEmpty()) {
        this.repeatedForeignMessage.forEach {
            encoder.writeMessage(fieldNr = 49, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedNestedEnum.isNotEmpty()) {
        encoder.writePackedEnum(fieldNr = 51, value = this.repeatedNestedEnum.map { it.number }, fieldSize = WireSize.packedEnum(this.repeatedNestedEnum.map { it.number }))
    }

    if (this.repeatedForeignEnum.isNotEmpty()) {
        encoder.writePackedEnum(fieldNr = 52, value = this.repeatedForeignEnum.map { it.number }, fieldSize = WireSize.packedEnum(this.repeatedForeignEnum.map { it.number }))
    }

    if (this.repeatedStringPiece.isNotEmpty()) {
        this.repeatedStringPiece.forEach {
            encoder.writeString(54, it)
        }
    }

    if (this.repeatedCord.isNotEmpty()) {
        this.repeatedCord.forEach {
            encoder.writeString(55, it)
        }
    }

    if (this.packedInt32.isNotEmpty()) {
        encoder.writePackedInt32(fieldNr = 75, value = this.packedInt32, fieldSize = WireSize.packedInt32(this.packedInt32))
    }

    if (this.packedInt64.isNotEmpty()) {
        encoder.writePackedInt64(fieldNr = 76, value = this.packedInt64, fieldSize = WireSize.packedInt64(this.packedInt64))
    }

    if (this.packedUint32.isNotEmpty()) {
        encoder.writePackedUInt32(fieldNr = 77, value = this.packedUint32, fieldSize = WireSize.packedUInt32(this.packedUint32))
    }

    if (this.packedUint64.isNotEmpty()) {
        encoder.writePackedUInt64(fieldNr = 78, value = this.packedUint64, fieldSize = WireSize.packedUInt64(this.packedUint64))
    }

    if (this.packedSint32.isNotEmpty()) {
        encoder.writePackedSInt32(fieldNr = 79, value = this.packedSint32, fieldSize = WireSize.packedSInt32(this.packedSint32))
    }

    if (this.packedSint64.isNotEmpty()) {
        encoder.writePackedSInt64(fieldNr = 80, value = this.packedSint64, fieldSize = WireSize.packedSInt64(this.packedSint64))
    }

    if (this.packedFixed32.isNotEmpty()) {
        encoder.writePackedFixed32(fieldNr = 81, value = this.packedFixed32)
    }

    if (this.packedFixed64.isNotEmpty()) {
        encoder.writePackedFixed64(fieldNr = 82, value = this.packedFixed64)
    }

    if (this.packedSfixed32.isNotEmpty()) {
        encoder.writePackedSFixed32(fieldNr = 83, value = this.packedSfixed32)
    }

    if (this.packedSfixed64.isNotEmpty()) {
        encoder.writePackedSFixed64(fieldNr = 84, value = this.packedSfixed64)
    }

    if (this.packedFloat.isNotEmpty()) {
        encoder.writePackedFloat(fieldNr = 85, value = this.packedFloat)
    }

    if (this.packedDouble.isNotEmpty()) {
        encoder.writePackedDouble(fieldNr = 86, value = this.packedDouble)
    }

    if (this.packedBool.isNotEmpty()) {
        encoder.writePackedBool(fieldNr = 87, value = this.packedBool, fieldSize = WireSize.packedBool(this.packedBool))
    }

    if (this.packedNestedEnum.isNotEmpty()) {
        encoder.writePackedEnum(fieldNr = 88, value = this.packedNestedEnum.map { it.number }, fieldSize = WireSize.packedEnum(this.packedNestedEnum.map { it.number }))
    }

    if (this.unpackedInt32.isNotEmpty()) {
        this.unpackedInt32.forEach {
            encoder.writeInt32(89, it)
        }
    }

    if (this.unpackedInt64.isNotEmpty()) {
        this.unpackedInt64.forEach {
            encoder.writeInt64(90, it)
        }
    }

    if (this.unpackedUint32.isNotEmpty()) {
        this.unpackedUint32.forEach {
            encoder.writeUInt32(91, it)
        }
    }

    if (this.unpackedUint64.isNotEmpty()) {
        this.unpackedUint64.forEach {
            encoder.writeUInt64(92, it)
        }
    }

    if (this.unpackedSint32.isNotEmpty()) {
        this.unpackedSint32.forEach {
            encoder.writeSInt32(93, it)
        }
    }

    if (this.unpackedSint64.isNotEmpty()) {
        this.unpackedSint64.forEach {
            encoder.writeSInt64(94, it)
        }
    }

    if (this.unpackedFixed32.isNotEmpty()) {
        this.unpackedFixed32.forEach {
            encoder.writeFixed32(95, it)
        }
    }

    if (this.unpackedFixed64.isNotEmpty()) {
        this.unpackedFixed64.forEach {
            encoder.writeFixed64(96, it)
        }
    }

    if (this.unpackedSfixed32.isNotEmpty()) {
        this.unpackedSfixed32.forEach {
            encoder.writeSFixed32(97, it)
        }
    }

    if (this.unpackedSfixed64.isNotEmpty()) {
        this.unpackedSfixed64.forEach {
            encoder.writeSFixed64(98, it)
        }
    }

    if (this.unpackedFloat.isNotEmpty()) {
        this.unpackedFloat.forEach {
            encoder.writeFloat(99, it)
        }
    }

    if (this.unpackedDouble.isNotEmpty()) {
        this.unpackedDouble.forEach {
            encoder.writeDouble(100, it)
        }
    }

    if (this.unpackedBool.isNotEmpty()) {
        this.unpackedBool.forEach {
            encoder.writeBool(101, it)
        }
    }

    if (this.unpackedNestedEnum.isNotEmpty()) {
        this.unpackedNestedEnum.forEach {
            encoder.writeEnum(102, it.number)
        }
    }

    if (this.mapInt32Int32.isNotEmpty()) {
        this.mapInt32Int32.forEach { kEntry ->
            TestAllTypesProto3Internal.MapInt32Int32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 56, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapInt64Int64.isNotEmpty()) {
        this.mapInt64Int64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapInt64Int64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 57, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapUint32Uint32.isNotEmpty()) {
        this.mapUint32Uint32.forEach { kEntry ->
            TestAllTypesProto3Internal.MapUint32Uint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 58, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapUint64Uint64.isNotEmpty()) {
        this.mapUint64Uint64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapUint64Uint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 59, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapSint32Sint32.isNotEmpty()) {
        this.mapSint32Sint32.forEach { kEntry ->
            TestAllTypesProto3Internal.MapSint32Sint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 60, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapSint64Sint64.isNotEmpty()) {
        this.mapSint64Sint64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapSint64Sint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 61, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapFixed32Fixed32.isNotEmpty()) {
        this.mapFixed32Fixed32.forEach { kEntry ->
            TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 62, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapFixed64Fixed64.isNotEmpty()) {
        this.mapFixed64Fixed64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 63, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapSfixed32Sfixed32.isNotEmpty()) {
        this.mapSfixed32Sfixed32.forEach { kEntry ->
            TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 64, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapSfixed64Sfixed64.isNotEmpty()) {
        this.mapSfixed64Sfixed64.forEach { kEntry ->
            TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 65, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapInt32Float.isNotEmpty()) {
        this.mapInt32Float.forEach { kEntry ->
            TestAllTypesProto3Internal.MapInt32FloatEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 66, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapInt32Double.isNotEmpty()) {
        this.mapInt32Double.forEach { kEntry ->
            TestAllTypesProto3Internal.MapInt32DoubleEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 67, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapBoolBool.isNotEmpty()) {
        this.mapBoolBool.forEach { kEntry ->
            TestAllTypesProto3Internal.MapBoolBoolEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 68, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapStringString.isNotEmpty()) {
        this.mapStringString.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringStringEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 69, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapStringBytes.isNotEmpty()) {
        this.mapStringBytes.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringBytesEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 70, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapStringNestedMessage.isNotEmpty()) {
        this.mapStringNestedMessage.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 71, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapStringForeignMessage.isNotEmpty()) {
        this.mapStringForeignMessage.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 72, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapStringNestedEnum.isNotEmpty()) {
        this.mapStringNestedEnum.forEach { kEntry ->
            TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 73, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapStringForeignEnum.isNotEmpty()) {
        this.mapStringForeignEnum.forEach { kEntry ->
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
        encoder.writeMessage(fieldNr = 201, value = this.optionalBoolWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[4]) {
        encoder.writeMessage(fieldNr = 202, value = this.optionalInt32Wrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[5]) {
        encoder.writeMessage(fieldNr = 203, value = this.optionalInt64Wrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[6]) {
        encoder.writeMessage(fieldNr = 204, value = this.optionalUint32Wrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[7]) {
        encoder.writeMessage(fieldNr = 205, value = this.optionalUint64Wrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[8]) {
        encoder.writeMessage(fieldNr = 206, value = this.optionalFloatWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[9]) {
        encoder.writeMessage(fieldNr = 207, value = this.optionalDoubleWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[10]) {
        encoder.writeMessage(fieldNr = 208, value = this.optionalStringWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[11]) {
        encoder.writeMessage(fieldNr = 209, value = this.optionalBytesWrapper.asInternal()) { encodeWith(it, config) }
    }

    if (this.repeatedBoolWrapper.isNotEmpty()) {
        this.repeatedBoolWrapper.forEach {
            encoder.writeMessage(fieldNr = 211, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedInt32Wrapper.isNotEmpty()) {
        this.repeatedInt32Wrapper.forEach {
            encoder.writeMessage(fieldNr = 212, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedInt64Wrapper.isNotEmpty()) {
        this.repeatedInt64Wrapper.forEach {
            encoder.writeMessage(fieldNr = 213, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedUint32Wrapper.isNotEmpty()) {
        this.repeatedUint32Wrapper.forEach {
            encoder.writeMessage(fieldNr = 214, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedUint64Wrapper.isNotEmpty()) {
        this.repeatedUint64Wrapper.forEach {
            encoder.writeMessage(fieldNr = 215, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedFloatWrapper.isNotEmpty()) {
        this.repeatedFloatWrapper.forEach {
            encoder.writeMessage(fieldNr = 216, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedDoubleWrapper.isNotEmpty()) {
        this.repeatedDoubleWrapper.forEach {
            encoder.writeMessage(fieldNr = 217, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedStringWrapper.isNotEmpty()) {
        this.repeatedStringWrapper.forEach {
            encoder.writeMessage(fieldNr = 218, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedBytesWrapper.isNotEmpty()) {
        this.repeatedBytesWrapper.forEach {
            encoder.writeMessage(fieldNr = 219, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (presenceMask[12]) {
        encoder.writeMessage(fieldNr = 301, value = this.optionalDuration.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[13]) {
        encoder.writeMessage(fieldNr = 302, value = this.optionalTimestamp.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[14]) {
        encoder.writeMessage(fieldNr = 303, value = this.optionalFieldMask.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[15]) {
        encoder.writeMessage(fieldNr = 304, value = this.optionalStruct.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[16]) {
        encoder.writeMessage(fieldNr = 305, value = this.optionalAny.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[17]) {
        encoder.writeMessage(fieldNr = 306, value = this.optionalValue.asInternal()) { encodeWith(it, config) }
    }

    if (this.optionalNullValue != NullValue.NULL_VALUE) {
        encoder.writeEnum(fieldNr = 307, value = this.optionalNullValue.number)
    }

    if (this.repeatedDuration.isNotEmpty()) {
        this.repeatedDuration.forEach {
            encoder.writeMessage(fieldNr = 311, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedTimestamp.isNotEmpty()) {
        this.repeatedTimestamp.forEach {
            encoder.writeMessage(fieldNr = 312, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedFieldmask.isNotEmpty()) {
        this.repeatedFieldmask.forEach {
            encoder.writeMessage(fieldNr = 313, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedStruct.isNotEmpty()) {
        this.repeatedStruct.forEach {
            encoder.writeMessage(fieldNr = 324, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedAny.isNotEmpty()) {
        this.repeatedAny.forEach {
            encoder.writeMessage(fieldNr = 315, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedValue.isNotEmpty()) {
        this.repeatedValue.forEach {
            encoder.writeMessage(fieldNr = 316, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.repeatedListValue.isNotEmpty()) {
        this.repeatedListValue.forEach {
            encoder.writeMessage(fieldNr = 317, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.fieldname1 != 0) {
        encoder.writeInt32(fieldNr = 401, value = this.fieldname1)
    }

    if (this.fieldName2 != 0) {
        encoder.writeInt32(fieldNr = 402, value = this.fieldName2)
    }

    if (this.FieldName3 != 0) {
        encoder.writeInt32(fieldNr = 403, value = this.FieldName3)
    }

    if (this.field_Name4_ != 0) {
        encoder.writeInt32(fieldNr = 404, value = this.field_Name4_)
    }

    if (this.field0name5 != 0) {
        encoder.writeInt32(fieldNr = 405, value = this.field0name5)
    }

    if (this.field_0Name6 != 0) {
        encoder.writeInt32(fieldNr = 406, value = this.field_0Name6)
    }

    if (this.fieldName7 != 0) {
        encoder.writeInt32(fieldNr = 407, value = this.fieldName7)
    }

    if (this.FieldName8 != 0) {
        encoder.writeInt32(fieldNr = 408, value = this.FieldName8)
    }

    if (this.field_Name9 != 0) {
        encoder.writeInt32(fieldNr = 409, value = this.field_Name9)
    }

    if (this.Field_Name10 != 0) {
        encoder.writeInt32(fieldNr = 410, value = this.Field_Name10)
    }

    if (this.FIELD_NAME11 != 0) {
        encoder.writeInt32(fieldNr = 411, value = this.FIELD_NAME11)
    }

    if (this.FIELDName12 != 0) {
        encoder.writeInt32(fieldNr = 412, value = this.FIELDName12)
    }

    if (this._FieldName13 != 0) {
        encoder.writeInt32(fieldNr = 413, value = this._FieldName13)
    }

    if (this.__FieldName14 != 0) {
        encoder.writeInt32(fieldNr = 414, value = this.__FieldName14)
    }

    if (this.field_Name15 != 0) {
        encoder.writeInt32(fieldNr = 415, value = this.field_Name15)
    }

    if (this.field__Name16 != 0) {
        encoder.writeInt32(fieldNr = 416, value = this.field__Name16)
    }

    if (this.fieldName17__ != 0) {
        encoder.writeInt32(fieldNr = 417, value = this.fieldName17__)
    }

    if (this.FieldName18__ != 0) {
        encoder.writeInt32(fieldNr = 418, value = this.FieldName18__)
    }

    this.oneofField?.also {
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
    if (this.optionalInt32 != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.optionalInt32))
    }

    if (this.optionalInt64 != 0L) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int64(this.optionalInt64))
    }

    if (this.optionalUint32 != 0u) {
        __result += (WireSize.tag(3, WireType.VARINT) + WireSize.uInt32(this.optionalUint32))
    }

    if (this.optionalUint64 != 0uL) {
        __result += (WireSize.tag(4, WireType.VARINT) + WireSize.uInt64(this.optionalUint64))
    }

    if (this.optionalSint32 != 0) {
        __result += (WireSize.tag(5, WireType.VARINT) + WireSize.sInt32(this.optionalSint32))
    }

    if (this.optionalSint64 != 0L) {
        __result += (WireSize.tag(6, WireType.VARINT) + WireSize.sInt64(this.optionalSint64))
    }

    if (this.optionalFixed32 != 0u) {
        __result += (WireSize.tag(7, WireType.FIXED32) + WireSize.fixed32(this.optionalFixed32))
    }

    if (this.optionalFixed64 != 0uL) {
        __result += (WireSize.tag(8, WireType.FIXED64) + WireSize.fixed64(this.optionalFixed64))
    }

    if (this.optionalSfixed32 != 0) {
        __result += (WireSize.tag(9, WireType.FIXED32) + WireSize.sFixed32(this.optionalSfixed32))
    }

    if (this.optionalSfixed64 != 0L) {
        __result += (WireSize.tag(10, WireType.FIXED64) + WireSize.sFixed64(this.optionalSfixed64))
    }

    if (this.optionalFloat != 0.0f) {
        __result += (WireSize.tag(11, WireType.FIXED32) + WireSize.float(this.optionalFloat))
    }

    if (this.optionalDouble != 0.0) {
        __result += (WireSize.tag(12, WireType.FIXED64) + WireSize.double(this.optionalDouble))
    }

    if (this.optionalBool != false) {
        __result += (WireSize.tag(13, WireType.VARINT) + WireSize.bool(this.optionalBool))
    }

    if (this.optionalString.isNotEmpty()) {
        __result += WireSize.string(this.optionalString).let { WireSize.tag(14, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.optionalBytes.isNotEmpty()) {
        __result += WireSize.bytes(this.optionalBytes).let { WireSize.tag(15, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += this.optionalNestedMessage.asInternal()._size.let { WireSize.tag(18, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[1]) {
        __result += this.optionalForeignMessage.asInternal()._size.let { WireSize.tag(19, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.optionalNestedEnum != TestAllTypesProto3.NestedEnum.FOO) {
        __result += (WireSize.tag(21, WireType.VARINT) + WireSize.enum(this.optionalNestedEnum.number))
    }

    if (this.optionalForeignEnum != ForeignEnum.FOREIGN_FOO) {
        __result += (WireSize.tag(22, WireType.VARINT) + WireSize.enum(this.optionalForeignEnum.number))
    }

    if (this.optionalAliasedEnum != TestAllTypesProto3.AliasedEnum.ALIAS_FOO) {
        __result += (WireSize.tag(23, WireType.VARINT) + WireSize.enum(this.optionalAliasedEnum.number))
    }

    if (this.optionalStringPiece.isNotEmpty()) {
        __result += WireSize.string(this.optionalStringPiece).let { WireSize.tag(24, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.optionalCord.isNotEmpty()) {
        __result += WireSize.string(this.optionalCord).let { WireSize.tag(25, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[2]) {
        __result += this.recursiveMessage.asInternal()._size.let { WireSize.tag(27, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.repeatedInt32.isNotEmpty()) {
        __result += WireSize.packedInt32(this.repeatedInt32).let { WireSize.tag(31, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.repeatedInt64.isNotEmpty()) {
        __result += WireSize.packedInt64(this.repeatedInt64).let { WireSize.tag(32, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.repeatedUint32.isNotEmpty()) {
        __result += WireSize.packedUInt32(this.repeatedUint32).let { WireSize.tag(33, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.repeatedUint64.isNotEmpty()) {
        __result += WireSize.packedUInt64(this.repeatedUint64).let { WireSize.tag(34, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.repeatedSint32.isNotEmpty()) {
        __result += WireSize.packedSInt32(this.repeatedSint32).let { WireSize.tag(35, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.repeatedSint64.isNotEmpty()) {
        __result += WireSize.packedSInt64(this.repeatedSint64).let { WireSize.tag(36, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.repeatedFixed32.isNotEmpty()) {
        __result += WireSize.packedFixed32(this.repeatedFixed32).let { WireSize.tag(37, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (this.repeatedFixed64.isNotEmpty()) {
        __result += WireSize.packedFixed64(this.repeatedFixed64).let { WireSize.tag(38, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (this.repeatedSfixed32.isNotEmpty()) {
        __result += WireSize.packedSFixed32(this.repeatedSfixed32).let { WireSize.tag(39, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (this.repeatedSfixed64.isNotEmpty()) {
        __result += WireSize.packedSFixed64(this.repeatedSfixed64).let { WireSize.tag(40, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (this.repeatedFloat.isNotEmpty()) {
        __result += WireSize.packedFloat(this.repeatedFloat).let { WireSize.tag(41, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (this.repeatedDouble.isNotEmpty()) {
        __result += WireSize.packedDouble(this.repeatedDouble).let { WireSize.tag(42, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (this.repeatedBool.isNotEmpty()) {
        __result += WireSize.packedBool(this.repeatedBool).let { WireSize.tag(43, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.repeatedString.isNotEmpty()) {
        __result += this.repeatedString.sumOf { WireSize.string(it).let { WireSize.tag(44, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedBytes.isNotEmpty()) {
        __result += this.repeatedBytes.sumOf { WireSize.bytes(it).let { WireSize.tag(45, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedNestedMessage.isNotEmpty()) {
        __result += this.repeatedNestedMessage.sumOf { it.asInternal()._size.let { WireSize.tag(48, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedForeignMessage.isNotEmpty()) {
        __result += this.repeatedForeignMessage.sumOf { it.asInternal()._size.let { WireSize.tag(49, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedNestedEnum.isNotEmpty()) {
        __result += WireSize.packedEnum(this.repeatedNestedEnum.map { it.number }).let { WireSize.tag(51, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.repeatedForeignEnum.isNotEmpty()) {
        __result += WireSize.packedEnum(this.repeatedForeignEnum.map { it.number }).let { WireSize.tag(52, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.repeatedStringPiece.isNotEmpty()) {
        __result += this.repeatedStringPiece.sumOf { WireSize.string(it).let { WireSize.tag(54, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedCord.isNotEmpty()) {
        __result += this.repeatedCord.sumOf { WireSize.string(it).let { WireSize.tag(55, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.packedInt32.isNotEmpty()) {
        __result += WireSize.packedInt32(this.packedInt32).let { WireSize.tag(75, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.packedInt64.isNotEmpty()) {
        __result += WireSize.packedInt64(this.packedInt64).let { WireSize.tag(76, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.packedUint32.isNotEmpty()) {
        __result += WireSize.packedUInt32(this.packedUint32).let { WireSize.tag(77, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.packedUint64.isNotEmpty()) {
        __result += WireSize.packedUInt64(this.packedUint64).let { WireSize.tag(78, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.packedSint32.isNotEmpty()) {
        __result += WireSize.packedSInt32(this.packedSint32).let { WireSize.tag(79, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.packedSint64.isNotEmpty()) {
        __result += WireSize.packedSInt64(this.packedSint64).let { WireSize.tag(80, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.packedFixed32.isNotEmpty()) {
        __result += WireSize.packedFixed32(this.packedFixed32).let { WireSize.tag(81, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (this.packedFixed64.isNotEmpty()) {
        __result += WireSize.packedFixed64(this.packedFixed64).let { WireSize.tag(82, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (this.packedSfixed32.isNotEmpty()) {
        __result += WireSize.packedSFixed32(this.packedSfixed32).let { WireSize.tag(83, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (this.packedSfixed64.isNotEmpty()) {
        __result += WireSize.packedSFixed64(this.packedSfixed64).let { WireSize.tag(84, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (this.packedFloat.isNotEmpty()) {
        __result += WireSize.packedFloat(this.packedFloat).let { WireSize.tag(85, WireType.FIXED32) + WireSize.int32(it) + it }
    }

    if (this.packedDouble.isNotEmpty()) {
        __result += WireSize.packedDouble(this.packedDouble).let { WireSize.tag(86, WireType.FIXED64) + WireSize.int32(it) + it }
    }

    if (this.packedBool.isNotEmpty()) {
        __result += WireSize.packedBool(this.packedBool).let { WireSize.tag(87, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.packedNestedEnum.isNotEmpty()) {
        __result += WireSize.packedEnum(this.packedNestedEnum.map { it.number }).let { WireSize.tag(88, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.unpackedInt32.isNotEmpty()) {
        __result += this.unpackedInt32.sumOf { WireSize.tag(89, WireType.VARINT) + WireSize.int32(it) }
    }

    if (this.unpackedInt64.isNotEmpty()) {
        __result += this.unpackedInt64.sumOf { WireSize.tag(90, WireType.VARINT) + WireSize.int64(it) }
    }

    if (this.unpackedUint32.isNotEmpty()) {
        __result += this.unpackedUint32.sumOf { WireSize.tag(91, WireType.VARINT) + WireSize.uInt32(it) }
    }

    if (this.unpackedUint64.isNotEmpty()) {
        __result += this.unpackedUint64.sumOf { WireSize.tag(92, WireType.VARINT) + WireSize.uInt64(it) }
    }

    if (this.unpackedSint32.isNotEmpty()) {
        __result += this.unpackedSint32.sumOf { WireSize.tag(93, WireType.VARINT) + WireSize.sInt32(it) }
    }

    if (this.unpackedSint64.isNotEmpty()) {
        __result += this.unpackedSint64.sumOf { WireSize.tag(94, WireType.VARINT) + WireSize.sInt64(it) }
    }

    if (this.unpackedFixed32.isNotEmpty()) {
        __result += this.unpackedFixed32.sumOf { WireSize.tag(95, WireType.FIXED32) + WireSize.fixed32(it) }
    }

    if (this.unpackedFixed64.isNotEmpty()) {
        __result += this.unpackedFixed64.sumOf { WireSize.tag(96, WireType.FIXED64) + WireSize.fixed64(it) }
    }

    if (this.unpackedSfixed32.isNotEmpty()) {
        __result += this.unpackedSfixed32.sumOf { WireSize.tag(97, WireType.FIXED32) + WireSize.sFixed32(it) }
    }

    if (this.unpackedSfixed64.isNotEmpty()) {
        __result += this.unpackedSfixed64.sumOf { WireSize.tag(98, WireType.FIXED64) + WireSize.sFixed64(it) }
    }

    if (this.unpackedFloat.isNotEmpty()) {
        __result += this.unpackedFloat.sumOf { WireSize.tag(99, WireType.FIXED32) + WireSize.float(it) }
    }

    if (this.unpackedDouble.isNotEmpty()) {
        __result += this.unpackedDouble.sumOf { WireSize.tag(100, WireType.FIXED64) + WireSize.double(it) }
    }

    if (this.unpackedBool.isNotEmpty()) {
        __result += this.unpackedBool.sumOf { WireSize.tag(101, WireType.VARINT) + WireSize.bool(it) }
    }

    if (this.unpackedNestedEnum.isNotEmpty()) {
        __result += this.unpackedNestedEnum.sumOf { WireSize.tag(102, WireType.VARINT) + WireSize.enum(it.number) }
    }

    if (this.mapInt32Int32.isNotEmpty()) {
        __result += mapInt32Int32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapInt32Int32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(56, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt64Int64.isNotEmpty()) {
        __result += mapInt64Int64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapInt64Int64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(57, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapUint32Uint32.isNotEmpty()) {
        __result += mapUint32Uint32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapUint32Uint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(58, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapUint64Uint64.isNotEmpty()) {
        __result += mapUint64Uint64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapUint64Uint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(59, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSint32Sint32.isNotEmpty()) {
        __result += mapSint32Sint32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapSint32Sint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(60, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSint64Sint64.isNotEmpty()) {
        __result += mapSint64Sint64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapSint64Sint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(61, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapFixed32Fixed32.isNotEmpty()) {
        __result += mapFixed32Fixed32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(62, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapFixed64Fixed64.isNotEmpty()) {
        __result += mapFixed64Fixed64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(63, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSfixed32Sfixed32.isNotEmpty()) {
        __result += mapSfixed32Sfixed32.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(64, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSfixed64Sfixed64.isNotEmpty()) {
        __result += mapSfixed64Sfixed64.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(65, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt32Float.isNotEmpty()) {
        __result += mapInt32Float.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapInt32FloatEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(66, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt32Double.isNotEmpty()) {
        __result += mapInt32Double.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapInt32DoubleEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(67, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapBoolBool.isNotEmpty()) {
        __result += mapBoolBool.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapBoolBoolEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(68, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringString.isNotEmpty()) {
        __result += mapStringString.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringStringEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(69, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringBytes.isNotEmpty()) {
        __result += mapStringBytes.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringBytesEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(70, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringNestedMessage.isNotEmpty()) {
        __result += mapStringNestedMessage.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(71, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringForeignMessage.isNotEmpty()) {
        __result += mapStringForeignMessage.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(72, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringNestedEnum.isNotEmpty()) {
        __result += mapStringNestedEnum.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(73, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringForeignEnum.isNotEmpty()) {
        __result += mapStringForeignEnum.entries.sumOf { kEntry ->
            TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(74, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (presenceMask[3]) {
        __result += this.optionalBoolWrapper.asInternal()._size.let { WireSize.tag(201, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[4]) {
        __result += this.optionalInt32Wrapper.asInternal()._size.let { WireSize.tag(202, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[5]) {
        __result += this.optionalInt64Wrapper.asInternal()._size.let { WireSize.tag(203, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[6]) {
        __result += this.optionalUint32Wrapper.asInternal()._size.let { WireSize.tag(204, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[7]) {
        __result += this.optionalUint64Wrapper.asInternal()._size.let { WireSize.tag(205, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[8]) {
        __result += this.optionalFloatWrapper.asInternal()._size.let { WireSize.tag(206, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[9]) {
        __result += this.optionalDoubleWrapper.asInternal()._size.let { WireSize.tag(207, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[10]) {
        __result += this.optionalStringWrapper.asInternal()._size.let { WireSize.tag(208, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[11]) {
        __result += this.optionalBytesWrapper.asInternal()._size.let { WireSize.tag(209, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.repeatedBoolWrapper.isNotEmpty()) {
        __result += this.repeatedBoolWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(211, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedInt32Wrapper.isNotEmpty()) {
        __result += this.repeatedInt32Wrapper.sumOf { it.asInternal()._size.let { WireSize.tag(212, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedInt64Wrapper.isNotEmpty()) {
        __result += this.repeatedInt64Wrapper.sumOf { it.asInternal()._size.let { WireSize.tag(213, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedUint32Wrapper.isNotEmpty()) {
        __result += this.repeatedUint32Wrapper.sumOf { it.asInternal()._size.let { WireSize.tag(214, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedUint64Wrapper.isNotEmpty()) {
        __result += this.repeatedUint64Wrapper.sumOf { it.asInternal()._size.let { WireSize.tag(215, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedFloatWrapper.isNotEmpty()) {
        __result += this.repeatedFloatWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(216, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedDoubleWrapper.isNotEmpty()) {
        __result += this.repeatedDoubleWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(217, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedStringWrapper.isNotEmpty()) {
        __result += this.repeatedStringWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(218, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedBytesWrapper.isNotEmpty()) {
        __result += this.repeatedBytesWrapper.sumOf { it.asInternal()._size.let { WireSize.tag(219, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[12]) {
        __result += this.optionalDuration.asInternal()._size.let { WireSize.tag(301, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[13]) {
        __result += this.optionalTimestamp.asInternal()._size.let { WireSize.tag(302, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[14]) {
        __result += this.optionalFieldMask.asInternal()._size.let { WireSize.tag(303, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[15]) {
        __result += this.optionalStruct.asInternal()._size.let { WireSize.tag(304, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[16]) {
        __result += this.optionalAny.asInternal()._size.let { WireSize.tag(305, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[17]) {
        __result += this.optionalValue.asInternal()._size.let { WireSize.tag(306, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.optionalNullValue != NullValue.NULL_VALUE) {
        __result += (WireSize.tag(307, WireType.VARINT) + WireSize.enum(this.optionalNullValue.number))
    }

    if (this.repeatedDuration.isNotEmpty()) {
        __result += this.repeatedDuration.sumOf { it.asInternal()._size.let { WireSize.tag(311, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedTimestamp.isNotEmpty()) {
        __result += this.repeatedTimestamp.sumOf { it.asInternal()._size.let { WireSize.tag(312, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedFieldmask.isNotEmpty()) {
        __result += this.repeatedFieldmask.sumOf { it.asInternal()._size.let { WireSize.tag(313, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedStruct.isNotEmpty()) {
        __result += this.repeatedStruct.sumOf { it.asInternal()._size.let { WireSize.tag(324, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedAny.isNotEmpty()) {
        __result += this.repeatedAny.sumOf { it.asInternal()._size.let { WireSize.tag(315, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedValue.isNotEmpty()) {
        __result += this.repeatedValue.sumOf { it.asInternal()._size.let { WireSize.tag(316, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.repeatedListValue.isNotEmpty()) {
        __result += this.repeatedListValue.sumOf { it.asInternal()._size.let { WireSize.tag(317, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.fieldname1 != 0) {
        __result += (WireSize.tag(401, WireType.VARINT) + WireSize.int32(this.fieldname1))
    }

    if (this.fieldName2 != 0) {
        __result += (WireSize.tag(402, WireType.VARINT) + WireSize.int32(this.fieldName2))
    }

    if (this.FieldName3 != 0) {
        __result += (WireSize.tag(403, WireType.VARINT) + WireSize.int32(this.FieldName3))
    }

    if (this.field_Name4_ != 0) {
        __result += (WireSize.tag(404, WireType.VARINT) + WireSize.int32(this.field_Name4_))
    }

    if (this.field0name5 != 0) {
        __result += (WireSize.tag(405, WireType.VARINT) + WireSize.int32(this.field0name5))
    }

    if (this.field_0Name6 != 0) {
        __result += (WireSize.tag(406, WireType.VARINT) + WireSize.int32(this.field_0Name6))
    }

    if (this.fieldName7 != 0) {
        __result += (WireSize.tag(407, WireType.VARINT) + WireSize.int32(this.fieldName7))
    }

    if (this.FieldName8 != 0) {
        __result += (WireSize.tag(408, WireType.VARINT) + WireSize.int32(this.FieldName8))
    }

    if (this.field_Name9 != 0) {
        __result += (WireSize.tag(409, WireType.VARINT) + WireSize.int32(this.field_Name9))
    }

    if (this.Field_Name10 != 0) {
        __result += (WireSize.tag(410, WireType.VARINT) + WireSize.int32(this.Field_Name10))
    }

    if (this.FIELD_NAME11 != 0) {
        __result += (WireSize.tag(411, WireType.VARINT) + WireSize.int32(this.FIELD_NAME11))
    }

    if (this.FIELDName12 != 0) {
        __result += (WireSize.tag(412, WireType.VARINT) + WireSize.int32(this.FIELDName12))
    }

    if (this._FieldName13 != 0) {
        __result += (WireSize.tag(413, WireType.VARINT) + WireSize.int32(this._FieldName13))
    }

    if (this.__FieldName14 != 0) {
        __result += (WireSize.tag(414, WireType.VARINT) + WireSize.int32(this.__FieldName14))
    }

    if (this.field_Name15 != 0) {
        __result += (WireSize.tag(415, WireType.VARINT) + WireSize.int32(this.field_Name15))
    }

    if (this.field__Name16 != 0) {
        __result += (WireSize.tag(416, WireType.VARINT) + WireSize.int32(this.field__Name16))
    }

    if (this.fieldName17__ != 0) {
        __result += (WireSize.tag(417, WireType.VARINT) + WireSize.int32(this.fieldName17__))
    }

    if (this.FieldName18__ != 0) {
        __result += (WireSize.tag(418, WireType.VARINT) + WireSize.int32(this.FieldName18__))
    }

    this.oneofField?.also {
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
    if (this.c != 0) {
        encoder.writeInt32(fieldNr = 1, value = this.c)
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
    if (this.c != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.c))
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
        this.corecursive.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.NestedMessageInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.a != 0) {
        encoder.writeInt32(fieldNr = 1, value = this.a)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 2, value = this.corecursive.asInternal()) { encodeWith(it, config) }
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
    if (this.a != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.a))
    }

    if (presenceMask[0]) {
        __result += this.corecursive.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
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
    if (this.key != 0) {
        encoder.writeInt32(fieldNr = 1, value = this.key)
    }

    if (this.value != 0) {
        encoder.writeInt32(fieldNr = 2, value = this.value)
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
    if (this.key != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.key))
    }

    if (this.value != 0) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int32(this.value))
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
    if (this.key != 0L) {
        encoder.writeInt64(fieldNr = 1, value = this.key)
    }

    if (this.value != 0L) {
        encoder.writeInt64(fieldNr = 2, value = this.value)
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
    if (this.key != 0L) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int64(this.key))
    }

    if (this.value != 0L) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int64(this.value))
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
    if (this.key != 0u) {
        encoder.writeUInt32(fieldNr = 1, value = this.key)
    }

    if (this.value != 0u) {
        encoder.writeUInt32(fieldNr = 2, value = this.value)
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
    if (this.key != 0u) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt32(this.key))
    }

    if (this.value != 0u) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.uInt32(this.value))
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
    if (this.key != 0uL) {
        encoder.writeUInt64(fieldNr = 1, value = this.key)
    }

    if (this.value != 0uL) {
        encoder.writeUInt64(fieldNr = 2, value = this.value)
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
    if (this.key != 0uL) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt64(this.key))
    }

    if (this.value != 0uL) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.uInt64(this.value))
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
    if (this.key != 0) {
        encoder.writeSInt32(fieldNr = 1, value = this.key)
    }

    if (this.value != 0) {
        encoder.writeSInt32(fieldNr = 2, value = this.value)
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
    if (this.key != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.sInt32(this.key))
    }

    if (this.value != 0) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.sInt32(this.value))
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
    if (this.key != 0L) {
        encoder.writeSInt64(fieldNr = 1, value = this.key)
    }

    if (this.value != 0L) {
        encoder.writeSInt64(fieldNr = 2, value = this.value)
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
    if (this.key != 0L) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.sInt64(this.key))
    }

    if (this.value != 0L) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.sInt64(this.value))
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
    if (this.key != 0u) {
        encoder.writeFixed32(fieldNr = 1, value = this.key)
    }

    if (this.value != 0u) {
        encoder.writeFixed32(fieldNr = 2, value = this.value)
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
    if (this.key != 0u) {
        __result += (WireSize.tag(1, WireType.FIXED32) + WireSize.fixed32(this.key))
    }

    if (this.value != 0u) {
        __result += (WireSize.tag(2, WireType.FIXED32) + WireSize.fixed32(this.value))
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
    if (this.key != 0uL) {
        encoder.writeFixed64(fieldNr = 1, value = this.key)
    }

    if (this.value != 0uL) {
        encoder.writeFixed64(fieldNr = 2, value = this.value)
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
    if (this.key != 0uL) {
        __result += (WireSize.tag(1, WireType.FIXED64) + WireSize.fixed64(this.key))
    }

    if (this.value != 0uL) {
        __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.fixed64(this.value))
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
    if (this.key != 0) {
        encoder.writeSFixed32(fieldNr = 1, value = this.key)
    }

    if (this.value != 0) {
        encoder.writeSFixed32(fieldNr = 2, value = this.value)
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
    if (this.key != 0) {
        __result += (WireSize.tag(1, WireType.FIXED32) + WireSize.sFixed32(this.key))
    }

    if (this.value != 0) {
        __result += (WireSize.tag(2, WireType.FIXED32) + WireSize.sFixed32(this.value))
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
    if (this.key != 0L) {
        encoder.writeSFixed64(fieldNr = 1, value = this.key)
    }

    if (this.value != 0L) {
        encoder.writeSFixed64(fieldNr = 2, value = this.value)
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
    if (this.key != 0L) {
        __result += (WireSize.tag(1, WireType.FIXED64) + WireSize.sFixed64(this.key))
    }

    if (this.value != 0L) {
        __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.sFixed64(this.value))
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
    if (this.key != 0) {
        encoder.writeInt32(fieldNr = 1, value = this.key)
    }

    if (this.value != 0.0f) {
        encoder.writeFloat(fieldNr = 2, value = this.value)
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
    if (this.key != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.key))
    }

    if (this.value != 0.0f) {
        __result += (WireSize.tag(2, WireType.FIXED32) + WireSize.float(this.value))
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
    if (this.key != 0) {
        encoder.writeInt32(fieldNr = 1, value = this.key)
    }

    if (this.value != 0.0) {
        encoder.writeDouble(fieldNr = 2, value = this.value)
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
    if (this.key != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.key))
    }

    if (this.value != 0.0) {
        __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.double(this.value))
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
    if (this.key != false) {
        encoder.writeBool(fieldNr = 1, value = this.key)
    }

    if (this.value != false) {
        encoder.writeBool(fieldNr = 2, value = this.value)
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
    if (this.key != false) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.bool(this.key))
    }

    if (this.value != false) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.bool(this.value))
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
    if (this.key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (this.value.isNotEmpty()) {
        encoder.writeString(fieldNr = 2, value = this.value)
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
    if (this.key.isNotEmpty()) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.value.isNotEmpty()) {
        __result += WireSize.string(this.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
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
    if (this.key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (this.value.isNotEmpty()) {
        encoder.writeBytes(fieldNr = 2, value = this.value)
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
    if (this.key.isNotEmpty()) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.value.isNotEmpty()) {
        __result += WireSize.bytes(this.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
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
        this.value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 2, value = this.value.asInternal()) { encodeWith(it, config) }
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
    if (this.key.isNotEmpty()) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += this.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
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
        this.value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 2, value = this.value.asInternal()) { encodeWith(it, config) }
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
    if (this.key.isNotEmpty()) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += this.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
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
    if (this.key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (this.value != TestAllTypesProto3.NestedEnum.FOO) {
        encoder.writeEnum(fieldNr = 2, value = this.value.number)
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
    if (this.key.isNotEmpty()) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.value != TestAllTypesProto3.NestedEnum.FOO) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.enum(this.value.number))
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
    if (this.key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (this.value != ForeignEnum.FOREIGN_FOO) {
        encoder.writeEnum(fieldNr = 2, value = this.value.number)
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
    if (this.key.isNotEmpty()) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.value != ForeignEnum.FOREIGN_FOO) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.enum(this.value.number))
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
