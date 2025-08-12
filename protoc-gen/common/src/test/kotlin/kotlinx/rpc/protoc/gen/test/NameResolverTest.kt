/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.NameResolver
import org.junit.jupiter.api.assertThrows
import org.slf4j.helpers.NOPLogger
import kotlin.test.Test
import kotlin.test.assertEquals

class NameResolverTest {
    @Test
    fun testInAndOut() {
        val resolver = resolver()

        resolver.add(fq("test.package", "Test"))
        resolver.add(fq("test.package", "Test2"))
        resolver.add(fq("test.package.subpackage", "Test"))

        assertThrows<IllegalStateException> {
            resolver.add(fq("test.package", "Test"))
        }
        assertThrows<IllegalStateException> {
            resolver.add(fq("test.package", "Test2"))
        }
        assertThrows<IllegalStateException> {
            resolver.add(fq("test.package.subpackage", "Test"))
        }

        assertEquals(
            expected = fq("test.package", "Test"),
            actual = resolver.resolve("test.package.Test"),
        )

        assertEquals(
            expected = fq("test.package", "Test2"),
            actual = resolver.resolve("test.package.Test2"),
        )

        assertEquals(
            expected = fq("test.package.subpackage", "Test"),
            actual = resolver.resolve("test.package.subpackage.Test"),
        )

        assertEquals(
            expected = null,
            actual = resolver.resolveOrNull("test.package.subpackage.Test2"),
        )

        assertEquals(
            expected = null,
            actual = resolver.resolveOrNull("test.package.Test3"),
        )

        assertEquals(
            expected = null,
            actual = resolver.resolveOrNull("test.package.not.found.Test"),
        )

        assertEquals(
            expected = null,
            actual = resolver.resolveOrNull("Test"),
        )
    }

    @Test
    fun testEmptyPackages() {
        val resolver = resolver()
        resolver.add(fq("", "Test"))
        resolver.add(fq("", "Test2"))

        assertThrows<IllegalStateException> {
            resolver.add(fq("", "Test2"))
        }

        assertEquals(
            expected = fq("", "Test"),
            actual = resolver.resolveOrNull("Test"),
        )

        assertEquals(
            expected = fq("", "Test2"),
            actual = resolver.resolveOrNull("Test2"),
        )

        assertEquals(
            expected = null,
            actual = resolver.resolveOrNull("Test3"),
        )
    }

    @Test
    fun testScoped() {
        val resolver = resolver()

        resolver.add(fq("test.package", "Test"))
        resolver.add(fq("test.package", "Test2"))
        resolver.add(fq("test.package.subpackage", "Test"))

        val scoped = resolver.withScope(FqName.Package.fromString("test.package"))

        assertEquals(
            expected = fq("test.package", "Test"),
            actual = scoped.resolveOrNull("Test"),
        )

        assertEquals(
            expected = fq("test.package", "Test"),
            actual = scoped.resolveOrNull("test.package.Test"),
        )

        assertEquals(
            expected = fq("test.package", "Test2"),
            actual = scoped.resolveOrNull("Test2"),
        )

        assertEquals(
            expected = fq("test.package", "Test2"),
            actual = scoped.resolveOrNull("test.package.Test2"),
        )

        assertEquals(
            expected = fq("test.package.subpackage", "Test"),
            actual = scoped.resolveOrNull("test.package.subpackage.Test"),
        )

        assertEquals(
            expected = null,
            actual = scoped.resolveOrNull("subpackage.Test"),
        )

        val subScoped = resolver.withScope(FqName.Package.fromString("test.package.subpackage"))

        assertEquals(
            expected = fq("test.package", "Test"),
            actual = subScoped.resolveOrNull("test.package.Test"),
        )

        assertEquals(
            expected = null,
            actual = subScoped.resolveOrNull("Test2"),
        )

        assertEquals(
            expected = fq("test.package", "Test2"),
            actual = subScoped.resolveOrNull("test.package.Test2"),
        )

        assertEquals(
            expected = fq("test.package.subpackage", "Test"),
            actual = subScoped.resolveOrNull("Test"),
        )

        assertEquals(
            expected = fq("test.package.subpackage", "Test"),
            actual = subScoped.resolveOrNull("test.package.subpackage.Test"),
        )

        val preScoped = resolver.withScope(FqName.Package.fromString("test"))

        assertEquals(
            expected = null,
            actual = preScoped.resolveOrNull("Test"),
        )

        assertEquals(
            expected = fq("test.package", "Test"),
            actual = preScoped.resolveOrNull("test.package.Test"),
        )

        assertEquals(
            expected = null,
            actual = preScoped.resolveOrNull("Test2"),
        )

        assertEquals(
            expected = fq("test.package", "Test2"),
            actual = preScoped.resolveOrNull("test.package.Test2"),
        )

        assertEquals(
            expected = fq("test.package.subpackage", "Test"),
            actual = preScoped.resolveOrNull("test.package.subpackage.Test"),
        )
    }

    @Test
    fun testSubclasses() {
        val resolver = resolver()

        assertThrows<IllegalStateException> {
            resolver.add(fq("", "A.B"))
        }

        resolver.add(fq("", "A"))
        resolver.add(fq("", "A.B"))
        resolver.add(fq("", "A.B.C"))
        resolver.add(fq("", "A.B.C.D"))

        assertEquals(
            expected = fq("", "A"),
            actual = resolver.resolveOrNull("A"),
        )

        assertEquals(
            expected = fq("", "A.B"),
            actual = resolver.resolveOrNull("A.B"),
        )

        assertEquals(
            expected = fq("", "A.B.C"),
            actual = resolver.resolveOrNull("A.B.C"),
        )

        assertEquals(
            expected = fq("", "A.B.C.D"),
            actual = resolver.resolveOrNull("A.B.C.D"),
        )

        resolver.add(fq("", "A.B2"))
        resolver.add(fq("", "A.B2.C"))
        resolver.add(fq("", "A.B2.F"))

        assertEquals(
            expected = fq("", "A.B2.C"),
            actual = resolver.resolveOrNull("A.B2.C"),
        )

        assertEquals(
            expected = fq("", "A.B2"),
            actual = resolver.resolveOrNull("A.B2"),
        )

        assertEquals(
            expected = null,
            actual = resolver.resolveOrNull("A.B.F"),
        )

        val inB = resolver.withScope(fq("", "A.B"))

        assertEquals(
            expected = fq("", "A.B"),
            actual = inB.resolveOrNull("B"),
        )

        assertEquals(
            expected = fq("", "A.B"),
            actual = inB.resolveOrNull("A.B"),
        )

        assertEquals(
            expected = fq("", "A.B.C"),
            actual = inB.resolveOrNull("C"),
        )

        assertEquals(
            expected = fq("", "A.B.C"),
            actual = inB.resolveOrNull("B.C"),
        )

        assertEquals(
            expected = fq("", "A.B.C"),
            actual = inB.resolveOrNull("A.B.C"),
        )

        assertEquals(
            expected = fq("", "A.B.C.D"),
            actual = inB.resolveOrNull("C.D"),
        )

        assertEquals(
            expected = fq("", "A.B2"),
            actual = inB.resolveOrNull("B2"),
        )

        assertEquals(
            expected = fq("", "A.B2.F"),
            actual = inB.resolveOrNull("B2.F"),
        )

        assertEquals(
            expected = null,
            actual = inB.resolveOrNull("F"),
        )

        val inC = resolver.withScope(fq("", "A.B.C"))

        assertEquals(
            expected = fq("", "A.B.C"),
            actual = inC.resolveOrNull("C"),
        )

        assertEquals(
            expected = fq("", "A.B.C"),
            actual = inC.resolveOrNull("B.C"),
        )

        assertEquals(
            expected = fq("", "A.B.C"),
            actual = inC.resolveOrNull("A.B.C"),
        )

        assertEquals(
            expected = fq("", "A.B.C.D"),
            actual = inC.resolveOrNull("D"),
        )

        assertEquals(
            expected = fq("", "A.B.C.D"),
            actual = inC.resolveOrNull("C.D"),
        )

        assertEquals(
            expected = fq("", "A.B.C.D"),
            actual = inC.resolveOrNull("B.C.D"),
        )

        assertEquals(
            expected = fq("", "A.B2.C"),
            actual = inC.resolveOrNull("B2.C"),
        )

        assertEquals(
            expected = fq("", "A.B2"),
            actual = inC.resolveOrNull("B2"),
        )

        assertEquals(
            expected = fq("", "A.B2"),
            actual = inC.resolveOrNull("A.B2"),
        )

        assertEquals(
            expected = fq("", "A.B2.F"),
            actual = inC.resolveOrNull("A.B2.F"),
        )

        assertEquals(
            expected = fq("", "A.B2.F"),
            actual = inC.resolveOrNull("B2.F"),
        )

        assertEquals(
            expected = null,
            actual = inC.resolveOrNull("F"),
        )

        resolver.add(fq("", "A.B2.C.C"))

        val inB2C = resolver.withScope(fq("", "A.B2.C"))

        assertEquals(
            expected = fq("", "A.B2.C.C"),
            actual = inB2C.resolveOrNull("C"),
        )

        assertEquals(
            expected = fq("", "A.B2.C"),
            actual = inB2C.resolveOrNull("B2.C"),
        )

        assertEquals(
            expected = null,
            actual = inB2C.resolveOrNull("C.C"),
        )
    }

    @Test
    fun mixed() {
        val resolver = resolver()

        resolver.add(fq("test.package", "Test"))
        resolver.add(fq("test.package", "Test.Test"))
        resolver.add(fq("test.package.subpackage", "Test"))

        assertEquals(
            expected = fq("test.package", "Test"),
            actual = resolver.resolveOrNull("test.package.Test"),
        )

        assertEquals(
            expected = fq("test.package", "Test.Test"),
            actual = resolver.resolveOrNull("test.package.Test.Test"),
        )

        assertEquals(
            expected = fq("test.package.subpackage", "Test"),
            actual = resolver.resolveOrNull("test.package.subpackage.Test"),
        )

        val inTest = resolver.withScope(fq("test.package", "Test"))

        assertEquals(
            expected = fq("test.package", "Test.Test"),
            actual = inTest.resolveOrNull("Test"),
        )

        assertEquals(
            expected = fq("test.package", "Test"),
            actual = inTest.resolveOrNull("test.package.Test"),
        )

        val nextedInTest = inTest.withScope(fq("test.package", "Test.Test"))

        assertEquals(
            expected = fq("test.package", "Test.Test"),
            actual = nextedInTest.resolveOrNull("Test"),
        )

        val inPackage = resolver.withScope(fq("test.package", ""))

        assertEquals(
            expected = fq("test.package", "Test"),
            actual = inPackage.resolveOrNull("Test"),
        )

        assertEquals(
            expected = fq("test.package", "Test.Test"),
            actual = inPackage.resolveOrNull("Test.Test"),
        )
    }

    private fun resolver(): NameResolver = NameResolver.create(NOPLogger.NOP_LOGGER)
}
