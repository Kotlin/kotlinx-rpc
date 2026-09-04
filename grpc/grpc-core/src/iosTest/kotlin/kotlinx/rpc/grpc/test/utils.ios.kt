/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

actual val runtime: Runtime
    get() = TODO("Implement iOS gRPC test utilities")

actual suspend fun captureStdErr(block: suspend () -> Unit): String =
    TODO("Implement iOS gRPC test utilities")

actual suspend fun captureStdOut(block: suspend () -> Unit): String =
    TODO("Implement iOS gRPC test utilities")

actual suspend fun captureGrpcLogs(
    jvmLogLevel: String,
    jvmLoggers: List<String>,
    nativeVerbosity: String,
    nativeTracers: List<String>,
    block: suspend () -> Unit,
): String = TODO("Implement iOS gRPC test utilities")
