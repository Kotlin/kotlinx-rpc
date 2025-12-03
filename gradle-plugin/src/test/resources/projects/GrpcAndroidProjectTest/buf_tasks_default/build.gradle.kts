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
    id("com.android.application") version "<android-version>"
    kotlin("android") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34
}

public abstract class BufLintTask @Inject constructor(properties: BufExecTask.Properties) : BufExecTask(properties) {
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
    vararg expected: String,
) {
    val names = tasks.toNames()
    if (expected.toSet() != names) {
        throw GradleException("[$tag] Expected: ${expected.toSet()}, actual: $names")
    }
}

tasks.register("test_tasks") {
    doLast {
        val genTasks = rpc.protoc.get().buf.generate.allTasks()

        assertTasks(
            "gen all", genTasks,
            "bufGenerateAndroidTestDebug",
            "bufGenerateTestDebug",
            "bufGenerateTestRelease",
            "bufGenerateDebug",
            "bufGenerateRelease",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            "bufGenerateAndroidTestDebug",
            "bufGenerateTestDebug",
            "bufGenerateTestRelease",
        )

        assertTasks(
            "testTasks", genTasks.unitTestTasks(),
            "bufGenerateTestDebug",
            "bufGenerateTestRelease",
        )

        assertTasks(
            "testTasks", genTasks.androidTestTasks(),
            "bufGenerateAndroidTestDebug",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            "bufGenerateDebug",
            "bufGenerateRelease",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())
        assertTasks("matchingSourceSet main", genTasks.matchingSourceSet("main"))
        assertTasks("matchingSourceSet test", genTasks.matchingSourceSet("test"))
        assertTasks("executedForSourceSet main", genTasks.executedForSourceSet("main"))
        assertTasks("executedForSourceSet test", genTasks.executedForSourceSet("test"))

        assertTasks(
            "matchingSourceSet debug",
            genTasks.matchingSourceSet("debug"),
            "bufGenerateDebug",
        )

        assertTasks(
            "matchingSourceSet testDebug",
            genTasks.matchingSourceSet("testDebug"),
            "bufGenerateTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidTestDebug",
            genTasks.matchingSourceSet("androidTestDebug"),
            "bufGenerateAndroidTestDebug",
        )

        assertTasks(
            "executedForSourceSet debug",
            genTasks.executedForSourceSet("debug"),
            "bufGenerateDebug",
        )

        assertTasks(
            "executedForSourceSet testDebug",
            genTasks.executedForSourceSet("testDebug"),
            "bufGenerateDebug",
            "bufGenerateTestDebug",
        )

        assertTasks(
            "executedForSourceSet androidTestDebug",
            genTasks.executedForSourceSet("androidTestDebug"),
            "bufGenerateDebug",
            "bufGenerateAndroidTestDebug",
        )

        assertTasks(
            "matchingFlavor freeapp", genTasks.matchingFlavor("freeapp"),
        )

        assertTasks(
            "matchingBuildType debug", genTasks.matchingBuildType("debug"),
            "bufGenerateAndroidTestDebug",
            "bufGenerateTestDebug",
            "bufGenerateDebug",
        )

        assertTasks(
            "matchingBuildType release", genTasks.matchingBuildType("release"),
            "bufGenerateTestRelease",
            "bufGenerateRelease",
        )

        assertTasks(
            "matchingVariant debug", genTasks.matchingVariant("debug"),
            "bufGenerateAndroidTestDebug",
            "bufGenerateTestDebug",
            "bufGenerateDebug",
        )

        assertTasks(
            "matchingBuildType debug unit tests", genTasks.matchingBuildType("debug").unitTestTasks(),
            "bufGenerateTestDebug",
        )

        assertTasks(
            "matchingBuildType debug android tests", genTasks.matchingBuildType("debug").androidTestTasks(),
            "bufGenerateAndroidTestDebug",
        )

        val allTasks = rpc.protoc.get().buf.tasks.all()

        assertTasks(
            "all", allTasks,
            "bufGenerateAndroidTestDebug",
            "bufGenerateTestDebug",
            "bufGenerateTestRelease",
            "bufGenerateDebug",
            "bufGenerateRelease",

            "bufLintAndroidTestDebug",
            "bufLintTestDebug",
            "bufLintTestRelease",
            "bufLintDebug",
            "bufLintRelease",
        )

        assertTasks(
            "all by type generate", allTasks.matchingType<BufGenerateTask>(),
            "bufGenerateAndroidTestDebug",
            "bufGenerateTestDebug",
            "bufGenerateTestRelease",
            "bufGenerateDebug",
            "bufGenerateRelease",
        )

        assertTasks(
            "all by type lint", allTasks.matchingType<BufLintTask>(),

            "bufLintAndroidTestDebug",
            "bufLintTestDebug",
            "bufLintTestRelease",
            "bufLintDebug",
            "bufLintRelease",
        )

        assertTasks(
            "all matchingSourceSet debug",
            allTasks.matchingSourceSet("debug"),
            "bufGenerateDebug",
            "bufLintDebug",
        )

        assertTasks(
            "all matchingSourceSet testDebug",
            allTasks.matchingSourceSet("testDebug"),
            "bufGenerateTestDebug",
            "bufLintTestDebug",
        )

        assertTasks(
            "all matchingSourceSet androidTestDebug",
            allTasks.matchingSourceSet("androidTestDebug"),
            "bufGenerateAndroidTestDebug",
            "bufLintAndroidTestDebug",
        )
    }
}
