/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
}

val verificationRepositoryDir = file("<verification-repo-dir>")
val verificationVersion = "<protobuf-shim-version>"
val verificationTargetSuffix = "<host-publication-suffix>"

repositories {
    mavenCentral()
    maven {
        url = verificationRepositoryDir.toURI()
    }
}

kotlin {
    <host-target-declaration>

    applyDefaultHierarchyTemplate()

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-rpc-protobuf-shim-$verificationTargetSuffix:$verificationVersion")
            }
        }
    }
}
