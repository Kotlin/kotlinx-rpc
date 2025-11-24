/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.base.GrpcBaseTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import kotlin.io.path.Path
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GrpcJvmProjectTest : GrpcBaseTest() {
    override val isKmp: Boolean = false

    @TestFactory
    fun `Minimal gRPC Configuration`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        result.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
    }

    @TestFactory
    fun `No gRPC`() = runGrpcTest {
        runNonExistentTask(bufGenerateCommonMain)
        runNonExistentTask(bufGenerateCommonTest)
        runNonExistentTask(processCommonMainProtoFiles)
        runNonExistentTask(processCommonTestProtoFiles)
        runNonExistentTask(processCommonTestProtoFilesImports)
        runNonExistentTask(generateBufYamlCommonMain)
        runNonExistentTask(generateBufYamlCommonTest)
        runNonExistentTask(generateBufGenYamlCommonMain)
        runNonExistentTask(generateBufGenYamlCommonTest)
    }

    @TestFactory
    fun `Test-Only Sources`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonTest)

        result.assertTestTaskExecuted(
            protoFiles = listOf(
                Path("some.proto"),
            ),
            importProtoFiles = emptyList(),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            ),
            importGeneratedFiles = emptyList()
        )
    }

    @TestFactory
    fun `Main and Test Mixed Sources`() = runGrpcTest {
        val mainRun = runGradle(bufGenerateCommonMain)

        mainRun.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )

        cleanProtoBuildDir()

        val testRun = runGradle(bufGenerateCommonTest)

        testRun.assertTestTaskExecuted(
            protoFiles = listOf(
                Path("other.proto")
            ),
            importProtoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Other.kt"),
                Path(RPC_INTERNAL, "Other.kt"),
            ),
            importGeneratedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
    }

    @TestFactory
    fun `Java Source Sets`() = runGrpcTest {
        val mainRun = runGradle(bufGenerateCommonMain)

        mainRun.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )

        cleanProtoBuildDir()

        val testRun = runGradle(bufGenerateCommonTest)

        testRun.assertTestTaskExecuted(
            protoFiles = listOf(
                Path("other.proto")
            ),
            importProtoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Other.kt"),
                Path(RPC_INTERNAL, "Other.kt"),
            ),
            importGeneratedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
    }

    @TestFactory
    fun `Java and Kotlin Source Sets`() = runGrpcTest {
        runGradle(processCommonMainProtoFiles)
        runGradle(generateBufGenYamlCommonMain)

        assertWorkspaceProtoFilesCopied(
            mainSourceSet,
            Path("some.proto"),
        )

        assertBufGenYaml(
            sourceSet = mainSourceSet,
            content = """
version: v2
clean: true
plugins:
  - local: some
    out: myPlugin
    opt:
      - explicitApiModeEnabled=true
  - local: some2
    out: myPlugin2
    opt:
      - explicitApiModeEnabled=true
  - local: [protoc-gen-kotlin-multiplatform]
    out: kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=true
  - local: [protoc-gen-grpc-kotlin-multiplatform]
    out: grpc-kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-grpc-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=true
inputs:
  - directory: proto
            """.trimIndent()
        )

        cleanProtoBuildDir()

        runGradle(processCommonTestProtoFiles)
        runGradle(processCommonTestProtoFilesImports)
        runGradle(generateBufGenYamlCommonTest)

        assertWorkspaceProtoFilesCopied(
            testSourceSet,
            Path("other.proto"),
        )

        assertWorkspaceImportProtoFilesCopied(
            testSourceSet,
            Path("some.proto"),
        )
    }

    @TestFactory
    fun `Exclude and Include in proto sourceSets`() = runGrpcTest {
        runGradle(processCommonMainProtoFiles)

        assertWorkspaceProtoFilesCopied(
            mainSourceSet,
            Path("some.proto"),
            Path("ok", "ok.proto"),
        )

        runGradle(processCommonTestProtoFiles)

        assertWorkspaceProtoFilesCopied(
            testSourceSet,
            Path("include.proto"),
            Path("some", "package", "hello", "world", "file.proto"),
            Path("include", "yes1.proto"),
            Path("include", "yes2.proto"),
        )

        runGradle(processCommonTestProtoFilesImports)

        assertWorkspaceImportProtoFilesCopied(
            testSourceSet,
            Path("some.proto"),
            Path("ok", "ok.proto"),
        )
    }

    @TestFactory
    fun `Can Add Custom Protoc Plugins`() = runGrpcTest {
        runGradle(generateBufGenYamlCommonMain)

        assertBufGenYaml(
            sourceSet = mainSourceSet,
            content = """
version: v2
clean: true
plugins:
  - local: [protoc-gen-kotlin-multiplatform]
    out: kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=true
  - local: [protoc-gen-grpc-kotlin-multiplatform]
    out: grpc-kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-grpc-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=true
  - local: [path, to, protoc-gen-myplugin.exe]
    out: myPlugin
    opt:
      - hello=world
      - foo=bar
      - explicitApiModeEnabled=true
    strategy: all
    include_imports: true
    include_wkt: false
    types:
      - my.type.Yay
    exclude_types:
      - my.type.Nope
  - remote: my.remote.plugin
    out: myRemotePlugin
    opt:
      - explicitApiModeEnabled=true
inputs:
  - directory: proto
            """.trimIndent()
        )
    }

    @TestFactory
    fun `Custom Protoc Plugins Must Declare an Artifact`() = runGrpcTest {
        val result = runGradleToFail(generateBufGenYamlCommonMain)
        assert(result.output.contains("Artifact is not specified for protoc plugin myPlugin"))
    }

    @Suppress("detekt.MaxLineLength")
    private val cliFlagsRegex =
        "- Buf Arguments: \\[.*?, generate, --output, .*?, --include-imports, --include-wkt, --error-format, json, --config, some\\.buf\\.yaml, --log-format, json, --timeout, 60s]".toRegex()

    @TestFactory
    fun `Custom Buf CLI Flags`() = runGrpcTest {
        val result = runGradleToFail(bufGenerateCommonMain)
        assert(result.output.contains("could not read file: open some.buf.yaml: no such file or directory")) {
            "Expected failure due to missing some.buf.yaml file"
        }

        val cliArgsLine = result.output.lines().first { it.contains("- Buf Arguments:") }
        assert(cliArgsLine.contains(cliFlagsRegex)) {
            "Buf CLI flags are not correct: $cliArgsLine"
        }
    }

    @TestFactory
    fun `Skip Generation When No Proto Files`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        assertEquals(TaskOutcome.SKIPPED, result.protoTaskOutcome(bufGenerateCommonMain))
    }

    @TestFactory
    fun `Proto Tasks Are Cached Properly`() = runGrpcTest {
        val firstRunMain = runGradle(bufGenerateCommonMain)

        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(processCommonMainProtoFiles))

        val secondRunMain = runGradle(bufGenerateCommonMain)

        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(processCommonMainProtoFiles))

        cleanProtoBuildDir()

        val thirdRunMain = runGradle(bufGenerateCommonMain)

        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(processCommonMainProtoFiles))

        mainProtoFileSources
            .resolve("some.proto")
            .replace("content = 1", "content = 2")

        val fourthRunMain = runGradle(bufGenerateCommonMain)

        assertEquals(TaskOutcome.SUCCESS, fourthRunMain.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunMain.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunMain.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, fourthRunMain.protoTaskOutcome(processCommonMainProtoFiles))

        val firstRunTest = runGradle(bufGenerateCommonTest)

        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(bufGenerateCommonTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(generateBufYamlCommonTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(generateBufGenYamlCommonTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(processCommonTestProtoFiles))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(processCommonTestProtoFilesImports))

        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(processCommonMainProtoFiles))

        val secondRunTest = runGradle(bufGenerateCommonTest)

        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(bufGenerateCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufGenYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(processCommonTestProtoFiles))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(processCommonTestProtoFilesImports))

        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(processCommonMainProtoFiles))

        testProtoFileSources
            .resolve("other.proto")
            .replace("content = 1", "content = 2")

        val thirdRunTest = runGradle(bufGenerateCommonTest)

        assertEquals(TaskOutcome.SUCCESS, thirdRunTest.protoTaskOutcome(bufGenerateCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufGenYamlCommonTest))
        assertEquals(TaskOutcome.SUCCESS, thirdRunTest.protoTaskOutcome(processCommonTestProtoFiles))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(processCommonTestProtoFilesImports))

        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(processCommonMainProtoFiles))

        mainProtoFileSources
            .resolve("some.proto")
            .replace("content = 2", "content = 3")

        val fourthRunTest = runGradle(bufGenerateCommonTest)

        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(bufGenerateCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufGenYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(processCommonTestProtoFiles))
        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(processCommonTestProtoFilesImports))

        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(processCommonMainProtoFiles))
    }

    @TestFactory
    fun `Buf Tasks`() = runGrpcTest {
        runGradle("test_tasks", "--no-configuration-cache")
    }
}
