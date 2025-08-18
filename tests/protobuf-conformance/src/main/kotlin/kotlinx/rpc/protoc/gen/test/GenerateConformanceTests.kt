/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import CONFORMANCE_OUTPUT_DIR
import kotlinx.rpc.protoc.gen.test.runner.createConformanceTestFiles
import kotlinx.rpc.protoc.gen.test.runner.execConformanceTestRunner
import kotlinx.rpc.protoc.gen.test.runner.getJavaClient
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

fun main(args: Array<String>) {
    val jarPath = args[0]

    val executable = getJavaClient(jarPath, "mock")

    val outputDir = Path(CONFORMANCE_OUTPUT_DIR).resolve("mock")

    val (failingTestsFile, textFormatFailingTestsFile) = createConformanceTestFiles(outputDir)

    execConformanceTestRunner(
        outputDir = outputDir,
        failingTestsFile = failingTestsFile,
        textFormatFailingTestsFile = textFormatFailingTestsFile,
        executable = executable.absolutePathString(),
    ).onFailure {
        throw it
    }.onSuccess {
        if (it.exitCode != 1 && it.exitCode != 0) {
            println("""
                |stdout: 
                |    ${it.stdout.joinToString("${System.lineSeparator()}|    ")}
                |stderr: 
                |    ${it.stderr.joinToString("${System.lineSeparator()}|    ")}
            """.trimMargin())

            error("Mock tests failed with non 1 exit code: ${it.exitCode}")
        }
    }
}
