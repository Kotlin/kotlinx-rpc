@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.editions.proto3

import com.google.protobuf.kotlin.*
import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

/**
* This proto includes every type of field in both singular and repeated
* forms.
* 
* Also, crucially, all messages and enums in this file are eventually
* submessages of this message.  So for example, a fuzz test of TestAllTypes
* could trigger bugs that occur in any message type in this file.  We verify
* this stays true in a unit test.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3Internal.CODEC::class)
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
    val optionalNestedMessage: com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedMessage
    val optionalForeignMessage: com.google.protobuf_test_messages.editions.proto3.ForeignMessage
    val optionalNestedEnum: com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedEnum
    val optionalForeignEnum: com.google.protobuf_test_messages.editions.proto3.ForeignEnum
    val optionalAliasedEnum: com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.AliasedEnum
    val optionalStringPiece: String
    val optionalCord: String
    val recursiveMessage: com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3
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
    val repeatedNestedMessage: List<com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedMessage>
    val repeatedForeignMessage: List<com.google.protobuf_test_messages.editions.proto3.ForeignMessage>
    val repeatedNestedEnum: List<com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedEnum>
    val repeatedForeignEnum: List<com.google.protobuf_test_messages.editions.proto3.ForeignEnum>
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
    val packedNestedEnum: List<com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedEnum>
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
    val unpackedNestedEnum: List<com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedEnum>
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
    val mapStringNestedMessage: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedMessage>
    val mapStringForeignMessage: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto3.ForeignMessage>
    val mapStringNestedEnum: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedEnum>
    val mapStringForeignEnum: Map<kotlin.String, com.google.protobuf_test_messages.editions.proto3.ForeignEnum>
    /**
    * Well-known types
    */
    val optionalBoolWrapper: com.google.protobuf.kotlin.BoolValue
    val optionalInt32Wrapper: com.google.protobuf.kotlin.Int32Value
    val optionalInt64Wrapper: com.google.protobuf.kotlin.Int64Value
    val optionalUint32Wrapper: com.google.protobuf.kotlin.UInt32Value
    val optionalUint64Wrapper: com.google.protobuf.kotlin.UInt64Value
    val optionalFloatWrapper: com.google.protobuf.kotlin.FloatValue
    val optionalDoubleWrapper: com.google.protobuf.kotlin.DoubleValue
    val optionalStringWrapper: com.google.protobuf.kotlin.StringValue
    val optionalBytesWrapper: com.google.protobuf.kotlin.BytesValue
    val repeatedBoolWrapper: List<com.google.protobuf.kotlin.BoolValue>
    val repeatedInt32Wrapper: List<com.google.protobuf.kotlin.Int32Value>
    val repeatedInt64Wrapper: List<com.google.protobuf.kotlin.Int64Value>
    val repeatedUint32Wrapper: List<com.google.protobuf.kotlin.UInt32Value>
    val repeatedUint64Wrapper: List<com.google.protobuf.kotlin.UInt64Value>
    val repeatedFloatWrapper: List<com.google.protobuf.kotlin.FloatValue>
    val repeatedDoubleWrapper: List<com.google.protobuf.kotlin.DoubleValue>
    val repeatedStringWrapper: List<com.google.protobuf.kotlin.StringValue>
    val repeatedBytesWrapper: List<com.google.protobuf.kotlin.BytesValue>
    val optionalDuration: com.google.protobuf.kotlin.Duration
    val optionalTimestamp: com.google.protobuf.kotlin.Timestamp
    val optionalFieldMask: com.google.protobuf.kotlin.FieldMask
    val optionalStruct: com.google.protobuf.kotlin.Struct
    val optionalAny: com.google.protobuf.kotlin.Any
    val optionalValue: com.google.protobuf.kotlin.Value
    val optionalNullValue: com.google.protobuf.kotlin.NullValue
    val repeatedDuration: List<com.google.protobuf.kotlin.Duration>
    val repeatedTimestamp: List<com.google.protobuf.kotlin.Timestamp>
    val repeatedFieldmask: List<com.google.protobuf.kotlin.FieldMask>
    val repeatedStruct: List<com.google.protobuf.kotlin.Struct>
    val repeatedAny: List<com.google.protobuf.kotlin.Any>
    val repeatedValue: List<com.google.protobuf.kotlin.Value>
    val repeatedListValue: List<com.google.protobuf.kotlin.ListValue>
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
    val oneofField: com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.OneofField?

    sealed interface OneofField { 
        @JvmInline
        value class OneofUint32(val value: UInt): OneofField

        @JvmInline
        value class OneofNestedMessage(
            val value: com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedMessage,
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
            val value: com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3.NestedEnum,
        ): OneofField

        @JvmInline
        value class OneofNullValue(
            val value: com.google.protobuf.kotlin.NullValue,
        ): OneofField
    }

    @kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3Internal.NestedMessageInternal.CODEC::class)
    interface NestedMessage { 
        val a: Int
        val corecursive: com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3

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

    sealed class AliasedEnum(open val number: Int) { 
        object ALIAS_FOO: AliasedEnum(number = 0)

        object ALIAS_BAR: AliasedEnum(number = 1)

        object ALIAS_BAZ: AliasedEnum(number = 2)

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

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto3.ForeignMessageInternal.CODEC::class)
interface ForeignMessage { 
    val c: Int

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto3.NullHypothesisProto3Internal.CODEC::class)
interface NullHypothesisProto3 { 
    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf_test_messages.editions.proto3.EnumOnlyProto3Internal.CODEC::class)
interface EnumOnlyProto3 { 
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

sealed class ForeignEnum(open val number: Int) { 
    object FOREIGN_FOO: ForeignEnum(number = 0)

    object FOREIGN_BAR: ForeignEnum(number = 1)

    object FOREIGN_BAZ: ForeignEnum(number = 2)

    data class UNRECOGNIZED(override val number: Int): ForeignEnum(number)

    companion object { 
        val entries: List<ForeignEnum> by lazy { listOf(FOREIGN_FOO, FOREIGN_BAR, FOREIGN_BAZ) }
    }
}
