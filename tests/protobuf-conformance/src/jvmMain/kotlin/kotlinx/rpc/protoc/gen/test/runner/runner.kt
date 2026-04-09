/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test.runner

import CONFORMANCE_EXECUTABLE_PATH
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readLines
import kotlin.properties.Delegates

class RunResult(
    val exitCode: Int,
    val stdout: List<String>,
    val stderr: List<String>,
)

fun execConformanceTestRunner(
    outputDir: Path,
    failingTestsFile: Path?,
    textFormatFailingTestsFile: Path?,
    executable: String,
    testName: String? = null,
): Result<RunResult> {
    val stdoutStream = Files.createTempFile("stdout", ".log")
    val errorStream = Files.createTempFile("error", ".log")

    val testNameFilter = testName?.let { listOf("--test", it) } ?: emptyList()

    var process: Process by Delegates.notNull()
    return runCatching {
        process = ProcessBuilder(
            CONFORMANCE_EXECUTABLE_PATH,
            *testNameFilter.toTypedArray(),
            "--maximum_edition", "MAX",
            "--enforce_recommended",
            "--failure_list", failingTestsFile.toString(),
            "--text_format_failure_list", textFormatFailingTestsFile.toString(),
            "--output_dir",
            outputDir.toString(),
            executable,
        ).redirectError(errorStream.toFile())
            .redirectOutput(stdoutStream.toFile())
            .start()

        Runtime.getRuntime().addShutdownHook(Thread {
            process.destroy()
        })

        val exitCode = process.waitFor()

        RunResult(
            exitCode = exitCode,
            stdout = stdoutStream.readLines(),
            stderr = errorStream.readLines(),
        )
    }.onFailure {
        if (process.isAlive) {
            process.destroy()
        }
    }
}

fun createConformanceTestFiles(
    outputDir: Path,
    createBlank: Boolean = true,
): Pair<Path, Path> {
    val failingTestsFile = outputDir.resolve("failing_tests.txt").apply {
        createParentDirectories()
        if (createBlank) {
            deleteIfExists()
            createFile()
        }
    }

    val textFormatFailingTestsFile = outputDir.resolve("text_format_failing_tests.txt").apply {
        createParentDirectories()
        if (createBlank) {
            deleteIfExists()
            createFile()
        }
    }

    return failingTestsFile to textFormatFailingTestsFile
}
