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
    val testName = args[1]
    val debug = args.size > 2 && args[2] == "--debug"

    println("Running conformance test: $testName with --debug=$debug")

    val outputDir = Path(CONFORMANCE_OUTPUT_DIR).resolve("manual")

    val executable = getJavaClient(jarPath, "conformance", outputDir, testName, debug = debug)

    val (failingTestsFile, textFormatFailingTestsFile) = createConformanceTestFiles(outputDir)

    execConformanceTestRunner(
        outputDir = outputDir,
        failingTestsFile = failingTestsFile,
        textFormatFailingTestsFile = textFormatFailingTestsFile,
        executable = executable.absolutePathString(),
        testName = testName,
    ).onFailure {
        throw it
    }.onSuccess {
        if (it.exitCode != 0) {
            println("""
                |stdout: 
                |    ${it.stdout.joinToString("${System.lineSeparator()}|    ")}
                |stderr: 
                |    ${it.stderr.joinToString("${System.lineSeparator()}|    ")}
            """.trimMargin())

            error("Conformance test failed with non 0 exit code: ${it.exitCode}")
        }
    }
}
