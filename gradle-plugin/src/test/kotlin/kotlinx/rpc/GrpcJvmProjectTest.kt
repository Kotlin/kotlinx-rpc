/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.base.GrpcBaseTest
import kotlinx.rpc.protoc.PlatformOption
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path
import kotlin.io.path.appendText
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GrpcJvmProjectTest : GrpcBaseTest() {
    override val type: Type = Type.Jvm

    @TestFactory
    fun `Minimal gRPC Configuration`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        result.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path("Some.ext.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
    }

    @TestFactory
    fun `Dependency Proto Codegen`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        result.assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, processCommonMainProtoFiles)
        result.assertOutcome(TaskOutcome.SUCCESS, generateBufYamlCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, generateBufGenYamlCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, extractProtoCommonMain)
        result.assertOutcome(TaskOutcome.NO_SOURCE, extractProtoImportCommonMain)

        // dependency.proto should be in the proto dir (codegen), not import dir
        assertWorkspaceProtoFilesCopied(mainSourceSet, Path("some.proto"), Path("dependency.proto"))

        // Both local and dependency protos generate code
        assertSourceCodeGenerated(
            mainSourceSet,
            Path("Some.kt"),
            Path("Some.ext.kt"),
            Path(RPC_INTERNAL, "Some.kt"),
            Path("dependency", "Dependency.kt"),
            Path("dependency", "Dependency.ext.kt"),
            Path("dependency", RPC_INTERNAL, "Dependency.kt"),
        )

        dryRunCompilation(SSetsJvm.main)
    }

    @TestFactory
    fun `Dependency Proto Import`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        result.assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, processCommonMainProtoFiles)
        result.assertOutcome(TaskOutcome.SUCCESS, generateBufYamlCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, generateBufGenYamlCommonMain)
        result.assertOutcome(TaskOutcome.NO_SOURCE, extractProtoCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, extractProtoImportCommonMain)

        // dependency.proto should be in the import dir (import-only), not proto dir
        assertWorkspaceProtoFilesCopied(mainSourceSet, Path("some.proto"))
        assertWorkspaceImportProtoFilesCopied(mainSourceSet, Path("dependency.proto"))

        // Only local protos generate code, dependency protos are imports only
        assertSourceCodeGenerated(
            mainSourceSet,
            Path("Some.kt"),
            Path("Some.ext.kt"),
            Path(RPC_INTERNAL, "Some.kt"),
        )

        dryRunCompilation(SSetsJvm.main)
    }

    @TestFactory
    fun `Dependency Proto Test Codegen`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonTest)

        result.assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonTest)
        result.assertOutcome(TaskOutcome.SUCCESS, processCommonTestProtoFiles)
        result.assertOutcome(TaskOutcome.NO_SOURCE, processCommonTestProtoFilesImports)
        result.assertOutcome(TaskOutcome.SUCCESS, generateBufYamlCommonTest)
        result.assertOutcome(TaskOutcome.SUCCESS, generateBufGenYamlCommonTest)
        result.assertOutcome(TaskOutcome.SUCCESS, extractProtoCommonTest)
        result.assertOutcome(TaskOutcome.NO_SOURCE, extractProtoImportCommonTest)


        // testDep proto reaches the test source set as codegen
        assertWorkspaceProtoFilesCopied(testSourceSet, Path("some.proto"), Path("testDep.proto"))

        // main is untouched: testProto must not leak to main
        assertWorkspaceProtoFilesCopied(mainSourceSet)
        assertWorkspaceImportProtoFilesCopied(mainSourceSet)

        assertSourceCodeGenerated(
            testSourceSet,
            Path("Some.kt"),
            Path("Some.ext.kt"),
            Path(RPC_INTERNAL, "Some.kt"),
            Path("dependency", "TestDep.kt"),
            Path("dependency", "TestDep.ext.kt"),
            Path("dependency", RPC_INTERNAL, "TestDep.kt"),
        )

        dryRunCompilation(SSetsJvm.test)
    }

    @TestFactory
    fun `Dependency Proto Test Import`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonTest)

        result.assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonTest)
        result.assertOutcome(TaskOutcome.SUCCESS, processCommonTestProtoFiles)
        result.assertOutcome(TaskOutcome.SUCCESS, processCommonTestProtoFilesImports)
        result.assertOutcome(TaskOutcome.SUCCESS, generateBufYamlCommonTest)
        result.assertOutcome(TaskOutcome.SUCCESS, generateBufGenYamlCommonTest)
        result.assertOutcome(TaskOutcome.NO_SOURCE, extractProtoCommonTest)
        result.assertOutcome(TaskOutcome.SUCCESS, extractProtoImportCommonTest)


        // testImportDep proto reaches test as import only
        assertWorkspaceProtoFilesCopied(testSourceSet, Path("some.proto"))
        assertWorkspaceImportProtoFilesCopied(testSourceSet, Path("testImportDep.proto"))

        // main is untouched
        assertWorkspaceProtoFilesCopied(mainSourceSet)
        assertWorkspaceImportProtoFilesCopied(mainSourceSet)

        // testImportDep.proto is import-only, so no TestImportDep.kt generated
        assertSourceCodeGenerated(
            testSourceSet,
            Path("Some.kt"),
            Path("Some.ext.kt"),
            Path(RPC_INTERNAL, "Some.kt"),
        )

        dryRunCompilation(SSetsJvm.test)
    }

    @TestFactory
    fun `Dependencies With Configure Block`() = runGrpcTest {
        runGradle("verifyExcludes", "--no-configuration-cache")
    }

    @TestFactory
    fun `Dependency Proto Extract Tasks Are Cached Properly`() = runGrpcTest {
        val firstRun = runGradle(extractProtoCommonMain, extractProtoImportCommonMain)
        assertEquals(TaskOutcome.SUCCESS, firstRun.protoTaskOutcome(extractProtoCommonMain))
        assertEquals(TaskOutcome.SUCCESS, firstRun.protoTaskOutcome(extractProtoImportCommonMain))

        val secondRun = runGradle(extractProtoCommonMain, extractProtoImportCommonMain)
        assertEquals(TaskOutcome.UP_TO_DATE, secondRun.protoTaskOutcome(extractProtoCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRun.protoTaskOutcome(extractProtoImportCommonMain))

        cleanProtoBuildDir()

        // Sync isn't a cacheable task, so a clean output dir means re-execute → SUCCESS,
        // not FROM_CACHE. UP_TO_DATE detection relies on the persisted output state.
        val thirdRun = runGradle(extractProtoCommonMain, extractProtoImportCommonMain)
        assertEquals(TaskOutcome.SUCCESS, thirdRun.protoTaskOutcome(extractProtoCommonMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRun.protoTaskOutcome(extractProtoImportCommonMain))

        // rewrite the codegen zip with different content — must invalidate extractProtoMain
        // but NOT extractProtoImportMain, which reads a separate zip.
        val codegenZip = projectDir.resolve("zip/main-dependency.zip")
        ZipOutputStream(Files.newOutputStream(codegenZip)).use { zos ->
            zos.putNextEntry(ZipEntry("mainDep.proto"))
            zos.write(
                """
                syntax = "proto3";

                package dependency;

                message MainDep {
                  string value = 1;
                  int32 version = 2;
                }
                """.trimIndent().toByteArray(),
            )
            zos.closeEntry()
        }

        val fourthRun = runGradle(extractProtoCommonMain, extractProtoImportCommonMain)
        assertEquals(TaskOutcome.SUCCESS, fourthRun.protoTaskOutcome(extractProtoCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRun.protoTaskOutcome(extractProtoImportCommonMain))
    }

    @TestFactory
    fun `No gRPC`() = runGrpcTest {
        SSetsJvm.entries.forEach {
            runNonExistentTasksForSourceSet(it)
        }
    }

    @TestFactory
    fun `Protoc Enabled Via Gradle Property`() = runGrpcTest {
        projectDir.resolve("gradle.properties").appendText("\nkotlinx.rpc.protoc=true\n")

        val result = runGradle(bufGenerateCommonMain)

        result.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path("Some.ext.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
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
                Path("Some.ext.kt"),
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
                Path("Some.ext.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )

        dryRunCompilation(SSetsJvm.main)

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
                Path("Other.ext.kt"),
                Path(RPC_INTERNAL, "Other.kt"),
            ),
            importGeneratedFiles = listOf(
                Path("Some.kt"),
                Path("Some.ext.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            ),
            mainGenerateOutcome = TaskOutcome.FROM_CACHE,
        )

        dryRunCompilation(SSetsJvm.test)
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
                Path("Some.ext.kt"),
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
                Path("Other.ext.kt"),
                Path(RPC_INTERNAL, "Other.kt"),
            ),
            importGeneratedFiles = listOf(
                Path("Some.kt"),
                Path("Some.ext.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            ),
            mainGenerateOutcome = TaskOutcome.FROM_CACHE,
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
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
  - local: some2
    out: myPlugin2
    opt:
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
  - local: [protoc-gen-kotlin-multiplatform]
    out: kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
  - local: [protoc-gen-grpc-kotlin-multiplatform]
    out: grpc-kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-grpc-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
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
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
  - local: [protoc-gen-grpc-kotlin-multiplatform]
    out: grpc-kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-grpc-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
  - local: [path, to, protoc-gen-myplugin.exe]
    out: myPlugin
    opt:
      - hello=world
      - foo=bar
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
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
      - hello=world
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
      - only=in main
inputs:
  - directory: proto
            """.trimIndent()
        )

        runGradle(generateBufGenYamlCommonTest)

        assertBufGenYaml(
            sourceSet = testSourceSet,
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
      - explicitApiModeEnabled=false
      - platform=jvm
  - local: [protoc-gen-grpc-kotlin-multiplatform]
    out: grpc-kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-grpc-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=false
      - platform=jvm
  - remote: my.remote.plugin
    out: myRemotePlugin
    opt:
      - hello=world
      - explicitApiModeEnabled=false
      - platform=jvm
      - only=in test
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
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(bufLockCommonMain))
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, firstRunMain.protoTaskOutcome(processCommonMainProtoFiles))

        val secondRunMain = runGradle(bufGenerateCommonMain)

        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(bufLockCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunMain.protoTaskOutcome(processCommonMainProtoFiles))

        cleanProtoBuildDir()

        val thirdRunMain = runGradle(bufGenerateCommonMain)

        assertEquals(TaskOutcome.FROM_CACHE, thirdRunMain.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(bufLockCommonMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, thirdRunMain.protoTaskOutcome(processCommonMainProtoFiles))

        mainProtoFileSources
            .resolve("some.proto")
            .replace("content = 1", "content = 2")

        val fourthRunMain = runGradle(bufGenerateCommonMain)

        assertEquals(TaskOutcome.SUCCESS, fourthRunMain.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunMain.protoTaskOutcome(bufLockCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunMain.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunMain.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, fourthRunMain.protoTaskOutcome(processCommonMainProtoFiles))

        projectDir
            .resolve("buf.lock")
            .appendText("# Force update lock task")

        val fifthRunMain = runGradle(bufGenerateCommonMain)

        assertEquals(TaskOutcome.SUCCESS, fifthRunMain.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.SUCCESS, fifthRunMain.protoTaskOutcome(bufLockCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fifthRunMain.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fifthRunMain.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fifthRunMain.protoTaskOutcome(processCommonMainProtoFiles))

        val firstRunTest = runGradle(bufGenerateCommonTest)

        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(bufGenerateCommonTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(bufLockCommonTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(generateBufYamlCommonTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(generateBufGenYamlCommonTest))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(processCommonTestProtoFiles))
        assertEquals(TaskOutcome.SUCCESS, firstRunTest.protoTaskOutcome(processCommonTestProtoFilesImports))

        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(bufLockCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, firstRunTest.protoTaskOutcome(processCommonMainProtoFiles))

        val secondRunTest = runGradle(bufGenerateCommonTest)

        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(bufGenerateCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(bufLockCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufGenYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(processCommonTestProtoFiles))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(processCommonTestProtoFilesImports))

        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(bufLockCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRunTest.protoTaskOutcome(processCommonMainProtoFiles))

        testProtoFileSources
            .resolve("other.proto")
            .replace("content = 1", "content = 2")

        val thirdRunTest = runGradle(bufGenerateCommonTest)

        assertEquals(TaskOutcome.SUCCESS, thirdRunTest.protoTaskOutcome(bufGenerateCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(bufLockCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufGenYamlCommonTest))
        assertEquals(TaskOutcome.SUCCESS, thirdRunTest.protoTaskOutcome(processCommonTestProtoFiles))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(processCommonTestProtoFilesImports))

        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(bufLockCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, thirdRunTest.protoTaskOutcome(processCommonMainProtoFiles))

        mainProtoFileSources
            .resolve("some.proto")
            .replace("content = 2", "content = 3")

        val fourthRunTest = runGradle(bufGenerateCommonTest)

        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(bufGenerateCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(bufLockCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufGenYamlCommonTest))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(processCommonTestProtoFiles))
        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(processCommonTestProtoFilesImports))

        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(bufGenerateCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(bufLockCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufYamlCommonMain))
        assertEquals(TaskOutcome.UP_TO_DATE, fourthRunTest.protoTaskOutcome(generateBufGenYamlCommonMain))
        assertEquals(TaskOutcome.SUCCESS, fourthRunTest.protoTaskOutcome(processCommonMainProtoFiles))
    }

    @TestFactory
    fun `Disable Default Grpc Plugin`() = runGrpcTest {
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
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
inputs:
  - directory: proto
            """.trimIndent()
        )
    }

    @TestFactory
    fun `Disable Default Protobuf Plugin`() = runGrpcTest {
        runGradle(generateBufGenYamlCommonMain)

        assertBufGenYaml(
            sourceSet = mainSourceSet,
            content = """
version: v2
clean: true
plugins:
  - local: [protoc-gen-grpc-kotlin-multiplatform]
    out: grpc-kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-grpc-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
inputs:
  - directory: proto
            """.trimIndent()
        )
    }

    @TestFactory
    fun `Disable Both Default Plugins`() = runGrpcTest {
        runGradle(generateBufGenYamlCommonMain)

        assertBufGenYaml(
            sourceSet = mainSourceSet,
            content = """
version: v2
clean: true
plugins:
  - local: [path, to, protoc-gen-myplugin.exe]
    out: myPlugin
    opt:
      - hello=world
      - explicitApiModeEnabled=false
      - platform=${PlatformOption.JVM}
inputs:
  - directory: proto
            """.trimIndent()
        )
    }

    @TestFactory
    fun `Buf Tasks`() = runGrpcTest {
        runGradle("test_tasks", "--no-configuration-cache")
    }

    @TestFactory
    fun `Buf Dependencies`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        result.assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, bufLockCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonMain)

        assertBufYaml(mainSourceSet,
            content = """
version: v2
lint:
  use:
    - STANDARD
breaking:
  use:
    - FILE
modules:
  - path: proto
deps:
  - buf.build/googleapis/googleapis
            """.trimIndent())

        val workspaceLockFile = protoBuildDirSourceSets
            .resolve(mainSourceSet.name)
            .resolve("buf.lock")
        assert(workspaceLockFile.exists()) { "buf.lock was not generated" }

        result.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path("Some.ext.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
    }

    @TestFactory
    fun `Buf Lock File`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        result.assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, bufLockCommonMain)
        result.assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonMain)

        assertBufYaml(mainSourceSet,
            content = """
version: v2
lint:
  use:
    - STANDARD
breaking:
  use:
    - FILE
modules:
  - path: proto
deps:
  - buf.build/googleapis/googleapis
            """.trimIndent())

        val workspaceLockFile = protoBuildDirSourceSets
            .resolve(mainSourceSet.name)
            .resolve("buf.lock")
        assert(workspaceLockFile.exists()) { "buf.lock was not copied" }
        assertEquals(
            projectDir.resolve("buf.lock").readText(),
            workspaceLockFile.readText(),
        )

        result.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path("Some.ext.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
    }

    @TestFactory
    fun `Skip Buf Lock When No Deps`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        result.assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonMain)
        result.assertOutcome(TaskOutcome.SKIPPED, bufLockCommonMain)
    }
}
