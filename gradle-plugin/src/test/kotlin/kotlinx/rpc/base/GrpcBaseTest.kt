/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.base

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestInstance
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.*
import kotlin.test.assertEquals
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class GrpcBaseTest : BaseTest() {
    abstract val isKmp: Boolean

    protected fun runGrpcTest(test: GrpcTestEnv.() -> Unit): Stream<DynamicTest> = runWithAllGradleVersions {
        runTest(GrpcTestEnv(it), test)
    }

    inner class GrpcTestEnv(versions: VersionsEnv) : TestEnv(versions) {
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

        fun assertSourceCodeGenerated(sourceSet: SSets, vararg files: Path) {
            val dir = protoBuildDirGenerated.resolve(sourceSet.name).resolve(KOTLIN_MULTIPLATFORM_DIR)
            if (files.isNotEmpty()) {
                assert(dir.exists()) {
                    "Directory '$dir' with generated sources does not exist"
                }
            }

            files.forEach { file ->
                assert(dir.resolve(file).exists()) {
                    "File '${file}' in '$dir' does not exist"
                }
            }
        }

        fun assertSourceCodeNotGenerated(sourceSet: SSets, vararg files: Path) {
            val dir = protoBuildDirGenerated.resolve(sourceSet.name).resolve(KOTLIN_MULTIPLATFORM_DIR)

            files.forEach { file ->
                assert(!dir.resolve(file).exists()) {
                    "File '${file}' in '$dir' should not exist"
                }
            }
        }

        fun assertSourceCodeNotGeneratedExcept(sourceSet: SSets, vararg files: Path) {
            val dir = protoBuildDirGenerated.resolve(sourceSet.name).resolve(KOTLIN_MULTIPLATFORM_DIR)

            fun Path.doAssert() {
                listDirectoryEntries().forEach { entry ->
                    when {
                        entry.isDirectory() -> {
                            entry.doAssert()
                        }

                        entry.isRegularFile() && entry.relativeTo(dir) in files -> {
                            // fine
                        }

                        entry.name == ".keep" -> {
                            // fine
                        }

                        else -> {
                            fail("File '${entry}' in '$this' should not exist")
                        }
                    }
                }
            }

            dir.doAssert()
        }

        @OptIn(ExperimentalPathApi::class)
        private fun assertWorkspaceProtoFilesCopiedInternal(vararg files: Path, sourceSet: SSets, dir: String) {
            val protoSources = protoBuildDirSourceSets.resolve(sourceSet.name).resolve(dir)
            val included = files.map { file ->
                val resolved = protoSources.resolve(file)
                assert(resolved.exists()) {
                    "File '${file}' in '$protoSources' does not exist"
                }
                resolved.relativeTo(protoSources).pathString
            }.toSet()

            protoSources.walk().forEach {
                val pathString = it.relativeTo(protoSources).pathString
                if (it.isRegularFile() && it.extension == "proto" && pathString !in included) {
                    fail("File '${it}' in '$protoSources' is not expected")
                }
            }
        }

        fun assertWorkspaceProtoFilesCopied(sourceSet: SSets, vararg files: Path) {
            assertWorkspaceProtoFilesCopiedInternal(*files, sourceSet = sourceSet, dir = "proto")
        }

        fun assertWorkspaceImportProtoFilesCopied(sourceSet: SSets, vararg files: Path) {
            assertWorkspaceProtoFilesCopiedInternal(*files, sourceSet = sourceSet, dir = "import")
        }

        @OptIn(ExperimentalPathApi::class)
        fun cleanProtoBuildDir() {
            protoBuildDir.deleteRecursively()
        }

        fun assertBufGenYaml(sourceSet: SSets, @Language("Yaml") content: String) {
            val file = protoBuildDirSourceSets
                .resolve(sourceSet.name)
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
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(bufGenerateCommonMain))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processCommonMainProtoFiles))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufYamlCommonMain))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufGenYamlCommonMain))

            assertProtoTaskNotExecuted(bufGenerateCommonTest)
            assertProtoTaskNotExecuted(processCommonTestProtoFiles)
            assertProtoTaskNotExecuted(processCommonTestProtoFilesImports)
            assertProtoTaskNotExecuted(generateBufYamlCommonTest)
            assertProtoTaskNotExecuted(generateBufGenYamlCommonTest)

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
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(bufGenerateCommonTest))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processCommonTestProtoFiles))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processCommonTestProtoFilesImports))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufYamlCommonTest))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufGenYamlCommonTest))

            val mainGenerateOutcome = if (importProtoFiles.isEmpty()) {
                TaskOutcome.SKIPPED
            } else {
                TaskOutcome.SUCCESS
            }

            assertEquals(mainGenerateOutcome, protoTaskOutcome(bufGenerateCommonMain))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processCommonMainProtoFiles))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufYamlCommonMain))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufGenYamlCommonMain))

            assertSourceCodeGenerated(testSourceSet, *generatedFiles.toTypedArray())
            assertSourceCodeNotGenerated(mainSourceSet, *generatedFiles.toTypedArray())

            assertSourceCodeGenerated(mainSourceSet, *importGeneratedFiles.toTypedArray())
            assertSourceCodeNotGenerated(testSourceSet, *importGeneratedFiles.toTypedArray())

            assertWorkspaceProtoFilesCopied(testSourceSet, *protoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(testSourceSet, *importProtoFiles.toTypedArray())

            assertWorkspaceProtoFilesCopied(mainSourceSet, *importProtoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(mainSourceSet)
        }

        fun BuildResult.assertTaskExecuted(
            sourceSet: SSets,
            protoFiles: List<Path>,
            importProtoFiles: List<Path>,
            generatedFiles: List<Path>,
            notExecuted: List<SSets>,
        ) {
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(bufGenerate(sourceSet)))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processProtoFiles(sourceSet)))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(processProtoFilesImports(sourceSet)))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufYaml(sourceSet)))
            assertEquals(TaskOutcome.SUCCESS, protoTaskOutcome(generateBufGenYaml(sourceSet)))

            assertSourceCodeGenerated(sourceSet, *generatedFiles.toTypedArray())
            assertSourceCodeNotGeneratedExcept(sourceSet, *generatedFiles.toTypedArray())
            assertWorkspaceProtoFilesCopied(sourceSet, *protoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(sourceSet, *importProtoFiles.toTypedArray())

            notExecuted.forEach {
                assertProtoTaskNotExecuted(bufGenerate(it))
                assertProtoTaskNotExecuted(processProtoFiles(it))
                assertProtoTaskNotExecuted(processProtoFilesImports(it))
                assertProtoTaskNotExecuted(generateBufYaml(it))
                assertProtoTaskNotExecuted(generateBufGenYaml(it))
            }
        }

        fun BuildResult.assertKmpSourceSet(
            sourceSet: SSets,
            vararg imports: SSets,
        ) {
            val ktFile = "${sourceSet.capital}.kt"
            val importsSet = imports.toSet()

            assertTaskExecuted(
                sourceSet = sourceSet,
                protoFiles = listOf(Path("${sourceSet.name}.proto")),
                importProtoFiles = imports.map {
                    Path("${it.name}.proto")
                },
                generatedFiles = listOf(
                    Path(ktFile),
                    Path(RPC_INTERNAL, ktFile),
                ),
                notExecuted = SSets.entries.filter { it != sourceSet && it !in importsSet },
            )
        }

        fun bufGenerate(sourceSet: SSets) = "bufGenerate${sourceSet.capital}"
        fun processProtoFiles(sourceSet: SSets) = "process${sourceSet.capital}ProtoFiles"
        fun processProtoFilesImports(sourceSet: SSets) = "process${sourceSet.capital}ProtoFilesImports"
        fun generateBufYaml(sourceSet: SSets) = "generateBufYaml${sourceSet.capital}"
        fun generateBufGenYaml(sourceSet: SSets) = "generateBufGenYaml${sourceSet.capital}"

        val mainSourceSet = if (isKmp) SSets.commonMain else SSets.main
        val testSourceSet = if (isKmp) SSets.commonTest else SSets.test

        val bufGenerateCommonMain = bufGenerate(mainSourceSet)
        val bufGenerateCommonTest = bufGenerate(testSourceSet)
        val processCommonMainProtoFiles = processProtoFiles(mainSourceSet)
        val processCommonTestProtoFiles = processProtoFiles(testSourceSet)
        val processCommonTestProtoFilesImports = processProtoFilesImports(testSourceSet)
        val generateBufYamlCommonMain = generateBufYaml(mainSourceSet)
        val generateBufYamlCommonTest = generateBufYaml(testSourceSet)
        val generateBufGenYamlCommonMain = generateBufGenYaml(mainSourceSet)
        val generateBufGenYamlCommonTest = generateBufGenYaml(testSourceSet)

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

        val mainProtoFileSources: Path by lazy {
            projectDir
                .resolve("src")
                .resolve(mainSourceSet.name)
                .resolve("proto")
        }

        val testProtoFileSources: Path by lazy {
            projectDir
                .resolve("src")
                .resolve(testSourceSet.name)
                .resolve("proto")
        }

        fun SSets.sourceDir(): Path {
            return projectDir
                .resolve("src")
                .resolve(name)
                .resolve("proto")
        }
    }

    @Suppress("EnumEntryName")
    enum class SSets {
        main, test,

        commonMain, commonTest,
        jvmMain, jvmTest,
        androidMain, androidTest,
        jsMain, jsTest,
        nativeMain, nativeTest,
        appleMain, appleTest,
        macosMain, macosTest,
        macosArm64Main, macosArm64Test,
    }

    private val SSets.capital get() = name.replaceFirstChar { it.titlecase() }

    companion object {
        private const val KOTLIN_MULTIPLATFORM_DIR = "kotlin-multiplatform"
        const val RPC_INTERNAL = "_rpc_internal"
    }
}
