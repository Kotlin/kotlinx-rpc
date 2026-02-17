/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import kotlinx.rpc.protoc.gen.core.model.asParentsAndSimpleName
import kotlinx.rpc.protoc.gen.core.model.fq
import kotlinx.rpc.protoc.gen.core.model.fullName
import kotlin.test.Test
import kotlin.test.assertEquals

class FqNameTest {
    @Test
    fun testAsParentsAndSimpleName() {
        assertEquals(listOf<String>() to "Test", "Test".asParentsAndSimpleName())
        assertEquals(listOf("hello") to "Test", "hello.Test".asParentsAndSimpleName())
        assertEquals(listOf("hello", "world") to "Test", "hello.world.Test".asParentsAndSimpleName())
    }

    @Test
    fun testFullName() {
        assertEquals("hello.world.Test", fq("hello.world", "Test").fullName())
        assertEquals("hello.world.Test.Test", fq("hello.world", "Test.Test").fullName())
        assertEquals("Test.Test", fq("", "Test.Test").fullName())
        assertEquals("Test", fq("", "Test").fullName())
        assertEquals("parent", fq("parent", "").fullName())
        assertEquals("parent.test", fq("parent.test", "").fullName())
    }
}
