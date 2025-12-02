/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.base.GrpcBaseTest
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import kotlin.io.path.Path
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GrpcKmpProjectTest : GrpcBaseTest() {
    override val isKmp: Boolean = true

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
    fun `No JVM Targets`() = runGrpcTest {
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
    fun `With Other Targets`() = runGrpcTest {
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
    fun `KMP Hierarchy`() = runGrpcTest {
        runKmpAndCheckFiles(
            SSets.commonMain,
        )

        runKmpAndCheckFiles(
            SSets.commonTest,
            SSets.commonMain,
        )

        runKmpAndCheckFiles(
            SSets.nativeMain,
            SSets.commonMain,
        )

        runKmpAndCheckFiles(
            SSets.nativeTest,
            SSets.commonMain, SSets.nativeMain,
            SSets.commonTest,
        )

        runKmpAndCheckFiles(
            SSets.jvmMain,
            SSets.commonMain,
        )

        runKmpAndCheckFiles(
            SSets.jvmTest,
            SSets.commonMain, SSets.jvmMain,
            SSets.commonTest,
        )

        runKmpAndCheckFiles(
            SSets.jsMain,
            SSets.commonMain, SSets.webMain,
        )

        runKmpAndCheckFiles(
            SSets.jsTest,
            SSets.commonMain, SSets.webMain, SSets.jsMain,
            SSets.commonTest, SSets.webTest
        )

        runKmpAndCheckFiles(
            SSets.appleMain,
            SSets.commonMain, SSets.nativeMain,
        )

        runKmpAndCheckFiles(
            SSets.appleTest,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain,
            SSets.commonTest, SSets.nativeTest
        )

        runKmpAndCheckFiles(
            SSets.macosMain,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain,
        )

        runKmpAndCheckFiles(
            SSets.macosTest,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain, SSets.macosMain,
            SSets.commonTest, SSets.nativeTest, SSets.appleTest
        )

        runKmpAndCheckFiles(
            SSets.macosArm64Main,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain, SSets.macosMain,
        )

        runKmpAndCheckFiles(
            SSets.macosArm64Test,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain, SSets.macosMain, SSets.macosArm64Main,
            SSets.commonTest, SSets.nativeTest, SSets.appleTest, SSets.macosTest,
            clean = false,
        )
    }

    @TestFactory
    fun `Proto Tasks Are Cached Properly`() = runGrpcTest {
        val firstRunCommonMain = runKmp(SSets.commonMain)

        assertOutcomes(
            result = firstRunCommonMain,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        assertOutcomes(firstRunCommonMain, SSets.commonTest)
        assertOutcomes(firstRunCommonMain, SSets.nativeMain)
        assertOutcomes(firstRunCommonMain, SSets.nativeTest)
        assertOutcomes(firstRunCommonMain, SSets.jvmMain)
        assertOutcomes(firstRunCommonMain, SSets.jvmTest)
        assertOutcomes(firstRunCommonMain, SSets.webMain)
        assertOutcomes(firstRunCommonMain, SSets.webTest)
        assertOutcomes(firstRunCommonMain, SSets.jsMain)
        assertOutcomes(firstRunCommonMain, SSets.jsTest)
        assertOutcomes(firstRunCommonMain, SSets.appleMain)
        assertOutcomes(firstRunCommonMain, SSets.appleTest)
        assertOutcomes(firstRunCommonMain, SSets.macosMain)
        assertOutcomes(firstRunCommonMain, SSets.macosTest)
        assertOutcomes(firstRunCommonMain, SSets.macosArm64Main)
        assertOutcomes(firstRunCommonMain, SSets.macosArm64Test)

        val secondRunCommonMain = runKmp(SSets.commonMain)

        assertOutcomes(
            result = secondRunCommonMain,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        cleanProtoBuildDir()

        val thirdRunCommonMain = runKmp(SSets.commonMain)

        assertOutcomes(
            result = thirdRunCommonMain,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        SSets.commonMain.sourceDir()
            .resolve("commonMain.proto")
            .replace("content = 1", "content = 2")

        val fourthRunCommonMain = runKmp(SSets.commonMain)

        assertOutcomes(
            result = fourthRunCommonMain,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        val firstRunMacosArm64Main = runKmp(SSets.macosArm64Main)

        assertOutcomes(
            result = firstRunMacosArm64Main,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = firstRunMacosArm64Main,
            sourceSet = SSets.nativeMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        assertOutcomes(
            result = firstRunMacosArm64Main,
            sourceSet = SSets.appleMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        assertOutcomes(
            result = firstRunMacosArm64Main,
            sourceSet = SSets.macosMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        assertOutcomes(
            result = firstRunMacosArm64Main,
            sourceSet = SSets.macosArm64Main,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        assertOutcomes(firstRunMacosArm64Main, SSets.nativeTest)
        assertOutcomes(firstRunMacosArm64Main, SSets.jvmMain)
        assertOutcomes(firstRunMacosArm64Main, SSets.jvmTest)
        assertOutcomes(firstRunMacosArm64Main, SSets.webMain)
        assertOutcomes(firstRunMacosArm64Main, SSets.webTest)
        assertOutcomes(firstRunMacosArm64Main, SSets.jsMain)
        assertOutcomes(firstRunMacosArm64Main, SSets.jsTest)
        assertOutcomes(firstRunMacosArm64Main, SSets.appleTest)
        assertOutcomes(firstRunMacosArm64Main, SSets.macosTest)
        assertOutcomes(firstRunMacosArm64Main, SSets.macosArm64Test)

        val firstRunMacosArm64Test = runKmp(SSets.macosArm64Test)

        assertOutcomes(
            result = firstRunMacosArm64Test,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = firstRunMacosArm64Test,
            sourceSet = SSets.nativeMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = firstRunMacosArm64Test,
            sourceSet = SSets.appleMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = firstRunMacosArm64Test,
            sourceSet = SSets.macosMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = firstRunMacosArm64Test,
            sourceSet = SSets.macosArm64Main,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = firstRunMacosArm64Test,
            sourceSet = SSets.nativeTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        assertOutcomes(
            result = firstRunMacosArm64Test,
            sourceSet = SSets.appleTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        assertOutcomes(
            result = firstRunMacosArm64Test,
            sourceSet = SSets.macosTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        assertOutcomes(
            result = firstRunMacosArm64Test,
            sourceSet = SSets.macosArm64Test,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        assertOutcomes(firstRunMacosArm64Test, SSets.jvmMain)
        assertOutcomes(firstRunMacosArm64Test, SSets.jvmTest)
        assertOutcomes(firstRunMacosArm64Test, SSets.webMain)
        assertOutcomes(firstRunMacosArm64Test, SSets.webTest)
        assertOutcomes(firstRunMacosArm64Test, SSets.jsMain)
        assertOutcomes(firstRunMacosArm64Test, SSets.jsTest)

        SSets.macosMain.sourceDir()
            .resolve("macosMain.proto")
            .replace("content = 1", "content = 2")

        val fifthRunCommonMain = runKmp(SSets.commonMain)

        assertOutcomes(
            result = fifthRunCommonMain,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        // didn't run
        assertOutcomes(fifthRunCommonMain, SSets.commonTest)
        assertOutcomes(fifthRunCommonMain, SSets.nativeMain)
        assertOutcomes(fifthRunCommonMain, SSets.nativeTest)
        assertOutcomes(fifthRunCommonMain, SSets.jvmMain)
        assertOutcomes(fifthRunCommonMain, SSets.jvmTest)
        assertOutcomes(fifthRunCommonMain, SSets.webMain)
        assertOutcomes(fifthRunCommonMain, SSets.webTest)
        assertOutcomes(fifthRunCommonMain, SSets.jsMain)
        assertOutcomes(fifthRunCommonMain, SSets.jsTest)
        assertOutcomes(fifthRunCommonMain, SSets.appleMain)
        assertOutcomes(fifthRunCommonMain, SSets.appleTest)
        assertOutcomes(fifthRunCommonMain, SSets.macosMain)
        assertOutcomes(fifthRunCommonMain, SSets.macosTest)
        assertOutcomes(fifthRunCommonMain, SSets.macosArm64Main)
        assertOutcomes(fifthRunCommonMain, SSets.macosArm64Test)

        val secondRunMacosArm64Main = runKmp(SSets.macosArm64Main)

        assertOutcomes(
            result = secondRunMacosArm64Main,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Main,
            sourceSet = SSets.nativeMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Main,
            sourceSet = SSets.appleMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Main,
            sourceSet = SSets.macosMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Main,
            sourceSet = SSets.macosArm64Main,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        assertOutcomes(secondRunMacosArm64Main, SSets.nativeTest)
        assertOutcomes(secondRunMacosArm64Main, SSets.jvmMain)
        assertOutcomes(secondRunMacosArm64Main, SSets.jvmTest)
        assertOutcomes(secondRunMacosArm64Main, SSets.webMain)
        assertOutcomes(secondRunMacosArm64Main, SSets.webTest)
        assertOutcomes(secondRunMacosArm64Main, SSets.jsMain)
        assertOutcomes(secondRunMacosArm64Main, SSets.jsTest)
        assertOutcomes(secondRunMacosArm64Main, SSets.appleTest)
        assertOutcomes(secondRunMacosArm64Main, SSets.macosTest)
        assertOutcomes(secondRunMacosArm64Main, SSets.macosArm64Test)

        val secondRunMacosArm64Test = runKmp(SSets.macosArm64Test)

        assertOutcomes(
            result = secondRunMacosArm64Test,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Test,
            sourceSet = SSets.nativeMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Test,
            sourceSet = SSets.appleMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Test,
            sourceSet = SSets.macosMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Test,
            sourceSet = SSets.macosArm64Main,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Test,
            sourceSet = SSets.nativeTest,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Test,
            sourceSet = SSets.appleTest,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunMacosArm64Test,
            sourceSet = SSets.macosTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        assertOutcomes(
            result = secondRunMacosArm64Test,
            sourceSet = SSets.macosArm64Test,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        assertOutcomes(secondRunMacosArm64Test, SSets.jvmMain)
        assertOutcomes(secondRunMacosArm64Test, SSets.jvmTest)
        assertOutcomes(secondRunMacosArm64Test, SSets.webMain)
        assertOutcomes(secondRunMacosArm64Test, SSets.webTest)
        assertOutcomes(secondRunMacosArm64Test, SSets.jsMain)
        assertOutcomes(secondRunMacosArm64Test, SSets.jsTest)

        val firstRunJvmMain = runKmp(SSets.jvmMain)

        assertOutcomes(
            result = firstRunJvmMain,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = firstRunJvmMain,
            sourceSet = SSets.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        assertOutcomes(firstRunJvmMain, SSets.commonTest)
        assertOutcomes(firstRunJvmMain, SSets.nativeMain)
        assertOutcomes(firstRunJvmMain, SSets.nativeTest)
        assertOutcomes(firstRunJvmMain, SSets.jvmTest)
        assertOutcomes(firstRunJvmMain, SSets.webMain)
        assertOutcomes(firstRunJvmMain, SSets.webTest)
        assertOutcomes(firstRunJvmMain, SSets.jsMain)
        assertOutcomes(firstRunJvmMain, SSets.jsTest)
        assertOutcomes(firstRunJvmMain, SSets.appleMain)
        assertOutcomes(firstRunJvmMain, SSets.appleTest)
        assertOutcomes(firstRunJvmMain, SSets.macosMain)
        assertOutcomes(firstRunJvmMain, SSets.macosTest)
        assertOutcomes(firstRunJvmMain, SSets.macosArm64Main)
        assertOutcomes(firstRunJvmMain, SSets.macosArm64Test)

        SSets.jvmMain.sourceDir()
            .resolve("jvmMain.proto")
            .replace("content = 1", "content = 2")

        val secondRunJvmMain = runKmp(SSets.jvmMain)

        assertOutcomes(
            result = secondRunJvmMain,
            sourceSet = SSets.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        assertOutcomes(
            result = secondRunJvmMain,
            sourceSet = SSets.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        // didn't run
        assertOutcomes(secondRunJvmMain, SSets.commonTest)
        assertOutcomes(secondRunJvmMain, SSets.nativeMain)
        assertOutcomes(secondRunJvmMain, SSets.nativeTest)
        assertOutcomes(secondRunJvmMain, SSets.jvmTest)
        assertOutcomes(secondRunJvmMain, SSets.webMain)
        assertOutcomes(secondRunJvmMain, SSets.webTest)
        assertOutcomes(secondRunJvmMain, SSets.jsMain)
        assertOutcomes(secondRunJvmMain, SSets.jsTest)
        assertOutcomes(secondRunJvmMain, SSets.appleMain)
        assertOutcomes(secondRunJvmMain, SSets.appleTest)
        assertOutcomes(secondRunJvmMain, SSets.macosMain)
        assertOutcomes(secondRunJvmMain, SSets.macosTest)
        assertOutcomes(secondRunJvmMain, SSets.macosArm64Main)
        assertOutcomes(secondRunJvmMain, SSets.macosArm64Test)
    }

    private fun GrpcTestEnv.runKmpAndCheckFiles(
        sourceSet: SSets,
        vararg imports: SSets,
        clean: Boolean = true,
    ) {
        runKmp(sourceSet).assertKmpSourceSet(sourceSet, *imports)

        if (clean) {
            cleanProtoBuildDir()
        }
    }

    private fun GrpcTestEnv.runKmp(sourceSet: SSets): BuildResult {
        return runGradle(bufGenerate(sourceSet))
    }

    private fun GrpcTestEnv.assertOutcomes(
        result: BuildResult,
        sourceSet: SSets,
        generate: TaskOutcome? = null,
        bufYaml: TaskOutcome? = null,
        bufGenYaml: TaskOutcome? = null,
        protoFiles: TaskOutcome? = null,
        protoFilesImports: TaskOutcome? = null,
    ) {
        assertEquals(generate, result.protoTaskOutcomeOrNull(bufGenerate(sourceSet)))
        assertEquals(bufYaml, result.protoTaskOutcomeOrNull(generateBufYaml(sourceSet)))
        assertEquals(bufGenYaml, result.protoTaskOutcomeOrNull(generateBufGenYaml(sourceSet)))
        assertEquals(protoFiles, result.protoTaskOutcomeOrNull(processProtoFiles(sourceSet)))
        assertEquals(protoFilesImports, result.protoTaskOutcomeOrNull(processProtoFilesImports(sourceSet)))
    }

    @TestFactory
    fun `Buf Tasks`() = runGrpcTest {
        runGradle("test_tasks", "--no-configuration-cache")
    }
}
