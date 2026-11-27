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
    id("com.android.application") version "<android-version>"
    kotlin("android") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34
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
            "protobufGenerateAndroidTestDebug",
            "protobufGenerateTestDebug",
            "protobufGenerateTestRelease",
            "protobufGenerateDebug",
            "protobufGenerateRelease",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            "protobufGenerateAndroidTestDebug",
            "protobufGenerateTestDebug",
            "protobufGenerateTestRelease",
        )

        assertTasks(
            "unit test tasks", genTasks.androidUnitTestTasks(),
            "protobufGenerateTestDebug",
            "protobufGenerateTestRelease",
        )

        assertTasks(
            "instrumented test tasks", genTasks.androidInstrumentedTestTasks(),
            "protobufGenerateAndroidTestDebug",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            "protobufGenerateDebug",
            "protobufGenerateRelease",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())

        assertTasks(
            "matchingSourceSet main", genTasks.matchingSourceSet("main"),
            "protobufGenerateDebug",
            "protobufGenerateRelease",
        )

        assertTasks(
            "matchingSourceSet test", genTasks.matchingSourceSet("test"),
            "protobufGenerateTestDebug",
            "protobufGenerateTestRelease",
        )

        assertTasks(
            "matchingSourceSet androidTest", genTasks.matchingSourceSet("androidTest"),
            "protobufGenerateAndroidTestDebug",
        )

        assertTasks(
            "gen all android", genTasks.androidTasks(),
            "protobufGenerateAndroidTestDebug",
            "protobufGenerateTestDebug",
            "protobufGenerateTestRelease",
            "protobufGenerateDebug",
            "protobufGenerateRelease",
        )

        assertTasks(
            "matchingSourceSet debug",
            genTasks.matchingSourceSet("debug"),
            "protobufGenerateDebug",
        )

        assertTasks(
            "matchingSourceSet testDebug",
            genTasks.matchingSourceSet("testDebug"),
            "protobufGenerateTestDebug",
        )

        assertTasks(
            "matchingSourceSet androidTestDebug",
            genTasks.matchingSourceSet("androidTestDebug"),
            "protobufGenerateAndroidTestDebug",
        )

        assertTasks(
            "matchingAndroidFlavor freeapp", genTasks.matchingAndroidFlavor("freeapp"),
        )

        assertTasks(
            "matchingAndroidBuildType debug", genTasks.matchingAndroidBuildType("debug"),
            "protobufGenerateAndroidTestDebug",
            "protobufGenerateTestDebug",
            "protobufGenerateDebug",
        )

        assertTasks(
            "matchingAndroidBuildType release", genTasks.matchingAndroidBuildType("release"),
            "protobufGenerateTestRelease",
            "protobufGenerateRelease",
        )

        assertTasks(
            "matchingAndroidVariant debug", genTasks.matchingAndroidVariant("debug"),
            "protobufGenerateAndroidTestDebug",
            "protobufGenerateTestDebug",
            "protobufGenerateDebug",
        )

        assertTasks(
            "matchingAndroidBuildType debug unit tests", genTasks.matchingAndroidBuildType("debug").androidUnitTestTasks(),
            "protobufGenerateTestDebug",
        )

        assertTasks(
            "matchingAndroidBuildType debug android tests", genTasks.matchingAndroidBuildType("debug").androidInstrumentedTestTasks(),
            "protobufGenerateAndroidTestDebug",
        )

        val allTasks = protoTasks.buf

        assertTasks(
            "all", allTasks,
            "bufLockAndroidTestDebug",
            "bufLockDebug",
            "bufLockRelease",
            "bufLockTestDebug",
            "bufLockTestRelease",

            "protobufGenerateAndroidTestDebug",
            "protobufGenerateTestDebug",
            "protobufGenerateTestRelease",
            "protobufGenerateDebug",
            "protobufGenerateRelease",

            "bufLintAndroidTestDebug",
            "bufLintTestDebug",
            "bufLintTestRelease",
            "bufLintDebug",
            "bufLintRelease",
        )

        assertTasks(
            "all by type generate", allTasks.matchingType<ProtobufGenerateTask>(),
            "protobufGenerateAndroidTestDebug",
            "protobufGenerateTestDebug",
            "protobufGenerateTestRelease",
            "protobufGenerateDebug",
            "protobufGenerateRelease",
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
            "all by type lock", allTasks.matchingType<BufLockTask>(),
            "bufLockAndroidTestDebug",
            "bufLockTestDebug",
            "bufLockTestRelease",
            "bufLockDebug",
            "bufLockRelease",
        )

        assertTasks(
            "all matchingSourceSet debug",
            allTasks.matchingSourceSet("debug"),
            "bufLockDebug",
            "protobufGenerateDebug",
            "bufLintDebug",
        )

        assertTasks(
            "all matchingSourceSet testDebug",
            allTasks.matchingSourceSet("testDebug"),
            "bufLockTestDebug",
            "protobufGenerateTestDebug",
            "bufLintTestDebug",
        )

        assertTasks(
            "all matchingSourceSet androidTestDebug",
            allTasks.matchingSourceSet("androidTestDebug"),
            "bufLockAndroidTestDebug",
            "protobufGenerateAndroidTestDebug",
            "bufLintAndroidTestDebug",
        )
    }
}
