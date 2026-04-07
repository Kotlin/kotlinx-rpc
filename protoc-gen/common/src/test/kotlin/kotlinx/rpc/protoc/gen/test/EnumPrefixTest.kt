/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import kotlinx.rpc.protoc.gen.core.camelToUpperSnakeCase
import kotlin.test.Test
import kotlin.test.assertEquals

class EnumPrefixTest {
    @Test
    fun simpleWord() {
        assertEquals("CARDINALITY", "Cardinality".camelToUpperSnakeCase())
    }

    @Test
    fun twoPascalCaseWords() {
        assertEquals("FIELD_KIND", "FieldKind".camelToUpperSnakeCase())
    }

    @Test
    fun acronym() {
        assertEquals("MY_HTTP_REQUEST", "MyHTTPRequest".camelToUpperSnakeCase())
    }

    @Test
    fun singleLetter() {
        assertEquals("A", "A".camelToUpperSnakeCase())
    }

    @Test
    fun alreadyUpperSnakeCase() {
        assertEquals("ALREADY_UPPER", "ALREADY_UPPER".camelToUpperSnakeCase())
    }

    @Test
    fun lowerCaseWithUnderscore() {
        assertEquals("MY_ENUM", "my_enum".camelToUpperSnakeCase())
    }

    @Test
    fun singleLowerWord() {
        assertEquals("STATUS", "status".camelToUpperSnakeCase())
    }

    @Test
    fun multipleWords() {
        assertEquals("SOME_LONG_NAME", "SomeLongName".camelToUpperSnakeCase())
    }

    @Test
    fun withDigits() {
        assertEquals("PROTO3_SYNTAX", "Proto3Syntax".camelToUpperSnakeCase())
    }
}
