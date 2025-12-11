/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.base.GrpcBaseTest
import kotlinx.rpc.base.testTestsForAndroidKmpLibExist
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import kotlin.io.path.Path

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
        SSetsKmp.Default.entries.forEach {
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
            SSetsKmp.Default.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.Default.commonTest,
            SSetsKmp.Default.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.Default.nativeMain,
            SSetsKmp.Default.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.Default.nativeTest,
            SSetsKmp.Default.commonMain, SSetsKmp.Default.nativeMain,
            SSetsKmp.Default.commonTest,
        )

        runAndCheckFiles(
            SSetsKmp.Default.jvmMain,
            SSetsKmp.Default.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.Default.jvmTest,
            SSetsKmp.Default.commonMain, SSetsKmp.Default.jvmMain,
            SSetsKmp.Default.commonTest,
        )

        runAndCheckFiles(
            SSetsKmp.Default.jsMain,
            SSetsKmp.Default.commonMain, SSetsKmp.Default.webMain,
        )

        runAndCheckFiles(
            SSetsKmp.Default.jsTest,
            SSetsKmp.Default.commonMain, SSetsKmp.Default.webMain, SSetsKmp.Default.jsMain,
            SSetsKmp.Default.commonTest, SSetsKmp.Default.webTest
        )

        runAndCheckFiles(
            SSetsKmp.Default.appleMain,
            SSetsKmp.Default.commonMain, SSetsKmp.Default.nativeMain,
        )

        runAndCheckFiles(
            SSetsKmp.Default.appleTest,
            SSetsKmp.Default.commonMain, SSetsKmp.Default.nativeMain, SSetsKmp.Default.appleMain,
            SSetsKmp.Default.commonTest, SSetsKmp.Default.nativeTest
        )

        runAndCheckFiles(
            SSetsKmp.Default.macosMain,
            SSetsKmp.Default.commonMain, SSetsKmp.Default.nativeMain, SSetsKmp.Default.appleMain,
        )

        runAndCheckFiles(
            SSetsKmp.Default.macosTest,
            SSetsKmp.Default.commonMain,
            SSetsKmp.Default.nativeMain,
            SSetsKmp.Default.appleMain,
            SSetsKmp.Default.macosMain,
            SSetsKmp.Default.commonTest,
            SSetsKmp.Default.nativeTest,
            SSetsKmp.Default.appleTest
        )

        runAndCheckFiles(
            SSetsKmp.Default.macosArm64Main,
            SSetsKmp.Default.commonMain,
            SSetsKmp.Default.nativeMain,
            SSetsKmp.Default.appleMain,
            SSetsKmp.Default.macosMain,
        )

        runAndCheckFiles(
            SSetsKmp.Default.macosArm64Test,
            SSetsKmp.Default.commonMain,
            SSetsKmp.Default.nativeMain,
            SSetsKmp.Default.appleMain,
            SSetsKmp.Default.macosMain,
            SSetsKmp.Default.macosArm64Main,
            SSetsKmp.Default.commonTest,
            SSetsKmp.Default.nativeTest,
            SSetsKmp.Default.appleTest,
            SSetsKmp.Default.macosTest,
        )
    }

    @TestFactory
    fun `KMP Hierarchy Android KMP Library`() = runGrpcTest {
        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.commonTest,
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.jvmMain,
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.jvmTest,
            SSetsKmp.AndroidKmpLib.commonMain, SSetsKmp.AndroidKmpLib.jvmMain,
            SSetsKmp.AndroidKmpLib.commonTest,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.androidMain,
            SSetsKmp.AndroidKmpLib.commonMain,
        )
    }

    @TestFactory
    fun `KMP Hierarchy Android KMP Library With Test Tasks`() = runGrpcTest(
        versionsPredicate = { testTestsForAndroidKmpLibExist() },
    ) {
        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.commonTest,
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.jvmMain,
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.jvmTest,
            SSetsKmp.AndroidKmpLib.commonMain, SSetsKmp.AndroidKmpLib.jvmMain,
            SSetsKmp.AndroidKmpLib.commonTest,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.androidMain,
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.androidHostTest,
            SSetsKmp.AndroidKmpLib.commonMain, SSetsKmp.AndroidKmpLib.commonTest,
            SSetsKmp.AndroidKmpLib.androidMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.androidDeviceTest,
            SSetsKmp.AndroidKmpLib.commonMain, SSetsKmp.AndroidKmpLib.commonTest,
            SSetsKmp.AndroidKmpLib.androidMain,
        )
    }

    @TestFactory
    fun `KMP Hierarchy Android KMP Library With Test Tasks Not Wired`() = runGrpcTest(
        versionsPredicate = { testTestsForAndroidKmpLibExist() },
    ) {
        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.commonTest,
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.jvmMain,
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.jvmTest,
            SSetsKmp.AndroidKmpLib.commonMain, SSetsKmp.AndroidKmpLib.jvmMain,
            SSetsKmp.AndroidKmpLib.commonTest,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.androidMain,
            SSetsKmp.AndroidKmpLib.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.androidHostTest,
            SSetsKmp.AndroidKmpLib.commonMain, SSetsKmp.AndroidKmpLib.commonTest,
            SSetsKmp.AndroidKmpLib.androidMain,
        )

        runAndCheckFiles(
            SSetsKmp.AndroidKmpLib.androidDeviceTest,
            SSetsKmp.AndroidKmpLib.commonMain, SSetsKmp.AndroidKmpLib.androidMain,
        )
    }

    @TestFactory
    fun `KMP Hierarchy Legacy Android`() = runGrpcTest {
        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.commonTest,
            SSetsKmp.LegacyAndroid.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.jvmMain,
            SSetsKmp.LegacyAndroid.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.jvmTest,
            SSetsKmp.LegacyAndroid.commonMain, SSetsKmp.LegacyAndroid.jvmMain,
            SSetsKmp.LegacyAndroid.commonTest,
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidDebug,
            SSetsKmp.LegacyAndroid.commonMain,
            extended = listOf(
                SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain,
                SSetsKmp.LegacyAndroid.debug,
            ),
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidRelease,
            SSetsKmp.LegacyAndroid.commonMain,
            extended = listOf(
                SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain,
                SSetsKmp.LegacyAndroid.release,
            )
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidUnitTestDebug,
            SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain,  SSetsKmp.LegacyAndroid.commonMain,
            SSetsKmp.LegacyAndroid.debug, SSetsKmp.LegacyAndroid.androidDebug,
            SSetsKmp.LegacyAndroid.commonTest,
            extended = listOf(
                SSetsKmp.LegacyAndroid.test,
                SSetsKmp.LegacyAndroid.androidUnitTest,
                SSetsKmp.LegacyAndroid.testDebug,
                SSetsKmp.LegacyAndroid.testFixtures,
                SSetsKmp.LegacyAndroid.testFixturesDebug,
            )
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidUnitTestRelease,
            SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain, SSetsKmp.LegacyAndroid.commonMain,
            SSetsKmp.LegacyAndroid.release, SSetsKmp.LegacyAndroid.androidRelease,
            SSetsKmp.LegacyAndroid.commonTest,
            extended = listOf(
                SSetsKmp.LegacyAndroid.test,
                SSetsKmp.LegacyAndroid.androidUnitTest,
                SSetsKmp.LegacyAndroid.testRelease,
                SSetsKmp.LegacyAndroid.testFixtures,
                SSetsKmp.LegacyAndroid.testFixturesRelease,
            )
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidInstrumentedTestDebug,
            SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain, SSetsKmp.LegacyAndroid.commonMain,
            SSetsKmp.LegacyAndroid.debug, SSetsKmp.LegacyAndroid.androidDebug,
            SSetsKmp.LegacyAndroid.commonTest,
            extended = listOf(
                SSetsKmp.LegacyAndroid.androidTest,
                SSetsKmp.LegacyAndroid.androidInstrumentedTest,
                SSetsKmp.LegacyAndroid.androidTestDebug,
                SSetsKmp.LegacyAndroid.testFixtures,
                SSetsKmp.LegacyAndroid.testFixturesDebug,
            )
        )
    }

    @TestFactory
    fun `KMP Hierarchy Legacy Android Not Wired`() = runGrpcTest {
        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.commonTest,
            SSetsKmp.LegacyAndroid.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.jvmMain,
            SSetsKmp.LegacyAndroid.commonMain,
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.jvmTest,
            SSetsKmp.LegacyAndroid.commonMain, SSetsKmp.LegacyAndroid.jvmMain,
            SSetsKmp.LegacyAndroid.commonTest,
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidDebug,
            SSetsKmp.LegacyAndroid.commonMain,
            extended = listOf(
                SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain,
                SSetsKmp.LegacyAndroid.debug,
            ),
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidRelease,
            SSetsKmp.LegacyAndroid.commonMain,
            extended = listOf(
                SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain,
                SSetsKmp.LegacyAndroid.release,
            )
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidUnitTestDebug,
            SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain,  SSetsKmp.LegacyAndroid.commonMain,
            SSetsKmp.LegacyAndroid.debug, SSetsKmp.LegacyAndroid.androidDebug,
            SSetsKmp.LegacyAndroid.commonTest,
            extended = listOf(
                SSetsKmp.LegacyAndroid.test,
                SSetsKmp.LegacyAndroid.androidUnitTest,
                SSetsKmp.LegacyAndroid.testDebug,
                SSetsKmp.LegacyAndroid.testFixtures,
                SSetsKmp.LegacyAndroid.testFixturesDebug,
            )
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidUnitTestRelease,
            SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain, SSetsKmp.LegacyAndroid.commonMain,
            SSetsKmp.LegacyAndroid.release, SSetsKmp.LegacyAndroid.androidRelease,
            SSetsKmp.LegacyAndroid.commonTest,
            extended = listOf(
                SSetsKmp.LegacyAndroid.test,
                SSetsKmp.LegacyAndroid.androidUnitTest,
                SSetsKmp.LegacyAndroid.testRelease,
                SSetsKmp.LegacyAndroid.testFixtures,
                SSetsKmp.LegacyAndroid.testFixturesRelease,
            )
        )

        runAndCheckFiles(
            SSetsKmp.LegacyAndroid.androidInstrumentedTestDebug,
            SSetsKmp.LegacyAndroid.main, SSetsKmp.LegacyAndroid.androidMain, SSetsKmp.LegacyAndroid.commonMain,
            SSetsKmp.LegacyAndroid.debug, SSetsKmp.LegacyAndroid.androidDebug,
            extended = listOf(
                SSetsKmp.LegacyAndroid.androidTest,
                SSetsKmp.LegacyAndroid.androidInstrumentedTest,
                SSetsKmp.LegacyAndroid.androidTestDebug,
                SSetsKmp.LegacyAndroid.testFixtures,
                SSetsKmp.LegacyAndroid.testFixturesDebug,
            )
        )
    }

    @TestFactory
    fun `Proto Tasks Are Cached Properly`() = runGrpcTest {
        val firstRunCommonMain = runForSet(SSetsKmp.Default.commonMain)

        firstRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.commonTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.nativeMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.nativeTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.jvmMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.jvmTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.webMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.webTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.jsMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.jsTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.appleMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.appleTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.macosMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.macosTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.macosArm64Main)
        firstRunCommonMain.assertOutcomes(SSetsKmp.Default.macosArm64Test)

        val secondRunCommonMain = runForSet(SSetsKmp.Default.commonMain)

        secondRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        cleanProtoBuildDir()

        val thirdRunCommonMain = runForSet(SSetsKmp.Default.commonMain)

        thirdRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        SSetsKmp.Default.commonMain.sourceDir()
            .resolve("commonMain.proto")
            .replace("content = 1", "content = 2")

        val fourthRunCommonMain = runForSet(SSetsKmp.Default.commonMain)

        fourthRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        val firstRunMacosArm64Main = runForSet(SSetsKmp.Default.macosArm64Main)

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.nativeMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.appleMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosArm64Main,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.nativeTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.jvmMain)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.jvmTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.webMain)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.webTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.jsMain)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.jsTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.appleTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.macosTest)
        firstRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.macosArm64Test)

        val firstRunMacosArm64Test = runForSet(SSetsKmp.Default.macosArm64Test)

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.nativeMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.appleMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosArm64Main,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.nativeTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.appleTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        firstRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosArm64Test,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.jvmMain)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.jvmTest)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.webMain)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.webTest)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.jsMain)
        firstRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.jsTest)

        SSetsKmp.Default.macosMain.sourceDir()
            .resolve("macosMain.proto")
            .replace("content = 1", "content = 2")

        val fifthRunCommonMain = runForSet(SSetsKmp.Default.commonMain)

        fifthRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.commonTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.nativeMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.nativeTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.jvmMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.jvmTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.webMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.webTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.jsMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.jsTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.appleMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.appleTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.macosMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.macosTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.macosArm64Main)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.Default.macosArm64Test)

        val secondRunMacosArm64Main = runForSet(SSetsKmp.Default.macosArm64Main)

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.nativeMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.appleMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Main.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosArm64Main,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.nativeTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.jvmMain)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.jvmTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.webMain)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.webTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.jsMain)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.jsTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.appleTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.macosTest)
        secondRunMacosArm64Main.assertOutcomes(SSetsKmp.Default.macosArm64Test)

        val secondRunMacosArm64Test = runForSet(SSetsKmp.Default.macosArm64Test)

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.nativeMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.appleMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosArm64Main,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.nativeTest,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.appleTest,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        secondRunMacosArm64Test.assertOutcomes(
            sourceSet = SSetsKmp.Default.macosArm64Test,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.jvmMain)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.jvmTest)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.webMain)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.webTest)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.jsMain)
        secondRunMacosArm64Test.assertOutcomes(SSetsKmp.Default.jsTest)

        val firstRunJvmMain = runForSet(SSetsKmp.Default.jvmMain)

        firstRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.Default.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.commonTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.nativeMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.nativeTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.jvmTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.webMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.webTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.jsMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.jsTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.appleMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.appleTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.macosMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.macosTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.macosArm64Main)
        firstRunJvmMain.assertOutcomes(SSetsKmp.Default.macosArm64Test)

        SSetsKmp.Default.jvmMain.sourceDir()
            .resolve("jvmMain.proto")
            .replace("content = 1", "content = 2")

        val secondRunJvmMain = runForSet(SSetsKmp.Default.jvmMain)

        secondRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.Default.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        secondRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.Default.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        // didn't run
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.commonTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.nativeMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.nativeTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.jvmTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.webMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.webTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.jsMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.jsTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.appleMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.appleTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.macosMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.macosTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.macosArm64Main)
        secondRunJvmMain.assertOutcomes(SSetsKmp.Default.macosArm64Test)
    }

    @TestFactory
    fun `Proto Tasks Are Cached Properly Android KMP Library`() = runGrpcTest(
        versionsPredicate = { testTestsForAndroidKmpLibExist() }
    ) {
        val firstRunCommonMain = runForSet(SSetsKmp.AndroidKmpLib.commonMain)

        firstRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        firstRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.commonTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.jvmMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.jvmTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidHostTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidDeviceTest)

        val secondRunCommonMain = runForSet(SSetsKmp.AndroidKmpLib.commonMain)

        secondRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        cleanProtoBuildDir()

        val thirdRunCommonMain = runForSet(SSetsKmp.AndroidKmpLib.commonMain)

        thirdRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        SSetsKmp.AndroidKmpLib.commonMain.sourceDir()
            .resolve("commonMain.proto")
            .replace("content = 1", "content = 2")

        val fourthRunCommonMain = runForSet(SSetsKmp.AndroidKmpLib.commonMain)

        fourthRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        SSetsKmp.AndroidKmpLib.androidMain.sourceDir()
            .resolve("androidMain.proto")
            .replace("content = 1", "content = 2")

        val fifthRunCommonMain = runForSet(SSetsKmp.AndroidKmpLib.commonMain)

        fifthRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        fifthRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.commonTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.jvmMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.jvmTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidHostTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidDeviceTest)

        val firstRunAndroidMain = runForSet(SSetsKmp.AndroidKmpLib.androidMain)

        firstRunAndroidMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.androidMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        val firstRunAndroidHostTest = runForSet(SSetsKmp.AndroidKmpLib.androidHostTest)

        firstRunAndroidHostTest.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.androidMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunAndroidHostTest.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.androidHostTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        SSetsKmp.AndroidKmpLib.androidHostTest.sourceDir()
            .resolve("androidHostTest.proto")
            .replace("content = 1", "content = 2")

        val secondRunAndroidHostTest = runForSet(SSetsKmp.AndroidKmpLib.androidHostTest)

        secondRunAndroidHostTest.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.androidMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunAndroidHostTest.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.androidHostTest,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        val firstRunJvmMain = runForSet(SSetsKmp.AndroidKmpLib.jvmMain)

        firstRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.commonTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.jvmTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidMain)
        firstRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidHostTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidDeviceTest)

        SSetsKmp.AndroidKmpLib.jvmMain.sourceDir()
            .resolve("jvmMain.proto")
            .replace("content = 1", "content = 2")

        val secondRunJvmMain = runForSet(SSetsKmp.AndroidKmpLib.jvmMain)

        secondRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        secondRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.AndroidKmpLib.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        // didn't run
        secondRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.commonTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.jvmTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidMain)
        secondRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidHostTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.AndroidKmpLib.androidDeviceTest)
    }

    @TestFactory
    fun `Proto Tasks Are Cached Properly Legacy Android`() = runGrpcTest {
        val firstRunCommonMain = runForSet(SSetsKmp.LegacyAndroid.commonMain)

        firstRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        firstRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.commonTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.jvmMain)
        firstRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.jvmTest)
        firstRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidDebug)
        firstRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidRelease)
        firstRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidUnitTestDebug)
        firstRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidUnitTestRelease)
        firstRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidInstrumentedTestDebug)

        val secondRunCommonMain = runForSet(SSetsKmp.LegacyAndroid.commonMain)

        secondRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        cleanProtoBuildDir()

        val thirdRunCommonMain = runForSet(SSetsKmp.LegacyAndroid.commonMain)

        thirdRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        SSetsKmp.LegacyAndroid.commonMain.sourceDir()
            .resolve("commonMain.proto")
            .replace("content = 1", "content = 2")

        val fourthRunCommonMain = runForSet(SSetsKmp.LegacyAndroid.commonMain)

        fourthRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.commonMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        SSetsKmp.LegacyAndroid.androidMain.sourceDir()
            .resolve("androidMain.proto")
            .replace("content = 1", "content = 2")

        val fifthRunCommonMain = runForSet(SSetsKmp.LegacyAndroid.commonMain)

        fifthRunCommonMain.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        fifthRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.commonTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.jvmMain)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.jvmTest)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidDebug)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidRelease)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidUnitTestDebug)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidUnitTestRelease)
        fifthRunCommonMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidInstrumentedTestDebug)

        val firstRunAndroidDebug = runForSet(SSetsKmp.LegacyAndroid.androidDebug)

        firstRunAndroidDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        SSetsKmp.LegacyAndroid.main.sourceDir()
            .resolve("main.proto")
            .replace("content = 1", "content = 2")

        val secondRunAndroidDebug = runForSet(SSetsKmp.LegacyAndroid.androidDebug)

        secondRunAndroidDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        SSetsKmp.LegacyAndroid.debug.sourceDir()
            .resolve("debug.proto")
            .replace("content = 1", "content = 2")

        val thirdRunAndroidDebug = runForSet(SSetsKmp.LegacyAndroid.androidDebug)

        thirdRunAndroidDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        val firstRunAndroidUnitTestDebug = runForSet(SSetsKmp.LegacyAndroid.androidUnitTestDebug)

        firstRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidDebug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        firstRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidUnitTestDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        SSetsKmp.LegacyAndroid.androidUnitTestDebug.sourceDir()
            .resolve("androidUnitTestDebug.proto")
            .replace("content = 1", "content = 2")

        val secondRunAndroidUnitTestDebug = runForSet(SSetsKmp.LegacyAndroid.androidUnitTestDebug)

        secondRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidDebug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        secondRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidUnitTestDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        SSetsKmp.LegacyAndroid.testFixtures.sourceDir()
            .resolve("testFixtures.proto")
            .replace("content = 1", "content = 2")

        val thirdRunAndroidUnitTestDebug = runForSet(SSetsKmp.LegacyAndroid.androidUnitTestDebug)

        thirdRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidDebug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        thirdRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidUnitTestDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        SSetsKmp.LegacyAndroid.testFixturesRelease.sourceDir()
            .resolve("testFixturesRelease.proto")
            .replace("content = 1", "content = 2")

        val fourthRunAndroidUnitTestDebug = runForSet(SSetsKmp.LegacyAndroid.androidUnitTestDebug)

        fourthRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidDebug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        fourthRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidUnitTestDebug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        SSetsKmp.LegacyAndroid.test.sourceDir()
            .resolve("test.proto")
            .replace("content = 1", "content = 2")

        val fifthRunAndroidUnitTestDebug = runForSet(SSetsKmp.LegacyAndroid.androidUnitTestDebug)

        fifthRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidDebug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        fifthRunAndroidUnitTestDebug.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.androidUnitTestDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        val firstRunJvmMain = runForSet(SSetsKmp.LegacyAndroid.jvmMain)

        firstRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.commonTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.jvmTest)
        firstRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidDebug)
        firstRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidRelease)
        firstRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidUnitTestDebug)
        firstRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidUnitTestRelease)
        firstRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidInstrumentedTestDebug)

        SSetsKmp.LegacyAndroid.jvmMain.sourceDir()
            .resolve("jvmMain.proto")
            .replace("content = 1", "content = 2")

        val secondRunJvmMain = runForSet(SSetsKmp.LegacyAndroid.jvmMain)

        secondRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.commonMain,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        secondRunJvmMain.assertOutcomes(
            sourceSet = SSetsKmp.LegacyAndroid.jvmMain,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        // didn't run
        secondRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.commonTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.jvmTest)
        secondRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidDebug)
        secondRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidRelease)
        secondRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidUnitTestDebug)
        secondRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidUnitTestRelease)
        secondRunJvmMain.assertOutcomes(SSetsKmp.LegacyAndroid.androidInstrumentedTestDebug)
    }

    @TestFactory
    fun `Buf Tasks`() = runGrpcTest {
        runGradle("test_tasks", "--no-configuration-cache")
    }

    @TestFactory
    fun `Buf Tasks Android Kmp Library`() = runGrpcTest {
        runGradle("test_tasks", "--no-configuration-cache")
    }

    @TestFactory
    fun `Buf Tasks Android Kmp Library With Test Tasks`() = runGrpcTest(
        versionsPredicate = { testTestsForAndroidKmpLibExist() }
    ) {
        runGradle("test_tasks", "--no-configuration-cache")
    }

    @TestFactory
    fun `Buf Tasks Legacy Android`() = runGrpcTest {
        runGradle("test_tasks", "--no-configuration-cache")
    }
}
