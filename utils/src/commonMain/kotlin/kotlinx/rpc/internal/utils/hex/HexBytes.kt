/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.hex

import kotlinx.rpc.internal.utils.InternalRpcApi

@OptIn(ExperimentalStdlibApi::class)
@InternalRpcApi
public fun String.rpcInternalHexToReadableBinary(): String {
    return hexToByteArray().joinToString("") { byte ->
        byte.toInt().toChar().display()
    }
}

private fun Char.display(): String {
    // visible symbols range
    // https://www.asciitable.com/
    return if (code !in 32..126) "?" else toString()
}
