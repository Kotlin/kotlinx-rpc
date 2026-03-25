/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("RemoveRedundantBackticks")

package kotlinx.rpc

import kotlinx.rpc.base.GrpcBaseTest
import kotlinx.rpc.base.isAgp9
import kotlinx.rpc.protoc.PlatformOption
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import kotlin.io.path.Path

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GrpcAndroidProjectTest : GrpcBaseTest() {
    override val type: Type = Type.Android

    @TestFactory
    fun `Minimal gRPC Configuration`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        minimalGrpcConfigurationBody()
    }

    @TestFactory
    fun `Minimal gRPC Configuration No KGP`() = runGrpcTest(versionsPredicate = { isAgp9 }) {
        minimalGrpcConfigurationBody()
    }

    @TestFactory
    fun `Minimal gRPC Configuration Library`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        minimalGrpcConfigurationBody()
    }

    @TestFactory
    fun `Minimal gRPC Configuration Library No KGP`() = runGrpcTest(versionsPredicate = { isAgp9 }) {
        minimalGrpcConfigurationBody()
    }

    // com.android.dynamic-feature is removed in AGP 9.0
    @TestFactory
    fun `Minimal gRPC Configuration Dynamic Feature`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        minimalGrpcConfigurationBody()
    }

    // com.android.test is removed in AGP 9.0
    @TestFactory
    fun `Minimal gRPC Configuration Test`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        minimalGrpcConfigurationTestBody()
    }

    private fun GrpcTestEnv.minimalGrpcConfigurationBody() {
        fun runForSetOnlyMain(sourceSet: SSets, vararg extraTasks: SSetsAndroid) {
            val result = runForSet(sourceSet)

            result.assertTaskExecuted(
                sourceSet = sourceSet,
                protoFiles = listOf(
                    Path("some.proto"),
                    Path("ok", "ok.proto"),
                ),
                importProtoFiles = emptyList(),
                generatedFiles = listOf(
                    Path("Some.kt"),
                    Path("Some.ext.kt"),
                    Path(RPC_INTERNAL, "Some.kt"),
                    Path("ok", "Ok.kt"),
                    Path("ok", "Ok.ext.kt"),
                    Path("ok", RPC_INTERNAL, "Ok.kt"),
                ),
                notExecuted = SSetsAndroid.Default.entries - extraTasks.toSet() - sourceSet,
            )
        }

        fun runForSetOnlyTest(sourceSet: SSets, vararg extraTasks: SSetsAndroid) {
            val result = runForSet(sourceSet)

            result.assertTaskExecuted(
                sourceSet = sourceSet,
                protoFiles = emptyList(),
                importProtoFiles = listOf(
                    Path("some.proto"),
                    Path("ok", "ok.proto"),
                ),
                generatedFiles = emptyList(),
                notExecuted = SSetsAndroid.Default.entries - extraTasks.toSet() - sourceSet,
            )
        }

        runForSetOnlyMain(SSetsAndroid.Default.debug)
        runForSetOnlyMain(SSetsAndroid.Default.release)
        runForSetOnlyTest(SSetsAndroid.Default.testDebug, SSetsAndroid.Default.debug)
        runForSetOnlyTest(SSetsAndroid.Default.testRelease, SSetsAndroid.Default.release)
        runForSetOnlyTest(SSetsAndroid.Default.androidTestDebug, SSetsAndroid.Default.debug)
    }

    private fun GrpcTestEnv.minimalGrpcConfigurationTestBody() {
        fun runForSetOnlyMain(sourceSet: SSets, vararg extraTasks: SSetsAndroid) {
            val result = runForSet(sourceSet)

            result.assertTaskExecuted(
                sourceSet = sourceSet,
                protoFiles = listOf(
                    Path("some.proto"),
                    Path("ok", "ok.proto"),
                ),
                importProtoFiles = emptyList(),
                generatedFiles = listOf(
                    Path("Some.kt"),
                    Path("Some.ext.kt"),
                    Path(RPC_INTERNAL, "Some.kt"),
                    Path("ok", "Ok.kt"),
                    Path("ok", "Ok.ext.kt"),
                    Path("ok", RPC_INTERNAL, "Ok.kt"),
                ),
                notExecuted = SSetsAndroid.Test.entries - extraTasks.toSet() - sourceSet,
            )
        }

        runForSetOnlyMain(SSetsAndroid.Test.debug)

        SSetsAndroid.Default.entries.forEach {
            if (it != SSetsAndroid.Default.debug) {
                runNonExistentTasksForSourceSet(it)
            }
        }
    }

    @TestFactory
    fun `Test-Only Sources`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        testOnlySourcesBody()
    }

    @TestFactory
    fun `Test-Only Sources No KGP`() = runGrpcTest(versionsPredicate = { isAgp9 }) {
        testOnlySourcesBody()
    }

    private fun GrpcTestEnv.testOnlySourcesBody() {
        fun runForSetOnlyMain(sourceSet: SSets, vararg extraTasks: SSetsAndroid) {
            val result = runForSet(sourceSet)

            result.assertTaskExecuted(
                sourceSet = sourceSet,
                protoFiles = emptyList(),
                importProtoFiles = emptyList(),
                generatedFiles = emptyList(),
                notExecuted = SSetsAndroid.Default.entries - extraTasks.toSet() - sourceSet,
            )
        }

        fun runForSetOnlyTest(sourceSet: SSets, vararg extraTasks: SSetsAndroid) {
            val result = runForSet(sourceSet)

            result.assertTaskExecuted(
                sourceSet = sourceSet,
                protoFiles = listOf(
                    Path("some.proto"),
                    Path("ok", "ok.proto"),
                ),
                importProtoFiles = emptyList(),
                generatedFiles = listOf(
                    Path("Some.kt"),
                    Path("Some.ext.kt"),
                    Path(RPC_INTERNAL, "Some.kt"),
                    Path("ok", "Ok.kt"),
                    Path("ok", "Ok.ext.kt"),
                    Path("ok", RPC_INTERNAL, "Ok.kt"),
                ),
                notExecuted = SSetsAndroid.Default.entries - extraTasks.toSet() - sourceSet,
            )
        }

        runForSetOnlyMain(SSetsAndroid.Default.debug)
        runForSetOnlyMain(SSetsAndroid.Default.release)
        runForSetOnlyTest(SSetsAndroid.Default.testDebug, SSetsAndroid.Default.debug)
        runForSetOnlyTest(SSetsAndroid.Default.testRelease, SSetsAndroid.Default.release)
        runForSetOnlyTest(SSetsAndroid.Default.androidTestDebug, SSetsAndroid.Default.debug)
    }

    @TestFactory
    fun `All Default Source Sets`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        allDefaultSourceSetsBody()
    }

    @TestFactory
    fun `All Default Source Sets No KGP`() = runGrpcTest(versionsPredicate = { isAgp9 }) {
        // AGP 9.0 built-in Kotlin only creates unit test compilation for testBuildType (debug),
        // so testRelease has no compile task, and we skip it here.
        allDefaultSourceSetsBody(includeTestRelease = false)
    }

    private fun GrpcTestEnv.allDefaultSourceSetsBody(includeTestRelease: Boolean = true) {
        runAndCheckFiles(
            SSetsAndroid.Default.debug,
            extended = listOf(SSetsAndroid.Default.main),
        )
        runAndCheckFiles(
            SSetsAndroid.Default.release,
            extended = listOf(SSetsAndroid.Default.main)
        )
        runAndCheckFiles(
            SSetsAndroid.Default.testDebug,
            SSetsAndroid.Default.debug, SSetsAndroid.Default.main,
            extended = listOf(
                SSetsAndroid.Default.test,
                SSetsAndroid.Default.testFixtures,
                SSetsAndroid.Default.testFixturesDebug,
            )
        )
        if (includeTestRelease) {
            runAndCheckFiles(
                SSetsAndroid.Default.testRelease,
                SSetsAndroid.Default.release, SSetsAndroid.Default.main,
                extended = listOf(
                    SSetsAndroid.Default.test,
                    SSetsAndroid.Default.testFixtures,
                    SSetsAndroid.Default.testFixturesRelease,
                )
            )
        }
        runAndCheckFiles(
            SSetsAndroid.Default.androidTestDebug,
            SSetsAndroid.Default.debug, SSetsAndroid.Default.main,
            extended = listOf(
                SSetsAndroid.Default.androidTest,
                SSetsAndroid.Default.testFixtures,
                SSetsAndroid.Default.testFixturesDebug,
            )
        )
    }

    @TestFactory
    fun `No gRPC`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        SSetsAndroid.Default.entries.forEach {
            runNonExistentTasksForSourceSet(it)
        }
    }

    @TestFactory
    fun `No gRPC No KGP`() = runGrpcTest(versionsPredicate = { isAgp9 }) {
        SSetsAndroid.Default.entries.forEach {
            runNonExistentTasksForSourceSet(it)
        }
    }

    @TestFactory
    fun `Proto Tasks Are Cached Properly`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        protoTasksAreCachedProperlyBody()
    }

    @TestFactory
    fun `Proto Tasks Are Cached Properly No KGP`() = runGrpcTest(
        versionsPredicate = { isAgp9 },
    ) {
        protoTasksAreCachedProperlyBody()
    }

    private fun GrpcTestEnv.protoTasksAreCachedProperlyBody() {
        val firstRunDebug = runForSet(SSetsAndroid.Default.debug)

        firstRunDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.debug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.release)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.androidTestDebug)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.testDebug)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.testRelease)

        val secondRunDebug = runForSet(SSetsAndroid.Default.debug)

        secondRunDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.debug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        cleanProtoBuildDir()

        val thirdRunDebug = runForSet(SSetsAndroid.Default.debug)

        thirdRunDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.debug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        SSetsAndroid.Default.debug.sourceDir()
            .resolve("debug.proto")
            .replace("content = 1", "content = 2")

        val fourthRunDebug = runForSet(SSetsAndroid.Default.debug)

        fourthRunDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.debug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        val firstRunTestDebug = runForSet(SSetsAndroid.Default.testDebug)

        firstRunTestDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.debug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunTestDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.testDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.release)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.androidTestDebug)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.testRelease)

        val firstRunAndroidTestDebug = runForSet(SSetsAndroid.Default.androidTestDebug)

        firstRunAndroidTestDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.debug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        firstRunAndroidTestDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.androidTestDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.SUCCESS,
            bufGenYaml = TaskOutcome.SUCCESS,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.SUCCESS,
        )

        // didn't run
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.release)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.testDebug)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.testRelease)

        SSetsAndroid.Default.testDebug.sourceDir()
            .resolve("testDebug.proto")
            .replace("content = 1", "content = 2")

        val fifthRunDebug = runForSet(SSetsAndroid.Default.debug)

        fifthRunDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.debug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        // didn't run
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.release)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.androidTestDebug)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.testDebug)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.testRelease)

        val secondRunTestDebug = runForSet(SSetsAndroid.Default.testDebug)

        secondRunTestDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.debug,
            generate = TaskOutcome.UP_TO_DATE,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.UP_TO_DATE,
            protoFilesImports = TaskOutcome.NO_SOURCE,
        )

        secondRunTestDebug.assertOutcomes(
            sourceSet = SSetsAndroid.Default.testDebug,
            generate = TaskOutcome.SUCCESS,
            bufYaml = TaskOutcome.UP_TO_DATE,
            bufGenYaml = TaskOutcome.UP_TO_DATE,
            protoFiles = TaskOutcome.SUCCESS,
            protoFilesImports = TaskOutcome.UP_TO_DATE,
        )

        // didn't run
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.release)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.androidTestDebug)
        firstRunDebug.assertOutcomes(SSetsAndroid.Default.testRelease)
    }

    @TestFactory
    fun `Android Platform Option`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        runPlatformOptionTest(SSetsAndroid.Default.debug, PlatformOption.ANDROID)
        runPlatformOptionTest(SSetsAndroid.Default.testDebug, PlatformOption.ANDROID)
    }

    @TestFactory
    fun `Android Platform Option No KGP`() = runGrpcTest(versionsPredicate = { isAgp9 }) {
        runPlatformOptionTest(SSetsAndroid.Default.debug, PlatformOption.ANDROID)
        runPlatformOptionTest(SSetsAndroid.Default.testDebug, PlatformOption.ANDROID)
    }

    @TestFactory
    fun `Buf Tasks Default`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        runGradle("test_tasks", "--no-configuration-cache")
    }

    @TestFactory
    fun `Buf Tasks Default No KGP`() = runGrpcTest(versionsPredicate = { isAgp9 }) {
        runGradle("test_tasks", "--no-configuration-cache")
    }

    @TestFactory
    fun `Buf Tasks Extended`() = runGrpcTest(versionsPredicate = { !isAgp9 }) {
        runGradle("test_tasks", "--no-configuration-cache")
    }

    @TestFactory
    fun `Buf Tasks Extended No KGP`() = runGrpcTest(versionsPredicate = { isAgp9 }) {
        runGradle("test_tasks", "--no-configuration-cache")
    }

    @TestFactory
    fun `PreBuild`() = runGrpcTest(
        versionsPredicate = { !isAgp9 },
    ) {
        dryRunAndroidPreBuild(SSetsAndroid.Default.debug)
        dryRunAndroidPreBuild(SSetsAndroid.Default.release)
        dryRunAndroidPreBuild(SSetsAndroid.Default.testDebug)
        dryRunAndroidPreBuild(SSetsAndroid.Default.testRelease)
        dryRunAndroidPreBuild(SSetsAndroid.Default.androidTestDebug)
    }

    @TestFactory
    fun `PreBuild No KGP`() = runGrpcTest(
        versionsPredicate = { isAgp9 },
    ) {
        dryRunAndroidPreBuild(SSetsAndroid.Default.debug)
        dryRunAndroidPreBuild(SSetsAndroid.Default.release)
        dryRunAndroidPreBuild(SSetsAndroid.Default.testDebug)
        dryRunAndroidPreBuild(SSetsAndroid.Default.androidTestDebug)
    }

    @TestFactory
    fun `PreBuild Library`() = runGrpcTest(
        versionsPredicate = { !isAgp9 },
    ) {
        dryRunAndroidPreBuild(SSetsAndroid.Default.debug)
        dryRunAndroidPreBuild(SSetsAndroid.Default.release)
        dryRunAndroidPreBuild(SSetsAndroid.Default.testDebug)
        dryRunAndroidPreBuild(SSetsAndroid.Default.testRelease)
        dryRunAndroidPreBuild(SSetsAndroid.Default.androidTestDebug)
    }

    @TestFactory
    fun `PreBuild Library No KGP`() = runGrpcTest(
        versionsPredicate = { isAgp9 },
    ) {
        dryRunAndroidPreBuild(SSetsAndroid.Default.debug)
        dryRunAndroidPreBuild(SSetsAndroid.Default.release)
        dryRunAndroidPreBuild(SSetsAndroid.Default.testDebug)
        dryRunAndroidPreBuild(SSetsAndroid.Default.androidTestDebug)
    }
}
