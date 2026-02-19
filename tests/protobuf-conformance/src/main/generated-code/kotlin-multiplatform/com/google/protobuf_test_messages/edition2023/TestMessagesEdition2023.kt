@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.edition2023

import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.GeneratedProtoMessage

@GeneratedProtoMessage
@WithCodec(ComplexMessageInternal.CODEC::class)
interface ComplexMessage {
    val d: Int?

    companion object
}

@GeneratedProtoMessage
@WithCodec(TestAllTypesEdition2023Internal.CODEC::class)
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
    val optionalNestedMessage: NestedMessage
    val optionalForeignMessage: ForeignMessageEdition2023
    val optionalNestedEnum: NestedEnum?
    val optionalForeignEnum: ForeignEnumEdition2023?
    val optionalStringPiece: String?
    val optionalCord: String?
    val recursiveMessage: TestAllTypesEdition2023
    /**
    * Repeated
    */
    val repeatedInt32: List<Int>
    val repeatedInt64: List<Long>
    val repeatedUint32: List<UInt>
    val repeatedUint64: List<ULong>
    val repeatedSint32: List<Int>
    val repeatedSint64: List<Long>
    val repeatedFixed32: List<UInt>
    val repeatedFixed64: List<ULong>
    val repeatedSfixed32: List<Int>
    val repeatedSfixed64: List<Long>
    val repeatedFloat: List<Float>
    val repeatedDouble: List<Double>
    val repeatedBool: List<Boolean>
    val repeatedString: List<String>
    val repeatedBytes: List<ByteArray>
    val repeatedNestedMessage: List<NestedMessage>
    val repeatedForeignMessage: List<ForeignMessageEdition2023>
    val repeatedNestedEnum: List<NestedEnum>
    val repeatedForeignEnum: List<ForeignEnumEdition2023>
    val repeatedStringPiece: List<String>
    val repeatedCord: List<String>
    /**
    * Packed
    */
    val packedInt32: List<Int>
    val packedInt64: List<Long>
    val packedUint32: List<UInt>
    val packedUint64: List<ULong>
    val packedSint32: List<Int>
    val packedSint64: List<Long>
    val packedFixed32: List<UInt>
    val packedFixed64: List<ULong>
    val packedSfixed32: List<Int>
    val packedSfixed64: List<Long>
    val packedFloat: List<Float>
    val packedDouble: List<Double>
    val packedBool: List<Boolean>
    val packedNestedEnum: List<NestedEnum>
    /**
    * Unpacked
    */
    val unpackedInt32: List<Int>
    val unpackedInt64: List<Long>
    val unpackedUint32: List<UInt>
    val unpackedUint64: List<ULong>
    val unpackedSint32: List<Int>
    val unpackedSint64: List<Long>
    val unpackedFixed32: List<UInt>
    val unpackedFixed64: List<ULong>
    val unpackedSfixed32: List<Int>
    val unpackedSfixed64: List<Long>
    val unpackedFloat: List<Float>
    val unpackedDouble: List<Double>
    val unpackedBool: List<Boolean>
    val unpackedNestedEnum: List<NestedEnum>
    /**
    * Map
    */
    val mapInt32Int32: Map<Int, Int>
    val mapInt64Int64: Map<Long, Long>
    val mapUint32Uint32: Map<UInt, UInt>
    val mapUint64Uint64: Map<ULong, ULong>
    val mapSint32Sint32: Map<Int, Int>
    val mapSint64Sint64: Map<Long, Long>
    val mapFixed32Fixed32: Map<UInt, UInt>
    val mapFixed64Fixed64: Map<ULong, ULong>
    val mapSfixed32Sfixed32: Map<Int, Int>
    val mapSfixed64Sfixed64: Map<Long, Long>
    val mapInt32Float: Map<Int, Float>
    val mapInt32Double: Map<Int, Double>
    val mapBoolBool: Map<Boolean, Boolean>
    val mapStringString: Map<String, String>
    val mapStringBytes: Map<String, ByteArray>
    val mapStringNestedMessage: Map<String, NestedMessage>
    val mapStringForeignMessage: Map<String, ForeignMessageEdition2023>
    val mapStringNestedEnum: Map<String, NestedEnum>
    val mapStringForeignEnum: Map<String, ForeignEnumEdition2023>
    val groupliketype: GroupLikeType
    val delimitedField: GroupLikeType
    val oneofField: OneofField?

    sealed interface OneofField {
        @JvmInline
        value class OneofUint32(val value: UInt): OneofField

        @JvmInline
        value class OneofNestedMessage(val value: NestedMessage): OneofField

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
        value class OneofEnum(val value: NestedEnum): OneofField
    }

    @GeneratedProtoMessage
    @WithCodec(TestAllTypesEdition2023Internal.NestedMessageInternal.CODEC::class)
    interface NestedMessage {
        val a: Int?
        val corecursive: TestAllTypesEdition2023

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
        data object FOO: NestedEnum(number = 0)

        data object BAR: NestedEnum(number = 1)

        data object BAZ: NestedEnum(number = 2)

        /**
        * Intentionally negative.
        */
        data object NEG: NestedEnum(number = -1)

        data class UNRECOGNIZED(override val number: Int): NestedEnum(number)

        companion object {
            val entries: List<NestedEnum> by lazy { listOf(NEG, FOO, BAR, BAZ) }
        }
    }

    companion object
}

@GeneratedProtoMessage
@WithCodec(ForeignMessageEdition2023Internal.CODEC::class)
interface ForeignMessageEdition2023 {
    val c: Int?

    companion object
}

@GeneratedProtoMessage
@WithCodec(GroupLikeTypeInternal.CODEC::class)
interface GroupLikeType {
    val c: Int?

    companion object
}

sealed class ForeignEnumEdition2023(open val number: Int) {
    data object FOREIGN_FOO: ForeignEnumEdition2023(number = 0)

    data object FOREIGN_BAR: ForeignEnumEdition2023(number = 1)

    data object FOREIGN_BAZ: ForeignEnumEdition2023(number = 2)

    data class UNRECOGNIZED(override val number: Int): ForeignEnumEdition2023(number)

    companion object {
        val entries: List<ForeignEnumEdition2023> by lazy { listOf(FOREIGN_FOO, FOREIGN_BAR, FOREIGN_BAZ) }
    }
}
