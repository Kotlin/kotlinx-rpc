@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.edition2023

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.internal.ExtensionValue
import kotlinx.rpc.protobuf.internal.InternalExtensionDescriptor
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.InternalPresenceObject
import kotlinx.rpc.protobuf.internal.KTag
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

class ComplexMessageInternal: ComplexMessage.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        const val d: Int = 0
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var d: Int? by MsgFieldDelegate(PresenceIndices.d) { null }

    private val _owner: ComplexMessageInternal = this

    @InternalRpcApi
    val _presence: ComplexMessagePresence = object : ComplexMessagePresence, InternalPresenceObject {
        override val _message: ComplexMessageInternal get() = _owner

        override val hasD: Boolean get() = presenceMask[0]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) (d?.hashCode() ?: 0) else 0
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ComplexMessageInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[0] && this.d != other.d) return false
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
        builder.appendLine("ComplexMessage(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}d=${this.d},")
        } else {
            builder.appendLine("${nextIndentString}d=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): ComplexMessageInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: ComplexMessageInternal.() -> Unit): ComplexMessageInternal {
        val copy = ComplexMessageInternal()
        if (presenceMask[0]) {
            copy.d = this.d
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<ComplexMessage> {
        override fun encode(value: ComplexMessage, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): ComplexMessage {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ComplexMessageInternal()
                checkForPlatformDecodeException {
                    ComplexMessageInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<ComplexMessage> {
        override val fullName: String = "protobuf_test_messages.editions.ComplexMessage"
    }

    @InternalRpcApi
    companion object
}

class TestAllTypesEdition2023Internal: TestAllTypesEdition2023.Builder, InternalMessage(fieldsWithPresence = 24) {
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
        const val groupliketype: Int = 22
        const val delimitedField: Int = 23
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
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
    override var optionalNestedMessage: TestAllTypesEdition2023.NestedMessage by MsgFieldDelegate(PresenceIndices.optionalNestedMessage) { NestedMessageInternal() }
    override var optionalForeignMessage: ForeignMessageEdition2023 by MsgFieldDelegate(PresenceIndices.optionalForeignMessage) { ForeignMessageEdition2023Internal() }
    override var optionalNestedEnum: TestAllTypesEdition2023.NestedEnum? by MsgFieldDelegate(PresenceIndices.optionalNestedEnum) { null }
    override var optionalForeignEnum: ForeignEnumEdition2023? by MsgFieldDelegate(PresenceIndices.optionalForeignEnum) { null }
    override var optionalStringPiece: String? by MsgFieldDelegate(PresenceIndices.optionalStringPiece) { null }
    override var optionalCord: String? by MsgFieldDelegate(PresenceIndices.optionalCord) { null }
    override var recursiveMessage: TestAllTypesEdition2023 by MsgFieldDelegate(PresenceIndices.recursiveMessage) { TestAllTypesEdition2023Internal() }
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
    override var repeatedNestedMessage: List<TestAllTypesEdition2023.NestedMessage> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignMessage: List<ForeignMessageEdition2023> by MsgFieldDelegate { mutableListOf() }
    override var repeatedNestedEnum: List<TestAllTypesEdition2023.NestedEnum> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignEnum: List<ForeignEnumEdition2023> by MsgFieldDelegate { mutableListOf() }
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
    override var packedNestedEnum: List<TestAllTypesEdition2023.NestedEnum> by MsgFieldDelegate { mutableListOf() }
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
    override var unpackedNestedEnum: List<TestAllTypesEdition2023.NestedEnum> by MsgFieldDelegate { mutableListOf() }
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
    override var mapStringNestedMessage: Map<String, TestAllTypesEdition2023.NestedMessage> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignMessage: Map<String, ForeignMessageEdition2023> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringNestedEnum: Map<String, TestAllTypesEdition2023.NestedEnum> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignEnum: Map<String, ForeignEnumEdition2023> by MsgFieldDelegate { mutableMapOf() }
    override var groupliketype: TestAllTypesEdition2023.GroupLikeType by MsgFieldDelegate(PresenceIndices.groupliketype) { GroupLikeTypeInternal() }
    override var delimitedField: TestAllTypesEdition2023.GroupLikeType by MsgFieldDelegate(PresenceIndices.delimitedField) { GroupLikeTypeInternal() }
    override var oneofField: TestAllTypesEdition2023.OneofField? = null

    private val _owner: TestAllTypesEdition2023Internal = this

    @InternalRpcApi
    val _presence: TestAllTypesEdition2023Presence = object : TestAllTypesEdition2023Presence, InternalPresenceObject {
        override val _message: TestAllTypesEdition2023Internal get() = _owner

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

        override val hasGroupliketype: Boolean get() = presenceMask[22]

        override val hasDelimitedField: Boolean get() = presenceMask[23]
    }

    override fun hashCode(): Int {
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
        result = 31 * result + if (presenceMask[10]) (optionalFloat?.toBits()?.hashCode() ?: 0) else 0
        result = 31 * result + if (presenceMask[11]) (optionalDouble?.toBits()?.hashCode() ?: 0) else 0
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
        result = 31 * result + repeatedBytes.fold(1) { acc, b -> 31 * acc + b.contentHashCode() }
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
        result = 31 * result + if (presenceMask[22]) groupliketype.hashCode() else 0
        result = 31 * result + if (presenceMask[23]) delimitedField.hashCode() else 0
        result = 31 * result + (oneofField?.oneOfHashCode() ?: 0)
        result = 31 * result + extensionsHashCode()
        return result
    }

    fun TestAllTypesEdition2023.OneofField.oneOfHashCode(): Int {
        return when (this) {
            is TestAllTypesEdition2023.OneofField.OneofUint32 -> hashCode() + 0
            is TestAllTypesEdition2023.OneofField.OneofNestedMessage -> hashCode() + 1
            is TestAllTypesEdition2023.OneofField.OneofString -> hashCode() + 2
            is TestAllTypesEdition2023.OneofField.OneofBytes -> value.contentHashCode() + 3
            is TestAllTypesEdition2023.OneofField.OneofBool -> hashCode() + 4
            is TestAllTypesEdition2023.OneofField.OneofUint64 -> hashCode() + 5
            is TestAllTypesEdition2023.OneofField.OneofFloat -> value.toBits().hashCode() + 6
            is TestAllTypesEdition2023.OneofField.OneofDouble -> value.toBits().hashCode() + 7
            is TestAllTypesEdition2023.OneofField.OneofEnum -> hashCode() + 8
        }
    }

    fun oneOfEquals(a: TestAllTypesEdition2023.OneofField?, b: TestAllTypesEdition2023.OneofField?): Boolean {
        if (a === b) return true
        if (a == null || b == null) return false
        if (a::class != b::class) return false
        return when (a) {
            is TestAllTypesEdition2023.OneofField.OneofUint32 -> a == b
            is TestAllTypesEdition2023.OneofField.OneofNestedMessage -> a == b
            is TestAllTypesEdition2023.OneofField.OneofString -> a == b
            is TestAllTypesEdition2023.OneofField.OneofBytes -> a.value.contentEquals((b as TestAllTypesEdition2023.OneofField.OneofBytes).value)
            is TestAllTypesEdition2023.OneofField.OneofBool -> a == b
            is TestAllTypesEdition2023.OneofField.OneofUint64 -> a == b
            is TestAllTypesEdition2023.OneofField.OneofFloat -> a.value.toBits() == (b as TestAllTypesEdition2023.OneofField.OneofFloat).value.toBits()
            is TestAllTypesEdition2023.OneofField.OneofDouble -> a.value.toBits() == (b as TestAllTypesEdition2023.OneofField.OneofDouble).value.toBits()
            is TestAllTypesEdition2023.OneofField.OneofEnum -> a == b
        }
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAllTypesEdition2023Internal
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
        if (presenceMask[14] && ((this.optionalBytes != null && (other.optionalBytes == null || !this.optionalBytes!!.contentEquals(other.optionalBytes!!))) || this.optionalBytes == null)) return false
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
        if ((this.repeatedBytes.size != other.repeatedBytes.size || !this.repeatedBytes.zip(other.repeatedBytes).all { (a, b) -> a.contentEquals(b) })) return false
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
        if (presenceMask[22] && this.groupliketype != other.groupliketype) return false
        if (presenceMask[23] && this.delimitedField != other.delimitedField) return false
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
        builder.appendLine("TestAllTypesEdition2023(")
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
            builder.appendLine("${nextIndentString}optionalBytes=${this.optionalBytes.contentToString()},")
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
        builder.appendLine("${nextIndentString}mapInt32Float=${this.mapInt32Float},")
        builder.appendLine("${nextIndentString}mapInt32Double=${this.mapInt32Double},")
        builder.appendLine("${nextIndentString}mapBoolBool=${this.mapBoolBool},")
        builder.appendLine("${nextIndentString}mapStringString=${this.mapStringString},")
        builder.appendLine("${nextIndentString}mapStringBytes=${this.mapStringBytes},")
        builder.appendLine("${nextIndentString}mapStringNestedMessage=${this.mapStringNestedMessage},")
        builder.appendLine("${nextIndentString}mapStringForeignMessage=${this.mapStringForeignMessage},")
        builder.appendLine("${nextIndentString}mapStringNestedEnum=${this.mapStringNestedEnum},")
        builder.appendLine("${nextIndentString}mapStringForeignEnum=${this.mapStringForeignEnum},")
        if (presenceMask[22]) {
            builder.appendLine("${nextIndentString}groupliketype=${this.groupliketype.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}groupliketype=<unset>,")
        }

        if (presenceMask[23]) {
            builder.appendLine("${nextIndentString}delimitedField=${this.delimitedField.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}delimitedField=<unset>,")
        }

        builder.appendLine("${nextIndentString}oneofField=${this.oneofField},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): TestAllTypesEdition2023Internal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: TestAllTypesEdition2023Internal.() -> Unit): TestAllTypesEdition2023Internal {
        val copy = TestAllTypesEdition2023Internal()
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
        copy.mapInt32Float = this.mapInt32Float.mapValues { it.value }
        copy.mapInt32Double = this.mapInt32Double.mapValues { it.value }
        copy.mapBoolBool = this.mapBoolBool.mapValues { it.value }
        copy.mapStringString = this.mapStringString.mapValues { it.value }
        copy.mapStringBytes = this.mapStringBytes.mapValues { it.value.copyOf() }
        copy.mapStringNestedMessage = this.mapStringNestedMessage.mapValues { it.value.copy() }
        copy.mapStringForeignMessage = this.mapStringForeignMessage.mapValues { it.value.copy() }
        copy.mapStringNestedEnum = this.mapStringNestedEnum.mapValues { it.value }
        copy.mapStringForeignEnum = this.mapStringForeignEnum.mapValues { it.value }
        if (presenceMask[22]) {
            copy.groupliketype = this.groupliketype.copy()
        }

        if (presenceMask[23]) {
            copy.delimitedField = this.delimitedField.copy()
        }

        copy.oneofField = this.oneofField?.oneOfCopy()
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    fun TestAllTypesEdition2023.OneofField.oneOfCopy(): TestAllTypesEdition2023.OneofField {
        return when (this) {
            is TestAllTypesEdition2023.OneofField.OneofUint32 -> {
                this
            }
            is TestAllTypesEdition2023.OneofField.OneofNestedMessage -> {
                TestAllTypesEdition2023.OneofField.OneofNestedMessage(this.value.copy())
            }
            is TestAllTypesEdition2023.OneofField.OneofString -> {
                this
            }
            is TestAllTypesEdition2023.OneofField.OneofBytes -> {
                TestAllTypesEdition2023.OneofField.OneofBytes(this.value.copyOf())
            }
            is TestAllTypesEdition2023.OneofField.OneofBool -> {
                this
            }
            is TestAllTypesEdition2023.OneofField.OneofUint64 -> {
                this
            }
            is TestAllTypesEdition2023.OneofField.OneofFloat -> {
                this
            }
            is TestAllTypesEdition2023.OneofField.OneofDouble -> {
                this
            }
            is TestAllTypesEdition2023.OneofField.OneofEnum -> {
                this
            }
        }
    }

    class NestedMessageInternal: TestAllTypesEdition2023.NestedMessage.Builder, InternalMessage(fieldsWithPresence = 2) {
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

        override var a: Int? by MsgFieldDelegate(PresenceIndices.a) { null }
        override var corecursive: TestAllTypesEdition2023 by MsgFieldDelegate(PresenceIndices.corecursive) { TestAllTypesEdition2023Internal() }

        private val _owner: NestedMessageInternal = this

        @InternalRpcApi
        val _presence: TestAllTypesEdition2023Presence.NestedMessage = object : TestAllTypesEdition2023Presence.NestedMessage, InternalPresenceObject {
            override val _message: NestedMessageInternal get() = _owner

            override val hasA: Boolean get() = presenceMask[0]

            override val hasCorecursive: Boolean get() = presenceMask[1]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) (a?.hashCode() ?: 0) else 0
            result = 31 * result + if (presenceMask[1]) corecursive.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.NestedMessage(")
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
        object MARSHALLER: GrpcMarshaller<TestAllTypesEdition2023.NestedMessage> {
            override fun encode(value: TestAllTypesEdition2023.NestedMessage, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesEdition2023.NestedMessage {
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
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesEdition2023.NestedMessage> {
            override val fullName: String = "protobuf_test_messages.editions.TestAllTypesEdition2023.NestedMessage"
        }

        @InternalRpcApi
        companion object
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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Int by MsgFieldDelegate(PresenceIndices.value) { 0 }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapInt32Int32Entry(")
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

        var key: Long by MsgFieldDelegate(PresenceIndices.key) { 0L }
        var value: Long by MsgFieldDelegate(PresenceIndices.value) { 0L }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapInt64Int64Entry(")
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

        var key: UInt by MsgFieldDelegate(PresenceIndices.key) { 0u }
        var value: UInt by MsgFieldDelegate(PresenceIndices.value) { 0u }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapUint32Uint32Entry(")
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

        var key: ULong by MsgFieldDelegate(PresenceIndices.key) { 0uL }
        var value: ULong by MsgFieldDelegate(PresenceIndices.value) { 0uL }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapUint64Uint64Entry(")
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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Int by MsgFieldDelegate(PresenceIndices.value) { 0 }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapSint32Sint32Entry(")
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

        var key: Long by MsgFieldDelegate(PresenceIndices.key) { 0L }
        var value: Long by MsgFieldDelegate(PresenceIndices.value) { 0L }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapSint64Sint64Entry(")
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

        var key: UInt by MsgFieldDelegate(PresenceIndices.key) { 0u }
        var value: UInt by MsgFieldDelegate(PresenceIndices.value) { 0u }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapFixed32Fixed32Entry(")
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

        var key: ULong by MsgFieldDelegate(PresenceIndices.key) { 0uL }
        var value: ULong by MsgFieldDelegate(PresenceIndices.value) { 0uL }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapFixed64Fixed64Entry(")
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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Int by MsgFieldDelegate(PresenceIndices.value) { 0 }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapSfixed32Sfixed32Entry(")
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

        var key: Long by MsgFieldDelegate(PresenceIndices.key) { 0L }
        var value: Long by MsgFieldDelegate(PresenceIndices.value) { 0L }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapSfixed64Sfixed64Entry(")
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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Float by MsgFieldDelegate(PresenceIndices.value) { 0.0f }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.toBits().hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapInt32FloatEntry(")
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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Double by MsgFieldDelegate(PresenceIndices.value) { 0.0 }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.toBits().hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapInt32DoubleEntry(")
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

        var key: Boolean by MsgFieldDelegate(PresenceIndices.key) { false }
        var value: Boolean by MsgFieldDelegate(PresenceIndices.value) { false }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapBoolBoolEntry(")
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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: String by MsgFieldDelegate(PresenceIndices.value) { "" }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapStringStringEntry(")
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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: ByteArray by MsgFieldDelegate(PresenceIndices.value) { byteArrayOf() }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.contentHashCode() else 0
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
            if (presenceMask[1] && !this.value.contentEquals(other.value)) return false
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
            builder.appendLine("TestAllTypesEdition2023.MapStringBytesEntry(")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}key=${this.key},")
            } else {
                builder.appendLine("${nextIndentString}key=<unset>,")
            }

            if (presenceMask[1]) {
                builder.appendLine("${nextIndentString}value=${this.value.contentToString()},")
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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: TestAllTypesEdition2023.NestedMessage by MsgFieldDelegate(PresenceIndices.value) { NestedMessageInternal() }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapStringNestedMessageEntry(")
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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: ForeignMessageEdition2023 by MsgFieldDelegate(PresenceIndices.value) { ForeignMessageEdition2023Internal() }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapStringForeignMessageEntry(")
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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: TestAllTypesEdition2023.NestedEnum by MsgFieldDelegate(PresenceIndices.value) { TestAllTypesEdition2023.NestedEnum.FOO }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapStringNestedEnumEntry(")
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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: ForeignEnumEdition2023 by MsgFieldDelegate(PresenceIndices.value) { ForeignEnumEdition2023.FOREIGN_FOO }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) key.hashCode() else 0
            result = 31 * result + if (presenceMask[1]) value.hashCode() else 0
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
            builder.appendLine("TestAllTypesEdition2023.MapStringForeignEnumEntry(")
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

    class GroupLikeTypeInternal: TestAllTypesEdition2023.GroupLikeType.Builder, InternalMessage(fieldsWithPresence = 2) {
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

        override var groupInt32: Int? by MsgFieldDelegate(PresenceIndices.groupInt32) { null }
        override var groupUint32: UInt? by MsgFieldDelegate(PresenceIndices.groupUint32) { null }

        private val _owner: GroupLikeTypeInternal = this

        @InternalRpcApi
        val _presence: TestAllTypesEdition2023Presence.GroupLikeType = object : TestAllTypesEdition2023Presence.GroupLikeType, InternalPresenceObject {
            override val _message: GroupLikeTypeInternal get() = _owner

            override val hasGroupInt32: Boolean get() = presenceMask[0]

            override val hasGroupUint32: Boolean get() = presenceMask[1]
        }

        override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[0]) (groupInt32?.hashCode() ?: 0) else 0
            result = 31 * result + if (presenceMask[1]) (groupUint32?.hashCode() ?: 0) else 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as GroupLikeTypeInternal
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
            builder.appendLine("TestAllTypesEdition2023.GroupLikeType(")
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

        override fun copyInternal(): GroupLikeTypeInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        fun copyInternal(body: GroupLikeTypeInternal.() -> Unit): GroupLikeTypeInternal {
            val copy = GroupLikeTypeInternal()
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
        object MARSHALLER: GrpcMarshaller<TestAllTypesEdition2023.GroupLikeType> {
            override fun encode(value: TestAllTypesEdition2023.GroupLikeType, config: GrpcMarshallerConfig?): Source {
                val buffer = Buffer()
                val encoder = WireEncoder(buffer)
                val internalMsg = value.asInternal()
                checkForPlatformEncodeException {
                    internalMsg.encodeWith(encoder, config as? ProtoConfig)
                }
                encoder.flush()
                return buffer
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesEdition2023.GroupLikeType {
                WireDecoder(source).use {
                    (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                    val msg = GroupLikeTypeInternal()
                    checkForPlatformDecodeException {
                        GroupLikeTypeInternal.decodeWith(msg, it, config as? ProtoConfig, null)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @InternalRpcApi
        object DESCRIPTOR: ProtoDescriptor<TestAllTypesEdition2023.GroupLikeType> {
            override val fullName: String = "protobuf_test_messages.editions.TestAllTypesEdition2023.GroupLikeType"
        }

        @InternalRpcApi
        companion object
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<TestAllTypesEdition2023> {
        override fun encode(value: TestAllTypesEdition2023, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): TestAllTypesEdition2023 {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = TestAllTypesEdition2023Internal()
                checkForPlatformDecodeException {
                    TestAllTypesEdition2023Internal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<TestAllTypesEdition2023> {
        override val fullName: String = "protobuf_test_messages.editions.TestAllTypesEdition2023"
    }

    @InternalRpcApi
    companion object
}

class ForeignMessageEdition2023Internal: ForeignMessageEdition2023.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        const val c: Int = 0
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var c: Int? by MsgFieldDelegate(PresenceIndices.c) { null }

    private val _owner: ForeignMessageEdition2023Internal = this

    @InternalRpcApi
    val _presence: ForeignMessageEdition2023Presence = object : ForeignMessageEdition2023Presence, InternalPresenceObject {
        override val _message: ForeignMessageEdition2023Internal get() = _owner

        override val hasC: Boolean get() = presenceMask[0]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) (c?.hashCode() ?: 0) else 0
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ForeignMessageEdition2023Internal
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
        builder.appendLine("ForeignMessageEdition2023(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}c=${this.c},")
        } else {
            builder.appendLine("${nextIndentString}c=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): ForeignMessageEdition2023Internal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: ForeignMessageEdition2023Internal.() -> Unit): ForeignMessageEdition2023Internal {
        val copy = ForeignMessageEdition2023Internal()
        if (presenceMask[0]) {
            copy.c = this.c
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<ForeignMessageEdition2023> {
        override fun encode(value: ForeignMessageEdition2023, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): ForeignMessageEdition2023 {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ForeignMessageEdition2023Internal()
                checkForPlatformDecodeException {
                    ForeignMessageEdition2023Internal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<ForeignMessageEdition2023> {
        override val fullName: String = "protobuf_test_messages.editions.ForeignMessageEdition2023"
    }

    @InternalRpcApi
    companion object
}

class GroupLikeTypeInternal: GroupLikeType.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        const val c: Int = 0
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var c: Int? by MsgFieldDelegate(PresenceIndices.c) { null }

    private val _owner: GroupLikeTypeInternal = this

    @InternalRpcApi
    val _presence: GroupLikeTypePresence = object : GroupLikeTypePresence, InternalPresenceObject {
        override val _message: GroupLikeTypeInternal get() = _owner

        override val hasC: Boolean get() = presenceMask[0]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[0]) (c?.hashCode() ?: 0) else 0
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as GroupLikeTypeInternal
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
        builder.appendLine("GroupLikeType(")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}c=${this.c},")
        } else {
            builder.appendLine("${nextIndentString}c=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): GroupLikeTypeInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: GroupLikeTypeInternal.() -> Unit): GroupLikeTypeInternal {
        val copy = GroupLikeTypeInternal()
        if (presenceMask[0]) {
            copy.c = this.c
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<GroupLikeType> {
        override fun encode(value: GroupLikeType, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): GroupLikeType {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = GroupLikeTypeInternal()
                checkForPlatformDecodeException {
                    GroupLikeTypeInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<GroupLikeType> {
        override val fullName: String = "protobuf_test_messages.editions.GroupLikeType"
    }

    @InternalRpcApi
    companion object
}

@InternalRpcApi
fun ComplexMessageInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun ComplexMessageInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.d?.also {
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
fun ComplexMessageInternal.Companion.decodeWith(msg: ComplexMessageInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.d = decoder.readInt32()
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

private fun ComplexMessageInternal.computeSize(): Int {
    var __result = 0
    this.d?.also {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(it))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun ComplexMessage.asInternal(): ComplexMessageInternal {
    return this as? ComplexMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.checkRequiredFields() {
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
        this.groupliketype.asInternal().checkRequiredFields()
    }

    if (presenceMask[23]) {
        this.delimitedField.asInternal().checkRequiredFields()
    }

    this.oneofField?.also {
        when {
            it is TestAllTypesEdition2023.OneofField.OneofNestedMessage -> {
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

    this.mapStringNestedMessage.values.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.mapStringForeignMessage.values.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
            TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapStringStringEntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapStringBytesEntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal().apply {
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
            TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 74, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }

    if (presenceMask[22]) {
        encoder.writeGroupMessage(fieldNr = 201, value = this.groupliketype.asInternal()) { encodeWith(it, config) }
    }

    if (presenceMask[23]) {
        encoder.writeGroupMessage(fieldNr = 202, value = this.delimitedField.asInternal()) { encodeWith(it, config) }
    }

    this.oneofField?.also {
        when (val value = it) {
            is TestAllTypesEdition2023.OneofField.OneofUint32 -> {
                encoder.writeUInt32(fieldNr = 111, value = value.value)
            }
            is TestAllTypesEdition2023.OneofField.OneofNestedMessage -> {
                encoder.writeMessage(fieldNr = 112, value = value.value.asInternal()) { encodeWith(it, config) }
            }
            is TestAllTypesEdition2023.OneofField.OneofString -> {
                encoder.writeString(fieldNr = 113, value = value.value)
            }
            is TestAllTypesEdition2023.OneofField.OneofBytes -> {
                encoder.writeBytes(fieldNr = 114, value = value.value)
            }
            is TestAllTypesEdition2023.OneofField.OneofBool -> {
                encoder.writeBool(fieldNr = 115, value = value.value)
            }
            is TestAllTypesEdition2023.OneofField.OneofUint64 -> {
                encoder.writeUInt64(fieldNr = 116, value = value.value)
            }
            is TestAllTypesEdition2023.OneofField.OneofFloat -> {
                encoder.writeFloat(fieldNr = 117, value = value.value)
            }
            is TestAllTypesEdition2023.OneofField.OneofDouble -> {
                encoder.writeDouble(fieldNr = 118, value = value.value)
            }
            is TestAllTypesEdition2023.OneofField.OneofEnum -> {
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
fun TestAllTypesEdition2023Internal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal, decoder: WireDecoder, config: ProtoConfig?) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(TestAllTypesEdition2023::class) ?: emptyMap()
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
                if (!msg.presenceMask[15]) {
                    msg.optionalNestedMessage = TestAllTypesEdition2023Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.optionalNestedMessage.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 19 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[16]) {
                    msg.optionalForeignMessage = ForeignMessageEdition2023Internal()
                }

                decoder.readMessage(msg.optionalForeignMessage.asInternal(), { msg, decoder -> ForeignMessageEdition2023Internal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 21 && tag.wireType == WireType.VARINT -> {
                msg.optionalNestedEnum = TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 22 && tag.wireType == WireType.VARINT -> {
                msg.optionalForeignEnum = ForeignEnumEdition2023.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 24 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.optionalStringPiece = decoder.readString()
            }
            tag.fieldNr == 25 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.optionalCord = decoder.readString()
            }
            tag.fieldNr == 27 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[21]) {
                    msg.recursiveMessage = TestAllTypesEdition2023Internal()
                }

                decoder.readMessage(msg.recursiveMessage.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.decodeWith(msg, decoder, config) })
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
                val elem = TestAllTypesEdition2023Internal.NestedMessageInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
                (msg.repeatedNestedMessage as MutableList).add(elem)
            }
            tag.fieldNr == 49 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = ForeignMessageEdition2023Internal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> ForeignMessageEdition2023Internal.decodeWith(msg, decoder, config) })
                (msg.repeatedForeignMessage as MutableList).add(elem)
            }
            tag.fieldNr == 51 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedNestedEnum += decoder.readPackedEnum().map { TestAllTypesEdition2023.NestedEnum.fromNumber(it) }
            }
            tag.fieldNr == 51 && tag.wireType == WireType.VARINT -> {
                val elem = TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
                (msg.repeatedNestedEnum as MutableList).add(elem)
            }
            tag.fieldNr == 52 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.repeatedForeignEnum += decoder.readPackedEnum().map { ForeignEnumEdition2023.fromNumber(it) }
            }
            tag.fieldNr == 52 && tag.wireType == WireType.VARINT -> {
                val elem = ForeignEnumEdition2023.fromNumber(decoder.readEnum())
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
                msg.packedNestedEnum += decoder.readPackedEnum().map { TestAllTypesEdition2023.NestedEnum.fromNumber(it) }
            }
            tag.fieldNr == 88 && tag.wireType == WireType.VARINT -> {
                val elem = TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
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
                msg.unpackedNestedEnum += decoder.readPackedEnum().map { TestAllTypesEdition2023.NestedEnum.fromNumber(it) }
            }
            tag.fieldNr == 102 && tag.wireType == WireType.VARINT -> {
                val elem = TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
                (msg.unpackedNestedEnum as MutableList).add(elem)
            }
            tag.fieldNr == 56 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapInt32Int32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 57 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapInt64Int64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 58 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapUint32Uint32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 59 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapUint64Uint64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 60 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapSint32Sint32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 61 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapSint64Sint64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 62 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapFixed32Fixed32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 63 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapFixed64Fixed64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 64 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapSfixed32Sfixed32 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 65 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapSfixed64Sfixed64 as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 66 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapInt32Float as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 67 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapInt32Double as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 68 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapBoolBool as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 69 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapStringStringEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapStringStringEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringString as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 70 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapStringBytesEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringBytes as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 71 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringNestedMessage as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 72 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringForeignMessage as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 73 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringNestedEnum as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 74 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.mapStringForeignEnum as MutableMap)[key] = value
                }
            }
            tag.fieldNr == 201 && tag.wireType == WireType.START_GROUP -> {
                if (!msg.presenceMask[22]) {
                    msg.groupliketype = TestAllTypesEdition2023Internal.GroupLikeTypeInternal()
                }

                decoder.readGroup(msg.groupliketype.asInternal()) { msg, decoder -> TestAllTypesEdition2023Internal.GroupLikeTypeInternal.decodeWith(msg, decoder, config, tag) }
            }
            tag.fieldNr == 202 && tag.wireType == WireType.START_GROUP -> {
                if (!msg.presenceMask[23]) {
                    msg.delimitedField = TestAllTypesEdition2023Internal.GroupLikeTypeInternal()
                }

                decoder.readGroup(msg.delimitedField.asInternal()) { msg, decoder -> TestAllTypesEdition2023Internal.GroupLikeTypeInternal.decodeWith(msg, decoder, config, tag) }
            }
            tag.fieldNr == 111 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesEdition2023.OneofField.OneofUint32(decoder.readUInt32())
            }
            tag.fieldNr == 112 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.oneofField as? TestAllTypesEdition2023.OneofField.OneofNestedMessage) ?: TestAllTypesEdition2023.OneofField.OneofNestedMessage(TestAllTypesEdition2023Internal.NestedMessageInternal()).also {
                    msg.oneofField = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 113 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.oneofField = TestAllTypesEdition2023.OneofField.OneofString(decoder.readString())
            }
            tag.fieldNr == 114 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.oneofField = TestAllTypesEdition2023.OneofField.OneofBytes(decoder.readBytes())
            }
            tag.fieldNr == 115 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesEdition2023.OneofField.OneofBool(decoder.readBool())
            }
            tag.fieldNr == 116 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesEdition2023.OneofField.OneofUint64(decoder.readUInt64())
            }
            tag.fieldNr == 117 && tag.wireType == WireType.FIXED32 -> {
                msg.oneofField = TestAllTypesEdition2023.OneofField.OneofFloat(decoder.readFloat())
            }
            tag.fieldNr == 118 && tag.wireType == WireType.FIXED64 -> {
                msg.oneofField = TestAllTypesEdition2023.OneofField.OneofDouble(decoder.readDouble())
            }
            tag.fieldNr == 119 && tag.wireType == WireType.VARINT -> {
                msg.oneofField = TestAllTypesEdition2023.OneofField.OneofEnum(TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum()))
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

private fun TestAllTypesEdition2023Internal.computeSize(): Int {
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
            TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(56, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt64Int64.isNotEmpty()) {
        __result += mapInt64Int64.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(57, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapUint32Uint32.isNotEmpty()) {
        __result += mapUint32Uint32.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(58, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapUint64Uint64.isNotEmpty()) {
        __result += mapUint64Uint64.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(59, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSint32Sint32.isNotEmpty()) {
        __result += mapSint32Sint32.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(60, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSint64Sint64.isNotEmpty()) {
        __result += mapSint64Sint64.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(61, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapFixed32Fixed32.isNotEmpty()) {
        __result += mapFixed32Fixed32.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(62, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapFixed64Fixed64.isNotEmpty()) {
        __result += mapFixed64Fixed64.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(63, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSfixed32Sfixed32.isNotEmpty()) {
        __result += mapSfixed32Sfixed32.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(64, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapSfixed64Sfixed64.isNotEmpty()) {
        __result += mapSfixed64Sfixed64.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(65, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt32Float.isNotEmpty()) {
        __result += mapInt32Float.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(66, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapInt32Double.isNotEmpty()) {
        __result += mapInt32Double.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(67, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapBoolBool.isNotEmpty()) {
        __result += mapBoolBool.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(68, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringString.isNotEmpty()) {
        __result += mapStringString.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapStringStringEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(69, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringBytes.isNotEmpty()) {
        __result += mapStringBytes.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapStringBytesEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(70, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringNestedMessage.isNotEmpty()) {
        __result += mapStringNestedMessage.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(71, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringForeignMessage.isNotEmpty()) {
        __result += mapStringForeignMessage.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(72, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringNestedEnum.isNotEmpty()) {
        __result += mapStringNestedEnum.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(73, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (this.mapStringForeignEnum.isNotEmpty()) {
        __result += mapStringForeignEnum.entries.sumOf { kEntry ->
            TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(74, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    if (presenceMask[22]) {
        __result += this.groupliketype.asInternal()._size.let { (2 * WireSize.tag(201, WireType.START_GROUP)) + it }
    }

    if (presenceMask[23]) {
        __result += this.delimitedField.asInternal()._size.let { (2 * WireSize.tag(202, WireType.START_GROUP)) + it }
    }

    this.oneofField?.also {
        when (val value = it) {
            is TestAllTypesEdition2023.OneofField.OneofUint32 -> {
                __result += (WireSize.tag(111, WireType.VARINT) + WireSize.uInt32(value.value))
            }
            is TestAllTypesEdition2023.OneofField.OneofNestedMessage -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(112, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestAllTypesEdition2023.OneofField.OneofString -> {
                __result += WireSize.string(value.value).let { WireSize.tag(113, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestAllTypesEdition2023.OneofField.OneofBytes -> {
                __result += WireSize.bytes(value.value).let { WireSize.tag(114, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is TestAllTypesEdition2023.OneofField.OneofBool -> {
                __result += (WireSize.tag(115, WireType.VARINT) + WireSize.bool(value.value))
            }
            is TestAllTypesEdition2023.OneofField.OneofUint64 -> {
                __result += (WireSize.tag(116, WireType.VARINT) + WireSize.uInt64(value.value))
            }
            is TestAllTypesEdition2023.OneofField.OneofFloat -> {
                __result += (WireSize.tag(117, WireType.FIXED32) + WireSize.float(value.value))
            }
            is TestAllTypesEdition2023.OneofField.OneofDouble -> {
                __result += (WireSize.tag(118, WireType.FIXED64) + WireSize.double(value.value))
            }
            is TestAllTypesEdition2023.OneofField.OneofEnum -> {
                __result += (WireSize.tag(119, WireType.VARINT) + WireSize.enum(value.value.number))
            }
        }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestAllTypesEdition2023.asInternal(): TestAllTypesEdition2023Internal {
    return this as? TestAllTypesEdition2023Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ForeignMessageEdition2023Internal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun ForeignMessageEdition2023Internal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun ForeignMessageEdition2023Internal.Companion.decodeWith(msg: ForeignMessageEdition2023Internal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun ForeignMessageEdition2023Internal.computeSize(): Int {
    var __result = 0
    this.c?.also {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(it))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun ForeignMessageEdition2023.asInternal(): ForeignMessageEdition2023Internal {
    return this as? ForeignMessageEdition2023Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun GroupLikeTypeInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun GroupLikeTypeInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun GroupLikeTypeInternal.Companion.decodeWith(msg: GroupLikeTypeInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun GroupLikeTypeInternal.computeSize(): Int {
    var __result = 0
    this.c?.also {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(it))
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun GroupLikeType.asInternal(): GroupLikeTypeInternal {
    return this as? GroupLikeTypeInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.NestedMessageInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[1]) {
        this.corecursive.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.NestedMessageInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.NestedMessageInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.NestedMessageInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.a = decoder.readInt32()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[1]) {
                    msg.corecursive = TestAllTypesEdition2023Internal()
                }

                decoder.readMessage(msg.corecursive.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.decodeWith(msg, decoder, config) })
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

private fun TestAllTypesEdition2023Internal.NestedMessageInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023.NestedMessage.asInternal(): TestAllTypesEdition2023Internal.NestedMessageInternal {
    return this as? TestAllTypesEdition2023Internal.NestedMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringStringEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringStringEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapStringStringEntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapStringStringEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapStringStringEntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapStringStringEntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapStringStringEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapStringBytesEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

private fun TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapStringBytesEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[1]) {
        this.value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[1]) {
                    msg.value = TestAllTypesEdition2023Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.value.asInternal(), { msg, decoder -> TestAllTypesEdition2023Internal.NestedMessageInternal.decodeWith(msg, decoder, config) })
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

private fun TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[1]) {
        this.value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[1]) {
                    msg.value = ForeignMessageEdition2023Internal()
                }

                decoder.readMessage(msg.value.asInternal(), { msg, decoder -> ForeignMessageEdition2023Internal.decodeWith(msg, decoder, config) })
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

private fun TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
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

private fun TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.value = ForeignEnumEdition2023.fromNumber(decoder.readEnum())
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

private fun TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.asInternal(): TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal {
    return this
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.GroupLikeTypeInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestAllTypesEdition2023Internal.GroupLikeTypeInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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
fun TestAllTypesEdition2023Internal.GroupLikeTypeInternal.Companion.decodeWith(msg: TestAllTypesEdition2023Internal.GroupLikeTypeInternal, decoder: WireDecoder, config: ProtoConfig?, startGroup: KTag?) {
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

private fun TestAllTypesEdition2023Internal.GroupLikeTypeInternal.computeSize(): Int {
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
fun TestAllTypesEdition2023.GroupLikeType.asInternal(): TestAllTypesEdition2023Internal.GroupLikeTypeInternal {
    return this as? TestAllTypesEdition2023Internal.GroupLikeTypeInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ForeignEnumEdition2023.Companion.fromNumber(number: Int): ForeignEnumEdition2023 {
    return when (number) {
        0 -> {
            ForeignEnumEdition2023.FOREIGN_FOO
        }
        1 -> {
            ForeignEnumEdition2023.FOREIGN_BAR
        }
        2 -> {
            ForeignEnumEdition2023.FOREIGN_BAZ
        }
        else -> {
            ForeignEnumEdition2023.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
fun TestAllTypesEdition2023.NestedEnum.Companion.fromNumber(number: Int): TestAllTypesEdition2023.NestedEnum {
    return when (number) {
        0 -> {
            TestAllTypesEdition2023.NestedEnum.FOO
        }
        1 -> {
            TestAllTypesEdition2023.NestedEnum.BAR
        }
        2 -> {
            TestAllTypesEdition2023.NestedEnum.BAZ
        }
        -1 -> {
            TestAllTypesEdition2023.NestedEnum.NEG
        }
        else -> {
            TestAllTypesEdition2023.NestedEnum.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
object TestMessagesEdition2023KtExtensions {
    val extensionInt32: InternalExtensionDescriptor<TestAllTypesEdition2023,  Int> = 
        InternalExtensionDescriptor.int32(
            fieldNumber = 120,
            name = "extensionInt32",
            extendee = TestAllTypesEdition2023::class,
        )

    val groupliketype: InternalExtensionDescriptor<TestAllTypesEdition2023,  GroupLikeType> = 
        InternalExtensionDescriptor.message(
            fieldNumber = 121,
            name = "groupliketype",
            extendee = TestAllTypesEdition2023::class,
            valueType = GroupLikeType::class,
            default = { GroupLikeTypeInternal() },
            asInternal = { it.asInternal() },
            encodeWith = { value, encoder, config -> value.asInternal().encodeWith(encoder, config) },
            decodeWith = { value, decoder, config -> GroupLikeTypeInternal.decodeWith(value.asInternal(), decoder, config) },
        )

    val delimitedExt: InternalExtensionDescriptor<TestAllTypesEdition2023,  GroupLikeType> = 
        InternalExtensionDescriptor.message(
            fieldNumber = 122,
            name = "delimitedExt",
            extendee = TestAllTypesEdition2023::class,
            valueType = GroupLikeType::class,
            default = { GroupLikeTypeInternal() },
            asInternal = { it.asInternal() },
            encodeWith = { value, encoder, config -> value.asInternal().encodeWith(encoder, config) },
            decodeWith = { value, decoder, config -> GroupLikeTypeInternal.decodeWith(value.asInternal(), decoder, config) },
        )
}
