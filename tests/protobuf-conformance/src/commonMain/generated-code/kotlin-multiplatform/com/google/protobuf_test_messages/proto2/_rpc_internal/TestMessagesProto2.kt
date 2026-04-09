@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.proto2

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.isNotEmpty
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.ExtensionValue
import kotlinx.rpc.protobuf.internal.InternalExtensionDescriptor
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.InternalPresenceObject
import kotlinx.rpc.protobuf.internal.KTag
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
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
import kotlinx.rpc.protobuf.internal.protoToString
import kotlinx.rpc.protobuf.internal.sFixed32
import kotlinx.rpc.protobuf.internal.sFixed64
import kotlinx.rpc.protobuf.internal.sInt32
import kotlinx.rpc.protobuf.internal.sInt64
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag
import kotlinx.rpc.protobuf.internal.uInt32
import kotlinx.rpc.protobuf.internal.uInt64

class TestAllTypesProto2Internal: TestAllTypesProto2.Builder, InternalMessage(fieldsWithPresence = 58) {
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
        val defaultBytes: ByteString = ByteString(0x6A, 0x6F, 0x73, 0x68, 0x75, 0x61)
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __optionalInt32Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.optionalInt32) { null }
    override var optionalInt32: Int? by __optionalInt32Delegate
    internal val __optionalInt64Delegate: MsgFieldDelegate<Long?> = MsgFieldDelegate(PresenceIndices.optionalInt64) { null }
    override var optionalInt64: Long? by __optionalInt64Delegate
    internal val __optionalUint32Delegate: MsgFieldDelegate<UInt?> = MsgFieldDelegate(PresenceIndices.optionalUint32) { null }
    override var optionalUint32: UInt? by __optionalUint32Delegate
    internal val __optionalUint64Delegate: MsgFieldDelegate<ULong?> = MsgFieldDelegate(PresenceIndices.optionalUint64) { null }
    override var optionalUint64: ULong? by __optionalUint64Delegate
    internal val __optionalSint32Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.optionalSint32) { null }
    override var optionalSint32: Int? by __optionalSint32Delegate
    internal val __optionalSint64Delegate: MsgFieldDelegate<Long?> = MsgFieldDelegate(PresenceIndices.optionalSint64) { null }
    override var optionalSint64: Long? by __optionalSint64Delegate
    internal val __optionalFixed32Delegate: MsgFieldDelegate<UInt?> = MsgFieldDelegate(PresenceIndices.optionalFixed32) { null }
    override var optionalFixed32: UInt? by __optionalFixed32Delegate
    internal val __optionalFixed64Delegate: MsgFieldDelegate<ULong?> = MsgFieldDelegate(PresenceIndices.optionalFixed64) { null }
    override var optionalFixed64: ULong? by __optionalFixed64Delegate
    internal val __optionalSfixed32Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.optionalSfixed32) { null }
    override var optionalSfixed32: Int? by __optionalSfixed32Delegate
    internal val __optionalSfixed64Delegate: MsgFieldDelegate<Long?> = MsgFieldDelegate(PresenceIndices.optionalSfixed64) { null }
    override var optionalSfixed64: Long? by __optionalSfixed64Delegate
    internal val __optionalFloatDelegate: MsgFieldDelegate<Float?> = MsgFieldDelegate(PresenceIndices.optionalFloat) { null }
    override var optionalFloat: Float? by __optionalFloatDelegate
    internal val __optionalDoubleDelegate: MsgFieldDelegate<Double?> = MsgFieldDelegate(PresenceIndices.optionalDouble) { null }
    override var optionalDouble: Double? by __optionalDoubleDelegate
    internal val __optionalBoolDelegate: MsgFieldDelegate<Boolean?> = MsgFieldDelegate(PresenceIndices.optionalBool) { null }
    override var optionalBool: Boolean? by __optionalBoolDelegate
    internal val __optionalStringDelegate: MsgFieldDelegate<String?> = MsgFieldDelegate(PresenceIndices.optionalString) { null }
    override var optionalString: String? by __optionalStringDelegate
    internal val __optionalBytesDelegate: MsgFieldDelegate<ByteString?> = MsgFieldDelegate(PresenceIndices.optionalBytes) { null }
    override var optionalBytes: ByteString? by __optionalBytesDelegate
    internal val __optionalNestedMessageDelegate: MsgFieldDelegate<TestAllTypesProto2.NestedMessage> = MsgFieldDelegate(PresenceIndices.optionalNestedMessage) { NestedMessageInternal.DEFAULT }
    override var optionalNestedMessage: TestAllTypesProto2.NestedMessage by __optionalNestedMessageDelegate
    internal val __optionalForeignMessageDelegate: MsgFieldDelegate<ForeignMessageProto2> = MsgFieldDelegate(PresenceIndices.optionalForeignMessage) { ForeignMessageProto2Internal.DEFAULT }
    override var optionalForeignMessage: ForeignMessageProto2 by __optionalForeignMessageDelegate
    internal val __optionalNestedEnumDelegate: MsgFieldDelegate<TestAllTypesProto2.NestedEnum?> = MsgFieldDelegate(PresenceIndices.optionalNestedEnum) { null }
    override var optionalNestedEnum: TestAllTypesProto2.NestedEnum? by __optionalNestedEnumDelegate
    internal val __optionalForeignEnumDelegate: MsgFieldDelegate<ForeignEnumProto2?> = MsgFieldDelegate(PresenceIndices.optionalForeignEnum) { null }
    override var optionalForeignEnum: ForeignEnumProto2? by __optionalForeignEnumDelegate
    internal val __optionalStringPieceDelegate: MsgFieldDelegate<String?> = MsgFieldDelegate(PresenceIndices.optionalStringPiece) { null }
    override var optionalStringPiece: String? by __optionalStringPieceDelegate
    internal val __optionalCordDelegate: MsgFieldDelegate<String?> = MsgFieldDelegate(PresenceIndices.optionalCord) { null }
    override var optionalCord: String? by __optionalCordDelegate
    internal val __recursiveMessageDelegate: MsgFieldDelegate<TestAllTypesProto2> = MsgFieldDelegate(PresenceIndices.recursiveMessage) { TestAllTypesProto2Internal.DEFAULT }
    override var recursiveMessage: TestAllTypesProto2 by __recursiveMessageDelegate
    internal val __repeatedInt32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var repeatedInt32: List<Int> by __repeatedInt32Delegate
    internal val __repeatedInt64Delegate: MsgFieldDelegate<List<Long>> = MsgFieldDelegate { emptyList() }
    override var repeatedInt64: List<Long> by __repeatedInt64Delegate
    internal val __repeatedUint32Delegate: MsgFieldDelegate<List<UInt>> = MsgFieldDelegate { emptyList() }
    override var repeatedUint32: List<UInt> by __repeatedUint32Delegate
    internal val __repeatedUint64Delegate: MsgFieldDelegate<List<ULong>> = MsgFieldDelegate { emptyList() }
    override var repeatedUint64: List<ULong> by __repeatedUint64Delegate
    internal val __repeatedSint32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var repeatedSint32: List<Int> by __repeatedSint32Delegate
    internal val __repeatedSint64Delegate: MsgFieldDelegate<List<Long>> = MsgFieldDelegate { emptyList() }
    override var repeatedSint64: List<Long> by __repeatedSint64Delegate
    internal val __repeatedFixed32Delegate: MsgFieldDelegate<List<UInt>> = MsgFieldDelegate { emptyList() }
    override var repeatedFixed32: List<UInt> by __repeatedFixed32Delegate
    internal val __repeatedFixed64Delegate: MsgFieldDelegate<List<ULong>> = MsgFieldDelegate { emptyList() }
    override var repeatedFixed64: List<ULong> by __repeatedFixed64Delegate
    internal val __repeatedSfixed32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var repeatedSfixed32: List<Int> by __repeatedSfixed32Delegate
    internal val __repeatedSfixed64Delegate: MsgFieldDelegate<List<Long>> = MsgFieldDelegate { emptyList() }
    override var repeatedSfixed64: List<Long> by __repeatedSfixed64Delegate
    internal val __repeatedFloatDelegate: MsgFieldDelegate<List<Float>> = MsgFieldDelegate { emptyList() }
    override var repeatedFloat: List<Float> by __repeatedFloatDelegate
    internal val __repeatedDoubleDelegate: MsgFieldDelegate<List<Double>> = MsgFieldDelegate { emptyList() }
    override var repeatedDouble: List<Double> by __repeatedDoubleDelegate
    internal val __repeatedBoolDelegate: MsgFieldDelegate<List<Boolean>> = MsgFieldDelegate { emptyList() }
    override var repeatedBool: List<Boolean> by __repeatedBoolDelegate
    internal val __repeatedStringDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    override var repeatedString: List<String> by __repeatedStringDelegate
    internal val __repeatedBytesDelegate: MsgFieldDelegate<List<ByteString>> = MsgFieldDelegate { emptyList() }
    override var repeatedBytes: List<ByteString> by __repeatedBytesDelegate
    internal val __repeatedNestedMessageDelegate: MsgFieldDelegate<List<TestAllTypesProto2.NestedMessage>> = MsgFieldDelegate { emptyList() }
    override var repeatedNestedMessage: List<TestAllTypesProto2.NestedMessage> by __repeatedNestedMessageDelegate
    internal val __repeatedForeignMessageDelegate: MsgFieldDelegate<List<ForeignMessageProto2>> = MsgFieldDelegate { emptyList() }
    override var repeatedForeignMessage: List<ForeignMessageProto2> by __repeatedForeignMessageDelegate
    internal val __repeatedNestedEnumDelegate: MsgFieldDelegate<List<TestAllTypesProto2.NestedEnum>> = MsgFieldDelegate { emptyList() }
    override var repeatedNestedEnum: List<TestAllTypesProto2.NestedEnum> by __repeatedNestedEnumDelegate
    internal val __repeatedForeignEnumDelegate: MsgFieldDelegate<List<ForeignEnumProto2>> = MsgFieldDelegate { emptyList() }
    override var repeatedForeignEnum: List<ForeignEnumProto2> by __repeatedForeignEnumDelegate
    internal val __repeatedStringPieceDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    override var repeatedStringPiece: List<String> by __repeatedStringPieceDelegate
    internal val __repeatedCordDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    override var repeatedCord: List<String> by __repeatedCordDelegate
    internal val __packedInt32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var packedInt32: List<Int> by __packedInt32Delegate
    internal val __packedInt64Delegate: MsgFieldDelegate<List<Long>> = MsgFieldDelegate { emptyList() }
    override var packedInt64: List<Long> by __packedInt64Delegate
    internal val __packedUint32Delegate: MsgFieldDelegate<List<UInt>> = MsgFieldDelegate { emptyList() }
    override var packedUint32: List<UInt> by __packedUint32Delegate
    internal val __packedUint64Delegate: MsgFieldDelegate<List<ULong>> = MsgFieldDelegate { emptyList() }
    override var packedUint64: List<ULong> by __packedUint64Delegate
    internal val __packedSint32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var packedSint32: List<Int> by __packedSint32Delegate
    internal val __packedSint64Delegate: MsgFieldDelegate<List<Long>> = MsgFieldDelegate { emptyList() }
    override var packedSint64: List<Long> by __packedSint64Delegate
    internal val __packedFixed32Delegate: MsgFieldDelegate<List<UInt>> = MsgFieldDelegate { emptyList() }
    override var packedFixed32: List<UInt> by __packedFixed32Delegate
    internal val __packedFixed64Delegate: MsgFieldDelegate<List<ULong>> = MsgFieldDelegate { emptyList() }
    override var packedFixed64: List<ULong> by __packedFixed64Delegate
    internal val __packedSfixed32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var packedSfixed32: List<Int> by __packedSfixed32Delegate
    internal val __packedSfixed64Delegate: MsgFieldDelegate<List<Long>> = MsgFieldDelegate { emptyList() }
    override var packedSfixed64: List<Long> by __packedSfixed64Delegate
    internal val __packedFloatDelegate: MsgFieldDelegate<List<Float>> = MsgFieldDelegate { emptyList() }
    override var packedFloat: List<Float> by __packedFloatDelegate
    internal val __packedDoubleDelegate: MsgFieldDelegate<List<Double>> = MsgFieldDelegate { emptyList() }
    override var packedDouble: List<Double> by __packedDoubleDelegate
    internal val __packedBoolDelegate: MsgFieldDelegate<List<Boolean>> = MsgFieldDelegate { emptyList() }
    override var packedBool: List<Boolean> by __packedBoolDelegate
    internal val __packedNestedEnumDelegate: MsgFieldDelegate<List<TestAllTypesProto2.NestedEnum>> = MsgFieldDelegate { emptyList() }
    override var packedNestedEnum: List<TestAllTypesProto2.NestedEnum> by __packedNestedEnumDelegate
    internal val __unpackedInt32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var unpackedInt32: List<Int> by __unpackedInt32Delegate
    internal val __unpackedInt64Delegate: MsgFieldDelegate<List<Long>> = MsgFieldDelegate { emptyList() }
    override var unpackedInt64: List<Long> by __unpackedInt64Delegate
    internal val __unpackedUint32Delegate: MsgFieldDelegate<List<UInt>> = MsgFieldDelegate { emptyList() }
    override var unpackedUint32: List<UInt> by __unpackedUint32Delegate
    internal val __unpackedUint64Delegate: MsgFieldDelegate<List<ULong>> = MsgFieldDelegate { emptyList() }
    override var unpackedUint64: List<ULong> by __unpackedUint64Delegate
    internal val __unpackedSint32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var unpackedSint32: List<Int> by __unpackedSint32Delegate
    internal val __unpackedSint64Delegate: MsgFieldDelegate<List<Long>> = MsgFieldDelegate { emptyList() }
    override var unpackedSint64: List<Long> by __unpackedSint64Delegate
    internal val __unpackedFixed32Delegate: MsgFieldDelegate<List<UInt>> = MsgFieldDelegate { emptyList() }
    override var unpackedFixed32: List<UInt> by __unpackedFixed32Delegate
    internal val __unpackedFixed64Delegate: MsgFieldDelegate<List<ULong>> = MsgFieldDelegate { emptyList() }
    override var unpackedFixed64: List<ULong> by __unpackedFixed64Delegate
    internal val __unpackedSfixed32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var unpackedSfixed32: List<Int> by __unpackedSfixed32Delegate
    internal val __unpackedSfixed64Delegate: MsgFieldDelegate<List<Long>> = MsgFieldDelegate { emptyList() }
    override var unpackedSfixed64: List<Long> by __unpackedSfixed64Delegate
    internal val __unpackedFloatDelegate: MsgFieldDelegate<List<Float>> = MsgFieldDelegate { emptyList() }
    override var unpackedFloat: List<Float> by __unpackedFloatDelegate
    internal val __unpackedDoubleDelegate: MsgFieldDelegate<List<Double>> = MsgFieldDelegate { emptyList() }
    override var unpackedDouble: List<Double> by __unpackedDoubleDelegate
    internal val __unpackedBoolDelegate: MsgFieldDelegate<List<Boolean>> = MsgFieldDelegate { emptyList() }
    override var unpackedBool: List<Boolean> by __unpackedBoolDelegate
    internal val __unpackedNestedEnumDelegate: MsgFieldDelegate<List<TestAllTypesProto2.NestedEnum>> = MsgFieldDelegate { emptyList() }
    override var unpackedNestedEnum: List<TestAllTypesProto2.NestedEnum> by __unpackedNestedEnumDelegate
    internal val __mapInt32Int32Delegate: MsgFieldDelegate<Map<Int, Int>> = MsgFieldDelegate { emptyMap() }
    override var mapInt32Int32: Map<Int, Int> by __mapInt32Int32Delegate
    internal val __mapInt64Int64Delegate: MsgFieldDelegate<Map<Long, Long>> = MsgFieldDelegate { emptyMap() }
    override var mapInt64Int64: Map<Long, Long> by __mapInt64Int64Delegate
    internal val __mapUint32Uint32Delegate: MsgFieldDelegate<Map<UInt, UInt>> = MsgFieldDelegate { emptyMap() }
    override var mapUint32Uint32: Map<UInt, UInt> by __mapUint32Uint32Delegate
    internal val __mapUint64Uint64Delegate: MsgFieldDelegate<Map<ULong, ULong>> = MsgFieldDelegate { emptyMap() }
    override var mapUint64Uint64: Map<ULong, ULong> by __mapUint64Uint64Delegate
    internal val __mapSint32Sint32Delegate: MsgFieldDelegate<Map<Int, Int>> = MsgFieldDelegate { emptyMap() }
    override var mapSint32Sint32: Map<Int, Int> by __mapSint32Sint32Delegate
    internal val __mapSint64Sint64Delegate: MsgFieldDelegate<Map<Long, Long>> = MsgFieldDelegate { emptyMap() }
    override var mapSint64Sint64: Map<Long, Long> by __mapSint64Sint64Delegate
    internal val __mapFixed32Fixed32Delegate: MsgFieldDelegate<Map<UInt, UInt>> = MsgFieldDelegate { emptyMap() }
    override var mapFixed32Fixed32: Map<UInt, UInt> by __mapFixed32Fixed32Delegate
    internal val __mapFixed64Fixed64Delegate: MsgFieldDelegate<Map<ULong, ULong>> = MsgFieldDelegate { emptyMap() }
    override var mapFixed64Fixed64: Map<ULong, ULong> by __mapFixed64Fixed64Delegate
    internal val __mapSfixed32Sfixed32Delegate: MsgFieldDelegate<Map<Int, Int>> = MsgFieldDelegate { emptyMap() }
    override var mapSfixed32Sfixed32: Map<Int, Int> by __mapSfixed32Sfixed32Delegate
    internal val __mapSfixed64Sfixed64Delegate: MsgFieldDelegate<Map<Long, Long>> = MsgFieldDelegate { emptyMap() }
    override var mapSfixed64Sfixed64: Map<Long, Long> by __mapSfixed64Sfixed64Delegate
    internal val __mapInt32BoolDelegate: MsgFieldDelegate<Map<Int, Boolean>> = MsgFieldDelegate { emptyMap() }
    override var mapInt32Bool: Map<Int, Boolean> by __mapInt32BoolDelegate
    internal val __mapInt32FloatDelegate: MsgFieldDelegate<Map<Int, Float>> = MsgFieldDelegate { emptyMap() }
    override var mapInt32Float: Map<Int, Float> by __mapInt32FloatDelegate
    internal val __mapInt32DoubleDelegate: MsgFieldDelegate<Map<Int, Double>> = MsgFieldDelegate { emptyMap() }
    override var mapInt32Double: Map<Int, Double> by __mapInt32DoubleDelegate
    internal val __mapInt32NestedMessageDelegate: MsgFieldDelegate<Map<Int, TestAllTypesProto2.NestedMessage>> = MsgFieldDelegate { emptyMap() }
    override var mapInt32NestedMessage: Map<Int, TestAllTypesProto2.NestedMessage> by __mapInt32NestedMessageDelegate
    internal val __mapBoolBoolDelegate: MsgFieldDelegate<Map<Boolean, Boolean>> = MsgFieldDelegate { emptyMap() }
    override var mapBoolBool: Map<Boolean, Boolean> by __mapBoolBoolDelegate
    internal val __mapStringStringDelegate: MsgFieldDelegate<Map<String, String>> = MsgFieldDelegate { emptyMap() }
    override var mapStringString: Map<String, String> by __mapStringStringDelegate
    internal val __mapStringBytesDelegate: MsgFieldDelegate<Map<String, ByteString>> = MsgFieldDelegate { emptyMap() }
    override var mapStringBytes: Map<String, ByteString> by __mapStringBytesDelegate
    internal val __mapStringNestedMessageDelegate: MsgFieldDelegate<Map<String, TestAllTypesProto2.NestedMessage>> = MsgFieldDelegate { emptyMap() }
    override var mapStringNestedMessage: Map<String, TestAllTypesProto2.NestedMessage> by __mapStringNestedMessageDelegate
    internal val __mapStringForeignMessageDelegate: MsgFieldDelegate<Map<String, ForeignMessageProto2>> = MsgFieldDelegate { emptyMap() }
    override var mapStringForeignMessage: Map<String, ForeignMessageProto2> by __mapStringForeignMessageDelegate
    internal val __mapStringNestedEnumDelegate: MsgFieldDelegate<Map<String, TestAllTypesProto2.NestedEnum>> = MsgFieldDelegate { emptyMap() }
    override var mapStringNestedEnum: Map<String, TestAllTypesProto2.NestedEnum> by __mapStringNestedEnumDelegate
    internal val __mapStringForeignEnumDelegate: MsgFieldDelegate<Map<String, ForeignEnumProto2>> = MsgFieldDelegate { emptyMap() }
    override var mapStringForeignEnum: Map<String, ForeignEnumProto2> by __mapStringForeignEnumDelegate
    internal val __dataDelegate: MsgFieldDelegate<TestAllTypesProto2.Data> = MsgFieldDelegate(PresenceIndices.data) { DataInternal.DEFAULT }
    override var data: TestAllTypesProto2.Data by __dataDelegate
    internal val __multiwordgroupfieldDelegate: MsgFieldDelegate<TestAllTypesProto2.MultiWordGroupField> = MsgFieldDelegate(PresenceIndices.multiwordgroupfield) { MultiWordGroupFieldInternal.DEFAULT }
    override var multiwordgroupfield: TestAllTypesProto2.MultiWordGroupField by __multiwordgroupfieldDelegate
    internal val __defaultInt32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.defaultInt32) { -123456789 }
    override var defaultInt32: Int by __defaultInt32Delegate
    internal val __defaultInt64Delegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.defaultInt64) { -9123456789123456789L }
    override var defaultInt64: Long by __defaultInt64Delegate
    internal val __defaultUint32Delegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.defaultUint32) { 2123456789u }
    override var defaultUint32: UInt by __defaultUint32Delegate
    internal val __defaultUint64Delegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.defaultUint64) { 10123456789123456789uL }
    override var defaultUint64: ULong by __defaultUint64Delegate
    internal val __defaultSint32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.defaultSint32) { -123456789 }
    override var defaultSint32: Int by __defaultSint32Delegate
    internal val __defaultSint64Delegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.defaultSint64) { -9123456789123456789L }
    override var defaultSint64: Long by __defaultSint64Delegate
    internal val __defaultFixed32Delegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.defaultFixed32) { 2123456789u }
    override var defaultFixed32: UInt by __defaultFixed32Delegate
    internal val __defaultFixed64Delegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.defaultFixed64) { 10123456789123456789uL }
    override var defaultFixed64: ULong by __defaultFixed64Delegate
    internal val __defaultSfixed32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.defaultSfixed32) { -123456789 }
    override var defaultSfixed32: Int by __defaultSfixed32Delegate
    internal val __defaultSfixed64Delegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.defaultSfixed64) { -9123456789123456789L }
    override var defaultSfixed64: Long by __defaultSfixed64Delegate
    internal val __defaultFloatDelegate: MsgFieldDelegate<Float> = MsgFieldDelegate(PresenceIndices.defaultFloat) { Float.fromBits(0x50061C46.toInt()) }
    override var defaultFloat: Float by __defaultFloatDelegate
    internal val __defaultDoubleDelegate: MsgFieldDelegate<Double> = MsgFieldDelegate(PresenceIndices.defaultDouble) { Double.fromBits(0x44ADA56A4B0835C0L) }
    override var defaultDouble: Double by __defaultDoubleDelegate
    internal val __defaultBoolDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.defaultBool) { true }
    override var defaultBool: Boolean by __defaultBoolDelegate
    internal val __defaultStringDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.defaultString) { "Rosebud" }
    override var defaultString: String by __defaultStringDelegate
    internal val __defaultBytesDelegate: MsgFieldDelegate<ByteString> = MsgFieldDelegate(PresenceIndices.defaultBytes) { BytesDefaults.defaultBytes }
    override var defaultBytes: ByteString by __defaultBytesDelegate
    internal val __fieldname1Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.fieldname1) { null }
    override var fieldname1: Int? by __fieldname1Delegate
    internal val __fieldName2Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.fieldName2) { null }
    override var fieldName2: Int? by __fieldName2Delegate
    internal val __FieldName3Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.FieldName3) { null }
    override var FieldName3: Int? by __FieldName3Delegate
    internal val __field_Name4_Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.field_Name4_) { null }
    override var field_Name4_: Int? by __field_Name4_Delegate
    internal val __field0name5Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.field0name5) { null }
    override var field0name5: Int? by __field0name5Delegate
    internal val __field_0Name6Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.field_0Name6) { null }
    override var field_0Name6: Int? by __field_0Name6Delegate
    internal val __fieldName7Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.fieldName7) { null }
    override var fieldName7: Int? by __fieldName7Delegate
    internal val __FieldName8Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.FieldName8) { null }
    override var FieldName8: Int? by __FieldName8Delegate
    internal val __field_Name9Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.field_Name9) { null }
    override var field_Name9: Int? by __field_Name9Delegate
    internal val __Field_Name10Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.Field_Name10) { null }
    override var Field_Name10: Int? by __Field_Name10Delegate
    internal val __FIELD_NAME11Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.FIELD_NAME11) { null }
    override var FIELD_NAME11: Int? by __FIELD_NAME11Delegate
    internal val __FIELDName12Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.FIELDName12) { null }
    override var FIELDName12: Int? by __FIELDName12Delegate
    internal val ___FieldName13Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices._FieldName13) { null }
    override var _FieldName13: Int? by ___FieldName13Delegate
    internal val ____FieldName14Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.__FieldName14) { null }
    override var __FieldName14: Int? by ____FieldName14Delegate
    internal val __field_Name15Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.field_Name15) { null }
    override var field_Name15: Int? by __field_Name15Delegate
    internal val __field__Name16Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.field__Name16) { null }
    override var field__Name16: Int? by __field__Name16Delegate
    internal val __fieldName17__Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.fieldName17__) { null }
    override var fieldName17__: Int? by __fieldName17__Delegate
    internal val __FieldName18__Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.FieldName18__) { null }
    override var FieldName18__: Int? by __FieldName18__Delegate
    internal val __messageSetCorrectDelegate: MsgFieldDelegate<TestAllTypesProto2.MessageSetCorrect> = MsgFieldDelegate(PresenceIndices.messageSetCorrect) { MessageSetCorrectInternal.DEFAULT }
    override var messageSetCorrect: TestAllTypesProto2.MessageSetCorrect by __messageSetCorrectDelegate
    override var oneofField: TestAllTypesProto2.OneofField? = null

    private val _owner: TestAllTypesProto2Internal = this

    @InternalRpcApi
    val _presence: TestAllTypesProto2Presence = object : TestAllTypesProto2Presence, InternalPresenceObject {
        override val _message: TestAllTypesProto2Internal get() = _owner

        override val hasOptionalInt32: Boolean get() = presenceMask[0]

        override val hasOptionalInt64: Boolean get() = presenceMask[1]

        override val hasOptionalUint32: Boolean get() = presenceMask[2]

        override val hasOptionalUint64: Boolean get() = presenceMask[3]

        override val hasOptionalSint32: Boolean get() = presenceMask[4]

        override val hasOptionalSint64: Boolean get() = presenceMask[5]

        override val hasOptionalFixed32: Boolean get() = presenceMask[6]

        override val hasOptionalFixed64: Boolean get() = presenceMask[7]

        override val hasOptionalSfixed32: Boolean get() = presenceMask[8]

        override val hasOptionalSfixed64: Boolean get() = presenceMask[9]

        override val hasOptionalFloat: Boolean get() = presenceMask[10]

        override val hasOptionalDouble: Boolean get() = presenceMask[11]

        override val hasOptionalBool: Boolean get() = presenceMask[12]

        override val hasOptionalString: Boolean get() = presenceMask[13]

        override val hasOptionalBytes: Boolean get() = presenceMask[14]

        override val hasOptionalNestedMessage: Boolean get() = presenceMask[15]

        override val hasOptionalForeignMessage: Boolean get() = presenceMask[16]

        override val hasOptionalNestedEnum: Boolean get() = presenceMask[17]

        override val hasOptionalForeignEnum: Boolean get() = presenceMask[18]

        override val hasOptionalStringPiece: Boolean get() = presenceMask[19]

        override val hasOptionalCord: Boolean get() = presenceMask[20]

        override val hasRecursiveMessage: Boolean get() = presenceMask[21]

        override val hasData: Boolean get() = presenceMask[22]

        override val hasMultiwordgroupfield: Boolean get() = presenceMask[23]

        override val hasDefaultInt32: Boolean get() = presenceMask[24]

        override val hasDefaultInt64: Boolean get() = presenceMask[25]

        override val hasDefaultUint32: Boolean get() = presenceMask[26]

        override val hasDefaultUint64: Boolean get() = presenceMask[27]

        override val hasDefaultSint32: Boolean get() = presenceMask[28]

        override val hasDefaultSint64: Boolean get() = presenceMask[29]

        override val hasDefaultFixed32: Boolean get() = presenceMask[30]

        override val hasDefaultFixed64: Boolean get() = presenceMask[31]

        override val hasDefaultSfixed32: Boolean get() = presenceMask[32]

        override val hasDefaultSfixed64: Boolean get() = presenceMask[33]

        override val hasDefaultFloat: Boolean get() = presenceMask[34]

        override val hasDefaultDouble: Boolean get() = presenceMask[35]

        override val hasDefaultBool: Boolean get() = presenceMask[36]

        override val hasDefaultString: Boolean get() = presenceMask[37]

        override val hasDefaultBytes: Boolean get() = presenceMask[38]

        override val hasFieldname1: Boolean get() = presenceMask[39]

        override val hasFieldName2: Boolean get() = presenceMask[40]

        override val hasFieldName3: Boolean get() = presenceMask[41]

        override val hasField_Name4_: Boolean get() = presenceMask[42]

        override val hasField0name5: Boolean get() = presenceMask[43]

        override val hasField_0Name6: Boolean get() = presenceMask[44]

        override val hasFieldName7: Boolean get() = presenceMask[45]

        override val hasFieldName8: Boolean get() = presenceMask[46]

        override val hasField_Name9: Boolean get() = presenceMask[47]

        override val hasField_Name10: Boolean get() = presenceMask[48]

        override val hasFIELD_NAME11: Boolean get() = presenceMask[49]

        override val hasFIELDName12: Boolean get() = presenceMask[50]

        override val has_FieldName13: Boolean get() = presenceMask[51]

        override val has__FieldName14: Boolean get() = presenceMask[52]

        override val hasField_Name15: Boolean get() = presenceMask[53]

        override val hasField__Name16: Boolean get() = presenceMask[54]

        override val hasFieldName17__: Boolean get() = presenceMask[55]

        override val hasFieldName18__: Boolean get() = presenceMask[56]

        override val hasMessageSetCorrect: Boolean get() = presenceMask[57]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) (this.optionalInt32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[1]) (this.optionalInt64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[2]) (this.optionalUint32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[3]) (this.optionalUint64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[4]) (this.optionalSint32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[5]) (this.optionalSint64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[6]) (this.optionalFixed32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[7]) (this.optionalFixed64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[8]) (this.optionalSfixed32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[9]) (this.optionalSfixed64?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[10]) (this.optionalFloat?.toBits()?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[11]) (this.optionalDouble?.toBits()?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[12]) (this.optionalBool?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[13]) (this.optionalString?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[14]) (this.optionalBytes?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[15]) this.optionalNestedMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[16]) this.optionalForeignMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[17]) (this.optionalNestedEnum?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[18]) (this.optionalForeignEnum?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[19]) (this.optionalStringPiece?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[20]) (this.optionalCord?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[21]) this.recursiveMessage.hashCode() else 0
        result = 31 * result + this.repeatedInt32.hashCode()
        result = 31 * result + this.repeatedInt64.hashCode()
        result = 31 * result + this.repeatedUint32.hashCode()
        result = 31 * result + this.repeatedUint64.hashCode()
        result = 31 * result + this.repeatedSint32.hashCode()
        result = 31 * result + this.repeatedSint64.hashCode()
        result = 31 * result + this.repeatedFixed32.hashCode()
        result = 31 * result + this.repeatedFixed64.hashCode()
        result = 31 * result + this.repeatedSfixed32.hashCode()
        result = 31 * result + this.repeatedSfixed64.hashCode()
        result = 31 * result + this.repeatedFloat.hashCode()
        result = 31 * result + this.repeatedDouble.hashCode()
        result = 31 * result + this.repeatedBool.hashCode()
        result = 31 * result + this.repeatedString.hashCode()
        result = 31 * result + this.repeatedBytes.hashCode()
        result = 31 * result + this.repeatedNestedMessage.hashCode()
        result = 31 * result + this.repeatedForeignMessage.hashCode()
        result = 31 * result + this.repeatedNestedEnum.hashCode()
        result = 31 * result + this.repeatedForeignEnum.hashCode()
        result = 31 * result + this.repeatedStringPiece.hashCode()
        result = 31 * result + this.repeatedCord.hashCode()
        result = 31 * result + this.packedInt32.hashCode()
        result = 31 * result + this.packedInt64.hashCode()
        result = 31 * result + this.packedUint32.hashCode()
        result = 31 * result + this.packedUint64.hashCode()
        result = 31 * result + this.packedSint32.hashCode()
        result = 31 * result + this.packedSint64.hashCode()
        result = 31 * result + this.packedFixed32.hashCode()
        result = 31 * result + this.packedFixed64.hashCode()
        result = 31 * result + this.packedSfixed32.hashCode()
        result = 31 * result + this.packedSfixed64.hashCode()
        result = 31 * result + this.packedFloat.hashCode()
        result = 31 * result + this.packedDouble.hashCode()
        result = 31 * result + this.packedBool.hashCode()
        result = 31 * result + this.packedNestedEnum.hashCode()
        result = 31 * result + this.unpackedInt32.hashCode()
        result = 31 * result + this.unpackedInt64.hashCode()
        result = 31 * result + this.unpackedUint32.hashCode()
        result = 31 * result + this.unpackedUint64.hashCode()
        result = 31 * result + this.unpackedSint32.hashCode()
        result = 31 * result + this.unpackedSint64.hashCode()
        result = 31 * result + this.unpackedFixed32.hashCode()
        result = 31 * result + this.unpackedFixed64.hashCode()
        result = 31 * result + this.unpackedSfixed32.hashCode()
        result = 31 * result + this.unpackedSfixed64.hashCode()
        result = 31 * result + this.unpackedFloat.hashCode()
        result = 31 * result + this.unpackedDouble.hashCode()
        result = 31 * result + this.unpackedBool.hashCode()
        result = 31 * result + this.unpackedNestedEnum.hashCode()
        result = 31 * result + this.mapInt32Int32.hashCode()
        result = 31 * result + this.mapInt64Int64.hashCode()
        result = 31 * result + this.mapUint32Uint32.hashCode()
        result = 31 * result + this.mapUint64Uint64.hashCode()
        result = 31 * result + this.mapSint32Sint32.hashCode()
        result = 31 * result + this.mapSint64Sint64.hashCode()
        result = 31 * result + this.mapFixed32Fixed32.hashCode()
        result = 31 * result + this.mapFixed64Fixed64.hashCode()
        result = 31 * result + this.mapSfixed32Sfixed32.hashCode()
        result = 31 * result + this.mapSfixed64Sfixed64.hashCode()
        result = 31 * result + this.mapInt32Bool.hashCode()
        result = 31 * result + this.mapInt32Float.hashCode()
        result = 31 * result + this.mapInt32Double.hashCode()
        result = 31 * result + this.mapInt32NestedMessage.hashCode()
        result = 31 * result + this.mapBoolBool.hashCode()
        result = 31 * result + this.mapStringString.hashCode()
        result = 31 * result + this.mapStringBytes.hashCode()
        result = 31 * result + this.mapStringNestedMessage.hashCode()
        result = 31 * result + this.mapStringForeignMessage.hashCode()
        result = 31 * result + this.mapStringNestedEnum.hashCode()
        result = 31 * result + this.mapStringForeignEnum.hashCode()
        result = 31 * result + if (presenceMask[22]) this.data.hashCode() else 0
        result = 31 * result + if (presenceMask[23]) this.multiwordgroupfield.hashCode() else 0
        result = 31 * result + if (presenceMask[24]) this.defaultInt32.hashCode() else 0
        result = 31 * result + if (presenceMask[25]) this.defaultInt64.hashCode() else 0
        result = 31 * result + if (presenceMask[26]) this.defaultUint32.hashCode() else 0
        result = 31 * result + if (presenceMask[27]) this.defaultUint64.hashCode() else 0
        result = 31 * result + if (presenceMask[28]) this.defaultSint32.hashCode() else 0
        result = 31 * result + if (presenceMask[29]) this.defaultSint64.hashCode() else 0
        result = 31 * result + if (presenceMask[30]) this.defaultFixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[31]) this.defaultFixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[32]) this.defaultSfixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[33]) this.defaultSfixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[34]) this.defaultFloat.toBits().hashCode() else 0
        result = 31 * result + if (presenceMask[35]) this.defaultDouble.toBits().hashCode() else 0
        result = 31 * result + if (presenceMask[36]) this.defaultBool.hashCode() else 0
        result = 31 * result + if (presenceMask[37]) this.defaultString.hashCode() else 0
        result = 31 * result + if (presenceMask[38]) this.defaultBytes.hashCode() else 0
        result = 31 * result + if (presenceMask[39]) (this.fieldname1?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[40]) (this.fieldName2?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[41]) (this.FieldName3?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[42]) (this.field_Name4_?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[43]) (this.field0name5?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[44]) (this.field_0Name6?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[45]) (this.fieldName7?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[46]) (this.FieldName8?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[47]) (this.field_Name9?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[48]) (this.Field_Name10?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[49]) (this.FIELD_NAME11?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[50]) (this.FIELDName12?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[51]) (this._FieldName13?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[52]) (this.__FieldName14?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[53]) (this.field_Name15?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[54]) (this.field__Name16?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[55]) (this.fieldName17__?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[56]) (this.FieldName18__?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[57]) this.messageSetCorrect.hashCode() else 0
        result = 31 * result + (this.oneofField?.oneOfHashCode() ?: 0)
        result = 31 * result + extensionsHashCode()
        return result
    }

    fun TestAllTypesProto2.OneofField.oneOfHashCode(): Int {
        return when (this) {
            is TestAllTypesProto2.OneofField.OneofUint32 -> hashCode() + 0
            is TestAllTypesProto2.OneofField.OneofNestedMessage -> hashCode() + 1
            is TestAllTypesProto2.OneofField.OneofString -> hashCode() + 2
            is TestAllTypesProto2.OneofField.OneofBytes -> hashCode() + 3
            is TestAllTypesProto2.OneofField.OneofBool -> hashCode() + 4
            is TestAllTypesProto2.OneofField.OneofUint64 -> hashCode() + 5
            is TestAllTypesProto2.OneofField.OneofFloat -> value.toBits().hashCode() + 6
            is TestAllTypesProto2.OneofField.OneofDouble -> value.toBits().hashCode() + 7
            is TestAllTypesProto2.OneofField.OneofEnum -> hashCode() + 8
        }
    }

    fun oneOfEquals(a: TestAllTypesProto2.OneofField?, b: TestAllTypesProto2.OneofField?): Boolean {
        if (a === b) return true
        if (a == null || b == null) return false
        if (a::class != b::class) return false
        return when (a) {
            is TestAllTypesProto2.OneofField.OneofUint32 -> a == b
            is TestAllTypesProto2.OneofField.OneofNestedMessage -> a == b
            is TestAllTypesProto2.OneofField.OneofString -> a == b
            is TestAllTypesProto2.OneofField.OneofBytes -> a == b
            is TestAllTypesProto2.OneofField.OneofBool -> a == b
            is TestAllTypesProto2.OneofField.OneofUint64 -> a == b
            is TestAllTypesProto2.OneofField.OneofFloat -> a.value.toBits() == (b as TestAllTypesProto2.OneofField.OneofFloat).value.toBits()
            is TestAllTypesProto2.OneofField.OneofDouble -> a.value.toBits() == (b as TestAllTypesProto2.OneofField.OneofDouble).value.toBits()
            is TestAllTypesProto2.OneofField.OneofEnum -> a == b
        }
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAllTypesProto2Internal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && this.optionalInt32 != other.optionalInt32) return false
        if (presenceMask[1] && this.optionalInt64 != other.optionalInt64) return false
        if (presenceMask[2] && this.optionalUint32 != other.optionalUint32) return false
        if (presenceMask[3] && this.optionalUint64 != other.optionalUint64) return false
        if (presenceMask[4] && this.optionalSint32 != other.optionalSint32) return false
        if (presenceMask[5] && this.optionalSint64 != other.optionalSint64) return false
        if (presenceMask[6] && this.optionalFixed32 != other.optionalFixed32) return false
        if (presenceMask[7] && this.optionalFixed64 != other.optionalFixed64) return false
        if (presenceMask[8] && this.optionalSfixed32 != other.optionalSfixed32) return false
        if (presenceMask[9] && this.optionalSfixed64 != other.optionalSfixed64) return false
        if (presenceMask[10] && this.optionalFloat?.toBits() != other.optionalFloat?.toBits()) return false
        if (presenceMask[11] && this.optionalDouble?.toBits() != other.optionalDouble?.toBits()) return false
        if (presenceMask[12] && this.optionalBool != other.optionalBool) return false
        if (presenceMask[13] && this.optionalString != other.optionalString) return false
        if (presenceMask[14] && this.optionalBytes != other.optionalBytes) return false
        if (presenceMask[15] && this.optionalNestedMessage != other.optionalNestedMessage) return false
        if (presenceMask[16] && this.optionalForeignMessage != other.optionalForeignMessage) return false
        if (presenceMask[17] && this.optionalNestedEnum != other.optionalNestedEnum) return false
        if (presenceMask[18] && this.optionalForeignEnum != other.optionalForeignEnum) return false
        if (presenceMask[19] && this.optionalStringPiece != other.optionalStringPiece) return false
        if (presenceMask[20] && this.optionalCord != other.optionalCord) return false
        if (presenceMask[21] && this.recursiveMessage != other.recursiveMessage) return false
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
        if (this.mapInt32Bool != other.mapInt32Bool) return false
        if (this.mapInt32Float != other.mapInt32Float) return false
        if (this.mapInt32Double != other.mapInt32Double) return false
        if (this.mapInt32NestedMessage != other.mapInt32NestedMessage) return false
        if (this.mapBoolBool != other.mapBoolBool) return false
        if (this.mapStringString != other.mapStringString) return false
        if (this.mapStringBytes != other.mapStringBytes) return false
        if (this.mapStringNestedMessage != other.mapStringNestedMessage) return false
        if (this.mapStringForeignMessage != other.mapStringForeignMessage) return false
        if (this.mapStringNestedEnum != other.mapStringNestedEnum) return false
        if (this.mapStringForeignEnum != other.mapStringForeignEnum) return false
        if (presenceMask[22] && this.data != other.data) return false
        if (presenceMask[23] && this.multiwordgroupfield != other.multiwordgroupfield) return false
        if (presenceMask[24] && this.defaultInt32 != other.defaultInt32) return false
        if (presenceMask[25] && this.defaultInt64 != other.defaultInt64) return false
        if (presenceMask[26] && this.defaultUint32 != other.defaultUint32) return false
        if (presenceMask[27] && this.defaultUint64 != other.defaultUint64) return false
        if (presenceMask[28] && this.defaultSint32 != other.defaultSint32) return false
        if (presenceMask[29] && this.defaultSint64 != other.defaultSint64) return false
        if (presenceMask[30] && this.defaultFixed32 != other.defaultFixed32) return false
        if (presenceMask[31] && this.defaultFixed64 != other.defaultFixed64) return false
        if (presenceMask[32] && this.defaultSfixed32 != other.defaultSfixed32) return false
        if (presenceMask[33] && this.defaultSfixed64 != other.defaultSfixed64) return false
        if (presenceMask[34] && this.defaultFloat.toBits() != other.defaultFloat.toBits()) return false
        if (presenceMask[35] && this.defaultDouble.toBits() != other.defaultDouble.toBits()) return false
        if (presenceMask[36] && this.defaultBool != other.defaultBool) return false
        if (presenceMask[37] && this.defaultString != other.defaultString) return false
        if (presenceMask[38] && this.defaultBytes != other.defaultBytes) return false
        if (presenceMask[39] && this.fieldname1 != other.fieldname1) return false
        if (presenceMask[40] && this.fieldName2 != other.fieldName2) return false
        if (presenceMask[41] && this.FieldName3 != other.FieldName3) return false
        if (presenceMask[42] && this.field_Name4_ != other.field_Name4_) return false
        if (presenceMask[43] && this.field0name5 != other.field0name5) return false
        if (presenceMask[44] && this.field_0Name6 != other.field_0Name6) return false
        if (presenceMask[45] && this.fieldName7 != other.fieldName7) return false
        if (presenceMask[46] && this.FieldName8 != other.FieldName8) return false
        if (presenceMask[47] && this.field_Name9 != other.field_Name9) return false
        if (presenceMask[48] && this.Field_Name10 != other.Field_Name10) return false
        if (presenceMask[49] && this.FIELD_NAME11 != other.FIELD_NAME11) return false
        if (presenceMask[50] && this.FIELDName12 != other.FIELDName12) return false
        if (presenceMask[51] && this._FieldName13 != other._FieldName13) return false
        if (presenceMask[52] && this.__FieldName14 != other.__FieldName14) return false
        if (presenceMask[53] && this.field_Name15 != other.field_Name15) return false
        if (presenceMask[54] && this.field__Name16 != other.field__Name16) return false
        if (presenceMask[55] && this.fieldName17__ != other.fieldName17__) return false
        if (presenceMask[56] && this.FieldName18__ != other.FieldName18__) return false
        if (presenceMask[57] && this.messageSetCorrect != other.messageSetCorrect) return false
        if (!oneOfEquals(this.oneofField, other.oneofField)) return false
        if (!extensionsEqual(other)) return false
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
        builder.appendLine("TestAllTypesProto2(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}optionalInt32=${this.optionalInt32},")
        } else {
            builder.appendLine("${nextIndentString}optionalInt32=<unset>,")
        }

        if (presenceMask[1]) {
            builder.appendLine("${nextIndentString}optionalInt64=${this.optionalInt64},")
        } else {
            builder.appendLine("${nextIndentString}optionalInt64=<unset>,")
        }

        if (presenceMask[2]) {
            builder.appendLine("${nextIndentString}optionalUint32=${this.optionalUint32},")
        } else {
            builder.appendLine("${nextIndentString}optionalUint32=<unset>,")
        }

        if (presenceMask[3]) {
            builder.appendLine("${nextIndentString}optionalUint64=${this.optionalUint64},")
        } else {
            builder.appendLine("${nextIndentString}optionalUint64=<unset>,")
        }

        if (presenceMask[4]) {
            builder.appendLine("${nextIndentString}optionalSint32=${this.optionalSint32},")
        } else {
            builder.appendLine("${nextIndentString}optionalSint32=<unset>,")
        }

        if (presenceMask[5]) {
            builder.appendLine("${nextIndentString}optionalSint64=${this.optionalSint64},")
        } else {
            builder.appendLine("${nextIndentString}optionalSint64=<unset>,")
        }

        if (presenceMask[6]) {
            builder.appendLine("${nextIndentString}optionalFixed32=${this.optionalFixed32},")
        } else {
            builder.appendLine("${nextIndentString}optionalFixed32=<unset>,")
        }

        if (presenceMask[7]) {
            builder.appendLine("${nextIndentString}optionalFixed64=${this.optionalFixed64},")
        } else {
            builder.appendLine("${nextIndentString}optionalFixed64=<unset>,")
        }

        if (presenceMask[8]) {
            builder.appendLine("${nextIndentString}optionalSfixed32=${this.optionalSfixed32},")
        } else {
            builder.appendLine("${nextIndentString}optionalSfixed32=<unset>,")
        }

        if (presenceMask[9]) {
            builder.appendLine("${nextIndentString}optionalSfixed64=${this.optionalSfixed64},")
        } else {
            builder.appendLine("${nextIndentString}optionalSfixed64=<unset>,")
        }

        if (presenceMask[10]) {
            builder.appendLine("${nextIndentString}optionalFloat=${this.optionalFloat},")
        } else {
            builder.appendLine("${nextIndentString}optionalFloat=<unset>,")
        }

        if (presenceMask[11]) {
            builder.appendLine("${nextIndentString}optionalDouble=${this.optionalDouble},")
        } else {
            builder.appendLine("${nextIndentString}optionalDouble=<unset>,")
        }

        if (presenceMask[12]) {
            builder.appendLine("${nextIndentString}optionalBool=${this.optionalBool},")
        } else {
            builder.appendLine("${nextIndentString}optionalBool=<unset>,")
        }

        if (presenceMask[13]) {
            builder.appendLine("${nextIndentString}optionalString=${this.optionalString},")
        } else {
            builder.appendLine("${nextIndentString}optionalString=<unset>,")
        }

        if (presenceMask[14]) {
            builder.appendLine("${nextIndentString}optionalBytes=${this.optionalBytes?.protoToString()},")
        } else {
            builder.appendLine("${nextIndentString}optionalBytes=<unset>,")
        }

        if (presenceMask[15]) {
            builder.appendLine("${nextIndentString}optionalNestedMessage=${this.optionalNestedMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalNestedMessage=<unset>,")
        }

        if (presenceMask[16]) {
            builder.appendLine("${nextIndentString}optionalForeignMessage=${this.optionalForeignMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalForeignMessage=<unset>,")
        }

        if (presenceMask[17]) {
            builder.appendLine("${nextIndentString}optionalNestedEnum=${this.optionalNestedEnum},")
        } else {
            builder.appendLine("${nextIndentString}optionalNestedEnum=<unset>,")
        }

        if (presenceMask[18]) {
            builder.appendLine("${nextIndentString}optionalForeignEnum=${this.optionalForeignEnum},")
        } else {
            builder.appendLine("${nextIndentString}optionalForeignEnum=<unset>,")
        }

        if (presenceMask[19]) {
            builder.appendLine("${nextIndentString}optionalStringPiece=${this.optionalStringPiece},")
        } else {
            builder.appendLine("${nextIndentString}optionalStringPiece=<unset>,")
        }

        if (presenceMask[20]) {
            builder.appendLine("${nextIndentString}optionalCord=${this.optionalCord},")
        } else {
            builder.appendLine("${nextIndentString}optionalCord=<unset>,")
        }

        if (presenceMask[21]) {
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
        builder.appendLine("${nextIndentString}mapInt32Bool=${this.mapInt32Bool},")
        builder.appendLine("${nextIndentString}mapInt32Float=${this.mapInt32Float},")
        builder.appendLine("${nextIndentString}mapInt32Double=${this.mapInt32Double},")
        builder.appendLine("${nextIndentString}mapInt32NestedMessage=${this.mapInt32NestedMessage},")
        builder.appendLine("${nextIndentString}mapBoolBool=${this.mapBoolBool},")
        builder.appendLine("${nextIndentString}mapStringString=${this.mapStringString},")
        builder.appendLine("${nextIndentString}mapStringBytes=${this.mapStringBytes},")
        builder.appendLine("${nextIndentString}mapStringNestedMessage=${this.mapStringNestedMessage},")
        builder.appendLine("${nextIndentString}mapStringForeignMessage=${this.mapStringForeignMessage},")
        builder.appendLine("${nextIndentString}mapStringNestedEnum=${this.mapStringNestedEnum},")
        builder.appendLine("${nextIndentString}mapStringForeignEnum=${this.mapStringForeignEnum},")
        if (presenceMask[22]) {
            builder.appendLine("${nextIndentString}data=${this.data.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}data=<unset>,")
        }

        if (presenceMask[23]) {
            builder.appendLine("${nextIndentString}multiwordgroupfield=${this.multiwordgroupfield.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}multiwordgroupfield=<unset>,")
        }

        if (presenceMask[24]) {
            builder.appendLine("${nextIndentString}defaultInt32=${this.defaultInt32},")
        } else {
            builder.appendLine("${nextIndentString}defaultInt32=<unset>,")
        }

        if (presenceMask[25]) {
            builder.appendLine("${nextIndentString}defaultInt64=${this.defaultInt64},")
        } else {
            builder.appendLine("${nextIndentString}defaultInt64=<unset>,")
        }

        if (presenceMask[26]) {
            builder.appendLine("${nextIndentString}defaultUint32=${this.defaultUint32},")
        } else {
            builder.appendLine("${nextIndentString}defaultUint32=<unset>,")
        }

        if (presenceMask[27]) {
            builder.appendLine("${nextIndentString}defaultUint64=${this.defaultUint64},")
        } else {
            builder.appendLine("${nextIndentString}defaultUint64=<unset>,")
        }

        if (presenceMask[28]) {
            builder.appendLine("${nextIndentString}defaultSint32=${this.defaultSint32},")
        } else {
            builder.appendLine("${nextIndentString}defaultSint32=<unset>,")
        }

        if (presenceMask[29]) {
            builder.appendLine("${nextIndentString}defaultSint64=${this.defaultSint64},")
        } else {
            builder.appendLine("${nextIndentString}defaultSint64=<unset>,")
        }

        if (presenceMask[30]) {
            builder.appendLine("${nextIndentString}defaultFixed32=${this.defaultFixed32},")
        } else {
            builder.appendLine("${nextIndentString}defaultFixed32=<unset>,")
        }

        if (presenceMask[31]) {
            builder.appendLine("${nextIndentString}defaultFixed64=${this.defaultFixed64},")
        } else {
            builder.appendLine("${nextIndentString}defaultFixed64=<unset>,")
        }

        if (presenceMask[32]) {
            builder.appendLine("${nextIndentString}defaultSfixed32=${this.defaultSfixed32},")
        } else {
            builder.appendLine("${nextIndentString}defaultSfixed32=<unset>,")
        }

        if (presenceMask[33]) {
            builder.appendLine("${nextIndentString}defaultSfixed64=${this.defaultSfixed64},")
        } else {
            builder.appendLine("${nextIndentString}defaultSfixed64=<unset>,")
        }

        if (presenceMask[34]) {
            builder.appendLine("${nextIndentString}defaultFloat=${this.defaultFloat},")
        } else {
            builder.appendLine("${nextIndentString}defaultFloat=<unset>,")
        }

        if (presenceMask[35]) {
            builder.appendLine("${nextIndentString}defaultDouble=${this.defaultDouble},")
        } else {
            builder.appendLine("${nextIndentString}defaultDouble=<unset>,")
        }

        if (presenceMask[36]) {
            builder.appendLine("${nextIndentString}defaultBool=${this.defaultBool},")
        } else {
            builder.appendLine("${nextIndentString}defaultBool=<unset>,")
        }

        if (presenceMask[37]) {
            builder.appendLine("${nextIndentString}defaultString=${this.defaultString},")
        } else {
            builder.appendLine("${nextIndentString}defaultString=<unset>,")
        }

        if (presenceMask[38]) {
            builder.appendLine("${nextIndentString}defaultBytes=${this.defaultBytes.protoToString()},")
        } else {
            builder.appendLine("${nextIndentString}defaultBytes=<unset>,")
        }

        if (presenceMask[39]) {
            builder.appendLine("${nextIndentString}fieldname1=${this.fieldname1},")
        } else {
            builder.appendLine("${nextIndentString}fieldname1=<unset>,")
        }

        if (presenceMask[40]) {
            builder.appendLine("${nextIndentString}fieldName2=${this.fieldName2},")
        } else {
            builder.appendLine("${nextIndentString}fieldName2=<unset>,")
        }

        if (presenceMask[41]) {
            builder.appendLine("${nextIndentString}FieldName3=${this.FieldName3},")
        } else {
            builder.appendLine("${nextIndentString}FieldName3=<unset>,")
        }

        if (presenceMask[42]) {
            builder.appendLine("${nextIndentString}field_Name4_=${this.field_Name4_},")
        } else {
            builder.appendLine("${nextIndentString}field_Name4_=<unset>,")
        }

        if (presenceMask[43]) {
            builder.appendLine("${nextIndentString}field0name5=${this.field0name5},")
        } else {
            builder.appendLine("${nextIndentString}field0name5=<unset>,")
        }

        if (presenceMask[44]) {
            builder.appendLine("${nextIndentString}field_0Name6=${this.field_0Name6},")
        } else {
            builder.appendLine("${nextIndentString}field_0Name6=<unset>,")
        }

        if (presenceMask[45]) {
            builder.appendLine("${nextIndentString}fieldName7=${this.fieldName7},")
        } else {
            builder.appendLine("${nextIndentString}fieldName7=<unset>,")
        }

        if (presenceMask[46]) {
            builder.appendLine("${nextIndentString}FieldName8=${this.FieldName8},")
        } else {
            builder.appendLine("${nextIndentString}FieldName8=<unset>,")
        }

        if (presenceMask[47]) {
            builder.appendLine("${nextIndentString}field_Name9=${this.field_Name9},")
        } else {
            builder.appendLine("${nextIndentString}field_Name9=<unset>,")
        }

        if (presenceMask[48]) {
            builder.appendLine("${nextIndentString}Field_Name10=${this.Field_Name10},")
        } else {
            builder.appendLine("${nextIndentString}Field_Name10=<unset>,")
        }

        if (presenceMask[49]) {
            builder.appendLine("${nextIndentString}FIELD_NAME11=${this.FIELD_NAME11},")
        } else {
            builder.appendLine("${nextIndentString}FIELD_NAME11=<unset>,")
        }

        if (presenceMask[50]) {
            builder.appendLine("${nextIndentString}FIELDName12=${this.FIELDName12},")
        } else {
            builder.appendLine("${nextIndentString}FIELDName12=<unset>,")
        }

        if (presenceMask[51]) {
            builder.appendLine("${nextIndentString}_FieldName13=${this._FieldName13},")
        } else {
            builder.appendLine("${nextIndentString}_FieldName13=<unset>,")
        }

        if (presenceMask[52]) {
            builder.appendLine("${nextIndentString}__FieldName14=${this.__FieldName14},")
        } else {
            builder.appendLine("${nextIndentString}__FieldName14=<unset>,")
        }

        if (presenceMask[53]) {
            builder.appendLine("${nextIndentString}field_Name15=${this.field_Name15},")
        } else {
            builder.appendLine("${nextIndentString}field_Name15=<unset>,")
        }

        if (presenceMask[54]) {
            builder.appendLine("${nextIndentString}field__Name16=${this.field__Name16},")
        } else {
            builder.appendLine("${nextIndentString}field__Name16=<unset>,")
        }

        if (presenceMask[55]) {
            builder.appendLine("${nextIndentString}fieldName17__=${this.fieldName17__},")
        } else {
            builder.appendLine("${nextIndentString}fieldName17__=<unset>,")
        }

        if (presenceMask[56]) {
            builder.appendLine("${nextIndentString}FieldName18__=${this.FieldName18__},")
        } else {
            builder.appendLine("${nextIndentString}FieldName18__=<unset>,")
        }

        if (presenceMask[57]) {
            builder.appendLine("${nextIndentString}messageSetCorrect=${this.messageSetCorrect.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}messageSetCorrect=<unset>,")
        }

        builder.appendLine("${nextIndentString}oneofField=${this.oneofField},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): TestAllTypesProto2Internal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: TestAllTypesProto2Internal.() -> Unit): TestAllTypesProto2Internal {
        val copy = TestAllTypesProto2Internal()
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
            copy.optionalBytes = this.optionalBytes
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
        copy.repeatedBytes = this.repeatedBytes.map { it }
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
        copy.mapStringBytes = this.mapStringBytes.mapValues { it.value }
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
            copy.defaultBytes = this.defaultBytes
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
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    fun TestAllTypesProto2.OneofField.oneOfCopy(): TestAllTypesProto2.OneofField {
        return when (this) {
            is TestAllTypesProto2.OneofField.OneofUint32 -> {
                this
            }
            is TestAllTypesProto2.OneofField.OneofNestedMessage -> {
                TestAllTypesProto2.OneofField.OneofNestedMessage(this.value.copy())
            }
            is TestAllTypesProto2.OneofField.OneofString -> {
                this
            }
            is TestAllTypesProto2.OneofField.OneofBytes -> {
                this
            }
            is TestAllTypesProto2.OneofField.OneofBool -> {
                this
            }
            is TestAllTypesProto2.OneofField.OneofUint64 -> {
                this
            }
            is TestAllTypesProto2.OneofField.OneofFloat -> {
                this
            }
            is TestAllTypesProto2.OneofField.OneofDouble -> {
                this
            }
            is TestAllTypesProto2.OneofField.OneofEnum -> {
                this
            }
        }
    }

    class NestedMessageInternal: TestAllTypesProto2.NestedMessage.Builder, InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val a: Int = 0
            const val corecursive: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __aDelegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.a) { null }
        override var a: Int? by __aDelegate
        internal val __corecursiveDelegate: MsgFieldDelegate<TestAllTypesProto2> = MsgFieldDelegate(PresenceIndices.corecursive) { TestAllTypesProto2Internal.DEFAULT }
        override var corecursive: TestAllTypesProto2 by __corecursiveDelegate

        private val _owner: NestedMessageInternal = this

        @InternalRpcApi
        val _presence: TestAllTypesProto2Presence.NestedMessage = object : TestAllTypesProto2Presence.NestedMessage, InternalPresenceObject {
            override val _message: NestedMessageInternal get() = _owner

            override val hasA: Boolean get() = presenceMask[0]

            override val hasCorecursive: Boolean get() = presenceMask[1]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) (this.a?.hashCode() ?: 0) else 0
            result = 31 * result + if (presenceMask[1]) this.corecursive.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as NestedMessageInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.a != other.a) return false
            if (presenceMask[1] && this.corecursive != other.corecursive) return false
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
            builder.appendLine("TestAllTypesProto2.NestedMessage(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}a=${this.a},")
            } else {
                builder.appendLine("${nextIndentString}a=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}corecursive=${this.corecursive.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}corecursive=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): NestedMessageInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: NestedMessageInternal.() -> Unit): NestedMessageInternal {
            val copy = NestedMessageInternal()
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

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllTypesProto2.NestedMessage> {
            override fun encode(value: TestAllTypesProto2.NestedMessage, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesProto2.NestedMessage {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = NestedMessageInternal()
                    checkForPlatformDecodeException {
                        NestedMessageInternal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto2.NestedMessage> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllTypesProto2.NestedMessage"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllTypesProto2.NestedMessage by lazy { NestedMessageInternal() }
        }
    }

    class MapInt32Int32EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.key) { 0 }
        var key: Int by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.value) { 0 }
        var value: Int by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32Int32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapInt32Int32Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapInt32Int32EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapInt64Int64EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.key) { 0L }
        var key: Long by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.value) { 0L }
        var value: Long by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt64Int64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapInt64Int64Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapInt64Int64EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapUint32Uint32EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.key) { 0u }
        var key: UInt by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.value) { 0u }
        var value: UInt by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapUint32Uint32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapUint32Uint32Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapUint32Uint32EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapUint64Uint64EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.key) { 0uL }
        var key: ULong by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.value) { 0uL }
        var value: ULong by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapUint64Uint64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapUint64Uint64Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapUint64Uint64EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapSint32Sint32EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.key) { 0 }
        var key: Int by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.value) { 0 }
        var value: Int by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSint32Sint32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapSint32Sint32Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapSint32Sint32EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapSint64Sint64EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.key) { 0L }
        var key: Long by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.value) { 0L }
        var value: Long by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSint64Sint64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapSint64Sint64Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapSint64Sint64EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapFixed32Fixed32EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.key) { 0u }
        var key: UInt by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.value) { 0u }
        var value: UInt by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapFixed32Fixed32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapFixed32Fixed32Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapFixed32Fixed32EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapFixed64Fixed64EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.key) { 0uL }
        var key: ULong by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.value) { 0uL }
        var value: ULong by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapFixed64Fixed64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapFixed64Fixed64Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapFixed64Fixed64EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapSfixed32Sfixed32EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.key) { 0 }
        var key: Int by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.value) { 0 }
        var value: Int by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSfixed32Sfixed32EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapSfixed32Sfixed32Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapSfixed32Sfixed32EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapSfixed64Sfixed64EntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.key) { 0L }
        var key: Long by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.value) { 0L }
        var value: Long by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapSfixed64Sfixed64EntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapSfixed64Sfixed64Entry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapSfixed64Sfixed64EntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapInt32BoolEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.key) { 0 }
        var key: Int by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.value) { false }
        var value: Boolean by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32BoolEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapInt32BoolEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapInt32BoolEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapInt32FloatEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.key) { 0 }
        var key: Int by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Float> = MsgFieldDelegate(PresenceIndices.value) { 0.0f }
        var value: Float by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.toBits().hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32FloatEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value.toBits() != other.value.toBits()) return false
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
            builder.appendLine("TestAllTypesProto2.MapInt32FloatEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapInt32FloatEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapInt32DoubleEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.key) { 0 }
        var key: Int by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Double> = MsgFieldDelegate(PresenceIndices.value) { 0.0 }
        var value: Double by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.toBits().hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32DoubleEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value.toBits() != other.value.toBits()) return false
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
            builder.appendLine("TestAllTypesProto2.MapInt32DoubleEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapInt32DoubleEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapInt32NestedMessageEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.key) { 0 }
        var key: Int by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<TestAllTypesProto2.NestedMessage> = MsgFieldDelegate(PresenceIndices.value) { NestedMessageInternal.DEFAULT }
        var value: TestAllTypesProto2.NestedMessage by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapInt32NestedMessageEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapInt32NestedMessageEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapInt32NestedMessageEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapBoolBoolEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.key) { false }
        var key: Boolean by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.value) { false }
        var value: Boolean by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapBoolBoolEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapBoolBoolEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapBoolBoolEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapStringStringEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.key) { "" }
        var key: String by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.value) { "" }
        var value: String by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringStringEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapStringStringEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapStringStringEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapStringBytesEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.key) { "" }
        var key: String by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<ByteString> = MsgFieldDelegate(PresenceIndices.value) { ByteString() }
        var value: ByteString by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringBytesEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapStringBytesEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value.protoToString()},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapStringBytesEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapStringNestedMessageEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.key) { "" }
        var key: String by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<TestAllTypesProto2.NestedMessage> = MsgFieldDelegate(PresenceIndices.value) { NestedMessageInternal.DEFAULT }
        var value: TestAllTypesProto2.NestedMessage by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringNestedMessageEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapStringNestedMessageEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapStringNestedMessageEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapStringForeignMessageEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.key) { "" }
        var key: String by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<ForeignMessageProto2> = MsgFieldDelegate(PresenceIndices.value) { ForeignMessageProto2Internal.DEFAULT }
        var value: ForeignMessageProto2 by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringForeignMessageEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapStringForeignMessageEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapStringForeignMessageEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapStringNestedEnumEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.key) { "" }
        var key: String by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<TestAllTypesProto2.NestedEnum> = MsgFieldDelegate(PresenceIndices.value) { TestAllTypesProto2.NestedEnum.FOO }
        var value: TestAllTypesProto2.NestedEnum by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringNestedEnumEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapStringNestedEnumEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapStringNestedEnumEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class MapStringForeignEnumEntryInternal: InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val key: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.key) { "" }
        var key: String by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<ForeignEnumProto2> = MsgFieldDelegate(PresenceIndices.value) { ForeignEnumProto2.FOREIGN_FOO }
        var value: ForeignEnumProto2 by __valueDelegate

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.value.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MapStringForeignEnumEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.key != other.key) return false
            if (presenceMask[1] && this.value != other.value) return false
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
            builder.appendLine("TestAllTypesProto2.MapStringForeignEnumEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MapStringForeignEnumEntryInternal {
            return this
        }

        @InternalRpcApi
        companion object
    }

    class DataInternal: TestAllTypesProto2.Data.Builder, InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val groupInt32: Int = 0
            const val groupUint32: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __groupInt32Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.groupInt32) { null }
        override var groupInt32: Int? by __groupInt32Delegate
        internal val __groupUint32Delegate: MsgFieldDelegate<UInt?> = MsgFieldDelegate(PresenceIndices.groupUint32) { null }
        override var groupUint32: UInt? by __groupUint32Delegate

        private val _owner: DataInternal = this

        @InternalRpcApi
        val _presence: TestAllTypesProto2Presence.Data = object : TestAllTypesProto2Presence.Data, InternalPresenceObject {
            override val _message: DataInternal get() = _owner

            override val hasGroupInt32: Boolean get() = presenceMask[0]

            override val hasGroupUint32: Boolean get() = presenceMask[1]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) (this.groupInt32?.hashCode() ?: 0) else 0
            result = 31 * result + if (presenceMask[1]) (this.groupUint32?.hashCode() ?: 0) else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as DataInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.groupInt32 != other.groupInt32) return false
            if (presenceMask[1] && this.groupUint32 != other.groupUint32) return false
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
            builder.appendLine("TestAllTypesProto2.Data(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}groupInt32=${this.groupInt32},")
            } else {
                builder.appendLine("${nextIndentString}groupInt32=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}groupUint32=${this.groupUint32},")
            } else {
                builder.appendLine("${nextIndentString}groupUint32=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): DataInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: DataInternal.() -> Unit): DataInternal {
            val copy = DataInternal()
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

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllTypesProto2.Data> {
            override fun encode(value: TestAllTypesProto2.Data, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesProto2.Data {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = DataInternal()
                    checkForPlatformDecodeException {
                        DataInternal.decodeWith(msg, it, config as? ProtoConfig, null)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto2.Data> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllTypesProto2.Data"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllTypesProto2.Data by lazy { DataInternal() }
        }
    }

    class MultiWordGroupFieldInternal: TestAllTypesProto2.MultiWordGroupField.Builder, InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val groupInt32: Int = 0
            const val groupUint32: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __groupInt32Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.groupInt32) { null }
        override var groupInt32: Int? by __groupInt32Delegate
        internal val __groupUint32Delegate: MsgFieldDelegate<UInt?> = MsgFieldDelegate(PresenceIndices.groupUint32) { null }
        override var groupUint32: UInt? by __groupUint32Delegate

        private val _owner: MultiWordGroupFieldInternal = this

        @InternalRpcApi
        val _presence: TestAllTypesProto2Presence.MultiWordGroupField = object : TestAllTypesProto2Presence.MultiWordGroupField, InternalPresenceObject {
            override val _message: MultiWordGroupFieldInternal get() = _owner

            override val hasGroupInt32: Boolean get() = presenceMask[0]

            override val hasGroupUint32: Boolean get() = presenceMask[1]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) (this.groupInt32?.hashCode() ?: 0) else 0
            result = 31 * result + if (presenceMask[1]) (this.groupUint32?.hashCode() ?: 0) else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MultiWordGroupFieldInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.groupInt32 != other.groupInt32) return false
            if (presenceMask[1] && this.groupUint32 != other.groupUint32) return false
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
            builder.appendLine("TestAllTypesProto2.MultiWordGroupField(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}groupInt32=${this.groupInt32},")
            } else {
                builder.appendLine("${nextIndentString}groupInt32=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}groupUint32=${this.groupUint32},")
            } else {
                builder.appendLine("${nextIndentString}groupUint32=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MultiWordGroupFieldInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: MultiWordGroupFieldInternal.() -> Unit): MultiWordGroupFieldInternal {
            val copy = MultiWordGroupFieldInternal()
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

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllTypesProto2.MultiWordGroupField> {
            override fun encode(value: TestAllTypesProto2.MultiWordGroupField, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesProto2.MultiWordGroupField {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = MultiWordGroupFieldInternal()
                    checkForPlatformDecodeException {
                        MultiWordGroupFieldInternal.decodeWith(msg, it, config as? ProtoConfig, null)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto2.MultiWordGroupField> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllTypesProto2.MultiWordGroupField"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllTypesProto2.MultiWordGroupField by lazy { MultiWordGroupFieldInternal() }
        }
    }

    class MessageSetCorrectInternal: TestAllTypesProto2.MessageSetCorrect.Builder, InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        private val _owner: MessageSetCorrectInternal = this

        @InternalRpcApi
        val _presence: TestAllTypesProto2Presence.MessageSetCorrect = object : TestAllTypesProto2Presence.MessageSetCorrect, InternalPresenceObject {
            override val _message: MessageSetCorrectInternal get() = _owner
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = this::class.hashCode()
            result = 31 * result + extensionsHashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectInternal
            other.checkRequiredFields()
            if (!extensionsEqual(other)) return false
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
            builder.appendLine("TestAllTypesProto2.MessageSetCorrect(")
            builder.appendExtensions(nextIndentString)
            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MessageSetCorrectInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: MessageSetCorrectInternal.() -> Unit): MessageSetCorrectInternal {
            val copy = MessageSetCorrectInternal()
            copy.copyExtensionsFrom(this)
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllTypesProto2.MessageSetCorrect> {
            override fun encode(value: TestAllTypesProto2.MessageSetCorrect, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesProto2.MessageSetCorrect {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = MessageSetCorrectInternal()
                    checkForPlatformDecodeException {
                        MessageSetCorrectInternal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto2.MessageSetCorrect> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllTypesProto2.MessageSetCorrect by lazy { MessageSetCorrectInternal() }
        }
    }

    class MessageSetCorrectExtension1Internal: TestAllTypesProto2.MessageSetCorrectExtension1.Builder, InternalMessage(fieldsWithPresence = 1) {
        private object PresenceIndices {
            const val str: Int = 0
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __strDelegate: MsgFieldDelegate<String?> = MsgFieldDelegate(PresenceIndices.str) { null }
        override var str: String? by __strDelegate

        private val _owner: MessageSetCorrectExtension1Internal = this

        @InternalRpcApi
        val _presence: TestAllTypesProto2Presence.MessageSetCorrectExtension1 = object : TestAllTypesProto2Presence.MessageSetCorrectExtension1, InternalPresenceObject {
            override val _message: MessageSetCorrectExtension1Internal get() = _owner

            override val hasStr: Boolean get() = presenceMask[0]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) (this.str?.hashCode() ?: 0) else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectExtension1Internal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.str != other.str) return false
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
            builder.appendLine("TestAllTypesProto2.MessageSetCorrectExtension1(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}str=${this.str},")
            } else {
                builder.appendLine("${nextIndentString}str=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MessageSetCorrectExtension1Internal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: MessageSetCorrectExtension1Internal.() -> Unit): MessageSetCorrectExtension1Internal {
            val copy = MessageSetCorrectExtension1Internal()
            if (presenceMask[0]) {
                copy.str = this.str
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllTypesProto2.MessageSetCorrectExtension1> {
            override fun encode(value: TestAllTypesProto2.MessageSetCorrectExtension1, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesProto2.MessageSetCorrectExtension1 {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = MessageSetCorrectExtension1Internal()
                    checkForPlatformDecodeException {
                        MessageSetCorrectExtension1Internal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto2.MessageSetCorrectExtension1> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllTypesProto2.MessageSetCorrectExtension1 by lazy { MessageSetCorrectExtension1Internal() }
        }
    }

    class MessageSetCorrectExtension2Internal: TestAllTypesProto2.MessageSetCorrectExtension2.Builder, InternalMessage(fieldsWithPresence = 1) {
        private object PresenceIndices {
            const val i: Int = 0
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __iDelegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.i) { null }
        override var i: Int? by __iDelegate

        private val _owner: MessageSetCorrectExtension2Internal = this

        @InternalRpcApi
        val _presence: TestAllTypesProto2Presence.MessageSetCorrectExtension2 = object : TestAllTypesProto2Presence.MessageSetCorrectExtension2, InternalPresenceObject {
            override val _message: MessageSetCorrectExtension2Internal get() = _owner

            override val hasI: Boolean get() = presenceMask[0]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) (this.i?.hashCode() ?: 0) else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectExtension2Internal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.i != other.i) return false
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
            builder.appendLine("TestAllTypesProto2.MessageSetCorrectExtension2(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}i=${this.i},")
            } else {
                builder.appendLine("${nextIndentString}i=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MessageSetCorrectExtension2Internal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: MessageSetCorrectExtension2Internal.() -> Unit): MessageSetCorrectExtension2Internal {
            val copy = MessageSetCorrectExtension2Internal()
            if (presenceMask[0]) {
                copy.i = this.i
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllTypesProto2.MessageSetCorrectExtension2> {
            override fun encode(value: TestAllTypesProto2.MessageSetCorrectExtension2, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesProto2.MessageSetCorrectExtension2 {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = MessageSetCorrectExtension2Internal()
                    checkForPlatformDecodeException {
                        MessageSetCorrectExtension2Internal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto2.MessageSetCorrectExtension2> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllTypesProto2.MessageSetCorrectExtension2 by lazy { MessageSetCorrectExtension2Internal() }
        }
    }

    class ExtensionWithOneofInternal: TestAllTypesProto2.ExtensionWithOneof.Builder, InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override var oneofField: TestAllTypesProto2.ExtensionWithOneof.OneofField? = null

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = (this.oneofField?.oneOfHashCode() ?: 0)
            return result
        }

        fun TestAllTypesProto2.ExtensionWithOneof.OneofField.oneOfHashCode(): Int {
            val offset = when (this) {
                is TestAllTypesProto2.ExtensionWithOneof.OneofField.A -> 0
                is TestAllTypesProto2.ExtensionWithOneof.OneofField.B -> 1
            }

            return hashCode() + offset
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as ExtensionWithOneofInternal
            other.checkRequiredFields()
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
            builder.appendLine("TestAllTypesProto2.ExtensionWithOneof(")
            builder.appendLine("${nextIndentString}oneofField=${this.oneofField},")
            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): ExtensionWithOneofInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: ExtensionWithOneofInternal.() -> Unit): ExtensionWithOneofInternal {
            val copy = ExtensionWithOneofInternal()
            copy.oneofField = this.oneofField?.oneOfCopy()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        fun TestAllTypesProto2.ExtensionWithOneof.OneofField.oneOfCopy(): TestAllTypesProto2.ExtensionWithOneof.OneofField {
            return this
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllTypesProto2.ExtensionWithOneof> {
            override fun encode(value: TestAllTypesProto2.ExtensionWithOneof, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesProto2.ExtensionWithOneof {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = ExtensionWithOneofInternal()
                    checkForPlatformDecodeException {
                        ExtensionWithOneofInternal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto2.ExtensionWithOneof> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllTypesProto2.ExtensionWithOneof"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllTypesProto2.ExtensionWithOneof by lazy { ExtensionWithOneofInternal() }
        }
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<TestAllTypesProto2> {
        override fun encode(value: TestAllTypesProto2, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesProto2 {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = TestAllTypesProto2Internal()
                checkForPlatformDecodeException {
                    TestAllTypesProto2Internal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<TestAllTypesProto2> {
        override val fullName: String = "protobuf_test_messages.proto2.TestAllTypesProto2"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: TestAllTypesProto2 by lazy { TestAllTypesProto2Internal() }
    }
}

class ForeignMessageProto2Internal: ForeignMessageProto2.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        const val c: Int = 0
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __cDelegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.c) { null }
    override var c: Int? by __cDelegate

    private val _owner: ForeignMessageProto2Internal = this

    @InternalRpcApi
    val _presence: ForeignMessageProto2Presence = object : ForeignMessageProto2Presence, InternalPresenceObject {
        override val _message: ForeignMessageProto2Internal get() = _owner

        override val hasC: Boolean get() = presenceMask[0]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) (this.c?.hashCode() ?: 0) else 0
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ForeignMessageProto2Internal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && this.c != other.c) return false
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
        builder.appendLine("ForeignMessageProto2(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}c=${this.c},")
        } else {
            builder.appendLine("${nextIndentString}c=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): ForeignMessageProto2Internal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: ForeignMessageProto2Internal.() -> Unit): ForeignMessageProto2Internal {
        val copy = ForeignMessageProto2Internal()
        if (presenceMask[0]) {
            copy.c = this.c
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<ForeignMessageProto2> {
        override fun encode(value: ForeignMessageProto2, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): ForeignMessageProto2 {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ForeignMessageProto2Internal()
                checkForPlatformDecodeException {
                    ForeignMessageProto2Internal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<ForeignMessageProto2> {
        override val fullName: String = "protobuf_test_messages.proto2.ForeignMessageProto2"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: ForeignMessageProto2 by lazy { ForeignMessageProto2Internal() }
    }
}

class GroupFieldInternal: GroupField.Builder, InternalMessage(fieldsWithPresence = 2) {
    private object PresenceIndices {
        const val groupInt32: Int = 0
        const val groupUint32: Int = 1
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __groupInt32Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.groupInt32) { null }
    override var groupInt32: Int? by __groupInt32Delegate
    internal val __groupUint32Delegate: MsgFieldDelegate<UInt?> = MsgFieldDelegate(PresenceIndices.groupUint32) { null }
    override var groupUint32: UInt? by __groupUint32Delegate

    private val _owner: GroupFieldInternal = this

    @InternalRpcApi
    val _presence: GroupFieldPresence = object : GroupFieldPresence, InternalPresenceObject {
        override val _message: GroupFieldInternal get() = _owner

        override val hasGroupInt32: Boolean get() = presenceMask[0]

        override val hasGroupUint32: Boolean get() = presenceMask[1]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) (this.groupInt32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[1]) (this.groupUint32?.hashCode() ?: 0) else 0
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as GroupFieldInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && this.groupInt32 != other.groupInt32) return false
        if (presenceMask[1] && this.groupUint32 != other.groupUint32) return false
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
        builder.appendLine("GroupField(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}groupInt32=${this.groupInt32},")
        } else {
            builder.appendLine("${nextIndentString}groupInt32=<unset>,")
        }

        if (presenceMask[1]) {
            builder.appendLine("${nextIndentString}groupUint32=${this.groupUint32},")
        } else {
            builder.appendLine("${nextIndentString}groupUint32=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): GroupFieldInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: GroupFieldInternal.() -> Unit): GroupFieldInternal {
        val copy = GroupFieldInternal()
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

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<GroupField> {
        override fun encode(value: GroupField, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): GroupField {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = GroupFieldInternal()
                checkForPlatformDecodeException {
                    GroupFieldInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<GroupField> {
        override val fullName: String = "protobuf_test_messages.proto2.GroupField"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: GroupField by lazy { GroupFieldInternal() }
    }
}

class UnknownToTestAllTypesInternal: UnknownToTestAllTypes.Builder, InternalMessage(fieldsWithPresence = 5) {
    private object PresenceIndices {
        const val optionalInt32: Int = 0
        const val optionalString: Int = 1
        const val nestedMessage: Int = 2
        const val optionalgroup: Int = 3
        const val optionalBool: Int = 4
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __optionalInt32Delegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.optionalInt32) { null }
    override var optionalInt32: Int? by __optionalInt32Delegate
    internal val __optionalStringDelegate: MsgFieldDelegate<String?> = MsgFieldDelegate(PresenceIndices.optionalString) { null }
    override var optionalString: String? by __optionalStringDelegate
    internal val __nestedMessageDelegate: MsgFieldDelegate<ForeignMessageProto2> = MsgFieldDelegate(PresenceIndices.nestedMessage) { ForeignMessageProto2Internal.DEFAULT }
    override var nestedMessage: ForeignMessageProto2 by __nestedMessageDelegate
    internal val __optionalgroupDelegate: MsgFieldDelegate<UnknownToTestAllTypes.OptionalGroup> = MsgFieldDelegate(PresenceIndices.optionalgroup) { OptionalGroupInternal.DEFAULT }
    override var optionalgroup: UnknownToTestAllTypes.OptionalGroup by __optionalgroupDelegate
    internal val __optionalBoolDelegate: MsgFieldDelegate<Boolean?> = MsgFieldDelegate(PresenceIndices.optionalBool) { null }
    override var optionalBool: Boolean? by __optionalBoolDelegate
    internal val __repeatedInt32Delegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    override var repeatedInt32: List<Int> by __repeatedInt32Delegate

    private val _owner: UnknownToTestAllTypesInternal = this

    @InternalRpcApi
    val _presence: UnknownToTestAllTypesPresence = object : UnknownToTestAllTypesPresence, InternalPresenceObject {
        override val _message: UnknownToTestAllTypesInternal get() = _owner

        override val hasOptionalInt32: Boolean get() = presenceMask[0]

        override val hasOptionalString: Boolean get() = presenceMask[1]

        override val hasNestedMessage: Boolean get() = presenceMask[2]

        override val hasOptionalgroup: Boolean get() = presenceMask[3]

        override val hasOptionalBool: Boolean get() = presenceMask[4]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) (this.optionalInt32?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[1]) (this.optionalString?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[2]) this.nestedMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[3]) this.optionalgroup.hashCode() else 0
        result = 31 * result + if (presenceMask[4]) (this.optionalBool?.hashCode() ?: 0) else 0
        result = 31 * result + this.repeatedInt32.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UnknownToTestAllTypesInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && this.optionalInt32 != other.optionalInt32) return false
        if (presenceMask[1] && this.optionalString != other.optionalString) return false
        if (presenceMask[2] && this.nestedMessage != other.nestedMessage) return false
        if (presenceMask[3] && this.optionalgroup != other.optionalgroup) return false
        if (presenceMask[4] && this.optionalBool != other.optionalBool) return false
        if (this.repeatedInt32 != other.repeatedInt32) return false
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
        builder.appendLine("UnknownToTestAllTypes(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}optionalInt32=${this.optionalInt32},")
        } else {
            builder.appendLine("${nextIndentString}optionalInt32=<unset>,")
        }

        if (presenceMask[1]) {
            builder.appendLine("${nextIndentString}optionalString=${this.optionalString},")
        } else {
            builder.appendLine("${nextIndentString}optionalString=<unset>,")
        }

        if (presenceMask[2]) {
            builder.appendLine("${nextIndentString}nestedMessage=${this.nestedMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}nestedMessage=<unset>,")
        }

        if (presenceMask[3]) {
            builder.appendLine("${nextIndentString}optionalgroup=${this.optionalgroup.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalgroup=<unset>,")
        }

        if (presenceMask[4]) {
            builder.appendLine("${nextIndentString}optionalBool=${this.optionalBool},")
        } else {
            builder.appendLine("${nextIndentString}optionalBool=<unset>,")
        }

        builder.appendLine("${nextIndentString}repeatedInt32=${this.repeatedInt32},")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): UnknownToTestAllTypesInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: UnknownToTestAllTypesInternal.() -> Unit): UnknownToTestAllTypesInternal {
        val copy = UnknownToTestAllTypesInternal()
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

    class OptionalGroupInternal: UnknownToTestAllTypes.OptionalGroup.Builder, InternalMessage(fieldsWithPresence = 1) {
        private object PresenceIndices {
            const val a: Int = 0
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __aDelegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.a) { null }
        override var a: Int? by __aDelegate

        private val _owner: OptionalGroupInternal = this

        @InternalRpcApi
        val _presence: UnknownToTestAllTypesPresence.OptionalGroup = object : UnknownToTestAllTypesPresence.OptionalGroup, InternalPresenceObject {
            override val _message: OptionalGroupInternal get() = _owner

            override val hasA: Boolean get() = presenceMask[0]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) (this.a?.hashCode() ?: 0) else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as OptionalGroupInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.a != other.a) return false
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
            builder.appendLine("UnknownToTestAllTypes.OptionalGroup(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}a=${this.a},")
            } else {
                builder.appendLine("${nextIndentString}a=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): OptionalGroupInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: OptionalGroupInternal.() -> Unit): OptionalGroupInternal {
            val copy = OptionalGroupInternal()
            if (presenceMask[0]) {
                copy.a = this.a
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<UnknownToTestAllTypes.OptionalGroup> {
            override fun encode(value: UnknownToTestAllTypes.OptionalGroup, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): UnknownToTestAllTypes.OptionalGroup {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = OptionalGroupInternal()
                    checkForPlatformDecodeException {
                        OptionalGroupInternal.decodeWith(msg, it, config as? ProtoConfig, null)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<UnknownToTestAllTypes.OptionalGroup> {
            override val fullName: String = "protobuf_test_messages.proto2.UnknownToTestAllTypes.OptionalGroup"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: UnknownToTestAllTypes.OptionalGroup by lazy { OptionalGroupInternal() }
        }
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<UnknownToTestAllTypes> {
        override fun encode(value: UnknownToTestAllTypes, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): UnknownToTestAllTypes {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = UnknownToTestAllTypesInternal()
                checkForPlatformDecodeException {
                    UnknownToTestAllTypesInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<UnknownToTestAllTypes> {
        override val fullName: String = "protobuf_test_messages.proto2.UnknownToTestAllTypes"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: UnknownToTestAllTypes by lazy { UnknownToTestAllTypesInternal() }
    }
}

class NullHypothesisProto2Internal: NullHypothesisProto2.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = this::class.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as NullHypothesisProto2Internal
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
        builder.appendLine("NullHypothesisProto2(")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): NullHypothesisProto2Internal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: NullHypothesisProto2Internal.() -> Unit): NullHypothesisProto2Internal {
        val copy = NullHypothesisProto2Internal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<NullHypothesisProto2> {
        override fun encode(value: NullHypothesisProto2, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): NullHypothesisProto2 {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = NullHypothesisProto2Internal()
                checkForPlatformDecodeException {
                    NullHypothesisProto2Internal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<NullHypothesisProto2> {
        override val fullName: String = "protobuf_test_messages.proto2.NullHypothesisProto2"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: NullHypothesisProto2 by lazy { NullHypothesisProto2Internal() }
    }
}

class EnumOnlyProto2Internal: EnumOnlyProto2.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = this::class.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumOnlyProto2Internal
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
        builder.appendLine("EnumOnlyProto2(")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): EnumOnlyProto2Internal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: EnumOnlyProto2Internal.() -> Unit): EnumOnlyProto2Internal {
        val copy = EnumOnlyProto2Internal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<EnumOnlyProto2> {
        override fun encode(value: EnumOnlyProto2, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): EnumOnlyProto2 {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = EnumOnlyProto2Internal()
                checkForPlatformDecodeException {
                    EnumOnlyProto2Internal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<EnumOnlyProto2> {
        override val fullName: String = "protobuf_test_messages.proto2.EnumOnlyProto2"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: EnumOnlyProto2 by lazy { EnumOnlyProto2Internal() }
    }
}

class OneStringProto2Internal: OneStringProto2.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        const val data: Int = 0
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __dataDelegate: MsgFieldDelegate<String?> = MsgFieldDelegate(PresenceIndices.data) { null }
    override var data: String? by __dataDelegate

    private val _owner: OneStringProto2Internal = this

    @InternalRpcApi
    val _presence: OneStringProto2Presence = object : OneStringProto2Presence, InternalPresenceObject {
        override val _message: OneStringProto2Internal get() = _owner

        override val hasData: Boolean get() = presenceMask[0]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) (this.data?.hashCode() ?: 0) else 0
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as OneStringProto2Internal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && this.data != other.data) return false
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
        builder.appendLine("OneStringProto2(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}data=${this.data},")
        } else {
            builder.appendLine("${nextIndentString}data=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): OneStringProto2Internal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: OneStringProto2Internal.() -> Unit): OneStringProto2Internal {
        val copy = OneStringProto2Internal()
        if (presenceMask[0]) {
            copy.data = this.data
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<OneStringProto2> {
        override fun encode(value: OneStringProto2, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): OneStringProto2 {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = OneStringProto2Internal()
                checkForPlatformDecodeException {
                    OneStringProto2Internal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<OneStringProto2> {
        override val fullName: String = "protobuf_test_messages.proto2.OneStringProto2"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: OneStringProto2 by lazy { OneStringProto2Internal() }
    }
}

class ProtoWithKeywordsInternal: ProtoWithKeywords.Builder, InternalMessage(fieldsWithPresence = 2) {
    private object PresenceIndices {
        const val inline: Int = 0
        const val concept: Int = 1
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __inlineDelegate: MsgFieldDelegate<Int?> = MsgFieldDelegate(PresenceIndices.inline) { null }
    override var inline: Int? by __inlineDelegate
    internal val __conceptDelegate: MsgFieldDelegate<String?> = MsgFieldDelegate(PresenceIndices.concept) { null }
    override var concept: String? by __conceptDelegate
    internal val __requiresDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    override var requires: List<String> by __requiresDelegate

    private val _owner: ProtoWithKeywordsInternal = this

    @InternalRpcApi
    val _presence: ProtoWithKeywordsPresence = object : ProtoWithKeywordsPresence, InternalPresenceObject {
        override val _message: ProtoWithKeywordsInternal get() = _owner

        override val hasInline: Boolean get() = presenceMask[0]

        override val hasConcept: Boolean get() = presenceMask[1]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) (this.inline?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[1]) (this.concept?.hashCode() ?: 0) else 0
        result = 31 * result + this.requires.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ProtoWithKeywordsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && this.inline != other.inline) return false
        if (presenceMask[1] && this.concept != other.concept) return false
        if (this.requires != other.requires) return false
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
        builder.appendLine("ProtoWithKeywords(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}inline=${this.inline},")
        } else {
            builder.appendLine("${nextIndentString}inline=<unset>,")
        }

        if (presenceMask[1]) {
            builder.appendLine("${nextIndentString}concept=${this.concept},")
        } else {
            builder.appendLine("${nextIndentString}concept=<unset>,")
        }

        builder.appendLine("${nextIndentString}requires=${this.requires},")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): ProtoWithKeywordsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: ProtoWithKeywordsInternal.() -> Unit): ProtoWithKeywordsInternal {
        val copy = ProtoWithKeywordsInternal()
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

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<ProtoWithKeywords> {
        override fun encode(value: ProtoWithKeywords, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): ProtoWithKeywords {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ProtoWithKeywordsInternal()
                checkForPlatformDecodeException {
                    ProtoWithKeywordsInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<ProtoWithKeywords> {
        override val fullName: String = "protobuf_test_messages.proto2.ProtoWithKeywords"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: ProtoWithKeywords by lazy { ProtoWithKeywordsInternal() }
    }
}

class TestAllRequiredTypesProto2Internal: TestAllRequiredTypesProto2.Builder, InternalMessage(fieldsWithPresence = 39) {
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
        val defaultBytes: ByteString = ByteString(0x6A, 0x6F, 0x73, 0x68, 0x75, 0x61)
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __requiredInt32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.requiredInt32) { 0 }
    override var requiredInt32: Int by __requiredInt32Delegate
    internal val __requiredInt64Delegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.requiredInt64) { 0L }
    override var requiredInt64: Long by __requiredInt64Delegate
    internal val __requiredUint32Delegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.requiredUint32) { 0u }
    override var requiredUint32: UInt by __requiredUint32Delegate
    internal val __requiredUint64Delegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.requiredUint64) { 0uL }
    override var requiredUint64: ULong by __requiredUint64Delegate
    internal val __requiredSint32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.requiredSint32) { 0 }
    override var requiredSint32: Int by __requiredSint32Delegate
    internal val __requiredSint64Delegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.requiredSint64) { 0L }
    override var requiredSint64: Long by __requiredSint64Delegate
    internal val __requiredFixed32Delegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.requiredFixed32) { 0u }
    override var requiredFixed32: UInt by __requiredFixed32Delegate
    internal val __requiredFixed64Delegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.requiredFixed64) { 0uL }
    override var requiredFixed64: ULong by __requiredFixed64Delegate
    internal val __requiredSfixed32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.requiredSfixed32) { 0 }
    override var requiredSfixed32: Int by __requiredSfixed32Delegate
    internal val __requiredSfixed64Delegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.requiredSfixed64) { 0L }
    override var requiredSfixed64: Long by __requiredSfixed64Delegate
    internal val __requiredFloatDelegate: MsgFieldDelegate<Float> = MsgFieldDelegate(PresenceIndices.requiredFloat) { 0.0f }
    override var requiredFloat: Float by __requiredFloatDelegate
    internal val __requiredDoubleDelegate: MsgFieldDelegate<Double> = MsgFieldDelegate(PresenceIndices.requiredDouble) { 0.0 }
    override var requiredDouble: Double by __requiredDoubleDelegate
    internal val __requiredBoolDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.requiredBool) { false }
    override var requiredBool: Boolean by __requiredBoolDelegate
    internal val __requiredStringDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.requiredString) { "" }
    override var requiredString: String by __requiredStringDelegate
    internal val __requiredBytesDelegate: MsgFieldDelegate<ByteString> = MsgFieldDelegate(PresenceIndices.requiredBytes) { ByteString() }
    override var requiredBytes: ByteString by __requiredBytesDelegate
    internal val __requiredNestedMessageDelegate: MsgFieldDelegate<TestAllRequiredTypesProto2.NestedMessage> = MsgFieldDelegate(PresenceIndices.requiredNestedMessage) { NestedMessageInternal.DEFAULT }
    override var requiredNestedMessage: TestAllRequiredTypesProto2.NestedMessage by __requiredNestedMessageDelegate
    internal val __requiredForeignMessageDelegate: MsgFieldDelegate<ForeignMessageProto2> = MsgFieldDelegate(PresenceIndices.requiredForeignMessage) { ForeignMessageProto2Internal.DEFAULT }
    override var requiredForeignMessage: ForeignMessageProto2 by __requiredForeignMessageDelegate
    internal val __requiredNestedEnumDelegate: MsgFieldDelegate<TestAllRequiredTypesProto2.NestedEnum> = MsgFieldDelegate(PresenceIndices.requiredNestedEnum) { TestAllRequiredTypesProto2.NestedEnum.FOO }
    override var requiredNestedEnum: TestAllRequiredTypesProto2.NestedEnum by __requiredNestedEnumDelegate
    internal val __requiredForeignEnumDelegate: MsgFieldDelegate<ForeignEnumProto2> = MsgFieldDelegate(PresenceIndices.requiredForeignEnum) { ForeignEnumProto2.FOREIGN_FOO }
    override var requiredForeignEnum: ForeignEnumProto2 by __requiredForeignEnumDelegate
    internal val __requiredStringPieceDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.requiredStringPiece) { "" }
    override var requiredStringPiece: String by __requiredStringPieceDelegate
    internal val __requiredCordDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.requiredCord) { "" }
    override var requiredCord: String by __requiredCordDelegate
    internal val __recursiveMessageDelegate: MsgFieldDelegate<TestAllRequiredTypesProto2> = MsgFieldDelegate(PresenceIndices.recursiveMessage) { TestAllRequiredTypesProto2Internal.DEFAULT }
    override var recursiveMessage: TestAllRequiredTypesProto2 by __recursiveMessageDelegate
    internal val __optionalRecursiveMessageDelegate: MsgFieldDelegate<TestAllRequiredTypesProto2> = MsgFieldDelegate(PresenceIndices.optionalRecursiveMessage) { TestAllRequiredTypesProto2Internal.DEFAULT }
    override var optionalRecursiveMessage: TestAllRequiredTypesProto2 by __optionalRecursiveMessageDelegate
    internal val __dataDelegate: MsgFieldDelegate<TestAllRequiredTypesProto2.Data> = MsgFieldDelegate(PresenceIndices.data) { DataInternal.DEFAULT }
    override var data: TestAllRequiredTypesProto2.Data by __dataDelegate
    internal val __defaultInt32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.defaultInt32) { -123456789 }
    override var defaultInt32: Int by __defaultInt32Delegate
    internal val __defaultInt64Delegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.defaultInt64) { -9123456789123456789L }
    override var defaultInt64: Long by __defaultInt64Delegate
    internal val __defaultUint32Delegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.defaultUint32) { 2123456789u }
    override var defaultUint32: UInt by __defaultUint32Delegate
    internal val __defaultUint64Delegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.defaultUint64) { 10123456789123456789uL }
    override var defaultUint64: ULong by __defaultUint64Delegate
    internal val __defaultSint32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.defaultSint32) { -123456789 }
    override var defaultSint32: Int by __defaultSint32Delegate
    internal val __defaultSint64Delegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.defaultSint64) { -9123456789123456789L }
    override var defaultSint64: Long by __defaultSint64Delegate
    internal val __defaultFixed32Delegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.defaultFixed32) { 2123456789u }
    override var defaultFixed32: UInt by __defaultFixed32Delegate
    internal val __defaultFixed64Delegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.defaultFixed64) { 10123456789123456789uL }
    override var defaultFixed64: ULong by __defaultFixed64Delegate
    internal val __defaultSfixed32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.defaultSfixed32) { -123456789 }
    override var defaultSfixed32: Int by __defaultSfixed32Delegate
    internal val __defaultSfixed64Delegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.defaultSfixed64) { -9123456789123456789L }
    override var defaultSfixed64: Long by __defaultSfixed64Delegate
    internal val __defaultFloatDelegate: MsgFieldDelegate<Float> = MsgFieldDelegate(PresenceIndices.defaultFloat) { Float.fromBits(0x50061C46.toInt()) }
    override var defaultFloat: Float by __defaultFloatDelegate
    internal val __defaultDoubleDelegate: MsgFieldDelegate<Double> = MsgFieldDelegate(PresenceIndices.defaultDouble) { Double.fromBits(0x44ADA56A4B0835C0L) }
    override var defaultDouble: Double by __defaultDoubleDelegate
    internal val __defaultBoolDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.defaultBool) { true }
    override var defaultBool: Boolean by __defaultBoolDelegate
    internal val __defaultStringDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.defaultString) { "Rosebud" }
    override var defaultString: String by __defaultStringDelegate
    internal val __defaultBytesDelegate: MsgFieldDelegate<ByteString> = MsgFieldDelegate(PresenceIndices.defaultBytes) { BytesDefaults.defaultBytes }
    override var defaultBytes: ByteString by __defaultBytesDelegate

    private val _owner: TestAllRequiredTypesProto2Internal = this

    @InternalRpcApi
    val _presence: TestAllRequiredTypesProto2Presence = object : TestAllRequiredTypesProto2Presence, InternalPresenceObject {
        override val _message: TestAllRequiredTypesProto2Internal get() = _owner

        override val hasRequiredInt32: Boolean get() = presenceMask[0]

        override val hasRequiredInt64: Boolean get() = presenceMask[1]

        override val hasRequiredUint32: Boolean get() = presenceMask[2]

        override val hasRequiredUint64: Boolean get() = presenceMask[3]

        override val hasRequiredSint32: Boolean get() = presenceMask[4]

        override val hasRequiredSint64: Boolean get() = presenceMask[5]

        override val hasRequiredFixed32: Boolean get() = presenceMask[6]

        override val hasRequiredFixed64: Boolean get() = presenceMask[7]

        override val hasRequiredSfixed32: Boolean get() = presenceMask[8]

        override val hasRequiredSfixed64: Boolean get() = presenceMask[9]

        override val hasRequiredFloat: Boolean get() = presenceMask[10]

        override val hasRequiredDouble: Boolean get() = presenceMask[11]

        override val hasRequiredBool: Boolean get() = presenceMask[12]

        override val hasRequiredString: Boolean get() = presenceMask[13]

        override val hasRequiredBytes: Boolean get() = presenceMask[14]

        override val hasRequiredNestedMessage: Boolean get() = presenceMask[15]

        override val hasRequiredForeignMessage: Boolean get() = presenceMask[16]

        override val hasRequiredNestedEnum: Boolean get() = presenceMask[17]

        override val hasRequiredForeignEnum: Boolean get() = presenceMask[18]

        override val hasRequiredStringPiece: Boolean get() = presenceMask[19]

        override val hasRequiredCord: Boolean get() = presenceMask[20]

        override val hasRecursiveMessage: Boolean get() = presenceMask[21]

        override val hasOptionalRecursiveMessage: Boolean get() = presenceMask[22]

        override val hasData: Boolean get() = presenceMask[23]

        override val hasDefaultInt32: Boolean get() = presenceMask[24]

        override val hasDefaultInt64: Boolean get() = presenceMask[25]

        override val hasDefaultUint32: Boolean get() = presenceMask[26]

        override val hasDefaultUint64: Boolean get() = presenceMask[27]

        override val hasDefaultSint32: Boolean get() = presenceMask[28]

        override val hasDefaultSint64: Boolean get() = presenceMask[29]

        override val hasDefaultFixed32: Boolean get() = presenceMask[30]

        override val hasDefaultFixed64: Boolean get() = presenceMask[31]

        override val hasDefaultSfixed32: Boolean get() = presenceMask[32]

        override val hasDefaultSfixed64: Boolean get() = presenceMask[33]

        override val hasDefaultFloat: Boolean get() = presenceMask[34]

        override val hasDefaultDouble: Boolean get() = presenceMask[35]

        override val hasDefaultBool: Boolean get() = presenceMask[36]

        override val hasDefaultString: Boolean get() = presenceMask[37]

        override val hasDefaultBytes: Boolean get() = presenceMask[38]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) this.requiredInt32.hashCode() else 0
        result = 31 * result + if (presenceMask[1]) this.requiredInt64.hashCode() else 0
        result = 31 * result + if (presenceMask[2]) this.requiredUint32.hashCode() else 0
        result = 31 * result + if (presenceMask[3]) this.requiredUint64.hashCode() else 0
        result = 31 * result + if (presenceMask[4]) this.requiredSint32.hashCode() else 0
        result = 31 * result + if (presenceMask[5]) this.requiredSint64.hashCode() else 0
        result = 31 * result + if (presenceMask[6]) this.requiredFixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[7]) this.requiredFixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[8]) this.requiredSfixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[9]) this.requiredSfixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[10]) this.requiredFloat.toBits().hashCode() else 0
        result = 31 * result + if (presenceMask[11]) this.requiredDouble.toBits().hashCode() else 0
        result = 31 * result + if (presenceMask[12]) this.requiredBool.hashCode() else 0
        result = 31 * result + if (presenceMask[13]) this.requiredString.hashCode() else 0
        result = 31 * result + if (presenceMask[14]) this.requiredBytes.hashCode() else 0
        result = 31 * result + if (presenceMask[15]) this.requiredNestedMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[16]) this.requiredForeignMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[17]) this.requiredNestedEnum.hashCode() else 0
        result = 31 * result + if (presenceMask[18]) this.requiredForeignEnum.hashCode() else 0
        result = 31 * result + if (presenceMask[19]) this.requiredStringPiece.hashCode() else 0
        result = 31 * result + if (presenceMask[20]) this.requiredCord.hashCode() else 0
        result = 31 * result + if (presenceMask[21]) this.recursiveMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[22]) this.optionalRecursiveMessage.hashCode() else 0
        result = 31 * result + if (presenceMask[23]) this.data.hashCode() else 0
        result = 31 * result + if (presenceMask[24]) this.defaultInt32.hashCode() else 0
        result = 31 * result + if (presenceMask[25]) this.defaultInt64.hashCode() else 0
        result = 31 * result + if (presenceMask[26]) this.defaultUint32.hashCode() else 0
        result = 31 * result + if (presenceMask[27]) this.defaultUint64.hashCode() else 0
        result = 31 * result + if (presenceMask[28]) this.defaultSint32.hashCode() else 0
        result = 31 * result + if (presenceMask[29]) this.defaultSint64.hashCode() else 0
        result = 31 * result + if (presenceMask[30]) this.defaultFixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[31]) this.defaultFixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[32]) this.defaultSfixed32.hashCode() else 0
        result = 31 * result + if (presenceMask[33]) this.defaultSfixed64.hashCode() else 0
        result = 31 * result + if (presenceMask[34]) this.defaultFloat.toBits().hashCode() else 0
        result = 31 * result + if (presenceMask[35]) this.defaultDouble.toBits().hashCode() else 0
        result = 31 * result + if (presenceMask[36]) this.defaultBool.hashCode() else 0
        result = 31 * result + if (presenceMask[37]) this.defaultString.hashCode() else 0
        result = 31 * result + if (presenceMask[38]) this.defaultBytes.hashCode() else 0
        result = 31 * result + extensionsHashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAllRequiredTypesProto2Internal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && this.requiredInt32 != other.requiredInt32) return false
        if (presenceMask[1] && this.requiredInt64 != other.requiredInt64) return false
        if (presenceMask[2] && this.requiredUint32 != other.requiredUint32) return false
        if (presenceMask[3] && this.requiredUint64 != other.requiredUint64) return false
        if (presenceMask[4] && this.requiredSint32 != other.requiredSint32) return false
        if (presenceMask[5] && this.requiredSint64 != other.requiredSint64) return false
        if (presenceMask[6] && this.requiredFixed32 != other.requiredFixed32) return false
        if (presenceMask[7] && this.requiredFixed64 != other.requiredFixed64) return false
        if (presenceMask[8] && this.requiredSfixed32 != other.requiredSfixed32) return false
        if (presenceMask[9] && this.requiredSfixed64 != other.requiredSfixed64) return false
        if (presenceMask[10] && this.requiredFloat.toBits() != other.requiredFloat.toBits()) return false
        if (presenceMask[11] && this.requiredDouble.toBits() != other.requiredDouble.toBits()) return false
        if (presenceMask[12] && this.requiredBool != other.requiredBool) return false
        if (presenceMask[13] && this.requiredString != other.requiredString) return false
        if (presenceMask[14] && this.requiredBytes != other.requiredBytes) return false
        if (presenceMask[15] && this.requiredNestedMessage != other.requiredNestedMessage) return false
        if (presenceMask[16] && this.requiredForeignMessage != other.requiredForeignMessage) return false
        if (presenceMask[17] && this.requiredNestedEnum != other.requiredNestedEnum) return false
        if (presenceMask[18] && this.requiredForeignEnum != other.requiredForeignEnum) return false
        if (presenceMask[19] && this.requiredStringPiece != other.requiredStringPiece) return false
        if (presenceMask[20] && this.requiredCord != other.requiredCord) return false
        if (presenceMask[21] && this.recursiveMessage != other.recursiveMessage) return false
        if (presenceMask[22] && this.optionalRecursiveMessage != other.optionalRecursiveMessage) return false
        if (presenceMask[23] && this.data != other.data) return false
        if (presenceMask[24] && this.defaultInt32 != other.defaultInt32) return false
        if (presenceMask[25] && this.defaultInt64 != other.defaultInt64) return false
        if (presenceMask[26] && this.defaultUint32 != other.defaultUint32) return false
        if (presenceMask[27] && this.defaultUint64 != other.defaultUint64) return false
        if (presenceMask[28] && this.defaultSint32 != other.defaultSint32) return false
        if (presenceMask[29] && this.defaultSint64 != other.defaultSint64) return false
        if (presenceMask[30] && this.defaultFixed32 != other.defaultFixed32) return false
        if (presenceMask[31] && this.defaultFixed64 != other.defaultFixed64) return false
        if (presenceMask[32] && this.defaultSfixed32 != other.defaultSfixed32) return false
        if (presenceMask[33] && this.defaultSfixed64 != other.defaultSfixed64) return false
        if (presenceMask[34] && this.defaultFloat.toBits() != other.defaultFloat.toBits()) return false
        if (presenceMask[35] && this.defaultDouble.toBits() != other.defaultDouble.toBits()) return false
        if (presenceMask[36] && this.defaultBool != other.defaultBool) return false
        if (presenceMask[37] && this.defaultString != other.defaultString) return false
        if (presenceMask[38] && this.defaultBytes != other.defaultBytes) return false
        if (!extensionsEqual(other)) return false
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
        builder.appendLine("TestAllRequiredTypesProto2(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}requiredInt32=${this.requiredInt32},")
        } else {
            builder.appendLine("${nextIndentString}requiredInt32=<unset>,")
        }

        if (presenceMask[1]) {
            builder.appendLine("${nextIndentString}requiredInt64=${this.requiredInt64},")
        } else {
            builder.appendLine("${nextIndentString}requiredInt64=<unset>,")
        }

        if (presenceMask[2]) {
            builder.appendLine("${nextIndentString}requiredUint32=${this.requiredUint32},")
        } else {
            builder.appendLine("${nextIndentString}requiredUint32=<unset>,")
        }

        if (presenceMask[3]) {
            builder.appendLine("${nextIndentString}requiredUint64=${this.requiredUint64},")
        } else {
            builder.appendLine("${nextIndentString}requiredUint64=<unset>,")
        }

        if (presenceMask[4]) {
            builder.appendLine("${nextIndentString}requiredSint32=${this.requiredSint32},")
        } else {
            builder.appendLine("${nextIndentString}requiredSint32=<unset>,")
        }

        if (presenceMask[5]) {
            builder.appendLine("${nextIndentString}requiredSint64=${this.requiredSint64},")
        } else {
            builder.appendLine("${nextIndentString}requiredSint64=<unset>,")
        }

        if (presenceMask[6]) {
            builder.appendLine("${nextIndentString}requiredFixed32=${this.requiredFixed32},")
        } else {
            builder.appendLine("${nextIndentString}requiredFixed32=<unset>,")
        }

        if (presenceMask[7]) {
            builder.appendLine("${nextIndentString}requiredFixed64=${this.requiredFixed64},")
        } else {
            builder.appendLine("${nextIndentString}requiredFixed64=<unset>,")
        }

        if (presenceMask[8]) {
            builder.appendLine("${nextIndentString}requiredSfixed32=${this.requiredSfixed32},")
        } else {
            builder.appendLine("${nextIndentString}requiredSfixed32=<unset>,")
        }

        if (presenceMask[9]) {
            builder.appendLine("${nextIndentString}requiredSfixed64=${this.requiredSfixed64},")
        } else {
            builder.appendLine("${nextIndentString}requiredSfixed64=<unset>,")
        }

        if (presenceMask[10]) {
            builder.appendLine("${nextIndentString}requiredFloat=${this.requiredFloat},")
        } else {
            builder.appendLine("${nextIndentString}requiredFloat=<unset>,")
        }

        if (presenceMask[11]) {
            builder.appendLine("${nextIndentString}requiredDouble=${this.requiredDouble},")
        } else {
            builder.appendLine("${nextIndentString}requiredDouble=<unset>,")
        }

        if (presenceMask[12]) {
            builder.appendLine("${nextIndentString}requiredBool=${this.requiredBool},")
        } else {
            builder.appendLine("${nextIndentString}requiredBool=<unset>,")
        }

        if (presenceMask[13]) {
            builder.appendLine("${nextIndentString}requiredString=${this.requiredString},")
        } else {
            builder.appendLine("${nextIndentString}requiredString=<unset>,")
        }

        if (presenceMask[14]) {
            builder.appendLine("${nextIndentString}requiredBytes=${this.requiredBytes.protoToString()},")
        } else {
            builder.appendLine("${nextIndentString}requiredBytes=<unset>,")
        }

        if (presenceMask[15]) {
            builder.appendLine("${nextIndentString}requiredNestedMessage=${this.requiredNestedMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}requiredNestedMessage=<unset>,")
        }

        if (presenceMask[16]) {
            builder.appendLine("${nextIndentString}requiredForeignMessage=${this.requiredForeignMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}requiredForeignMessage=<unset>,")
        }

        if (presenceMask[17]) {
            builder.appendLine("${nextIndentString}requiredNestedEnum=${this.requiredNestedEnum},")
        } else {
            builder.appendLine("${nextIndentString}requiredNestedEnum=<unset>,")
        }

        if (presenceMask[18]) {
            builder.appendLine("${nextIndentString}requiredForeignEnum=${this.requiredForeignEnum},")
        } else {
            builder.appendLine("${nextIndentString}requiredForeignEnum=<unset>,")
        }

        if (presenceMask[19]) {
            builder.appendLine("${nextIndentString}requiredStringPiece=${this.requiredStringPiece},")
        } else {
            builder.appendLine("${nextIndentString}requiredStringPiece=<unset>,")
        }

        if (presenceMask[20]) {
            builder.appendLine("${nextIndentString}requiredCord=${this.requiredCord},")
        } else {
            builder.appendLine("${nextIndentString}requiredCord=<unset>,")
        }

        if (presenceMask[21]) {
            builder.appendLine("${nextIndentString}recursiveMessage=${this.recursiveMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}recursiveMessage=<unset>,")
        }

        if (presenceMask[22]) {
            builder.appendLine("${nextIndentString}optionalRecursiveMessage=${this.optionalRecursiveMessage.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}optionalRecursiveMessage=<unset>,")
        }

        if (presenceMask[23]) {
            builder.appendLine("${nextIndentString}data=${this.data.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}data=<unset>,")
        }

        if (presenceMask[24]) {
            builder.appendLine("${nextIndentString}defaultInt32=${this.defaultInt32},")
        } else {
            builder.appendLine("${nextIndentString}defaultInt32=<unset>,")
        }

        if (presenceMask[25]) {
            builder.appendLine("${nextIndentString}defaultInt64=${this.defaultInt64},")
        } else {
            builder.appendLine("${nextIndentString}defaultInt64=<unset>,")
        }

        if (presenceMask[26]) {
            builder.appendLine("${nextIndentString}defaultUint32=${this.defaultUint32},")
        } else {
            builder.appendLine("${nextIndentString}defaultUint32=<unset>,")
        }

        if (presenceMask[27]) {
            builder.appendLine("${nextIndentString}defaultUint64=${this.defaultUint64},")
        } else {
            builder.appendLine("${nextIndentString}defaultUint64=<unset>,")
        }

        if (presenceMask[28]) {
            builder.appendLine("${nextIndentString}defaultSint32=${this.defaultSint32},")
        } else {
            builder.appendLine("${nextIndentString}defaultSint32=<unset>,")
        }

        if (presenceMask[29]) {
            builder.appendLine("${nextIndentString}defaultSint64=${this.defaultSint64},")
        } else {
            builder.appendLine("${nextIndentString}defaultSint64=<unset>,")
        }

        if (presenceMask[30]) {
            builder.appendLine("${nextIndentString}defaultFixed32=${this.defaultFixed32},")
        } else {
            builder.appendLine("${nextIndentString}defaultFixed32=<unset>,")
        }

        if (presenceMask[31]) {
            builder.appendLine("${nextIndentString}defaultFixed64=${this.defaultFixed64},")
        } else {
            builder.appendLine("${nextIndentString}defaultFixed64=<unset>,")
        }

        if (presenceMask[32]) {
            builder.appendLine("${nextIndentString}defaultSfixed32=${this.defaultSfixed32},")
        } else {
            builder.appendLine("${nextIndentString}defaultSfixed32=<unset>,")
        }

        if (presenceMask[33]) {
            builder.appendLine("${nextIndentString}defaultSfixed64=${this.defaultSfixed64},")
        } else {
            builder.appendLine("${nextIndentString}defaultSfixed64=<unset>,")
        }

        if (presenceMask[34]) {
            builder.appendLine("${nextIndentString}defaultFloat=${this.defaultFloat},")
        } else {
            builder.appendLine("${nextIndentString}defaultFloat=<unset>,")
        }

        if (presenceMask[35]) {
            builder.appendLine("${nextIndentString}defaultDouble=${this.defaultDouble},")
        } else {
            builder.appendLine("${nextIndentString}defaultDouble=<unset>,")
        }

        if (presenceMask[36]) {
            builder.appendLine("${nextIndentString}defaultBool=${this.defaultBool},")
        } else {
            builder.appendLine("${nextIndentString}defaultBool=<unset>,")
        }

        if (presenceMask[37]) {
            builder.appendLine("${nextIndentString}defaultString=${this.defaultString},")
        } else {
            builder.appendLine("${nextIndentString}defaultString=<unset>,")
        }

        if (presenceMask[38]) {
            builder.appendLine("${nextIndentString}defaultBytes=${this.defaultBytes.protoToString()},")
        } else {
            builder.appendLine("${nextIndentString}defaultBytes=<unset>,")
        }

        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): TestAllRequiredTypesProto2Internal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: TestAllRequiredTypesProto2Internal.() -> Unit): TestAllRequiredTypesProto2Internal {
        val copy = TestAllRequiredTypesProto2Internal()
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
            copy.requiredBytes = this.requiredBytes
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
            copy.defaultBytes = this.defaultBytes
        }

        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    class NestedMessageInternal: TestAllRequiredTypesProto2.NestedMessage.Builder, InternalMessage(fieldsWithPresence = 3) {
        private object PresenceIndices {
            const val a: Int = 0
            const val corecursive: Int = 1
            const val optionalCorecursive: Int = 2
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __aDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.a) { 0 }
        override var a: Int by __aDelegate
        internal val __corecursiveDelegate: MsgFieldDelegate<TestAllRequiredTypesProto2> = MsgFieldDelegate(PresenceIndices.corecursive) { TestAllRequiredTypesProto2Internal.DEFAULT }
        override var corecursive: TestAllRequiredTypesProto2 by __corecursiveDelegate
        internal val __optionalCorecursiveDelegate: MsgFieldDelegate<TestAllRequiredTypesProto2> = MsgFieldDelegate(PresenceIndices.optionalCorecursive) { TestAllRequiredTypesProto2Internal.DEFAULT }
        override var optionalCorecursive: TestAllRequiredTypesProto2 by __optionalCorecursiveDelegate

        private val _owner: NestedMessageInternal = this

        @InternalRpcApi
        val _presence: TestAllRequiredTypesProto2Presence.NestedMessage = object : TestAllRequiredTypesProto2Presence.NestedMessage, InternalPresenceObject {
            override val _message: NestedMessageInternal get() = _owner

            override val hasA: Boolean get() = presenceMask[0]

            override val hasCorecursive: Boolean get() = presenceMask[1]

            override val hasOptionalCorecursive: Boolean get() = presenceMask[2]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.a.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.corecursive.hashCode() else 0
            result = 31 * result + if (presenceMask[2]) this.optionalCorecursive.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as NestedMessageInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.a != other.a) return false
            if (presenceMask[1] && this.corecursive != other.corecursive) return false
            if (presenceMask[2] && this.optionalCorecursive != other.optionalCorecursive) return false
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
            builder.appendLine("TestAllRequiredTypesProto2.NestedMessage(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}a=${this.a},")
            } else {
                builder.appendLine("${nextIndentString}a=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}corecursive=${this.corecursive.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}corecursive=<unset>,")
            }

            if (presenceMask[2]) {
                builder.appendLine("${nextIndentString}optionalCorecursive=${this.optionalCorecursive.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}optionalCorecursive=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): NestedMessageInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: NestedMessageInternal.() -> Unit): NestedMessageInternal {
            val copy = NestedMessageInternal()
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

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllRequiredTypesProto2.NestedMessage> {
            override fun encode(value: TestAllRequiredTypesProto2.NestedMessage, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllRequiredTypesProto2.NestedMessage {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = NestedMessageInternal()
                    checkForPlatformDecodeException {
                        NestedMessageInternal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllRequiredTypesProto2.NestedMessage> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllRequiredTypesProto2.NestedMessage"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllRequiredTypesProto2.NestedMessage by lazy { NestedMessageInternal() }
        }
    }

    class DataInternal: TestAllRequiredTypesProto2.Data.Builder, InternalMessage(fieldsWithPresence = 2) {
        private object PresenceIndices {
            const val groupInt32: Int = 0
            const val groupUint32: Int = 1
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __groupInt32Delegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.groupInt32) { 0 }
        override var groupInt32: Int by __groupInt32Delegate
        internal val __groupUint32Delegate: MsgFieldDelegate<UInt> = MsgFieldDelegate(PresenceIndices.groupUint32) { 0u }
        override var groupUint32: UInt by __groupUint32Delegate

        private val _owner: DataInternal = this

        @InternalRpcApi
        val _presence: TestAllRequiredTypesProto2Presence.Data = object : TestAllRequiredTypesProto2Presence.Data, InternalPresenceObject {
            override val _message: DataInternal get() = _owner

            override val hasGroupInt32: Boolean get() = presenceMask[0]

            override val hasGroupUint32: Boolean get() = presenceMask[1]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.groupInt32.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) this.groupUint32.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as DataInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.groupInt32 != other.groupInt32) return false
            if (presenceMask[1] && this.groupUint32 != other.groupUint32) return false
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
            builder.appendLine("TestAllRequiredTypesProto2.Data(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}groupInt32=${this.groupInt32},")
            } else {
                builder.appendLine("${nextIndentString}groupInt32=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}groupUint32=${this.groupUint32},")
            } else {
                builder.appendLine("${nextIndentString}groupUint32=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): DataInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: DataInternal.() -> Unit): DataInternal {
            val copy = DataInternal()
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

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllRequiredTypesProto2.Data> {
            override fun encode(value: TestAllRequiredTypesProto2.Data, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllRequiredTypesProto2.Data {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = DataInternal()
                    checkForPlatformDecodeException {
                        DataInternal.decodeWith(msg, it, config as? ProtoConfig, null)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllRequiredTypesProto2.Data> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllRequiredTypesProto2.Data"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllRequiredTypesProto2.Data by lazy { DataInternal() }
        }
    }

    class MessageSetCorrectInternal: TestAllRequiredTypesProto2.MessageSetCorrect.Builder, InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        private val _owner: MessageSetCorrectInternal = this

        @InternalRpcApi
        val _presence: TestAllRequiredTypesProto2Presence.MessageSetCorrect = object : TestAllRequiredTypesProto2Presence.MessageSetCorrect, InternalPresenceObject {
            override val _message: MessageSetCorrectInternal get() = _owner
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = this::class.hashCode()
            result = 31 * result + extensionsHashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectInternal
            other.checkRequiredFields()
            if (!extensionsEqual(other)) return false
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
            builder.appendLine("TestAllRequiredTypesProto2.MessageSetCorrect(")
            builder.appendExtensions(nextIndentString)
            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MessageSetCorrectInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: MessageSetCorrectInternal.() -> Unit): MessageSetCorrectInternal {
            val copy = MessageSetCorrectInternal()
            copy.copyExtensionsFrom(this)
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllRequiredTypesProto2.MessageSetCorrect> {
            override fun encode(value: TestAllRequiredTypesProto2.MessageSetCorrect, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllRequiredTypesProto2.MessageSetCorrect {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = MessageSetCorrectInternal()
                    checkForPlatformDecodeException {
                        MessageSetCorrectInternal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllRequiredTypesProto2.MessageSetCorrect> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllRequiredTypesProto2.MessageSetCorrect by lazy { MessageSetCorrectInternal() }
        }
    }

    class MessageSetCorrectExtension1Internal: TestAllRequiredTypesProto2.MessageSetCorrectExtension1.Builder, InternalMessage(fieldsWithPresence = 1) {
        private object PresenceIndices {
            const val str: Int = 0
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __strDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.str) { "" }
        override var str: String by __strDelegate

        private val _owner: MessageSetCorrectExtension1Internal = this

        @InternalRpcApi
        val _presence: TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension1 = object : TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension1, InternalPresenceObject {
            override val _message: MessageSetCorrectExtension1Internal get() = _owner

            override val hasStr: Boolean get() = presenceMask[0]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.str.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectExtension1Internal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.str != other.str) return false
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
            builder.appendLine("TestAllRequiredTypesProto2.MessageSetCorrectExtension1(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}str=${this.str},")
            } else {
                builder.appendLine("${nextIndentString}str=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MessageSetCorrectExtension1Internal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: MessageSetCorrectExtension1Internal.() -> Unit): MessageSetCorrectExtension1Internal {
            val copy = MessageSetCorrectExtension1Internal()
            if (presenceMask[0]) {
                copy.str = this.str
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllRequiredTypesProto2.MessageSetCorrectExtension1> {
            override fun encode(value: TestAllRequiredTypesProto2.MessageSetCorrectExtension1, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllRequiredTypesProto2.MessageSetCorrectExtension1 {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = MessageSetCorrectExtension1Internal()
                    checkForPlatformDecodeException {
                        MessageSetCorrectExtension1Internal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllRequiredTypesProto2.MessageSetCorrectExtension1> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllRequiredTypesProto2.MessageSetCorrectExtension1 by lazy { MessageSetCorrectExtension1Internal() }
        }
    }

    class MessageSetCorrectExtension2Internal: TestAllRequiredTypesProto2.MessageSetCorrectExtension2.Builder, InternalMessage(fieldsWithPresence = 1) {
        private object PresenceIndices {
            const val i: Int = 0
        }

        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __iDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.i) { 0 }
        override var i: Int by __iDelegate

        private val _owner: MessageSetCorrectExtension2Internal = this

        @InternalRpcApi
        val _presence: TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension2 = object : TestAllRequiredTypesProto2Presence.MessageSetCorrectExtension2, InternalPresenceObject {
            override val _message: MessageSetCorrectExtension2Internal get() = _owner

            override val hasI: Boolean get() = presenceMask[0]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) this.i.hashCode() else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as MessageSetCorrectExtension2Internal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[0] && this.i != other.i) return false
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
            builder.appendLine("TestAllRequiredTypesProto2.MessageSetCorrectExtension2(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}i=${this.i},")
            } else {
                builder.appendLine("${nextIndentString}i=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): MessageSetCorrectExtension2Internal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: MessageSetCorrectExtension2Internal.() -> Unit): MessageSetCorrectExtension2Internal {
            val copy = MessageSetCorrectExtension2Internal()
            if (presenceMask[0]) {
                copy.i = this.i
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestAllRequiredTypesProto2.MessageSetCorrectExtension2> {
            override fun encode(value: TestAllRequiredTypesProto2.MessageSetCorrectExtension2, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllRequiredTypesProto2.MessageSetCorrectExtension2 {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = MessageSetCorrectExtension2Internal()
                    checkForPlatformDecodeException {
                        MessageSetCorrectExtension2Internal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllRequiredTypesProto2.MessageSetCorrectExtension2> {
            override val fullName: String = "protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestAllRequiredTypesProto2.MessageSetCorrectExtension2 by lazy { MessageSetCorrectExtension2Internal() }
        }
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<TestAllRequiredTypesProto2> {
        override fun encode(value: TestAllRequiredTypesProto2, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllRequiredTypesProto2 {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = TestAllRequiredTypesProto2Internal()
                checkForPlatformDecodeException {
                    TestAllRequiredTypesProto2Internal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<TestAllRequiredTypesProto2> {
        override val fullName: String = "protobuf_test_messages.proto2.TestAllRequiredTypesProto2"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: TestAllRequiredTypesProto2 by lazy { TestAllRequiredTypesProto2Internal() }
    }
}

class TestLargeOneofInternal: TestLargeOneof.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var largeOneof: TestLargeOneof.LargeOneof? = null

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = (this.largeOneof?.oneOfHashCode() ?: 0)
        return result
    }

    fun TestLargeOneof.LargeOneof.oneOfHashCode(): Int {
        val offset = when (this) {
            is TestLargeOneof.LargeOneof.A1 -> 0
            is TestLargeOneof.LargeOneof.A2 -> 1
            is TestLargeOneof.LargeOneof.A3 -> 2
            is TestLargeOneof.LargeOneof.A4 -> 3
            is TestLargeOneof.LargeOneof.A5 -> 4
        }

        return hashCode() + offset
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestLargeOneofInternal
        other.checkRequiredFields()
        if (this.largeOneof != other.largeOneof) return false
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
        builder.appendLine("TestLargeOneof(")
        builder.appendLine("${nextIndentString}largeOneof=${this.largeOneof},")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): TestLargeOneofInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: TestLargeOneofInternal.() -> Unit): TestLargeOneofInternal {
        val copy = TestLargeOneofInternal()
        copy.largeOneof = this.largeOneof?.oneOfCopy()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    fun TestLargeOneof.LargeOneof.oneOfCopy(): TestLargeOneof.LargeOneof {
        return when (this) {
            is TestLargeOneof.LargeOneof.A1 -> {
                TestLargeOneof.LargeOneof.A1(this.value.copy())
            }
            is TestLargeOneof.LargeOneof.A2 -> {
                TestLargeOneof.LargeOneof.A2(this.value.copy())
            }
            is TestLargeOneof.LargeOneof.A3 -> {
                TestLargeOneof.LargeOneof.A3(this.value.copy())
            }
            is TestLargeOneof.LargeOneof.A4 -> {
                TestLargeOneof.LargeOneof.A4(this.value.copy())
            }
            is TestLargeOneof.LargeOneof.A5 -> {
                TestLargeOneof.LargeOneof.A5(this.value.copy())
            }
        }
    }

    class A1Internal: TestLargeOneof.A1.Builder, InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = this::class.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A1Internal
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
            builder.appendLine("TestLargeOneof.A1(")
            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): A1Internal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: A1Internal.() -> Unit): A1Internal {
            val copy = A1Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestLargeOneof.A1> {
            override fun encode(value: TestLargeOneof.A1, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestLargeOneof.A1 {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = A1Internal()
                    checkForPlatformDecodeException {
                        A1Internal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestLargeOneof.A1> {
            override val fullName: String = "protobuf_test_messages.proto2.TestLargeOneof.A1"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestLargeOneof.A1 by lazy { A1Internal() }
        }
    }

    class A2Internal: TestLargeOneof.A2.Builder, InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = this::class.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A2Internal
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
            builder.appendLine("TestLargeOneof.A2(")
            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): A2Internal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: A2Internal.() -> Unit): A2Internal {
            val copy = A2Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestLargeOneof.A2> {
            override fun encode(value: TestLargeOneof.A2, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestLargeOneof.A2 {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = A2Internal()
                    checkForPlatformDecodeException {
                        A2Internal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestLargeOneof.A2> {
            override val fullName: String = "protobuf_test_messages.proto2.TestLargeOneof.A2"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestLargeOneof.A2 by lazy { A2Internal() }
        }
    }

    class A3Internal: TestLargeOneof.A3.Builder, InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = this::class.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A3Internal
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
            builder.appendLine("TestLargeOneof.A3(")
            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): A3Internal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: A3Internal.() -> Unit): A3Internal {
            val copy = A3Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestLargeOneof.A3> {
            override fun encode(value: TestLargeOneof.A3, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestLargeOneof.A3 {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = A3Internal()
                    checkForPlatformDecodeException {
                        A3Internal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestLargeOneof.A3> {
            override val fullName: String = "protobuf_test_messages.proto2.TestLargeOneof.A3"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestLargeOneof.A3 by lazy { A3Internal() }
        }
    }

    class A4Internal: TestLargeOneof.A4.Builder, InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = this::class.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A4Internal
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
            builder.appendLine("TestLargeOneof.A4(")
            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): A4Internal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: A4Internal.() -> Unit): A4Internal {
            val copy = A4Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestLargeOneof.A4> {
            override fun encode(value: TestLargeOneof.A4, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestLargeOneof.A4 {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = A4Internal()
                    checkForPlatformDecodeException {
                        A4Internal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestLargeOneof.A4> {
            override val fullName: String = "protobuf_test_messages.proto2.TestLargeOneof.A4"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestLargeOneof.A4 by lazy { A4Internal() }
        }
    }

    class A5Internal: TestLargeOneof.A5.Builder, InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = this::class.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as A5Internal
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
            builder.appendLine("TestLargeOneof.A5(")
            builder.append("${indentString})")
            return builder.toString()
        }

        override fun copyInternal(): A5Internal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: A5Internal.() -> Unit): A5Internal {
            val copy = A5Internal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        object MARSHALLER: GrpcMarshaller<TestLargeOneof.A5> {
            override fun encode(value: TestLargeOneof.A5, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestLargeOneof.A5 {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = A5Internal()
                    checkForPlatformDecodeException {
                        A5Internal.decodeWith(msg, it, config as? ProtoConfig)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestLargeOneof.A5> {
            override val fullName: String = "protobuf_test_messages.proto2.TestLargeOneof.A5"
        }

        @InternalRpcApi
        companion object {
            val DEFAULT: TestLargeOneof.A5 by lazy { A5Internal() }
        }
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<TestLargeOneof> {
        override fun encode(value: TestLargeOneof, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): TestLargeOneof {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = TestLargeOneofInternal()
                checkForPlatformDecodeException {
                    TestLargeOneofInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<TestLargeOneof> {
        override val fullName: String = "protobuf_test_messages.proto2.TestLargeOneof"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: TestLargeOneof by lazy { TestLargeOneofInternal() }
    }
}

@InternalRpcApi
fun TestAllTypesProto2Internal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[15]) {
        this.optionalNestedMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[16]) {
        this.optionalForeignMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[21]) {
        this.recursiveMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[22]) {
        this.data.asInternal().checkRequiredFields()
    }

    if (presenceMask[23]) {
        this.multiwordgroupfield.asInternal().checkRequiredFields()
    }

    if (presenceMask[57]) {
        this.messageSetCorrect.asInternal().checkRequiredFields()
    }

    this.oneofField?.also {
        when {
            it is TestAllTypesProto2.OneofField.OneofNestedMessage -> {
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

    this.mapInt32NestedMessage.values.forEach {
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
fun TestAllTypesProto2Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.optionalInt32?.also {
        encoder.writeInt32(fieldNr = 1, value = it)
    }

    this.optionalInt64?.also {
        encoder.writeInt64(fieldNr = 2, value = it)
    }

    this.optionalUint32?.also {
        encoder.writeUInt32(fieldNr = 3, value = it)
    }

    this.optionalUint64?.also {
        encoder.writeUInt64(fieldNr = 4, value = it)
    }

    this.optionalSint32?.also {
        encoder.writeSInt32(fieldNr = 5, value = it)
    }

    this.optionalSint64?.also {
        encoder.writeSInt64(fieldNr = 6, value = it)
    }

    this.optionalFixed32?.also {
        encoder.writeFixed32(fieldNr = 7, value = it)
    }

    this.optionalFixed64?.also {
        encoder.writeFixed64(fieldNr = 8, value = it)
    }

    this.optionalSfixed32?.also {
        encoder.writeSFixed32(fieldNr = 9, value = it)
    }

    this.optionalSfixed64?.also {
        encoder.writeSFixed64(fieldNr = 10, value = it)
    }

    this.optionalFloat?.also {
        encoder.writeFloat(fieldNr = 11, value = it)
    }

    this.optionalDouble?.also {
        encoder.writeDouble(fieldNr = 12, value = it)
    }

    this.optionalBool?.also {
        encoder.writeBool(fieldNr = 13, value = it)
    }

    this.optionalString?.also {
        encoder.writeString(fieldNr = 14, value = it)
    }

    this.optionalBytes?.also {
        encoder.writeBytes(fieldNr = 15, value = it)
    }

    if (presenceMask[15]) {
        encoder.writeMessage(fieldNr = 18, value = this.optionalNestedMessage.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[16]) {
        encoder.writeMessage(fieldNr = 19, value = this.optionalForeignMessage.asInternal()) { encodeWith(it, config) }
    }

    this.optionalNestedEnum?.also {
        encoder.writeEnum(fieldNr = 21, value = it.number)
    }

    this.optionalForeignEnum?.also {
        encoder.writeEnum(fieldNr = 22, value = it.number)
    }

    this.optionalStringPiece?.also {
        encoder.writeString(fieldNr = 24, value = it)
    }

    this.optionalCord?.also {
        encoder.writeString(fieldNr = 25, value = it)
    }

    if (presenceMask[21]) {
        encoder.writeMessage(fieldNr = 27, value = this.recursiveMessage.asInternal()) { encodeWith(it, config) }
    }

    if (this.repeatedInt32.isNotEmpty()) {
        this.repeatedInt32.forEach {
            encoder.writeInt32(31, it)
        }
    }

    if (this.repeatedInt64.isNotEmpty()) {
        this.repeatedInt64.forEach {
            encoder.writeInt64(32, it)
        }
    }

    if (this.repeatedUint32.isNotEmpty()) {
        this.repeatedUint32.forEach {
            encoder.writeUInt32(33, it)
        }
    }

    if (this.repeatedUint64.isNotEmpty()) {
        this.repeatedUint64.forEach {
            encoder.writeUInt64(34, it)
        }
    }

    if (this.repeatedSint32.isNotEmpty()) {
        this.repeatedSint32.forEach {
            encoder.writeSInt32(35, it)
        }
    }

    if (this.repeatedSint64.isNotEmpty()) {
        this.repeatedSint64.forEach {
            encoder.writeSInt64(36, it)
        }
    }

    if (this.repeatedFixed32.isNotEmpty()) {
        this.repeatedFixed32.forEach {
            encoder.writeFixed32(37, it)
        }
    }

    if (this.repeatedFixed64.isNotEmpty()) {
        this.repeatedFixed64.forEach {
            encoder.writeFixed64(38, it)
        }
    }

    if (this.repeatedSfixed32.isNotEmpty()) {
        this.repeatedSfixed32.forEach {
            encoder.writeSFixed32(39, it)
        }
    }

    if (this.repeatedSfixed64.isNotEmpty()) {
        this.repeatedSfixed64.forEach {
            encoder.writeSFixed64(40, it)
        }
    }

    if (this.repeatedFloat.isNotEmpty()) {
        this.repeatedFloat.forEach {
            encoder.writeFloat(41, it)
        }
    }

    if (this.repeatedDouble.isNotEmpty()) {
        this.repeatedDouble.forEach {
            encoder.writeDouble(42, it)
        }
    }

    if (this.repeatedBool.isNotEmpty()) {
        this.repeatedBool.forEach {
            encoder.writeBool(43, it)
        }
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
        this.repeatedNestedEnum.forEach {
            encoder.writeEnum(51, it.number)
        }
    }

    if (this.repeatedForeignEnum.isNotEmpty()) {
        this.repeatedForeignEnum.forEach {
            encoder.writeEnum(52, it.number)
        }
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
            TestAllTypesProto2Internal.MapInt32Int32EntryInternal().apply {
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
            TestAllTypesProto2Internal.MapInt64Int64EntryInternal().apply {
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
            TestAllTypesProto2Internal.MapUint32Uint32EntryInternal().apply {
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
            TestAllTypesProto2Internal.MapUint64Uint64EntryInternal().apply {
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
            TestAllTypesProto2Internal.MapSint32Sint32EntryInternal().apply {
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
            TestAllTypesProto2Internal.MapSint64Sint64EntryInternal().apply {
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
            TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal().apply {
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
            TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal().apply {
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
            TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal().apply {
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
            TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 65, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapInt32Bool.isNotEmpty()) {
        this.mapInt32Bool.forEach { kEntry ->
            TestAllTypesProto2Internal.MapInt32BoolEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 104, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapInt32Float.isNotEmpty()) {
        this.mapInt32Float.forEach { kEntry ->
            TestAllTypesProto2Internal.MapInt32FloatEntryInternal().apply {
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
            TestAllTypesProto2Internal.MapInt32DoubleEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 67, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapInt32NestedMessage.isNotEmpty()) {
        this.mapInt32NestedMessage.forEach { kEntry ->
            TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 103, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (this.mapBoolBool.isNotEmpty()) {
        this.mapBoolBool.forEach { kEntry ->
            TestAllTypesProto2Internal.MapBoolBoolEntryInternal().apply {
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
            TestAllTypesProto2Internal.MapStringStringEntryInternal().apply {
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
            TestAllTypesProto2Internal.MapStringBytesEntryInternal().apply {
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
            TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal().apply {
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
            TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal().apply {
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
            TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal().apply {
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
            TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 74, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (presenceMask[22]) {
        encoder.writeGroupMessage(fieldNr = 201, value = this.data.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[23]) {
        encoder.writeGroupMessage(fieldNr = 204, value = this.multiwordgroupfield.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[24]) {
        encoder.writeInt32(fieldNr = 241, value = this.defaultInt32)
    }

    if (presenceMask[25]) {
        encoder.writeInt64(fieldNr = 242, value = this.defaultInt64)
    }

    if (presenceMask[26]) {
        encoder.writeUInt32(fieldNr = 243, value = this.defaultUint32)
    }

    if (presenceMask[27]) {
        encoder.writeUInt64(fieldNr = 244, value = this.defaultUint64)
    }

    if (presenceMask[28]) {
        encoder.writeSInt32(fieldNr = 245, value = this.defaultSint32)
    }

    if (presenceMask[29]) {
        encoder.writeSInt64(fieldNr = 246, value = this.defaultSint64)
    }

    if (presenceMask[30]) {
        encoder.writeFixed32(fieldNr = 247, value = this.defaultFixed32)
    }

    if (presenceMask[31]) {
        encoder.writeFixed64(fieldNr = 248, value = this.defaultFixed64)
    }

    if (presenceMask[32]) {
        encoder.writeSFixed32(fieldNr = 249, value = this.defaultSfixed32)
    }

    if (presenceMask[33]) {
        encoder.writeSFixed64(fieldNr = 250, value = this.defaultSfixed64)
    }

    if (presenceMask[34]) {
        encoder.writeFloat(fieldNr = 251, value = this.defaultFloat)
    }

    if (presenceMask[35]) {
        encoder.writeDouble(fieldNr = 252, value = this.defaultDouble)
    }

    if (presenceMask[36]) {
        encoder.writeBool(fieldNr = 253, value = this.defaultBool)
    }

    if (presenceMask[37]) {
        encoder.writeString(fieldNr = 254, value = this.defaultString)
    }

    if (presenceMask[38]) {
        encoder.writeBytes(fieldNr = 255, value = this.defaultBytes)
    }

    this.fieldname1?.also {
        encoder.writeInt32(fieldNr = 401, value = it)
    }

    this.fieldName2?.also {
        encoder.writeInt32(fieldNr = 402, value = it)
    }

    this.FieldName3?.also {
        encoder.writeInt32(fieldNr = 403, value = it)
    }

    this.field_Name4_?.also {
        encoder.writeInt32(fieldNr = 404, value = it)
    }

    this.field0name5?.also {
        encoder.writeInt32(fieldNr = 405, value = it)
    }

    this.field_0Name6?.also {
        encoder.writeInt32(fieldNr = 406, value = it)
    }

    this.fieldName7?.also {
        encoder.writeInt32(fieldNr = 407, value = it)
    }

    this.FieldName8?.also {
        encoder.writeInt32(fieldNr = 408, value = it)
    }

    this.field_Name9?.also {
        encoder.writeInt32(fieldNr = 409, value = it)
    }

    this.Field_Name10?.also {
        encoder.writeInt32(fieldNr = 410, value = it)
    }

    this.FIELD_NAME11?.also {
        encoder.writeInt32(fieldNr = 411, value = it)
    }

    this.FIELDName12?.also {
        encoder.writeInt32(fieldNr = 412, value = it)
    }

    this._FieldName13?.also {
        encoder.writeInt32(fieldNr = 413, value = it)
    }

    this.__FieldName14?.also {
        encoder.writeInt32(fieldNr = 414, value = it)
    }

    this.field_Name15?.also {
        encoder.writeInt32(fieldNr = 415, value = it)
    }

    this.field__Name16?.also {
        encoder.writeInt32(fieldNr = 416, value = it)
    }

    this.fieldName17__?.also {
        encoder.writeInt32(fieldNr = 417, value = it)
    }

    this.FieldName18__?.also {
        encoder.writeInt32(fieldNr = 418, value = it)
    }

    if (presenceMask[57]) {
        encoder.writeMessage(fieldNr = 500, value = this.messageSetCorrect.asInternal()) { encodeWith(it, config) }
    }

    this.oneofField?.also {
        when (val value = it) {
            is TestAllTypesProto2.OneofField.OneofUint32 -> {
                encoder.writeUInt32(fieldNr = 111, value = value.value)
            }
            is TestAllTypesProto2.OneofField.OneofNestedMessage -> {
                encoder.writeMessage(fieldNr = 112, value = value.value.asInternal()) { encodeWith(it, config) }
            }
            is TestAllTypesProto2.OneofField.OneofString -> {
                encoder.writeString(fieldNr = 113, value = value.value)
            }
            is TestAllTypesProto2.OneofField.OneofBytes -> {
                encoder.writeBytes(fieldNr = 114, value = value.value)
            }
            is TestAllTypesProto2.OneofField.OneofBool -> {
                encoder.writeBool(fieldNr = 115, value = value.value)
            }
            is TestAllTypesProto2.OneofField.OneofUint64 -> {
                encoder.writeUInt64(fieldNr = 116, value = value.value)
            }
            is TestAllTypesProto2.OneofField.OneofFloat -> {
                encoder.writeFloat(fieldNr = 117, value = value.value)
            }
            is TestAllTypesProto2.OneofField.OneofDouble -> {
                encoder.writeDouble(fieldNr = 118, value = value.value)
            }
            is TestAllTypesProto2.OneofField.OneofEnum -> {
                encoder.writeEnum(fieldNr = 119, value = value.value.number)
            }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.Companion.decodeWith(msg: TestAllTypesProto2Internal, decoder: WireDecoder, config: ProtoConfig?) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(TestAllTypesProto2::class) ?: emptyMap()
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
                val target = msg.__optionalNestedMessageDelegate.getOrCreate(msg) { TestAllTypesProto2Internal.NestedMessageInternal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 19 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionalForeignMessageDelegate.getOrCreate(msg) { ForeignMessageProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> ForeignMessageProto2Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 21 && tag.wireType == WireType.VARINT -> {
                msg.optionalNestedEnum = TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 22 && tag.wireType == WireType.VARINT -> {
                msg.optionalForeignEnum = ForeignEnumProto2.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 24 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.optionalStringPiece = decoder.readString()
            }
            tag.fieldNr == 25 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.optionalCord = decoder.readString()
            }
            tag.fieldNr == 27 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__recursiveMessageDelegate.getOrCreate(msg) { TestAllTypesProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 31 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedInt32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt32())
            }
            tag.fieldNr == 31 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedInt32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt32()
                target.add(elem)
            }
            tag.fieldNr == 32 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedInt64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt64())
            }
            tag.fieldNr == 32 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedInt64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt64()
                target.add(elem)
            }
            tag.fieldNr == 33 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedUint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedUInt32())
            }
            tag.fieldNr == 33 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedUint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readUInt32()
                target.add(elem)
            }
            tag.fieldNr == 34 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedUint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedUInt64())
            }
            tag.fieldNr == 34 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedUint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readUInt64()
                target.add(elem)
            }
            tag.fieldNr == 35 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedSint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSInt32())
            }
            tag.fieldNr == 35 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedSint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSInt32()
                target.add(elem)
            }
            tag.fieldNr == 36 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedSint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSInt64())
            }
            tag.fieldNr == 36 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedSint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSInt64()
                target.add(elem)
            }
            tag.fieldNr == 37 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedFixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedFixed32())
            }
            tag.fieldNr == 37 && tag.wireType == WireType.FIXED32 -> {
                val target = msg.__repeatedFixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readFixed32()
                target.add(elem)
            }
            tag.fieldNr == 38 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedFixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedFixed64())
            }
            tag.fieldNr == 38 && tag.wireType == WireType.FIXED64 -> {
                val target = msg.__repeatedFixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readFixed64()
                target.add(elem)
            }
            tag.fieldNr == 39 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedSfixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSFixed32())
            }
            tag.fieldNr == 39 && tag.wireType == WireType.FIXED32 -> {
                val target = msg.__repeatedSfixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSFixed32()
                target.add(elem)
            }
            tag.fieldNr == 40 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedSfixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSFixed64())
            }
            tag.fieldNr == 40 && tag.wireType == WireType.FIXED64 -> {
                val target = msg.__repeatedSfixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSFixed64()
                target.add(elem)
            }
            tag.fieldNr == 41 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedFloatDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedFloat())
            }
            tag.fieldNr == 41 && tag.wireType == WireType.FIXED32 -> {
                val target = msg.__repeatedFloatDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readFloat()
                target.add(elem)
            }
            tag.fieldNr == 42 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedDoubleDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedDouble())
            }
            tag.fieldNr == 42 && tag.wireType == WireType.FIXED64 -> {
                val target = msg.__repeatedDoubleDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readDouble()
                target.add(elem)
            }
            tag.fieldNr == 43 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedBoolDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedBool())
            }
            tag.fieldNr == 43 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedBoolDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readBool()
                target.add(elem)
            }
            tag.fieldNr == 44 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedStringDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
            }
            tag.fieldNr == 45 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedBytesDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readBytes()
                target.add(elem)
            }
            tag.fieldNr == 48 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedNestedMessageDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = TestAllTypesProto2Internal.NestedMessageInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
            }
            tag.fieldNr == 49 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedForeignMessageDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = ForeignMessageProto2Internal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> ForeignMessageProto2Internal.decodeWith(msg, decoder, config) })
                target.add(elem)
            }
            tag.fieldNr == 51 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedNestedEnumDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedEnum().map { TestAllTypesProto2.NestedEnum.fromNumber(it) })
            }
            tag.fieldNr == 51 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedNestedEnumDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
                target.add(elem)
            }
            tag.fieldNr == 52 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedForeignEnumDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedEnum().map { ForeignEnumProto2.fromNumber(it) })
            }
            tag.fieldNr == 52 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedForeignEnumDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = ForeignEnumProto2.fromNumber(decoder.readEnum())
                target.add(elem)
            }
            tag.fieldNr == 54 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedStringPieceDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
            }
            tag.fieldNr == 55 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedCordDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
            }
            tag.fieldNr == 75 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedInt32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt32())
            }
            tag.fieldNr == 75 && tag.wireType == WireType.VARINT -> {
                val target = msg.__packedInt32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt32()
                target.add(elem)
            }
            tag.fieldNr == 76 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedInt64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt64())
            }
            tag.fieldNr == 76 && tag.wireType == WireType.VARINT -> {
                val target = msg.__packedInt64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt64()
                target.add(elem)
            }
            tag.fieldNr == 77 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedUint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedUInt32())
            }
            tag.fieldNr == 77 && tag.wireType == WireType.VARINT -> {
                val target = msg.__packedUint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readUInt32()
                target.add(elem)
            }
            tag.fieldNr == 78 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedUint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedUInt64())
            }
            tag.fieldNr == 78 && tag.wireType == WireType.VARINT -> {
                val target = msg.__packedUint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readUInt64()
                target.add(elem)
            }
            tag.fieldNr == 79 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedSint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSInt32())
            }
            tag.fieldNr == 79 && tag.wireType == WireType.VARINT -> {
                val target = msg.__packedSint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSInt32()
                target.add(elem)
            }
            tag.fieldNr == 80 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedSint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSInt64())
            }
            tag.fieldNr == 80 && tag.wireType == WireType.VARINT -> {
                val target = msg.__packedSint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSInt64()
                target.add(elem)
            }
            tag.fieldNr == 81 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedFixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedFixed32())
            }
            tag.fieldNr == 81 && tag.wireType == WireType.FIXED32 -> {
                val target = msg.__packedFixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readFixed32()
                target.add(elem)
            }
            tag.fieldNr == 82 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedFixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedFixed64())
            }
            tag.fieldNr == 82 && tag.wireType == WireType.FIXED64 -> {
                val target = msg.__packedFixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readFixed64()
                target.add(elem)
            }
            tag.fieldNr == 83 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedSfixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSFixed32())
            }
            tag.fieldNr == 83 && tag.wireType == WireType.FIXED32 -> {
                val target = msg.__packedSfixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSFixed32()
                target.add(elem)
            }
            tag.fieldNr == 84 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedSfixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSFixed64())
            }
            tag.fieldNr == 84 && tag.wireType == WireType.FIXED64 -> {
                val target = msg.__packedSfixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSFixed64()
                target.add(elem)
            }
            tag.fieldNr == 85 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedFloatDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedFloat())
            }
            tag.fieldNr == 85 && tag.wireType == WireType.FIXED32 -> {
                val target = msg.__packedFloatDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readFloat()
                target.add(elem)
            }
            tag.fieldNr == 86 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedDoubleDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedDouble())
            }
            tag.fieldNr == 86 && tag.wireType == WireType.FIXED64 -> {
                val target = msg.__packedDoubleDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readDouble()
                target.add(elem)
            }
            tag.fieldNr == 87 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedBoolDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedBool())
            }
            tag.fieldNr == 87 && tag.wireType == WireType.VARINT -> {
                val target = msg.__packedBoolDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readBool()
                target.add(elem)
            }
            tag.fieldNr == 88 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__packedNestedEnumDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedEnum().map { TestAllTypesProto2.NestedEnum.fromNumber(it) })
            }
            tag.fieldNr == 88 && tag.wireType == WireType.VARINT -> {
                val target = msg.__packedNestedEnumDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
                target.add(elem)
            }
            tag.fieldNr == 89 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedInt32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt32())
            }
            tag.fieldNr == 89 && tag.wireType == WireType.VARINT -> {
                val target = msg.__unpackedInt32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt32()
                target.add(elem)
            }
            tag.fieldNr == 90 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedInt64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt64())
            }
            tag.fieldNr == 90 && tag.wireType == WireType.VARINT -> {
                val target = msg.__unpackedInt64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt64()
                target.add(elem)
            }
            tag.fieldNr == 91 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedUint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedUInt32())
            }
            tag.fieldNr == 91 && tag.wireType == WireType.VARINT -> {
                val target = msg.__unpackedUint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readUInt32()
                target.add(elem)
            }
            tag.fieldNr == 92 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedUint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedUInt64())
            }
            tag.fieldNr == 92 && tag.wireType == WireType.VARINT -> {
                val target = msg.__unpackedUint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readUInt64()
                target.add(elem)
            }
            tag.fieldNr == 93 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedSint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSInt32())
            }
            tag.fieldNr == 93 && tag.wireType == WireType.VARINT -> {
                val target = msg.__unpackedSint32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSInt32()
                target.add(elem)
            }
            tag.fieldNr == 94 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedSint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSInt64())
            }
            tag.fieldNr == 94 && tag.wireType == WireType.VARINT -> {
                val target = msg.__unpackedSint64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSInt64()
                target.add(elem)
            }
            tag.fieldNr == 95 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedFixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedFixed32())
            }
            tag.fieldNr == 95 && tag.wireType == WireType.FIXED32 -> {
                val target = msg.__unpackedFixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readFixed32()
                target.add(elem)
            }
            tag.fieldNr == 96 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedFixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedFixed64())
            }
            tag.fieldNr == 96 && tag.wireType == WireType.FIXED64 -> {
                val target = msg.__unpackedFixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readFixed64()
                target.add(elem)
            }
            tag.fieldNr == 97 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedSfixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSFixed32())
            }
            tag.fieldNr == 97 && tag.wireType == WireType.FIXED32 -> {
                val target = msg.__unpackedSfixed32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSFixed32()
                target.add(elem)
            }
            tag.fieldNr == 98 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedSfixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedSFixed64())
            }
            tag.fieldNr == 98 && tag.wireType == WireType.FIXED64 -> {
                val target = msg.__unpackedSfixed64Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readSFixed64()
                target.add(elem)
            }
            tag.fieldNr == 99 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedFloatDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedFloat())
            }
            tag.fieldNr == 99 && tag.wireType == WireType.FIXED32 -> {
                val target = msg.__unpackedFloatDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readFloat()
                target.add(elem)
            }
            tag.fieldNr == 100 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedDoubleDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedDouble())
            }
            tag.fieldNr == 100 && tag.wireType == WireType.FIXED64 -> {
                val target = msg.__unpackedDoubleDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readDouble()
                target.add(elem)
            }
            tag.fieldNr == 101 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedBoolDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedBool())
            }
            tag.fieldNr == 101 && tag.wireType == WireType.VARINT -> {
                val target = msg.__unpackedBoolDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readBool()
                target.add(elem)
            }
            tag.fieldNr == 102 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__unpackedNestedEnumDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedEnum().map { TestAllTypesProto2.NestedEnum.fromNumber(it) })
            }
            tag.fieldNr == 102 && tag.wireType == WireType.VARINT -> {
                val target = msg.__unpackedNestedEnumDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
                target.add(elem)
            }
            tag.fieldNr == 56 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapInt32Int32Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapInt32Int32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapInt32Int32EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 57 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapInt64Int64Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapInt64Int64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapInt64Int64EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 58 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapUint32Uint32Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapUint32Uint32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 59 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapUint64Uint64Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapUint64Uint64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 60 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapSint32Sint32Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapSint32Sint32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 61 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapSint64Sint64Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapSint64Sint64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 62 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapFixed32Fixed32Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 63 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapFixed64Fixed64Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 64 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapSfixed32Sfixed32Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 65 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapSfixed64Sfixed64Delegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 104 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapInt32BoolDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapInt32BoolEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapInt32BoolEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 66 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapInt32FloatDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapInt32FloatEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapInt32FloatEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 67 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapInt32DoubleDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapInt32DoubleEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 103 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapInt32NestedMessageDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 68 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapBoolBoolDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapBoolBoolEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapBoolBoolEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 69 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapStringStringDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapStringStringEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapStringStringEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 70 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapStringBytesDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapStringBytesEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapStringBytesEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 71 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapStringNestedMessageDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 72 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapStringForeignMessageDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 73 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapStringNestedEnumDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 74 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mapStringForeignEnumDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.decodeWith(msg, decoder, config) })
                    target[key] = value
                }
            }
            tag.fieldNr == 201 && tag.wireType == WireType.START_GROUP -> {
                val target = msg.__dataDelegate.getOrCreate(msg) { TestAllTypesProto2Internal.DataInternal() }
                decoder.readGroup(target.asInternal()) { msg, decoder -> TestAllTypesProto2Internal.DataInternal.decodeWith(msg, decoder, config, tag) }
            }
            tag.fieldNr == 204 && tag.wireType == WireType.START_GROUP -> {
                val target = msg.__multiwordgroupfieldDelegate.getOrCreate(msg) { TestAllTypesProto2Internal.MultiWordGroupFieldInternal() }
                decoder.readGroup(target.asInternal()) { msg, decoder -> TestAllTypesProto2Internal.MultiWordGroupFieldInternal.decodeWith(msg, decoder, config, tag) }
            }
            tag.fieldNr == 241 && tag.wireType == WireType.VARINT -> {
                msg.defaultInt32 = decoder.readInt32()
            }
            tag.fieldNr == 242 && tag.wireType == WireType.VARINT -> {
                msg.defaultInt64 = decoder.readInt64()
            }
            tag.fieldNr == 243 && tag.wireType == WireType.VARINT -> {
                msg.defaultUint32 = decoder.readUInt32()
            }
            tag.fieldNr == 244 && tag.wireType == WireType.VARINT -> {
                msg.defaultUint64 = decoder.readUInt64()
            }
            tag.fieldNr == 245 && tag.wireType == WireType.VARINT -> {
                msg.defaultSint32 = decoder.readSInt32()
            }
            tag.fieldNr == 246 && tag.wireType == WireType.VARINT -> {
                msg.defaultSint64 = decoder.readSInt64()
            }
            tag.fieldNr == 247 && tag.wireType == WireType.FIXED32 -> {
                msg.defaultFixed32 = decoder.readFixed32()
            }
            tag.fieldNr == 248 && tag.wireType == WireType.FIXED64 -> {
                msg.defaultFixed64 = decoder.readFixed64()
            }
            tag.fieldNr == 249 && tag.wireType == WireType.FIXED32 -> {
                msg.defaultSfixed32 = decoder.readSFixed32()
            }
            tag.fieldNr == 250 && tag.wireType == WireType.FIXED64 -> {
                msg.defaultSfixed64 = decoder.readSFixed64()
            }
            tag.fieldNr == 251 && tag.wireType == WireType.FIXED32 -> {
                msg.defaultFloat = decoder.readFloat()
            }
            tag.fieldNr == 252 && tag.wireType == WireType.FIXED64 -> {
                msg.defaultDouble = decoder.readDouble()
            }
            tag.fieldNr == 253 && tag.wireType == WireType.VARINT -> {
                msg.defaultBool = decoder.readBool()
            }
            tag.fieldNr == 254 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.defaultString = decoder.readString()
            }
            tag.fieldNr == 255 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.defaultBytes = decoder.readBytes()
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
            tag.fieldNr == 500 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__messageSetCorrectDelegate.getOrCreate(msg) { TestAllTypesProto2Internal.MessageSetCorrectInternal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.MessageSetCorrectInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 111 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto2.OneofField.OneofUint32(decoder.readUInt32())
            }
            tag.fieldNr == 112 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.oneofField as? TestAllTypesProto2.OneofField.OneofNestedMessage) ?: TestAllTypesProto2.OneofField.OneofNestedMessage(TestAllTypesProto2Internal.NestedMessageInternal()).also {
                    msg.oneofField = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 113 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.oneofField = TestAllTypesProto2.OneofField.OneofString(decoder.readString())
            }
            tag.fieldNr == 114 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.oneofField = TestAllTypesProto2.OneofField.OneofBytes(decoder.readBytes())
            }
            tag.fieldNr == 115 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto2.OneofField.OneofBool(decoder.readBool())
            }
            tag.fieldNr == 116 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto2.OneofField.OneofUint64(decoder.readUInt64())
            }
            tag.fieldNr == 117 && tag.wireType == WireType.FIXED32 -> {
                msg.oneofField = TestAllTypesProto2.OneofField.OneofFloat(decoder.readFloat())
            }
            tag.fieldNr == 118 && tag.wireType == WireType.FIXED64 -> {
                msg.oneofField = TestAllTypesProto2.OneofField.OneofDouble(decoder.readDouble())
            }
            tag.fieldNr == 119 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto2.OneofField.OneofEnum(TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum()))
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.computeSize(): Int {
    var __result = 0
    this.optionalInt32?.also {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(it))
    }

    this.optionalInt64?.also {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int64(it))
    }

    this.optionalUint32?.also {
        __result += (WireSize.tag(3, WireType.VARINT) + WireSize.uInt32(it))
    }

    this.optionalUint64?.also {
        __result += (WireSize.tag(4, WireType.VARINT) + WireSize.uInt64(it))
    }

    this.optionalSint32?.also {
        __result += (WireSize.tag(5, WireType.VARINT) + WireSize.sInt32(it))
    }

    this.optionalSint64?.also {
        __result += (WireSize.tag(6, WireType.VARINT) + WireSize.sInt64(it))
    }

    this.optionalFixed32?.also {
        __result += (WireSize.tag(7, WireType.FIXED32) + WireSize.fixed32(it))
    }

    this.optionalFixed64?.also {
        __result += (WireSize.tag(8, WireType.FIXED64) + WireSize.fixed64(it))
    }

    this.optionalSfixed32?.also {
        __result += (WireSize.tag(9, WireType.FIXED32) + WireSize.sFixed32(it))
    }

    this.optionalSfixed64?.also {
        __result += (WireSize.tag(10, WireType.FIXED64) + WireSize.sFixed64(it))
    }

    this.optionalFloat?.also {
        __result += (WireSize.tag(11, WireType.FIXED32) + WireSize.float(it))
    }

    this.optionalDouble?.also {
        __result += (WireSize.tag(12, WireType.FIXED64) + WireSize.double(it))
    }

    this.optionalBool?.also {
        __result += (WireSize.tag(13, WireType.VARINT) + WireSize.bool(it))
    }

    this.optionalString?.also {
        __result += WireSize.string(it).let { WireSize.tag(14, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    this.optionalBytes?.also {
        __result += WireSize.bytes(it).let { WireSize.tag(15, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[15]) {
        __result += this.optionalNestedMessage.asInternal()._size.let { WireSize.tag(18, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[16]) {
        __result += this.optionalForeignMessage.asInternal()._size.let { WireSize.tag(19, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    this.optionalNestedEnum?.also {
        __result += (WireSize.tag(21, WireType.VARINT) + WireSize.enum(it.number))
    }

    this.optionalForeignEnum?.also {
        __result += (WireSize.tag(22, WireType.VARINT) + WireSize.enum(it.number))
    }

    this.optionalStringPiece?.also {
        __result += WireSize.string(it).let { WireSize.tag(24, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    this.optionalCord?.also {
        __result += WireSize.string(it).let { WireSize.tag(25, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[21]) {
        __result += this.recursiveMessage.asInternal()._size.let { WireSize.tag(27, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.repeatedInt32.isNotEmpty()) {
        __result += this.repeatedInt32.sumOf { WireSize.tag(31, WireType.VARINT) + WireSize.int32(it) }
    }

    if (this.repeatedInt64.isNotEmpty()) {
        __result += this.repeatedInt64.sumOf { WireSize.tag(32, WireType.VARINT) + WireSize.int64(it) }
    }

    if (this.repeatedUint32.isNotEmpty()) {
        __result += this.repeatedUint32.sumOf { WireSize.tag(33, WireType.VARINT) + WireSize.uInt32(it) }
    }

    if (this.repeatedUint64.isNotEmpty()) {
        __result += this.repeatedUint64.sumOf { WireSize.tag(34, WireType.VARINT) + WireSize.uInt64(it) }
    }

    if (this.repeatedSint32.isNotEmpty()) {
        __result += this.repeatedSint32.sumOf { WireSize.tag(35, WireType.VARINT) + WireSize.sInt32(it) }
    }

    if (this.repeatedSint64.isNotEmpty()) {
        __result += this.repeatedSint64.sumOf { WireSize.tag(36, WireType.VARINT) + WireSize.sInt64(it) }
    }

    if (this.repeatedFixed32.isNotEmpty()) {
        __result += this.repeatedFixed32.sumOf { WireSize.tag(37, WireType.FIXED32) + WireSize.fixed32(it) }
    }

    if (this.repeatedFixed64.isNotEmpty()) {
        __result += this.repeatedFixed64.sumOf { WireSize.tag(38, WireType.FIXED64) + WireSize.fixed64(it) }
    }

    if (this.repeatedSfixed32.isNotEmpty()) {
        __result += this.repeatedSfixed32.sumOf { WireSize.tag(39, WireType.FIXED32) + WireSize.sFixed32(it) }
    }

    if (this.repeatedSfixed64.isNotEmpty()) {
        __result += this.repeatedSfixed64.sumOf { WireSize.tag(40, WireType.FIXED64) + WireSize.sFixed64(it) }
    }

    if (this.repeatedFloat.isNotEmpty()) {
        __result += this.repeatedFloat.sumOf { WireSize.tag(41, WireType.FIXED32) + WireSize.float(it) }
    }

    if (this.repeatedDouble.isNotEmpty()) {
        __result += this.repeatedDouble.sumOf { WireSize.tag(42, WireType.FIXED64) + WireSize.double(it) }
    }

    if (this.repeatedBool.isNotEmpty()) {
        __result += this.repeatedBool.sumOf { WireSize.tag(43, WireType.VARINT) + WireSize.bool(it) }
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
        __result += this.repeatedNestedEnum.sumOf { WireSize.tag(51, WireType.VARINT) + WireSize.enum(it.number) }
    }

    if (this.repeatedForeignEnum.isNotEmpty()) {
        __result += this.repeatedForeignEnum.sumOf { WireSize.tag(52, WireType.VARINT) + WireSize.enum(it.number) }
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
        __result += this.mapInt32Int32.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapInt32Int32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(56, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt64Int64.isNotEmpty()) {
        __result += this.mapInt64Int64.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapInt64Int64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(57, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapUint32Uint32.isNotEmpty()) {
        __result += this.mapUint32Uint32.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapUint32Uint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(58, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapUint64Uint64.isNotEmpty()) {
        __result += this.mapUint64Uint64.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapUint64Uint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(59, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSint32Sint32.isNotEmpty()) {
        __result += this.mapSint32Sint32.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapSint32Sint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(60, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSint64Sint64.isNotEmpty()) {
        __result += this.mapSint64Sint64.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapSint64Sint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(61, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapFixed32Fixed32.isNotEmpty()) {
        __result += this.mapFixed32Fixed32.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(62, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapFixed64Fixed64.isNotEmpty()) {
        __result += this.mapFixed64Fixed64.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(63, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSfixed32Sfixed32.isNotEmpty()) {
        __result += this.mapSfixed32Sfixed32.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(64, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSfixed64Sfixed64.isNotEmpty()) {
        __result += this.mapSfixed64Sfixed64.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(65, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt32Bool.isNotEmpty()) {
        __result += this.mapInt32Bool.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapInt32BoolEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(104, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt32Float.isNotEmpty()) {
        __result += this.mapInt32Float.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapInt32FloatEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(66, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt32Double.isNotEmpty()) {
        __result += this.mapInt32Double.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapInt32DoubleEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(67, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt32NestedMessage.isNotEmpty()) {
        __result += this.mapInt32NestedMessage.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(103, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapBoolBool.isNotEmpty()) {
        __result += this.mapBoolBool.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapBoolBoolEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(68, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringString.isNotEmpty()) {
        __result += this.mapStringString.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapStringStringEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(69, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringBytes.isNotEmpty()) {
        __result += this.mapStringBytes.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapStringBytesEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(70, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringNestedMessage.isNotEmpty()) {
        __result += this.mapStringNestedMessage.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(71, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringForeignMessage.isNotEmpty()) {
        __result += this.mapStringForeignMessage.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(72, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringNestedEnum.isNotEmpty()) {
        __result += this.mapStringNestedEnum.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(73, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringForeignEnum.isNotEmpty()) {
        __result += this.mapStringForeignEnum.entries.sumOf { kEntry ->
            TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(74, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (presenceMask[22]) {
        __result += this.data.asInternal()._size.let { (2 * WireSize.tag(201, WireType.START_GROUP)) + it }
    }

    if (presenceMask[23]) {
        __result += this.multiwordgroupfield.asInternal()._size.let { (2 * WireSize.tag(204, WireType.START_GROUP)) + it }
    }

    if (presenceMask[24]) {
        __result += (WireSize.tag(241, WireType.VARINT) + WireSize.int32(this.defaultInt32))
    }

    if (presenceMask[25]) {
        __result += (WireSize.tag(242, WireType.VARINT) + WireSize.int64(this.defaultInt64))
    }

    if (presenceMask[26]) {
        __result += (WireSize.tag(243, WireType.VARINT) + WireSize.uInt32(this.defaultUint32))
    }

    if (presenceMask[27]) {
        __result += (WireSize.tag(244, WireType.VARINT) + WireSize.uInt64(this.defaultUint64))
    }

    if (presenceMask[28]) {
        __result += (WireSize.tag(245, WireType.VARINT) + WireSize.sInt32(this.defaultSint32))
    }

    if (presenceMask[29]) {
        __result += (WireSize.tag(246, WireType.VARINT) + WireSize.sInt64(this.defaultSint64))
    }

    if (presenceMask[30]) {
        __result += (WireSize.tag(247, WireType.FIXED32) + WireSize.fixed32(this.defaultFixed32))
    }

    if (presenceMask[31]) {
        __result += (WireSize.tag(248, WireType.FIXED64) + WireSize.fixed64(this.defaultFixed64))
    }

    if (presenceMask[32]) {
        __result += (WireSize.tag(249, WireType.FIXED32) + WireSize.sFixed32(this.defaultSfixed32))
    }

    if (presenceMask[33]) {
        __result += (WireSize.tag(250, WireType.FIXED64) + WireSize.sFixed64(this.defaultSfixed64))
    }

    if (presenceMask[34]) {
        __result += (WireSize.tag(251, WireType.FIXED32) + WireSize.float(this.defaultFloat))
    }

    if (presenceMask[35]) {
        __result += (WireSize.tag(252, WireType.FIXED64) + WireSize.double(this.defaultDouble))
    }

    if (presenceMask[36]) {
        __result += (WireSize.tag(253, WireType.VARINT) + WireSize.bool(this.defaultBool))
    }

    if (presenceMask[37]) {
        __result += WireSize.string(this.defaultString).let { WireSize.tag(254, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[38]) {
        __result += WireSize.bytes(this.defaultBytes).let { WireSize.tag(255, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    this.fieldname1?.also {
        __result += (WireSize.tag(401, WireType.VARINT) + WireSize.int32(it))
    }

    this.fieldName2?.also {
        __result += (WireSize.tag(402, WireType.VARINT) + WireSize.int32(it))
    }

    this.FieldName3?.also {
        __result += (WireSize.tag(403, WireType.VARINT) + WireSize.int32(it))
    }

    this.field_Name4_?.also {
        __result += (WireSize.tag(404, WireType.VARINT) + WireSize.int32(it))
    }

    this.field0name5?.also {
        __result += (WireSize.tag(405, WireType.VARINT) + WireSize.int32(it))
    }

    this.field_0Name6?.also {
        __result += (WireSize.tag(406, WireType.VARINT) + WireSize.int32(it))
    }

    this.fieldName7?.also {
        __result += (WireSize.tag(407, WireType.VARINT) + WireSize.int32(it))
    }

    this.FieldName8?.also {
        __result += (WireSize.tag(408, WireType.VARINT) + WireSize.int32(it))
    }

    this.field_Name9?.also {
        __result += (WireSize.tag(409, WireType.VARINT) + WireSize.int32(it))
    }

    this.Field_Name10?.also {
        __result += (WireSize.tag(410, WireType.VARINT) + WireSize.int32(it))
    }

    this.FIELD_NAME11?.also {
        __result += (WireSize.tag(411, WireType.VARINT) + WireSize.int32(it))
    }

    this.FIELDName12?.also {
        __result += (WireSize.tag(412, WireType.VARINT) + WireSize.int32(it))
    }

    this._FieldName13?.also {
        __result += (WireSize.tag(413, WireType.VARINT) + WireSize.int32(it))
    }

    this.__FieldName14?.also {
        __result += (WireSize.tag(414, WireType.VARINT) + WireSize.int32(it))
    }

    this.field_Name15?.also {
        __result += (WireSize.tag(415, WireType.VARINT) + WireSize.int32(it))
    }

    this.field__Name16?.also {
        __result += (WireSize.tag(416, WireType.VARINT) + WireSize.int32(it))
    }

    this.fieldName17__?.also {
        __result += (WireSize.tag(417, WireType.VARINT) + WireSize.int32(it))
    }

    this.FieldName18__?.also {
        __result += (WireSize.tag(418, WireType.VARINT) + WireSize.int32(it))
    }

    if (presenceMask[57]) {
        __result += this.messageSetCorrect.asInternal()._size.let { WireSize.tag(500, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    this.oneofField?.also {
        when (val value = it) {
            is TestAllTypesProto2.OneofField.OneofUint32 -> {
                __result += (WireSize.tag(111, WireType.VARINT) + WireSize.uInt32(value.value))
            }
            is TestAllTypesProto2.OneofField.OneofNestedMessage -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(112, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestAllTypesProto2.OneofField.OneofString -> {
                __result += WireSize.string(value.value).let { WireSize.tag(113, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestAllTypesProto2.OneofField.OneofBytes -> {
                __result += WireSize.bytes(value.value).let { WireSize.tag(114, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestAllTypesProto2.OneofField.OneofBool -> {
                __result += (WireSize.tag(115, WireType.VARINT) + WireSize.bool(value.value))
            }
            is TestAllTypesProto2.OneofField.OneofUint64 -> {
                __result += (WireSize.tag(116, WireType.VARINT) + WireSize.uInt64(value.value))
            }
            is TestAllTypesProto2.OneofField.OneofFloat -> {
                __result += (WireSize.tag(117, WireType.FIXED32) + WireSize.float(value.value))
            }
            is TestAllTypesProto2.OneofField.OneofDouble -> {
                __result += (WireSize.tag(118, WireType.FIXED64) + WireSize.double(value.value))
            }
            is TestAllTypesProto2.OneofField.OneofEnum -> {
                __result += (WireSize.tag(119, WireType.VARINT) + WireSize.enum(value.value.number))
            }
        }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2.asInternal(): TestAllTypesProto2Internal {
    return this as? TestAllTypesProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ForeignMessageProto2Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun ForeignMessageProto2Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.c?.also {
        encoder.writeInt32(fieldNr = 1, value = it)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun ForeignMessageProto2Internal.Companion.decodeWith(msg: ForeignMessageProto2Internal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun ForeignMessageProto2Internal.computeSize(): Int {
    var __result = 0
    this.c?.also {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(it))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun ForeignMessageProto2.asInternal(): ForeignMessageProto2Internal {
    return this as? ForeignMessageProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun GroupFieldInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun GroupFieldInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.groupInt32?.also {
        encoder.writeInt32(fieldNr = 122, value = it)
    }

    this.groupUint32?.also {
        encoder.writeUInt32(fieldNr = 123, value = it)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun GroupFieldInternal.Companion.decodeWith(msg: GroupFieldInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 122 && tag.wireType == WireType.VARINT -> {
                msg.groupInt32 = decoder.readInt32()
            }
            tag.fieldNr == 123 && tag.wireType == WireType.VARINT -> {
                msg.groupUint32 = decoder.readUInt32()
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun GroupFieldInternal.computeSize(): Int {
    var __result = 0
    this.groupInt32?.also {
        __result += (WireSize.tag(122, WireType.VARINT) + WireSize.int32(it))
    }

    this.groupUint32?.also {
        __result += (WireSize.tag(123, WireType.VARINT) + WireSize.uInt32(it))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun GroupField.asInternal(): GroupFieldInternal {
    return this as? GroupFieldInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun UnknownToTestAllTypesInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[2]) {
        this.nestedMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[3]) {
        this.optionalgroup.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun UnknownToTestAllTypesInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.optionalInt32?.also {
        encoder.writeInt32(fieldNr = 1001, value = it)
    }

    this.optionalString?.also {
        encoder.writeString(fieldNr = 1002, value = it)
    }

    if (presenceMask[2]) {
        encoder.writeMessage(fieldNr = 1003, value = this.nestedMessage.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[3]) {
        encoder.writeGroupMessage(fieldNr = 1004, value = this.optionalgroup.asInternal()) { encodeWith(it, config) }
    }

    this.optionalBool?.also {
        encoder.writeBool(fieldNr = 1006, value = it)
    }

    if (this.repeatedInt32.isNotEmpty()) {
        this.repeatedInt32.forEach {
            encoder.writeInt32(1011, it)
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun UnknownToTestAllTypesInternal.Companion.decodeWith(msg: UnknownToTestAllTypesInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1001 && tag.wireType == WireType.VARINT -> {
                msg.optionalInt32 = decoder.readInt32()
            }
            tag.fieldNr == 1002 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.optionalString = decoder.readString()
            }
            tag.fieldNr == 1003 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__nestedMessageDelegate.getOrCreate(msg) { ForeignMessageProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> ForeignMessageProto2Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 1004 && tag.wireType == WireType.START_GROUP -> {
                val target = msg.__optionalgroupDelegate.getOrCreate(msg) { UnknownToTestAllTypesInternal.OptionalGroupInternal() }
                decoder.readGroup(target.asInternal()) { msg, decoder -> UnknownToTestAllTypesInternal.OptionalGroupInternal.decodeWith(msg, decoder, config, tag) }
            }
            tag.fieldNr == 1006 && tag.wireType == WireType.VARINT -> {
                msg.optionalBool = decoder.readBool()
            }
            tag.fieldNr == 1011 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__repeatedInt32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt32())
            }
            tag.fieldNr == 1011 && tag.wireType == WireType.VARINT -> {
                val target = msg.__repeatedInt32Delegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt32()
                target.add(elem)
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun UnknownToTestAllTypesInternal.computeSize(): Int {
    var __result = 0
    this.optionalInt32?.also {
        __result += (WireSize.tag(1001, WireType.VARINT) + WireSize.int32(it))
    }

    this.optionalString?.also {
        __result += WireSize.string(it).let { WireSize.tag(1002, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[2]) {
        __result += this.nestedMessage.asInternal()._size.let { WireSize.tag(1003, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[3]) {
        __result += this.optionalgroup.asInternal()._size.let { (2 * WireSize.tag(1004, WireType.START_GROUP)) + it }
    }

    this.optionalBool?.also {
        __result += (WireSize.tag(1006, WireType.VARINT) + WireSize.bool(it))
    }

    if (this.repeatedInt32.isNotEmpty()) {
        __result += this.repeatedInt32.sumOf { WireSize.tag(1011, WireType.VARINT) + WireSize.int32(it) }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun UnknownToTestAllTypes.asInternal(): UnknownToTestAllTypesInternal {
    return this as? UnknownToTestAllTypesInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun NullHypothesisProto2Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun NullHypothesisProto2Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun NullHypothesisProto2Internal.Companion.decodeWith(msg: NullHypothesisProto2Internal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun NullHypothesisProto2Internal.computeSize(): Int {
    var __result = 0
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun NullHypothesisProto2.asInternal(): NullHypothesisProto2Internal {
    return this as? NullHypothesisProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun EnumOnlyProto2Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun EnumOnlyProto2Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun EnumOnlyProto2Internal.Companion.decodeWith(msg: EnumOnlyProto2Internal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun EnumOnlyProto2Internal.computeSize(): Int {
    var __result = 0
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun EnumOnlyProto2.asInternal(): EnumOnlyProto2Internal {
    return this as? EnumOnlyProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun OneStringProto2Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun OneStringProto2Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.data?.also {
        encoder.writeString(fieldNr = 1, value = it)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun OneStringProto2Internal.Companion.decodeWith(msg: OneStringProto2Internal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.data = decoder.readString()
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun OneStringProto2Internal.computeSize(): Int {
    var __result = 0
    this.data?.also {
        __result += WireSize.string(it).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun OneStringProto2.asInternal(): OneStringProto2Internal {
    return this as? OneStringProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ProtoWithKeywordsInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun ProtoWithKeywordsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.inline?.also {
        encoder.writeInt32(fieldNr = 1, value = it)
    }

    this.concept?.also {
        encoder.writeString(fieldNr = 2, value = it)
    }

    if (this.requires.isNotEmpty()) {
        this.requires.forEach {
            encoder.writeString(3, it)
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun ProtoWithKeywordsInternal.Companion.decodeWith(msg: ProtoWithKeywordsInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.inline = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.concept = decoder.readString()
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__requiresDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun ProtoWithKeywordsInternal.computeSize(): Int {
    var __result = 0
    this.inline?.also {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(it))
    }

    this.concept?.also {
        __result += WireSize.string(it).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.requires.isNotEmpty()) {
        __result += this.requires.sumOf { WireSize.string(it).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun ProtoWithKeywords.asInternal(): ProtoWithKeywordsInternal {
    return this as? ProtoWithKeywordsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.checkRequiredFields() {
    if (!presenceMask[0]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredInt32")
    }

    if (!presenceMask[1]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredInt64")
    }

    if (!presenceMask[2]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredUint32")
    }

    if (!presenceMask[3]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredUint64")
    }

    if (!presenceMask[4]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredSint32")
    }

    if (!presenceMask[5]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredSint64")
    }

    if (!presenceMask[6]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredFixed32")
    }

    if (!presenceMask[7]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredFixed64")
    }

    if (!presenceMask[8]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredSfixed32")
    }

    if (!presenceMask[9]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredSfixed64")
    }

    if (!presenceMask[10]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredFloat")
    }

    if (!presenceMask[11]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredDouble")
    }

    if (!presenceMask[12]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredBool")
    }

    if (!presenceMask[13]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredString")
    }

    if (!presenceMask[14]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredBytes")
    }

    if (!presenceMask[15]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredNestedMessage")
    }

    if (!presenceMask[16]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredForeignMessage")
    }

    if (!presenceMask[17]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredNestedEnum")
    }

    if (!presenceMask[18]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredForeignEnum")
    }

    if (!presenceMask[19]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredStringPiece")
    }

    if (!presenceMask[20]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "requiredCord")
    }

    if (!presenceMask[21]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "recursiveMessage")
    }

    if (!presenceMask[23]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "data")
    }

    if (!presenceMask[24]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultInt32")
    }

    if (!presenceMask[25]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultInt64")
    }

    if (!presenceMask[26]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultUint32")
    }

    if (!presenceMask[27]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultUint64")
    }

    if (!presenceMask[28]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultSint32")
    }

    if (!presenceMask[29]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultSint64")
    }

    if (!presenceMask[30]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultFixed32")
    }

    if (!presenceMask[31]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultFixed64")
    }

    if (!presenceMask[32]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultSfixed32")
    }

    if (!presenceMask[33]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultSfixed64")
    }

    if (!presenceMask[34]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultFloat")
    }

    if (!presenceMask[35]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultDouble")
    }

    if (!presenceMask[36]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultBool")
    }

    if (!presenceMask[37]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultString")
    }

    if (!presenceMask[38]) {
        throw ProtobufDecodingException.missingRequiredField("TestAllRequiredTypesProto2", "defaultBytes")
    }

    if (presenceMask[15]) {
        this.requiredNestedMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[16]) {
        this.requiredForeignMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[21]) {
        this.recursiveMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[22]) {
        this.optionalRecursiveMessage.asInternal().checkRequiredFields()
    }

    if (presenceMask[23]) {
        this.data.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt32(fieldNr = 1, value = this.requiredInt32)
    }

    if (presenceMask[1]) {
        encoder.writeInt64(fieldNr = 2, value = this.requiredInt64)
    }

    if (presenceMask[2]) {
        encoder.writeUInt32(fieldNr = 3, value = this.requiredUint32)
    }

    if (presenceMask[3]) {
        encoder.writeUInt64(fieldNr = 4, value = this.requiredUint64)
    }

    if (presenceMask[4]) {
        encoder.writeSInt32(fieldNr = 5, value = this.requiredSint32)
    }

    if (presenceMask[5]) {
        encoder.writeSInt64(fieldNr = 6, value = this.requiredSint64)
    }

    if (presenceMask[6]) {
        encoder.writeFixed32(fieldNr = 7, value = this.requiredFixed32)
    }

    if (presenceMask[7]) {
        encoder.writeFixed64(fieldNr = 8, value = this.requiredFixed64)
    }

    if (presenceMask[8]) {
        encoder.writeSFixed32(fieldNr = 9, value = this.requiredSfixed32)
    }

    if (presenceMask[9]) {
        encoder.writeSFixed64(fieldNr = 10, value = this.requiredSfixed64)
    }

    if (presenceMask[10]) {
        encoder.writeFloat(fieldNr = 11, value = this.requiredFloat)
    }

    if (presenceMask[11]) {
        encoder.writeDouble(fieldNr = 12, value = this.requiredDouble)
    }

    if (presenceMask[12]) {
        encoder.writeBool(fieldNr = 13, value = this.requiredBool)
    }

    if (presenceMask[13]) {
        encoder.writeString(fieldNr = 14, value = this.requiredString)
    }

    if (presenceMask[14]) {
        encoder.writeBytes(fieldNr = 15, value = this.requiredBytes)
    }

    if (presenceMask[15]) {
        encoder.writeMessage(fieldNr = 18, value = this.requiredNestedMessage.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[16]) {
        encoder.writeMessage(fieldNr = 19, value = this.requiredForeignMessage.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[17]) {
        encoder.writeEnum(fieldNr = 21, value = this.requiredNestedEnum.number)
    }

    if (presenceMask[18]) {
        encoder.writeEnum(fieldNr = 22, value = this.requiredForeignEnum.number)
    }

    if (presenceMask[19]) {
        encoder.writeString(fieldNr = 24, value = this.requiredStringPiece)
    }

    if (presenceMask[20]) {
        encoder.writeString(fieldNr = 25, value = this.requiredCord)
    }

    if (presenceMask[21]) {
        encoder.writeMessage(fieldNr = 27, value = this.recursiveMessage.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[22]) {
        encoder.writeMessage(fieldNr = 28, value = this.optionalRecursiveMessage.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[23]) {
        encoder.writeGroupMessage(fieldNr = 201, value = this.data.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[24]) {
        encoder.writeInt32(fieldNr = 241, value = this.defaultInt32)
    }

    if (presenceMask[25]) {
        encoder.writeInt64(fieldNr = 242, value = this.defaultInt64)
    }

    if (presenceMask[26]) {
        encoder.writeUInt32(fieldNr = 243, value = this.defaultUint32)
    }

    if (presenceMask[27]) {
        encoder.writeUInt64(fieldNr = 244, value = this.defaultUint64)
    }

    if (presenceMask[28]) {
        encoder.writeSInt32(fieldNr = 245, value = this.defaultSint32)
    }

    if (presenceMask[29]) {
        encoder.writeSInt64(fieldNr = 246, value = this.defaultSint64)
    }

    if (presenceMask[30]) {
        encoder.writeFixed32(fieldNr = 247, value = this.defaultFixed32)
    }

    if (presenceMask[31]) {
        encoder.writeFixed64(fieldNr = 248, value = this.defaultFixed64)
    }

    if (presenceMask[32]) {
        encoder.writeSFixed32(fieldNr = 249, value = this.defaultSfixed32)
    }

    if (presenceMask[33]) {
        encoder.writeSFixed64(fieldNr = 250, value = this.defaultSfixed64)
    }

    if (presenceMask[34]) {
        encoder.writeFloat(fieldNr = 251, value = this.defaultFloat)
    }

    if (presenceMask[35]) {
        encoder.writeDouble(fieldNr = 252, value = this.defaultDouble)
    }

    if (presenceMask[36]) {
        encoder.writeBool(fieldNr = 253, value = this.defaultBool)
    }

    if (presenceMask[37]) {
        encoder.writeString(fieldNr = 254, value = this.defaultString)
    }

    if (presenceMask[38]) {
        encoder.writeBytes(fieldNr = 255, value = this.defaultBytes)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.Companion.decodeWith(msg: TestAllRequiredTypesProto2Internal, decoder: WireDecoder, config: ProtoConfig?) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(TestAllRequiredTypesProto2::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.requiredInt32 = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.requiredInt64 = decoder.readInt64()
            }
            tag.fieldNr == 3 && tag.wireType == WireType.VARINT -> {
                msg.requiredUint32 = decoder.readUInt32()
            }
            tag.fieldNr == 4 && tag.wireType == WireType.VARINT -> {
                msg.requiredUint64 = decoder.readUInt64()
            }
            tag.fieldNr == 5 && tag.wireType == WireType.VARINT -> {
                msg.requiredSint32 = decoder.readSInt32()
            }
            tag.fieldNr == 6 && tag.wireType == WireType.VARINT -> {
                msg.requiredSint64 = decoder.readSInt64()
            }
            tag.fieldNr == 7 && tag.wireType == WireType.FIXED32 -> {
                msg.requiredFixed32 = decoder.readFixed32()
            }
            tag.fieldNr == 8 && tag.wireType == WireType.FIXED64 -> {
                msg.requiredFixed64 = decoder.readFixed64()
            }
            tag.fieldNr == 9 && tag.wireType == WireType.FIXED32 -> {
                msg.requiredSfixed32 = decoder.readSFixed32()
            }
            tag.fieldNr == 10 && tag.wireType == WireType.FIXED64 -> {
                msg.requiredSfixed64 = decoder.readSFixed64()
            }
            tag.fieldNr == 11 && tag.wireType == WireType.FIXED32 -> {
                msg.requiredFloat = decoder.readFloat()
            }
            tag.fieldNr == 12 && tag.wireType == WireType.FIXED64 -> {
                msg.requiredDouble = decoder.readDouble()
            }
            tag.fieldNr == 13 && tag.wireType == WireType.VARINT -> {
                msg.requiredBool = decoder.readBool()
            }
            tag.fieldNr == 14 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.requiredString = decoder.readString()
            }
            tag.fieldNr == 15 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.requiredBytes = decoder.readBytes()
            }
            tag.fieldNr == 18 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__requiredNestedMessageDelegate.getOrCreate(msg) { TestAllRequiredTypesProto2Internal.NestedMessageInternal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllRequiredTypesProto2Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 19 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__requiredForeignMessageDelegate.getOrCreate(msg) { ForeignMessageProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> ForeignMessageProto2Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 21 && tag.wireType == WireType.VARINT -> {
                msg.requiredNestedEnum = TestAllRequiredTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 22 && tag.wireType == WireType.VARINT -> {
                msg.requiredForeignEnum = ForeignEnumProto2.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 24 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.requiredStringPiece = decoder.readString()
            }
            tag.fieldNr == 25 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.requiredCord = decoder.readString()
            }
            tag.fieldNr == 27 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__recursiveMessageDelegate.getOrCreate(msg) { TestAllRequiredTypesProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllRequiredTypesProto2Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 28 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionalRecursiveMessageDelegate.getOrCreate(msg) { TestAllRequiredTypesProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllRequiredTypesProto2Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 201 && tag.wireType == WireType.START_GROUP -> {
                val target = msg.__dataDelegate.getOrCreate(msg) { TestAllRequiredTypesProto2Internal.DataInternal() }
                decoder.readGroup(target.asInternal()) { msg, decoder -> TestAllRequiredTypesProto2Internal.DataInternal.decodeWith(msg, decoder, config, tag) }
            }
            tag.fieldNr == 241 && tag.wireType == WireType.VARINT -> {
                msg.defaultInt32 = decoder.readInt32()
            }
            tag.fieldNr == 242 && tag.wireType == WireType.VARINT -> {
                msg.defaultInt64 = decoder.readInt64()
            }
            tag.fieldNr == 243 && tag.wireType == WireType.VARINT -> {
                msg.defaultUint32 = decoder.readUInt32()
            }
            tag.fieldNr == 244 && tag.wireType == WireType.VARINT -> {
                msg.defaultUint64 = decoder.readUInt64()
            }
            tag.fieldNr == 245 && tag.wireType == WireType.VARINT -> {
                msg.defaultSint32 = decoder.readSInt32()
            }
            tag.fieldNr == 246 && tag.wireType == WireType.VARINT -> {
                msg.defaultSint64 = decoder.readSInt64()
            }
            tag.fieldNr == 247 && tag.wireType == WireType.FIXED32 -> {
                msg.defaultFixed32 = decoder.readFixed32()
            }
            tag.fieldNr == 248 && tag.wireType == WireType.FIXED64 -> {
                msg.defaultFixed64 = decoder.readFixed64()
            }
            tag.fieldNr == 249 && tag.wireType == WireType.FIXED32 -> {
                msg.defaultSfixed32 = decoder.readSFixed32()
            }
            tag.fieldNr == 250 && tag.wireType == WireType.FIXED64 -> {
                msg.defaultSfixed64 = decoder.readSFixed64()
            }
            tag.fieldNr == 251 && tag.wireType == WireType.FIXED32 -> {
                msg.defaultFloat = decoder.readFloat()
            }
            tag.fieldNr == 252 && tag.wireType == WireType.FIXED64 -> {
                msg.defaultDouble = decoder.readDouble()
            }
            tag.fieldNr == 253 && tag.wireType == WireType.VARINT -> {
                msg.defaultBool = decoder.readBool()
            }
            tag.fieldNr == 254 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.defaultString = decoder.readString()
            }
            tag.fieldNr == 255 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.defaultBytes = decoder.readBytes()
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllRequiredTypesProto2Internal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.requiredInt32))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int64(this.requiredInt64))
    }

    if (presenceMask[2]) {
        __result += (WireSize.tag(3, WireType.VARINT) + WireSize.uInt32(this.requiredUint32))
    }

    if (presenceMask[3]) {
        __result += (WireSize.tag(4, WireType.VARINT) + WireSize.uInt64(this.requiredUint64))
    }

    if (presenceMask[4]) {
        __result += (WireSize.tag(5, WireType.VARINT) + WireSize.sInt32(this.requiredSint32))
    }

    if (presenceMask[5]) {
        __result += (WireSize.tag(6, WireType.VARINT) + WireSize.sInt64(this.requiredSint64))
    }

    if (presenceMask[6]) {
        __result += (WireSize.tag(7, WireType.FIXED32) + WireSize.fixed32(this.requiredFixed32))
    }

    if (presenceMask[7]) {
        __result += (WireSize.tag(8, WireType.FIXED64) + WireSize.fixed64(this.requiredFixed64))
    }

    if (presenceMask[8]) {
        __result += (WireSize.tag(9, WireType.FIXED32) + WireSize.sFixed32(this.requiredSfixed32))
    }

    if (presenceMask[9]) {
        __result += (WireSize.tag(10, WireType.FIXED64) + WireSize.sFixed64(this.requiredSfixed64))
    }

    if (presenceMask[10]) {
        __result += (WireSize.tag(11, WireType.FIXED32) + WireSize.float(this.requiredFloat))
    }

    if (presenceMask[11]) {
        __result += (WireSize.tag(12, WireType.FIXED64) + WireSize.double(this.requiredDouble))
    }

    if (presenceMask[12]) {
        __result += (WireSize.tag(13, WireType.VARINT) + WireSize.bool(this.requiredBool))
    }

    if (presenceMask[13]) {
        __result += WireSize.string(this.requiredString).let { WireSize.tag(14, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[14]) {
        __result += WireSize.bytes(this.requiredBytes).let { WireSize.tag(15, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[15]) {
        __result += this.requiredNestedMessage.asInternal()._size.let { WireSize.tag(18, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[16]) {
        __result += this.requiredForeignMessage.asInternal()._size.let { WireSize.tag(19, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[17]) {
        __result += (WireSize.tag(21, WireType.VARINT) + WireSize.enum(this.requiredNestedEnum.number))
    }

    if (presenceMask[18]) {
        __result += (WireSize.tag(22, WireType.VARINT) + WireSize.enum(this.requiredForeignEnum.number))
    }

    if (presenceMask[19]) {
        __result += WireSize.string(this.requiredStringPiece).let { WireSize.tag(24, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[20]) {
        __result += WireSize.string(this.requiredCord).let { WireSize.tag(25, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[21]) {
        __result += this.recursiveMessage.asInternal()._size.let { WireSize.tag(27, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[22]) {
        __result += this.optionalRecursiveMessage.asInternal()._size.let { WireSize.tag(28, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[23]) {
        __result += this.data.asInternal()._size.let { (2 * WireSize.tag(201, WireType.START_GROUP)) + it }
    }

    if (presenceMask[24]) {
        __result += (WireSize.tag(241, WireType.VARINT) + WireSize.int32(this.defaultInt32))
    }

    if (presenceMask[25]) {
        __result += (WireSize.tag(242, WireType.VARINT) + WireSize.int64(this.defaultInt64))
    }

    if (presenceMask[26]) {
        __result += (WireSize.tag(243, WireType.VARINT) + WireSize.uInt32(this.defaultUint32))
    }

    if (presenceMask[27]) {
        __result += (WireSize.tag(244, WireType.VARINT) + WireSize.uInt64(this.defaultUint64))
    }

    if (presenceMask[28]) {
        __result += (WireSize.tag(245, WireType.VARINT) + WireSize.sInt32(this.defaultSint32))
    }

    if (presenceMask[29]) {
        __result += (WireSize.tag(246, WireType.VARINT) + WireSize.sInt64(this.defaultSint64))
    }

    if (presenceMask[30]) {
        __result += (WireSize.tag(247, WireType.FIXED32) + WireSize.fixed32(this.defaultFixed32))
    }

    if (presenceMask[31]) {
        __result += (WireSize.tag(248, WireType.FIXED64) + WireSize.fixed64(this.defaultFixed64))
    }

    if (presenceMask[32]) {
        __result += (WireSize.tag(249, WireType.FIXED32) + WireSize.sFixed32(this.defaultSfixed32))
    }

    if (presenceMask[33]) {
        __result += (WireSize.tag(250, WireType.FIXED64) + WireSize.sFixed64(this.defaultSfixed64))
    }

    if (presenceMask[34]) {
        __result += (WireSize.tag(251, WireType.FIXED32) + WireSize.float(this.defaultFloat))
    }

    if (presenceMask[35]) {
        __result += (WireSize.tag(252, WireType.FIXED64) + WireSize.double(this.defaultDouble))
    }

    if (presenceMask[36]) {
        __result += (WireSize.tag(253, WireType.VARINT) + WireSize.bool(this.defaultBool))
    }

    if (presenceMask[37]) {
        __result += WireSize.string(this.defaultString).let { WireSize.tag(254, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[38]) {
        __result += WireSize.bytes(this.defaultBytes).let { WireSize.tag(255, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllRequiredTypesProto2.asInternal(): TestAllRequiredTypesProto2Internal {
    return this as? TestAllRequiredTypesProto2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestLargeOneofInternal.checkRequiredFields() {
    // no required fields to check
    this.largeOneof?.also {
        when {
            it is TestLargeOneof.LargeOneof.A1 -> {
                it.value.asInternal().checkRequiredFields()
            }
            it is TestLargeOneof.LargeOneof.A2 -> {
                it.value.asInternal().checkRequiredFields()
            }
            it is TestLargeOneof.LargeOneof.A3 -> {
                it.value.asInternal().checkRequiredFields()
            }
            it is TestLargeOneof.LargeOneof.A4 -> {
                it.value.asInternal().checkRequiredFields()
            }
            it is TestLargeOneof.LargeOneof.A5 -> {
                it.value.asInternal().checkRequiredFields()
            }
        }
    }
}

@InternalRpcApi
fun TestLargeOneofInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.largeOneof?.also {
        when (val value = it) {
            is TestLargeOneof.LargeOneof.A1 -> {
                encoder.writeMessage(fieldNr = 1, value = value.value.asInternal()) { encodeWith(it, config) }
            }
            is TestLargeOneof.LargeOneof.A2 -> {
                encoder.writeMessage(fieldNr = 2, value = value.value.asInternal()) { encodeWith(it, config) }
            }
            is TestLargeOneof.LargeOneof.A3 -> {
                encoder.writeMessage(fieldNr = 3, value = value.value.asInternal()) { encodeWith(it, config) }
            }
            is TestLargeOneof.LargeOneof.A4 -> {
                encoder.writeMessage(fieldNr = 4, value = value.value.asInternal()) { encodeWith(it, config) }
            }
            is TestLargeOneof.LargeOneof.A5 -> {
                encoder.writeMessage(fieldNr = 5, value = value.value.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestLargeOneofInternal.Companion.decodeWith(msg: TestLargeOneofInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.largeOneof as? TestLargeOneof.LargeOneof.A1) ?: TestLargeOneof.LargeOneof.A1(TestLargeOneofInternal.A1Internal()).also {
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> TestLargeOneofInternal.A1Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.largeOneof as? TestLargeOneof.LargeOneof.A2) ?: TestLargeOneof.LargeOneof.A2(TestLargeOneofInternal.A2Internal()).also {
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> TestLargeOneofInternal.A2Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.largeOneof as? TestLargeOneof.LargeOneof.A3) ?: TestLargeOneof.LargeOneof.A3(TestLargeOneofInternal.A3Internal()).also {
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> TestLargeOneofInternal.A3Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.largeOneof as? TestLargeOneof.LargeOneof.A4) ?: TestLargeOneof.LargeOneof.A4(TestLargeOneofInternal.A4Internal()).also {
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> TestLargeOneofInternal.A4Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 5 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.largeOneof as? TestLargeOneof.LargeOneof.A5) ?: TestLargeOneof.LargeOneof.A5(TestLargeOneofInternal.A5Internal()).also {
                    msg.largeOneof = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> TestLargeOneofInternal.A5Internal.decodeWith(msg, decoder, config) })
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestLargeOneofInternal.computeSize(): Int {
    var __result = 0
    this.largeOneof?.also {
        when (val value = it) {
            is TestLargeOneof.LargeOneof.A1 -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestLargeOneof.LargeOneof.A2 -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestLargeOneof.LargeOneof.A3 -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestLargeOneof.LargeOneof.A4 -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestLargeOneof.LargeOneof.A5 -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
        }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestLargeOneof.asInternal(): TestLargeOneofInternal {
    return this as? TestLargeOneofInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesProto2Internal.NestedMessageInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[1]) {
        this.corecursive.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto2Internal.NestedMessageInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.a?.also {
        encoder.writeInt32(fieldNr = 1, value = it)
    }

    if (presenceMask[1]) {
        encoder.writeMessage(fieldNr = 2, value = this.corecursive.asInternal()) { encodeWith(it, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.NestedMessageInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.NestedMessageInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.a = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__corecursiveDelegate.getOrCreate(msg) { TestAllTypesProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.decodeWith(msg, decoder, config) })
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.NestedMessageInternal.computeSize(): Int {
    var __result = 0
    this.a?.also {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(it))
    }

    if (presenceMask[1]) {
        __result += this.corecursive.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2.NestedMessage.asInternal(): TestAllTypesProto2Internal.NestedMessageInternal {
    return this as? TestAllTypesProto2Internal.NestedMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32Int32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32Int32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt32(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeInt32(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32Int32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapInt32Int32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapInt32Int32EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int32(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32Int32EntryInternal.asInternal(): TestAllTypesProto2Internal.MapInt32Int32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt64Int64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt64Int64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt64(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeInt64(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt64Int64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapInt64Int64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapInt64Int64EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int64(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int64(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt64Int64EntryInternal.asInternal(): TestAllTypesProto2Internal.MapInt64Int64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeUInt32(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeUInt32(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapUint32Uint32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt32(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.uInt32(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapUint32Uint32EntryInternal.asInternal(): TestAllTypesProto2Internal.MapUint32Uint32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeUInt64(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeUInt64(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapUint64Uint64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt64(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.uInt64(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapUint64Uint64EntryInternal.asInternal(): TestAllTypesProto2Internal.MapUint64Uint64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeSInt32(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeSInt32(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapSint32Sint32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.sInt32(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.sInt32(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSint32Sint32EntryInternal.asInternal(): TestAllTypesProto2Internal.MapSint32Sint32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeSInt64(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeSInt64(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapSint64Sint64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.sInt64(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.sInt64(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSint64Sint64EntryInternal.asInternal(): TestAllTypesProto2Internal.MapSint64Sint64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeFixed32(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeFixed32(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.FIXED32) + WireSize.fixed32(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.FIXED32) + WireSize.fixed32(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal.asInternal(): TestAllTypesProto2Internal.MapFixed32Fixed32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeFixed64(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeFixed64(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.FIXED64) + WireSize.fixed64(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.fixed64(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal.asInternal(): TestAllTypesProto2Internal.MapFixed64Fixed64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeSFixed32(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeSFixed32(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.FIXED32) + WireSize.sFixed32(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.FIXED32) + WireSize.sFixed32(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal.asInternal(): TestAllTypesProto2Internal.MapSfixed32Sfixed32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeSFixed64(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeSFixed64(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.FIXED64) + WireSize.sFixed64(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.sFixed64(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal.asInternal(): TestAllTypesProto2Internal.MapSfixed64Sfixed64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32BoolEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32BoolEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt32(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeBool(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32BoolEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapInt32BoolEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readInt32()
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapInt32BoolEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.bool(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32BoolEntryInternal.asInternal(): TestAllTypesProto2Internal.MapInt32BoolEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32FloatEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32FloatEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt32(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeFloat(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32FloatEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapInt32FloatEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapInt32FloatEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.FIXED32) + WireSize.float(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32FloatEntryInternal.asInternal(): TestAllTypesProto2Internal.MapInt32FloatEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt32(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeDouble(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapInt32DoubleEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.double(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32DoubleEntryInternal.asInternal(): TestAllTypesProto2Internal.MapInt32DoubleEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[1]) {
        this.value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt32(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeMessage(fieldNr = 2, value = this.value.asInternal()) { encodeWith(it, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.key = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__valueDelegate.getOrCreate(msg) { TestAllTypesProto2Internal.NestedMessageInternal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.key))
    }

    if (presenceMask[1]) {
        __result += this.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal.asInternal(): TestAllTypesProto2Internal.MapInt32NestedMessageEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapBoolBoolEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapBoolBoolEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeBool(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeBool(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapBoolBoolEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapBoolBoolEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapBoolBoolEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.bool(this.key))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.bool(this.value))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapBoolBoolEntryInternal.asInternal(): TestAllTypesProto2Internal.MapBoolBoolEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringStringEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringStringEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeString(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringStringEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapStringStringEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapStringStringEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[1]) {
        __result += WireSize.string(this.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringStringEntryInternal.asInternal(): TestAllTypesProto2Internal.MapStringStringEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringBytesEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringBytesEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeBytes(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringBytesEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapStringBytesEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapStringBytesEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[1]) {
        __result += WireSize.bytes(this.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringBytesEntryInternal.asInternal(): TestAllTypesProto2Internal.MapStringBytesEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[1]) {
        this.value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeMessage(fieldNr = 2, value = this.value.asInternal()) { encodeWith(it, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__valueDelegate.getOrCreate(msg) { TestAllTypesProto2Internal.NestedMessageInternal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllTypesProto2Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[1]) {
        __result += this.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal.asInternal(): TestAllTypesProto2Internal.MapStringNestedMessageEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[1]) {
        this.value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeMessage(fieldNr = 2, value = this.value.asInternal()) { encodeWith(it, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__valueDelegate.getOrCreate(msg) { ForeignMessageProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> ForeignMessageProto2Internal.decodeWith(msg, decoder, config) })
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[1]) {
        __result += this.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal.asInternal(): TestAllTypesProto2Internal.MapStringForeignMessageEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeEnum(fieldNr = 2, value = this.value.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = TestAllTypesProto2.NestedEnum.fromNumber(decoder.readEnum())
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.enum(this.value.number))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal.asInternal(): TestAllTypesProto2Internal.MapStringNestedEnumEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[1]) {
        encoder.writeEnum(fieldNr = 2, value = this.value.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = ForeignEnumProto2.fromNumber(decoder.readEnum())
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.enum(this.value.number))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal.asInternal(): TestAllTypesProto2Internal.MapStringForeignEnumEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesProto2Internal.DataInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.DataInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.groupInt32?.also {
        encoder.writeInt32(fieldNr = 202, value = it)
    }

    this.groupUint32?.also {
        encoder.writeUInt32(fieldNr = 203, value = it)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.DataInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.DataInternal, decoder: WireDecoder, config: ProtoConfig?, startGroup: KTag?) {
    while (true) {
        val tag = decoder.readTag() ?: run {
            startGroup?.let {
                throw ProtobufDecodingException("Missing END_GROUP tag for field: ${startGroup.fieldNr}.")
            }

            return
        }

        if (tag.wireType == WireType.END_GROUP) {
            if (tag.fieldNr != startGroup?.fieldNr) {
                throw ProtobufDecodingException("Wrong END_GROUP tag. Expected ${startGroup?.fieldNr}, got ${tag.fieldNr}.")
            }

            return
        }

        when {
            tag.fieldNr == 202 && tag.wireType == WireType.VARINT -> {
                msg.groupInt32 = decoder.readInt32()
            }
            tag.fieldNr == 203 && tag.wireType == WireType.VARINT -> {
                msg.groupUint32 = decoder.readUInt32()
            }
            else -> {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.DataInternal.computeSize(): Int {
    var __result = 0
    this.groupInt32?.also {
        __result += (WireSize.tag(202, WireType.VARINT) + WireSize.int32(it))
    }

    this.groupUint32?.also {
        __result += (WireSize.tag(203, WireType.VARINT) + WireSize.uInt32(it))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2.Data.asInternal(): TestAllTypesProto2Internal.DataInternal {
    return this as? TestAllTypesProto2Internal.DataInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MultiWordGroupFieldInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MultiWordGroupFieldInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.groupInt32?.also {
        encoder.writeInt32(fieldNr = 205, value = it)
    }

    this.groupUint32?.also {
        encoder.writeUInt32(fieldNr = 206, value = it)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MultiWordGroupFieldInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MultiWordGroupFieldInternal, decoder: WireDecoder, config: ProtoConfig?, startGroup: KTag?) {
    while (true) {
        val tag = decoder.readTag() ?: run {
            startGroup?.let {
                throw ProtobufDecodingException("Missing END_GROUP tag for field: ${startGroup.fieldNr}.")
            }

            return
        }

        if (tag.wireType == WireType.END_GROUP) {
            if (tag.fieldNr != startGroup?.fieldNr) {
                throw ProtobufDecodingException("Wrong END_GROUP tag. Expected ${startGroup?.fieldNr}, got ${tag.fieldNr}.")
            }

            return
        }

        when {
            tag.fieldNr == 205 && tag.wireType == WireType.VARINT -> {
                msg.groupInt32 = decoder.readInt32()
            }
            tag.fieldNr == 206 && tag.wireType == WireType.VARINT -> {
                msg.groupUint32 = decoder.readUInt32()
            }
            else -> {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MultiWordGroupFieldInternal.computeSize(): Int {
    var __result = 0
    this.groupInt32?.also {
        __result += (WireSize.tag(205, WireType.VARINT) + WireSize.int32(it))
    }

    this.groupUint32?.also {
        __result += (WireSize.tag(206, WireType.VARINT) + WireSize.uInt32(it))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2.MultiWordGroupField.asInternal(): TestAllTypesProto2Internal.MultiWordGroupFieldInternal {
    return this as? TestAllTypesProto2Internal.MultiWordGroupFieldInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MessageSetCorrectInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MessageSetCorrectInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MessageSetCorrectInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MessageSetCorrectInternal, decoder: WireDecoder, config: ProtoConfig?) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(TestAllTypesProto2.MessageSetCorrect::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MessageSetCorrectInternal.computeSize(): Int {
    var __result = 0
    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2.MessageSetCorrect.asInternal(): TestAllTypesProto2Internal.MessageSetCorrectInternal {
    return this as? TestAllTypesProto2Internal.MessageSetCorrectInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.str?.also {
        encoder.writeString(fieldNr = 25, value = it)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 25 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.str = decoder.readString()
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.computeSize(): Int {
    var __result = 0
    this.str?.also {
        __result += WireSize.string(it).let { WireSize.tag(25, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2.MessageSetCorrectExtension1.asInternal(): TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal {
    return this as? TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.i?.also {
        encoder.writeInt32(fieldNr = 9, value = it)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.Companion.decodeWith(msg: TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 9 && tag.wireType == WireType.VARINT -> {
                msg.i = decoder.readInt32()
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.computeSize(): Int {
    var __result = 0
    this.i?.also {
        __result += (WireSize.tag(9, WireType.VARINT) + WireSize.int32(it))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2.MessageSetCorrectExtension2.asInternal(): TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal {
    return this as? TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesProto2Internal.ExtensionWithOneofInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesProto2Internal.ExtensionWithOneofInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.oneofField?.also {
        when (val value = it) {
            is TestAllTypesProto2.ExtensionWithOneof.OneofField.A -> {
                encoder.writeInt32(fieldNr = 1, value = value.value)
            }
            is TestAllTypesProto2.ExtensionWithOneof.OneofField.B -> {
                encoder.writeInt32(fieldNr = 2, value = value.value)
            }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllTypesProto2Internal.ExtensionWithOneofInternal.Companion.decodeWith(msg: TestAllTypesProto2Internal.ExtensionWithOneofInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto2.ExtensionWithOneof.OneofField.A(decoder.readInt32())
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesProto2.ExtensionWithOneof.OneofField.B(decoder.readInt32())
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllTypesProto2Internal.ExtensionWithOneofInternal.computeSize(): Int {
    var __result = 0
    this.oneofField?.also {
        when (val value = it) {
            is TestAllTypesProto2.ExtensionWithOneof.OneofField.A -> {
                __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(value.value))
            }
            is TestAllTypesProto2.ExtensionWithOneof.OneofField.B -> {
                __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int32(value.value))
            }
        }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesProto2.ExtensionWithOneof.asInternal(): TestAllTypesProto2Internal.ExtensionWithOneofInternal {
    return this as? TestAllTypesProto2Internal.ExtensionWithOneofInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun UnknownToTestAllTypesInternal.OptionalGroupInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun UnknownToTestAllTypesInternal.OptionalGroupInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.a?.also {
        encoder.writeInt32(fieldNr = 1, value = it)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun UnknownToTestAllTypesInternal.OptionalGroupInternal.Companion.decodeWith(msg: UnknownToTestAllTypesInternal.OptionalGroupInternal, decoder: WireDecoder, config: ProtoConfig?, startGroup: KTag?) {
    while (true) {
        val tag = decoder.readTag() ?: run {
            startGroup?.let {
                throw ProtobufDecodingException("Missing END_GROUP tag for field: ${startGroup.fieldNr}.")
            }

            return
        }

        if (tag.wireType == WireType.END_GROUP) {
            if (tag.fieldNr != startGroup?.fieldNr) {
                throw ProtobufDecodingException("Wrong END_GROUP tag. Expected ${startGroup?.fieldNr}, got ${tag.fieldNr}.")
            }

            return
        }

        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.a = decoder.readInt32()
            }
            else -> {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun UnknownToTestAllTypesInternal.OptionalGroupInternal.computeSize(): Int {
    var __result = 0
    this.a?.also {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(it))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun UnknownToTestAllTypes.OptionalGroup.asInternal(): UnknownToTestAllTypesInternal.OptionalGroupInternal {
    return this as? UnknownToTestAllTypesInternal.OptionalGroupInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.NestedMessageInternal.checkRequiredFields() {
    if (!presenceMask[0]) {
        throw ProtobufDecodingException.missingRequiredField("NestedMessage", "a")
    }

    if (!presenceMask[1]) {
        throw ProtobufDecodingException.missingRequiredField("NestedMessage", "corecursive")
    }

    if (presenceMask[1]) {
        this.corecursive.asInternal().checkRequiredFields()
    }

    if (presenceMask[2]) {
        this.optionalCorecursive.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.NestedMessageInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt32(fieldNr = 1, value = this.a)
    }

    if (presenceMask[1]) {
        encoder.writeMessage(fieldNr = 2, value = this.corecursive.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[2]) {
        encoder.writeMessage(fieldNr = 3, value = this.optionalCorecursive.asInternal()) { encodeWith(it, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.NestedMessageInternal.Companion.decodeWith(msg: TestAllRequiredTypesProto2Internal.NestedMessageInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.a = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__corecursiveDelegate.getOrCreate(msg) { TestAllRequiredTypesProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllRequiredTypesProto2Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionalCorecursiveDelegate.getOrCreate(msg) { TestAllRequiredTypesProto2Internal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> TestAllRequiredTypesProto2Internal.decodeWith(msg, decoder, config) })
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllRequiredTypesProto2Internal.NestedMessageInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.a))
    }

    if (presenceMask[1]) {
        __result += this.corecursive.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[2]) {
        __result += this.optionalCorecursive.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllRequiredTypesProto2.NestedMessage.asInternal(): TestAllRequiredTypesProto2Internal.NestedMessageInternal {
    return this as? TestAllRequiredTypesProto2Internal.NestedMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.DataInternal.checkRequiredFields() {
    if (!presenceMask[0]) {
        throw ProtobufDecodingException.missingRequiredField("Data", "groupInt32")
    }

    if (!presenceMask[1]) {
        throw ProtobufDecodingException.missingRequiredField("Data", "groupUint32")
    }
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.DataInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt32(fieldNr = 202, value = this.groupInt32)
    }

    if (presenceMask[1]) {
        encoder.writeUInt32(fieldNr = 203, value = this.groupUint32)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.DataInternal.Companion.decodeWith(msg: TestAllRequiredTypesProto2Internal.DataInternal, decoder: WireDecoder, config: ProtoConfig?, startGroup: KTag?) {
    while (true) {
        val tag = decoder.readTag() ?: run {
            startGroup?.let {
                throw ProtobufDecodingException("Missing END_GROUP tag for field: ${startGroup.fieldNr}.")
            }

            return
        }

        if (tag.wireType == WireType.END_GROUP) {
            if (tag.fieldNr != startGroup?.fieldNr) {
                throw ProtobufDecodingException("Wrong END_GROUP tag. Expected ${startGroup?.fieldNr}, got ${tag.fieldNr}.")
            }

            return
        }

        when {
            tag.fieldNr == 202 && tag.wireType == WireType.VARINT -> {
                msg.groupInt32 = decoder.readInt32()
            }
            tag.fieldNr == 203 && tag.wireType == WireType.VARINT -> {
                msg.groupUint32 = decoder.readUInt32()
            }
            else -> {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllRequiredTypesProto2Internal.DataInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(202, WireType.VARINT) + WireSize.int32(this.groupInt32))
    }

    if (presenceMask[1]) {
        __result += (WireSize.tag(203, WireType.VARINT) + WireSize.uInt32(this.groupUint32))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllRequiredTypesProto2.Data.asInternal(): TestAllRequiredTypesProto2Internal.DataInternal {
    return this as? TestAllRequiredTypesProto2Internal.DataInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.Companion.decodeWith(msg: TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal, decoder: WireDecoder, config: ProtoConfig?) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(TestAllRequiredTypesProto2.MessageSetCorrect::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.computeSize(): Int {
    var __result = 0
    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllRequiredTypesProto2.MessageSetCorrect.asInternal(): TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal {
    return this as? TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.checkRequiredFields() {
    if (!presenceMask[0]) {
        throw ProtobufDecodingException.missingRequiredField("MessageSetCorrectExtension1", "str")
    }
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeString(fieldNr = 25, value = this.str)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.Companion.decodeWith(msg: TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 25 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.str = decoder.readString()
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += WireSize.string(this.str).let { WireSize.tag(25, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllRequiredTypesProto2.MessageSetCorrectExtension1.asInternal(): TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal {
    return this as? TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.checkRequiredFields() {
    if (!presenceMask[0]) {
        throw ProtobufDecodingException.missingRequiredField("MessageSetCorrectExtension2", "i")
    }
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[0]) {
        encoder.writeInt32(fieldNr = 9, value = this.i)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.Companion.decodeWith(msg: TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 9 && tag.wireType == WireType.VARINT -> {
                msg.i = decoder.readInt32()
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.computeSize(): Int {
    var __result = 0
    if (presenceMask[0]) {
        __result += (WireSize.tag(9, WireType.VARINT) + WireSize.int32(this.i))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllRequiredTypesProto2.MessageSetCorrectExtension2.asInternal(): TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal {
    return this as? TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestLargeOneofInternal.A1Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestLargeOneofInternal.A1Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestLargeOneofInternal.A1Internal.Companion.decodeWith(msg: TestLargeOneofInternal.A1Internal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestLargeOneofInternal.A1Internal.computeSize(): Int {
    var __result = 0
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestLargeOneof.A1.asInternal(): TestLargeOneofInternal.A1Internal {
    return this as? TestLargeOneofInternal.A1Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestLargeOneofInternal.A2Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestLargeOneofInternal.A2Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestLargeOneofInternal.A2Internal.Companion.decodeWith(msg: TestLargeOneofInternal.A2Internal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestLargeOneofInternal.A2Internal.computeSize(): Int {
    var __result = 0
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestLargeOneof.A2.asInternal(): TestLargeOneofInternal.A2Internal {
    return this as? TestLargeOneofInternal.A2Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestLargeOneofInternal.A3Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestLargeOneofInternal.A3Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestLargeOneofInternal.A3Internal.Companion.decodeWith(msg: TestLargeOneofInternal.A3Internal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestLargeOneofInternal.A3Internal.computeSize(): Int {
    var __result = 0
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestLargeOneof.A3.asInternal(): TestLargeOneofInternal.A3Internal {
    return this as? TestLargeOneofInternal.A3Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestLargeOneofInternal.A4Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestLargeOneofInternal.A4Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestLargeOneofInternal.A4Internal.Companion.decodeWith(msg: TestLargeOneofInternal.A4Internal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestLargeOneofInternal.A4Internal.computeSize(): Int {
    var __result = 0
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestLargeOneof.A4.asInternal(): TestLargeOneofInternal.A4Internal {
    return this as? TestLargeOneofInternal.A4Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestLargeOneofInternal.A5Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestLargeOneofInternal.A5Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestLargeOneofInternal.A5Internal.Companion.decodeWith(msg: TestLargeOneofInternal.A5Internal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun TestLargeOneofInternal.A5Internal.computeSize(): Int {
    var __result = 0
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestLargeOneof.A5.asInternal(): TestLargeOneofInternal.A5Internal {
    return this as? TestLargeOneofInternal.A5Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ForeignEnumProto2.Companion.fromNumber(number: Int): ForeignEnumProto2 {
    return when (number) {
        0 -> {
            ForeignEnumProto2.FOREIGN_FOO
        }
        1 -> {
            ForeignEnumProto2.FOREIGN_BAR
        }
        2 -> {
            ForeignEnumProto2.FOREIGN_BAZ
        }
        else -> {
            ForeignEnumProto2.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
fun TestAllTypesProto2.NestedEnum.Companion.fromNumber(number: Int): TestAllTypesProto2.NestedEnum {
    return when (number) {
        0 -> {
            TestAllTypesProto2.NestedEnum.FOO
        }
        1 -> {
            TestAllTypesProto2.NestedEnum.BAR
        }
        2 -> {
            TestAllTypesProto2.NestedEnum.BAZ
        }
        -1 -> {
            TestAllTypesProto2.NestedEnum.NEG
        }
        else -> {
            TestAllTypesProto2.NestedEnum.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
fun EnumOnlyProto2.Bool.Companion.fromNumber(number: Int): EnumOnlyProto2.Bool {
    return when (number) {
        0 -> {
            EnumOnlyProto2.Bool.kFalse
        }
        1 -> {
            EnumOnlyProto2.Bool.kTrue
        }
        else -> {
            EnumOnlyProto2.Bool.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
fun TestAllRequiredTypesProto2.NestedEnum.Companion.fromNumber(number: Int): TestAllRequiredTypesProto2.NestedEnum {
    return when (number) {
        0 -> {
            TestAllRequiredTypesProto2.NestedEnum.FOO
        }
        1 -> {
            TestAllRequiredTypesProto2.NestedEnum.BAR
        }
        2 -> {
            TestAllRequiredTypesProto2.NestedEnum.BAZ
        }
        -1 -> {
            TestAllRequiredTypesProto2.NestedEnum.NEG
        }
        else -> {
            TestAllRequiredTypesProto2.NestedEnum.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
object TestMessagesProto2KtExtensions {
    val extensionInt32: InternalExtensionDescriptor<com.google.protobuf_test_messages.proto2.TestAllTypesProto2,  Int> = 
        InternalExtensionDescriptor.int32(
            fieldNumber = 120,
            name = "extensionInt32",
            extendee = com.google.protobuf_test_messages.proto2.TestAllTypesProto2::class,
        )

    val extensionString: InternalExtensionDescriptor<com.google.protobuf_test_messages.proto2.TestAllTypesProto2,  String> = 
        InternalExtensionDescriptor.string(
            fieldNumber = 133,
            name = "extensionString",
            extendee = com.google.protobuf_test_messages.proto2.TestAllTypesProto2::class,
        )

    val extensionBytes: InternalExtensionDescriptor<com.google.protobuf_test_messages.proto2.TestAllTypesProto2,  ByteString> = 
        InternalExtensionDescriptor.bytes(
            fieldNumber = 134,
            name = "extensionBytes",
            extendee = com.google.protobuf_test_messages.proto2.TestAllTypesProto2::class,
        )

    val groupfield: InternalExtensionDescriptor<com.google.protobuf_test_messages.proto2.TestAllTypesProto2,  GroupField> = 
        InternalExtensionDescriptor.message(
            fieldNumber = 121,
            name = "groupfield",
            extendee = com.google.protobuf_test_messages.proto2.TestAllTypesProto2::class,
            valueType = GroupField::class,
            default = { GroupFieldInternal.DEFAULT },
            asInternal = { it.asInternal() },
            encodeWith = { value, encoder, config -> value.asInternal().encodeWith(encoder, config) },
            decodeWith = { value, decoder, config -> GroupFieldInternal.decodeWith(value.asInternal(), decoder, config) },
        )

    object TestAllTypesProto2 {
        object MessageSetCorrectExtension1 {
            val messageSetExtension: InternalExtensionDescriptor<com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect,  com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1> = 
                InternalExtensionDescriptor.message(
                    fieldNumber = 1547769,
                    name = "messageSetExtension",
                    extendee = com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect::class,
                    valueType = com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension1::class,
                    default = { TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.DEFAULT },
                    asInternal = { it.asInternal() },
                    encodeWith = { value, encoder, config -> value.asInternal().encodeWith(encoder, config) },
                    decodeWith = { value, decoder, config -> TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.decodeWith(value.asInternal(), decoder, config) },
                )
        }

        object MessageSetCorrectExtension2 {
            val messageSetExtension: InternalExtensionDescriptor<com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect,  com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2> = 
                InternalExtensionDescriptor.message(
                    fieldNumber = 4135312,
                    name = "messageSetExtension",
                    extendee = com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect::class,
                    valueType = com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrectExtension2::class,
                    default = { TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.DEFAULT },
                    asInternal = { it.asInternal() },
                    encodeWith = { value, encoder, config -> value.asInternal().encodeWith(encoder, config) },
                    decodeWith = { value, decoder, config -> TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.decodeWith(value.asInternal(), decoder, config) },
                )
        }

        object ExtensionWithOneof {
            val extensionWithOneof: InternalExtensionDescriptor<com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect,  com.google.protobuf_test_messages.proto2.TestAllTypesProto2.ExtensionWithOneof> = 
                InternalExtensionDescriptor.message(
                    fieldNumber = 123456789,
                    name = "extensionWithOneof",
                    extendee = com.google.protobuf_test_messages.proto2.TestAllTypesProto2.MessageSetCorrect::class,
                    valueType = com.google.protobuf_test_messages.proto2.TestAllTypesProto2.ExtensionWithOneof::class,
                    default = { TestAllTypesProto2Internal.ExtensionWithOneofInternal.DEFAULT },
                    asInternal = { it.asInternal() },
                    encodeWith = { value, encoder, config -> value.asInternal().encodeWith(encoder, config) },
                    decodeWith = { value, decoder, config -> TestAllTypesProto2Internal.ExtensionWithOneofInternal.decodeWith(value.asInternal(), decoder, config) },
                )
        }
    }

    object TestAllRequiredTypesProto2 {
        object MessageSetCorrectExtension1 {
            val messageSetExtension: InternalExtensionDescriptor<com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect,  com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1> = 
                InternalExtensionDescriptor.message(
                    fieldNumber = 1547769,
                    name = "messageSetExtension",
                    extendee = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect::class,
                    valueType = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1::class,
                    default = { TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.DEFAULT },
                    asInternal = { it.asInternal() },
                    encodeWith = { value, encoder, config -> value.asInternal().encodeWith(encoder, config) },
                    decodeWith = { value, decoder, config -> TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.decodeWith(value.asInternal(), decoder, config) },
                )
        }

        object MessageSetCorrectExtension2 {
            val messageSetExtension: InternalExtensionDescriptor<com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect,  com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2> = 
                InternalExtensionDescriptor.message(
                    fieldNumber = 4135312,
                    name = "messageSetExtension",
                    extendee = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrect::class,
                    valueType = com.google.protobuf_test_messages.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2::class,
                    default = { TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.DEFAULT },
                    asInternal = { it.asInternal() },
                    encodeWith = { value, encoder, config -> value.asInternal().encodeWith(encoder, config) },
                    decodeWith = { value, decoder, config -> TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.decodeWith(value.asInternal(), decoder, config) },
                )
        }
    }
}
