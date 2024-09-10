/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.utils.InternalRPCApi

private const val HEX_RADIX = 16

@InternalRPCApi
public fun Any.objectId(vararg tags: String): String {
    val tagsString = tags.takeIf { it.isNotEmpty() }?.joinToString { "[$it]" } ?: ""
    return "${this::class.simpleName}$tagsString[${hashCode().toString(HEX_RADIX)}]"
}
