/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TestBadIdentifiers.java
//
// The original Java test verifies that proto2 code generation doesn't cause compile errors
// when protobuf names conflict with Java reserved identifiers (like @Deprecated, @Override).
// It uses TestBadIdentifiersProto which comes from test_bad_identifiers.proto.
//
// This proto file is NOT part of the standard unittest proto set (it lives in the Java test infrastructure).
// Instead, we test similar scenarios using the evil_names proto files from the Kotlin test suite,
// which exercise Kotlin keyword conflicts.
//
// Tests NOT copied:
// - testCompilation: Tests Java-specific Deprecated/Override message names (test_bad_identifiers.proto not available)
// - testGetDescriptor: Java Descriptor API
// - testConflictingFieldNames: Requires TestConflictingFieldNames from test_bad_identifiers.proto + Extensions API
// - testNumberFields: Requires TestLeadingNumberFields from test_bad_identifiers.proto
//
// Also NOT copied (from TestBadIdentifiersLite.java):
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/TestBadIdentifiersLite.java
// - All tests: Duplicate of TestBadIdentifiers for lite runtime, same proto dependency

package proto2_unittest

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TestBadIdentifiers {

    @Test
    fun testBadFieldNamesCompilation() {
        // unittest.proto contains a BadFieldNames message with a field named "for".
        // If this compiles, the generator correctly escapes Kotlin keywords.
        val msg = BadFieldNames {
            `for` = 42
            this.OptionalInt32 = 1
        }
        assertNotNull(msg)
        assertEquals(42, msg.`for`)
        assertEquals(1, msg.OptionalInt32)
    }
}
