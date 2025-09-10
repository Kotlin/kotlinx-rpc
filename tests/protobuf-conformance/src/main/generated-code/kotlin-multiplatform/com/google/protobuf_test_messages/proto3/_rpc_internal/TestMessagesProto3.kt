@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf_test_messages.proto3

import com.google.protobuf.kotlin.*
import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

class TestAllTypesProto3Internal: com.google.protobuf_test_messages.proto3.TestAllTypesProto3, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 18) { 
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

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

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
    override var optionalNestedMessage: com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage by MsgFieldDelegate(PresenceIndices.optionalNestedMessage) { com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal() }
    override var optionalForeignMessage: com.google.protobuf_test_messages.proto3.ForeignMessage by MsgFieldDelegate(PresenceIndices.optionalForeignMessage) { com.google.protobuf_test_messages.proto3.ForeignMessageInternal() }
    override var optionalNestedEnum: com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum by MsgFieldDelegate { com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.FOO }
    override var optionalForeignEnum: com.google.protobuf_test_messages.proto3.ForeignEnum by MsgFieldDelegate { com.google.protobuf_test_messages.proto3.ForeignEnum.FOREIGN_FOO }
    override var optionalAliasedEnum: com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum by MsgFieldDelegate { com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum.ALIAS_FOO }
    override var optionalStringPiece: String by MsgFieldDelegate { "" }
    override var optionalCord: String by MsgFieldDelegate { "" }
    override var recursiveMessage: com.google.protobuf_test_messages.proto3.TestAllTypesProto3 by MsgFieldDelegate(PresenceIndices.recursiveMessage) { com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal() }
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
    override var repeatedNestedMessage: List<com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignMessage: List<com.google.protobuf_test_messages.proto3.ForeignMessage> by MsgFieldDelegate { mutableListOf() }
    override var repeatedNestedEnum: List<com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignEnum: List<com.google.protobuf_test_messages.proto3.ForeignEnum> by MsgFieldDelegate { mutableListOf() }
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
    override var packedNestedEnum: List<com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum> by MsgFieldDelegate { mutableListOf() }
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
    override var unpackedNestedEnum: List<com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum> by MsgFieldDelegate { mutableListOf() }
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
    override var mapInt32Float: Map<kotlin.Int, kotlin.Float> by MsgFieldDelegate { mutableMapOf() }
    override var mapInt32Double: Map<kotlin.Int, kotlin.Double> by MsgFieldDelegate { mutableMapOf() }
    override var mapBoolBool: Map<kotlin.Boolean, kotlin.Boolean> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringString: Map<kotlin.String, kotlin.String> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringBytes: Map<kotlin.String, kotlin.ByteArray> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringNestedMessage: Map<kotlin.String, com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignMessage: Map<kotlin.String, com.google.protobuf_test_messages.proto3.ForeignMessage> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringNestedEnum: Map<kotlin.String, com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignEnum: Map<kotlin.String, com.google.protobuf_test_messages.proto3.ForeignEnum> by MsgFieldDelegate { mutableMapOf() }
    override var optionalBoolWrapper: com.google.protobuf.kotlin.BoolValue by MsgFieldDelegate(PresenceIndices.optionalBoolWrapper) { com.google.protobuf.kotlin.BoolValueInternal() }
    override var optionalInt32Wrapper: com.google.protobuf.kotlin.Int32Value by MsgFieldDelegate(PresenceIndices.optionalInt32Wrapper) { com.google.protobuf.kotlin.Int32ValueInternal() }
    override var optionalInt64Wrapper: com.google.protobuf.kotlin.Int64Value by MsgFieldDelegate(PresenceIndices.optionalInt64Wrapper) { com.google.protobuf.kotlin.Int64ValueInternal() }
    override var optionalUint32Wrapper: com.google.protobuf.kotlin.UInt32Value by MsgFieldDelegate(PresenceIndices.optionalUint32Wrapper) { com.google.protobuf.kotlin.UInt32ValueInternal() }
    override var optionalUint64Wrapper: com.google.protobuf.kotlin.UInt64Value by MsgFieldDelegate(PresenceIndices.optionalUint64Wrapper) { com.google.protobuf.kotlin.UInt64ValueInternal() }
    override var optionalFloatWrapper: com.google.protobuf.kotlin.FloatValue by MsgFieldDelegate(PresenceIndices.optionalFloatWrapper) { com.google.protobuf.kotlin.FloatValueInternal() }
    override var optionalDoubleWrapper: com.google.protobuf.kotlin.DoubleValue by MsgFieldDelegate(PresenceIndices.optionalDoubleWrapper) { com.google.protobuf.kotlin.DoubleValueInternal() }
    override var optionalStringWrapper: com.google.protobuf.kotlin.StringValue by MsgFieldDelegate(PresenceIndices.optionalStringWrapper) { com.google.protobuf.kotlin.StringValueInternal() }
    override var optionalBytesWrapper: com.google.protobuf.kotlin.BytesValue by MsgFieldDelegate(PresenceIndices.optionalBytesWrapper) { com.google.protobuf.kotlin.BytesValueInternal() }
    override var repeatedBoolWrapper: List<com.google.protobuf.kotlin.BoolValue> by MsgFieldDelegate { mutableListOf() }
    override var repeatedInt32Wrapper: List<com.google.protobuf.kotlin.Int32Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedInt64Wrapper: List<com.google.protobuf.kotlin.Int64Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedUint32Wrapper: List<com.google.protobuf.kotlin.UInt32Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedUint64Wrapper: List<com.google.protobuf.kotlin.UInt64Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFloatWrapper: List<com.google.protobuf.kotlin.FloatValue> by MsgFieldDelegate { mutableListOf() }
    override var repeatedDoubleWrapper: List<com.google.protobuf.kotlin.DoubleValue> by MsgFieldDelegate { mutableListOf() }
    override var repeatedStringWrapper: List<com.google.protobuf.kotlin.StringValue> by MsgFieldDelegate { mutableListOf() }
    override var repeatedBytesWrapper: List<com.google.protobuf.kotlin.BytesValue> by MsgFieldDelegate { mutableListOf() }
    override var optionalDuration: com.google.protobuf.kotlin.Duration by MsgFieldDelegate(PresenceIndices.optionalDuration) { com.google.protobuf.kotlin.DurationInternal() }
    override var optionalTimestamp: com.google.protobuf.kotlin.Timestamp by MsgFieldDelegate(PresenceIndices.optionalTimestamp) { com.google.protobuf.kotlin.TimestampInternal() }
    override var optionalFieldMask: com.google.protobuf.kotlin.FieldMask by MsgFieldDelegate(PresenceIndices.optionalFieldMask) { com.google.protobuf.kotlin.FieldMaskInternal() }
    override var optionalStruct: com.google.protobuf.kotlin.Struct by MsgFieldDelegate(PresenceIndices.optionalStruct) { com.google.protobuf.kotlin.StructInternal() }
    override var optionalAny: com.google.protobuf.kotlin.Any by MsgFieldDelegate(PresenceIndices.optionalAny) { com.google.protobuf.kotlin.AnyInternal() }
    override var optionalValue: com.google.protobuf.kotlin.Value by MsgFieldDelegate(PresenceIndices.optionalValue) { com.google.protobuf.kotlin.ValueInternal() }
    override var optionalNullValue: com.google.protobuf.kotlin.NullValue by MsgFieldDelegate { com.google.protobuf.kotlin.NullValue.NULL_VALUE }
    override var repeatedDuration: List<com.google.protobuf.kotlin.Duration> by MsgFieldDelegate { mutableListOf() }
    override var repeatedTimestamp: List<com.google.protobuf.kotlin.Timestamp> by MsgFieldDelegate { mutableListOf() }
    override var repeatedFieldmask: List<com.google.protobuf.kotlin.FieldMask> by MsgFieldDelegate { mutableListOf() }
    override var repeatedStruct: List<com.google.protobuf.kotlin.Struct> by MsgFieldDelegate { mutableListOf() }
    override var repeatedAny: List<com.google.protobuf.kotlin.Any> by MsgFieldDelegate { mutableListOf() }
    override var repeatedValue: List<com.google.protobuf.kotlin.Value> by MsgFieldDelegate { mutableListOf() }
    override var repeatedListValue: List<com.google.protobuf.kotlin.ListValue> by MsgFieldDelegate { mutableListOf() }
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
    override var oneofField: com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField? = null

    class NestedMessageInternal: com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            const val corecursive: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        override var a: Int by MsgFieldDelegate { 0 }
        override var corecursive: com.google.protobuf_test_messages.proto3.TestAllTypesProto3 by MsgFieldDelegate(PresenceIndices.corecursive) { com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage> { 
            override fun encode(value: com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt32Int32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: Int by MsgFieldDelegate { 0 }
        var value: Int by MsgFieldDelegate { 0 }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt64Int64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: Long by MsgFieldDelegate { 0L }
        var value: Long by MsgFieldDelegate { 0L }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapUint32Uint32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: UInt by MsgFieldDelegate { 0u }
        var value: UInt by MsgFieldDelegate { 0u }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapUint64Uint64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: ULong by MsgFieldDelegate { 0uL }
        var value: ULong by MsgFieldDelegate { 0uL }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapSint32Sint32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: Int by MsgFieldDelegate { 0 }
        var value: Int by MsgFieldDelegate { 0 }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapSint64Sint64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: Long by MsgFieldDelegate { 0L }
        var value: Long by MsgFieldDelegate { 0L }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapFixed32Fixed32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: UInt by MsgFieldDelegate { 0u }
        var value: UInt by MsgFieldDelegate { 0u }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapFixed64Fixed64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: ULong by MsgFieldDelegate { 0uL }
        var value: ULong by MsgFieldDelegate { 0uL }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapSfixed32Sfixed32EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: Int by MsgFieldDelegate { 0 }
        var value: Int by MsgFieldDelegate { 0 }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapSfixed64Sfixed64EntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: Long by MsgFieldDelegate { 0L }
        var value: Long by MsgFieldDelegate { 0L }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt32FloatEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: Int by MsgFieldDelegate { 0 }
        var value: Float by MsgFieldDelegate { 0.0f }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapInt32DoubleEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: Int by MsgFieldDelegate { 0 }
        var value: Double by MsgFieldDelegate { 0.0 }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapBoolBoolEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: Boolean by MsgFieldDelegate { false }
        var value: Boolean by MsgFieldDelegate { false }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringStringEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: String by MsgFieldDelegate { "" }
        var value: String by MsgFieldDelegate { "" }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringBytesEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: String by MsgFieldDelegate { "" }
        var value: ByteArray by MsgFieldDelegate { byteArrayOf() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringNestedMessageEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            const val value: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: String by MsgFieldDelegate { "" }
        var value: com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringForeignMessageEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            const val value: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: String by MsgFieldDelegate { "" }
        var value: com.google.protobuf_test_messages.proto3.ForeignMessage by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.proto3.ForeignMessageInternal() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringNestedEnumEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: String by MsgFieldDelegate { "" }
        var value: com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum by MsgFieldDelegate { com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.FOO }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MapStringForeignEnumEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        var key: String by MsgFieldDelegate { "" }
        var value: com.google.protobuf_test_messages.proto3.ForeignEnum by MsgFieldDelegate { com.google.protobuf_test_messages.proto3.ForeignEnum.FOREIGN_FOO }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.proto3.TestAllTypesProto3> { 
        override fun encode(value: com.google.protobuf_test_messages.proto3.TestAllTypesProto3): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.proto3.TestAllTypesProto3 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class ForeignMessageInternal: com.google.protobuf_test_messages.proto3.ForeignMessage, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    override var c: Int by MsgFieldDelegate { 0 }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.proto3.ForeignMessage> { 
        override fun encode(value: com.google.protobuf_test_messages.proto3.ForeignMessage): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.proto3.ForeignMessage { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.proto3.ForeignMessageInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.proto3.ForeignMessageInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class NullHypothesisProto3Internal: com.google.protobuf_test_messages.proto3.NullHypothesisProto3, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.proto3.NullHypothesisProto3> { 
        override fun encode(value: com.google.protobuf_test_messages.proto3.NullHypothesisProto3): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.proto3.NullHypothesisProto3 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class EnumOnlyProto3Internal: com.google.protobuf_test_messages.proto3.EnumOnlyProto3, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.proto3.EnumOnlyProto3> { 
        override fun encode(value: com.google.protobuf_test_messages.proto3.EnumOnlyProto3): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.proto3.EnumOnlyProto3 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

operator fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.Companion.invoke(body: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.() -> Unit): com.google.protobuf_test_messages.proto3.TestAllTypesProto3 { 
    val msg = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.proto3.ForeignMessage.Companion.invoke(body: com.google.protobuf_test_messages.proto3.ForeignMessageInternal.() -> Unit): com.google.protobuf_test_messages.proto3.ForeignMessage { 
    val msg = com.google.protobuf_test_messages.proto3.ForeignMessageInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.proto3.NullHypothesisProto3.Companion.invoke(body: com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal.() -> Unit): com.google.protobuf_test_messages.proto3.NullHypothesisProto3 { 
    val msg = com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.proto3.EnumOnlyProto3.Companion.invoke(body: com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal.() -> Unit): com.google.protobuf_test_messages.proto3.EnumOnlyProto3 { 
    val msg = com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage.Companion.invoke(body: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage { 
    val msg = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.checkRequiredFields() { 
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
            it is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofNestedMessage -> { 
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

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
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
        encoder.writeMessage(fieldNr = 18, value = optionalNestedMessage.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[1]) { 
        encoder.writeMessage(fieldNr = 19, value = optionalForeignMessage.asInternal()) { encodeWith(it) }
    }

    if (optionalNestedEnum != com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.FOO) { 
        encoder.writeEnum(fieldNr = 21, value = optionalNestedEnum.number)
    }

    if (optionalForeignEnum != com.google.protobuf_test_messages.proto3.ForeignEnum.FOREIGN_FOO) { 
        encoder.writeEnum(fieldNr = 22, value = optionalForeignEnum.number)
    }

    if (optionalAliasedEnum != com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum.ALIAS_FOO) { 
        encoder.writeEnum(fieldNr = 23, value = optionalAliasedEnum.number)
    }

    if (optionalStringPiece.isNotEmpty()) { 
        encoder.writeString(fieldNr = 24, value = optionalStringPiece)
    }

    if (optionalCord.isNotEmpty()) { 
        encoder.writeString(fieldNr = 25, value = optionalCord)
    }

    if (presenceMask[2]) { 
        encoder.writeMessage(fieldNr = 27, value = recursiveMessage.asInternal()) { encodeWith(it) }
    }

    if (repeatedInt32.isNotEmpty()) { 
        encoder.writePackedInt32(fieldNr = 31, value = repeatedInt32, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedInt32(repeatedInt32))
    }

    if (repeatedInt64.isNotEmpty()) { 
        encoder.writePackedInt64(fieldNr = 32, value = repeatedInt64, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedInt64(repeatedInt64))
    }

    if (repeatedUint32.isNotEmpty()) { 
        encoder.writePackedUInt32(fieldNr = 33, value = repeatedUint32, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedUInt32(repeatedUint32))
    }

    if (repeatedUint64.isNotEmpty()) { 
        encoder.writePackedUInt64(fieldNr = 34, value = repeatedUint64, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedUInt64(repeatedUint64))
    }

    if (repeatedSint32.isNotEmpty()) { 
        encoder.writePackedSInt32(fieldNr = 35, value = repeatedSint32, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedSInt32(repeatedSint32))
    }

    if (repeatedSint64.isNotEmpty()) { 
        encoder.writePackedSInt64(fieldNr = 36, value = repeatedSint64, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedSInt64(repeatedSint64))
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
        encoder.writePackedBool(fieldNr = 43, value = repeatedBool, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedBool(repeatedBool))
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
        encoder.writePackedEnum(fieldNr = 51, value = repeatedNestedEnum.map { it.number }, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedEnum(repeatedNestedEnum.map { it.number }))
    }

    if (repeatedForeignEnum.isNotEmpty()) { 
        encoder.writePackedEnum(fieldNr = 52, value = repeatedForeignEnum.map { it.number }, fieldSize = kotlinx.rpc.protobuf.internal.WireSize.packedEnum(repeatedForeignEnum.map { it.number }))
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 65, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapInt32Float.isNotEmpty()) { 
        mapInt32Float.forEach { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 67, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (mapBoolBool.isNotEmpty()) { 
        mapBoolBool.forEach { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal().apply { 
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 74, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (presenceMask[3]) { 
        encoder.writeMessage(fieldNr = 201, value = optionalBoolWrapper.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[4]) { 
        encoder.writeMessage(fieldNr = 202, value = optionalInt32Wrapper.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[5]) { 
        encoder.writeMessage(fieldNr = 203, value = optionalInt64Wrapper.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[6]) { 
        encoder.writeMessage(fieldNr = 204, value = optionalUint32Wrapper.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[7]) { 
        encoder.writeMessage(fieldNr = 205, value = optionalUint64Wrapper.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[8]) { 
        encoder.writeMessage(fieldNr = 206, value = optionalFloatWrapper.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[9]) { 
        encoder.writeMessage(fieldNr = 207, value = optionalDoubleWrapper.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[10]) { 
        encoder.writeMessage(fieldNr = 208, value = optionalStringWrapper.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[11]) { 
        encoder.writeMessage(fieldNr = 209, value = optionalBytesWrapper.asInternal()) { encodeWith(it) }
    }

    if (repeatedBoolWrapper.isNotEmpty()) { 
        repeatedBoolWrapper.forEach { 
            encoder.writeMessage(fieldNr = 211, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedInt32Wrapper.isNotEmpty()) { 
        repeatedInt32Wrapper.forEach { 
            encoder.writeMessage(fieldNr = 212, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedInt64Wrapper.isNotEmpty()) { 
        repeatedInt64Wrapper.forEach { 
            encoder.writeMessage(fieldNr = 213, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedUint32Wrapper.isNotEmpty()) { 
        repeatedUint32Wrapper.forEach { 
            encoder.writeMessage(fieldNr = 214, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedUint64Wrapper.isNotEmpty()) { 
        repeatedUint64Wrapper.forEach { 
            encoder.writeMessage(fieldNr = 215, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedFloatWrapper.isNotEmpty()) { 
        repeatedFloatWrapper.forEach { 
            encoder.writeMessage(fieldNr = 216, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedDoubleWrapper.isNotEmpty()) { 
        repeatedDoubleWrapper.forEach { 
            encoder.writeMessage(fieldNr = 217, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedStringWrapper.isNotEmpty()) { 
        repeatedStringWrapper.forEach { 
            encoder.writeMessage(fieldNr = 218, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedBytesWrapper.isNotEmpty()) { 
        repeatedBytesWrapper.forEach { 
            encoder.writeMessage(fieldNr = 219, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (presenceMask[12]) { 
        encoder.writeMessage(fieldNr = 301, value = optionalDuration.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[13]) { 
        encoder.writeMessage(fieldNr = 302, value = optionalTimestamp.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[14]) { 
        encoder.writeMessage(fieldNr = 303, value = optionalFieldMask.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[15]) { 
        encoder.writeMessage(fieldNr = 304, value = optionalStruct.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[16]) { 
        encoder.writeMessage(fieldNr = 305, value = optionalAny.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[17]) { 
        encoder.writeMessage(fieldNr = 306, value = optionalValue.asInternal()) { encodeWith(it) }
    }

    if (optionalNullValue != com.google.protobuf.kotlin.NullValue.NULL_VALUE) { 
        encoder.writeEnum(fieldNr = 307, value = optionalNullValue.number)
    }

    if (repeatedDuration.isNotEmpty()) { 
        repeatedDuration.forEach { 
            encoder.writeMessage(fieldNr = 311, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedTimestamp.isNotEmpty()) { 
        repeatedTimestamp.forEach { 
            encoder.writeMessage(fieldNr = 312, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedFieldmask.isNotEmpty()) { 
        repeatedFieldmask.forEach { 
            encoder.writeMessage(fieldNr = 313, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedStruct.isNotEmpty()) { 
        repeatedStruct.forEach { 
            encoder.writeMessage(fieldNr = 324, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedAny.isNotEmpty()) { 
        repeatedAny.forEach { 
            encoder.writeMessage(fieldNr = 315, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedValue.isNotEmpty()) { 
        repeatedValue.forEach { 
            encoder.writeMessage(fieldNr = 316, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (repeatedListValue.isNotEmpty()) { 
        repeatedListValue.forEach { 
            encoder.writeMessage(fieldNr = 317, value = it.asInternal()) { encodeWith(it) }
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
            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofUint32 -> { 
                encoder.writeUInt32(fieldNr = 111, value = value.value)
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofNestedMessage -> { 
                encoder.writeMessage(fieldNr = 112, value = value.value.asInternal()) { encodeWith(it) }
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofString -> { 
                encoder.writeString(fieldNr = 113, value = value.value)
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofBytes -> { 
                encoder.writeBytes(fieldNr = 114, value = value.value)
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofBool -> { 
                encoder.writeBool(fieldNr = 115, value = value.value)
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofUint64 -> { 
                encoder.writeUInt64(fieldNr = 116, value = value.value)
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofFloat -> { 
                encoder.writeFloat(fieldNr = 117, value = value.value)
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofDouble -> { 
                encoder.writeDouble(fieldNr = 118, value = value.value)
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofEnum -> { 
                encoder.writeEnum(fieldNr = 119, value = value.value.number)
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofNullValue -> { 
                encoder.writeEnum(fieldNr = 120, value = value.value.number)
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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
                if (!msg.presenceMask[0]) { 
                    msg.optionalNestedMessage = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.optionalNestedMessage.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal::decodeWith)
            }

            tag.fieldNr == 19 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[1]) { 
                    msg.optionalForeignMessage = com.google.protobuf_test_messages.proto3.ForeignMessageInternal()
                }

                decoder.readMessage(msg.optionalForeignMessage.asInternal(), com.google.protobuf_test_messages.proto3.ForeignMessageInternal::decodeWith)
            }

            tag.fieldNr == 21 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalNestedEnum = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 22 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalForeignEnum = com.google.protobuf_test_messages.proto3.ForeignEnum.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 23 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalAliasedEnum = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 24 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.optionalStringPiece = decoder.readString()
            }

            tag.fieldNr == 25 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.optionalCord = decoder.readString()
            }

            tag.fieldNr == 27 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[2]) { 
                    msg.recursiveMessage = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal()
                }

                decoder.readMessage(msg.recursiveMessage.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal::decodeWith)
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
                val elem = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal::decodeWith)
                (msg.repeatedNestedMessage as MutableList).add(elem)
            }

            tag.fieldNr == 49 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf_test_messages.proto3.ForeignMessageInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf_test_messages.proto3.ForeignMessageInternal::decodeWith)
                (msg.repeatedForeignMessage as MutableList).add(elem)
            }

            tag.fieldNr == 51 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedNestedEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.fromNumber(it) }
            }

            tag.fieldNr == 51 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
                (msg.repeatedNestedEnum as MutableList).add(elem)
            }

            tag.fieldNr == 52 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedForeignEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.proto3.ForeignEnum.fromNumber(it) }
            }

            tag.fieldNr == 52 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.proto3.ForeignEnum.fromNumber(decoder.readEnum())
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
                msg.packedNestedEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.fromNumber(it) }
            }

            tag.fieldNr == 88 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
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
                msg.unpackedNestedEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.fromNumber(it) }
            }

            tag.fieldNr == 102 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
                (msg.unpackedNestedEnum as MutableList).add(elem)
            }

            tag.fieldNr == 56 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal::decodeWith)
                    (msg.mapInt32Int32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 57 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal::decodeWith)
                    (msg.mapInt64Int64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 58 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal::decodeWith)
                    (msg.mapUint32Uint32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 59 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal::decodeWith)
                    (msg.mapUint64Uint64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 60 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal::decodeWith)
                    (msg.mapSint32Sint32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 61 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal::decodeWith)
                    (msg.mapSint64Sint64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 62 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal::decodeWith)
                    (msg.mapFixed32Fixed32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 63 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal::decodeWith)
                    (msg.mapFixed64Fixed64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 64 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal::decodeWith)
                    (msg.mapSfixed32Sfixed32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 65 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal::decodeWith)
                    (msg.mapSfixed64Sfixed64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 66 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal::decodeWith)
                    (msg.mapInt32Float as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 67 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal::decodeWith)
                    (msg.mapInt32Double as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 68 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal::decodeWith)
                    (msg.mapBoolBool as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 69 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal::decodeWith)
                    (msg.mapStringString as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 70 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal::decodeWith)
                    (msg.mapStringBytes as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 71 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal::decodeWith)
                    (msg.mapStringNestedMessage as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 72 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal::decodeWith)
                    (msg.mapStringForeignMessage as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 73 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal::decodeWith)
                    (msg.mapStringNestedEnum as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 74 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal::decodeWith)
                    (msg.mapStringForeignEnum as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 201 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[3]) { 
                    msg.optionalBoolWrapper = com.google.protobuf.kotlin.BoolValueInternal()
                }

                decoder.readMessage(msg.optionalBoolWrapper.asInternal(), com.google.protobuf.kotlin.BoolValueInternal::decodeWith)
            }

            tag.fieldNr == 202 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[4]) { 
                    msg.optionalInt32Wrapper = com.google.protobuf.kotlin.Int32ValueInternal()
                }

                decoder.readMessage(msg.optionalInt32Wrapper.asInternal(), com.google.protobuf.kotlin.Int32ValueInternal::decodeWith)
            }

            tag.fieldNr == 203 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[5]) { 
                    msg.optionalInt64Wrapper = com.google.protobuf.kotlin.Int64ValueInternal()
                }

                decoder.readMessage(msg.optionalInt64Wrapper.asInternal(), com.google.protobuf.kotlin.Int64ValueInternal::decodeWith)
            }

            tag.fieldNr == 204 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[6]) { 
                    msg.optionalUint32Wrapper = com.google.protobuf.kotlin.UInt32ValueInternal()
                }

                decoder.readMessage(msg.optionalUint32Wrapper.asInternal(), com.google.protobuf.kotlin.UInt32ValueInternal::decodeWith)
            }

            tag.fieldNr == 205 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[7]) { 
                    msg.optionalUint64Wrapper = com.google.protobuf.kotlin.UInt64ValueInternal()
                }

                decoder.readMessage(msg.optionalUint64Wrapper.asInternal(), com.google.protobuf.kotlin.UInt64ValueInternal::decodeWith)
            }

            tag.fieldNr == 206 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[8]) { 
                    msg.optionalFloatWrapper = com.google.protobuf.kotlin.FloatValueInternal()
                }

                decoder.readMessage(msg.optionalFloatWrapper.asInternal(), com.google.protobuf.kotlin.FloatValueInternal::decodeWith)
            }

            tag.fieldNr == 207 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[9]) { 
                    msg.optionalDoubleWrapper = com.google.protobuf.kotlin.DoubleValueInternal()
                }

                decoder.readMessage(msg.optionalDoubleWrapper.asInternal(), com.google.protobuf.kotlin.DoubleValueInternal::decodeWith)
            }

            tag.fieldNr == 208 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[10]) { 
                    msg.optionalStringWrapper = com.google.protobuf.kotlin.StringValueInternal()
                }

                decoder.readMessage(msg.optionalStringWrapper.asInternal(), com.google.protobuf.kotlin.StringValueInternal::decodeWith)
            }

            tag.fieldNr == 209 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[11]) { 
                    msg.optionalBytesWrapper = com.google.protobuf.kotlin.BytesValueInternal()
                }

                decoder.readMessage(msg.optionalBytesWrapper.asInternal(), com.google.protobuf.kotlin.BytesValueInternal::decodeWith)
            }

            tag.fieldNr == 211 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.BoolValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.BoolValueInternal::decodeWith)
                (msg.repeatedBoolWrapper as MutableList).add(elem)
            }

            tag.fieldNr == 212 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.Int32ValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.Int32ValueInternal::decodeWith)
                (msg.repeatedInt32Wrapper as MutableList).add(elem)
            }

            tag.fieldNr == 213 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.Int64ValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.Int64ValueInternal::decodeWith)
                (msg.repeatedInt64Wrapper as MutableList).add(elem)
            }

            tag.fieldNr == 214 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.UInt32ValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.UInt32ValueInternal::decodeWith)
                (msg.repeatedUint32Wrapper as MutableList).add(elem)
            }

            tag.fieldNr == 215 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.UInt64ValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.UInt64ValueInternal::decodeWith)
                (msg.repeatedUint64Wrapper as MutableList).add(elem)
            }

            tag.fieldNr == 216 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.FloatValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.FloatValueInternal::decodeWith)
                (msg.repeatedFloatWrapper as MutableList).add(elem)
            }

            tag.fieldNr == 217 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.DoubleValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.DoubleValueInternal::decodeWith)
                (msg.repeatedDoubleWrapper as MutableList).add(elem)
            }

            tag.fieldNr == 218 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.StringValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.StringValueInternal::decodeWith)
                (msg.repeatedStringWrapper as MutableList).add(elem)
            }

            tag.fieldNr == 219 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.BytesValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.BytesValueInternal::decodeWith)
                (msg.repeatedBytesWrapper as MutableList).add(elem)
            }

            tag.fieldNr == 301 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[12]) { 
                    msg.optionalDuration = com.google.protobuf.kotlin.DurationInternal()
                }

                decoder.readMessage(msg.optionalDuration.asInternal(), com.google.protobuf.kotlin.DurationInternal::decodeWith)
            }

            tag.fieldNr == 302 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[13]) { 
                    msg.optionalTimestamp = com.google.protobuf.kotlin.TimestampInternal()
                }

                decoder.readMessage(msg.optionalTimestamp.asInternal(), com.google.protobuf.kotlin.TimestampInternal::decodeWith)
            }

            tag.fieldNr == 303 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[14]) { 
                    msg.optionalFieldMask = com.google.protobuf.kotlin.FieldMaskInternal()
                }

                decoder.readMessage(msg.optionalFieldMask.asInternal(), com.google.protobuf.kotlin.FieldMaskInternal::decodeWith)
            }

            tag.fieldNr == 304 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[15]) { 
                    msg.optionalStruct = com.google.protobuf.kotlin.StructInternal()
                }

                decoder.readMessage(msg.optionalStruct.asInternal(), com.google.protobuf.kotlin.StructInternal::decodeWith)
            }

            tag.fieldNr == 305 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[16]) { 
                    msg.optionalAny = com.google.protobuf.kotlin.AnyInternal()
                }

                decoder.readMessage(msg.optionalAny.asInternal(), com.google.protobuf.kotlin.AnyInternal::decodeWith)
            }

            tag.fieldNr == 306 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[17]) { 
                    msg.optionalValue = com.google.protobuf.kotlin.ValueInternal()
                }

                decoder.readMessage(msg.optionalValue.asInternal(), com.google.protobuf.kotlin.ValueInternal::decodeWith)
            }

            tag.fieldNr == 307 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalNullValue = com.google.protobuf.kotlin.NullValue.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 311 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.DurationInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.DurationInternal::decodeWith)
                (msg.repeatedDuration as MutableList).add(elem)
            }

            tag.fieldNr == 312 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.TimestampInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.TimestampInternal::decodeWith)
                (msg.repeatedTimestamp as MutableList).add(elem)
            }

            tag.fieldNr == 313 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.FieldMaskInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.FieldMaskInternal::decodeWith)
                (msg.repeatedFieldmask as MutableList).add(elem)
            }

            tag.fieldNr == 324 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.StructInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.StructInternal::decodeWith)
                (msg.repeatedStruct as MutableList).add(elem)
            }

            tag.fieldNr == 315 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.AnyInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.AnyInternal::decodeWith)
                (msg.repeatedAny as MutableList).add(elem)
            }

            tag.fieldNr == 316 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.ValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.ValueInternal::decodeWith)
                (msg.repeatedValue as MutableList).add(elem)
            }

            tag.fieldNr == 317 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.ListValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.ListValueInternal::decodeWith)
                (msg.repeatedListValue as MutableList).add(elem)
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

            tag.fieldNr == 111 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofUint32(decoder.readUInt32())
            }

            tag.fieldNr == 112 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.oneofField as? com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofNestedMessage) ?: com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofNestedMessage(com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal()).also { 
                    msg.oneofField = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal::decodeWith)
            }

            tag.fieldNr == 113 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.oneofField = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofString(decoder.readString())
            }

            tag.fieldNr == 114 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.oneofField = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofBytes(decoder.readBytes())
            }

            tag.fieldNr == 115 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofBool(decoder.readBool())
            }

            tag.fieldNr == 116 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofUint64(decoder.readUInt64())
            }

            tag.fieldNr == 117 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.oneofField = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofFloat(decoder.readFloat())
            }

            tag.fieldNr == 118 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.oneofField = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofDouble(decoder.readDouble())
            }

            tag.fieldNr == 119 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofEnum(com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum()))
            }

            tag.fieldNr == 120 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofNullValue(com.google.protobuf.kotlin.NullValue.fromNumber(decoder.readEnum()))
            }

            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.computeSize(): Int { 
    var __result = 0
    if (optionalInt32 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(optionalInt32))
    }

    if (optionalInt64 != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(optionalInt64))
    }

    if (optionalUint32 != 0u) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(optionalUint32))
    }

    if (optionalUint64 != 0uL) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(optionalUint64))
    }

    if (optionalSint32 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt32(optionalSint32))
    }

    if (optionalSint64 != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt64(optionalSint64))
    }

    if (optionalFixed32 != 0u) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(7, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.fixed32(optionalFixed32))
    }

    if (optionalFixed64 != 0uL) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(8, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.fixed64(optionalFixed64))
    }

    if (optionalSfixed32 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(9, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.sFixed32(optionalSfixed32))
    }

    if (optionalSfixed64 != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(10, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.sFixed64(optionalSfixed64))
    }

    if (optionalFloat != 0.0f) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(11, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(optionalFloat))
    }

    if (optionalDouble != 0.0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(12, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(optionalDouble))
    }

    if (optionalBool != false) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(13, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(optionalBool))
    }

    if (optionalString.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(optionalString).let { kotlinx.rpc.protobuf.internal.WireSize.tag(14, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (optionalBytes.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(optionalBytes).let { kotlinx.rpc.protobuf.internal.WireSize.tag(15, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[0]) { 
        __result += optionalNestedMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(18, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[1]) { 
        __result += optionalForeignMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(19, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (optionalNestedEnum != com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.FOO) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(21, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(optionalNestedEnum.number))
    }

    if (optionalForeignEnum != com.google.protobuf_test_messages.proto3.ForeignEnum.FOREIGN_FOO) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(22, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(optionalForeignEnum.number))
    }

    if (optionalAliasedEnum != com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum.ALIAS_FOO) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(23, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(optionalAliasedEnum.number))
    }

    if (optionalStringPiece.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(optionalStringPiece).let { kotlinx.rpc.protobuf.internal.WireSize.tag(24, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (optionalCord.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(optionalCord).let { kotlinx.rpc.protobuf.internal.WireSize.tag(25, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[2]) { 
        __result += recursiveMessage.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(27, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedInt32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedInt32(repeatedInt32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(31, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedInt64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedInt64(repeatedInt64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(32, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedUint32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedUInt32(repeatedUint32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(33, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedUint64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedUInt64(repeatedUint64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(34, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedSint32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedSInt32(repeatedSint32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(35, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedSint64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedSInt64(repeatedSint64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(36, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedFixed32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedFixed32(repeatedFixed32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(37, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedFixed64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedFixed64(repeatedFixed64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(38, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedSfixed32.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedSFixed32(repeatedSfixed32).let { kotlinx.rpc.protobuf.internal.WireSize.tag(39, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedSfixed64.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedSFixed64(repeatedSfixed64).let { kotlinx.rpc.protobuf.internal.WireSize.tag(40, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedFloat.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedFloat(repeatedFloat).let { kotlinx.rpc.protobuf.internal.WireSize.tag(41, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedDouble.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedDouble(repeatedDouble).let { kotlinx.rpc.protobuf.internal.WireSize.tag(42, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedBool.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedBool(repeatedBool).let { kotlinx.rpc.protobuf.internal.WireSize.tag(43, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
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
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedEnum(repeatedNestedEnum.map { it.number }).let { kotlinx.rpc.protobuf.internal.WireSize.tag(51, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedForeignEnum.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.packedEnum(repeatedForeignEnum.map { it.number }).let { kotlinx.rpc.protobuf.internal.WireSize.tag(52, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
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
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt64Int64.isNotEmpty()) { 
        __result += mapInt64Int64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapUint32Uint32.isNotEmpty()) { 
        __result += mapUint32Uint32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapUint64Uint64.isNotEmpty()) { 
        __result += mapUint64Uint64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSint32Sint32.isNotEmpty()) { 
        __result += mapSint32Sint32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSint64Sint64.isNotEmpty()) { 
        __result += mapSint64Sint64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapFixed32Fixed32.isNotEmpty()) { 
        __result += mapFixed32Fixed32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapFixed64Fixed64.isNotEmpty()) { 
        __result += mapFixed64Fixed64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSfixed32Sfixed32.isNotEmpty()) { 
        __result += mapSfixed32Sfixed32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSfixed64Sfixed64.isNotEmpty()) { 
        __result += mapSfixed64Sfixed64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt32Float.isNotEmpty()) { 
        __result += mapInt32Float.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt32Double.isNotEmpty()) { 
        __result += mapInt32Double.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapBoolBool.isNotEmpty()) { 
        __result += mapBoolBool.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringString.isNotEmpty()) { 
        __result += mapStringString.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringBytes.isNotEmpty()) { 
        __result += mapStringBytes.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringNestedMessage.isNotEmpty()) { 
        __result += mapStringNestedMessage.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringForeignMessage.isNotEmpty()) { 
        __result += mapStringForeignMessage.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringNestedEnum.isNotEmpty()) { 
        __result += mapStringNestedEnum.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringForeignEnum.isNotEmpty()) { 
        __result += mapStringForeignEnum.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (presenceMask[3]) { 
        __result += optionalBoolWrapper.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(201, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[4]) { 
        __result += optionalInt32Wrapper.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(202, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[5]) { 
        __result += optionalInt64Wrapper.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(203, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[6]) { 
        __result += optionalUint32Wrapper.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(204, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[7]) { 
        __result += optionalUint64Wrapper.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(205, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[8]) { 
        __result += optionalFloatWrapper.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(206, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[9]) { 
        __result += optionalDoubleWrapper.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(207, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[10]) { 
        __result += optionalStringWrapper.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(208, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[11]) { 
        __result += optionalBytesWrapper.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(209, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (repeatedBoolWrapper.isNotEmpty()) { 
        __result += repeatedBoolWrapper.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(211, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedInt32Wrapper.isNotEmpty()) { 
        __result += repeatedInt32Wrapper.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(212, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedInt64Wrapper.isNotEmpty()) { 
        __result += repeatedInt64Wrapper.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(213, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedUint32Wrapper.isNotEmpty()) { 
        __result += repeatedUint32Wrapper.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(214, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedUint64Wrapper.isNotEmpty()) { 
        __result += repeatedUint64Wrapper.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(215, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedFloatWrapper.isNotEmpty()) { 
        __result += repeatedFloatWrapper.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(216, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedDoubleWrapper.isNotEmpty()) { 
        __result += repeatedDoubleWrapper.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(217, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedStringWrapper.isNotEmpty()) { 
        __result += repeatedStringWrapper.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(218, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedBytesWrapper.isNotEmpty()) { 
        __result += repeatedBytesWrapper.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(219, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (presenceMask[12]) { 
        __result += optionalDuration.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(301, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[13]) { 
        __result += optionalTimestamp.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(302, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[14]) { 
        __result += optionalFieldMask.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(303, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[15]) { 
        __result += optionalStruct.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(304, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[16]) { 
        __result += optionalAny.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(305, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[17]) { 
        __result += optionalValue.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(306, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (optionalNullValue != com.google.protobuf.kotlin.NullValue.NULL_VALUE) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(307, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(optionalNullValue.number))
    }

    if (repeatedDuration.isNotEmpty()) { 
        __result += repeatedDuration.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(311, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedTimestamp.isNotEmpty()) { 
        __result += repeatedTimestamp.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(312, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedFieldmask.isNotEmpty()) { 
        __result += repeatedFieldmask.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(313, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedStruct.isNotEmpty()) { 
        __result += repeatedStruct.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(324, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedAny.isNotEmpty()) { 
        __result += repeatedAny.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(315, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedValue.isNotEmpty()) { 
        __result += repeatedValue.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(316, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (repeatedListValue.isNotEmpty()) { 
        __result += repeatedListValue.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(317, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (fieldname1 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(401, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(fieldname1))
    }

    if (fieldName2 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(402, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(fieldName2))
    }

    if (FieldName3 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(403, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(FieldName3))
    }

    if (field_Name4_ != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(404, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(field_Name4_))
    }

    if (field0name5 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(405, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(field0name5))
    }

    if (field_0Name6 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(406, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(field_0Name6))
    }

    if (fieldName7 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(407, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(fieldName7))
    }

    if (FieldName8 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(408, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(FieldName8))
    }

    if (field_Name9 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(409, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(field_Name9))
    }

    if (Field_Name10 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(410, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(Field_Name10))
    }

    if (FIELD_NAME11 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(411, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(FIELD_NAME11))
    }

    if (FIELDName12 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(412, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(FIELDName12))
    }

    if (_FieldName13 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(413, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(_FieldName13))
    }

    if (__FieldName14 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(414, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(__FieldName14))
    }

    if (field_Name15 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(415, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(field_Name15))
    }

    if (field__Name16 != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(416, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(field__Name16))
    }

    if (fieldName17__ != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(417, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(fieldName17__))
    }

    if (FieldName18__ != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(418, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(FieldName18__))
    }

    oneofField?.also { 
        when (val value = it) { 
            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofUint32 -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(111, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(value.value))
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofNestedMessage -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(112, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofString -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(113, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofBytes -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(114, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofBool -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(115, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(value.value))
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofUint64 -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(116, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(value.value))
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofFloat -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(117, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(value.value))
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofDouble -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(118, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(value.value))
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofEnum -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(119, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(value.value.number))
            }

            is com.google.protobuf_test_messages.proto3.TestAllTypesProto3.OneofField.OneofNullValue -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(120, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(value.value.number))
            }
        }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal { 
    return this as? com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.ForeignMessageInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.ForeignMessageInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (c != 0) { 
        encoder.writeInt32(fieldNr = 1, value = c)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.ForeignMessageInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.ForeignMessageInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.ForeignMessageInternal.computeSize(): Int { 
    var __result = 0
    if (c != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(c))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.ForeignMessage.asInternal(): com.google.protobuf_test_messages.proto3.ForeignMessageInternal { 
    return this as? com.google.protobuf_test_messages.proto3.ForeignMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.NullHypothesisProto3.asInternal(): com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal { 
    return this as? com.google.protobuf_test_messages.proto3.NullHypothesisProto3Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.EnumOnlyProto3.asInternal(): com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal { 
    return this as? com.google.protobuf_test_messages.proto3.EnumOnlyProto3Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[0]) { 
        corecursive.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (a != 0) { 
        encoder.writeInt32(fieldNr = 1, value = a)
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 2, value = corecursive.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.a = decoder.readInt32()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.corecursive = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal()
                }

                decoder.readMessage(msg.corecursive.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal::decodeWith)
            }

            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal.computeSize(): Int { 
    var __result = 0
    if (a != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(a))
    }

    if (presenceMask[0]) { 
        __result += corecursive.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedMessage.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal { 
    return this as? com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (value != 0) { 
        encoder.writeInt32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(key))
    }

    if (value != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32Int32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0L) { 
        encoder.writeInt64(fieldNr = 1, value = key)
    }

    if (value != 0L) { 
        encoder.writeInt64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(key))
    }

    if (value != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt64Int64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0u) { 
        encoder.writeUInt32(fieldNr = 1, value = key)
    }

    if (value != 0u) { 
        encoder.writeUInt32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0u) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(key))
    }

    if (value != 0u) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint32Uint32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0uL) { 
        encoder.writeUInt64(fieldNr = 1, value = key)
    }

    if (value != 0uL) { 
        encoder.writeUInt64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0uL) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(key))
    }

    if (value != 0uL) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapUint64Uint64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0) { 
        encoder.writeSInt32(fieldNr = 1, value = key)
    }

    if (value != 0) { 
        encoder.writeSInt32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt32(key))
    }

    if (value != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint32Sint32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0L) { 
        encoder.writeSInt64(fieldNr = 1, value = key)
    }

    if (value != 0L) { 
        encoder.writeSInt64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt64(key))
    }

    if (value != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.sInt64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSint64Sint64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0u) { 
        encoder.writeFixed32(fieldNr = 1, value = key)
    }

    if (value != 0u) { 
        encoder.writeFixed32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0u) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.fixed32(key))
    }

    if (value != 0u) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.fixed32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed32Fixed32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0uL) { 
        encoder.writeFixed64(fieldNr = 1, value = key)
    }

    if (value != 0uL) { 
        encoder.writeFixed64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0uL) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.fixed64(key))
    }

    if (value != 0uL) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.fixed64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapFixed64Fixed64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0) { 
        encoder.writeSFixed32(fieldNr = 1, value = key)
    }

    if (value != 0) { 
        encoder.writeSFixed32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.sFixed32(key))
    }

    if (value != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.sFixed32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed32Sfixed32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0L) { 
        encoder.writeSFixed64(fieldNr = 1, value = key)
    }

    if (value != 0L) { 
        encoder.writeSFixed64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.sFixed64(key))
    }

    if (value != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.sFixed64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapSfixed64Sfixed64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (value != 0.0f) { 
        encoder.writeFloat(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(key))
    }

    if (value != 0.0f) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32FloatEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != 0) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (value != 0.0) { 
        encoder.writeDouble(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(key))
    }

    if (value != 0.0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapInt32DoubleEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key != false) { 
        encoder.writeBool(fieldNr = 1, value = key)
    }

    if (value != false) { 
        encoder.writeBool(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key != false) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(key))
    }

    if (value != false) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapBoolBoolEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (value.isNotEmpty()) { 
        encoder.writeString(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (value.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringStringEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (value.isNotEmpty()) { 
        encoder.writeBytes(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (value.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringBytesEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[0]) { 
        value.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.value = com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.value.asInternal(), com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.NestedMessageInternal::decodeWith)
            }

            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[0]) { 
        __result += value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedMessageEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[0]) { 
        value.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.value = com.google.protobuf_test_messages.proto3.ForeignMessageInternal()
                }

                decoder.readMessage(msg.value.asInternal(), com.google.protobuf_test_messages.proto3.ForeignMessageInternal::decodeWith)
            }

            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[0]) { 
        __result += value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignMessageEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (value != com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.FOO) { 
        encoder.writeEnum(fieldNr = 2, value = value.number)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.fromNumber(decoder.readEnum())
            }

            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (value != com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.FOO) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(value.number))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringNestedEnumEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (value != com.google.protobuf_test_messages.proto3.ForeignEnum.FOREIGN_FOO) { 
        encoder.writeEnum(fieldNr = 2, value = value.number)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = com.google.protobuf_test_messages.proto3.ForeignEnum.fromNumber(decoder.readEnum())
            }

            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (value != com.google.protobuf_test_messages.proto3.ForeignEnum.FOREIGN_FOO) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(value.number))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal.asInternal(): com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MapStringForeignEnumEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.ForeignEnum.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.proto3.ForeignEnum { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.proto3.ForeignEnum.FOREIGN_FOO
        }

        1 -> { 
            com.google.protobuf_test_messages.proto3.ForeignEnum.FOREIGN_BAR
        }

        2 -> { 
            com.google.protobuf_test_messages.proto3.ForeignEnum.FOREIGN_BAZ
        }

        else -> { 
            com.google.protobuf_test_messages.proto3.ForeignEnum.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.FOO
        }

        1 -> { 
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.BAR
        }

        2 -> { 
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.BAZ
        }

        -1 -> { 
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.NEG
        }

        else -> { 
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3.NestedEnum.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum.ALIAS_FOO
        }

        1 -> { 
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum.ALIAS_BAR
        }

        2 -> { 
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum.ALIAS_BAZ
        }

        else -> { 
            com.google.protobuf_test_messages.proto3.TestAllTypesProto3.AliasedEnum.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.proto3.EnumOnlyProto3.Bool.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.proto3.EnumOnlyProto3.Bool { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.proto3.EnumOnlyProto3.Bool.kFalse
        }

        1 -> { 
            com.google.protobuf_test_messages.proto3.EnumOnlyProto3.Bool.kTrue
        }

        else -> { 
            com.google.protobuf_test_messages.proto3.EnumOnlyProto3.Bool.UNRECOGNIZED(number)
        }
    }
}

