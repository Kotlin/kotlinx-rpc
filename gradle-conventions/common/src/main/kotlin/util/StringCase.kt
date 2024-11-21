/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import java.util.*

fun String.capitalized(): String {
    if (isEmpty()) {
        return ""
    }
    val firstChar = get(0)
    return replaceFirst(firstChar, Character.toTitleCase(firstChar))
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "NOTHING_TO_INLINE")
inline fun String.lowercase(): String = (this as java.lang.String).toLowerCase(Locale.ROOT)
