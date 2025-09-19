@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf_test_messages.editions.proto2

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

    override var optionalInt32: Int? = null
    override var optionalInt64: Long? = null
    override var optionalUint32: UInt? = null
    override var optionalUint64: ULong? = null
    override var optionalSint32: Int? = null
    override var optionalSint64: Long? = null
    override var optionalFixed32: UInt? = null
    override var optionalFixed64: ULong? = null
    override var optionalSfixed32: Int? = null
    override var optionalSfixed64: Long? = null
    override var optionalFloat: Float? = null
    override var optionalDouble: Double? = null
    override var optionalBool: Boolean? = null
    override var optionalString: String? = null
    override var optionalBytes: ByteArray? = null
    override var optionalNestedMessage: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage by MsgFieldDelegate(PresenceIndices.optionalNestedMessage) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal() }
    override var optionalForeignMessage: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 by MsgFieldDelegate(PresenceIndices.optionalForeignMessage) { com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal() }
    override var optionalNestedEnum: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum? = null
    override var optionalForeignEnum: com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2? = null
    override var optionalStringPiece: String? = null
    override var optionalCord: String? = null
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
    override var fieldname1: Int? = null
    override var fieldName2: Int? = null
    override var FieldName3: Int? = null
    override var field_Name4_: Int? = null
    override var field0name5: Int? = null
    override var field_0Name6: Int? = null
    override var fieldName7: Int? = null
    override var FieldName8: Int? = null
    override var field_Name9: Int? = null
    override var Field_Name10: Int? = null
    override var FIELD_NAME11: Int? = null
    override var FIELDName12: Int? = null
    override var _FieldName13: Int? = null
    override var __FieldName14: Int? = null
    override var field_Name15: Int? = null
    override var field__Name16: Int? = null
    override var fieldName17__: Int? = null
    override var FieldName18__: Int? = null
    override var messageSetCorrect: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect by MsgFieldDelegate(PresenceIndices.messageSetCorrect) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal() }
    override var oneofField: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField? = null

    class NestedMessageInternal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val a: Int = 0
            const val corecursive: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        override var a: Int? = null
        override var corecursive: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2 by MsgFieldDelegate(PresenceIndices.corecursive) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Int by MsgFieldDelegate(PresenceIndices.value) { 0 }

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

        var key: Long by MsgFieldDelegate(PresenceIndices.key) { 0L }
        var value: Long by MsgFieldDelegate(PresenceIndices.value) { 0L }

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

        var key: UInt by MsgFieldDelegate(PresenceIndices.key) { 0u }
        var value: UInt by MsgFieldDelegate(PresenceIndices.value) { 0u }

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

        var key: ULong by MsgFieldDelegate(PresenceIndices.key) { 0uL }
        var value: ULong by MsgFieldDelegate(PresenceIndices.value) { 0uL }

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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Int by MsgFieldDelegate(PresenceIndices.value) { 0 }

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

        var key: Long by MsgFieldDelegate(PresenceIndices.key) { 0L }
        var value: Long by MsgFieldDelegate(PresenceIndices.value) { 0L }

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

        var key: UInt by MsgFieldDelegate(PresenceIndices.key) { 0u }
        var value: UInt by MsgFieldDelegate(PresenceIndices.value) { 0u }

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

        var key: ULong by MsgFieldDelegate(PresenceIndices.key) { 0uL }
        var value: ULong by MsgFieldDelegate(PresenceIndices.value) { 0uL }

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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Int by MsgFieldDelegate(PresenceIndices.value) { 0 }

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

        var key: Long by MsgFieldDelegate(PresenceIndices.key) { 0L }
        var value: Long by MsgFieldDelegate(PresenceIndices.value) { 0L }

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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Boolean by MsgFieldDelegate(PresenceIndices.value) { false }

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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Float by MsgFieldDelegate(PresenceIndices.value) { 0.0f }

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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: Double by MsgFieldDelegate(PresenceIndices.value) { 0.0 }

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

        var key: Int by MsgFieldDelegate(PresenceIndices.key) { 0 }
        var value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal() }

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

        var key: Boolean by MsgFieldDelegate(PresenceIndices.key) { false }
        var value: Boolean by MsgFieldDelegate(PresenceIndices.value) { false }

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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: String by MsgFieldDelegate(PresenceIndices.value) { "" }

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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: ByteArray by MsgFieldDelegate(PresenceIndices.value) { byteArrayOf() }

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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal() }

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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal() }

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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum.FOO }

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

        var key: String by MsgFieldDelegate(PresenceIndices.key) { "" }
        var value: com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2 by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2.FOREIGN_FOO }

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

        override var groupInt32: Int? = null
        override var groupUint32: UInt? = null

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

        override var groupInt32: Int? = null
        override var groupUint32: UInt? = null

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MessageSetCorrectInternal: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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

        override var str: String? = null

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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

        override var i: Int? = null

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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

        override var oneofField: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField? = null

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
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

    override var c: Int? = null

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
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

    override var groupInt32: Int? = null
    override var groupUint32: UInt? = null

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.GroupField> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.GroupField): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.GroupField { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
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

    override var optionalInt32: Int? = null
    override var optionalString: String? = null
    override var nestedMessage: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 by MsgFieldDelegate(PresenceIndices.nestedMessage) { com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal() }
    override var optionalgroup: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup by MsgFieldDelegate(PresenceIndices.optionalgroup) { com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal() }
    override var optionalBool: Boolean? = null
    override var repeatedInt32: List<kotlin.Int> by MsgFieldDelegate { mutableListOf() }

    class OptionalGroupInternal: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            const val a: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        override var a: Int? = null

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
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
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
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
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
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

    override var data: String? = null

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.OneStringProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.OneStringProto2): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.OneStringProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
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

    override var inline: Int? = null
    override var concept: String? = null
    override var requires: List<kotlin.String> by MsgFieldDelegate { mutableListOf() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
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

    class NestedMessageInternal: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 3) { 
        private object PresenceIndices { 
            const val a: Int = 0
            const val corecursive: Int = 1
            const val optionalCorecursive: Int = 2
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        override var a: Int by MsgFieldDelegate(PresenceIndices.a) { 0 }
        override var corecursive: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2 by MsgFieldDelegate(PresenceIndices.corecursive) { com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal() }
        override var optionalCorecursive: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2 by MsgFieldDelegate(PresenceIndices.optionalCorecursive) { com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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

        override var groupInt32: Int by MsgFieldDelegate(PresenceIndices.groupInt32) { 0 }
        override var groupUint32: UInt by MsgFieldDelegate(PresenceIndices.groupUint32) { 0u }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class MessageSetCorrectInternal: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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

        override var str: String by MsgFieldDelegate(PresenceIndices.str) { "" }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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

        override var i: Int by MsgFieldDelegate(PresenceIndices.i) { 0 }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
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

    override var largeOneof: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof? = null

    class A1Internal: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
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
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5> { 
            override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5 { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal.decodeWith(msg, it)
                    }
                    msg.checkRequiredFields()
                    return msg
                }
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.editions.proto2.TestLargeOneof> { 
        override fun encode(value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.GroupField.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.GroupField { 
    val msg = com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes { 
    val msg = com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.OneStringProto2.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.OneStringProto2 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywords { 
    val msg = com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.DataInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MultiWordGroupFieldInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension1 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrectExtension2 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup { 
    val msg = com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.OptionalGroupInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.DataInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrect { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension1 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.MessageSetCorrectExtension2 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5.Companion.invoke(body: com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal.() -> Unit): com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5 { 
    val msg = com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal().apply(body)
    msg.checkRequiredFields()
    return msg
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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
