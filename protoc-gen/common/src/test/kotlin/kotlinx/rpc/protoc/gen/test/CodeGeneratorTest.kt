/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import kotlinx.rpc.protoc.gen.core.CodeGenerator
import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.FqNameTable
import kotlinx.rpc.protoc.gen.core.Platform
import kotlinx.rpc.protoc.gen.core.file
import kotlinx.rpc.protoc.gen.core.merge
import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.fq
import kotlinx.rpc.protoc.gen.core.scoped
import kotlinx.rpc.protoc.gen.core.wrapIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CodeGeneratorTest {
    @Test
    fun testString() = codeGeneratorTest { table ->
        val (imports, generated) = generate {
            code("%T".scoped(FqName.Implicits.String))
        }

        assertEquals("String", generated)
        assertTrue { imports.isEmpty() }
    }

    @Test
    fun testStringShadow() = codeGeneratorTest { table ->
        table.register(fq("com.example", "String"))

        val (imports, generated) = generate {
            code("%T".scoped(FqName.Implicits.String))
        }

        assertEquals("kotlin.String", generated)
        assertTrue { imports.isEmpty() }
    }

    @Test
    fun testSimpleClass() = codeGeneratorTest { table ->
        val superName = fq("com.other", "Super")
        val myClassName = fq("com.example", "MyClass")

        table.register(superName)
        table.register(myClassName)
        table.register(FqName.Annotations.ExperimentalRpcApi)

        val (imports, generated) = generate {
            clazz(
                name = myClassName.simpleName,
                superTypes = listOf("%T".scoped(superName)),
                constructorArgs = listOf("val arg: %T".scoped(FqName.Implicits.String)),
                annotations = listOf(
                    "@%T(%T::class)".scoped(FqName.Implicits.OptIn, FqName.Annotations.ExperimentalRpcApi),
                ),
            ) {
                property(
                    name = "myProperty",
                    type = "%T".scoped(myClassName),
                )
            }
        }

        assertEquals(
            """
            @OptIn(ExperimentalRpcApi::class)
            class MyClass(val arg: String): Super {
                val myProperty: MyClass
            }
        """.trimIndent(), generated.trim()
        )

        assertEquals(
            expected = setOf(
                "kotlinx.rpc.internal.utils.ExperimentalRpcApi",
                "com.other.Super",
            ),
            actual = imports,
        )
    }

    @Test
    fun wrappingMerging() = codeGeneratorTest { table ->
        val scopedInt = FqName.Implicits.Int.scoped()
        val myClassName = fq("com.example", "Int")

        table.register(myClassName)

        val (imports, generated) = generate {
            clazz(name = "Int")

            function(
                name = "some",
                returnType = scopedInt,
            ) {
                code(myClassName.scoped().merge(scopedInt) { f, s -> "// not this $f, but this one $s" })
                code(scopedInt.wrapIn { "return $it.MAX_VALUE" })
            }
        }

        assertEquals(
            """
                class Int
    
                fun some(): kotlin.Int {
                    // not this Int, but this one kotlin.Int
                    return kotlin.Int.MAX_VALUE
                }
            """.trimIndent(), generated.trim()
        )

        assertEquals(emptySet(), imports)
    }

    private class Env(
        private val generator: CodeGenerator,
    ) {
        fun generate(body: CodeGenerator.() -> Unit): Pair<Set<String>, String> {
            return generator.apply(body).run { __imports to build() }
        }
    }

    private fun codeGeneratorTest(
        packageFqName: FqName.Package = fq("com.example", "") as FqName.Package,
        platform: Platform = Platform.Common,
        body: Env.(FqNameTable) -> Unit,
    ) {
        val nameTable = FqNameTable(platform)
        val generator = CodeGenerator(
            config = Config(
                explicitApiModeEnabled = false,
                generateComments = false,
                generateFileLevelComments = false,
                indentSize = 4,
                platform = platform,
            ),
            nameTable = nameTable.scoped(packageFqName, mutableSetOf()),
            indent = "",
        )

        Env(generator).body(nameTable)
    }

    private fun fileGeneratorTest(
        packageFqName: FqName.Package = fq("com.example", "") as FqName.Package,
        platform: Platform = Platform.Common,
        body: Env.(FqNameTable) -> Unit,
    ) {
        val nameTable = FqNameTable(platform)
        val generator = file(
            config = Config(
                explicitApiModeEnabled = false,
                generateComments = false,
                generateFileLevelComments = false,
                indentSize = 4,
                platform = platform,
            ),
            packageName = packageFqName,
            nameTable = nameTable,
            name = "Test.kt",
        ) { }

        Env(generator).body(nameTable)
    }
}
