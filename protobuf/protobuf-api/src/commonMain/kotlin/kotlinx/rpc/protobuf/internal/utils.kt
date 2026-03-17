/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.toHexString
import kotlinx.rpc.internal.utils.InternalRpcApi

@OptIn(ExperimentalStdlibApi::class)
private val HEX_FORMAT = HexFormat {
    bytes {
        byteSeparator = " "
    }
}

/**
 * Converts a ByteString to a string representation like:
 * ```
 * 7b 01 02
 * ```
 *
 * This is used by generated internal proto message class to implement the `toString` method for
 * messages with a `bytes` field.
 */
@InternalRpcApi
@OptIn(ExperimentalStdlibApi::class)
public fun ByteString.protoToString(): String {
    return toHexString(HEX_FORMAT)
}