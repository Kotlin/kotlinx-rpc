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
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",
            "protobufGenerateAndroidTestX86FreeappDebug",
            "protobufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappRelease",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestArmRetailappRelease",

            // test x86
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86FreeappRelease",
            "protobufGenerateTestX86RetailappDebug",
            "protobufGenerateTestX86RetailappRelease",

            // arm
            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",
            "protobufGenerateArmRetailappDebug",
            "protobufGenerateArmRetailappRelease",

            // x86
            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86FreeappRelease",
            "protobufGenerateX86RetailappDebug",
            "protobufGenerateX86RetailappRelease",
        )

        assertTasks(
            "testTasks", genTasks.testTasks(),
            // android test
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",
            "protobufGenerateAndroidTestX86FreeappDebug",
            "protobufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappRelease",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestArmRetailappRelease",

            // test x86
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86FreeappRelease",
            "protobufGenerateTestX86RetailappDebug",
            "protobufGenerateTestX86RetailappRelease",
        )

        assertTasks(
            "unit test tasks", genTasks.androidUnitTestTasks(),
            // test arm
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappRelease",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestArmRetailappRelease",

            // test x86
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86FreeappRelease",
            "protobufGenerateTestX86RetailappDebug",
            "protobufGenerateTestX86RetailappRelease",
        )

        assertTasks(
            "instrumented test tasks", genTasks.androidInstrumentedTestTasks(),
            // android test
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",
            "protobufGenerateAndroidTestX86FreeappDebug",
            "protobufGenerateAndroidTestX86RetailappDebug",
        )

        assertTasks(
            "nonTestTasks", genTasks.nonTestTasks(),
            // arm
            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",
            "protobufGenerateArmRetailappDebug",
            "protobufGenerateArmRetailappRelease",

            // x86
            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86FreeappRelease",
            "protobufGenerateX86RetailappDebug",
            "protobufGenerateX86RetailappRelease",
        )

        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())

        assertTasks(
            "matchingSourceSet main", genTasks.matchingSourceSet("main"),

            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",
            "protobufGenerateArmRetailappDebug",
            "protobufGenerateArmRetailappRelease",

            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86FreeappRelease",
            "protobufGenerateX86RetailappDebug",
            "protobufGenerateX86RetailappRelease",
        )

        assertTasks(
            "matchingSourceSet freeapp", genTasks.matchingSourceSet("freeapp"),

            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",

            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86FreeappRelease",
        )

        assertTasks(
            "matchingSourceSet arm", genTasks.matchingSourceSet("arm"),

            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",
            "protobufGenerateArmRetailappDebug",
            "protobufGenerateArmRetailappRelease",
        )

        assertTasks(
            "matchingSourceSet x86", genTasks.matchingSourceSet("x86"),

            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86FreeappRelease",
            "protobufGenerateX86RetailappDebug",
            "protobufGenerateX86RetailappRelease",
        )

        assertTasks(
            "matchingSourceSet test", genTasks.matchingSourceSet("test"),

            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappRelease",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestArmRetailappRelease",

            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86FreeappRelease",
            "protobufGenerateTestX86RetailappDebug",
            "protobufGenerateTestX86RetailappRelease",
        )

        assertTasks(
            "matchingSourceSet armFreeapp", genTasks.matchingSourceSet("armFreeapp"),

            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",
        )

        assertTasks(
            "matchingSourceSet androidTest", genTasks.matchingSourceSet("androidTest"),

            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",

            "protobufGenerateAndroidTestX86FreeappDebug",
            "protobufGenerateAndroidTestX86RetailappDebug",
        )

        assertTasks(
            "gen all android", genTasks.androidTasks(),

            // android test
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",
            "protobufGenerateAndroidTestX86FreeappDebug",
            "protobufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappRelease",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestArmRetailappRelease",

            // test x86
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86FreeappRelease",
            "protobufGenerateTestX86RetailappDebug",
            "protobufGenerateTestX86RetailappRelease",

            // arm
            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",
            "protobufGenerateArmRetailappDebug",
            "protobufGenerateArmRetailappRelease",

            // x86
            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86FreeappRelease",
            "protobufGenerateX86RetailappDebug",
            "protobufGenerateX86RetailappRelease",
        )

        assertTasks(
            "matchingSourceSet armFreeappDebug",
            genTasks.matchingSourceSet("armFreeappDebug"),
            "protobufGenerateArmFreeappDebug",
        )

        assertTasks(
            "matchingSourceSet testArmFreeappDebug",
            genTasks.matchingSourceSet("testArmFreeappDebug"),
            "protobufGenerateTestArmFreeappDebug",
        )

        assertTasks(
            "matchingSourceSet androidTestArmFreeappDebug",
            genTasks.matchingSourceSet("androidTestArmFreeappDebug"),
            "protobufGenerateAndroidTestArmFreeappDebug",
        )

        assertTasks(
            "matchingAndroidFlavor freeapp", genTasks.matchingAndroidFlavor("freeapp"),

            // android test
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestX86FreeappDebug",

            // test arm
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappRelease",

            // test x86
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86FreeappRelease",

            // arm
            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",

            // x86
            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86FreeappRelease",
        )

        assertTasks(
            "matchingAndroidFlavor arm", genTasks.matchingAndroidFlavor("arm"),

            // android test
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",

            // test arm
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappRelease",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestArmRetailappRelease",

            // arm
            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",
            "protobufGenerateArmRetailappDebug",
            "protobufGenerateArmRetailappRelease",
        )

        assertTasks(
            "matchingAndroidBuildType debug", genTasks.matchingAndroidBuildType("debug"),

            // android test
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",
            "protobufGenerateAndroidTestX86FreeappDebug",
            "protobufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",

            // test x86
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86RetailappDebug",

            // arm
            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmRetailappDebug",

            // x86
            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86RetailappDebug",
        )

        assertTasks(
            "matchingAndroidBuildType relase", genTasks.matchingAndroidBuildType("release"),

            // test arm
            "protobufGenerateTestArmFreeappRelease",
            "protobufGenerateTestArmRetailappRelease",

            // test x86
            "protobufGenerateTestX86FreeappRelease",
            "protobufGenerateTestX86RetailappRelease",

            // arm
            "protobufGenerateArmFreeappRelease",
            "protobufGenerateArmRetailappRelease",

            // x86
            "protobufGenerateX86FreeappRelease",
            "protobufGenerateX86RetailappRelease",
        )

        assertTasks(
            "matchingAndroidVariant armFreeappDebug", genTasks.matchingAndroidVariant("armFreeappDebug"),

            // android test
            "protobufGenerateAndroidTestArmFreeappDebug",

            // test arm
            "protobufGenerateTestArmFreeappDebug",

            // arm
            "protobufGenerateArmFreeappDebug",
        )

        assertTasks(
            "matchingAndroidVariant armFreeappDebug udet tests", genTasks.matchingAndroidVariant("armFreeappDebug").androidUnitTestTasks(),
            "protobufGenerateTestArmFreeappDebug",
        )

        assertTasks(
            "armFreeappDebug armFreeappDebug android tests", genTasks.matchingAndroidVariant("armFreeappDebug").androidInstrumentedTestTasks(),
            "protobufGenerateAndroidTestArmFreeappDebug",
        )

        val allTasks = protoTasks.buf

        assertTasks(
            "all", allTasks,
            // android test
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",
            "protobufGenerateAndroidTestX86FreeappDebug",
            "protobufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappRelease",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestArmRetailappRelease",

            // test x86
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86FreeappRelease",
            "protobufGenerateTestX86RetailappDebug",
            "protobufGenerateTestX86RetailappRelease",

            // arm
            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",
            "protobufGenerateArmRetailappDebug",
            "protobufGenerateArmRetailappRelease",

            // x86
            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86FreeappRelease",
            "protobufGenerateX86RetailappDebug",
            "protobufGenerateX86RetailappRelease",

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

            // android test
            "bufLockAndroidTestArmFreeappDebug",
            "bufLockAndroidTestArmRetailappDebug",
            "bufLockAndroidTestX86FreeappDebug",
            "bufLockAndroidTestX86RetailappDebug",

            // test arm
            "bufLockTestArmFreeappDebug",
            "bufLockTestArmFreeappRelease",
            "bufLockTestArmRetailappDebug",
            "bufLockTestArmRetailappRelease",

            // test x86
            "bufLockTestX86FreeappDebug",
            "bufLockTestX86FreeappRelease",
            "bufLockTestX86RetailappDebug",
            "bufLockTestX86RetailappRelease",

            // arm
            "bufLockArmFreeappDebug",
            "bufLockArmFreeappRelease",
            "bufLockArmRetailappDebug",
            "bufLockArmRetailappRelease",

            // x86
            "bufLockX86FreeappDebug",
            "bufLockX86FreeappRelease",
            "bufLockX86RetailappDebug",
            "bufLockX86RetailappRelease",
        )

        assertTasks(
            "all by type generate",
            allTasks.matchingType<ProtobufGenerateTask>(),
            // android test
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",
            "protobufGenerateAndroidTestX86FreeappDebug",
            "protobufGenerateAndroidTestX86RetailappDebug",

            // test arm
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappRelease",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestArmRetailappRelease",

            // test x86
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86FreeappRelease",
            "protobufGenerateTestX86RetailappDebug",
            "protobufGenerateTestX86RetailappRelease",

            // arm
            "protobufGenerateArmFreeappDebug",
            "protobufGenerateArmFreeappRelease",
            "protobufGenerateArmRetailappDebug",
            "protobufGenerateArmRetailappRelease",

            // x86
            "protobufGenerateX86FreeappDebug",
            "protobufGenerateX86FreeappRelease",
            "protobufGenerateX86RetailappDebug",
            "protobufGenerateX86RetailappRelease",
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
            "bufLockArmFreeappDebug",
            "protobufGenerateArmFreeappDebug",
            "bufLintArmFreeappDebug",
        )

        assertTasks(
            "all matchingSourceSet testArmFreeappDebug",
            allTasks.matchingSourceSet("testArmFreeappDebug"),
            "bufLockTestArmFreeappDebug",
            "protobufGenerateTestArmFreeappDebug",
            "bufLintTestArmFreeappDebug",
        )

        assertTasks(
            "all matchingSourceSet androidTestArmFreeappDebug",
            allTasks.matchingSourceSet("androidTestArmFreeappDebug"),
            "bufLockAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmFreeappDebug",
            "bufLintAndroidTestArmFreeappDebug",
        )
    }
}
