/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TestUtil.java
//
// Reflection-based methods (ReflectionTester) are omitted
// as they depend on Java-specific protobuf APIs not available in kotlinx-rpc.

package proto2_unittest

import com.google.protobuf.test.ImportEnum
import com.google.protobuf.test.ImportMessage
import com.google.protobuf.test.PublicImportMessage
import com.google.protobuf.test.invoke
import kotlinx.rpc.protobuf.ProtoExtensionRegistry
import proto2_unittest.TestAllTypes.NestedEnum
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

object TestUtil {
    fun toBytes(str: String): ByteArray = str.encodeToByteArray()

    /**
     * Creates a [TestAllTypes] with all fields set, mirroring Java's TestUtil.getAllSet().
     */
    fun getAllSet(): TestAllTypes = TestAllTypes {
        // Optional fields
        optionalInt32 = 101
        optionalInt64 = 102L
        optionalUint32 = 103u
        optionalUint64 = 104uL
        optionalSint32 = 105
        optionalSint64 = 106L
        optionalFixed32 = 107u
        optionalFixed64 = 108uL
        optionalSfixed32 = 109
        optionalSfixed64 = 110L
        optionalFloat = 111f
        optionalDouble = 112.0
        optionalBool = true
        optionalString = "115"
        optionalBytes = toBytes("116")

        optionalgroup = TestAllTypes.OptionalGroup { a = 117 }
        optionalNestedMessage = TestAllTypes.NestedMessage { bb = 118 }
        optionalForeignMessage = ForeignMessage { c = 119 }
        optionalImportMessage = ImportMessage { d = 120 }
        optionalPublicImportMessage = PublicImportMessage { e = 126 }
        optionalLazyMessage = TestAllTypes.NestedMessage { bb = 127 }
        optionalUnverifiedLazyMessage = TestAllTypes.NestedMessage { bb = 128 }

        optionalNestedEnum = NestedEnum.BAZ
        optionalForeignEnum = ForeignEnum.FOREIGN_BAZ
        optionalImportEnum = ImportEnum.IMPORT_BAZ
        optionalStringPiece = "124"
        optionalCord = "125"

        // Repeated fields (two elements each)
        repeatedInt32 = listOf(201, 301)
        repeatedInt64 = listOf(202L, 302L)
        repeatedUint32 = listOf(203u, 303u)
        repeatedUint64 = listOf(204uL, 304uL)
        repeatedSint32 = listOf(205, 305)
        repeatedSint64 = listOf(206L, 306L)
        repeatedFixed32 = listOf(207u, 307u)
        repeatedFixed64 = listOf(208uL, 308uL)
        repeatedSfixed32 = listOf(209, 309)
        repeatedSfixed64 = listOf(210L, 310L)
        repeatedFloat = listOf(211f, 311f)
        repeatedDouble = listOf(212.0, 312.0)
        repeatedBool = listOf(true, false)
        repeatedString = listOf("215", "315")
        repeatedBytes = listOf(toBytes("216"), toBytes("316"))

        repeatedgroup = listOf(
            TestAllTypes.RepeatedGroup { a = 217 },
            TestAllTypes.RepeatedGroup { a = 317 },
        )
        repeatedNestedMessage = listOf(
            TestAllTypes.NestedMessage { bb = 218 },
            TestAllTypes.NestedMessage { bb = 318 },
        )
        repeatedForeignMessage = listOf(
            ForeignMessage { c = 219 },
            ForeignMessage { c = 319 },
        )
        repeatedImportMessage = listOf(
            ImportMessage { d = 220 },
            ImportMessage { d = 320 },
        )
        repeatedLazyMessage = listOf(
            TestAllTypes.NestedMessage { bb = 227 },
            TestAllTypes.NestedMessage { bb = 327 },
        )

        repeatedNestedEnum = listOf(NestedEnum.BAR, NestedEnum.BAZ)
        repeatedForeignEnum = listOf(ForeignEnum.FOREIGN_BAR, ForeignEnum.FOREIGN_BAZ)
        repeatedImportEnum = listOf(ImportEnum.IMPORT_BAR, ImportEnum.IMPORT_BAZ)
        repeatedStringPiece = listOf("224", "324")
        repeatedCord = listOf("225", "325")

        // Default fields (overridden from proto-defined defaults)
        defaultInt32 = 401
        defaultInt64 = 402L
        defaultUint32 = 403u
        defaultUint64 = 404uL
        defaultSint32 = 405
        defaultSint64 = 406L
        defaultFixed32 = 407u
        defaultFixed64 = 408uL
        defaultSfixed32 = 409
        defaultSfixed64 = 410L
        defaultFloat = 411f
        defaultDouble = 412.0
        defaultBool = false
        defaultString = "415"
        defaultBytes = toBytes("416")
        defaultNestedEnum = NestedEnum.FOO
        defaultForeignEnum = ForeignEnum.FOREIGN_FOO
        defaultImportEnum = ImportEnum.IMPORT_FOO
        defaultStringPiece = "424"
        defaultCord = "425"

        // Oneof field (last one wins)
        oneofField = TestAllTypes.OneofField.OneofBytes(toBytes("604"))
    }

    /**
     * Asserts that all fields in [message] are set to the values from [getAllSet].
     */
    fun assertAllFieldsSet(message: TestAllTypes) {
        // Optional fields
        assertTrue(message.presence.hasOptionalInt32)
        assertTrue(message.presence.hasOptionalInt64)
        assertTrue(message.presence.hasOptionalUint32)
        assertTrue(message.presence.hasOptionalUint64)
        assertTrue(message.presence.hasOptionalSint32)
        assertTrue(message.presence.hasOptionalSint64)
        assertTrue(message.presence.hasOptionalFixed32)
        assertTrue(message.presence.hasOptionalFixed64)
        assertTrue(message.presence.hasOptionalSfixed32)
        assertTrue(message.presence.hasOptionalSfixed64)
        assertTrue(message.presence.hasOptionalFloat)
        assertTrue(message.presence.hasOptionalDouble)
        assertTrue(message.presence.hasOptionalBool)
        assertTrue(message.presence.hasOptionalString)
        assertTrue(message.presence.hasOptionalBytes)
        assertTrue(message.presence.hasOptionalgroup)
        assertTrue(message.presence.hasOptionalNestedMessage)
        assertTrue(message.presence.hasOptionalForeignMessage)
        assertTrue(message.presence.hasOptionalImportMessage)
        assertTrue(message.presence.hasOptionalPublicImportMessage)
        assertTrue(message.presence.hasOptionalLazyMessage)
        assertTrue(message.presence.hasOptionalUnverifiedLazyMessage)
        assertTrue(message.presence.hasOptionalNestedEnum)
        assertTrue(message.presence.hasOptionalForeignEnum)
        assertTrue(message.presence.hasOptionalImportEnum)
        assertTrue(message.presence.hasOptionalStringPiece)
        assertTrue(message.presence.hasOptionalCord)

        assertEquals(101, message.optionalInt32)
        assertEquals(102L, message.optionalInt64)
        assertEquals(103u, message.optionalUint32)
        assertEquals(104uL, message.optionalUint64)
        assertEquals(105, message.optionalSint32)
        assertEquals(106L, message.optionalSint64)
        assertEquals(107u, message.optionalFixed32)
        assertEquals(108uL, message.optionalFixed64)
        assertEquals(109, message.optionalSfixed32)
        assertEquals(110L, message.optionalSfixed64)
        assertEquals(111f, message.optionalFloat)
        assertEquals(112.0, message.optionalDouble)
        assertEquals(true, message.optionalBool)
        assertEquals("115", message.optionalString)
        assertByteArrayEquals(toBytes("116"), message.optionalBytes)

        assertEquals(117, message.optionalgroup.a)
        assertEquals(118, message.optionalNestedMessage.bb)
        assertEquals(119, message.optionalForeignMessage.c)
        assertEquals(120, message.optionalImportMessage.d)
        assertEquals(126, message.optionalPublicImportMessage.e)
        assertEquals(127, message.optionalLazyMessage.bb)
        assertEquals(128, message.optionalUnverifiedLazyMessage.bb)

        assertEquals(NestedEnum.BAZ, message.optionalNestedEnum)
        assertEquals(ForeignEnum.FOREIGN_BAZ, message.optionalForeignEnum)
        assertEquals(ImportEnum.IMPORT_BAZ, message.optionalImportEnum)
        assertEquals("124", message.optionalStringPiece)
        assertEquals("125", message.optionalCord)

        // Repeated fields
        assertEquals(2, message.repeatedInt32.size)
        assertEquals(2, message.repeatedInt64.size)
        assertEquals(2, message.repeatedUint32.size)
        assertEquals(2, message.repeatedUint64.size)
        assertEquals(2, message.repeatedSint32.size)
        assertEquals(2, message.repeatedSint64.size)
        assertEquals(2, message.repeatedFixed32.size)
        assertEquals(2, message.repeatedFixed64.size)
        assertEquals(2, message.repeatedSfixed32.size)
        assertEquals(2, message.repeatedSfixed64.size)
        assertEquals(2, message.repeatedFloat.size)
        assertEquals(2, message.repeatedDouble.size)
        assertEquals(2, message.repeatedBool.size)
        assertEquals(2, message.repeatedString.size)
        assertEquals(2, message.repeatedBytes.size)
        assertEquals(2, message.repeatedgroup.size)
        assertEquals(2, message.repeatedNestedMessage.size)
        assertEquals(2, message.repeatedForeignMessage.size)
        assertEquals(2, message.repeatedImportMessage.size)
        assertEquals(2, message.repeatedLazyMessage.size)
        assertEquals(2, message.repeatedNestedEnum.size)
        assertEquals(2, message.repeatedForeignEnum.size)
        assertEquals(2, message.repeatedImportEnum.size)
        assertEquals(2, message.repeatedStringPiece.size)
        assertEquals(2, message.repeatedCord.size)

        assertEquals(201, message.repeatedInt32[0])
        assertEquals(202L, message.repeatedInt64[0])
        assertEquals(203u, message.repeatedUint32[0])
        assertEquals(204uL, message.repeatedUint64[0])
        assertEquals(205, message.repeatedSint32[0])
        assertEquals(206L, message.repeatedSint64[0])
        assertEquals(207u, message.repeatedFixed32[0])
        assertEquals(208uL, message.repeatedFixed64[0])
        assertEquals(209, message.repeatedSfixed32[0])
        assertEquals(210L, message.repeatedSfixed64[0])
        assertEquals(211f, message.repeatedFloat[0])
        assertEquals(212.0, message.repeatedDouble[0])
        assertEquals(true, message.repeatedBool[0])
        assertEquals("215", message.repeatedString[0])
        assertByteArrayEquals(toBytes("216"), message.repeatedBytes[0])
        assertEquals(217, message.repeatedgroup[0].a)
        assertEquals(218, message.repeatedNestedMessage[0].bb)
        assertEquals(219, message.repeatedForeignMessage[0].c)
        assertEquals(220, message.repeatedImportMessage[0].d)
        assertEquals(227, message.repeatedLazyMessage[0].bb)
        assertEquals(NestedEnum.BAR, message.repeatedNestedEnum[0])
        assertEquals(ForeignEnum.FOREIGN_BAR, message.repeatedForeignEnum[0])
        assertEquals(ImportEnum.IMPORT_BAR, message.repeatedImportEnum[0])
        assertEquals("224", message.repeatedStringPiece[0])
        assertEquals("225", message.repeatedCord[0])

        assertEquals(301, message.repeatedInt32[1])
        assertEquals(302L, message.repeatedInt64[1])
        assertEquals(303u, message.repeatedUint32[1])
        assertEquals(304uL, message.repeatedUint64[1])
        assertEquals(305, message.repeatedSint32[1])
        assertEquals(306L, message.repeatedSint64[1])
        assertEquals(307u, message.repeatedFixed32[1])
        assertEquals(308uL, message.repeatedFixed64[1])
        assertEquals(309, message.repeatedSfixed32[1])
        assertEquals(310L, message.repeatedSfixed64[1])
        assertEquals(311f, message.repeatedFloat[1])
        assertEquals(312.0, message.repeatedDouble[1])
        assertEquals(false, message.repeatedBool[1])
        assertEquals("315", message.repeatedString[1])
        assertByteArrayEquals(toBytes("316"), message.repeatedBytes[1])
        assertEquals(317, message.repeatedgroup[1].a)
        assertEquals(318, message.repeatedNestedMessage[1].bb)
        assertEquals(319, message.repeatedForeignMessage[1].c)
        assertEquals(320, message.repeatedImportMessage[1].d)
        assertEquals(327, message.repeatedLazyMessage[1].bb)
        assertEquals(NestedEnum.BAZ, message.repeatedNestedEnum[1])
        assertEquals(ForeignEnum.FOREIGN_BAZ, message.repeatedForeignEnum[1])
        assertEquals(ImportEnum.IMPORT_BAZ, message.repeatedImportEnum[1])
        assertEquals("324", message.repeatedStringPiece[1])
        assertEquals("325", message.repeatedCord[1])

        // Default fields (overridden)
        assertTrue(message.presence.hasDefaultInt32)
        assertTrue(message.presence.hasDefaultInt64)
        assertTrue(message.presence.hasDefaultUint32)
        assertTrue(message.presence.hasDefaultUint64)
        assertTrue(message.presence.hasDefaultSint32)
        assertTrue(message.presence.hasDefaultSint64)
        assertTrue(message.presence.hasDefaultFixed32)
        assertTrue(message.presence.hasDefaultFixed64)
        assertTrue(message.presence.hasDefaultSfixed32)
        assertTrue(message.presence.hasDefaultSfixed64)
        assertTrue(message.presence.hasDefaultFloat)
        assertTrue(message.presence.hasDefaultDouble)
        assertTrue(message.presence.hasDefaultBool)
        assertTrue(message.presence.hasDefaultString)
        assertTrue(message.presence.hasDefaultBytes)
        assertTrue(message.presence.hasDefaultNestedEnum)
        assertTrue(message.presence.hasDefaultForeignEnum)
        assertTrue(message.presence.hasDefaultImportEnum)
        assertTrue(message.presence.hasDefaultStringPiece)
        assertTrue(message.presence.hasDefaultCord)

        assertEquals(401, message.defaultInt32)
        assertEquals(402L, message.defaultInt64)
        assertEquals(403u, message.defaultUint32)
        assertEquals(404uL, message.defaultUint64)
        assertEquals(405, message.defaultSint32)
        assertEquals(406L, message.defaultSint64)
        assertEquals(407u, message.defaultFixed32)
        assertEquals(408uL, message.defaultFixed64)
        assertEquals(409, message.defaultSfixed32)
        assertEquals(410L, message.defaultSfixed64)
        assertEquals(411f, message.defaultFloat)
        assertEquals(412.0, message.defaultDouble)
        assertEquals(false, message.defaultBool)
        assertEquals("415", message.defaultString)
        assertByteArrayEquals(toBytes("416"), message.defaultBytes)
        assertEquals(NestedEnum.FOO, message.defaultNestedEnum)
        assertEquals(ForeignEnum.FOREIGN_FOO, message.defaultForeignEnum)
        assertEquals(ImportEnum.IMPORT_FOO, message.defaultImportEnum)
        assertEquals("424", message.defaultStringPiece)
        assertEquals("425", message.defaultCord)

        // Oneof
        val oneofField = message.oneofField
        assertTrue(oneofField is TestAllTypes.OneofField.OneofBytes)
        assertByteArrayEquals(toBytes("604"), oneofField.value)
    }

    /**
     * Asserts that all fields in [message] are cleared to their default state.
     */
    fun assertClear(message: TestAllTypes) {
        // All optional fields should not be present
        assertFalse(message.presence.hasOptionalInt32)
        assertFalse(message.presence.hasOptionalInt64)
        assertFalse(message.presence.hasOptionalUint32)
        assertFalse(message.presence.hasOptionalUint64)
        assertFalse(message.presence.hasOptionalSint32)
        assertFalse(message.presence.hasOptionalSint64)
        assertFalse(message.presence.hasOptionalFixed32)
        assertFalse(message.presence.hasOptionalFixed64)
        assertFalse(message.presence.hasOptionalSfixed32)
        assertFalse(message.presence.hasOptionalSfixed64)
        assertFalse(message.presence.hasOptionalFloat)
        assertFalse(message.presence.hasOptionalDouble)
        assertFalse(message.presence.hasOptionalBool)
        assertFalse(message.presence.hasOptionalString)
        assertFalse(message.presence.hasOptionalBytes)
        assertFalse(message.presence.hasOptionalgroup)
        assertFalse(message.presence.hasOptionalNestedMessage)
        assertFalse(message.presence.hasOptionalForeignMessage)
        assertFalse(message.presence.hasOptionalImportMessage)
        assertFalse(message.presence.hasOptionalNestedEnum)
        assertFalse(message.presence.hasOptionalForeignEnum)
        assertFalse(message.presence.hasOptionalImportEnum)
        assertFalse(message.presence.hasOptionalStringPiece)
        assertFalse(message.presence.hasOptionalCord)

        // Optional scalar defaults
        assertNull(message.optionalInt32)
        assertNull(message.optionalInt64)
        assertNull(message.optionalUint32)
        assertNull(message.optionalUint64)
        assertNull(message.optionalSint32)
        assertNull(message.optionalSint64)
        assertNull(message.optionalFixed32)
        assertNull(message.optionalFixed64)
        assertNull(message.optionalSfixed32)
        assertNull(message.optionalSfixed64)
        assertNull(message.optionalFloat)
        assertNull(message.optionalDouble)
        assertNull(message.optionalBool)
        assertNull(message.optionalString)
        assertNull(message.optionalBytes)
        assertNull(message.optionalNestedEnum)
        assertNull(message.optionalForeignEnum)
        assertNull(message.optionalImportEnum)
        assertNull(message.optionalStringPiece)
        assertNull(message.optionalCord)

        // Repeated fields should be empty
        assertEquals(0, message.repeatedInt32.size)
        assertEquals(0, message.repeatedInt64.size)
        assertEquals(0, message.repeatedUint32.size)
        assertEquals(0, message.repeatedUint64.size)
        assertEquals(0, message.repeatedSint32.size)
        assertEquals(0, message.repeatedSint64.size)
        assertEquals(0, message.repeatedFixed32.size)
        assertEquals(0, message.repeatedFixed64.size)
        assertEquals(0, message.repeatedSfixed32.size)
        assertEquals(0, message.repeatedSfixed64.size)
        assertEquals(0, message.repeatedFloat.size)
        assertEquals(0, message.repeatedDouble.size)
        assertEquals(0, message.repeatedBool.size)
        assertEquals(0, message.repeatedString.size)
        assertEquals(0, message.repeatedBytes.size)
        assertEquals(0, message.repeatedgroup.size)
        assertEquals(0, message.repeatedNestedMessage.size)
        assertEquals(0, message.repeatedForeignMessage.size)
        assertEquals(0, message.repeatedImportMessage.size)
        assertEquals(0, message.repeatedLazyMessage.size)
        assertEquals(0, message.repeatedNestedEnum.size)
        assertEquals(0, message.repeatedForeignEnum.size)
        assertEquals(0, message.repeatedImportEnum.size)
        assertEquals(0, message.repeatedStringPiece.size)
        assertEquals(0, message.repeatedCord.size)

        // Default fields: not present but have proto-defined defaults
        assertFalse(message.presence.hasDefaultInt32)
        assertFalse(message.presence.hasDefaultInt64)
        assertFalse(message.presence.hasDefaultUint32)
        assertFalse(message.presence.hasDefaultUint64)
        assertFalse(message.presence.hasDefaultSint32)
        assertFalse(message.presence.hasDefaultSint64)
        assertFalse(message.presence.hasDefaultFixed32)
        assertFalse(message.presence.hasDefaultFixed64)
        assertFalse(message.presence.hasDefaultSfixed32)
        assertFalse(message.presence.hasDefaultSfixed64)
        assertFalse(message.presence.hasDefaultFloat)
        assertFalse(message.presence.hasDefaultDouble)
        assertFalse(message.presence.hasDefaultBool)
        assertFalse(message.presence.hasDefaultString)
        assertFalse(message.presence.hasDefaultBytes)
        assertFalse(message.presence.hasDefaultNestedEnum)
        assertFalse(message.presence.hasDefaultForeignEnum)
        assertFalse(message.presence.hasDefaultImportEnum)
        assertFalse(message.presence.hasDefaultStringPiece)
        assertFalse(message.presence.hasDefaultCord)

        // Proto-defined default values
        assertEquals(41, message.defaultInt32)
        assertEquals(42L, message.defaultInt64)
        assertEquals(43u, message.defaultUint32)
        assertEquals(44uL, message.defaultUint64)
        assertEquals(-45, message.defaultSint32)
        assertEquals(46L, message.defaultSint64)
        assertEquals(47u, message.defaultFixed32)
        assertEquals(48uL, message.defaultFixed64)
        assertEquals(49, message.defaultSfixed32)
        assertEquals(-50L, message.defaultSfixed64)
        assertEquals(51.5f, message.defaultFloat)
        assertEquals(52e3, message.defaultDouble)
        assertEquals(true, message.defaultBool)
        assertEquals("hello", message.defaultString)
        assertByteArrayEquals("world".encodeToByteArray(), message.defaultBytes)
        assertEquals(NestedEnum.BAR, message.defaultNestedEnum)
        assertEquals(ForeignEnum.FOREIGN_BAR, message.defaultForeignEnum)
        assertEquals(ImportEnum.IMPORT_BAR, message.defaultImportEnum)
        assertEquals("abc", message.defaultStringPiece)
        assertEquals("123", message.defaultCord)

        // Oneof not set
        assertNull(message.oneofField)
    }

    /**
     * Creates a [TestPackedTypes] with all packed fields populated.
     */
    fun getPackedSet(): TestPackedTypes = TestPackedTypes {
        packedInt32 = listOf(601, 701)
        packedInt64 = listOf(602L, 702L)
        packedUint32 = listOf(603u, 703u)
        packedUint64 = listOf(604uL, 704uL)
        packedSint32 = listOf(605, 705)
        packedSint64 = listOf(606L, 706L)
        packedFixed32 = listOf(607u, 707u)
        packedFixed64 = listOf(608uL, 708uL)
        packedSfixed32 = listOf(609, 709)
        packedSfixed64 = listOf(610L, 710L)
        packedFloat = listOf(611f, 711f)
        packedDouble = listOf(612.0, 712.0)
        packedBool = listOf(true, false)
        packedEnum = listOf(ForeignEnum.FOREIGN_BAR, ForeignEnum.FOREIGN_BAZ)
    }

    fun assertPackedFieldsSet(message: TestPackedTypes) {
        assertEquals(2, message.packedInt32.size)
        assertEquals(2, message.packedInt64.size)
        assertEquals(2, message.packedUint32.size)
        assertEquals(2, message.packedUint64.size)
        assertEquals(2, message.packedSint32.size)
        assertEquals(2, message.packedSint64.size)
        assertEquals(2, message.packedFixed32.size)
        assertEquals(2, message.packedFixed64.size)
        assertEquals(2, message.packedSfixed32.size)
        assertEquals(2, message.packedSfixed64.size)
        assertEquals(2, message.packedFloat.size)
        assertEquals(2, message.packedDouble.size)
        assertEquals(2, message.packedBool.size)
        assertEquals(2, message.packedEnum.size)

        assertEquals(601, message.packedInt32[0])
        assertEquals(602L, message.packedInt64[0])
        assertEquals(603u, message.packedUint32[0])
        assertEquals(604uL, message.packedUint64[0])
        assertEquals(605, message.packedSint32[0])
        assertEquals(606L, message.packedSint64[0])
        assertEquals(607u, message.packedFixed32[0])
        assertEquals(608uL, message.packedFixed64[0])
        assertEquals(609, message.packedSfixed32[0])
        assertEquals(610L, message.packedSfixed64[0])
        assertEquals(611f, message.packedFloat[0])
        assertEquals(612.0, message.packedDouble[0])
        assertEquals(true, message.packedBool[0])
        assertEquals(ForeignEnum.FOREIGN_BAR, message.packedEnum[0])

        assertEquals(701, message.packedInt32[1])
        assertEquals(702L, message.packedInt64[1])
        assertEquals(703u, message.packedUint32[1])
        assertEquals(704uL, message.packedUint64[1])
        assertEquals(705, message.packedSint32[1])
        assertEquals(706L, message.packedSint64[1])
        assertEquals(707u, message.packedFixed32[1])
        assertEquals(708uL, message.packedFixed64[1])
        assertEquals(709, message.packedSfixed32[1])
        assertEquals(710L, message.packedSfixed64[1])
        assertEquals(711f, message.packedFloat[1])
        assertEquals(712.0, message.packedDouble[1])
        assertEquals(false, message.packedBool[1])
        assertEquals(ForeignEnum.FOREIGN_BAZ, message.packedEnum[1])
    }

    /**
     * Creates a [TestUnpackedTypes] with all unpacked fields populated (same values as packed).
     */
    fun getUnpackedSet(): TestUnpackedTypes = TestUnpackedTypes {
        unpackedInt32 = listOf(601, 701)
        unpackedInt64 = listOf(602L, 702L)
        unpackedUint32 = listOf(603u, 703u)
        unpackedUint64 = listOf(604uL, 704uL)
        unpackedSint32 = listOf(605, 705)
        unpackedSint64 = listOf(606L, 706L)
        unpackedFixed32 = listOf(607u, 707u)
        unpackedFixed64 = listOf(608uL, 708uL)
        unpackedSfixed32 = listOf(609, 709)
        unpackedSfixed64 = listOf(610L, 710L)
        unpackedFloat = listOf(611f, 711f)
        unpackedDouble = listOf(612.0, 712.0)
        unpackedBool = listOf(true, false)
        unpackedEnum = listOf(ForeignEnum.FOREIGN_BAR, ForeignEnum.FOREIGN_BAZ)
    }

    fun assertUnpackedFieldsSet(message: TestUnpackedTypes) {
        assertEquals(2, message.unpackedInt32.size)
        assertEquals(2, message.unpackedInt64.size)
        assertEquals(2, message.unpackedUint32.size)
        assertEquals(2, message.unpackedUint64.size)
        assertEquals(2, message.unpackedSint32.size)
        assertEquals(2, message.unpackedSint64.size)
        assertEquals(2, message.unpackedFixed32.size)
        assertEquals(2, message.unpackedFixed64.size)
        assertEquals(2, message.unpackedSfixed32.size)
        assertEquals(2, message.unpackedSfixed64.size)
        assertEquals(2, message.unpackedFloat.size)
        assertEquals(2, message.unpackedDouble.size)
        assertEquals(2, message.unpackedBool.size)
        assertEquals(2, message.unpackedEnum.size)

        assertEquals(601, message.unpackedInt32[0])
        assertEquals(602L, message.unpackedInt64[0])
        assertEquals(603u, message.unpackedUint32[0])
        assertEquals(604uL, message.unpackedUint64[0])
        assertEquals(605, message.unpackedSint32[0])
        assertEquals(606L, message.unpackedSint64[0])
        assertEquals(607u, message.unpackedFixed32[0])
        assertEquals(608uL, message.unpackedFixed64[0])
        assertEquals(609, message.unpackedSfixed32[0])
        assertEquals(610L, message.unpackedSfixed64[0])
        assertEquals(611f, message.unpackedFloat[0])
        assertEquals(612.0, message.unpackedDouble[0])
        assertEquals(true, message.unpackedBool[0])
        assertEquals(ForeignEnum.FOREIGN_BAR, message.unpackedEnum[0])

        assertEquals(701, message.unpackedInt32[1])
        assertEquals(702L, message.unpackedInt64[1])
        assertEquals(703u, message.unpackedUint32[1])
        assertEquals(704uL, message.unpackedUint64[1])
        assertEquals(705, message.unpackedSint32[1])
        assertEquals(706L, message.unpackedSint64[1])
        assertEquals(707u, message.unpackedFixed32[1])
        assertEquals(708uL, message.unpackedFixed64[1])
        assertEquals(709, message.unpackedSfixed32[1])
        assertEquals(710L, message.unpackedSfixed64[1])
        assertEquals(711f, message.unpackedFloat[1])
        assertEquals(712.0, message.unpackedDouble[1])
        assertEquals(false, message.unpackedBool[1])
        assertEquals(ForeignEnum.FOREIGN_BAZ, message.unpackedEnum[1])
    }

    fun <M> encodeDecode(msg: M, marshaller: kotlinx.rpc.grpc.marshaller.GrpcMarshaller<M>): M {
        val source = marshaller.encode(msg)
        return marshaller.decode(source)
    }

    // ===========================================================================================================
    // Extension helpers — translated from TestUtil.java extension methods
    // ===========================================================================================================

    /**
     * Creates a [TestAllExtensions] with all extension fields set,
     * mirroring Java's TestUtil.getAllExtensionsSet().
     * Uses the same field values as [getAllSet] since extension field numbers match TestAllTypes.
     */
    fun getAllExtensionsSet(): TestAllExtensions = TestAllExtensions {
        // Optional extensions
        optionalInt32Extension = 101
        optionalInt64Extension = 102L
        optionalUint32Extension = 103u
        optionalUint64Extension = 104uL
        optionalSint32Extension = 105
        optionalSint64Extension = 106L
        optionalFixed32Extension = 107u
        optionalFixed64Extension = 108uL
        optionalSfixed32Extension = 109
        optionalSfixed64Extension = 110L
        optionalFloatExtension = 111f
        optionalDoubleExtension = 112.0
        optionalBoolExtension = true
        optionalStringExtension = "115"
        optionalBytesExtension = toBytes("116")

        optionalgroupExtension = OptionalGroupExtension { a = 117 }
        optionalNestedMessageExtension = TestAllTypes.NestedMessage { bb = 118 }
        optionalForeignMessageExtension = ForeignMessage { c = 119 }
        optionalImportMessageExtension = ImportMessage { d = 120 }
        optionalPublicImportMessageExtension = PublicImportMessage { e = 126 }
        optionalLazyMessageExtension = TestAllTypes.NestedMessage { bb = 127 }
        optionalUnverifiedLazyMessageExtension = TestAllTypes.NestedMessage { bb = 128 }

        optionalNestedEnumExtension = NestedEnum.BAZ
        optionalForeignEnumExtension = ForeignEnum.FOREIGN_BAZ
        optionalImportEnumExtension = ImportEnum.IMPORT_BAZ
        optionalStringPieceExtension = "124"
        optionalCordExtension = "125"

        // Repeated extensions (two elements each)
        repeatedInt32Extension = listOf(201, 301)
        repeatedInt64Extension = listOf(202L, 302L)
        repeatedUint32Extension = listOf(203u, 303u)
        repeatedUint64Extension = listOf(204uL, 304uL)
        repeatedSint32Extension = listOf(205, 305)
        repeatedSint64Extension = listOf(206L, 306L)
        repeatedFixed32Extension = listOf(207u, 307u)
        repeatedFixed64Extension = listOf(208uL, 308uL)
        repeatedSfixed32Extension = listOf(209, 309)
        repeatedSfixed64Extension = listOf(210L, 310L)
        repeatedFloatExtension = listOf(211f, 311f)
        repeatedDoubleExtension = listOf(212.0, 312.0)
        repeatedBoolExtension = listOf(true, false)
        repeatedStringExtension = listOf("215", "315")
        repeatedBytesExtension = listOf(toBytes("216"), toBytes("316"))

        repeatedgroupExtension = listOf(
            RepeatedGroupExtension { a = 217 },
            RepeatedGroupExtension { a = 317 },
        )
        repeatedNestedMessageExtension = listOf(
            TestAllTypes.NestedMessage { bb = 218 },
            TestAllTypes.NestedMessage { bb = 318 },
        )
        repeatedForeignMessageExtension = listOf(
            ForeignMessage { c = 219 },
            ForeignMessage { c = 319 },
        )
        repeatedImportMessageExtension = listOf(
            ImportMessage { d = 220 },
            ImportMessage { d = 320 },
        )
        repeatedLazyMessageExtension = listOf(
            TestAllTypes.NestedMessage { bb = 227 },
            TestAllTypes.NestedMessage { bb = 327 },
        )

        repeatedNestedEnumExtension = listOf(NestedEnum.BAR, NestedEnum.BAZ)
        repeatedForeignEnumExtension = listOf(ForeignEnum.FOREIGN_BAR, ForeignEnum.FOREIGN_BAZ)
        repeatedImportEnumExtension = listOf(ImportEnum.IMPORT_BAR, ImportEnum.IMPORT_BAZ)
        repeatedStringPieceExtension = listOf("224", "324")
        repeatedCordExtension = listOf("225", "325")

        // Default extensions (overridden from proto-defined defaults)
        defaultInt32Extension = 401
        defaultInt64Extension = 402L
        defaultUint32Extension = 403u
        defaultUint64Extension = 404uL
        defaultSint32Extension = 405
        defaultSint64Extension = 406L
        defaultFixed32Extension = 407u
        defaultFixed64Extension = 408uL
        defaultSfixed32Extension = 409
        defaultSfixed64Extension = 410L
        defaultFloatExtension = 411f
        defaultDoubleExtension = 412.0
        defaultBoolExtension = false
        defaultStringExtension = "415"
        defaultBytesExtension = toBytes("416")
        defaultNestedEnumExtension = NestedEnum.FOO
        defaultForeignEnumExtension = ForeignEnum.FOREIGN_FOO
        defaultImportEnumExtension = ImportEnum.IMPORT_FOO
        defaultStringPieceExtension = "424"
        defaultCordExtension = "425"

        // Oneof extension (only set one, matching regular test)
        oneofBytesExtension = toBytes("604")
    }

    /**
     * Asserts that all extension fields in [message] are set to the values from [getAllExtensionsSet].
     */
    fun assertAllExtensionsSet(message: TestAllExtensions) {
        // Optional extensions
        assertTrue(message.presence.hasOptionalInt32Extension)
        assertTrue(message.presence.hasOptionalInt64Extension)
        assertTrue(message.presence.hasOptionalUint32Extension)
        assertTrue(message.presence.hasOptionalUint64Extension)
        assertTrue(message.presence.hasOptionalSint32Extension)
        assertTrue(message.presence.hasOptionalSint64Extension)
        assertTrue(message.presence.hasOptionalFixed32Extension)
        assertTrue(message.presence.hasOptionalFixed64Extension)
        assertTrue(message.presence.hasOptionalSfixed32Extension)
        assertTrue(message.presence.hasOptionalSfixed64Extension)
        assertTrue(message.presence.hasOptionalFloatExtension)
        assertTrue(message.presence.hasOptionalDoubleExtension)
        assertTrue(message.presence.hasOptionalBoolExtension)
        assertTrue(message.presence.hasOptionalStringExtension)
        assertTrue(message.presence.hasOptionalBytesExtension)
        assertTrue(message.presence.hasOptionalgroupExtension)
        assertTrue(message.presence.hasOptionalNestedMessageExtension)
        assertTrue(message.presence.hasOptionalForeignMessageExtension)
        assertTrue(message.presence.hasOptionalImportMessageExtension)
        assertTrue(message.presence.hasOptionalPublicImportMessageExtension)
        assertTrue(message.presence.hasOptionalLazyMessageExtension)
        assertTrue(message.presence.hasOptionalUnverifiedLazyMessageExtension)
        assertTrue(message.presence.hasOptionalNestedEnumExtension)
        assertTrue(message.presence.hasOptionalForeignEnumExtension)
        assertTrue(message.presence.hasOptionalImportEnumExtension)
        assertTrue(message.presence.hasOptionalStringPieceExtension)
        assertTrue(message.presence.hasOptionalCordExtension)

        assertEquals(101, message.optionalInt32Extension)
        assertEquals(102L, message.optionalInt64Extension)
        assertEquals(103u, message.optionalUint32Extension)
        assertEquals(104uL, message.optionalUint64Extension)
        assertEquals(105, message.optionalSint32Extension)
        assertEquals(106L, message.optionalSint64Extension)
        assertEquals(107u, message.optionalFixed32Extension)
        assertEquals(108uL, message.optionalFixed64Extension)
        assertEquals(109, message.optionalSfixed32Extension)
        assertEquals(110L, message.optionalSfixed64Extension)
        assertEquals(111f, message.optionalFloatExtension)
        assertEquals(112.0, message.optionalDoubleExtension)
        assertEquals(true, message.optionalBoolExtension)
        assertEquals("115", message.optionalStringExtension)
        assertByteArrayEquals(toBytes("116"), message.optionalBytesExtension)

        assertEquals(117, message.optionalgroupExtension.a)
        assertEquals(118, message.optionalNestedMessageExtension.bb)
        assertEquals(119, message.optionalForeignMessageExtension.c)
        assertEquals(120, message.optionalImportMessageExtension.d)
        assertEquals(126, message.optionalPublicImportMessageExtension.e)
        assertEquals(127, message.optionalLazyMessageExtension.bb)
        assertEquals(128, message.optionalUnverifiedLazyMessageExtension.bb)

        assertEquals(NestedEnum.BAZ, message.optionalNestedEnumExtension)
        assertEquals(ForeignEnum.FOREIGN_BAZ, message.optionalForeignEnumExtension)
        assertEquals(ImportEnum.IMPORT_BAZ, message.optionalImportEnumExtension)
        assertEquals("124", message.optionalStringPieceExtension)
        assertEquals("125", message.optionalCordExtension)

        // Repeated extensions
        assertEquals(2, message.repeatedInt32Extension.size)
        assertEquals(2, message.repeatedInt64Extension.size)
        assertEquals(2, message.repeatedUint32Extension.size)
        assertEquals(2, message.repeatedUint64Extension.size)
        assertEquals(2, message.repeatedSint32Extension.size)
        assertEquals(2, message.repeatedSint64Extension.size)
        assertEquals(2, message.repeatedFixed32Extension.size)
        assertEquals(2, message.repeatedFixed64Extension.size)
        assertEquals(2, message.repeatedSfixed32Extension.size)
        assertEquals(2, message.repeatedSfixed64Extension.size)
        assertEquals(2, message.repeatedFloatExtension.size)
        assertEquals(2, message.repeatedDoubleExtension.size)
        assertEquals(2, message.repeatedBoolExtension.size)
        assertEquals(2, message.repeatedStringExtension.size)
        assertEquals(2, message.repeatedBytesExtension.size)
        assertEquals(2, message.repeatedgroupExtension.size)
        assertEquals(2, message.repeatedNestedMessageExtension.size)
        assertEquals(2, message.repeatedForeignMessageExtension.size)
        assertEquals(2, message.repeatedImportMessageExtension.size)
        assertEquals(2, message.repeatedLazyMessageExtension.size)
        assertEquals(2, message.repeatedNestedEnumExtension.size)
        assertEquals(2, message.repeatedForeignEnumExtension.size)
        assertEquals(2, message.repeatedImportEnumExtension.size)
        assertEquals(2, message.repeatedStringPieceExtension.size)
        assertEquals(2, message.repeatedCordExtension.size)

        assertEquals(201, message.repeatedInt32Extension[0])
        assertEquals(202L, message.repeatedInt64Extension[0])
        assertEquals(203u, message.repeatedUint32Extension[0])
        assertEquals(204uL, message.repeatedUint64Extension[0])
        assertEquals(205, message.repeatedSint32Extension[0])
        assertEquals(206L, message.repeatedSint64Extension[0])
        assertEquals(207u, message.repeatedFixed32Extension[0])
        assertEquals(208uL, message.repeatedFixed64Extension[0])
        assertEquals(209, message.repeatedSfixed32Extension[0])
        assertEquals(210L, message.repeatedSfixed64Extension[0])
        assertEquals(211f, message.repeatedFloatExtension[0])
        assertEquals(212.0, message.repeatedDoubleExtension[0])
        assertEquals(true, message.repeatedBoolExtension[0])
        assertEquals("215", message.repeatedStringExtension[0])
        assertByteArrayEquals(toBytes("216"), message.repeatedBytesExtension[0])
        assertEquals(217, message.repeatedgroupExtension[0].a)
        assertEquals(218, message.repeatedNestedMessageExtension[0].bb)
        assertEquals(219, message.repeatedForeignMessageExtension[0].c)
        assertEquals(220, message.repeatedImportMessageExtension[0].d)
        assertEquals(227, message.repeatedLazyMessageExtension[0].bb)
        assertEquals(NestedEnum.BAR, message.repeatedNestedEnumExtension[0])
        assertEquals(ForeignEnum.FOREIGN_BAR, message.repeatedForeignEnumExtension[0])
        assertEquals(ImportEnum.IMPORT_BAR, message.repeatedImportEnumExtension[0])
        assertEquals("224", message.repeatedStringPieceExtension[0])
        assertEquals("225", message.repeatedCordExtension[0])

        assertEquals(301, message.repeatedInt32Extension[1])
        assertEquals(302L, message.repeatedInt64Extension[1])
        assertEquals(303u, message.repeatedUint32Extension[1])
        assertEquals(304uL, message.repeatedUint64Extension[1])
        assertEquals(305, message.repeatedSint32Extension[1])
        assertEquals(306L, message.repeatedSint64Extension[1])
        assertEquals(307u, message.repeatedFixed32Extension[1])
        assertEquals(308uL, message.repeatedFixed64Extension[1])
        assertEquals(309, message.repeatedSfixed32Extension[1])
        assertEquals(310L, message.repeatedSfixed64Extension[1])
        assertEquals(311f, message.repeatedFloatExtension[1])
        assertEquals(312.0, message.repeatedDoubleExtension[1])
        assertEquals(false, message.repeatedBoolExtension[1])
        assertEquals("315", message.repeatedStringExtension[1])
        assertByteArrayEquals(toBytes("316"), message.repeatedBytesExtension[1])
        assertEquals(317, message.repeatedgroupExtension[1].a)
        assertEquals(318, message.repeatedNestedMessageExtension[1].bb)
        assertEquals(319, message.repeatedForeignMessageExtension[1].c)
        assertEquals(320, message.repeatedImportMessageExtension[1].d)
        assertEquals(327, message.repeatedLazyMessageExtension[1].bb)
        assertEquals(NestedEnum.BAZ, message.repeatedNestedEnumExtension[1])
        assertEquals(ForeignEnum.FOREIGN_BAZ, message.repeatedForeignEnumExtension[1])
        assertEquals(ImportEnum.IMPORT_BAZ, message.repeatedImportEnumExtension[1])
        assertEquals("324", message.repeatedStringPieceExtension[1])
        assertEquals("325", message.repeatedCordExtension[1])

        // Default extensions (overridden)
        assertTrue(message.presence.hasDefaultInt32Extension)
        assertTrue(message.presence.hasDefaultInt64Extension)
        assertTrue(message.presence.hasDefaultUint32Extension)
        assertTrue(message.presence.hasDefaultUint64Extension)
        assertTrue(message.presence.hasDefaultSint32Extension)
        assertTrue(message.presence.hasDefaultSint64Extension)
        assertTrue(message.presence.hasDefaultFixed32Extension)
        assertTrue(message.presence.hasDefaultFixed64Extension)
        assertTrue(message.presence.hasDefaultSfixed32Extension)
        assertTrue(message.presence.hasDefaultSfixed64Extension)
        assertTrue(message.presence.hasDefaultFloatExtension)
        assertTrue(message.presence.hasDefaultDoubleExtension)
        assertTrue(message.presence.hasDefaultBoolExtension)
        assertTrue(message.presence.hasDefaultStringExtension)
        assertTrue(message.presence.hasDefaultBytesExtension)
        assertTrue(message.presence.hasDefaultNestedEnumExtension)
        assertTrue(message.presence.hasDefaultForeignEnumExtension)
        assertTrue(message.presence.hasDefaultImportEnumExtension)
        assertTrue(message.presence.hasDefaultStringPieceExtension)
        assertTrue(message.presence.hasDefaultCordExtension)

        assertEquals(401, message.defaultInt32Extension)
        assertEquals(402L, message.defaultInt64Extension)
        assertEquals(403u, message.defaultUint32Extension)
        assertEquals(404uL, message.defaultUint64Extension)
        assertEquals(405, message.defaultSint32Extension)
        assertEquals(406L, message.defaultSint64Extension)
        assertEquals(407u, message.defaultFixed32Extension)
        assertEquals(408uL, message.defaultFixed64Extension)
        assertEquals(409, message.defaultSfixed32Extension)
        assertEquals(410L, message.defaultSfixed64Extension)
        assertEquals(411f, message.defaultFloatExtension)
        assertEquals(412.0, message.defaultDoubleExtension)
        assertEquals(false, message.defaultBoolExtension)
        assertEquals("415", message.defaultStringExtension)
        assertByteArrayEquals(toBytes("416"), message.defaultBytesExtension)
        assertEquals(NestedEnum.FOO, message.defaultNestedEnumExtension)
        assertEquals(ForeignEnum.FOREIGN_FOO, message.defaultForeignEnumExtension)
        assertEquals(ImportEnum.IMPORT_FOO, message.defaultImportEnumExtension)
        assertEquals("424", message.defaultStringPieceExtension)
        assertEquals("425", message.defaultCordExtension)

        // Oneof extension
        assertTrue(message.presence.hasOneofBytesExtension)
        assertByteArrayEquals(toBytes("604"), message.oneofBytesExtension)
    }

    /**
     * Asserts that all extension fields in [message] are cleared to their default state.
     */
    fun assertExtensionsClear(message: TestAllExtensions) {
        // All optional extensions should not be present
        assertFalse(message.presence.hasOptionalInt32Extension)
        assertFalse(message.presence.hasOptionalInt64Extension)
        assertFalse(message.presence.hasOptionalUint32Extension)
        assertFalse(message.presence.hasOptionalUint64Extension)
        assertFalse(message.presence.hasOptionalSint32Extension)
        assertFalse(message.presence.hasOptionalSint64Extension)
        assertFalse(message.presence.hasOptionalFixed32Extension)
        assertFalse(message.presence.hasOptionalFixed64Extension)
        assertFalse(message.presence.hasOptionalSfixed32Extension)
        assertFalse(message.presence.hasOptionalSfixed64Extension)
        assertFalse(message.presence.hasOptionalFloatExtension)
        assertFalse(message.presence.hasOptionalDoubleExtension)
        assertFalse(message.presence.hasOptionalBoolExtension)
        assertFalse(message.presence.hasOptionalStringExtension)
        assertFalse(message.presence.hasOptionalBytesExtension)
        assertFalse(message.presence.hasOptionalgroupExtension)
        assertFalse(message.presence.hasOptionalNestedMessageExtension)
        assertFalse(message.presence.hasOptionalForeignMessageExtension)
        assertFalse(message.presence.hasOptionalImportMessageExtension)
        assertFalse(message.presence.hasOptionalNestedEnumExtension)
        assertFalse(message.presence.hasOptionalForeignEnumExtension)
        assertFalse(message.presence.hasOptionalImportEnumExtension)
        assertFalse(message.presence.hasOptionalStringPieceExtension)
        assertFalse(message.presence.hasOptionalCordExtension)

        // Nullable optional extensions should be null
        assertNull(message.optionalInt32Extension)
        assertNull(message.optionalInt64Extension)
        assertNull(message.optionalUint32Extension)
        assertNull(message.optionalUint64Extension)
        assertNull(message.optionalSint32Extension)
        assertNull(message.optionalSint64Extension)
        assertNull(message.optionalFixed32Extension)
        assertNull(message.optionalFixed64Extension)
        assertNull(message.optionalSfixed32Extension)
        assertNull(message.optionalSfixed64Extension)
        assertNull(message.optionalFloatExtension)
        assertNull(message.optionalDoubleExtension)
        assertNull(message.optionalBoolExtension)
        assertNull(message.optionalStringExtension)
        assertNull(message.optionalBytesExtension)
        assertNull(message.optionalNestedEnumExtension)
        assertNull(message.optionalForeignEnumExtension)
        assertNull(message.optionalImportEnumExtension)
        assertNull(message.optionalStringPieceExtension)
        assertNull(message.optionalCordExtension)

        // Repeated extension fields should be empty
        assertEquals(0, message.repeatedInt32Extension.size)
        assertEquals(0, message.repeatedInt64Extension.size)
        assertEquals(0, message.repeatedUint32Extension.size)
        assertEquals(0, message.repeatedUint64Extension.size)
        assertEquals(0, message.repeatedSint32Extension.size)
        assertEquals(0, message.repeatedSint64Extension.size)
        assertEquals(0, message.repeatedFixed32Extension.size)
        assertEquals(0, message.repeatedFixed64Extension.size)
        assertEquals(0, message.repeatedSfixed32Extension.size)
        assertEquals(0, message.repeatedSfixed64Extension.size)
        assertEquals(0, message.repeatedFloatExtension.size)
        assertEquals(0, message.repeatedDoubleExtension.size)
        assertEquals(0, message.repeatedBoolExtension.size)
        assertEquals(0, message.repeatedStringExtension.size)
        assertEquals(0, message.repeatedBytesExtension.size)
        assertEquals(0, message.repeatedgroupExtension.size)
        assertEquals(0, message.repeatedNestedMessageExtension.size)
        assertEquals(0, message.repeatedForeignMessageExtension.size)
        assertEquals(0, message.repeatedImportMessageExtension.size)
        assertEquals(0, message.repeatedLazyMessageExtension.size)
        assertEquals(0, message.repeatedNestedEnumExtension.size)
        assertEquals(0, message.repeatedForeignEnumExtension.size)
        assertEquals(0, message.repeatedImportEnumExtension.size)
        assertEquals(0, message.repeatedStringPieceExtension.size)
        assertEquals(0, message.repeatedCordExtension.size)

        // Default extensions: not present but have proto-defined defaults
        assertFalse(message.presence.hasDefaultInt32Extension)
        assertFalse(message.presence.hasDefaultInt64Extension)
        assertFalse(message.presence.hasDefaultUint32Extension)
        assertFalse(message.presence.hasDefaultUint64Extension)
        assertFalse(message.presence.hasDefaultSint32Extension)
        assertFalse(message.presence.hasDefaultSint64Extension)
        assertFalse(message.presence.hasDefaultFixed32Extension)
        assertFalse(message.presence.hasDefaultFixed64Extension)
        assertFalse(message.presence.hasDefaultSfixed32Extension)
        assertFalse(message.presence.hasDefaultSfixed64Extension)
        assertFalse(message.presence.hasDefaultFloatExtension)
        assertFalse(message.presence.hasDefaultDoubleExtension)
        assertFalse(message.presence.hasDefaultBoolExtension)
        assertFalse(message.presence.hasDefaultStringExtension)
        assertFalse(message.presence.hasDefaultBytesExtension)
        assertFalse(message.presence.hasDefaultNestedEnumExtension)
        assertFalse(message.presence.hasDefaultForeignEnumExtension)
        assertFalse(message.presence.hasDefaultImportEnumExtension)
        assertFalse(message.presence.hasDefaultStringPieceExtension)
        assertFalse(message.presence.hasDefaultCordExtension)

        // Note: Unlike regular fields, extension descriptors do not carry proto-defined defaults.
        // The getter returns the type-level zero value (0 for int, "" for string, etc.)
        // when the field is not set. Presence checks above already verify clear state.

        // Oneof extension not set
        assertFalse(message.presence.hasOneofBytesExtension)
        assertNull(message.oneofBytesExtension)
    }

    /**
     * Creates a [TestPackedExtensions] with all packed extension fields populated.
     */
    fun getPackedExtensionsSet(): TestPackedExtensions = TestPackedExtensions {
        packedInt32Extension = listOf(601, 701)
        packedInt64Extension = listOf(602L, 702L)
        packedUint32Extension = listOf(603u, 703u)
        packedUint64Extension = listOf(604uL, 704uL)
        packedSint32Extension = listOf(605, 705)
        packedSint64Extension = listOf(606L, 706L)
        packedFixed32Extension = listOf(607u, 707u)
        packedFixed64Extension = listOf(608uL, 708uL)
        packedSfixed32Extension = listOf(609, 709)
        packedSfixed64Extension = listOf(610L, 710L)
        packedFloatExtension = listOf(611f, 711f)
        packedDoubleExtension = listOf(612.0, 712.0)
        packedBoolExtension = listOf(true, false)
        packedEnumExtension = listOf(ForeignEnum.FOREIGN_BAR, ForeignEnum.FOREIGN_BAZ)
    }

    fun assertPackedExtensionsSet(message: TestPackedExtensions) {
        assertEquals(2, message.packedInt32Extension.size)
        assertEquals(2, message.packedInt64Extension.size)
        assertEquals(2, message.packedUint32Extension.size)
        assertEquals(2, message.packedUint64Extension.size)
        assertEquals(2, message.packedSint32Extension.size)
        assertEquals(2, message.packedSint64Extension.size)
        assertEquals(2, message.packedFixed32Extension.size)
        assertEquals(2, message.packedFixed64Extension.size)
        assertEquals(2, message.packedSfixed32Extension.size)
        assertEquals(2, message.packedSfixed64Extension.size)
        assertEquals(2, message.packedFloatExtension.size)
        assertEquals(2, message.packedDoubleExtension.size)
        assertEquals(2, message.packedBoolExtension.size)
        assertEquals(2, message.packedEnumExtension.size)

        assertEquals(601, message.packedInt32Extension[0])
        assertEquals(602L, message.packedInt64Extension[0])
        assertEquals(603u, message.packedUint32Extension[0])
        assertEquals(604uL, message.packedUint64Extension[0])
        assertEquals(605, message.packedSint32Extension[0])
        assertEquals(606L, message.packedSint64Extension[0])
        assertEquals(607u, message.packedFixed32Extension[0])
        assertEquals(608uL, message.packedFixed64Extension[0])
        assertEquals(609, message.packedSfixed32Extension[0])
        assertEquals(610L, message.packedSfixed64Extension[0])
        assertEquals(611f, message.packedFloatExtension[0])
        assertEquals(612.0, message.packedDoubleExtension[0])
        assertEquals(true, message.packedBoolExtension[0])
        assertEquals(ForeignEnum.FOREIGN_BAR, message.packedEnumExtension[0])

        assertEquals(701, message.packedInt32Extension[1])
        assertEquals(702L, message.packedInt64Extension[1])
        assertEquals(703u, message.packedUint32Extension[1])
        assertEquals(704uL, message.packedUint64Extension[1])
        assertEquals(705, message.packedSint32Extension[1])
        assertEquals(706L, message.packedSint64Extension[1])
        assertEquals(707u, message.packedFixed32Extension[1])
        assertEquals(708uL, message.packedFixed64Extension[1])
        assertEquals(709, message.packedSfixed32Extension[1])
        assertEquals(710L, message.packedSfixed64Extension[1])
        assertEquals(711f, message.packedFloatExtension[1])
        assertEquals(712.0, message.packedDoubleExtension[1])
        assertEquals(false, message.packedBoolExtension[1])
        assertEquals(ForeignEnum.FOREIGN_BAZ, message.packedEnumExtension[1])
    }

    /**
     * Returns an [ProtoExtensionRegistry] containing all TestAllExtensions extension descriptors.
     */
    fun getExtensionRegistry(): ProtoExtensionRegistry = ProtoExtensionRegistry {
        // Optional
        +TestAllExtensions.optionalInt32Extension
        +TestAllExtensions.optionalInt64Extension
        +TestAllExtensions.optionalUint32Extension
        +TestAllExtensions.optionalUint64Extension
        +TestAllExtensions.optionalSint32Extension
        +TestAllExtensions.optionalSint64Extension
        +TestAllExtensions.optionalFixed32Extension
        +TestAllExtensions.optionalFixed64Extension
        +TestAllExtensions.optionalSfixed32Extension
        +TestAllExtensions.optionalSfixed64Extension
        +TestAllExtensions.optionalFloatExtension
        +TestAllExtensions.optionalDoubleExtension
        +TestAllExtensions.optionalBoolExtension
        +TestAllExtensions.optionalStringExtension
        +TestAllExtensions.optionalBytesExtension
        +TestAllExtensions.optionalgroupExtension
        +TestAllExtensions.optionalNestedMessageExtension
        +TestAllExtensions.optionalForeignMessageExtension
        +TestAllExtensions.optionalImportMessageExtension
        +TestAllExtensions.optionalPublicImportMessageExtension
        +TestAllExtensions.optionalLazyMessageExtension
        +TestAllExtensions.optionalUnverifiedLazyMessageExtension
        +TestAllExtensions.optionalNestedEnumExtension
        +TestAllExtensions.optionalForeignEnumExtension
        +TestAllExtensions.optionalImportEnumExtension
        +TestAllExtensions.optionalStringPieceExtension
        +TestAllExtensions.optionalCordExtension
        // Repeated
        +TestAllExtensions.repeatedInt32Extension
        +TestAllExtensions.repeatedInt64Extension
        +TestAllExtensions.repeatedUint32Extension
        +TestAllExtensions.repeatedUint64Extension
        +TestAllExtensions.repeatedSint32Extension
        +TestAllExtensions.repeatedSint64Extension
        +TestAllExtensions.repeatedFixed32Extension
        +TestAllExtensions.repeatedFixed64Extension
        +TestAllExtensions.repeatedSfixed32Extension
        +TestAllExtensions.repeatedSfixed64Extension
        +TestAllExtensions.repeatedFloatExtension
        +TestAllExtensions.repeatedDoubleExtension
        +TestAllExtensions.repeatedBoolExtension
        +TestAllExtensions.repeatedStringExtension
        +TestAllExtensions.repeatedBytesExtension
        +TestAllExtensions.repeatedgroupExtension
        +TestAllExtensions.repeatedNestedMessageExtension
        +TestAllExtensions.repeatedForeignMessageExtension
        +TestAllExtensions.repeatedImportMessageExtension
        +TestAllExtensions.repeatedLazyMessageExtension
        +TestAllExtensions.repeatedNestedEnumExtension
        +TestAllExtensions.repeatedForeignEnumExtension
        +TestAllExtensions.repeatedImportEnumExtension
        +TestAllExtensions.repeatedStringPieceExtension
        +TestAllExtensions.repeatedCordExtension
        // Default
        +TestAllExtensions.defaultInt32Extension
        +TestAllExtensions.defaultInt64Extension
        +TestAllExtensions.defaultUint32Extension
        +TestAllExtensions.defaultUint64Extension
        +TestAllExtensions.defaultSint32Extension
        +TestAllExtensions.defaultSint64Extension
        +TestAllExtensions.defaultFixed32Extension
        +TestAllExtensions.defaultFixed64Extension
        +TestAllExtensions.defaultSfixed32Extension
        +TestAllExtensions.defaultSfixed64Extension
        +TestAllExtensions.defaultFloatExtension
        +TestAllExtensions.defaultDoubleExtension
        +TestAllExtensions.defaultBoolExtension
        +TestAllExtensions.defaultStringExtension
        +TestAllExtensions.defaultBytesExtension
        +TestAllExtensions.defaultNestedEnumExtension
        +TestAllExtensions.defaultForeignEnumExtension
        +TestAllExtensions.defaultImportEnumExtension
        +TestAllExtensions.defaultStringPieceExtension
        +TestAllExtensions.defaultCordExtension
        // Oneof
        +TestAllExtensions.oneofUint32Extension
        +TestAllExtensions.oneofNestedMessageExtension
        +TestAllExtensions.oneofStringExtension
        +TestAllExtensions.oneofBytesExtension
    }

    /**
     * Returns an [ProtoExtensionRegistry] containing all TestPackedExtensions extension descriptors.
     */
    fun getPackedExtensionRegistry(): ProtoExtensionRegistry = ProtoExtensionRegistry {
        +TestPackedExtensions.packedInt32Extension
        +TestPackedExtensions.packedInt64Extension
        +TestPackedExtensions.packedUint32Extension
        +TestPackedExtensions.packedUint64Extension
        +TestPackedExtensions.packedSint32Extension
        +TestPackedExtensions.packedSint64Extension
        +TestPackedExtensions.packedFixed32Extension
        +TestPackedExtensions.packedFixed64Extension
        +TestPackedExtensions.packedSfixed32Extension
        +TestPackedExtensions.packedSfixed64Extension
        +TestPackedExtensions.packedFloatExtension
        +TestPackedExtensions.packedDoubleExtension
        +TestPackedExtensions.packedBoolExtension
        +TestPackedExtensions.packedEnumExtension
    }
}

fun assertByteArrayEquals(expected: ByteArray?, actual: ByteArray?) {
    assertTrue(expected.contentEquals(actual), "Expected ${expected?.contentToString()} but got ${actual?.contentToString()}")
}
