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
            "protobufGenerateCommonMain", "protobufGenerateCommonTest",
            "protobufGenerateNativeMain", "protobufGenerateNativeTest",
            "protobufGenerateAppleMain", "protobufGenerateAppleTest",
            "protobufGenerateMacosMain", "protobufGenerateMacosTest",
            "protobufGenerateMacosArm64Main", "protobufGenerateMacosArm64Test",
            "protobufGenerateJvmMain", "protobufGenerateJvmTest",
            "protobufGenerateAndroidMain", "protobufGenerateAndroidDeviceTest", "protobufGenerateAndroidHostTest",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            "protobufGenerateCommonTest",
            "protobufGenerateNativeTest",
            "protobufGenerateAppleTest",
            "protobufGenerateMacosTest",
            "protobufGenerateMacosArm64Test",
            "protobufGenerateJvmTest",
            "protobufGenerateAndroidDeviceTest", "protobufGenerateAndroidHostTest",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            "protobufGenerateCommonMain",
            "protobufGenerateNativeMain",
            "protobufGenerateAppleMain",
            "protobufGenerateMacosMain",
            "protobufGenerateMacosArm64Main",
            "protobufGenerateJvmMain",
            "protobufGenerateAndroidMain",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())

        assertTasks(
            "unit test tasks", genTasks.androidUnitTestTasks(),
            "protobufGenerateAndroidHostTest",
        )

        assertTasks(
            "instrumented test tasks", genTasks.androidInstrumentedTestTasks(),
            "protobufGenerateAndroidDeviceTest",
        )

        assertTasks("matchingSourceSet main", genTasks.matchingSourceSet("main"))
        assertTasks("matchingSourceSet test", genTasks.matchingSourceSet("test"))

        assertTasks(
            "gen all android", genTasks.androidTasks(),
            "protobufGenerateAndroidMain", "protobufGenerateAndroidDeviceTest", "protobufGenerateAndroidHostTest",
        )

        assertTasks(
            "gen all android non test", genTasks.androidTasks().nonTestTasks(),
            "protobufGenerateAndroidMain",
        )

        assertTasks(
            "gen all android non test", genTasks.androidTasks().testTasks(),
            "protobufGenerateAndroidDeviceTest", "protobufGenerateAndroidHostTest",
        )

        assertTasks(
            "matchingAndroidFlavor null", genTasks.matchingAndroidFlavor(null),
            "protobufGenerateAndroidMain", "protobufGenerateAndroidDeviceTest", "protobufGenerateAndroidHostTest",
        )

        assertTasks(
            "matchingAndroidBuildType debug", genTasks.matchingAndroidBuildType(null),
            "protobufGenerateAndroidMain", "protobufGenerateAndroidDeviceTest", "protobufGenerateAndroidHostTest",
        )

        assertTasks(
            "matchingAndroidVariant debug", genTasks.matchingAndroidVariant(null),
            "protobufGenerateAndroidMain", "protobufGenerateAndroidDeviceTest", "protobufGenerateAndroidHostTest",
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
            "protobufGenerateAndroidMain", "protobufGenerateAndroidDeviceTest", "protobufGenerateAndroidHostTest",

            // lint
            "bufLintCommonMain", "bufLintCommonTest",
            "bufLintNativeMain", "bufLintNativeTest",
            "bufLintAppleMain", "bufLintAppleTest",
            "bufLintMacosMain", "bufLintMacosTest",
            "bufLintMacosArm64Main", "bufLintMacosArm64Test",
            "bufLintJvmMain", "bufLintJvmTest",
            "bufLintAndroidMain", "bufLintAndroidDeviceTest", "bufLintAndroidHostTest",

            // lock
            "bufLockCommonMain", "bufLockCommonTest",
            "bufLockNativeMain", "bufLockNativeTest",
            "bufLockAppleMain", "bufLockAppleTest",
            "bufLockMacosMain", "bufLockMacosTest",
            "bufLockMacosArm64Main", "bufLockMacosArm64Test",
            "bufLockJvmMain", "bufLockJvmTest",
            "bufLockAndroidMain", "bufLockAndroidDeviceTest", "bufLockAndroidHostTest",
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
            "protobufGenerateAndroidMain", "protobufGenerateAndroidDeviceTest", "protobufGenerateAndroidHostTest",
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

        assertTasks(
            "all by type lock", allTasks.matchingType<BufLockTask>(),
            "bufLockCommonMain", "bufLockCommonTest",
            "bufLockNativeMain", "bufLockNativeTest",
            "bufLockAppleMain", "bufLockAppleTest",
            "bufLockMacosMain", "bufLockMacosTest",
            "bufLockMacosArm64Main", "bufLockMacosArm64Test",
            "bufLockJvmMain", "bufLockJvmTest",
            "bufLockAndroidMain", "bufLockAndroidDeviceTest", "bufLockAndroidHostTest",
        )
    }
}
