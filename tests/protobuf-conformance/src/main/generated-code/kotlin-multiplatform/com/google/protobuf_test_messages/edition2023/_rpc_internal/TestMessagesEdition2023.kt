@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf_test_messages.edition2023

import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

class ComplexMessageInternal: com.google.protobuf_test_messages.edition2023.ComplexMessage, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        const val d: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    override var d: Int? = null

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.edition2023.ComplexMessage> { 
        override fun encode(value: com.google.protobuf_test_messages.edition2023.ComplexMessage): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.edition2023.ComplexMessage { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.edition2023.ComplexMessageInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.edition2023.ComplexMessageInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class TestAllTypesEdition2023Internal: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 24) { 
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
    override var optionalNestedMessage: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage by MsgFieldDelegate(PresenceIndices.optionalNestedMessage) { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal() }
    override var optionalForeignMessage: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023 by MsgFieldDelegate(PresenceIndices.optionalForeignMessage) { com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal() }
    override var optionalNestedEnum: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum? = null
    override var optionalForeignEnum: com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023? = null
    override var optionalStringPiece: String? = null
    override var optionalCord: String? = null
    override var recursiveMessage: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023 by MsgFieldDelegate(PresenceIndices.recursiveMessage) { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal() }
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
    override var repeatedNestedMessage: List<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignMessage: List<com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023> by MsgFieldDelegate { mutableListOf() }
    override var repeatedNestedEnum: List<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum> by MsgFieldDelegate { mutableListOf() }
    override var repeatedForeignEnum: List<com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023> by MsgFieldDelegate { mutableListOf() }
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
    override var packedNestedEnum: List<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum> by MsgFieldDelegate { mutableListOf() }
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
    override var unpackedNestedEnum: List<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum> by MsgFieldDelegate { mutableListOf() }
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
    override var mapStringNestedMessage: Map<kotlin.String, com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignMessage: Map<kotlin.String, com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringNestedEnum: Map<kotlin.String, com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum> by MsgFieldDelegate { mutableMapOf() }
    override var mapStringForeignEnum: Map<kotlin.String, com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023> by MsgFieldDelegate { mutableMapOf() }
    override var groupliketype: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType by MsgFieldDelegate(PresenceIndices.groupliketype) { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal() }
    override var delimitedField: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType by MsgFieldDelegate(PresenceIndices.delimitedField) { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal() }
    override var oneofField: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField? = null

    class NestedMessageInternal: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
        private object PresenceIndices { 
            const val a: Int = 0
            const val corecursive: Int = 1
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        override val _size: Int by lazy { computeSize() }

        override var a: Int? = null
        override var corecursive: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023 by MsgFieldDelegate(PresenceIndices.corecursive) { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage> { 
            override fun encode(value: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage): kotlinx.rpc.protobuf.input.stream.InputStream { 
                val buffer = kotlinx.io.Buffer()
                val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
                kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                    value.asInternal().encodeWith(encoder)
                }
                encoder.flush()
                return buffer.asInputStream()
            }

            override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage { 
                kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                    val msg = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal()
                    kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                        com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal.decodeWith(msg, it)
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
        var value: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal() }

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
        var value: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023 by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal() }

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
        var value: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.FOO }

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
        var value: com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023 by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.FOREIGN_FOO }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        companion object
    }

    class GroupLikeTypeInternal: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 2) { 
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

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023> { 
        override fun encode(value: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class ForeignMessageEdition2023Internal: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        const val c: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    override var c: Int? = null

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023> { 
        override fun encode(value: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023 { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class GroupLikeTypeInternal: com.google.protobuf_test_messages.edition2023.GroupLikeType, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        const val c: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    override var c: Int? = null

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf_test_messages.edition2023.GroupLikeType> { 
        override fun encode(value: com.google.protobuf_test_messages.edition2023.GroupLikeType): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf_test_messages.edition2023.GroupLikeType { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

operator fun com.google.protobuf_test_messages.edition2023.ComplexMessage.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.ComplexMessageInternal.() -> Unit): com.google.protobuf_test_messages.edition2023.ComplexMessage { 
    val msg = com.google.protobuf_test_messages.edition2023.ComplexMessageInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.() -> Unit): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023 { 
    val msg = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal.() -> Unit): com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023 { 
    val msg = com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.edition2023.GroupLikeType.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal.() -> Unit): com.google.protobuf_test_messages.edition2023.GroupLikeType { 
    val msg = com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal.() -> Unit): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage { 
    val msg = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType.Companion.invoke(body: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal.() -> Unit): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType { 
    val msg = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.ComplexMessageInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.ComplexMessageInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    d?.also { 
        encoder.writeInt32(fieldNr = 1, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.ComplexMessageInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.ComplexMessageInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.d = decoder.readInt32()
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

private fun com.google.protobuf_test_messages.edition2023.ComplexMessageInternal.computeSize(): Int { 
    var __result = 0
    d?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.ComplexMessage.asInternal(): com.google.protobuf_test_messages.edition2023.ComplexMessageInternal { 
    return this as? com.google.protobuf_test_messages.edition2023.ComplexMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.checkRequiredFields() { 
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
        groupliketype.asInternal().checkRequiredFields()
    }

    if (presenceMask[23]) { 
        delimitedField.asInternal().checkRequiredFields()
    }

    oneofField?.also { 
        when { 
            it is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofNestedMessage -> { 
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

    mapStringNestedMessage.values.forEach { 
        it.asInternal().checkRequiredFields()
    }

    mapStringForeignMessage.values.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal().apply { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 74, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }

    if (presenceMask[22]) { 
        encoder.writeGroupMessage(fieldNr = 201, value = groupliketype.asInternal()) { encodeWith(it) }
    }

    if (presenceMask[23]) { 
        encoder.writeGroupMessage(fieldNr = 202, value = delimitedField.asInternal()) { encodeWith(it) }
    }

    oneofField?.also { 
        when (val value = it) { 
            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofUint32 -> { 
                encoder.writeUInt32(fieldNr = 111, value = value.value)
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofNestedMessage -> { 
                encoder.writeMessage(fieldNr = 112, value = value.value.asInternal()) { encodeWith(it) }
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofString -> { 
                encoder.writeString(fieldNr = 113, value = value.value)
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofBytes -> { 
                encoder.writeBytes(fieldNr = 114, value = value.value)
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofBool -> { 
                encoder.writeBool(fieldNr = 115, value = value.value)
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofUint64 -> { 
                encoder.writeUInt64(fieldNr = 116, value = value.value)
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofFloat -> { 
                encoder.writeFloat(fieldNr = 117, value = value.value)
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofDouble -> { 
                encoder.writeDouble(fieldNr = 118, value = value.value)
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofEnum -> { 
                encoder.writeEnum(fieldNr = 119, value = value.value.number)
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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
                    msg.optionalNestedMessage = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.optionalNestedMessage.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal::decodeWith)
            }

            tag.fieldNr == 19 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[16]) { 
                    msg.optionalForeignMessage = com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal()
                }

                decoder.readMessage(msg.optionalForeignMessage.asInternal(), com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal::decodeWith)
            }

            tag.fieldNr == 21 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalNestedEnum = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 22 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.optionalForeignEnum = com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 24 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.optionalStringPiece = decoder.readString()
            }

            tag.fieldNr == 25 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.optionalCord = decoder.readString()
            }

            tag.fieldNr == 27 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[21]) { 
                    msg.recursiveMessage = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal()
                }

                decoder.readMessage(msg.recursiveMessage.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal::decodeWith)
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
                val elem = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal::decodeWith)
                (msg.repeatedNestedMessage as MutableList).add(elem)
            }

            tag.fieldNr == 49 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal::decodeWith)
                (msg.repeatedForeignMessage as MutableList).add(elem)
            }

            tag.fieldNr == 51 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedNestedEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.fromNumber(it) }
            }

            tag.fieldNr == 51 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
                (msg.repeatedNestedEnum as MutableList).add(elem)
            }

            tag.fieldNr == 52 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.repeatedForeignEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.fromNumber(it) }
            }

            tag.fieldNr == 52 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.fromNumber(decoder.readEnum())
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
                msg.packedNestedEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.fromNumber(it) }
            }

            tag.fieldNr == 88 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
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
                msg.unpackedNestedEnum += decoder.readPackedEnum().map { com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.fromNumber(it) }
            }

            tag.fieldNr == 102 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                val elem = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
                (msg.unpackedNestedEnum as MutableList).add(elem)
            }

            tag.fieldNr == 56 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal::decodeWith)
                    (msg.mapInt32Int32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 57 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal::decodeWith)
                    (msg.mapInt64Int64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 58 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal::decodeWith)
                    (msg.mapUint32Uint32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 59 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal::decodeWith)
                    (msg.mapUint64Uint64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 60 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal::decodeWith)
                    (msg.mapSint32Sint32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 61 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal::decodeWith)
                    (msg.mapSint64Sint64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 62 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal::decodeWith)
                    (msg.mapFixed32Fixed32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 63 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal::decodeWith)
                    (msg.mapFixed64Fixed64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 64 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal::decodeWith)
                    (msg.mapSfixed32Sfixed32 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 65 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal::decodeWith)
                    (msg.mapSfixed64Sfixed64 as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 66 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal::decodeWith)
                    (msg.mapInt32Float as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 67 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal::decodeWith)
                    (msg.mapInt32Double as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 68 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal::decodeWith)
                    (msg.mapBoolBool as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 69 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal::decodeWith)
                    (msg.mapStringString as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 70 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal::decodeWith)
                    (msg.mapStringBytes as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 71 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal::decodeWith)
                    (msg.mapStringNestedMessage as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 72 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal::decodeWith)
                    (msg.mapStringForeignMessage as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 73 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal::decodeWith)
                    (msg.mapStringNestedEnum as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 74 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal::decodeWith)
                    (msg.mapStringForeignEnum as MutableMap)[key] = value
                }
            }

            tag.fieldNr == 201 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.START_GROUP -> { 
                if (!msg.presenceMask[22]) { 
                    msg.groupliketype = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal()
                }

                com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal.decodeWith(msg.groupliketype.asInternal(), decoder, tag)
            }

            tag.fieldNr == 202 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.START_GROUP -> { 
                if (!msg.presenceMask[23]) { 
                    msg.delimitedField = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal()
                }

                com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal.decodeWith(msg.delimitedField.asInternal(), decoder, tag)
            }

            tag.fieldNr == 111 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofUint32(decoder.readUInt32())
            }

            tag.fieldNr == 112 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.oneofField as? com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofNestedMessage) ?: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofNestedMessage(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal()).also { 
                    msg.oneofField = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal::decodeWith)
            }

            tag.fieldNr == 113 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.oneofField = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofString(decoder.readString())
            }

            tag.fieldNr == 114 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.oneofField = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofBytes(decoder.readBytes())
            }

            tag.fieldNr == 115 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofBool(decoder.readBool())
            }

            tag.fieldNr == 116 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofUint64(decoder.readUInt64())
            }

            tag.fieldNr == 117 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.oneofField = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofFloat(decoder.readFloat())
            }

            tag.fieldNr == 118 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.oneofField = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofDouble(decoder.readDouble())
            }

            tag.fieldNr == 119 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofField = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofEnum(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum()))
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.computeSize(): Int { 
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
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt64Int64.isNotEmpty()) { 
        __result += mapInt64Int64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapUint32Uint32.isNotEmpty()) { 
        __result += mapUint32Uint32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapUint64Uint64.isNotEmpty()) { 
        __result += mapUint64Uint64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSint32Sint32.isNotEmpty()) { 
        __result += mapSint32Sint32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSint64Sint64.isNotEmpty()) { 
        __result += mapSint64Sint64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapFixed32Fixed32.isNotEmpty()) { 
        __result += mapFixed32Fixed32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapFixed64Fixed64.isNotEmpty()) { 
        __result += mapFixed64Fixed64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSfixed32Sfixed32.isNotEmpty()) { 
        __result += mapSfixed32Sfixed32.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapSfixed64Sfixed64.isNotEmpty()) { 
        __result += mapSfixed64Sfixed64.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt32Float.isNotEmpty()) { 
        __result += mapInt32Float.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapInt32Double.isNotEmpty()) { 
        __result += mapInt32Double.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapBoolBool.isNotEmpty()) { 
        __result += mapBoolBool.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringString.isNotEmpty()) { 
        __result += mapStringString.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringBytes.isNotEmpty()) { 
        __result += mapStringBytes.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringNestedMessage.isNotEmpty()) { 
        __result += mapStringNestedMessage.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringForeignMessage.isNotEmpty()) { 
        __result += mapStringForeignMessage.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringNestedEnum.isNotEmpty()) { 
        __result += mapStringNestedEnum.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (mapStringForeignEnum.isNotEmpty()) { 
        __result += mapStringForeignEnum.entries.sumOf { kEntry ->
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    if (presenceMask[22]) { 
        __result += groupliketype.asInternal()._size.let { (2 * kotlinx.rpc.protobuf.internal.WireSize.tag(201, kotlinx.rpc.protobuf.internal.WireType.START_GROUP)) + it }
    }

    if (presenceMask[23]) { 
        __result += delimitedField.asInternal()._size.let { (2 * kotlinx.rpc.protobuf.internal.WireSize.tag(202, kotlinx.rpc.protobuf.internal.WireType.START_GROUP)) + it }
    }

    oneofField?.also { 
        when (val value = it) { 
            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofUint32 -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(111, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(value.value))
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofNestedMessage -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(112, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofString -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(113, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofBytes -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(114, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofBool -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(115, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(value.value))
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofUint64 -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(116, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(value.value))
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofFloat -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(117, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(value.value))
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofDouble -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(118, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(value.value))
            }

            is com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField.OneofEnum -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(119, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(value.value.number))
            }
        }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal { 
    return this as? com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    c?.also { 
        encoder.writeInt32(fieldNr = 1, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal.computeSize(): Int { 
    var __result = 0
    c?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023.asInternal(): com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal { 
    return this as? com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    c?.also { 
        encoder.writeInt32(fieldNr = 1, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal.computeSize(): Int { 
    var __result = 0
    c?.also { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(it))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.GroupLikeType.asInternal(): com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal { 
    return this as? com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[1]) { 
        corecursive.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    a?.also { 
        encoder.writeInt32(fieldNr = 1, value = it)
    }

    if (presenceMask[1]) { 
        encoder.writeMessage(fieldNr = 2, value = corecursive.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.a = decoder.readInt32()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[1]) { 
                    msg.corecursive = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal()
                }

                decoder.readMessage(msg.corecursive.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal::decodeWith)
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal { 
    return this as? com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeInt32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32Int32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeInt64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt64Int64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeUInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeUInt32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint32Uint32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeUInt64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeUInt64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapUint64Uint64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeSInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeSInt32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint32Sint32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeSInt64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeSInt64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSint64Sint64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeFixed32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeFixed32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed32Fixed32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeFixed64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeFixed64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapFixed64Fixed64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeSFixed32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeSFixed32(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed32Sfixed32EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeSFixed64(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeSFixed64(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapSfixed64Sfixed64EntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeFloat(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32FloatEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeInt32(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeDouble(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapInt32DoubleEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeBool(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeBool(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapBoolBoolEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeString(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringStringEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeBytes(fieldNr = 2, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringBytesEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[1]) { 
        value.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[1]) { 
                    msg.value = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal()
                }

                decoder.readMessage(msg.value.asInternal(), com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal::decodeWith)
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedMessageEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[1]) { 
        value.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[1]) { 
                    msg.value = com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal()
                }

                decoder.readMessage(msg.value.asInternal(), com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal::decodeWith)
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignMessageEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeEnum(fieldNr = 2, value = value.number)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.fromNumber(decoder.readEnum())
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringNestedEnumEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (presenceMask[0]) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[1]) { 
        encoder.writeEnum(fieldNr = 2, value = value.number)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.fromNumber(decoder.readEnum())
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.MapStringForeignEnumEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    groupInt32?.also { 
        encoder.writeInt32(fieldNr = 202, value = it)
    }

    groupUint32?.also { 
        encoder.writeUInt32(fieldNr = 203, value = it)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal.Companion.decodeWith(msg: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, startGroup: kotlinx.rpc.protobuf.internal.KTag) { 
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

private fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal.computeSize(): Int { 
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
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType.asInternal(): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal { 
    return this as? com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.GroupLikeTypeInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023 { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.FOREIGN_FOO
        }

        1 -> { 
            com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.FOREIGN_BAR
        }

        2 -> { 
            com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.FOREIGN_BAZ
        }

        else -> { 
            com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.Companion.fromNumber(number: Int): com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum { 
    return when (number) { 
        0 -> { 
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.FOO
        }

        1 -> { 
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.BAR
        }

        2 -> { 
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.BAZ
        }

        -1 -> { 
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.NEG
        }

        else -> { 
            com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum.UNRECOGNIZED(number)
        }
    }
}
