/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import kotlinx.rpc.protoc.*
import kotlinx.rpc.buf.*

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
    id("com.android.application") version "<android-version>"
}

// BSR-only project: each source set declares one BSR module, no local .proto files. The test
// verifies that the generated buf.yaml for every source set contains its own module plus every
// module inherited from its parent source sets in the (legacy Android) KMP hierarchy.
kotlin {
    jvm()

    androidTarget {
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
        }

        unitTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
        }
    }
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34
}

fun bsrModuleFor(name: String): String =
    "buf.build/kotlinx-rpc/" + name.replace(Regex("([a-z0-9])([A-Z])"), "$1-$2").lowercase()

val bsrSourceSets = setOf(
    "commonMain", "commonTest",
    "jvmMain", "jvmTest",

    // KMP android
    "androidMain",
    "androidDebug", "androidRelease",
    "androidUnitTest", "androidUnitTestDebug", "androidUnitTestRelease",
    "androidInstrumentedTest", "androidInstrumentedTestDebug",

    // legacy android
    "main", "test", "androidTest",
    "debug", "release",
    "testDebug", "testRelease",
    "androidTestDebug",
    "testFixtures", "testFixturesDebug", "testFixturesRelease",
)

protoSourceSets {
    all {
        if (name in bsrSourceSets) {
            bsrDeps { module(bsrModuleFor(name)) }
        }
    }
}

rpc {
    protoc()
}
