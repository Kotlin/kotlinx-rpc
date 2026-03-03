/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

/**
 * Kotlin language keywords that need special handling when used as identifiers in generated code.
 */
object KotlinKeywords {
    /**
     * Hard keywords that ALWAYS need backtick escaping when used as identifiers.
     * https://kotlinlang.org/docs/keyword-reference.html#hard-keywords
     */
    val HARD_KEYWORDS: Set<String> = setOf(
        "as",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "interface",
        "is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "when",
        "while",
    )

    /**
     * Escapes the name with backticks if it is a Kotlin hard keyword.
     */
    fun escapeIfKeyword(name: String): String {
        return if (name in HARD_KEYWORDS) "`$name`" else name
    }
}
