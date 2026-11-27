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
            "protobufGenerateCommonMain", "protobufGenerateCommonTest",
            "protobufGenerateNativeMain", "protobufGenerateNativeTest",
            "protobufGenerateAppleMain", "protobufGenerateAppleTest",
            "protobufGenerateMacosMain", "protobufGenerateMacosTest",
            "protobufGenerateMacosArm64Main", "protobufGenerateMacosArm64Test",
            "protobufGenerateJvmMain", "protobufGenerateJvmTest",

            // android
            "protobufGenerateAndroidDebug",
            "protobufGenerateAndroidRelease",
            "protobufGenerateAndroidInstrumentedTestDebug",
            "protobufGenerateAndroidUnitTestDebug",
            "protobufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            "protobufGenerateCommonTest",
            "protobufGenerateNativeTest",
            "protobufGenerateAppleTest",
            "protobufGenerateMacosTest",
            "protobufGenerateMacosArm64Test",
            "protobufGenerateJvmTest",

            // android
            "protobufGenerateAndroidInstrumentedTestDebug",
            "protobufGenerateAndroidUnitTestDebug",
            "protobufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            "protobufGenerateCommonMain",
            "protobufGenerateNativeMain",
            "protobufGenerateAppleMain",
            "protobufGenerateMacosMain",
            "protobufGenerateMacosArm64Main",
            "protobufGenerateJvmMain",

            // android
            "protobufGenerateAndroidDebug",
            "protobufGenerateAndroidRelease",
        )

        assertTasks(
            "unit test tasks", genTasks.androidUnitTestTasks(),
            "protobufGenerateAndroidUnitTestDebug",
            "protobufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "instrumented test tasks", genTasks.androidInstrumentedTestTasks(),
            "protobufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())

        assertTasks(
            "matchingSourceSet main", genTasks.matchingSourceSet("main"),
            "protobufGenerateAndroidDebug",
            "protobufGenerateAndroidRelease",
        )

        assertTasks(
            "matchingSourceSet test", genTasks.matchingSourceSet("test"),
            "protobufGenerateAndroidUnitTestDebug",
            "protobufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "matchingSourceSet androidUnitTest", genTasks.matchingSourceSet("androidUnitTest"),
            "protobufGenerateAndroidUnitTestDebug",
            "protobufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "matchingSourceSet androidInstrumentedTest", genTasks.matchingSourceSet("androidInstrumentedTest"),
            "protobufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidTest", genTasks.matchingSourceSet("androidTest"),
            "protobufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks(
            "gen all android", genTasks.androidTasks(),
            "protobufGenerateAndroidDebug",
            "protobufGenerateAndroidRelease",
            "protobufGenerateAndroidInstrumentedTestDebug",
            "protobufGenerateAndroidUnitTestDebug",
            "protobufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "matchingSourceSet debug",
            genTasks.matchingSourceSet("debug"),
            "protobufGenerateAndroidDebug",
        )

        assertTasks(
            "matchingSourceSet testDebug",
            genTasks.matchingSourceSet("testDebug"),
            "protobufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidTestDebug",
            genTasks.matchingSourceSet("androidTestDebug"),
            "protobufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidDebug",
            genTasks.matchingSourceSet("androidDebug"),
            "protobufGenerateAndroidDebug",
        )

        assertTasks(
            "matchingSourceSet androidUnitTestDebug",
            genTasks.matchingSourceSet("androidUnitTestDebug"),
            "protobufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidInstrumentedTestDebug",
            genTasks.matchingSourceSet("androidInstrumentedTestDebug"),
            "protobufGenerateAndroidInstrumentedTestDebug",
        )

        assertTasks(
            "matchingAndroidFlavor freeapp", genTasks.matchingAndroidFlavor("freeapp"),
        )

        assertTasks(
            "matchingAndroidBuildType debug", genTasks.matchingAndroidBuildType("debug"),
            "protobufGenerateAndroidDebug",
            "protobufGenerateAndroidInstrumentedTestDebug",
            "protobufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingAndroidBuildType release", genTasks.matchingAndroidBuildType("release"),
            "protobufGenerateAndroidRelease",
            "protobufGenerateAndroidUnitTestRelease",
        )

        assertTasks(
            "matchingAndroidVariant debug", genTasks.matchingAndroidVariant("debug"),
            "protobufGenerateAndroidDebug",
            "protobufGenerateAndroidInstrumentedTestDebug",
            "protobufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingAndroidBuildType debug unit tests", genTasks.matchingAndroidBuildType("debug").androidUnitTestTasks(),
            "protobufGenerateAndroidUnitTestDebug",
        )

        assertTasks(
            "matchingAndroidBuildType debug android tests", genTasks.matchingAndroidBuildType("debug").androidInstrumentedTestTasks(),
            "protobufGenerateAndroidInstrumentedTestDebug",
        )

        val allTasks = protoTasks.buf

        assertTasks(
            "all", allTasks,
            // generate
            "protobufGenerateCommonMain", "protobufGenerateCommonTest",
            "protobufGenerateNativeMain", "protobufGenerateNativeTest",
            "protobufGenerateAppleMain", "protobufGenerateAppleTest",
            "protobufGenerateMacosMain", "protobufGenerateMacosTest",
            "protobufGenerateMacosArm64Main", "protobufGenerateMacosArm64Test",
            "protobufGenerateJvmMain", "protobufGenerateJvmTest",

            // android generate
            "protobufGenerateAndroidDebug",
            "protobufGenerateAndroidRelease",
            "protobufGenerateAndroidInstrumentedTestDebug",
            "protobufGenerateAndroidUnitTestDebug",
            "protobufGenerateAndroidUnitTestRelease",

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

            // lock
            "bufLockCommonMain", "bufLockCommonTest",
            "bufLockNativeMain", "bufLockNativeTest",
            "bufLockAppleMain", "bufLockAppleTest",
            "bufLockMacosMain", "bufLockMacosTest",
            "bufLockMacosArm64Main", "bufLockMacosArm64Test",
            "bufLockJvmMain", "bufLockJvmTest",

            // android lock
            "bufLockAndroidDebug",
            "bufLockAndroidRelease",
            "bufLockAndroidInstrumentedTestDebug",
            "bufLockAndroidUnitTestDebug",
            "bufLockAndroidUnitTestRelease",
        )

        assertTasks(
            "all by type generate",
            allTasks.matchingType<ProtobufGenerateTask>(),
            "protobufGenerateCommonMain", "protobufGenerateCommonTest",
            "protobufGenerateNativeMain", "protobufGenerateNativeTest",
            "protobufGenerateAppleMain", "protobufGenerateAppleTest",
            "protobufGenerateMacosMain", "protobufGenerateMacosTest",
            "protobufGenerateMacosArm64Main", "protobufGenerateMacosArm64Test",
            "protobufGenerateJvmMain", "protobufGenerateJvmTest",

            // android generate
            "protobufGenerateAndroidDebug",
            "protobufGenerateAndroidRelease",
            "protobufGenerateAndroidInstrumentedTestDebug",
            "protobufGenerateAndroidUnitTestDebug",
            "protobufGenerateAndroidUnitTestRelease",
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

        assertTasks(
            "all by type lock", allTasks.matchingType<BufLockTask>(),
            "bufLockCommonMain", "bufLockCommonTest",
            "bufLockNativeMain", "bufLockNativeTest",
            "bufLockAppleMain", "bufLockAppleTest",
            "bufLockMacosMain", "bufLockMacosTest",
            "bufLockMacosArm64Main", "bufLockMacosArm64Test",
            "bufLockJvmMain", "bufLockJvmTest",

            // android lock
            "bufLockAndroidDebug",
            "bufLockAndroidRelease",
            "bufLockAndroidInstrumentedTestDebug",
            "bufLockAndroidUnitTestDebug",
            "bufLockAndroidUnitTestRelease",
        )
    }
}
