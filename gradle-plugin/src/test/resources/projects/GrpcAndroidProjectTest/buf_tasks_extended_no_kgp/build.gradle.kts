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
    // No kotlin("android") plugin - using AGP 9.0+ built-in Kotlin
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34

    flavorDimensions += listOf("abi", "version")

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

// AGP 9.0 built-in Kotlin only creates unit test variants for testBuildType (debug).
// So there are no Test*Release variants compared to the KGP version.
tasks.register("test_tasks") {
    doLast {
        val genTasks = protoTasks.buf.generate

        assertTasks(
            "gen all", genTasks,

            // android test (instrumented, debug only)
            "protobufGenerateAndroidTestArmFreeappDebug",
            "protobufGenerateAndroidTestArmRetailappDebug",
            "protobufGenerateAndroidTestX86FreeappDebug",
            "protobufGenerateAndroidTestX86RetailappDebug",

            // test arm (debug only)
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",

            // test x86 (debug only)
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86RetailappDebug",

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

            // test arm (debug only)
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",

            // test x86 (debug only)
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86RetailappDebug",
        )

        assertTasks(
            "unit test tasks", genTasks.androidUnitTestTasks(),
            // test arm (debug only)
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",

            // test x86 (debug only)
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86RetailappDebug",
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

            // debug only
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",

            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86RetailappDebug",
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

            // test (debug only)
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86RetailappDebug",

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

            // test (debug only)
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestX86FreeappDebug",

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

            // test arm (debug only)
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",

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

            // test (debug only)
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",
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
            "matchingAndroidBuildType release", genTasks.matchingAndroidBuildType("release"),

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
            "matchingAndroidVariant armFreeappDebug unit tests", genTasks.matchingAndroidVariant("armFreeappDebug").androidUnitTestTasks(),
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

            // test (debug only)
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86RetailappDebug",

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

            // lint android test
            "bufLintAndroidTestArmFreeappDebug",
            "bufLintAndroidTestArmRetailappDebug",
            "bufLintAndroidTestX86FreeappDebug",
            "bufLintAndroidTestX86RetailappDebug",

            // lint test (debug only)
            "bufLintTestArmFreeappDebug",
            "bufLintTestArmRetailappDebug",
            "bufLintTestX86FreeappDebug",
            "bufLintTestX86RetailappDebug",

            // lint arm
            "bufLintArmFreeappDebug",
            "bufLintArmFreeappRelease",
            "bufLintArmRetailappDebug",
            "bufLintArmRetailappRelease",

            // lint x86
            "bufLintX86FreeappDebug",
            "bufLintX86FreeappRelease",
            "bufLintX86RetailappDebug",
            "bufLintX86RetailappRelease",

            // lcok android test
            "bufLockAndroidTestArmFreeappDebug",
            "bufLockAndroidTestArmRetailappDebug",
            "bufLockAndroidTestX86FreeappDebug",
            "bufLockAndroidTestX86RetailappDebug",

            // lock test (debug only)
            "bufLockTestArmFreeappDebug",
            "bufLockTestArmRetailappDebug",
            "bufLockTestX86FreeappDebug",
            "bufLockTestX86RetailappDebug",

            // lock arm
            "bufLockArmFreeappDebug",
            "bufLockArmFreeappRelease",
            "bufLockArmRetailappDebug",
            "bufLockArmRetailappRelease",

            // lock x86
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

            // test (debug only)
            "protobufGenerateTestArmFreeappDebug",
            "protobufGenerateTestArmRetailappDebug",
            "protobufGenerateTestX86FreeappDebug",
            "protobufGenerateTestX86RetailappDebug",

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

            // lint android test
            "bufLintAndroidTestArmFreeappDebug",
            "bufLintAndroidTestArmRetailappDebug",
            "bufLintAndroidTestX86FreeappDebug",
            "bufLintAndroidTestX86RetailappDebug",

            // lint test (debug only)
            "bufLintTestArmFreeappDebug",
            "bufLintTestArmRetailappDebug",
            "bufLintTestX86FreeappDebug",
            "bufLintTestX86RetailappDebug",

            // lint arm
            "bufLintArmFreeappDebug",
            "bufLintArmFreeappRelease",
            "bufLintArmRetailappDebug",
            "bufLintArmRetailappRelease",

            // lint x86
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
