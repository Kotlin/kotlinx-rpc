/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import java.io.ByteArrayOutputStream
import java.io.PrintStream

actual suspend fun captureStdErr(block: suspend () -> Unit): String {
    val orig = System.out
    val baos = ByteArrayOutputStream()
    System.setOut(PrintStream(baos))
    try {
        block()
        return baos.toString()
    } finally {
        System.setOut(orig)
    }
}

actual fun printErrLn(message: Any?) {
    System.err.println(message)
}

actual fun setNativeEnv(key: String, value: String) {
    // Nothing to do on JVM
}

actual fun clearNativeEnv(key: String) {
    // Nothing to do on JVM
}

actual val runtime: Runtime
    get() = Runtime.JVM
