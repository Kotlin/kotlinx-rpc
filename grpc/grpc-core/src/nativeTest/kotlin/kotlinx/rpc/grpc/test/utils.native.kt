/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.test

import platform.posix.setenv

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import platform.posix.STDERR_FILENO
import platform.posix.STDOUT_FILENO
import platform.posix.close
import platform.posix.dup
import platform.posix.dup2
import platform.posix.fflush
import platform.posix.pipe
import platform.posix.read
import platform.posix.FILE
import platform.posix.stderr
import platform.posix.stdout
import kotlinx.rpc.grpc.internal.cinterop.grpc_tracer_set_enabled
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi
import platform.posix.unsetenv

actual val runtime: Runtime
    get() = Runtime.NATIVE

fun setNativeEnv(key: String, value: String) {
    setenv(key, value, 1)
}

fun clearNativeEnv(key: String) {
    unsetenv(key)
}

@OptIn(UnsafeNumber::class)
private suspend fun captureFileDescriptor(
    fileno: Int,
    stream: CPointer<FILE>,
    block: suspend () -> Unit,
): String = coroutineScope {
    memScoped {
        val pipeFd = allocArray<IntVar>(2)
        check(pipe(pipeFd) == 0) { "pipe failed" }

        val saved = dup(fileno)
        check(dup2(pipeFd[1], fileno) != -1) { "dup2 failed" }
        close(pipeFd[1])

        val outputBuf = StringBuilder()
        val readJob = launch(Dispatchers.IO) {
            val buf = ByteArray(4096)
            var r: Long
            do {
                r = read(pipeFd[0], buf.refTo(0), buf.size.convert()).convert()
                if (r > 0) outputBuf.append(buf.decodeToString(0, r.convert()))
            } while (r > 0)
            close(pipeFd[0])
        }

        try {
            block()
        } finally {
            fflush(stream)
            dup2(saved, fileno)
            close(saved)
        }

        readJob.join()
        outputBuf.toString()
    }
}

actual suspend fun captureStdErr(block: suspend () -> Unit): String =
    captureFileDescriptor(STDERR_FILENO, stderr!!, block)

actual suspend fun captureStdOut(block: suspend () -> Unit): String =
    captureFileDescriptor(STDOUT_FILENO, stdout!!, block)

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
