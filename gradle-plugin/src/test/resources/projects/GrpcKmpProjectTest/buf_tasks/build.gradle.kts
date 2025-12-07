/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.version
import kotlinx.rpc.buf.tasks.*
import kotlinx.rpc.buf.*
import org.gradle.api.provider.Provider
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


public abstract class BufLintTask @Inject constructor(properties: Provider<BufExecTask.Properties>) : BufExecTask(properties) {
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
        val genTasks = rpc.protoc.get().buf.generate.allTasks()

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

        assertTasks(
            "executedForSourceSet commonMain",
            genTasks.executedForSourceSet("commonMain"),
            "bufGenerateCommonMain",
        )

        assertTasks(
            "executedForSourceSet jvmMain",
            genTasks.executedForSourceSet("jvmMain"),
            "bufGenerateCommonMain",
            "bufGenerateJvmMain",
        )

        assertTasks(
            "executedForSourceSet macosArm64Main",
            genTasks.executedForSourceSet("macosArm64Main"),
            "bufGenerateCommonMain",
            "bufGenerateNativeMain",
            "bufGenerateAppleMain",
            "bufGenerateMacosMain",
            "bufGenerateMacosArm64Main",
        )

        assertTasks(
            "executedForSourceSet commonTest",
            genTasks.executedForSourceSet("commonTest"),
            "bufGenerateCommonMain", "bufGenerateCommonTest",
        )

        assertTasks(
            "executedForSourceSet jvmTest",
            genTasks.executedForSourceSet("jvmTest"),
            "bufGenerateCommonMain", "bufGenerateCommonTest",
            "bufGenerateJvmMain", "bufGenerateJvmTest",
        )

        assertTasks(
            "executedForSourceSet macosArm64Test",
            genTasks.executedForSourceSet("macosArm64Test"),
            "bufGenerateCommonMain", "bufGenerateCommonTest",
            "bufGenerateNativeMain", "bufGenerateNativeTest",
            "bufGenerateAppleMain", "bufGenerateAppleTest",
            "bufGenerateMacosMain", "bufGenerateMacosTest",
            "bufGenerateMacosArm64Main", "bufGenerateMacosArm64Test",
        )

        assertTasks(
            "executedForSourceSet macosArm64Test test tasks",
            genTasks.executedForSourceSet("macosArm64Test").testTasks(),
            "bufGenerateCommonTest",
            "bufGenerateNativeTest",
            "bufGenerateAppleTest",
            "bufGenerateMacosTest",
            "bufGenerateMacosArm64Test",
        )

        assertTasks(
            "executedForSourceSet macosArm64Test non test tasks",
            genTasks.executedForSourceSet("macosArm64Test").nonTestTasks(),
            "bufGenerateCommonMain",
            "bufGenerateNativeMain",
            "bufGenerateAppleMain",
            "bufGenerateMacosMain",
            "bufGenerateMacosArm64Main",
        )

        assertTasks(
            "executedForSourceSet macosArm64Test non test tasks matching macosMain",
            genTasks.executedForSourceSet("macosArm64Test").nonTestTasks().matchingSourceSet("macosMain"),
            "bufGenerateMacosMain",
        )

        assertTasks(
            "executedForSourceSet macosArm64Test non test tasks matching jvmMain",
            genTasks.executedForSourceSet("macosArm64Test").nonTestTasks().matchingSourceSet("jvmMain"),
        )

        assertTasks(
            "executedForSourceSet macosArm64Test non test tasks executed for nativeMain",
            genTasks.executedForSourceSet("macosArm64Test").nonTestTasks().executedForSourceSet("nativeMain"),
            "bufGenerateCommonMain",
            "bufGenerateNativeMain",
        )

        assertTasks("buf depends on commonMain", genTasks.matchingSourceSet("commonMain").single().bufDependsOn())
        assertTasks(
            "buf depends on macosMain", genTasks.matchingSourceSet("macosMain").single().bufDependsOn(),
            "bufGenerateCommonMain",
            "bufGenerateNativeMain",
            "bufGenerateAppleMain",
        )
        assertTasks(
            "buf depends on commonTest",
            genTasks.matchingSourceSet("commonTest").single().bufDependsOn(),
            "bufGenerateCommonMain",
        )

        assertTasks(
            "buf depends on jsTest",
            genTasks.matchingSourceSet("jsTest").single().bufDependsOn(),
            "bufGenerateCommonMain", "bufGenerateCommonTest",
            "bufGenerateWebMain".ifKotlinAtLeast("2.2.20"), "bufGenerateWebTest".ifKotlinAtLeast("2.2.20"),
            "bufGenerateJsMain",
        )

        val allTasks = rpc.protoc.get().buf.tasks.all()

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

        assertTasks(
            "all by type lint for macosMain", allTasks.matchingType<BufLintTask>().executedForSourceSet("macosMain"),
            "bufLintCommonMain",
            "bufLintNativeMain",
            "bufLintAppleMain",
            "bufLintMacosMain",
        )
    }
}
