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
    id("com.android.application") version "<android-version>"
    kotlin("android") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34

    flavorDimensions("abi", "version")

    productFlavors {
        create("freeapp") {
            dimension = "version"
        }
        create("retailapp") {
            dimension = "version"
        }
        create("x86") {
            dimension = "abi"
        }
        create("arm") {
            dimension = "abi"
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

            // android test
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestArmRetailappDebug",
            "bufGenerateAndroidTestX86FreeappDebug",
            "bufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmFreeappRelease",
            "bufGenerateTestArmRetailappDebug",
            "bufGenerateTestArmRetailappRelease",

            // test x86
            "bufGenerateTestX86FreeappDebug",
            "bufGenerateTestX86FreeappRelease",
            "bufGenerateTestX86RetailappDebug",
            "bufGenerateTestX86RetailappRelease",

            // arm
            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",
            "bufGenerateArmRetailappDebug",
            "bufGenerateArmRetailappRelease",

            // x86
            "bufGenerateX86FreeappDebug",
            "bufGenerateX86FreeappRelease",
            "bufGenerateX86RetailappDebug",
            "bufGenerateX86RetailappRelease",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            // android test
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestArmRetailappDebug",
            "bufGenerateAndroidTestX86FreeappDebug",
            "bufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmFreeappRelease",
            "bufGenerateTestArmRetailappDebug",
            "bufGenerateTestArmRetailappRelease",

            // test x86
            "bufGenerateTestX86FreeappDebug",
            "bufGenerateTestX86FreeappRelease",
            "bufGenerateTestX86RetailappDebug",
            "bufGenerateTestX86RetailappRelease",
        )

        assertTasks(
            "unit test tasks", genTasks.androidUnitTestTasks(),
            // test arm
            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmFreeappRelease",
            "bufGenerateTestArmRetailappDebug",
            "bufGenerateTestArmRetailappRelease",

            // test x86
            "bufGenerateTestX86FreeappDebug",
            "bufGenerateTestX86FreeappRelease",
            "bufGenerateTestX86RetailappDebug",
            "bufGenerateTestX86RetailappRelease",
        )

        assertTasks(
            "instrumented test tasks", genTasks.androidInstrumentedTestTasks(),
            // android test
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestArmRetailappDebug",
            "bufGenerateAndroidTestX86FreeappDebug",
            "bufGenerateAndroidTestX86RetailappDebug",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            // arm
            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",
            "bufGenerateArmRetailappDebug",
            "bufGenerateArmRetailappRelease",

            // x86
            "bufGenerateX86FreeappDebug",
            "bufGenerateX86FreeappRelease",
            "bufGenerateX86RetailappDebug",
            "bufGenerateX86RetailappRelease",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())

        assertTasks(
            "matchingSourceSet main", genTasks.matchingSourceSet("main"),

            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",
            "bufGenerateArmRetailappDebug",
            "bufGenerateArmRetailappRelease",

            "bufGenerateX86FreeappDebug",
            "bufGenerateX86FreeappRelease",
            "bufGenerateX86RetailappDebug",
            "bufGenerateX86RetailappRelease",
        )

        assertTasks(
            "matchingSourceSet freeapp", genTasks.matchingSourceSet("freeapp"),

            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",

            "bufGenerateX86FreeappDebug",
            "bufGenerateX86FreeappRelease",
        )

        assertTasks(
            "matchingSourceSet arm", genTasks.matchingSourceSet("arm"),

            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",
            "bufGenerateArmRetailappDebug",
            "bufGenerateArmRetailappRelease",
        )

        assertTasks(
            "matchingSourceSet x86", genTasks.matchingSourceSet("x86"),

            "bufGenerateX86FreeappDebug",
            "bufGenerateX86FreeappRelease",
            "bufGenerateX86RetailappDebug",
            "bufGenerateX86RetailappRelease",
        )

        assertTasks(
            "matchingSourceSet test", genTasks.matchingSourceSet("test"),

            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmFreeappRelease",
            "bufGenerateTestArmRetailappDebug",
            "bufGenerateTestArmRetailappRelease",

            "bufGenerateTestX86FreeappDebug",
            "bufGenerateTestX86FreeappRelease",
            "bufGenerateTestX86RetailappDebug",
            "bufGenerateTestX86RetailappRelease",
        )

        assertTasks(
            "matchingSourceSet armFreeapp", genTasks.matchingSourceSet("armFreeapp"),

            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",
        )

        assertTasks(
            "matchingSourceSet androidTest", genTasks.matchingSourceSet("androidTest"),

            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestArmRetailappDebug",

            "bufGenerateAndroidTestX86FreeappDebug",
            "bufGenerateAndroidTestX86RetailappDebug",
        )

        assertTasks(
            "gen all android", genTasks.androidTasks(),

            // android test
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestArmRetailappDebug",
            "bufGenerateAndroidTestX86FreeappDebug",
            "bufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmFreeappRelease",
            "bufGenerateTestArmRetailappDebug",
            "bufGenerateTestArmRetailappRelease",

            // test x86
            "bufGenerateTestX86FreeappDebug",
            "bufGenerateTestX86FreeappRelease",
            "bufGenerateTestX86RetailappDebug",
            "bufGenerateTestX86RetailappRelease",

            // arm
            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",
            "bufGenerateArmRetailappDebug",
            "bufGenerateArmRetailappRelease",

            // x86
            "bufGenerateX86FreeappDebug",
            "bufGenerateX86FreeappRelease",
            "bufGenerateX86RetailappDebug",
            "bufGenerateX86RetailappRelease",
        )

        assertTasks(
            "matchingSourceSet armFreeappDebug",
            genTasks.matchingSourceSet("armFreeappDebug"),
            "bufGenerateArmFreeappDebug",
        )

        assertTasks(
            "matchingSourceSet testArmFreeappDebug",
            genTasks.matchingSourceSet("testArmFreeappDebug"),
            "bufGenerateTestArmFreeappDebug",
        )

        assertTasks(
            "matchingSourceSet androidTestArmFreeappDebug",
            genTasks.matchingSourceSet("androidTestArmFreeappDebug"),
            "bufGenerateAndroidTestArmFreeappDebug",
        )

        assertTasks(
            "matchingAndroidFlavor freeapp", genTasks.matchingAndroidFlavor("freeapp"),

            // android test
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestX86FreeappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmFreeappRelease",

            // test x86
            "bufGenerateTestX86FreeappDebug",
            "bufGenerateTestX86FreeappRelease",

            // arm
            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",

            // x86
            "bufGenerateX86FreeappDebug",
            "bufGenerateX86FreeappRelease",
        )

        assertTasks(
            "matchingAndroidFlavor arm", genTasks.matchingAndroidFlavor("arm"),

            // android test
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestArmRetailappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmFreeappRelease",
            "bufGenerateTestArmRetailappDebug",
            "bufGenerateTestArmRetailappRelease",

            // arm
            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",
            "bufGenerateArmRetailappDebug",
            "bufGenerateArmRetailappRelease",
        )

        assertTasks(
            "matchingAndroidBuildType debug", genTasks.matchingAndroidBuildType("debug"),

            // android test
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestArmRetailappDebug",
            "bufGenerateAndroidTestX86FreeappDebug",
            "bufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmRetailappDebug",

            // test x86
            "bufGenerateTestX86FreeappDebug",
            "bufGenerateTestX86RetailappDebug",

            // arm
            "bufGenerateArmFreeappDebug",
            "bufGenerateArmRetailappDebug",

            // x86
            "bufGenerateX86FreeappDebug",
            "bufGenerateX86RetailappDebug",
        )

        assertTasks(
            "matchingAndroidBuildType relase", genTasks.matchingAndroidBuildType("release"),

            // test arm
            "bufGenerateTestArmFreeappRelease",
            "bufGenerateTestArmRetailappRelease",

            // test x86
            "bufGenerateTestX86FreeappRelease",
            "bufGenerateTestX86RetailappRelease",

            // arm
            "bufGenerateArmFreeappRelease",
            "bufGenerateArmRetailappRelease",

            // x86
            "bufGenerateX86FreeappRelease",
            "bufGenerateX86RetailappRelease",
        )

        assertTasks(
            "matchingAndroidVariant armFreeappDebug", genTasks.matchingAndroidVariant("armFreeappDebug"),

            // android test
            "bufGenerateAndroidTestArmFreeappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",

            // arm
            "bufGenerateArmFreeappDebug",
        )

        assertTasks(
            "matchingAndroidVariant armFreeappDebug udet tests", genTasks.matchingAndroidVariant("armFreeappDebug").androidUnitTestTasks(),
            "bufGenerateTestArmFreeappDebug",
        )

        assertTasks(
            "armFreeappDebug armFreeappDebug android tests", genTasks.matchingAndroidVariant("armFreeappDebug").androidInstrumentedTestTasks(),
            "bufGenerateAndroidTestArmFreeappDebug",
        )

        val allTasks = protoTasks.buf

        assertTasks(
            "all", allTasks,
            // android test
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestArmRetailappDebug",
            "bufGenerateAndroidTestX86FreeappDebug",
            "bufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmFreeappRelease",
            "bufGenerateTestArmRetailappDebug",
            "bufGenerateTestArmRetailappRelease",

            // test x86
            "bufGenerateTestX86FreeappDebug",
            "bufGenerateTestX86FreeappRelease",
            "bufGenerateTestX86RetailappDebug",
            "bufGenerateTestX86RetailappRelease",

            // arm
            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",
            "bufGenerateArmRetailappDebug",
            "bufGenerateArmRetailappRelease",

            // x86
            "bufGenerateX86FreeappDebug",
            "bufGenerateX86FreeappRelease",
            "bufGenerateX86RetailappDebug",
            "bufGenerateX86RetailappRelease",

            // android test
            "bufLintAndroidTestArmFreeappDebug",
            "bufLintAndroidTestArmRetailappDebug",
            "bufLintAndroidTestX86FreeappDebug",
            "bufLintAndroidTestX86RetailappDebug",

            // test arm
            "bufLintTestArmFreeappDebug",
            "bufLintTestArmFreeappRelease",
            "bufLintTestArmRetailappDebug",
            "bufLintTestArmRetailappRelease",

            // test x86
            "bufLintTestX86FreeappDebug",
            "bufLintTestX86FreeappRelease",
            "bufLintTestX86RetailappDebug",
            "bufLintTestX86RetailappRelease",

            // arm
            "bufLintArmFreeappDebug",
            "bufLintArmFreeappRelease",
            "bufLintArmRetailappDebug",
            "bufLintArmRetailappRelease",

            // x86
            "bufLintX86FreeappDebug",
            "bufLintX86FreeappRelease",
            "bufLintX86RetailappDebug",
            "bufLintX86RetailappRelease",
        )

        assertTasks(
            "all by type generate",
            allTasks.matchingType<BufGenerateTask>(),
            // android test
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufGenerateAndroidTestArmRetailappDebug",
            "bufGenerateAndroidTestX86FreeappDebug",
            "bufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",
            "bufGenerateTestArmFreeappRelease",
            "bufGenerateTestArmRetailappDebug",
            "bufGenerateTestArmRetailappRelease",

            // test x86
            "bufGenerateTestX86FreeappDebug",
            "bufGenerateTestX86FreeappRelease",
            "bufGenerateTestX86RetailappDebug",
            "bufGenerateTestX86RetailappRelease",

            // arm
            "bufGenerateArmFreeappDebug",
            "bufGenerateArmFreeappRelease",
            "bufGenerateArmRetailappDebug",
            "bufGenerateArmRetailappRelease",

            // x86
            "bufGenerateX86FreeappDebug",
            "bufGenerateX86FreeappRelease",
            "bufGenerateX86RetailappDebug",
            "bufGenerateX86RetailappRelease",
        )

        assertTasks(
            "all by type lint", allTasks.matchingType<BufLintTask>(),

            // android test
            "bufLintAndroidTestArmFreeappDebug",
            "bufLintAndroidTestArmRetailappDebug",
            "bufLintAndroidTestX86FreeappDebug",
            "bufLintAndroidTestX86RetailappDebug",

            // test arm
            "bufLintTestArmFreeappDebug",
            "bufLintTestArmFreeappRelease",
            "bufLintTestArmRetailappDebug",
            "bufLintTestArmRetailappRelease",

            // test x86
            "bufLintTestX86FreeappDebug",
            "bufLintTestX86FreeappRelease",
            "bufLintTestX86RetailappDebug",
            "bufLintTestX86RetailappRelease",

            // arm
            "bufLintArmFreeappDebug",
            "bufLintArmFreeappRelease",
            "bufLintArmRetailappDebug",
            "bufLintArmRetailappRelease",

            // x86
            "bufLintX86FreeappDebug",
            "bufLintX86FreeappRelease",
            "bufLintX86RetailappDebug",
            "bufLintX86RetailappRelease",
        )

        assertTasks(
            "all matchingSourceSet armFreeappDebug",
            allTasks.matchingSourceSet("armFreeappDebug"),
            "bufGenerateArmFreeappDebug",
            "bufLintArmFreeappDebug",
        )

        assertTasks(
            "all matchingSourceSet testArmFreeappDebug",
            allTasks.matchingSourceSet("testArmFreeappDebug"),
            "bufGenerateTestArmFreeappDebug",
            "bufLintTestArmFreeappDebug",
        )

        assertTasks(
            "all matchingSourceSet androidTestArmFreeappDebug",
            allTasks.matchingSourceSet("androidTestArmFreeappDebug"),
            "bufGenerateAndroidTestArmFreeappDebug",
            "bufLintAndroidTestArmFreeappDebug",
        )
    }
}
