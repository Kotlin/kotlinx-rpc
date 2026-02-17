@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf_test_messages.proto2

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
@WithCodec(TestAllTypesProto2Internal.CODEC::class)
interface TestAllTypesProto2 {
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
    val optionalForeignMessage: ForeignMessageProto2
    val optionalNestedEnum: NestedEnum?
    val optionalForeignEnum: ForeignEnumProto2?
    val optionalStringPiece: String?
    val optionalCord: String?
    val recursiveMessage: TestAllTypesProto2
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
    val repeatedForeignMessage: List<ForeignMessageProto2>
    val repeatedNestedEnum: List<NestedEnum>
    val repeatedForeignEnum: List<ForeignEnumProto2>
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
    val mapInt32Bool: Map<Int, Boolean>
    val mapInt32Float: Map<Int, Float>
    val mapInt32Double: Map<Int, Double>
    val mapInt32NestedMessage: Map<Int, NestedMessage>
    val mapBoolBool: Map<Boolean, Boolean>
    val mapStringString: Map<String, String>
    val mapStringBytes: Map<String, ByteArray>
    val mapStringNestedMessage: Map<String, NestedMessage>
    val mapStringForeignMessage: Map<String, ForeignMessageProto2>
    val mapStringNestedEnum: Map<String, NestedEnum>
    val mapStringForeignEnum: Map<String, ForeignEnumProto2>
    val data: Data
    val multiwordgroupfield: MultiWordGroupField
    /**
    * default values
    */
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
    /**
    * Test field-name-to-JSON-name convention.
    * (protobuf says names can be any valid C/C++ identifier.)
    */
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
    val messageSetCorrect: MessageSetCorrect
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

    @WithCodec(TestAllTypesProto2Internal.NestedMessageInternal.CODEC::class)
    interface NestedMessage {
        val a: Int?
        val corecursive: TestAllTypesProto2

        companion object
    }

    /**
    * groups
    */
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

    /**
    * message_set test case.
    */
    @WithCodec(TestAllTypesProto2Internal.MessageSetCorrectInternal.CODEC::class)
    interface MessageSetCorrect {
        companion object
    }

    @WithCodec(TestAllTypesProto2Internal.MessageSetCorrectExtension1Internal.CODEC::class)
    interface MessageSetCorrectExtension1 {
        val str: String?

        companion object
    }

    @WithCodec(TestAllTypesProto2Internal.MessageSetCorrectExtension2Internal.CODEC::class)
    interface MessageSetCorrectExtension2 {
        val i: Int?

        companion object
    }

    @WithCodec(TestAllTypesProto2Internal.ExtensionWithOneofInternal.CODEC::class)
    interface ExtensionWithOneof {
        val oneofField: OneofField?

        sealed interface OneofField {
            @JvmInline
            value class A(val value: Int): OneofField

            @JvmInline
            value class B(val value: Int): OneofField
        }

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

@WithCodec(ForeignMessageProto2Internal.CODEC::class)
interface ForeignMessageProto2 {
    val c: Int?

    companion object
}

@WithCodec(GroupFieldInternal.CODEC::class)
interface GroupField {
    val groupInt32: Int?
    val groupUint32: UInt?

    companion object
}

@WithCodec(UnknownToTestAllTypesInternal.CODEC::class)
interface UnknownToTestAllTypes {
    val optionalInt32: Int?
    val optionalString: String?
    val nestedMessage: ForeignMessageProto2
    val optionalgroup: OptionalGroup
    val optionalBool: Boolean?
    val repeatedInt32: List<Int>

    interface OptionalGroup {
        val a: Int?

        companion object
    }

    companion object
}

@WithCodec(NullHypothesisProto2Internal.CODEC::class)
interface NullHypothesisProto2 {
    companion object
}

@WithCodec(EnumOnlyProto2Internal.CODEC::class)
interface EnumOnlyProto2 {
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

@WithCodec(OneStringProto2Internal.CODEC::class)
interface OneStringProto2 {
    val data: String?

    companion object
}

@WithCodec(ProtoWithKeywordsInternal.CODEC::class)
interface ProtoWithKeywords {
    val inline: Int?
    val concept: String?
    val requires: List<String>

    companion object
}

@WithCodec(TestAllRequiredTypesProto2Internal.CODEC::class)
interface TestAllRequiredTypesProto2 {
    /**
    * Singular
    */
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
    val requiredNestedMessage: NestedMessage
    val requiredForeignMessage: ForeignMessageProto2
    val requiredNestedEnum: NestedEnum
    val requiredForeignEnum: ForeignEnumProto2
    val requiredStringPiece: String
    val requiredCord: String
    val recursiveMessage: TestAllRequiredTypesProto2
    val optionalRecursiveMessage: TestAllRequiredTypesProto2
    val data: Data
    /**
    * default values
    */
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

    @WithCodec(TestAllRequiredTypesProto2Internal.NestedMessageInternal.CODEC::class)
    interface NestedMessage {
        val a: Int
        val corecursive: TestAllRequiredTypesProto2
        val optionalCorecursive: TestAllRequiredTypesProto2

        companion object
    }

    /**
    * groups
    */
    interface Data {
        val groupInt32: Int
        val groupUint32: UInt

        companion object
    }

    /**
    * message_set test case.
    */
    @WithCodec(TestAllRequiredTypesProto2Internal.MessageSetCorrectInternal.CODEC::class)
    interface MessageSetCorrect {
        companion object
    }

    @WithCodec(TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension1Internal.CODEC::class)
    interface MessageSetCorrectExtension1 {
        val str: String

        companion object
    }

    @WithCodec(TestAllRequiredTypesProto2Internal.MessageSetCorrectExtension2Internal.CODEC::class)
    interface MessageSetCorrectExtension2 {
        val i: Int

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

@WithCodec(TestLargeOneofInternal.CODEC::class)
interface TestLargeOneof {
    val largeOneof: LargeOneof?

    sealed interface LargeOneof {
        @JvmInline
        value class A1(val value: TestLargeOneof.A1): LargeOneof

        @JvmInline
        value class A2(val value: TestLargeOneof.A2): LargeOneof

        @JvmInline
        value class A3(val value: TestLargeOneof.A3): LargeOneof

        @JvmInline
        value class A4(val value: TestLargeOneof.A4): LargeOneof

        @JvmInline
        value class A5(val value: TestLargeOneof.A5): LargeOneof
    }

    @WithCodec(TestLargeOneofInternal.A1Internal.CODEC::class)
    interface A1 {
        companion object
    }

    @WithCodec(TestLargeOneofInternal.A2Internal.CODEC::class)
    interface A2 {
        companion object
    }

    @WithCodec(TestLargeOneofInternal.A3Internal.CODEC::class)
    interface A3 {
        companion object
    }

    @WithCodec(TestLargeOneofInternal.A4Internal.CODEC::class)
    interface A4 {
        companion object
    }

    @WithCodec(TestLargeOneofInternal.A5Internal.CODEC::class)
    interface A5 {
        companion object
    }

    companion object
}

sealed class ForeignEnumProto2(open val number: Int) {
    data object FOREIGN_FOO: ForeignEnumProto2(number = 0)

    data object FOREIGN_BAR: ForeignEnumProto2(number = 1)

    data object FOREIGN_BAZ: ForeignEnumProto2(number = 2)

    data class UNRECOGNIZED(override val number: Int): ForeignEnumProto2(number)

    companion object {
        val entries: List<ForeignEnumProto2> by lazy { listOf(FOREIGN_FOO, FOREIGN_BAR, FOREIGN_BAZ) }
    }
}
