/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.util

import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

// See https://github.com/diffplug/spotless/blob/0fd20bb80c6c426d20e0a3157c3c2b89317032da/lib/src/main/java/com/diffplug/spotless/ProcessRunner.java
internal class ProcessRunner : Closeable {
    private val threadStdOut = Executors.newSingleThreadExecutor()
    private val threadStdErr = Executors.newSingleThreadExecutor()
    private val bufStdOut = ByteArrayOutputStream()
    private val bufStdErr = ByteArrayOutputStream()

    fun shell(
        name: String,
        workingDir: File,
        args: List<Any>,
    ): Result {
        val processBuilder = ProcessBuilder(args.map(Any::toString))
        processBuilder.directory(workingDir)
        val process = processBuilder.start()
        val out = threadStdOut.submit<ByteArray> { drain(process.inputStream, bufStdOut) }
        val err = threadStdErr.submit<ByteArray> { drain(process.errorStream, bufStdErr) }
        val exitCode = process.waitFor()
        return Result(name, args, exitCode, out.get(), err.get())
    }

    private fun drain(
        input: InputStream,
        output: ByteArrayOutputStream,
    ): ByteArray {
        output.reset()
        input.copyTo(output)
        return output.toByteArray()
    }

    override fun close() {
        threadStdOut.shutdown()
        threadStdErr.shutdown()
    }

    class Result(
        val name: String,
        val args: List<Any>,
        val exitCode: Int,
        val stdOut: ByteArray,
        val stdErr: ByteArray,
    ) {
        fun formattedOutput() = buildString {
            appendLine("Process $name finished:")
            appendLine("  - Buf Arguments: $args")
            appendLine("  - Exit code: $exitCode")
            val perStream = { name: String, content: ByteArray ->
                val string = content.toString(StandardCharsets.UTF_8)
                if (string.isEmpty()) {
                    appendLine("  - $name: No output")
                } else {
                    appendLine("  - $name:")
                    val lines = string.replace("\r", "").lines()
                    lines.forEach { appendLine("      $it") }
                }
            }
            perStream("Stdout", stdOut)
            perStream("Stderr", stdErr)
            return toString()
        }
    }
}
