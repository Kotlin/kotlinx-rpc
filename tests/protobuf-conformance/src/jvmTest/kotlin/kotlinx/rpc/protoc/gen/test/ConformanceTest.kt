/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
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
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
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

        return runConformanceWith(
            executable = executable.absolutePathString(),
            outputDirName = "conformance",
            knownFailuresResource = "/known_failures.txt",
        )
    }

    // This test must NEVER be silently skipped. If the native binary is unavailable
    // (e.g., Kotlin master builds where native targets are disabled), the build script
    // must explicitly exclude this test via Gradle test filters — see build.gradle.kts.
    @TestFactory
    fun nativeConformance(): Stream<DynamicTest> {
        val nativeBinary = System.getenv("NATIVE_CLIENT_BINARY")
            ?: error(
                "Expected environment variable 'NATIVE_CLIENT_BINARY' to be set. " +
                    "Native conformance tests are not available on: ${System.getProperty("os.name")} " +
                    "(${System.getProperty("os.arch")}). " +
                    "Supported platforms: macosArm64, macosX64, linuxX64, linuxArm64."
            )

        // The native binary takes args directly: <binary> conformance
        // conformance_test_runner spawns it as a subprocess, so we need a wrapper script
        // that passes the "conformance" argument
        val wrapper = Files.createTempFile("nativeClientRunner", ".sh")
        Files.setPosixFilePermissions(wrapper, PosixFilePermission.entries.toSet())
        wrapper.toFile().writeText(
            """
                #!/bin/bash
                exec $nativeBinary conformance
            """.trimIndent()
        )

        return runConformanceWith(
            executable = wrapper.absolutePathString(),
            outputDirName = "native-conformance",
            knownFailuresResource = "/native_known_failures.txt",
        )
    }

    private fun runConformanceWith(
        executable: String,
        outputDirName: String,
        knownFailuresResource: String,
    ): Stream<DynamicTest> {
        val outputDir = Path(CONFORMANCE_OUTPUT_DIR).resolve(outputDirName)

        val (failingTestsFile, textFormatFailingTestsFile) = createConformanceTestFiles(outputDir)

        val result = execConformanceTestRunner(
            outputDir = outputDir,
            failingTestsFile = failingTestsFile,
            textFormatFailingTestsFile = textFormatFailingTestsFile,
            executable = executable,
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

        val knownFailures = ConformanceTest::class.java
            .getResourceAsStream(knownFailuresResource)!!
            .bufferedReader()
            .readLines()
            .map { it.substringBefore('#').trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        // TODO: Remove once we support JSON encoding (KRPC-195)
        // Exclude any JSON-related tests
        fun includeTest(name: String): Boolean {
            val trimmed = name.substringBefore('#').trim()
            return !trimmed.contains(".Json") && trimmed !in knownFailures
        }

        val baseline = baselineFile
            .readLines()
            .map { it.substringBefore('#').trim() }
            .filter { it.isNotEmpty() }
            .filter { includeTest(it) }
            .toSet()

        val fails = failingTestsFile.readLines()
            .map { it to (it.substringAfter('#', missingDelimiterValue = "").trim()) }
            .map { (line, msg) -> line.substringBefore('#').trim() to msg }
            .filter { (name, _) -> includeTest(name) }
            .toMap()

        val passed = baseline - fails.keys

        println(
            """

                === Conformance Test Results ($outputDirName, filtered) ===
                Total baseline tests (filtered):   ${baseline.size}
                [+] Passed tests:                  ${passed.size}
                [-] Failed tests:                  ${fails.size}
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
