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
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        named("compiler-specific-module") {
            version = libs.versions.krpc.core.get()
        }
    }
}
