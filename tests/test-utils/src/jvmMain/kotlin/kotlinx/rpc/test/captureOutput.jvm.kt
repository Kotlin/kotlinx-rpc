/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import java.io.ByteArrayOutputStream
import java.io.PrintStream

actual suspend fun captureStdErr(block: suspend () -> Unit): String {
    val orig = System.err
    val baos = ByteArrayOutputStream()
    System.setErr(PrintStream(baos))
    try {
        block()
        return baos.toString()
    } finally {
        System.setErr(orig)
    }
}

actual suspend fun captureStdOut(block: suspend () -> Unit): String {
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
