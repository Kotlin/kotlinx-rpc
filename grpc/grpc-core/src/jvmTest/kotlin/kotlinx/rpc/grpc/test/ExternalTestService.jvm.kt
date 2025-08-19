/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

private class JvmExternalServiceHandle(
    private val process: Process,
    private val stdoutThread: Thread,
    private val stderrThread: Thread,
) : ExternalServiceHandle {
    override fun close() {
        try {
            process.destroy()
            process.waitFor(5, TimeUnit.SECONDS)
            if (process.isAlive) {
                process.destroyForcibly()
                process.waitFor(5, TimeUnit.SECONDS)
            }
        } finally {
            if (stdoutThread.isAlive) stdoutThread.interrupt()
            if (stderrThread.isAlive) stderrThread.interrupt()
        }
    }
}

internal actual fun startExternalService(mainClassName: String, jarPath: String): ExternalServiceHandle {
    val javaCmd = System.getProperty("java.home")?.let { "$it/bin/java" } ?: "java"
    val command = listOf(javaCmd, "-cp", jarPath, mainClassName)

    val pb = ProcessBuilder(command)
        .redirectErrorStream(false)

    val process = try {
        pb.start()
    } catch (e: Exception) {
        throw IllegalStateException("Failed to start external service: $command", e)
    }

    val readySignal = CountDownLatch(1)
    val startedMsg = "Server started"
    val outputBuffer = StringBuilder()

    val stdoutThread = thread(name = "ext-svc-stdout", isDaemon = true) {
        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line!!
                outputBuffer.appendLine(l)
                // Forward to test logs
                println("[external-service][out] $l")
                if (l.contains(startedMsg)) {
                    readySignal.countDown()
                }
            }
        }
    }

    val stderrThread = thread(name = "ext-svc-stderr", isDaemon = true) {
        BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line!!
                outputBuffer.appendLine(l)
                System.err.println("[external-service][err] $l")
            }
        }
    }

    // Wait until the server prints the readiness message or the process exits
    val timeout = Duration.ofSeconds(20)
    val ready = readySignal.await(timeout.seconds, TimeUnit.SECONDS)

    if (!ready) {
        // If not ready, check if the process already exited
        val exited = !process.isAlive
        val exitCode = if (exited) process.exitValue() else null
        throw IllegalStateException(
            buildString {
                append("External service did not become ready within $timeout. ")
                if (exited) append("Process exited with code $exitCode. ")
                append("Command: $command\n")
                append("Output so far:\n")
                append(outputBuffer.toString())
            }
        )
    }

    return JvmExternalServiceHandle(process, stdoutThread, stderrThread)
}
