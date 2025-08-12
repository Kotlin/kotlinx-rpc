/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.base

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.TestInstance
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.test.assertEquals
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class GrpcBaseTest : BaseTest() {
    abstract val isKmp: Boolean

    protected fun runGrpcTest(test: GrpcTestEnv.() -> Unit) {
        runTest(GrpcTestEnv(), test)
    }

    inner class GrpcTestEnv : TestEnv() {
        fun BuildResult.protoTaskOutcome(name: String): TaskOutcome {
            return tasks.find { it.path == ":$name" }?.outcome
                ?: fail("Task ':$name' was not present in the build result")
        }

        fun BuildResult.assertProtoTaskNotExecuted(name: String) {
            val task = tasks.find { it.path == ":$name" }
            if (task != null) {
                fail("Task ':$name' was present in the build result, but it shouldn't have been executed")
            }
        }

        fun assertSourceCodeGenerated(sourceSet: String, vararg files: Path) {
            val dir = protoBuildDirGenerated.resolve(sourceSet).resolve(KOTLIN_MULTIPLATFORM_DIR)
            if (files.isNotEmpty()) {
                assert(dir.exists()) {
                    "Directory '$dir' with generated sources does not exist"
                }
            }

            files.forEach { file ->
                assert(dir.resolve(file).exists()) {
                    "File 'file://${file.absolutePathString()}' in '$dir' does not exist"
                }
            }
        }

        fun assertSourceCodeNotGenerated(sourceSet: String, vararg files: Path) {
            val dir = protoBuildDirGenerated.resolve(sourceSet).resolve(KOTLIN_MULTIPLATFORM_DIR)

            files.forEach { file ->
                assert(!dir.resolve(file).exists()) {
                    "File 'file://${file.absolutePathString()}' in '$dir' should not exist"
                }
            }
        }

        @OptIn(ExperimentalPathApi::class)
        private fun assertWorkspaceProtoFilesCopiedInternal(vararg files: Path, sourceSet: String, dir: String) {
            val protoSources = protoBuildDirSourceSets.resolve(sourceSet).resolve(dir)
            val included = files.map { file ->
                val resolved = protoSources.resolve(file)
                assert(resolved.exists()) {
                    "File 'file://${file.absolutePathString()}' in '$protoSources' does not exist"
                }
                resolved.relativeTo(protoSources).pathString
            }.toSet()

            protoSources.walk().forEach {
                val pathString = it.relativeTo(protoSources).pathString
                if (it.isRegularFile() && it.extension == "proto" && pathString !in included) {
                    fail("File 'file://${it.absolutePathString()}' in '$protoSources' is not expected")
                }
            }
        }

        fun assertWorkspaceProtoFilesCopied(sourceSet: String, vararg files: Path) {
            assertWorkspaceProtoFilesCopiedInternal(*files, sourceSet = sourceSet, dir = "proto")
        }

        fun assertWorkspaceImportProtoFilesCopied(sourceSet: String, vararg files: Path) {
            assertWorkspaceProtoFilesCopiedInternal(*files, sourceSet = sourceSet, dir = "import")
        }

        @OptIn(ExperimentalPathApi::class)
        fun cleanProtoBuildDir() {
            protoBuildDir.deleteRecursively()
        }

        fun assertBufGenYaml(sourceSet: String, @Language("Yaml") content: String) {
            val file = protoBuildDirSourceSets
                .resolve(sourceSet)
                .resolve("buf.gen.yaml")

            assert(file.exists()) {
                "File '${file.absolutePathString()}' does not exist"
            }

            val fileContent = file.readLines().joinToString("\n") {
                when {
                    it.contains("protoc-gen-kotlin-multiplatform") -> {
                        it.replace(localPluginExecRegex, "[protoc-gen-kotlin-multiplatform]")
                    }
                    it.contains("protoc-gen-grpc-kotlin-multiplatform") -> {
                        it.replace(localPluginExecRegex, "[protoc-gen-grpc-kotlin-multiplatform]")
                    }
                    else -> {
                        it
                    }
                }
            }

            assertEquals(content.trimIndent(), fileContent.trimIndent())
        }

        private val localPluginExecRegex = "\\[.*?]".toRegex()

        fun BuildResult.assertMainTaskExecuted(
            protoFiles: List<Path>,
            generatedFiles: List<Path>,
        ) {
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(bufGenerateMain))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processMainProtoFiles))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufYamlMain))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufGenYamlMain))

            assertProtoTaskNotExecuted(bufGenerateTest)
            assertProtoTaskNotExecuted(processTestProtoFiles)
            assertProtoTaskNotExecuted(processTestImportProtoFiles)
            assertProtoTaskNotExecuted(generateBufYamlTest)
            assertProtoTaskNotExecuted(generateBufGenYamlTest)

            assertSourceCodeGenerated(mainSourceSet, *generatedFiles.toTypedArray())
            assertSourceCodeNotGenerated(testSourceSet, *generatedFiles.toTypedArray())

            assertWorkspaceProtoFilesCopied(mainSourceSet, *protoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(mainSourceSet)

            assertWorkspaceProtoFilesCopied(testSourceSet)
            assertWorkspaceImportProtoFilesCopied(testSourceSet)
        }

        fun BuildResult.assertTestTaskExecuted(
            protoFiles: List<Path>,
            importProtoFiles: List<Path>,
            generatedFiles: List<Path>,
            importGeneratedFiles: List<Path>,
        ) {
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(bufGenerateTest))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processTestProtoFiles))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processTestImportProtoFiles))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufYamlTest))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufGenYamlTest))

            val mainGenerateOutcome = if (importProtoFiles.isEmpty()) {
                TaskOutcome.SKIPPED
            } else {
                TaskOutcome.SUCCESS
            }

            assertEquals(mainGenerateOutcome, protoTaskOutcome(bufGenerateMain))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processMainProtoFiles))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufYamlMain))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufGenYamlMain))

            assertSourceCodeGenerated(testSourceSet, *generatedFiles.toTypedArray())
            assertSourceCodeNotGenerated(mainSourceSet, *generatedFiles.toTypedArray())

            assertSourceCodeGenerated(mainSourceSet, *importGeneratedFiles.toTypedArray())
            assertSourceCodeNotGenerated(testSourceSet, *importGeneratedFiles.toTypedArray())

            assertWorkspaceProtoFilesCopied(testSourceSet, *protoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(testSourceSet, *importProtoFiles.toTypedArray())

            assertWorkspaceProtoFilesCopied(mainSourceSet, *importProtoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(mainSourceSet)
        }

        val mainSourceSet = if (isKmp) "${KMP_SOURCE_SET}Main" else "main"
        val testSourceSet = if (isKmp) "${KMP_SOURCE_SET}Test" else "test"
        val bufGenerateMain = if (isKmp) "bufGenerate${KMP_SOURCE_SET_CAPITAL}Main" else "bufGenerateMain"
        val bufGenerateTest = if (isKmp) "bufGenerate${KMP_SOURCE_SET_CAPITAL}Test" else "bufGenerateTest"
        val processMainProtoFiles =
            if (isKmp) "process${KMP_SOURCE_SET_CAPITAL}MainProtoFiles" else "processMainProtoFiles"
        val processTestProtoFiles =
            if (isKmp) "process${KMP_SOURCE_SET_CAPITAL}TestProtoFiles" else "processTestProtoFiles"
        val processTestImportProtoFiles =
            if (isKmp) "process${KMP_SOURCE_SET_CAPITAL}TestImportProtoFiles" else "processTestImportProtoFiles"
        val generateBufYamlMain = if (isKmp) "generateBufYaml${KMP_SOURCE_SET_CAPITAL}Main" else "generateBufYamlMain"
        val generateBufYamlTest = if (isKmp) "generateBufYaml${KMP_SOURCE_SET_CAPITAL}Test" else "generateBufYamlTest"
        val generateBufGenYamlMain =
            if (isKmp) "generateBufGenYaml${KMP_SOURCE_SET_CAPITAL}Main" else "generateBufGenYamlMain"
        val generateBufGenYamlTest =
            if (isKmp) "generateBufGenYaml${KMP_SOURCE_SET_CAPITAL}Test" else "generateBufGenYamlTest"

        val protoBuildDir: Path by lazy {
            projectDir
                .resolve("build")
                .resolve("protoBuild")
        }

        val TestEnv.protoBuildDirSourceSets: Path by lazy {
            protoBuildDir
                .resolve("sourceSets")
        }

        val TestEnv.protoBuildDirGenerated: Path by lazy {
            protoBuildDir
                .resolve("generated")
        }

        val mainProtoFileSources = projectDir
            .resolve("src")
            .resolve(mainSourceSet)
            .resolve("proto")

        val testProtoFileSources = projectDir
            .resolve("src")
            .resolve(testSourceSet)
            .resolve("proto")
    }

    companion object {
        private const val KMP_SOURCE_SET = "common"
        private val KMP_SOURCE_SET_CAPITAL = KMP_SOURCE_SET.replaceFirstChar(Char::uppercaseChar)
        private const val KOTLIN_MULTIPLATFORM_DIR = "kotlin-multiplatform"
        const val RPC_INTERNAL = "_rpc_internal"
    }
}
