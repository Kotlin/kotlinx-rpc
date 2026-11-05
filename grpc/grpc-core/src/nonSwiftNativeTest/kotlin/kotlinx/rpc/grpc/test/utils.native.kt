/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.test

import platform.posix.setenv

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv
import kotlinx.rpc.grpc.internal.cinterop.grpc_tracer_set_enabled
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi
import platform.posix.unsetenv
import kotlinx.rpc.test.captureStdErr

actual val runtime: Runtime
    get() = Runtime.NATIVE

actual val grpcTestServerPort: Int by lazy {
    checkNotNull(getenv("GRPC_TEST_SERVER_PORT")?.toKString()) { "GRPC_TEST_SERVER_PORT isn't set" }.toInt()
}

fun setNativeEnv(key: String, value: String) {
    setenv(key, value, 1)
}

fun clearNativeEnv(key: String) {
    unsetenv(key)
}

actual suspend fun captureGrpcLogs(
    jvmLogLevel: String,
    jvmLoggers: List<String>,
    nativeVerbosity: String,
    nativeTracers: List<String>,
    block: suspend () -> Unit
): String {
    try {
        return captureStdErr {
            setNativeEnv("GRPC_VERBOSITY", nativeVerbosity)
            nativeTracers.forEach { tracer ->
                grpc_tracer_set_enabled(tracer, 1)
            }
            block()
        }
    } finally {
        clearNativeEnv("GRPC_VERBOSITY")
        nativeTracers.forEach { tracer ->
            // set tracer to disabled
            grpc_tracer_set_enabled(tracer, 0)
        }
    }
}
