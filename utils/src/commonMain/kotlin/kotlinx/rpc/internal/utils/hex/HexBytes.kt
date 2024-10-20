/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.hex

import kotlinx.rpc.internal.utils.InternalRPCApi

// Original HexExtensions.kt from stdlib are not available for Kotlin 1.8 and earlier versions

private const val LOWER_CASE_HEX_DIGITS = "0123456789abcdef"
private const val UPPER_CASE_HEX_DIGITS = "0123456789ABCDEF"

// case-insensitive parsing
private val HEX_DIGITS_TO_DECIMAL = IntArray(128) { -1 }.apply {
    LOWER_CASE_HEX_DIGITS.forEachIndexed { index, char -> this[char.code] = index }
    UPPER_CASE_HEX_DIGITS.forEachIndexed { index, char -> this[char.code] = index }
}

@InternalRPCApi
fun ByteArray.toHexStringInternal(lowercase: Boolean = true): String {
    val digits = if (lowercase) LOWER_CASE_HEX_DIGITS else UPPER_CASE_HEX_DIGITS

    return buildString(size * 2) {
        for (element in this@toHexStringInternal) {
            val byte = element.toInt() and 0xFF

            append(digits[byte shr 4])
            append(digits[byte and 0xF])
        }
    }
}

@InternalRPCApi
fun String.hexToByteArrayInternal(): ByteArray {
    val result = ByteArray(length / 2)

    var i = 0
    var byteIndex = 0
    while (i < length) {
        result[byteIndex++] = ((decimalFromHexDigitAt(i++) shl 4) or decimalFromHexDigitAt(i++)).toByte()
    }

    return result
}

private fun String.decimalFromHexDigitAt(index: Int): Int {
    val code = this[index].code
    if (code > 127 || HEX_DIGITS_TO_DECIMAL[code] < 0) {
        throw NumberFormatException("Expected a hexadecimal digit at index $index, but was ${this[index]}")
    }
    return HEX_DIGITS_TO_DECIMAL[code]
}

@InternalRPCApi
fun String.hexToReadableBinary(): String {
    return hexToByteArrayInternal().joinToString("") { byte ->
        byte.toInt().toChar().display()
    }
}

private fun Char.display(): String {
    // visible symbols range
    // https://www.asciitable.com/
    return if (code !in 32..126) "?" else toString()
}
