/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.input.stream

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public abstract class BufferInputStream(public val buffer: Buffer)

public fun bufferInputStream(buffer: Buffer): BufferInputStream = object : BufferInputStream(buffer) {}

@ExperimentalRpcApi
public actual typealias InputStream = BufferInputStream

@ExperimentalRpcApi
public actual fun Source.asInputStream(): InputStream {
    return bufferInputStream(this as Buffer)
}

@ExperimentalRpcApi
public actual fun InputStream.asSource(): Source {
    return buffer
}
