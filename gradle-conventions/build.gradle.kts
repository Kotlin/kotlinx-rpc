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
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)

    if (kotlinVersion >= "1.8.0") {
        implementation(libs.kover.gradle.plugin)
    }

    // https://stackoverflow.com/questions/76713758/use-version-catalog-inside-precompiled-gradle-plugin
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        named("conventions-common") {
            version = libs.versions.krpc.core.get()
        }
    }

    plugins {
        named("conventions-jvm") {
            version = libs.versions.krpc.core.get()
        }
    }

    plugins {
        named("conventions-kmp") {
            version = libs.versions.krpc.core.get()
        }
    }
}
