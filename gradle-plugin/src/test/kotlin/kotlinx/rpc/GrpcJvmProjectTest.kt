/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.base.GrpcBaseTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.TestInstance
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GrpcJvmProjectTest : GrpcBaseTest() {
    override val isKmp: Boolean = false

    @Test
    fun `Minimal gRPC Configuration`() = runGrpcTest {
        val result = runGradle(bufGenerateMain)

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

    @Test
    fun `No gRPC`() = runGrpcTest {
        runNonExistentTask(bufGenerateMain)
        runNonExistentTask(bufGenerateTest)
        runNonExistentTask(processMainProtoFiles)
        runNonExistentTask(processTestProtoFiles)
        runNonExistentTask(processTestImportProtoFiles)
        runNonExistentTask(generateBufYamlMain)
        runNonExistentTask(generateBufYamlTest)
        runNonExistentTask(generateBufGenYamlMain)
        runNonExistentTask(generateBufGenYamlTest)
    }

    @Test
    fun `Test-Only Sources`() = runGrpcTest {
        val result = runGradle(bufGenerateTest)

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

    @Test
    fun `Main and Test Mixed Sources`() = runGrpcTest {
        val mainRun = runGradle(bufGenerateMain)

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

        val testRun = runGradle(bufGenerateTest)

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

    @Test
    fun `Exclude and Include in protoSourceSets`() = runGrpcTest {
        runGradle(processMainProtoFiles)

        assertWorkspaceProtoFilesCopied(
            mainSourceSet,
            Path("some.proto"),
            Path("ok", "ok.proto"),
        )

        runGradle(processTestProtoFiles)

        assertWorkspaceProtoFilesCopied(
            testSourceSet,
            Path("include.proto"),
            Path("some", "package", "hello", "world", "file.proto"),
            Path("include", "yes1.proto"),
            Path("include", "yes2.proto"),
        )

        runGradle(processTestImportProtoFiles)

        assertWorkspaceImportProtoFilesCopied(
            testSourceSet,
            Path("some.proto"),
            Path("ok", "ok.proto"),
        )
    }

    @Test
    fun `Can Add Custom Protoc Plugins`() = runGrpcTest {
        runGradle(generateBufGenYamlMain)

        assertBufGenYaml(
            sourceSet = mainSourceSet,
            content = """
version: v2
clean: true
plugins:
  - remote: buf.build/protocolbuffers/java:v${TEST_PROTOBUF_VERSION.substringAfter(".")}
    out: java
  - remote: buf.build/grpc/java:v$TEST_GRPC_VERSION
    out: grpc-java
  - remote: buf.build/grpc/kotlin:v$TEST_GRPC_KOTLIN_VERSION
    out: grpc-kotlin
  - local: [protoc-gen-kotlin-multiplatform]
    out: kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-kotlin-multiplatform.log
      - messageMode=interface
      - explicitApiModeEnabled=true
  - local: [path, to, protoc-gen-myplugin.exe]
    out: myPlugin
    opt:
      - hello=world
      - foo=bar
    strategy: all
    include_imports: true
    include_wkt: false
    types:
      - my.type.Yay
    exclude_types:
      - my.type.Nope
  - remote: my.remote.plugin
    out: myRemotePlugin
inputs:
  - directory: proto
            """.trimIndent()
        )
    }

    @Test
    fun `Custom Protoc Plugins Must Declare an Artifact`() = runGrpcTest {
        val result = runGradleToFail(generateBufGenYamlMain)
        assert(result.output.contains("Artifact is not specified for protoc plugin myPlugin"))
    }

    @Suppress("detekt.MaxLineLength")
    private val cliFlagsRegex = "- Buf Arguments: \\[.*?, generate, --output, .*?, --include-imports, --include-wkt, --error-format, json, --config, some\\.buf\\.yaml, --log-format, json, --timeout, 60s]".toRegex()

    @Test
    fun `Custom Buf CLI Flags`() = runGrpcTest {
        val result = runGradleToFail(bufGenerateMain)
        assert(result.output.contains("could not read file: open some.buf.yaml: no such file or directory")) {
            "Expected failure due to missing some.buf.yaml file"
        }

        val cliArgsLine = result.output.lines().first { it.contains("- Buf Arguments:") }
        assert(cliArgsLine.contains(cliFlagsRegex)) {
            "Buf CLI flags are not correct: $cliArgsLine"
        }
    }

    @Test
    fun `Skip Generation When No Proto Files`() = runGrpcTest {
        val result = runGradle(bufGenerateMain)

        assertEquals(TaskOutcome.SKIPPED, result.protoTaskOutcome(bufGenerateMain))
    }

    @Test
    fun `Proto Tasks Are Cached Properly`() = runGrpcTest {
        val firstRunMain = runGradle(bufGenerateMain)

        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(bufGenerateMain))
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(generateBufYamlMain))
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(generateBufGenYamlMain))
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(processMainProtoFiles))

        val secondRunMain = runGradle(bufGenerateMain)

        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(bufGenerateMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(generateBufYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(generateBufGenYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(processMainProtoFiles))

        cleanProtoBuildDir()

        val thirdRunMain = runGradle(bufGenerateMain)

        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(bufGenerateMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(generateBufYamlMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(generateBufGenYamlMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(processMainProtoFiles))

        mainProtoFileSources
            .resolve("some.proto")
            .replace("content = 1", "content = 2")

        val fourthRunMain = runGradle(bufGenerateMain)

        assertEquals(TaskOutcome.SUCCESS, fourthRunMain.protoTaskOutcome(bufGenerateMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunMain.protoTaskOutcome(generateBufYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunMain.protoTaskOutcome(generateBufGenYamlMain))
        assertEquals(TaskOutcome.SUCCESS, fourthRunMain.protoTaskOutcome(processMainProtoFiles))

        val firstRunTest = runGradle(bufGenerateTest)

        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(bufGenerateTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(generateBufYamlTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(generateBufGenYamlTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(processTestProtoFiles))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(processTestImportProtoFiles))

        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(bufGenerateMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(generateBufYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(generateBufGenYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(processMainProtoFiles))

        val secondRunTest = runGradle(bufGenerateTest)

        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(bufGenerateTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufYamlTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufGenYamlTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(processTestProtoFiles))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(processTestImportProtoFiles))

        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(bufGenerateMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufGenYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(processMainProtoFiles))

        testProtoFileSources
            .resolve("other.proto")
            .replace("content = 1", "content = 2")

        val thirdRunTest = runGradle(bufGenerateTest)

        assertEquals(TaskOutcome.SUCCESS, thirdRunTest.protoTaskOutcome(bufGenerateTest))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufYamlTest))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufGenYamlTest))
        assertEquals(TaskOutcome.SUCCESS, thirdRunTest.protoTaskOutcome(processTestProtoFiles))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(processTestImportProtoFiles))

        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(bufGenerateMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufGenYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(processMainProtoFiles))

        mainProtoFileSources
            .resolve("some.proto")
            .replace("content = 2", "content = 3")

        val fourthRunTest = runGradle(bufGenerateTest)

        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(bufGenerateTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufYamlTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufGenYamlTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(processTestProtoFiles))
        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(processTestImportProtoFiles))

        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(bufGenerateMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufYamlMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufGenYamlMain))
        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(processMainProtoFiles))
    }
}
