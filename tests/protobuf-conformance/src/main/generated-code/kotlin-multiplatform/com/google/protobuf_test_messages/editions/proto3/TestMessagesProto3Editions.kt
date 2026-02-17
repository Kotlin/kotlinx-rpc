@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.editions.proto3

import com.google.protobuf.kotlin.*
import com.google.protobuf.kotlin.BoolValue
import com.google.protobuf.kotlin.BytesValue
import com.google.protobuf.kotlin.DoubleValue
import com.google.protobuf.kotlin.Duration
import com.google.protobuf.kotlin.FieldMask
import com.google.protobuf.kotlin.FloatValue
import com.google.protobuf.kotlin.Int32Value
import com.google.protobuf.kotlin.Int64Value
import com.google.protobuf.kotlin.ListValue
import com.google.protobuf.kotlin.NullValue
import com.google.protobuf.kotlin.StringValue
import com.google.protobuf.kotlin.Struct
import com.google.protobuf.kotlin.Timestamp
import com.google.protobuf.kotlin.UInt32Value
import com.google.protobuf.kotlin.UInt64Value
import com.google.protobuf.kotlin.Value
import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* This proto includes every type of field in both singular and repeated
* forms.
* 
* Also, crucially, all messages and enums in this file are eventually
* submessages of this message.  So for example, a fuzz test of TestAllTypes
* could trigger bugs that occur in any message type in this file.  We verify
* this stays true in a unit test.
*/
@WithCodec(TestAllTypesProto3Internal.CODEC::class)
interface TestAllTypesProto3 {
    /**
    * Singular
    * test [kotlin] comment
    */
    val optionalInt32: Int
    val optionalInt64: Long
    val optionalUint32: UInt
    val optionalUint64: ULong
    val optionalSint32: Int
    val optionalSint64: Long
    val optionalFixed32: UInt
    val optionalFixed64: ULong
    val optionalSfixed32: Int
    val optionalSfixed64: Long
    val optionalFloat: Float
    val optionalDouble: Double
    val optionalBool: Boolean
    val optionalString: String
    val optionalBytes: ByteArray
    val optionalNestedMessage: NestedMessage
    val optionalForeignMessage: ForeignMessage
    val optionalNestedEnum: NestedEnum
    val optionalForeignEnum: ForeignEnum
    val optionalAliasedEnum: AliasedEnum
    val optionalStringPiece: String
    val optionalCord: String
    val recursiveMessage: TestAllTypesProto3
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
    val repeatedForeignMessage: List<ForeignMessage>
    val repeatedNestedEnum: List<NestedEnum>
    val repeatedForeignEnum: List<ForeignEnum>
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
    val mapStringForeignMessage: Map<String, ForeignMessage>
    val mapStringNestedEnum: Map<String, NestedEnum>
    val mapStringForeignEnum: Map<String, ForeignEnum>
    /**
    * Well-known types
    */
    val optionalBoolWrapper: BoolValue
    val optionalInt32Wrapper: Int32Value
    val optionalInt64Wrapper: Int64Value
    val optionalUint32Wrapper: UInt32Value
    val optionalUint64Wrapper: UInt64Value
    val optionalFloatWrapper: FloatValue
    val optionalDoubleWrapper: DoubleValue
    val optionalStringWrapper: StringValue
    val optionalBytesWrapper: BytesValue
    val repeatedBoolWrapper: List<BoolValue>
    val repeatedInt32Wrapper: List<Int32Value>
    val repeatedInt64Wrapper: List<Int64Value>
    val repeatedUint32Wrapper: List<UInt32Value>
    val repeatedUint64Wrapper: List<UInt64Value>
    val repeatedFloatWrapper: List<FloatValue>
    val repeatedDoubleWrapper: List<DoubleValue>
    val repeatedStringWrapper: List<StringValue>
    val repeatedBytesWrapper: List<BytesValue>
    val optionalDuration: Duration
    val optionalTimestamp: Timestamp
    val optionalFieldMask: FieldMask
    val optionalStruct: Struct
    val optionalAny: com.google.protobuf.kotlin.Any
    val optionalValue: Value
    val optionalNullValue: NullValue
    val repeatedDuration: List<Duration>
    val repeatedTimestamp: List<Timestamp>
    val repeatedFieldmask: List<FieldMask>
    val repeatedStruct: List<Struct>
    val repeatedAny: List<com.google.protobuf.kotlin.Any>
    val repeatedValue: List<Value>
    val repeatedListValue: List<ListValue>
    /**
    * Test field-name-to-JSON-name convention.
    * (protobuf says names can be any valid C/C++ identifier.)
    */
    val fieldname1: Int
    val fieldName2: Int
    val FieldName3: Int
    val field_Name4_: Int
    val field0name5: Int
    val field_0Name6: Int
    val fieldName7: Int
    val FieldName8: Int
    val field_Name9: Int
    val Field_Name10: Int
    val FIELD_NAME11: Int
    val FIELDName12: Int
    val _FieldName13: Int
    val __FieldName14: Int
    val field_Name15: Int
    val field__Name16: Int
    val fieldName17__: Int
    val FieldName18__: Int
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

        @JvmInline
        value class OneofNullValue(val value: NullValue): OneofField
    }

    @WithCodec(TestAllTypesProto3Internal.NestedMessageInternal.CODEC::class)
    interface NestedMessage {
        val a: Int
        val corecursive: TestAllTypesProto3

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

    sealed class AliasedEnum(open val number: Int) {
        data object ALIAS_FOO: AliasedEnum(number = 0)

        data object ALIAS_BAR: AliasedEnum(number = 1)

        data object ALIAS_BAZ: AliasedEnum(number = 2)

        data class UNRECOGNIZED(override val number: Int): AliasedEnum(number)

        companion object {
            val MOO: AliasedEnum get() = ALIAS_BAZ

            val moo: AliasedEnum get() = ALIAS_BAZ

            val bAz: AliasedEnum get() = ALIAS_BAZ

            val entries: List<AliasedEnum> by lazy { listOf(ALIAS_FOO, ALIAS_BAR, ALIAS_BAZ) }
        }
    }

    companion object
}

@WithCodec(ForeignMessageInternal.CODEC::class)
interface ForeignMessage {
    val c: Int

    companion object
}

@WithCodec(NullHypothesisProto3Internal.CODEC::class)
interface NullHypothesisProto3 {
    companion object
}

@WithCodec(EnumOnlyProto3Internal.CODEC::class)
interface EnumOnlyProto3 {
    sealed class Bool(open val number: Int) {
        data object kFalse: Bool(number = 0)

        data object kTrue: Bool(number = 1)

        data class UNRECOGNIZED(override val number: Int): Bool(number)

        companion object {
            val entries: List<Bool> by lazy { listOf(kFalse, kTrue) }
        }
    }

    companion object
}

sealed class ForeignEnum(open val number: Int) {
    data object FOREIGN_FOO: ForeignEnum(number = 0)

    data object FOREIGN_BAR: ForeignEnum(number = 1)

    data object FOREIGN_BAZ: ForeignEnum(number = 2)

    data class UNRECOGNIZED(override val number: Int): ForeignEnum(number)

    companion object {
        val entries: List<ForeignEnum> by lazy { listOf(FOREIGN_FOO, FOREIGN_BAR, FOREIGN_BAZ) }
    }
}
