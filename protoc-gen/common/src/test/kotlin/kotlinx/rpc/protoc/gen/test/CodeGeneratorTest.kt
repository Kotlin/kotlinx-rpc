/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import kotlinx.rpc.protoc.gen.core.CodeGenerator
import kotlinx.rpc.protoc.gen.core.Comment
import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.FqNameTable
import kotlinx.rpc.protoc.gen.core.Platform
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
    fun testString() = codeGeneratorTest {
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
                this.property(
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

    @Test
    fun testProperty() = codeGeneratorTest {
        val (imports, generated) = generate {
            property(
                name = "value",
                type = FqName.Implicits.String.scoped(),
            ) {
                scope(prefix = "buildString".scoped()) {
                    code("append(\"hello\")".scoped())
                }
            }
        }

        assertEquals(
            """
            val value: String = buildString {
                    append("hello")
                }
            """.trimIndent(),
            generated.trim(),
        )
        assertTrue { imports.isEmpty() }
    }

    @Test
    fun testFunctionCall() = codeGeneratorTest {
        val (imports, generated) = generate {
            functionCall(
                function = "createValue".scoped(),
                namedArgs = listOf(
                    "first" to "1".scoped(),
                    "second" to "\"two\"".scoped(),
                ),
            )
        }

        assertEquals(
            """
            createValue(
                first = 1,
                second = "two",
            )
            """.trimIndent(),
            generated.trim(),
        )
        assertTrue { imports.isEmpty() }
    }

    @Test
    fun testBlockCommentFormatting() = codeGeneratorTest(generateComments = true) {
        val (_, generated) = generate {
            appendComment(Comment.leading("A simple comment"))
        }

        assertEquals(
            """
            /**
             * A simple comment
             */
            """.trimIndent(),
            generated.trim(),
        )
    }

    @Test
    fun testBlankLineBeforePropertyWithComment() = codeGeneratorTest(generateComments = true) {
        val (_, generated) = generate {
            property(
                name = "name",
                comment = Comment.leading("file name, relative to root of source tree"),
                type = FqName.Implicits.String.scoped().wrapIn { "$it?" },
                needsNewLineAfterDeclaration = false,
            )
            property(
                name = "`package`",
                comment = Comment.leading("e.g. \"foo\", \"foo.bar\", etc."),
                type = FqName.Implicits.String.scoped().wrapIn { "$it?" },
                needsNewLineAfterDeclaration = true,
            )
        }

        assertEquals(
            """
            /**
             * file name, relative to root of source tree
             */
            val name: String?

            /**
             * e.g. "foo", "foo.bar", etc.
             */
            val `package`: String?
            """.trimIndent(),
            generated.trim(),
        )
    }

    @Test
    fun testMultiLineBlockCommentFormatting() = codeGeneratorTest(generateComments = true) {
        val (_, generated) = generate {
            appendComment(
                Comment(
                    leadingDetached = listOf("Detached comment"),
                    leading = listOf("Leading comment"),
                    trailing = listOf("Trailing comment"),
                )
            )
        }

        assertEquals(
            """
            /**
             * Detached comment
             *
             * Leading comment
             *
             * Trailing comment
             */
            """.trimIndent(),
            generated.trim(),
        )
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
        generateComments: Boolean = false,
        body: Env.(FqNameTable) -> Unit,
    ) {
        val nameTable = FqNameTable(platform)
        val generator = CodeGenerator(
            config = Config(
                explicitApiModeEnabled = false,
                generateComments = generateComments,
                generateFileLevelComments = false,
                indentSize = 4,
                platform = platform,
                protoNamesOutput = null,
            ),
            nameTable = nameTable.scoped(packageFqName, mutableSetOf()),
            indent = "",
        )

        Env(generator).body(nameTable)
    }
}
