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

dependencies {
    implementation(project(":conventions-utils"))
}

gradlePlugin {
    plugins {
        named("conventions-kotlin-version-jvm") {
            id = "conventions-kotlin-version-jvm"
            version = libs.versions.kotlinx.rpc.get()
        }
    }

    plugins {
        named("conventions-kotlin-version-kmp") {
            id = "conventions-kotlin-version-kmp"
            version = libs.versions.kotlinx.rpc.get()
        }
    }
}
