/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.buf.*
import kotlinx.rpc.buf.tasks.*
import kotlinx.rpc.protoc.*
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.version
import javax.inject.Inject

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
    id("com.android.library") version "<android-version>"
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34
}

kotlin {
    jvm()
    macosArm64()
    androidTarget()
}

public abstract class BufLintTask @Inject constructor(properties: ProtoTask.Properties) : BufExecTask(properties) {
    init {
        command.set("lint")
        args.set(emptyList())
    }
}

rpc {
    protoc {
        buf {
            tasks {
                registerWorkspaceTask<BufLintTask>("lint")
            }
        }
    }
}

fun Iterable<DefaultTask>.toNames() = map { it.name }.toSet()

fun assertTasks(
    tag: String,
    tasks: Iterable<DefaultTask>,
    vararg expected: String?,
) {
    val names = tasks.toNames()
    val expectedSet = expected.filterNotNull().toSet()
    if (expectedSet != names) {
        throw GradleException(
            """
                [$tag] Expected: ${expectedSet}, actual: $names
                Missing: ${expectedSet - names}
                Extra: ${names - expectedSet}
            """.trimIndent()
        )
    }
}

tasks.register("test_tasks") {
    doLast {
        val genTasks = protoTasks.buf.generate

        assertTasks(
            "gen all", genTasks,
            "bufGenerateCommonMain", "bufGenerateCommonTest",
            "bufGenerateNativeMain", "bufGenerateNativeTest",
            "bufGenerateAppleMain", "bufGenerateAppleTest",
            "bufGenerateMacosMain", "bufGenerateMacosTest",
            "bufGenerateMacosArm64Main", "bufGenerateMacosArm64Test",
            "bufGenerateJvmMain", "bufGenerateJvmTest",

            // android
            "bufGenerateAndroidDebug",
            "bufGenerateAndroidRelease",
            "bufGenerateAndroidInstrumentedTestDebug",
            "bufGenerateAndroidUnitTestDebug",
            "bufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            "bufGenerateCommonTest",
            "bufGenerateNativeTest",
            "bufGenerateAppleTest",
            "bufGenerateMacosTest",
            "bufGenerateMacosArm64Test",
            "bufGenerateJvmTest",

            // android
            "bufGenerateAndroidInstrumentedTestDebug",
            "bufGenerateAndroidUnitTestDebug",
            "bufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            "bufGenerateCommonMain",
            "bufGenerateNativeMain",
            "bufGenerateAppleMain",
            "bufGenerateMacosMain",
            "bufGenerateMacosArm64Main",
            "bufGenerateJvmMain",

            // android
            "bufGenerateAndroidDebug",
            "bufGenerateAndroidRelease",
        )

        assertTasks(
            "unit test tasks", genTasks.androidUnitTestTasks(),
            "bufGenerateAndroidUnitTestDebug",
            "bufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "instrumented test tasks", genTasks.androidInstrumentedTestTasks(),
            "bufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())

        assertTasks(
            "matchingSourceSet main", genTasks.matchingSourceSet("main"),
            "bufGenerateAndroidDebug",
            "bufGenerateAndroidRelease",
        )

        assertTasks(
            "matchingSourceSet test", genTasks.matchingSourceSet("test"),
            "bufGenerateAndroidUnitTestDebug",
            "bufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "matchingSourceSet androidUnitTest", genTasks.matchingSourceSet("androidUnitTest"),
            "bufGenerateAndroidUnitTestDebug",
            "bufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "matchingSourceSet androidInstrumentedTest", genTasks.matchingSourceSet("androidInstrumentedTest"),
            "bufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidTest", genTasks.matchingSourceSet("androidTest"),
            "bufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks(
            "gen all android", genTasks.androidTasks(),
            "bufGenerateAndroidDebug",
            "bufGenerateAndroidRelease",
            "bufGenerateAndroidInstrumentedTestDebug",
            "bufGenerateAndroidUnitTestDebug",
            "bufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "matchingSourceSet debug",
            genTasks.matchingSourceSet("debug"),
            "bufGenerateAndroidDebug",
        )

        assertTasks(
            "matchingSourceSet testDebug",
            genTasks.matchingSourceSet("testDebug"),
            "bufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidTestDebug",
            genTasks.matchingSourceSet("androidTestDebug"),
            "bufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidDebug",
            genTasks.matchingSourceSet("androidDebug"),
            "bufGenerateAndroidDebug",
        )

        assertTasks(
            "matchingSourceSet androidUnitTestDebug",
            genTasks.matchingSourceSet("androidUnitTestDebug"),
            "bufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidInstrumentedTestDebug",
            genTasks.matchingSourceSet("androidInstrumentedTestDebug"),
            "bufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks(
            "matchingAndroidFlavor freeapp", genTasks.matchingAndroidFlavor("freeapp"),
        )

        assertTasks(
            "matchingAndroidBuildType debug", genTasks.matchingAndroidBuildType("debug"),
            "bufGenerateAndroidDebug",
            "bufGenerateAndroidInstrumentedTestDebug",
            "bufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingAndroidBuildType release", genTasks.matchingAndroidBuildType("release"),
            "bufGenerateAndroidRelease",
            "bufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "matchingAndroidVariant debug", genTasks.matchingAndroidVariant("debug"),
            "bufGenerateAndroidDebug",
            "bufGenerateAndroidInstrumentedTestDebug",
            "bufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingAndroidBuildType debug unit tests", genTasks.matchingAndroidBuildType("debug").androidUnitTestTasks(),
            "bufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingAndroidBuildType debug android tests", genTasks.matchingAndroidBuildType("debug").androidInstrumentedTestTasks(),
            "bufGenerateAndroidInstrumentedTestDebug",
        )

        val allTasks = protoTasks.buf

        assertTasks(
            "all", allTasks,
            // generate
            "bufGenerateCommonMain", "bufGenerateCommonTest",
            "bufGenerateNativeMain", "bufGenerateNativeTest",
            "bufGenerateAppleMain", "bufGenerateAppleTest",
            "bufGenerateMacosMain", "bufGenerateMacosTest",
            "bufGenerateMacosArm64Main", "bufGenerateMacosArm64Test",
            "bufGenerateJvmMain", "bufGenerateJvmTest",

            // android generate
            "bufGenerateAndroidDebug",
            "bufGenerateAndroidRelease",
            "bufGenerateAndroidInstrumentedTestDebug",
            "bufGenerateAndroidUnitTestDebug",
            "bufGenerateAndroidUnitTestRelease",

            // lint
            "bufLintCommonMain", "bufLintCommonTest",
            "bufLintNativeMain", "bufLintNativeTest",
            "bufLintAppleMain", "bufLintAppleTest",
            "bufLintMacosMain", "bufLintMacosTest",
            "bufLintMacosArm64Main", "bufLintMacosArm64Test",
            "bufLintJvmMain", "bufLintJvmTest",

            // android lint
            "bufLintAndroidDebug",
            "bufLintAndroidRelease",
            "bufLintAndroidInstrumentedTestDebug",
            "bufLintAndroidUnitTestDebug",
            "bufLintAndroidUnitTestRelease",
        )

        assertTasks(
            "all by type generate",
            allTasks.matchingType<BufGenerateTask>(),
            "bufGenerateCommonMain", "bufGenerateCommonTest",
            "bufGenerateNativeMain", "bufGenerateNativeTest",
            "bufGenerateAppleMain", "bufGenerateAppleTest",
            "bufGenerateMacosMain", "bufGenerateMacosTest",
            "bufGenerateMacosArm64Main", "bufGenerateMacosArm64Test",
            "bufGenerateJvmMain", "bufGenerateJvmTest",

            // android generate
            "bufGenerateAndroidDebug",
            "bufGenerateAndroidRelease",
            "bufGenerateAndroidInstrumentedTestDebug",
            "bufGenerateAndroidUnitTestDebug",
            "bufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "all by type lint", allTasks.matchingType<BufLintTask>(),
            "bufLintCommonMain", "bufLintCommonTest",
            "bufLintNativeMain", "bufLintNativeTest",
            "bufLintAppleMain", "bufLintAppleTest",
            "bufLintMacosMain", "bufLintMacosTest",
            "bufLintMacosArm64Main", "bufLintMacosArm64Test",
            "bufLintJvmMain", "bufLintJvmTest",

            // android lint
            "bufLintAndroidDebug",
            "bufLintAndroidRelease",
            "bufLintAndroidInstrumentedTestDebug",
            "bufLintAndroidUnitTestDebug",
            "bufLintAndroidUnitTestRelease",
        )
    }
}
