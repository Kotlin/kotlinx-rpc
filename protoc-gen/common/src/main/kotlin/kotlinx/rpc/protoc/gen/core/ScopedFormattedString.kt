/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.fullName

class ScopedName(val name: String)

fun String.scoped(vararg args: Any): ScopedFormattedString {
    return ScopedFormattedString(this, args.toList())
}

fun FqName.scoped(): ScopedFormattedString {
    return ScopedFormattedString("%T", listOf(this))
}

fun FqName.scopedAnnotation(): ScopedFormattedString {
    return ScopedFormattedString("@%T", listOf(this))
}

class ScopedFormattedString(val value: String, val args: List<Any>) {
    fun resolve(nameTable: ScopedFqNameTable): String = formatCode(this, nameTable)

    companion object {
        val empty = ScopedFormattedString("", emptyList())
    }

    override fun toString(): String {
        error("Scoped strings must not be strigified directly, use wrapIn() or merge() instead.")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScopedFormattedString

        if (value != other.value) return false
        if (args != other.args) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + args.hashCode()
        return result
    }
}

fun ScopedFormattedString.wrapIn(block: (String) -> String): ScopedFormattedString {
    return ScopedFormattedString(block(value), args)
}

/**
 * IMPORTANT TO PRESERVE ORDER IN BLOCK INTERPOLATION AND USE EACH ARGUMENT ONLY ONCE
 */
fun ScopedFormattedString.merge(
    other: ScopedFormattedString,
    block: (String, String) -> String,
): ScopedFormattedString {
    return ScopedFormattedString(
        value = block(value, other.value),
        args = args + other.args,
    )
}

/**
 * IMPORTANT TO PRESERVE ORDER IN BLOCK INTERPOLATION AND USE EACH ARGUMENT ONLY ONCE
 */
fun ScopedFormattedString.merge(
    other: ScopedFormattedString,
    another: ScopedFormattedString,
    block: (String, String, String) -> String,
): ScopedFormattedString {
    return ScopedFormattedString(
        value = block(value, other.value, another.value),
        args = args + other.args + another.args,
    )
}

/**
 * IMPORTANT TO PRESERVE ORDER IN BLOCK INTERPOLATION AND USE EACH ARGUMENT ONLY ONCE
 */
fun ScopedFormattedString.merge(
    other: ScopedFormattedString,
    another: ScopedFormattedString,
    anotherOne: ScopedFormattedString,
    block: (String, String, String, String) -> String,
): ScopedFormattedString {
    return ScopedFormattedString(
        value = block(value, other.value, another.value, anotherOne.value),
        args = args + other.args + another.args + anotherOne.args,
    )
}

fun List<ScopedFormattedString>.joinToScopedString(separator: String): ScopedFormattedString {
    return ScopedFormattedString(
        value = joinToString(separator) { it.value },
        args = flatMap { it.args },
    )
}

private fun formatCode(string: ScopedFormattedString, nameTable: ScopedFqNameTable): String = buildString {
    val args = string.args
    val string = string.value

    var argIndex = 0
    var i = 0

    while (i < string.length) {
        if (string[i] != '%' || i + 1 >= string.length) {
            append(string[i])
            i++
            continue
        }

        when (string[i + 1]) {
            'T' -> {
                val arg = args.getOrNull(argIndex++)
                    ?: error("Missing argument at position ${argIndex - 1} for %T")

                if (arg !is FqName) {
                    error("Expected FqName for %T at position ${argIndex - 1}, but got ${arg::class.simpleName}")
                }

                append(nameTable.resolve(arg))

                i += 2
            }

            'N' -> {
                val arg = args.getOrNull(argIndex++)
                    ?: error("Missing argument at position ${argIndex - 1} for %N")

                if (arg !is ScopedName) {
                    error("Expected ScopedName for %N at position ${argIndex - 1}, but got ${arg::class.simpleName}")
                }

                append(arg.name)
                i += 2
            }

            '%' -> {
                append('%')
                i += 2
            }

            else -> {
                append(string[i])
                i++
            }
        }
    }
}
