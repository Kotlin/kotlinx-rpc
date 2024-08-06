/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.defaultConventionConfiguration
import util.otherwise
import util.whenKotlinLatest

plugins {
    alias(libs.plugins.gradle.kotlin.dsl)
}

// chicken-egg
apply(from = "src/main/kotlin/compiler-specific-module.gradle.kts")

defaultConventionConfiguration()

dependencies {
    implementation(":gradle-conventions-settings")

    project.whenKotlinLatest {
        implementation(project(":latest-only"))
    } otherwise {
        implementation(project(":empty"))
    }
}
