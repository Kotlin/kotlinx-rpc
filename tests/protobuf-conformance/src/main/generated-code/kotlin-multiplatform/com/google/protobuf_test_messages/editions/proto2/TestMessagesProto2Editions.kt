@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.editions.proto2

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.CODEC::class)
interface TestAllTypesProto2 { 
    val optionalInt32: Int?
    val optionalInt64: Long?
    val optionalUint32: UInt?
    val optionalUint64: ULong?
    val optionalSint32: Int?
    val optionalSint64: Long?
    val optionalFixed32: UInt?
    val optionalFixed64: ULong?
    val optionalSfixed32: Int?
    val optionalSfixed64: Long?
    val optionalFloat: Float?
    val optionalDouble: Double?
    val optionalBool: Boolean?
    val optionalString: String?
    val optionalBytes: ByteArray?
    val optionalNestedMessage: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage
    val optionalForeignMessage: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2
    val optionalNestedEnum: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum?
    val optionalForeignEnum: com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2?
    val optionalStringPiece: String?
    val optionalCord: String?
    val recursiveMessage: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2
    val repeatedInt32: List<kotlin.Int>
    val repeatedInt64: List<kotlin.Long>
    val repeatedUint32: List<kotlin.UInt>
    val repeatedUint64: List<kotlin.ULong>
    val repeatedSint32: List<kotlin.Int>
    val repeatedSint64: List<kotlin.Long>
    val repeatedFixed32: List<kotlin.UInt>
    val repeatedFixed64: List<kotlin.ULong>
    val repeatedSfixed32: List<kotlin.Int>
    val repeatedSfixed64: List<kotlin.Long>
    val repeatedFloat: List<kotlin.Float>
    val repeatedDouble: List<kotlin.Double>
    val repeatedBool: List<kotlin.Boolean>
    val repeatedString: List<kotlin.String>
    val repeatedBytes: List<kotlin.ByteArray>
    val repeatedNestedMessage: List<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage>
    val repeatedForeignMessage: List<com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2>
    val repeatedNestedEnum: List<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum>
    val repeatedForeignEnum: List<com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2>
    val repeatedStringPiece: List<kotlin.String>
    val repeatedCord: List<kotlin.String>
    val packedInt32: List<kotlin.Int>
    val packedInt64: List<kotlin.Long>
    val packedUint32: List<kotlin.UInt>
    val packedUint64: List<kotlin.ULong>
    val packedSint32: List<kotlin.Int>
    val packedSint64: List<kotlin.Long>
    val packedFixed32: List<kotlin.UInt>
    val packedFixed64: List<kotlin.ULong>
    val packedSfixed32: List<kotlin.Int>
    val packedSfixed64: List<kotlin.Long>
    val packedFloat: List<kotlin.Float>
    val packedDouble: List<kotlin.Double>
    val packedBool: List<kotlin.Boolean>
    val packedNestedEnum: List<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum>
    val unpackedInt32: List<kotlin.Int>
    val unpackedInt64: List<kotlin.Long>
    val unpackedUint32: List<kotlin.UInt>
    val unpackedUint64: List<kotlin.ULong>
    val unpackedSint32: List<kotlin.Int>
    val unpackedSint64: List<kotlin.Long>
    val unpackedFixed32: List<kotlin.UInt>
    val unpackedFixed64: List<kotlin.ULong>
    val unpackedSfixed32: List<kotlin.Int>
    val unpackedSfixed64: List<kotlin.Long>
    val unpackedFloat: List<kotlin.Float>
    val unpackedDouble: List<kotlin.Double>
    val unpackedBool: List<kotlin.Boolean>
    val unpackedNestedEnum: List<com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum>
    val mapInt32Int32: Map<kotlin.Int, kotlin.Int>
    val mapInt64Int64: Map<kotlin.Long, kotlin.Long>
    val mapUint32Uint32: Map<kotlin.UInt, kotlin.UInt>
    val mapUint64Uint64: Map<kotlin.ULong, kotlin.ULong>
    val mapSint32Sint32: Map<kotlin.Int, kotlin.Int>
    val mapSint64Sint64: Map<kotlin.Long, kotlin.Long>
    val mapFixed32Fixed32: Map<kotlin.UInt, kotlin.UInt>
    val mapFixed64Fixed64: Map<kotlin.ULong, kotlin.ULong>
    val mapSfixed32Sfixed32: Map<kotlin.Int, kotlin.Int>
    val mapSfixed64Sfixed64: Map<kotlin.Long, kotlin.Long>
    val mapInt32Bool: Map<kotlin.Int, kotlin.Boolean>
    val mapInt32Float: Map<kotlin.Int, kotlin.Float>
    val mapInt32Double: Map<kotlin.Int, kotlin.Double>
    val mapInt32NestedMessage: Map<kotlin.Int, com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage>
    val mapBoolBool: Map<kotlin.Boolean, kotlin.Boolean>
    val mapStringString: Map<kotlin.String, kotlin.String>
    val mapStringBytes: Map<kotlin.String, kotlin.ByteArray>
    val mapStringNestedMessage: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage>
    val mapStringForeignMessage: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2>
    val mapStringNestedEnum: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum>
    val mapStringForeignEnum: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2>
    val data: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.Data
    val multiwordgroupfield: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MultiWordGroupField
    val defaultInt32: Int
    val defaultInt64: Long
    val defaultUint32: UInt
    val defaultUint64: ULong
    val defaultSint32: Int
    val defaultSint64: Long
    val defaultFixed32: UInt
    val defaultFixed64: ULong
    val defaultSfixed32: Int
    val defaultSfixed64: Long
    val defaultFloat: Float
    val defaultDouble: Double
    val defaultBool: Boolean
    val defaultString: String
    val defaultBytes: ByteArray
    val fieldname1: Int?
    val fieldName2: Int?
    val FieldName3: Int?
    val field_Name4_: Int?
    val field0name5: Int?
    val field_0Name6: Int?
    val fieldName7: Int?
    val FieldName8: Int?
    val field_Name9: Int?
    val Field_Name10: Int?
    val FIELD_NAME11: Int?
    val FIELDName12: Int?
    val _FieldName13: Int?
    val __FieldName14: Int?
    val field_Name15: Int?
    val field__Name16: Int?
    val fieldName17__: Int?
    val FieldName18__: Int?
    val messageSetCorrect: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.MessageSetCorrect
    val oneofField: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.OneofField?

    sealed interface OneofField { 
        @JvmInline
        value class OneofUint32(val value: UInt): OneofField

        @JvmInline
        value class OneofNestedMessage(
            val value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedMessage,
        ): OneofField

        @JvmInline
        value class OneofString(val value: String): OneofField

        @JvmInline
        value class OneofBytes(val value: ByteArray): OneofField

        @JvmInline
        value class OneofBool(val value: Boolean): OneofField

        @JvmInline
        value class OneofUint64(val value: ULong): OneofField

        @JvmInline
        value class OneofFloat(val value: Float): OneofField

        @JvmInline
        value class OneofDouble(val value: Double): OneofField

        @JvmInline
        value class OneofEnum(
            val value: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.NestedEnum,
        ): OneofField
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.NestedMessageInternal.CODEC::class)
    interface NestedMessage { 
        val a: Int?
        val corecursive: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2

        companion object
    }

    interface Data { 
        val groupInt32: Int?
        val groupUint32: UInt?

        companion object
    }

    interface MultiWordGroupField { 
        val groupInt32: Int?
        val groupUint32: UInt?

        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectInternal.CODEC::class)
    interface MessageSetCorrect { 
        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.CODEC::class)
    interface MessageSetCorrectExtension1 { 
        val str: String?

        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.CODEC::class)
    interface MessageSetCorrectExtension2 { 
        val i: Int?

        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.ExtensionWithOneofInternal.CODEC::class)
    interface ExtensionWithOneof { 
        val oneofField: com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2.ExtensionWithOneof.OneofField?

        sealed interface OneofField { 
            @JvmInline
            value class A(val value: Int): OneofField

            @JvmInline
            value class B(val value: Int): OneofField
        }

        companion object
    }

    sealed class NestedEnum(open val number: Int) { 
        object FOO: NestedEnum(number = 0)

        object BAR: NestedEnum(number = 1)

        object BAZ: NestedEnum(number = 2)

        object NEG: NestedEnum(number = -1)

        data class UNRECOGNIZED(override val number: Int): NestedEnum(number)

        companion object { 
            val entries: List<NestedEnum> by lazy { listOf(NEG, FOO, BAR, BAZ) }
        }
    }

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2Internal.CODEC::class)
interface ForeignMessageProto2 { 
    val c: Int?

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.GroupFieldInternal.CODEC::class)
interface GroupField { 
    val groupInt32: Int?
    val groupUint32: UInt?

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypesInternal.CODEC::class)
interface UnknownToTestAllTypes { 
    val optionalInt32: Int?
    val optionalString: String?
    val nestedMessage: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2
    val optionalgroup: com.google.protobuf_test_messages.editions.proto2.UnknownToTestAllTypes.OptionalGroup
    val optionalBool: Boolean?
    val repeatedInt32: List<kotlin.Int>

    interface OptionalGroup { 
        val a: Int?

        companion object
    }

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.NullHypothesisProto2Internal.CODEC::class)
interface NullHypothesisProto2 { 
    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.EnumOnlyProto2Internal.CODEC::class)
interface EnumOnlyProto2 { 
    sealed class Bool(open val number: Int) { 
        object kFalse: Bool(number = 0)

        object kTrue: Bool(number = 1)

        data class UNRECOGNIZED(override val number: Int): Bool(number)

        companion object { 
            val entries: List<Bool> by lazy { listOf(kFalse, kTrue) }
        }
    }

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.OneStringProto2Internal.CODEC::class)
interface OneStringProto2 { 
    val data: String?

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.ProtoWithKeywordsInternal.CODEC::class)
interface ProtoWithKeywords { 
    val inline: Int?
    val concept: String?
    val requires: List<kotlin.String>

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.CODEC::class)
interface TestAllRequiredTypesProto2 { 
    val requiredInt32: Int
    val requiredInt64: Long
    val requiredUint32: UInt
    val requiredUint64: ULong
    val requiredSint32: Int
    val requiredSint64: Long
    val requiredFixed32: UInt
    val requiredFixed64: ULong
    val requiredSfixed32: Int
    val requiredSfixed64: Long
    val requiredFloat: Float
    val requiredDouble: Double
    val requiredBool: Boolean
    val requiredString: String
    val requiredBytes: ByteArray
    val requiredNestedMessage: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedMessage
    val requiredForeignMessage: com.google.protobuf_test_messages.editions.proto2.ForeignMessageProto2
    val requiredNestedEnum: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.NestedEnum
    val requiredForeignEnum: com.google.protobuf_test_messages.editions.proto2.ForeignEnumProto2
    val requiredStringPiece: String
    val requiredCord: String
    val recursiveMessage: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2
    val optionalRecursiveMessage: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2
    val data: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2.Data
    val defaultInt32: Int
    val defaultInt64: Long
    val defaultUint32: UInt
    val defaultUint64: ULong
    val defaultSint32: Int
    val defaultSint64: Long
    val defaultFixed32: UInt
    val defaultFixed64: ULong
    val defaultSfixed32: Int
    val defaultSfixed64: Long
    val defaultFloat: Float
    val defaultDouble: Double
    val defaultBool: Boolean
    val defaultString: String
    val defaultBytes: ByteArray

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.NestedMessageInternal.CODEC::class)
    interface NestedMessage { 
        val a: Int
        val corecursive: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2
        val optionalCorecursive: com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2

        companion object
    }

    interface Data { 
        val groupInt32: Int
        val groupUint32: UInt

        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.CODEC::class)
    interface MessageSetCorrect { 
        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.CODEC::class)
    interface MessageSetCorrectExtension1 { 
        val str: String

        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.CODEC::class)
    interface MessageSetCorrectExtension2 { 
        val i: Int

        companion object
    }

    sealed class NestedEnum(open val number: Int) { 
        object FOO: NestedEnum(number = 0)

        object BAR: NestedEnum(number = 1)

        object BAZ: NestedEnum(number = 2)

        object NEG: NestedEnum(number = -1)

        data class UNRECOGNIZED(override val number: Int): NestedEnum(number)

        companion object { 
            val entries: List<NestedEnum> by lazy { listOf(NEG, FOO, BAR, BAZ) }
        }
    }

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.CODEC::class)
interface TestLargeOneof { 
    val largeOneof: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.LargeOneof?

    sealed interface LargeOneof { 
        @JvmInline
        value class A1(
            val value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A1,
        ): LargeOneof

        @JvmInline
        value class A2(
            val value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A2,
        ): LargeOneof

        @JvmInline
        value class A3(
            val value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A3,
        ): LargeOneof

        @JvmInline
        value class A4(
            val value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A4,
        ): LargeOneof

        @JvmInline
        value class A5(
            val value: com.google.protobuf_test_messages.editions.proto2.TestLargeOneof.A5,
        ): LargeOneof
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A1Internal.CODEC::class)
    interface A1 { 
        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A2Internal.CODEC::class)
    interface A2 { 
        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A3Internal.CODEC::class)
    interface A3 { 
        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A4Internal.CODEC::class)
    interface A4 { 
        companion object
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto2.TestLargeOneofInternal.A5Internal.CODEC::class)
    interface A5 { 
        companion object
    }

    companion object
}

sealed class ForeignEnumProto2(open val number: Int) { 
    object FOREIGN_FOO: ForeignEnumProto2(number = 0)

    object FOREIGN_BAR: ForeignEnumProto2(number = 1)

    object FOREIGN_BAZ: ForeignEnumProto2(number = 2)

    data class UNRECOGNIZED(override val number: Int): ForeignEnumProto2(number)

    companion object { 
        val entries: List<ForeignEnumProto2> by lazy { listOf(FOREIGN_FOO, FOREIGN_BAR, FOREIGN_BAZ) }
    }
}

