/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.gradle.kotlin.dsl)
}

configurations.configureEach {
    resolutionStrategy {
        force(libs.kotlin.reflect)
        force(libs.kotlin.stdlib)
        force(libs.kotlin.stdlib.jdk7)
        force(libs.kotlin.stdlib.jdk8)
    }
}

val kotlinVersion: String by extra

dependencies {
    implementation(project(":kotlin-version"))
    implementation(project(":conventions-utils"))
}

gradlePlugin {
    plugins {
        named("conventions-publishing") {
            version = libs.versions.kotlinx.rpc.get()
        }
    }

    plugins {
        named("conventions-common") {
            version = libs.versions.kotlinx.rpc.get()
        }
    }

    plugins {
        named("conventions-jvm") {
            version = libs.versions.kotlinx.rpc.get()
        }
    }

    plugins {
        named("conventions-kmp") {
            version = libs.versions.kotlinx.rpc.get()
        }
    }
}
