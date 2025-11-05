/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

actual val runtime: Runtime
    get() = Runtime.JVM

actual fun setNativeEnv(key: String, value: String) {
    // Nothing to do on JVM
}

actual fun clearNativeEnv(key: String) {
    // Nothing to do on JVM
}

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

actual suspend fun captureGrpcLogs(
    jvmLogLevel: String,
    jvmLoggers: List<String>,
    nativeVerbosity: String,
    nativeTracers: List<String>,
    block: suspend () -> Unit
): String {
    val sb = StringBuilder()
    val handler = object : Handler() {
        override fun publish(record: LogRecord) {
            sb.append('[').append(record.loggerName).append("] ")
                .append(record.level).append(": ")
                .append(record.message).append('\n')
        }
        override fun flush() {}
        override fun close() {}
    }
    handler.level = Level.ALL

    val saved = mutableListOf<Pair<Logger, Level?>>()
    try {
        for (name in jvmLoggers) {
            val logger = Logger.getLogger(name)
            saved += logger to logger.level
            logger.level = Level.ALL
            logger.useParentHandlers = false
            logger.addHandler(handler)
        }
        block()
        return sb.toString()
    } finally {
        saved.forEach { (lg, lvl) ->
            lg.level = lvl
            lg.handlers.filterIsInstance<Handler>()
                .forEach { if (it === handler) lg.removeHandler(it) }
        }
    }
}
