/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.version

plugins {
    id("com.android.application") version "<android-version>"
    kotlin("android") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

rpc {
    protoc()
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

dependencies {
    protoImport(files("dependency-protos.zip"))
}

fun assertConfigurationExists(name: String) {
    configurations.findByName(name)
        ?: throw GradleException("Configuration '$name' does not exist")
}

fun assertTaskExists(name: String) {
    tasks.findByName(name)
        ?: throw GradleException("Task '$name' does not exist")
}

tasks.register("test_tasks") {
    doLast {
        // Verify proto and protoImport configurations exist for root source sets
        assertConfigurationExists("proto")           // main
        assertConfigurationExists("protoImport")     // main
        assertConfigurationExists("testProto")       // test
        assertConfigurationExists("testProtoImport") // test
        assertConfigurationExists("androidTestProto")
        assertConfigurationExists("androidTestProtoImport")

        // Verify proto configurations exist for flavor source sets
        assertConfigurationExists("armProto")
        assertConfigurationExists("armProtoImport")
        assertConfigurationExists("x86Proto")
        assertConfigurationExists("x86ProtoImport")
        assertConfigurationExists("freeappProto")
        assertConfigurationExists("freeappProtoImport")
        assertConfigurationExists("retailappProto")
        assertConfigurationExists("retailappProtoImport")

        // Verify proto configurations exist for combined flavor source sets
        assertConfigurationExists("armFreeappProto")
        assertConfigurationExists("armFreeappProtoImport")
        assertConfigurationExists("armRetailappProto")
        assertConfigurationExists("armRetailappProtoImport")
        assertConfigurationExists("x86FreeappProto")
        assertConfigurationExists("x86FreeappProtoImport")
        assertConfigurationExists("x86RetailappProto")
        assertConfigurationExists("x86RetailappProtoImport")

        // Verify proto configurations exist for build type source sets
        assertConfigurationExists("debugProto")
        assertConfigurationExists("debugProtoImport")
        assertConfigurationExists("releaseProto")
        assertConfigurationExists("releaseProtoImport")

        // Verify proto configurations for variant source sets
        assertConfigurationExists("armFreeappDebugProto")
        assertConfigurationExists("armFreeappDebugProtoImport")
        assertConfigurationExists("armFreeappReleaseProto")
        assertConfigurationExists("armFreeappReleaseProtoImport")
        assertConfigurationExists("armRetailappDebugProto")
        assertConfigurationExists("armRetailappDebugProtoImport")
        assertConfigurationExists("armRetailappReleaseProto")
        assertConfigurationExists("armRetailappReleaseProtoImport")
        assertConfigurationExists("x86FreeappDebugProto")
        assertConfigurationExists("x86FreeappDebugProtoImport")
        assertConfigurationExists("x86FreeappReleaseProto")
        assertConfigurationExists("x86FreeappReleaseProtoImport")
        assertConfigurationExists("x86RetailappDebugProto")
        assertConfigurationExists("x86RetailappDebugProtoImport")
        assertConfigurationExists("x86RetailappReleaseProto")
        assertConfigurationExists("x86RetailappReleaseProtoImport")

        // Verify proto configurations for test variant source sets
        assertConfigurationExists("testArmFreeappDebugProto")
        assertConfigurationExists("testArmFreeappDebugProtoImport")
        assertConfigurationExists("testArmFreeappReleaseProto")
        assertConfigurationExists("testArmFreeappReleaseProtoImport")
        assertConfigurationExists("testX86FreeappDebugProto")
        assertConfigurationExists("testX86FreeappDebugProtoImport")

        // Verify proto configurations for androidTest variant source sets
        assertConfigurationExists("androidTestArmFreeappDebugProto")
        assertConfigurationExists("androidTestArmFreeappDebugProtoImport")
        assertConfigurationExists("androidTestX86FreeappDebugProto")
        assertConfigurationExists("androidTestX86FreeappDebugProtoImport")

        // Verify extract tasks exist for main variants
        assertTaskExists("extractProtoArmFreeappDebug")
        assertTaskExists("extractProtoImportArmFreeappDebug")
        assertTaskExists("extractProtoArmFreeappRelease")
        assertTaskExists("extractProtoImportArmFreeappRelease")
        assertTaskExists("extractProtoArmRetailappDebug")
        assertTaskExists("extractProtoImportArmRetailappDebug")
        assertTaskExists("extractProtoArmRetailappRelease")
        assertTaskExists("extractProtoImportArmRetailappRelease")
        assertTaskExists("extractProtoX86FreeappDebug")
        assertTaskExists("extractProtoImportX86FreeappDebug")
        assertTaskExists("extractProtoX86FreeappRelease")
        assertTaskExists("extractProtoImportX86FreeappRelease")
        assertTaskExists("extractProtoX86RetailappDebug")
        assertTaskExists("extractProtoImportX86RetailappDebug")
        assertTaskExists("extractProtoX86RetailappRelease")
        assertTaskExists("extractProtoImportX86RetailappRelease")

        // Verify extract tasks exist for test variants
        assertTaskExists("extractProtoTestArmFreeappDebug")
        assertTaskExists("extractProtoImportTestArmFreeappDebug")
        assertTaskExists("extractProtoTestArmFreeappRelease")
        assertTaskExists("extractProtoImportTestArmFreeappRelease")
        assertTaskExists("extractProtoTestX86FreeappDebug")
        assertTaskExists("extractProtoImportTestX86FreeappDebug")

        // Verify extract tasks exist for androidTest variants
        assertTaskExists("extractProtoAndroidTestArmFreeappDebug")
        assertTaskExists("extractProtoImportAndroidTestArmFreeappDebug")
        assertTaskExists("extractProtoAndroidTestX86FreeappDebug")
        assertTaskExists("extractProtoImportAndroidTestX86FreeappDebug")

        // Verify the protoImport configuration on main has the dependency
        val protoImportConfig = configurations.getByName("protoImport")
        val depCount = protoImportConfig.dependencies.size
        if (depCount != 1) {
            throw GradleException("Expected 1 dependency on protoImport, got $depCount")
        }
    }
}
