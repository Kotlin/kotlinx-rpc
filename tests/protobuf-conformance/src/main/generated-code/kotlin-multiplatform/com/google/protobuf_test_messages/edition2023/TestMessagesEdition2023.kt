@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.edition2023

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.edition2023.ComplexMessageInternal.CODEC::class)
interface ComplexMessage { 
    val d: Int?

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.CODEC::class)
interface TestAllTypesEdition2023 { 
    /**
    * Singular
    */
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
    val optionalNestedMessage: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage
    val optionalForeignMessage: com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023
    val optionalNestedEnum: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum?
    val optionalForeignEnum: com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023?
    val optionalStringPiece: String?
    val optionalCord: String?
    val recursiveMessage: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023
    /**
    * Repeated
    */
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
    val repeatedNestedMessage: List<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage>
    val repeatedForeignMessage: List<com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023>
    val repeatedNestedEnum: List<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum>
    val repeatedForeignEnum: List<com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023>
    val repeatedStringPiece: List<kotlin.String>
    val repeatedCord: List<kotlin.String>
    /**
    * Packed
    */
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
    val packedNestedEnum: List<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum>
    /**
    * Unpacked
    */
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
    val unpackedNestedEnum: List<com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum>
    /**
    * Map
    */
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
    val mapInt32Float: Map<kotlin.Int, kotlin.Float>
    val mapInt32Double: Map<kotlin.Int, kotlin.Double>
    val mapBoolBool: Map<kotlin.Boolean, kotlin.Boolean>
    val mapStringString: Map<kotlin.String, kotlin.String>
    val mapStringBytes: Map<kotlin.String, kotlin.ByteArray>
    val mapStringNestedMessage: Map<kotlin.String, com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage>
    val mapStringForeignMessage: Map<kotlin.String, com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023>
    val mapStringNestedEnum: Map<kotlin.String, com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum>
    val mapStringForeignEnum: Map<kotlin.String, com.google.protobuf_test_messages.edition2023.ForeignEnumEdition2023>
    val groupliketype: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType
    val delimitedField: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.GroupLikeType
    val oneofField: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.OneofField?

    sealed interface OneofField { 
        @JvmInline
        value class OneofUint32(val value: UInt): OneofField

        @JvmInline
        value class OneofNestedMessage(
            val value: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedMessage,
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
            val value: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023.NestedEnum,
        ): OneofField
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal.NestedMessageInternal.CODEC::class)
    interface NestedMessage { 
        val a: Int?
        val corecursive: com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023

        companion object
    }

    /**
    * groups
    */
    interface GroupLikeType { 
        val groupInt32: Int?
        val groupUint32: UInt?

        companion object
    }

    sealed class NestedEnum(open val number: Int) { 
        object FOO: NestedEnum(number = 0)

        object BAR: NestedEnum(number = 1)

        object BAZ: NestedEnum(number = 2)

        /**
        * Intentionally negative.
        */
        object NEG: NestedEnum(number = -1)

        data class UNRECOGNIZED(override val number: Int): NestedEnum(number)

        companion object { 
            val entries: List<NestedEnum> by lazy { listOf(NEG, FOO, BAR, BAZ) }
        }
    }

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.edition2023.ForeignMessageEdition2023Internal.CODEC::class)
interface ForeignMessageEdition2023 { 
    val c: Int?

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.edition2023.GroupLikeTypeInternal.CODEC::class)
interface GroupLikeType { 
    val c: Int?

    companion object
}

sealed class ForeignEnumEdition2023(open val number: Int) { 
    object FOREIGN_FOO: ForeignEnumEdition2023(number = 0)

    object FOREIGN_BAR: ForeignEnumEdition2023(number = 1)

    object FOREIGN_BAZ: ForeignEnumEdition2023(number = 2)

    data class UNRECOGNIZED(override val number: Int): ForeignEnumEdition2023(number)

    companion object { 
        val entries: List<ForeignEnumEdition2023> by lazy { listOf(FOREIGN_FOO, FOREIGN_BAR, FOREIGN_BAZ) }
    }
}
