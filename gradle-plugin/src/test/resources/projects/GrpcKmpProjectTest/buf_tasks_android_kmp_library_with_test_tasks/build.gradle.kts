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
    id("com.android.kotlin.multiplatform.library") version "<android-version>"
}

kotlin {
    jvm()
    macosArm64()

    androidLibrary {
        namespace = "com.example.namespace"
        compileSdk = 34

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        withHostTestBuilder {
            sourceSetTreeName = "test"
        }
    }
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
            "bufGenerateAndroidMain", "bufGenerateAndroidDeviceTest", "bufGenerateAndroidHostTest",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            "bufGenerateCommonTest",
            "bufGenerateNativeTest",
            "bufGenerateAppleTest",
            "bufGenerateMacosTest",
            "bufGenerateMacosArm64Test",
            "bufGenerateJvmTest",
            "bufGenerateAndroidDeviceTest", "bufGenerateAndroidHostTest",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            "bufGenerateCommonMain",
            "bufGenerateNativeMain",
            "bufGenerateAppleMain",
            "bufGenerateMacosMain",
            "bufGenerateMacosArm64Main",
            "bufGenerateJvmMain",
            "bufGenerateAndroidMain",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())

        assertTasks(
            "unit test tasks", genTasks.androidUnitTestTasks(),
            "bufGenerateAndroidHostTest",
        )

        assertTasks(
            "instrumented test tasks", genTasks.androidInstrumentedTestTasks(),
            "bufGenerateAndroidDeviceTest",
        )

        assertTasks("matchingSourceSet main", genTasks.matchingSourceSet("main"))
        assertTasks("matchingSourceSet test", genTasks.matchingSourceSet("test"))

        assertTasks(
            "gen all android", genTasks.androidTasks(),
            "bufGenerateAndroidMain", "bufGenerateAndroidDeviceTest", "bufGenerateAndroidHostTest",
        )

        assertTasks(
            "gen all android non test", genTasks.androidTasks().nonTestTasks(),
            "bufGenerateAndroidMain",
        )

        assertTasks(
            "gen all android non test", genTasks.androidTasks().testTasks(),
            "bufGenerateAndroidDeviceTest", "bufGenerateAndroidHostTest",
        )

        assertTasks(
            "matchingAndroidFlavor null", genTasks.matchingAndroidFlavor(null),
            "bufGenerateAndroidMain", "bufGenerateAndroidDeviceTest", "bufGenerateAndroidHostTest",
        )

        assertTasks(
            "matchingAndroidBuildType debug", genTasks.matchingAndroidBuildType(null),
            "bufGenerateAndroidMain", "bufGenerateAndroidDeviceTest", "bufGenerateAndroidHostTest",
        )

        assertTasks(
            "matchingAndroidVariant debug", genTasks.matchingAndroidVariant(null),
            "bufGenerateAndroidMain", "bufGenerateAndroidDeviceTest", "bufGenerateAndroidHostTest",
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
            "bufGenerateAndroidMain", "bufGenerateAndroidDeviceTest", "bufGenerateAndroidHostTest",

            // lint
            "bufLintCommonMain", "bufLintCommonTest",
            "bufLintNativeMain", "bufLintNativeTest",
            "bufLintAppleMain", "bufLintAppleTest",
            "bufLintMacosMain", "bufLintMacosTest",
            "bufLintMacosArm64Main", "bufLintMacosArm64Test",
            "bufLintJvmMain", "bufLintJvmTest",
            "bufLintAndroidMain", "bufLintAndroidDeviceTest", "bufLintAndroidHostTest",
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
            "bufGenerateAndroidMain", "bufGenerateAndroidDeviceTest", "bufGenerateAndroidHostTest",
        )

        assertTasks(
            "all by type lint", allTasks.matchingType<BufLintTask>(),
            "bufLintCommonMain", "bufLintCommonTest",
            "bufLintNativeMain", "bufLintNativeTest",
            "bufLintAppleMain", "bufLintAppleTest",
            "bufLintMacosMain", "bufLintMacosTest",
            "bufLintMacosArm64Main", "bufLintMacosArm64Test",
            "bufLintJvmMain", "bufLintJvmTest",
            "bufLintAndroidMain", "bufLintAndroidDeviceTest", "bufLintAndroidHostTest",
        )
    }
}
