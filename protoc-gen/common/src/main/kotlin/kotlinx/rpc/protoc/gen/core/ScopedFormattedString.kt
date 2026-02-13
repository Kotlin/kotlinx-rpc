/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import kotlinx.rpc.protoc.gen.core.model.FqName

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

/**
 * ## No references
 * Before:
 * ```kotlin
 * "return 0"
 * ```
 * After:
 * ```kotlin
 * "return 0".scoped()
 * ```
 * ## Simple reference
 * Before:
 * ```kotlin
 * "val name: Int"
 * ```
 * After:
 * ```kotlin
 * "val name: %T".scoped(FqName.Implicits.Int)
 * ```
 * ## Simple multiple references
 * Before:
 * ```kotlin
 * "val a = MyClass(name: Int, string: String)"
 * ```
 * After:
 * ```kotlin
 * "val a = %T(name: %T, string: %T)".scoped(
 *     myClass.name,
 *     FqName.Implicits.Int,
 *     FqName.Implicits.String,
 * )
 * ```
 * ## Wrapping
 * Before:
 * ```kotlin
 * val inner = if (condition) {
 *     "val a: Int"
 * } else {
 *     "val b: String"
 * }
 * val result = "return $inner"
 * ```
 * After:
 * ```kotlin
 * val inner = if (condition) {
 *     "val a: %T".scoped(FqName.Implicits.Int)
 * } else {
 *     "val b: %T".scoped(FqName.Implicits.String)
 * }
 * val result = inner.wrapIn { inner -> "return $inner" }
 * ```
 * ## Wrapping multiple references
 * Before:
 * ```kotlin
 * val ctor = "MyClass"
 * val inner = if (condition) {
 *     "val a: Int"
 * } else {
 *     "val b: String"
 * }
 * val result = "return $ctor($inner)"
 * ```
 * After:
 * ```kotlin
 * val ctor = myClass.name.scoped()
 * val inner = if (condition) {
 *     "val a: %T".scoped(FqName.Implicits.Int)
 * } else {
 *     "val b: %T".scoped(FqName.Implicits.String)
 * }
 * // Mind the order in merge function!
 * // if ctor is before inner
 * // then in the interpolated string the 'ctor' parameter
 * // must be before the 'inner' parameter
 * val result = ctor.merge(inner) { ctor, inner -> "return $ctor($inner)" }
 * ```
 */
class ScopedFormattedString(val value: String, val args: List<Any>) {
    fun resolve(nameTable: ScopedFqNameTable): String = formatCode(this, nameTable)

    companion object {
        val empty = ScopedFormattedString("", emptyList())
    }

    override fun toString(): String {
        error("Scoped strings must not be stringified directly, use wrapIn() or merge() instead.")
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

/**
 * IMPORTANT TO USE THE ARGUMENT ONLY ONCE
 */
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
