/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.test

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

actual val runtime: Runtime
    get() = Runtime.NATIVE

actual val grpcTestServerPort: Int by lazy {
    checkNotNull(getenv("GRPC_TEST_SERVER_PORT")?.toKString()) { "GRPC_TEST_SERVER_PORT isn't set" }.toInt()
}

actual suspend fun captureGrpcLogs(
    jvmLogLevel: String,
    jvmLoggers: List<String>,
    nativeVerbosity: String,
    nativeTracers: List<String>,
    block: suspend () -> Unit,
): String = TODO("C-core log capture is not available on iOS")
