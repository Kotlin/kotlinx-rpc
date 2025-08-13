/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.input.stream

import kotlinx.io.Source
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

@ExperimentalRpcApi
public actual abstract class InputStream

@ExperimentalRpcApi
public actual fun Source.asInputStream(): InputStream {
    TODO("Not yet implemented")
}

@ExperimentalRpcApi
public actual fun InputStream.asSource(): Source {
    TODO("Not yet implemented")
}
