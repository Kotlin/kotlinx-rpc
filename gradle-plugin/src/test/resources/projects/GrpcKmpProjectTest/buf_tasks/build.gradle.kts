/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.version
import kotlinx.rpc.buf.tasks.*
import kotlinx.rpc.buf.*
import kotlinx.rpc.protoc.*
import javax.inject.Inject

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

kotlin {
    jvm()
    js {
        nodejs()
    }
    macosArm64()
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

fun String.parseToKotlinVersion(): KotlinVersion {
    val (major, minor, patch) = substringBefore('-').split(".").map { it.toInt() }
    return KotlinVersion(major, minor, patch)
}

val currentKotlin = "<kotlin-version>".parseToKotlinVersion()

fun String.ifKotlinAtLeast(minVersion: String): String? {
    return if (currentKotlin >= minVersion.parseToKotlinVersion()) this else null
}

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
            "protobufGenerateWebMain".ifKotlinAtLeast("2.2.20"), "protobufGenerateWebTest".ifKotlinAtLeast("2.2.20"),
            "protobufGenerateJsMain", "protobufGenerateJsTest",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            "protobufGenerateCommonTest",
            "protobufGenerateNativeTest",
            "protobufGenerateAppleTest",
            "protobufGenerateMacosTest",
            "protobufGenerateMacosArm64Test",
            "protobufGenerateJvmTest",
            "protobufGenerateWebTest".ifKotlinAtLeast("2.2.20"),
            "protobufGenerateJsTest",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            "protobufGenerateCommonMain",
            "protobufGenerateNativeMain",
            "protobufGenerateAppleMain",
            "protobufGenerateMacosMain",
            "protobufGenerateMacosArm64Main",
            "protobufGenerateJvmMain",
            "protobufGenerateWebMain".ifKotlinAtLeast("2.2.20"),
            "protobufGenerateJsMain",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())

        assertTasks("matchingSourceSet main", genTasks.matchingSourceSet("main"))
        assertTasks("matchingSourceSet test", genTasks.matchingSourceSet("test"))

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
            "protobufGenerateWebMain".ifKotlinAtLeast("2.2.20"), "protobufGenerateWebTest".ifKotlinAtLeast("2.2.20"),
            "protobufGenerateJsMain", "protobufGenerateJsTest",

            // lint
            "bufLintCommonMain", "bufLintCommonTest",
            "bufLintNativeMain", "bufLintNativeTest",
            "bufLintAppleMain", "bufLintAppleTest",
            "bufLintMacosMain", "bufLintMacosTest",
            "bufLintMacosArm64Main", "bufLintMacosArm64Test",
            "bufLintJvmMain", "bufLintJvmTest",
            "bufLintWebMain".ifKotlinAtLeast("2.2.20"), "bufLintWebTest".ifKotlinAtLeast("2.2.20"),
            "bufLintJsMain", "bufLintJsTest",

            // lock
            "bufLockCommonMain", "bufLockCommonTest",
            "bufLockNativeMain", "bufLockNativeTest",
            "bufLockAppleMain", "bufLockAppleTest",
            "bufLockMacosMain", "bufLockMacosTest",
            "bufLockMacosArm64Main", "bufLockMacosArm64Test",
            "bufLockJvmMain", "bufLockJvmTest",
            "bufLockWebMain".ifKotlinAtLeast("2.2.20"), "bufLockWebTest".ifKotlinAtLeast("2.2.20"),
            "bufLockJsMain", "bufLockJsTest",
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
            "protobufGenerateWebMain".ifKotlinAtLeast("2.2.20"), "protobufGenerateWebTest".ifKotlinAtLeast("2.2.20"),
            "protobufGenerateJsMain", "protobufGenerateJsTest",
        )

        assertTasks(
            "all by type lint", allTasks.matchingType<BufLintTask>(),
            "bufLintCommonMain", "bufLintCommonTest",
            "bufLintNativeMain", "bufLintNativeTest",
            "bufLintAppleMain", "bufLintAppleTest",
            "bufLintMacosMain", "bufLintMacosTest",
            "bufLintMacosArm64Main", "bufLintMacosArm64Test",
            "bufLintJvmMain", "bufLintJvmTest",
            "bufLintWebMain".ifKotlinAtLeast("2.2.20"), "bufLintWebTest".ifKotlinAtLeast("2.2.20"),
            "bufLintJsMain", "bufLintJsTest",
        )

        assertTasks(
            "all by type lock", allTasks.matchingType<BufLockTask>(),
            "bufLockCommonMain", "bufLockCommonTest",
            "bufLockNativeMain", "bufLockNativeTest",
            "bufLockAppleMain", "bufLockAppleTest",
            "bufLockMacosMain", "bufLockMacosTest",
            "bufLockMacosArm64Main", "bufLockMacosArm64Test",
            "bufLockJvmMain", "bufLockJvmTest",
            "bufLockWebMain".ifKotlinAtLeast("2.2.20"), "bufLockWebTest".ifKotlinAtLeast("2.2.20"),
            "bufLockJsMain", "bufLockJsTest",
        )
    }
}
