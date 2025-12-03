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
            "testTasks", genTasks.unitTestTasks(),
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
            "testTasks", genTasks.androidTestTasks(),
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
        assertTasks("matchingSourceSet main", genTasks.matchingSourceSet("main"))
        assertTasks("matchingSourceSet test", genTasks.matchingSourceSet("test"))
        assertTasks("executedForSourceSet main", genTasks.executedForSourceSet("main"))
        assertTasks("executedForSourceSet test", genTasks.executedForSourceSet("test"))

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
            "executedForSourceSet armFreeappDebug",
            genTasks.executedForSourceSet("armFreeappDebug"),
            "bufGenerateArmFreeappDebug",
        )

        assertTasks(
            "executedForSourceSet testArmFreeappDebug",
            genTasks.executedForSourceSet("testArmFreeappDebug"),
            "bufGenerateArmFreeappDebug",
            "bufGenerateTestArmFreeappDebug",
        )

        assertTasks(
            "executedForSourceSet androidTestArmFreeappDebug",
            genTasks.executedForSourceSet("androidTestArmFreeappDebug"),
            "bufGenerateArmFreeappDebug",
            "bufGenerateAndroidTestArmFreeappDebug",
        )

        assertTasks(
            "matchingFlavor freeapp", genTasks.matchingFlavor("freeapp"),

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
            "matchingFlavor arm", genTasks.matchingFlavor("arm"),

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
            "matchingBuildType debug", genTasks.matchingBuildType("debug"),

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
            "matchingBuildType relase", genTasks.matchingBuildType("release"),

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
            "matchingVariant armFreeappDebug", genTasks.matchingVariant("armFreeappDebug"),

            // android test
            "bufGenerateAndroidTestArmFreeappDebug",

            // test arm
            "bufGenerateTestArmFreeappDebug",

            // arm
            "bufGenerateArmFreeappDebug",
        )

        assertTasks(
            "matchingVariant armFreeappDebug udet tests", genTasks.matchingVariant("armFreeappDebug").unitTestTasks(),
            "bufGenerateTestArmFreeappDebug",
        )

        assertTasks(
            "armFreeappDebug armFreeappDebug android tests", genTasks.matchingVariant("armFreeappDebug").androidTestTasks(),
            "bufGenerateAndroidTestArmFreeappDebug",
        )

        val allTasks = rpc.protoc.get().buf.tasks.all()

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
