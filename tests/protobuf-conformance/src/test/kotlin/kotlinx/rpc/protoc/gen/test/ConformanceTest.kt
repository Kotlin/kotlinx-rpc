/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import CONFORMANCE_OUTPUT_DIR
import kotlinx.rpc.protoc.gen.test.runner.createConformanceTestFiles
import kotlinx.rpc.protoc.gen.test.runner.execConformanceTestRunner
import kotlinx.rpc.protoc.gen.test.runner.getJavaClient
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import java.util.stream.Stream
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.readLines

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ConformanceTest {
    @TestFactory
    fun conformance(): Stream<DynamicTest> {
        val jarPath = System.getenv("MOCK_CLIENT_JAR")
            ?: error("Expected environment variable 'MOCK_CLIENT_JAR' to be set")

        val outputDir = Path(CONFORMANCE_OUTPUT_DIR).resolve("conformance")

        val executable = getJavaClient(jarPath, "conformance", outputDir)

        val (failingTestsFile, textFormatFailingTestsFile) = createConformanceTestFiles(outputDir)

        val result = execConformanceTestRunner(
            outputDir = outputDir,
            failingTestsFile = failingTestsFile,
            textFormatFailingTestsFile = textFormatFailingTestsFile,
            executable = executable.absolutePathString(),
        )

        result.fold(
            onSuccess = { run ->
                if (run.exitCode != 1 && run.exitCode != 0) {
                    fail(
                        """
                            |Conformance tests failed with non 1 exit code: ${run.exitCode}
                            |
                            |stdout: 
                            |    ${run.stdout.joinToString("${System.lineSeparator()}|    ")}
                            |stderr: 
                            |    ${run.stderr.joinToString("${System.lineSeparator()}|    ")}
                        """.trimMargin()
                    )
                }
            },
            onFailure = {
                fail(it)
            }
        )

        val mockDir = Path(CONFORMANCE_OUTPUT_DIR).resolve("mock")
        val (baselineFile, _) = createConformanceTestFiles(mockDir, createBlank = false)

        val baseline = baselineFile
            .readLines()
            .map { it.substringBefore('#').trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        val fails = failingTestsFile.readLines().associate {
            it.substringBefore('#').trim() to it.substringAfter('#').trim()
        }

        val passed = baseline - fails.keys

        println(
            """
                
                === Conformance Test Results ===
                Total baseline tests:   ${baseline.size}
                [+] Passed tests:       ${passed.size}
                [-] Failed tests:       ${fails.size}
            """.trimIndent()
        )

        return (fails.entries.map { (test, message) ->
            DynamicTest.dynamicTest(test.testDisplayName()) {
                fail("FAILED: '${test}' with: $message")
            }
        } + passed.map { test ->
            DynamicTest.dynamicTest(test.testDisplayName()) {
                println("PASSED: '${test}'")
            }
        }).stream()
    }

    private fun String.testDisplayName(): String {
        return replace(".", " ")
            .replace("[", "(")
            .replace("]", ")")
    }
}
