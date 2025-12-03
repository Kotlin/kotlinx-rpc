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
    override val type: Type = Type.Kmp

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
        SSetsKmp.entries.forEach {
            runNonExistentTasksForSourceSet(it)
        }
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
        runAndCheckFiles(
            SSetsKmp.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.commonTest,
            SSetsKmp.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.nativeMain,
            SSetsKmp.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.nativeTest,
            SSetsKmp.commonMain, SSetsKmp.nativeMain,
            SSetsKmp.commonTest,
        )

        runAndCheckFiles(
            SSetsKmp.jvmMain,
            SSetsKmp.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.jvmTest,
            SSetsKmp.commonMain, SSetsKmp.jvmMain,
            SSetsKmp.commonTest,
        )

        runAndCheckFiles(
            SSetsKmp.jsMain,
            SSetsKmp.commonMain, SSetsKmp.webMain,
        )

        runAndCheckFiles(
            SSetsKmp.jsTest,
            SSetsKmp.commonMain, SSetsKmp.webMain, SSetsKmp.jsMain,
            SSetsKmp.commonTest, SSetsKmp.webTest
        )

        runAndCheckFiles(
            SSetsKmp.appleMain,
            SSetsKmp.commonMain, SSetsKmp.nativeMain,
        )

        runAndCheckFiles(
            SSetsKmp.appleTest,
            SSetsKmp.commonMain, SSetsKmp.nativeMain, SSetsKmp.appleMain,
            SSetsKmp.commonTest, SSetsKmp.nativeTest
        )

        runAndCheckFiles(
            SSetsKmp.macosMain,
            SSetsKmp.commonMain, SSetsKmp.nativeMain, SSetsKmp.appleMain,
        )

        runAndCheckFiles(
            SSetsKmp.macosTest,
            SSetsKmp.commonMain, SSetsKmp.nativeMain, SSetsKmp.appleMain, SSetsKmp.macosMain,
            SSetsKmp.commonTest, SSetsKmp.nativeTest, SSetsKmp.appleTest
        )

        runAndCheckFiles(
            SSetsKmp.macosArm64Main,
            SSetsKmp.commonMain, SSetsKmp.nativeMain, SSetsKmp.appleMain, SSetsKmp.macosMain,
        )

        runAndCheckFiles(
            SSetsKmp.macosArm64Test,
            SSetsKmp.commonMain, SSetsKmp.nativeMain, SSetsKmp.appleMain, SSetsKmp.macosMain, SSetsKmp.macosArm64Main,
            SSetsKmp.commonTest, SSetsKmp.nativeTest, SSetsKmp.appleTest, SSetsKmp.macosTest,
        )
    }

    @TestFactory
    fun `Proto Tasks Are Cached Properly`() = runGrpcTest {
        val firstRunCommonMain = runForSet(SSetsKmp.commonMain)

        firstRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        firstRunCommonMain.assertOutcomes(SSetsKmp.commonTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.nativeMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.nativeTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.jvmMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.jvmTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.webMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.webTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.jsMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.jsTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.appleMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.appleTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.macosMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.macosTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.macosArm64Main)
        firstRunCommonMain.assertOutcomes(SSetsKmp.macosArm64Test)

        val secondRunCommonMain = runForSet(SSetsKmp.commonMain)

        secondRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        cleanProtoBuildDir()

        val thirdRunCommonMain = runForSet(SSetsKmp.commonMain)

        thirdRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        SSetsKmp.commonMain.sourceDir()
            .resolve("commonMain.proto")
            .replace("content = 1", "content = 2")

        val fourthRunCommonMain = runForSet(SSetsKmp.commonMain)

        fourthRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        val firstRunMacosArm64Main = runForSet(SSetsKmp.macosArm64Main)

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.nativeMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.appleMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.macosMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.macosArm64Main,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.nativeTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.jvmMain)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.jvmTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.webMain)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.webTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.jsMain)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.jsTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.appleTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.macosTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.macosArm64Test)

        val firstRunMacosArm64Test = runForSet(SSetsKmp.macosArm64Test)

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.nativeMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.appleMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.macosMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.macosArm64Main,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.nativeTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.appleTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.macosTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.macosArm64Test,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.jvmMain)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.jvmTest)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.webMain)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.webTest)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.jsMain)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.jsTest)

        SSetsKmp.macosMain.sourceDir()
            .resolve("macosMain.proto")
            .replace("content = 1", "content = 2")

        val fifthRunCommonMain = runForSet(SSetsKmp.commonMain)

        fifthRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        fifthRunCommonMain.assertOutcomes(SSetsKmp.commonTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.nativeMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.nativeTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.jvmMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.jvmTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.webMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.webTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.jsMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.jsTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.appleMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.appleTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.macosMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.macosTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.macosArm64Main)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.macosArm64Test)

        val secondRunMacosArm64Main = runForSet(SSetsKmp.macosArm64Main)

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.nativeMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.appleMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.macosMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.macosArm64Main,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.nativeTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.jvmMain)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.jvmTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.webMain)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.webTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.jsMain)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.jsTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.appleTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.macosTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.macosArm64Test)

        val secondRunMacosArm64Test = runForSet(SSetsKmp.macosArm64Test)

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.nativeMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.appleMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.macosMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.macosArm64Main,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.nativeTest,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.appleTest,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.macosTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.macosArm64Test,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.jvmMain)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.jvmTest)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.webMain)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.webTest)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.jsMain)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.jsTest)

        val firstRunJvmMain = runForSet(SSetsKmp.jvmMain)

        firstRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunJvmMain.assertOutcomes(SSetsKmp.commonTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.nativeMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.nativeTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.jvmTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.webMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.webTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.jsMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.jsTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.appleMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.appleTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.macosMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.macosTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.macosArm64Main)
        firstRunJvmMain.assertOutcomes(SSetsKmp.macosArm64Test)

        SSetsKmp.jvmMain.sourceDir()
            .resolve("jvmMain.proto")
            .replace("content = 1", "content = 2")

        val secondRunJvmMain = runForSet(SSetsKmp.jvmMain)

        secondRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        secondRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        // didn't run
        secondRunJvmMain.assertOutcomes(SSetsKmp.commonTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.nativeMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.nativeTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.jvmTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.webMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.webTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.jsMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.jsTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.appleMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.appleTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.macosMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.macosTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.macosArm64Main)
        secondRunJvmMain.assertOutcomes(SSetsKmp.macosArm64Test)
    }

    @TestFactory
    fun `Buf Tasks`() = runGrpcTest {
        runGradle("test_tasks", "--no-configuration-cache")
    }
}
