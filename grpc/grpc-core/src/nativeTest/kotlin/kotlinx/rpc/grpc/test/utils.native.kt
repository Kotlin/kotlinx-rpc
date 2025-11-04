/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.test

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
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
import platform.posix.close
import platform.posix.dup
import platform.posix.dup2
import platform.posix.fflush
import platform.posix.fprintf
import platform.posix.pipe
import platform.posix.read
import platform.posix.stderr

actual val runtime: Runtime
    get() = Runtime.NATIVE

actual fun setNativeEnv(key: String, value: String) {
    platform.posix.setenv(key, value, 1)
}

actual fun clearNativeEnv(key: String) {
    platform.posix.unsetenv(key)
}

actual suspend fun captureStdErr(block: suspend () -> Unit): String = coroutineScope {
    memScoped {
        val pipeErr = allocArray<IntVar>(2)
        check(pipe(pipeErr) == 0) { "pipe stderr failed" }

        val savedStderr = dup(STDERR_FILENO)

        // redirect stderr write end
        check(dup2(pipeErr[1], STDERR_FILENO) != -1) { "dup2 stderr failed" }
        close(pipeErr[1])

        val outputBuf = StringBuilder()
        val readJob = launch(Dispatchers.IO) {
            val buf = ByteArray(4096)
            var r: Long
            do {
                r = read(pipeErr[0], buf.refTo(0), buf.size.convert())
                if (r > 0) outputBuf.append(buf.decodeToString(0, r.convert()))
            } while (r > 0)
            close(pipeErr[0])
        }

        try {
            block()
        } finally {
            fflush(stderr)
            // restore stderr
            dup2(savedStderr, STDERR_FILENO)
            close(savedStderr)
        }

        // wait reading to finish
        readJob.join()
        outputBuf.toString()
    }
}

