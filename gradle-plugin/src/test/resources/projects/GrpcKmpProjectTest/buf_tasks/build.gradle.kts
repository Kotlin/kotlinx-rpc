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
            "bufGenerateCommonMain", "bufGenerateCommonTest",
            "bufGenerateNativeMain", "bufGenerateNativeTest",
            "bufGenerateAppleMain", "bufGenerateAppleTest",
            "bufGenerateMacosMain", "bufGenerateMacosTest",
            "bufGenerateMacosArm64Main", "bufGenerateMacosArm64Test",
            "bufGenerateJvmMain", "bufGenerateJvmTest",
            "bufGenerateWebMain".ifKotlinAtLeast("2.2.20"), "bufGenerateWebTest".ifKotlinAtLeast("2.2.20"),
            "bufGenerateJsMain", "bufGenerateJsTest",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            "bufGenerateCommonTest",
            "bufGenerateNativeTest",
            "bufGenerateAppleTest",
            "bufGenerateMacosTest",
            "bufGenerateMacosArm64Test",
            "bufGenerateJvmTest",
            "bufGenerateWebTest".ifKotlinAtLeast("2.2.20"),
            "bufGenerateJsTest",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            "bufGenerateCommonMain",
            "bufGenerateNativeMain",
            "bufGenerateAppleMain",
            "bufGenerateMacosMain",
            "bufGenerateMacosArm64Main",
            "bufGenerateJvmMain",
            "bufGenerateWebMain".ifKotlinAtLeast("2.2.20"),
            "bufGenerateJsMain",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())

        assertTasks("matchingSourceSet main", genTasks.matchingSourceSet("main"))
        assertTasks("matchingSourceSet test", genTasks.matchingSourceSet("test"))

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
            "bufGenerateWebMain".ifKotlinAtLeast("2.2.20"), "bufGenerateWebTest".ifKotlinAtLeast("2.2.20"),
            "bufGenerateJsMain", "bufGenerateJsTest",

            // lint
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
            "all by type generate",
            allTasks.matchingType<BufGenerateTask>(),
            "bufGenerateCommonMain", "bufGenerateCommonTest",
            "bufGenerateNativeMain", "bufGenerateNativeTest",
            "bufGenerateAppleMain", "bufGenerateAppleTest",
            "bufGenerateMacosMain", "bufGenerateMacosTest",
            "bufGenerateMacosArm64Main", "bufGenerateMacosArm64Test",
            "bufGenerateJvmMain", "bufGenerateJvmTest",
            "bufGenerateWebMain".ifKotlinAtLeast("2.2.20"), "bufGenerateWebTest".ifKotlinAtLeast("2.2.20"),
            "bufGenerateJsMain", "bufGenerateJsTest",
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
    }
}
