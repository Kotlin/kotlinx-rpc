/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.input.stream

import kotlinx.io.Source
import kotlinx.io.asInputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

@ExperimentalRpcApi
public actual typealias InputStream = java.io.InputStream

@ExperimentalRpcApi
@Suppress("NOTHING_TO_INLINE")
public actual inline fun Source.asInputStream(): InputStream = asInputStream()

@ExperimentalRpcApi
@Suppress("NOTHING_TO_INLINE")
public actual inline fun InputStream.asSource(): Source {
    return asSource().buffered()
}
